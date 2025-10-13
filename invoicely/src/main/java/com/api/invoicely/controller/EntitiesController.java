package com.api.invoicely.controller;

import com.api.invoicely.dto.entities.EntitiesCreateDTO;
import com.api.invoicely.dto.entities.EntitiesUpdateDTO;
import com.api.invoicely.dto.entities.EntitiesResponseDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.EntitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
public class EntitiesController {

    private final EntitiesService entitiesService;

    @PostMapping("/create")
    public ResponseEntity<EntitiesResponseDTO> createEntity(@RequestBody EntitiesCreateDTO dto, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(entitiesService.createEntity(owner, dto));
    }

    @PutMapping("/update/{entityId}")
    public ResponseEntity<EntitiesResponseDTO> updateEntity(@PathVariable UUID entityId, @RequestBody EntitiesUpdateDTO dto, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(entitiesService.updateEntity(owner, entityId, dto));
    }

    @GetMapping("/{entityId}")
    public ResponseEntity<EntitiesResponseDTO> getEntityById(@PathVariable UUID entityId, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(entitiesService.getEntityById(owner, entityId));
    }

    @GetMapping
    public ResponseEntity<List<EntitiesResponseDTO>> getAllEntities(
            @AuthenticationPrincipal User owner,
            @RequestParam(required = false) String type) {

        String cleanType = (type != null) ? type.trim().toUpperCase() : null;
        return ResponseEntity.ok(entitiesService.getAllEntities(owner, cleanType));
    }


    @DeleteMapping("/delete/{entityId}")
    public ResponseEntity<Void> deleteEntity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID entityId
    ) {
        entitiesService.deleteEntity(user, entityId);
        return ResponseEntity.noContent().build();
    }
}
