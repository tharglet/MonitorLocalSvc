package com.k_int.monitor

import grails.test.mixin.TestFor
import spock.lang.Specification
import uk.ac.jisc.monitorlocal.*
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.databinding.SimpleMapDataBindingSource


/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class AOBindingTest extends Specification {
    
    def dataBinder

    def setup() {
        // Use Grails data binding
        dataBinder = applicationContext.getBean('grailsWebDataBinder')
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
        dataBinder.bind(ao, source)

        then:
        with(ao) {
            name == 'My AO Name 1'
        }
    }
}
