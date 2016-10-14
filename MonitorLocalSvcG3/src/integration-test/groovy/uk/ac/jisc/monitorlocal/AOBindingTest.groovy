package uk.ac.jisc.monitorlocal

import grails.databinding.SimpleMapDataBindingSource
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.databinding.GrailsWebDataBinder

import org.springframework.beans.factory.annotation.*

import spock.lang.Specification


/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Integration
@Rollback
class AOBindingTest extends Specification {
    
    @Autowired
    GrailsWebDataBinder grailsWebDataBinder

    def setup() {
    }

    def cleanup() {
    }

    void "Binding name to AO should set name property"() {
        given:
          final AcademicOutput ao = new AcademicOutput()

        and:
          final SimpleMapDataBindingSource source = 
            [id: 1, 
             name: 'My AO Name 1',
             identifiers:[
               [
                 [ identifier:[namespace:[value:'doi'],value:'1234/5678/1234' ] ]
               ]
             ],
             ownerInstitution: [ name: 'SomeOrg' ]
            ]

        when:
          grailsWebDataBinder.bind(ao, source)

        then:
          with(ao) {
              name == 'My AO Name 1'
          }
    }
}
