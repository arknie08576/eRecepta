package pl.ezdrowie.eRecepta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.exception.PatientAlreadyExistsException;
import pl.ezdrowie.eRecepta.exception.PatientNotFoundException;
import pl.ezdrowie.eRecepta.model.Patient;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PatientService patientService;

    private static final String PESEL = "90010100009";
    private static final PatientRequest REQUEST = new PatientRequest(PESEL, "Jan", "Kowalski");

    @Test
    void addPatient_success() {
        when(patientRepository.existsByPesel(PESEL)).thenReturn(false);

        Patient result = patientService.addPatient(REQUEST);

        assertThat(result.getPesel()).isEqualTo(PESEL);
        assertThat(result.getFirstName()).isEqualTo("Jan");
        assertThat(result.getLastName()).isEqualTo("Kowalski");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void addPatient_alreadyExists_throwsException() {
        when(patientRepository.existsByPesel(PESEL)).thenReturn(true);

        assertThatThrownBy(() -> patientService.addPatient(REQUEST))
                .isInstanceOf(PatientAlreadyExistsException.class)
                .hasMessageContaining(PESEL);
    }

    @Test
    void getPatientByPesel_success() {
        Patient patient = Patient.builder().pesel(PESEL).firstName("Jan").lastName("Kowalski").build();
        when(patientRepository.findByPesel(PESEL)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByPesel(PESEL);

        assertThat(result).isEqualTo(patient);
    }

    @Test
    void getPatientByPesel_notFound_throwsException() {
        when(patientRepository.findByPesel(PESEL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientByPesel(PESEL))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining(PESEL);
    }

    @Test
    void deletePatient_cascadeDeletesPrescriptions() {
        when(patientRepository.deleteByPesel(PESEL)).thenReturn(true);

        patientService.deletePatient(PESEL);

        verify(patientRepository).deleteByPesel(PESEL);
        verify(prescriptionRepository).deleteAllByPesel(PESEL);
    }

    @Test
    void deletePatient_notFound_throwsException() {
        when(patientRepository.deleteByPesel(PESEL)).thenReturn(false);

        assertThatThrownBy(() -> patientService.deletePatient(PESEL))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining(PESEL);

        verify(prescriptionRepository, never()).deleteAllByPesel(any());
    }
}
