package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j

@Log4j
@Resource(uri="/org", superClass=ExtendedRestfulController)
class Org extends Component {
  
  @Defaults([
    'Charity Open Access Fund (COAF)',
    'Research Councils UK (RCUK)',
    'Research Excellence Framework (REF)'
  ])
  FundingGroup fundingGroup
  String address
  String taxCode
  
  @Defaults([
    'Publisher',
    'HEI',
    'Funder'
  ])
  RefdataValue type

  // A closure that can be used when databinding an instance of org
  // Called with the object that represents the property, and the source
  public static orgBinder = { obj, source, createMissing=false ->

    println("orgBinder");

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

    // Bind all properties from source to obj

    return obj
  }
  
  static constraints = {
    address nullable: true, blank: false
    taxCode nullable: true, blank: false
    fundingGroup nullable: true
  }
  
  static mapping = {
    // Use a join table here to prevent a load of nulls in our table.
    fundingGroup (
      joinTable: [
        name:     'org_funding_groups',
        key:      'org_id',
        column:   'funding_group_id'
      ]
    )
  }
}
