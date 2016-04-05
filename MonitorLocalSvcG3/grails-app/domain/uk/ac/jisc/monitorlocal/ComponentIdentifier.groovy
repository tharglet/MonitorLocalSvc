package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import com.k_int.grails.tools.identifiers.Identifier

class ComponentIdentifier {

  Identifier identifier
  Component component

  static constraints = {
    identifier nullable: false, blank:false
    component nullable: false, blank:false
  }

}
