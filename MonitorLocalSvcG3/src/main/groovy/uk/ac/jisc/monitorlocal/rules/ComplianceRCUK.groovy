package uk.ac.jisc.monitorlocal.rules

/**
 * Rules for compliance. Defining them here as the YML config lacks the ability to
 * include closures. And the application.groovy file doesn't appear to be read properly at the moment.
 */
class ComplianceRCUK {
  
  // Single closure defines the rule names and the code to be executed. Can be a closure or a string. String is assumed to be groovy code.
  public static final RULES = {
    "Embargo" {
      // Publication route.
      def val = publicationRoute?.value?.toLowerCase()
      
      if (val == 'gold') {
        val = (embargoPeriod == 0 && (embargoEndDate == null || embargoEndDate == publishedDate))
      } else {
        // If not gold return null... As not a failure, but irrelevant.
        val = null
      }
      
      val
    }
    
    "License" {
      // Publication route.
      def val = publicationRoute?.value?.toLowerCase()
      
      if (val == 'gold') {
        val = licence?.value
        if (val)  val = (val == 'CC BY')
      } else {
        val = null
      }
      val
    }
    
    "Deposit" {
      // Publication route.
      def val = publicationRoute?.value?.toLowerCase()

      int num = deposits?.size() ?: 0
      if (val == 'green' && num > 0) {
        
        
        val = false
        for (int i=0; i<num && !val; i++ ) {
          def version = deposits[i].versionDeposited?.value?.toLowerCase()
          val = version == 'accepted manuscript'
        }
      } else {
        val = null
      }
      
      val
    }
    
    "Funder acknowledgement" {
      def val = acknowledgement?.value?.toLowerCase()
      if (val) {
        val = val == 'yes'
      }
      val    
    }
    
    "Research materials access" {
      def val = accessStatement?.value?.toLowerCase()
      if (val) {
        val = val == 'yes'
      }
      val    
    }
  }
}
