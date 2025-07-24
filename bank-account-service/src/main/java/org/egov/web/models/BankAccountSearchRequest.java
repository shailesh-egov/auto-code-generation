package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.web.models.Pagination;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountSearchRequest {

    @JsonProperty("requestInfo")
    @Valid
    private RequestInfo requestInfo;

    @JsonProperty("bankAccountDetails")
    @Valid
    private BankAccountSearchCriteria bankAccountDetails;

    @JsonProperty("pagination")
    @Valid
    private Pagination pagination;
}