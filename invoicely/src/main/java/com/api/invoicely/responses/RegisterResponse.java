package com.api.invoicely.responses;

import com.api.invoicely.entity.Company;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private UUID id;
    private String email;
    private String username;
    private boolean active;
    private boolean isAdmin;
    private Company company;


    public static RegisterResponse fromUser(com.api.invoicely.entity.User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .active(user.isActive())
                .isAdmin(user.isAdmin())
                .company(user.getCompany())
                .build();
    }
}
