package org.egov.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.service.BankAccountService;
import org.egov.util.ResponseInfoCreator;
import org.egov.web.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankAccountApiControllerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseInfoCreator responseInfoCreator;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountApiController controller;

    private BankAccountRequest bankAccountRequest;
    private List<BankAccount> bankAccounts;

    @BeforeEach
    public void setUp() {
        bankAccounts = new ArrayList<>();
        BankAccount bankAccount = BankAccount.builder()
                .id("test-id")
                .tenantId("pb.amritsar")
                .serviceCode("IND")
                .referenceId("ref-id-123")
                .build();
        bankAccounts.add(bankAccount);

        bankAccountRequest = BankAccountRequest.builder()
                .bankAccounts(bankAccounts)
                .build();
    }

    @Test
    public void testCreateBankAccount() {
        // Mock service response
        when(bankAccountService.createBankAccount(any(BankAccountRequest.class)))
                .thenReturn(bankAccounts);

        // Mock response info creator
        when(responseInfoCreator.createResponseInfoFromRequestInfo(any(), any()))
                .thenReturn(new org.egov.common.contract.response.ResponseInfo());

        // Call the controller method
        ResponseEntity<BankAccountResponse> response = controller.createBankAccount(bankAccountRequest, "application/json");

        // Assertions
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(bankAccounts, response.getBody().getBankAccounts());
    }

    @Test
    public void testUpdateBankAccount() {
        // Mock service response
        when(bankAccountService.updateBankAccount(any(BankAccountRequest.class)))
                .thenReturn(bankAccounts);

        // Mock response info creator
        when(responseInfoCreator.createResponseInfoFromRequestInfo(any(), any()))
                .thenReturn(new org.egov.common.contract.response.ResponseInfo());

        // Call the controller method
        ResponseEntity<BankAccountResponse> response = controller.updateBankAccount(bankAccountRequest, "application/json");

        // Assertions
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(bankAccounts, response.getBody().getBankAccounts());
    }
}