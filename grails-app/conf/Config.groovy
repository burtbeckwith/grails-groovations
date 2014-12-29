//test root dir
//grails.groovations.migrationsRootDir = 'scripts/test-groovy-migrations'

log4j = {

    appenders {
        console name: 'groovationsScripts'
    }

    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    info  'grails.app', 'com.commercehub.grails.groovations'

    info groovationsScripts: 'groovations'
}

grails.doc.title = 'Grails Groovations Plugin'
grails.doc.subtitle = 'Automatically run Groovy-based migration scripts'
