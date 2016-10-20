package uk.ac.jisc.monitorlocal

class MonitorUkIntegrationJob {

    def monitorUKIntegrationService

    static boolean running = false;

    static triggers = {
      simple repeatInterval: 60000l // execute job once in 60 seconds
      // cronExpression: "s m h D M W Y"  -- Every minute
      // cron name:'cronTrigger', startDelay:190000, cronExpression: "0 * * * * ?"

    }

    def execute() {
      log.debug("MonitorUkIntegrationJob::execute()");
      if ( !running ) {
        synchronized(this) {
          running = true;
          pushToMonitorUK();
        }
      }
    }

    def pushToMonitorUK() {
      // execute job
      log.debug("MonitorUkIntegrationJob::pushToMonitorUK");
      try {
        if ( grailsApplication.config.monitor.pushToMonitorUK ) {
          def bc = BatchCursor.findByDomainClassNameAndActivity(AcademicOutput.class.name,'UKFeed') ?: new BatchCursor(domainClassName:AcademicOutput.class.name,activity:'UKFeed',lastTimestamp:0).save(flush:true, failOnError:true);
          def max_ts = bc.lastTimestamp;
 
          log.debug("Fetch all AOs for orgs with a monitor UK account modified after ${max_ts}");

          def aos = AcademicOutput.executeQuery('select ao from AcademicOutput as ao where ao.ownerInstitution.monitorLocalAPIKey is not null AND ao.lastUpdated > :ts',[ts:new Date(max_ts)]);
          aos.each { ao ->
            log.debug("Check that AO has the necessary fields (DOI) and dispatch using api key ${ao.ownerInstitution.monitorLocalAPIKey}");

            // IF the AO does not have the necessary fields, worry not, next time it is edited and those properties are added this job will pick up the
            // updated AO and push it through the system. Similarly, updated AOs will get pushed to UK

            // https://github.com/JiscMonitor/monitor-uk/blob/develop/docs/API/CONTRIBUTION.md
            monitorUKIntegrationService.pushToUK(ao);

            def last_updated_for_this_ao = ao.lastUpdated?.getTime() ?: 0
            if ( last_updated_for_this_ao > max_ts ) {
              max_ts = last_updated_for_this_ao;
            }
          }
          bc.lastTimestamp=max_ts
          bc.save(flush:true, failOnError:true);
        }
        else {
          log.debug("Not pushing to monitor UK. set pushToMonitorUK=true in config to enable");
        }
      }
      catch ( Exception e ) {
        log.error("Error pushing to monitorUK",e);
      }
      finally {
        running = false;
        log.debug("monitorUK Push completed");
      }
    }
}
