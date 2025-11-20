package org.example.proxibanque;

import org.example.proxibanque.entity.Conseiller;
import org.example.proxibanque.service.ClientService;

import org.example.proxibanque.service.ConseillerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConseillerServiceTest {

    @Autowired
    ConseillerService service;

    @Test
    void testCreateConseiller() {
        Conseiller c = new Conseiller();
        c.setNom("Dupont");
        c.setPrenom("Jean");

        Conseiller saved = service.create(c);

        assertNotNull(saved.getId());
        assertEquals("Dupont", saved.getNom());
    }

    @Test
    void testFindAll() {
        List<Conseiller> list = service.findAll();
        assertNotNull(list);
    }
}
