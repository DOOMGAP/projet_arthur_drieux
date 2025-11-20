package org.example.proxibanque.entity;

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
    private List<Client> clients;
}
