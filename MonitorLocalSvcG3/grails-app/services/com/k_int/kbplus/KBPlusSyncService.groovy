package com.k_int.kbplus


import grails.core.GrailsApplication
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
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
    doSync('http://www.kbplus.ac.uk','/test/oai/titles', 'kbplus', 0) { r ->
      log.debug("Process record ${r}");
    }
  }

  public doSync(host, path, notificationTarget, prefix, ts) {
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
          uri.query = [ verb:'ListRecords', metadataPrefix: prefix ]
        }

        // response handler for a success response code:
        response.success = { resp, xml ->
          println resp.statusLine

          xml.'ListRecords'.'record'.each { r ->
            notificationTarget(r)
          }
        }
      }
    }
  }

}
