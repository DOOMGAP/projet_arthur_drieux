package org.example.proxibanque.service;

import org.example.proxibanque.entity.Conseiller;

import java.util.List;
import java.util.Optional;

public interface ConseillerService {
    List<Conseiller> findAll();
    Optional<Conseiller> findById(Long id);
    Conseiller create(Conseiller conseiller);
}
