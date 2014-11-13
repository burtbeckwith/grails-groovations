package com.commercehub.grails.groovations

import grails.util.GrailsUtil
import groovy.time.Duration

class ScriptEvaluation {

    Object result
    Throwable exception
    Duration duration
    String output

    void setException(Throwable exception) {
        this.exception = GrailsUtil.deepSanitize(exception)
    }

}