# Certificate Service - Requirements Document (Essential Services Only)

## Overview
The Certificate Service is a DIGIT microservice that manages digital certificate issuance, storage, and verification for government departments and authorized agencies. It provides secure, tamper-proof certificate management with cryptographic signatures and follows the Verifiable Credentials (VC) standard for interoperability.

**Architecture Pattern**: This service follows the DIGIT persister pattern where all CRUD operations are handled asynchronously through Kafka messaging and the persister service, ensuring data consistency, transaction management, and platform compliance.

## Technical Stack

### Core Framework
- **Java Version**: 17
- **Spring Boot**: 3.2.2
- **Maven**: Project build tool
- **Database**: PostgreSQL
- **Message Queue**: Apache Kafka
- **Database Migration**: Flyway

### Key Dependencies
```xml
<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.1</version>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
        <version>9.22.3</version>
    </dependency>
    
    <!-- DIGIT/eGov Dependencies -->
    <dependency>
        <groupId>org.egov.works</groupId>
        <artifactId>works-services-common</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.egov.services</groupId>
        <artifactId>tracer</artifactId>
        <version>2.9.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Maven Repositories
- eGov ERP Releases Repository: https://nexus-repo.egovernments.org/nexus/content/repositories/releases/
- eGov ERP Snapshots Repository: https://nexus-repo.egovernments.org/nexus/content/repositories/snapshots/
- eGov Public Repository Group: https://nexus-repo.egovernments.org/nexus/content/groups/public/
- eGov DIGIT Repository: https://nexus-repo.digit.org/nexus/content/repositories/snapshots/

## DIGIT Persister Pattern

### Architecture Overview
The Certificate Service implements the DIGIT persister pattern for all database operations:

**Read Operations (Synchronous)**:
- Search operations are handled directly by the service
- Uses optimized queries with pagination and filtering
- Returns real-time data from database

**Write Operations (Asynchronous)**:
- Create, Update, Delete operations are handled via Kafka messaging
- Service validates, enriches, and publishes messages to Kafka topics
- Persister service consumes messages and performs actual database operations
- Ensures data consistency through transaction management

### Required Services Integration

#### 1. Persister Service (REQUIRED)
- Handles all database write operations asynchronously
- Provides transactional integrity across related tables
- Manages bulk operations and error recovery
- Configuration: `certificate-persister.yml`

#### 2. ID Generation Service (REQUIRED)
- Generates unique certificate IDs following DIGIT standards
- Provides sequential ID generation for certificates
- Ensures uniqueness across distributed deployments

### Kafka Topics
- `save-certificate`: Certificate creation events
- `update-certificate`: Certificate update events
- `revoke-certificate`: Certificate revocation events

## Application Configuration

### Server Configuration
```properties
server.contextPath=/certificate-service
server.servlet.contextPath=/certificate-service
server.port=8039
app.timezone=UTC
```

### Database Configuration
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/certificates
spring.datasource.username=postgres
spring.datasource.password=root
```

### Flyway Configuration
```properties
spring.flyway.url=jdbc:postgresql://localhost:5432/postgres
spring.flyway.user=postgres
spring.flyway.password=root
spring.flyway.baseline-on-migrate=true
spring.flyway.outOfOrder=true
spring.flyway.locations=classpath:/db/migration/main
spring.flyway.enabled=true
```

### Kafka Configuration
```properties
kafka.config.bootstrap_server_config=localhost:9092
spring.kafka.consumer.group-id=certificates
certificate.kafka.create.topic=save-certificate
certificate.kafka.update.topic=update-certificate
certificate.kafka.revoke.topic=revoke-certificate
```

### Required External Service URLs
```properties
# ID Generation Service (REQUIRED)
egov.idgen.host=https://works-dev.digit.org/
egov.idgen.path=egov-idgen/id/_generate
```

### Search Configuration
```properties
certificate.default.offset=0
certificate.default.limit=100
certificate.search.max.limit=200
```

## API Specifications

### Base URL
`/certificate/v1`

### Endpoints

#### Certificate Creation
```
POST /certificate/v1/_create
Content-Type: application/json
Body: CertificateRequest
Response: CertificateResponse
```

#### Certificate Search
```
GET /certificate/v1/_search
Query Parameters: 
  - tenantId (required)
  - ids (optional)
  - issuerIds (optional)
  - subjectIds (optional)
  - certificateTypes (optional)
  - status (optional)
  - fromDate (optional)
  - toDate (optional)
  - limit (optional)
  - offset (optional)
Response: CertificateResponse
```

#### Certificate Update
```
POST /certificate/v1/_update
Content-Type: application/json
Body: CertificateRequest
Response: CertificateResponse
```

#### Certificate Revocation
```
POST /certificate/v1/_revoke
Content-Type: application/json
Body: CertificateRevocationRequest
Response: CertificateResponse
```

## Data Models

**Reference**: Refer to `@certificate-service.yaml` for the complete OpenAPI specification and detailed schema definitions for all models.

### Certificate Entity
Based on Verifiable Credentials (VC) standard:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | Yes | Unique certificate identifier |
| tenantId | String | Yes | Tenant identifier |
| context | String | Yes | W3C credentials context |
| type | String[] | Yes | Certificate type array |
| issuer | Object | Yes | Issuing authority details |
| issued | DateTime | Yes | Certificate issuance timestamp |
| expirationDate | DateTime | No | Certificate expiry date |
| credentialSubject | Object | Yes | Actual certificate data |
| proof | Object | Yes | Digital signature details |
| status | Enum | Yes | ACTIVE, REVOKED, EXPIRED |
| additionalDetails | JSONB | No | Additional metadata |

### Issuer Object
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | String | Yes | Issuer identifier |
| name | String | Yes | Issuer name |
| type | String | Yes | Issuer type (DEPARTMENT, AGENCY) |

### Proof Object
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Signature type (Ed25519Signature2020) |
| created | DateTime | Yes | Signature creation time |
| proofPurpose | String | Yes | Purpose (assertionMethod) |
| verificationMethod | String | Yes | Public key reference |
| signatureValue | String | Yes | Digital signature |

## Database Schema

### Tables

#### 1. eg_certificate
```sql
CREATE TABLE eg_certificate(
    id                           varchar(256) PRIMARY KEY,
    tenant_id                    varchar(64) NOT NULL,
    context                      varchar(512) NOT NULL,
    certificate_type             varchar(128) NOT NULL,
    issuer_id                    varchar(256) NOT NULL,
    issuer_name                  varchar(256) NOT NULL,
    issuer_type                  varchar(64) NOT NULL,
    subject_id                   varchar(256) NOT NULL,
    issued_at                    bigint NOT NULL,
    expires_at                   bigint,
    status                       varchar(20) DEFAULT 'ACTIVE',
    credential_subject           JSONB NOT NULL,
    additional_details           JSONB,
    created_by                   varchar(256) NOT NULL,
    last_modified_by             varchar(256),
    created_time                 bigint,
    last_modified_time           bigint
);
```

#### 2. eg_certificate_proof
```sql
CREATE TABLE eg_certificate_proof(
    id                           varchar(256) PRIMARY KEY,
    certificate_id               varchar(256) NOT NULL,
    proof_type                   varchar(128) NOT NULL,
    created_at                   bigint NOT NULL,
    proof_purpose                varchar(128) NOT NULL,
    verification_method          varchar(512) NOT NULL,
    signature_value              text NOT NULL,
    additional_details           JSONB,
    FOREIGN KEY (certificate_id) REFERENCES eg_certificate (id)
);
```

### Database Indexes
- Index on certificate ID, tenant ID, issuer ID
- Index on subject ID, certificate type
- Index on issued_at, expires_at timestamps
- Index on status for filtering active certificates
- Composite index on (tenant_id, issuer_id, subject_id)

## Business Logic Components

### Service Layer
- **CertificateService**: Core business logic for certificate operations
- **EnrichmentService**: Data enrichment, ID generation, and digital signing
- **ValidationService**: Certificate validation and business rules
- **RevocationService**: Certificate revocation management

### Repository Layer
- **CertificateRepository**: Database operations
- **CertificateQueryBuilder**: Dynamic SQL query construction
- **CertificateRowMapper**: Result set mapping

### Utility Components
- **DigitalSignatureUtil**: Certificate signing and verification
- **VCFormatUtil**: Verifiable Credentials format handling
- **IdgenUtil**: ID generation utility (integrates with ID Generation Service)

## Security Features

### Digital Signatures
- **Certificate Signing**: Ed25519 or RSA-2048 digital signatures
- **Verification**: Public key verification against participant registry
- **Key Management**: Secure key storage and rotation

### Data Security
- **Database Security**: PostgreSQL security features for data at rest
- **Transport Security**: TLS encryption for all API communications
- **Input Validation**: Comprehensive request validation and sanitization

### Access Control
- **Basic Authentication**: Request validation and tenant isolation
- **Audit Logging**: Complete audit trail for all operations

## Required Service Integration Points

### 1. Persister Service Integration
- **Purpose**: Asynchronous database operations
- **Configuration**: certificate-persister.yml
- **Topics**: save-certificate, update-certificate, revoke-certificate
- **Benefits**: Data consistency, transaction management, bulk operations

### 2. ID Generation Service Integration
- **Purpose**: Generate unique certificate IDs
- **Endpoint**: `/egov-idgen/id/_generate`
- **Configuration**: Certificate ID format and sequencing
- **Benefits**: Guaranteed uniqueness, DIGIT standard compliance

## Error Handling

### Common Error Codes
- `CERTIFICATE_INVALID_REQUEST`: Invalid request format
- `CERTIFICATE_NOT_FOUND`: Certificate not found
- `CERTIFICATE_EXPIRED`: Certificate has expired
- `CERTIFICATE_REVOKED`: Certificate has been revoked
- `ISSUER_NOT_AUTHORIZED`: Issuer not authorized for certificate type
- `SUBJECT_INVALID`: Invalid subject identifier
- `SIGNATURE_VERIFICATION_FAILED`: Digital signature verification failed
- `TENANT_ID_REQUIRED`: Missing tenant ID
- `REQUEST_INFO_MISSING`: Missing request info

## Persister Configuration Sample

```yaml
serviceMaps:
  - serviceName: certificate-service
    mappings:
      - version: 1.0
        description: Persists certificate data
        fromTopic: save-certificate
        isTransaction: true
        queryMaps:
          - query: INSERT INTO eg_certificate(id, tenant_id, context, certificate_type, issuer_id, issuer_name, issuer_type, subject_id, issued_at, expires_at, status, credential_subject, additional_details, created_by, created_time, last_modified_by, last_modified_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            basePath: certificate
            jsonMaps:
              - jsonPath: $.certificate.id
              - jsonPath: $.certificate.tenantId  
              - jsonPath: $.certificate.context
              - jsonPath: $.certificate.type[0]
              - jsonPath: $.certificate.issuer.id
              - jsonPath: $.certificate.issuer.name
              - jsonPath: $.certificate.issuer.type
              - jsonPath: $.certificate.credentialSubject.id
              - jsonPath: $.certificate.issued
              - jsonPath: $.certificate.expirationDate
              - jsonPath: $.certificate.status
              - jsonPath: $.certificate.credentialSubject
                type: JSON
                dbType: JSONB
              - jsonPath: $.certificate.additionalDetails
                type: JSON
                dbType: JSONB
              - jsonPath: $.certificate.auditDetails.createdBy
              - jsonPath: $.certificate.auditDetails.createdTime
              - jsonPath: $.certificate.auditDetails.lastModifiedBy
              - jsonPath: $.certificate.auditDetails.lastModifiedTime

          - query: INSERT INTO eg_certificate_proof(id, certificate_id, proof_type, created_at, proof_purpose, verification_method, signature_value, additional_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            basePath: certificate.proof
            jsonMaps:
              - jsonPath: $.certificate.proof.id
              - jsonPath: $.certificate.id
              - jsonPath: $.certificate.proof.type
              - jsonPath: $.certificate.proof.created
              - jsonPath: $.certificate.proof.proofPurpose
              - jsonPath: $.certificate.proof.verificationMethod
              - jsonPath: $.certificate.proof.signatureValue
              - jsonPath: $.certificate.proof.additionalDetails
                type: JSON
                dbType: JSONB
```

## Deployment Requirements

### Environment Variables
- Database connection details
- Kafka broker configuration
- ID Generation service URL

### Docker Support
- Dockerfile for containerized deployment
- Environment-specific configuration files
- Health check endpoints

### Testing
- Unit tests with JUnit 5
- Integration tests for API endpoints
- Load testing for bulk operations
- Security testing for signature verification

## Service Constants

### Certificate Status
- `ACTIVE`: Certificate is valid and active
- `REVOKED`: Certificate has been revoked
- `EXPIRED`: Certificate has expired

### Issuer Types
- `DEPARTMENT`: Government department
- `AGENCY`: Authorized agency or institution

### Default Values
- Default search offset: 0
- Default search limit: 100
- Maximum search limit: 200
- Default certificate validity: No expiry (unless specified)

## Compliance and Standards
- Follows DIGIT platform standards
- Implements W3C Verifiable Credentials specification
- Uses standard audit trail (created_by, created_time, last_modified_by, last_modified_time)
- Supports multi-tenancy through tenant_id
- Complies with digital signature standards

This streamlined requirements document focuses only on the core essential services needed for a functional Certificate Service: the Certificate Service itself, Persister Service for database operations, and ID Generation Service for unique identifiers. The encryption service has been removed to simplify the initial implementation while maintaining core certificate management functionality.