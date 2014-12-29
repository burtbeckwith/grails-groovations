package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

@CompileStatic
class GroovationsPluginConfig {

    static final String DEFAULT_MIGRATIONS_ROOT_DIR = 'scripts/groovy-migrations'

    /**
     * The directory on the classpath in which migration scripts will be placed during packaging.
     */
    static final String MIGRATIONS_RESOURCE_PATH = 'groovations/migrations'

    String migrationsRootDir
}
