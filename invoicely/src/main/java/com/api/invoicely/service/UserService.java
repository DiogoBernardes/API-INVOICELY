package com.api.invoicely.service;

import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void toggleUserActiveStatus(String email, boolean active) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Utilizador não encontrado.", HttpStatus.NOT_FOUND));

        if (user.isAdmin()) {
            throw new ApiException("Não é possível desativar um administrador.", HttpStatus.FORBIDDEN);
        }

        user.setActive(active);
        userRepository.save(user);
    }
}
