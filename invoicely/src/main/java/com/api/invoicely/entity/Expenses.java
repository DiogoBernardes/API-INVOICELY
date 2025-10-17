package com.api.invoicely.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expenses {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    private Entities entity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String description;

    @Column(name = "file_url")
    private String fileUrl; // URL do ficheiro no R2

    @Column(name = "file_type")
    private String fileType; // Ex: "application/pdf", "image/png"

    @Column(name = "file_uploaded_at")
    private LocalDateTime fileUploadedAt;

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

}
