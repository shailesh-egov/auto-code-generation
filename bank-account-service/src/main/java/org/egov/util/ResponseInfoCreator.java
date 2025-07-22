package org.egov.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoCreator {

    public ResponseInfo createResponseInfoFromRequestInfo(RequestInfo requestInfo, boolean success) {
        String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = requestInfo != null ? requestInfo.getTs() : System.currentTimeMillis();
        String resMsgId = "uief87324";
        String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        String responseStatus = success ? "SUCCESS" : "FAILED";

        return ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .resMsgId(resMsgId)
                .msgId(msgId)
                .status(responseStatus)
                .build();
    }
}