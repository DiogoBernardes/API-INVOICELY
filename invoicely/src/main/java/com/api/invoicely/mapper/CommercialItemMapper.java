package com.api.invoicely.mapper;

import com.api.invoicely.dto.comercialItem.CommercialItemResponseDTO;
import com.api.invoicely.entity.CommercialItem;
import org.springframework.stereotype.Component;

@Component
public class CommercialItemMapper {

    public static CommercialItemResponseDTO toComercialItemDTO(CommercialItem commercialItem) {
        if (commercialItem == null) return null;

        return CommercialItemResponseDTO.builder()
                .id(commercialItem.getId())
                .name(commercialItem.getName())
                .description(commercialItem.getDescription())
                .price(commercialItem.getPrice())
                .type(commercialItem.getType())
                .company(CompanyMapper.toCompanyDTO(commercialItem.getCompany()))
                .build();
    }
}
