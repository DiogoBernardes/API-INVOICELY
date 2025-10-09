package com.api.invoicely.controller;

import com.api.invoicely.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserStatus(@PathVariable UUID userId, @RequestParam String email, @RequestParam boolean active) {
        userService.toggleUserActiveStatus(email, active);
        String message = active ? "Utilizador ativado com sucesso" : "Utilizador desativado com sucesso";
        return ResponseEntity.ok(message);
    }
}
