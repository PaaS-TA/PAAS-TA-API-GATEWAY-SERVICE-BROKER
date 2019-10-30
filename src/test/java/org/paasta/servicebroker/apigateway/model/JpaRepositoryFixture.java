package org.paasta.servicebroker.apigateway.model;

import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.service.TestConstants;

import java.util.Date;

/**
 * The type Request fixture.
 */
public class JpaRepositoryFixture {

    /**
     * Gets jpa service instance.
     *
     * @return the jpa service instance
     */
    public static JpaServiceInstance getJpaServiceInstance() {

        CreateServiceInstanceRequest createServiceInstanceRequest = RequestFixture.getCreateServiceInstanceRequest();
        ServiceInstance serviceInstance = RequestFixture.getServiceInstance();

        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();

        new JpaServiceInstance(serviceInstance.getServiceInstanceId(),
                serviceInstance.getServiceDefinitionId(),
                serviceInstance.getPlanId(),
                serviceInstance.getOrganizationGuid(),
                serviceInstance.getSpaceGuid(),
                serviceInstance.getDashboardUrl(),
                new Date());

        return jpaServiceInstance;

    }

    /**
     * Gets jpa dedicated vm.
     *
     * @return the jpa dedicated vm
     */
    public static JpaDedicatedVM getJpaDedicatedVM() {
        CreateServiceInstanceRequest createServiceInstanceRequest = RequestFixture.getCreateServiceInstanceRequest();

        return JpaDedicatedVM.builder()
                .ip("123.123.123.123")
                .vmName("TEST_VM_NAME")
                .vmId("TEST_VM_ID")
                .assignment(0)
                .dashboardUrl(TestConstants.DASHBOARD_URL)
                .provisionedServiceInstanceId(TestConstants.SV_INSTANCE_ID)
                .provisionedTime(new Date())
                .createdTime(new Date())
                .build();

    }
}
