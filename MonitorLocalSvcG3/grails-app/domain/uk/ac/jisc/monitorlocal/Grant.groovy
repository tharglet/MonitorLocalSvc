package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j

@Log4j
@Resource(uri="/grant", superClass=ExtendedRestfulController)
class Grant extends Component {

    String code
    String internalCode
    Funder funder

    static constraints = {
    }

}
