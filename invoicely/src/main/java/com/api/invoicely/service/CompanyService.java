package com.api.invoicely.service;

import com.api.invoicely.dto.company.CompanyCreateDTO;
import com.api.invoicely.dto.company.CompanyUpdateDTO;
import com.api.invoicely.dto.company.CompanyResponseDTO;
import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.CompanyMapper;
import com.api.invoicely.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponseDTO findCompanyByOwner(User owner) {
        return companyRepository.findByOwner(owner)
                .map(CompanyMapper::toCompanyDTO)
                .orElse(null);
    }

    public CompanyResponseDTO createCompany(User owner, CompanyCreateDTO dto)  {
        if (companyRepository.findByOwner(owner).isPresent()) {
            throw new ApiException("O utilizador já possui uma empresa.", HttpStatus.BAD_REQUEST);
        }

        try {
            Company company = Company.builder()
                    .name(dto.getName())
                    .nif(dto.getNif())
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .address(dto.getAddress())
                    .logo(dto.getLogo() != null ? dto.getLogo().getBytes() : null)
                    .signature(dto.getSignature() != null ? dto.getSignature().getBytes() : null)
                    .stamp(dto.getStamp() != null ? dto.getStamp().getBytes() : null)
                    .owner(owner)
                    .build();

            companyRepository.save(company);
            return CompanyMapper.toCompanyDTO(company);

        } catch (IOException e) {
            throw new ApiException("Erro ao processar imagens da empresa.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public CompanyResponseDTO updateCompany(User owner, CompanyUpdateDTO dto) {
        Company company = companyRepository.findByOwner(owner)
                .orElseThrow(() -> new ApiException("Empresa não encontrada.", HttpStatus.NOT_FOUND));
        try {
            if (dto.getEmail() != null) company.setEmail(dto.getEmail());
            if (dto.getPhone() != null) company.setPhone(dto.getPhone());
            if (dto.getAddress() != null) company.setAddress(dto.getAddress());
            if (dto.getLogo() != null) company.setLogo(dto.getLogo().getBytes());
            if (dto.getSignature() != null) company.setSignature(dto.getSignature().getBytes());
            if (dto.getStamp() != null) company.setStamp(dto.getStamp().getBytes());

            companyRepository.save(company);
            return CompanyMapper.toCompanyDTO(company);
        } catch (IOException e) {
            throw new ApiException("Erro ao processar imagens da empresa.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
