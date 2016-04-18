package uk.ac.jisc.monitorlocal

class ContactDetails {

    static mappedBy = [
            person:'personContactDetails',
    ]

    Person person
    Org organisation

    String department
    String jobTitle
    String ldapId
    String emailAddress
    String telephoneNumber

    static constraints = {
    }
}