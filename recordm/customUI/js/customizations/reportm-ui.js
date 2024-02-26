cob.custom.customize.push(async function(core, utils, ui) {

    const REPORT_DEFINITION = "Reports";


    // ************************************************************************
    // Special input field that will display all available definitions when
    // the field definition has the keyword $definitions
    // ************************************************************************

    const regex = /\$definitions/

    core.customizeInstances(REPORT_DEFINITION, async (instance, presenter) => {
        const definitionsSelectContainer = await loadDefinitions()
            .then(definitions => definitions.map(def => `<option value="${def.name}">${def.name}</option>`).join(""))
            .then(options => $(`<div><select class="js-select-definition"><option></option>${options}</select></div>`))

        presenter.findFieldPs(fp => regex.test(fp.field.fieldDefinition.description))
            .forEach(fp => {
                const $input = fp.content().find(".field-value");

                $input.css('display', 'none')
                $input.after(definitionsSelectContainer.html())

                // need to look for the input in the DOM so I can attach a listener
                fp.content().find(".js-select-definition")
                    .val(fp.getValue())
                    .on('change', function() {
                            $input.val(this.value)
                        }
                    )
            })
    })

    async function loadDefinitions() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: `/recordm/recordm/definitions?included=disabled`,
                method: "GET",
                dataType: "json",
                xhrFields: {withCredentials: true},
                cache: false,
                ignoreErrors: true,
                complete: (jqXHR, textStatus) => resolve(jqXHR.responseJSON)
            })
        })
    }


    // ************************************************************************
    // Code responsible to generate a report.
    // ************************************************************************

    const REPORT_FIELD_NAME = "Template";

    const CLASS_REPORT = "js-report";
    const DATA_REPORT_ID = "data-report-id";

    $(document).on("click", `a.${CLASS_REPORT}`, function(ev) {
        ev.preventDefault();

        const reportInstance = $(ev.target)
        executeReport(parseInt(reportInstance.attr(`${DATA_REPORT_ID}`), 10), null)
    })

    core.customizeColumns(REPORT_DEFINITION, {
        "Execute": function(node, esDoc, colDef) {
            const $node = $(node)
            const $link = $node
                .css("text-align", "center")
                .children()
                .first();

            if (esDoc[REPORT_FIELD_NAME.toLowerCase()].length) {

                if (esDoc['trigger'] && (esDoc['trigger'][0] === 'MANUAL' || esDoc['trigger'][0] === 'SCHEDULED')) {
                    $link.text("Build report")
                    $link.addClass(CLASS_REPORT)
                    $link.attr(DATA_REPORT_ID, esDoc.instanceId)

                } else {
                    $node.addClass("link-disabled").html("Build report")
                }
            }
        }
    })

    function executeReport(reportId, reportQuery) {
        ReportmUI.executeReport({
            reportId,
            reportQuery,
            containerId: core.getAppInstanceId()
        }, {
            core, ui
        })
    }


    // ************************************************************************
    // Code responsible to list all available reports for a definition.
    // ************************************************************************

    function getReports() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: `/recordm/recordm/definitions/search?def=${REPORT_DEFINITION}&q=definition:*`,
                method: "GET",
                dataType: "json",
                xhrFields: {withCredentials: true},
                cache: false,
                ignoreErrors: true,
                complete: function(jqXHR, textStatus) {
                    const hits = jqXHR.responseJSON?.hits?.hits || [];
                    const result = hits.reduce((previousValue, currentValue) => {
                        previousValue[currentValue._id] = currentValue._source
                        return previousValue
                    }, {})

                    return resolve(result)
                }
            })
        })
    }

    function addCustomReportAction(reportIndexedInstance) {
        console.log("Adding custom batch action for report", reportIndexedInstance)
        core.addCustomBatchAction({
            key: `report_${reportIndexedInstance.id}`,
            label: `${reportIndexedInstance["name"][0]}`,
            group: "Reports",
            isAllowed: function(definitionM) {
                return definitionM.getName() === reportIndexedInstance["definition"][0]
            },
            execute: function(definitionId, indexedInstancesM, ctx) {
                window.console.debug('execute report for a total of ', indexedInstancesM.length, "instances");

                const ids = indexedInstancesM.map((i) => i.getInstanceId());
                executeReport(reportIndexedInstance.id, {total: ids.length, query: `id.raw:(${ids.join(' OR ')})`})
            },
            executeOnQuery: async function(definitionId, query, ctx) {
                window.console.debug('execute report on query', query);

                const _$totalAffected = $("div.js-select-all-container.hidden > span.js-total-selected")
                const totalAffected = _$totalAffected.length > 0
                    ? parseInt(_$totalAffected[0].innerText.replaceAll(/[\.,]/g, ""), 10)
                    : 0

                executeReport(reportIndexedInstance.id, {total: totalAffected, query})
            }
        });
    }

    let loadAlreadyCalled = false;

    async function loadReports() {
        if (core.getCurrentLoggedInUser() !== "anonymous" && !loadAlreadyCalled) {
            loadAlreadyCalled = true;
            console.debug("Loading reports")
            await getReports()
                .then(reportsMap => Object.values(reportsMap).forEach(report => addCustomReportAction(report)))
        }
    }

    // Temos de fazer as 2 chamadas porque, ocasionalmente, o evento "updated-app-info" já foi disparado e este
    // subscribe não tinha efeito; nessa situção a chamada directa ao load irá resolver a situação pois já temos a
    // app-info carregada (a flag loadAlreadyCalled garantirá que só fazemos o load uma vez)
    core.subscribe("updated-app-info",loadReports)
    await loadReports();

})