package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AOLicenceEvidence {

    static mappedBy = [
            academicOutput:'aoLicenceEvidence',
    ]

    @Defaults(['Publisher Website', 'Published item', 'Sherpa Romeo'])
    RefdataValue licenceEvidenceSource

    @Defaults(['Unknown'])
    RefdataValue licenceEvidenceFinder

    String licenceEvidenceURL

    static constraints = {
        licenceEvidenceSource nullable: true
        licenceEvidenceFinder nullable: true
        licenceEvidenceURL nullable: true
    }

}
