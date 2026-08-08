# Error Codes — JEMIL Backend

| Type              | HTTP Code | Business Code    | Description                                |
|-------------------|-----------|------------------|--------------------------------------------|
| `DomainException` | `400`     | `AGENCY_400_001` | Invalid agency state                       |
| `DomainException` | `400`     | `AGENCY_400_002` | Not enough seats available                 |
| `DomainException` | `400`     | `AGENCY_400_003` | Agency name is required                    |
| `DomainException` | `400`     | `AGENCY_400_004` | Address is required                        |
| `DomainException` | `400`     | `AGENCY_400_005` | Phone number is required                   |
| `DomainException` | `400`     | `AGENCY_400_006` | Route cities are required                  |
| `DomainException` | `400`     | `AGENCY_400_007` | Route price must be positive               |
| `DomainException` | `400`     | `AGENCY_400_008` | Seat count must be positive                |
| `DomainException` | `400`     | `AGENCY_400_009` | Departure time is required                 |
| `DomainException` | `400`     | `PHONE_400_001`  | Phone number is required                   |
| `DomainException` | `400`     | `AUTH_400_001`   | Email is required                          |
| `DomainException` | `400`     | `AUTH_400_002`   | Password is required                       |
| `DomainException` | `400`     | `CITY_400_001`   | City name is required                      |
| `DomainException` | `400`     | `BRANCH_400_001` | Branch name is required                    |
| `DomainException` | `400`     | `BRANCH_400_002` | Branch name already exists for this agency |
| `DomainException` | `400`     | `BRANCH_400_003` | Branch address is required                 |
| `DomainException` | `401`     | `AUTH_401_001`   | Invalid email or password                  |
| `DomainException` | `401`     | `AUTH_401_002`   | Invalid or expired token                   |
| `DomainException` | `403`     | `AUTH_403_001`   | Insufficient permissions                   |
| `DomainException` | `404`     | `AGENCY_404_001` | Agency not found                           |
| `DomainException` | `404`     | `AGENCY_404_002` | Demo not found                             |
| `DomainException` | `404`     | `CITY_404_001`   | City not found                             |
| `DomainException` | `404`     | `BRANCH_404_001` | Branch not found                           |
| `DomainException` | `409`     | `AUTH_409_001`   | Email already registered                   |
