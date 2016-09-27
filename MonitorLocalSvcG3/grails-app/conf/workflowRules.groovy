
General = {
  'Add a title' {
    name?.size() ?: 0 > 0
  }
  
  'Set the publication route' 'publicationRoute != null'
  'Set APC funding approval' 'publicationRoute?.value?.toLowerCase()?.startsWith("gold") ? (apcFundingApproval != null) : null'
  'Attach a grant' '(funds?.size() ?: 0) > 0'
}

People = {
  'Add a main contact' {
    names?.find {
      it.keyContact == true
    } as boolean
  }
}

Publication = {
  'Add a DOI' {
    def required_ns = "doi"
    boolean found = false
    for (int i=0; i< it.identifiers.size() && !found; i++ ) {
      found =  it.identifiers[i].identifier?.namespace?.value?.toLowerCase() ==  required_ns
    }
    return found
  }

  'Add a journal/conference title' 'publishedIn != null'
  'Add a publisher' 'publisher != null'
}

Finance = {
  'Add a committed cost item against a budget' {
    def required_types = ['committed']
    Boolean found = true
    def costs = it.academicOutputCosts
    
    if (apcFundingApproval?.value?.toLowerCase() == 'accepted') {
      found = false
      for (int i=0; i< costs.size() && !found; i++ ) {
        String type = costs.getAt(i)?.status?.value?.toLowerCase()
        found = (type && required_types.contains(type) ? true : null)
      }
    } else {
      // Not applicable.
      found = null
    }
    return found
  }
  
  'Add a cost item for the actual expenditure' {
    Boolean found = null
    if (apcFundingApproval?.value?.toLowerCase() == 'accepted') {
      found = false
      def required_types = ['actual']
      def costs = it.academicOutputCosts
      
      for (int i=0; i< costs.size() && !found; i++ ) {
        String type = costs.getAt(i)?.status?.value?.toLowerCase()
        found = type && required_types.contains(type)
      
      }
    }
    return found
  }
  
  'Actual costs marked as paid' {
    def required_types = ['actual']
    Boolean found = null
    def costs = it.academicOutputCosts
    
    for (int i=0; i< costs.size() && !found; i++ ) {
      String type = costs.getAt(i)?.status?.value?.toLowerCase()
      String paid = costs.getAt(i)?.paid?.value?.toLowerCase()
      found = type && paid && paid == 'yes' && required_types.contains(type)
    }
    return found
  }
}