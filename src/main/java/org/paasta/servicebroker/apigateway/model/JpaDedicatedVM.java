package org.paasta.servicebroker.apigateway.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * The type Jpa Dedicated VM.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dedicated_vm")
public class JpaDedicatedVM {

    @Id
    @Column(name = "vm_ip")
    private String ip;
    @NotNull
    @Column(name = "vm_name")
    private String vmName;
    @NotNull
    @Column(name = "vm_id")
    private String vmId;
    @NotNull
    @Column(name = "assignment")
    private int assignment;
    @Column(name = "dashboard_url")
    private String dashboardUrl;
    @Column(name = "provisioned_service_instance_id")
    private String provisionedServiceInstanceId;
    @UpdateTimestamp
    @Column(name = "provisioned_time")
    private Date provisionedTime;
    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdTime;
}
