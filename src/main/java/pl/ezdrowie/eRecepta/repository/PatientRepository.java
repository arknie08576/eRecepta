package pl.ezdrowie.eRecepta.repository;

import org.springframework.stereotype.Repository;
import pl.ezdrowie.eRecepta.model.Patient;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PatientRepository {

    private final Map<String, Patient> storage = new ConcurrentHashMap<>();

    public void save(Patient patient) {
        storage.put(patient.getPesel(), patient);
    }

    public Optional<Patient> findByPesel(String pesel) {
        return Optional.ofNullable(storage.get(pesel));
    }

    public Collection<Patient> findAll() {
        return storage.values();
    }

    public boolean existsByPesel(String pesel) {
        return storage.containsKey(pesel);
    }

    public boolean deleteByPesel(String pesel) {
        return storage.remove(pesel) != null;
    }

    public void clear() {
        storage.clear();
    }
}
