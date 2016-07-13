package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.finance.MonetaryValue;
import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

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
    
      // Grab the Refund value
      Long refundId = RefdataValue.findByValue("Refund")?.id
    
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
      for (CostItem ci : costs) {
        
        def val = ci.grossValueGBP?.value ?: 0
        if (refundId && ci?.category?.id == refundId) {
          val = -val
        }
        
        // Go through each cost item and subtract from the remaining.
        this.allocatedFunds.add("${val}")
      }
    }
    
    this.allocatedFunds
  }

  @Defaults([
          'Yes', 'No'
  ])
  RefdataValue credit

  @Defaults([
    'Yes', 'No'
  ])
  RefdataValue prepay

  static constraints = {
    'totalFunds'      ( nullable: true )
    'code'            ( nullable: true, blank: false )
    'source'          ( nullable: true )
    'credit'          ( nullable: true )
    'prepay'          ( nullable: false )
  }

  static mapping = {
    totalFunds cascade: "all-delete-orphan"
    allocatedCosts cascade: "all"
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.Budget',
      title:'Budget',
      group:'Secondary',
      defaultSort:'name',
      defaultOrder:'asc',
      qbeConfig:[
        qbeForm:[
          [
            prompt:'Name or Title',
            qparam:'q',
            placeholder:'Name or title of item',
            contextTree: [ 'ctxtp':'disjunctive',
                             'terms':[
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'R']
                             ]
                         ]

          ],
          [
            prompt:'Owner Institution',
            qparam:'instCtx',
            placeholder:'Owner Institution',
            contextTree: [ 'ctxtp':'qry', 'comparator' : 'eq', 'prop':'ownerInstitution.id', type:'java.lang.Long' ],
          ]
        ],
        qbeGlobals:[
        ],
        qbeResults:[
          [heading:'Name', property:'name',sort:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ],
          [heading:'Status', sort:'status', property:'status.value'],
        ]
      ]
    ]
  }

}
