package com.api.invoicely.service;

import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toggleUserActiveStatus_success() {
        User user = new User();
        user.setAdmin(false);

        String email = "user@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.toggleUserActiveStatus(email, false);

        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void toggleUserActiveStatus_userNotFound_throwsException() {
        String email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> userService.toggleUserActiveStatus(email, false));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Utilizador não encontrado.", ex.getMessage());
    }

    @Test
    void toggleUserActiveStatus_adminUser_throwsException() {
        User user = new User();
        user.setAdmin(true);

        String email = "admin@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class, () -> userService.toggleUserActiveStatus(email, false));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals("Não é possível desativar um administrador.", ex.getMessage());
    }
}
