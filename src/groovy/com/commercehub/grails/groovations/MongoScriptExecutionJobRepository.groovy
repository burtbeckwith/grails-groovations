package com.commercehub.grails.groovations

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.Mongo

import javax.annotation.PostConstruct
import javax.annotation.Resource

class MongoScriptExecutionJobRepository implements ScriptExecutionJobRepository {

    private static final String COLLECTION_NAME = 'groovationsJobs'

    private static final String FIELD_ID = '_id'
    private static final String FIELD_STATUS = 'status'
    private static final String FIELD_SCRIPT_RESOURCE_PATHS = 'scriptResourcePaths'

    @Resource
    Mongo mongo

    String databaseName

    @Lazy
    private DB db = mongo.getDB(databaseName)

    @Lazy
    private DBCollection collection = db.getCollection(COLLECTION_NAME)

    @Override
    void save(ScriptExecutionJob job) {
        def dbObject = toDbObject(job)
        collection.save(dbObject)
        job.id = dbObject.get(FIELD_ID)
    }

    @Override
    boolean areJobsRunning() {
        def runningJobsCount = collection.find(new BasicDBObject([
                (FIELD_STATUS): JobStatus.RUNNING.name()
        ])).count()
        return runningJobsCount as boolean
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
