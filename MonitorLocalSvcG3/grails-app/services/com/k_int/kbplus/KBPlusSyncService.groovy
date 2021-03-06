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

    def cursor = SyncCursor.findByActivity('KBPlusTitles') ?: new SyncCursor(activity:'KBPlusTitles', lastTimestamp:0).save(flush:true, failOnError:true);

    log.debug("Got cursor ${cursor}");

    doSync('https://www.kbplus.ac.uk','/test/oai/titles', 'kbplus', cursor) { r ->

      def result = [:]

      try {
        PublicationTitle.withNewTransaction {
          def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

          def record_ts_str = r.header.datestamp.text()
          result.ts = sdf.parse(record_ts_str)?.getTime()

          def identifiers = []
          log.debug("Process record ${r}");
          def title_id = r.metadata.kbplus.title.@id.text()
          def title = r.metadata.kbplus.title.title.text()
          def pub_name = r.metadata.kbplus.publisher.name.text()

          println("${title_id} ${title} ${pub_name}");
          r.metadata.kbplus.identifiers.identifier.each {
            log.debug("id ${it.@namespace} ${it.@value}");
            identifiers.add([namespace:it.@namespace.text(),value:it.@value.text()]);
          }

          log.debug("call PublicationTitle.lookupOrCreate");
          def t = PublicationTitle.lookupOrCreate(title, identifiers);
          result.title_id = t.id

          def note = new Note(owner:t,note:'Created/Updated by KBPlus Title Sync')
          note.save(flush:true, failOnError:true)

        }
      }
      catch ( Exception e ) {
        result.message = e.message;
      }

      result
    }
  }

  public doSync(host, path, prefix, cursor, notificationTarget) {
    println("Get latest changes ${host} ${path} ${prefix} ${cursor}");

    def http = new HTTPBuilder( host )
    http.ignoreSSLIssues()
    http.contentType = XML
    http.headers = [Accept : 'application/xml']
    def lastTimestamp = 0;

    def more = true
    println("Attempt get...");

    def resumption=null

    // perform a GET request, expecting XML response data
    while ( more ) {

      println("Make request....");

      def qry = null
      if ( resumption ) {
        log.debug("Processing resumption");
        qry = [ verb:'ListRecords', resumptionToken: resumption ]
      }
      else {
        log.debug("Fetch all records since ${cursor.lastTimestamp}");
        def the_date = new Date(cursor.lastTimestamp)
        def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        def from=sdf.format(the_date)
        log.debug("Requesting records since ${cursor.lastTimestamp} :: ${from}");
        qry = [ verb:'ListRecords', metadataPrefix: prefix, from:from ]
      }

      println("Query params : ${qry} ");

      http.request( GET, XML ) { req ->

        uri.path = path
        uri.query = qry 
        contentType=XML

        // response handler for a success response code:
        response.success = { resp, xml ->
          int ctr=0
          println("In response handler");
          println("Status ${resp.statusLine}")

          def slurper = new groovy.util.XmlSlurper()
          // def parsed_xml = slurper.parseText(xml.text)
          def parsed_xml = slurper.parse(xml)

          parsed_xml?.'ListRecords'?.'record'.each { r ->
            def clr = notificationTarget(r)
            println(clr);
            ctr++
            if ( clr.ts > lastTimestamp )
              lastTimestamp = clr.ts
          }

          if ( ctr > 0 ) {
            more=true
          }
          else {
            more=false
          }

          resumption = parsed_xml?.'ListRecords'?.'resumptionToken'
          println("Complete ${ctr} ${more} ${resumption}");
        }

        response.error = { err ->
          println(err)
        }
      }
      // update cursor
      cursor.lastTimestamp = lastTimestamp
      cursor.save(flush:true, failOnError:true);
    }
  }

}
