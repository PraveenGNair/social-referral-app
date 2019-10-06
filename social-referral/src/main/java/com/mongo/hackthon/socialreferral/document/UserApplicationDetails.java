package com.mongo.hackthon.socialreferral.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "ReferralCollection")
public class UserApplicationDetails {

  private String email;
  private String badgeOccupied;
  private List<String> netflixReferredTo;
  private Integer netflixReferralRewards;
}
