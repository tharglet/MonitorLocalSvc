package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import groovy.util.logging.Log4j;

import com.k_int.grails.tools.rest.ExtendedRestfulController
/**
 *  A Grant attached to an AO
 */
@Log4j
@Resource(uri="/grant", superClass=ExtendedRestfulController)
class AoGrant extends Component {
  
  AcademicOutput academicOutput
  CostItem costItem
  Org funder
  String grantId
  String internalGrantId
  Person fundedAuthor
  
  
  static namedQueries = {
    Component.namedQueries()
  }
  
  static mappedBy = [
    costItem: 'grants',
    academicOutput: 'grants',
  ]
  
  def beforeValidate () {
    // Set the name if it isn't already.
     if( !name ) {
      // Lets update the name
      def nameStr = "${grantId}"
      if (internalGrantId) {
        nameStr += " / ${internalGrantId}"
      }
      if (funder?.name?.length() ?: 0 > 0) {
        nameStr += " / ${funder.name}"
      }
      
      if ( !(nameStr?.length() > 0) ) {
        // No string
        nameStr = "Grant ${id}"
      }
      
      setName( nameStr )
    }
  }
  
  static constraints = {
    'academicOutput'  (nullable: true)
    'funder'          (nullable: true)
    'costItem'        (nullable: true)
    'grantId'         (nullable: true,  blank:false, validator: { val, inst ->
      def result = AoGrant.ownedComponents {
        and {
          def instance = inst
          if (inst.id) {
            not {
              idEq inst.id
            }
          }
          ilike 'grantId' , "${val}"
        }
        projections {
          count("id")
        }
      }
      
      if (result[0] > 0) {
        ['ensureUnique', "${val}", "grant", "code"]
      }
    })
    
    'internalGrantId' (nullable: true,  blank:false)
    'fundedAuthor'    (nullable: true)
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.AoGrant',
      useDistinct: true,
      title:'AOGrant',
      group:'Secondary',
      defaultSort:'name',
      defaultOrder:'asc',
      qbeConfig:[
        qbeForm:[
          [
            prompt:'Search',
            qparam:'q',
            placeholder:'Search Grants',
            contextTree: [ 'ctxtp':'disjunctive',
              'terms':[
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'R'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'grantId', 'wildcard':'R'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'internalGrantId', 'wildcard':'R'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funder.name', 'wildcard':'R']
              ]
            ]

          ],
          [
            expose: false,
            prompt:'Owner Institution',
            qparam:'instCtx',
            placeholder:'Owner Institution',
            contextTree: [ 'ctxtp':'qry', 'comparator' : 'eq', 'prop':'ownerInstitution.id'],
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
