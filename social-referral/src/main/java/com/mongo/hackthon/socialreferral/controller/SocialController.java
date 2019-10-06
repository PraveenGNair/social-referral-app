package com.mongo.hackthon.socialreferral.controller;

import com.mongo.hackthon.socialreferral.document.MutualFriendsGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.NetflixReferredGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.ReferralDocument;
import com.mongo.hackthon.socialreferral.document.UserApplicationDetails;
import com.mongo.hackthon.socialreferral.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by praveen.nair on 5/2/2018.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/rest/v1")
public class SocialController {

  @Value("${socialreferral.netflix.referral.graph.depth:1}")
  private Integer netflixBasedGraphDepth;
  @Value("${socialreferral.friends.graph.depth:2}")
  private Integer friendsBasedGraphDepth;
  private SocialService socialService;

  @Autowired
  public SocialController(SocialService socialService) {
    this.socialService = socialService;
  }

  /**
   * As soon as user signpup for this app his details with friends gets updated in mongo db.
   *
   * @param referralDocument new user details in this document.
   * @return returns the updated details of user.
   */
  @PostMapping("/referrals/signup")
  public Mono<ReferralDocument> signupNewUser(@RequestBody ReferralDocument referralDocument) {
    return socialService.signupNewUser(referralDocument);
  }

  /**
   * Fetch user details by his/her name.
   *
   * @param name Name of user whose details need to be fetched.
   * @return Returns details of user.
   */
  @GetMapping("/referrals/user/{name}")
  public Flux<ReferralDocument> getUserDetailsByName(@PathVariable("name") String name) {
    return socialService.getUserDetailsByName(name);
  }

  /**
   * Fetch user details by his/her email.
   *
   * @param email Name of user whose details need to be fetched.
   * @return Returns details of user.
   */
  @GetMapping("/referrals/login/{email}/")
  public Flux<ReferralDocument> getLoginDetailsByEmail(@PathVariable("email") String email) {
    return socialService.getLoginDetailsByEmail(email);
  }

  /**
   * This method allows to fetch the user data based on referral code provided in uri.
   *
   * @param referralCode Referral code of a user.
   * @return a user specific details to consumer.
   */
  @GetMapping("/referrals/{referralcode}")
  public Mono<ReferralDocument> getUserDetailsByReferralCode(
      @PathVariable("referralcode") String referralCode) {
    return socialService.getUserByReferralCode(referralCode);
  }

  /**
   * This method allows to fetch the user data based on referral code provided in uri.
   *
   * @param referralCode Referral code of a user.
   * @return a user specific details to consumer.
   */
  @PostMapping("/promotion/{applicationName}/{referralCode}")
  public UserApplicationDetails getRefereeDetailsForApplication(
      @PathVariable("applicationName") String applicationName,
      @PathVariable("referralCode") String referralCode,
      @RequestBody ReferralDocument referralDocument) {

    return socialService
        .getRefereeDetailsForApplication(applicationName, referralCode, referralDocument);
  }

  /**
   * This method updates details of user who referred someone else successfully.
   *
   * @param referralDocument updated document of referee.
   * @param id unique id of user.
   * @return status of updates.
   */
  @PostMapping("/referrals/{id}")
  public String updateRefereeDetails(
      @RequestBody ReferralDocument referralDocument,
      @PathVariable("id") String id) {
    return socialService.updateRefereeDetails(referralDocument, id);
  }

  /**
   * This method fetch mutual friends data from graph collection. These graphs information is
   * automatically stored by scheduler using cron jobs for each user.
   *
   * @param name name of person whose graph information needs to be fetched from UI.
   * @return mutual friend graph information.
   */
  @GetMapping("/referrals/{name}/mutualfriendsgraph")
  public MutualFriendsGraphDocumentWrapper fetchMutualFriendsGraphDetails(
      @PathVariable("name") String name) {
    return socialService.fetchMutualFriendsGraphDetails(name, friendsBasedGraphDepth);
  }


  /**
   * This method fetch netflix referred user graph details on runtime.
   *
   * @param name name filter is currently not used due to database limit constraints.
   * @return netflix referred graph information.
   */
  @GetMapping("/referrals/{name}/netflixreferredgraph")
  public NetflixReferredGraphDocumentWrapper fetchNetflixFollowersGraphDetails(
      @PathVariable("name") String name) {
    return socialService.fetchNetflixFollowersGraphDetails(name, netflixBasedGraphDepth);
  }
}