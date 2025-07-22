package org.egov.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.repository.rowmapper.BankAccountRowMapper;
import org.egov.web.models.BankAccount;
import org.egov.web.models.BankAccountSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class BankAccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BankAccountQueryBuilder queryBuilder;
    private final BankAccountRowMapper rowMapper;

    @Autowired
    public BankAccountRepository(JdbcTemplate jdbcTemplate,
                                BankAccountQueryBuilder queryBuilder,
                                BankAccountRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    /**
     * Saves bank accounts to database
     */
    public void save(List<BankAccount> bankAccounts) {
        log.info("Saving {} bank accounts to database", bankAccounts.size());
        
        for (BankAccount bankAccount : bankAccounts) {
            // Insert main bank account
            String insertBankAccountQuery = queryBuilder.getInsertBankAccountQuery();
            List<Object> bankAccountParams = queryBuilder.getBankAccountInsertParams(bankAccount);
            
            jdbcTemplate.update(insertBankAccountQuery, bankAccountParams.toArray());
            
            // Insert bank account details
            if (bankAccount.getBankAccountDetails() != null) {
                for (var detail : bankAccount.getBankAccountDetails()) {
                    String insertDetailQuery = queryBuilder.getInsertBankAccountDetailQuery();
                    List<Object> detailParams = queryBuilder.getBankAccountDetailInsertParams(detail, bankAccount.getId());
                    
                    jdbcTemplate.update(insertDetailQuery, detailParams.toArray());
                    
                    // Insert bank branch identifier
                    if (detail.getBankBranchIdentifier() != null) {
                        String insertBranchQuery = queryBuilder.getInsertBankBranchIdentifierQuery();
                        List<Object> branchParams = queryBuilder.getBankBranchIdentifierInsertParams(
                                detail.getBankBranchIdentifier(), detail.getId());
                        
                        jdbcTemplate.update(insertBranchQuery, branchParams.toArray());
                    }
                    
                    // Insert documents
                    if (detail.getDocuments() != null) {
                        for (var document : detail.getDocuments()) {
                            String insertDocQuery = queryBuilder.getInsertBankAccountDocumentQuery();
                            List<Object> docParams = queryBuilder.getBankAccountDocumentInsertParams(document, detail.getId());
                            
                            jdbcTemplate.update(insertDocQuery, docParams.toArray());
                        }
                    }
                }
            }
        }
        
        log.info("Successfully saved bank accounts to database");
    }

    /**
     * Updates bank accounts in database
     */
    public void update(List<BankAccount> bankAccounts) {
        log.info("Updating {} bank accounts in database", bankAccounts.size());
        
        for (BankAccount bankAccount : bankAccounts) {
            // Update main bank account
            String updateBankAccountQuery = queryBuilder.getUpdateBankAccountQuery();
            List<Object> bankAccountParams = queryBuilder.getBankAccountUpdateParams(bankAccount);
            
            jdbcTemplate.update(updateBankAccountQuery, bankAccountParams.toArray());
            
            // Update bank account details
            if (bankAccount.getBankAccountDetails() != null) {
                for (var detail : bankAccount.getBankAccountDetails()) {
                    String updateDetailQuery = queryBuilder.getUpdateBankAccountDetailQuery();
                    List<Object> detailParams = queryBuilder.getBankAccountDetailUpdateParams(detail);
                    
                    jdbcTemplate.update(updateDetailQuery, detailParams.toArray());
                    
                    // Update bank branch identifier
                    if (detail.getBankBranchIdentifier() != null) {
                        String updateBranchQuery = queryBuilder.getUpdateBankBranchIdentifierQuery();
                        List<Object> branchParams = queryBuilder.getBankBranchIdentifierUpdateParams(
                                detail.getBankBranchIdentifier(), detail.getId());
                        
                        jdbcTemplate.update(updateBranchQuery, branchParams.toArray());
                    }
                }
            }
        }
        
        log.info("Successfully updated bank accounts in database");
    }

    /**
     * Searches bank accounts based on criteria
     */
    public List<BankAccount> getBankAccounts(BankAccountSearchCriteria criteria) {
        log.info("Searching bank accounts for tenant: {}", criteria.getTenantId());
        
        String query = queryBuilder.getBankAccountSearchQuery(criteria, new ArrayList<>());
        List<Object> preparedStatementValues = new ArrayList<>();
        query = queryBuilder.getBankAccountSearchQuery(criteria, preparedStatementValues);
        
        log.debug("Executing query: {} with params: {}", query, preparedStatementValues);
        
        List<BankAccount> bankAccounts = jdbcTemplate.query(query, 
                preparedStatementValues.toArray(), rowMapper);
        
        log.info("Found {} bank accounts matching criteria", bankAccounts.size());
        return bankAccounts;
    }
}