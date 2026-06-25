package pl.ezdrowie.eRecepta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PatientControllerIT {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private MockMvc mockMvc;

    private static final String PESEL = "12345678901";
    private static final PatientRequest VALID_REQUEST = new PatientRequest(PESEL, "Jan", "Kowalski");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        patientRepository.clear();
        prescriptionRepository.clear();
    }

    @Test
    void addPatient_returns201() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pesel").value(PESEL))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
    }

    @Test
    void addPatient_duplicate_returns409() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST)))
                .andExpect(status().isConflict());
    }

    @Test
    void addPatient_invalidPesel_returns400() throws Exception {
        PatientRequest invalid = new PatientRequest("123", "Jan", "Kowalski");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAllPatients_returnsArray() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPatient_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/patients/{pesel}", PESEL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deletePatient_success_returns204() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/patients/{pesel}", PESEL))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePatient_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/patients/{pesel}", PESEL))
                .andExpect(status().isNotFound());
    }
}
