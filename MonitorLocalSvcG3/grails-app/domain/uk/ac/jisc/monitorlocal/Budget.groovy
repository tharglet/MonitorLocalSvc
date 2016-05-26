package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import com.k_int.grailt.tools.finance.MonetaryValue

@Resource(uri="/budget", superClass=ExtendedRestfulController)
class Budget extends Component {

  MonetaryValue totalFunds = new MonetaryValue()
  String code
  Org source

  // Calculate the remaining funds from the CostItems.
  public MonetaryValue getRemainingFunds () {

    // Start with a remaining value of the initial "total" funds.
    final MonetaryValue remaining = new MonetaryValue(value: totalFunds.value)
    final theId = getId()
    
    // Grab all the costs.
    def costs = CostItem.withCriteria {
      budget {
        idEq theId
      }
    }
    
    // Go through eaach cost and total them up.
    costs.each { CostItem ci ->
      // Go through each cost item and subtract from the remaining.
      MonetaryValue netValue = new MonetaryValue(value: ci.grossValueGBP)
      remaining.subtract("${netValue.value}")
    }
    return remaining
  }

  @Defaults([
    'Yes', 'No'
  ])
  RefdataValue prepay

  static constraints = {
    'totalFunds'      ( nullable: true )
    'code'            ( nullable: true, blank: false )
    'source'          ( nullable: true )
    'prepay'          ( nullable: true )
  }

  static mapping = {
    totalFunds cascade: "all-delete-orphan"
    allocatedCosts cascade: "all"
  }
}