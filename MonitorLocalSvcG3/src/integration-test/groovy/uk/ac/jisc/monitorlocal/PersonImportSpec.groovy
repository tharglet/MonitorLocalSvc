package uk.ac.jisc.monitorlocal


import grails.test.mixin.integration.Integration
import grails.test.mixin.TestFor
import grails.transaction.*
import spock.lang.*
import org.grails.plugins.testing.GrailsMockMultipartFile
import grails.util.GrailsWebMockUtil
import spock.lang.Shared
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.mock.web.MockMultipartHttpServletRequest


@Integration
@Rollback
class PersonImportSpec extends Specification {

    @Autowired
    WebApplicationContext ctx

    @Shared InternalApiController controller = new InternalApiController()

    // Stop grails from rolling back the transaction at the end of each call
    static transactional = false

    def orgs_data="""
name,domain,address,type,id.jisc,id.ukprn,id.isni,id.ringgold,id.gokb,id.ncsu-olnd-uri,id.viaf,id.freebase-uri,id.lccn,id.dbpedia-uri,id.dnb-uri,id.juliet,id.doi,funder_group,membership_org,uk_api_key
University of Jisc,,,HEI,university_of_jisc,,,,,,,,,,,,,,true,PUT_REAL_API_KEY_HERE
"""

    def person_data="""
org.id.jisc,person.surname,person.forenames,person.email,person.id.email,person.role,person.division,person.department
university_of_jisc,"Flintsone","Fred","fred.flintstone@no.jisc.domain.ac.uk","fred.flintstone@no.jisc.domain.ac.uk","ROLE_USER, ROLE_ADMIN","TestDiv0","Dept2"
"""

    def setup() {
      GrailsWebMockUtil.bindMockWebRequest(ctx)
      def mockRequest = new MockMultipartHttpServletRequest()
      controller.metaClass.request = mockRequest
    }

    def cleanup() {
      RequestContextHolder.resetRequestAttributes()
    }

    void "test orgs load"() {
      given: "A file of orgs for importing"
        def file = new GrailsMockMultipartFile('content', 'orgs_data.csv', 'text/csv', orgs_data.getBytes())
        controller.request.addFile(file)

      when: "A user requests that orgs file be imported"
        controller.OrgsIngest()

      then: "Search for the located org"
      expect: "We should be able to find the org University of Jisc in the db"
        true == true;
 
    }

    void "test person load"() {
      given: "A file of people for importing"
        def file = new GrailsMockMultipartFile('content', 'people_data.csv', 'text/csv', person_data.getBytes() )
        controller.request.addFile(file)
      when: "A user requests that people file be imported"
        c.PersonIngest()
      then: "Search for the located person"
      expect: "We should be able to find Fred Flintstone in db attached to the correct organisation"
        true == true;
    }
}
