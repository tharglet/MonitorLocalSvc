package uk.ac.jisc.monitorlocal

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder

import com.k_int.grails.tools.refdata.*

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
  
  @Defaults(["Yes","No"])
  RefdataValue paid
  
  @Defaults(["Yes","No"])
  RefdataValue estimated
  
  Currency currency = Constants.GBP
  
  BigDecimal grossValue = new BigDecimal("0.00")
  BigDecimal grossValueGBP = new BigDecimal("0.00")
  BigDecimal tax = new BigDecimal("0.00")
  
  public String toString () {
    // Default the value.
    String val = "${grossValue ?: 0.00}"
    
    // If we have the currency and the value then we can provide a value.
    if (currency && grossValue) {
    
      // Create a formatter.
      MoneyFormatter formatter = new MoneyFormatterBuilder()
        .appendCurrencySymbolLocalized()
        .appendAmount()
        .toFormatter()
      ;
      
      // Format the value using the currency and the gross value.
      val = "${category?.value ?: ''} ${formatter.print(Money.of(CurrencyUnit.of(currency), grossValue))}"
      
      // Only append GBP if in a different currency.
      if (grossValueGBP && currency != Constants.GBP) {
        val += " (${formatter.print(Money.of(CurrencyUnit.GBP, grossValueGBP))})"
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
    'currency'        ( shared: 'required', size:3 )
    'grossValue'      ( shared: 'required' )
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
