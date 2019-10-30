package org.paasta.servicebroker.apigateway.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.model.JpaDedicatedVM;
import org.paasta.servicebroker.apigateway.model.JpaRepositoryFixture;
import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.paasta.servicebroker.apigateway.model.RequestFixture;
import org.paasta.servicebroker.apigateway.repository.JpaDedicatedVMRepository;
import org.paasta.servicebroker.apigateway.repository.JpaServiceInstanceRepository;
import org.paasta.servicebroker.apigateway.service.impl.ApiGatewayCommonService;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
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
        when(boshDirector.updateInstanceState(TestConstants.DEPLOYMENT_NAME, jpaDedicatedVM.getVmName(), jpaDedicatedVM.getVmId(), TestConstants.JOB_STATE_RECREATE)).thenReturn(false);

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
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Failed to recreate dedecated VM");
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
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Failed to recreate dedecated VM");
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

        JpaDedicatedVM result = apiGatewayCommonService.deprovisionVM(TestConstants.SV_INSTANCE_ID);

        assertThatThrownBy(() -> apiGatewayCommonService.deprovisionVM(TestConstants.SV_INSTANCE_ID))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Cannot deprovision");
    }

}



