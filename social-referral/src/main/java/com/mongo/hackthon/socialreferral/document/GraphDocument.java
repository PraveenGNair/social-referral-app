package com.mongo.hackthon.socialreferral.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphDocument {

  @Id
  private String id;
  private String name;
  private List<String> netflixFollowerList;
  private List<String> mutualFriendsList;
  private List<ReferralDocument> referralDocuments;
}
