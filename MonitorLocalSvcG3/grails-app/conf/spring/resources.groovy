// Place your Spring DSL code here
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.core.Ordered
import grails.plugin.springsecurity.SecurityFilterPosition


beans = {

    // corsFilter(CorsFilter)

    userDetailsService(GormUserDetailsService) {
      grailsApplication = ref('grailsApplication')
    }

    userDetailsByNameServiceWrapper(org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper) {
      userDetailsService = ref('userDetailsService')
    }

    preAuthenticatedAuthenticationProvider(org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider) {
      preAuthenticatedUserDetailsService = ref('userDetailsByNameServiceWrapper')
    }

    authEventHandler(uk.ac.jisc.monitorlocal.AuthEventHandler) {
    }

    jwtPreauthFilter(uk.ac.jisc.monitorlocal.JWTPreauthFilter){
      grailsApplication = ref('grailsApplication')
      authenticationManager = ref('authenticationManager')
      publicKeyService = ref('publicKeyService')
      // authenticationSuccessHandler = ref('authEventHandler')
      // authenticationFailureHandler = ref('authEventHandler')
    }


    // Register our preauth filter
    // jwtPreauthRefistration(FilterRegistrationBean) {
    //         filter = ref('jwtPreauthFilter')
    //         urlPatterns = ['/*']
    //         // order = SecurityFilterPosition.PRE_AUTH_FILTER.order
    //         order = Ordered.HIGHEST_PRECEDENCE + 10
    // }
    
    // Change the authentication entry point so a 401 is sent instead of a redirect.
    authenticationEntryPoint(org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint,  "JWT")

}
