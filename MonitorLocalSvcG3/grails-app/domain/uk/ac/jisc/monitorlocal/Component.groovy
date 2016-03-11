package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier

class Component {
  
  String name
  List identifiers
  
  static hasMany = [
    identifiers:Identifier
  ]

  static constraints = {
    name nullable: false, blank:false
  }
  
  static mapping = {
    identifiers cascade: "all"
  }
  
  public String toString() {
    return this.getName()
  }
}