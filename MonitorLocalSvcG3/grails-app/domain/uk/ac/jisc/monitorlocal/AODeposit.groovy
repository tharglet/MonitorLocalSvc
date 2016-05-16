package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AODeposit {
  static belongsTo = [ academicOutput: AcademicOutput ]
  static mappedBy = [
    academicOutput:'deposits',
  ]

  @Defaults(['Author\'s Original', 'Submitted Manuscript Under Review', 'Accepted Manuscript', 'Proof', 'Version of Record', 'Corrected Version of Record', 'Enhanced Version of Record', 'Not Applicable'])
  RefdataValue versionDeposited

  String name
  String url
  Date depositDate

  static constraints = {
    name nullable: true
    url nullable: true
    versionDeposited nullable: true
    depositDate nullable: true
  }

}
