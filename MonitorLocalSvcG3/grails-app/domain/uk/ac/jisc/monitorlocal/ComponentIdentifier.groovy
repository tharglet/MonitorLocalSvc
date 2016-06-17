package uk.ac.jisc.monitorlocal

import grails.databinding.SimpleMapDataBindingSource
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j

import javax.persistence.Transient

import org.grails.databinding.BindUsing

import com.k_int.grails.tools.identifiers.Identifier

@Log4j
@EqualsAndHashCode(includes=["component,identifier"])
class ComponentIdentifier {

  @Transient
  def grailsWebDataBinder

  /**
   * Extend data binding to allow lookup by value as well as primary key, if none found,
   * then create an identifier.
   * obj -> the component identifier being bound
   * source -> json for identifier
   */
  @BindUsing({obj,source ->
    println("BindUsing");
    return obj.bindIdentifier(obj,source['identifier']);
  })
  Identifier identifier = new Identifier()

  Component component

  static constraints = {
    identifier nullable: false
    component nullable: false
  }
  
  static mapping = {
    identifier cascade: 'all'
  }

  static belongsTo = [component:Component]

  // See if we can match a component identifier - in the context of a parent object
  static ComponentIdentifier fuzzyMatch(owner, ci) {

    log.debug("ComponentIdentifier::fuzzyMatch(${owner},${ci})");

    def result = null;

    if ( ci['id'] ) {
      log.debug("ComponentIdentifier::fuzzyMatch on id - perform get ${ci['id']}");
      result = ComponentIdentifier.get(ci['id']);
    }
    else if ( owner.id ) {
      def idf = Identifier.fuzzyMatch(ci['identifier']);
      if ( idf ) {
        result = ComponentIdentifier.findByIdentifierAndComponent(idf,owner)
      }
    }

    result;
  }

  public void setIdentifier(Identifier i) {
    println("setIdentifier(${i})");
    this.identifier = i;
  }

  // def bind(source) {
  //   grailsWebDataBinder.bind(this,source);
  // }

  public String toString() {
    "CI id: ${id} identifier:${identifier} identifiercls:${identifier?.class?.name} comp:${component} compcls:${component?.class?.name}"
  }

  public Identifier bindIdentifier(obj,source) {

    println("**");
    println("Bind identifier in componentIdentifier ${obj} ${source}");
    println("**");

    Identifier result = null;
    try {
      def needs_save = false;
      result = Identifier.fuzzyMatch(source);
      if ( result == null ) {
        println("Create and bind a new identifier");
        result = new Identifier()
        needs_save=true;
        println("Calling bind on instance of ${result?.class.name} with identifier ${source}");
      }

      grailsWebDataBinder.bind(result, new SimpleMapDataBindingSource(source));

      if ( needs_save || result.isDirty() ) {
        result.save(flush:true, failOnError:true);
      }
    }
    catch ( Exception e ) {
      e.printStackTrace()
    }
    finally {
      println("Bind identifier in componentIdentifier returning ${result}");
    }

    result;
  }
}
