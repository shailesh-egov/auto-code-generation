package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResult {

    @JsonProperty("certificateId")
    private String certificateId;

    @JsonProperty("isValid")
    private Boolean isValid;

    @JsonProperty("signatureValid")
    private Boolean signatureValid;

    @JsonProperty("notExpired")
    private Boolean notExpired;

    @JsonProperty("notRevoked")
    private Boolean notRevoked;

    @JsonProperty("issuerVerified")
    private Boolean issuerVerified;

    @JsonProperty("verificationTimestamp")
    private Long verificationTimestamp;

    @JsonProperty("verificationDetails")
    private Map<String, Object> verificationDetails;
}