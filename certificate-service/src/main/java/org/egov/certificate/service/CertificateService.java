package org.egov.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.certificate.repository.CertificateRepository;
import org.egov.certificate.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private CertificateKafkaProducer certificateKafkaProducer;

    @Autowired
    private CertificateConfiguration configuration;

    public List<Certificate> createCertificates(CertificateRequest certificateRequest) {
        RequestInfo requestInfo = certificateRequest.getRequestInfo();
        List<Certificate> certificates = certificateRequest.getCertificates();

        log.info("Creating {} certificates for tenant", certificates.size());

        // Validate certificates
        validationService.validateCertificateRequest(certificateRequest);

        // Enrich certificates
        enrichmentService.enrichCertificatesForCreate(certificates, requestInfo);

        // Publish to Kafka for persistence
        certificateKafkaProducer.push(configuration.getCreateTopic(), certificateRequest);

        log.info("Successfully processed {} certificates for creation", certificates.size());
        return certificates;
    }

    public List<Certificate> searchCertificates(CertificateSearchRequest searchRequest) {
        RequestInfo requestInfo = searchRequest.getRequestInfo();
        CertificateSearchCriteria criteria = searchRequest.getSearchCriteria();

        log.info("Searching certificates with criteria: {}", criteria);

        // Apply default values if not provided
        if (criteria.getLimit() == null) {
            criteria.setLimit(configuration.getDefaultLimit());
        }
        if (criteria.getOffset() == null) {
            criteria.setOffset(configuration.getDefaultOffset());
        }

        // Validate search request
        validationService.validateSearchRequest(searchRequest);

        // Search certificates
        List<Certificate> certificates = certificateRepository.getCertificates(criteria);

        log.info("Found {} certificates matching search criteria", certificates.size());
        return certificates;
    }

    public Integer getCertificateCount(CertificateSearchCriteria criteria) {
        return certificateRepository.getCertificateCount(criteria);
    }

    public List<Certificate> updateCertificates(CertificateRequest certificateRequest) {
        RequestInfo requestInfo = certificateRequest.getRequestInfo();
        List<Certificate> certificates = certificateRequest.getCertificates();

        log.info("Updating {} certificates", certificates.size());

        // Validate certificates for update
        validationService.validateCertificateUpdateRequest(certificateRequest);

        // Enrich certificates for update
        enrichmentService.enrichCertificatesForUpdate(certificates, requestInfo);

        // Publish to Kafka for persistence
        certificateKafkaProducer.push(configuration.getUpdateTopic(), certificateRequest);

        log.info("Successfully processed {} certificates for update", certificates.size());
        return certificates;
    }

    public List<Certificate> revokeCertificates(CertificateRevocationRequest revocationRequest) {
        RequestInfo requestInfo = revocationRequest.getRequestInfo();
        List<RevocationDetail> revocationDetails = revocationRequest.getRevocationDetails();

        log.info("Revoking {} certificates", revocationDetails.size());

        // Validate revocation request
        validationService.validateRevocationRequest(revocationRequest);

        // Get certificates to be revoked
        List<Certificate> certificates = getCertificatesForRevocation(revocationDetails);

        // Enrich certificates for revocation
        enrichmentService.enrichCertificatesForRevocation(certificates, revocationDetails, requestInfo);

        // Create request for persistence
        CertificateRequest certificateRequest = CertificateRequest.builder()
                .requestInfo(requestInfo)
                .certificates(certificates)
                .build();

        // Publish to Kafka for persistence
        certificateKafkaProducer.push(configuration.getRevokeTopic(), certificateRequest);

        log.info("Successfully processed {} certificates for revocation", certificates.size());
        return certificates;
    }

    private List<Certificate> getCertificatesForRevocation(List<RevocationDetail> revocationDetails) {
        // Group by tenant for efficiency
        return revocationDetails.stream()
                .map(detail -> certificateRepository.getCertificateById(detail.getTenantId(), detail.getCertificateId()))
                .filter(cert -> cert != null)
                .toList();
    }

    public Certificate getCertificateById(String tenantId, String certificateId) {
        return certificateRepository.getCertificateById(tenantId, certificateId);
    }

    public boolean existsCertificate(String tenantId, String certificateId) {
        return certificateRepository.existsCertificate(tenantId, certificateId);
    }
}