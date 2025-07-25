package org.egov.certificate.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateSearchCriteria {

    @JsonProperty("tenantId")
    @NotBlank
    @Size(max = 128)
    private String tenantId;

    @JsonProperty("ids")
    @Size(max = 50)
    private List<String> ids;

    @JsonProperty("issuerIds")
    @Size(max = 10)
    private List<String> issuerIds;

    @JsonProperty("subjectIds")
    @Size(max = 50)
    private List<String> subjectIds;

    @JsonProperty("certificateTypes")
    @Size(max = 10)
    private List<String> certificateTypes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("fromDate")
    private Long fromDate;

    @JsonProperty("toDate")
    private Long toDate;

    @JsonProperty("sortBy")
    @Builder.Default
    private String sortBy = "issued";

    @JsonProperty("sortOrder")
    @Builder.Default
    private String sortOrder = "DESC";

    @JsonProperty("limit")
    @Min(1)
    @Max(200)
    @Builder.Default
    private Integer limit = 100;

    @JsonProperty("offset")
    @Min(0)
    @Builder.Default
    private Integer offset = 0;
}