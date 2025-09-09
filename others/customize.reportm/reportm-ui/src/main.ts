import {createApp} from 'vue'
import ReportApp from './ReportApp.vue'
import {Report} from "@/model/Report";
import {CobApp, ReportQuery} from "@/model/Types";

async function executeReport(request: { reportId: number, reportQuery?: ReportQuery, containerId: string }, cobApp: CobApp) {
    const report = await Report.getReportInstance(request, cobApp)

    const divReportApp = document.createElement("div");
    divReportApp.id = "reportApp"
    document.getElementById(request.containerId)!!.appendChild(divReportApp);

    const reportApp = createApp(ReportApp, {
        report,
        onCloseRequest: () => {
            reportApp?.unmount()
            document.getElementById("reportApp")?.remove()
        }
    })
    reportApp.mount('#reportApp')

}

export default {executeReport}

//
// **********************************
//  Comment the previous function and
//  uncomment the next block to use the
//  npm run serve for development
// **********************************
// import {ArgumentType} from "@/model/Types";
//
// const report = new Report({
//         id: 1,
//         name: "Report name",
//         description: "A simple description of the report",
//         reportTmpl: "513/5138331/13518/relatorio-kpi-gt_20220512152842.xlsx",
//         emails: "hugo.m.marcelino@gmail.com",
//         args: [
//             {
//                 name: "Cpe",
//                 type: ArgumentType.TEXT
//             },
//             {
//                 name: "Data instalação1",
//                 type: ArgumentType.DATE
//             },
//             {
//                 name: "Data instalação 2",
//                 type: ArgumentType.DATE
//             },
//             {
//                 name: "Data instalação 3",
//                 type: ArgumentType.DATE
//             },
//             {
//                 name: "Data instalação 4",
//                 type: ArgumentType.DATE
//             },
//             {
//                 name: "Data instalação 5",
//                 type: ArgumentType.DATE
//             },
//         ],
//         reportQuery: {
//             total: 10,
//             query: "id.raw:(1 OR 2 OR 3)"
//         }
//     }
// )
// const btn = document.createElement("button");
// btn.innerHTML = "Show Report Form";
// btn.onclick = () => executeReport(1)
// document.body.appendChild(btn);
//
// async function executeReport(reportId: number, reportQuery?: ReportQuery) {
//     const divReportApp = document.createElement("div");
//     divReportApp.id = "reportApp"
//     document.body.appendChild(divReportApp);
//
//     const app = createApp(ReportApp, {
//         report,
//         onCloseRequest: () => {
//             app?.unmount()
//             document.getElementById("reportApp")?.remove()
//         }
//     })
//     app.mount('#reportApp')
// }