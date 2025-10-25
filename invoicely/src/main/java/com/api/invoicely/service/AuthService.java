package com.api.invoicely.service;

import com.api.invoicely.dto.auth.AuthRequest;
import com.api.invoicely.dto.auth.RegisterRequest;
import com.api.invoicely.dto.auth.ChangePasswordRequest;
import com.api.invoicely.dto.user.UserDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.UserMapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
                .active(true)
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
            String accessToken = jwtService.generateToken(user, 15 * 60 * 1000); // 15 min
            String refreshToken = jwtService.generateToken(user, 7 * 24 * 60 * 60 * 1000); // 7 dias

            UserDTO userDto = UserMapper.toUserDTO(user);

            return new AuthResponseDTO(accessToken, refreshToken, userDto);

        } catch (DisabledException ex) {
            throw new ApiException("Conta desativada. Por favor contacte um administrador.", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException ex) {
            throw new ApiException("Credenciais inválidas. Verifique o email e a password.", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            throw new ApiException("Ocorreu um erro inesperado durante a autenticação.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new ApiException("Refresh token inválido ou expirado.", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiException("Utilizador não encontrado.", HttpStatus.NOT_FOUND));

        String newAccessToken = jwtService.generateToken(user, 15 * 60 * 1000);

        UserDTO userDto = UserMapper.toUserDTO(user);

        return new AuthResponseDTO(newAccessToken, refreshToken, userDto);
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

    public AuthResponseDTO authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user, 15 * 60 * 1000); // 15 min
        String refreshToken = jwtService.generateToken(user, 7 * 24 * 60 * 60 * 1000); // 7 dias

        UserDTO userDto = UserMapper.toUserDTO(user);

        return new AuthResponseDTO(accessToken, refreshToken, userDto);
    }
}
