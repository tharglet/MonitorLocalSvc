package uk.ac.jisc.monitorlocal
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(excludes=["id"])
class ContactDetails {

  Org organisation

  InstitutionalRefdataValue department
  InstitutionalRefdataValue division
  String jobTitle
  String emailAddress
  String telephoneNumber

  static belongsTo = [Person]

  static constraints = {
    organisation nullable: true
    department nullable: true
    division nullable: true
    jobTitle nullable: true, blank: false
    emailAddress nullable: true, blank: false
    telephoneNumber nullable: true, blank: false
  }
  static mapping = {
    division cascade: 'all'
    department cascade: 'all'
  }
}
