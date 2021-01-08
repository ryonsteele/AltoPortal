package com.alto.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionHandlingController {

  public static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<ExceptionResponse> resourceConflict(ResourceConflictException ex) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("Conflict");
    response.setErrorMessage(ex.getMessage());
    return new ResponseEntity<ExceptionResponse>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity handleBadRequest(BadRequestException ex) {

    logger.error("Global Handler 400", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(UnAuthorizedException.class)
  public ResponseEntity handleAuthRequest(UnAuthorizedException ex) {

    logger.error("Global Handler 401", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity handleInternalServerError(InternalServerException ex) {

    logger.error("Global Handler 500", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
