package org.paasta.servicebroker.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Jpa service instance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_instance")
public class JpaServiceInstance {

    @Id
    @Column(name = "service_instance_id")
    private String serviceInstanceId;
    @Column(name = "service_id", nullable = false)
    private String serviceId;
    @Column(name = "plan_id", nullable = false)
    private String planId;
    @Column(name = "organization_guid", nullable = false)
    private String organizationGuid;
    @Column(name = "space_guid", nullable = false)
    private String spaceGuid;
    @Column(name = "dashboard_url", nullable = false)
    private String dashboardUrl;
    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdTime;
}
