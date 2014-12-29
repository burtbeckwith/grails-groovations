package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

@CompileStatic
interface ScriptExecutionJobRepository {

    void save(ScriptExecutionJob job)

    boolean areJobsRunning()
}
