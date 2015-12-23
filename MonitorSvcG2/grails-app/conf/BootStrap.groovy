import ac.uk.jisc.monitor.*

class BootStrap {

  def grailsApplication

  def init = { servletContext ->

    def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
    def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR').save(failOnError: true)
    def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
    def apiRole = Role.findByAuthority('ROLE_API') ?: new Role(authority: 'ROLE_API').save(failOnError: true)
    def clientRole = Role.findByAuthority('ROLE_CLIENT') ?: new Role(authority: 'ROLE_CLIENT').save(failOnError: true)

    makeDefaultAccounts()

    def monitor_client = OAuthClient.findByClientId('monitorLocal') ?: 
         new OAuthClient(
                   clientId: 'monitorLocal',
                   clientSecret: 'a3802436c3c558ea5725e40756fa7b16',
                   authorizedGrantTypes: ['authorization_code', 'refresh_token', 'implicit', 'password', 'client_credentials'],
                   authorities: ['ROLE_CLIENT'],
                   scopes: ['read','write'],
                   redirectUris: ['http://localhost:8080/MonitorSvcG2/redirect']
       ).save(flush: true)
  }


  def destroy = {
  }

  def makeDefaultAccounts() {

    grailsApplication.config.sysusers.each { su ->
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
                    // display: su.display,
                    // email: su.email,
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
}
