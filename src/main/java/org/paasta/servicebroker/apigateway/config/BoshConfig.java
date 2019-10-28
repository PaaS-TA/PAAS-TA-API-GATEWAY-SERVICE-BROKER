package org.paasta.servicebroker.apigateway.config;

import org.openpaas.paasta.bosh.director.BoshDirector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Bosh config.
 */
@Configuration
public class BoshConfig {

    @Value("${bosh.client_id}")
    public String client_id;
    @Value("${bosh.client_secret}")
    public String client_secret;
    @Value("${bosh.url}")
    public String bosh_url;
    @Value("${bosh.oauth_url}")
    public String oauth_url;

    /**
     * Bosh director bosh director.
     *
     * @return the bosh director
     */
    @Bean
    BoshDirector boshDirector() {
        BoshDirector boshDirector = new BoshDirector(client_id, client_secret, bosh_url, oauth_url);
        return boshDirector;
    }
}