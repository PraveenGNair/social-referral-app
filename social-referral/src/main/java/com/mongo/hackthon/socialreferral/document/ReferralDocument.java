package com.mongo.hackthon.socialreferral.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "ReferralCollection")
public class ReferralDocument {

  @Id
  private String id;
  private String name;
  private String email;
  private List<String> friends = new ArrayList<>();
  private List<String> subscribedto = new ArrayList<>();
  private String referralcode;
  private String netflixReferredBy;
  private List<String> netflixReferredTo = new ArrayList<>();
  private Integer netflixReferralRewards;
  private String badgeOccupied;
  private String primeReferredBy;
  private List<String> primeReferredTo = new ArrayList<>();
  private Integer primeReferralRewards;
}
