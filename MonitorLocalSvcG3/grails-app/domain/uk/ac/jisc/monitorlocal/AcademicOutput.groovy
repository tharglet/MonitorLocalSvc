package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import groovy.util.logging.Log4j

import org.hibernate.proxy.HibernateProxy

import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection
import uk.ac.jisc.monitorlocal.rest.AcademicOutputRestfulController

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import com.k_int.grails.tools.rules.RulesService
import com.k_int.grails.tools.utils.MapUtils


@Log4j
@Resource(uri="/ao", superClass=AcademicOutputRestfulController)
class AcademicOutput extends Component {
  
  static transients = ["rulesService"]
  
  static searchable = {
    type component: true
  }
  
  static namedQueries = {
    Component.namedQueries()
  }

  User assignedTo
  Date contactDate = new Date()
  RulesService rulesService
  
  @Defaults([
    'Green', 'Gold', 'Gold Paid by Other'
  ])
  RefdataValue publicationRoute

  String publicationTitle
  
  String authorNameList

  @Defaults(['Published', 'Awaiting Publication', 'Unknown'])
  RefdataValue publicationStatus
  
  @Defaults(['Accepted', 'Rejected', 'Pending', 'Eligible'])
  RefdataValue apcFundingApproval
  Date apcFundingDate

  String publisherURL
  Date publishedDate
  
  @Defaults(['Submitted', 'Not Submitted'])
  RefdataValue publisherSubmissionStatus
  Date publisherSubmissionDate
  
  @Defaults(['Accepted', 'Rejected'])
  RefdataValue publisherResponse
  Date publisherResponseDate

  @Defaults(['CC BY', 'CC BY-SA', 'CC BY-ND', 'CC BY-NC', 'CC BY-NC-SA', 'CC BY-NC-ND', 'Other'])
  RefdataValue licence

  // Ugh - hate this model - really would prefer publication to be separate to the AO
  Org publisher

  PublicationTitle publishedIn
  
  String localReference

  @Defaults(['Journal Article', 'Conference Paper'])
  RefdataValue outputType

  String journalIssueDate
  String journalVolume
  String journalIssue
  String journalPages

  String publicationPlace
  Date conferenceStartDate
  Date conferenceEndDate
  String conferenceOrg

  @Defaults(['Delayed', 'Hybrid', 'Pure', 'None'])
  RefdataValue openAccessStatus

  @Defaults(['Yes', 'No'])
  RefdataValue verifiedAuthor

  Integer embargoPeriod = 0
  Date embargoEndDate

  @Defaults(['Yes', 'No'])
  RefdataValue acknowledgement

  @Defaults(['Yes', 'No'])
  RefdataValue accessStatement

  @AbsoluteCollection
  Set academicOutputCosts = []
  
  @AbsoluteCollection
  Set names = []
  
  @AbsoluteCollection
  Set funds = []
  
  @AbsoluteCollection
  Set deposits = []
  
  @Defaults(['Yes', 'No'])
  RefdataValue deposited
  
  Boolean complianceStatus
  Boolean workflowStatus = false
  
  def beforeValidate () {
    
    // Check compliance..
    calculateComplainceStatus()
    calculateWorkflowStatus()
  }
  
  transient void calculateComplainceStatus () {
    
    // Init the results.
    Map<String, ?> results = runRules()
    if (!results) results = [:]
    
    // Set the lowest value.
    Boolean lowestVal = true
    
    // Go through each rule result and just look for the lowest value.
    MapUtils.flattenMap(results)?.each { k, v ->
      if (k.endsWith('.result')) {
        if (lowestVal != v) {
          if (lowestVal == true) {
              lowestVal = v
          } else if (lowestVal == false) {
            if (v != true) {
              lowestVal = v
            }
          } else {
            // Lowest val == null
            lowestVal = null
          }
        }
      }
    }
    
    complianceStatus = lowestVal
  }
  
  transient void calculateWorkflowStatus () {
    
    // This should be called after calculating the compliance status.
    // We use that calculated value as the default as it may avoid us having to run the rules.
    boolean value = complianceStatus
    def wf = MapUtils.flattenMap(runWorkflowRules())
    
    for (int i=0; i<wf.size() && value; i++) {
      value = (wf[i] != false)
    }
    
    workflowStatus = value
  }
  
  
  transient Set<String> complianceRules = null
  
  transient public Set<String> getApplicableComplianceRules() {
    
    // With AOs we also include extra rules depending on the value of the publication route.
    String route = publicationRoute?.value?.replaceAll("^(\\S+).*", "\$1")
    
    // Get all the applied rule sets for this AO.
    if (complianceRules == null) { 
      complianceRules = []
      for (AoFunding funding : funds) {
        funding?.grant?.funder?.each { Org funder ->
          
          // Start with this org and keep going through funderGroups.
          while (funder) {
            // Add all the rules denoted here.          
            funder.appliedComplianceRuleSets?.each { String ruleSet ->
              // The rules exist we should add them.
              complianceRules << ruleSet
              
              // AOs also include extra rules if the rule def isn't recursive (i.e. does not end with '*'
              // and the publication route has been set.
              if (route && !ruleSet.endsWith("*")) {
                // Check for particular values.
                String inclusiveKey = "${ruleSet}.${route}*"
                
                if (rulesService.keyExists(inclusiveKey)) {
                  complianceRules << inclusiveKey
                }
              }
            }
            
            // Check the funding group.
            funder = funder.funderGroup
          }
        }
      }
    }
    
    complianceRules
  }
  
  public Map<String, ?> runRules () {
    
    def applicableRules = applicableComplianceRules
    
    // Run the rules.
    rulesService.flattenRuleKeys(
      rulesService.runRules(applicableRules, this)
    )
  }
  
  public Map<String, ?> runWorkflowRules () {
    // Run the workflow rules.
    rulesService.runRules('workflow*', this)
  }

  static hasMany = [
    academicOutputCosts: CostItem,
    names: AoName,
    funds: AoFunding,
    deposits: AoDeposit
  ]

  static constraints = {
    Component.constraints.rehydrate (delegate, owner, thisObject).call()
    publicationRoute nullable: true
    apcFundingApproval nullable: true
    apcFundingDate nullable: true
    publisherSubmissionStatus nullable: true
    publisherSubmissionDate nullable: true
    publisherResponse nullable: true
    publisherResponseDate nullable: true
    publisher nullable: true
    assignedTo nullable: true
    contactDate nullable: true
    publishedIn nullable: true
    publishedDate nullable: true
    publicationStatus nullable: true
    openAccessStatus nullable: true
    outputType nullable: true
    localReference nullable: true, blank:false
    publicationTitle nullable: true, blank:false
    publisherURL nullable: true, blank:false
    journalIssueDate nullable: true
    journalVolume nullable: true
    journalIssue nullable: true
    journalPages nullable: true
    publicationPlace nullable: true
    conferenceStartDate nullable: true
    conferenceEndDate nullable: true
    conferenceOrg nullable: true
    verifiedAuthor nullable: true
    authorNameList nullable: true
    deposited nullable: true
    licence nullable:true
    embargoPeriod nullable:true
    embargoEndDate nullable:true
    acknowledgement nullable:true
    accessStatement nullable:true
    complianceStatus nullable: true
    workflowStatus nullable: false
  }
  
  static mappedBy = [
    names:'academicOutput',
    grants:'academicOutput',
    academicOutputCosts:'academicOutput',
  ]

  static mapping = {
    Component.mapping.rehydrate (delegate, owner, thisObject).call()
    // namedRoles sort:'role', order:'asc', cascade: "all"
    names cascade: "all-delete-orphan"
    grants cascade: "all"
    funds cascade: "all-delete-orphan"
    deposits cascade: "all-delete-orphan"
    academicOutputCosts cascade: 'all', sort: 'id', order: 'asc'
  }

  // The default list of properties to exclude when data binding
  static String[] getExcludeList() {
    []
  }

  /**
   *  Try and match an AO using identifies or other combinations of properties that might yield a unique match.
   *  Input is the generic ao datamodel. This function codifies the business rules for looking up an AO by value. 
   *  Ideally, it will be the *only* place we codify these rules.
   *
   *  {
   *    name: AONAME
   *    identifiers:[
   *      { namespace: { value:NS}, value:VALUE }
   *    ]
   */
  static AcademicOutput fuzzyMatch(ao) {

    log.debug("fuzzyMatch(${ao})");
    def result

    if ( ao.id ) {
      result = AcademicOutput.get(ao.id)
    }

    if ( !result ) {
      def ids = []
      ao.identifiers?.each { id_occurrence ->
        // log.debug("Lookup by identifier ${id_occurrence.identifier.namespace.value} ${id_occurrence.identifier.value}");
        ids.add( [ namespace: id_occurrence.identifier.namespace.value, value:id_occurrence.identifier.value ] );
      }

      if ( ids.size() > 0 ) {
        def matching_aos = AcademicOutput.lookupByIdentifierValue(ids);
        switch(matching_aos.size() ) {
          case 0:
            break;
          case 1:
            result = matching_aos.get(0);
            if (result instanceof HibernateProxy) {
              result = (AcademicOutput) ((HibernateProxy) result).getHibernateLazyInitializer().getImplementation();
            }
            break;
          default:
            throw new RuntimeException("Identifiers match multiple components - unable to continue");
            break;
        }
      }
    }

    return result;
  }

  public static Map getSearchConfig() {
    log.debug("getSearchConfig()");

    return [
      baseclass:'uk.ac.jisc.monitorlocal.AcademicOutput',
      useDistinct: true,
      title:'AO',
      group:'Secondary',
      defaultSort:'name',
      defaultOrder:'asc',
      qbeConfig:[
        qbeForm:[
          [
            prompt:'Search',
            qparam:'q',
            placeholder:'Search Academic Outputs',
            contextTree: [ 'ctxtp':'disjunctive', 
              'terms':[
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'identifiers.identifier.value', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'assignedTo.name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'names.person.personContactDetails.department', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'names.person.firstName', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'names.person.surname', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'authorNameList', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'publishedIn.name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'publisher.name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funds.grant.name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funds.grant.grantId', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funds.grant.internalGrantId', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'funds.grant.funder.name', 'wildcard':'B'],
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'assignedTo.name', 'wildcard':'B']
              ]
            ]
          ],
          [
            prompt:'Workflow Complete',
            qparam:'wfc',
            placeholder:'Workflow Complete',
            contextTree: [ 'ctxtp':'filter', 'comparator' : 'eq', 'prop':'workflowStatus' ],
          ],
          [
            expose: false,
            prompt:'Owner Institution',
            qparam:'instCtx',
            placeholder:'Owner Institution',
            contextTree: [ 'ctxtp':'qry', 'comparator' : 'eq', 'prop':'ownerInstitution.id' ],
          ],
          [
            expose: false,
            prompt:'Id',
            qparam:'id',
            placeholder:'ID',
            contextTree: [ 'ctxtp':'qry', 'comparator' : 'eq', 'prop':'id' ],
          ]
        ],
        qbeGlobals:[
        ],
        qbeResults:[
          [heading:'Name', property:'name',sort:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ],
          [heading:'Status', sort:'status', property:'status.value'],
        ]
      ]
    ]
  }


        // def record = [
      //   record : [
      //     "dc:identifier" : [
      //          {"type" : "pmcid", "id" : "<europe pubmed central id>"},
      //          {"type" : "pmid", "id" : "<pubmed id>"},
      //          {"type" : "doi", "id" : "<doi>"},
      //          {"type" : "url", "id" : "<url to object>"}
      //      ],
      //      "rioxxterms:type" : "<publication type (article, etc) - ideally from rioxx guidelines>",
      //      "dc:title" : "<title>",
      //      "dc:subject" : ["<list of subject categories, ideally standardised>"],
      //      "rioxxterms:version" : "<rioxx resource version - from guidelines>",
      //      "rioxxterms:author" : [
      //          [
      //              "name" : "<author name>",
      //              "identifier" : [
      //                  ["type" : "orcid", "id" : "<author's orcid>"],
      //                  ["type" : "email", "id" : "<author's email address>"],
      //                  ["type" : "<identifier type>", "id" : "<author identifier>"]
      //              ],
      //              "affiliation" : [
      //                  [
      //                      "name" : "<name of organisation>",
      //                      "identifier" : [
      //                          ["type" : "<identifier type>", "id" : "<organisation identifier>"]
      //                      ]
      //                  ]
      //              ]
      //          ]
      //      ]
      //   ]
      // ]

  // Create a rioxx themed record as described at https://github.com/JiscMonitor/monitor-uk/blob/develop/docs/system/DATA_MODELS.md
  transient toRioxx() {
    def result = [:]
    result.'dc:identifier' = []
    result.'dc:title' = this.name
    result.'dc:subject' = [] // List of subjects if present
    result.'rioxxterms:type' = outputType?.value
    result.'rioxxterms:author' = []

    this.identifiers.each { component_id ->
      result."dc:identifier".add(['type':component_id.identifier.namespace.value,'id':component_id.identifier.value]);
    }

    this.names.each { name ->
      result.'rioxxterms:author'.add(['name':name.name, 'identifier':[],'affiliation':[]])
    }

    if ( this.publisher ) {
      result.'dcterms:publisher'=[
                 'name':publisher.name,
                 'identifier':[]]  // list of  {"type" : "<identifier type>", "id" : "<publisher identifier>"}
    }
    
    result
  }
  
  def afterUpdate() {
//    CostItem.withNewSession {
      CostItem.tidyOrphans()
//    }
  }

}
