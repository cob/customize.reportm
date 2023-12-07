package com.cultofbits.customizations.reportm.utils

import org.codehaus.groovy.runtime.InvokerHelper

class Report {

    def static GCLoader = new GroovyClassLoader(Report.class.getClassLoader());

    def id;
    def name;
    def description;
    def condition;
    def reportTmplPath;

    def conditionScriptClass

    Report(recordm, hit) {
        this.id = hit.getId()
        this.name = hit.value("Name")
        this.description = hit.value("Description")
        this.condition = hit.value("Condition")

        def reportInstance = recordm.get(hit.getId()).body
        this.reportTmplPath = getReportRelativeFilePath(id, reportInstance.fields.find { field -> field.fieldDefinition.name == "Template" })

        if (condition != null && condition.length() > 0) {
            this.conditionScriptClass = GCLoader.parseClass(condition, "Report_${this.id}.groovy")
        }
    }

    def evaluateCondition(msg, log) {
        if (conditionScriptClass == null) {
            log.info("No condition available for report ${this.name}. Confition evaluation will be true")
            return true
        }

        def binding = new Binding()
        binding.setVariable("msg", msg)
        binding.setVariable("log", log)

        def value = InvokerHelper.createScript(this.conditionScriptClass, binding).run()
        log.info("Condition evaluation for report ${this.name} is ${value}")

        return value
    }

    static String getReportRelativeFilePath(reportId, reportTmplFileField) {
        return "${(reportId / 10000) as Integer}/${reportId}/${reportTmplFileField.fieldDefinition.id}/${reportTmplFileField.value}" as String
    }

}

