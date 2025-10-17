package com.api.invoicely.dto.budget;

import com.api.invoicely.dto.itemBudget.ItemBudgetResponseDTO;
import com.api.invoicely.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDTO {
    private UUID id;
    private UUID entityId;
    private String entityName;
    private LocalDate date;
    private Double discount;
    private Double total;
    private Budget.BudgetStatus state;
    private String pdfUrl;
    private LocalDateTime pdfGeneratedAt;
    private List<ItemBudgetResponseDTO> items;
}
