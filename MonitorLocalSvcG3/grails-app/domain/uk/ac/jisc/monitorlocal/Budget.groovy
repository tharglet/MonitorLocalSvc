package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import com.k_int.grailt.tools.finance.MonetaryValue

@Resource(uri="/budget", superClass=ExtendedRestfulController)
class Budget extends Component {

  MonetaryValue remainingFunds = new MonetaryValue("baseCurrency" : Constants.GBP)
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