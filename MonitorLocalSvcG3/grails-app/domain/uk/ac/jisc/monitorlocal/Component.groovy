package uk.ac.jisc.monitorlocal

class Component {

  String name
//  static hasMany = [
//    ids:Identifier
//  ]
//  
//  static mappedBy = [
//    ids:'components'
//  ]

  static constraints = {
    name shared: "required"
  }
  
//  static mapping = {
//    ids cascade: "all"
//  }
//  
  public String toString() {
    return this.getName()
  }
}