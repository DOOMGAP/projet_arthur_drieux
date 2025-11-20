package org.example.proxibanque;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.proxibanque.entity.*;
import org.example.proxibanque.repository.ClientRepository;
import org.example.proxibanque.repository.CompteRepository;
import org.example.proxibanque.repository.ConseillerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CompteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

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
    void testCreateCompteCourant() throws Exception {
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        compte.setAutorisationDecouvert(new BigDecimal("1000.00"));

        mockMvc.perform(post("/comptes/courant/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numeroCompte").value("CC123456"))
                .andExpect(jsonPath("$.autorisationDecouvert").value(1000.00))
                .andExpect(jsonPath("$.solde").value(0.00));
    }

    @Test
    void testCreateCompteEpargne() throws Exception {
        CompteEpargne compte = new CompteEpargne();
        compte.setNumeroCompte("CE789012");
        compte.setTauxRemuneration(new BigDecimal("0.03"));

        mockMvc.perform(post("/comptes/epargne/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numeroCompte").value("CE789012"))
                .andExpect(jsonPath("$.tauxRemuneration").value(0.03))
                .andExpect(jsonPath("$.solde").value(0.00));
    }

    @Test
    void testGetComptesByClient() throws Exception {
        // Créer d'abord des comptes
        CompteCourant cc = new CompteCourant();
        cc.setNumeroCompte("CC111111");
        mockMvc.perform(post("/comptes/courant/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cc)))
                .andExpect(status().isOk());

        CompteEpargne ce = new CompteEpargne();
        ce.setNumeroCompte("CE222222");
        mockMvc.perform(post("/comptes/epargne/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ce)))
                .andExpect(status().isOk());

        // Récupérer les comptes du client
        mockMvc.perform(get("/comptes/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numeroCompte").exists())
                .andExpect(jsonPath("$[1].numeroCompte").exists());
    }

    @Test
    void testDepot() throws Exception {
        // Créer un compte courant
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        String response = mockMvc.perform(post("/comptes/courant/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte)))
                .andReturn().getResponse().getContentAsString();

        CompteCourant created = mapper.readValue(response, CompteCourant.class);

        // Effectuer un dépôt
        mockMvc.perform(post("/comptes/{id}/depot", created.getId())
                        .param("montant", "1500.50"))
                .andExpect(status().isOk());

        // Vérifier le solde
        mockMvc.perform(get("/comptes/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solde").value(1500.50));
    }

    @Test
    void testRetrait() throws Exception {
        // Créer un compte courant
        CompteCourant compte = new CompteCourant();
        compte.setNumeroCompte("CC123456");
        String response = mockMvc.perform(post("/comptes/courant/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte)))
                .andReturn().getResponse().getContentAsString();

        CompteCourant created = mapper.readValue(response, CompteCourant.class);

        // Dépôt initial
        mockMvc.perform(post("/comptes/{id}/depot", created.getId())
                        .param("montant", "1000.00"))
                .andExpect(status().isOk());

        // Retrait
        mockMvc.perform(post("/comptes/{id}/retrait", created.getId())
                        .param("montant", "300.00"))
                .andExpect(status().isOk());

        // Vérifier le solde
        mockMvc.perform(get("/comptes/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solde").value(700.00));
    }

    @Test
    void testVirement() throws Exception {
        // Créer un deuxième client avec le même conseiller
        Client client2 = new Client();
        client2.setNom("Martin");
        client2.setPrenom("Marie");
        client2.setConseiller(clientRepo.findById(clientId).orElseThrow().getConseiller());
        Long client2Id = clientRepo.save(client2).getId();

        // Créer compte courant pour client 1
        CompteCourant compte1 = new CompteCourant();
        compte1.setNumeroCompte("CC111111");
        String response1 = mockMvc.perform(post("/comptes/courant/client/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte1)))
                .andReturn().getResponse().getContentAsString();
        CompteCourant created1 = mapper.readValue(response1, CompteCourant.class);

        // Créer compte courant pour client 2
        CompteCourant compte2 = new CompteCourant();
        compte2.setNumeroCompte("CC222222");
        String response2 = mockMvc.perform(post("/comptes/courant/client/{clientId}", client2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compte2)))
                .andReturn().getResponse().getContentAsString();
        CompteCourant created2 = mapper.readValue(response2, CompteCourant.class);

        // Dépôt sur le compte source
        mockMvc.perform(post("/comptes/{id}/depot", created1.getId())
                        .param("montant", "2000.00"))
                .andExpect(status().isOk());

        // Effectuer le virement
        mockMvc.perform(post("/comptes/virement")
                        .param("sourceId", created1.getId().toString())
                        .param("destId", created2.getId().toString())
                        .param("montant", "750.00"))
                .andExpect(status().isOk());

        // Vérifier les soldes
        mockMvc.perform(get("/comptes/{id}", created1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solde").value(1250.00));

        mockMvc.perform(get("/comptes/{id}", created2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solde").value(750.00));
    }
}