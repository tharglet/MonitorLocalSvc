package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import java.util.List;

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Resource(uri="/invoice", superClass=ExtendedRestfulController)
class Invoice extends Component {
  
  // Ref is the component name.
  
  Date date
  Date dueDate
  
  
  Tag supplier
  
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
  
  Set invoiceCosts = []
  static hasMany = [
    invoiceCosts: CostItem
  ]

  static mapping = {
    invoiceCosts sort:'category', order:'asc', cascade: "all"
    supplier cascade: 'all'
  }
  
  static constraints = {
    date nullable: true
    dueDate nullable: true
    supplier nullable: true
    
    received nullable: true
    receivedDate nullable: true
    
    passedToFinance nullable: true
    passedToFinanceDate nullable: true
    
    paidInFull nullable: true
    paidInFullDate nullable: true
    
    paymentType nullable: true
    
    filename nullable: true, blank: false
    invoiceCosts nullable: false
  }
}