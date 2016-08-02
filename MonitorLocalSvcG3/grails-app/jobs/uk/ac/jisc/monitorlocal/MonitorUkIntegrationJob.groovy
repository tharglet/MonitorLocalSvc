package uk.ac.jisc.monitorlocal

class MonitorUkIntegrationJob {

    static boolean running = false;

    static triggers = {
      simple repeatInterval: 600000l // execute job once in 600 seconds == every 10 mins
    }

    def synchronized execute() {
      if ( !running ) {
        running = true;
        pushToMonitorUK();
      }
    }

    def pushToMonitorUK() {
      // execute job
      try {
        if ( grailsApplication.config.pushToMonitorUK ) {
          def bc = BatchCursor.findByDomainClassNameAndActivity(AcademicOutput.class.name,'UKFeed') ?: new BatchCursor(domainClassName:AcademicOutput.class.name,activity:'UKFeed',lastTimestamp:0).save(flush:true, failOnError:true);
          def max_ts = bc.lastTimestamp;
          def aos = AcademicOutput.executeQuery('select ao from AcademicOutput as ao where ao.ownerInstitution.monitorLocalAPIKey is not null AND ao.lastUpdated > :ts',[ts:ts]);
          aos.each { ao ->
            log.debug("Check that AO has the necessary fields (DOI) and dispatch using api key ${ao.ownerInstitution.monitorLocalAPIKey}");
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
      }
    }
}
