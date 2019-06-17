package org.paasta.servicebroker.apigateway.repository;

import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Jpa service instance repository.
 */
@Repository
public interface JpaServiceInstanceRepository extends JpaRepository<JpaServiceInstance, String> {

    JpaServiceInstance findDistinctFirstByOrganizationGuid(String orgId);

}
