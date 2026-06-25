package pl.ezdrowie.eRecepta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.model.Patient;
import pl.ezdrowie.eRecepta.service.PatientService;

import java.util.Collection;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.addPatient(request));
    }

    @GetMapping
    public ResponseEntity<Collection<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{pesel}")
    public ResponseEntity<Patient> getPatient(@PathVariable String pesel) {
        return ResponseEntity.ok(patientService.getPatientByPesel(pesel));
    }

    @DeleteMapping("/{pesel}")
    public ResponseEntity<Void> deletePatient(@PathVariable String pesel) {
        patientService.deletePatient(pesel);
        return ResponseEntity.noContent().build();
    }
}
