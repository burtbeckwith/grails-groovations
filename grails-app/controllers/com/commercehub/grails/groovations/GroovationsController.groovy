package com.commercehub.grails.groovations

class GroovationsController {

    private static final int DEFAULT_MIGRATIONS_PAGE_SIZE = 10

    def groovationsService

    def index() {
        int offset = params.int('offset', 0)
        int pageSize = params.int('max', DEFAULT_MIGRATIONS_PAGE_SIZE)

        def pendingMigrations = groovationsService.getScriptsPendingExecution()
        def executedMigrations = groovationsService.getExecutedScripts(offset, pageSize)
        def numExecutedMigrations = groovationsService.getCountOfExecutedScripts()
        def areMigrationsExecuting = groovationsService.areMigrationsExecuting()

        return [
                pendingMigrations: pendingMigrations,
                executedMigrations: executedMigrations,
                pageSize: pageSize,
                numExecutedMigrations: numExecutedMigrations,
                areMigrationsExecuting: areMigrationsExecuting
        ]
    }

    def executePendingMigrations() {
        groovationsService.startExecutePendingMigrationsJob()
        flash.message = 'Ran Groovations'
        redirect(action: 'index')
    }

}
