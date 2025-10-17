package com.api.invoicely.repository;

import com.api.invoicely.entity.Category;
import com.api.invoicely.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.company.id = :companyId AND c.removedAt IS NULL")
    Optional<Category> findActiveByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("SELECT c FROM Category c WHERE c.company = :company AND c.removedAt IS NULL")
    List<Category> findActiveByCompany(@Param("company") Company company);

}
