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
public class ComercialItem {

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
    private ItemType type; // PRODUTO | SERVICO

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

    public enum ItemType {
        PRODUTO, SERVICO
    }
}
