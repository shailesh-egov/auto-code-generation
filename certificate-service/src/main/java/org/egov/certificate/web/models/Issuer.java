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
public class Issuer {

    @JsonProperty("id")
    @NotBlank
    @Size(max = 256)
    private String id;

    @JsonProperty("name")
    @NotBlank
    @Size(max = 256)
    private String name;

    @JsonProperty("type")
    @NotBlank
    private String type;
}