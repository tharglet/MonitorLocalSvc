
class CORSInterceptor {
  CORSInterceptor() {
    matchAll()
  }
  
  boolean before() {
    header( "Access-Control-Allow-Origin", "http://localhost:9090" )
    header( "Access-Control-Allow-Credentials", "true" )
    header( "Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE" )
    header( "Access-Control-Allow-Headers", "X-Requested-With, content-type" )
    
    true
  }

  boolean after() { true }

  void afterView() {
    // no-op
  }
}
