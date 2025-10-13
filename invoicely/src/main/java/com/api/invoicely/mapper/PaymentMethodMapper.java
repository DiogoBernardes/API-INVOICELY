package com.api.invoicely.mapper;

import com.api.invoicely.dto.paymentMethod.PaymentMethodResponseDTO;
import com.api.invoicely.entity.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {
    public static PaymentMethodResponseDTO toPaymentMethodDto(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return null;

        return PaymentMethodResponseDTO.builder()
                .id(paymentMethod.getId())
                .name(paymentMethod.getName())
                .description(paymentMethod.getDescription())
                .build();
    }
}
