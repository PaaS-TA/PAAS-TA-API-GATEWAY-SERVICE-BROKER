package org.paasta.servicebroker.apigateway.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.exception.ServiceException;
import org.paasta.servicebroker.apigateway.model.JpaDedicatedVM;
import org.paasta.servicebroker.apigateway.model.JpaRepositoryFixture;
import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.paasta.servicebroker.apigateway.model.RequestFixture;
import org.paasta.servicebroker.apigateway.repository.JpaDedicatedVMRepository;
import org.paasta.servicebroker.apigateway.repository.JpaServiceInstanceRepository;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayCommonService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * The type Api gateway common service test.
 */
@RunWith(SpringRunner.class)
public class ApiGatewayCommonServiceTest {

    @InjectMocks
    ApiGatewayCommonService apiGatewayCommonService;

    @Mock
    JpaServiceInstanceRepository jpaServiceInstanceRepository;
    @Mock
    JpaDedicatedVMRepository jpaDedicatedVMRepository;
    @Mock
    BoshDirector boshDirector;

    JpaServiceInstance jpaServiceInstance;
    JpaDedicatedVM jpaDedicatedVM;
    ServiceInstance serviceInstance;
    CreateServiceInstanceRequest createServiceInstanceRequest;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        ReflectionTestUtils.setField(apiGatewayCommonService, "deploymentName", TestConstants.DEPLOYMENT_NAME);
        createServiceInstanceRequest = RequestFixture.getCreateServiceInstanceRequest();
        Map vaildParam = new HashMap<>();
        vaildParam.put(TestConstants.PARAMETERS_KEY, TestConstants.VAILD_PARAMETER_VALUE);
        createServiceInstanceRequest.setParameters(vaildParam);

        jpaServiceInstance = JpaRepositoryFixture.getJpaServiceInstance();
        jpaDedicatedVM = JpaRepositoryFixture.getJpaDedicatedVM();
        serviceInstance = RequestFixture.getServiceInstance();
    }

    /**
     * Gets service instance test verify return.
     */
    @Test
    public void getServiceInstanceTest_VerifyReturn() {

        when(jpaServiceInstanceRepository.findOne(anyString())).thenReturn(jpaServiceInstance);

        ServiceInstance result = apiGatewayCommonService.getServiceInstance(TestConstants.SV_INSTANCE_ID);

        assertThat(result.getServiceDefinitionId(), is(jpaServiceInstance.getServiceId()));
        assertThat(result.getPlanId(), is(jpaServiceInstance.getPlanId()));
        assertThat(result.getOrganizationGuid(), is(jpaServiceInstance.getOrganizationGuid()));
        assertThat(result.getSpaceGuid(), is(jpaServiceInstance.getSpaceGuid()));
        assertThat(result.getServiceInstanceId(), is(jpaServiceInstance.getServiceInstanceId()));
        assertThat(result.getDashboardUrl(), is(jpaServiceInstance.getDashboardUrl()));
    }

    /**
     * Gets service instance test verify return is null.
     */
    @Test
    public void getServiceInstanceTest_VerifyReturnIsNull() {

        when(jpaServiceInstanceRepository.findOne(anyString())).thenReturn(null);

        ServiceInstance result = apiGatewayCommonService.getServiceInstance(TestConstants.SV_INSTANCE_ID);

        assertThat(result, is(nullValue()));
    }

    /**
     * Find by org guid test verify return.
     */
    @Test
    public void findByOrgGuidTest_VerifyReturn() {

        when(jpaServiceInstanceRepository.findDistinctFirstByOrganizationGuid(anyString())).thenReturn(jpaServiceInstance);

        ServiceInstance result = apiGatewayCommonService.findByOrgGuid(TestConstants.ORG_GUID);

        assertThat(result.getServiceDefinitionId(), is(jpaServiceInstance.getServiceId()));
        assertThat(result.getPlanId(), is(jpaServiceInstance.getPlanId()));
        assertThat(result.getOrganizationGuid(), is(jpaServiceInstance.getOrganizationGuid()));
        assertThat(result.getSpaceGuid(), is(jpaServiceInstance.getSpaceGuid()));
        assertThat(result.getServiceInstanceId(), is(jpaServiceInstance.getServiceInstanceId()));
    }

    /**
     * Find by org guid test verify return is null.
     */
    @Test
    public void findByOrgGuidTest_VerifyReturnIsNull() {

        when(jpaServiceInstanceRepository.findDistinctFirstByOrganizationGuid(anyString())).thenReturn(null);

        ServiceInstance result = apiGatewayCommonService.findByOrgGuid(TestConstants.ORG_GUID);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void serviceAssignmentTest_VerifyAssignVMIsNull() throws ServiceException {

        when(jpaDedicatedVMRepository.findDistinctFirstByAssignmentEquals(TestConstants.STATUS_WATING_FOR_ASSIGNMENT)).thenReturn(null);
        assertThatThrownBy(() -> apiGatewayCommonService.serviceAssignment(createServiceInstanceRequest))
                .isInstanceOf(ServiceException.class).hasMessageContaining("Cannot assign VM");
    }

    /**
     * Create service instance test.
     */
    @Test
    public void createServiceInstanceTest() {

        apiGatewayCommonService.createServiceInstance(serviceInstance);

        verify(jpaServiceInstanceRepository, times(1)).save(any(JpaServiceInstance.class));
    }

    /**
     * Proc de provisioning test.
     *
     * @throws Exception the exception
     */
    @Test
    public void procDeProvisioningTest() throws Exception {

        doNothing().when(jpaServiceInstanceRepository).delete(anyString());
        when(jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(anyString())).thenReturn(jpaDedicatedVM);
        when(boshDirector.updateInstanceState(TestConstants.DEPLOYMENT_NAME, jpaDedicatedVM.getVmName(), jpaDedicatedVM.getVmId(), TestConstants.JOB_STATE_RECREATE)).thenReturn(true);

        apiGatewayCommonService.procDeProvisioning(TestConstants.SV_INSTANCE_ID);
    }

    /**
     * Proc de provisioning test verify recreate vm case 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void procDeProvisioningTest_VerifyRecreateVM_Case1() throws Exception {

        doNothing().when(jpaServiceInstanceRepository).delete(anyString());
        when(jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(anyString())).thenReturn(jpaDedicatedVM);
        when(boshDirector.updateInstanceState(TestConstants.DEPLOYMENT_NAME, jpaDedicatedVM.getVmName(), jpaDedicatedVM.getVmId(), TestConstants.JOB_STATE_RECREATE)).thenReturn(false);

        assertThatThrownBy(() -> apiGatewayCommonService.procDeProvisioning(TestConstants.SV_INSTANCE_ID))
                .isInstanceOf(ServiceException.class).hasMessageContaining("Failed to recreate dedecated VM");
    }

    /**
     * Proc de provisioning test verify recreate vm case 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void procDeProvisioningTest_VerifyRecreateVM_Case2() throws Exception {

        doNothing().when(jpaServiceInstanceRepository).delete(anyString());
        when(jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(anyString())).thenReturn(jpaDedicatedVM);
        when(boshDirector.updateInstanceState(TestConstants.DEPLOYMENT_NAME, jpaDedicatedVM.getVmName(), jpaDedicatedVM.getVmId(), TestConstants.JOB_STATE_RECREATE)).thenThrow(Exception.class);

        assertThatThrownBy(() -> apiGatewayCommonService.procDeProvisioning(TestConstants.SV_INSTANCE_ID))
                .isInstanceOf(ServiceException.class).hasMessageContaining("Failed to recreate dedecated VM");
    }

    /**
     * Deprovision vm test verify dedicated vm.
     *
     * @throws Exception the exception
     */
    @Test
    public void deprovisionVMTest_VerifyDedicatedVM() throws Exception {

        when(jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(anyString())).thenReturn(jpaDedicatedVM);

        JpaDedicatedVM result = apiGatewayCommonService.deprovisionVM(TestConstants.SV_INSTANCE_ID);

        assertThat(result.getAssignment(), is(TestConstants.STATUS_WATING_FOR_VM_RECREATE));
        assertThat(result.getProvisionedServiceInstanceId(), is(nullValue()));
        assertThat(result.getProvisionedTime(), is(nullValue()));
        verify(jpaDedicatedVMRepository, times(1)).save(any(JpaDedicatedVM.class));
    }

    /**
     * Deprovision vm test verify dedicated vm is null.
     *
     * @throws Exception the exception
     */
    @Test
    public void deprovisionVMTest_VerifyDedicatedVMIsNull() throws Exception {

        when(jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> apiGatewayCommonService.deprovisionVM(TestConstants.SV_INSTANCE_ID))
                .isInstanceOf(ServiceException.class).hasMessageContaining("Cannot deprovision");
    }

    @Test
    public void jpaDedicatedVMTest() {

        JpaDedicatedVM jpaDedicatedVMTest = new JpaDedicatedVM();

        jpaDedicatedVMTest.setIp(TestConstants.DEDICATED_VM_IP);
        jpaDedicatedVMTest.setVmName(TestConstants.DEDICATED_VM_NAME);
        jpaDedicatedVMTest.setVmId(TestConstants.DEDICATED_VM_ID);
        jpaDedicatedVMTest.setAssignment(TestConstants.STATUS_WATING_FOR_ASSIGNMENT);
        jpaDedicatedVMTest.setDashboardUrl(TestConstants.DASHBOARD_URL);
        jpaDedicatedVMTest.setProvisionedServiceInstanceId(TestConstants.SV_INSTANCE_ID);
        jpaDedicatedVMTest.setProvisionedTime(new Date());
        jpaDedicatedVMTest.setCreatedTime(new Date());

        assertThat(jpaDedicatedVMTest.getIp(), is(TestConstants.DEDICATED_VM_IP));
        assertThat(jpaDedicatedVMTest.getVmName(), is(TestConstants.DEDICATED_VM_NAME));
        assertThat(jpaDedicatedVMTest.getVmId(), is(TestConstants.DEDICATED_VM_ID));
        assertThat(jpaDedicatedVMTest.getAssignment(), is(TestConstants.STATUS_WATING_FOR_ASSIGNMENT));
        assertThat(jpaDedicatedVMTest.getDashboardUrl(), is(TestConstants.DASHBOARD_URL));
        assertThat(jpaDedicatedVMTest.getProvisionedServiceInstanceId(), is(TestConstants.SV_INSTANCE_ID));
        assertThat(jpaDedicatedVMTest.getProvisionedTime(), is(notNullValue()));
        assertThat(jpaDedicatedVMTest.getCreatedTime(), is(notNullValue()));
    }

    @Test
    public void jpaServiceInstanceTest() {

        JpaServiceInstance jpaServiceInstanceTest = new JpaServiceInstance();
        jpaServiceInstanceTest.setServiceInstanceId(serviceInstance.getServiceInstanceId());
        jpaServiceInstanceTest.setServiceId(serviceInstance.getServiceDefinitionId());
        jpaServiceInstanceTest.setPlanId(serviceInstance.getPlanId());
        jpaServiceInstanceTest.setOrganizationGuid(serviceInstance.getOrganizationGuid());
        jpaServiceInstanceTest.setSpaceGuid(serviceInstance.getSpaceGuid());
        jpaServiceInstanceTest.setDashboardUrl(serviceInstance.getDashboardUrl());
        jpaServiceInstanceTest.setCreatedTime(new Date());

        assertThat(jpaServiceInstanceTest.getServiceInstanceId(), is(serviceInstance.getServiceInstanceId()));
        assertThat(jpaServiceInstanceTest.getServiceId(), is(serviceInstance.getServiceDefinitionId()));
        assertThat(jpaServiceInstanceTest.getPlanId(), is(serviceInstance.getPlanId()));
        assertThat(jpaServiceInstanceTest.getOrganizationGuid(), is(serviceInstance.getOrganizationGuid()));
        assertThat(jpaServiceInstanceTest.getSpaceGuid(), is(serviceInstance.getSpaceGuid()));
        assertThat(jpaServiceInstanceTest.getDashboardUrl(), is(serviceInstance.getDashboardUrl()));
        assertThat(jpaServiceInstanceTest.getCreatedTime(), is(notNullValue()));
    }

}



