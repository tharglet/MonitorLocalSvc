package monitorlocalsvcg3

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.apache.commons.lang3.exception.ExceptionUtils

class Application extends GrailsAutoConfiguration {
  static void main(String[] args) {
    
    Thread.setDefaultUncaughtExceptionHandler(
      new Thread.UncaughtExceptionHandler() {
        @Override public void uncaughtException(Thread t, Throwable e) {
          System.out.println(ExceptionUtils.getStackTrace(e));
        }
      }
    );
    GrailsApp.run(Application, args)
  }
}