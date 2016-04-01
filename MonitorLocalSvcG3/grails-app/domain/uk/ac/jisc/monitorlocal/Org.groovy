package uk.ac.jisc.monitorlocal

import grails.rest.Resource

import com.k_int.grails.tools.refdata.*
import com.k_int.grails.tools.rest.ExtendedRestfulController

@Resource(uri="/org", superClass=ExtendedRestfulController)
class Org extends Component {
  String address
  String taxCode
  
  @Defaults([
    'Organisation type 1',
    'Organisation type 2'
  ])
  RefdataValue type
}
