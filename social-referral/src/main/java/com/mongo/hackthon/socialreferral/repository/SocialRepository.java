package com.mongo.hackthon.socialreferral.repository;

import com.mongo.hackthon.socialreferral.document.ReferralDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by praveen.nair on 5/2/2018.
 */
@Repository
public interface SocialRepository extends ReactiveMongoRepository<ReferralDocument, String> {

  Flux<ReferralDocument> findByNameStartingWith(String name);
  Mono<ReferralDocument> findByReferralcode(String referralCode);
  Mono<String> findAllByName();
  Flux<ReferralDocument> findByEmail(String email);
  @Query("{ 'email' : ?0 }")
  Boolean exists(String email);
}
