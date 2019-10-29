package org.paasta.servicebroker.apigateway.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.paasta.servicebroker.apigateway.model.RequestFixture;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayServiceInstanceBindingService;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * The type Api gateway service instance binding service test.
 */
@RunWith(SpringRunner.class)
public class ApiGatewayServiceInstanceBindingServiceTest {

    @InjectMocks
    ApiGatewayServiceInstanceBindingService apiGatewayServiceInstanceBindingService;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Create service instance binding valid return.
     */
    @Test
    public void createServiceInstanceBindingTest() {
        CreateServiceInstanceBindingRequest request = RequestFixture.getCreateServiceInstanceBindingRequest();

        assertThatThrownBy(() -> apiGatewayServiceInstanceBindingService.createServiceInstanceBinding(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Not Supported");

    }

    /**
     * Delete service instance binding valid return.
     */
    @Test
    public void deleteServiceInstanceBindingTest() {
        DeleteServiceInstanceBindingRequest request = RequestFixture.getDeleteServiceInstanceBindingRequest();

        assertThatThrownBy(() -> apiGatewayServiceInstanceBindingService.deleteServiceInstanceBinding(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Not Supported");

    }
}