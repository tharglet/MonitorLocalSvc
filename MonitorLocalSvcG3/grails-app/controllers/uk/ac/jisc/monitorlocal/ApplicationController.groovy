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
  // @Secured(['ROLE_USER'])
  @Secured
  def settings () {

    def user = springSecurityService.currentUser
    log.debug("Application::Settings - user us ${user}");
    
    def rates = yahooRatesService.allRates
    
    // Grab the current currencies and exchange rates etc...
    respond ([
      currency : [
        base:   yahooRatesService.baseCurrency,
        rates:  rates,
        all:    rates.keySet()
      ],
      user : user.createUserDTO()
    ])
  }
}
