package com.api.invoicely.mapper;

import com.api.invoicely.dto.company.CompanyDTO;
import com.api.invoicely.dto.user.UserDTO;
import com.api.invoicely.entity.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public static UserDTO toUserDTO(User user) {

        CompanyDTO companyDto = null;
        if (user.getCompany() != null) {
            companyDto = CompanyDTO.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .nif(user.getCompany().getNif())
                    .email(user.getCompany().getEmail())
                    .phone(user.getCompany().getPhone())
                    .address(user.getCompany().getAddress())
                    .build();
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isAdmin(user.isAdmin())
                .company(companyDto)
                .build();
    }
}
