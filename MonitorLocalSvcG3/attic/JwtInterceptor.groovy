package uk.ac.jisc.monitorlocal

// Prior Art imported into monitor. Not subject to same license
class JwtInterceptor {

  int order = HIGHEST_PRECEDENCE + 50

  static final String[] allowedOrigins = [ 'http://localhost:9090' ]

  public JwtInterceptor() {
    // matchAll().excludes(controller: 'login')
    // matchAll().excludes(uri: '/login')
    matchAll()
  }

  @javax.annotation.PostConstruct
  def init() {
    log.debug("JwtInterceptor::init");
  }

  boolean before() { 

    String origin = request.getHeader("Origin");
    log.debug("Request origin : ${origin}");

    boolean options = ("OPTIONS" == request.method)

    if (options) {
      header("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS")
      if (origin != null) {
        header("Access-Control-Allow-Headers", "origin, authorization, accept, content-type, x-requested-with")
        header("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS")
        header("Access-Control-Max-Age", "3600")
      }
    }


    if( allowedOrigins && allowedOrigins.contains(origin)) {
      header("Access-Control-Allow-Origin", origin)
      header("Access-Control-Allow-Credentials", "true")
    }
    else if( !allowedOrigins ) { // no origin white list
      header("Access-Control-Allow-Origin", origin ?: "*")
      header("Access-Control-Allow-Credentials", "true")
    }

    // For dev
    // header( "Access-Control-Allow-Origin", "http://localhost:9090" )
    // header( "Access-Control-Allow-Credentials", "true" )
    // header( "Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE" )
    // header( "Access-Control-Max-Age", "3600" )  
    true 
  }

  boolean after() { 
    true 
  }


}
