package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier

class Component {
  String name
  static hasMany = [
    ids:Identifier
  ]

  static constraints = {
    name shared: "required"
  }
  
  static mapping = {
    ids cascade: "all"
  }
//  
  public String toString() {
    return this.getName()
  }
}