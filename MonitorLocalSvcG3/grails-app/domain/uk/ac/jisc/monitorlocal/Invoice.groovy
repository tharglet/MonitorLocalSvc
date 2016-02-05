package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*
import java.util.Currency

class Invoice extends Component {
  Date date
  Currency currency = Constants.GBP
  Date dueDate
  
  String supplier
  
  @Defaults(['Yes','No'])
  RefdataValue received
  
  @Defaults(['Yes','No'])
  RefdataValue passedToFinance
  
  @Defaults(['Yes','No'])  
  RefdataValue paidInFull
  
  @Defaults(['Card','BACS','CHAPS'])
  RefdataValue paymentType
  
  Date receivedDate
  Date passedToFinanceDate
  Date paidInFullDate
  
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