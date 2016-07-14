package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import com.k_int.grails.tools.rest.ExtendedRestfulController
/**
 *  A Grant attached to an AO - May contain between one and all of:
 *  A funder, the fund name and the grant ID 
 */
@Resource(uri="/grant", superClass=ExtendedRestfulController)
class AoGrant extends Component {
  
  AcademicOutput academicOutput
  CostItem costItem
  Org funder
  String fund
  String grantId
  String internalGrantId
  Person fundedAuthor
  
  static mappedBy = [
    costItem: 'grants',
    academicOutput: 'grants',
  ]
  
  def beforeValidate () {
    // Set the name if it isn't already.
     if( !name ) {
      // Lets update the name
      def nameStr = "${funder?.name ?: ''}:${fund ? ' ' + fund : ''}"
      if (grantId) {
        nameStr += " / ${grantId}"
      }
      if (internalGrantId) {
        nameStr += " / ${internalGrantId}"
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
    'fund'            (nullable: true,  blank:false)
    'grantId'         (nullable: true,  blank:false)
    'internalGrantId' (nullable: true,  blank:false)
    'fundedAuthor'    (nullable: true)
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.AoGrant',
      title:'AOGrant',
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
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'grantId', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'internalGrantId', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funder.name', 'wildcard':'R']
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
