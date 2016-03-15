package com.k_int.jwt


import grails.core.GrailsApplication
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import grails.transaction.Transactional

@Transactional
class CrossrefSyncService {

  // https://github.com/CrossRef/rest-api-doc/blob/master/rest_api.md


  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  def syncJournals() {
    // http://api.crossref.org/journals?offset=10&rows=100
    def crossref = new RESTClient("http://api.crossref.org")
    def rows_processed = 1
    def offset = 0
    while (rows_processed > 0 ) {
      rows_processed = 0;
      // Request 100 rows from offset
      res = es.get(path:"/journals",
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
}
