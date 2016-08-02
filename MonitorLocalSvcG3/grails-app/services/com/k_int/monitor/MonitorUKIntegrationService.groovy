package com.k_int.monitor


import grails.core.GrailsApplication
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.converters.*
import grails.transaction.Transactional
import uk.ac.jisc.monitorlocal.*
import org.springframework.transaction.TransactionStatus

import com.k_int.grails.tools.refdata.*

@Transactional
class MonitorUKIntegrationService {

  def grailsApplication
  def grailsWebDataBinder

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  // https://github.com/JiscMonitor/monitor-uk/blob/develop/docs/API/CONTRIBUTION.md
  def pushToUK(ao) {
    log.debug("Push to MUK: ${ao} at ${grailsApplication.config.monitor.monitorUk} APIKEY:${ao.ownerInstitution.monitorLocalAPIKey}");

    if ( ao.ownerInstitution.monitorLocalAPIKey ) {

      def http = new HTTPBuilder( grailsApplication.config.monitor.monitorUk )
      http.ignoreSSLIssues()
      http.contentType = JSON
      http.headers = [Accept : 'application/json']

      def qry = [ 'api_key' : ao.ownerInstitution.monitorLocalAPIKey ]

      // def record = [
      //   record : [
      //     "dc:identifier" : [
      //          {"type" : "pmcid", "id" : "<europe pubmed central id>"},
      //          {"type" : "pmid", "id" : "<pubmed id>"},
      //          {"type" : "doi", "id" : "<doi>"},
      //          {"type" : "url", "id" : "<url to object>"}
      //      ],
      //      "rioxxterms:type" : "<publication type (article, etc) - ideally from rioxx guidelines>",
      //      "dc:title" : "<title>",
      //      "dc:subject" : ["<list of subject categories, ideally standardised>"],
      //      "rioxxterms:version" : "<rioxx resource version - from guidelines>",
      //      "rioxxterms:author" : [
      //          [
      //              "name" : "<author name>",
      //              "identifier" : [
      //                  ["type" : "orcid", "id" : "<author's orcid>"],
      //                  ["type" : "email", "id" : "<author's email address>"],
      //                  ["type" : "<identifier type>", "id" : "<author identifier>"]
      //              ],
      //              "affiliation" : [
      //                  [
      //                      "name" : "<name of organisation>",
      //                      "identifier" : [
      //                          ["type" : "<identifier type>", "id" : "<organisation identifier>"]
      //                      ]
      //                  ]
      //              ]
      //          ]
      //      ]
      //   ]
      // ]

      http.request( POST, JSON ) { req ->
        uri.path = '/api/v1/apc'
        uri.query = qry
        contentType=JSON
        body=ao.toRioxx()

        // response handler for a success response code:
        response.success = { resp, json ->
          log.debug("OK ${json}");
        }

        response.error = { err ->
          log.error("Problem talking to monitor UK service ${err}");
        }

      }
    }
   

  }

}
