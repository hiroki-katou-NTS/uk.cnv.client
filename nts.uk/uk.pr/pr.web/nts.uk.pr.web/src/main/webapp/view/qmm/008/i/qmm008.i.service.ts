module nts.uk.pr.view.qmm008.i.service {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    var paths = {
        findAllOfficeAndHistory: "ctx/core/socialinsurance/contributionrate/getAll",
        findContributionRateByHistoryId: "ctx/core/socialinsurance/contributionrate/getByHistoryId/{0}",
        addEmployeePension: "ctx/core/socialinsurance/welfarepensioninsurance/addEmployeePension"
    }
    /**
     * get all
    */
    export function findAllOfficeAndHistory(): JQueryPromise<any> {
        return ajax(paths.findAllOfficeAndHistory);
    }
    
    export function findContributionRateByHistoryId (historyId: string): JQueryPromise<any> {
        return ajax(format(paths.findContributionRateByHistoryId, historyId));
    }
    
    export function addEmployeePension(command): JQueryPromise<any> {
        return ajax(paths.addEmployeePension, command);
    }
}