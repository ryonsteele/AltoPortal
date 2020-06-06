package com.alto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception class to be created when a HTTP Status 404 in encountered.
 */
@ResponseStatus(value= HttpStatus.UNAUTHORIZED)
public class UnAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param message 
     */
    public  UnAuthorizedException(String message) {
        super(message);
        
    }
}
