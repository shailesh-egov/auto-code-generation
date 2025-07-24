package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.DecimalMax;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagination {

    @JsonProperty("limit")
    @DecimalMax("100")
    @Builder.Default
    private Double limit = 10.0;

    @JsonProperty("offSet")
    @Builder.Default
    private Double offSet = 0.0;

    @JsonProperty("totalCount")
    @Builder.Default
    private Double totalCount = null;

    @JsonProperty("sortBy")
    @Builder.Default
    private String sortBy = null;

    @JsonProperty("order")
    @Builder.Default
    private Order order = null;

    /**
     * Sorting order
     */
    public enum Order {
        ASC("asc"),
        DESC("desc");

        private String value;

        Order(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static Order fromValue(String text) {
            for (Order b : Order.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}