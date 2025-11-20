package org.example.proxibanque.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Agence {

    @Id
    private String id;

    private LocalDate dateCreation;

    @OneToOne
    @JsonManagedReference
    private Conseiller gerant;
}