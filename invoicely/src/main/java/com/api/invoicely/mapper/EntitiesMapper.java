package com.api.invoicely.mapper;

import com.api.invoicely.dto.entities.EntitiesResponseDTO;
import com.api.invoicely.entity.Entities;
import org.springframework.stereotype.Component;

@Component
public class EntitiesMapper {
    public static EntitiesResponseDTO toEntitiesDto(Entities entity) {
        if (entity == null) return null;

        return EntitiesResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nif(entity.getNif())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .type(entity.getType().toString())
                .build();
    }
}