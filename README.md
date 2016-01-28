# MonitorLocalSvc
Monitor Local Service Layer


http://blog.nbostech.com/2015/09/grails-sample-oauth2/


Example Oauth flows 
http://docs.oracle.com/cd/E39820_01/doc.11121/gateway_docs/content/oauth_flows.html
https://msdn.microsoft.com/en-us/library/azure/dn645543.aspx

## Dependencies
The grails 3 Application in this repository uses a grails-tools module. This will be pulled in using Maven, but if you wish, you can use a local filesystem version of the plugin when in development.

To point to a local version create a local file named "settings.gradle" in **the root** of your local repository, and add the following lines to that file:  

``` Groovy
include 'MonitorLocalSvcG3', 'grails-tools'
project(':grails-tools').projectDir = new File('/path/to/grails-tools/directory')
```
