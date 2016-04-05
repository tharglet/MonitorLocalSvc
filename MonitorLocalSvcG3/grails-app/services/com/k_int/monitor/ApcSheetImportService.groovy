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
class ApcSheetImportService {

  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def assimilateApcSpreadsheet(File f) {
    log.debug("assimilateApcSpreadsheet");
  }

}
