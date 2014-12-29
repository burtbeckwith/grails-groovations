package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

@CompileStatic
class ScriptExecutionRecord {
    def id
    String resourcePath
    Date executionDate
}
