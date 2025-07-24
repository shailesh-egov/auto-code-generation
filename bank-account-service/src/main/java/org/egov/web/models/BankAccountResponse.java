package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.web.models.Pagination;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountResponse {

    @JsonProperty("responseInfo")
    @Valid
    private ResponseInfo responseInfo;

    @JsonProperty("bankAccounts")
    @Valid
    private List<BankAccount> bankAccounts;

    @JsonProperty("pagination")
    @Valid
    private Pagination pagination;
}