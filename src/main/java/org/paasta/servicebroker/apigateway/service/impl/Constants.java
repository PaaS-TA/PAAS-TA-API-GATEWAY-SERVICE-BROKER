package org.paasta.servicebroker.apigateway.service.impl;

/**
 * The type Constants.
 */
public class Constants {

    /** The constant STATUS_WATING_FOR_ASSIGNMENT. */
    public static final int STATUS_WATING_FOR_ASSIGNMENT = 0;
    /** The constant STATUS_WATING_FOR_VM_RECREATE. */
    public static final int STATUS_WATING_FOR_VM_RECREATE = 1;
    /** The constant STATUS_ASSIGNED. */
    public static final int STATUS_ASSIGNED = 2;
    /** The constant JOB_STATE_RECREATE. */
    public static final String JOB_STATE_RECREATE = "recreate";
    /** The constant PARAMETERS_KEY. */
    public static final String PARAMETERS_KEY = "password";
    /** The constant SCIM2_USERS API URL. */
    public static final String SCIM2_USERS = ":9443/scim2/Users";
    /** The constant SCIM2_GROUPS API URL. */
    public static final String SCIM2_GROUPS = ":9443/scim2/Groups";

}
