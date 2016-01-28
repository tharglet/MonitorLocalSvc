package uk.ac.jisc.monitorlocal;


import com.k_int.grails.tools.refdata.*

class AcademicOutput extends Component {
  static DEFAULT_REFDATA_VALS = [
    type: [
      'A - Authored book',
      'B - Edited book',
      'C - Chapter in book',
      'D - Journal article',
      'E - Conference contribution',
      'F - Patent',
      'G - Software',
      'H - Website content',
      'I - Performance',
      'J - Composition',
      'K - Design',
      'L - Artefact',
      'M - Exhibition',
      'N - Research report for external body',
      'O - Confidential report for external body',
      'P - Devices and products',
      'Q - Digital or visual media',
      'R - Scholarly edition',
      'S - Research data sets / databases',
      'T - Other',
      'U - Working paper',
    ]
  ]
  
  RefdataValue type

  static constraints = {
    'name' (shared: 'titleText')
    'type' (shared: 'required')
  }

  static hasMany = [
    costs: CostItem,
    namedRoles: NamedRole
  ]

  static mappedBy = [
    costs:'academicOutput',
    namedRoles:'academicOutput'
  ]

  static mapping = {
    costs sort:'category', order:'asc', cascade: "all"
    namedRoles sort:'role', order:'asc', cascade: "all"
  }
}
