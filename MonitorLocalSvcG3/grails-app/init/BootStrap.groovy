import uk.ac.jisc.monitorlocal.*

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.SecurityFilterPosition


class BootStrap {

  def grailsApplication

  def init = { servletContext ->

    SpringSecurityUtils.clientRegisterFilter('jwtPreauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER)

    log.debug("grailsApplication.config.monitorLocal_jwk : ${grailsApplication.config.monitorLocal_jwk}")
    def role_user = Role.findByAuthority('ROLE_USER') ?: new Role(authority:'ROLE_USER').save(flush:true, failOnError:true)
    def role_verified_user = Role.findByAuthority('ROLE_VERIFIED_USER') ?: new Role(authority:'ROLE_VERIFIED_USER').save(flush:true, failOnError:true)
    
    // Run the rules.
//    AcademicOutput ao = AcademicOutput.list(max:1)?.get(0)
//    def result = rulesService.runRules('workflow', ao)
//    
//    log.debug "${result}"
//    System.out.println("${result}")


  }

  def destroy = {
  }

}
