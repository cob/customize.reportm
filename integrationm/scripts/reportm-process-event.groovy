import com.google.common.cache.CacheBuilder
import groovy.transform.Field
import utils.Report

import java.util.concurrent.TimeUnit

// ========================================================================================================
// Cache management code
// ========================================================================================================
@Field static reportDefinitionCache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

if (msg.product != "recordm" || msg.user == "integrationm") return

if (msg.product == "recordm" && msg.type == "Reports") {
    log.info("Invalidating Reports cache")
    reportDefinitionCache.invalidateAll()
}

def loadDefinitionReports(definitionName) {
    def reportsMap = [:]
    recordm.stream("Reports", "definition:${definitionName} AND template:* AND execution:EVENT", { hit ->
        log.info("Populating cache for definition reports. Definition=${definitionName}")

        try {
            Report report = new Report(recordm, hit)
            reportsMap[report.id] = report

        } catch (Exception e) {
            log.error("Error processing report {{ reportId:${hit.getId()} }}", e)
        }
    })

    return reportsMap
}


// ========================================================================================================
// Report Message management
// ========================================================================================================

def definition = msg.type
reportDefinitionCache.get(definition, { loadDefinitionReports(definition) }).each { reportEntry ->

    def id = reportEntry.key
    def report = reportEntry.value

    try {
        if (!report.evaluateCondition(msg, log)) {
            return
        }
    } catch (Exception e) {
        log.warn("Error evaluating condition of report. {{" +
                "reportId: ${id}, " +
                "reportName: ${report.name}, " +
                "errorMsg: ${e.getMessage()}}", e)
        return
    }

    reportm.generateAsync(
            report.reportTmplPath,
            ["query": "id.raw:${msg.instance.id}".toString()],
            "http://localhost:40380/concurrent/reportm-send-by-email?reportId=${id}"
    )
}
