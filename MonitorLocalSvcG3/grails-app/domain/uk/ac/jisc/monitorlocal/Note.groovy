package uk.ac.jisc.monitorlocal

import grails.plugins.orm.auditable.Stamp

@Stamp
class Note {

  Component owner
  String typeString
  String note
  
  User author

  static mapping = {
    note column:'note_txt', type:'text'
  }

  static constraints = {
    owner nullable: false
    typeString nullable: true, blank:false
    note nullable: true, blank:false
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }
}
