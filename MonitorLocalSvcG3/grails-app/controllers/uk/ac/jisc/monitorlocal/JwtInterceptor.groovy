package uk.ac.jisc.monitorlocal

// Prior Art imported into monitor. Not subject to same license
class JwtInterceptor {

  int order = HIGHEST_PRECEDENCE + 50

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

    // For dev
    header( "Access-Control-Allow-Origin", "http://localhost:9090" )
    header( "Access-Control-Allow-Credentials", "true" )
    header( "Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE" )
    header( "Access-Control-Max-Age", "3600" )  
    true 
  }

  boolean after() { 
    true 
  }


}
