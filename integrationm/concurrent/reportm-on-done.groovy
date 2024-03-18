import com.cultofbits.customizations.reportm.utils.MarkdownProcessor
import com.cultofbits.customizations.reportm.utils.TemplateUtils
import groovy.json.JsonSlurper

def reportId = argsMap["reportId"]
def reportFile = argsMap["report"]
def emails = argsMap["emails"] as String

def variables = new JsonSlurper().parseText(argsMap["extracts"] ?: "{}")

if (reportId == null || reportFile == null) return

def reportInstanceResponse = recordm.get(reportId.toInteger())
if (!reportInstanceResponse.success()) {
    return json(404, ["success": false])
}

def reportmInstance = reportInstanceResponse.getBody()

def actions = reportmInstance.value("Trigger") == "MANUAL" || reportmInstance.value("Trigger") == "SCHEDULED"
        ? reportmInstance.values("Trigger Actions")
        : reportmInstance.values("Event Actions")

actions.each {
    action ->
        switch (action) {
            case "Send Email":

                Map<String, Object> finalVariables = [:]
                finalVariables["REPORT_NAME"] = reportmInstance.value("Name")
                finalVariables["REPORT_DESCRIPTION"] = reportmInstance.value("Description")
                finalVariables["REPORT_SOURCE_INSTANCE_ID"] = argsMap["sourceInstanceId"]
                finalVariables.putAll(variables)

                def emailAddresses = TemplateUtils.apply(emails != null ? emails : reportmInstance.value("Destinations"), finalVariables)
                def emailSubject = TemplateUtils.apply(reportmInstance.value("Subject") ?: "{{REPORT_NAME}}", finalVariables)
                def emailBody = TemplateUtils.apply(reportmInstance.value("Body") ?: "{{REPORT_DESCRIPTION}}", finalVariables)

                def markdownProcessor = new MarkdownProcessor()

                email.send(
                        emailSubject,
                        markdownProcessor.toHtml(emailBody + "<br><br>"),
                        [to: emailAddresses.split(";").findAll { it != null }, attachments: [reportFile]]
                )

                log.info("Report email sent. {{reportFile: ${reportFile}, emails: ${emailAddresses} }} ")

                return

            case "Attach to Instance":
                def sourceInstanceId = argsMap["sourceInstanceId"] as String
                if (sourceInstanceId == null) {
                    log.error("Instance not provided to attach the repoort")
                }

                def targetField = reportmInstance.value("Definition Field")
                def fileReport = new File(reportFile.toString())

                recordm.attach(sourceInstanceId.toInteger(), targetField, fileReport.getName(), fileReport)
                recordm.update(reportmInstance.value("Definition"), sourceInstanceId.toInteger(), [(targetField): fileReport.getName()])
                log.info("Report uploaded to instance. {{instanceId: ${sourceInstanceId}, reportFile: ${reportFile} }} ")

                return;

            default:
                log.error("Unsupported on done action {{action: ${action} }}")
        }
}



