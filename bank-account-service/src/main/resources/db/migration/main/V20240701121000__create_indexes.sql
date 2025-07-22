-- Create indexes for bank account service tables

-- Indexes on eg_bank_account table
CREATE INDEX idx_bank_account_tenant_id ON eg_bank_account (tenant_id);
CREATE INDEX idx_bank_account_service_code ON eg_bank_account (service_code);
CREATE INDEX idx_bank_account_reference_id ON eg_bank_account (reference_id);
CREATE INDEX idx_bank_account_tenant_service_ref ON eg_bank_account (tenant_id, service_code, reference_id);

-- Indexes on eg_bank_account_detail table
CREATE INDEX idx_bank_account_detail_tenant_id ON eg_bank_account_detail (tenant_id);
CREATE INDEX idx_bank_account_detail_bank_account_id ON eg_bank_account_detail (bank_account_id);
CREATE INDEX idx_bank_account_detail_account_number ON eg_bank_account_detail (account_number);
CREATE INDEX idx_bank_account_detail_account_holder_name ON eg_bank_account_detail (account_holder_name);
CREATE INDEX idx_bank_account_detail_is_primary ON eg_bank_account_detail (is_primary);
CREATE INDEX idx_bank_account_detail_is_active ON eg_bank_account_detail (is_active);

-- Indexes on eg_bank_branch_identifier table
CREATE INDEX idx_bank_branch_identifier_detail_id ON eg_bank_branch_identifier (bank_account_detail_id);
CREATE INDEX idx_bank_branch_identifier_type ON eg_bank_branch_identifier (type);
CREATE INDEX idx_bank_branch_identifier_code ON eg_bank_branch_identifier (code);
CREATE INDEX idx_bank_branch_identifier_type_code ON eg_bank_branch_identifier (type, code);

-- Indexes on eg_bank_accounts_doc table
CREATE INDEX idx_bank_accounts_doc_detail_id ON eg_bank_accounts_doc (bank_account_detail_id);
CREATE INDEX idx_bank_accounts_doc_type ON eg_bank_accounts_doc (document_type);
CREATE INDEX idx_bank_accounts_doc_uid ON eg_bank_accounts_doc (document_uid);