package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode

import com.k_int.grails.tools.finance.MonetaryValue
import com.k_int.grails.tools.refdata.*

@EqualsAndHashCode
class CostItem {
  
  static mappedBy = [
    academicOutput:'academicOutputCosts',
    invoice:'invoiceCosts'
  ]  
  
  AcademicOutput academicOutput
  Invoice invoice
  Budget budget
  
  @Defaults([
    'Basic APC Cost',
    'Bank Charge',
    'Admin Cost',
    'Discount',
    'Licensing Charge',
    'Currency Adjustment',
    'Page Charge',
    'Colour Charge',
    'Membership',
    'Pre-Pay Deposit',
    'Other'
  ])
  RefdataValue category
  
  @Defaults([
    'Committed',
    'Actual'
  ])
  RefdataValue status

  InstitutionalRefdataValue purchaseOrder
  
  Currency currency = Constants.GBP
  
  @Defaults(["Yes","No"])
  RefdataValue paid
  
  @Defaults(["Yes","No"])
  RefdataValue estimated

  @Defaults(["Debit","Credit"])
  RefdataValue type
  
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
    'purchaseOrder'   ( nullable: true )
    'type'            ( nullable: true )
  }
  
  static mapping = {
    budget cascade: "all"
    grossValue cascade: 'all-delete-orphan'
    grossValueGBP cascade: 'all-delete-orphan'
    tax cascade: 'all-delete-orphan'
    academicOutput cascade: 'all'
    purchaseOrder cascade: 'all'
  }
}
