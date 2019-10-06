package com.mongo.hackthon.socialreferral.exception;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class MicroserviceExceptionhandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  @ExceptionHandler(value = {
      SocialReferralException.class})
  public SocialReferralError handleDataNotFoundException(HttpClientErrorException e) {
    return new SocialReferralError(HttpStatus.NOT_FOUND.toString(),
        "Data not found in database.");
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  @ExceptionHandler(value = {
      UserAlreadyExistException.class})
  public SocialReferralError handleAlreadyExistxception(UserAlreadyExistException e) {
    return new SocialReferralError(HttpStatus.CONFLICT.toString(),
        "User already exist in database.");
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  @ExceptionHandler(value = {
      HttpClientErrorException.class,
      Exception.class})
  public SocialReferralError handleGeneralException(Exception e) {
    return new SocialReferralError(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
        "Unexpected error has occurred");
  }

  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ResponseBody
  @ExceptionHandler(value = {
      HttpServerErrorException.class,
      HystrixRuntimeException.class,
      ResourceAccessException.class})
  public SocialReferralError handleDatabaseTimeoutException(Exception e) {
    return new SocialReferralError(HttpStatus.SERVICE_UNAVAILABLE.toString(),
        "Database service is not available");
  }
}
