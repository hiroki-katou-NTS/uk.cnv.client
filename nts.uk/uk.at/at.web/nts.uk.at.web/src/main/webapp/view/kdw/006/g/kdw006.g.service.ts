module nts.uk.at.view.kdw006.g.service {

    let paths: any = {
        findWorkType: "at/screen/worktype/findAll",
        defaultValue : "at/screen/worktype/find/dailyworktypekdw006",
        getWorkType: 'at/record/workrecord/worktype/get/',
        register: 'at/record/workrecord/worktype/register',
        deleteEmploymentSetting: 'at/record/workrecord/worktype/delete',
        checkSetting: 'at/record/workrecord/worktype/checkSetting',
        copy: "at/record/workrecord/worktype/copy",
        getDistinctEmpCodeByCompanyId: 'at/record/workrecord/worktype/getDistinctEmpCodeByCompanyId',
    }

    export function getAllWorkTypes(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.findWorkType);
    }
    
    export function defaultValue(workTypeClasses : number[]): JQueryPromise<any> {
        let param = {
            dailyWorkTypeAtr : workTypeClasses   
        }
        return nts.uk.request.ajax("at", paths.defaultValue, param);
    }

    export function getWorkTypes(employmentCode: string): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.getWorkType + employmentCode);
    }
    
    export function checkSetting(employmentCode: any): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.checkSetting, employmentCode);
    }
    
    export function copy(command: any): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.copy, command);
    }

    export function deleteEmploymentSetting(employmentCode : any): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.deleteEmploymentSetting, employmentCode);
    }
    export function getDistinctEmpCodeByCompanyId(): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.getDistinctEmpCodeByCompanyId);
    }

    export function register(employmentCode: string, groups1: any, groups2 : any): JQueryPromise<Array<any>> {
        let groups = [];
        _.forEach(groups1 , function( group) {
            groups.push({
                no : group.no,
                name : group.name(),
                workTypeList : group.workTypeCodes
            });
        });
        _.forEach(groups2 , function( group) {
            groups.push({
                no : group.no,
                name : group.name(),
                workTypeList : group.workTypeCodes
            });
        });
        let command = {
            employmentCode: employmentCode,
            groups : groups
        };
        return nts.uk.request.ajax("at", paths.register, command);
    }


}
