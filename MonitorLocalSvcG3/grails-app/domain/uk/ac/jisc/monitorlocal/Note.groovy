package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.RefdataValue

class Note {
  Component owner
  RefdataValue type
  String secondaryType
  
  static constraints = {
    owner nullable: false
    type nullable: false
    secondaryType nullable: true, blank:false
  }
}
