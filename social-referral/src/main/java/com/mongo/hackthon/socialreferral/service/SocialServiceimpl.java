package com.mongo.hackthon.socialreferral.service;

import com.mongo.hackthon.socialreferral.document.GraphDocument;
import com.mongo.hackthon.socialreferral.document.MutualFriendsGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.NetflixReferredGraphDocumentWrapper;
import com.mongo.hackthon.socialreferral.document.ReferralDocument;
import com.mongo.hackthon.socialreferral.document.UserApplicationDetails;
import com.mongo.hackthon.socialreferral.exception.SocialReferralException;
import com.mongo.hackthon.socialreferral.exception.UserAlreadyExistException;
import com.mongo.hackthon.socialreferral.repository.SocialRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by team stallion on 5/2/2018.
 */
@Service
@Slf4j
public class SocialServiceimpl implements SocialService {


  @Value("${socialreferral.netflix.referral.graph.depth:1}")
  private Integer netflixBasedGraphDepth;

  @Value("${socialreferral.friends.graph.depth:2}")
  private Integer friendsBasedGraphDepth;

  private SocialRepository socialRepository;
  private MongoTemplate mongoTemplate;

  public static final String REFERRAL_COLLECTION = "ReferralCollection";
  public static final String FRIENDS = "friends";
  public static final String NETFLIX_REFERRED_TO = "netflixReferredTo";
  public static final String NAME = "name";

  @Autowired
  public SocialServiceimpl(SocialRepository socialRepository,
      MongoTemplate mongoTemplate) {
    this.socialRepository = socialRepository;
    this.mongoTemplate = mongoTemplate;
  }


  /**
   * This function is responsible for persisting a graph details per user in a new collection. This
   * way it overcomes the traversal of multiple nodes when fetched from frontend giving a immediate
   * response to user. Thus allowing user to see results immediately.
   */
  @Scheduled(cron = "${cron.job.duration}")
  public void createMutualFriendsGraphByName()
      throws SocialReferralException, HttpClientErrorException {

    log.info("SocialServiceimpl:createMutualFriendsGraphByName: Started a batch job");
    BulkOperations bulkOperations = mongoTemplate
        .bulkOps(BulkMode.UNORDERED, MutualFriendsGraphDocumentWrapper.class);

    Flux<ReferralDocument> referralFlux = socialRepository.findAll();
    List<ReferralDocument> referralList = referralFlux.collectList().block();
    referralList.forEach(
        referral -> {
          MutualFriendsGraphDocumentWrapper documentWrapper = persistMutualFriendGraphDetails(
              referral.getName(), friendsBasedGraphDepth);
          Query query = Query.query(Criteria.where("name").is(documentWrapper.getName()));
          bulkOperations.remove(query);
          bulkOperations.insert(documentWrapper);
        });

    bulkOperations.execute();
    log.info("SocialServiceimpl:createMutualFriendsGraphByName: Ended a batch job");
  }

  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public MutualFriendsGraphDocumentWrapper persistMutualFriendGraphDetails(String filterName,
      Integer maxDepth) throws SocialReferralException, HttpClientErrorException {
    GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder()
        .from(REFERRAL_COLLECTION)
        .startWith(FRIENDS)
        .connectFrom(FRIENDS)
        .connectTo(NAME)
        .maxDepth(netflixBasedGraphDepth)
        .restrict(Criteria.where(FRIENDS).is(filterName))
        .as("referralDocuments");

    //Filter all null and empty list from the results
    MatchOperation matchOperation = Aggregation
        .match(new Criteria().orOperator(Criteria.where("referralDocuments.name").exists(true)));

    //Filter netflix referred names and give alias to it
    ProjectionOperation projectionOperation = Aggregation
        .project("name").and("referralDocuments.name").as("mutualFriendsList");

    //Sort all results by id
    SortOperation sortOperation = Aggregation.sort(Direction.ASC, "referralDocuments.id");

    List<GraphDocument> mutualFriendsGraphOutput = mongoTemplate
        .aggregate(Aggregation.newAggregation(
            graphLookupOperation, matchOperation, projectionOperation),
            REFERRAL_COLLECTION, GraphDocument.class).getMappedResults();

    MutualFriendsGraphDocumentWrapper documentWrapper = new MutualFriendsGraphDocumentWrapper();
    documentWrapper.setMutualFriendsGraphDocuments(mutualFriendsGraphOutput);
    documentWrapper.setName(filterName);
    return documentWrapper;
  }

  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public NetflixReferredGraphDocumentWrapper fetchNetflixFollowersGraphDetails(String filterName,
      Integer maxDepth) throws SocialReferralException, HttpClientErrorException {
    GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder()
        .from(REFERRAL_COLLECTION)
        .startWith(NETFLIX_REFERRED_TO)
        .connectFrom(NETFLIX_REFERRED_TO)
        .connectTo(NAME)
        .maxDepth(netflixBasedGraphDepth)
        // Below filter is not helpful to plot a graph
        //.restrict(Criteria.where("netflixReferredBy").regex(".*\\b" + filterName + "\\b.*"))
        .as("referralDocuments");

    //Filter all null and empty list from the results
    MatchOperation matchOperation = Aggregation
        .match(new Criteria().orOperator(Criteria.where("referralDocuments.name").exists(true)));

    //Filter netflix referred names and give alias to it
    ProjectionOperation projectionOperation = Aggregation
        .project("name").and("referralDocuments.name").as("netflixFollowerList");

    //Execute aggregation pipeline (Traversal with graph lookup, eliminate empty results and provide alias)
    // with mongo template
    List<GraphDocument> netflixReferredGraphOutput = mongoTemplate
        .aggregate(
            Aggregation.newAggregation(graphLookupOperation, matchOperation, projectionOperation),
            REFERRAL_COLLECTION, GraphDocument.class).getMappedResults();

    NetflixReferredGraphDocumentWrapper documentWrapper = new NetflixReferredGraphDocumentWrapper();
    documentWrapper.setNetflixReferredGraphDocuments(netflixReferredGraphOutput);
    documentWrapper.setName(filterName);
    return documentWrapper;
  }

  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public MutualFriendsGraphDocumentWrapper fetchMutualFriendsGraphDetails(String filterName,
      Integer maxDepth) throws SocialReferralException, HttpClientErrorException {
    try {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(filterName));
      List<MutualFriendsGraphDocumentWrapper> documentWrapperList = mongoTemplate
          .find(query, MutualFriendsGraphDocumentWrapper.class);
      return documentWrapperList.get(0);
    } catch (HttpClientErrorException hce) {
      if (hce.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new SocialReferralException(HttpStatus.NOT_FOUND, hce.getMessage(), hce);
      }
      throw hce;
    }
  }


  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public Mono<ReferralDocument> signupNewUser(ReferralDocument referralDocument)
      throws HttpClientErrorException {
    return socialRepository.save(referralDocument);
  }

  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")},
      fallbackMethod = "reliable"
  )
  public Flux<ReferralDocument> getUserDetailsByName(String name)
      throws SocialReferralException, HttpClientErrorException {
    log.info("###########NAME-" + name);
    return socialRepository.findByNameStartingWith(name);
  }
//FIND BY EMAILID
  @Override
  @HystrixCommand(
          ignoreExceptions = {
                  IllegalArgumentException.class,
          },
          commandProperties = {
                  @HystrixProperty(name = "fallback.enabled", value = "false"),
                  @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                          value = "60000")}//,
        //  fallbackMethod = "reliable"
  )
  public Flux<ReferralDocument> getLoginDetailsByEmail(String email)
          throws SocialReferralException, HttpClientErrorException {
    log.info("###########email-" + email);
    return socialRepository.findByEmail(email);
  }
  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "true"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public Mono<ReferralDocument> getUserByReferralCode(String referralCode)
      throws SocialReferralException, HttpClientErrorException {
    return socialRepository.findByReferralcode(referralCode);
  }

  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public UserApplicationDetails getRefereeDetailsForApplication(String applicationName,
      String referralCode, ReferralDocument referralDocument)
      throws SocialReferralException, HttpClientErrorException {

      List<ReferralDocument> docExist = mongoTemplate
        .find(new Query(Criteria.where("email").is(referralDocument.getEmail())),
            ReferralDocument.class, REFERRAL_COLLECTION);
    if (docExist.size() > 0) {
      throw new UserAlreadyExistException();
    }

    String email = referralDocument.getEmail();
    if (!StringUtils.isEmpty(email)) {
      String[] arr = email.split("@");
      referralDocument.setName(arr[0]);
    }
    //Generate referral code
    referralDocument.setReferralcode(RandomStringUtils.randomAlphanumeric(12).toUpperCase());

    //FETCH REFEREE DETAILS
    Query query = new Query();
    query.addCriteria(Criteria.where("referralcode").is(referralCode));
    query.addCriteria(Criteria.where("subscribedto").regex(".*\\b" + applicationName + "\\b.*"));
    List<ReferralDocument> referralDocuments = mongoTemplate.find(query, ReferralDocument.class,REFERRAL_COLLECTION);
    if (referralDocuments.size() == 0) {
      throw new SocialReferralException(HttpStatus.NOT_FOUND, "Data not found with this search",
          new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    //Update details of referee
    ReferralDocument refereeDocument = referralDocuments.get(0);
    Query refereeQuery= new Query();
    refereeQuery.addCriteria(Criteria.where("id").is(refereeDocument.getId()));
    Update update = new Update();

    //Update friends
    List<String> refereeFriends = refereeDocument.getFriends();
    refereeFriends.add(referralDocument.getName());
    refereeDocument.setFriends(refereeFriends);
    update.set("friends", refereeFriends);
    //Update netflix referred list
    List<String> refereeNetflixReferredList = refereeDocument.getNetflixReferredTo();
    refereeNetflixReferredList.add(referralDocument.getName());
    refereeDocument.setNetflixReferredTo(refereeNetflixReferredList);
    update.set("netflixReferredTo", refereeNetflixReferredList);
    refereeDocument.setNetflixReferralRewards(refereeDocument.getNetflixReferralRewards() + 10);
    update.set("netflixReferralRewards", refereeDocument.getNetflixReferralRewards() + 10);
    int referralCount = refereeNetflixReferredList.size();
    if (referralCount == 1) {
      refereeDocument.setBadgeOccupied("Bronze");
    } else if (referralCount == 2) {
      refereeDocument.setBadgeOccupied("Silver");
    } else if (referralCount == 3) {
      refereeDocument.setBadgeOccupied("Gold");
    } else if (referralCount > 3) {
      refereeDocument.setBadgeOccupied("Platinum");
    } else {
      refereeDocument.setBadgeOccupied("NA");
    }
    update.set("badgeOccupied", refereeDocument.getBadgeOccupied());
    mongoTemplate.findAndModify(refereeQuery, update, ReferralDocument.class,REFERRAL_COLLECTION);

    //Save new user details
    referralDocument.setNetflixReferredBy(refereeDocument.getName());
    List<String> friendsList = new ArrayList<>();
    friendsList.add(refereeDocument.getName());
    referralDocument.setFriends(friendsList);
    referralDocument.setBadgeOccupied("NA");
    referralDocument.setNetflixReferralRewards(10);
    referralDocument.setPrimeReferralRewards(0);
    List<String> subscriptionList = new ArrayList<>();
    subscriptionList.add(applicationName);
    referralDocument.setSubscribedto(subscriptionList);
    mongoTemplate.save(referralDocument,REFERRAL_COLLECTION);

    UserApplicationDetails userApplicationDetails = new UserApplicationDetails();
    userApplicationDetails.setBadgeOccupied(referralDocument.getBadgeOccupied());
    userApplicationDetails.setNetflixReferralRewards(referralDocument.getNetflixReferralRewards());
    userApplicationDetails.setNetflixReferredTo(referralDocument.getNetflixReferredTo());
    userApplicationDetails.setEmail(referralDocument.getEmail());
    return userApplicationDetails;
  }


  @Override
  @HystrixCommand(
      ignoreExceptions = {
          IllegalArgumentException.class,
      },
      commandProperties = {
          @HystrixProperty(name = "fallback.enabled", value = "false"),
          @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
              value = "60000")}
  )
  public String updateRefereeDetails(ReferralDocument referralDocument, String id)
      throws SocialReferralException, HttpClientErrorException {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    Update update = new Update();
    update.set("friends", referralDocument.getFriends());
    update.set("netflixReferredTo", referralDocument.getNetflixReferredTo());
    mongoTemplate.findAndModify(query, update, ReferralDocument.class,REFERRAL_COLLECTION);
    return "Success";
  }

  public String reliable() {
    return "Mongo database is down. Circuit is open";
  }
}