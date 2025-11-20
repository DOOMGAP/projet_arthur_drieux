package org.example.proxibanque.controller;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.*;
import org.example.proxibanque.service.CompteService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService service;

    @PostMapping("/courant/client/{clientId}")
    public CompteCourant createCompteCourant(@RequestBody CompteCourant compte,
                                             @PathVariable Long clientId) {
        return service.createCompteCourant(compte, clientId);
    }

    @PostMapping("/epargne/client/{clientId}")
    public CompteEpargne createCompteEpargne(@RequestBody CompteEpargne compte,
                                             @PathVariable Long clientId) {
        return service.createCompteEpargne(compte, clientId);
    }

    @GetMapping("/{id}")
    public Optional<Compte> getCompte(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/client/{clientId}")
    public List<Compte> getComptesByClient(@PathVariable Long clientId) {
        return service.findByClient(clientId);
    }

    @PostMapping("/virement")
    public void effectuerVirement(@RequestParam Long sourceId,
                                  @RequestParam Long destId,
                                  @RequestParam BigDecimal montant) {
        service.effectuerVirement(sourceId, destId, montant);
    }

    @PostMapping("/{id}/depot")
    public void deposer(@PathVariable Long id, @RequestParam BigDecimal montant) {
        service.deposer(id, montant);
    }

    @PostMapping("/{id}/retrait")
    public void retirer(@PathVariable Long id, @RequestParam BigDecimal montant) {
        service.retirer(id, montant);
    }
}