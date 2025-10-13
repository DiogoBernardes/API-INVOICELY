package com.api.invoicely.service;

import com.api.invoicely.dto.expenses.ExpensesCreateDTO;
import com.api.invoicely.dto.expenses.ExpensesResponseDTO;
import com.api.invoicely.dto.expenses.ExpensesUpdateDTO;
import com.api.invoicely.entity.*;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.ExpenseMapper;
import com.api.invoicely.repository.CategoryRepository;
import com.api.invoicely.repository.EntitiesRepository;
import com.api.invoicely.repository.ExpensesRepository;
import com.api.invoicely.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import static com.api.invoicely.mapper.ExpenseMapper.toExpenseDto;


@Service
@RequiredArgsConstructor
public class ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final EntitiesRepository entitiesRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ExpensesResponseDTO createExpense(User owner, ExpensesCreateDTO dto) {
        Entities entity = entitiesRepository.findById(dto.getEntityId())
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        PaymentMethod paymentMethod = paymentMethodRepository.findActiveById(dto.getPaymentMethodId())
                .orElseThrow(() -> new ApiException("Método de pagamento não encontrado", HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findActiveByIdAAndCompanyId(dto.getCategoryId(), owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));

        Expenses expense = Expenses.builder()
                .company(owner.getCompany())
                .entity(entity)
                .date(dto.getDate())
                .value(dto.getValue())
                .description(dto.getDescription())
                .category(category)
                .paymentMethod(paymentMethod)
                .build();

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String fileUrl = fileStorageService.upload(
                    dto.getFile(),
                    "expenses/" + owner.getCompany().getName().replaceAll("\\s+", "_").toLowerCase()
            );
            expense.setFileUrl(fileUrl);
        }

        expensesRepository.save(expense);
        return toExpenseDto(expense);
    }

    public ExpensesResponseDTO updateExpense(User owner, UUID expenseId, ExpensesUpdateDTO dto) {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new ApiException("Despesa não encontrada", HttpStatus.NOT_FOUND));

        if (!expense.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para alterar esta despesa", HttpStatus.FORBIDDEN);
        }

        if (dto.getEntityId() != null) {
            Entities entity = entitiesRepository.findById(dto.getEntityId())
                    .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));
            expense.setEntity(entity);
        }

        if (dto.getDate() != null) expense.setDate(dto.getDate());
        if (dto.getValue() != null) expense.setValue(dto.getValue());
        if (dto.getDescription() != null) expense.setDescription(dto.getDescription());

        if (dto.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findActiveById(dto.getPaymentMethodId())
                    .orElseThrow(() -> new ApiException("Método de pagamento não encontrado", HttpStatus.NOT_FOUND));
            expense.setPaymentMethod(paymentMethod);
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findActiveByIdAAndCompanyId(dto.getCategoryId(), owner.getCompany().getId())
                    .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));
            expense.setCategory(category);
        }

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String fileUrl = fileStorageService.upload(
                    dto.getFile(),
                    "expenses/" + owner.getCompany().getName().replaceAll("\\s+", "_").toLowerCase()
            );
            expense.setFileUrl(fileUrl);
        }

        expensesRepository.save(expense);
        return toExpenseDto(expense);
    }

    public ExpensesResponseDTO getExpenseById(User owner, UUID expenseId) {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new ApiException("Despesa não encontrada", HttpStatus.NOT_FOUND));

        if (!expense.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para ver esta despesa", HttpStatus.FORBIDDEN);
        }

        return  toExpenseDto(expense);
    }

    public List<ExpensesResponseDTO> getAllExpenses(User owner) {
        return expensesRepository.findByCompany(owner.getCompany())
                .stream()
                .map(ExpenseMapper::toExpenseDto)
                .toList();

    }
}
