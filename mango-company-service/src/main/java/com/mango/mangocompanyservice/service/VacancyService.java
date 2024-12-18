package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.vacancy.VacancyInfoDto;
import com.mango.mangocompanyservice.dto.vacancy.VacancySaveDto;
import com.mango.mangocompanyservice.entity.Vacancy;
import com.mango.mangocompanyservice.exception.CompanyNotFoundException;
import com.mango.mangocompanyservice.exception.NotEmployeeException;
import com.mango.mangocompanyservice.exception.UserNotFoundException;
import com.mango.mangocompanyservice.exception.VacancyNotFoundException;
import com.mango.mangocompanyservice.repository.CompanyRepository;
import com.mango.mangocompanyservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final UserRedisService userRedisService;
    private final VacancyRepository vacancyRepository;
    private final CompanyRepository companyRepository;

    public Mono<Response> saveVacancy(ServerWebExchange exchange,
                                      long id,
                                      VacancySaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> vacancyRepository.isUserEmployeeOfCompany(user.getId(), company.getId())
                                .flatMap(employee -> {
                                    if (!employee) {
                                        log.error("The user is not a member of the company.");
                                        return Mono.error(new NotEmployeeException("The user is not a member of the company."));
                                    }

                                    Vacancy vacancy = buildVacancy(user.getId(), company.getId(), dto);
                                    return vacancyRepository.save(vacancy)
                                            .then(Mono.just(Response.builder()
                                                    .message("Vacancy successfully saved.")
                                                    .status(HttpStatus.CREATED)
                                                    .build()));
                                }))
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(UserNotFoundException.class, this::errorResponse);
    }

    public Flux<VacancyInfoDto> findCompanyVacancies(long id, Pageable pageable) {
        return companyRepository.findById(id)
                .flatMapMany(vacancy -> vacancyRepository.findAllBy(id, pageable))
                .onErrorResume(CompanyNotFoundException.class, e -> Flux.empty());
    }

    public Mono<Response> deleteVacancy(ServerWebExchange exchange,
                                        long id,
                                        long vacancyId) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> {
                            if (company.getCeoId() == user.getId()) {
                                log.info("Vacancy successfully deleted by CEO");
                                return deleteVacancyById(vacancyId);
                            }

                            return vacancyRepository.findById(vacancyId)
                                    .flatMap(vacancy -> {
                                        if (vacancy.getUserId() == user.getId()) {
                                            log.info("Vacancy successfully deleted by author");
                                            return deleteVacancyById(vacancyId);
                                        }

                                        return Mono.error(new NotEmployeeException("You don't have permission."));
                                    })
                                    .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")));
                        })
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(UserNotFoundException.class, this::errorResponse);
    }

    public Mono<Vacancy> findVacancy(long id) {
        return vacancyRepository.findById(id)
                .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")));
    }

    public Flux<Vacancy> findVacanciesBy(String title, Pageable pageable) {
        return vacancyRepository.findVacanciesBy(title, pageable)
                .switchIfEmpty(Flux.empty());
    }

    private Mono<Response> deleteVacancyById(long vacancyId) {
        return vacancyRepository.deleteById(vacancyId)
                .then(Mono.just(Response.builder()
                        .message("Vacancy successfully deleted.")
                        .status(HttpStatus.NO_CONTENT)
                        .build()));
    }

    private Vacancy buildVacancy(long userId, long companyId, VacancySaveDto dto) {
        return Vacancy.builder()
                .userId(userId)
                .isAnonymous(dto.isAnonymous())
                .companyId(companyId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Mono<Response> errorResponse(Throwable e) {
        log.error("Something went wrong: {}", e.getMessage());
        return Mono.error(new RuntimeException(e.getMessage()));
    }
}
