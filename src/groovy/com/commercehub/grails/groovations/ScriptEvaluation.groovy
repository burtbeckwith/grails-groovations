package com.commercehub.grails.groovations

import grails.util.GrailsUtil
import groovy.time.Duration
import groovy.transform.CompileStatic

@CompileStatic
class ScriptEvaluation {

    def result
    Throwable exception
    Duration duration

    void setException(Throwable exception) {
        this.exception = GrailsUtil.deepSanitize(exception)
    }
}
