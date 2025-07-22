package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountRequest {

    @JsonProperty("requestInfo")
    @Valid
    private RequestInfo requestInfo;

    @JsonProperty("bankAccounts")
    @Valid
    private List<BankAccount> bankAccounts;
}