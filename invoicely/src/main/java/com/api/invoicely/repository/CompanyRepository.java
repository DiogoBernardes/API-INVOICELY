package com.api.invoicely.repository;

import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByOwner(User owner);
    Boolean existsByNif(String nif);
    Boolean existsByEmail(String email);
}

