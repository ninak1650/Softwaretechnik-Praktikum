package de.uni_leipzig.swtp.borna_lecker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;
import de.uni_leipzig.swtp.borna_lecker.util.JwtUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureEmbeddedDatabase
public class KundenControllerIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private KundeRepository kundeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void contextLoads() {
        assert kundeRepository != null;
    }

    @Test
    public void givenKunde_whenGetKunde_thenStatus200() throws Exception {
        Kunde kunde = new Kunde();
        kunde.setKundenNummer(100);
        kunde.setName("Test");
        kunde.setStandort("Test");
        kundeRepository.save(kunde);
        String token = jwtUtil.generateTestToken("Test", Rolle.VERWALTUNG, "Test", 1);

        mvc.perform(MockMvcRequestBuilders.get("/kunden/100").accept(MediaType.APPLICATION_JSON).header("Authorization",
                "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kundenNummer").value(100));
    }

    @Test
    public void givenNoKunde_whenGetKunde_thenStatus404() throws Exception {
        String token = jwtUtil.generateTestToken("Test", Rolle.VERWALTUNG, "Test", 1);
        mvc.perform(
                MockMvcRequestBuilders.get("/kunden/1000").accept(MediaType.APPLICATION_JSON).header("Authorization",
                        "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void givenKunde_whenUpdateKunde_thenStatus200() throws Exception {
        Kunde kunde = new Kunde();
        kunde.setKundenNummer(110);
        kunde.setName("Test");
        kunde.setStandort("Test");
        kundeRepository.save(kunde);
        String token = jwtUtil.generateTestToken("Test", Rolle.VERWALTUNG, "Test", 1);
        mvc.perform(MockMvcRequestBuilders.post("/kunden/110").contentType(MediaType.APPLICATION_JSON).content(
                "{\"name\": \"Test2\", \"standort\": \"Test2\", \"gruppenNummer\": 0}").header("Authorization",
                        "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Kunde kunde2 = kundeRepository.findByKundenNummer(110);
        assert kunde2 != null;
        assert kunde2.getName().equals("Test2");
        assert kunde2.getStandort().equals("Test2");
    }
}
