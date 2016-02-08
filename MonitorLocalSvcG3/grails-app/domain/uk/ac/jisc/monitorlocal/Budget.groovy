package uk.ac.jisc.monitorlocal

import com.k_int.grailt.tools.finance.MonetaryValue

class Budget extends Component {

  static constraints = {
  }

  String description
  MonetaryValue remainingFunds = new MonetaryValue()
}