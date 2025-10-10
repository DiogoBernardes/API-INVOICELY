package com.api.invoicely.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Apenas permite a alteração de campos não criticos **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUpdateDTO {
    private String email;
    private String phone;
    private String address;
    private byte[] logo;
    private byte[] signature;
    private byte[] stamp;
}