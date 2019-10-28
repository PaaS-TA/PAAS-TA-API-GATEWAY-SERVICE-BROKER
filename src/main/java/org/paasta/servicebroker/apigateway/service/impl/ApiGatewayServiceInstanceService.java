package org.paasta.servicebroker.apigateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceExistsException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.openpaas.servicebroker.model.UpdateServiceInstanceRequest;
import org.openpaas.servicebroker.service.ServiceInstanceService;
import org.paasta.servicebroker.apigateway.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Api gateway service instance service.
 */
@Slf4j
@Service
public class ApiGatewayServiceInstanceService implements ServiceInstanceService {

    @Autowired
    private final ApiGatewayCommonService apiGatewayCommonService;

    /**
     * Instantiates a new Api gateway service instance service.
     *
     * @param apiGatewayCommonService the api gateway common service
     */
    @Autowired
    public ApiGatewayServiceInstanceService(ApiGatewayCommonService apiGatewayCommonService) {
        this.apiGatewayCommonService = apiGatewayCommonService;
    }

    @Override
    public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request)
            throws ServiceInstanceExistsException, ServiceBrokerException {

        log.debug("ApiGatewayServiceInstanceService : Provision (Create) createServiceInstance");

        // [ 유효성 체크 ]=================================================================================================
        // 파라미터 필수 입력 체크 (비밀번호)
        if (request.getParameters() == null || request.getParameters().isEmpty() || !request.getParameters().containsKey(Constants.PARAMETERS_KEY_PASSWORD)) {
            throw new ServiceBrokerException("Required [" + Constants.PARAMETERS_KEY_PASSWORD + "] parameter.");
        }

        // 비밀번호 유효성 체크 패턴 :: 영문 대문자 + 영문 소문자 + 숫자 또는 특수문자($@!%*#?&)
        Pattern patternPassword = Pattern.compile("^((?=.*[A-Z])(?=.*[a-z])(?=.*\\d)|(?=.*[A-Z])(?=.*[a-z])(?=.*[$@!%*#?&]))[A-Za-z\\d$@!%*#?&]{6,30}$");

        // 사용자 암호 :: 파라미터 입력값 유효성 체크
        Matcher matcher = patternPassword.matcher((String) request.getParameters().get(Constants.PARAMETERS_KEY_PASSWORD));
        if (!matcher.matches()) {
            throw new ServiceBrokerException("password does not meet the requirements.[use letters(mix uppercase and lowercase letters) and numbers(or special characters($@!%*#?&), use 6-30 characters.]");
        }

        // 서비스 인스턴스 Guid Check
        ServiceInstance serviceInstance = apiGatewayCommonService.getServiceInstance(request.getServiceInstanceId());

        if (serviceInstance != null) {
            log.error("ServiceInstance : {} is exist.", request.getServiceInstanceId());
            throw new ServiceInstanceExistsException(new ServiceInstance(request));
        }

        // 조직 Guid Check (방침 : space 구분 없이 조직별 1개만 생성)
        ServiceInstance serviceInstanceOrg = apiGatewayCommonService.findByOrgGuid(request.getOrganizationGuid());
        if (serviceInstanceOrg != null) {
            log.error("ServiceInstance already exists in your organization: OrgGuid : {}, spaceId : {}", request.getOrganizationGuid(), serviceInstanceOrg.getSpaceGuid());
            throw new ServiceBrokerException("ServiceInstance already exists in your organization.");
        }

        // [ Dedicated Service 할당 ]=================================================================================================
        // Dedicated Service VM 할당
        String service_url = apiGatewayCommonService.serviceAssignment(request);

        // 서비스 인스턴스 정보 저장
        serviceInstance = new ServiceInstance(request).withDashboardUrl(service_url);
        apiGatewayCommonService.createServiceInstance(serviceInstance);

        return serviceInstance;
    }

    @Override
    public ServiceInstance getServiceInstance(String id) {
        return apiGatewayCommonService.getServiceInstance(id);
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceException {

        log.debug("ApiGatewayServiceInstanceService : Deprovision (Delete) deleteServiceInstance");

        // ServiceInstanceId로 ServiceInstance 정보 조회
        ServiceInstance serviceInstance = apiGatewayCommonService.getServiceInstance(request.getServiceInstanceId());

        if (serviceInstance == null) {
            return null;
        }

        // Deprovisioning 처리
        apiGatewayCommonService.procDeProvisioning(request.getServiceInstanceId());

        return serviceInstance;
    }

    @Override
    public ServiceInstance updateServiceInstance(UpdateServiceInstanceRequest request) throws ServiceBrokerException {
        log.debug("ApiGatewayServiceInstanceService : Update Provision (Update) updateServiceInstance :: Not Supported");
        throw new ServiceBrokerException("Not Supported");
    }

}
