package com.api.invoicely.dto.comercialItem;

import com.api.invoicely.dto.company.CompanyResponseDTO;
import com.api.invoicely.entity.CommercialItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommercialItemResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private CommercialItem.ItemType type;
    private CompanyResponseDTO company;
}
