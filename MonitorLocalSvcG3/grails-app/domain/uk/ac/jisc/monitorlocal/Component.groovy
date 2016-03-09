package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier

class Component {
  
  static searchable = {
    root = true
  }
  
  String name
  List identifiers
  
  static hasMany = [
    identifiers:Identifier
  ]

  static constraints = {
    name shared: "required"
  }
  
  static mapping = {
    identifiers cascade: "all"
  }
  
  public String toString() {
    return this.getName()
  }
}