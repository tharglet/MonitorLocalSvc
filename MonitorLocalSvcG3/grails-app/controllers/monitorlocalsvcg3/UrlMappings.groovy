package monitorlocalsvcg3

class UrlMappings {

  static mappings = {

    "/ao" (controller: 'academicOutput', action: 'query', method: 'QUERY')
    "/ao/$q?" (controller: 'academicOutput', action: 'query', method: 'QUERY')
    "/ao/query(.(*))?" (controller: 'academicOutput', action: 'query', method: 'GET')
    "/ao/search(.(*))?" (controller: 'academicOutput', action: 'query', method: 'GET')

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
