package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.refdata.*


/**
 *  A name attached to an AO - the link between AO and person, with the ability to specify a name.
 *  We may have a name, but no way to uniquely look up or resolve that name into an identified person.
 *  In that case, the relationship from an AO may be terminated by here and not link to a Person, allowing
 *  an admin to resolve the ambiguity later, whilst still recording the name.
 */
class AOName {
  
  AcademicOutput academicOutput
  Person person
  String name

  @Defaults([ 'Author'])
  RefdataValue namerel

  static constraints = {
    'academicOutput'  (nullable: false)
    'person'          (nullable: true)
    'name'            (nullable: true,  blank:false)
    'namerel'         (nullable: true)
  }
}
