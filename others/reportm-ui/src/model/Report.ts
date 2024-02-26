import $ from "jquery";
import {Argument, ArgumentType, CobApp, ReportAttributes, ReportQuery} from "@/model/Types";

const FIELD_IDENTIFICATION_BLOCK = "Identification";
const FIELD_ONDONE_BLOCK = "On Done Actions";

const FIELD_REPORT_NAME = "Name";
const FIELD_REPORT_DESCRIPTION = "Description";
const FIELD_REPORT_EMAILS = "Emails";
const FIELD_REPORT_TEMPLATE = "Template";

export class Report implements ReportAttributes {
    readonly id: number
    readonly name: string
    readonly description?: string
    readonly emails?: string
    readonly reportTmpl: string

    readonly reportQuery?: ReportQuery
    readonly args: Argument[] = []

    private cobApp: CobApp

    constructor(reportAttributes: ReportAttributes, cobApp: CobApp) {
        this.id = reportAttributes.id
        this.name = reportAttributes.name
        this.description = reportAttributes.description
        this.emails = reportAttributes.emails
        this.reportTmpl = reportAttributes.reportTmpl
        this.args = reportAttributes.args ?? []
        this.reportQuery = reportAttributes.reportQuery

        this.cobApp = cobApp
    }

    /**
     * Trigger the report file generation and download the report
     */
    generateAndDownloadReport() {
        const form = $('<form></form>')
            .attr('action', `/reportm/report`)
            .attr('method', 'post');

        form.append($("<input></input>")
            .attr('type', 'hidden')
            .attr('name', 'report')
            .attr('value', this.reportTmpl));

        if (this.args || this.reportQuery?.query) {
            const argumentsObj = this.getArgsObject()

            form.append($("<input></input>").attr('type', 'hidden')
                .attr('name', "arguments")
                .attr('value', JSON.stringify(argumentsObj)));
        }

        this.cobApp.ui.notification.showInfo("Please wait while report is being generated", true)
        form.appendTo('body')
            .submit()
            .remove();
    }

    /**
     * Trigger the report file generation and send it through the email for the list of email destinations
     */
    async generateAndSendEmail(emails?: string) {
        if (!emails?.length) {
            console.debug("No emails to send the report. Stopping the process here")
            return
        }

        const payload = {
            report: this.reportTmpl,
            arguments: this.getArgsObject(),
            callback: {
                url: `http://localhost:40380/concurrent/reportm-send-by-email?reportId=${this.id}&emails=${encodeURIComponent(emails)}`,
                auth: {
                    type: "COB"
                }
            }
        }

        $.ajax({
            url: `/reportm/report`,
            method: "POST",
            dataType: "json",
            xhrFields: {withCredentials: true},
            cache: false,
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: () => {
                console.debug("Report request accepted")
                this.cobApp.ui.notification.showInfo("New report generation request submitted successfully", false)
            },
            error: (jqXHR: any, textStatus: string) => {
                console.error("Error requesting reportm to build report and send it by email:", textStatus)
                this.cobApp.ui.notification.showError("Error requesting new job for report generation", true)
            }
        })
    }

    /**
     * Build the object with all the argument values including the query if provided
     * @private
     */
    private getArgsObject() {
        const args = this.args.reduce((previousValue: { [name: string]: string | null }, currentValue: Argument) => {
            if (currentValue.value !== null) {
                if (currentValue) {
                    previousValue[currentValue.name] = currentValue.value;
                }
            }
            return previousValue
        }, {})

        if (this.reportQuery?.query) {
            // keeping this pragmatic for now, the argument for the selected ids will be "query".
            // and we only send it if it has a value
            args.query = this.reportQuery.query
        }
        return args;
    }

    static async getReportInstance(request: {
        reportId: number,
        reportQuery?: ReportQuery,
        containerId: string
    }, cobApp: CobApp): Promise<Report> {
        const instance: any = await (new Promise((resolve) => {
                // @ts-ignore
                $.ajax({
                    url: `/recordm/recordm/instances/${request.reportId}`,
                    method: "GET",
                    dataType: "json",
                    xhrFields: {withCredentials: true},
                    cache: false,
                    ignoreErrors: true,
                    complete: (jqXHR: any) => resolve(jqXHR.responseJSON)
                })
            })
        )

        const identificationField = instance.fields.find((field: any) => field.fieldDefinition.name === FIELD_IDENTIFICATION_BLOCK)
        const onDoneField = instance.fields.find((field: any) => field.fieldDefinition.name === FIELD_ONDONE_BLOCK)

        const name = identificationField.fields.find((field: any) => field.fieldDefinition.name === FIELD_REPORT_NAME).value
        const description = identificationField.fields.find((field: any) => field.fieldDefinition.name === FIELD_REPORT_DESCRIPTION).value
        const emails = onDoneField.fields.find((field: any) => field.fieldDefinition.name === FIELD_REPORT_EMAILS).value
        const reportTmpl = Report.getRelativePath(instance.id, identificationField.fields.find((field: any) => field.fieldDefinition.name === FIELD_REPORT_TEMPLATE))

        return new Report({
            id: request.reportId,
            name,
            description,
            emails,
            reportTmpl,
            args: [],
            reportQuery: request.reportQuery
        }, cobApp)
    }

    private static getRelativePath(instanceId: number, field: any) {
        return `${parseInt(String(instanceId / 10000), 10)}/${instanceId}/${field.fieldDefinition.id}/${field.value}`
    }
}