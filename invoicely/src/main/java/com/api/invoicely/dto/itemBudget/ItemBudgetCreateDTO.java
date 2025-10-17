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
public class ItemBudgetCreateDTO {
    private UUID itemId;
    private Double quantity;
    private Double unitPrice;
    private Double iva;
}
