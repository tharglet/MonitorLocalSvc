package uk.ac.jisc.monitorlocal

class AoFunding {
  
  static belongsTo = ['academicOutput', AcademicOutput]
  static mappedBy = [
    academicOutput:'funds',
  ]

  AoGrant grant
  String apcCharge

  static constraints = {
    grant nullable: false
    apcCharge nullable: false
  }
}
