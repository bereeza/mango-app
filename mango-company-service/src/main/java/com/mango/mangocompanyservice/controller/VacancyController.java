package com.mango.mangocompanyservice.controller;

import com.mango.mangocompanyservice.dto.SearchRequest;
import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.vacancy.VacancyApplicantsDto;
import com.mango.mangocompanyservice.dto.vacancy.VacancyApplyDto;
import com.mango.mangocompanyservice.dto.vacancy.VacancyStatisticDto;
import com.mango.mangocompanyservice.entity.Vacancy;
import com.mango.mangocompanyservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @GetMapping("/{id}")
    public Mono<Vacancy> findVacancy(@PathVariable long id) {
        return vacancyService.findVacancy(id);
    }

    @PostMapping("/all")
    public Flux<Vacancy> findVacancies(@RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return vacancyService.findVacanciesBy(request.getName(), pageable);
    }

    @PostMapping("/{id}/apply")
    public Mono<Response<String>> applyVacancy(ServerWebExchange exchange,
                                       @PathVariable long id,
                                       @RequestBody VacancyApplyDto dto) {
        return vacancyService.applyVacancy(exchange, id, dto);
    }

    @GetMapping("/{id}/statistic")
    public Mono<VacancyStatisticDto> findStatisticByVacancy(@PathVariable long id) {
        return vacancyService.findStatisticByVacancyId(id);
    }

    @PostMapping("/{id}/applicants")
    public Flux<VacancyApplicantsDto> findVacancyApplicants(ServerWebExchange exchange,
                                                            @PathVariable long id,
                                                            @RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return vacancyService.findVacancyApplicants(exchange, id, pageable);
    }
}
