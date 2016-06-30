// Place your Spring DSL code here
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.web.databinding.DataBindingUtils
import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollectionListener


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

    // Register the binding listener to act on collection properties.
    absoluteCollectionListener (AbsoluteCollectionListener) {
      binder = ref (DataBindingUtils.DATA_BINDER_BEAN_NAME)
    }
}
