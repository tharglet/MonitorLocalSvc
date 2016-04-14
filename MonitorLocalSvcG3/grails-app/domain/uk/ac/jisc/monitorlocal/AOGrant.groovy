package uk.ac.jisc.monitorlocal


/**
 *  A Grant attached to an AO - May contain between one and all of:
 *  A funder, the fund name and the grant ID 
 */
class AOGrant {
  
  AcademicOutput academicOutput
  Org funder
  String fund
  String grantId

  static constraints = {
    'academicOutput'  (nullable: false, blank:false)
    'funder'          (nullable: true,  blank:false)
    'fund'            (nullable: true,  blank:false)
    'grantId'         (nullable: true,  blank:false)
  }
}
