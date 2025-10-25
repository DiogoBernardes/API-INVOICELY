package com.api.invoicely.dto.auth;


import com.api.invoicely.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;
}