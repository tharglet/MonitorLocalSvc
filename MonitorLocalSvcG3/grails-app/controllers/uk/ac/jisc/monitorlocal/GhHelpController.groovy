package uk.ac.jisc.monitorlocal

class GhHelpController {

  def proxy (String helpPage) {

    def host =  grailsApplication.config.monitor.helpHost
    def path =  "${grailsApplication.config.monitor.helpUri}/${helpPage}"
    response <<  ("${host}${path}").toURL().getText()
  }
}
