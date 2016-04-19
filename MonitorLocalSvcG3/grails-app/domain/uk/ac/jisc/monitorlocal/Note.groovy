package uk.ac.jisc.monitorlocal


class Note {
  Component owner
  String typeString
  
  static constraints = {
    owner nullable: false
    typeString nullable: false, blank:false
  }
}
