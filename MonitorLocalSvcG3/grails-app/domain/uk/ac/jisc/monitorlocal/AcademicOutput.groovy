package uk.ac.jisc.monitorlocal;


import grails.rest.Resource

import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Resource(uri="/ao", superClass=ExtendedRestfulController)
class AcademicOutput extends Component {
  
  static searchable = {
    type component: true
  }
  
  @Defaults([
    'Green', 'Gold', 'Gold Paid by Other'
  ])
  RefdataValue publicationRoute
  
  @Defaults(['Accepted', 'Rejected', 'Unfunded', 'Unknown (follow up with author)'])
  RefdataValue apcFundingApproval
  Date apcFundingDate
  
  @Defaults(['Submitted', 'Not Submitted'])
  RefdataValue publisherSubmissionStatus
  Date publisherSubmissionDate
  
  @Defaults(['Accepted', 'Rejected'])
  RefdataValue publisherResponse
  Date publisherResponseDate

  static hasMany = [
    costs: CostItem,
    names: AOName
  ]

  static constraints = {
    publicationRoute nullable: true
    apcFundingApproval nullable: true
    apcFundingDate nullable: true
    publisherSubmissionStatus nullable: true
    publisherSubmissionDate nullable: true
    publisherResponse nullable: true
    publisherResponseDate nullable: true
  }

  static mappedBy = [
    costs:'academicOutput',
    names:'academicOutput'
  ]

  static mapping = {
    costs sort:'category', order:'asc', cascade: "all"
//    namedRoles sort:'role', order:'asc', cascade: "all"
  }
}
