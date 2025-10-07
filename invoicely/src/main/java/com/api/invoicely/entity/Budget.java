package com.api.invoicely.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    private Entities entity;

    @Column(nullable = false)
    private LocalDate date;
    private Double discount;
    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus state; // PENDENTE | ACEITE | REJEITADO

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<ItemBudget> itens;

    public enum BudgetStatus {
        PENDENTE, ACEITE, REJEITADO
    }
}
