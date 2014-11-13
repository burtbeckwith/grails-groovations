package com.commercehub.grails.groovations

interface ScriptExecutionRepository {

    void save(ScriptExecutionRecord record)

    ScriptExecutionRecord getByResourcePath(String resourcePath)

    List<ScriptExecutionRecord> getMostRecentByPage(int offset, int pageSize)

    long countAll()

}