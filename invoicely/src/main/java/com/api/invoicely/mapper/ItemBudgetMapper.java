package com.api.invoicely.mapper;

import com.api.invoicely.dto.itemBudget.ItemBudgetResponseDTO;
import com.api.invoicely.entity.ItemBudget;
import org.springframework.stereotype.Component;

@Component
public class ItemBudgetMapper {
    public static ItemBudgetResponseDTO toItemBudgetDTO(ItemBudget itemBudget) {

        double totalWithIva = (itemBudget.getUnitPrice() * itemBudget.getQuantity()) * (1 + itemBudget.getIva() / 100);

        return ItemBudgetResponseDTO.builder()
                .id(itemBudget.getId())
                .budgetId(itemBudget.getBudget().getId())
                .itemId(itemBudget.getItem().getId())
                .itemName(itemBudget.getItem().getName())
                .quantity(itemBudget.getQuantity())
                .unitPrice(itemBudget.getUnitPrice())
                .iva(itemBudget.getIva())
                .totalWithIva(totalWithIva)
                .build();
    }
}
