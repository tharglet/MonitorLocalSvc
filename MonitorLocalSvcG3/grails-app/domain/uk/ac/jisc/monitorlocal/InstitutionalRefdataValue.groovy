package uk.ac.jisc.monitorlocal

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders

import javax.persistence.Transient

import org.grails.databinding.BindUsing

import com.k_int.grails.tools.refdata.RefdataValue

class InstitutionalRefdataValue extends RefdataValue {
  @BindUsing({
    obj,source ->
    Org.orgBinder(obj.ownerInstitution, source['ownerInstitution'], true);
  })
  Org ownerInstitution
  
  @Transient
  protected static SpringSecurityService getSpringSecurityService () {
    Holders.applicationContext.getBean("springSecurityService", SpringSecurityService)
  }
  
  def beforeInsert () {
    User currentUser = springSecurityService.currentUser
    
    // Added to prevent null wiping out an explicitly set ownerInstitution when doing person import
    if ( ownerInstitution == null ) {
      ownerInstitution = currentUser?.getUserOrg()
    }
    
    true
  }

  static mapping = {
    RefdataValue.mapping.rehydrate(delegate, owner, thisObject).call()
    
    // Custom
    ownerInstitution cascade: "merge, save-update, lock, refresh, evict, replicate"
  }

  static constraints = {
    // Run the constraints of the superclass,.
    RefdataValue.constraints.rehydrate(delegate, owner, thisObject).call()
    ownerInstitution nullable: true
  }
  
  static lookupBase = {
    or {
      isNull ("ownerInstitution") 
      
      def currentAffiliation =  ((User)springSecurityService?.currentUser)?.getUserOrg()
      if (currentAffiliation) {
        eq ("ownerInstitution",currentAffiliation)
      }
    }
  }
}
