package uk.ac.jisc.monitorlocal

class PublicationTitle extends Component {

  public static PublicationTitle lookupOrCreate(String name, List identifiers) {
    super.lookupOrCreate(PublicationTitle.class,name,identifiers);
  }

}
