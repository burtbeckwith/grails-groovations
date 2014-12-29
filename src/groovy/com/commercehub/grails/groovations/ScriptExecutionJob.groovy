package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

@CompileStatic
class ScriptExecutionJob {
    def id
    JobStatus status = JobStatus.RUNNING
    Date createdDate = new Date()
    List<String> scriptResourcePaths = []
}

@CompileStatic
enum JobStatus {
    RUNNING,
    COMPLETED
}
