package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*
import com.k_int.grailt.tools.finance.MonetaryValue

class CostItem extends Component {
  
  static hasMany = [
    awards:Tag,
    prepay:Tag
  ]
  
  static mappedBy = [
    academicOutput:'costs',
    invoice:'costs'
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
  
  List prepay
  List awards
  
  Currency currency = Constants.GBP
  
  @Defaults(["Yes","No"])
  RefdataValue paid
  
  @Defaults(["Yes","No"])
  RefdataValue estimated
  
  MonetaryValue grossValue = new MonetaryValue("baseCurrency" : currency)
  MonetaryValue grossValueGBP = new MonetaryValue("baseCurrency" : Constants.GBP)
  MonetaryValue tax = new MonetaryValue("baseCurrency" : currency)
  
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
  
  def beforeValidate () {
    
    // Check if we have updates to values or Currency
    if( !name || (isDirty() && getDirtyPropertyNames().intersect(["grossValue", "currency"]).size() > 0) ) {
      
      // Set the name just before validation as the name is required.
      name = toString()
    }
  }

  static constraints = {
    'status'          ( shared: 'required' )
    'category'        ( shared: 'required' )
    'paid'            ( shared: 'required' )
  }
  
  static mapping = {
    prepay cascade: "all"
    awards cascade: "all"
    budget cascade: "all"
  }
}
