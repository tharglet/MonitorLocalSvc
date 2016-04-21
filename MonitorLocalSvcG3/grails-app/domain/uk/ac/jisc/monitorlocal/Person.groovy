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

  @Defaults([
    'Mr',
    'Mrs',
    'Miss',
    'Ms',
    'Dr',
    'Professor'
  ])
  RefdataValue title

  Set personContactDetails = []
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
    title nullable: true
  }

  def beforeValidate () {
    // Set the name just before validation as the name is required.
    name = "${surname}, ${title?.value ? title?.value + ' ' : ''}${firstName}";
  }
}