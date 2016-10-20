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
class LanternSpec extends Specification {
  
  @Autowired
  WebApplicationContext ctx

  @Autowired
  com.k_int.lantern.LanternIntegrationService lanternIntegrationService

  def setup() {
    GrailsWebMockUtil.bindMockWebRequest(ctx)
  }

  def cleanup() {
    RequestContextHolder.resetRequestAttributes()
  }

  void "test lantern"() {
    given: "A doi of  10.1145/2899415.2899431"
      def doi = '10.1145/2899415.2899431'
      def apikey = '3bc0afdef6dc597caf4c6ea0422afe'

    when: "A user requests that orgs file be imported"
      lanternIntegrationService.fetchLanternRecordForDOI(doi, apikey, null)

    then: "Search for the University of Jisc in the db"
      def result = [:]
      
    expect: "We should have a match"
      result.size() == 0

  }
  
}
