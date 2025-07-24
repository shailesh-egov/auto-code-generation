# Participant Registry - Requirements Document

## 1. Overview

### 1.1 Purpose
The Participant Registry is a centrally managed registry component of the State Data Integration Platform (SDIP) that maintains a comprehensive list of all participating departments and agencies that provide and consume data across the Government of Punjab ecosystem.

### 1.2 Scope
This document defines the functional and non-functional requirements for the Participant Registry component based on the SDIP architecture for the Government of Punjab.

### 1.3 Context
As part of Punjab's objective to enable interdepartmental data exchange for proactive citizen-centric service delivery and good governance through data, the Participant Registry serves as the foundational component that enables secure and authorized participation in the SDIP ecosystem.

## 2. DIGIT Persister Pattern Implementation

### 2.1 Architecture Overview
The Participant Registry Service implements the DIGIT persister pattern for all database operations:

**Read Operations (Synchronous)**:
- Search operations are handled directly by the service
- Uses optimized queries with pagination and filtering
- Returns real-time data from database

**Write Operations (Asynchronous)**:
- Create, Update, Delete operations are handled via Kafka messaging
- Service validates, enriches, and publishes messages to Kafka topics
- Persister service consumes messages and performs actual database operations
- Ensures data consistency through transaction management

### 2.2 Persister Configuration
The service uses a dedicated persister configuration (`participant-registry-persister.yml`) that defines:
- Kafka topic mappings for CRUD operations
- Database table relationships and dependencies
- JSON path mappings for complex nested objects
- Transaction boundaries for multi-table operations
- Support for encrypted field handling

### 2.3 Benefits
1. **Data Consistency**: Transactional integrity across related tables
2. **Scalability**: Asynchronous processing of write operations
3. **Platform Compliance**: Follows DIGIT architectural standards
4. **Maintainability**: Centralized data persistence logic
5. **Error Handling**: Robust error recovery and dead letter queue support

### 2.4 Kafka Topics
- `save-participant-registration`: Participant registration creation events
- `update-participant-registration`: Participant registration update events
- `save-participant`: Participant activation events
- `update-participant`: Participant profile update events
- `save-participant-key`: Cryptographic key creation events
- `update-participant-key`: Key rotation events

## 3. Key Objectives

### 3.1 Primary Objectives
- Enable secure registration and onboarding of government departments and agencies
- Maintain verification keys to ensure data authenticity and integrity
- Support both Data Providers and Data Consumers in the ecosystem
- Enable self-service onboarding with minimal administrative support
- Ensure proper access control and authorization management across the platform

### 3.2 Alignment with SDIP Goals
- **Proactive delivery of schemes and services**: Enable departments to quickly onboard and participate in data sharing
- **Exclusion of ineligible beneficiaries**: Ensure only authorized and verified participants can access data
- **Data backed governance and transparency**: Maintain clear records of all participants and their capabilities
- **Improved service delivery**: Reduce time-to-onboard for new departments and services

## 3. Functional Requirements

### 3.1 Participant Registration and Onboarding

#### FR-001: Department and Agency Registration
**Description**: Departments and agencies can register as Data Providers, Data Consumers, or both on the SDIP platform.

**Requirements**:
- Support registration of Government Departments
- Support registration of Local Bodies (Municipal Corporations, Panchayats)
- Support registration of Authorized Agencies (NGOs, Religious Institutions)
- Allow participants to register for multiple roles (Provider, Consumer, Certificate Issuer)
- Capture essential participant information including contact details, technical specifications, and capabilities

**Acceptance Criteria**:
- Departments can initiate registration through a self-service interface
- Registration captures all mandatory information as per SDIP standards
- System generates unique participant identifier upon successful registration
- Registration request is submitted for administrator approval

#### FR-002: Self-Service Onboarding
**Description**: It should be possible for departments to self-onboard themselves onto SDIP with no or minimal support.

**Requirements**:
- Provide intuitive registration interface for departments
- Offer guided onboarding process with clear instructions
- Enable document upload and validation capabilities
- Provide real-time status updates during the registration process
- Offer comprehensive documentation and help resources

**Acceptance Criteria**:
- Departments can complete registration without manual intervention
- System provides clear feedback at each step of the process
- Help documentation is easily accessible and comprehensive
- Registration process can be completed within reasonable time frame

#### FR-003: Asynchronous Departmental Onboarding
**Description**: Departments should be able to onboard at different times and integrate incrementally without disrupting the platform's functionality.

**Requirements**:
- Support staggered onboarding of departments
- Ensure new participant registration doesn't affect existing participants
- Enable incremental capability addition for existing participants
- Support bulk onboarding for multiple departments

**Acceptance Criteria**:
- New participant registration doesn't impact system performance
- Existing participants continue normal operations during new onboardings
- System can handle multiple simultaneous registration requests

### 3.2 Access Control and Authorization

#### FR-004: SDIP Administrator Functions
**Description**: SDIP Administrators will approve registrations and manage access control across the platform.

**Requirements**:
- Provide administrator interface for registration review and approval
- Enable approval workflow with proper audit trails
- Support rejection of registrations with reasons
- Allow modification of participant permissions and capabilities
- Enable suspension or revocation of participant access

**Acceptance Criteria**:
- Administrators can review all pending registrations
- Approval/rejection process is properly audited and logged
- Participants receive notifications about registration status changes
- Access control changes take effect immediately

#### FR-005: Role-Based Access Management
**Description**: Support different participant roles with appropriate access controls.

**Requirements**:
- Support **Data Provider** role for departments that share data
- Support **Data Consumer** role for departments that request data
- Support **Certificate Issuer** role for departments that issue digital certificates
- Enable participants to have multiple roles simultaneously
- Implement granular permission management within roles

**Acceptance Criteria**:
- Participants can be assigned appropriate roles based on their functions
- Role-based permissions are enforced across all SDIP components
- Role changes are reflected immediately in the system

#### FR-006: Authorization and Permission Management
**Description**: Once authorization is granted, participants can securely request and retrieve data according to their permissions.

**Requirements**:
- Maintain detailed permissions for each participant
- Support service-specific and data-specific access controls
- Enable dynamic permission updates without system restart
- Provide clear visibility into participant permissions
- Support time-bound or conditional permissions

**Acceptance Criteria**:
- Participants can only access data and services they are authorized for
- Permission changes are immediately effective
- Unauthorized access attempts are properly logged and blocked

### 3.3 Cryptographic Key Management

#### FR-007: Public/Private Key Pair Generation
**Description**: As part of registration, departments must generate cryptographic key pairs for secure data exchange.

**Requirements**:
- Generate unique public/private key pairs for each participant
- Support standard cryptographic algorithms (RSA, ECC)
- Securely store and manage private keys
- Publish public keys for verification purposes
- Support key rotation and renewal processes

**Acceptance Criteria**:
- Each participant receives unique cryptographic keys
- Public keys are accessible for verification by other participants
- Private keys are securely stored and not accessible to unauthorized parties
- Key generation follows industry-standard security practices

#### FR-008: Key Verification and Management
**Description**: Maintain verification keys to ensure data authenticity and support ongoing key management operations.

**Requirements**:
- Store and manage public keys for all participants
- Provide key verification APIs for other SDIP components
- Support key expiration and renewal workflows
- Enable emergency key revocation capabilities
- Maintain key usage audit trails

**Acceptance Criteria**:
- Public keys are easily accessible for verification
- Key expiration is properly managed with advance notifications
- Revoked keys are immediately invalidated across the system
- Key usage is properly logged and auditable

### 3.4 Participant Information Management

#### FR-009: Participant Profile Management
**Description**: Maintain comprehensive participant profiles with all relevant information.

**Requirements**:
- Store department/agency basic information (name, type, contact details)
- Maintain technical configuration details (API endpoints, protocols)
- Track participant capabilities and services offered
- Support participant profile updates and modifications
- Maintain historical records of participant changes

**Acceptance Criteria**:
- Participant profiles contain all necessary information for integration
- Profile updates are properly validated and approved
- Historical changes are maintained for audit purposes

#### FR-010: Service and Capability Registration
**Description**: Allow participants to publish data and services they offer for discovery by other participants.

**Requirements**:
- Enable participants to register available services and datasets
- Support service metadata and documentation
- Provide service discovery capabilities for consumers
- Allow updates to service offerings
- Support service versioning and deprecation

**Acceptance Criteria**:
- Participants can easily register and update their service offerings
- Other participants can discover available services through the registry
- Service metadata is comprehensive and accurate

## 4. Non-Functional Requirements

### 4.1 Scalability Requirements

#### NFR-001: State-Wide Scalability
**Description**: The system must be designed to seamlessly onboard any department and operate at a state-wide scale.

**Requirements**:
- Support registration of all Punjab state departments and agencies
- Handle growth in participant numbers without performance degradation
- Support distributed deployment across multiple data centers
- Enable horizontal scaling of registry components

**Success Criteria**:
- System can support 500+ registered participants
- Registration response times remain under 200ms for 95% of requests
- System maintains performance during peak registration periods

#### NFR-002: High Availability and Fault Tolerance
**Description**: Ensure continuous operation of the registry with minimal downtime.

**Requirements**:
- Achieve 99.9% uptime for registry services
- Implement automated failover mechanisms
- Support load balancing across multiple instances
- Provide disaster recovery capabilities

**Success Criteria**:
- Maximum planned downtime of 4 hours per month
- Automatic recovery from single component failures
- Data backup and restoration capabilities

### 4.2 Security Requirements

#### NFR-003: End-to-End Encryption
**Description**: All data in transit and at rest must be encrypted using strong cryptographic methods.

**Requirements**:
- Encrypt all API communications using TLS 1.3
- Encrypt sensitive data at rest using AES-256
- Implement secure key storage mechanisms
- Support encrypted backup and restoration

**Success Criteria**:
- All communications are encrypted and secure
- Data breaches do not expose sensitive information
- Encryption standards meet government security requirements

#### NFR-004: Authentication and Authorization
**Description**: Implement robust authentication and authorization mechanisms.

**Requirements**:
- Support multi-factor authentication (MFA) for administrators
- Implement role-based access control (RBAC)
- Use industry-standard authentication protocols (OAuth 2.0, JWT)
- Support integration with existing identity management systems

**Success Criteria**:
- Only authorized users can access registry functions
- Authentication mechanisms are secure and user-friendly
- Access controls are properly enforced

#### NFR-005: Audit and Monitoring
**Description**: Every action must be logged with time-stamped audit trails to detect unauthorized activity.

**Requirements**:
- Log all participant registration and modification activities
- Track all administrative actions with user attribution
- Implement real-time monitoring and alerting
- Maintain audit logs for compliance and forensics

**Success Criteria**:
- All actions are properly logged and attributable
- Suspicious activities are detected and alerted
- Audit logs meet regulatory compliance requirements

### 4.3 Performance Requirements

#### NFR-006: Response Time
**Description**: System must provide acceptable response times for all operations.

**Requirements**:
- Participant lookup queries: < 100ms for 95% of requests
- Registration submission: < 500ms for 95% of requests
- Key verification: < 50ms for 95% of requests
- Bulk operations: Complete within reasonable time limits

**Success Criteria**:
- Users experience responsive system behavior
- API SLAs are consistently met
- System performance is monitored and optimized

#### NFR-007: Concurrent User Support
**Description**: Support multiple simultaneous users and operations.

**Requirements**:
- Support 100+ concurrent administrative users
- Handle 1000+ concurrent API requests
- Process multiple registration requests simultaneously
- Maintain performance under peak load conditions

**Success Criteria**:
- System remains responsive under normal and peak loads
- Concurrent operations do not interfere with each other

### 4.4 Usability Requirements

#### NFR-008: User Interface Design
**Description**: Provide intuitive and user-friendly interfaces for all user types.

**Requirements**:
- Design responsive web interfaces for desktop and mobile
- Provide clear navigation and workflow guidance
- Implement accessibility standards (WCAG 2.1)
- Support multiple languages as required

**Success Criteria**:
- Users can complete registration without training
- Interface is accessible to users with disabilities
- User satisfaction scores meet acceptable thresholds

#### NFR-009: Documentation and Support
**Description**: Provide comprehensive documentation and support resources.

**Requirements**:
- Create user guides for different participant types
- Provide API documentation with examples
- Offer technical integration guides
- Maintain help desk and support channels

**Success Criteria**:
- Users can find answers to common questions in documentation
- Technical integration is well-documented and straightforward

### 4.5 Integration Requirements

#### NFR-010: Standards Compliance
**Description**: Follow standardized data exchange frameworks to ensure interoperability.

**Requirements**:
- Implement RESTful API design principles
- Support standard data formats (JSON, XML)
- Follow OpenAPI specification for API documentation
- Comply with government data standards

**Success Criteria**:
- APIs are easily consumable by other systems
- Data exchange follows established standards
- Integration with external systems is straightforward

#### NFR-011: Backward Compatibility
**Description**: Ensure system changes don't break existing integrations.

**Requirements**:
- Maintain API version compatibility
- Provide migration paths for breaking changes
- Support legacy authentication methods during transition
- Implement graceful deprecation processes

**Success Criteria**:
- Existing integrations continue to work after system updates
- Breaking changes are properly communicated and managed

## 5. Data Requirements

### 5.1 Participant Information
- **Department/Agency Name**: Official name and abbreviation
- **Participant Type**: Government Department, Local Body, Authorized Agency
- **Contact Information**: Primary contact, email, phone, address
- **Technical Details**: API endpoints, supported protocols, configuration
- **Roles**: Data Provider, Data Consumer, Certificate Issuer
- **Capabilities**: List of services and datasets offered
- **Status**: Active, Pending, Suspended, Revoked
- **Audit Information**: Created by, creation date, last modified details

### 5.2 Cryptographic Keys
- **Key Identifier**: Unique identifier for the key pair
- **Public Key**: For verification and encryption
- **Algorithm**: Cryptographic algorithm used (RSA, ECC)
- **Key Purpose**: Signing, encryption, verification
- **Creation Date**: When the key was generated
- **Expiration Date**: When the key expires
- **Status**: Active, Expired, Revoked

### 5.3 Registration Requests
- **Request ID**: Unique identifier for the registration request
- **Participant Information**: All details about the requesting entity
- **Requested Roles**: Roles being requested (Provider, Consumer, Issuer)
- **Supporting Documents**: Uploaded verification documents
- **Submission Details**: Submitted by, submission date
- **Review Status**: Pending, Under Review, Approved, Rejected
- **Review Information**: Reviewed by, review date, comments

## 6. System Architecture and Integration Flow

### 6.1 Overall System Flow (Local Development)

#### 6.1.1 Simplified Local Development Architecture
```
┌─────────────────────────────────────────────────────────────────────┐
│                    Local Development Setup                          │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│  │   SDIP Admin    │  │ Department      │  │    Keycloak     │     │
│  │   Portal        │  │ Self-Service    │  │      IAM        │     │
│  │   (React Web)   │  │ Portal          │  │    :8080        │     │
│  │   :3001         │  │ (React Web)     │  │                 │     │
│  │                 │  │   :3000         │  │                 │     │
│  └─────────┬───────┘  └─────────┬───────┘  └─────────┬───────┘     │
│            │                    │                    │             │
│            └────────────────────┼────────────────────┘             │
│                                 │                                  │
├─────────────────────────────────┼──────────────────────────────────┤
│            ┌────────────────────▼────────────────────┐             │
│            │     Participant Registry Service        │             │
│            │           :8080                         │             │
│            │                                         │             │
│            │  • Self-Registration Management         │             │
│            │  • User Account Creation                │             │
│            │  • Participant Application Processing   │             │
│            │  • DIGIT Service Orchestration          │             │
│            │  • Authentication Integration           │             │
│            └─────────────┬───────┬───────────────────┘             │
├─────────────────────────────────┼───────┼───────────────────────────┤
│                         ┌───────▼───┐   ▼                         │
│                         │  DIGIT    │  DIGIT                      │
│                         │  IDGen    │  Workflow                   │
│                         │  Service  │  Service                    │
│                         │  :8285    │  :8081                      │
│                         └───────────┘                             │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │              PostgreSQL Database                               │ │
│  │                     :5432                                      │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

#### 6.1.2 Two-Phase Registration Flow
```
Phase 1: Self-Registration (Account Creation)
Department Admin → Self-Service Portal → Account Creation → Login Credentials

Phase 2: Participant Application
Department Admin → Login → Complete Application → SDIP Admin Review → Approval
```

#### 6.1.3 Detailed Registration Flow (Local Development)
```
Complete Department Registration Flow:

┌─────────────────────────────────────────────────────────────────────┐
│  Phase 1: Self-Registration & Account Creation                     │
│  ├─► Department Admin visits Self-Service Portal                   │
│  ├─► Fills basic information (Name, Email, Department)             │
│  ├─► POST /api/v1/auth/self-register                              │
│  ├─► System creates Keycloak user account                         │
│  ├─► Sends email with login credentials                           │
│  └─► Admin receives username/password via email                   │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
┌─────────────────────▼───────────────────────────────────────────────┐
│  Phase 2: Login & Complete Participant Application                 │
│  ├─► Department Admin logs in with received credentials            │
│  ├─► Keycloak authentication → JWT token                          │
│  ├─► Admin accesses participant registration form                  │
│  ├─► Fills complete department details, technical info, documents  │
│  ├─► POST /api/v1/participants/register                           │
│  ├─► System generates Application ID (DIGIT IDGen)                │
│  ├─► Starts approval workflow (DIGIT Workflow)                    │
│  └─► Application submitted for SDIP Admin review                  │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
┌─────────────────────▼───────────────────────────────────────────────┐
│  Phase 3: SDIP Admin Review & Approval                             │
│  ├─► SDIP Admin Portal: GET /api/v1/admin/pending-applications    │
│  ├─► Admin reviews each participant request details               │
│  ├─► Admin can view documents, technical specifications           │
│  ├─► Admin decision: POST /api/v1/participants/{id}/approve       │
│  ├─► System generates Participant ID (DIGIT IDGen)                │
│  ├─► Creates cryptographic keys                                   │
│  ├─► Activates participant account                                │
│  └─► Sends activation notification to department                   │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
┌─────────────────────▼───────────────────────────────────────────────┐
│  Phase 4: Active Participation                                     │
│  ├─► Department admin can log into participant portal              │
│  ├─► Access SDIP services with Participant ID                     │
│  ├─► Data sharing and certificate operations                      │
│  └─► Ongoing profile and key management                           │
└─────────────────────────────────────────────────────────────────────┘
```

#### 6.1.3 Detailed Registration Workflow with DIGIT Services

```
Participant Registration Flow with DIGIT Integration:

┌─────────────────────────────────────────────────────────────────────┐
│                   Department Initiates Registration                  │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 1: Department Portal (React UI)                              │
│  ├─► Fill Registration Form                                         │
│  ├─► Upload Supporting Documents                                    │
│  ├─► Select Roles (Provider/Consumer/Issuer)                       │
│  └─► Submit Application                                            │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 2: Participant Registry Service Processing                   │
│  ├─► Validate Input Data & Business Rules                          │
│  ├─► Generate Application ID (DIGIT IDGen)                         │
│  │   └─► Format: REG-2024-01-000001                               │
│  ├─► Store Registration Request in Database                        │
│  ├─► Upload Documents to File Service                              │
│  └─► Trigger Registration Workflow (DIGIT Workflow)                │
│      └─► Business Service: "participant.registration"              │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 3: DIGIT Workflow Engine Processing                          │
│  ├─► Create Workflow Instance                                      │
│  ├─► Set Initial State: "SUBMITTED"                                │
│  ├─► Route to SDIP Operator for Initial Review                     │
│  ├─► Send Email Notification to Admin Team                         │
│  └─► Update Application Status in Registry                         │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 4: Admin Review Process (Admin Portal)                      │
│  ├─► SDIP Operator Reviews Application                             │
│  │   ├─► Action: FORWARD_TO_TECHNICAL_REVIEW                      │
│  │   ├─► Action: REJECT                                           │
│  │   └─► Action: SEND_BACK_TO_APPLICANT                           │
│  ├─► Technical Reviewer Assesses Integration                       │
│  │   ├─► Validate API Endpoints                                   │
│  │   ├─► Security Review                                          │
│  │   └─► Technical Approval/Rejection                             │
│  └─► SDIP Administrator Final Approval                             │
│      ├─► Business Case Review                                      │
│      ├─► Policy Compliance Check                                   │
│      └─► Final Approval/Rejection                                  │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 5: Post-Approval Automated Processing                        │
│  ├─► Generate Participant ID (DIGIT IDGen)                         │
│  │   ├─► Government Dept: DEPT-PB-000001-A4B2                    │
│  │   ├─► Local Body: LB-AMR-000001-C3D4                          │
│  │   └─► Auth Agency: AUTH-RELI-000001-E5F6                      │
│  ├─► Generate Cryptographic Key Pair                               │
│  │   ├─► RSA 2048-bit or ECC P-256                               │
│  │   ├─► Store Private Key Securely                               │
│  │   └─► Publish Public Key                                      │
│  ├─► Register with Certificate Service                             │
│  │   └─► Enable certificate issuance capabilities                 │
│  ├─► Update Analytics Service                                      │
│  │   └─► Track registration metrics                               │
│  ├─► Configure API Gateway Access                                  │
│  │   ├─► Setup rate limiting                                     │
│  │   ├─► Configure authentication                                 │
│  │   └─► Enable service discovery                                 │
│  └─► Send Activation Notification                                  │
│      ├─► Email with credentials                                   │
│      ├─► API documentation links                                  │
│      └─► Integration guides                                       │
└─────────────────────┬───────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 6: Department Integration & Activation                       │
│  ├─► Department Receives Activation Package                        │
│  ├─► Setup API Integration                                         │
│  ├─► Test Data Exchange                                            │
│  ├─► Go Live with SDIP Integration                                 │
│  └─► Begin Data Provider/Consumer Operations                       │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.2 UI Integration Requirements

#### 6.2.1 Department Portal Integration
**Purpose**: Self-service registration interface for departments

**UI Components Required**:
- **Registration Wizard**: Multi-step form for participant onboarding
- **Document Upload**: Secure file upload with validation
- **Status Dashboard**: Real-time registration status tracking
- **Profile Management**: Update participant information and capabilities
- **API Documentation**: Integrated technical documentation

**Integration Points**:
- **Backend APIs**: RESTful APIs for all registration operations
- **File Storage**: Integration with secure document storage service
- **Authentication**: SSO integration with government identity systems
- **Notifications**: Real-time updates via WebSockets or Server-Sent Events

#### 6.2.3 Workflow State Management in UI

**Department Portal Workflow Integration**:
```
Registration Status Dashboard UI States:

1. DRAFT (Local State)
   UI: "Complete Your Application"
   Actions: [Continue Editing, Save Draft, Submit]
   
2. SUBMITTED (Workflow State)
   UI: "Application Submitted Successfully"
   Display: Application ID, Submission Date, Next Steps
   Actions: [View Status, Upload Additional Documents]
   
3. CLARIFICATION_REQUIRED (Workflow State)
   UI: "Additional Information Required"
   Display: Admin Comments, Required Documents
   Actions: [Upload Documents, Resubmit, Withdraw]
   
4. TECHNICAL_REVIEW (Workflow State)
   UI: "Under Technical Review"
   Display: Review Stage, Estimated Completion
   Actions: [View Status, Contact Support]
   
5. ADMIN_APPROVAL (Workflow State)
   UI: "Pending Final Approval"
   Display: Current Approver, Expected Timeline
   Actions: [View Status, Track Progress]
   
6. APPROVED (Workflow State)
   UI: "Application Approved"
   Display: Participant ID, Integration Guide
   Actions: [Download Credentials, View Documentation]
   
7. ACTIVE (Final State)
   UI: "Registration Complete - Account Active"
   Display: Dashboard, API Status, Usage Statistics
   Actions: [Manage Profile, View Analytics, Request Changes]
```

**Admin Portal Workflow Management**:
```
Admin Workflow Interface:

1. Registration Queue View
   ├─► Filter by State: [SUBMITTED, TECHNICAL_REVIEW, ADMIN_APPROVAL]
   ├─► Sort by: [Date, Priority, Department Type]
   ├─► Bulk Actions: [Assign Reviewer, Export List]
   └─► SLA Indicators: [On Time, Overdue, Critical]

2. Application Detail View
   ├─► Workflow History Timeline
   ├─► Current State and Available Actions
   ├─► Document Viewer with Approval Controls
   ├─► Comment System for Review Notes
   └─► Action Buttons Based on Current State:
       ├─► SUBMITTED: [Forward to Technical, Reject, Send Back]
       ├─► TECHNICAL_REVIEW: [Approve, Reject, Send Back]
       └─► ADMIN_APPROVAL: [Final Approve, Reject, Return]

3. Workflow Analytics Dashboard
   ├─► Processing Time Metrics
   ├─► Approval Rate Statistics
   ├─► SLA Compliance Reports
   └─► Bottleneck Analysis
```

#### 6.2.2 Admin Portal Integration
**Purpose**: Administrative interface for registration management

**UI Components Required**:
- **Registration Queue**: List of pending applications with filtering
- **Application Review**: Detailed view for application assessment
- **Bulk Operations**: Approve/reject multiple applications
- **Participant Management**: Manage existing participants
- **System Analytics**: Dashboard for system health and usage

**Integration Points**:
- **Workflow Service**: Integration with DIGIT Workflow for approval processes
- **Analytics Service**: Real-time dashboards and reporting
- **Audit Service**: Complete audit trail visibility
- **Notification Service**: Admin alerts and system notifications

### 6.3 DIGIT Services Integration

#### 6.3.1 DIGIT Workflow Service Integration

**Purpose**: Automate and manage the participant registration approval workflow

**Integration Requirements**:

**FR-011: Workflow Configuration**
- Define approval workflow for participant registration
- Support multi-level approval process based on participant type
- Enable conditional routing based on registration criteria
- Support escalation rules for delayed approvals
- Maintain workflow state and history

**FR-012: Workflow Triggers**
- Trigger workflow on new registration submission
- Handle workflow events (approval, rejection, escalation)
- Support manual and automatic workflow transitions
- Enable workflow pause and resume capabilities
- Trigger notifications at each workflow stage

**Technical Integration**:
```yaml
Workflow Service Integration:

Registration Workflow Definition:
  - Name: "participant-registration-workflow"
  - Stages:
    1. Initial Validation
       - Auto-validation of required fields
       - Document completeness check
       - Duplicate registration check
    
    2. Technical Review
       - Assigned to: Technical Team Lead
       - Duration: 2 business days
       - Tasks: API endpoint validation, security review
    
    3. Administrative Approval
       - Assigned to: SDIP Administrator
       - Duration: 3 business days
       - Tasks: Final approval, access rights assignment
    
    4. Post-Approval Setup
       - Auto-execution of participant setup
       - Key generation and distribution
       - Service registration

API Integration Points:
  - POST /workflow/v1/process/participant-registration
  - GET /workflow/v1/process/{processId}/status
  - PUT /workflow/v1/process/{processId}/action
  - GET /workflow/v1/process/{processId}/history
```

**FR-013: Workflow Data Exchange**
- Pass registration data to workflow service
- Receive workflow status updates
- Handle workflow completion events
- Support custom workflow data fields
- Maintain data consistency between services

#### 6.3.3 DIGIT Workflow Service Configuration

**Workflow Business Services for Participant Registry**:

#### 6.3.3.1 Primary Registration Workflow
**Business Service**: `participant.registration`
**Purpose**: Handle new participant registration approval process

**Workflow States and Transitions**:
```
Initial State (null)
    │ SUBMIT
    ├─► SUBMITTED
    │   ├─► FORWARD_TO_TECHNICAL_REVIEW → TECHNICAL_REVIEW
    │   ├─► REJECT → REJECTED
    │   └─► SEND_BACK_TO_APPLICANT → CLARIFICATION_REQUIRED
    │
    ├─► CLARIFICATION_REQUIRED
    │   ├─► RESUBMIT → SUBMITTED
    │   └─► WITHDRAW → WITHDRAWN
    │
    ├─► TECHNICAL_REVIEW
    │   ├─► TECHNICAL_APPROVE → ADMIN_APPROVAL
    │   ├─► TECHNICAL_REJECT → REJECTED
    │   └─► SEND_BACK_FOR_CLARIFICATION → CLARIFICATION_REQUIRED
    │
    ├─► ADMIN_APPROVAL
    │   ├─► APPROVE → APPROVED
    │   ├─► REJECT → REJECTED
    │   └─► SEND_BACK_TO_TECHNICAL → TECHNICAL_REVIEW
    │
    ├─► APPROVED
    │   └─► ACTIVATE → ACTIVE
    │
    └─► ACTIVE
        ├─► SUSPEND → SUSPENDED
        └─► REVOKE → REVOKED
```

**Role Definitions**:
- **PARTICIPANT_APPLICANT**: Department users initiating registration
- **SDIP_OPERATOR**: Initial processing and validation
- **TECHNICAL_REVIEWER**: Technical assessment of integration capabilities
- **SDIP_ADMINISTRATOR**: Final approval authority
- **SYSTEM_USER**: Automated system processes

**SLA Configuration**:
- **SUBMITTED**: 2 days (172800000 ms)
- **CLARIFICATION_REQUIRED**: 1 day (86400000 ms)
- **TECHNICAL_REVIEW**: 2 days (172800000 ms)
- **ADMIN_APPROVAL**: 3 days (259200000 ms)
- **Total Process SLA**: 5 days (432000000 ms)

#### 6.3.3.2 Profile Update Workflow
**Business Service**: `participant.profile.update`
**Purpose**: Handle participant profile modification requests

**Workflow States**:
- **UPDATE_SUBMITTED**: Initial profile update request
- **UPDATE_APPROVED**: Profile changes approved and applied
- **UPDATE_REJECTED**: Profile changes rejected

**SLA**: 2 days for profile update approval

#### 6.3.3.3 Key Rotation Workflow
**Business Service**: `participant.key.rotation`
**Purpose**: Handle cryptographic key rotation requests

**Workflow States**:
- **KEY_ROTATION_REQUESTED**: Request for key rotation submitted
- **KEY_ROTATION_APPROVED**: Key rotation approved and executed
- **KEY_ROTATION_REJECTED**: Key rotation request rejected

**SLA**: 12 hours for key rotation approval (43200000 ms)

**FR-017: Workflow Integration Requirements**
- Integrate with DIGIT Workflow Service for all approval processes
- Support automatic state transitions based on business rules
- Enable manual interventions at defined checkpoints
- Maintain complete audit trail of workflow actions
- Support escalation mechanisms for SLA breaches
- Enable bulk operations for administrative efficiency

**FR-018: Workflow Event Handling**
- Listen to workflow state change events
- Trigger appropriate actions based on workflow transitions
- Update participant status in registry based on workflow state
- Send notifications for each workflow transition
- Handle workflow failures and error conditions gracefully

#### 6.3.3.4 Workflow Service Integration Code Examples

**Workflow Initiation**:
```java
@Service
public class ParticipantWorkflowService {
    
    @Autowired
    private WorkflowService workflowService;
    
    public void initiateRegistrationWorkflow(RegistrationRequest request) {
        ProcessRequest processRequest = ProcessRequest.builder()
            .businessKey(request.getApplicationId())
            .businessService("participant.registration")
            .action("SUBMIT")
            .moduleName("participant-registry")
            .tenantId("pb")
            .businessId(request.getApplicationId())
            .documents(request.getDocuments())
            .assignee(null) // Will be auto-assigned
            .comment("New participant registration submitted")
            .build();
        
        workflowService.transition(processRequest);
    }
    
    public void approveRegistration(String applicationId, String approverUuid, String comments) {
        ProcessRequest processRequest = ProcessRequest.builder()
            .businessKey(applicationId)
            .businessService("participant.registration")
            .action("APPROVE")
            .assignee(approverUuid)
            .comment(comments)
            .tenantId("pb")
            .build();
        
        workflowService.transition(processRequest);
    }
}
```

**Workflow State Listener**:
```java
@Component
public class ParticipantWorkflowListener {
    
    @EventListener
    public void handleWorkflowStateChange(WorkflowStateChangeEvent event) {
        if ("participant.registration".equals(event.getBusinessService())) {
            switch (event.getNewState()) {
                case "APPROVED":
                    participantService.activateParticipant(event.getBusinessKey());
                    notificationService.sendApprovalNotification(event.getBusinessKey());
                    break;
                case "REJECTED":
                    participantService.rejectParticipant(event.getBusinessKey(), event.getComment());
                    notificationService.sendRejectionNotification(event.getBusinessKey());
                    break;
                case "CLARIFICATION_REQUIRED":
                    notificationService.sendClarificationRequest(event.getBusinessKey());
                    break;
            }
        }
    }
}
```

### 6.4 Service Integration Points

#### 6.4.1 Internal SDIP Components

**Certificate Service Integration**:
- **Registration**: Register new participants for certificate issuance
- **Key Management**: Share public keys for certificate verification
- **Authorization**: Validate participant permissions for certificate operations
- **Audit**: Track certificate-related participant activities

**ID Matching Service Integration**:
- **Participant Authentication**: Verify participant identity for ID mapping
- **Service Authorization**: Validate participant permissions for ID operations
- **Data Sharing**: Share participant metadata for matching algorithms

**Analytics Service Integration**:
- **Event Streaming**: Send participant registration and activity events
- **Performance Monitoring**: Share service metrics and health data
- **Usage Analytics**: Provide participant activity data for reporting
- **Audit Integration**: Feed audit data for compliance reporting

**Request Service Integration**:
- **Authorization**: Validate participant permissions for data requests
- **Service Discovery**: Provide participant capability information
- **Rate Limiting**: Share participant quotas and usage limits

#### 6.4.2 External System Integration

**Government Identity Management**:
- **SSO Integration**: Single sign-on for department users
- **User Authentication**: Validate user credentials
- **Role Mapping**: Map government roles to SDIP permissions
- **Directory Integration**: Sync organizational structure

**Government PKI Infrastructure**:
- **Certificate Authority**: Integration for digital certificates
- **Key Validation**: Verify cryptographic keys against PKI standards
- **Certificate Lifecycle**: Manage certificate renewal and revocation

**Notification Services**:
- **Email Gateway**: Send registration updates and notifications
- **SMS Gateway**: Send mobile notifications for critical updates
- **Push Notifications**: Real-time alerts for web and mobile apps

### 6.5 Data Flow Architecture

#### 6.5.1 Registration Data Flow
```
Registration Request Flow:

1. Department Submission
   Department Portal → API Gateway → Participant Registry Service
   ├─► Validate Request Data
   ├─► Generate Application ID (IDGen Service)
   ├─► Store Registration Request
   ├─► Trigger Workflow (Workflow Service)
   └─► Send Confirmation to Department

2. Workflow Processing
   Workflow Service → Participant Registry Service
   ├─► Route to Appropriate Approver
   ├─► Send Notifications
   ├─► Track Approval Status
   └─► Handle Escalations

3. Approval Processing
   Admin Portal → API Gateway → Participant Registry Service
   ├─► Retrieve Application Details
   ├─► Update Approval Status
   ├─► Trigger Post-Approval Actions
   └─► Update Workflow Status

4. Participant Activation
   Participant Registry Service → Multiple Services
   ├─► Generate Participant ID (IDGen Service)
   ├─► Create Cryptographic Keys
   ├─► Register with Certificate Service
   ├─► Update Analytics Service
   ├─► Configure API Gateway Access
   └─► Send Activation Notifications
```

#### 6.5.2 Operational Data Flow
```
Ongoing Operations Flow:

1. Participant Verification
   External Service → API Gateway → Participant Registry Service
   ├─► Lookup Participant Details
   ├─► Verify Participant Status
   ├─► Validate Permissions
   └─► Return Verification Result

2. Key Management
   Participant Registry Service → Certificate Service
   ├─► Key Rotation Events
   ├─► Certificate Updates
   ├─► Revocation Notifications
   └─► Audit Trail Updates

3. Analytics and Monitoring
   Participant Registry Service → Analytics Service
   ├─► Registration Metrics
   ├─► Usage Statistics
   ├─► Performance Data
   └─► Error Reports
```

### 6.6 API Integration Specifications

#### 6.6.1 DIGIT Workflow Integration APIs

**Workflow Transition API** (Moving between workflow states):
```yaml
POST /egov-workflow-v2/egov-wf/process/_transition
Content-Type: application/json

Request Body:
{
  "RequestInfo": {
    "apiId": "Rainmaker",
    "action": "",
    "did": 1,
    "key": "",
    "msgId": "20240115130900|en_IN",
    "requesterId": "",
    "ts": 1705311000000,
    "ver": ".01",
    "authToken": "{jwt-token}",
    "userInfo": {
      "id": 101,
      "uuid": "user-uuid",
      "userName": "USERNAME",
      "name": "User Name",
      "mobileNumber": "9876543210",
      "emailId": "user@punjab.gov.in",
      "type": "EMPLOYEE",
      "roles": [
        {
          "name": "Role Name",
          "code": "ROLE_CODE",
          "tenantId": "pb"
        }
      ],
      "active": true,
      "tenantId": "pb"
    }
  },
  "ProcessInstances": [
    {
      "moduleName": "participant-registry-service",
      "tenantId": "pb",
      "businessService": "participant.registration",
      "businessId": "REG-2024-01-000001",
      "action": "SUBMIT",
      "comment": "Application submitted for review",
      "assignee": "assignee-uuid",
      "previousStatus": null
    }
  ]
}
```

**Workflow Search API** (Getting workflow status and history):
```yaml
GET /egov-workflow-v2/egov-wf/process/_search?tenantId=pb&history=true&businessIds=REG-2024-01-000001
Content-Type: application/json

Request Body:
{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "action": "",
    "did": 1,
    "key": "",
    "msgId": "20240115130900|en_IN",
    "requesterId": "",
    "ts": 1705311000000,
    "ver": "1.0.0",
    "authToken": "{jwt-token}",
    "userInfo": {
      "uuid": "user-uuid"
    }
  }
}

Query Parameters:
- tenantId: "pb" (required)
- history: true/false (optional, default false)
- businessIds: comma-separated list of business IDs
- businessService: "participant.registration" (optional)
- status: comma-separated list of statuses (optional)
- assignee: assignee UUID (optional)
```

#### 6.6.2 DIGIT IDGen Integration APIs

**ID Generation API**:
```yaml
POST /egov-idgen/id/_generate
Content-Type: application/json

Request Body:
{
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "{jwt-token}",
    "userInfo": {
      "id": 101,
      "uuid": "user-uuid",
      "userName": "USERNAME",
      "name": "User Name",
      "mobileNumber": "9876543210",
      "emailId": "user@punjab.gov.in",
      "type": "EMPLOYEE",
      "roles": [
        {
          "name": "Role Name",
          "code": "ROLE_CODE",
          "tenantId": "pb"
        }
      ],
      "active": true,
      "tenantId": "pb"
    },
    "msgId": "1705311000000|en_IN",
    "plainAccessRequest": {}
  },
  "idRequests": [
    {
      "idName": "registration.application",
      "tenantId": "pb"
    }
  ]
}

Response:
{
  "ResponseInfo": {
    "apiId": "Rainmaker",
    "ver": "1.0.0",
    "ts": 1705311000000,
    "resMsgId": "uief87324",
    "msgId": "1705311000000|en_IN",
    "status": "successful"
  },
  "idResponses": [
    {
      "idName": "registration.application",
      "id": "REG-2024-01-000001"
    }
  ]
}
```

**Supported ID Names for Participant Registry**:
- `registration.application` → Generates: REG-2024-01-000001
- `participant.government.dept` → Generates: DEPT-PB-000001-A4B2
- `participant.local.body` → Generates: LB-AMR-000001-C3D4
- `participant.authorized.agency` → Generates: AUTH-RELI-000001-E5F6
- `profile.update.request` → Generates: PU-2024-01-000001
- `key.rotation.request` → Generates: KR-2024-01-000001

#### 6.6.3 Internal Service Integration APIs

**Certificate Service Integration**:
```yaml
POST /certificate/v1/participants/register
Headers:
  Content-Type: application/json
  Authorization: Bearer {jwt-token}
Body:
{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "authToken": "{jwt-token}",
    "userInfo": { ... }
  },
  "participants": [
    {
      "participantId": "DEPT-PB-000001-A4B2",
      "publicKey": "{public-key-content}",
      "capabilities": ["CERTIFICATE_ISSUER"],
      "tenantId": "pb"
    }
  ]
}
```

**Analytics Service Integration**:
```yaml
POST /analytics/v1/events
Headers:
  Content-Type: application/json
  Authorization: Bearer {jwt-token}
Body:
{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "authToken": "{jwt-token}",
    "userInfo": { ... }
  },
  "events": [
    {
      "eventType": "PARTICIPANT_REGISTERED",
      "participantId": "DEPT-PB-000001-A4B2",
      "timestamp": "2024-01-15T10:30:00Z",
      "tenantId": "pb",
      "metadata": {
        "departmentName": "Department of Social Welfare",
        "registrationDuration": "5 days"
      }
    }
  ]
}
```

**ID Matching Service Integration**:
```yaml
POST /id-matching/v1/participants/register
Headers:
  Content-Type: application/json
  Authorization: Bearer {jwt-token}
Body:
{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "authToken": "{jwt-token}",
    "userInfo": { ... }
  },
  "participants": [
    {
      "participantId": "DEPT-PB-000001-A4B2",
      "identifiers": [
        {
          "type": "PARTICIPANT_ID",
          "value": "DEPT-PB-000001-A4B2"
        },
        {
          "type": "APPLICATION_ID", 
          "value": "REG-2024-01-000001"
        }
      ],
      "tenantId": "pb"
    }
  ]
}
```

### 6.7 Error Handling and Rollback Procedures

#### 6.7.1 Integration Failure Handling
- **Workflow Service Failure**: Retry mechanism with exponential backoff
- **IDGen Service Failure**: Fallback to temporary ID generation
- **Certificate Service Failure**: Queue registration for retry
- **Analytics Service Failure**: Store events locally for later sync

#### 6.7.2 Data Consistency Management
- **Distributed Transaction Support**: Use saga pattern for cross-service operations
- **Compensation Actions**: Define rollback procedures for each integration point
- **Event Sourcing**: Maintain event log for system state reconstruction
- **Audit Trail**: Complete audit trail for troubleshooting integration issues

## 7. Application Configuration

### 7.1 Server Configuration
```properties
server.contextPath=/participant-registry-service
server.servlet.contextPath=/participant-registry-service
server.port=8080
app.timezone=UTC
```

### 7.2 Database Configuration
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/sdip_participant_registry
spring.datasource.username=postgres
spring.datasource.password=root
```

### 7.3 Flyway Configuration
```properties
spring.flyway.url=jdbc:postgresql://localhost:5432/sdip_participant_registry
spring.flyway.user=postgres
spring.flyway.password=root
spring.flyway.baseline-on-migrate=true
spring.flyway.outOfOrder=true
spring.flyway.locations=classpath:/db/migration/main
spring.flyway.enabled=true
```

### 7.4 Kafka Configuration
```properties
kafka.config.bootstrap_server_config=localhost:9092
spring.kafka.consumer.group-id=participant-registry
participant.registry.kafka.create.registration.topic=save-participant-registration
participant.registry.kafka.update.registration.topic=update-participant-registration
participant.registry.kafka.create.participant.topic=save-participant
participant.registry.kafka.update.participant.topic=update-participant
participant.registry.kafka.create.key.topic=save-participant-key
participant.registry.kafka.update.key.topic=update-participant-key
```

### 7.5 External Service URLs
```properties
# MDMS Service
egov.mdms.host=http://localhost:8094
egov.mdms.search.endpoint=/mdms-v2/v1/_search

# User Service
egov.user.host=http://localhost:8081
egov.user.context.path=/user/users
egov.user.create.path=/_createnovalidate
egov.user.search.path=/user/_search
egov.user.update.path=/_updatenovalidate

# ID Generation Service
egov.idgen.host=http://localhost:8285
egov.idgen.path=/egov-idgen/id/_generate

# Workflow Service
egov.workflow.host=http://localhost:8081
egov.workflow.transition.path=/egov-workflow-v2/egov-wf/process/_transition
egov.workflow.businessservice.search.path=/egov-workflow-v2/egov-wf/businessservice/_search
egov.workflow.processinstance.search.path=/egov-workflow-v2/egov-wf/process/_search

# Keycloak Configuration
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=sdip
keycloak.resource=participant-registry-service
keycloak.credentials.secret=participant-registry-secret

# File Service
egov.filestore.host=http://localhost:8083
egov.filestore.endpoint=/filestore/v1/files

# Notification Service
egov.notification.email.host=http://localhost:8089
egov.notification.email.endpoint=/notification-sms/v1/_create
egov.notification.sms.host=http://localhost:8089
egov.notification.sms.endpoint=/notification-sms/v1/_create

# Encryption Service
egov.enc.host=http://localhost:8087
egov.enc.encrypt.endpoint=/crypto-service/crypto/_encrypt
egov.enc.decrypt.endpoint=/crypto-service/crypto/_decrypt

# Certificate Service
sdip.certificate.host=http://localhost:8090
sdip.certificate.register.endpoint=/certificate/v1/participants/register

# Analytics Service
sdip.analytics.host=http://localhost:8091
sdip.analytics.events.endpoint=/analytics/v1/events
```

### 7.6 Search Configuration
```properties
participant.registry.default.offset=0
participant.registry.default.limit=100
participant.registry.search.max.limit=200
```

### 7.7 Encryption Configuration
```properties
# Encryption keys for PII fields
egov.enc.key.participant.contact.email=ParticipantContactEmailEncrypt
egov.enc.key.participant.contact.phone=ParticipantContactPhoneEncrypt
egov.enc.key.participant.decrypt=ParticipantDecrypt
```

## 8. Docker Compose Configuration for External Services

### 8.1 Docker Compose Setup Instructions

Create a `docker-compose.yml` file for hosting external DIGIT services that the Participant Registry Service depends on. This setup allows the registry service to connect to externally hosted Kafka, PostgreSQL, IDGen, MDMS, and Workflow services.

### 8.2 Network Configuration

Create a custom Docker network to enable communication between services:

```bash
# Create custom network for SDIP services
docker network create sdip-network --driver bridge --subnet=172.20.0.0/16
```

### 8.3 Docker Compose File

```yaml
# docker-compose.yml
version: '3.8'

networks:
  sdip-network:
    external: true
    name: sdip-network

services:
  # PostgreSQL Database
  postgres:
    image: postgres:13
    container_name: sdip-postgres
    networks:
      - sdip-network
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: sdip_registry
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d/
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Apache Kafka
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: sdip-zookeeper
    networks:
      - sdip-network
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    restart: unless-stopped

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: sdip-kafka
    networks:
      - sdip-network
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper
    restart: unless-stopped
    healthcheck:
      test: kafka-topics --bootstrap-server kafka:9092 --list
      interval: 30s
      timeout: 10s
      retries: 3

  # Redis for caching
  redis:
    image: redis:7.0-alpine
    container_name: sdip-redis
    networks:
      - sdip-network
    ports:
      - "6379:6379"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3

  # DIGIT IDGen Service
  idgen-service:
    image: egovio/egov-idgen:v1.3.2-48a03ad102-3
    container_name: sdip-idgen-service
    networks:
      - sdip-network
    ports:
      - "8285:8080"
    environment:
      SERVER_PORT: 8080
      JAVA_OPTS: -Xmx256m -Xms256m
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/sdip_registry
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_KAFKA_CONSUMER_GROUP_ID: idgen-service
      EGOV_MDMS_HOST: http://mdms-service:8080
      EGOV_MDMS_SEARCH_ENDPOINT: /mdms-v2/v1/_search
      LOGGING_LEVEL_ORG_EGOV: DEBUG
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/egov-idgen/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  # DIGIT MDMS Service
  mdms-service:
    image: egovio/mdms-service:v1.3.2-48a03ad102-24
    container_name: sdip-mdms-service
    networks:
      - sdip-network
    ports:
      - "8094:8080"
    environment:
      SERVER_PORT: 8080
      JAVA_OPTS: -Xmx512m -Xms512m
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/sdip_registry
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      EGOV_MDMS_CONF_PATH: https://raw.githubusercontent.com/egovernments/egov-mdms-data/master/data
      LOGGING_LEVEL_ORG_EGOV: DEBUG
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/mdms-v2/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  # DIGIT Workflow Service
  workflow-service:
    image: egovio/egov-workflow-v2:v1.3.2-48a03ad102-24
    container_name: sdip-workflow-service
    networks:
      - sdip-network
    ports:
      - "8081:8080"
    environment:
      SERVER_PORT: 8080
      JAVA_OPTS: -Xmx512m -Xms512m
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/sdip_registry
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_KAFKA_CONSUMER_GROUP_ID: workflow-service
      EGOV_MDMS_HOST: http://mdms-service:8080
      EGOV_MDMS_SEARCH_ENDPOINT: /mdms-v2/v1/_search
      EGOV_USER_HOST: http://user-service:8080
      LOGGING_LEVEL_ORG_EGOV: DEBUG
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
      mdms-service:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/egov-workflow-v2/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  # DIGIT User Service (for user management)
  user-service:
    image: egovio/egov-user:v1.3.2-48a03ad102-9
    container_name: sdip-user-service
    networks:
      - sdip-network
    ports:
      - "8082:8080"
    environment:
      SERVER_PORT: 8080
      JAVA_OPTS: -Xmx256m -Xms256m
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/sdip_registry
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_KAFKA_CONSUMER_GROUP_ID: user-service
      EGOV_MDMS_HOST: http://mdms-service:8080
      EGOV_MDMS_SEARCH_ENDPOINT: /mdms-v2/v1/_search
      LOGGING_LEVEL_ORG_EGOV: DEBUG
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
      mdms-service:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/user/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  # DIGIT Persister Service
  persister-service:
    image: egovio/egov-persister:v1.1.4-48a03ad102-4
    container_name: sdip-persister-service
    networks:
      - sdip-network
    ports:
      - "8083:8080"
    environment:
      SERVER_PORT: 8080
      JAVA_OPTS: -Xmx512m -Xms512m
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/sdip_registry
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_KAFKA_CONSUMER_GROUP_ID: persister-service
      PERSISTER_CONFIG_PATH: /opt/egov/participant-registry-persister.yml
      LOGGING_LEVEL_ORG_EGOV: DEBUG
    volumes:
      - ./persister-configs:/opt/egov/
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/egov-persister/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Keycloak for Authentication
  keycloak:
    image: quay.io/keycloak/keycloak:21.1.0
    container_name: sdip-keycloak
    networks:
      - sdip-network
    ports:
      - "8080:8080"
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin123
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/sdip_registry
      KC_DB_USERNAME: postgres
      KC_DB_PASSWORD: root
      KC_HOSTNAME_STRICT: false
      KC_HTTP_ENABLED: true
    command: start-dev
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/health/ready || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
    driver: local
```

### 8.4 Service URLs After Docker Setup

Once the Docker Compose setup is running, the services will be available at:

- **PostgreSQL**: `localhost:5432`
- **Kafka**: `localhost:9092` (internal), `localhost:29092` (external)
- **Redis**: `localhost:6379`
- **Keycloak**: `http://localhost:8080`
- **IDGen Service**: `http://localhost:8285`
- **MDMS Service**: `http://localhost:8094`
- **Workflow Service**: `http://localhost:8081`
- **User Service**: `http://localhost:8082`
- **Persister Service**: `http://localhost:8083`

### 8.5 Docker Compose Commands

**Start all services:**
```bash
docker-compose up -d
```

**Check service health:**
```bash
docker-compose ps
docker-compose logs -f [service-name]
```

**Stop all services:**
```bash
docker-compose down
```

**Stop and remove volumes:**
```bash
docker-compose down -v
```

## 9. Compliance and Regulatory Requirements

### 9.1 Data Privacy
- Comply with applicable data protection regulations
- Implement privacy-by-design principles
- Support data subject rights (access, correction, deletion)
- Maintain data processing audit trails

### 9.2 Security Standards
- Follow government cybersecurity guidelines
- Implement industry-standard security practices
- Regular security assessments and penetration testing
- Compliance with ISO 27001 standards

### 9.3 Audit and Governance
- Maintain comprehensive audit trails
- Support regulatory compliance reporting
- Implement data governance policies
- Regular compliance reviews and assessments

## 10. Success Metrics

### 10.1 Operational Metrics
- **Registration Success Rate**: > 95% of valid registrations approved
- **Time to Onboard**: < 5 business days for standard registrations
- **System Uptime**: > 99.9% availability
- **Response Times**: Meet all defined SLA targets

### 10.2 User Satisfaction
- **User Experience Score**: > 4.0/5.0 rating from participants
- **Support Ticket Volume**: < 10% of registrations require support
- **Documentation Completeness**: > 95% of questions answered in docs

### 10.3 Security Metrics
- **Security Incidents**: Zero critical security breaches
- **Access Control Violations**: < 0.1% of access attempts
- **Audit Compliance**: 100% compliance with audit requirements

## 11. Assumptions and Dependencies

### 11.1 Assumptions
- Departments have basic IT infrastructure for API integration
- Participants have designated technical contacts for integration
- Government PKI infrastructure is available for key management
- Network connectivity is reliable across government departments

### 11.2 Dependencies
- SDIP platform infrastructure and shared services
- Government identity management systems
- Secure network infrastructure between departments
- Administrative approval processes for participant onboarding

## 12. Risks and Mitigation

### 12.1 Technical Risks
- **Risk**: Key management complexity
- **Mitigation**: Use proven cryptographic libraries and follow industry standards

- **Risk**: Scalability challenges with high participant volumes
- **Mitigation**: Design for horizontal scaling and implement performance monitoring

### 12.2 Operational Risks
- **Risk**: Slow adoption by departments
- **Mitigation**: Provide comprehensive support and documentation

- **Risk**: Security vulnerabilities
- **Mitigation**: Regular security assessments and prompt patching

## 13. Appendix A: DIGIT Workflow Configuration Commands

### A.1 Primary Registration Workflow Setup

Use the following CURL command to create the participant registration workflow in DIGIT Workflow Service:

```bash
curl --location 'http://localhost:8081/egov-workflow-v2/egov-wf/businessservice/_create' \
--header 'Content-Type: application/json' \
--data '{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "action": "",
    "did": 1,
    "key": "",
    "msgId": "20240115130900|en_IN",
    "requesterId": "",
    "ts": 1705311000000,
    "ver": "1.0.0",
    "authToken": "participant-registry-auth-token",
    "userInfo": {
      "uuid": "sdip-admin-uuid"
    }
  },
  "BusinessServices": [
    {
      "tenantId": "pb",
      "businessService": "participant.registration",
      "business": "participant-registry-service",
      "businessServiceSla": 432000000,
      "states": [
        {
          "sla": null,
          "state": null,
          "applicationStatus": null,
          "docUploadRequired": true,
          "isStartState": true,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "SUBMIT",
              "nextState": "SUBMITTED",
              "roles": [
                "PARTICIPANT_APPLICANT",
                "DEPARTMENT_USER"
              ]
            }
          ]
        },
        {
          "sla": 172800000,
          "state": "SUBMITTED",
          "applicationStatus": "SUBMITTED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "FORWARD_TO_TECHNICAL_REVIEW",
              "nextState": "TECHNICAL_REVIEW",
              "roles": [
                "SDIP_OPERATOR",
                "REGISTRY_CLERK"
              ]
            },
            {
              "action": "REJECT",
              "nextState": "REJECTED",
              "roles": [
                "SDIP_OPERATOR",
                "REGISTRY_CLERK"
              ]
            },
            {
              "action": "SEND_BACK_TO_APPLICANT",
              "nextState": "CLARIFICATION_REQUIRED",
              "roles": [
                "SDIP_OPERATOR",
                "REGISTRY_CLERK"
              ]
            }
          ]
        },
        {
          "sla": 86400000,
          "state": "CLARIFICATION_REQUIRED",
          "applicationStatus": "CLARIFICATION_REQUIRED",
          "docUploadRequired": true,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "RESUBMIT",
              "nextState": "SUBMITTED",
              "roles": [
                "PARTICIPANT_APPLICANT",
                "DEPARTMENT_USER"
              ]
            },
            {
              "action": "WITHDRAW",
              "nextState": "WITHDRAWN",
              "roles": [
                "PARTICIPANT_APPLICANT",
                "DEPARTMENT_USER"
              ]
            }
          ]
        },
        {
          "sla": 172800000,
          "state": "TECHNICAL_REVIEW",
          "applicationStatus": "UNDER_TECHNICAL_REVIEW",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "TECHNICAL_APPROVE",
              "nextState": "ADMIN_APPROVAL",
              "roles": [
                "TECHNICAL_REVIEWER",
                "SDIP_TECHNICAL_LEAD"
              ]
            },
            {
              "action": "TECHNICAL_REJECT",
              "nextState": "REJECTED",
              "roles": [
                "TECHNICAL_REVIEWER",
                "SDIP_TECHNICAL_LEAD"
              ]
            },
            {
              "action": "SEND_BACK_FOR_CLARIFICATION",
              "nextState": "CLARIFICATION_REQUIRED",
              "roles": [
                "TECHNICAL_REVIEWER",
                "SDIP_TECHNICAL_LEAD"
              ]
            }
          ]
        },
        {
          "sla": 259200000,
          "state": "ADMIN_APPROVAL",
          "applicationStatus": "PENDING_ADMIN_APPROVAL",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "APPROVE",
              "nextState": "APPROVED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            },
            {
              "action": "REJECT",
              "nextState": "REJECTED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            },
            {
              "action": "SEND_BACK_TO_TECHNICAL",
              "nextState": "TECHNICAL_REVIEW",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "APPROVED",
          "applicationStatus": "APPROVED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": [
            {
              "action": "ACTIVATE",
              "nextState": "ACTIVE",
              "roles": [
                "SYSTEM_USER",
                "SDIP_ADMINISTRATOR"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "ACTIVE",
          "applicationStatus": "ACTIVE",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "SUSPEND",
              "nextState": "SUSPENDED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            },
            {
              "action": "REVOKE",
              "nextState": "REVOKED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "SUSPENDED",
          "applicationStatus": "SUSPENDED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "REACTIVATE",
              "nextState": "ACTIVE",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            },
            {
              "action": "REVOKE",
              "nextState": "REVOKED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "SDIP_ADMIN"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "REJECTED",
          "applicationStatus": "REJECTED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        },
        {
          "sla": null,
          "state": "WITHDRAWN",
          "applicationStatus": "WITHDRAWN",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        },
        {
          "sla": null,
          "state": "REVOKED",
          "applicationStatus": "REVOKED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        }
      ]
    }
  ]
}'
```

### A.2 Profile Update Workflow Setup

```bash
curl --location 'http://localhost:8081/egov-workflow-v2/egov-wf/businessservice/_create' \
--header 'Content-Type: application/json' \
--data '{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "action": "",
    "did": 2,
    "key": "",
    "msgId": "20240115131000|en_IN",
    "requesterId": "",
    "ts": 1705311060000,
    "ver": "1.0.0",
    "authToken": "participant-registry-auth-token",
    "userInfo": {
      "uuid": "sdip-admin-uuid"
    }
  },
  "BusinessServices": [
    {
      "tenantId": "pb",
      "businessService": "participant.profile.update",
      "business": "participant-registry-service",
      "businessServiceSla": 172800000,
      "states": [
        {
          "sla": null,
          "state": null,
          "applicationStatus": null,
          "docUploadRequired": false,
          "isStartState": true,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "SUBMIT_UPDATE",
              "nextState": "UPDATE_SUBMITTED",
              "roles": [
                "PARTICIPANT_USER",
                "DEPARTMENT_ADMIN"
              ]
            }
          ]
        },
        {
          "sla": 86400000,
          "state": "UPDATE_SUBMITTED",
          "applicationStatus": "UPDATE_PENDING",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "APPROVE_UPDATE",
              "nextState": "UPDATE_APPROVED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "REGISTRY_MANAGER"
              ]
            },
            {
              "action": "REJECT_UPDATE",
              "nextState": "UPDATE_REJECTED",
              "roles": [
                "SDIP_ADMINISTRATOR",
                "REGISTRY_MANAGER"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "UPDATE_APPROVED",
          "applicationStatus": "PROFILE_UPDATED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        },
        {
          "sla": null,
          "state": "UPDATE_REJECTED",
          "applicationStatus": "UPDATE_REJECTED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        }
      ]
    }
  ]
}'
```

### A.3 Key Rotation Workflow Setup

```bash
curl --location 'http://localhost:8081/egov-workflow-v2/egov-wf/businessservice/_create' \
--header 'Content-Type: application/json' \
--data '{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "action": "",
    "did": 3,
    "key": "",
    "msgId": "20240115131100|en_IN",
    "requesterId": "",
    "ts": 1705311120000,
    "ver": "1.0.0",
    "authToken": "participant-registry-auth-token",
    "userInfo": {
      "uuid": "sdip-admin-uuid"
    }
  },
  "BusinessServices": [
    {
      "tenantId": "pb",
      "businessService": "participant.key.rotation",
      "business": "participant-registry-service",
      "businessServiceSla": 86400000,
      "states": [
        {
          "sla": null,
          "state": null,
          "applicationStatus": null,
          "docUploadRequired": false,
          "isStartState": true,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "REQUEST_KEY_ROTATION",
              "nextState": "KEY_ROTATION_REQUESTED",
              "roles": [
                "PARTICIPANT_ADMIN",
                "DEPARTMENT_TECHNICAL_LEAD"
              ]
            }
          ]
        },
        {
          "sla": 43200000,
          "state": "KEY_ROTATION_REQUESTED",
          "applicationStatus": "KEY_ROTATION_PENDING",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": false,
          "isStateUpdatable": true,
          "actions": [
            {
              "action": "APPROVE_KEY_ROTATION",
              "nextState": "KEY_ROTATION_APPROVED",
              "roles": [
                "SDIP_SECURITY_ADMIN",
                "SDIP_ADMINISTRATOR"
              ]
            },
            {
              "action": "REJECT_KEY_ROTATION",
              "nextState": "KEY_ROTATION_REJECTED",
              "roles": [
                "SDIP_SECURITY_ADMIN",
                "SDIP_ADMINISTRATOR"
              ]
            }
          ]
        },
        {
          "sla": null,
          "state": "KEY_ROTATION_APPROVED",
          "applicationStatus": "KEYS_ROTATED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        },
        {
          "sla": null,
          "state": "KEY_ROTATION_REJECTED",
          "applicationStatus": "KEY_ROTATION_REJECTED",
          "docUploadRequired": false,
          "isStartState": false,
          "isTerminateState": true,
          "isStateUpdatable": false,
          "actions": []
        }
      ]
    }
  ]
}'
```

## 12. Appendix B: DIGIT IDGen Configuration

### B.1 ID Format Configuration Setup

```bash
# Configure Participant ID formats in DIGIT IDGen Service
curl --location 'http://localhost:8080/egov-idgen/id/_generate' \
--header 'Content-Type: application/json' \
--data '{
  "RequestInfo": {
    "apiId": "participant-registry-service",
    "ver": "1.0.0",
    "ts": 1705311180000,
    "action": "CREATE",
    "authToken": "participant-registry-auth-token"
  },
  "idRequests": [
    {
      "tenantId": "pb",
      "idName": "participant.government.dept",
      "format": "DEPT-PB-[COUNTER:6]-[CHECKSUM]"
    },
    {
      "tenantId": "pb", 
      "idName": "participant.local.body",
      "format": "LB-[DISTRICT:3]-[COUNTER:6]-[CHECKSUM]"
    },
    {
      "tenantId": "pb",
      "idName": "participant.authorized.agency", 
      "format": "AUTH-[TYPE:4]-[COUNTER:6]-[CHECKSUM]"
    },
    {
      "tenantId": "pb",
      "idName": "registration.application",
      "format": "REG-[YYYY]-[MM]-[COUNTER:6]"
    }
  ]
}'
```

## 13. API Specification

### 13.1 Participant Registry Service API Specification

The Participant Registry Service provides RESTful APIs for managing participant registration, authentication, and administrative operations. The complete API specification is available in a separate file for better organization and maintainability.

**API Specification File**: [`participant_registry_api_specification.yaml`](./participant_registry_api_specification.yaml)

#### 13.1.1 API Overview

The API specification includes:

**Authentication & Self-Registration APIs:**
- `POST /auth/self-register` - Department admin self-registration
- `POST /auth/login` - Login with received credentials
- `POST /auth/change-password` - Change temporary password

**Participant Registration APIs:**
- `POST /participants/register` - Submit registration application
- `POST /participants/applications/_search` - Search applications
- `GET /participants/applications/{id}` - Get application details
- `PUT /participants/applications/{id}` - Update/resubmit application

**Administrative APIs (SDIP Admin Portal):**
- `POST /admin/applications/_search` - Admin search pending applications
- `POST /admin/applications/{id}/_approve` - Approve/reject applications
- `POST /admin/applications/{id}/assign` - Assign to reviewers
- `GET /admin/dashboard/metrics` - Dashboard metrics

**Participant Management APIs:**
- `POST /participants/_search` - Search active participants
- `GET /participants/{id}` - Get participant details
- `PUT /participants/{id}` - Update participant profile
- `POST /participants/{id}/keys/_rotate` - Request key rotation
- `PUT /participants/{id}/status` - Update participant status

**Document Management APIs:**
- `POST /documents/upload` - Upload supporting documents
- `GET /documents/{id}` - Download documents
- `DELETE /documents/{id}` - Delete documents

**Verification and Lookup APIs:**
- `GET /verify/participant/{id}` - Verify participant status
- `GET /lookup/participants` - Lookup participants by criteria
- `GET /health` - Service health check

#### 13.1.2 Two-Phase Registration Flow

The API supports the two-phase registration process described in the requirements:

1. **Phase 1 - Self-Registration**: Department admin uses `/auth/self-register` to create account and receive login credentials via email
2. **Phase 2 - Participant Application**: Admin logs in and submits complete application via `/participants/register`
3. **Admin Review**: SDIP admin reviews and approves via `/admin/applications/{id}/_approve`

#### 13.1.3 API Standards and Authentication

- **OpenAPI 3.0 Specification**: Complete specification with schemas, examples, and error handling
- **Hybrid Authentication Architecture**: 
  - External APIs accept Keycloak JWT access tokens
  - Internal DIGIT service calls use DIGIT RequestInfo format
  - User context extracted from JWT and mapped to DIGIT UserInfo
- **API Response Format**: Standard REST API responses (not DIGIT format)
- **Error Handling**: Comprehensive error responses with validation details
- **Documentation**: Detailed descriptions, examples, and usage guidelines

#### 13.1.4 Authentication Flow

**External API Authentication:**
1. Department admin receives Keycloak JWT token after login
2. JWT token contains user claims (sub, preferred_username, email, name, roles)
3. Token passed as Authorization: Bearer header to Participant Registry APIs

**Internal DIGIT Service Integration:**
1. Service extracts user context from JWT token claims
2. Constructs DIGIT RequestInfo format with extracted user information
3. Calls DIGIT services (Workflow, IDGen) using DIGIT patterns
4. Maps JWT roles to DIGIT role format for internal operations

**JWT Token Mapping:**
- `sub` claim → `userInfo.uuid`
- `preferred_username` claim → `userInfo.userName` 
- `email` claim → `userInfo.emailId`
- `name` claim → `userInfo.name`
- `realm_access.roles` → `userInfo.roles` (mapped to DIGIT format)

This hybrid approach provides a clean REST API interface while maintaining full compatibility with the DIGIT platform's internal service architecture.

Please refer to the complete API specification file for detailed endpoint definitions, request/response schemas, and implementation examples.

## 13. Appendix C: DIGIT Workflow Transition Commands

### C.1 Workflow Transition API Usage

The following commands demonstrate how to transition the participant registry workflow between different states:

#### C.1.1 Submit Registration Application
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115130900|en_IN",
        "requesterId": "",
        "ts": 1705311000000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 101,
            "uuid": "dept-user-uuid-12345",
            "userName": "DEPT_USER",
            "name": "Department User",
            "mobileNumber": "9876543210",
            "emailId": "dept.user@punjab.gov.in",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Participant Applicant",
                    "code": "PARTICIPANT_APPLICANT",
                    "tenantId": "pb"
                },
                {
                    "name": "Department User",
                    "code": "DEPARTMENT_USER",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000001",
            "action": "SUBMIT",
            "comment": "New participant registration submitted for Department of Social Welfare",
            "assignee": null,
            "previousStatus": null
        }
    ]
}'
```

#### C.1.2 Forward to Technical Review
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115131000|en_IN",
        "requesterId": "",
        "ts": 1705311060000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 201,
            "uuid": "sdip-operator-uuid-67890",
            "userName": "SDIP_OP",
            "name": "SDIP Operator",
            "mobileNumber": "9876543211",
            "emailId": "sdip.operator@punjab.gov.in",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "SDIP Operator",
                    "code": "SDIP_OPERATOR",
                    "tenantId": "pb"
                },
                {
                    "name": "Registry Clerk",
                    "code": "REGISTRY_CLERK",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000001",
            "action": "FORWARD_TO_TECHNICAL_REVIEW",
            "comment": "Initial validation completed. Forwarding for technical assessment.",
            "assignee": "technical-reviewer-uuid-11111",
            "previousStatus": "SUBMITTED"
        }
    ]
}'
```

#### C.1.3 Technical Approval
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115132000|en_IN",
        "requesterId": "",
        "ts": 1705311120000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 301,
            "uuid": "technical-reviewer-uuid-11111",
            "userName": "TECH_REVIEWER",
            "name": "Technical Reviewer",
            "mobileNumber": "9876543212",
            "emailId": "tech.reviewer@punjab.gov.in",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Technical Reviewer",
                    "code": "TECHNICAL_REVIEWER",
                    "tenantId": "pb"
                },
                {
                    "name": "SDIP Technical Lead",
                    "code": "SDIP_TECHNICAL_LEAD",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000001",
            "action": "TECHNICAL_APPROVE",
            "comment": "Technical review completed successfully. API endpoints validated, security requirements met.",
            "assignee": "sdip-admin-uuid-22222",
            "previousStatus": "TECHNICAL_REVIEW"
        }
    ]
}'
```

#### C.1.4 Final Administrative Approval
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115133000|en_IN",
        "requesterId": "",
        "ts": 1705311180000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 401,
            "uuid": "sdip-admin-uuid-22222",
            "userName": "SDIP_ADMIN",
            "name": "SDIP Administrator",
            "mobileNumber": "9876543213",
            "emailId": "sdip.admin@punjab.gov.in",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "SDIP Administrator",
                    "code": "SDIP_ADMINISTRATOR",
                    "tenantId": "pb"
                },
                {
                    "name": "SDIP Admin",
                    "code": "SDIP_ADMIN",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000001",
            "action": "APPROVE",
            "comment": "Registration approved. Department meets all requirements for SDIP participation.",
            "assignee": null,
            "previousStatus": "ADMIN_APPROVAL"
        }
    ]
}'
```

#### C.1.5 System Activation
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115134000|en_IN",
        "requesterId": "",
        "ts": 1705311240000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 999,
            "uuid": "system-user-uuid-99999",
            "userName": "SYSTEM",
            "name": "System User",
            "type": "SYSTEM",
            "roles": [
                {
                    "name": "System User",
                    "code": "SYSTEM_USER",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000001",
            "action": "ACTIVATE",
            "comment": "Participant activated. Participant ID: DEPT-PB-000001-A4B2, API credentials generated.",
            "assignee": null,
            "previousStatus": "APPROVED"
        }
    ]
}'
```

#### C.1.6 Rejection Scenarios

**Reject at Initial Review:**
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115135000|en_IN",
        "requesterId": "",
        "ts": 1705311300000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 201,
            "uuid": "sdip-operator-uuid-67890",
            "userName": "SDIP_OP",
            "name": "SDIP Operator",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "SDIP Operator",
                    "code": "SDIP_OPERATOR",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000002",
            "action": "REJECT",
            "comment": "Incomplete documentation. Missing authorization letter and technical specifications.",
            "assignee": null,
            "previousStatus": "SUBMITTED"
        }
    ]
}'
```

**Send Back for Clarification:**
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115136000|en_IN",
        "requesterId": "",
        "ts": 1705311360000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 301,
            "uuid": "technical-reviewer-uuid-11111",
            "userName": "TECH_REVIEWER",
            "name": "Technical Reviewer",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Technical Reviewer",
                    "code": "TECHNICAL_REVIEWER",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.registration",
            "businessId": "REG-2024-01-000003",
            "action": "SEND_BACK_FOR_CLARIFICATION",
            "comment": "Please provide additional details about data security measures and API rate limiting requirements.",
            "assignee": "dept-user-uuid-12345",
            "previousStatus": "TECHNICAL_REVIEW"
        }
    ]
}'
```

### C.2 Profile Update Workflow Transitions

#### C.2.1 Submit Profile Update
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115140000|en_IN",
        "requesterId": "",
        "ts": 1705311600000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 501,
            "uuid": "participant-user-uuid-33333",
            "userName": "PARTICIPANT_USER",
            "name": "Participant User",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Participant User",
                    "code": "PARTICIPANT_USER",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.profile.update",
            "businessId": "PU-2024-01-000001",
            "action": "SUBMIT_UPDATE",
            "comment": "Updating contact information and adding new API endpoints",
            "assignee": "registry-manager-uuid-44444",
            "previousStatus": null
        }
    ]
}'
```

#### C.2.2 Approve Profile Update
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115141000|en_IN",
        "requesterId": "",
        "ts": 1705311660000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 601,
            "uuid": "registry-manager-uuid-44444",
            "userName": "REGISTRY_MGR",
            "name": "Registry Manager",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Registry Manager",
                    "code": "REGISTRY_MANAGER",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.profile.update",
            "businessId": "PU-2024-01-000001",
            "action": "APPROVE_UPDATE",
            "comment": "Profile update approved. Changes are now active.",
            "assignee": null,
            "previousStatus": "UPDATE_SUBMITTED"
        }
    ]
}'
```

### C.3 Key Rotation Workflow Transitions

#### C.3.1 Request Key Rotation
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115142000|en_IN",
        "requesterId": "",
        "ts": 1705311720000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 701,
            "uuid": "dept-tech-lead-uuid-55555",
            "userName": "DEPT_TECH_LEAD",
            "name": "Department Technical Lead",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "Department Technical Lead",
                    "code": "DEPARTMENT_TECHNICAL_LEAD",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.key.rotation",
            "businessId": "KR-2024-01-000001",
            "action": "REQUEST_KEY_ROTATION",
            "comment": "Key rotation requested due to security policy compliance",
            "assignee": "security-admin-uuid-66666",
            "previousStatus": null
        }
    ]
}'
```

#### C.3.2 Approve Key Rotation
```bash
curl --location 'http://localhost:8096/egov-workflow-v2/egov-wf/process/_transition' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "action": "",
        "did": 1,
        "key": "",
        "msgId": "20240115143000|en_IN",
        "requesterId": "",
        "ts": 1705311780000,
        "ver": ".01",
        "authToken": "participant-registry-auth-token",
        "userInfo": {
            "id": 801,
            "uuid": "security-admin-uuid-66666",
            "userName": "SECURITY_ADMIN",
            "name": "Security Administrator",
            "type": "EMPLOYEE",
            "roles": [
                {
                    "name": "SDIP Security Admin",
                    "code": "SDIP_SECURITY_ADMIN",
                    "tenantId": "pb"
                }
            ],
            "active": true,
            "tenantId": "pb"
        }
    },
    "ProcessInstances": [
        {
            "moduleName": "participant-registry-service",
            "tenantId": "pb",
            "businessService": "participant.key.rotation",
            "businessId": "KR-2024-01-000001",
            "action": "APPROVE_KEY_ROTATION",
            "comment": "Key rotation approved. New keys will be generated and distributed.",
            "assignee": null,
            "previousStatus": "KEY_ROTATION_REQUESTED"
        }
    ]
}'
```

### C.4 Integration with Participant Registry Service

The Participant Registry Service should listen for workflow state changes and trigger appropriate actions:

```java
@EventListener
public void handleWorkflowTransition(WorkflowTransitionEvent event) {
    String businessService = event.getBusinessService();
    String newState = event.getNewState();
    String businessId = event.getBusinessId();
    
    switch (businessService) {
        case "participant.registration":
            handleRegistrationTransition(businessId, newState, event.getComment());
            break;
        case "participant.profile.update":
            handleProfileUpdateTransition(businessId, newState, event.getComment());
            break;
        case "participant.key.rotation":
            handleKeyRotationTransition(businessId, newState, event.getComment());
            break;
    }
}

private void handleRegistrationTransition(String applicationId, String newState, String comment) {
    switch (newState) {
        case "APPROVED":
            // Generate participant ID, create keys, activate participant
            participantActivationService.activateParticipant(applicationId);
            break;
        case "REJECTED":
            // Update application status, send rejection notification
            registrationService.rejectApplication(applicationId, comment);
            break;
        case "CLARIFICATION_REQUIRED":
            // Send clarification request to applicant
            notificationService.sendClarificationRequest(applicationId, comment);
            break;
        case "ACTIVE":
            // Complete activation, send credentials, update other services
            participantActivationService.completeActivation(applicationId);
            break;
    }
}
```