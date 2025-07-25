# Certificate Service - Postman Collection

This directory contains Postman collection and environment files for testing the DIGIT Certificate Service API endpoints.

## Files

- `Certificate-Service.postman_collection.json` - Main Postman collection with all API requests
- `Certificate-Service-Local.postman_environment.json` - Environment variables for local development
- `Certificate-Service-Dev.postman_environment.json` - Environment variables for development server

## Setup Instructions

### 1. Import Collection
1. Open Postman
2. Click **Import** button
3. Select `Certificate-Service.postman_collection.json`
4. The collection will be imported with all requests

### 2. Import Environment
1. Click **Import** button again
2. Select the appropriate environment file:
   - `Certificate-Service-Local.postman_environment.json` for local testing
   - `Certificate-Service-Dev.postman_environment.json` for dev server testing
3. Select the imported environment from the environment dropdown

### 3. Update Environment Variables
Update the following variables in your selected environment:
- `base_url` - Service base URL
- `auth_token` - Authentication token (if required)
- `tenant_id` - Tenant identifier for multi-tenancy

## API Endpoints Included

### 1. Health Check
- **GET** `/health`
- Simple health check endpoint to verify service is running

### 2. Certificate Management

#### Create Certificate
- **POST** `/v1/_create`
- Create single or multiple certificates
- Includes examples for:
  - Birth Certificate
  - Marriage Certificate  
  - Bulk Certificate Creation

#### Search Certificates
- **POST** `/v1/_search`
- Search certificates with various criteria:
  - All active certificates
  - By certificate type
  - By issuer
  - By date range
  - By specific IDs

#### Update Certificate
- **POST** `/v1/_update`
- Update existing certificate data
- Maintains audit trail

#### Revoke Certificate
- **POST** `/v1/_revoke`
- Revoke single or multiple certificates
- Requires revocation reason

#### Verify Certificate
- **POST** `/v1/_verify`
- Verify certificate authenticity and validity
- Includes basic and advanced verification options

## Request Flow

### Typical Testing Sequence:
1. **Health Check** - Verify service is running
2. **Create Certificate** - Create test certificates
3. **Search Certificates** - Find created certificates
4. **Update Certificate** - Modify certificate data
5. **Verify Certificate** - Check certificate validity
6. **Revoke Certificate** - Revoke when needed

## Dynamic Variables

The collection uses several dynamic variables that are automatically generated:

- `{{$timestamp}}` - Current timestamp
- `{{$guid}}` - Random UUID
- `{{certificate_id_1}}` - ID of first created certificate
- `{{certificate_id_2}}` - ID of second created certificate
- `{{certificate_id_to_update}}` - Certificate ID for update operations
- `{{certificate_id_to_revoke}}` - Certificate ID for revocation
- `{{certificate_id_to_verify}}` - Certificate ID for verification

## Environment Variables

### Local Environment
```json
{
  "base_url": "http://localhost:8039/certificate-service",
  "auth_token": "local-dev-token-12345",
  "tenant_id": "pb.mohali",
  "user_id": "1",
  "user_name": "certificate.tester",
  "user_uuid": "test-user-uuid-12345"
}
```

### Development Environment  
```json
{
  "base_url": "https://works-dev.digit.org/certificate-service",
  "auth_token": "{{dev_auth_token}}",
  "tenant_id": "pb.mohali",
  "user_id": "{{dev_user_id}}",
  "user_name": "{{dev_user_name}}",
  "user_uuid": "{{dev_user_uuid}}"
}
```

## Test Scripts

The collection includes automated test scripts that:

1. **Validate Response Status** - Ensures successful HTTP status codes
2. **Check Content Type** - Verifies JSON response format
3. **Validate Response Structure** - Confirms ResponseInfo presence
4. **Extract Certificate IDs** - Stores certificate IDs for subsequent requests

## Certificate Types Supported

### Birth Certificate
```json
{
  "type": ["VerifiableCredential", "BirthCertificate"],
  "credentialSubject": {
    "name": "Child Name",
    "dateOfBirth": "1990-05-15",
    "placeOfBirth": "City, State",
    "fatherName": "Father Name",
    "motherName": "Mother Name"
  }
}
```

### Marriage Certificate
```json
{
  "type": ["VerifiableCredential", "MarriageCertificate"],
  "credentialSubject": {
    "brideName": "Bride Name",
    "groomName": "Groom Name", 
    "marriageDate": "2024-01-15",
    "marriagePlace": "Marriage Venue"
  }
}
```

## Error Handling

The collection handles various error scenarios:
- Invalid request format (400)
- Missing required fields (400)
- Certificate not found (404)
- Server errors (500)

## Usage Tips

1. **Run Health Check First** - Always verify service availability
2. **Create Before Search** - Create test certificates before running search queries
3. **Use Collection Runner** - Run entire collection for comprehensive testing
4. **Monitor Console** - Check Postman console for detailed request/response logs
5. **Update Variables** - Modify environment variables for different test scenarios

## Authentication

If the service requires authentication:
1. Obtain valid auth token from DIGIT authentication service
2. Update `auth_token` environment variable
3. Ensure UserInfo in requests matches authenticated user

## Troubleshooting

### Common Issues:

1. **Connection Refused**
   - Ensure certificate service is running
   - Check if port 8039 is accessible
   - Verify base_url in environment

2. **Invalid Certificate ID**
   - Run create certificate request first
   - Check if certificate IDs are properly extracted
   - Verify certificate exists in database

3. **Authentication Errors**
   - Update auth_token with valid token
   - Ensure UserInfo fields are correctly populated
   - Check user permissions for certificate operations

4. **Validation Errors**
   - Review request payload structure
   - Ensure all required fields are provided
   - Check field formats (dates, IDs, etc.)

## Support

For issues or questions:
1. Check service logs for detailed error messages
2. Verify database connectivity and schema
3. Review API documentation in `certificate-service.yaml`
4. Check DIGIT platform documentation

## Contributing

To add new test cases:
1. Create new request in the collection
2. Follow existing naming convention
3. Add appropriate test scripts
4. Update this README with new endpoint details