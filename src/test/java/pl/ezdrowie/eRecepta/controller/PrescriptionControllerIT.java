package pl.ezdrowie.eRecepta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.dto.PrescriptionResponse;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PrescriptionControllerIT {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private MockMvc mockMvc;

    private static final String PESEL = "90010100009";
    private static final String PRESCRIPTIONS_URL = "/api/patients/{pesel}/prescriptions";

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        patientRepository.clear();
        prescriptionRepository.clear();

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatientRequest(PESEL, "Jan", "Kowalski"))));
    }

    @Test
    void addPrescription_returns201() throws Exception {
        PrescriptionRequest request = new PrescriptionRequest("Ibuprom", "200mg");

        mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.medicationName").value("Ibuprom"))
                .andExpect(jsonPath("$.dosage").value("200mg"))
                .andExpect(jsonPath("$.pesel").doesNotExist());
    }

    @Test
    void addPrescription_patientNotFound_returns404() throws Exception {
        mockMvc.perform(post(PRESCRIPTIONS_URL, "99999999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PrescriptionRequest("Ibuprom", "200mg"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void addPrescription_blankMedicationName_returns400() throws Exception {
        mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PrescriptionRequest("", "200mg"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addPrescription_blankDosage_returns400() throws Exception {
        mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PrescriptionRequest("Ibuprom", "  "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getPrescriptions_returnsList() throws Exception {
        mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PrescriptionRequest("Aspirin", "100mg"))));

        mockMvc.perform(get(PRESCRIPTIONS_URL, PESEL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationName").value("Aspirin"));
    }

    @Test
    void deletePrescription_success_returns204() throws Exception {
        MvcResult result = mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PrescriptionRequest("Ibuprom", "200mg"))))
                .andReturn();

        PrescriptionResponse prescription = objectMapper.readValue(result.getResponse().getContentAsString(), PrescriptionResponse.class);

        mockMvc.perform(delete(PRESCRIPTIONS_URL + "/{id}", PESEL, prescription.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePrescription_notFound_returns404() throws Exception {
        mockMvc.perform(delete(PRESCRIPTIONS_URL + "/{id}", PESEL, "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePrescription_malformedId_returns400() throws Exception {
        mockMvc.perform(delete(PRESCRIPTIONS_URL + "/{id}", PESEL, "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deletingPatient_cascadeRemovesPrescriptions() throws Exception {
        // given a prescription for the patient created in setUp
        mockMvc.perform(post(PRESCRIPTIONS_URL, PESEL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PrescriptionRequest("Ibuprom", "200mg"))))
                .andExpect(status().isCreated());

        // when the patient is deleted
        mockMvc.perform(delete("/api/patients/{pesel}", PESEL))
                .andExpect(status().isNoContent());

        // then re-creating the same patient shows no leftover prescriptions
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatientRequest(PESEL, "Jan", "Kowalski"))));

        mockMvc.perform(get(PRESCRIPTIONS_URL, PESEL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
