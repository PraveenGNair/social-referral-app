package com.mongo.hackthon.socialreferral.service;

import com.mongo.hackthon.socialreferral.document.MutualFriendsGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.NetflixReferredGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.ReferralDocument;
import com.mongo.hackthon.socialreferral.document.UserApplicationDetails;
import com.mongo.hackthon.socialreferral.exception.SocialReferralException;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by praveen.nair 5/2/2018.
 */
public interface SocialService {

  Flux<ReferralDocument> getUserDetailsByName(String name)
      throws SocialReferralException, HttpClientErrorException;

  Flux<ReferralDocument> getLoginDetailsByEmail(String email)
          throws SocialReferralException, HttpClientErrorException;

  Mono<ReferralDocument> getUserByReferralCode(String referralCode)
      throws SocialReferralException, HttpClientErrorException;

  UserApplicationDetails getRefereeDetailsForApplication(String applicationName,
      String referralCode,ReferralDocument referralDocument) throws SocialReferralException, HttpClientErrorException;

  Mono<ReferralDocument> signupNewUser(ReferralDocument referralDocument)
      throws HttpClientErrorException;

  String updateRefereeDetails(ReferralDocument referralDocument, String id)
      throws SocialReferralException, HttpClientErrorException;

  MutualFriendsGraphDocumentWrapper fetchMutualFriendsGraphDetails(String filterName,
      Integer maxDepth) throws SocialReferralException, HttpClientErrorException;

  NetflixReferredGraphDocumentWrapper fetchNetflixFollowersGraphDetails(String filterName,
      Integer maxDepth) throws SocialReferralException, HttpClientErrorException;
}
