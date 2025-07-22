package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountSearchCriteria {

    @JsonProperty("tenantId")
    @NotNull
    @Size(min = 2, max = 64)
    private String tenantId;

    @JsonProperty("ids")
    private List<String> ids;

    @JsonProperty("serviceCode")
    @Size(min = 2, max = 64)
    private String serviceCode;

    @JsonProperty("referenceId")
    private List<String> referenceId;

    @JsonProperty("accountHolderName")
    @Size(min = 2, max = 64)
    private String accountHolderName;

    @JsonProperty("accountNumber")
    private List<String> accountNumber;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("bankBranchIdentifierCode")
    private BankBranchIdentifier bankBranchIdentifierCode;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;
}