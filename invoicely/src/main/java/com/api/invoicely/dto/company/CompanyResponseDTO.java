package com.api.invoicely.dto.company;

import com.api.invoicely.dto.user.OwnerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDTO {
    private UUID id;
    private String name;
    private String nif;
    private String email;
    private String phone;
    private String address;
    private byte[] logo;
    private byte[] signature;
    private byte[] stamp;
    private OwnerDTO owner;
}