package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j

@Log4j
@Resource(uri="/person", superClass=ExtendedRestfulController)
class Person extends Component {

  String firstName
  String surname
  String orcId

  @Defaults([
          'Mr',
          'Mrs',
          'Miss',
          'Ms',
          'Dr',
          'Professor'
  ])
  RefdataValue title

  List personContactDetails = []
  static hasMany = [
          personContactDetails: ContactDetails
  ]

  static constraints = {
  }
}