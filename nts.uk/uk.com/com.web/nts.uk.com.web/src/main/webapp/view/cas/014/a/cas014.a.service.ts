module nts.uk.com.view.cas014.a {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    export module service {
        var paths = {
            getAllData: "ctx/sys/auth/grant/rolesetjob/start",
            registerData: "ctx/sys/auth/grant/rolesetjob/register"
        }
        export function saveAsExcel(languageId: string, date: string): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
            let domainType = "CAS014";
            if (program.length > 1){
                program.shift();
                domainType = domainType + program.join(" ");
            }
            return nts.uk.request.exportFile('/masterlist/report/print', {domainId: "JobInfo", domainType:domainType, languageId: languageId, reportType: 0, baseDate: date});
        }

        export function getAllData(refDate: any): JQueryPromise<any> {
            return ajax("com", paths.getAllData, { refDate: refDate });
        };

        export function registerData(data: any): JQueryPromise<any> {
            return ajax("com", paths.registerData, data);
        };

    }
}
