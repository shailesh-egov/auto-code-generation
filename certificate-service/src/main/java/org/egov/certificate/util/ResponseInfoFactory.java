package org.egov.certificate.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(RequestInfo requestInfo, Boolean success) {
        String status = success ? "successful" : "failed";
        
        return ResponseInfo.builder()
                .apiId(requestInfo.getApiId())
                .ver(requestInfo.getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(requestInfo.getMsgId())
                .msgId(requestInfo.getMsgId())
                .status(status)
                .build();
    }

    public ResponseInfo createSuccessResponseInfo(RequestInfo requestInfo) {
        return createResponseInfoFromRequestInfo(requestInfo, true);
    }

    public ResponseInfo createFailureResponseInfo(RequestInfo requestInfo) {
        return createResponseInfoFromRequestInfo(requestInfo, false);
    }
}