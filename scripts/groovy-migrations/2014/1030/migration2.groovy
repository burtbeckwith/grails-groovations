println('Running 2014.1030.migration2')

def migrationService = ctx.groovationsService
if (migrationService) {
    log.info('2014.1030.migration2 successfully accessed application services')
}
else {
    log.warn('2014.1030.migration2 could not access application services')
}
