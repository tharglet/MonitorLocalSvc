package uk.ac.jisc.monitorlocal;


import grails.databinding.SimpleMapDataBindingSource
import grails.rest.Resource
import groovy.util.logging.Log4j

import org.grails.databinding.BindUsing
import org.hibernate.proxy.HibernateProxy

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController


@Log4j
@Resource(uri="/ao", superClass=ExtendedRestfulController)
class AcademicOutput extends Component {
  
  static searchable = {
    type component: true
  }

  Person assignedTo
  Date contactDate
  
  @Defaults([
    'Green', 'Gold', 'Gold Paid by Other'
  ])
  RefdataValue publicationRoute
  
  @Defaults(['Accepted', 'Rejected', 'Unfunded', 'Unknown (follow up with author)'])
  RefdataValue apcFundingApproval
  Date apcFundingDate
  
  @Defaults(['Submitted', 'Not Submitted'])
  RefdataValue publisherSubmissionStatus
  Date publisherSubmissionDate
  
  @Defaults(['Accepted', 'Rejected'])
  RefdataValue publisherResponse
  Date publisherResponseDate

  @Defaults(['CC BY-SA'])
  RefdataValue license

  @BindUsing({obj,source ->
    Org.orgBinder(obj.ownerInstitution, new SimpleMapDataBindingSource(source['ownerInstitution']), true);
  })
  Org ownerInstitution

  // Ugh - hate this model - really would prefer publication to be separate to the AO
  Org publisher

  
  List academicOutputCosts = []
  static hasMany = [
    academicOutputCosts: CostItem,
    names: AOName,
    grants: AOGrant
  ]

  static constraints = {
    publicationRoute nullable: true
    apcFundingApproval nullable: true
    apcFundingDate nullable: true
    publisherSubmissionStatus nullable: true
    publisherSubmissionDate nullable: true
    publisherResponse nullable: true
    publisherResponseDate nullable: true
    ownerInstitution nullable: true
    publisher nullable: true
    license nullable: true
    assignedTo nullable: true
    contactDate nullable: true
  }
  
  static mappedBy = [
    academicOutputCosts:'academicOutput',
    names:'academicOutput',
    grants:'academicOutput'
  ]

  static mapping = {
    academicOutputCosts sort:'category', order:'asc', cascade: "all"
//    namedRoles sort:'role', order:'asc', cascade: "all"
  }

  // The default list of properties to exclude when data binding
  static String[] getExcludeList() {
    []
  }

  /**
   *  Try and match an AO using identifies or other combinations of properties that might yield a unique match.
   *  Input is the generic ao datamodel. This function codifies the business rules for looking up an AO by value. 
   *  Ideally, it will be the *only* place we codify these rules.
   *
   *  {
   *    name: AONAME
   *    identifiers:[
   *      { namespace: { value:NS}, value:VALUE }
   *    ]
   */
  static AcademicOutput fuzzyMatch(ao) {

    log.debug("fuzzyMatch(${ao})");
    def result

    if ( ao.id ) {
      result = AcademicOutput.get(ao.id)
    }

    if ( !result ) {
      def ids = []
      ao.identifiers?.each { id_occurrence ->
        // log.debug("Lookup by identifier ${id_occurrence.identifier.namespace.value} ${id_occurrence.identifier.value}");
        ids.add( [ namespace: id_occurrence.identifier.namespace.value, value:id_occurrence.identifier.value ] );
      }

      if ( ids.size() > 0 ) {
        def matching_aos = AcademicOutput.lookupByIdentifierValue(ids);
        switch(matching_aos.size() ) {
          case 0:
            break;
          case 1:
            result = matching_aos.get(0);
            if (result instanceof HibernateProxy) {
              result = (AcademicOutput) ((HibernateProxy) result).getHibernateLazyInitializer().getImplementation();
            }
            break;
          default:
            throw new RuntimeException("Identifiers match multiple components - unable to continue");
            break;
        }
      }
    }

    return result;
  }
}
