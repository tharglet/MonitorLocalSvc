package com.k_int.lantern

import grails.core.GrailsApplication
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.converters.*
import grails.transaction.Transactional
import org.springframework.transaction.TransactionStatus
import com.k_int.grails.tools.refdata.*

@Transactional
public class LanternIntegrationService {

  static def pendingRequests = []

  def grailsApplication
  def grailsWebDataBinder

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def fetchLanternRecordForDOI(String doi, String lanternApiKey, response_email) {

    def result = [:]

    if ( lanternApiKey ) {

      def http = new HTTPBuilder( grailsApplication.config.lantern.apiUrl )
      http.ignoreSSLIssues()
      http.headers = [Accept : 'application/json']

      def qry = [ 'api_key' : lanternApiKey ]

      http.request( POST ) { req ->
        uri.path = '/service/lantern'
        uri.query = qry
        contentType=groovyx.net.http.ContentType.JSON
        requestContentType=groovyx.net.http.ContentType.JSON
        body=[
          "email": response_email,
          "filename": "MonitorLocalIntegration",
          "list" : [
              [
                  "DOI" : doi
              ]
          ]
        ]

        // response handler for a success response code:
        response.success = { resp, json ->
          log.debug("Lantern Response :: ${json}");
          if ( json.status == 'success' ) {
            log.debug("Got job : json.data.job")
            pendingRequests.add([job:json.data.job, apiKey:lanternApiKey])
          }
        }

        response.error = { err ->
          log.error("Problem talking to monitor UK service ${err}");
        }

      }
    }

    result
  }

  def checkPendingRequests() {

    def completed_requests = []

    if ( pendingRequests.size() > 0 ) {
      def http = new HTTPBuilder( grailsApplication.config.lantern.apiUrl )
      http.ignoreSSLIssues()
      http.contentType = groovyx.net.http.ContentType.JSON
      http.headers = [Accept : 'application/json']


      pendingRequests.each { pr ->

        log.debug("Check pending request  ${pr}");

        def qry = [ 'api_key' : pr.apiKey ]
        http.request( POST, groovyx.net.http.ContentType.JSON ) { req ->
          uri.path = pr.job'/progress'
          uri.query = qry
          contentType=groovyx.net.http.ContentType.JSON

          // response handler for a success response code:
          response.success = { resp, json ->
            log.debug("Lantern Response :: ${json}");
            if ( json.progress?.progress == 100 ) {
              log.debug("Job Completed")
              completed_requests.remove(pr)
              getLanternResult(pr)
            }
          }

          response.error = { err ->
            log.error("Problem talking to monitor UK service ${err}");
          }

        }

      }
    }

    pendingRequests.removeAll(completed_requests);
  }

  def getLanternResult(pr) {
    def http = new HTTPBuilder( grailsApplication.config.lantern.apiUrl )
    http.ignoreSSLIssues()
    http.contentType = groovyx.net.http.ContentType.JSON
    http.headers = [Accept : 'application/json']
    http.request( POST, groovyx.net.http.ContentType.JSON ) { req ->
      uri.path = pr.job'/results'
      uri.query = qry
      contentType=groovyx.net.http.ContentType.JSON

      response.success = { resp, json ->
        log.debug("Lantern Response :: ${json}");
      }

      response.error = { err ->
        log.error("Problem talking to monitor UK service ${err}");
      }
    }
  }
}

