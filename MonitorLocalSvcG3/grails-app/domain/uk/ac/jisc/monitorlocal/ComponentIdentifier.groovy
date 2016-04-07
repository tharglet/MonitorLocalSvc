package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import com.k_int.grails.tools.identifiers.Identifier
import org.grails.databinding.BindUsing
import grails.databinding.SimpleMapDataBindingSource
import javax.persistence.Transient
import groovy.util.logging.Log4j

@Log4j
class ComponentIdentifier {

  @BindUsing({obj,source ->
    log.debug("Bind identifier in componentIdentifier ${obj} ${source}");
    obj['identifier']
  })
  Identifier identifier
  Component component

  static constraints = {
    identifier nullable: false, blank:false
    component nullable: false, blank:false
  }

  static ComponentIdentifier fuzzyMatch(ci) {

    def result = null;

    if ( ci['id'] ) {
      println("lookup component identifier by id ${ci['id']}");
      result = ComponentIdentifier.get(ci['id']);
    }

    result;
  }

}
