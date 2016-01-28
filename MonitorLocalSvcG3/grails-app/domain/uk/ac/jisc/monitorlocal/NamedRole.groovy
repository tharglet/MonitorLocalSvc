package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*

class NamedRole extends Component {
  
  RefdataValue role
  Institution institution
  Person person
  AcademicOutput academicOutput
  RefdataValue verified
  String verificationMethod
  String department
  
  public String getName() {
    String n = super.getName()
    if (n == null || (n?.trim() == "")) {
      n = person?.name
    }
    
    n
  }
  
  static DEFAULT_REFDATA_VALS = [
    role: [
      'Corresponding Author',
      'Additional Author',
    ],
    verified: [
      'Yes',
      'No',
    ]
  ]
  
  static constraints = {
    role shared: 'required'
    institution nullable: true, blank: false
    person nullable: true, blank: false
  }
}
