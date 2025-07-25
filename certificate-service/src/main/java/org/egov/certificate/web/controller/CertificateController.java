package org.egov.certificate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.service.CertificateService;
import org.egov.certificate.service.VerificationService;
import org.egov.certificate.web.models.*;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/_create")
    public ResponseEntity<CertificateResponse> createCertificates(
            @Valid @RequestBody CertificateRequest certificateRequest) {
        
        log.info("Received request to create certificates");
        
        List<Certificate> certificates = certificateService.createCertificates(certificateRequest);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(certificateRequest.getRequestInfo().getApiId())
                .ver(certificateRequest.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(certificateRequest.getRequestInfo().getMsgId())
                .msgId(certificateRequest.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        CertificateResponse response = CertificateResponse.builder()
                .responseInfo(responseInfo)
                .certificates(certificates)
                .totalCount(certificates.size())
                .build();

        log.info("Successfully created {} certificates", certificates.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/_search")
    public ResponseEntity<CertificateResponse> searchCertificates(
            @Valid @RequestBody CertificateSearchRequest searchRequest) {
        
        log.info("Received request to search certificates");
        
        List<Certificate> certificates = certificateService.searchCertificates(searchRequest);
        Integer totalCount = certificateService.getCertificateCount(searchRequest.getSearchCriteria());
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(searchRequest.getRequestInfo().getApiId())
                .ver(searchRequest.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(searchRequest.getRequestInfo().getMsgId())
                .msgId(searchRequest.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        CertificateResponse response = CertificateResponse.builder()
                .responseInfo(responseInfo)
                .certificates(certificates)
                .totalCount(totalCount)
                .build();

        log.info("Successfully retrieved {} certificates out of {} total", certificates.size(), totalCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_update")
    public ResponseEntity<CertificateResponse> updateCertificates(
            @Valid @RequestBody CertificateRequest certificateRequest) {
        
        log.info("Received request to update certificates");
        
        List<Certificate> certificates = certificateService.updateCertificates(certificateRequest);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(certificateRequest.getRequestInfo().getApiId())
                .ver(certificateRequest.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(certificateRequest.getRequestInfo().getMsgId())
                .msgId(certificateRequest.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        CertificateResponse response = CertificateResponse.builder()
                .responseInfo(responseInfo)
                .certificates(certificates)
                .totalCount(certificates.size())
                .build();

        log.info("Successfully updated {} certificates", certificates.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_revoke")
    public ResponseEntity<CertificateResponse> revokeCertificates(
            @Valid @RequestBody CertificateRevocationRequest revocationRequest) {
        
        log.info("Received request to revoke certificates");
        
        List<Certificate> certificates = certificateService.revokeCertificates(revocationRequest);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(revocationRequest.getRequestInfo().getApiId())
                .ver(revocationRequest.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(revocationRequest.getRequestInfo().getMsgId())
                .msgId(revocationRequest.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        CertificateResponse response = CertificateResponse.builder()
                .responseInfo(responseInfo)
                .certificates(certificates)
                .totalCount(certificates.size())
                .build();

        log.info("Successfully revoked {} certificates", certificates.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_verify")
    public ResponseEntity<CertificateVerificationResponse> verifyCertificate(
            @Valid @RequestBody CertificateVerificationRequest verificationRequest) {
        
        log.info("Received request to verify certificate");
        
        VerificationResult verificationResult = verificationService.verifyCertificate(verificationRequest);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(verificationRequest.getRequestInfo().getApiId())
                .ver(verificationRequest.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(verificationRequest.getRequestInfo().getMsgId())
                .msgId(verificationRequest.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        CertificateVerificationResponse response = CertificateVerificationResponse.builder()
                .responseInfo(responseInfo)
                .verificationResult(verificationResult)
                .build();

        log.info("Successfully verified certificate: {} - Valid: {}", 
                verificationResult.getCertificateId(), verificationResult.getIsValid());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("Certificate Service is running", HttpStatus.OK);
    }
}