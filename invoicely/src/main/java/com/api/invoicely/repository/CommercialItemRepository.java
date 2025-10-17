package com.api.invoicely.repository;

import com.api.invoicely.entity.CommercialItem;
import com.api.invoicely.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommercialItemRepository extends JpaRepository<CommercialItem, UUID> {
    @Query("SELECT c FROM CommercialItem c WHERE c.id = :id AND c.company.id = :companyId AND c.removedAt IS NULL")
    Optional<CommercialItem> findActiveByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("SELECT c FROM CommercialItem c WHERE c.company = :company AND c.removedAt IS NULL")
    List<CommercialItem> findActiveByCompany(@Param("company") Company company);

    @Query("SELECT c FROM CommercialItem c WHERE c.company = :company AND c.type = :type AND c.removedAt IS NULL")
    List<CommercialItem> findActiveByCompanyAndType(Company company, CommercialItem.ItemType type);

}
