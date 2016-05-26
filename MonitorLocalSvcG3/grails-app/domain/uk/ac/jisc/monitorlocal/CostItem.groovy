package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*
import com.k_int.grailt.tools.finance.MonetaryValue

class CostItem {
  
  static mappedBy = [
    academicOutput:'academicOutputCosts',
    invoice:'invoiceCosts'
  ]

  Budget budget
  
  AcademicOutput academicOutput
  Invoice invoice
  
  @Defaults([
    'Basic APC Cost',
    'Bank Charge',
    'Admin Cost',
    'Discount',
    'Refund',
    'Page Charge',
    'Colour Charge',
    'Other'
  ])
  RefdataValue category
  
  @Defaults([
    'Committed',
    'Actual'
  ])
  RefdataValue status
  
  Currency currency = Constants.GBP
  
  @Defaults(["Yes","No"])
  RefdataValue paid
  
  @Defaults(["Yes","No"])
  RefdataValue estimated
  
  MonetaryValue grossValue = new MonetaryValue("baseCurrency" : currency)
  MonetaryValue grossValueGBP = new MonetaryValue("baseCurrency" : Constants.GBP)
  MonetaryValue tax = new MonetaryValue("baseCurrency" : Constants.GBP)
  
  public String toString () {
    
    // Default the value.
    String val = "0.00"
    
    // If we have the currency and the value then we can provide a value.
    if (grossValue) {
      
      // Format the value using the currency and the gross value.
      val = "${category?.value ?: ''} ${grossValue}"
      
      // Only append GBP if in a different currency.
      if (grossValueGBP && grossValue?.baseCurrency != Constants.GBP) {
        val += " (${grossValueGBP})"
      }
    }

    return val
  }

  static constraints = {
    'status'          ( nullable: true )
    'category'        ( nullable: true )
    'budget'          ( nullable: true )
    'academicOutput'  ( nullable: true )
    'invoice'         ( nullable: true )
    'paid'            ( nullable: true )
    'status'          ( nullable: true )
    'category'        ( nullable: true )
    'estimated'       ( nullable: true )
  }
  
  static mapping = {
    budget cascade: "all"
    grossValue cascade: 'all-delete-orphan'
    grossValueGBP cascade: 'all-delete-orphan'
    tax cascade: 'all-delete-orphan'
    academicOutput cascade: 'all'
    invoice cascade: 'all'
  }
}
