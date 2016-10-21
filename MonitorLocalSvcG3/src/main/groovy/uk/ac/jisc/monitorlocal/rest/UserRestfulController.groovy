package uk.ac.jisc.monitorlocal.rest;

import static org.springframework.http.HttpStatus.*
import grails.artefact.Artefact
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.GrailsPluginManager

import org.springframework.beans.factory.annotation.Autowired

import com.k_int.grails.tools.rest.ExtendedRestfulController
@Artefact("Controller")
public class UserRestfulController<User> extends ExtendedRestfulController<User> {
  
  UserRestfulController (Class<User> domainClass) {
      this(domainClass, false)
  }
  
  UserRestfulController (Class<User> domainClass, boolean readOnly) {
      super(domainClass, readOnly)
      
      log.debug "Initiating the User controller."
  }

  /**
   * Shows a single resource
   * @param id The id of the resource
   * @return The rendered resource or a 404 if it doesn't exist
   */
  def show() {
    User cu = springSecurityService.currentUser
    if (cu) {
      if (cu.id == params.int('id') || cu.hasRole("ROLE_ADMIN")) {
        super.show()
      } else {
        render (status : org.springframework.http.HttpStatus.FORBIDDEN)
      }
    } else {
      render (status : org.springframework.http.HttpStatus.UNAUTHORIZED)
    }
  }
  
  /**
   * Get the current User information.
   */
  @Secured("hasRole('USER')")
  def current () {
    
    def cu = springSecurityService.currentUser
    
    
    respond cu ? resource.get(cu.id) : null
  }
  
  /**
   * Get the current User information.
   */
  def create () {
    render (status : org.springframework.http.HttpStatus.NOT_FOUND)
  }
}
