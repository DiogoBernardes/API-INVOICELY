package com.api.invoicely.dto.category;

import com.api.invoicely.dto.company.CompanyResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    private UUID id;
    private CompanyResponseDTO company;
    private String name;
    private String description;
}