package uk.ac.jisc.monitorlocal

import java.util.Date;

import javax.persistence.Transient;

import grails.plugin.springsecurity.SpringSecurityService;
import grails.plugins.orm.auditable.Stamp
class Note {
  static auditable = [ignore:['version','lastUpdated','created','lastUpdatedBy','createdBy']]
  static transients = ['springSecurityService']
  
  Component owner
  String typeString
  String note

  @Transient
  SpringSecurityService springSecurityService
  Date created
  User createdBy
  Date lastUpdated
  User lastUpdatedBy
  
 def onSave = {
    def now = new Date()
    created = now
    createdBy =  springSecurityService.currentUser ?: null
    
    // Run the onchange too, passing in the dates so they match.!
    currentUpdateStamps (now)
  }
  
  def onChange = { oldVals, newVals ->
    currentUpdateStamps ()
  }
  
  def currentUpdateStamps(Date changed = new Date()) {
    lastUpdated = changed
    lastUpdatedBy = springSecurityService.currentUser ?: null
  }
  
  static mapping = {
    note column:'note_txt', type:'text'
  }

  static constraints = {
    owner nullable: false
    typeString nullable: true, blank:false
    note nullable: true, blank:false
    createdBy nullable: true
    lastUpdatedBy nullable: true
    created nullable: true
    lastUpdated nullable: true
  }
}
