package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import com.k_int.grailt.tools.finance.MonetaryValue

@Resource(uri="/budget", superClass=ExtendedRestfulController)
class Budget extends Component {
  
  static transients = [ "remainingFunds", "allocatedFunds" ]
  MonetaryValue remainingFunds = null
  MonetaryValue allocatedFunds = null

  MonetaryValue totalFunds = new MonetaryValue()
  String code
  Org source

  // Calculate the remaining funds from the CostItems.
  public MonetaryValue getRemainingFunds () {
    
    if (this.remainingFunds == null) {

      // Start with a remaining value of the initial "total" funds.
      this.remainingFunds = new MonetaryValue()
      this.remainingFunds.fromString("${getTotalFunds().value}")
      this.remainingFunds.subtract("${getAllocatedFunds().value}")
    }
    this.remainingFunds
  }
  
  public MonetaryValue getAllocatedFunds () {
    
    if (this.allocatedFunds == null) {
    
      // Calculate and store as a property, however hibernate should not persist.
      this.allocatedFunds = new MonetaryValue()
      final long theId = getId()
      
      // Grab all the costs.
      def costs = CostItem.withCriteria {
        budget {
          idEq theId
        }
      }
      
      // Go through eaach cost and total them up.
      for (CostItem ci : costs ) {
        // Go through each cost item and subtract from the remaining.
        this.allocatedFunds.add("${ci.grossValueGBP.value}")
      }
    }
    
    this.allocatedFunds
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