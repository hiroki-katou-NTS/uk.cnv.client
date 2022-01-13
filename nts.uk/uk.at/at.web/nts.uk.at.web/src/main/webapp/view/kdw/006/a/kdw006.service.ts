module nts.uk.at.view.kdw006 {
    export module service {
        export class Service {
        }

        let servicePath = {
            start: 'at/record/workrecord/attendanceitemprepare/start',

            getFormat: 'at/record/workrecord/operationsetting/getFormat',
        }

        export function start(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.start);
        }

        export function getFormat(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.getFormat);
        }

        //Export common excel
        export function saveAsExcelCommon(languageId: String): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
            let domainType = "KDW006";
                if (program.length > 1){
                   program.shift();
                   domainType = domainType + program.join(" ");
                }
            return nts.uk.request.exportFile('/masterlist/report/print', { domainId: "OperationSetting", domainType: domainType, languageId: languageId, reportType: 0 });
        }
    }
}
 