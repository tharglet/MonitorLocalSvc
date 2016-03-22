package com.k_int.kbplus


import grails.core.GrailsApplication
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import uk.ac.jisc.monitorlocal.*
import grails.transaction.Transactional

import groovyx.net.http.*

@Transactional
class KbplusSyncService {

  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def getLatestKBPlusTitles() {
    def cursor = SyncCursor.findByActivity('KBPlusTitles') ?: new SyncCursor(activity:'KBPlusTitles').save(flush:true, failOnError:true);

    doSync('http://www.kbplus.ac.uk','/test/oai/titles', 'kbplus', cursor) { r ->
      def result = [:]
      log.debug("Process record ${r}");
      result
    }
  }

  public doSync(host, path, notificationTarget, prefix, cursor) {
    println("Get latest changes");

    def http = new HTTPBuilder( host )

    def more = true
    println("Attempt get...");
    def resumption=null

    // perform a GET request, expecting XML response data
    while ( more ) {
      http.request( GET, XML ) {
        uri.path = path
        if ( resumption ) {
          uri.query = [ verb:'ListRecords', resumptionToken: resumption ]
        }
        else {
          def the_date = new Date(cursor.highestTimestamp)
          def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
          def from=sdf.format(the_date)
          log.debug("Requesting records since ${cursor.highestTimestamp} :: ${from}");
          uri.query = [ verb:'ListRecords', metadataPrefix: prefix, from:from ]
        }

        // response handler for a success response code:
        response.success = { resp, xml ->
          println resp.statusLine

          xml.'ListRecords'.'record'.each { r ->
            def clr = notificationTarget(r)
            log.debug(clr);
          }
        }

        // update cursor
        cursor.lastTimestamp = highestTimestamp
        cursor.save(flush:true, failOnError:true);
      }
    }
  }

}
