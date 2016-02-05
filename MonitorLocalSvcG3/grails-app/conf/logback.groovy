import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

// Explanation for this from http://www.ociweb.com/resources/publications/sett/september-2015-grails-3-interceptors/
logger 'grails.app.controllers.com.k_int', DEBUG
logger 'grails.app.controllers.uk', DEBUG
logger 'grails.plugin.springsecurity', WARN
logger 'org.springframework.security', WARN
logger 'org.springframework.web', WARN
logger 'grails.artefact.Interceptor', WARN, ['STDOUT'], false

logger 'com.k_int', DEBUG
logger 'com.k_int.grails.tools.refdata', DEBUG
logger 'com.k_int.grails.tools.identifiers', DEBUG
logger 'grails.app.controllers.com.k_int', DEBUG
logger 'grails.app.init.BootStrap', DEBUG
logger 'grails.artefact.Interceptor', WARN


def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}
