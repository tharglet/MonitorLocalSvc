package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*;
import com.k_int.grailt.tools.finance.MonetaryValue

class Budget extends Component {

  MonetaryValue remainingFunds = new MonetaryValue()
  String code
  Org source
  
  @Defaults([
    'Yes', 'No'
  ])
  RefdataValue prepay
  
  static constraints = {
    'remainingFunds'  ( nullable: true )
    'code'            ( nullable: true, blank: false )
    'source'          ( nullable: true )
    'prepay'          ( nullable: true )
  }
  
  static mapping = {
    remainingFunds cascade: "all-delete-orphan"
  }
}