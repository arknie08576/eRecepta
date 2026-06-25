package pl.ezdrowie.eRecepta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.exception.PatientNotFoundException;
import pl.ezdrowie.eRecepta.exception.PrescriptionNotFoundException;
import pl.ezdrowie.eRecepta.model.Prescription;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private static final String PESEL = "12345678901";
    private static final PrescriptionRequest REQUEST = new PrescriptionRequest("Ibuprom", "200mg");

    @Test
    void addPrescription_success() {
        when(patientRepository.existsByPesel(PESEL)).thenReturn(true);
        Prescription saved = Prescription.builder()
                .id(UUID.randomUUID()).pesel(PESEL)
                .medicationName("Ibuprom").dosage("200mg").build();
        when(prescriptionRepository.save(eq(PESEL), any())).thenReturn(saved);

        Prescription result = prescriptionService.addPrescription(PESEL, REQUEST);

        assertThat(result.getMedicationName()).isEqualTo("Ibuprom");
        assertThat(result.getDosage()).isEqualTo("200mg");
    }

    @Test
    void addPrescription_patientNotFound_throwsException() {
        when(patientRepository.existsByPesel(PESEL)).thenReturn(false);

        assertThatThrownBy(() -> prescriptionService.addPrescription(PESEL, REQUEST))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining(PESEL);
    }

    @Test
    void getPrescriptionsByPesel_success() {
        when(patientRepository.existsByPesel(PESEL)).thenReturn(true);
        List<Prescription> prescriptions = List.of(
                Prescription.builder().id(UUID.randomUUID()).pesel(PESEL)
                        .medicationName("Aspirin").dosage("100mg").build()
        );
        when(prescriptionRepository.findAllByPesel(PESEL)).thenReturn(prescriptions);

        List<Prescription> result = prescriptionService.getPrescriptionsByPesel(PESEL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMedicationName()).isEqualTo("Aspirin");
    }

    @Test
    void deletePrescription_success() {
        UUID id = UUID.randomUUID();
        when(patientRepository.existsByPesel(PESEL)).thenReturn(true);
        when(prescriptionRepository.deleteById(PESEL, id)).thenReturn(true);

        prescriptionService.deletePrescription(PESEL, id);

        verify(prescriptionRepository).deleteById(PESEL, id);
    }

    @Test
    void deletePrescription_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(patientRepository.existsByPesel(PESEL)).thenReturn(true);
        when(prescriptionRepository.deleteById(PESEL, id)).thenReturn(false);

        assertThatThrownBy(() -> prescriptionService.deletePrescription(PESEL, id))
                .isInstanceOf(PrescriptionNotFoundException.class);
    }
}
