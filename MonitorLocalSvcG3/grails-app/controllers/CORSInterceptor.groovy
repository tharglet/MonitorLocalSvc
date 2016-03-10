
class CORSInterceptor {
  CORSInterceptor() {
//    log.debug ("Initialising Interceptor")
//    matchAll()
  }
  
  boolean before() {
//    header( "Access-Control-Allow-Origin", "http://localhost:9090" )
//    header( "Access-Control-Allow-Credentials", "true" )
//    header( "Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, QUERY" )
//    header( "Access-Control-Allow-Headers", "X-Requested-With, Access, Content-Type" )
//    
//    log.debug ("Executing before interceptor")
//    
    true
  }

  boolean after() { true }

  void afterView() {
    // no-op
  }
}
