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

Gold = {
  
  // Embargo period should always be set. This should return true or false.
  "Embargo" "it.embargoPeriod == 0 && (it.embargoEndDate == null || it.embargoEndDate == it.publishedDate)"

  // Tri-state boolean. If we don't have enough information to perform the check, we return null to denote "needs review"
  "License" {
    def val = it.licence?.value
    if (val)  val = (val.toUpperCase() == 'CC BY')
    val
  }
}

Green = {
    // Tri-state boolean. If we don't have enough information to perform the check, we return null to denote "needs review"
   "Deposit" {
      // Publication route.
      Boolean  val = null
      int num = it.deposits?.size() ?: 0
      
      if ( num > 0 ) {
        val = false
        for (int i=0; i<num && !val; i++ ) {
          def version = it.deposits[i].versionDeposited?.value?.toLowerCase()
          val = version == 'accepted manuscript'
        }
      }
      
      val
    }
}