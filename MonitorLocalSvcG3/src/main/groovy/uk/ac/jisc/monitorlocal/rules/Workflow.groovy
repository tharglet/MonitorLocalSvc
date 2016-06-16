package uk.ac.jisc.monitorlocal.rules

/**
 * Rules for workflow. Defining them here as the YML config lacks the ability to
 * include closures. And the application.groovy file doesn't appear to be read properly at the moment.
 */
class Workflow {

  // Single closure defines the rule names and the code to be executed. Can be a closure or a string. String is assumed to be groovy code.
  public static final RULES = {

    // General
    'Add a Title' 'name != null'
    'Attach a grant' '(funds?.size() ?: 0) > 0'
    'Set the publication route' 'publicationRoute != null'
    'Set APC Funding Approval' 'apcFundingApproval != null'

    // People
    'Add a main contact' 'keyContact != null'

    // Publication
    'Add an identifier' {
      final def required_ns = ['doi', 'pmid', 'pmcid']
      boolean found = false
      for (int i=0; i< identifiers.size() && !found; i++ ) {
        def ns = identifiers[i].identifier?.namespace?.value
        found = ns && required_ns.contains(ns)
      }
      return found
    }
    'Add a Publication Title' 'publicationTitle != null'
    'Add an ISSN or eISSN' {
      final def required_ns = ['issn', 'eissn']
      boolean found = false
      for (int i=0; i< identifiers.size() && !found; i++ ) {
        def ns = identifiers[i].identifier?.namespace?.value
        found = ns && required_ns.contains(ns)
      }
      return found
    }

    // Finance
    'Add a cost item for the actual expenditure' {
      final def required_types = ['actual']
      boolean found = false
      for (int i=0; i< academicOutputCosts.size() && !found; i++ ) {
        def type = academicOutputCosts[i].status?.value
        found = type && required_types.contains(type)
      }
      return found
    }
  }
}
