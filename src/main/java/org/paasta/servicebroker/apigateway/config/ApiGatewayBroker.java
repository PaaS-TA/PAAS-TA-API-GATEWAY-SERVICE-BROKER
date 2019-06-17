package org.paasta.servicebroker.apigateway.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The type Application.
 */
@SpringBootApplication(scanBasePackages = {"org.openpaas.servicebroker", "org.paasta.servicebroker"})
@EnableJpaRepositories("org.paasta.servicebroker.apigateway.repository")
@EntityScan(value = "org.paasta.servicebroker.apigateway.model")
public class ApiGatewayBroker {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayBroker.class, args);
    }
}
