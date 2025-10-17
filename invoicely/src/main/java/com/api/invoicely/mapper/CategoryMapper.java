package com.api.invoicely.mapper;

import com.api.invoicely.dto.category.CategoryResponseDTO;
import com.api.invoicely.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public static CategoryResponseDTO toCategoryDto(Category category) {
        if (category == null) return null;

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .company(CompanyMapper.toCompanyDTO(category.getCompany()))
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}

