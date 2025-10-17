package com.api.invoicely.repository;

import com.api.invoicely.entity.Budget;
import com.api.invoicely.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Query("SELECT b FROM Budget b WHERE b.id = :id AND b.company.id = :companyId AND b.removedAt IS NULL")
    Optional<Budget> findActiveByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("SELECT b FROM Budget b WHERE b.company = :company AND b.removedAt IS NULL")
    List<Budget> findActiveByCompany(@Param("company") Company company);

    @Query("SELECT b FROM Budget b WHERE b.company = :company AND b.state = :status AND b.removedAt IS NULL")
    List<Budget> findActiveByCompanyAndState(Company company, Budget.BudgetStatus status);
}
