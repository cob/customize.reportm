import com.cultofbits.customizations.reportm.utils.TemplateUtils

def reportId = argsMap["reportId"]
def reportFile = argsMap["report"]
def emails = argsMap["emails"] as String
def variables = argsMap["variables"] as Map<String, Object>

if (reportId == null || reportFile == null) return

def reportInstanceResponse = recordm.get(reportId.toInteger())
if (!reportInstanceResponse.success()) {
    return json(404, ["success": false])
}

def reportmInstance = reportInstanceResponse.getBody()

reportmInstance.values("Actions").each {
    action ->
        switch (action) {
            case "Send Email":

                Map<String, Object> finalVariables = [:]
                finalVariables["name"] = reportmInstance.value("Name")
                finalVariables["description"] = reportmInstance.value("description")
                finalVariables["sourceInstanceId"] = argsMap["sourceInstanceId"]
                finalVariables.putAll(variables)

                def emailAddresses = emails != null ? emails.split(";").findAll { it != null } : TemplateUtils.apply(reportmInstance.value("Destinations"), finalVariables)
                def emailSubject = TemplateUtils.apply(reportmInstance.value("Subject"), finalVariables)
                def emailBody = TemplateUtils.apply(reportmInstance.value("Body"), finalVariables)

                email.send(emailSubject, emailBody + "\n\n", [to: emailAddresses, attachments: [reportFile]])
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



