package com.api.invoicely.dto.expenses;

import com.api.invoicely.dto.category.CategoryResponseDTO;
import com.api.invoicely.dto.entities.EntitiesResponseDTO;
import com.api.invoicely.dto.paymentMethod.PaymentMethodResponseDTO;
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
public class ExpensesResponseDTO {
    private UUID id;
    private EntitiesResponseDTO entity;
    private LocalDate date;
    private Double value;
    private String description;
    private CategoryResponseDTO category;
    private PaymentMethodResponseDTO paymentMethod;
    private String fileUrl;

}

