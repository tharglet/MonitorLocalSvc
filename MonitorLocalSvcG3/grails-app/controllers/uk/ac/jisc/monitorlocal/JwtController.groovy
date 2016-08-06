package uk.ac.jisc.monitorlocal

import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import org.jose4j.jwk.*
import org.jose4j.jwt.*
import org.jose4j.jws.*


/**
 *  NOTICE: This notice must not be removed
 *  This class contains prior art attributed to knowledge integration ltd and distributed as a part of the monitor local
 *  application whilst we wait to discover Jisc plans for OAuth services. Code remains the property of k-int, not Jisc or MonitorLocal
 */
class JwtController {

  def publicKeyService

  def callback() {
    def result = [:]
    String provider = params.provider

    log.debug("CeJwt::callback ${params}")

    def auth_cfg = grailsApplication.config.auth[provider]

    if ( auth_cfg ) {

      log.debug("Request.JSON.code:${request.JSON.code} redirectUri:${request.JSON.redirectUri}");

      def access_params = [
         code: request.JSON.code,
         client_id: request.JSON.clientId,
         redirect_uri: request.JSON.redirectUri,
         grant_type: 'authorization_code'
      ];

      if ( auth_cfg.secret ) {
        // access_params.client_secret = auth_cfg.secret
      }

      // get the URI to hit for exchanging the auth-code for a token.
      def tokenUri = auth_cfg.accessTokenUrl.toURI()

      log.debug("Auth server: ${tokenUri.scheme}://${tokenUri.host}");

      def http = new HTTPBuilder(tokenUri.scheme + "://" + tokenUri.host)
      http.ignoreSSLIssues()

      log.debug("About to call post on ${tokenUri.path} to validate token using access params ${access_params}")

      http.post( 
                 path: tokenUri.path,
                 body:access_params) { resp, json ->

        log.debug("POST to ${tokenUri.path} Success for validation of token ${resp} ${json}")

        def accessToken = json.access_token;

        def authorization_header = request.getHeader('Authorization')

        log.debug("accessToken: ${accessToken}, authorization_header:${authorization_header}");

        // get the URI to hit for obtaining meta-data about the user from the social API.
        def peopleUri = auth_cfg.peopleApiUrl.toURI()

        // Locate a user for...
        def people_api = new HTTPBuilder(peopleUri.scheme + "://" + peopleUri.host)
        people_api.ignoreSSLIssues()

        try {

          log.debug("Fetch the person data via the people URI -- ${peopleUri} api -- ${peopleUri?.scheme}://${peopleUri?.host} auth:${accessToken}")

          people_api.request(GET,groovyx.net.http.ContentType.JSON) { req ->
  
            uri.path = peopleUri.path
            uri.query = auth_cfg.query
            headers.'Authorization' = 'Bearer ' + accessToken
            headers.Accept = 'application/json'
  
            response.success = { r2, j2 ->
  
              log.debug("response: ${r2} ${j2}");
  
              // get hold of the mapping from the social API to the CE user properties, obtain the user reference, which
              // is the unique identifier for the user within the scope of the social provider.
              def userMapping = auth_cfg.userMapping
              def userReference = j2[userMapping.username]
  
              log.debug("Got person data -- need to see if we alreadt have a user matching ${userReference}")
  
              // attempt to locate an existing SocialIdentity for the user.
              def social_identity = SocialIdentity.findByProviderAndReference(provider, userReference)
  
              if ( authorization_header ) {
                log.debug("Request already contains an Authorization header :  ${authorization_header} so we should already have been through this process")
                if ( social_identity ) {
                  log.debug("Got user for that JWT")
                  result.message =  'There is already a Google account that belongs to you'
                  response.status = 409;
                }
                else {
                  
                  log.debug("Process auth header to get token ${token}")
                  def token = authorization_header.split(' ')[1];
                  log.debug("Token = ${token}")
                  def payload = publicKeyService.decodeJWT(token);
                  result.token = createToken(social_identity)
                }
              }
              else {
                log.debug("Request does not contain an Authorization header")
  
                def user;
                if ( social_identity ) {
                  log.debug("Located social identity for ${j2.sub} :: ${social_identity.user}");
                  user = social_identity.user
                  result.token = createToken(social_identity)
                }
                else {
                  // if there is no "user" role, the system is in an invalid state, this should be created as necessary
                  // on bootstrap.
                  log.debug("Create user ${user}")
                  def role_user = Role.findByAuthority('ROLE_USER')

                  if (!role_user) {
                      // no "user" role, log an error and respond with a 500 InternalServerError.
                      log.error('missing role "user"')
                      response.status(500, "invalid system state")
                  } else {
                      log.debug("Creating new user :: Source data ${j2}, user mapping is ${userMapping}");

                      user = new User()
  
                      // copy properties from the social API to the User object.
                      if (userMapping) {
                          userMapping.each{ k, v ->
                              log.debug("Copy user mapping ${k} ${v} ${j2[v]}");
                              user[k] = j2[v]
                          }
                      }
  
                      if ( ( user.username ) && ( user.username.trim().length() > 0 ) ) {
                        // prefix the username with the social provider.
                        user.username = provider + '_' + user.username
                        user.password = java.util.UUID.randomUUID().toString()
                        user.accountExpired=false;
                        user.accountLocked=false;
                        user.passwordExpired=false;
                        // TODO: add created and lastUsed timestamp fields?
                        user.save(flush:true, failOnError:true);
  
                        social_identity = new SocialIdentity(provider: provider,reference:userReference,user:user).save(flush:true, failOnError:true);
  
                        log.debug("Grant user role");
                        def new_grant = new UserRole(role:role_user, user:user).save(flush:true, failOnError:true);
  
                        result.token = createToken(social_identity)
                      }
                      else {
                        // Trying to stop database filling with null users
                        throw new RuntimeException("No username available to create new user - perhaps this is meant to be anonymous?");
                      }
                  }
                }
  
                if ( user ) {
                  // if there is no "ROLE_VERIFIED_USER" role, the system is in an invalid state, this should be created as
                  // necessary on bootstrap.
                  def verified_user = Role.findByAuthority('ROLE_VERIFIED_USER')
                  if (!verified_user) {
                      // no "ROLE_VERIFIED_USER" role, log an error and respond with a 500 InternalServerError.
                      log.error('missing role "ROLE_VERIFIED_USER"')
                      response.status(500, "invalid system state")
                  } else {
                      // create a user object to push down with the JWT to the client.
                      result.user = user
                  }
                }
              }
            }
  
            response.failure = { resp2, reader ->
                log.error("Failure result ${resp2.statusLine}");
                log.error(reader.text)
            }
  
          }
        }
        catch ( Exception e ) {
          log.error("Error",e);
        }

      }
    }
    else {
     log.error("Unable to locate config for provider ${provider}");
    }

    log.debug("JwtController returning...");
    result
  }

  private String createToken(user) {

    log.debug("Request seems to contain a legitimate user - create and sign a token for that user");

    // See https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples

    RsaJsonWebKey rsaJsonWebKey = publicKeyService.getAppPublicKey()
    // log.debug("Got app public key ${rsaJsonWebKey}");
    // RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    // Give the JWK a Key ID (kid), which is just the polite thing to do
    // rsaJsonWebKey.setKeyId("k1");

    // Create the Claims, which will be the content of the JWT
    JwtClaims claims = new JwtClaims();
    claims.setIssuer("MonitorLocal");  // who creates the token and signs it
    claims.setAudience("MonitorLocal"); // to whom the token is intended to be sent
    claims.setExpirationTimeMinutesInTheFuture(60*15); // time when the token will expire (60*15 minutes from now)
    claims.setGeneratedJwtId(); // a unique identifier for the token
    claims.setIssuedAtToNow();  // when the token was issued/created (now)
    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
    claims.setSubject(user.user.username); // the subject/principal is whom the token is about
    // claims.setClaim("email","mail@example.com"); // additional claims/attributes about the subject can be added
    // List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
    // claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    JsonWebSignature jws = new JsonWebSignature();

    // The payload of the JWS is JSON content of the JWT Claims
    jws.setPayload(claims.toJson());

    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey());

    // Set the Key ID (kid) header because it's just the polite thing to do.
    // We only have one key in this example but a using a Key ID helps
    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
    // representation, which is a string consisting of three dot ('.') separated
    // base64url-encoded parts in the form Header.Payload.Signature
    // If you wanted to encrypt it, you can simply set this jwt as the payload
    // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
    String jwt = jws.getCompactSerialization();

    // log.debug("Created jwt : ${jwt}")
    return jwt
  }
}
