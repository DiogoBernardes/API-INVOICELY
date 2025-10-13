package com.api.invoicely.dto.entities;

import com.api.invoicely.entity.Entities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntitiesCreateDTO {
    private String name;
    private String nif;
    private String email;
    private String phone;
    private String address;
    private Entities.EntityType type;
}