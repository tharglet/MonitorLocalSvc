package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*
import java.util.Currency

class Invoice extends Component {
  Date date
  Currency currency = Constants.GBP
  Date dueDate
  
  String supplier
  
  static DEFAULT_REFDATA_VALS = [
    received: [
      'Yes',
      'No'
    ],
    passedToFinance: [
      'Yes',
      'No'
    ],
    paidInFull: [
      'Yes',
      'No'
    ],
    paymentType: [
      'Card',
      'BACS',
      'CHAPS'
    ]
  ]
  
  RefdataValue received
  Date receivedDate
  
  RefdataValue passedToFinance
  Date passedToFinanceDate
  
  RefdataValue paidInFull
  Date paidInFullDate
  
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