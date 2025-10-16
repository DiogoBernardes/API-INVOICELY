package com.api.invoicely.dto.comercialItem;

import com.api.invoicely.entity.CommercialItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommercialItemCreateDTO {
    private String name;
    private String description;
    private Double price;
    private CommercialItem.ItemType type;
}
