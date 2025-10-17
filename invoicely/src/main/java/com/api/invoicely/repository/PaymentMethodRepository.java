package com.api.invoicely.repository;

import com.api.invoicely.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    @Query("SELECT e FROM PaymentMethod e WHERE e.id = :id AND e.removedAt IS NULL")
    Optional<PaymentMethod> findActiveById(@Param("id") UUID id);
}
