package org.paasta.servicebroker.apigateway.config;

import org.openpaas.servicebroker.model.Catalog;
import org.openpaas.servicebroker.model.DashboardClient;
import org.openpaas.servicebroker.model.Plan;
import org.openpaas.servicebroker.model.ServiceDefinition;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayCatalogPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * The type Catalog config.
 */
@Configuration
@EnableConfigurationProperties(ApiGatewayCatalogPropertyService.class)
public class CatalogConfig {

    private final ApiGatewayCatalogPropertyService apiGatewayCatalogPropertyService;


    /**
     * Instantiates a new Catalog config.
     *
     * @param apiGatewayCatalogPropertyService the api gateway catalog property service
     */
    @Autowired
    public CatalogConfig(ApiGatewayCatalogPropertyService apiGatewayCatalogPropertyService) {
        this.apiGatewayCatalogPropertyService = apiGatewayCatalogPropertyService;
    }

    /**
     * Catalog catalog.
     *
     * @return the catalog
     */
    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        apiGatewayCatalogPropertyService.getId(),
                        apiGatewayCatalogPropertyService.getName(),
                        apiGatewayCatalogPropertyService.getDescription(),
                        apiGatewayCatalogPropertyService.isBindable(),
                        apiGatewayCatalogPropertyService.isPlanUpdatable(),
                        getPlans(),
                        apiGatewayCatalogPropertyService.getTags(),
                        apiGatewayCatalogPropertyService.getMetadata(),
                        apiGatewayCatalogPropertyService.getRequires(),
                        apiGatewayCatalogPropertyService.getDashboardClient() == null ? null :
                                new DashboardClient(apiGatewayCatalogPropertyService.getDashboardClient().getId(),
                                        apiGatewayCatalogPropertyService.getDashboardClient().getSecret(),
                                        apiGatewayCatalogPropertyService.getDashboardClient().getRedirectUri())
                ))
        );
    }

    private List<Plan> getPlans() {
        List<Plan> plans = new ArrayList<>();

        apiGatewayCatalogPropertyService.getPlans().forEach(e -> plans.add(
                new Plan(e.getId(),
                        e.getName(),
                        e.getDescription(),
                        getPlanMetadata(e.getMetadata()),
                        e.isFree())
        ));

        return plans;
    }

    private Map<String, Object> getPlanMetadata(ApiGatewayCatalogPropertyService.PlanMetaData metaData) {
        Map<String, Object> planMetadata = new HashMap<>();

        planMetadata.put("costs", getCosts(metaData.getCosts()));
        planMetadata.put("bullets", metaData.getBullets());

        return planMetadata;
    }

    private List<Map<String, Object>> getCosts(ApiGatewayCatalogPropertyService.Cost cost) {
        Map<String, Object> costsMap = new HashMap<>();

        costsMap.put("amount", cost.getAmount());
        costsMap.put("unit", cost.getUnit());

        return Collections.singletonList(costsMap);
    }

}
