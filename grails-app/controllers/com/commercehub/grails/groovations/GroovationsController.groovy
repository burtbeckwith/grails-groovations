package com.commercehub.grails.groovations

class GroovationsController {

    private static final int DEFAULT_MIGRATIONS_PAGE_SIZE = 10

    def groovationsService

    def index() {
        int offset = params.int('offset', 0)
        int pageSize = params.int('max', DEFAULT_MIGRATIONS_PAGE_SIZE)

        [
            pendingMigrations: groovationsService.getScriptsPendingExecution(),
            executedMigrations: groovationsService.getExecutedScripts(offset, pageSize),
            pageSize: pageSize,
            numExecutedMigrations: groovationsService.getCountOfExecutedScripts(),
            areMigrationsExecuting: groovationsService.areMigrationsExecuting()
        ]
    }

    def executePendingMigrations() {
        groovationsService.startExecutePendingMigrationsJob()
        flash.message = 'Ran Groovations'
        redirect(action: 'index')
    }
}
