package com.api.invoicely.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String nif;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String address;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] logo;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] signature;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] stamp;

    private LocalDateTime insertedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime removedAt;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    public void prePersist() {
        insertedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}