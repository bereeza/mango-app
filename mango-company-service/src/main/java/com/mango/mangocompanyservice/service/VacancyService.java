package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.user.UserInfoDto;
import com.mango.mangocompanyservice.dto.vacancy.*;
import com.mango.mangocompanyservice.entity.Applicant;
import com.mango.mangocompanyservice.entity.Vacancy;
import com.mango.mangocompanyservice.exception.*;
import com.mango.mangocompanyservice.repository.CompanyRepository;
import com.mango.mangocompanyservice.repository.VacancyApplicantRepository;
import com.mango.mangocompanyservice.repository.VacancyRepository;
import com.mango.mangocompanyservice.repository.VacancyStatisticRepository;
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
    private final VacancyStatisticRepository statisticRepository;
    private final VacancyApplicantRepository applicantRepository;
    private final CompanyRepository companyRepository;

    public Mono<Response<String>> saveVacancy(ServerWebExchange exchange,
                                              long id,
                                              VacancySaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> vacancyRepository.isUserEmployeeOfCompany(user.getId(), company.getId())
                                .flatMap(employee -> {
                                    if (company.getCeoId() != user.getId() && !employee) {
                                        log.error("The user is not a member of the company.");
                                        return Mono.error(new AccessForbiddenException("The user is not a member of the company."));
                                    }

                                    Vacancy vacancy = buildVacancy(user.getId(), company.getId(), dto);
                                    return vacancyRepository.save(vacancy)
                                            .then(Mono.just(Response.<String>builder()
                                                    .code(HttpStatus.OK.value())
                                                    .message("Vacancy successfully saved.")
                                                    .body("Vacancy successfully saved.")
                                                    .build()));
                                }))
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Flux<VacancyInfoDto> findCompanyVacancies(long id, Pageable pageable) {
        return companyRepository.findById(id)
                .flatMapMany(vacancy -> vacancyRepository.findAllBy(id, pageable))
                .switchIfEmpty(Flux.empty());
    }

    public Mono<Response<String>> deleteVacancy(ServerWebExchange exchange,
                                                long id,
                                                long vacancyId) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> vacancyRepository.findById(vacancyId)
                                .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")))
                                .flatMap(vacancy -> {
                                    if (user.getId() == company.getCeoId() || vacancy.getUserId() == user.getId()) {
                                        log.info("Vacancy successfully deleted by author");
                                        return deleteVacancyById(vacancyId);
                                    }

                                    return Mono.error(new AccessForbiddenException("You don't have permission."));
                                }))
                        .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found."))))
                .onErrorResume(this::errorResponse);
    }

    public Mono<Vacancy> findVacancy(long id) {
        return vacancyRepository.findById(id)
                .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")))
                .flatMap(vacancy -> applicantRepository.incrementViews(id)
                        .thenReturn(vacancy)
                );
    }

    public Flux<Vacancy> findVacanciesBy(String title, Pageable pageable) {
        return vacancyRepository.findVacanciesBy(title, pageable)
                .switchIfEmpty(Flux.empty());
    }

    public Mono<Response<String>> applyVacancy(ServerWebExchange exchange,
                                               long id,
                                               VacancyApplyDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> vacancyRepository.findById(id)
                        .flatMap(vacancy -> applicantRepository.existsByUserIdAndVacancyId(user.getId(), id)
                                .flatMap(alreadyApplied -> {
                                    if (alreadyApplied) {
                                        return Mono.error(new BadRequestException("User has already applied."));
                                    }

                                    Applicant applicant = buildApplicant(id, user, dto);
                                    return applicantRepository.save(applicant)
                                            .then(applicantRepository.incrementApplicants(id))
                                            .then(Mono.just(Response.<String>builder()
                                                    .code(HttpStatus.OK.value())
                                                    .message("User successfully applied.")
                                                    .body("User successfully applied.")
                                                    .build()));
                                })
                        )
                        .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")))
                )
                .onErrorResume(UserNotFoundException.class, this::errorResponse);
    }


    public Mono<VacancyStatisticDto> findStatisticByVacancyId(long id) {
        return statisticRepository.findStatisticByVacancyId(id)
                .switchIfEmpty(Mono.error(new VacancyNotFoundException("Vacancy not found.")));
    }

    public Flux<VacancyApplicantsDto> findVacancyApplicants(ServerWebExchange exchange, long id, Pageable pageable) {
        return userRedisService.buildUser(exchange)
                .flatMapMany(user -> vacancyRepository.findById(id)
                        .flatMapMany(vacancy -> {
                            if (vacancy.getUserId() != user.getId()) {
                                return Flux.error(new AccessForbiddenException("You do not have permission to view applicants for this vacancy."));
                            }

                            return applicantRepository.findAllApplicantsByVacancyId(id, pageable);
                        })
                        .switchIfEmpty(Flux.error(new VacancyNotFoundException("Vacancy not found.")))
                )
                .onErrorResume(e -> Flux.error(new BadRequestException(e.getMessage())));
    }

    private Applicant buildApplicant(long id, UserInfoDto user, VacancyApplyDto dto) {
        return Applicant.builder()
                .vacancyId(id)
                .userId(user.getId())
                .coverLetter(dto.getCoverLetter())
                .userCv(user.getCv())
                .applicationDate(LocalDateTime.now())
                .build();
    }

    private Mono<Response<String>> deleteVacancyById(long vacancyId) {
        return vacancyRepository.deleteById(vacancyId)
                .then(Mono.just(Response.<String>builder()
                        .code(HttpStatus.NO_CONTENT.value())
                        .message("Vacancy successfully deleted.")
                        .body("Vacancy successfully deleted.")
                        .build()));
    }

    private Mono<Response<String>> errorResponse(Throwable e) {
        log.error("Something went wrong: {}", e.getMessage());
        return Mono.error(new BadRequestException(e.getMessage()));
    }

    private Vacancy buildVacancy(long userId, long companyId, VacancySaveDto dto) {
        return Vacancy.builder()
                .userId(userId)
                .isAnonymous(dto.getIsAnonymous())
                .companyId(companyId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
