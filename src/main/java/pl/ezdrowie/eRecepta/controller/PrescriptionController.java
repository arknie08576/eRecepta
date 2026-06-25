package pl.ezdrowie.eRecepta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.model.Prescription;
import pl.ezdrowie.eRecepta.service.PrescriptionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients/{pesel}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Prescription> addPrescription(
            @PathVariable String pesel,
            @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.addPrescription(pesel, request));
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getPrescriptions(@PathVariable String pesel) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPesel(pesel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(
            @PathVariable String pesel,
            @PathVariable UUID id) {
        prescriptionService.deletePrescription(pesel, id);
        return ResponseEntity.noContent().build();
    }
}
