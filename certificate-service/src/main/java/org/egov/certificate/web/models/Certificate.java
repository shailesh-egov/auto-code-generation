package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @JsonProperty("id")
    @Size(max = 256)
    private String id;

    @JsonProperty("tenantId")
    @NotBlank
    @Size(min = 2, max = 128)
    private String tenantId;

    @JsonProperty("context")
    @NotBlank
    @Size(max = 512)
    private String context;

    @JsonProperty("type")
    @NotBlank
    @Size(min = 1)
    private String type;

    @JsonProperty("issuer")
    @NotNull
    @Valid
    private Issuer issuer;

    @JsonProperty("issued")
    private Long issued;

    @JsonProperty("expirationDate")
    private Long expirationDate;

    @JsonProperty("credentialSubject")
    @NotNull
    private Map<String, Object> credentialSubject;

    @JsonProperty("proof")
    @Valid
    private Proof proof;

    @JsonProperty("status")
    @Builder.Default
    private String status = "ACTIVE";

    @JsonProperty("additionalDetails")
    private Map<String, Object> additionalDetails;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails;
}