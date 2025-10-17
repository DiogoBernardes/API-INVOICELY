package com.api.invoicely.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comercial_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommercialItem {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

    @PrePersist
    public void prePersist() {
        insertedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ItemType {
        PRODUTO, SERVICO
    }
}
