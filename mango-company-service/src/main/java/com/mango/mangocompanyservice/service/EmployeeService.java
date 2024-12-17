package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.employee.EmployeeInfoDto;
import com.mango.mangocompanyservice.dto.employee.EmployeeSaveDto;
import com.mango.mangocompanyservice.entity.Employee;
import com.mango.mangocompanyservice.entity.User;
import com.mango.mangocompanyservice.exception.CompanyNotFoundException;
import com.mango.mangocompanyservice.exception.UserNotFoundException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRedisService userRedisService;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Mono<Response> saveEmployee(ServerWebExchange exchange,
                                       long companyId,
                                       EmployeeSaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return userRepository.findById(dto.getUserId())
                                    .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                                    .flatMap(existingUser -> {
                                        Employee employee = buildEmployee(companyId, dto, existingUser);
                                        return employeeRepository.save(employee)
                                                .thenReturn(Response.builder()
                                                        .message("Employee saved successfully.")
                                                        .status(HttpStatus.OK)
                                                        .build());
                                    });
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found.")))
                )
                .onErrorResume(this::errorResponse);
    }

    public Mono<Response> deleteEmployee(ServerWebExchange exchange,
                                         long companyId,
                                         long userId) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return userRepository.findById(userId)
                                    .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                                    .flatMap(existingUser -> employeeRepository.deleteByUserId(userId)
                                            .thenReturn(Response.builder()
                                                    .message("Employee deleted successfully.")
                                                    .status(HttpStatus.OK)
                                                    .build()));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Mono<Response> updateEmployeeRole(ServerWebExchange exchange,
                                             long companyId,
                                             EmployeeSaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(companyId)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return permissionDeniedError();
                            }

                            return userRepository.findById(dto.getUserId())
                                    .switchIfEmpty(Mono.error(new UserNotFoundException("User not found.")))
                                    .flatMap(existingUser -> employeeRepository.updateEmployeeRole(companyId, dto.getUserId(), dto.getRole())
                                            .thenReturn(Response.builder()
                                                    .message("Employee updated successfully.")
                                                    .status(HttpStatus.OK)
                                                    .build()));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Flux<EmployeeInfoDto> findCompanyEmployees(long id, Pageable pageable) {
        return companyRepository.findById(id)
                .flatMapMany(company -> employeeRepository.findAllBy(id, pageable))
                .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found.")));
    }

    private Mono<Response> permissionDeniedError() {
        log.error("You have no permissions.");
        return Mono.error(new RuntimeException("You have no permissions."));
    }

    private Mono<Response> errorResponse(Throwable e) {
        log.error("Something went wrong: {}", e.getMessage());
        return Mono.error(new RuntimeException(e.getMessage()));
    }

    private Employee buildEmployee(long companyId, EmployeeSaveDto dto, User user) {
        return Employee.builder()
                .companyId(companyId)
                .userId(dto.getUserId())
                .role(dto.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .build();
    }
}
