package org.example.proxibanque;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.proxibanque.entity.*;


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
public class ConseillerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void testCreateConseiller() throws Exception {
        Conseiller conseiller = new Conseiller();
        conseiller.setNom("Dupont");
        conseiller.setPrenom("Jean");

        mockMvc.perform(post("/conseillers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(conseiller)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    void testGetAllConseillers() throws Exception {
        mockMvc.perform(get("/conseillers"))
                .andExpect(status().isOk());
    }
}
