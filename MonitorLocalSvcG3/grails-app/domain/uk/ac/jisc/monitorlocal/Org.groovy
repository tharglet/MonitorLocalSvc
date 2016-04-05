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
  
  @Defaults([
    'Organisation type 1',
    'Organisation type 2'
  ])
  RefdataValue type

  // A closure that can be used when databinding an instance of org
  // Called with the object that represents the property, and the source
  public static orgBinder = { obj, source, createMissing=false ->

    log.error("orgBinder");

    if ( obj == null ) {
      if ( source['id'] ) {
        obj = Org.get(source['id'])
      }
    }

    // Assuming we have a full set of properties, create a new Org
    if ( createMissing && ( obj == null ) ) {
      obj = new Org()
    }

    // Bind all properties from source to obj

    return obj
  }
}
