package com.api.invoicely.mapper;

import com.api.invoicely.dto.user.OwnerDTO;
import com.api.invoicely.entity.User;

public class OwnerMapper {
    public static OwnerDTO toOwnerDTO(User owner) {
        return OwnerDTO.builder()
                .id(owner.getId())
                .email(owner.getEmail())
                .username(owner.getUsername())
                .build();
    }
}
