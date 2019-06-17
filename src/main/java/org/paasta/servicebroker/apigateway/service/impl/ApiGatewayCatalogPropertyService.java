package org.paasta.servicebroker.apigateway.service.impl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Api gateway catalog property service.
 */
@Data
@ConfigurationProperties(prefix = "services")
public class ApiGatewayCatalogPropertyService {

    private String id;
    private String name;
    private String description;
    private boolean bindable;
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> metadata;
    private List<String> requires = new ArrayList<>();
    private boolean planUpdatable;
    private List<Plan> plans;
    private DashboardClient dashboardClient;

    /**
     * The type Plan.
     */
    @Data
    public static class Plan {
        private String id;
        private String name;
        private String description;
        private PlanMetaData metadata;
        private boolean free;
    }

    /**
     * The type Plan meta data.
     */
    @Data
    public static class PlanMetaData {

        private List<String> bullets;
        private Cost costs;
    }

    /**
     * The type Cost.
     */
    @Data
    public static class Cost {
        private Map<String, Object> amount;
        private String unit;
    }

    /**
     * The type Dashboard client.
     */
    @Data
    public static class DashboardClient {
        private String id = null;
        private String secret = null;
        private String redirectUri = null;
    }

}
