package uk.ac.jisc.monitorlocal.compliance

import org.joda.time.Months

class ComplianceUtils {
  public static refdataCheck (String field, String expected) {
    return {
      def ret = [
        details: [
          type: 'refdata',
          expected: expected,
          field: field
        ]
      ]
      ret['result'] = it."${ret['details']['field']}"?.value?.toLowerCase()
      if (ret['result']) {
        ret['result'] = ret['result'] == ret['details']['expected']?.toLowerCase()
      }
      ret
    }
  }
  
  public static depositCheck (Collection versions = [], String repoName = null) {
    return {
       
      // Test version?
      boolean versionTest = versions.size() > 0;
      
      def ret = [
        details: [
          type: 'deposits',
          description: "At least one deposite must exist ${repoName ? 'within the \"' + repoName + '\" repository and' : 'where'} the version deposited must be" +
            "${ versions.size() > 1 ? ' one of' : ''} \"${versions.join("\", \"")}\""
        ]
      ]
      
      int num = it.deposits?.size() ?: 0
      if ( num > 0 ) {
        ret['result'] = false
        for (int i=0; i<num && !ret['result']; i++ ) {
          
          if (versionTest) {
            def version = it.deposits[i]?.versionDeposited?.value?.toLowerCase()
            if (version) {
              ret['result'] = (versions.contains(version))
            }
          }
          
          def repository = it.deposits[i]?.name?.toLowerCase()
          if (repository) {
            ret['result'] = (!versionTest || ret['result']) && (repository ==~ "(?i)(^\\Q${repoName}\\E)")
          }
        }
      }
      ret
    }
  }
  
  public static embargoCheck (int months = 0, boolean exact = false) {
    return {    
      def ret = [
        details: [
          type: 'embargo',
          description: "Embargo period must be ${exact ? months : ' no more than ' + months} months"
        ]
      ]
      
      // If the period is greater than 0 then use that else we should check the dates and work out the months.
      int period = it.embargoPeriod ?: 0
      if (period == 0 && it.embargoEndDate && it.publishedDate) {
        period = Months.monthsBetween(it.publishedDate, it.embargoEndDate).getMonths()
      }
      
      if (exact) {
        ret['result'] == months
      } else {
        ret['result'] <= months
      }
      ret
    }
  }
}
