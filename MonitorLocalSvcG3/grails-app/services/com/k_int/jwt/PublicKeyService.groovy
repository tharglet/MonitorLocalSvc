package com.k_int.jwt


import grails.plugin.springsecurity.annotation.Secured
import org.apache.commons.codec.binary.Base64
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.nio.charset.StandardCharsets
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import org.jose4j.jwk.*
import org.jose4j.jwt.*
import org.jose4j.jwt.consumer.*
import org.jose4j.jws.*
import grails.transaction.Transactional

@Transactional
class PublicKeyService {

  private RsaJsonWebKey thekey = null;
  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  def getAppPublicKey() {
    // See if the app has a public key, if not generate one and store it
    if ( thekey == null ) {
      log.debug("Creating public key");
      // thekey = RsaJwkGenerator.generateJwk(2048);
      // Give the JWK a Key ID (kid), which is just the polite thing to do
      // thekey.setKeyId("k1");

      Map<String, Object> keyparams = grailsApplication.config.monitorLocal_jwk
      // keyparams.n = 'nB-sbsjkNr7AH4hzsH-_3P6UlWlYA0Izy2UgjowJkNQfiaPvILS320FJaHqBQ6r2qP86xssPYqxSIvXS4qxWgxaKkwNx5myYKVNO4aIN6DpnIGmtzcjdVMZYWSVe4dGXwrGJ8dqLxEL6FthVu6DkTkCICpqIPU2gsgRqPPgt61GAu87_3Q8qGjgeb-gm1YfLZdlm_JQqWEPDmiwEiR7PARVPkNByhZISWX9U1CG0LBPRDh9eLnm6hdFdQAgesHqhRMB7UXrNJftCnDKw8Ay_m6wk2Tv86Mwi3jx0RBWm0sVnjVlHVqgGeDk1IMJQaWkcUC5KvfXcSTpn4ZX_IneIuQ'
      // keyparams.e = 'AQAB'
      thekey = new org.jose4j.jwk.RsaJsonWebKey(keyparams)

      //log.debug("Got json key ${thekey.toJson()}")
      //log.debug("Got public key ${thekey.getRsaPublicKey().class.name}")
      //log.debug("Got private key ${thekey.getRsaPrivateKey().class.name}")
      //log.debug("JWK to params ${thekey.toParams(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)}")
      //log.debug("JWK to json ${thekey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)}")
    }

    return thekey;
  }


  def decodeJWT(jwt) {

    JwtClaims result = null

    def rsaJsonWebKey = getAppPublicKey();


    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer("CultureEverywhere")  // who creates the token and signs it
            .setExpectedAudience("CEUsers") // to whom the token is intended to be sent
            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
            .build(); // create the JwtConsumer instance

    try
    {
        //  Validate the JWT and process it to the Claims
        result = jwtConsumer.processToClaims(jwt);
        //log.debug("JWT validation succeeded! " + result);
    }
    catch (InvalidJwtException e)
    {
        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
        // Hopefully with meaningful explanations(s) about what went wrong.
        log.error("Invalid JWT! " + e);
    }

    result
  }


}
