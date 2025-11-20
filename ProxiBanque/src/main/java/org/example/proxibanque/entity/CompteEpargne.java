package org.example.proxibanque.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CompteEpargne extends Compte {

    private BigDecimal tauxRemuneration = new BigDecimal("0.03");

    public CompteEpargne(String numeroCompte) {
        super(numeroCompte);
    }

    public CompteEpargne(String numeroCompte, Client client) {
        super(numeroCompte, client);
    }

    public CompteEpargne(String numeroCompte, Client client, BigDecimal tauxRemuneration) {
        super(numeroCompte, client);
        this.tauxRemuneration = tauxRemuneration;
    }

    public BigDecimal calculerInteretsAnnuels() {
        return getSolde().multiply(tauxRemuneration);
    }
}