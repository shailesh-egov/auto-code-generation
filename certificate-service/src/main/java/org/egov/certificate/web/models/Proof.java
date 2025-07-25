package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proof {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    @NotBlank
    private String type;

    @JsonProperty("created")
    @NotNull
    private Long created;

    @JsonProperty("proofPurpose")
    @NotBlank
    private String proofPurpose;

    @JsonProperty("verificationMethod")
    @NotBlank
    private String verificationMethod;

    @JsonProperty("signatureValue")
    @NotBlank
    private String signatureValue;

    @JsonProperty("additionalDetails")
    private Map<String, Object> additionalDetails;
}