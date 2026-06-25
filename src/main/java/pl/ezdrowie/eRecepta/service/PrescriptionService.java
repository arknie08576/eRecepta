package pl.ezdrowie.eRecepta.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.exception.PatientNotFoundException;
import pl.ezdrowie.eRecepta.exception.PrescriptionNotFoundException;
import pl.ezdrowie.eRecepta.model.Prescription;
import pl.ezdrowie.eRecepta.repository.PatientRepository;
import pl.ezdrowie.eRecepta.repository.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public Prescription addPrescription(String pesel, PrescriptionRequest request) {
        if (!patientRepository.existsByPesel(pesel)) {
            throw new PatientNotFoundException(pesel);
        }
        Prescription prescription = Prescription.builder()
                .id(UUID.randomUUID())
                .pesel(pesel)
                .medicationName(request.medicationName())
                .dosage(request.dosage())
                .build();
        return prescriptionRepository.save(pesel, prescription);
    }

    public List<Prescription> getPrescriptionsByPesel(String pesel) {
        if (!patientRepository.existsByPesel(pesel)) {
            throw new PatientNotFoundException(pesel);
        }
        return prescriptionRepository.findAllByPesel(pesel);
    }

    public void deletePrescription(String pesel, UUID id) {
        if (!patientRepository.existsByPesel(pesel)) {
            throw new PatientNotFoundException(pesel);
        }
        if (!prescriptionRepository.deleteById(pesel, id)) {
            throw new PrescriptionNotFoundException(id);
        }
    }
}
