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
     * NOTE: All CRUD operations are now handled by the DIGIT persister service via Kafka.
     * This repository only handles search operations as per DIGIT persister pattern.
     */

    /**
     * Searches bank accounts based on criteria
     */
    public List<BankAccount> getBankAccounts(BankAccountSearchCriteria criteria) {
        log.info("Searching bank accounts for tenant: {}", criteria.getTenantId());
        
        String query = queryBuilder.getBankAccountSearchQuery(criteria, new ArrayList<>());
        List<Object> preparedStatementValues = new ArrayList<>();
        query = queryBuilder.getBankAccountSearchQuery(criteria, preparedStatementValues);
        
        log.debug("Executing search query: {} with params: {}", query, preparedStatementValues);
        
        List<BankAccount> bankAccounts = jdbcTemplate.query(query, 
                preparedStatementValues.toArray(), rowMapper);
        
        log.info("Found {} bank accounts matching search criteria", bankAccounts.size());
        return bankAccounts;
    }
}