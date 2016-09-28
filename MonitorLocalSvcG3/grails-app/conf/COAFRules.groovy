import uk.ac.jisc.monitorlocal.compliance.ComplianceUtils

/**
 * Compliance rules should return a map, with the following keys.
 * 
 * - result   : Boolean ( True / False / Null ) Representing (Pass / Fail / Needs review )
 * - details  : Map <String, ?> Passed back to the UI. Values vary per rule and are used to populate the templates.
 */

"Funder acknowledgement" ComplianceUtils.refdataCheck("acknowledgement", "Yes")
Gold = {
  "Deposit" ComplianceUtils.depositCheck(
    ['Accepted Manuscript', 'Version of Record', 'Corrected Version of Record', 'Enhanced Version of Record'],
    'PubMed Central'
  )
  "Licence" ComplianceUtils.refdataCheck("licence", "CC BY")
}

Green = {
  "Deposit" ComplianceUtils.depositCheck(
    ['Accepted Manuscript', 'Version of Record', 'Corrected Version of Record', 'Enhanced Version of Record'],
    'Europe PubMed Central'
  )
  
  "Embarg" ComplianceUtils.embargoCheck(6)
}