package uk.ac.jisc.monitorlocal

import grails.rest.Resource
import com.k_int.grails.tools.rest.ExtendedRestfulController
/**
 *  A Grant attached to an AO - May contain between one and all of:
 *  A funder, the fund name and the grant ID 
 */
@Resource(uri="/grant", superClass=ExtendedRestfulController)
class AOGrant extends Component {
  
  AcademicOutput academicOutput
  CostItem costItem
  Org funder
  String fund
  String grantId
  String internalGrantId
  
  static mappedBy = [
    costItem: 'grants',
    academicOutput: 'grants',
  ]
  
  def beforeValidate () {
    // Set the name if it isn't already.
     if( !name ) {
      // Lets update the name
      def nameStr = "${funder?.name ?: ''}:${fund ? ' ' + fund : ''}"
      if (grantId) {
        nameStr += " / ${grantId}"
      }
      if (internalGrantId) {
        nameStr += " / ${internalGrantId}"
      }
      
      if ( !(nameStr?.length() > 0) ) {
        // No string
        nameStr = "Grant ${id}"
      }
      
      setName( nameStr )
    }
  }
  
  static constraints = {
    'academicOutput'  (nullable: true)
    'funder'          (nullable: true)
    'costItem'        (nullable: true)
    'fund'            (nullable: true,  blank:false)
    'grantId'         (nullable: false,  blank:false)
    'internalGrantId' (nullable: true,  blank:false)
  }
}
