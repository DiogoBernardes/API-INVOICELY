package com.api.invoicely.controller;

import com.api.invoicely.dto.budget.*;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> getBudgetById(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId) {
        return ResponseEntity.ok(budgetService.getBudgetById(owner, budgetId));
    }


    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getAllBudgets(
            @AuthenticationPrincipal User owner,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateBefore
    ) {
        String cleanState = (state != null) ? state.trim().toUpperCase() : null;
        return ResponseEntity.ok(budgetService.getAllBudgets(owner, entityName, cleanState, startDate, endDate, dateAfter, dateBefore));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId) {
        budgetService.deleteBudget(owner, budgetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{budgetId}/generate-pdf")
    public ResponseEntity<BudgetResponseDTO> generateBudgetPdf(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId) {
        return ResponseEntity.ok(budgetService.generateBudgetPdf(owner, budgetId));
    }

    @PostMapping("/{budgetId}/send-to-client")
    public ResponseEntity<String> sendToClient(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId) {
        budgetService.sendBudgetToClient(owner, budgetId);
        return ResponseEntity.ok("Orçamento enviado para o cliente com sucesso!");
    }

    @PostMapping("/{budgetId}/send-pdf-by-email")
    public ResponseEntity<String> sendToAccountant(@AuthenticationPrincipal User owner, @PathVariable UUID budgetId,
                                                   @RequestParam String accountantEmail) {
        budgetService.sendBudgetByEmail(owner, budgetId, accountantEmail );
        return ResponseEntity.ok("Orçamento enviado com sucesso!");
    }
}
