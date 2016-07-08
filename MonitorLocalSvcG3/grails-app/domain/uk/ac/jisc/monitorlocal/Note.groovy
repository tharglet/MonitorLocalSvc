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
  
 def beforeInsert () {
    def now = new Date()
    if (created == null) {
      created = now
    }
    
    if (createdBy == null && springSecurityService.currentUser != null) {
      
      // Read in the user to overcome any unsaved changes.
      User u = User.read(springSecurityService.currentUser.id)
      if (u) {
        createdBy =  springSecurityService.currentUser
      }
    }
    // Run the onchange too, passing in the dates so they match.!
    beforeUpdate (now)
  }
  
  def beforeUpdate(Date changed = new Date()) {
    lastUpdated = changed
    if (lastUpdatedBy == null && springSecurityService.currentUser != null) {
      // Read in the user to overcome any unsaved changes.
      User u = User.read(springSecurityService.currentUser.id)
      if (u) {
        lastUpdatedBy =  springSecurityService.currentUser
      }
    }
  }
  
  static mapping = {
    note column:'note_txt', type:'text'
  }

  static constraints = {
    owner nullable: false
    typeString nullable: true, blank:false
    note nullable: true, blank:false
    createdBy nullable: true, bindable:false
    lastUpdatedBy nullable: true, bindable:false
    created nullable: true, bindable:false
    lastUpdated nullable: true, bindable:false
  }
}
