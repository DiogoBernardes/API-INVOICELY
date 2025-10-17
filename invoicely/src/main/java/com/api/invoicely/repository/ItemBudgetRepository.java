package com.api.invoicely.repository;

import com.api.invoicely.entity.ItemBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemBudgetRepository extends JpaRepository<ItemBudget, UUID> {
    @Query("SELECT ib FROM ItemBudget ib WHERE ib.id = :id AND ib.company.id = :companyId AND ib.removedAt IS NULL")
    Optional<ItemBudget> findActiveByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("SELECT ib FROM ItemBudget ib WHERE ib.id = :id AND ib.budget.id = :budgetId AND ib.company.id = :companyId AND ib.removedAt IS NULL")
    Optional<ItemBudget> findActiveByIdAndBudgetAndCompanyId(@Param("id") UUID id,@Param("budgetId") UUID budgetId,
                                                             @Param("companyId") UUID companyId);

}
