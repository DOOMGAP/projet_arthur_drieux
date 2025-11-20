package org.example.proxibanque;

import org.example.proxibanque.entity.*;


import org.example.proxibanque.repository.ClientRepository;
import org.example.proxibanque.repository.ConseillerRepository;
import org.example.proxibanque.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClientServiceTest {

    @Autowired
    ClientService service;

    @Autowired
    ConseillerRepository conseillerRepo;

    @Autowired
    ClientRepository clientRepo;

    Long conseillerId;

    @BeforeEach
    void setup() {
        clientRepo.deleteAll();
        conseillerRepo.deleteAll();

        Conseiller c = new Conseiller();
        c.setNom("Service");
        c.setPrenom("Test");
        conseillerId = conseillerRepo.save(c).getId();
    }

    @Test
    void testCreateClient() {
        Client client = new Client();
        client.setNom("Nom");
        client.setPrenom("Prenom");
        client.setAdresse("Somewhere");
        client.setTelephone("123");

        Client saved = service.create(client, conseillerId);

        assertNotNull(saved.getId());
        assertEquals("Nom", saved.getNom());
        assertEquals(conseillerId, saved.getConseiller().getId());
    }

    @Test
    void testFindClientsByConseiller() {
        Client c = new Client();
        c.setNom("X");
        c.setPrenom("Y");
        service.create(c, conseillerId);

        List<Client> clients = service.findByConseiller(conseillerId);
        assertEquals(1, clients.size());
    }
}

