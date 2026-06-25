package pl.ezdrowie.eRecepta.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.exception.PatientAlreadyExistsException;
import pl.ezdrowie.eRecepta.exception.PatientNotFoundException;
import pl.ezdrowie.eRecepta.model.Patient;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public Patient addPatient(PatientRequest request) {
        if (patientRepository.existsByPesel(request.pesel())) {
            throw new PatientAlreadyExistsException(request.pesel());
        }
        Patient patient = Patient.builder()
                .pesel(request.pesel())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
        patientRepository.save(patient);
        return patient;
    }

    public Collection<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientByPesel(String pesel) {
        return patientRepository.findByPesel(pesel)
                .orElseThrow(() -> new PatientNotFoundException(pesel));
    }

    public void deletePatient(String pesel) {
        if (!patientRepository.deleteByPesel(pesel)) {
            throw new PatientNotFoundException(pesel);
        }
        prescriptionRepository.deleteAllByPesel(pesel);
    }
}
