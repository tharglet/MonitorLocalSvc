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
          log.debug("AO Wizard");
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
}
