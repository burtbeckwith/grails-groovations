package com.commercehub.grails.groovations

interface ScriptExecutionJobRepository {

    void save(ScriptExecutionJob job)

    boolean areJobsRunning()

}
