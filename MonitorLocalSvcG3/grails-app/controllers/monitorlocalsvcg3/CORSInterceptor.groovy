package monitorlocalsvcg3

class CORSInterceptor {
  CORSInterceptor() {
    log.debug ("Initialising Interceptor")
    matchAll()
  }
  
  boolean before() {
    header( "Access-Control-Allow-Origin", "*" )
    header( "Access-Control-Allow-Credentials", "true" )
    header( "Access-Control-Allow-Methods", "POST, GET, PUT, DELETE" )
    header( "Access-Control-Allow-Headers", "x-requested-with,access,accept,content-type" )
    
    log.debug ("Executing before interceptor")
    
    true
  }

  boolean after() { true }

  void afterView() {
    // no-op
  }
}
