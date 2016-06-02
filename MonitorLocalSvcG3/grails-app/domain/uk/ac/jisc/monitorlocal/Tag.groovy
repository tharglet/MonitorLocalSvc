package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.RefdataValue

class Tag extends RefdataValue {
  
  static Tag lookupOrCreate(category_name, value, sortkey = null) {
    return this.lookupOrCreate(category_name, value, sortkey, this)
  }
}
