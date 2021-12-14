module nts.uk.com.view.cas014.b {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    export module service {
        var paths = {
            getAllRoleSet: "ctx/sys/auth/grant/rolesetperson/allroleset",
            getAllRoleSetPerson: "ctx/sys/auth/grant/rolesetperson/selectroleset/{0}",
            registerData: "ctx/sys/auth/grant/rolesetperson/register",
            getEmpInfo: "ctx/sys/auth/grant/rolesetperson/getempinfo/{0}",
            deleteData: "ctx/sys/auth/grant/rolesetperson/delete"
        }
        
        export function saveAsExcel(languageId: string, date: string): JQueryPromise<any> {
                return nts.uk.request.exportFile('/masterlist/report/print', {domainId: "JobInfo", domainType: "CAS014ロールセットの付与", languageId: languageId, reportType: 0, baseDate: date});
        }

        export function getAllRoleSet(): JQueryPromise<any> {
            return ajax("com", paths.getAllRoleSet);
        };

        export function getAllRoleSetPerson(roleSetCd: string): JQueryPromise<any> {
            let _path = format(paths.getAllRoleSetPerson, roleSetCd);
            return nts.uk.request.ajax("com", _path);
        };

        export function getEmployeeInfo(empId: string): JQueryPromise<any> {
            let _path = format(paths.getEmpInfo, empId);
            return nts.uk.request.ajax("com", _path);
        };

        export function registerData(data: any): JQueryPromise<any> {
            return ajax("com", paths.registerData, data);
        };

        export function deleteData(data: any): JQueryPromise<any> {
            return ajax("com", paths.deleteData, data);
        }

    }
}