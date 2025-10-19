package com.api.invoicely.service;

import com.api.invoicely.dto.auth.AuthRequest;
import com.api.invoicely.dto.auth.RegisterRequest;
import com.api.invoicely.dto.auth.ChangePasswordRequest;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.repository.UserRepository;
import com.api.invoicely.dto.auth.AuthResponseDTO;
import com.api.invoicely.dto.auth.RegisterResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDTO register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email existente. Por favor insira outro email!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("Nome de utilizador existente. Por favor insira outro nome de utilizador!", HttpStatus.BAD_REQUEST);
        }
        User newUser = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAdmin(false)
                .build();

        userRepository.save(newUser);

        return RegisterResponseDTO.fromUser(newUser);
    }

    public AuthResponseDTO login(AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) auth.getPrincipal();
            String token = jwtService.generateToken(user);
            return new AuthResponseDTO(token);

        } catch (DisabledException ex) {
            throw new ApiException("Conta desativada. Por favor contacte um administrador.", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException ex) {
            throw new ApiException("Credenciais inválidas. Verifique o email e a password.", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            throw new ApiException("Ocorreu um erro inesperado durante a autenticação.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public void changePassword(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApiException("Password atual incorreta", HttpStatus.BAD_REQUEST);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ApiException("As novas passwords não coincidem", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ApiException("A nova password não pode ser igual à atual", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
