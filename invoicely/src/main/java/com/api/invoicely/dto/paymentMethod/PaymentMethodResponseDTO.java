package com.api.invoicely.dto.paymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponseDTO {
    private UUID id;
    private String name;
    private String description;
}
