package com.mango.mangocompanyservice.controller;

import com.mango.mangocompanyservice.dto.Response;
import com.mango.mangocompanyservice.dto.SearchRequest;
import com.mango.mangocompanyservice.dto.company.CompanyInfoDto;
import com.mango.mangocompanyservice.dto.company.CompanySaveDto;
import com.mango.mangocompanyservice.dto.company.CompanySearchDto;
import com.mango.mangocompanyservice.dto.company.CompanyUpdateDto;
import com.mango.mangocompanyservice.dto.employee.EmployeeInfoDto;
import com.mango.mangocompanyservice.dto.employee.EmployeeSaveDto;
import com.mango.mangocompanyservice.dto.employee.PagePayload;
import com.mango.mangocompanyservice.dto.vacancy.VacancyInfoDto;
import com.mango.mangocompanyservice.dto.vacancy.VacancySaveDto;
import com.mango.mangocompanyservice.service.CompanyService;
import com.mango.mangocompanyservice.service.EmployeeService;
import com.mango.mangocompanyservice.service.VacancyService;
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
    private final EmployeeService employeeService;
    private final VacancyService vacancyService;

    @PostMapping
    public Mono<Response<String>> saveCompany(ServerWebExchange exchange,
                                              @RequestBody CompanySaveDto dto) {
        return companyService.saveCompany(exchange, dto);
    }

    @GetMapping("/{id}")
    public Mono<CompanyInfoDto> getCompanyById(@PathVariable long id) {
        return companyService.getCompany(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Response<String>> deleteCompany(ServerWebExchange exchange,
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
    public Mono<Response<String>> updateWebsiteLink(ServerWebExchange exchange,
                                                    @PathVariable long id,
                                                    @RequestBody CompanyUpdateDto dto) {
        return companyService.updateWebsite(exchange, id, dto.getParam());
    }

    @PatchMapping("/{id}/logo")
    public Mono<Response<String>> updateLogo(ServerWebExchange exchange,
                                             @PathVariable long id,
                                             @ModelAttribute FilePart file) {
        return companyService.updateLogo(exchange, id, file);
    }

    @PostMapping("/{id}/employees")
    public Mono<Response<String>> saveEmployee(ServerWebExchange exchange,
                                     @PathVariable long id,
                                     @RequestBody EmployeeSaveDto dto) {
        return employeeService.saveEmployee(exchange, id, dto);
    }

    @DeleteMapping("/{id}/employees/{userId}")
    public Mono<Response<String>> deleteEmployee(ServerWebExchange exchange,
                                                 @PathVariable long id,
                                                 @PathVariable long userId) {
        return employeeService.deleteEmployee(exchange, id, userId);
    }

    @PatchMapping("/{id}/employees")
    public Mono<Response<String>> updateEmployeeRole(ServerWebExchange exchange,
                                                     @PathVariable long id,
                                                     @RequestBody EmployeeSaveDto dto) {
        return employeeService.updateEmployeeRole(exchange, id, dto);
    }

    @PostMapping("/{id}/employees/all")
    public Flux<EmployeeInfoDto> findCompanyEmployees(@PathVariable long id,
                                                      @RequestBody PagePayload request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.findCompanyEmployees(id, pageable);
    }

    @PostMapping("/{id}/vacancy")
    public Mono<Response<String>> saveVacancy(ServerWebExchange exchange,
                                              @PathVariable long id,
                                              @RequestBody VacancySaveDto dto) {
        return vacancyService.saveVacancy(exchange, id, dto);
    }

    @PostMapping("/{id}/vacancy/all")
    public Flux<VacancyInfoDto> findCompanyVacancies(@PathVariable long id,
                                                     @RequestBody PagePayload request) {
        int page = request.getPage();
        int size = request.getSize();
        Pageable pageable = PageRequest.of(page, size);
        return vacancyService.findCompanyVacancies(id, pageable);
    }

    @DeleteMapping("/{id}/vacancy/{vacancyId}")
    public Mono<Response<String>> deleteVacancy(ServerWebExchange exchange,
                                                @PathVariable long id,
                                                @PathVariable long vacancyId) {
        return vacancyService.deleteVacancy(exchange, id, vacancyId);
    }
}
