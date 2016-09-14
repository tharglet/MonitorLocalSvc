import uk.ac.jisc.monitorlocal.compliance.ComplianceUtils

/**
 * 
 * These are custom rules that only apply to particular funders. We could create each it's own file,
 * but there will only be a small amount of rules defined so we are sharing a file, at least for now.
 * 
 * Compliance rules should return a map, with the following keys.
 * 
 * - result   : Boolean ( True / False / Null ) Representing (Pass / Fail / Needs review )
 * - details  : Map <String, ?> Passed back to the UI. Values vary per rule and are used to populate the templates.
 */

AHRC = {
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(24)
  }
}

BBSRC = {
  "Deposit" ComplianceUtils.depositCheck(
    null,
    'Europe PubMed Central'
  )
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(12)
  }
}

EuropeanCommissionHorizon2020 = {
  "Deposit" ComplianceUtils.depositCheck(
    ["Accepted Manuscript", "Version of Record", "Corrected Version of Record", "Enhanced Version of Record"],
  )
  "Funder acknowledgement" ComplianceUtils.refdataCheck("acknowledgement", "Yes")
  
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(6)
  }
}

EuropeanCommissionFP7 = {
  "Deposit" ComplianceUtils.depositCheck(
    ["Accepted Manuscript", "Version of Record", "Corrected Version of Record", "Enhanced Version of Record"],
  )
  "Embargo" ComplianceUtils.embargoCheck(6)
}

ESRC = {
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(24)
  }
}

EPSRC = {
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(12)
  }
}

MRC = {
  "Deposit" ComplianceUtils.depositCheck(
    null,
    'Europe PubMed Central'
  )
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(6)
  }
}

NERC = {
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(12)
  }
}

STFC = {
  Green = {
    "Embargo" ComplianceUtils.embargoCheck(12)
  }
}