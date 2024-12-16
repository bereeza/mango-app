package com.mango.mangocompanyservice.service;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.company.CompanyInfoDto;
import com.mango.mangocompanyservice.dto.company.CompanySaveDto;
import com.mango.mangocompanyservice.dto.company.CompanySearchDto;
import com.mango.mangocompanyservice.entity.Company;
import com.mango.mangocompanyservice.exception.CompanyNotFoundException;
import com.mango.mangocompanyservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRedisService userRedisService;
    private final BucketService bucketService;

    public Mono<Response> saveCompany(ServerWebExchange exchange, CompanySaveDto dto) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> {
                    Company company = buildCompany(user.getId(), dto);
                    return companyRepository.save(company)
                            .then(Mono.just(Response.builder()
                                    .message("Company saved successfully.")
                                    .status(HttpStatus.CREATED)
                                    .build()));
                })
                .onErrorResume(e -> {
                    log.error("Company didn't saved: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException("Company didn't saved."));
                });
    }

    public Mono<CompanyInfoDto> getCompany(long id) {
        return companyRepository.findById(id)
                .map(this::buildCompanyInfo)
                .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found.")));
    }

    public Mono<Response> deleteCompany(ServerWebExchange exchange, long id) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                log.error("You cannot delete other users' companies.");
                                return Mono.error(new IllegalArgumentException("You cannot delete other users' companies."));
                            }

                            bucketService.delete(company.getLogo());

                            return companyRepository.deleteById(id)
                                    .then(Mono.just(Response.builder()
                                            .message("Company deleted successfully.")
                                            .status(HttpStatus.NO_CONTENT)
                                            .build()));
                        }))
                .onErrorResume(e -> {
                    log.error("Company not found: {}", e.getMessage());
                    return Mono.error(new CompanyNotFoundException("Company didn't saved."));
                });
    }

    public Flux<CompanySearchDto> searchByCompanyName(String name, Pageable pageable) {
        return companyRepository.searchByCompanyName(name, pageable)
                .switchIfEmpty(Mono.error(new CompanyNotFoundException("Company not found.")));
    }

    public Mono<Response> updateWebsite(ServerWebExchange exchange, long id, String website) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return Mono.error(new IllegalArgumentException("You cannot edit other users' companies."));
                            }

                            return companyRepository.updateWebsite(id, website)
                                    .then(Mono.just(Response.builder()
                                            .message("Company website link updated successfully.")
                                            .status(HttpStatus.OK)
                                            .build()));
                        }))
                .onErrorResume(e -> {
                    log.error("Company not found: {}", e.getMessage());
                    return Mono.error(new CompanyNotFoundException("Company not found."));
                });
    }

    public Mono<Response> updateLogo(ServerWebExchange exchange, long id, FilePart file) {
        return userRedisService.buildUser(exchange)
                .flatMap(user -> companyRepository.findById(id)
                        .flatMap(company -> {
                            if (user.getId() != company.getCeoId()) {
                                return Mono.error(new IllegalArgumentException("You cannot edit other users' companies."));
                            }

                            String logo = bucketService.save(file);
                            return companyRepository.updateLogo(id, logo)
                                    .then(Mono.just(Response.builder()
                                            .message("Company website logo updated successfully.")
                                            .status(HttpStatus.OK)
                                            .build()));
                        }))
                .onErrorResume(e -> {
                    log.error("Company not found: {}", e.getMessage());
                    return Mono.error(new CompanyNotFoundException("Company not found."));
                });
    }

    private CompanyInfoDto buildCompanyInfo(Company company) {
        return CompanyInfoDto.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .logo(company.getLogo())
                .createdAt(company.getCreatedAt())
                .ceoId(company.getCeoId())
                .build();
    }

    private Company buildCompany(long id, CompanySaveDto dto) {
        return Company.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .ceoId(id)
                .build();
    }
}
