package pl.ezdrowie.eRecepta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private String pesel;
    private String firstName;
    private String lastName;
}
