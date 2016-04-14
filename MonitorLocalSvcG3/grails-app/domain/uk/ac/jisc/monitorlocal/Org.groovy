package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j

@Log4j
@Resource(uri="/org", superClass=ExtendedRestfulController)
class Org extends Component {
  String address
  String taxCode
  Integer isni
  String ringgold
  
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
}
