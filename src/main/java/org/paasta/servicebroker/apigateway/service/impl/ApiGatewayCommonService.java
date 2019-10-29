package org.paasta.servicebroker.apigateway.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.paasta.servicebroker.apigateway.exception.ServiceException;
import org.paasta.servicebroker.apigateway.model.JpaDedicatedVM;
import org.paasta.servicebroker.apigateway.model.JpaServiceInstance;
import org.paasta.servicebroker.apigateway.repository.JpaDedicatedVMRepository;
import org.paasta.servicebroker.apigateway.repository.JpaServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * The type Api gateway common service.
 */
@Slf4j
@Service
public class ApiGatewayCommonService {

    @Value("${bosh.deployment_name}")
    public String deploymentName;
    @Value("${service.admin}")
    public String admin;
    @Value("${service.admin_password}")
    public String adminPassword;
    @Value("${service.service_admin}")
    public String serviceAdmin;

    @Autowired
    BoshDirector boshDirector;
    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;
    @Autowired
    JpaDedicatedVMRepository jpaDedicatedVMRepository;

    /**
     * Gets service instance.
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance
     */
    public ServiceInstance getServiceInstance(String serviceInstanceId) {
        JpaServiceInstance jpaServiceInstance = jpaServiceInstanceRepository.findOne(serviceInstanceId);

        if (jpaServiceInstance != null) {
            return new ServiceInstance(new CreateServiceInstanceRequest(
                    jpaServiceInstance.getServiceId(),
                    jpaServiceInstance.getPlanId(),
                    jpaServiceInstance.getOrganizationGuid(),
                    jpaServiceInstance.getSpaceGuid()
            ).withServiceInstanceId(jpaServiceInstance.getServiceInstanceId())
            ).withDashboardUrl(jpaServiceInstance.getDashboardUrl());
        }

        return null;
    }

    /**
     * Find by org guid service instance.
     *
     * @param orgGuid the org guid
     * @return the service instance
     */
    public ServiceInstance findByOrgGuid(String orgGuid) {
        JpaServiceInstance jpaServiceInstanceOrg = jpaServiceInstanceRepository.findDistinctFirstByOrganizationGuid(orgGuid);

        ServiceInstance serviceInstance = null;

        if ( jpaServiceInstanceOrg != null ) {
            serviceInstance = new ServiceInstance(
                    new CreateServiceInstanceRequest(
                            jpaServiceInstanceOrg.getServiceId(),
                            jpaServiceInstanceOrg.getPlanId(),
                            jpaServiceInstanceOrg.getOrganizationGuid(),
                            jpaServiceInstanceOrg.getSpaceGuid()
                    ).withServiceInstanceId(jpaServiceInstanceOrg.getServiceInstanceId()));
        }

        return serviceInstance;
    }

    /**
     * Service assignment string.
     *
     * @param request the request
     * @return the string
     * @throws ServiceException the service exception
     */
    public String serviceAssignment(CreateServiceInstanceRequest request) throws ServiceException {
        String serviceInstanceId = request.getServiceInstanceId();
        String password = (String)request.getParameters().get(Constants.PARAMETERS_KEY);
        JpaDedicatedVM jpaDedicatedVM = jpaDedicatedVMRepository.findDistinctFirstByAssignmentEquals(Constants.STATUS_WATING_FOR_ASSIGNMENT);

        if (jpaDedicatedVM != null) {
            jpaDedicatedVM.setAssignment(Constants.STATUS_ASSIGNED);
            jpaDedicatedVM.setProvisionedServiceInstanceId(serviceInstanceId);
            jpaDedicatedVMRepository.save(jpaDedicatedVM);
        } else {
            throw new ServiceException("Cannot assign VM. There are no available service VM.");
        }

        // Service Admin User 생성
        try {
            // admin group 정보 조회
            String groupId = getGroups(jpaDedicatedVM.getIp());

            // 사용자 생성 :: service admin
            String userId = createUser(jpaDedicatedVM.getIp(), password);

            // 사용자 (service admin) admin 권한 부여
            regAdmin(jpaDedicatedVM.getIp(), groupId, userId);

        } catch (Exception e) {
            jpaDedicatedVM.setAssignment(Constants.STATUS_WATING_FOR_ASSIGNMENT);
            jpaDedicatedVM.setProvisionedServiceInstanceId(null);
            jpaDedicatedVMRepository.save(jpaDedicatedVM);
            throw e;
        }

        return jpaDedicatedVM.getDashboardUrl();
    }

    /**
     * provisioning.
     *
     * @param serviceInstance the service instance
     */
    public void createServiceInstance(ServiceInstance serviceInstance) {
        JpaServiceInstance jpaServiceInstance = JpaServiceInstance.builder()
                .serviceInstanceId(serviceInstance.getServiceInstanceId())
                .serviceId(serviceInstance.getServiceDefinitionId())
                .planId(serviceInstance.getPlanId())
                .organizationGuid(serviceInstance.getOrganizationGuid())
                .spaceGuid(serviceInstance.getSpaceGuid())
                .dashboardUrl(serviceInstance.getDashboardUrl())
                .build();

        jpaServiceInstanceRepository.save(jpaServiceInstance);
    }

    /**
     * Proc deprovisioning.
     *
     * @param serviceInstanceId the service instance id
     * @throws ServiceException the service exception
     */
    public void procDeProvisioning(String serviceInstanceId) throws ServiceException {

        // Delete service instance data
        jpaServiceInstanceRepository.delete(serviceInstanceId);

        // Deprovision dedicated VM
        JpaDedicatedVM jpaDedicatedVM = deprovisionVM(serviceInstanceId);

        // Call Bosh RecreateVM API
        String vmName = jpaDedicatedVM.getVmName();
        String vmId = jpaDedicatedVM.getVmId();
        try {

            boolean result = boshDirector.updateInstanceState(deploymentName, vmName, vmId, Constants.JOB_STATE_RECREATE);

            if (!result) {
                log.error("##### reCreateVM :: Bosh API ::: deploymentName :: {}, vmName :: {}, vmId :: {} ", deploymentName, vmName, vmId);
                throw new ServiceException("Failed to recreate dedecated VM :: Deployment Name [" + deploymentName + "], VM Name/VM ID [" + vmName+"/"+vmId + "]");
            }

        } catch (Exception e) {
            log.error("Failed to recreate dedecated VM ::" + e);
            throw new ServiceException("Failed to recreate dedecated VM :: " + e.getMessage());
        }

    }

    /**
     * Deprovision vm jpa dedicated vm.
     *
     * @param serviceInstanceId the service instance id
     * @return the jpa dedicated vm
     * @throws ServiceException the service exception
     */
    public JpaDedicatedVM deprovisionVM(String serviceInstanceId) throws ServiceException {
        JpaDedicatedVM jpaDedicatedVM = jpaDedicatedVMRepository.findDistinctFirstByProvisionedServiceInstanceId(serviceInstanceId);

        if (jpaDedicatedVM != null) {
            jpaDedicatedVM.setAssignment(Constants.STATUS_WATING_FOR_VM_RECREATE);
            jpaDedicatedVM.setProvisionedServiceInstanceId(null);
            jpaDedicatedVM.setProvisionedTime(null);
            jpaDedicatedVMRepository.save(jpaDedicatedVM);
            return jpaDedicatedVM;
        } else {
            throw new ServiceException("Cannot deprovision. There are no provisioned VM.");
        }
    }

    // [ Use WSO2 API ]=================================================================================================
    private String getGroups(String url) throws ServiceException {

        // Admin Group 정보 조회
        // https://<VM_IP>:9443/scim2/Groups?filter=displayName+eq+PRIMARY/admin
        String reqUrl = "https://"+ url + Constants.SCIM2_GROUPS+"?filter=displayName+eq+PRIMARY/admin";
        HttpEntity<Object> entity = restCommonHeaders(null);
        String groupId;

        try {
            RestTemplate restTemplate = ignoreSslRestTemplate();

            // 그룹 정보 조회
            ResponseEntity<Map> response = restTemplate.exchange(URLDecoder.decode(reqUrl, "UTF-8"), HttpMethod.GET, entity, Map.class);
            List<Map> resources = (List<Map>) response.getBody().get("Resources");
            groupId = (String) resources.get(0).get("id");

            log.info("retrieve Admin group data :: group id :: {}", groupId);

        } catch (Exception e) {
            log.error("Failed to retrieve Admin group data ::" + e);
            throw new ServiceException("Failed to retrieve Admin group data > URL [ "+ reqUrl +"] "+ e.getMessage());
        }

        return groupId;

    }

    private String createUser(String url, String password) throws ServiceException {

        // 사용자 생성
        // https://<VM_IP>:9443/scim2/Users
        String reqUrl = "https://"+ url + Constants.SCIM2_USERS;

        Gson gson = new Gson();
        JsonObject user =  new JsonObject();
        user.addProperty("userName", serviceAdmin);
        user.addProperty("password", password);

        String param = gson.toJson(user);
        HttpEntity<Object> entity = restCommonHeaders(param);

        String userId;

        try {
            RestTemplate restTemplate = ignoreSslRestTemplate();
            Map response = restTemplate.exchange(reqUrl, HttpMethod.POST, entity, Map.class).getBody();
            userId = (String) response.get("id");
            log.info("create service admin :: user id :: {}", userId);
        } catch (Exception e) {
            log.error("Failed to create service admin ::" + e);
            throw new ServiceException("Failed to create service admin > URL [ "+ reqUrl +"] "+ e.getMessage());
        }

        return userId;
    }

    private void regAdmin(String url, String groupId, String userId) throws ServiceException {
        // 사용자 (service admin) admin 권한 부여
        // https://<VM_IP>:9443/scim2/Groups/<ADMIN_GROUP_ID>
        String reqUrl = "https://"+ url + Constants.SCIM2_GROUPS + "/" + groupId;

        Gson gson = new Gson();
        JsonObject operations =  new JsonObject();
        JsonObject members =  new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("display", serviceAdmin);
        jsonObject.addProperty("value", userId);
        jsonArray.add(jsonObject);
        members.add("members", jsonArray);

        jsonObject = new JsonObject();
        jsonObject.addProperty("op", "add");
        jsonObject.add("value", members);
        jsonArray = new JsonArray();
        jsonArray.add(jsonObject);
        operations.add("Operations", jsonArray);

        String param = gson.toJson(operations);
        HttpEntity<Object> entity = restCommonHeaders(param);

        try {
            RestTemplate restTemplate = ignoreSslRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.PATCH, entity, String.class);
            log.info("register admin group :: user id :: {} :: group id :: {} :: response :: {} ", userId, groupId, response);
        } catch (Exception e) {
            log.error("Failed to register admin group ::" + e);

            // error 발생 시 생성했던 사용자 정보 삭제
            deleteUser(url, userId);
            throw new ServiceException("Failed to register admin group > URL [ "+ reqUrl +"] "+ e.getMessage());
        }

    }

    private void deleteUser(String url, String userId) throws ServiceException {

        // 사용자 삭제
        // https://<VM_IP>:9443/scim2/Users/<USER_ID>
        String reqUrl = "https://"+ url + Constants.SCIM2_USERS + "/" + userId;
        HttpEntity<Object> entity = restCommonHeaders(null);

        try {
            RestTemplate restTemplate = ignoreSslRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.DELETE, entity, String.class);
            log.info("delete user :: user id :: {} :: response :: {}", userId, response);
        } catch (Exception e) {
            log.error("Failed to delete service admin ::" + e);
            throw new ServiceException("Failed to delete service admin > URL [ "+ reqUrl +"] "+ e.getMessage());
        }
    }

    private RestTemplate ignoreSslRestTemplate() throws ServiceException {

        RestTemplate restTemplate;

        try {
            TrustStrategy trustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslConnectionSocketFactory)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();
            BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).setConnectionManager(connectionManager).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            restTemplate = new RestTemplate(requestFactory);

        } catch (GeneralSecurityException e) {
            log.error("The Operation Failed :: ignoreSslRestTemplate ::" + e);
            throw new ServiceException("The Operation Failed :: ignoreSslRestTemplate - GeneralSecurityException  :: " + e.getMessage());
        } catch (Exception e) {
            log.error("The Operation Failed :: ignoreSslRestTemplate ::" + e);
            throw new ServiceException("The Operation Failed :: ignoreSslRestTemplate - Exception  :: " + e.getMessage());
        }

        return restTemplate;

    }

    private HttpEntity<Object> restCommonHeaders(Object param) {

        String basicAuth = "Basic " + (Base64.getEncoder().encodeToString((admin + ":" + adminPassword).getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", basicAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> entity = param == null ? new HttpEntity<>(headers): new HttpEntity<>(param, headers);

        return entity;
    }
}
