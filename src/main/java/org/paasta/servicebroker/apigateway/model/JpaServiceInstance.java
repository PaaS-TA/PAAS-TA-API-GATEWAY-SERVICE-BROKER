package org.paasta.servicebroker.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "service_id")
    private String serviceId;
    @NotNull
    @Column(name = "plan_id")
    private String planId;
    @NotNull
    @Column(name = "organization_guid")
    private String organizationGuid;
    @NotNull
    @Column(name = "space_guid")
    private String spaceGuid;
    @NotNull
    @Column(name = "dashboard_url")
    private String dashboardUrl;
    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdTime;
}
