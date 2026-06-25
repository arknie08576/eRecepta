package pl.ezdrowie.eRecepta.repository;

import org.springframework.stereotype.Repository;
import pl.ezdrowie.eRecepta.model.Prescription;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class PrescriptionRepository {

    private final Map<String, List<Prescription>> storage = new ConcurrentHashMap<>();

    public Prescription save(String pesel, Prescription prescription) {
        storage.computeIfAbsent(pesel, k -> new CopyOnWriteArrayList<>()).add(prescription);
        return prescription;
    }

    public List<Prescription> findAllByPesel(String pesel) {
        return storage.getOrDefault(pesel, Collections.emptyList());
    }

    public Optional<Prescription> findById(String pesel, UUID id) {
        return findAllByPesel(pesel).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean deleteById(String pesel, UUID id) {
        List<Prescription> prescriptions = storage.get(pesel);
        if (prescriptions == null) return false;
        return prescriptions.removeIf(p -> p.getId().equals(id));
    }

    public void deleteAllByPesel(String pesel) {
        storage.remove(pesel);
    }

    public void clear() {
        storage.clear();
    }
}
