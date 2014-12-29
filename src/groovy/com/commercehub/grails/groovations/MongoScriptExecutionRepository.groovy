package com.commercehub.grails.groovations

import groovy.transform.CompileStatic

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo

import javax.annotation.PostConstruct

@CompileStatic
class MongoScriptExecutionRepository implements ScriptExecutionRepository {

    private static final String COLLECTION_NAME = 'groovations'

    private static final String FIELD_ID = '_id'
    private static final String FIELD_RESOURCE_PATH = 'resourcePath'
    private static final String FIELD_EXECUTION_DATE = 'executionDate'

    Mongo mongo

    String databaseName

    @Lazy
    private DB db = mongo.getDB(databaseName)

    @Lazy
    private DBCollection collection = db.getCollection(COLLECTION_NAME)

    void save(ScriptExecutionRecord record) {
        def dbObject = toDbObject(record)
        collection.save(dbObject)
        record.id = dbObject.get(FIELD_ID)
    }

    ScriptExecutionRecord getByResourcePath(String resourcePath) {
        def cursor = collection.find(new BasicDBObject([
                (FIELD_RESOURCE_PATH): resourcePath
        ]))
        if (cursor.hasNext()) {
            return fromDbObject(cursor.next())
        }
        return null
    }

    List<ScriptExecutionRecord> getMostRecentByPage(int offset, int pageSize) {
        def cursor = queryByMostRecent()
        return cursor.skip(offset).limit(pageSize).iterator().collect{ DBObject it -> fromDbObject(it) }
    }

    long countAll() {
        return collection.count()
    }

    private DBCursor queryByMostRecent() {
        return collection.find().sort(new BasicDBObject([
                (FIELD_EXECUTION_DATE): -1
        ]))
    }

    private static ScriptExecutionRecord fromDbObject(DBObject dbObject) {
        return new ScriptExecutionRecord(
                id: dbObject.get(FIELD_ID),
                resourcePath: dbObject.get(FIELD_RESOURCE_PATH) as String,
                executionDate: dbObject.get(FIELD_EXECUTION_DATE) as Date
        )
    }

    private static DBObject toDbObject(ScriptExecutionRecord record) {
        return new BasicDBObject([
                (FIELD_ID): record.id,
                (FIELD_RESOURCE_PATH): record.resourcePath,
                (FIELD_EXECUTION_DATE): record.executionDate
        ])
    }

    @PostConstruct
    void postConstruct() {
        collection.createIndex(new BasicDBObject([
                (FIELD_RESOURCE_PATH): 1,
                (FIELD_EXECUTION_DATE): -1
        ]))
        collection.createIndex(
                new BasicDBObject([ (FIELD_RESOURCE_PATH): 1 ]),
                new BasicDBObject([ unique: true ])
        )
    }
}
