package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AOPeople {

    static mappedBy = [
            academicOutput:'aoPeople',
    ]

    AcademicOutput academicOutput
    Org organisation
    Person person
    String emailAddress
    String telephoneNumber
    Role role

    static constraints = {
        'academicOutput'  (nullable: false)
        'organisation'    (nullable: true)
        'person'          (nullable: true)
        'emailAddress'    (nullable: true)
        'telephoneNumber' (nullable: true)
        'role'            (nullable: true)
    }

    def beforeValidate () {
        person.personContactDetails?.each() {
            if (it.organisation == organisation) {
                emailAddress = it.emailAddress
                telephoneNumber = it.telephoneNumber

            }
        }
    }
}
