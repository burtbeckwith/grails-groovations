package com.commercehub.grails.groovations

import groovy.io.FileType
import groovy.time.TimeCategory
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.io.support.FileSystemResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PreDestroy
import javax.annotation.Resource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Slf4j
class GroovationsService {

    private static final String GROOVY_FILE_SUFFIX = '.groovy'

    private static final Logger SCRIPT_LOGGER = LoggerFactory.getLogger('groovations')

    static def transactional = false

    def grailsApplication

    @Resource
    GroovationsPluginConfig groovyMigrationsPluginConfig

    @Resource
    ScriptExecutionRepository scriptExecutionRepository

    @Resource
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
        return getMigrationScriptResources().findAll {
            !scriptExecutionRepository.getByResourcePath(it.path)
        }.collect {
            new ScriptExecutionRecord(resourcePath: it.path)
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

        for(def script : getScriptsPendingExecution()) {
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

    private List<FileSystemResource> getMigrationScriptResources() {
        def rootDir = new File(groovyMigrationsPluginConfig.migrationsRootDir)

        if (!rootDir.exists()) {
            return []
        }

        List<File> migrationScriptFiles = []

        rootDir.eachFileRecurse(FileType.FILES) {
            if (it.name.endsWith(GROOVY_FILE_SUFFIX)) {
                migrationScriptFiles << it
            }
        }
        def migrationResources = migrationScriptFiles.collect{ new FileSystemResource(it) }.sort{ it.path }
        return migrationResources
    }

    private ScriptEvaluation executeScript(String resourcePath) {
        def file = new File(resourcePath)
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
        catch (Exception ex) {
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
