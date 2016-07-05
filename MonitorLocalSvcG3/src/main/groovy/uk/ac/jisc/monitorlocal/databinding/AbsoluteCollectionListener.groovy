package uk.ac.jisc.monitorlocal.databinding

import grails.databinding.events.DataBindingListener
import grails.databinding.events.DataBindingListenerAdapter
import grails.util.GrailsNameUtils
import grails.web.databinding.GrailsWebDataBinder
import groovy.util.logging.Log4j

import java.lang.reflect.Field

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

import org.grails.core.artefact.DomainClassArtefactHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Log4j
class AbsoluteCollectionListener extends DataBindingListenerAdapter {

  @Autowired
  GrailsWebDataBinder binder

  @PostConstruct
  void init () {
    log.debug ("Instantiating and registering...")
    DataBindingListener[] listeners = new DataBindingListener[1]
    listeners[0] = this
    binder.setDataBindingListeners ( listeners )
  }

  protected Field getField(Class clazz, String fieldName) {
    Field field = null
    try {
      field = clazz.getDeclaredField(fieldName)
    } catch (NoSuchFieldException nsfe) {
      def superClass = clazz.getSuperclass()
      if(superClass != Object) {
        field = getField(superClass, fieldName)
      }
    }
    return field
  }

  protected Set findAnnotatedFields ( Class clazz ) {
    def results = []
    Class c = clazz
    while (c != null) {
      for (Field field : c.getDeclaredFields()) {
        if (field.isAnnotationPresent(AbsoluteCollection)) {
          results << field
        }
      }
      c = c.getSuperclass()
    }
    results
  }

  protected Set actOn ( obj ) {
    def results = []
    if (hasSpecialHeader()) {
      results = findAnnotatedFields (obj.class)
    }
    
    results
  }

  protected boolean hasSpecialHeader () {
    boolean headerPresent = false
    try {
      // Grab the request from the existing thread local in Spring.
      HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).request
  
      // Check the value of a specific header.
      headerPresent = request.getHeader("Binding-Source")?.toLowerCase() == 'ng-app'
      log.debug ("hasSpecialHeader ${headerPresent}")
    } catch ( IllegalStateException e ) {
      // This simply means no request was found bound to the thread. Some system processes
      // will validly not create a request. We can safely ignore this in the context of our listener,
      // and return false.
      headerPresent = false
    }
    headerPresent
  }

  public boolean supports(Class<?> clazz) {
    boolean supported = DomainClassArtefactHandler.isDomainClass(clazz)
    log.debug ("supports ${clazz} == ${supported}")
    supported
  }

  public Boolean beforeBinding(Object obj, Object errors) {
    actOn (obj)?.each { Field f ->
      if (obj."${f.name}" instanceof Collection ) {
        log.debug "Clearing property ${f.name} on ${obj}"
//        obj."${f.name}".clear()
        
        // clear doesn't work as you would expect it to. Lets remove each item.
        obj."${f.name}".collect().each {
          obj."removeFrom${GrailsNameUtils.getClassName(f.name)}" (it)
        }
      } else {
        log.error "AbsoluteCollection annotation used on none-collection type property ${f.name} on ${obj.class}"
      }
    }

    // Return true to state that we still want binding to continue no matter what happens above.
    true
  }
}
