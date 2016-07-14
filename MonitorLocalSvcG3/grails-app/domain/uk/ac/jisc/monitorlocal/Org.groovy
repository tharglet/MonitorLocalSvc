package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import groovy.util.logging.Log4j
import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection;

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Log4j
@Resource(uri="/org", superClass=ExtendedRestfulController)
class Org extends Component {
  
  String address
  String taxCode

  // Can users be affiliated with this org?
  Boolean membershipOrg
  

  // Still don't like this - these are roles, not types really, and an org needs to be able to play multiple. Not for this release
  // But pretty critical to fix
  @Defaults([
    'Publisher',
    'HEI',
    'Funder',
    'Funder Group'
  ])
  RefdataValue type

  @AbsoluteCollection
  Set userAffiliations = []
  static hasMany = [
    userAffiliations: UserOrg
  ]

  static mappedBy = [
    userAffiliations: 'org'
  ]

  // A closure that can be used when databinding an instance of org
  // Called with the object that represents the property, and the source
  public static orgBinder = { obj, source, createMissing=false ->

    println("orgBinder");
    if (source) {

      if ( obj == null ) {
        if ( source['id'] ) {
          obj = Org.get(source['id'])
        }
      }
  
      if ( ( obj == null ) && ( source['name'] != null ) ) {
        obj = Org.findByName(source['name']);
      }
  
      // Assuming we have a full set of properties, create a new Org
      if ( ( obj == null ) && createMissing && ( source['name'] != null ) ) {
        obj = new Org(name:source['name'])
        obj.save(flush:true, failOnError:true);
      }
    } else {
      // No org to bind.
      println "no org to bind"
    }

    // Bind all properties from source to obj

    return obj
  }
  
  static constraints = {
    address nullable: true, blank: false
    taxCode nullable: true, blank: false
    membershipOrg nullable:true, blank:false
    type nullable: true
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.Org',
      title:'Organizations',
      group:'Secondary',
      defaultSort:'name',
      defaultOrder:'asc',
      qbeConfig:[
        qbeForm:[
          [
            prompt:'Name or Title',
            qparam:'name',
            placeholder:'Name or title of item',
            contextTree: [ 'ctxtp':'disjunctive',
                             'terms':[
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'id', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'identifiers.identifier.value', 'wildcard':'R']
                             ]
                         ]
          ],
        ],
        qbeGlobals:[
          // ['ctxtp':'filter', 'prop':'status.value', 'comparator' : 'eq', 'value':'Deleted', 'negate' : true, 'prompt':'Hide Deleted', 'qparam':'qp_showDeleted', 'default':'on']
        ],
        qbeResults:[
          [heading:'Name', property:'name',sort:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ],
          [heading:'Status', sort:'status', property:'status.value'],
        ]
      ]
    ]
  }
}
