package uk.ac.jisc.monitorlocal.compliance

import org.joda.time.Months

class ComplianceUtils {
  public static refdataCheck (String field, String expected) {
    return refdataCheck(field, [expected])
  }
  
  public static refdataCheck (String field, Collection<String> expected) {
    return {
      String expectedString = ""
      def total = expected.size()
      expected.eachWithIndex { String exp, index ->
        if (index > 0) {
          expectedString += "${index == (total - 1) ? ' or' : ','} "
        }
        expectedString += exp
      }
      
      def ret = [
        details: [
          type: 'refdata',
          expected: expectedString,
          field: field
        ]
      ]
      
      ret['result'] = it."${ret['details']['field']}"?.value?.toLowerCase()
      if (ret['result']) {
        
        ret['result'] = (expected?.find { ret['result'] == it.toLowerCase() } != null)
      }
      ret
    }
  }
  
  public static depositCheck (Collection<String> versions = [], String repoName = null) {
    return {
      
      if (versions == null) {
        versions = []
      } 
      
      // Test version?
      boolean versionTest = versions.size() > 0
      
      def ret = [
        details: [
          type: 'deposits',
          description: "At least one deposit must exist ${repoName ? 'within the \"' + repoName + '\" repository ' : ''}"
        ]
      ]
      
      if (versionTest) {
        ret['details']['description'] += "${repoName ? 'and' : 'where' } the version deposited must be" +
        "${ versions.size() > 1 ? ' one of' : ''} \"${versions.join("\", \"")}\""
      }
      
      int num = it.deposits?.size() ?: 0
      if ( num > 0 ) {
        ret['result'] = false
        def compareVersions = versions*.toLowerCase()
        for (int i=0; i<num && !ret['result']; i++ ) {
          
          if (versionTest) {
            def version = it.deposits[i]?.versionDeposited?.value?.toLowerCase()
            if (version) {
              ret['result'] = (compareVersions.contains(version))
            }
          }
          
          if (repoName) {
            def repository = it.deposits[i]?.name?.value
            if (repository) {
              ret['result'] = (!versionTest || ret['result']) && (repository =~ "(?i)(^\\Q${repoName}\\E)")
            }
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
        ret['result'] = period == months
      } else {
        ret['result'] = period <= months
      }
      ret
    }
  }
}
