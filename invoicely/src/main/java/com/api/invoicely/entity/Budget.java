package com.api.invoicely.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

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
    private BudgetStatus state;

    @Column(name = "pdf_url")
    private String pdfUrl; // URL do PDF gerado e guardado no R2

    @Column(name = "pdf_generated_at")
    private LocalDateTime pdfGeneratedAt;

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<ItemBudget> itens;

    @PrePersist
    public void prePersist() {
        insertedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BudgetStatus {
        PENDENTE, ACEITE, REJEITADO
    }
}
