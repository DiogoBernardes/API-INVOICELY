package com.api.invoicely.service;

import com.api.invoicely.dto.auth.*;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.repository.UserRepository;
import com.api.invoicely.dto.auth.AuthResponseDTO;
import com.api.invoicely.dto.auth.RegisterResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------
    // Register tests
    // --------------------
    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("TestUser");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        RegisterResponseDTO response = authService.register(request);

        assertEquals("test@example.com", response.getEmail());
        assertEquals("TestUser", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_emailExists_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Email existente. Por favor insira outro email!", ex.getMessage());
    }

    // --------------------
    // Login tests
    // --------------------
    @Test
    void login_success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("user@test.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(user, 15 * 60 * 1000)).thenReturn("access-token");
        when(jwtService.generateToken(user, 7 * 24 * 60 * 60 * 1000)).thenReturn("refresh-token");

        AuthResponseDTO response = authService.authenticate(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }


    @Test
    void login_disabledAccount_throwsException() {
        AuthRequest request = new AuthRequest();
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("Disabled"));

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals("Conta desativada. Por favor contacte um administrador.", ex.getMessage());
    }

    @Test
    void login_badCredentials_throwsException() {
        AuthRequest request = new AuthRequest();
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad"));

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("Credenciais inválidas. Verifique o email e a password.", ex.getMessage());
    }

    // --------------------
    // Change password tests
    // --------------------
    @Test
    void changePassword_success() {
        User user = new User();
        user.setPassword("encodedOld");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmNewPassword("newPass");

        when(passwordEncoder.matches("oldPass", "encodedOld")).thenReturn(true);
        when(passwordEncoder.matches("newPass", "encodedOld")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        authService.changePassword(request, user);

        assertEquals("encodedNew", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_wrongOldPassword_throwsException() {
        User user = new User();
        user.setPassword("encodedOld");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("new");
        request.setConfirmNewPassword("new");

        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> authService.changePassword(request, user));
        assertEquals("Password atual incorreta", ex.getMessage());
    }

    @Test
    void changePassword_newPasswordMismatch_throwsException() {
        User user = new User();
        user.setPassword("encodedOld");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new1");
        request.setConfirmNewPassword("new2");

        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.changePassword(request, user));
        assertEquals("As novas passwords não coincidem", ex.getMessage());
    }

    @Test
    void changePassword_newPasswordSameAsOld_throwsException() {
        User user = new User();
        user.setPassword("encodedOld");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("old");
        request.setConfirmNewPassword("old");

        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.changePassword(request, user));
        assertEquals("A nova password não pode ser igual à atual", ex.getMessage());
    }
}
