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

  def grailsApplication
  def grailsWebDataBinder

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def fetchLanternRecordForDOI(String doi) {
    def result = [:]
    // Stub implementation in public repo - see lantern feature branch for current implementation / testing
    result
  }
}

