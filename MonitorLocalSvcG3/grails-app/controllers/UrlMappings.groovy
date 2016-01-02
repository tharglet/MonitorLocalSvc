class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/jwt/${action}/${provider}"(controller: 'ceJwt')

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

    }
}
