package com.commercehub.grails.groovations

import groovy.io.FileType
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.io.support.ClassPathResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PreDestroy
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@CompileStatic
@Slf4j
class GroovationsService {

    private static final String GROOVY_FILE_SUFFIX = '.groovy'

    private static final Logger SCRIPT_LOGGER = LoggerFactory.getLogger('groovations')

    static transactional = false

    GrailsApplication grailsApplication

    GroovationsPluginConfig groovyMigrationsPluginConfig

    ScriptExecutionRepository scriptExecutionRepository

    ScriptExecutionJobRepository scriptExecutionJobRepository

    @Lazy
    private ExecutorService executorService = Executors.newFixedThreadPool(1)

    void startExecutePendingMigrationsJob() {
        if (scriptExecutionJobRepository.areJobsRunning()) {
            log.warn('Migrations are currently executing; ignoring request to execute pending migrations.')
            return
        }

        def job = new ScriptExecutionJob()
        scriptExecutionJobRepository.save(job)

        executorService.submit({
            def executedScripts = executePendingMigrations()
            job.status = JobStatus.COMPLETED
            job.scriptResourcePaths = executedScripts*.resourcePath
            scriptExecutionJobRepository.save(job)
        })
    }

    List<ScriptExecutionRecord> getScriptsPendingExecution() {
        return getMigrationScriptResourcePaths().findAll {
            !scriptExecutionRepository.getByResourcePath(it)
        }.collect {
            new ScriptExecutionRecord(resourcePath: it)
        }
    }

    List<ScriptExecutionRecord> getExecutedScripts(int offset, int pageSize) {
        return scriptExecutionRepository.getMostRecentByPage(offset, pageSize)
    }

    long getCountOfExecutedScripts() {
        return scriptExecutionRepository.countAll()
    }

    boolean areMigrationsExecuting() {
        return scriptExecutionJobRepository.areJobsRunning()
    }

    private List<ScriptExecutionRecord> executePendingMigrations() {
        log.info('Executing pending migrations...')
        int migrationsExecuted = 0
        def executedScripts = []

        for(script in getScriptsPendingExecution()) {
            def evaluation = executeScript(script.resourcePath)
            if (evaluation.exception) {
                log.warn('Stopping migration executions due to exception.')
                break
            }
            script.executionDate = new Date()
            migrationsExecuted++
            executedScripts << script
            scriptExecutionRepository.save(script)
        }

        log.info("Executed $migrationsExecuted migrations.")
        return executedScripts
    }

    private List<String> getMigrationScriptResourcePaths() {
        def rootDir = new ClassPathResource(GroovationsPluginConfig.MIGRATIONS_RESOURCE_PATH).getFile()

        if (!rootDir.exists()) {
            return []
        }

        List<File> migrationScriptFiles = []

        rootDir.eachFileRecurse(FileType.FILES) {
            if (it.name.endsWith(GROOVY_FILE_SUFFIX)) {
                def relativeFile = new File(rootDir.toURI().relativize(it.toURI()).toString())
                migrationScriptFiles << relativeFile
            }
        }
        return migrationScriptFiles*.path.sort()
    }

    private ScriptEvaluation executeScript(String resourcePath) {
        def file = new ClassPathResource("${GroovationsPluginConfig.MIGRATIONS_RESOURCE_PATH}/${resourcePath}").getFile()
        log.info("Started execution of Groovy script: ${resourcePath}")
        def evaluation = evaluate(file)

        if (evaluation.exception) {
            log.error("Exception occured while executing Groovy script: ${resourcePath}", evaluation.exception)
        }
        else {
            log.info("Completed execution of Groovy script: ${resourcePath} (${evaluation.duration})")
        }

        return evaluation
    }

    private ScriptEvaluation evaluate(File scriptFile) {
        def evaluation = new ScriptEvaluation()
        def startDate = new Date()

        try {
            def binding = createBinding()
            def groovyShell = new GroovyShell(grailsApplication.classLoader, binding)
            evaluation.result = groovyShell.evaluate(scriptFile)
        }
        catch (ex) {
            evaluation.exception = ex
        }

        evaluation.duration = TimeCategory.minus(new Date(), startDate)

        return evaluation
    }

    private Binding createBinding() {
        return new Binding([
            grailsApplication: grailsApplication,
            ctx: grailsApplication.mainContext,
            config: grailsApplication.config,
            log: SCRIPT_LOGGER
        ])
    }

    @PreDestroy
    void preDestroy() {
        executorService.shutdown()
    }
}
