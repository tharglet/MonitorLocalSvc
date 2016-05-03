package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AOAcknowledgementEvidence {

    static mappedBy = [
            academicOutput:'aoAcknowledgementEvidence',
    ]

    @Defaults(['Checked by person'])
    RefdataValue acknowledgementEvidenceSource

    @Defaults(['Unknown'])
    RefdataValue acknowledgementEvidenceFinder

    String acknowledgementEvidenceURL

    static constraints = {
        acknowledgementEvidenceSource nullable: true
        acknowledgementEvidenceFinder nullable: true
        acknowledgementEvidenceURL nullable: true
    }

}
