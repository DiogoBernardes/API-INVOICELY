package com.api.invoicely.repository;

import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, UUID> {
        List<Expenses> findByCompany(Company company);
}
