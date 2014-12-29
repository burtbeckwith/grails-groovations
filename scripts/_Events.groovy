import grails.util.BuildSettings

def MIGRATIONS_RESOURCE_PATH = 'groovations/migrations'
def DEFAULT_MIGRATIONS_ROOT_DIR = 'scripts/groovy-migrations'

eventPackagingEnd = {
    def migrationScriptsPath = config?.grails?.groovations?.migrationsRootDir ?: DEFAULT_MIGRATIONS_ROOT_DIR
    def sourceDirectory = new File(buildSettings.baseDir, migrationScriptsPath)
    def targetDirectory = new File(buildSettings.resourcesDir, MIGRATIONS_RESOURCE_PATH)
    if (sourceDirectory.exists()) {
        ant.sync(toDir: targetDirectory, overwrite: true) {
            fileset(dir: sourceDirectory, includes: '**/*.groovy')
        }
    }
}

eventCreateWarStart = { String name, File stagingDir ->
    def migrationScriptsPath = config?.grails?.groovations?.migrationsRootDir ?: DEFAULT_MIGRATIONS_ROOT_DIR
    def sourceDirectory = new File(buildSettings.baseDir, migrationScriptsPath)
    def targetDirectory = new File(stagingDir, "WEB-INF/classes/${MIGRATIONS_RESOURCE_PATH}")
    if (sourceDirectory.exists()) {
        ant.sync(toDir: targetDirectory, overwrite: true) {
            fileset(dir: sourceDirectory, includes: '**/*.groovy')
        }
    }
}
