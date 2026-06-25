# eRecepta

REST API do zarządzania pacjentami i receptami, zrealizowane w ramach zadania rekrutacyjnego dla Centrum e-Zdrowia.

## Stack technologiczny

- Java 17
- Spring Boot 4.1.0
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

## Przykłady użycia

### Dodanie pacjenta

```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"pesel": "12345678901", "firstName": "Jan", "lastName": "Kowalski"}'
```

### Pobranie pacjenta

```bash
curl http://localhost:8080/api/patients/12345678901
```

### Dodanie recepty

```bash
curl -X POST http://localhost:8080/api/patients/12345678901/prescriptions \
  -H "Content-Type: application/json" \
  -d '{"medicationName": "Ibuprom", "dosage": "200mg"}'
```

### Usunięcie recepty

```bash
curl -X DELETE http://localhost:8080/api/patients/12345678901/prescriptions/{id}
```

## Uruchomienie testów

```bash
./mvnw test
```

Na Windows:

```cmd
mvnw.cmd test
```
