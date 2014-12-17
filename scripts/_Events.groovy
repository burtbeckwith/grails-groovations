import grails.util.BuildSettings

def MIGRATIONS_RESOURCE_PATH = 'groovations/migrations'

eventPackagingEnd = {
    def buildSettings = buildSettings as BuildSettings
    def config = config as ConfigObject

    def migrationScriptsPath = config?.grails?.groovations?.migrationsRootDir ?: MIGRATIONS_RESOURCE_PATH
    def sourceDirectory = new File(buildSettings.baseDir, migrationScriptsPath)
    def targetDirectory = new File(buildSettings.resourcesDir, MIGRATIONS_RESOURCE_PATH)
    if (sourceDirectory.exists()) {
        ant.sync(toDir: targetDirectory, overwrite: true) {
            fileset(dir: sourceDirectory, includes: '**/*.groovy')
        }
    }
}

eventCreateWarStart = { String name, File stagingDir ->
    def buildSettings = buildSettings as BuildSettings
    def config = config as ConfigObject

    def migrationScriptsPath = config?.grails?.groovations?.migrationsRootDir ?: MIGRATIONS_RESOURCE_PATH
    def sourceDirectory = new File(buildSettings.baseDir, migrationScriptsPath)
    def targetDirectory = new File(stagingDir, "WEB-INF/classes/${MIGRATIONS_RESOURCE_PATH}")
    if (sourceDirectory.exists()) {
        ant.sync(toDir: targetDirectory, overwrite: true) {
            fileset(dir: sourceDirectory, includes: '**/*.groovy')
        }
    }
}