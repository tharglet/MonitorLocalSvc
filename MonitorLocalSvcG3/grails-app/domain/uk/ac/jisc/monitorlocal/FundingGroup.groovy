package uk.ac.jisc.monitorlocal

import java.util.List;

class FundingGroup extends Component {
  static hasMany = [
    funders: Org,
  ]
}
