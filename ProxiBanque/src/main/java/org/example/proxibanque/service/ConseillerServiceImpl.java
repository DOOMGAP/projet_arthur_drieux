package org.example.proxibanque.service;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.Conseiller;
import org.example.proxibanque.repository.ConseillerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConseillerServiceImpl implements ConseillerService {

    private final ConseillerRepository repo;

    @Override
    public List<Conseiller> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Conseiller> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Conseiller create(Conseiller conseiller) {
        return repo.save(conseiller);
    }
}
