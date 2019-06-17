package org.paasta.servicebroker.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.paasta.servicebroker.apigateway.repository.JpaServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Api gateway common service.
 */
@Slf4j
@Service
public class ApiGatewayCommonService {

    private final JpaServiceInstanceRepository jpaServiceInstanceRepository;

    /**
     * Instantiates a new Api gateway common service.
     *
     * @param jpaServiceInstanceRepository the jpa service instance repository
     */
    @Autowired
    public ApiGatewayCommonService(JpaServiceInstanceRepository jpaServiceInstanceRepository) {
        this.jpaServiceInstanceRepository = jpaServiceInstanceRepository;
    }

    /**
     * Gets service instance.
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance
     */
    ServiceInstance getServiceInstance(String serviceInstanceId) {
        JpaServiceInstance jpaServiceInstance = jpaServiceInstanceRepository.findOne(serviceInstanceId);

        if (jpaServiceInstance != null) {
        return new ServiceInstance(new CreateServiceInstanceRequest(
                jpaServiceInstance.getServiceId(),
                jpaServiceInstance.getPlanId(),
                jpaServiceInstance.getOrganizationGuid(),
                jpaServiceInstance.getSpaceGuid()
        ).withServiceInstanceId(jpaServiceInstance.getServiceInstanceId())
        ).withDashboardUrl(jpaServiceInstance.getDashboardUrl());
    }
        return null;
}

    /**
     * provisioning.
     *
     * @param serviceInstance the service instance
     */
    void createServiceInstance(ServiceInstance serviceInstance) {
        JpaServiceInstance jpaServiceInstance = JpaServiceInstance.builder()
                .serviceInstanceId(serviceInstance.getServiceInstanceId())
                .serviceId(serviceInstance.getServiceDefinitionId())
                .planId(serviceInstance.getPlanId())
                .organizationGuid(serviceInstance.getOrganizationGuid())
                .spaceGuid(serviceInstance.getSpaceGuid())
                .dashboardUrl(serviceInstance.getDashboardUrl())
                .build();

        jpaServiceInstanceRepository.save(jpaServiceInstance);
    }

    /**
     * Find by org guid service instance.
     *
     * @param orgGuid the org guid
     * @return the service instance
     */
    ServiceInstance findByOrgGuid(String orgGuid) {
        JpaServiceInstance jpaServiceInstanceorg = jpaServiceInstanceRepository.findDistinctFirstByOrganizationGuid(orgGuid);

        ServiceInstance serviceInstance = null;

        if ( jpaServiceInstanceorg != null ) {
            serviceInstance = new ServiceInstance(
                    new CreateServiceInstanceRequest(
                            jpaServiceInstanceorg.getServiceInstanceId(),
                            jpaServiceInstanceorg.getPlanId(),
                            jpaServiceInstanceorg.getOrganizationGuid(),
                            jpaServiceInstanceorg.getSpaceGuid()
                    ).withServiceInstanceId(jpaServiceInstanceorg.getServiceInstanceId()));
        }

        return serviceInstance;
    }


    /**
     * Proc deprovisioning.
     *
     * @param serviceInstanceId the service instance id
     */
    void procDeProvisioning(String serviceInstanceId) {

        //TODO
        log.debug("deprovisioning...");
        jpaServiceInstanceRepository.delete(serviceInstanceId);
    }
}
