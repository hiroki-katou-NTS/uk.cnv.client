module nts.uk.at.view.kaf022.company.service {
    import ajax = nts.uk.request.ajax;
    import exportFile = nts.uk.request.exportFile;

    let paths: any = {
        find: "at/request/application/getalldatabyclosureId",
        //jobId. Muốn lấy được jobid thì phải truyền companyId và baseDate (com)
        findJobId: "bs/employee/jobtitle/findAll",
        // update kaf022
        update: 'screen/at/kaf022/update',
        finAllData : "screen/at/kaf022/findAll"
    };

    export function update(command): JQueryPromise<Array<string>>{
        return ajax("at", paths.update, command);
    }
    export function findAllData():JQueryPromise<Array<string>>{
        return ajax("at", paths.finAllData);
    }
    export function findApp(ids: any): JQueryPromise<void> {
        return ajax("at", paths.find,ids);
    }

    // view model cho thêm đối tượng Kcp003Dto chỉ có 1 trường baseDate: truyền ngày hiện tại
    export function findJobId(date: any): JQueryPromise<void>{
        return nts.uk.request.ajax("com", paths.findJobId, date);
    }

    export function exportExcel(languageId: string, domainId, domainType: string): JQueryPromise<any> {
        return exportFile('/masterlist/report/print', { domainId: domainId, domainType: domainType, languageId: languageId, reportType: 0, data: null });
    }

}