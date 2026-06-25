# eRecepta

REST API do zarządzania pacjentami i receptami, zrealizowane w ramach zadania rekrutacyjnego dla Centrum e-Zdrowia.

## Stack technologiczny

- Java 17
- Spring Boot 3.5.14
- Maven (wrapper dołączony)
- Dane przechowywane w pamięci (ConcurrentHashMap)

## Uruchomienie

```bash
./mvnw spring-boot:run
```

Na Windows:

```cmd
mvnw.cmd spring-boot:run
```

Aplikacja startuje na `http://localhost:8080`.

## Dokumentacja API (Swagger UI)

Po uruchomieniu dostępna pod adresem:

```
http://localhost:8080/swagger-ui/index.html
```

## Endpointy

### Pacjenci

| Metoda | Ścieżka | Opis | Status |
|--------|---------|------|--------|
| `POST` | `/api/patients` | Dodanie pacjenta | 201 / 409 |
| `GET` | `/api/patients` | Lista wszystkich pacjentów | 200 |
| `GET` | `/api/patients/{pesel}` | Pobranie pacjenta po PESEL | 200 / 404 |
| `DELETE` | `/api/patients/{pesel}` | Usunięcie pacjenta (kaskadowo usuwa recepty) | 204 / 404 |

### Recepty

| Metoda | Ścieżka | Opis | Status |
|--------|---------|------|--------|
| `POST` | `/api/patients/{pesel}/prescriptions` | Dodanie recepty dla pacjenta | 201 / 404 |
| `GET` | `/api/patients/{pesel}/prescriptions` | Pobranie recept pacjenta | 200 / 404 |
| `DELETE` | `/api/patients/{pesel}/prescriptions/{id}` | Usunięcie recepty | 204 / 404 |

## Walidacja danych

| Pole | Reguła |
|------|--------|
| `pesel` | dokładnie 11 cyfr, poprawna cyfra kontrolna oraz prawidłowa zakodowana data urodzenia |
| `firstName`, `lastName` | wymagane, niepuste |
| `medicationName`, `dosage` | wymagane, niepuste |

## Obsługa błędów

Błędy zwracane są w jednolitym formacie JSON:

```json
{
  "status": 404,
  "message": "Patient with PESEL 90010100009 not found",
  "timestamp": "2026-06-25T12:00:00"
}
```

| Status | Kiedy |
|--------|-------|
| `400 Bad Request` | niepoprawne dane wejściowe (zły PESEL, zły format UUID, niepoprawny JSON) |
| `404 Not Found` | pacjent lub recepta nie istnieje |
| `409 Conflict` | pacjent o danym PESEL już istnieje |

## Przykłady użycia

### Dodanie pacjenta

```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"pesel": "90010100009", "firstName": "Jan", "lastName": "Kowalski"}'
```

### Pobranie pacjenta

```bash
curl http://localhost:8080/api/patients/90010100009
```

### Dodanie recepty

```bash
curl -X POST http://localhost:8080/api/patients/90010100009/prescriptions \
  -H "Content-Type: application/json" \
  -d '{"medicationName": "Ibuprom", "dosage": "200mg"}'
```

### Usunięcie recepty

```bash
curl -X DELETE http://localhost:8080/api/patients/90010100009/prescriptions/{id}
```

## Uruchomienie testów

Pełny zestaw (testy jednostkowe + integracyjne):

```bash
./mvnw verify
```

Na Windows:

```cmd
mvnw.cmd verify
```

`./mvnw test` uruchamia same testy jednostkowe; testy integracyjne (`*IT`) wykonują się w fazie `verify` przez maven-failsafe-plugin.
