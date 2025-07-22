# Bank Account Service - Requirements Document

## Overview
The Bank Account Service is a DIGIT microservice that manages financial account details for both individual and organizational entities. It provides secure storage and management of bank account information with encryption of personally identifiable information (PII).

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
        <groupId>org.egov.common</groupId>
        <artifactId>health-services-models</artifactId>
        <version>1.0.22-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.egov.services</groupId>
        <artifactId>tracer</artifactId>
        <version>2.9.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.egov</groupId>
        <artifactId>mdms-client</artifactId>
        <version>2.9.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.egov</groupId>
        <artifactId>enc-client</artifactId>
        <version>2.9.0</version>
    </dependency>
</dependencies>
```

### Maven Repositories
- eGov ERP Releases Repository: https://nexus-repo.egovernments.org/nexus/content/repositories/releases/
- eGov ERP Snapshots Repository: https://nexus-repo.egovernments.org/nexus/content/repositories/snapshots/
- eGov Public Repository Group: https://nexus-repo.egovernments.org/nexus/content/groups/public/
- eGov DIGIT Repository: https://nexus-repo.digit.org/nexus/content/repositories/snapshots/

## Application Configuration

### Server Configuration
```properties
server.contextPath=/bankaccount-service
server.servlet.contextPath=/bankaccount-service
server.port=8038
app.timezone=UTC
```

### Database Configuration
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/works
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
spring.kafka.consumer.group-id=bankaccounts
bank.account.kafka.create.topic=save-bank-account
bank.account.kafka.update.topic=update-bank-account
```

### External Service URLs
```properties
# MDMS Service
egov.mdms.host=https://works-dev.digit.org
egov.mdms.search.endpoint=/mdms-v2/v1/_search

# User Service
egov.user.host=https://works-dev.digit.org
egov.user.context.path=/user/users
egov.user.create.path=/_createnovalidate
egov.user.search.path=/user/_search
egov.user.update.path=/_updatenovalidate

# Individual Service
egov.individual.host=https://works-dev.digit.org
egov.individual.search.endpoint=/individual/v1/_search

# Organisation Service
egov.organisation.host=https://works-dev.digit.org
egov.organisation.search.endpoint=/org-services/organisation/v1/_search

# ID Generation Service
egov.idgen.host=https://works-dev.digit.org/
egov.idgen.path=egov-idgen/id/_generate

# Workflow Service
egov.workflow.host=https://works-dev.digit.org
egov.workflow.transition.path=/egov-workflow-v2/egov-wf/process/_transition
egov.workflow.businessservice.search.path=/egov-workflow-v2/egov-wf/businessservice/_search
egov.workflow.processinstance.search.path=/egov-workflow-v2/egov-wf/process/_search

# URL Shortener Service
egov.url.shortner.host=https://works-dev.digit.org
egov.url.shortner.endpoint=/egov-url-shortening/shortener
```

### Search Configuration
```properties
bank.account.default.offset=0
bank.account.default.limit=100
bank.account.search.max.limit=200
```

## API Specifications

Complete API specifications are defined in the OpenAPI 3.0 specification:
- **Location**: `docs/bank-account-v1.0.0.yaml`
- **Base URL**: `/bankaccount/v1`
- **Endpoints**: 
  - `POST /_create` - Create bank accounts
  - `POST /_search` - Search bank accounts  
  - `POST /_update` - Update bank accounts
- **External References**: Uses DIGIT common contracts for RequestInfo, ResponseInfo, ErrorRes, AuditDetails, Document, and Pagination

## Data Models

All data models and request/response schemas are defined in the OpenAPI specification:
- **Location**: `docs/bank-account-v1.0.0.yaml`
- **Key Models**: 
  - `BankAccount` - Main entity with tenant, service code, reference ID
  - `BankAccountDetails` - Account specifics (encrypted account number/holder name)
  - `BankBranchIdentifier` - Branch details (IFSC, SWIFT codes)
  - `BankAccountRequest/Response` - API contracts
  - `BankAccountSearchRequest/Criteria` - Search contracts

**Note**: Account numbers and holder names are encrypted using DIGIT encryption service.

## Database Schema

### Tables

#### 1. eg_bank_account
```sql
CREATE TABLE eg_bank_account(
    id                           varchar(256) PRIMARY KEY,
    tenant_id                    varchar(64) NOT NULL,
    service_code                 varchar(128) NOT NULL,
    reference_id                 varchar(256) NOT NULL,
    additional_details           JSONB,
    created_by                   varchar(256) NOT NULL,
    last_modified_by             varchar(256),
    created_time                 bigint,
    last_modified_time           bigint
);
```

#### 2. eg_bank_account_detail
```sql
CREATE TABLE eg_bank_account_detail(
    id                           varchar(256) PRIMARY KEY,
    tenant_id                    varchar(64) NOT NULL,
    bank_account_id              varchar(256) NOT NULL,
    account_holder_name          varchar(256),
    account_number               varchar(256) NOT NULL,
    account_type                 varchar(140) NOT NULL,
    is_primary                   boolean,
    is_active                    boolean,
    additional_details           JSONB,
    created_by                   varchar(256) NOT NULL,
    last_modified_by             varchar(256),
    created_time                 bigint,
    last_modified_time           bigint,
    FOREIGN KEY (bank_account_id) REFERENCES eg_bank_account (id)
);
```

#### 3. eg_bank_branch_identifier
```sql
CREATE TABLE eg_bank_branch_identifier(
    id                           varchar(256) PRIMARY KEY,
    bank_account_detail_id       varchar(64) NOT NULL,
    type                         varchar(140),
    code                         varchar(140),
    additional_details           JSONB,
    FOREIGN KEY (bank_account_detail_id) REFERENCES eg_bank_account_detail (id)
);
```

#### 4. eg_bank_accounts_doc
```sql
CREATE TABLE eg_bank_accounts_doc(
    id                           varchar(256) PRIMARY KEY,
    bank_account_detail_id       varchar(64) NOT NULL,
    document_type                varchar,
    file_store                   varchar,
    document_uid                 varchar(256),
    additional_details           jsonb,
    FOREIGN KEY (bank_account_detail_id) REFERENCES eg_bank_account_detail (id)
);
```

### Database Indexes
- Index on bank account ID, service code, reference ID
- Index on account holder name, account number (encrypted fields)
- Index on isPrimary, isActive flags
- Index on branch identifier type and code

## Business Logic Components

### Service Layer
- **BankAccountService**: Core business logic for CRUD operations
- **EnrichmentService**: Data enrichment and ID generation
- **EncryptionService**: PII encryption/decryption

### Repository Layer
- **BankAccountRepository**: Database operations
- **BankAccountQueryBuilder**: Dynamic SQL query construction
- **BankAccountRowMapper**: Result set mapping

### Validation Layer
- **BankAccountValidator**: Request validation and business rules

### Utility Components
- **IndividualUtil**: Integration with Individual service
- **OrganisationUtil**: Integration with Organisation service
- **IdgenUtil**: ID generation utility
- **MdmsUtil**: Master data management integration
- **WorkflowUtil**: Workflow integration
- **UserUtil**: User service integration
- **UrlShortenerUtil**: URL shortening utility
- **EncryptionDecryptionUtil**: Encryption utilities

## Security Features

### Data Encryption
- **Account Number**: Encrypted using DIGIT encryption service
- **Account Holder Name**: Encrypted for privacy
- **Encryption Keys**:
  - `BankAccountEncrypt`: General encryption
  - `BankAccountNumberEncrypt`: Account number specific
  - `BankAccountHolderNameEncrypt`: Account holder name specific
  - `BankAccountDecrypt`: Decryption key

### Validation Rules
- Mandatory fields validation
- Individual/Organization ID validation via external services
- User authentication validation (UUID required)
- Business rule validations

## Integration Points

### External Services
1. **Individual Service**: Validate individual reference IDs
2. **Organisation Service**: Validate organization reference IDs
3. **MDMS Service**: Master data management
4. **User Service**: User management
5. **ID Generation Service**: Generate unique IDs
6. **Workflow Service**: Process workflow management
7. **URL Shortener Service**: URL shortening
8. **Encryption Service**: Data encryption/decryption

### Kafka Topics
- **save-bank-account**: Bank account creation events
- **update-bank-account**: Bank account update events

## Error Handling

### Common Error Codes
- `BANK_ACCOUNTS_SEARCH_CRITERIA_REQUEST`: Missing search criteria
- `TENANT_ID`: Missing tenant ID
- `BANK_ACCOUNTS`: Missing bank accounts
- `REFERENCE_ID`: Missing reference ID
- `SERVICE_CODE`: Missing service code
- `BANK_ACCOUNT_DETAILS`: Missing account details
- `BRANCH_IDENTIFIER`: Missing branch identifier
- `REQUEST_INFO`: Missing request info
- `USERINFO`: Missing user info
- `USERINFO_UUID`: Missing user UUID

## Deployment Requirements

### Environment Variables
- Database connection details
- Kafka broker configuration
- External service URLs
- Encryption service configuration

### Docker Support
- Dockerfile available in `src/main/resources/db/`
- Migration script: `migrate.sh`

### Testing
- Unit test support with JUnit 4.13.2
- Test configuration in `TestConfiguration.java`
- API controller tests in `BankaccountApiControllerTest.java`

## Documentation Assets

### API Documentation
- OpenAPI 3.0 specification: `bank-account-v1.0.0.yaml`
- Postman collection: `BankAccount.postman_collection.json`

### Flow Diagrams
- Bank Account Creation: `BankAccountCreate.puml`
- Bank Account Search: `BankAccountSearch.puml`
- Bank Account Update: `BankAccountUpdate.puml`

### Database Schema
- Visual schema: `Bank_Account_DB_Schema.png`

## Service Constants

### Service Codes
- `ORG`: Organization service code
- `IND`: Individual service code

### Default Values
- Default search offset: 0
- Default search limit: 100
- Maximum search limit: 200

## Compliance and Standards
- Follows DIGIT platform standards
- Implements eGovernments framework patterns
- Uses standard audit trail (created_by, created_time, last_modified_by, last_modified_time)
- Supports multi-tenancy through tenant_id