package com.api.invoicely.service;

import com.api.invoicely.dto.itemBudget.ItemBudgetCreateDTO;
import com.api.invoicely.dto.itemBudget.ItemBudgetResponseDTO;
import com.api.invoicely.entity.Budget;
import com.api.invoicely.entity.CommercialItem;
import com.api.invoicely.entity.ItemBudget;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.ItemBudgetMapper;
import com.api.invoicely.repository.BudgetRepository;
import com.api.invoicely.repository.CommercialItemRepository;
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
public class ItemBudgetService {

    private final BudgetRepository budgetRepository;
    private final CommercialItemRepository commercialItemRepository;
    private final ItemBudgetRepository itemBudgetRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional
    public ItemBudgetResponseDTO addItemToBudget(User owner, UUID budgetId, ItemBudgetCreateDTO dto) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(budgetId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        CommercialItem item = commercialItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ApiException("Item comercial não encontrado", HttpStatus.NOT_FOUND));

        ItemBudget itemBudget = ItemBudget.builder()
                .budget(budget)
                .company(owner.getCompany())
                .item(item)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .iva(dto.getIva())
                .build();

        itemBudgetRepository.save(itemBudget);

        recalculateBudget(budget);

        return ItemBudgetMapper.toItemBudgetDTO(itemBudget);
    }

    @Transactional
    public ItemBudgetResponseDTO updateItemBudget(User owner, UUID itemBudgetId, ItemBudgetCreateDTO dto) {
        ItemBudget itemBudget = itemBudgetRepository.findById(itemBudgetId)
                .orElseThrow(() -> new ApiException("Item do orçamento não encontrado", HttpStatus.NOT_FOUND));

        if (!itemBudget.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para alterar este item", HttpStatus.FORBIDDEN);
        }

        if (dto.getQuantity() != null) itemBudget.setQuantity(dto.getQuantity());
        if (dto.getUnitPrice() != null) itemBudget.setUnitPrice(dto.getUnitPrice());
        if (dto.getIva() != null) itemBudget.setIva(dto.getIva());

        itemBudgetRepository.save(itemBudget);
        recalculateBudget(itemBudget.getBudget());

        return ItemBudgetMapper.toItemBudgetDTO(itemBudget);
    }

    @Transactional
    public List<ItemBudgetResponseDTO> listItemsByBudget(User owner, UUID budgetId) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(budgetId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        return budget.getItens().stream()
                .filter(item -> item.getRemovedAt() == null)
                .map(ItemBudgetMapper::toItemBudgetDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteItemBudget(User owner, UUID itemBudgetId) {
        ItemBudget itemBudget = itemBudgetRepository.findById(itemBudgetId)
                .orElseThrow(() -> new ApiException("Item do orçamento não encontrado", HttpStatus.NOT_FOUND));

        if (!itemBudget.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para remover este item", HttpStatus.FORBIDDEN);
        }

        Budget budget = itemBudget.getBudget();

        itemBudgetRepository.delete(itemBudget);

        budget.getItens().remove(itemBudget);

        recalculateBudget(budget);
    }



    // Recalcula total do orçamento e sobrescreve PDF
    private void recalculateBudget(Budget budget) {
        double totalItems = budget.getItens().stream()
                .filter(i -> i.getRemovedAt() == null)
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity() * (1 + i.getIva() / 100))
                .sum();

        double discountPercentage = budget.getDiscount() != null ? budget.getDiscount() : 0.0;
        double discountValue = totalItems * (discountPercentage / 100.0);
        budget.setTotal(totalItems - discountValue);

        // Gera e sobrescreve PDF no Cloudflare
        String pdfUrl = pdfGeneratorService.generateBudgetPdf(budget);
        budget.setPdfUrl(pdfUrl);
        budget.setPdfGeneratedAt(LocalDateTime.now());

        budgetRepository.save(budget);
    }
}