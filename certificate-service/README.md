# Certificate Service

The Certificate Service is a DIGIT microservice that manages digital certificates for government departments and authorized agencies. It provides secure, tamper-proof certificate management with cryptographic signatures following the Verifiable Credentials (VC) standard.

## Features

- Digital certificate issuance and management
- Verifiable Credentials (VC) standard compliance
- Cryptographic signature support
- Multi-tenant architecture
- Bulk operations support (up to 100 certificates per request)
- Certificate lifecycle management (create, update, revoke, verify)
- Asynchronous persistence through Kafka and persister service
- Comprehensive search and filtering capabilities

## Technical Stack

- **Java Version**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL with JSONB support
- **Message Queue**: Apache Kafka
- **Migration Tool**: Flyway
- **Build Tool**: Maven

## API Endpoints

### Base URL
`/certificate-service/v1`

### Available Endpoints

1. **POST /v1/_create** - Create certificates
2. **POST /v1/_search** - Search certificates with filtering and pagination
3. **POST /v1/_update** - Update existing certificates
4. **POST /v1/_revoke** - Revoke certificates
5. **POST /v1/_verify** - Verify certificate authenticity and validity
6. **GET /health** - Health check endpoint

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8039 |
| `SPRING_DATASOURCE_URL` | Database URL | jdbc:postgresql://localhost:5432/certificates |
| `SPRING_DATASOURCE_USERNAME` | Database username | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Database password | root |
| `KAFKA_CONFIG_BOOTSTRAP_SERVER_CONFIG` | Kafka server | localhost:9092 |
| `EGOV_IDGEN_HOST` | ID generation service host | https://works-dev.digit.org/ |

### Kafka Topics

- `save-certificate` - Certificate creation events
- `update-certificate` - Certificate update events
- `revoke-certificate` - Certificate revocation events

## Database Schema

### Tables

1. **eg_certificate** - Main certificate data
2. **eg_certificate_proof** - Digital signature proofs

See `src/main/resources/db/migration/main/V20240101120000__create_certificate_tables.sql` for complete schema.

## Getting Started

### Prerequisites

- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka 2.8+

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd certificate-service
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

### Docker Development

1. **Build and start all services**
   ```bash
   docker-compose up --build
   ```

2. **Stop services**
   ```bash
   docker-compose down
   ```

3. **View logs**
   ```bash
   docker-compose logs certificate-service
   ```

## Usage Examples

### Create Certificate

```bash
curl -X POST "http://localhost:8039/certificate-service/v1/_create" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1234567890123,
      "action": "_create",
      "userInfo": {
        "id": 1,
        "userName": "officer",
        "uuid": "user-uuid"
      }
    },
    "certificates": [{
      "tenantId": "pb.mohali",
      "context": "https://www.w3.org/2018/credentials/v1",
      "type": ["VerifiableCredential", "BirthCertificate"],
      "issuer": {
        "id": "did:gov:pb:civil-registration",
        "name": "Civil Registration Department",
        "type": "DEPARTMENT"
      },
      "credentialSubject": {
        "id": "did:citizen:pb:1234567890",
        "name": "John Doe",
        "dateOfBirth": "1990-01-15",
        "placeOfBirth": "Mohali, Punjab"
      }
    }]
  }'
```

### Search Certificates

```bash
curl -X POST "http://localhost:8039/certificate-service/v1/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1234567890123
    },
    "searchCriteria": {
      "tenantId": "pb.mohali",
      "status": "ACTIVE",
      "limit": 10,
      "offset": 0
    }
  }'
```

## Integration

### Persister Service

The service uses the DIGIT persister pattern for database operations. Configure the persister service with the provided `certificate-persister.yml` configuration.

### ID Generation Service

Integrates with DIGIT ID generation service for unique certificate IDs. Configure the service endpoint in application properties.

## Security

- Digital signatures using Ed25519 (placeholder implementation)
- Multi-tenant data isolation
- Input validation and sanitization
- Audit trail for all operations

## Monitoring

- Health check endpoint at `/health`
- Application metrics via Spring Actuator
- Structured logging with correlation IDs

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Contributing

1. Follow DIGIT coding standards
2. Add unit tests for new features
3. Update documentation
4. Ensure all validations pass

## License

This project is licensed under the MIT License - see the requirements document for details.

## Support

For issues and questions:
- Check the troubleshooting section in requirements document
- Review logs in `/var/log/certificate-service/`
- Contact the DIGIT platform team