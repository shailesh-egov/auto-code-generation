package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Size;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankBranchIdentifier {

    @JsonProperty("type")
    @Size(min = 2, max = 64)
    private String type;

    @JsonProperty("code")
    @Size(min = 2, max = 64)
    private String code;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;
}