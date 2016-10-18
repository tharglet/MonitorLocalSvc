package uk.ac.jisc.monitorlocal.mappings

class UrlMappings {

  static mappings = {
    
    // Specific mappings
    "/user/current"   (controller: 'user', action: 'current')
    "/user/query/$q?"  (controller: 'user', action: 'query')
    
    "/jwt/${action}/${provider}"(controller: 'jwt')
    
    "/help/${helpPage}"(controller: 'ghHelp', action: 'proxy')

    "/$controller/$action?/$id?(.$format)?"{
      constraints {
        // apply constraints here
      }
    }

    "/"(controller: 'application', action:'index')
    "500"(view: '/error')
    "404"(view: '/notFound')
  }
}
