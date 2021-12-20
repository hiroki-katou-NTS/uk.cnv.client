module nts.uk.at.view.kdw001.b {
    export module service {
        var path: any = {
                findMonthlyResult: "at/shared/workclosuredate/findbyclosureid",
                getDataClosure: 'at/record/log/getDataClosure',
                isCreatingFutureDay: "at/record/createDailyResultsCondtion/isCreatingFutureDay"
            };
        
        export function findMonthlyResult(closureId: number, yearMonth: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findMonthlyResult + "/" + closureId);
        }

        export function findDataClosure(closureId: number): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.getDataClosure + "/" + closureId);
        }

        export function isCreatingFutureDay(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(path.isCreatingFutureDay, param);
        }
    }
}
