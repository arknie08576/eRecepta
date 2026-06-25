package pl.ezdrowie.eRecepta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ezdrowie.eRecepta.dto.PrescriptionRequest;
import pl.ezdrowie.eRecepta.dto.PrescriptionResponse;
import pl.ezdrowie.eRecepta.service.PrescriptionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients/{pesel}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> addPrescription(
            @PathVariable String pesel,
            @Valid @RequestBody PrescriptionRequest request) {
        PrescriptionResponse response = PrescriptionResponse.from(prescriptionService.addPrescription(pesel, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptions(@PathVariable String pesel) {
        List<PrescriptionResponse> response = prescriptionService.getPrescriptionsByPesel(pesel).stream()
                .map(PrescriptionResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(
            @PathVariable String pesel,
            @PathVariable UUID id) {
        prescriptionService.deletePrescription(pesel, id);
        return ResponseEntity.noContent().build();
    }
}
