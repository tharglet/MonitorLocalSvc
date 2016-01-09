import uk.ac.jisc.monitorlocal.*;

class BootStrap {

  def grailsApplication

  def init = { servletContext ->
    log.debug("grailsApplication.config.monitorLocal_jwk : ${grailsApplication.config.monitorLocal_jwk}");
    def role_user = Role.findByAuthority('ROLE_USER') ?: new Role(authority:'ROLE_USER').save(flush:true, failOnError:true);
    def role_verified_user = Role.findByAuthority('ROLE_VERIFIED_USER') ?: new Role(authority:'ROLE_VERIFIED_USER').save(flush:true, failOnError:true);
  }

  def destroy = {
  }

}
