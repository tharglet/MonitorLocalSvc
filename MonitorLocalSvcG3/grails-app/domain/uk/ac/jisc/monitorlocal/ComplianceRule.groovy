package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class ComplianceRule {

  AcademicOutput academicOutput
  
  @Defaults(['For Review', 'Pass', 'Fail'])
  RefdataValue status

  @Defaults(['Publisher Website', 'Published item', 'Sherpa Romeo', 'Checked by person'])
  RefdataValue evidenceSource

  @Defaults(['Unknown'])
  RefdataValue evidenceFinder

  String evidenceURL

  static belongsTo = [ academicOutput: AcademicOutput ]
  
  static constraints = {
    
    academicOutput    nullable: false
    status            nullable: true
    evidenceSource    nullable: true
    evidenceFinder    nullable: true
    evidenceURL       nullable: true
  }
}
