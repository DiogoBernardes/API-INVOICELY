package com.api.invoicely.dto.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpensesUpdateDTO {
    private UUID entityId;
    private LocalDate date;
    private Double value;
    private String description;
    private UUID categoryId;
    private UUID paymentMethodId;
    private MultipartFile file;
}
