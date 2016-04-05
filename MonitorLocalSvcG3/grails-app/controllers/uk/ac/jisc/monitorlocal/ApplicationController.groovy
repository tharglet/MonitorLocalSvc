package uk.ac.jisc.monitorlocal

import grails.core.GrailsApplication
import grails.util.Environment
import grails.plugins.*
import grails.converters.*

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager
    def kbplusSyncService
    def crossrefSyncService
    def apcSheetImportService

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

    log.debug("ApcIngest ${params}");

    def upload_mime_type = request.getFile("content")?.contentType  // getPart?
    def upload_filename = request.getFile("content")?.getOriginalFilename()
    def new_datafile_id = null

    if ( upload_mime_type && upload_filename ) {

      log.debug("got mime type");

      // def deposit_token = java.util.UUID.randomUUID().toString();
      def upload_file = request.getFile("content");

      log.debug("Calling ingest");
      apcSheetImportService.assimilateApcSpreadsheet(upload_file.getInputStream() )
    }

    render result as JSON

  }
}
