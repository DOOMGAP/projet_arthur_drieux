package org.example.proxibanque.service;

import org.example.proxibanque.entity.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CompteService {
    CompteCourant createCompteCourant(CompteCourant compteCourant, Long clientId);
    CompteEpargne createCompteEpargne(CompteEpargne compteEpargne, Long clientId);
    Optional<Compte> findById(Long id);
    List<Compte> findByClient(Long clientId);
    List<Compte> findAll();
    void effectuerVirement(Long compteSourceId, Long compteDestId, BigDecimal montant);
    void deposer(Long compteId, BigDecimal montant);
    void retirer(Long compteId, BigDecimal montant);
}