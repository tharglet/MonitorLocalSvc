package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.*


/**
 *  A name attached to an AO - the link between AO and person, with the ability to specify a name.
 *  We may have a name, but no way to uniquely look up or resolve that name into an identified person.
 *  In that case, the relationship from an AO may be terminated by here and not link to a Person, allowing
 *  an admin to resolve the ambiguity later, whilst still recording the name.
 */
class AoName {
  
  static belongsTo = [AcademicOutput]
  
  AcademicOutput academicOutput
  Person person
  String name
  
  def beforeValidate() {
    if (!name && person) {
      name = person.getName();
    }
  }
  
  boolean keyContact = false;

  @Defaults([
    'Corresponding Author',
    'Funded Author',
    'Author',
    'Principal Investigator',
    'Publisher Contact',
    'Funder Contact',
    'PhD Student'])
  RefdataValue namerel

  static constraints = {
    'academicOutput'  (nullable: false)
    'person'                     (nullable: true)
    'name'                        (nullable: true,  blank:false)
    'keyContact'             (nullable: false, blank:false)
    'namerel'                   (nullable: true)
  }
}
