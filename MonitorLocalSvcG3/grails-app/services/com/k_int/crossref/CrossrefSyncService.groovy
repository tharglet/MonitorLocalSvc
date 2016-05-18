package com.k_int.crossref


import grails.core.GrailsApplication
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import grails.transaction.Transactional

import groovyx.net.http.*
import uk.ac.jisc.monitorlocal.*;

@Transactional
class CrossrefSyncService {

  // https://github.com/CrossRef/rest-api-doc/blob/master/rest_api.md


  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  /**
   * Take a DOI and look up as much of an AO as is possible, creating the initial AO before
   * proceeding.
   */
  def crossrefWizzard(doi) {

    log.debug("crossrefWizzard(${doi})");

    def result = null;

    def crossref_info = lookupDOI(doi);

    // if we had a positive lookup
    if ( crossref_info ) {

      def publication_type = null;
      if ( crossref_info.message.type )
        publication_type = com.k_int.grails.tools.refdata.RefdataValue.lookupOrCreate('AcademicOutput.Type',crossref_info.message.type);

      result = new AcademicOutput(name:crossref_info.message.title, type:publication_type);
      result.save(flush:true, failOnError:true)

      // Item may be published in crossref_info.message.container_title

      crossref_info.message.author.each { it ->
        // Resolve the author name if we are able
        Person p = null;
        def author_entry = new AoName (academicOutput:result,person:p,name:"${it.given?:''} ${it.family?:''}").save(flush:true)
      }

      // Add AONames
    }

    result
  }

  def syncJournals() {
    // http://api.crossref.org/journals?offset=10&rows=100
    def crossref = new RESTClient("http://api.crossref.org")
    def rows_processed = 1
    def offset = 0
    while (rows_processed > 0 ) {
      rows_processed = 0;
      // Request 100 rows from offset
      res = crossref.get(path:"/journals",
                   query:[
                     offset:offset,
                     rows:100
                   ]);

      res.data.items.each { crossref_journal_record ->
        // N.B. ISSN is an array of values
        log.debug("Got journal: ${crossref_journal_record.title} / ${crossref_journal_record.publisher} / ${crossref_journal_record.issn}");
        rows_processed++
      }
    }
  }

  def syncArticles() {
    // http://api.crossref.org/works?offset=10&rows=100
    
  }

  // Example http://api.crossref.org/works/10.7567/ssdm.1986.c-3-1
  def lookupDOI(doi) {
    log.debug("Crossref lookup doi : ${doi}");
    def result = null;
    try {
      def crossref = new RESTClient("http://api.crossref.org")

      // Request 100 rows from offset
      def res = crossref.get(path:"/works/"+doi,query:[:]);
      result = res.data;
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    log.debug("Returning ${result}");

    result;
  }
}
