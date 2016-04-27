package uk.ac.jisc.monitorlocal
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(excludes=["id"])
class ContactDetails {

    Org organisation
    Person person

    String department
    String jobTitle
    String emailAddress
    String telephoneNumber
    
    static belongsTo = [Person]
    
    static mappedBy = [
      person: 'personContactDetails'
    ]

    static constraints = {
      organisation nullable: true
      
      department nullable: true, blank: false
      jobTitle nullable: true, blank: false
      emailAddress nullable: true, blank: false
      telephoneNumber nullable: true, blank: false
    }
}
