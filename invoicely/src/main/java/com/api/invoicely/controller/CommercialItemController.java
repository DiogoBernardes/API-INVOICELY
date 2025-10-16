package com.api.invoicely.controller;

import com.api.invoicely.dto.comercialItem.CommercialItemCreateDTO;
import com.api.invoicely.dto.comercialItem.CommercialItemResponseDTO;
import com.api.invoicely.dto.comercialItem.CommercialItemUpdateDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.CommercialItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commercial-items")
@RequiredArgsConstructor
public class CommercialItemController {

    private final CommercialItemService commercialItemService;

    @GetMapping("/{commercialItemId}")
    public ResponseEntity<CommercialItemResponseDTO> getCommercialItemById(@AuthenticationPrincipal User owner, @PathVariable UUID commercialItemId) {
        return ResponseEntity.ok(commercialItemService.getCommercialItemById(owner, commercialItemId));
    }

    @GetMapping
    public ResponseEntity<List<CommercialItemResponseDTO>> getAllCommercialItems(@AuthenticationPrincipal User owner,
                                                                                 @RequestParam(required = false) String type) {

        String cleanType = (type != null) ? type.trim().toUpperCase() : null;
        return ResponseEntity.ok(commercialItemService.getAllCommercialItems(owner, cleanType));
    }

    @PostMapping("/create")
    public ResponseEntity<CommercialItemResponseDTO> createCommercialItem(@AuthenticationPrincipal User owner, @RequestBody CommercialItemCreateDTO dto) {
        return ResponseEntity.ok(commercialItemService.createCommercialItem(owner, dto));
    }

    @PutMapping("/update/{commercialItemId}")
    public ResponseEntity<CommercialItemResponseDTO> updateCommercialItem(@AuthenticationPrincipal User owner, @PathVariable UUID commercialItemId,
                                                                          @RequestBody CommercialItemUpdateDTO dto) {
        return ResponseEntity.ok(commercialItemService.updateCommercialItem(owner, commercialItemId, dto));
    }

    @DeleteMapping("/delete/{commercialItemId}")
    public ResponseEntity<Void> deleteCommercialItem(@AuthenticationPrincipal User user, @PathVariable UUID commercialItemId) {
        commercialItemService.deleteCommercialItem(user, commercialItemId);
        return ResponseEntity.noContent().build();
    }
}
