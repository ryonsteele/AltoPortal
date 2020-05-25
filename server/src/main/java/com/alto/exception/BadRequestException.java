package com.alto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

	private final String enrollmentId;

	public BadRequestException(String message, String enrollmentId) {
		super(message);
		this.enrollmentId = enrollmentId;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}
}
