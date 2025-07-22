package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDetails {

    @JsonProperty("id")
    @Size(min = 2, max = 64)
    private String id;

    @JsonProperty("tenantId")
    @NotNull
    @Size(min = 2, max = 64)
    private String tenantId;

    @JsonProperty("accountHolderName")
    @Size(min = 2, max = 64)
    private String accountHolderName;

    @JsonProperty("accountNumber")
    @NotNull
    @Size(min = 2, max = 64)
    private String accountNumber;

    @JsonProperty("accountType")
    @Size(min = 2, max = 64)
    private String accountType;

    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("bankBranchIdentifier")
    @NotNull
    @Valid
    private BankBranchIdentifier bankBranchIdentifier;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents;

    @JsonProperty("additionalFields")
    private Object additionalFields;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Integer rowVersion;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;
}