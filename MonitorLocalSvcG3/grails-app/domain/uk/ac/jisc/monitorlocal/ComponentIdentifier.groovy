package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import com.k_int.grails.tools.identifiers.Identifier
import org.grails.databinding.BindUsing
import grails.databinding.SimpleMapDataBindingSource
import javax.persistence.Transient
import groovy.util.logging.Log4j

@Log4j
class ComponentIdentifier {

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
  Identifier identifier

  Component component

  static constraints = {
    identifier nullable: false, blank:false
    component nullable: false, blank:false
  }

  static ComponentIdentifier fuzzyMatch(ci, owner) {

    log.debug("ComponentIdentifier::fuzzyMatch(${ci},${owner}");

    def result = null;
    if ( ci['id'] ) {
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
