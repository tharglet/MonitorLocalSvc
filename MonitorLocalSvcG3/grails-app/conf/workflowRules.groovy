
General = {
  'Add a title' 'name != null'
  'Set the publication route' 'publicationRoute != null'
  'Set APC funding approval' 'apcFundingApproval != null'
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
  'Add a journal/conference title' 'publicationTitle != null'
  'Add a publisher' 'publisher != null'
}

Finance = {
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