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
    'type':[action:'process', target:"type", subtype:'refdata', refdataCategory:'Org.Type'],
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
    'funder_group':[action:'process', target:"funder_group", subtype:'org'],
    'membership_org':[action:'process', target:"membership_org", subtype:'simple'],
    'uk_api_key':[action:'process', target:"uk_api_key",subtype:'simple'],
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
    'person.surname':[action:'process', target:'surname', subtype:'simple'],
    'person.forenames':[action:'process', target:'forenames', subtype:'simple'],
    'person.email':[action:'process', target:'email', subtype:'simple'],
    'person.division':[action:'process', target:'division', subtype:'simple'],
    'person.department':[action:'process', target:'department', subtype:'simple'],
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
          case 'refdata':
            if ( it && ( it.trim().length() > 0 ) )  {
              orgdata[cfg.target] = RefdataValue.lookupOrCreate(cfg.refdataCategory, it)
            }
            break;
          case 'org':
            if ( it && ( it.trim().length() > 0 ) )  {
              orgdata[cfg.target] = Org.findByName(it.trim()) ?: new Org(name:it).save(flush:true, failOnError:true);
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
          if ( orgdata.type ) {
            o.type = orgdata.type;
          }
          if ( orgdata.funder_group ) {
            o.funderGroup = orgdata.funder_group;
          }
          if ( orgdata.uk_api_key ) {
            o.monitorLocalAPIKey = orgdata.uk_api_key;
          }
          o.save(flush:true, failOnError:true);
        }
      }

      if ( rownum++ % 50 == 0 ) {
        cleanUpGorm()
      }

      nl=csv.readNext()
    }
  }

  def PersonIngest() {
    def result = null;

    def upload_mime_type = request.getFile("content")?.contentType
    def upload_filename = request.getFile("content")?.getOriginalFilename()

    if ( upload_mime_type && upload_filename ) {
      def upload_file = request.getFile("content");
      result = processPersonIngest(upload_file.getInputStream());
    }
    else {
      log.warn("No mimetype or filename ${upload_mime_type} or ${upload_filename}");
    }

    render result as JSON
  }

  private def processPersonIngest(InputStream is) {

    def result = [:]
    result.messages = []

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

    def persdata_list = []
    def valid = true;
    def rowctr = 1;

    // Load persdata
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

      def name="${persdata.surname}, ${persdata.forenames}"

      log.debug("Process person: ${persdata}");
      if ( ( name ) &&
           ( name.trim().length() > 0 ) &&
           ( persdata.pers_ids.size() > 0 ) &&
           ( persdata.org_ids.size() > 0 ) ) {
        // Try to look up the person
        def p_list = Component.lookupByIdentifierValue(persdata.pers_ids)
        if ( p_list.size() <= 1 ) {
          persdata_list.add(persdata) 
        }
        else {
          result.messages.add([msg:"ROW ${rowctr} Person matched multiple people in the database. ERROR.", rowdata:nl]);
          valid = false;
        }
      }
      else {
        valid = false
        result.messages.add([msg:"ROW ${rowctr} Failed validation, name, ids or org not present.", rowdata:nl]);
                             
      }
      rowctr++
      nl=csv.readNext()
    }

    if ( valid ) {

      persdata_list.each { persdata ->

        Component.withNewTransaction {

          def o_list = Component.lookupByIdentifierValue(persdata.org_ids)
          def o = o_list.size() == 1 ? o_list[0] : null
      
          def name="${persdata.surname}, ${persdata.forenames}"
          if ( ( o ) && ( o instanceof Org ) ) {

            if ( name ) {
              log.debug("Got org : ${o} lookupOrCreate ${name}");
              def person = Component.lookupOrCreate(uk.ac.jisc.monitorlocal.Person.class, name, persdata.pers_ids)
              person.ownerInstitution = o;
              person.firstName = persdata.forenames
              person.surname = persdata.surname
              person.save(flush:true, failOnError:true);

              // Find contact details for this person
              def contact_details = null
              if ( persdata.email ) {
                contact_details = person.personContactDetails.find { it.emailAddress == persdata.email }
                if ( contact_details ) {
                }
                else {
                  contact_details = new ContactDetails(person:person)
                }
                contact_details.emailAddress = persdata.email
                if ( o ) {
                  contact_details.organisation = o
                  contact_details.division = getInstitutionalRefdataValue(o, 'ContactDetails.Division', persdata.division)
                  contact_details.department = getInstitutionalRefdataValue(o, 'ContactDetails.Department', persdata.department)
                }
                contact_details.save(flush:true, failOnError:true)
              }
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
      result.status='SUCCESS';
    }
    else {
      result.status='FAILED';
    }

    result
  }

  private InstitutionalRefdataValue getInstitutionalRefdataValue(org, catname, val) {

    InstitutionalRefdataValue result = null;

    def cat = RefdataCategory.findByDescription(catname) ?: new RefdataCategory(description:catname).save(flush:true, failOnError:true);

    if ( cat ) {
      def q = InstitutionalRefdataValue.executeQuery('select irv from InstitutionalRefdataValue as irv where irv.ownerInstitution = :inst and irv.owner = :d and irv.value = :v',
                                                     [inst:org, d:cat, v:val.trim()]);
  
      if ( q.size() == 0 ) {
        log.debug("Unable to locate irv for \"${org}\" \"${cat}\" \"${val.trim()}\" - create new");
        result = new InstitutionalRefdataValue(value:val.trim(), owner:cat)
        result.ownerInstitution = org
        result.save(flush:true, failOnError:true);
 
        log.debug("Created new irv, owner institution of created object is ${result.ownerInstitution}");
      }
      else if (q.size() == 1 ) {
        log.debug("Looked up irv for \"${org}\" \"${cat}\" \"${val.trim()}\" - ${result}");
        result = (InstitutionalRefdataValue) q.get(0);
      }
      else {
        log.error("Matched multiple..");
      }
    }

    return result;
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
