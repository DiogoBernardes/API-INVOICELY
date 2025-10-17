package com.api.invoicely.dto.itemBudget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemBudgetResponseDTO {
    private UUID id;
    private UUID budgetId;
    private UUID itemId;
    private String itemName;
    private Double quantity;
    private Double unitPrice;
    private Double iva;
    private Double totalWithIva;
}
