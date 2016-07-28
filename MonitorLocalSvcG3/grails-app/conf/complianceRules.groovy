RCUK = {
    "Embargo" {
      // Publication route.
      def val = it.publicationRoute?.value?.toLowerCase()
      
        if (val?.startsWith('gold')) {
          val = (it.embargoPeriod == 0 && (it.embargoEndDate == null || it.embargoEndDate == it.publishedDate))
        } else {
          // If not gold return null... As not a failure, but irrelevant.
          val = null
        }
      
      val
    }
    
    "License" {
      // Publication route.
      def val = it.publicationRoute?.value?.toLowerCase()
      
       if (val?.startsWith('gold')) {
          val = it.licence?.value
          if (val)  val = (val == 'CC BY')
        } else {
          val = null
        }
      val
    }
    
    "Deposit" {
      // Publication route.
      def val = it.publicationRoute?.value?.toLowerCase()
      int num = it.deposits?.size() ?: 0
      
      if (val?.startsWith('green') && num > 0) {        
        val = false
        for (int i=0; i<num && !val; i++ ) {
          def version = it.deposits[i].versionDeposited?.value?.toLowerCase()
          val = version == 'accepted manuscript'
        }
      } else {
        val = null
      }
      
      val
    }
    
    "Funder acknowledgement" {
      def val = it.acknowledgement?.value?.toLowerCase()
      if (val) {
        val = val == 'yes'
      }
      val    
    }
    
    "Research materials access" {
      def val = it.accessStatement?.value?.toLowerCase()
      if (val) {
        val = val == 'yes'
      }
      val    
    }
  }