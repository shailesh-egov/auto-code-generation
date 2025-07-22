# Bank Account Service

A DIGIT-compliant microservice for managing financial account details of both individual and organizational entities with secure storage and encryption of personally identifiable information (PII).

## Features

- **CRUD Operations**: Create, read, update bank account details
- **Multi-tenancy Support**: Tenant-based data isolation
- **PII Encryption**: Secure encryption of account numbers and holder names
- **Audit Trail**: Complete audit trail with created/modified timestamps and user information
- **External Service Integration**: Validates individual and organization reference IDs
- **Kafka Integration**: Asynchronous messaging for bank account events
- **Search & Pagination**: Flexible search with pagination support
- **Document Management**: Support for bank account related documents

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL with Flyway migrations
- **Messaging**: Apache Kafka
- **Build Tool**: Maven
- **Framework**: DIGIT eGovernance Platform

## API Endpoints

- `POST /bankaccount/v1/_create` - Create bank accounts
- `POST /bankaccount/v1/_search` - Search bank accounts
- `POST /bankaccount/v1/_update` - Update bank accounts

## Database Schema

The service uses 4 main tables:
- `eg_bank_account` - Main bank account information
- `eg_bank_account_detail` - Account details with encrypted PII
- `eg_bank_branch_identifier` - Bank branch identifiers (IFSC, SWIFT)
- `eg_bank_accounts_doc` - Document attachments

## Configuration

### Server Configuration
```properties
server.port=8038
server.contextPath=/bankaccount-service
```

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/works
spring.datasource.username=postgres
spring.datasource.password=root
```

### Kafka Configuration
```properties
bank.account.kafka.create.topic=save-bank-account
bank.account.kafka.update.topic=update-bank-account
```

## Security Features

- **Data Encryption**: Account numbers and holder names are encrypted using DIGIT encryption service
- **Validation**: Comprehensive validation of business rules and external service integration
- **Audit Trail**: Complete audit information for all operations

## Build and Run

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL
- Apache Kafka

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn spring-boot:run
```

### Database Migration
```bash
mvn flyway:migrate
```

## Testing

Run unit tests:
```bash
mvn test
```

## Integration

The service integrates with:
- **Individual Service**: For individual reference ID validation
- **Organisation Service**: For organization reference ID validation
- **ID Generation Service**: For generating unique identifiers
- **Encryption Service**: For PII data encryption/decryption
- **MDMS Service**: For master data management

## Error Handling

The service provides comprehensive error handling with specific error codes:
- `TENANT_ID`: Missing tenant ID
- `REFERENCE_ID`: Invalid reference ID
- `BANK_ACCOUNT_DETAILS`: Missing account details
- `BRANCH_IDENTIFIER`: Missing branch identifier

## Development

### Project Structure
```
src/
├── main/java/org/egov/
│   ├── config/           # Configuration classes
│   ├── producer/         # Kafka producers
│   ├── repository/       # Data access layer
│   ├── service/          # Business logic
│   ├── util/            # Utility classes
│   ├── validator/       # Validation logic
│   └── web/             # REST controllers and models
└── main/resources/
    ├── db/migration/    # Flyway migration scripts
    └── application.properties
```

### Adding New Features
1. Update data models in `web/models/`
2. Add business logic in `service/`
3. Update repository layer if needed
4. Add validation rules in `validator/`
5. Update API documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.