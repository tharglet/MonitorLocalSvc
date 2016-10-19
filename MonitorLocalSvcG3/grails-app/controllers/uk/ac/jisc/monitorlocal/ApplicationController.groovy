package uk.ac.jisc.monitorlocal

import static groovyx.net.http.Method.GET
import grails.converters.*
import grails.core.GrailsApplication
import grails.plugins.*

import com.k_int.grails.tools.finance.YahooRatesService
import com.k_int.grails.tools.refdata.RefdataValue


class ApplicationController { // implements PluginManagerAware {
  static responseFormats = ['json', 'xml']

  YahooRatesService yahooRatesService

  def crossrefSyncService
  def springSecurityService

  /**
   * This method is where we'll add the app config. This will eventually
   * be behind auth and can therefore be used to return role based permissions etc...
   */
  def settings () {

    def user = springSecurityService.currentUser
    log.debug("Application::Settings - user us ${user}");
    
    [yahooRatesService: yahooRatesService, user: user]
  }


  def requestAffiliation() {
    def result = [:]
    def user = springSecurityService.currentUser
    log.debug("Application::requestAffiliation - ${request.JSON}");

    // [details:[institution:[lastUpdated:2016-07-12T08:13:09+0000, created:2016-07-12T08:13:09+0000, name:University of Jisc, id:3]]]
    def org = Org.get(request.JSON.details.institution.id)

    if ( user && org ) {
      def existing_affiliation = UserOrg.findByOrgAndUser(org, user)
      def requested_role = Role.findByAuthority(request.JSON.details.role)

      if ( existing_affiliation ) {
        log.debug("Found existing affiliation");
      }
      else {
        // Status 0=Pending, 1=Approved, 2=Rejected, 3=AutoApproved
        existing_affiliation = new UserOrg(org:org, user:user, formalRole:requested_role, status: 0).save(flush:true, failOnError:true)
        result.message="Affiliation Requested"
        result.org_id=org.id
        result.org=org.name
        result.role = request.JSON.details.role
      }
    }

    render result as JSON
  }
  
  /**
   *  Only callable by admin
   *  /application/setAffiliationStatus?aid=99&s=[0,1,2,3]
   *  aid = affiliation record id (UserOrg)
   *  s = status code - 0=Pending, 1=Approved, 2=Rejected, 3=AutoApproved
   */
  def setAffiliationStatus() {
    def result = [:]
    def user = springSecurityService.currentUser
    log.debug("Application::requestAffiliation - ${user} ${params}");
    if ( ( user ) && (request.isUserInRole("ROLE_ADMIN") && params.int('s') ) ) {
      def affiliation = UserOrg.get(params.aid)
      if ( affiliation ) {
        affiliation.status = params.int('s')
        result.status=0
        result.message="Affiliation updated"
      }
      else {
        result.message="Attempt to approve non-existent affiliation ${params.aid}";
        result.status = 1;
        log.warn("Attempt to approve non-existent affiliation ${params.aid}");
      }
    }
    else {
      log.warn("Attempt to approve affiliation by non-admin user ${user} ${params}");
      result.message="Only admin users may approve affiliation requests"
      result.status = 1;
    }

    respond result
  }


  /**
   *  POST a json document in containing 1 map entry of doi:'value to lookup'
   *  return a json document containing {
   *    containerType
   *    containerTitle
   *    itemTitle
   *    issn
   *    eissn
   *    doi
   *    authorNames
   *    message
   *    identifiers : [ { namespace:'ns', value:'value}, {...}, {...} ]
   */
  def crossrefLookup() {
    def result = [:]
    log.debug("crossrefLookup ${params} ${request.JSON}");
    try {
      if ( ( request.JSON ) && ( request.JSON.doi ) ) {
        def r = crossrefSyncService.lookupDOI(request.JSON.doi)
        log.debug("Result of crossref lookup ${r}");

        if ( r ) {

          result.r = r;
        
          // See if we can find any journalTitle using the ISSNs crossref have supplied for us. 
          // If we can, thats what we should use.
          def issns = r.message.ISSN

          result.containerType=r.message.type;
          result.containerTitle=r.message.'container-title'[0];
          result.itemTitle=r.message.title[0];
          result.doi=r.message.DOI;

          // Add all authors to name list
          result.authorNames = []
          r.message.author.each { a ->
            result.authorNames.add([given:a.given, family:a.family])
          }

          def publications = PublicationTitle.executeQuery('select distinct t from PublicationTitle as t join t.identifiers as id where id.identifier.value in ( :v )',[v:issns]);
          def publication = null;

          switch ( publications.size() ) {
            case 0:
              log.debug("No pub match");
              break;
            case 1:
              publication = publications.getAt(0)
              break;
            default:
              log.error("Matched ${publications.size()} Identifiers for ${issns}");
       
          }

          if ( publication ) {
            log.debug("Got publication title");
            result.publication_id = publication.id;
            result.message="Title ${publication.name} matched with a publication in the monitor database via crossref supplied identifiers";
            log.debug("Pub has ${publication.identifiers.size()} ids");
            result.identifiers = []
            publication.getIdentifiers().each { pid ->
              log.debug("Add Pub ID ${pid.id} ${pid.identifier.id} ${pid.identifier.namespace.value} ${pid.identifier.value}");
              result.identifiers.add([namespace:pid.identifier.namespace.value, value:pid.identifier.value]);
            }
          }
          else {
            log.debug("Unable to match a publication with identifiers ${issns}");
            result.message="Unable to match a journal in monitor from crossref supplied identifiers. The information used may not be correct"
          }

          def jatype = null;
          switch ( r.message.type ) {
            case 'journal-article':
              jatype = RefdataValue.lookupOrCreate('AcademicOutput.OutputType', 'Journal Article');
              break;
            case 'conference-paper':
              jatype = RefdataValue.lookupOrCreate('AcademicOutput.OutputType', 'Conference Paper');
              break;
            default:
              jatype = RefdataValue.lookupOrCreate('AcademicOutput.OutputType', 'Unknown');
          }
          result.type=[id:jatype?.id, value:jatype?.value];
        }
      }
      else {
        log.warn("crossrefLookup called without { doi:'.....' }");
      }
    }
    catch ( Exception e ) {
      log.error("Unexpected error looking up DOI",e)
      result.message="Unexpected error looking up DOI : "+e.message;
    }
    render result as JSON
  }
}
