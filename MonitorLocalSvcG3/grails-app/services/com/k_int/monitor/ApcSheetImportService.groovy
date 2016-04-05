package com.k_int.monitor


import grails.core.GrailsApplication
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import grails.transaction.Transactional

import groovyx.net.http.*
import uk.ac.jisc.monitorlocal.*;

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.bean.CsvToBean
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy
import java.text.SimpleDateFormat


@Transactional
class ApcSheetImportService {

  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def assimilateApcSpreadsheet(InputStream is) {
    log.debug("assimilateApcSpreadsheet");

    def charset = 'ISO-8859-1' // 'UTF-8'
    def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),'\t' as char,'\0' as char)

    String[] header = csv.readNext()
    log.debug("Got header ${header}");
    int ctr = 0
    String[] nl=csv.readNext()
    int rownum = 0;
    while(nl!=null) {
      log.debug(nl);
      nl=csv.readNext()
    }
  }

}
