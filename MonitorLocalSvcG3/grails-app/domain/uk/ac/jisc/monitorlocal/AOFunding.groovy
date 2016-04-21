package uk.ac.jisc.monitorlocal

class AOFunding {


    static mappedBy = [
            academicOutput:'aoFunds',
    ]

    Org funder

    AOGrant grant
    String apcCharge

    static constraints = {
        funder nullable: false
        grant nullable: false
        apcCharge nullable: false
    }

}
