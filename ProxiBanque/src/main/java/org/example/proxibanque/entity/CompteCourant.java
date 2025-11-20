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
public class CompteCourant extends Compte {

    private BigDecimal autorisationDecouvert = new BigDecimal("1000.00");

    public CompteCourant(String numeroCompte, Client client) {
        super(numeroCompte, client);
    }

    public CompteCourant(String numeroCompte, Client client, BigDecimal autorisationDecouvert) {
        super(numeroCompte, client);
        this.autorisationDecouvert = autorisationDecouvert;
    }

    // Méthode pour vérifier si un retrait est possible
    public boolean peutRetirer(BigDecimal montant) {
        BigDecimal soldeApresRetrait = getSolde().subtract(montant);
        return soldeApresRetrait.compareTo(autorisationDecouvert.negate()) >= 0;
    }
}