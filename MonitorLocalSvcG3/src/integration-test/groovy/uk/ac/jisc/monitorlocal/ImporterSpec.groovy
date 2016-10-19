package uk.ac.jisc.monitorlocal


import grails.test.mixin.integration.Integration
import grails.transaction.*
import grails.util.GrailsWebMockUtil

import org.grails.compiler.injection.test.IntegrationTestMixinTransformation
import org.grails.plugins.testing.GrailsMockMultipartFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder

import spock.lang.*


@Integration
@Transactional
@Stepwise
class ImporterSpec extends Specification {
  
  def orgs_data="""name,domain,address,type,id.jisc,id.ukprn,id.isni,id.ringgold,id.gokb,id.ncsu-olnd-uri,id.viaf,id.freebase-uri,id.lccn,id.dbpedia-uri,id.dnb-uri,id.juliet,id.doi,funder_group,membership_org,uk_api_key
University of Jisc,,,HEI,university_of_jisc,,,,,,,,,,,,,,true,PUT_REAL_API_KEY_HERE
"""
  
  def person_data="""org.id.jisc,person.surname,person.forenames,person.email,person.id.email,person.role,person.division,person.department
university_of_jisc,"Flintsone","Fred","fred.flintstone@no.jisc.domain.ac.uk","fred.flintstone@no.jisc.domain.ac.uk","ROLE_USER, ROLE_ADMIN","TestDiv0","Dept2"
"""
  
  @Autowired
  WebApplicationContext ctx

  def setup() {
    GrailsWebMockUtil.bindMockWebRequest(ctx)
  }

  def cleanup() {
    RequestContextHolder.resetRequestAttributes()
  }

  void "test orgs load"() {
    given: "A file of orgs for importing"
      // def file = new GrailsMockMultipartFile('content', 'orgs_data.csv', 'text/csv', orgs_data.getBytes())
      InternalApiController controller = new InternalApiController()
      def mockRequest = new MockMultipartHttpServletRequest()
      controller.metaClass.request = mockRequest
      def file = new MockMultipartFile('content', 'orgs_data.csv', 'text/csv', new ByteArrayInputStream(orgs_data.getBytes()))
      controller.request.addFile(file)

    when: "A user requests that orgs file be imported"
      controller.OrgsIngest()

    then: "Search for the University of Jisc in the db"
      Org o = Org.findByName('University of Jisc')
      
    expect: "We should have a match"
      o != null

  }
  
  void "test person load"() {
    given: "A file of people for importing"
      InternalApiController controller = new InternalApiController()
      def mockRequest = new MockMultipartHttpServletRequest()
      controller.metaClass.request = mockRequest
      
      def file = new GrailsMockMultipartFile('content', 'people_data.csv', 'text/csv', new ByteArrayInputStream(person_data.getBytes()))
      controller.request.addFile(file)
      
    when: "A user requests that people file be imported"
      controller.PersonIngest()
      
    then: "Search for Fred Flintstone in db by email address"
      Person p = Person.createCriteria().get {
        personContactDetails {
          eq "emailAddress", "fred.flintstone@no.jisc.domain.ac.uk"
        }
      }
    
    expect: "We should be able to Fred Flinstone attached to the University of Jisc"
      p.id
      p.firstName == 'Fred'
      p.surname == 'Flintsone'
      
      p.personContactDetails.find { ContactDetails c ->
        c.organisation.name == 'University of Jisc'
      }
  }
}
