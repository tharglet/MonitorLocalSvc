package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AODeposit {

    static mappedBy = [
            academicOutput:'aoDeposits',
    ]

    @Defaults(['Author\'s Original', 'Submitted Manuscript Under Review', 'Accepted Manuscript', 'Proof', 'Version of Record', 'Corrected Version of Record', 'Enhanced Version of Record', 'Unknown'])
    RefdataValue versionDeposited

    String repositoryName
    String repositoryURL
    Date depositDate

    static constraints = {
        repositoryName nullable: true
        repositoryURL nullable: true
        versionDeposited nullable: true
        depositDate nullable: true
    }

}
