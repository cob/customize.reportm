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

    def finalEmails = emails != null ? emails : hit.value('emails')

    if (finalEmails == null || finalEmails.trim().length() == 0) {
        log.info("Stopping sending the report. No emails to send")
        return
    }


    def emailsList = finalEmails.split(";").findAll { it != null }
    email.send("Report: ${reportName}".toString(), description + "\n\n", [to: emailsList, attachments: [reportFile]])
    log.info("Report email sent. {{reportFile: ${reportFile}, emails: ${emailsList} }} ")
})