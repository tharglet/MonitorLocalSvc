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

  @BindUsing({obj,source ->
    log.debug("-> Bind identifier in componentIdentifier ${obj} ${source}");
    def result = null;
    try {
      result = Identifier.fuzzyMatch(source['identifier']);
      if ( result == null ) {
        log.debug("Create and bind a new identifier");
        result = new Identifier()
        // grailsWebDataBinder.bind(result,new SimpleMapDataBindingSource(source['identifier']));
        log.debug("Calling bind on instance of ${result?.class.name} with identifier ${source['identifier']}");
        result.bind(result, new SimpleMapDataBindingSource(source['identifier']));
      }
      else {
        log.debug("matched identifier");
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      log.debug("Bind identifier in componentIdentifier returning ${result}");
    }
    result;
  })
  Identifier identifier

  Component component

  static constraints = {
    identifier nullable: false, blank:false
    component nullable: false, blank:false
  }

  static ComponentIdentifier fuzzyMatch(ci) {
    println("ComponentIdentifier::fuzzyMatch(${ci})");
    def result = null;
    if ( ci['id'] ) {
      result = ComponentIdentifier.get(ci['id']);
    }
    result;
  }

  def bind(source) {
    grailsWebDataBinder.bind(this,source);
  }

}
