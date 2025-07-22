package org.egov.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.BankAccountService;
import org.egov.util.ResponseInfoCreator;
import org.egov.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-07-22T13:05:25.880+05:30")
@Controller
@RequestMapping("/bankaccount/v1")
public class BankAccountApiController {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final ResponseInfoCreator responseInfoCreator;
    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountApiController(ObjectMapper objectMapper, 
                                   HttpServletRequest request,
                                   ResponseInfoCreator responseInfoCreator, 
                                   BankAccountService bankAccountService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.responseInfoCreator = responseInfoCreator;
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<BankAccountResponse> createBankAccount(
            @ApiParam(value = "Request object to create bank account in the system", required = true) 
            @Valid @RequestBody BankAccountRequest body, 
            @ApiParam(value = "", allowableValues = "application/json") 
            @RequestHeader(value = "Content-Type", required = false) String contentType) {
        
        List<BankAccount> bankAccounts = bankAccountService.createBankAccount(body);
        ResponseInfo responseInfo = responseInfoCreator.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        BankAccountResponse response = BankAccountResponse.builder()
                .responseInfo(responseInfo)
                .bankAccounts(bankAccounts)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<BankAccountResponse> searchBankAccounts(
            @Valid @ModelAttribute BankAccountSearchCriteria searchCriteria, 
            @Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
        
        List<BankAccount> bankAccounts = bankAccountService.searchBankAccount(requestInfoWrapper, searchCriteria);
        ResponseInfo responseInfo = responseInfoCreator.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        
        BankAccountResponse response = BankAccountResponse.builder()
                .responseInfo(responseInfo)
                .bankAccounts(bankAccounts)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_update", method = RequestMethod.POST)
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @ApiParam(value = "Request object to update bank account in the system", required = true) 
            @Valid @RequestBody BankAccountRequest body, 
            @ApiParam(value = "", allowableValues = "application/json") 
            @RequestHeader(value = "Content-Type", required = false) String contentType) {
        
        List<BankAccount> bankAccounts = bankAccountService.updateBankAccount(body);
        ResponseInfo responseInfo = responseInfoCreator.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
        BankAccountResponse response = BankAccountResponse.builder()
                .responseInfo(responseInfo)
                .bankAccounts(bankAccounts)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}