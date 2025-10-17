package com.api.invoicely.mapper;

import com.api.invoicely.dto.expenses.ExpensesResponseDTO;
import com.api.invoicely.entity.Expenses;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    public static ExpensesResponseDTO toExpenseDto(Expenses expense) {
        if (expense == null) return null;

        return ExpensesResponseDTO.builder()
                .id(expense.getId())
                .entity(EntitiesMapper.toEntitiesDto(expense.getEntity()))
                .date(expense.getDate())
                .value(expense.getValue())
                .description(expense.getDescription())
                .category(CategoryMapper.toCategoryDto(expense.getCategory()))
                .paymentMethod(PaymentMethodMapper.toPaymentMethodDto(expense.getPaymentMethod()))
                .fileUrl(expense.getFileUrl())
                .build();
    }
}
