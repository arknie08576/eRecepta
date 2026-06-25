package pl.ezdrowie.eRecepta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    private UUID id;
    private String pesel;
    private String medicationName;
    private String dosage;
}
