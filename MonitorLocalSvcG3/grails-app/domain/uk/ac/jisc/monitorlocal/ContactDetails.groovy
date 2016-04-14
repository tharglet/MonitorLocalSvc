package uk.ac.jisc.monitorlocal

class ContactDetails extends Component{

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
