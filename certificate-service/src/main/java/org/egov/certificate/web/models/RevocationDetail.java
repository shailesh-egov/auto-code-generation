package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevocationDetail {

    @JsonProperty("certificateId")
    @NotBlank
    @Size(max = 256)
    private String certificateId;

    @JsonProperty("tenantId")
    @NotBlank
    @Size(max = 128)
    private String tenantId;

    @JsonProperty("reason")
    @NotBlank
    @Size(max = 512)
    private String reason;
}