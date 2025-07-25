package org.egov.certificate.util;

public class ErrorConstants {

    // Error Codes (as integers for DIGIT common contracts)
    public static final String VALIDATION_ERROR_CODE = "400001";
    public static final String BINDING_ERROR_CODE = "400002";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500001";
    public static final String CUSTOM_ERROR_CODE = "400000";

    // Error Messages
    public static final String VALIDATION_ERROR_MESSAGE = "Validation failed";
    public static final String BINDING_ERROR_MESSAGE = "Request binding failed";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    // Error Descriptions
    public static final String VALIDATION_ERROR_DESCRIPTION = "One or more field validations failed";
    public static final String BINDING_ERROR_DESCRIPTION = "Request binding failed due to malformed input";
    public static final String INTERNAL_SERVER_ERROR_DESCRIPTION = "An unexpected error occurred while processing the request";

    private ErrorConstants() {
        // Utility class
    }
}