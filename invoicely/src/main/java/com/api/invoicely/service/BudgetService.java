package com.api.invoicely.service;

import com.api.invoicely.dto.budget.BudgetCreateDTO;
import com.api.invoicely.dto.budget.BudgetResponseDTO;
import com.api.invoicely.entity.*;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.BudgetMapper;
import com.api.invoicely.repository.BudgetRepository;
import com.api.invoicely.repository.CommercialItemRepository;
import com.api.invoicely.repository.EntitiesRepository;
import com.api.invoicely.repository.ItemBudgetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ItemBudgetRepository itemBudgetRepository;
    private final EntitiesRepository entitiesRepository;
    private final CommercialItemRepository commercialItemRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional
    public BudgetResponseDTO createBudget(User owner, BudgetCreateDTO dto) {
        Company company = owner.getCompany();
        Entities entity = entitiesRepository.findById(dto.getEntityId())
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        Budget budget = Budget.builder()
                .company(company)
                .entity(entity)
                .date(dto.getDate())
                .discount(dto.getDiscount() != null ? dto.getDiscount() : 0.0)
                .total(dto.getTotal())
                .state(dto.getState())
                .build();

        List<ItemBudget> items = dto.getItems().stream().map(itemDto -> {
            CommercialItem item = commercialItemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new ApiException("Item comercial não encontrado", HttpStatus.NOT_FOUND));
            return ItemBudget.builder()
                    .company(company)
                    .budget(budget)
                    .item(item)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .iva(itemDto.getIva())
                    .build();
        }).collect(Collectors.toList());

        budget.setItens(items);

        // Calcula o total dos itens com IVA
        double totalItems = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity() * (1 + i.getIva() / 100))
                .sum();

        // Calcula valor do desconto em percentagem
        double discountPercentage = budget.getDiscount();
        double discountValue = totalItems * (discountPercentage / 100.0);

        // Total final = totalItems - desconto
        double totalFinal = totalItems - discountValue;
        budget.setTotal(totalFinal);

        budgetRepository.save(budget);

        // Gera PDF automaticamente
        String pdfUrl = pdfGeneratorService.generateBudgetPdf(budget);
        budget.setPdfUrl(pdfUrl);
        budget.setPdfGeneratedAt(LocalDateTime.now());
        budgetRepository.save(budget);

        return BudgetMapper.toBudgetDTO(budget);
    }

    public BudgetResponseDTO getBudgetById(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));
        return BudgetMapper.toBudgetDTO(budget);
    }

    public List<BudgetResponseDTO> getAllBudgets(User owner, String status) {
        List<Budget> budgets;

        if (status == null || status.isBlank()) {
            budgets = budgetRepository.findActiveByCompany(owner.getCompany());
        } else {
            try {
                Budget.BudgetStatus budgetStatus = Budget.BudgetStatus.valueOf(status);
                budgets = budgetRepository.findActiveByCompanyAndState(owner.getCompany(), budgetStatus);
            } catch (IllegalArgumentException e) {
                throw new ApiException("Tipo de estado inválido: " + status, HttpStatus.BAD_REQUEST);
            }
        }

        return budgets.stream()
                .map(BudgetMapper::toBudgetDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBudget(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        budget.setRemovedAt(LocalDateTime.now());
        budgetRepository.save(budget);
    }
}
