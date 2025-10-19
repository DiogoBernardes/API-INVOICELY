package com.api.invoicely.dto.auth;

import com.api.invoicely.entity.Company;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {

    private UUID id;
    private String email;
    private String username;
    private boolean active;
    private boolean isAdmin;
    private Company company;


    public static RegisterResponseDTO fromUser(com.api.invoicely.entity.User user) {
        return RegisterResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .active(user.isActive())
                .isAdmin(user.isAdmin())
                .company(user.getCompany())
                .build();
    }
}
