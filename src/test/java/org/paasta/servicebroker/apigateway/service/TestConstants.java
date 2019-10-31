package org.paasta.servicebroker.apigateway.service;

import java.util.ArrayList;
import java.util.List;


/**
 * The type TestConstants.
 */
public class TestConstants {

    public static final String SERVICES_ID = "test_services_id";
    public static final String SERVICES_NAME = "test_services_name";
    public static final String SERVICES_DESCRIPTION = "test_services_description";
    public static final boolean SERVICES_BINDABLE = false;
    public static final List<String> SERVICES_TAGS = new ArrayList<>();
    public static final String SERVICES_METADATA_DISPLAYNAME = "test_services_metadata_displayName";
    public static final String SERVICES_METADATA_IMAGEURL = "test_services_metadata_imageUrl";
    public static final String SERVICES_METADATA_LONGDESCRIPTION = "test_services_metadata_longDescription";
    public static final String SERVICES_METADATA_PROVIDERDISPLAYNAME = "test_services_metadata_providerDisplayName";
    public static final String SERVICES_METADATA_DOCUMENTATIONURL = "test_services_metadata_documentationUrl";
    public static final String SERVICES_METADATA_SUPPORTURL = "test_services_metadata_supportUrl";
    public static final List<String> SERVICES_REQUIRES = new ArrayList<>();
    public static final boolean SERVICES_PLAN_UPDATABLE = false;
    public static final String SERVICES_PLANS_ID = "test_services_plans_id";
    public static final String SERVICES_PLANS_NAME = "test_services_plans_name";
    public static final String SERVICES_PLANS_DESCRIPTION = "test_services_plans_description";
    public static final boolean SERVICES_PLANS_FREE = true;
    public static final String DASHBOARDCLIENT_ID = "test_dashboardClient_id";
    public static final String DASHBOARDCLIENT_SECRET = "test_dashboardClient_secret";
    public static final String DASHBOARDCLIENT_REDIRECTURI = "test_dashboardClient_redirectUri";

    public static final String ORG_GUID = "test_org_guid";
    public static final String SPACE_GUID = "test_space_guid";
    public static final String SV_INSTANCE_ID = "test_sv_instance_id";
    public static final String APP_GUID = "test_app_guid";
    public static final String BIND_GUID = "test_bind_guid";
    public static final String DASHBOARD_URL = "https://test-dashboard.com";

    public static final String DEDICATED_VM_NAME = "test_dedicated_vm_name";
    public static final String DEDICATED_VM_ID = "test_dedicated_vm_id";
    public static final String DEDICATED_VM_IP = "test_dedicated_vm_ip";
    public static final int STATUS_WATING_FOR_ASSIGNMENT = 0;
    public static final String JOB_STATE_RECREATE = "recreate";
    public static final int STATUS_WATING_FOR_VM_RECREATE = 1;

    public static final String PARAMETERS_KEY = "password";
    public static final String VAILD_PARAMETER_VALUE = "Test12";

    public static final String DEPLOYMENT_NAME = "test_deployment_name";
    public static final String SERVICE_ADMIN = "serviceadmin";
    public static final String ADMIN = "admin";
    public static final String ADMIN_PASSWORD = "admin_password";
    public static final String SCIM2_GROUPS = ":9443/scim2/Groups";
    public static final String SCIM2_USERS = ":9443/scim2/Users";
    public static final String USER_GUID = "test_user_guid";
    public static final String ADMIN_GROUP_GUID = "test_admin_group_id";
}
