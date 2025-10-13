package com.api.invoicely.repository;

import com.api.invoicely.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT e FROM Category e WHERE e.id = :id AND e.company.id = :companyId AND e.removedAt IS NULL")
    Optional<Category> findActiveByIdAAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);
}
