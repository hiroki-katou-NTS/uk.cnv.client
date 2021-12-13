module nts.uk.at.view.kmf001.a.service {
    var paths: any = {

    }

    //export excel
    export function exportExcel(): JQueryPromise<any> {
        let program = __viewContext.program.programName;
        let domainType = "KMF001_" + program;
        let _params = {
            domainId: "EmployeeSystem",
            domainType: domainType,
            languageId: "ja",
            reportType: 0
        };
        return nts.uk.request.exportFile('/masterlist/report/print', _params);
    }


}