package org.example.proxibanque.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private String prenom;
    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;

    @ManyToOne
    @JsonBackReference
    private Conseiller conseiller;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // ← Côté "parent" de la relation
    private List<Compte> comptes = new ArrayList<>();

    // Méthodes utilitaires
    public void ajouterCompte(Compte compte) {
        compte.setClient(this);
        this.comptes.add(compte);
    }

    public CompteCourant getCompteCourant() {
        return comptes.stream()
                .filter(CompteCourant.class::isInstance)
                .map(CompteCourant.class::cast)
                .findFirst()
                .orElse(null);
    }

    public CompteEpargne getCompteEpargne() {
        return comptes.stream()
                .filter(CompteEpargne.class::isInstance)
                .map(CompteEpargne.class::cast)
                .findFirst()
                .orElse(null);
    }
}