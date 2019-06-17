package org.paasta.servicebroker.apigateway.exception;

import org.openpaas.servicebroker.exception.ServiceBrokerException;

/**
 * The type Service exception.
 */
public class ServiceException extends ServiceBrokerException {

    private static final long serialVersionUID = 426023620291455818L;

    /**
     * Instantiates a new Service exception.
     *
     * @param message the message
     */
    public ServiceException(String message) {
        super(message);
    }

}
