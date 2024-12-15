package com.mango.mangocompanyservice.controller;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.company.CompanyInfoDto;
import com.mango.mangocompanyservice.dto.company.CompanySaveDto;
import com.mango.mangocompanyservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public Mono<Response> saveCompany(ServerWebExchange exchange,
                                      @RequestBody CompanySaveDto dto) {
        return companyService.saveCompany(exchange, dto);
    }

    @GetMapping("/{id}")
    public Mono<CompanyInfoDto> getCompanyById(@PathVariable long id) {
        return companyService.getCompany(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Response> deleteCompany(ServerWebExchange exchange,
                                        @PathVariable long id) {
        return companyService.deleteCompany(exchange, id);
    }
}
