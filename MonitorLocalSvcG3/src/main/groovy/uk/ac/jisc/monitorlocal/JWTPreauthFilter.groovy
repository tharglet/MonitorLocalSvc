package uk.ac.jisc.monitorlocal

import org.springframework.security.core.*;
import org.jose4j.jwt.*
import uk.ac.jisc.monitorlocal.*
import org.springframework.security.core.context.SecurityContextHolder

public class JWTPreauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  def grailsApplication
  def publicKeyService

  def setPublicKeyService(publicKeyService) {
    this.publicKeyService = publicKeyService;
  }


  // @javax.annotation.PostConstruct
  def init() {
    // log.debug("Init");
  }

  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {

    log.debug("JWTPreauthFilter::getPreAuthenticatedPrincipal() -- starting");

    // Result should be the username of the logged in user if present
    def result = null
  
    if ( publicKeyService == null ) {
      log.error("Unable to continue - no public key service");
      return null
    }

    try {
      log.debug("checking auth header");
      def authorization = request.getHeader("Authorization")
      if ( authorization ) {
        log.debug("Got auth header");
        def token = authorization.split(' ')[1];
  
        def payload = publicKeyService.decodeJWT(token)

        log.debug("Got payload ${payload} ${payload?.subject} from ${token}");

        if ( payload?.subject ) {

          log.debug("payload.subject present, attempting to locate user ${payload.subject}")

          log.debug("Got user ${payload.subject} ${payload.subject.class}")
          result = payload.subject


          // Doing this here instead of in the standard filter should enable @secured annotations
          if ( result ) {
            // log.debug("Attempting to locate user ${payload.subject}")
            User.withTransaction() {
              def principal = cesvc.User.findByUsername(payload.subject)
              // log.debug("Got user ${principal}")
              org.springframework.security.core.Authentication auth = new JWTAuthentication(token,payload,principal);
              auth.setAuthenticated(true);
              // log.debug("Setting auth - principal is ${auth.principal}")
              SecurityContextHolder.getContext().setAuthentication(auth);
            }
          }


          // Notes
          // https://github.com/JacobASeverson/smart-notes/blob/master/src/groovy/com/objectpartners/security/MathAuthenticationFilter.groovy
          // https://objectpartners.com/2013/07/11/custom-authentication-with-the-grails-spring-security-core-plugin/
        }
      }
      else {
        // log.debug("No auth header");
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


  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    return ""
  }

}
