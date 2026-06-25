package pl.ezdrowie.eRecepta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.model.Prescription;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PrescriptionControllerIT {

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
    void setUp() throws Exception {
        patientRepository.clear();
        prescriptionRepository.clear();

        PatientRequest patient = new PatientRequest(PESEL, "Jan", "Kowalski");
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)));
    }

    @Test
    void addPrescription_returns201() throws Exception {
        PrescriptionRequest request = new PrescriptionRequest("Ibuprom", "200mg");

        mockMvc.perform(post("/api/patients/{pesel}/prescriptions", PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicationName").value("Ibuprom"))
                .andExpect(jsonPath("$.dosage").value("200mg"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void addPrescription_patientNotFound_returns404() throws Exception {
        PrescriptionRequest request = new PrescriptionRequest("Ibuprom", "200mg");

        mockMvc.perform(post("/api/patients/{pesel}/prescriptions", "99999999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPrescriptions_returnsList() throws Exception {
        PrescriptionRequest request = new PrescriptionRequest("Aspirin", "100mg");
        mockMvc.perform(post("/api/patients/{pesel}/prescriptions", PESEL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/patients/{pesel}/prescriptions", PESEL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationName").value("Aspirin"));
    }

    @Test
    void deletePrescription_success_returns204() throws Exception {
        PrescriptionRequest request = new PrescriptionRequest("Ibuprom", "200mg");
        MvcResult result = mockMvc.perform(post("/api/patients/{pesel}/prescriptions", PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Prescription prescription = objectMapper.readValue(result.getResponse().getContentAsString(), Prescription.class);

        mockMvc.perform(delete("/api/patients/{pesel}/prescriptions/{id}", PESEL, prescription.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePrescription_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/patients/{pesel}/prescriptions/{id}",
                        PESEL, "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
