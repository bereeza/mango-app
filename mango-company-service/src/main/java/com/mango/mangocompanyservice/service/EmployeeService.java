package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.employee.EmployeeInfoDto;
import com.mango.mangocompanyservice.dto.employee.EmployeeSaveDto;
import com.mango.mangocompanyservice.entity.Employee;
import com.mango.mangocompanyservice.entity.User;
import com.mango.mangocompanyservice.exception.*;
import com.mango.mangocompanyservice.repository.CompanyRepository;
import com.mango.mangocompanyservice.repository.EmployeeRepository;
import com.mango.mangocompanyservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRedisService userRedisService;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Mono<Response<String>> saveEmployee(ServerWebExchange exchange,
                                               long companyId,
                                               EmployeeSaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return employeeRepository.findByCompanyIdAndEmail(companyId, dto.getEmail())
                                    .flatMap(e -> userAlreadyInCompany())
                                    .switchIfEmpty(userPreprocessing(companyId, dto));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found.")))
                )
                .onErrorResume(this::errorResponse);
    }

    public Mono<Response<String>> deleteEmployee(ServerWebExchange exchange,
                                                 long companyId,
                                                 long userId) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return userRepository.findById(userId)
                                    .switchIfEmpty(Mono.error(new UserNotFoundException("Employee not found.")))
                                    .flatMap(existingUser -> employeeRepository.deleteByUserId(userId)
                                            .thenReturn(Response.<String>builder()
                                                    .code(HttpStatus.NO_CONTENT.value())
                                                    .message("Employee deleted successfully.")
                                                    .body("Employee deleted successfully.")
                                                    .build()));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Mono<Response<String>> updateEmployeeRole(ServerWebExchange exchange,
                                                     long companyId,
                                                     EmployeeSaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return userRepository.findByEmail(dto.getEmail())
                                    .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                                    .flatMap(updateExistingUser(companyId, dto));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Flux<EmployeeInfoDto> findCompanyEmployees(long id, Pageable pageable) {
        return companyRepository.findById(id)
                .flatMapMany(company -> employeeRepository.findAllBy(id, pageable))
                .switchIfEmpty(Flux.empty());
    }

    private Mono<Response<String>> userPreprocessing(long companyId, EmployeeSaveDto dto) {
        return userRepository.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                .flatMap(existingUser -> {
                    Employee employee = buildEmployee(companyId, dto, existingUser);
                    return employeeRepository.save(employee)
                            .thenReturn(Response.<String>builder()
                                    .code(HttpStatus.OK.value())
                                    .message("Employee saved successfully.")
                                    .body(employee.getRole())
                                    .build());
                });
    }

    private Function<User, Mono<? extends Response<String>>> updateExistingUser(long companyId, EmployeeSaveDto dto) {
        return existingUser -> employeeRepository.updateEmployeeRole(companyId, existingUser.getId(), dto.getRole())
                .thenReturn(Response.<String>builder()
                        .code(HttpStatus.OK.value())
                        .message("Employee updated successfully.")
                        .body("Employee updated successfully.")
                        .build());
    }

    private static Mono<Response<String>> userAlreadyInCompany() {
        return Mono.just(Response.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("User is already in the company.")
                .body("User is already in the company.")
                .build());
    }

    private Mono<Response<String>> permissionDeniedError() {
        log.error("You have no permissions.");
        return Mono.error(new AccessForbiddenException("You have no permissions."));
    }

    private Mono<Response<String>> errorResponse(Throwable e) {
        log.error("Something went wrong: {}", e.getMessage());
        return Mono.error(new BadRequestException(e.getMessage()));
    }

    private Employee buildEmployee(long companyId, EmployeeSaveDto dto, User user) {
        return Employee.builder()
                .companyId(companyId)
                .userId(user.getId())
                .role(dto.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .email(dto.getEmail())
                .build();
    }
}
