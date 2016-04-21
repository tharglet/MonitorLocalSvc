package uk.ac.jisc.monitorlocal.interceptors

class NoCacheInterceptor {

  int order = HIGHEST_PRECEDENCE;

  NoCacheInterceptor() {
    matchAll()
  }

  boolean before() {
    
    // Eventually we can build in some conditional gets using last modified,
    // but for now we prevent caching. 
    header ("Cache-Control", "no-store, no-cache, must-revalidate")

    true
  }
}
