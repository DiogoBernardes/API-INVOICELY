package com.api.invoicely.controller;

import com.api.invoicely.dto.itemBudget.ItemBudgetCreateDTO;
import com.api.invoicely.dto.itemBudget.ItemBudgetResponseDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.ItemBudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item-budgets")
@RequiredArgsConstructor
public class ItemBudgetController {

    private final ItemBudgetService itemBudgetService;

    @PostMapping("/{budgetId}")
    public ResponseEntity<ItemBudgetResponseDTO> addItem(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId,
                                              @RequestBody ItemBudgetCreateDTO dto) {
        return ResponseEntity.ok(itemBudgetService.addItemToBudget(owner, budgetId, dto));
    }

    @PutMapping("/{itemBudgetId}")
    public ResponseEntity<ItemBudgetResponseDTO> updateItem(@AuthenticationPrincipal User owner, @PathVariable UUID itemBudgetId,
                                                 @RequestBody ItemBudgetCreateDTO dto) {
        return ResponseEntity.ok(itemBudgetService.updateItemBudget(owner, itemBudgetId, dto));
    }

    @DeleteMapping("/{itemBudgetId}")
    public ResponseEntity<Void> deleteItem(@AuthenticationPrincipal User owner, @PathVariable UUID itemBudgetId) {
        itemBudgetService.deleteItemBudget(owner, itemBudgetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<List<ItemBudgetResponseDTO>> listItems(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId) {
        List<ItemBudgetResponseDTO> items = itemBudgetService.listItemsByBudget(owner, budgetId);
        return ResponseEntity.ok(items);
    }
}
