package org.example.proxibanque;

import org.example.proxibanque.entity.*;
import org.example.proxibanque.repository.ClientRepository;
import org.example.proxibanque.repository.CompteRepository;
import org.example.proxibanque.repository.ConseillerRepository;
import org.example.proxibanque.service.CompteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompteServiceTest {

    @Autowired
    CompteService compteService;

    @Autowired
    ClientRepository clientRepo;

    @Autowired
    CompteRepository compteRepo;

    @Autowired
    ConseillerRepository conseillerRepo;

    Long clientId;

    @BeforeEach
    void setup() {
        compteRepo.deleteAll();
        clientRepo.deleteAll();
        conseillerRepo.deleteAll();

        // Créer et sauvegarder un conseiller d'abord
        Conseiller conseiller = new Conseiller();
        conseiller.setNom("Conseiller");
        conseiller.setPrenom("Test");
        Conseiller savedConseiller = conseillerRepo.save(conseiller);

        // Créer un client pour les tests
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setAdresse("123 Rue Test");
        client.setCodePostal("75000");
        client.setVille("Paris");
        client.setTelephone("0123456789");
        client.setConseiller(savedConseiller);

        clientId = clientRepo.save(client).getId();
    }

    @Test
    void testCreateCompteCourant() {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        compte.setAutorisationDecouvert(new BigDecimal("500.00"));

        CompteCourant saved = compteService.createCompteCourant(compte, clientId);

        assertNotNull(saved.getId());
        assertEquals("CC123456", saved.getNumeroCompte());
        assertEquals(new BigDecimal("500.00"), saved.getAutorisationDecouvert());
        assertEquals(BigDecimal.ZERO, saved.getSolde());
        assertNotNull(saved.getClient());
    }

    @Test
    void testCreateCompteEpargne() {
        CompteEpargne compte = new CompteEpargne();
        compte.setNumeroCompte("CE789012");
        compte.setTauxRemuneration(new BigDecimal("0.025")); // 2.5%

        CompteEpargne saved = compteService.createCompteEpargne(compte, clientId);

        assertNotNull(saved.getId());
        assertEquals("CE789012", saved.getNumeroCompte());
        assertEquals(new BigDecimal("0.025"), saved.getTauxRemuneration());
        assertEquals(BigDecimal.ZERO, saved.getSolde());
        assertNotNull(saved.getClient());
    }

    @Test
    void testCannotCreateMultipleCompteCourantForSameClient() {
        CompteCourant compte1 = new CompteCourant();
        compte1.setNumeroCompte("CC111111");
        compteService.createCompteCourant(compte1, clientId);

        CompteCourant compte2 = new CompteCourant();
        compte2.setNumeroCompte("CC222222");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.createCompteCourant(compte2, clientId);
        });

        assertEquals("Le client a déjà un compte courant", exception.getMessage());
    }

    @Test
    void testCannotCreateMultipleCompteEpargneForSameClient() {
        CompteEpargne compte1 = new CompteEpargne();
        compte1.setNumeroCompte("CE111111");
        compteService.createCompteEpargne(compte1, clientId);

        CompteEpargne compte2 = new CompteEpargne();
        compte2.setNumeroCompte("CE222222");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.createCompteEpargne(compte2, clientId);
        });

        assertEquals("Le client a déjà un compte épargne", exception.getMessage());
    }

    @Test
    void testDepotCompteCourant() {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        CompteCourant saved = compteService.createCompteCourant(compte, clientId);

        compteService.deposer(saved.getId(), new BigDecimal("1000.00"));

        Compte updated = compteService.findById(saved.getId()).orElseThrow();
        assertEquals(new BigDecimal("1000.00"), updated.getSolde());
    }

    @Test
    void testDepotCompteEpargne() {
        CompteEpargne compte = new CompteEpargne();
        compte.setNumeroCompte("CE789012");
        CompteEpargne saved = compteService.createCompteEpargne(compte, clientId);

        compteService.deposer(saved.getId(), new BigDecimal("5000.00"));

        Compte updated = compteService.findById(saved.getId()).orElseThrow();
        assertEquals(new BigDecimal("5000.00"), updated.getSolde());
    }

    @Test
    void testRetraitCompteCourantAvecDecouvert() {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        compte.setAutorisationDecouvert(new BigDecimal("500.00"));
        CompteCourant saved = compteService.createCompteCourant(compte, clientId);

        // Dépôt initial
        compteService.deposer(saved.getId(), new BigDecimal("200.00"));

        // Retrait avec découvert
        compteService.retirer(saved.getId(), new BigDecimal("600.00"));

        Compte updated = compteService.findById(saved.getId()).orElseThrow();
        assertEquals(new BigDecimal("-400.00"), updated.getSolde());
    }

    @Test
    void testRetraitCompteCourantSansFondsSuffisants() {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        compte.setAutorisationDecouvert(new BigDecimal("500.00"));
        CompteCourant saved = compteService.createCompteCourant(compte, clientId);

        // Dépôt initial
        compteService.deposer(saved.getId(), new BigDecimal("200.00"));

        // Tentative de retrait au-delà du découvert autorisé
        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.retirer(saved.getId(), new BigDecimal("800.00"));
        });

        assertEquals("Fonds insuffisants", exception.getMessage());
    }

    @Test
    void testRetraitCompteEpargneSansFondsSuffisants() {
        CompteEpargne compte = new CompteEpargne();
        compte.setNumeroCompte("CE789012");
        CompteEpargne saved = compteService.createCompteEpargne(compte, clientId);

        // Dépôt initial
        compteService.deposer(saved.getId(), new BigDecimal("100.00"));

        // Tentative de retrait sans fonds suffisants
        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.retirer(saved.getId(), new BigDecimal("200.00"));
        });

        assertEquals("Fonds insuffisants", exception.getMessage());
    }

    @Test
    void testVirementEntreComptesCourants() {
        // Créer deux clients
        Client client2 = new Client();
        client2.setNom("Martin");
        client2.setPrenom("Marie");
        client2.setConseiller(clientRepo.findById(clientId).orElseThrow().getConseiller());
        Long client2Id = clientRepo.save(client2).getId();

        // Créer comptes courants
        CompteCourant compte1 = new CompteCourant();
        compte1.setNumeroCompte("CC111111");
        CompteCourant compte2 = new CompteCourant();
        compte2.setNumeroCompte("CC222222");

        CompteCourant saved1 = compteService.createCompteCourant(compte1, clientId);
        CompteCourant saved2 = compteService.createCompteCourant(compte2, client2Id);

        // Dépôt sur le compte source
        compteService.deposer(saved1.getId(), new BigDecimal("1000.00"));

        // Virement
        compteService.effectuerVirement(saved1.getId(), saved2.getId(), new BigDecimal("300.00"));

        // Vérifier les soldes
        Compte updated1 = compteService.findById(saved1.getId()).orElseThrow();
        Compte updated2 = compteService.findById(saved2.getId()).orElseThrow();

        assertEquals(new BigDecimal("700.00"), updated1.getSolde());
        assertEquals(new BigDecimal("300.00"), updated2.getSolde());
    }

    @Test
    void testVirementCompteCourantVersCompteEpargne() {
        // Créer compte courant
        CompteCourant compteCourant = new CompteCourant();
        compteCourant.setNumeroCompte("CC111111");
        CompteCourant savedCC = compteService.createCompteCourant(compteCourant, clientId);

        // Créer compte épargne pour le même client
        CompteEpargne compteEpargne = new CompteEpargne();
        compteEpargne.setNumeroCompte("CE222222");
        CompteEpargne savedCE = compteService.createCompteEpargne(compteEpargne, clientId);

        // Dépôt sur le compte courant
        compteService.deposer(savedCC.getId(), new BigDecimal("2000.00"));

        // Virement vers compte épargne
        compteService.effectuerVirement(savedCC.getId(), savedCE.getId(), new BigDecimal("500.00"));

        // Vérifier les soldes
        Compte updatedCC = compteService.findById(savedCC.getId()).orElseThrow();
        Compte updatedCE = compteService.findById(savedCE.getId()).orElseThrow();

        assertEquals(new BigDecimal("1500.00"), updatedCC.getSolde());
        assertEquals(new BigDecimal("500.00"), updatedCE.getSolde());
    }

    @Test
    void testVirementSansFondsSuffisants() {
        // Créer deux clients
        Client client2 = new Client();
        client2.setNom("Martin");
        client2.setPrenom("Marie");
        client2.setConseiller(clientRepo.findById(clientId).orElseThrow().getConseiller());
        Long client2Id = clientRepo.save(client2).getId();

        // Créer comptes courants
        CompteCourant compte1 = new CompteCourant();
        compte1.setNumeroCompte("CC111111");
        CompteCourant compte2 = new CompteCourant();
        compte2.setNumeroCompte("CC222222");

        CompteCourant saved1 = compteService.createCompteCourant(compte1, clientId);
        CompteCourant saved2 = compteService.createCompteCourant(compte2, client2Id);

        // Dépôt minimal
        compteService.deposer(saved1.getId(), new BigDecimal("100.00"));

        // Tentative de virement sans fonds suffisants
        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.effectuerVirement(saved1.getId(), saved2.getId(), new BigDecimal("500.00"));
        });

        assertEquals("Fonds insuffisants pour effectuer le virement", exception.getMessage());
    }

    @Test
    void testCalculInteretsCompteEpargne() {
        CompteEpargne compte = new CompteEpargne();
        compte.setNumeroCompte("CE789012");
        compte.setTauxRemuneration(new BigDecimal("0.05")); // 5%
        CompteEpargne saved = compteService.createCompteEpargne(compte, clientId);

        // Dépôt
        compteService.deposer(saved.getId(), new BigDecimal("10000.00"));

        BigDecimal interets = saved.calculerInteretsAnnuels();
        assertEquals(new BigDecimal("500.00"), interets); // 10000 * 0.05 = 500
    }

    @Test
    void testFindComptesByClient() {
        // Créer compte courant
        CompteCourant cc = new CompteCourant();
        cc.setNumeroCompte("CC111111");
        compteService.createCompteCourant(cc, clientId);

        // Créer compte épargne
        CompteEpargne ce = new CompteEpargne();
        ce.setNumeroCompte("CE222222");
        compteService.createCompteEpargne(ce, clientId);

        List<Compte> comptes = compteService.findByClient(clientId);

        assertEquals(2, comptes.size());
        assertTrue(comptes.stream().anyMatch(c -> c.getNumeroCompte().equals("CC111111")));
        assertTrue(comptes.stream().anyMatch(c -> c.getNumeroCompte().equals("CE222222")));
    }

    @Test
    void testDepotMontantNegatif() {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        CompteCourant saved = compteService.createCompteCourant(compte, clientId);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.deposer(saved.getId(), new BigDecimal("-100.00"));
        });

        assertEquals("Le montant doit être positif", exception.getMessage());
    }

    @Test
    void testVirementMontantNegatif() {
        CompteCourant compte1 = new CompteCourant();
        compte1.setNumeroCompte("CC111111");
        CompteCourant compte2 = new CompteCourant();
        compte2.setNumeroCompte("CC222222");

        CompteCourant saved1 = compteService.createCompteCourant(compte1, clientId);

        Client client2 = new Client();
        client2.setNom("Test");
        client2.setPrenom("Client2");
        client2.setConseiller(clientRepo.findById(clientId).orElseThrow().getConseiller());
        Long client2Id = clientRepo.save(client2).getId();
        CompteCourant saved2 = compteService.createCompteCourant(compte2, client2Id);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            compteService.effectuerVirement(saved1.getId(), saved2.getId(), new BigDecimal("-50.00"));
        });

        assertEquals("Le montant doit être positif", exception.getMessage());
    }
}