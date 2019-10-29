package org.paasta.servicebroker.apigateway.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.paasta.servicebroker.apigateway.model.ServiceDefinitionFixture;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayCatalogPropertyService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * The type Api gateway catalog property service test.
 */
@RunWith(SpringRunner.class)
public class ApiGatewayCatalogPropertyServiceTest {

    @InjectMocks
    ApiGatewayCatalogPropertyService apiGatewayCatalogPropertyService;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Gets method valid return.
     *
     * @throws Exception the exception
     */
    @Test
    public void catalogPropertyTest() throws Exception {

        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "id", Constants.SERVICES_ID);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "name", Constants.SERVICES_NAME);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "description", Constants.SERVICES_DESCRIPTION);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "bindable", Constants.SERVICES_BINDABLE);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "tags", Constants.SERVICES_TAGS);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "metadata", ServiceDefinitionFixture.getMetadata());
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "requires", Constants.SERVICES_REQUIRES);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "planUpdatable", Constants.SERVICES_PLAN_UPDATABLE);
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "plans", ServiceDefinitionFixture.getPlans());
        ReflectionTestUtils.setField (apiGatewayCatalogPropertyService, "dashboardClient", ServiceDefinitionFixture.getDashboardClient());

        // getId()
        assertThat(apiGatewayCatalogPropertyService.getId(),is(ServiceDefinitionFixture.getService().getId()));
        // getName()
        assertThat(apiGatewayCatalogPropertyService.getName(),is(ServiceDefinitionFixture.getService().getName()));
        // getDescription()
        assertThat(apiGatewayCatalogPropertyService.getDescription(),is(ServiceDefinitionFixture.getService().getDescription()));
        // isBindable
        assertThat(apiGatewayCatalogPropertyService.isBindable(),is(ServiceDefinitionFixture.getService().isBindable()));
        // getTags()
        assertThat(apiGatewayCatalogPropertyService.getTags(),is(ServiceDefinitionFixture.getService().getTags()));
        // getMetadata()
        assertThat(apiGatewayCatalogPropertyService.getMetadata(),is(ServiceDefinitionFixture.getMetadata()));
        // getRequires()
        assertThat(apiGatewayCatalogPropertyService.getRequires(),is(ServiceDefinitionFixture.getService().getRequires()));
        // isPlanUpdatable()
        assertThat(apiGatewayCatalogPropertyService.isPlanUpdatable(),is(ServiceDefinitionFixture.getService().isPlanUpdatable()));
        // getPlans()
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).getId(),is(ServiceDefinitionFixture.getPlans().get(0).getId()));
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).getId(),is(ServiceDefinitionFixture.getPlans().get(0).getId()));
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).getName(),is(ServiceDefinitionFixture.getPlans().get(0).getName()));
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).getDescription(),is(ServiceDefinitionFixture.getPlans().get(0).getDescription()));
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).getMetadata(),is(ServiceDefinitionFixture.getPlans().get(0).getMetadata()));
        assertThat(apiGatewayCatalogPropertyService.getPlans().get(0).isFree(),is(ServiceDefinitionFixture.getPlans().get(0).isFree()));
        // getDashboardClient()
        assertThat(apiGatewayCatalogPropertyService.getDashboardClient().getId(),is(ServiceDefinitionFixture.getDashboardClient().getId()));
        assertThat(apiGatewayCatalogPropertyService.getDashboardClient().getRedirectUri(),is(ServiceDefinitionFixture.getDashboardClient().getRedirectUri()));
        assertThat(apiGatewayCatalogPropertyService.getDashboardClient().getSecret(),is(ServiceDefinitionFixture.getDashboardClient().getSecret()));
    }
}



