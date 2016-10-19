import uk.ac.jisc.monitorlocal.compliance.ComplianceUtils

/**
 * Compliance rules should return a map, with the following keys.
 * 
 * - result   : Boolean ( True / False / Null ) Representing (Pass / Fail / Needs review )
 * - details  : Map <String, ?> Passed back to the UI. Values vary per rule and are used to populate the templates.
 */

"Funder acknowledgement" ComplianceUtils.refdataCheck("acknowledgement", "Yes")
"Research materials access" ComplianceUtils.refdataCheck("accessStatement", "Yes")

Gold = {
  "Embargo" ComplianceUtils.embargoCheck(0)
  "Licence" ComplianceUtils.refdataCheck("licence", "CC BY")
}

Green = {
 "Deposit" ComplianceUtils.depositCheck(
   ['Accepted Manuscript']
 )
 
 "Licence" ComplianceUtils.refdataCheck("licence", ["CC BY", "CC BY-NC"])
}