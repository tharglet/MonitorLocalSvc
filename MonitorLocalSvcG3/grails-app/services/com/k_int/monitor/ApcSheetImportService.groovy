package com.k_int.monitor


import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import grails.converters.*
import grails.databinding.SimpleMapDataBindingSource
import grails.transaction.Transactional
import groovyx.net.http.*
import uk.ac.jisc.monitorlocal.*
import au.com.bytecode.opencsv.CSVReader
import org.springframework.transaction.TransactionStatus

import com.k_int.grails.tools.refdata.*

@Transactional
class ApcSheetImportService {

  def grailsApplication
  def grailsWebDataBinder

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }


  def assimilateApcSpreadsheet(Org institution, InputStream is, String filename) {
    log.debug("assimilateApcSpreadsheet");

    // def charset = 'UTF-8' // 'ISO-8859-1' or 'UTF-8' // Windows-1252
    def charset = 'Windows-1252'
    // def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),'\t' as char,'\0' as char)
    def csv = new CSVReader(new InputStreamReader(new org.apache.commons.io.input.BOMInputStream(is),java.nio.charset.Charset.forName(charset)),',' as char,'"' as char)

    String[] header = csv.readNext()
    log.debug("Got header ${header}");
    int ctr = 0
    String[] nl=csv.readNext()
    int rownum = 0;

    // APC Columns


    //  0. Date of initial application by author
    //  1. Submitted by
    //  2. University department
    //  3. PubMed Central (PMC) ID
    //  4. PubMed ID
    //  5. DOI
    //  6. Affiliated author
    //  7. Publisher
    //  8. Journal
    //  9. ISSN
    // 10. Type of publication
    // 11. Article title
    // 12. Date of publication
    // Fund that APC is paid from (1)
    // Fund that APC is paid from (2)
    // Fund that APC is paid from (3)
    // Funder of research (1)
    // Funder of research (2)
    // Funder of research (3)
    // Grant ID (1)
    // Grant ID (2)
    // Grant ID (3)
    // 22. Date of APC payment
    // 23. APC paid (actual currency) including VAT if charged
    // 24. APC paid (actual currency) excluding VAT
    // 25. VAT (actual currency)
    // 26. Currency of APC
    // APC paid (GBP) including VAT if charged
    // APC paid (GBP) excluding VAT
    // VAT (GBP)
    // 30. Additional publication costs (GBP)
    // 31. "Discounts, memberships & pre-payment agreements"
    // Amount of APC charged to COAF grant (include VAT if charged) in �
    // Amount of APC charged to RCUK OA fund (include VAT if charged) in �
    // 34. Licence
    // 35. Correct license applied
    // 36. Problem-free open access publication
    // 37. Notes

    while(nl!=null) {
      log.debug(nl);

      if ( nl.length > 11 ) {

        // Must have a doi and a title to be able to process the row
        if ( ( nl[5] != null ) && 
             ( nl[5].trim().length() > 0 ) &&
             ( nl[11] != null ) && 
             ( nl[11].trim().length() > 0 ) ) {
    
          AcademicOutput.withNewTransaction { TransactionStatus status ->
    
            def aoRecord = [
              ownerInstitution: [
                id : institution.id
              ],
              name:nl[11],
              identifiers:[
                [
                  identifier:[namespace:[value:'doi'],value:nl[5]],
                ]
              ],
            ]
    
            log.debug("Add AO: ${aoRecord}");
            def ao = AcademicOutput.fuzzyMatch(aoRecord)
            if ( ao == null ) {
              log.debug("Create new AO");
              ao = new AcademicOutput()
            }
            log.debug("Bind...");
            def dbs = new SimpleMapDataBindingSource(aoRecord)
            grailsWebDataBinder.bind(ao, dbs) // , null, AcademicOutput.getExcludeList())
            log.debug("Save...");
            ao.save(flush:true, failOnError:true);
    
            // Established baseline for AO - now process remaining data
            // nl[1] - Submitted by - Name of local person submitting APC
            // Lookup or create a person record within this institution
            if ( ( nl[1] != null ) && ( nl[1].trim().length() > 0 ) ) {
              Person person = null;
              AoName name = new AoName(academicOutput:ao, name:nl[1], person:person)
              name.setNamerelFromString('SubmittedBy')
              name.save(flush:true, failOnError:true)
            }
    
            if ( ( nl[6] != null ) && ( nl[6].trim().length() > 0 ) ) {
              Person person = null;
              AoName name = new AoName(academicOutput:ao, name:nl[6], person:person)
              name.setNamerelFromString('Author')
              name.save(flush:true, failOnError:true)
            }
    
            if ( ( nl[7] != null ) && ( nl[7].trim().length() > 0 ) ) {
              def publisher = Org.findByName(nl[7])
              if (publisher) {
                publisher.setTypeFromString('Publisher')
              }
              ao.publisher = publisher;
            }

            // 8. Journal
            // 9. ISSN
            if ( ( nl[9] != null ) && ( nl[9].trim().length() > 0 ) ) {
              def identifiers = []
              identifiers.add([namespace:'issn',value:nl[9].trim()]);
              def pubs =  Component.lookupByIdentifierValue(identifiers);
              def pub = pubs.size() == 1 ? pubs[0] : null;
 
              // If we didn't find based on ISSN, and we have a title, create a publication.
              if ( ( pub == null ) && ( nl[8].trim().length() > 0 ) ) {
                pub = PublicationTitle.lookupOrCreate(nl[8].trim(), identifiers);
              }

              ao.publishedIn=pub;
            }
    
            // APC Spreadsheet allows three separate sets of values for grant information, process each one here
            [13,14,15].each { fund ->
              if ( ( nl[fund+3] != null ) &&  ( nl[fund+3].trim().length() > 0 ) ) {
                Org funder = Org.findByName(nl[fund+3]) ?: new Org(name:nl[fund+3])
                
                if (funder) {
                  // We should set the type of the org to funder.
                  funder.setTypeFromString('Funder')
                  funder.save(flush:true, failOnError:true)
                }
                
                // The other properties.
                def fundVal = (nl[fund]?.length() > 0 ? nl[fund] : null)
                def grantId = (nl[fund+6]?.length() > 0 ? nl[fund+6] : null)
                
                // Find Grant
                AoGrant grant = AoGrant.findByFunderAndFundAndGrantId (
                  funder,
                  fundVal,
                  grantId
                )
                
                if (!grant) {
                  // Create a new Grant
                  grant = new AoGrant(funder:(funder),fund:fundVal,grantId:(grantId))
                }

                
                // Associate with AO and save.
                grant.academicOutput = ao
                grant.save(flush:true, failOnError:true)
              }
            }
    
            if ( ( nl[34] ) && ( nl[34].trim().length() > 0 ) ) {
              ao.setLicenceFromString( "${nl[34]}" )
            }
    
            // Column 18 -- Date of APC Payment

            // 19 APC paid (actual currency) including VAT if charged
            // def apc_cost_grossValue = new MonetaryValue(value:nl[19])

            // 20 APC paid (actual currency) excluding VAT
            // 21 VAT (actual currency)
            // 22 Currency of APC

            // def apc_cost_item_actual = new CostItem(grossValue:apc_cost_grossValue)

            // 23 APC paid (GBP) including VAT if charged
            // 24 APC paid (GBP) excluding VAT
            // 25 VAT (GBP)
            // 26 Additional publication costs (GBP)


            // Process cost items  -- ao.addToAcademicOutputCosts()
            ao.save(flush:true, failOnError:true);

            def note = new Note(owner:ao,note:'Created by APC/TCO Sheet Import of file '+filename)
            note.save(flush:true, failOnError:true)
          }
        }
        else {
          log.error("Bad row at ${filename}:${rownum} title and or doi missing. Unable to process");
        }
      }
      else {
        log.error("insufficient columns at ${filename}:${rownum}. Unable to process");
      }

      nl=csv.readNext()
      rownum++
    }
  }

}
