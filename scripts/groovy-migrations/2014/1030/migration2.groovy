import com.commercehub.grails.groovations.GroovationsService
import org.slf4j.Logger

println('Running 2014.1030.migration2')
def log = log as Logger

def migrationService = ctx.groovationsService as GroovationsService
if (migrationService) {
    log.info('2014.1030.migration2 successfully accessed application services')
}
else {
    log.warn('2014.1030.migration2 could not access application services')
}
