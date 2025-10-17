package com.api.invoicely.dto.budget;

import com.api.invoicely.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetUpdateDTO {
    private LocalDate date;
    private Double discount;
    private Double total;
    private Budget.BudgetStatus state;
    private UUID entityId;
}
