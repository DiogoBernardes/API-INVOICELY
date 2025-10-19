package com.api.invoicely.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/** Apenas permite a alteração de campos não criticos **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUpdateDTO {
    private String email;
    private String phone;
    private String address;
    private MultipartFile logo;
    private MultipartFile signature;
    private MultipartFile stamp;
}