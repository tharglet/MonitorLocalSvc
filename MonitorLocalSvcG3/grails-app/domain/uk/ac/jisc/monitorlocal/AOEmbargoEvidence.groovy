package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AOEmbargoEvidence {

    static mappedBy = [
            academicOutput:'aoEmbargoEvidence',
    ]

    @Defaults(['Checked by person'])
    RefdataValue embargoEvidenceSource

    @Defaults(['Unknown'])
    RefdataValue embargoEvidenceFinder

    String embargoEvidenceURL

    static constraints = {
        embargoEvidenceSource nullable: true
        embargoEvidenceFinder nullable: true
        embargoEvidenceURL nullable: true
    }

}
