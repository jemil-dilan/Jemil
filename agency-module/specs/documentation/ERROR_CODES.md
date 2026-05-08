# Error Codes

| Type                        | HTTP Code | Business Code                | Description                                 |
|-----------------------------|-----------|------------------------------|---------------------------------------------|
| `UnauthorizedException`     | `401`     | `LIFEKORA_BLOODBANK-401-001` | USER SHOULD BE AUTHOR OF COLLECTOR          |
|                             |           |                              |                                             |
| `ForbiddenException`        | `403`     | `LIFEKORA_BLOODBANK-403-001` | User Is Not Author Of Collector             |
|                             |           |                              |                                             |
| `ResourceNotFoundException` | `404`     | `LIFEKORA_BLOODBANK-404-001` | DEMO NOT FOUND                              |
|                             |           | `LIFEKORA_BLOODBANK-404-002` | USER NOT FOUND                              |
|                             |           | `LIFEKORA_BLOODBANK-404-003` | COLLECTOR NOT FOUND                         |
|                             |           |                              |                                             |
|                             |           |                              |                                             |
| `ConflictException`         | `409`     | `LIFEKORA_BLOODBANK-409-001` | A COLLECTOR WITH THIS NUMBER ALREADY EXISTS |
|                             |           | `LIFEKORA_BLOODBANK-409-002` | A Collector With This Email Already Exists  |
|                             |           | `LIFEKORA_BLOODBANK-409-003` | The User Already Has A Collector            |