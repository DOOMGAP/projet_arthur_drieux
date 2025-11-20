package org.example.proxibanque.service;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.*;
import org.example.proxibanque.repository.ClientRepository;
import org.example.proxibanque.repository.CompteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompteServiceImpl implements CompteService {

    private final CompteRepository compteRepo;
    private final ClientRepository clientRepo;

    @Override
    @Transactional
    public CompteCourant createCompteCourant(CompteCourant compteCourant, Long clientId) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Vérifier si le client a déjà un compte courant
        if (client.getCompteCourant() != null) {
            throw new RuntimeException("Le client a déjà un compte courant");
        }

        client.ajouterCompte(compteCourant);
        return (CompteCourant) compteRepo.save(compteCourant);
    }

    @Override
    @Transactional
    public CompteEpargne createCompteEpargne(CompteEpargne compteEpargne, Long clientId) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Vérifier si le client a déjà un compte épargne
        if (client.getCompteEpargne() != null) {
            throw new RuntimeException("Le client a déjà un compte épargne");
        }

        client.ajouterCompte(compteEpargne);
        return (CompteEpargne) compteRepo.save(compteEpargne);
    }

    @Override
    public Optional<Compte> findById(Long id) {
        return compteRepo.findById(id);
    }

    @Override
    public List<Compte> findByClient(Long clientId) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return client.getComptes();
    }

    @Override
    @Transactional
    public void effectuerVirement(Long compteSourceId, Long compteDestId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant doit être positif");
        }

        Compte source = compteRepo.findById(compteSourceId)
                .orElseThrow(() -> new RuntimeException("Compte source non trouvé"));
        Compte destination = compteRepo.findById(compteDestId)
                .orElseThrow(() -> new RuntimeException("Compte destination non trouvé"));

        // Vérifier les fonds pour le compte courant
        if (source instanceof CompteCourant compteCourant) {
            if (!compteCourant.peutRetirer(montant)) {
                throw new RuntimeException("Fonds insuffisants pour effectuer le virement");
            }
        } else if (source.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Fonds insuffisants pour effectuer le virement");
        }

        // Effectuer le virement
        source.setSolde(source.getSolde().subtract(montant));
        destination.setSolde(destination.getSolde().add(montant));

        compteRepo.save(source);
        compteRepo.save(destination);
    }

    @Override
    @Transactional
    public void deposer(Long compteId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant doit être positif");
        }

        Compte compte = compteRepo.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        compte.setSolde(compte.getSolde().add(montant));
        compteRepo.save(compte);
    }

    @Override
    @Transactional
    public void retirer(Long compteId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant doit être positif");
        }

        Compte compte = compteRepo.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        if (compte instanceof CompteCourant compteCourant) {
            if (!compteCourant.peutRetirer(montant)) {
                throw new RuntimeException("Fonds insuffisants");
            }
        } else if (compte.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Fonds insuffisants");
        }

        compte.setSolde(compte.getSolde().subtract(montant));
        compteRepo.save(compte);
    }
}