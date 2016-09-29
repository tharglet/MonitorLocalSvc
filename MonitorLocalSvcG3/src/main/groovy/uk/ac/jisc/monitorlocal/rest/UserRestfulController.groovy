package uk.ac.jisc.monitorlocal.rest;

import grails.artefact.Artefact
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.access.prepost.PreAuthorize

import com.k_int.grails.tools.rest.ExtendedRestfulController
@Artefact("Controller")
public class UserRestfulController<User> extends ExtendedRestfulController<User> {
  
  SpringSecurityService springSecurityService
  
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
  @PreAuthorize("#id == 'current' or #id == authentication.id or hasRole('ADMIN')")
  def show() {
    String id = "${params.id}".toLowerCase()
    if (id == "current") {
      current()
    } else {
      super.show()
    }
  }
  
  /**
   * Get the current User information.
   */
  def current () {
    
    def cu = springSecurityService.currentUser
    
    
    respond cu ? resource.get(cu.id) : null
  }
}
