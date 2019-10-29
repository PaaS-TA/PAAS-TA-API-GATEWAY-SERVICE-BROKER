package org.paasta.servicebroker.apigateway.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openpaas.servicebroker.model.Catalog;
import org.openpaas.servicebroker.model.ServiceDefinition;
import org.paasta.servicebroker.apigateway.model.ServiceDefinitionFixture;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayCatalogService;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * The type Api gateway catalog service test.
 */
@RunWith(SpringRunner.class)
public class ApiGatewayCatalogServiceTest {

    @InjectMocks
    ApiGatewayCatalogService apiGatewayCatalogService;

    @Mock
    Catalog catalog;

    @Mock
    ServiceDefinition serviceDefinition;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        catalog = new Catalog(ServiceDefinitionFixture.getCatalog());
        apiGatewayCatalogService = new ApiGatewayCatalogService(catalog);
    }

    /**
     * Gets catalog valid return.
     */
    @Test
    public void getCatalogTest() {
        Catalog result = apiGatewayCatalogService.getCatalog();

        assertThat(result.getServiceDefinitions().get(0).getId(), is(ServiceDefinitionFixture.getService().getId()));
        assertThat(result.getServiceDefinitions().get(0).getName(), is(ServiceDefinitionFixture.getService().getName()));
        assertThat(result.getServiceDefinitions().get(0).isBindable(), is(ServiceDefinitionFixture.getService().isBindable()));
        assertThat(result.getServiceDefinitions().get(0).isPlanUpdatable(), is(ServiceDefinitionFixture.getService().isPlanUpdatable()));
    }

    /**
     * Gets service definition valid return.
     */
    @Test
    public void getServiceDefinitionTest() {
        serviceDefinition = apiGatewayCatalogService.getServiceDefinition(Constants.SERVICES_ID);

        assertThat(serviceDefinition.getId(), is(ServiceDefinitionFixture.getService().getId()));
        assertThat(serviceDefinition.getName(), is(ServiceDefinitionFixture.getService().getName()));
        assertThat(serviceDefinition.isBindable(), is(ServiceDefinitionFixture.getService().isBindable()));
        assertThat(serviceDefinition.isPlanUpdatable(), is(ServiceDefinitionFixture.getService().isPlanUpdatable()));
    }

}



