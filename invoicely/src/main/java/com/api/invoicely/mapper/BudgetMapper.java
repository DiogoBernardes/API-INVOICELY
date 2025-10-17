package com.api.invoicely.mapper;

import com.api.invoicely.dto.budget.BudgetResponseDTO;
import com.api.invoicely.entity.Budget;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BudgetMapper {
    public static BudgetResponseDTO toBudgetDTO(Budget budget) {
        return BudgetResponseDTO.builder()
                .id(budget.getId())
                .entityId(budget.getEntity().getId())
                .entityName(budget.getEntity().getName())
                .date(budget.getDate())
                .discount(budget.getDiscount())
                .total(budget.getTotal())
                .state(budget.getState())
                .pdfUrl(budget.getPdfUrl())
                .pdfGeneratedAt(budget.getPdfGeneratedAt())
                .items(budget.getItens() != null
                        ? budget.getItens().stream().map(ItemBudgetMapper::toItemBudgetDTO).collect(Collectors.toList())
                        : null)
                .build();
    }
}
