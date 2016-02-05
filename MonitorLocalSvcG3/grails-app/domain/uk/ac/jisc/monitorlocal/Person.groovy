package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier

class Person extends Component {
  
  Identifier orcid
  Identifier reference
  String email

  static constraints = {
  }
}