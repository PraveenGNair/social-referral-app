package com.mongo.hackthon.socialreferral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableScheduling
@EnableCircuitBreaker
public class SocialReferralApplication {

  public static void main(String[] args) {
    SpringApplication.run(SocialReferralApplication.class, args);
  }
}
