package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController
import groovy.util.logging.Log4j

@Log4j
@Resource(uri="/funder", superClass=ExtendedRestfulController)
class Funder extends Component {


    @Defaults([
            'Charity Open Access Fund (COAF)',
            'Research Councils UK (RCUK)',
            'Higher Education Funding Council for England (HEFCE)',
            'Horizon 2020 Institution Fund'
    ])
    RefdataValue group

    static constraints = {
    }

}
