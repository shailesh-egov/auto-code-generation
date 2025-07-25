package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCriteria {

    @JsonProperty("tenantId")
    @NotBlank
    @Size(max = 128)
    private String tenantId;

    @JsonProperty("certificateId")
    @NotBlank
    @Size(max = 256)
    private String certificateId;

    @JsonProperty("includeProofValidation")
    @Builder.Default
    private Boolean includeProofValidation = true;

    @JsonProperty("includeExpiryCheck")
    @Builder.Default
    private Boolean includeExpiryCheck = true;

    @JsonProperty("includeRevocationCheck")
    @Builder.Default
    private Boolean includeRevocationCheck = true;

    @JsonProperty("additionalValidations")
    private Map<String, Object> additionalValidations;
}