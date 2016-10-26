package uk.ac.jisc.monitorlocal

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.GrailsClassUtils
import grails.util.Holders
import grails.web.databinding.GrailsWebDataBinder
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import javax.persistence.Transient

import org.grails.databinding.BindUsing
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter

import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection

import com.k_int.grails.tools.identifiers.Identifier
import com.k_int.grails.tools.rules.DomainRulePropertySource



@Log4j
@EqualsAndHashCode(includes=["id"])
class Component  implements DomainRulePropertySource{
  static auditable = [ignore:['version','lastUpdated','created','lastUpdatedBy','createdBy']]
  static transients = ["springSecurityService"]
  
  static lookupBase = 'ownedComponents'
  
  static namedQueries = {
    ownedComponents {
      or {
        isNull ("ownerInstitution") 
        
        def currentAffiliation =  Holders.applicationContext.getBean("springSecurityService", SpringSecurityService)?.currentUser?.getUserOrg()
        if (currentAffiliation?.id != null) {
          eq ("ownerInstitution",currentAffiliation)
        }
      }
    }
  }
  
  @Transient
  protected static SpringSecurityService getSpringSecurityService () {
    Holders.applicationContext.getBean("springSecurityService", SpringSecurityService)
  }
  
  Date created
  User createdBy
  Date lastUpdated
  User lastUpdatedBy

  @BindUsing({obj,source ->
    Org.orgBinder(obj.ownerInstitution, source['ownerInstitution'], true);
  })
  Org ownerInstitution
  
  def beforeInsert () {
    def now = new Date()
    if (created == null) {
      created = now
    }
    
    if (createdBy == null && springSecurityService.currentUser != null) {
      createdBy =  springSecurityService.currentUser
    }
    // Run the onchange too, passing in the dates so they match.!
    beforeUpdate (now)
  }
  
  def beforeUpdate(Date changed = new Date()) {
    lastUpdated = changed
    lastUpdatedBy = springSecurityService.currentUser ?: null
  }
  
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
  @AbsoluteCollection
  Set identifiers = []
  
  @AbsoluteCollection
  Set notes = []

  static hasMany = [
    identifiers:ComponentIdentifier,
    notes:Note
  ]

  static mappedBy = [
    identifiers:'component',
    notes:'owner'
  ]

  static constraints = {
    name nullable: false, blank:false,maxSize:512
    createdBy nullable: true, bindable:false
    lastUpdatedBy nullable: true, bindable:false
    created nullable: true, bindable:false
    lastUpdated nullable: true, bindable:false
    ownerInstitution nullable: true
  }

  static mapping = {
    identifiers cascade: "all-delete-orphan"
    tablePerHierarchy false
    notes cascade: "all-delete-orphan", sort: 'created', order: 'desc'
  }

  public String toString() {
    return this.getName()
  }

  public static Component lookupOrCreate(Class cls, String name, List identifiers) {
    def result

    def existingComponents = lookupByIdentifierValue(identifiers)

    switch ( existingComponents.size() ) {
      case 0:
      
        // Create a new object in the list. Then drop through to set the Identifiers.
        existingComponents = [cls.newInstance('name': name)]
      case 1:
        result = existingComponents.get(0)
        
        // Ensure all identifiers are added to new and existing components.
        identifiers.each {
          def new_identifier = Identifier.lookupOrCreate(it.namespace, it.value)
          def new_ci = new ComponentIdentifier()
          new_ci.identifier=new_identifier
          result.addToIdentifiers(new_ci)
        }
        
        // Save...
        result.save(flush:true, failOnError:true)
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
      sw.write("select distinct c from Component as c join c.identifiers as i where ")

      def bindvars = [:]
      identifiers.eachWithIndex { id, idx ->
        
        // OR
        if ( idx > 0 ) {
          sw.write(' or ');
        }
        
        // Positional params have been deprecated in HQL in favour of named params
        sw.write(" ( i.identifier.namespace.value = :nsVal${idx} AND i.identifier.value = :idVal${idx} ) ")
        bindvars["nsVal${idx}"] = (id.namespace)
        bindvars["idVal${idx}"] = (id.value)
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
