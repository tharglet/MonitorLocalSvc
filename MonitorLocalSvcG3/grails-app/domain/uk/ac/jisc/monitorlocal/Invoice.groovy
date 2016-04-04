package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Resource(uri="/invoice", superClass=ExtendedRestfulController)
class Invoice extends Component {
  
  // Ref is the component name.
  
  Date date
  Currency currency = Constants.GBP
  Date dueDate
  String supplier
  
  @Defaults(['Yes','No'])
  RefdataValue received
  Date receivedDate
  
  @Defaults(['Yes','No'])
  RefdataValue passedToFinance
  Date passedToFinanceDate
  
  @Defaults(['Yes','No'])  
  RefdataValue paidInFull
  Date paidInFullDate
  
  @Defaults(['Card','BACS','CHAPS'])
  RefdataValue paymentType  
  
  String filename
  
  static hasMany = [
    costs:CostItem
  ]

  static mappedBy = [
    costs:'invoice'
  ]

  static mapping = {
    costs sort:'category', order:'asc', cascade: "all"
  }
}