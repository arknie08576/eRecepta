package pl.ezdrowie.eRecepta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ezdrowie.eRecepta.dto.PatientRequest;
import pl.ezdrowie.eRecepta.dto.PatientResponse;
import pl.ezdrowie.eRecepta.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> addPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = PatientResponse.from(patientService.addPatient(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> response = patientService.getAllPatients().stream()
                .map(PatientResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pesel}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable String pesel) {
        return ResponseEntity.ok(PatientResponse.from(patientService.getPatientByPesel(pesel)));
    }

    @DeleteMapping("/{pesel}")
    public ResponseEntity<Void> deletePatient(@PathVariable String pesel) {
        patientService.deletePatient(pesel);
        return ResponseEntity.noContent().build();
    }
}
