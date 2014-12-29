package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.Mongo

import javax.annotation.PostConstruct

@CompileStatic
class MongoScriptExecutionJobRepository implements ScriptExecutionJobRepository {

    private static final String COLLECTION_NAME = 'groovationsJobs'

    private static final String FIELD_ID = '_id'
    private static final String FIELD_STATUS = 'status'
    private static final String FIELD_SCRIPT_RESOURCE_PATHS = 'scriptResourcePaths'

    Mongo mongo

    String databaseName

    @Lazy
    private DB db = mongo.getDB(databaseName)

    @Lazy
    private DBCollection collection = db.getCollection(COLLECTION_NAME)

    void save(ScriptExecutionJob job) {
        def dbObject = toDbObject(job)
        collection.save(dbObject)
        job.id = dbObject.get(FIELD_ID)
    }

    boolean areJobsRunning() {
        collection.find(new BasicDBObject([(FIELD_STATUS): JobStatus.RUNNING.name()])).count()
    }

    private static DBObject toDbObject(ScriptExecutionJob job) {
        return new BasicDBObject(
                (FIELD_ID): job.id,
                (FIELD_STATUS): job.status.name(),
                (FIELD_SCRIPT_RESOURCE_PATHS): job.scriptResourcePaths
        )
    }

    @PostConstruct
    void postConstruct() {
        collection.createIndex(new BasicDBObject([
                (FIELD_STATUS): 1,
        ]))
    }
}
