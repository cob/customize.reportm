import com.cultofbits.customizations.reportm.Report
import java.time.LocalDateTime
import java.time.ZoneId

def weekdays = [
        1: "Monday",
        2: "Tuesday",
        3: "Wednesday",
        4: "Thursday",
        5: "Friday",
        6: "Saturday",
        7: "Sunday"
]

def now = LocalDateTime.now(ZoneId.of("Europe/Lisbon"))

def dayOfMonthQuery = "month_day:${now.dayOfMonth}"
def weekdayQuery = "week_day:${weekdays[now.dayOfWeek.value]}"
def hourQuery = "hour:${now.hour}h"

def query = "execution:SCHEDULED AND template:* AND emails:* AND ( " +
        "(periodicity:MONTHLY AND ${dayOfMonthQuery}  AND ${hourQuery}) " +
        "OR (periodicity:WEEKLY AND ${weekdayQuery} AND ${hourQuery}) " +
        "OR (periodicity:DAILY AND ${hourQuery})" +
        ")"

log.info("Executing scheduled reports that match query [query= ${query}]")

recordm.stream("Reports", query, { hit ->
    def reportInstance = recordm.get(hit.getId()).body

    reportm.generateAsync(
            Report.getReportRelativeFilePath(hit.id, reportInstance.fields.find { field -> field.fieldDefinition.name == "Template" }),
            "http://localhost:40380/concurrent/reportm-send-by-email?reportId=${reportInstance.id}"
    )
})