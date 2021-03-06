import com.commercehub.grails.groovations.GroovationsPluginConfig
import com.commercehub.grails.groovations.MongoScriptExecutionJobRepository
import com.commercehub.grails.groovations.MongoScriptExecutionRepository

@SuppressWarnings("GroovyUnusedDeclaration")
class GroovationsGrailsPlugin {

    def version = '0.1.6'

    def grailsVersion = '2.2 > *'

    def pluginExcludes = [
        'grails-app/views/index.gsp',
        'scripts/groovy-migrations/**'
    ]

    def title = 'Groovations - The Groovy Migrations Plugin'
    def developers = [
        [name: 'David A. Purcell', email: 'dpurcell@commercehub.com'],
    ]

    def description = '''Automatically run Groovy based migration scripts.
Grails console scripts are powerful tools for performing data migrations, since they have access to business logic and domain classes. \
Running these scripts manually means that sometimes these scripts do not get run on all environments and there is no visibility into what \
scripts have been run or need to be run.

This plugin was created to automate the script execution process and provide visibility and consistency.'''

    def organization = [name: 'CommerceHub', url: 'http://www.commercehub.com']
    def documentation = 'http://commercehub-oss.github.io/grails-groovations'
    def issueManagement = [system: 'GitHub', url: 'https://github.com/commercehub-oss/grails-groovations/issues']
    def scm = [url: 'https://github.com/commercehub-oss/grails-groovations']
    def license = 'APACHE'

    def doWithSpring = {
        def rootDir = application.config?.grails?.groovations?.migrationsRootDir ?: GroovationsPluginConfig.DEFAULT_MIGRATIONS_ROOT_DIR

        groovyMigrationsPluginConfig(GroovationsPluginConfig) {
            migrationsRootDir = rootDir
        }

        def mongoBeanName = (
                application.config?.grails?.groovations?.mongoBean ?:
                        manager?.hasGrailsPlugin('mongodb') ? 'mongoBean' : 'mongo'
        )
        def dbName = (
                application.config?.grails?.groovations?.databaseName ?:
                        application.config?.grails?.mongo?.databaseName ?:
                                application.config?.mongo?.databaseName ?:
                                        application.metadata.getApplicationName()
        )

        scriptExecutionRepository(MongoScriptExecutionRepository) {
            mongo = ref(mongoBeanName)
            databaseName = dbName
        }

        scriptExecutionJobRepository(MongoScriptExecutionJobRepository) {
            mongo = ref(mongoBeanName)
            databaseName = dbName
        }
    }
}
