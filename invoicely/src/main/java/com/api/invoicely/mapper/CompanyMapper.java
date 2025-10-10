package com.api.invoicely.mapper;

import com.api.invoicely.dto.company.CompanyResponseDTO;
import com.api.invoicely.entity.Company;

import static com.api.invoicely.mapper.OwnerMapper.toOwnerDTO;

public class CompanyMapper {

    public static CompanyResponseDTO toCompanyDTO(Company company) {
        if (company == null) return null;

        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .nif(company.getNif())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .logo(company.getLogo())
                .signature(company.getSignature())
                .stamp(company.getStamp())
                .owner(toOwnerDTO(company.getOwner()))
                .build();
    }
}
