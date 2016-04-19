package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.RefdataValue
import java.util.List;

class FundingGroup extends RefdataValue {
  
  Set funders = []
  static hasMany = [
    funders: Org,
  ]
}
