grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        mavenLocal()
        grailsCentral()
        mavenCentral()
    }

    dependencies {
        compile 'org.mongodb:mongo-java-driver:2.12.4'
    }

    plugins {

        //noinspection GroovyAssignabilityCheck
        build ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
