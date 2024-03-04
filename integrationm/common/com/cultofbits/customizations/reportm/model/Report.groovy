package com.cultofbits.customizations.reportm.model

import com.cultofbits.integrationm.service.actionpack.EmailActionPack
import com.cultofbits.integrationm.service.actionpack.ReportmActionPack
import com.cultofbits.integrationm.service.dictionary.recordm.RecordmInstance
import org.codehaus.groovy.runtime.InvokerHelper

class Report {

    def static GCLoader = new GroovyClassLoader(Report.class.getClassLoader());

    private RecordmInstance recordmInstance
    private ReportmActionPack reportm

    private Integer id;
    private String name;
    private String description;
    private String reportTmplPath;

    private Class conditionScriptClass

    Report(RecordmInstance recordmInstance, ReportmActionPack reportm) {
        this.recordmInstance = recordmInstance
        this.reportm = reportm

        this.id = recordmInstance.id
        this.name = recordmInstance.value("Name")
        this.description = recordmInstance.value("Description")
        this.reportTmplPath = getReportRelativeFilePath(this.id, recordmInstance.fields.find { field -> field.fieldDefinition.name == "Template" })

        def trigger = recordmInstance.value("Trigger")
        if (trigger == "EVENT") {
            def condition = recordmInstance.value("Condition")
            if (condition != null && condition.length() > 0) {
                this.conditionScriptClass = GCLoader.parseClass(condition, "Report_${this.id}.groovy")
            }
        }
    }

    def evaluateCondition(msg, log) {
        if (conditionScriptClass == null) {
            log.info("No condition available for report ${name}. Confition evaluation will be true")
            return true
        }

        def binding = new Binding()
        binding.setVariable("msg", msg)
        binding.setVariable("log", log)

        def value = InvokerHelper.createScript(conditionScriptClass, binding).run()
        log.info("Condition evaluation for report ${name} is ${value}")

        return value
    }

    def executeAsync() {
        Map<String, Object> args = ["query": "id.raw:${id}".toString()]
        def callbackUri = "http://localhost:40380/concurrent/reportm-after-done?reportId=${id}&sourceInstanceId=${id}"
        reportm.generateAsync(reportTmplPath, args, callbackUri)
    }

    static String getReportRelativeFilePath(reportId, reportTmplFileField) {
        return "${(reportId / 10000) as Integer}/${reportId}/${reportTmplFileField.fieldDefinition.id}/${reportTmplFileField.value}" as String
    }

}

