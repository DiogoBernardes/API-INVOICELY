package com.api.invoicely.controller;

import com.api.invoicely.dto.budget.*;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDTO> createBudget(@AuthenticationPrincipal User owner, @RequestBody BudgetCreateDTO dto) {
        return ResponseEntity.ok(budgetService.createBudget(owner, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> getBudgetById(@AuthenticationPrincipal User owner, @PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.getBudgetById(owner, id));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getAllBudgets(@AuthenticationPrincipal User owner,
                                                                 @RequestParam(required = false) String state) {
        String cleanState = (state != null) ? state.trim().toUpperCase() : null;
        return ResponseEntity.ok(budgetService.getAllBudgets(owner, cleanState));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@AuthenticationPrincipal User owner, @PathVariable UUID id) {
        budgetService.deleteBudget(owner, id);
        return ResponseEntity.noContent().build();
    }
}
