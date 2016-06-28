#!groovy

@Grapes([
  @GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/'),
  @Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.14'),
  @Grab(group='javax.mail', module='mail', version='1.4.7'),
  @Grab(group='net.sourceforge.htmlunit', module='htmlunit', version='2.21'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.5.2'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.5.2'),
  @GrabExclude('org.codehaus.groovy:groovy-all')
])


import javax.mail.*
import javax.mail.search.*
import java.util.Properties
import static groovy.json.JsonOutput.*
import groovy.json.JsonSlurper
import java.security.MessageDigest
import com.gargoylesoftware.htmlunit.*
import groovyx.net.http.HTTPBuilder
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import groovyx.net.http.*
import org.apache.http.entity.mime.MultipartEntityBuilder /* we'll use the new builder strategy */
import org.apache.http.entity.mime.content.ByteArrayBody /* this will encapsulate our file uploads */
import org.apache.http.entity.mime.content.StringBody /* this will encapsulate string params */
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*

config = null;
cfg_file = new File('./sync-publications-router.json')
if ( cfg_file.exists() ) {
  config = new JsonSlurper().parseText(cfg_file.text);
}
else {
  config=[:]
}

println("Using config ${config}");

println("Pulling latest messages");
pullLatest(config,'http://link.springer.com/lists');
println("All done");

println("Updating config");
cfg_file.delete()
cfg_file << toJson(config);


def pullLatest(config) {

  // def cursor = SyncCursor.findByActivity('KBPlusTitles') ?: new SyncCursor(activity:'KBPlusTitles', lastTimestamp:0).save(flush:true, failOnError:true);
  def cursor = null;

  // log.debug("Got cursor ${cursor}");

  doSync('https://www.kbplus.ac.uk','/test/oai/titles', 'kbplus', cursor) { r ->

    def result = [:]

    try {
      def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      def record_ts_str = r.header.datestamp.text()
      result.ts = sdf.parse(record_ts_str)?.getTime()

      def identifiers = []
      println("Process record ${r}");
      // def title_id = r.metadata.kbplus.title.@id.text()
    }
    catch ( Exception e ) {
      result.message = e.message;
    }

    result
  }
}

public doSync(host, path, prefix, cursor, notificationTarget) {
  println("Get latest changes ${host} ${path} ${prefix} ${cursor}");

  def http = new HTTPBuilder( host )
  http.ignoreSSLIssues()
  http.contentType = XML
  http.headers = [Accept : 'application/xml']
  def lastTimestamp = 0;

  def more = true
  println("Attempt get...");

  def resumption=null

  // perform a GET request, expecting XML response data
  while ( more ) {

    println("Make request....");

    def qry = null
    if ( resumption ) {
        println("Processing resumption");
        qry = [ verb:'ListRecords', resumptionToken: resumption ]
    }
    else {
        println("Fetch all records since ${cursor.lastTimestamp}");
        def the_date = new Date(cursor.lastTimestamp)
        def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        def from=sdf.format(the_date)
        println("Requesting records since ${cursor.lastTimestamp} :: ${from}");
        qry = [ verb:'ListRecords', metadataPrefix: prefix, from:from ]
    }

    println("Query params : ${qry} ");

    http.request( GET, XML ) { req ->

      uri.path = path
      uri.query = qry 
      contentType=XML

      // response handler for a success response code:
      response.success = { resp, xml ->
        int ctr=0
        println("In response handler");
        println("Status ${resp.statusLine}")

        def slurper = new groovy.util.XmlSlurper()
        // def parsed_xml = slurper.parseText(xml.text)
        def parsed_xml = slurper.parse(xml)

        parsed_xml?.'ListRecords'?.'record'.each { r ->
          def clr = notificationTarget(r)
          println(clr);
          ctr++
          if ( clr.ts > lastTimestamp )
            lastTimestamp = clr.ts
        }

        if ( ctr > 0 ) {
          more=true
        }
        else {
          more=false
        }

        resumption = parsed_xml?.'ListRecords'?.'resumptionToken'
        println("Complete ${ctr} ${more} ${resumption}");
      }

      response.error = { err ->
        println(err)
      }
    }
    // update cursor
    cursor.lastTimestamp = lastTimestamp
    // cursor.save(flush:true, failOnError:true);
  }
}
