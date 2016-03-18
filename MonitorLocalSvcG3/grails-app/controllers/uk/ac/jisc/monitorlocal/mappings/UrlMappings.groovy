package uk.ac.jisc.monitorlocal.mappings

class UrlMappings {

  static mappings = {

    "/jwt/${action}/${provider}"(controller: 'jwt')

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
