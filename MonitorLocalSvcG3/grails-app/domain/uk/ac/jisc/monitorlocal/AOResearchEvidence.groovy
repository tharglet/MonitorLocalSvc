package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AOResearchEvidence {

    static mappedBy = [
            academicOutput:'aoResearchEvidence',
    ]

    @Defaults(['Checked by person'])
    RefdataValue researchEvidenceSource

    @Defaults(['Unknown'])
    RefdataValue researchEvidenceFinder

    String researchEvidenceURL

    static constraints = {
        researchEvidenceSource nullable: true
        researchEvidenceFinder nullable: true
        researchEvidenceURL nullable: true
    }

}
