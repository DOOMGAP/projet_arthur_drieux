package org.example.proxibanque.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Compte {

    @Id
    @GeneratedValue
    private Long id;

    private String numeroCompte;
    private BigDecimal solde = BigDecimal.ZERO;
    private LocalDate dateOuverture = LocalDate.now();

    @ManyToOne
    @JsonBackReference
    private Client client;

    public Compte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public Compte(String numeroCompte, Client client) {
        this.numeroCompte = numeroCompte;
        this.client = client;
    }
}