module nts.uk.com.view.ksm001.m {
    import exportFile = nts.uk.request.exportFile;
    export module service {
        // Service paths.
        var servicePath = {
            saveAsExcel: "person/report/masterData"
        }
        export function saveAsExcel(languageId: string, startDate: any , endDate: any): JQueryPromise<any> {
            return exportFile('/masterlist/report/print', { domainId: "ShiftEstimate", domainType: "KSM001目安時間・金額の登録", languageId: languageId, reportType: 0, startDate : startDate, endDate : endDate });
        }
    }

}