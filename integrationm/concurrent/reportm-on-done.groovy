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

                def emailAddresses = emails != null ? emails.split(";").findAll { it != null } : TemplateUtils.apply(reportmInstance.value("Destinations"), variables)
                def emailSubject = TemplateUtils.apply(reportmInstance.value("Subject"), variables)
                def emailBody = TemplateUtils.apply(reportmInstance.value("Body"), variables)

                email.send(emailSubject, emailBody + "\n\n", [to: emailAddresses, attachments: [reportFile]])
                log.info("Report email sent. {{reportFile: ${reportFile}, emails: ${emailAddresses} }} ")

                return

            case "Attach to Instance":
                def instanceId = argsMap["sourceInstanceId"]
                if (instanceId == null) {
                    log.error("Instance not provided to attach the repoort")
                }

                def targetField = reportmInstance.value("Definition Field")
                def fileReport = new File(reportFile.toString())

                def instance = recordm.get(instanceId)
                recordm.attach(instanceId.toInteger(), targetField, fileReport.getName(), fileReport)
                recordm.update(reportmInstance.value("Definition"), instanceId, [(targetField): fileReport.getName()])
                log.info("Report uploaded to instance. {{instanceId: ${instanceId}, reportFile: ${reportFile} }} ")

                return;

            default:
                log.error("Unsupported on done action {{action: ${action} }}")
        }
}



