package uk.ac.jisc.monitorlocal

import groovy.util.logging.Log4j

import org.jose4j.jwt.*
import org.springframework.security.core.*
import org.springframework.security.core.context.SecurityContextHolder

// http://www.learningthegoodstuff.com/2014/12/spring-security-pre-authentication-and.html
// http://blog.serindu.com/2011/05/26/grails-spring-security-using-preauthenticated-authentication-provider/
// https://github.com/kl4n4/grails-spring-security-webservice/blob/master/SpringSecurityWebserviceGrailsPlugin.groovy


@Log4j
public class JWTPreauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  def grailsApplication
  def publicKeyService

  def setPublicKeyService(publicKeyService) {
    this.publicKeyService = publicKeyService;
  }


  // @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  @Override
  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {

    // log.debug("JWTPreauthFilter::getPreAuthenticatedPrincipal() -- starting");

    // Result should be the username of the logged in user if present
    def result = null
  
    if ( publicKeyService == null ) {
      log.error("Unable to continue - no public key service");
      return null
    }

    try {
      // log.debug("checking auth header");
      def authorization = request.getHeader("Authorization")
      if ( authorization ) {
        // log.debug("Got auth header");
        def token = authorization.split(' ')[1];
  
        def payload = publicKeyService.decodeJWT(token)

        // log.debug("Got payload ${payload} ${payload?.subject} from ${token}");

        if ( payload?.subject ) {

          // log.debug("payload.subject present, attempting to locate user ${payload.subject}")

          // log.debug("Got user ${payload.subject} ${payload.subject.class}")
          result = payload.subject


          // Doing this here instead of in the standard filter should enable @secured annotations
          if ( payload.subject ) {
            // log.debug("Attempting to locate user ${payload.subject}")
            User.withTransaction() {
              def principal = User.findByUsername(payload.subject)
              // log.debug("Got user ${principal}")
              org.springframework.security.core.Authentication auth = new JWTAuthentication(token,payload,principal);
              result = auth

              // log.debug("Auth authorities -- ${auth.getAuthorities()}");

              //auth.setAuthenticated(true);
              //log.debug("Setting auth - principal is ${auth.principal}")
              //SecurityContextHolder.getContext().setAuthentication(auth);
              // result = principal
            }
          }


          // Notes
          // https://github.com/JacobASeverson/smart-notes/blob/master/src/groovy/com/objectpartners/security/MathAuthenticationFilter.groovy
          // https://objectpartners.com/2013/07/11/custom-authentication-with-the-grails-spring-security-core-plugin/
        }
      }
      else {
        log.debug("No auth header present in request");
      }
    }
    catch ( Exception e ) {
      log.error("Problem in JWTPreauthFilter::getPreAuthenticatedCredentials",e);
    }
    finally {
      // log.debug("getPreAuthenticatedPrincipal completing");
    }

    result
  }


  @Override
  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    log.debug("getPreAuthenticatedCredentials....");
    return ""
  }

}
