-- Add persister pattern fields to existing tables

-- Add to eg_bank_account table
ALTER TABLE eg_bank_account ADD COLUMN row_version INTEGER DEFAULT 1;
ALTER TABLE eg_bank_account ADD COLUMN is_deleted BOOLEAN DEFAULT false;

-- Add to eg_bank_account_detail table  
ALTER TABLE eg_bank_account_detail ADD COLUMN row_version INTEGER DEFAULT 1;
ALTER TABLE eg_bank_account_detail ADD COLUMN is_deleted BOOLEAN DEFAULT false;

-- Add to eg_bank_branch_identifier table
ALTER TABLE eg_bank_branch_identifier ADD COLUMN row_version INTEGER DEFAULT 1;
ALTER TABLE eg_bank_branch_identifier ADD COLUMN is_deleted BOOLEAN DEFAULT false;

-- Add to eg_bank_accounts_doc table
ALTER TABLE eg_bank_accounts_doc ADD COLUMN row_version INTEGER DEFAULT 1;
ALTER TABLE eg_bank_accounts_doc ADD COLUMN is_deleted BOOLEAN DEFAULT false;

-- Create indexes for new fields
CREATE INDEX idx_bank_account_is_deleted ON eg_bank_account (is_deleted);
CREATE INDEX idx_bank_account_detail_is_deleted ON eg_bank_account_detail (is_deleted);
CREATE INDEX idx_bank_branch_identifier_is_deleted ON eg_bank_branch_identifier (is_deleted);
CREATE INDEX idx_bank_accounts_doc_is_deleted ON eg_bank_accounts_doc (is_deleted);