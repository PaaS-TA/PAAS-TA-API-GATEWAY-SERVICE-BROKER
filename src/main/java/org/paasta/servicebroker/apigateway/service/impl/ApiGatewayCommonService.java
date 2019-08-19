package org.paasta.servicebroker.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.exception.ServiceException;
import org.paasta.servicebroker.apigateway.model.JpaDedicatedVM;
import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.paasta.servicebroker.apigateway.repository.JpaDedicatedVMRepository;
import org.paasta.servicebroker.apigateway.repository.JpaServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The type Api gateway common service.
 */
@Slf4j
@Service
public class ApiGatewayCommonService {

    @Value("${bosh.deployment_name}")
    public String deploymentName;
    @Autowired
    BoshDirector boshDirector;

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;
    @Autowired
    JpaDedicatedVMRepository jpaDedicatedVMRepository;

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

    String serviceAssignment(String serviceInstanceId) throws ServiceException {
        JpaDedicatedVM jpaDedicatedVM = jpaDedicatedVMRepository.findDistinctFirstByAssignmentEquals(Constants.STATUS_WATING_FOR_ASSIGNMENT);

        if (jpaDedicatedVM != null) {
            jpaDedicatedVM.setAssignment(Constants.STATUS_ASSIGNED);
            jpaDedicatedVM.setProvisionedServiceInstanceId(serviceInstanceId);
            jpaDedicatedVMRepository.save(jpaDedicatedVM);
            return jpaDedicatedVM.getDashboardUrl();
        } else {
            throw new ServiceException("Cannot assign VM. There are no available service VM.");
        }
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
    void procDeProvisioning(String serviceInstanceId) throws ServiceException {

        // Delete service instance data
        jpaServiceInstanceRepository.delete(serviceInstanceId);

        // Deprovision dedicated VM
        JpaDedicatedVM jpaDedicatedVM = deprovisionVM(serviceInstanceId);

        // Call Bosh RecreateVM API
        reCreateVM(jpaDedicatedVM.getVmName(), jpaDedicatedVM.getVmId());

    }

    /**
     * Deprovision vm jpa dedicated vm.
     *
     * @param serviceInstanceId the service instance id
     * @return the jpa dedicated vm
     * @throws ServiceException the service exception
     */
    JpaDedicatedVM deprovisionVM(String serviceInstanceId) throws ServiceException {
        JpaDedicatedVM jpaDedicatedVM = jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(serviceInstanceId);

        if (jpaDedicatedVM != null) {
            jpaDedicatedVM.setAssignment(Constants.STATUS_WATING_FOR_VM_RECREATE);
            jpaDedicatedVM.setProvisionedServiceInstanceId(null);
            jpaDedicatedVM.setProvisionedTime(null);
            jpaDedicatedVMRepository.save(jpaDedicatedVM);
            return jpaDedicatedVM;
        } else {
            throw new ServiceException("Cannot deprovision. There are no provisioned VM.");
        }
    }

    /**
     * Request Bosh RecreateVM API
     *
     * @param vmName vm name
     * @param vmId vm id
     * @throws ServiceException the service exception
     */
    private void reCreateVM(String vmName, String vmId) throws ServiceException {
        try {

            boolean result = boshDirector.updateInstanceState(deploymentName, vmName, vmId, Constants.JOB_STATE_RECREATE);

            if (!result) {
                log.error("##### reCreateVM :: use Bosh API ::: deploymentName :: {}, vmName :: {}, vmId :: {} ", deploymentName, vmName, vmId);
                throw new ServiceException("Failed to recreate dedecated VM :: Deployment Name [" + deploymentName + "], VM Name/VM ID [" + vmName+"/"+vmId + "]");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("Failed to recreate dedecated VM :: " + e.getMessage());
        }
    }
}
