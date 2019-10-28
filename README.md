# PAAS-TA-API-GATEWAY-SERVICE-BROKER

PaaS-TA 에서 제공하는 API Gateway 서비스 브로커로 클라우드 컨트롤러와 서비스 브로커간의 v2 서비스 API를 제공합니다.
API Gateway 서비스 브로커가 수행하는 API Gateway 서비스 관리 작업은 다음과 같습니다.
 
- Catalog : API Gateway 서비스 및 서비스 Plan 정보를 조회한다.
- Provisioning : API Gateway 서비스 인스턴스를 할당한다.
- Deprovisioning : API Gateway 서비스 인스턴스의 할당을 해제한다.

[서비스팩 개발 가이드](https://github.com/PaaS-TA/Guide-1.0-Spaghetti-/blob/master/Development-Guide/ServicePack_develope_guide.md)의 API 개발 가이드를 참고하시면 아키텍쳐와 기술, 구현과 개발에 대해 자세히 알 수 있습니다.

# 개발 환경

- JDK 8
- Gradle 4.9
- Spring Boot 1.5.10
- CF Service Broker 2.4.0
- Lombok 1.18.8
- Jacoco 0.8.4
- Gson 2.8.6

# API Gateway 서비스 브로커 API 가이드 

- Catalog
    - Route : `GET /v2/catalog`
    - cURL      
        ```$ curl -H "X-Broker-API-Version: 2.4" http://username:password@broker-url/v2/catalog```
- Provisioning
    - Route : `PUT /v2/service_instances/:instance_id`
    - cURL  
        ```
        $ curl http://username:password@broker-url/v2/service_instances/:instance_id -d '{
          "service_id": "service-guid-here",
          "plan_id": "plan-guid-here",
          "organization_guid": "org-guid-here",
          "space_guid": "space-guid-here",
          "parameters": {
            "parameter1": 1,
            "parameter2": "parameter2"
          }
        }' -X PUT -H "X-Broker-API-Version: 2.4" -H "Content-Type: application/json"
        ```
- Deprovisioning
    - Route : `DELETE /v2/service_instances/:instance_id`
    - cURL  
        ```
        $ curl 'http://username:password@broker-url/v2/service_instances/:instance_id?
          service_id=service-id-here&plan_id=plan-id-here' -X DELETE -H "X-Broker-API-Version: 2.4"
        ```
       
