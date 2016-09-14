package uk.ac.jisc.monitorlocal.rest;

import grails.artefact.Artefact

import com.k_int.grails.tools.rest.ExtendedRestfulController
@Artefact("Controller")
public class AcademicOutputRestfulController<AcademicOutput> extends ExtendedRestfulController<AcademicOutput> {
  
  AcademicOutputRestfulController (Class<AcademicOutput> domainClass) {
      this(domainClass, false)
  }
  
  AcademicOutputRestfulController (Class<AcademicOutput> domainClass, boolean readOnly) {
      super(domainClass, readOnly)
  }

  /**
   * Add check rules with no id.
   */
  def checkRules (String id) {
    
    if (id) {
      // Offload to the original.
      super.checkRules(id)
    }  else {
    
      // Assume compliance.
      AcademicOutput ao = (AcademicOutput)createResource()
      
      // Creates a new instance and binds the data to it.
      respond ao.runRules()
    }
  }
}
