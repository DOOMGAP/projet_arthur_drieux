package org.example.proxibanque.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Conseiller {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private String prenom;

    @OneToMany(mappedBy = "conseiller", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Client> clients;

    @ManyToOne
    @JsonBackReference
    private Agence agence;
}