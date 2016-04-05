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


  def assimilateApcSpreadsheet(Org institution, InputStream is) {
    log.debug("assimilateApcSpreadsheet");

    def charset = 'ISO-8859-1' // 'UTF-8'
    def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),'\t' as char,'\0' as char)

    String[] header = csv.readNext()
    log.debug("Got header ${header}");
    int ctr = 0
    String[] nl=csv.readNext()
    int rownum = 0;

    // APC Columns
    // Date of initial application by author
    // Submitted by
    // University department
    // PubMed Central (PMC) ID
    // PubMed ID
    // DOI
    // Affiliated author
    // Publisher
    // Journal
    // ISSN
    // Type of publication
    // Article title
    // Date of publication
    // Fund that APC is paid from (1)
    // Fund that APC is paid from (2)
    // Fund that APC is paid from (3)
    // Funder of research (1)
    // Funder of research (2)
    // Funder of research (3)
    // Grant ID (1)
    // Grant ID (2)
    // Grant ID (3)
    // Date of APC payment
    // APC paid (actual currency) including VAT if charged
    // APC paid (actual currency) excluding VAT
    // VAT (actual currency)
    // Currency of APC
    // APC paid (�) including VAT if charged
    // APC paid (�) excluding VAT
    // VAT (�)
    // Additional publication costs (�)
    // "Discounts
    //  memberships & pre-payment agreements"
    // Amount of APC charged to COAF grant (include VAT if charged) in �
    // Amount of APC charged to RCUK OA fund (include VAT if charged) in �
    // Licence
    // Correct license applied
    // Problem-free open access publication
    // Notes
    while(nl!=null) {
      log.debug(nl);
      nl=csv.readNext()
    }
  }

}
