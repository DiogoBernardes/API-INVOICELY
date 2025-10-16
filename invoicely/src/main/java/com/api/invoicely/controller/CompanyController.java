package com.api.invoicely.controller;

import com.api.invoicely.dto.company.CompanyCreateDTO;
import com.api.invoicely.dto.company.CompanyUpdateDTO;
import com.api.invoicely.dto.company.CompanyResponseDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> getCompany(@AuthenticationPrincipal User owner) {
        CompanyResponseDTO dto = companyService.findCompanyByOwner(owner);

        return ResponseEntity.ok(Objects.requireNonNullElse(dto, "Nenhuma empresa encontrada. Complete o formulário para registar a sua empresa."));
    }

    @PostMapping("/create")
    public ResponseEntity<CompanyResponseDTO> createCompany(@AuthenticationPrincipal User owner, @RequestBody CompanyCreateDTO dto) {
        return ResponseEntity.ok(companyService.createCompany(owner, dto));
    }

    @PutMapping("/update/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@AuthenticationPrincipal User owner, @RequestBody CompanyUpdateDTO dto,
                                                            @PathVariable String companyId) {
        return ResponseEntity.ok(companyService.updateCompany(owner, dto));
    }
}
