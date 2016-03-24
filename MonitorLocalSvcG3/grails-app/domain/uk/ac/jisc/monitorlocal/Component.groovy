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

  public static Component lookupOrCreate(Class cls, String name, List identifiers) {
    def result
    def existingComponents = lookupByIdentifierValue(identifiers)
    switch ( existingComponents.size() ) {
      case 0:
        result = cls.newInstance();
        result.name = name
        result.identifiers = []
        identifiers.each {
          result.identifiers.add(Identifier.lookupOrCreate(it.namespace, it.value))
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

    def result = []

    if ( identifiers != null ) {
      def crit = Identifier.createCriteria()
      // def combotype = RefdataCategory.lookupOrCreate('Combo.Type','KBComponent.Ids');

      def lr = crit.list {
        or {
          identifiers.each {
            if ( ( it?.value != null ) && ( it.value.trim().length() > 0 ) ) {
              eq('value', it.value)
            }
          }
        }
      }

      lr?.each { id ->
        id.components.each { component ->
          result.add ( component )
        }
      }
    }

    result
  }

}
