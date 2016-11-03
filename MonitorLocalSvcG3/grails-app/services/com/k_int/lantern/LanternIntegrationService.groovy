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
  static String new_aos = '''
select ao from AcademicOutput as ao 
left outer join ao.lanternCheckStatus as lcs
join ao.identifiers as ci
where ( ao.lanternCheckStatus is null OR lcs.value = 'Unchecked' )
  AND ci.identifier.namespace.value = 'DOI'
  AND ao.ownerInstitution.lanternAPIKey is not null
'''

  def grailsApplication
  def grailsWebDataBinder

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  /**
   * Find all AOs with a lantern status of unchecked where a DOI is present
   */
  def checkAOsNeedingLookup() {
    log.debug("Looking for AOs needing a lantern lookup");
    def aos_needing_lantern_lookup = AcademicOutput.executeQuery(new_aos)
    aos_needing_lantern_lookup.each { ao ->
      ao.getIdentifierValues('DOI').each { doi ->
        fetchLanternRecordForDOI(doi, ao.ownerInstitution.lanternAPIKey, null, ao.id);
      }
    }
  }

  def fetchLanternRecordForDOI(String doi, String lanternApiKey, response_email, localId) {

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
            pendingRequests.add([job:json.data.job, apiKey:lanternApiKey, localId:localId])
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
        enrichAOWithLanternData(pr.localId, json);
      }

      response.error = { err ->
        log.error("Problem talking to monitor UK service ${err}");
      }
    }
  }

  def enrichAOWithLanternData(ao_id, lantern_record) {
    try {
      log.debug("enrichAOWithLanternData(${ao_id}, ${lantern_record}");
      def academic_output = AcademicOutput.get(ao_id)
      if ( academic_output ) {
        // Use cute data binding to turn string into refdata value
        academic_output.lanternCheckStatus='Checked'

        academic_output.save(flush:true, failOnError:true)
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      log.debug("enrichAOWithLanternData complete");
    }
  }
}

