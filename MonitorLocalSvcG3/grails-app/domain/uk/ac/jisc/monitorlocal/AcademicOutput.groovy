package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.identifiers.Identifier;
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

  User assignedTo
  Date contactDate = new Date()
  
  @Defaults([
    'Green', 'Gold', 'Gold Paid by Other'
  ])
  RefdataValue publicationRoute

  String publicationTitle
  
  String authorNameList

  @Defaults(['Published', 'Awaiting Publication', 'Unknown'])
  RefdataValue publicationStatus
  
  @Defaults(['Accepted', 'Rejected', 'Pending', 'Eligible'])
  RefdataValue apcFundingApproval
  Date apcFundingDate

  String publisherURL
  Date publishedDate
  
  @Defaults(['Submitted', 'Not Submitted'])
  RefdataValue publisherSubmissionStatus
  Date publisherSubmissionDate
  
  @Defaults(['Accepted', 'Rejected'])
  RefdataValue publisherResponse
  Date publisherResponseDate

  @Defaults(['CC BY', 'CC BY-SA', 'CC BY-ND', 'CC BY-NC', 'CC BY-NC-SA', 'CC BY-NC-ND'])
  RefdataValue licence

  @BindUsing({obj,source ->
    Org.orgBinder(obj.ownerInstitution, source['ownerInstitution'], true);
  })
  Org ownerInstitution

  // Ugh - hate this model - really would prefer publication to be separate to the AO
  Org publisher

  PublicationTitle publishedIn
  
  String localReference

  @Defaults(['Journal Article', 'Conference Paper'])
  RefdataValue outputType

  String journalIssueDate
  String journalVolume
  String journalIssue
  String journalPages

  String publicationPlace
  Date conferenceStartDate
  Date conferenceEndDate
  Org conferenceOrg

  @Defaults(['Delayed', 'Hybrid', 'Pure', 'None'])
  RefdataValue openAccessStatus

  @Defaults(['Yes', 'No'])
  RefdataValue verifiedAuthor

  Integer embargoPeriod = 0
  Date embargoEndDate

  @Defaults(['Yes', 'No'])
  RefdataValue acknowledgement

  @Defaults(['Yes', 'No'])
  RefdataValue accessStatement

  List academicOutputCosts = []
  
  Set names = []
  Set grants = []
  Set funds = []
  
  Set complianceRules = []
  Set deposits = []
  
  @Defaults(['Yes', 'No'])
  RefdataValue deposited

  static hasMany = [
    academicOutputCosts: CostItem,
    names: AoName,
    grants: AoGrant,
    funds: AoFunding,
    complianceRules: ComplianceRule,
    deposits: AoDeposit
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
    assignedTo nullable: true
    contactDate nullable: true
    publishedIn nullable: true
    publishedDate nullable: true
    publicationStatus nullable: true
    openAccessStatus nullable: true
    outputType nullable: true
    localReference nullable: true, blank:false
    publicationTitle nullable: true, blank:false
    publisherURL nullable: true, blank:false
    journalIssueDate nullable: true
    journalVolume nullable: true
    journalIssue nullable: true
    journalPages nullable: true
    publicationPlace nullable: true
    conferenceStartDate nullable: true
    conferenceEndDate nullable: true
    conferenceOrg nullable: true
    verifiedAuthor nullable: true
    authorNameList nullable: true
    deposited nullable: true
    
    licence nullable:true
    embargoPeriod nullable:true
    embargoEndDate nullable:true
    acknowledgement nullable:true
    accessStatement nullable:true
  }
  
  static mappedBy = [
    academicOutputCosts:'academicOutput',
    names:'academicOutput',
    grants:'academicOutput',
  ]

  static mapping = {
    academicOutputCosts sort:'category', order:'asc', cascade: "all"
//    namedRoles sort:'role', order:'asc', cascade: "all"
    names cascade: "all-delete-orphan"
    grants cascade: "all"
    funds cascade: "all-delete-orphan"
    deposits cascade: "all-delete-orphan"
    complianceRules cascade: "all-delete-orphan"
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
