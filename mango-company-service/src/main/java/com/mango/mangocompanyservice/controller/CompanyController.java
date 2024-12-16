package com.mango.mangocompanyservice.controller;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.SearchRequest;
import com.mango.mangocompanyservice.dto.company.CompanyInfoDto;
import com.mango.mangocompanyservice.dto.company.CompanySaveDto;
import com.mango.mangocompanyservice.dto.company.CompanySearchDto;
import com.mango.mangocompanyservice.dto.company.CompanyUpdateDto;
import com.mango.mangocompanyservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
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

    @PostMapping("/search")
    public Flux<CompanySearchDto> searchByCompanyName(@RequestBody SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return companyService.searchByCompanyName(request.getName(), pageable);
    }

    @PatchMapping("/{id}/website")
    public Mono<Response> updateWebsiteLink(ServerWebExchange exchange,
                                            @PathVariable long id,
                                            @RequestBody CompanyUpdateDto dto) {
        return companyService.updateWebsite(exchange, id, dto.getParam());
    }

    @PatchMapping("/{id}/logo")
    public Mono<Response> updateLogo(ServerWebExchange exchange,
                                            @PathVariable long id,
                                            @ModelAttribute FilePart file) {
        return companyService.updateLogo(exchange, id, file);
    }
}
