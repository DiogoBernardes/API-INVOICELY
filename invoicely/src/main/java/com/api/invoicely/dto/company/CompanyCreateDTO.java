package com.api.invoicely.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateDTO {
    private String name;
    private String nif;
    private String email;
    private String phone;
    private String address;
    private byte[] logo;
    private byte[] signature;
    private byte[] stamp;
}