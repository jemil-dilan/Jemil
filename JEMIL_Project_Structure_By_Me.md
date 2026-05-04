# JEMIL Backend - Current Project Structure                                                                                                          
                                                                                                                                                     
> This document describes the architecture currently implemented in the repository.                                                                  
> It keeps the `Demo` scaffold intentionally as roadmap material and defines the                                                                     
> conventions to preserve while the real domain grows.                                                                                               
                                                                                                                                                     
Architecture: **single Gradle module · modular monolith · hexagonal package layout · module-owned code**                                             
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                   
## Root Layout                                                                                                                                       
                                                                                                                                                     
```
text                                                                                                                                              
     jemil-backend/                                                                                                                                       
     ├── build.gradle.kts                                                                                                                                 
     ├── settings.gradle.kts                                                                                                                              
     ├── gradle/                                                                                                                                          
     │   ├── wrapper/                                                                                                                                     
     │   └── libs.versions.toml                                                                                                                           
     ├── config/                                                                                                                                          
     │   └── checkstyle/                                                                                                                                  
     ├── docker-compose.yml                                                                                                                               
     ├── README.md                                                                                                                                        
     └── src/                                                                                                                                             
         ├── main/                                                                                                                                        
         │   ├── java/cm/jemil/                                                                                                                           
         │   │   ├── JemilApplication.java                                                                                                                
         │   │   ├── shared/                                                                                                                              
         │   │   ├── auth/                                                                                                                                
         │   │   ├── agency/                                                                                                                              
         │   │   ├── booking/                                                                                                                             
         │   │   ├── payment/                                                                                                                             
         │   │   ├── ticket/                                                                                                                              
         │   │   ├── validation/                                                                                                                          
         │   │   └── dashboard/                                                                                                                           
         │   └── resources/                                                                                                                               
         └── test/                                                                                                                                        
             └── java/cm/jemil/                                                                                                                           
                 ├── architecture/                                                                                                                        
                 ├── auth/                                                                                                                                
                 ├── agency/                                                                                                                              
                 ├── booking/                                                                                                                             
                 ├── payment/                                                                                                                             
                 ├── ticket/                                                                                                                              
                 └── validation/                                                                                                                          
```                                                                                                                                                  
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Current Module Convention                                                                                                                         
                                                                                                                                                     
Every business module follows the same internal shape:                                                                                               
                                                                                                                                                     
```
text                                                                                                                                              
     module-name/                                                                                                                                         
     ├── adapter/                                                                                                                                         
     │   ├── inbound/rest/                                                                                                                                
     │   └── outbond/persistence/jpa/                                                                                                                     
     ├── application/                                                                                                                                     
     │   └── inbound/usecase/                                                                                                                             
     ├── config/                                                                                                                                          
     └── demo/
```                                                                                                                                                
                                                                                                                                                     
### What each part means                                                                                                                             
                                                                                                                                                     
- `adapter/inbound/rest` contains REST controllers, request/response mapping, and OpenAPI-facing DTO conversion.                                     
- `adapter/outbond/persistence/jpa` contains JPA entities, Spring Data repositories, and persistence adapters.                                       
- `application/inbound/usecase` contains orchestration classes that implement the inbound use cases.                                                 
- `config` contains module-specific Spring wiring.                                                                                                   
- `demo` is kept as a roadmap scaffold and also hosts lightweight module-local exceptions. 
- `auth` currently keeps a fuller `domain/demo` family, while the roadmap modules use `demo/` directly.
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Ownership Rules                                                                                                                                   
                                                                                                                                                     
- Each module owns its own `Demo` scaffold, repository, mapper, entity, and use case classes.                                                        
- `agency`, `booking`, `payment`, and `ticket` must not import internal classes from `auth` or from each other.                                      
- `application` code must not depend on persistence or web adapter classes.                                                                          
- `adapter` code may depend on its own module application/domain classes and on generated OpenAPI types.                                             
- `shared/` is the only place for cross-cutting utilities and must not depend on business modules.                                                   
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Current Module Boundaries                                                                                                                         
                                                                                                                                                     
### `auth`                                                                                                                                           
                                                                                                                                                     
- Reference implementation for the current module convention.
- Owns its local `domain/demo` model family, JPA adapter, REST adapter, and Spring wiring.                                                                                                                                                     

### `agency`                                                                                                                                         
                                                                                                                                                     
- Owns agency-specific `Demo` scaffold and must stay self-contained.                                                                                 
- Used as a template for future agency-specific domain work.                                                                                         
                                                                                                                                                     
### `booking`                                                                                                                                        
                                                                                                                                                     
- Owns booking-specific `Demo` scaffold and must stay self-contained.                                                                                
- Current code is a roadmap slice, not the final booking model.                                                                                      
                                                                                                                                                     
### `payment`                                                                                                                                        
                                                                                                                                                     
- Owns payment-specific `Demo` scaffold and must stay self-contained.                                                                                
- Current code is a roadmap slice, not the final payment model.                                                                                      
                                                                                                                                                     
### `ticket`                                                                                                                                         
                                                                                                                                                     
- Owns ticket-specific `Demo` scaffold and must stay self-contained.                                                                                 
- Current code is a roadmap slice, not the final ticketing model.                                                                                    
                                                                                                                                                     
### `validation` and `dashboard`                                                                                                                     
                                                                                                                                                     
- Reserved packages for upcoming features.                                                                                                           
- They may stay empty until the corresponding module is introduced.                                                                                  
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Generated API Namespaces                                                                                                                          
                                                                                                                                                     
- Generated REST contracts are module-specific.                                                                                                      
- Current pattern: `cm.jemil.generated.<module>.adapter.rest.inbound...`                                                                       
- Module code must use the matching namespace for its own module only.                                                                               
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## JPMS Descriptor                                                                                                                                   
                                                                                                                                                     
- `module-info.java` is kept only as a compatibility descriptor.                                                                                     
- If present, it must use the real project module name and must not reference unrelated legacy project names.                                        
- The descriptor should remain minimal and reflect the actual runtime dependencies of this repository.                                               
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Architecture Checks                                                                                                                               
                                                                                                                                                     
The repository should enforce these rules with ArchUnit:                                                                                             
                                                                                                                                                     
1. A module must not depend on sibling module internals.                                                                                             
2. Application code must not depend on adapter/persistence code.                                                                                     
3. Shared code must not depend on business modules.                                                                                                  
4. Module-specific generated contracts are allowed, but only for the module that owns them.                                                          
                                                                                                                                                     
---                                                                                                                                                  
                                                                                                                                                     
## Status                                                                                                                                            
                                                                                                                                                     
- `auth` is the current reference slice.                                                                                                             
- `agency`, `booking`, `payment`, and `ticket` now follow the same ownership convention and must remain module-local.                                
- The `Demo` scaffold is intentionally preserved as a roadmap for the real domain model.                                                             
                                                                    