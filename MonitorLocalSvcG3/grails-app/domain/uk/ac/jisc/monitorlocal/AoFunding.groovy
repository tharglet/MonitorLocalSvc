package uk.ac.jisc.monitorlocal

class AoFunding {
  static mappedBy = [
    academicOutput:'funds',
  ]

  Org funder

  AoGrant grant
  String apcCharge

  static constraints = {
    funder nullable: false
    grant nullable: false
    apcCharge nullable: false
  }
}
