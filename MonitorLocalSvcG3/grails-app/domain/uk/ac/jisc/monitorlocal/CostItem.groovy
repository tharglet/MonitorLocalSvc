package uk.ac.jisc.monitorlocal

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder

import com.k_int.grails.tools.refdata.*

class CostItem extends Component {
  
  String name
  static DEFAULT_REFDATA_VALS = [
    'status' : [
      'Committed',
      'Actual'
    ],
    'category' : [
      'Basic APC Cost',
      'Bank Charge',
      'Admin Cost',
      'Discount',
      'Refund',
      'Page Charge',
      'Colour Charge',
      'Other'
    ],
    'prepay' : [
      'Elsevier:Prepay2015',
      'Wiley:Prepay2015',
      'Springer:2014'
    ],
    'paid' : [
      'Yes',
      'No'
    ],
    'estimated' : [
      'Yes',
      'No'
    ],
    'awards' : [
      'BB/J009709/1',
      'G1000708',
      'NE/G016003/1',
      'MC_UU_12019/4',
      'K013351',
      'U105260558',
      'G9901012',
      'WT089698/Z/09/Z',
      '089698/Z/09/Z',
      'EP/J007560/1',
      'WT089698',
      'EP/I01778X/1'
    ],
  ]
  
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
  RefdataValue category
  RefdataValue status
  List prepay
  List awards
  
  RefdataValue paid
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
