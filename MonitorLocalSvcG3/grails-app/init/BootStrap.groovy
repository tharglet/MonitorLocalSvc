import uk.ac.jisc.monitorlocal.*

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.SecurityFilterPosition


class BootStrap {

  def grailsApplication

  def init = { servletContext ->

    SpringSecurityUtils.clientRegisterFilter('jwtPreauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER)

    log.debug("grailsApplication.config.monitorLocal_jwk : ${grailsApplication.config.monitorLocal_jwk}")

    def role_user = Role.findByAuthority('ROLE_USER') ?: new Role(authority:'ROLE_USER').save(flush:true, failOnError:true)
    def role_ro_user = Role.findByAuthority('ROLE_RO_USER') ?: new Role(authority:'ROLE_RO_USER').save(flush:true, failOnError:true)
    def role_admin_user = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority:'ROLE_ADMIN').save(flush:true, failOnError:true)
    def role_verified_user = Role.findByAuthority('ROLE_VERIFIED_USER') ?: new Role(authority:'ROLE_VERIFIED_USER').save(flush:true, failOnError:true)
    def role_system = Role.findByAuthority('ROLE_SYSTEM') ?: new Role(authority:'ROLE_SYSTEM').save(flush:true, failOnError:true)
    
    // Run the rules.
//    AcademicOutput ao = AcademicOutput.list(max:1)?.get(0)
//    def result = rulesService.runRules('workflow', ao)
//    
//    log.debug "${result}"
//    System.out.println("${result}")

    log.debug("Register User");
    registerUsers();


  }

  def destroy = {
  }

  def registerUsers() {


    def cfg_file = new File("${System.properties['user.home']}/.grails/monitor_users.groovy")
    if ( cfg_file.exists() ) {
      def cfg = cfg_file.text

      def usercfg = new groovy.util.ConfigSlurper().parse(cfg);
      log.debug("Processing additional users:: ${cfg} \n${usercfg} ");
  
      usercfg.sysusers.each { su ->
        log.debug("test ${su.name} ${su.pass} ${su.display} ${su.roles}");
        def user = User.findByUsername(su.name)
        if ( user ) {
          if ( user.password != su.pass ) {
            log.debug("Hard change of user password from config ${user.password} -> ${su.pass}");
            user.password = su.pass;
            user.save(failOnError: true)
          }
          else {
            log.debug("${su.name} present and correct");
          }
        }
        else {
          log.debug("Create user...");
          user = new User(
                          username: su.name,
                          password: su.pass,
                          name: su.display,
                          email: su.email,
                          enabled: true).save(failOnError: true)
        }
  
        log.debug("Add roles for ${su.name}");
        su.roles.each { r ->
          def role = Role.findByAuthority(r)
          if ( ! ( user.authorities.contains(role) ) ) {
            log.debug("  -> adding role ${role}");
            UserRole.create user, role
          }
          else {
            log.debug("  -> ${role} already present");
          }
        }
      }
    }
    else {
      log.warn("No local user config file:: ${System.properties['user.home']}/.grails/monitor_users.groovy");
    }


  }

}
