package uk.ac.jisc.monitorlocal

import groovy.util.logging.Log4j
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.Authentication


@Log4j
public class AuthEventHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler,
                                         org.springframework.security.web.authentication.AuthenticationFailureHandler

{

  void onAuthenticationSuccess(javax.servlet.http.HttpServletRequest request,
                              javax.servlet.http.HttpServletResponse response,
                              Authentication authentication)
                             throws IOException, javax.servlet.ServletException {
    log.debug("onAuthenticationSuccess(...${authentication}");
  }

  void onAuthenticationFailure(javax.servlet.http.HttpServletRequest request,
                               javax.servlet.http.HttpServletResponse response,
                               AuthenticationException exception)
                             throws IOException, javax.servlet.ServletException {
    log.error("onAuthenticationSuccess(...${authentication}");
  }




}
