#!/bin/bash

# Certificate Service - Sample cURL Commands
# Quick commands for testing the certificate service API

# Base URL
BASE_URL="http://localhost:8039/certificate-service"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}Certificate Service - Sample cURL Commands${NC}"
echo "=================================================="

# 1. Health Check
echo -e "\n${GREEN}1. Health Check${NC}"
echo "curl -X GET \"$BASE_URL/health\""
echo ""

# 2. Create Birth Certificate
echo -e "${GREEN}2. Create Birth Certificate${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_create" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_create",
      "did": "1",
      "msgId": "create-birth-cert-001",
      "userInfo": {
        "id": 1,
        "userName": "civil.officer",
        "name": "Civil Officer",
        "type": "EMPLOYEE",
        "uuid": "user-uuid-12345"
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
        "dateOfBirth": "2023-01-15",
        "placeOfBirth": "Mohali, Punjab",
        "fatherName": "Richard Doe",
        "motherName": "Jane Doe"
      }
    }]
  }'
EOF
echo ""

# 3. Search Active Certificates
echo -e "\n${GREEN}3. Search Active Certificates${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_search"
    },
    "searchCriteria": {
      "tenantId": "pb.mohali",
      "status": "ACTIVE",
      "limit": 10,
      "offset": 0
    }
  }'
EOF
echo ""

# 4. Search by Certificate Type
echo -e "\n${GREEN}4. Search by Certificate Type${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_search"
    },
    "searchCriteria": {
      "tenantId": "pb.mohali",
      "certificateTypes": ["BirthCertificate"],
      "limit": 5,
      "offset": 0
    }
  }'
EOF
echo ""

# 5. Update Certificate
echo -e "\n${GREEN}5. Update Certificate (Replace CERTIFICATE_ID with actual ID)${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_update" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_update",
      "userInfo": {
        "id": 1,
        "userName": "civil.officer",
        "uuid": "user-uuid-12345"
      }
    },
    "certificates": [{
      "id": "CERTIFICATE_ID",
      "tenantId": "pb.mohali",
      "context": "https://www.w3.org/2018/credentials/v1",
      "type": ["VerifiableCredential", "BirthCertificate"],
      "issuer": {
        "id": "did:gov:pb:civil-registration",
        "name": "Civil Registration Department - Updated",
        "type": "DEPARTMENT"
      },
      "credentialSubject": {
        "id": "did:citizen:pb:1234567890",
        "name": "John Doe",
        "dateOfBirth": "2023-01-15",
        "placeOfBirth": "Mohali, Punjab",
        "fatherName": "Richard Doe",
        "motherName": "Jane Doe - Updated"
      }
    }]
  }'
EOF
echo ""

# 6. Verify Certificate
echo -e "\n${GREEN}6. Verify Certificate (Replace CERTIFICATE_ID with actual ID)${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_verify" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_verify"
    },
    "verificationCriteria": {
      "tenantId": "pb.mohali",
      "certificateId": "CERTIFICATE_ID",
      "includeProofValidation": true,
      "includeExpiryCheck": true,
      "includeRevocationCheck": true
    }
  }'
EOF
echo ""

# 7. Revoke Certificate
echo -e "\n${GREEN}7. Revoke Certificate (Replace CERTIFICATE_ID with actual ID)${NC}"
cat << 'EOF'
curl -X POST "http://localhost:8039/certificate-service/v1/_revoke" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": {
      "apiId": "org.egov.certificate",
      "ver": "1.0",
      "ts": 1672531200000,
      "action": "_revoke",
      "userInfo": {
        "id": 1,
        "userName": "supervisor.officer",
        "uuid": "supervisor-uuid-12345"
      }
    },
    "revocationDetails": [{
      "certificateId": "CERTIFICATE_ID",
      "tenantId": "pb.mohali",
      "reason": "Test revocation for demonstration"
    }]
  }'
EOF
echo ""

# Usage Instructions
echo -e "\n${BLUE}Usage Instructions:${NC}"
echo "1. Copy and paste the commands above into your terminal"
echo "2. Make sure the certificate service is running on localhost:8039"
echo "3. For update/verify/revoke operations, replace CERTIFICATE_ID with actual certificate ID from create/search responses"
echo "4. Modify tenant_id, user details, and other parameters as needed"
echo ""

echo -e "${BLUE}Quick Test Sequence:${NC}"
echo "1. Run Health Check to verify service is up"
echo "2. Create a certificate and note the returned ID"
echo "3. Search for certificates to verify creation"
echo "4. Use the certificate ID for update/verify/revoke operations"