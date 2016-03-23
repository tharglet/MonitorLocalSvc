package uk.ac.jisc.monitorlocal

import grails.core.GrailsApplication
import grails.util.Environment
import grails.plugins.*
import grails.converters.*

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager
    def kbplusSyncService

    def index() {
        [grailsApplication: grailsApplication, pluginManager: pluginManager]
    }

    def command() {
      def response = [:]
      log.debug("Command ${params}");
      switch(params.id) {
        case 'kbplus':
          log.debug("Trigger KB+ Sync");
          kbplusSyncService.getLatestKBPlusTitles()
          break;
        default:
          log.debug("Unhandled command ${params.id}");
          break;
      }

      render response as JSON
    }
}
