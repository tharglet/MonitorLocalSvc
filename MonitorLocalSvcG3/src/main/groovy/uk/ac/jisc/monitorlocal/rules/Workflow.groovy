package uk.ac.jisc.monitorlocal.rules

/**
 * Rules for workflow. Defining them here as the YML config lacks the ability to
 * include closures. And the application.groovy file doesn't appear to be read properly at the moment.
 */
class Workflow {

  // Single closure defines the rule names and the code to be executed. Can be a closure or a string. String is assumed to be groovy code.
  public static final RULES = {

    // General
    'Add a title' 'name != null'
    'Attach a grant' '(funds?.size() ?: 0) > 0'
    'Set the publication route' 'publicationRoute != null'
    'Set APC funding approval' 'apcFundingApproval != null'

    // People
    'Add a main contact' 'keyContact != null'

    // Publication
    'Add an identifier (DOI/PMID/PMCID)' {
      def required_ns = ['doi', 'pmid', 'pmcid']
      boolean found = false
      for (int i=0; i< it.identifiers.size() && !found; i++ ) {
        String ns = it.identifiers[i].identifier?.namespace?.value
        found = ns && required_ns.contains(ns)
      }
      return found
    }
    'Add a journal/conference title' 'publicationTitle != null'
    'Add an ISSN or eISSN' {
      def required_ns = ['issn', 'eissn']
      boolean found = false
      
      def ids = it.identifiers
      for (int i=0; i< ids.size() && !found; i++ ) {
        String ns = ids.getAt(i)?.identifier?.namespace?.value?.toLowerCase()
        found = ns && required_ns.contains(ns)
      }
      return found
    }

    // Finance
    'Add a cost item for the actual expenditure' {
      def required_types = ['actual']
      boolean found = false
      def costs = it.academicOutputCosts
      
      for (int i=0; i< costs.size() && !found; i++ ) {
        String type = costs.getAt(i)?.status?.value?.toLowerCase()
        found = type && required_types.contains(type)
      }
      return found
    }
  }
}