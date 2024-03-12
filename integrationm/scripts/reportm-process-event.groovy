import com.cultofbits.customizations.reportm.model.Report
import com.cultofbits.integrationm.service.dictionary.recordm.RecordmSearchHit
import com.google.common.cache.CacheBuilder
import groovy.transform.Field

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
    return
}

def loadDefinitionReports(definitionName) {
    Map<Integer, Report> reportsMap = [:]

    recordm.stream("Reports", "definition:${definitionName} AND template:* AND trigger:EVENT", { RecordmSearchHit hit ->
        log.info("Populating cache for definition reports. Definition=${definitionName}")

        try {
            Report report = new Report(recordm.get(hit.id).getBody(), reportm)
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

reportDefinitionCache.get(definition, { loadDefinitionReports(definition) })
        .each { reportEntry ->

            def id = reportEntry.key
            def report = reportEntry.value

            try {
                if (report.evaluateCondition(msg, log)) {
                    report.executeAsync()
                }
            } catch (Exception e) {
                log.warn("Error evaluating condition of report. {{" +
                        "reportId: ${id}, " +
                        "reportName: ${report.name}, " +
                        "errorMsg: ${e.getMessage()}}", e)
                return
            }
        }