package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import groovy.util.logging.Log4j

import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection;

import java.util.List;

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Log4j
@Resource(uri="/invoice", superClass=ExtendedRestfulController)
class Invoice extends Component {
  
  // Ref is the component name.
  
  Date date
  Date dueDate
  
  InstitutionalRefdataValue supplier
  
  @Defaults(['Yes','No'])
  RefdataValue received
  Date receivedDate
  
  @Defaults(['Yes','No'])
  RefdataValue passedToFinance
  Date passedToFinanceDate
  
  @Defaults(['Yes','No'])  
  RefdataValue paidInFull
  Date paidInFullDate
  
  @Defaults(['Card','BACS','CHAPS'])
  RefdataValue paymentType  
  
  String filename
  
  @AbsoluteCollection
  Set invoiceCosts = []
  
  static namedQueries = {
	Component.namedQueries()
  }
  
  static hasMany = [
    invoiceCosts: CostItem
  ]

  static mapping = {
    invoiceCosts sort:'category', order:'asc', cascade: "all"
    supplier cascade: 'all'
  }
  
  static constraints = {
    date nullable: true
    dueDate nullable: true
    supplier nullable: true
    
    received nullable: true
    receivedDate nullable: true
    
    passedToFinance nullable: true
    passedToFinanceDate nullable: true
    
    paidInFull nullable: true
    paidInFullDate nullable: true
    
    paymentType nullable: true
    
    filename nullable: true, blank: false
    invoiceCosts nullable: false
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.Invoice',
      title:'Invoice',
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
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'id', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'filename', 'wildcard':'R']
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
