package monitorlocalsvcg3

class UrlMappings {

  static mappings = {
    "/ao/query/${q}?" (controller: 'academicOutput', action: 'query', method: 'GET')

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
