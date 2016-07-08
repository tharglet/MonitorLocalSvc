package uk.ac.jisc.monitorlocal

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import grails.converters.*
import grails.core.GrailsApplication
import grails.plugins.*
import au.com.bytecode.opencsv.CSVReader

import com.k_int.grails.tools.finance.YahooRatesService
import com.k_int.grails.tools.refdata.*


class InternalApiController implements PluginManagerAware {

  static responseFormats = ['json', 'xml']

  static orgs_import_cfg = [
    'name':[action:'process', target:"name", subtype:'simple'],
    'domain':[action:'process', target:"domain", subtype:'simple'],
    'address':[action:'process', target:"address", subtype:'simple'],
    'type':[action:'process', target:"type", subtype:'simple'],
    'id.jisc':[action:'process', target:"id", subtype:'id'],
    'id.ukprn':[action:'process', target:"id", subtype:'id'],
    'id.isni':[action:'process', target:"id", subtype:'id'],
    'id.ringgold':[action:'process', target:"id", subtype:'id'],
    'id.gokb':[action:'process', target:"id", subtype:'id'],
    'id.ncsu-olnd-uri':[action:'process', target:"id", subtype:'id'],
    'id.viaf':[action:'process', target:"id", subtype:'id'],
    'id.freebase-uri':[action:'process', target:"id", subtype:'id'],
    'id.lccn':[action:'process', target:"id", subtype:'id'],
    'id.dbpedia-uri':[action:'process', target:"id", subtype:'id'],
    'id.dnb-uri':[action:'process', target:"id", subtype:'id'],
    'id.juliet':[action:'process', target:"id", subtype:'id'],
    'id.doi':[action:'process', target:"id", subtype:'id'],
    'funder_group':[action:'process', target:"funder_group", subtype:'simple'],
    'membership_org':[action:'process', target:"membership_org", subtype:'simple']
  ]

  static person_import_cfg = [
    'org.id.gokb':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.jisc':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.ukprn':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.isni':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.ringold':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.gokb':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.ncsu-olnd-uri':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.viaf':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.freebase-uri':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.lccn':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.dbpedia-uri':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.dnb-uri':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.juliet':[action:'process', target:'org_ids', subtype:'id'],
    'org.id.doi':[action:'process', target:'org_ids', subtype:'id'],
    'person.name':[action:'process', target:'name', subtype:'simple'],
    'person.email':[action:'process', target:'email', subtype:'simple'],
    'person.id.email':[action:'process', target:'pers_ids', subtype:'id'],
    'person.id.orcid':[action:'process', target:'pers_ids', subtype:'id'],
    'person.role':[action:'process', target:'role', subtype:'simple']
  ]

  GrailsApplication grailsApplication
  GrailsPluginManager pluginManager
  YahooRatesService yahooRatesService

  def kbplusSyncService
  def crossrefSyncService
  def apcSheetImportService
  def springSecurityService
  def sessionFactory

  /**
   *  /application/command/kbplus -- Sync with kb+
   *  /application/command/AOWizard?doi=DOITOLOOKUP -- Create a new AO based on the DOI
   */
  def command() {
    def response = [:]
    log.debug("Command ${params}");
    switch(params.id) {
      case 'kbplus':
        log.debug("Trigger KB+ Sync");
        kbplusSyncService.getLatestKBPlusTitles()
        break;
      case 'AOWizard':
        log.debug("AO Wizardi -- example :: curl -v -d \"doi=10.7567/ssdm.1986.c-3-1\" http://localhost:8080/application/command/AOWizard");
        if ( params.doi ) {
          crossrefSyncService.crossrefWizzard(params.doi);
        }
        break;

      case 'exchange-rates':
        log.debug("Grab the exchange rates from Yahoo");

      // Fetched all rates
        response = yahooRatesService.getAllRates()
        break;
      default:
        log.debug("Unhandled command ${params.id}");
        break;
    }

    render response as JSON
  }


  /**
   *  curl -v --user admin:admin -X POST \ --form content=@./YBP1.tsv http://localhost:8080/application/apcIngest
   *
   */
  def ApcIngest() {

    def result = [:]
    result.messages = []

    log.debug("ApcIngest ${params}");

    def upload_mime_type = request.getFile("content")?.contentType  // getPart?
    def upload_filename = request.getFile("content")?.getOriginalFilename()
    def new_datafile_id = null
    def org = Org.findByName(params.instname) ?: new Org(name:params.instname)

    // Set the type to HEI.
    org.setTypeFromString('HEI')
    org.save(flush:true, failOnError:true)

    if ( upload_mime_type && upload_filename && org ) {

      log.debug("got mime type, upload org is ${org}");

      // def deposit_token = java.util.UUID.randomUUID().toString();
      def upload_file = request.getFile("content");

      log.debug("Calling ingest");
      apcSheetImportService.assimilateApcSpreadsheet(org, upload_file.getInputStream(),upload_filename )

    }
    else {
      if ( org == null ){
        result.messages.add("Unable to locate org with name ${params.instname}. Please add and retry");
      }
    }

    render result as JSON

  }


  def OrgsIngest() {
    def result = [ status:'OK' ]

    def upload_mime_type = request.getFile("content")?.contentType 
    def upload_filename = request.getFile("content")?.getOriginalFilename()

    if ( upload_mime_type && upload_filename ) {
      def upload_file = request.getFile("content");
      processOrgsIngest(upload_file.getInputStream());
    }
    else {
      log.warn("No mimetype or filename ${upload_mime_type} or ${upload_filename}");
    }

    render result as JSON
  }

  private def processOrgsIngest(InputStream is) {

    log.debug("assimilateOrgsData");

    // def charset = 'UTF-8' // 'ISO-8859-1' or 'UTF-8' // Windows-1252
    def charset = 'UTF-8'
    // def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),'\t' as char,'\0' as char)
    def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),',' as char,'"' as char)

    String[] header = csv.readNext()
    log.debug("Got header ${header}");
    int ctr = 0
    String[] nl=csv.readNext()
    int rownum = 0;
    while ( nl ) {
      log.debug(nl);
      def col = 0;
      def orgdata = [:]
      orgdata.identifiers = []
      nl.each {
        def cfg = orgs_import_cfg[header[col]]
        switch (cfg.subtype) {
          case 'simple':
            orgdata[cfg.target] = it
            break;
          case 'id':
            def id_components = header[col].split('\\.');
            if ( it.trim().size() > 0 ) {
              orgdata.identifiers.add([namespace:id_components[1], value:it?.trim()])
            }
            break;
          default:
            log.debug("Unhandled type ${cfg.type} for column ${header[col]} config ${cfg} value ${it}");
            break;
        }
        col++
      }

      log.debug("Process org: ${orgdata}");
      if ( ( orgdata.name ) &&
           ( orgdata.name.trim().length() > 0 ) &&
           ( orgdata.identifiers.size() > 0 ) ) {
        Org.withNewTransaction() {
          uk.ac.jisc.monitorlocal.Org o = Component.lookupOrCreate(uk.ac.jisc.monitorlocal.Org.class, orgdata.name, orgdata.identifiers)
        }
      }

      if ( rownum++ % 50 == 0 ) {
        cleanUpGorm()
      }

      nl=csv.readNext()
    }
  }

  def PersonIngest() {
    def result = [ status:'OK' ]

    def upload_mime_type = request.getFile("content")?.contentType
    def upload_filename = request.getFile("content")?.getOriginalFilename()

    if ( upload_mime_type && upload_filename ) {
      def upload_file = request.getFile("content");
      processPersonIngest(upload_file.getInputStream());
    }
    else {
      log.warn("No mimetype or filename ${upload_mime_type} or ${upload_filename}");
    }

    render result as JSON
  }

  private def processPersonIngest(InputStream is) {
    log.debug("processUserIngest");

    // def charset = 'UTF-8' // 'ISO-8859-1' or 'UTF-8' // Windows-1252
    def charset = 'UTF-8'
    // def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),'\t' as char,'\0' as char)
    def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),',' as char,'"' as char)

    String[] header = csv.readNext()
    log.debug("Got header ${header}");
    int ctr = 0
    String[] nl=csv.readNext()
    int rownum = 0;
    while ( nl ) {
      log.debug(nl);
      def col = 0;
      def persdata = [:]
      persdata.pers_ids = []
      persdata.org_ids = []

      nl.each {
        def cfg = person_import_cfg[header[col]?.trim()]
        if ( cfg ) {
          switch (cfg.subtype) {
            case 'simple':
              persdata[cfg.target] = it
              break;
            case 'id':
              def id_components = header[col].split('\\.');
              if ( it.trim().size() > 0 ) {
                persdata[cfg.target].add([namespace:id_components[2], value:it?.trim()])
              }
              break;
            default:
              log.debug("Unhandled type ${cfg.type} for column ${header[col]} config ${cfg} value ${it}");
              break;
          }
        }
        else {
          log.warn("Unable to lookup config for ${header[col]}");
        }

        col++
      }

      log.debug("Process person: ${persdata}");
      if ( ( persdata.name ) &&
           ( persdata.name.trim().length() > 0 ) &&
           ( persdata.pers_ids.size() > 0 ) &&
           ( persdata.org_ids.size() > 0 ) ) {
        Component.withNewTransaction {

          def o_list = Component.lookupByIdentifierValue(persdata.org_ids)
          def o = o_list.size() == 1 ? o_list[0] : null
      
          if ( ( o ) && ( o instanceof Org ) ) {
            if ( persdata.name ) {
              log.debug("Got org : ${o} lookupOrCreate ${persdata.name}");
              def person = Component.lookupOrCreate(uk.ac.jisc.monitorlocal.Person.class, persdata.name, persdata.pers_ids)
              person.institution = o;
              person.save(flush:true, failOnError:true);
            }
            else {
              log.warn("No person name.. cannot process");
            }
          }
          else {
            log.debug("Unable to lookup org for ${persdata.org_ids}");
          }
        }
      }

      nl=csv.readNext()
    }

  }

  private def cleanUpGorm() {
    // log.debug("Clean up GORM");

    // Get the current session.
    def session = sessionFactory.currentSession

    // flush and clear the session.
    session.flush()
    session.clear()
  }

}
