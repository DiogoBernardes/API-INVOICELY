package com.api.invoicely.repository;

import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.Entities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntitiesRepository extends JpaRepository<Entities, UUID> {

    @Query("SELECT e FROM Entities e WHERE e.id = :id AND e.removedAt IS NULL")
    Optional<Entities> findActiveById(@Param("id") UUID id);

    @Query("SELECT e FROM Entities e WHERE e.company = :company AND e.removedAt IS NULL")
    List<Entities> findActiveByCompany(@Param("company") Company company);

    @Query("SELECT e FROM Entities e WHERE e.company = :company AND e.type = :type AND e.removedAt IS NULL")
    List<Entities> findActiveByCompanyAndType(Company company, Entities.EntityType type);

    // Todas as entidades (ativas e removidas)
    List<Entities> findByCompany(Company company);
}
