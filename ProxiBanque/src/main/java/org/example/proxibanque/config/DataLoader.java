package org.example.proxibanque.config;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.*;
import org.example.proxibanque.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ConseillerRepository conseillerRepo;
    private final ClientRepository clientRepo;
    private final CompteRepository compteRepo;
    private final AgenceRepository agenceRepo;

    @Override
    public void run(String... args) throws Exception {
        // Nettoyer la base d'abord
        System.out.println("=== NETTOYAGE DE LA BASE DE DONNÉES ===");
        compteRepo.deleteAll();
        clientRepo.deleteAll();
        conseillerRepo.deleteAll();
        agenceRepo.deleteAll();

        // Petite pause pour être sûr que tout est nettoyé
        Thread.sleep(100);

        System.out.println("=== CRÉATION DES NOUVELLES DONNÉES ===");

        // 1. Créer une agence
        Agence agence = new Agence();
        agence.setId("AG001");
        agence.setDateCreation(java.time.LocalDate.now());
        agence = agenceRepo.save(agence);

        // 2. Créer des conseillers
        Conseiller conseiller1 = new Conseiller();
        conseiller1.setNom("Dupont");
        conseiller1.setPrenom("Marie");
        conseiller1.setAgence(agence);
        conseiller1 = conseillerRepo.save(conseiller1);

        Conseiller conseiller2 = new Conseiller();
        conseiller2.setNom("Martin");
        conseiller2.setPrenom("Pierre");
        conseiller2.setAgence(agence);
        conseiller2 = conseillerRepo.save(conseiller2);

        // Définir le gérant de l'agence
        agence.setGerant(conseiller1);
        agenceRepo.save(agence);

        // 3. Créer des clients pour le conseiller 1
        Client client1 = new Client();
        client1.setNom("Bernard");
        client1.setPrenom("Sophie");
        client1.setAdresse("15 Rue de la République");
        client1.setCodePostal("75001");
        client1.setVille("Paris");
        client1.setTelephone("01 23 45 67 89");
        client1.setConseiller(conseiller1);
        client1 = clientRepo.save(client1);

        Client client2 = new Client();
        client2.setNom("Moreau");
        client2.setPrenom("Luc");
        client2.setAdresse("22 Avenue des Champs-Élysées");
        client2.setCodePostal("75008");
        client2.setVille("Paris");
        client2.setTelephone("01 34 56 78 90");
        client2.setConseiller(conseiller1);
        client2 = clientRepo.save(client2);

        // 4. Créer des clients pour le conseiller 2
        Client client3 = new Client();
        client3.setNom("Petit");
        client3.setPrenom("Isabelle");
        client3.setAdresse("8 Rue du Commerce");
        client3.setCodePostal("69001");
        client3.setVille("Lyon");
        client3.setTelephone("04 12 34 56 78");
        client3.setConseiller(conseiller2);
        client3 = clientRepo.save(client3);

        Client client4 = new Client();
        client4.setNom("Robert");
        client4.setPrenom("Antoine");
        client4.setAdresse("45 Cours de la Liberté");
        client4.setCodePostal("13001");
        client4.setVille("Marseille");
        client4.setTelephone("04 91 23 45 67");
        client4.setConseiller(conseiller2);
        client4 = clientRepo.save(client4);

        // 5. Créer des comptes pour les clients (AVEC LES NOUVEAUX CONSTRUCTEURS)

        // Comptes pour client1 (Sophie Bernard)
        CompteCourant cc1 = new CompteCourant(); // Utilise constructeur par défaut
        cc1.setNumeroCompte("CC001");
        cc1.setSolde(new BigDecimal("3500.00"));
        cc1.setAutorisationDecouvert(new BigDecimal("1000.00"));
        cc1.setClient(client1);
        compteRepo.save(cc1);

        CompteEpargne ce1 = new CompteEpargne(); // Utilise constructeur par défaut
        ce1.setNumeroCompte("CE001");
        ce1.setSolde(new BigDecimal("12500.00"));
        ce1.setTauxRemuneration(new BigDecimal("0.03"));
        ce1.setClient(client1);
        compteRepo.save(ce1);

        // Comptes pour client2 (Luc Moreau)
        CompteCourant cc2 = new CompteCourant();
        cc2.setNumeroCompte("CC002");
        cc2.setSolde(new BigDecimal("2800.50"));
        cc2.setAutorisationDecouvert(new BigDecimal("1500.00"));
        cc2.setClient(client2);
        compteRepo.save(cc2);

        // Comptes pour client3 (Isabelle Petit) - Client fortuné
        CompteCourant cc3 = new CompteCourant();
        cc3.setNumeroCompte("CC003");
        cc3.setSolde(new BigDecimal("15000.00"));
        cc3.setAutorisationDecouvert(new BigDecimal("2000.00"));
        cc3.setClient(client3);
        compteRepo.save(cc3);

        CompteEpargne ce3 = new CompteEpargne();
        ce3.setNumeroCompte("CE003");
        ce3.setSolde(new BigDecimal("250000.00")); // Client fortuné > 500 000€
        ce3.setTauxRemuneration(new BigDecimal("0.035"));
        ce3.setClient(client3);
        compteRepo.save(ce3);

        // Comptes pour client4 (Antoine Robert)
        CompteCourant cc4 = new CompteCourant();
        cc4.setNumeroCompte("CC004");
        cc4.setSolde(new BigDecimal("4200.75"));
        cc4.setAutorisationDecouvert(new BigDecimal("1000.00"));
        cc4.setClient(client4);
        compteRepo.save(cc4);

        CompteEpargne ce4 = new CompteEpargne();
        ce4.setNumeroCompte("CE004");
        ce4.setSolde(new BigDecimal("8500.00"));
        ce4.setTauxRemuneration(new BigDecimal("0.025"));
        ce4.setClient(client4);
        compteRepo.save(ce4);

        System.out.println("=== DONNÉES DE TEST CRÉÉES AVEC SUCCÈS ===");
        System.out.println("Conseillers: 2");
        System.out.println("Clients: 4");
        System.out.println("Comptes: 7 (4 courants + 3 épargne)");
        System.out.println("Agence: 1");
        System.out.println("URL Console H2: http://localhost:8080/h2-console");
        System.out.println("URL Clients: http://localhost:8080/clients");
        System.out.println("URL Conseillers: http://localhost:8080/conseillers");
    }
}