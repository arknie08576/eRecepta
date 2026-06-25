package pl.ezdrowie.eRecepta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private static final String PESEL = "12345678901";

    @BeforeEach
    void setUp() {
        patientRepository.clear();
        prescriptionRepository.clear();
    }

    @Test
    void addPatient_returns201() throws Exception {
        PatientRequest request = new PatientRequest(PESEL, "Jan", "Kowalski");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pesel").value(PESEL))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
    }

    @Test
    void addPatient_duplicate_returns409() throws Exception {
        PatientRequest request = new PatientRequest(PESEL, "Jan", "Kowalski");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void addPatient_invalidPesel_returns400() throws Exception {
        PatientRequest request = new PatientRequest("123", "Jan", "Kowalski");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAllPatients_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPatient_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/patients/{pesel}", PESEL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deletePatient_success_returns204() throws Exception {
        PatientRequest request = new PatientRequest(PESEL, "Jan", "Kowalski");
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
