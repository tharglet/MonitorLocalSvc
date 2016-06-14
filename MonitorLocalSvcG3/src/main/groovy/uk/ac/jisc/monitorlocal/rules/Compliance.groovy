package uk.ac.jisc.monitorlocal.rules

/**
 * Rules for compliance. Defining them here as the YML config lacks the ability to
 * include closures. And the application.groovy file doesn't appear to be read properly at the moment.
 */
class Compliance {
  
  // Single closure defines the rule names and the code to be executed. Can be a closure or a string. String is assumed to be groovy code.
  public static final RULES = {
    
  }
}
