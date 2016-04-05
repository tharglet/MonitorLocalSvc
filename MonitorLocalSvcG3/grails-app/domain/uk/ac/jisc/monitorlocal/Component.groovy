package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import com.k_int.grails.tools.identifiers.Identifier
import groovy.util.logging.Log4j

@Log4j
@EqualsAndHashCode(includes=["id"])
class Component {

  String name
  List identifiers

  static hasMany = [
    identifiers:ComponentIdentifier
  ]

  static mappedBy = [
    identifiers:'component'
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

  public static Component lookupOrCreate(Class cls, String name, List identifiers) {
    def result
    def existingComponents = lookupByIdentifierValue(identifiers)
    switch ( existingComponents.size() ) {
      case 0:
        result = cls.newInstance();
        result.name = name
        result.identifiers = []
        identifiers.each {
          result.addToIdentifiers(Identifier.lookupOrCreate(it.namespace, it.value))
        }
        result.save(flush:true, failOnError:true);
        break;
      case 1:
        result = existingComponents.get(0)
        break;
      default:
        throw new RuntimeException("Identifiers ${identifiers} matched ${existingComponents.size()} components in lookupOrCreate for ${name}");
        break;
    }
    result
  }


  /**
   *  @Param identifiers : List of maps [ [ namespace:'issn', value:'1234-6789' ],...]
   */ 
  static def lookupByIdentifierValue(identifiers) {

    log.debug("lookupByIdentifierValue ${identifiers}");

    def result = []

    if ( ( identifiers != null ) && ( identifiers.size() > 0 ) ) {
   
      // def combotype = RefdataCategory.lookupOrCreate('Combo.Type','KBComponent.Ids');
      def sw = new StringWriter()
      sw.write("select c from Component as c join c.identifiers as i where ")

      def bindvars = []
      def first = true
      identifiers.each {
        if ( first ) {
          first = false
        }
        else {
          sw.write(' or ');
        }

        sw.write(' ( i.namespace.value = ? AND i.value = ? ) ')
        bindvars.add(it.namespace)
        bindvars.add(it.value)
      }

      def qry = sw.toString();

      log.debug("lookup ${qry} ${bindvars}");

      result = Component.executeQuery(qry,bindvars);
    }

    result
  }

}
