
grails.gorm.default.constraints = {
  // Default everything to nullable but not blank.
  '*' ( nullable: true, blank:false )
//  'required' ( validator: { value, obj ->
//    if (value == null || value == "") return 'shared.required.error'
//  })
//  'alphanumeric' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z|\d]+$/)) return 'shared.alphanumeric.error'
//  })
//  'alphanumericSpace' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z|\d\s]+$/)) return 'shared.alphanumericSpace.error'
//  })
//  'titleText' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z\d\s\-\(\)\:\;\?\,\"\']+$/)) return 'shared.titleText.error'
//  })
//  'number' ( validator: { value, obj ->
//    if (!(value =~ /^(\-)?\d*$/)) return 'shared.number.error'
//  })
}

// AuditLog Plugin config
grails {
  plugin {
    auditLog {
      auditDomainClassName = 'uk.ac.jisc.monitorlocal.AuditTrail'
      
      stampEnabled = true // enable stamping support
      stampAlways = false // always stamp domain classes, regardless of @Stamp or static stampable = true existence
      stampCreatedBy = 'createdBy' // fieldname
      stampLastUpdatedBy = 'lastUpdatedBy' // fieldname
      
      // Add field information added to the Database
      verbose = true
      
      // Log the IDs of  associated objects.
      logIds = true
      
      // Do not log a deletion of each field, just that it was deleted.
      nonVerboseDelete = true

      // Current user.
      actorClosure = { request, session ->
        def name = request.applicationContext.springSecurityService.currentUser?.name
        
        if (!name && request.applicationContext.springSecurityService.principal instanceof String){
         name = request.applicationContext.springSecurityService.principal
        }
        
//        if (SpringSecurityUtils.isSwitched()){
//          SpringSecurityUtils.
//          name = SpringSecurityUtils.switchedUserOriginalUsername +" AS "+ name
//        }
        return name
      }
    }
  }
}


grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->
   println "\nERROR auth failed for user $e.authentication.name: $e.exception.message\n"
}
