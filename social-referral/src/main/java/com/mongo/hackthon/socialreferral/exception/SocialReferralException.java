package com.mongo.hackthon.socialreferral.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SocialReferralException extends RuntimeException {

  public SocialReferralException(HttpStatus code, String message, Exception e) {
    super();
  }
}
