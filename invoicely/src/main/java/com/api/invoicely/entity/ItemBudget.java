package com.api.invoicely.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemBudget {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ComercialItem item;

    @Column(nullable = false)
    private Double quantity;
    @Column(nullable = false)
    private Double unitPrice;
    @Column(nullable = false)
    private Double iva;

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;
}
