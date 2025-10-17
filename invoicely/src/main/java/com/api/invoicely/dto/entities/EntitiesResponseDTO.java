package com.api.invoicely.dto.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntitiesResponseDTO {
    private UUID id;
    private String name;
    private String nif;
    private String email;
    private String phone;
    private String address;
    private String type;
}
