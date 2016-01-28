package uk.ac.jisc.monitorlocal

class Budget extends Component {

  static constraints = {
  }

  String description
  BigDecimal remainingFunds = new BigDecimal("0.00")
}