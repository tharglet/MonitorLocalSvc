// Place your Spring DSL code here
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.core.Ordered

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

    jwtPreauthFilter(uk.ac.jisc.monitorlocal.JWTPreauthFilter){
      grailsApplication = ref('grailsApplication')
      authenticationManager = ref('authenticationManager')
      publicKeyService = ref('publicKeyService')
    }


    // Register our preauth filter
    jwtPreauthRefistration(FilterRegistrationBean) {
            filter = ref('jwtPreauthFilter')
            urlPatterns = ['/*']
            order = Ordered.HIGHEST_PRECEDENCE + 10
    }

}
