def reportId = argsMap["reportId"]
def emails = argsMap["emails"]
def reportFile = argsMap["report"]

if (reportId == null || reportFile == null) return

recordm.stream("Reports", "id.raw:${reportId}", { hit ->
    def reportName = hit.value('name')
    def description = hit.value('description')

    if (description == null) {
        description = ""
    }

    hit.values("Actions").each {
        action ->
            switch (action) {
                case "Send Email":

                    def finalEmails = emails != null ? emails : hit.value('emails')
                    if (finalEmails == null || finalEmails.trim().length() == 0) {
                        log.info("Stopping sending the report. No emails to send")
                        return
                    }

                    def emailsList = finalEmails.split(";").findAll { it != null }
                    email.send("Report: ${reportName}".toString(), description + "\n\n", [to: emailsList, attachments: [reportFile]])
                    log.info("Report email sent. {{reportFile: ${reportFile}, emails: ${emailsList} }} ")

                    return

                case "Attach to Instance":
                    def instanceId = argsMap["sourceInstanceId"]
                    if (instanceId == null) {
                        log.error("Instance not provided to attach the repoort")
                    }

                    def targetField = hit.value("Definition Field")
                    def fileReport = new File(reportFile.toString())

                    def instance = recordm.get(instanceId)
                    recordm.attach(instanceId.toInteger(), targetField, fileReport.getName(), fileReport)
                    recordm.update(hit.value("Definition"), instanceId, [(targetField): fileReport.getName()])

                    return;

                default:
                    log.error("Unsupported on done action {{action: ${action} }}")
            }
    }
})



