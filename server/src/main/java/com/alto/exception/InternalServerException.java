package com.alto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException{

	private final String enrollmentId;

	public InternalServerException(String message, String enrollmentId) {
		super(message);
		this.enrollmentId = enrollmentId;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}
}
