package com.commercehub.grails.groovations

class ScriptExecutionJob {

    Object id
    JobStatus status = JobStatus.RUNNING
    Date createdDate = new Date()
    List<String> scriptResourcePaths = []

}

enum JobStatus {

    RUNNING,
    COMPLETED

}
