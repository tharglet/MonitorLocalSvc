package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j
import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection;

@Log4j
@Resource(uri="/person", superClass=ExtendedRestfulController)
class Person extends Component {

  String firstName
  String surname
  Org institution

  @AbsoluteCollection
  Set personContactDetails = []
  
  static namedQueries = {
	Component.namedQueries()
  }
  
  static hasMany = [
    personContactDetails: ContactDetails
  ]
  
  static mappedBy = [
    personContactDetails:'person'
  ]
  
  static mapping = {
    personContactDetails cascade: "all"
  }

  static constraints = {
    firstName nullable: true
    surname nullable: true
    institution nullable: true
  }

  def beforeValidate () {
    // Set the name just before validation as the name is required.
    name = "${surname}, ${firstName}";
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.Person',
      title:'Person',
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
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'firstName', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'surname', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'id', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'personContactDetails.emailAddress', 'wildcard':'R'],
                                  ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'personContactDetails.department', 'wildcard':'R']
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
