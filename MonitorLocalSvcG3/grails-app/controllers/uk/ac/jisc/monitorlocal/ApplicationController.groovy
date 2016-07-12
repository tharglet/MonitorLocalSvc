package uk.ac.jisc.monitorlocal

import grails.converters.*
import grails.core.GrailsApplication
import grails.plugins.*
import grails.plugin.springsecurity.annotation.Secured


import com.k_int.grails.tools.finance.YahooRatesService


class ApplicationController implements PluginManagerAware {
  static responseFormats = ['json', 'xml']

  GrailsApplication grailsApplication
  GrailsPluginManager pluginManager
  YahooRatesService yahooRatesService

  def kbplusSyncService
  def crossrefSyncService
  def apcSheetImportService
  def springSecurityService

  def index() {
    [grailsApplication: grailsApplication, pluginManager: pluginManager]
  }

  /**
   *  /application/command/kbplus -- Sync with kb+
   *  /application/command/AOWizard?doi=DOITOLOOKUP -- Create a new AO based on the DOI
   */
  def command() {
    def response = [:]
    log.debug("Command ${params}");
    switch(params.id) {
      case 'kbplus':
        log.debug("Trigger KB+ Sync");
        kbplusSyncService.getLatestKBPlusTitles()
        break;
      case 'AOWizard':
        log.debug("AO Wizardi -- example :: curl -v -d \"doi=10.7567/ssdm.1986.c-3-1\" http://localhost:8080/application/command/AOWizard");
        if ( params.doi ) {
          crossrefSyncService.crossrefWizzard(params.doi);
        }
        break;

      case 'exchange-rates':
        log.debug("Grab the exchange rates from Yahoo");

      // Fetched all rates
        response = yahooRatesService.getAllRates()
        break;
      default:
        log.debug("Unhandled command ${params.id}");
        break;
    }

    render response as JSON
  }


  /**
   *  curl -v --user admin:admin -X POST \ --form content=@./YBP1.tsv http://localhost:8080/application/apcIngest
   *
   */
  def ApcIngest() {

    def result = [:]
    result.messages = []

    log.debug("ApcIngest ${params}");

    def upload_mime_type = request.getFile("content")?.contentType  // getPart?
    def upload_filename = request.getFile("content")?.getOriginalFilename()
    def new_datafile_id = null
    def org = Org.findByName(params.instname) ?: new Org(name:params.instname)

    // Set the type to HEI.
    org.setTypeFromString('HEI')
    org.save(flush:true, failOnError:true)

    if ( upload_mime_type && upload_filename && org ) {

      log.debug("got mime type, upload org is ${org}");

      // def deposit_token = java.util.UUID.randomUUID().toString();
      def upload_file = request.getFile("content");

      log.debug("Calling ingest");
      apcSheetImportService.assimilateApcSpreadsheet(org, upload_file.getInputStream(),upload_filename )

    }
    else {
      if ( org == null ){
        result.messages.add("Unable to locate org with name ${params.instname}. Please add and retry");
      }
    }

    render result as JSON

  }

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

    if ( org ) {
      def existing_affiliation = UserOrg.findByOrgAndUser(org, user)
      def requested_role = Role.findByAuthority(request.JSON.details.role)

      if ( existing_affiliation ) {
        log.debug("Found existing affiliation");
      }
      else {
        // Status == 1 is approved
        existing_affiliation = new UserOrg(org:org, user:user, formalRole:requested_role, status: 1).save(flush:true, failOnError:true)
        result.message="Affiliation Requested"
        result.org_id=org.id
        result.org=org.name
        result.role = request.JSON.details.role
      }
    }

    render result as JSON
  }
  
}
