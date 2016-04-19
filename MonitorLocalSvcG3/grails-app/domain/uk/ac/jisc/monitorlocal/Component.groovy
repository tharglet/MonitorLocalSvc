package uk.ac.jisc.monitorlocal

import grails.web.databinding.GrailsWebDataBinder
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j

import javax.persistence.Transient;

import org.grails.databinding.BindUsing

import com.k_int.grails.tools.identifiers.Identifier



@Log4j
@EqualsAndHashCode(includes=["id"])
class Component {
  
  @Transient
  def gwdb
  public setGrailsWebDataBinder( GrailsWebDataBinder gwdb ) {
    this.gwdb = gwdb
  }
  
  String name

  @BindUsing({obj,source ->
    def result = obj.bindIdentifiers(obj,source);
    // log.debug("Result of bind identifiers: ${result}")
    result
  })
  List identifiers

  static hasMany = [
    identifiers:ComponentIdentifier,
    notes:Note
  ]

  static mappedBy = [
    identifiers:'component',
    notes:'owner'
  ]

  static constraints = {
    name nullable: false, blank:false
  }

  static mapping = {
    identifiers cascade: "all"
    tablePerHierarchy false
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

      def bindvars = [:]
      def first = true
      identifiers.each {
        if ( first ) {
          first = false
        }
        else {
          sw.write(' or ');
        }

        sw.write(' ( i.identifier.namespace.value = :nsVal AND i.identifier.value = :idVal ) ')
        bindvars['nsVal'] = (it.namespace)
        bindvars['idVal'] = (it.value)
      }

      def qry = sw.toString();

      log.debug("lookup ${qry} ${bindvars}");

      result = Component.executeQuery(qry,bindvars);
      
    }

    result
  }

  def bindIdentifiers(obj,source) {
    try {
      com.k_int.grails.tools.databinding.Helpers.manyToManyCollectionHelper(obj,
                                                                            source,
                                                                            'identifiers', 
                                                                            ComponentIdentifier.class,
                                                                            gwdb,
                                                                            'component',
                                                                            true);
    }
    catch ( Throwable t ) {
      log.error("Problem",t);
    }
  }

}
