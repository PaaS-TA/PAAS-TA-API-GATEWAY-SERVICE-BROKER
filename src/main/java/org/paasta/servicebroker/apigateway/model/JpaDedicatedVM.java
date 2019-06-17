package org.paasta.servicebroker.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Jpa Dedicated VM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dedicated_vm")
public class JpaDedicatedVM {

    @Id
    @Column(name = "vm_id")
    private String vmId;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "assignment", nullable = false)
    private String assignment;
    @Column(name = "dashboard_url")
    private String dashboardUrl;
    @Column(name = "provisioned_service_instance_id")
    private String provisionedServiceInstance_id;
    @Column(name = "provisioned_time")
    private String provisionedTime;

}
