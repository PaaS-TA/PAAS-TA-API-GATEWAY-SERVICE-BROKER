package org.paasta.servicebroker.apigateway.model;

import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.service.Constants;

import java.util.Date;

/**
 * The type Request fixture.
 */
public class JpaRepositoryFixture {

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

    public static JpaDedicatedVM getJpaDedicatedVM() {
        CreateServiceInstanceRequest createServiceInstanceRequest = RequestFixture.getCreateServiceInstanceRequest();

        return JpaDedicatedVM.builder()
                .ip("123.123.123.123")
                .vmName("TEST_VM_NAME")
                .vmId("TEST_VM_ID")
                .assignment(0)
                .dashboardUrl(Constants.DASHBOARD_URL)
                .provisionedServiceInstanceId(Constants.SV_INSTANCE_ID)
                .provisionedTime(new Date())
                .createdTime(new Date())
                .build();

    }
}
