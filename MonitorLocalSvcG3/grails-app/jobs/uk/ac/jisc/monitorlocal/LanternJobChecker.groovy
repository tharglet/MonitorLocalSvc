package uk.ac.jisc.monitorlocal

class LanternJobChecker {

    def lanternIntegrationService

    static boolean running = false;

    static triggers = {
      simple repeatInterval: 300000l // execute job once in 300 seconds (5 mins)
      // cronExpression: "s m h D M W Y"  -- Every minute
      // cron name:'cronTrigger', startDelay:190000, cronExpression: "0 * * * * ?"

    }

    def execute() {
      log.debug("LanternJobChecker::execute()");
      if ( !running ) {
        synchronized(this) {
          running = true;
          checkLantern();
        }
      }
    }

    def checkLantern() {
      lanternIntegrationService.checkPendingRequests()
    }
}
