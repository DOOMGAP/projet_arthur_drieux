package org.example.proxibanque.controller;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.Client;
import org.example.proxibanque.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients") // Note: c'est "/clients" pas "/api/clients"
public class ClientController {

    private final ClientService service;

    @PostMapping("/conseiller/{id}")
    public Client create(@RequestBody Client client, @PathVariable Long id) {
        return service.create(client, id);
    }

    @GetMapping("/conseiller/{id}")
    public List<Client> getClientsByConseiller(@PathVariable Long id) {
        return service.findByConseiller(id);
    }

    // Ajoute un endpoint pour récupérer tous les clients
    @GetMapping
    public List<Client> getAllClients() {
        return service.findAll();
    }
}