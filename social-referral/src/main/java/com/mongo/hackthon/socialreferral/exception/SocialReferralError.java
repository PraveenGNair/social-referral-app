package com.mongo.hackthon.socialreferral.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialReferralError {

  public String code;
  public String message;

  public SocialReferralError(String code, String message) {
    this.code = code;
    this.message = message;
  }
}