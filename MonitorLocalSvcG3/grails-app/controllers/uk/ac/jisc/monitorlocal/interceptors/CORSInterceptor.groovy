package uk.ac.jisc.monitorlocal.interceptors

class CORSInterceptor {

  int order = HIGHEST_PRECEDENCE;

  CORSInterceptor() {
    matchAll()
  }

  boolean before() {
        
    header("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS")
    header("Access-Control-Allow-Origin", "http://localhost:9090" )
    header("Access-Control-Allow-Credentials", "true" )
    header("Access-Control-Allow-Headers", "Origin, Authorization,X-Requested-With,Access,Accept,Content-Type" )
    
    // If this is an OPTIONS request type, then the browser is performing some pre-flight
    // requests to the server to see if it is indeed allowed to send the data here.
    if (request.method == "OPTIONS") {
      render (status : 200)
      return false
    }

    true
  }

  boolean after() { true }

  void afterView() {
    // no-op
  }
}
