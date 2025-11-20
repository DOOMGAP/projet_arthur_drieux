package org.example.proxibanque;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.proxibanque.entity.*;

import org.example.proxibanque.repository.ConseillerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ConseillerRepository conseillerRepo;

    Long conseillerId;

    @BeforeEach
    void setup() {
        Conseiller c = new Conseiller();
        c.setNom("Test");
        c.setPrenom("Conseiller");
        conseillerId = conseillerRepo.save(c).getId();
    }

    @Test
    void testCreateClient() throws Exception {
        Client client = new Client();
        client.setNom("Martin");
        client.setPrenom("Paul");
        client.setAdresse("1 Rue A");
        client.setTelephone("0102030405");

        mockMvc.perform(post("/clients/conseiller/" + conseillerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("Martin"));
    }

    @Test
    void testGetClientsByConseiller() throws Exception {
        mockMvc.perform(get("/clients/conseiller/" + conseillerId))
                .andExpect(status().isOk());
    }
}

