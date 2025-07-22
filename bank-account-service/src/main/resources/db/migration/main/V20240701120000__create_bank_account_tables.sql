-- Bank Account Service Tables

-- Main bank account table
CREATE TABLE eg_bank_account(
    id                           VARCHAR(256) PRIMARY KEY,
    tenant_id                    VARCHAR(64) NOT NULL,
    service_code                 VARCHAR(128) NOT NULL,
    reference_id                 VARCHAR(256) NOT NULL,
    additional_details           JSONB,
    created_by                   VARCHAR(256) NOT NULL,
    last_modified_by             VARCHAR(256),
    created_time                 BIGINT,
    last_modified_time           BIGINT
);

-- Bank account detail table
CREATE TABLE eg_bank_account_detail(
    id                           VARCHAR(256) PRIMARY KEY,
    tenant_id                    VARCHAR(64) NOT NULL,
    bank_account_id              VARCHAR(256) NOT NULL,
    account_holder_name          VARCHAR(256),
    account_number               VARCHAR(256) NOT NULL,
    account_type                 VARCHAR(140) NOT NULL,
    is_primary                   BOOLEAN,
    is_active                    BOOLEAN,
    additional_details           JSONB,
    created_by                   VARCHAR(256) NOT NULL,
    last_modified_by             VARCHAR(256),
    created_time                 BIGINT,
    last_modified_time           BIGINT,
    FOREIGN KEY (bank_account_id) REFERENCES eg_bank_account (id)
);

-- Bank branch identifier table
CREATE TABLE eg_bank_branch_identifier(
    id                           VARCHAR(256) PRIMARY KEY,
    bank_account_detail_id       VARCHAR(64) NOT NULL,
    type                         VARCHAR(140),
    code                         VARCHAR(140),
    additional_details           JSONB,
    FOREIGN KEY (bank_account_detail_id) REFERENCES eg_bank_account_detail (id)
);

-- Bank accounts document table
CREATE TABLE eg_bank_accounts_doc(
    id                           VARCHAR(256) PRIMARY KEY,
    bank_account_detail_id       VARCHAR(64) NOT NULL,
    document_type                VARCHAR(256),
    file_store                   VARCHAR(256),
    document_uid                 VARCHAR(256),
    additional_details           JSONB,
    FOREIGN KEY (bank_account_detail_id) REFERENCES eg_bank_account_detail (id)
);