-- Create certificate table
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
    created_time                 bigint NOT NULL,
    last_modified_time           bigint NOT NULL
);

-- Create certificate proof table
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

-- Create indexes for performance optimization
CREATE INDEX idx_certificate_tenant_id ON eg_certificate (tenant_id);
CREATE INDEX idx_certificate_issuer_id ON eg_certificate (issuer_id);
CREATE INDEX idx_certificate_subject_id ON eg_certificate (subject_id);
CREATE INDEX idx_certificate_type ON eg_certificate (certificate_type);
CREATE INDEX idx_certificate_status ON eg_certificate (status);
CREATE INDEX idx_certificate_issued_at ON eg_certificate (issued_at);
CREATE INDEX idx_certificate_expires_at ON eg_certificate (expires_at);

-- Composite indexes for common query patterns
CREATE INDEX idx_certificate_tenant_issuer ON eg_certificate (tenant_id, issuer_id);
CREATE INDEX idx_certificate_tenant_subject ON eg_certificate (tenant_id, subject_id);
CREATE INDEX idx_certificate_tenant_type ON eg_certificate (tenant_id, certificate_type);
CREATE INDEX idx_certificate_tenant_status ON eg_certificate (tenant_id, status);
CREATE INDEX idx_certificate_tenant_issued ON eg_certificate (tenant_id, issued_at);

-- Index for proof table
CREATE INDEX idx_certificate_proof_certificate_id ON eg_certificate_proof (certificate_id);
CREATE INDEX idx_certificate_proof_type ON eg_certificate_proof (proof_type);