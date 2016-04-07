package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Resource(uri="/person", superClass=ExtendedRestfulController)
class Person extends Component {
  
  Identifier orcid
  Identifier reference
  String title
  String firstName
  String surname

  static constraints = {
  }
}