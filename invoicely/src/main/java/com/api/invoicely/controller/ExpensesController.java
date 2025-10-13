package com.api.invoicely.controller;

import com.api.invoicely.dto.expenses.ExpensesCreateDTO;
import com.api.invoicely.dto.expenses.ExpensesResponseDTO;
import com.api.invoicely.dto.expenses.ExpensesUpdateDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.ExpensesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private final ExpensesService expensesService;

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpensesResponseDTO> getExpense(@PathVariable UUID expenseId, Authentication authentication) {

        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(expensesService.getExpenseById(owner, expenseId));
    }

    @GetMapping
    public ResponseEntity<List<ExpensesResponseDTO>> getAllExpenses(Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(expensesService.getAllExpenses(owner));
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<ExpensesResponseDTO> createExpense(@ModelAttribute ExpensesCreateDTO dto, Authentication authentication){
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(expensesService.createExpense(owner, dto));
    }


    @PutMapping("/update/{expenseId}")
    public ResponseEntity<ExpensesResponseDTO> updateExpense(@PathVariable("expenseId") UUID expenseId, @RequestBody ExpensesUpdateDTO dto,
                                                             Authentication authentication)  {

        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(expensesService.updateExpense(owner, expenseId, dto));
    }


}

