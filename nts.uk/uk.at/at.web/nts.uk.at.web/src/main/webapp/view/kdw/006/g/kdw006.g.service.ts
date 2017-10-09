module nts.uk.at.view.kdw006.g.service {

    let paths: any = {
        findWorkType: "at/screen/worktype/findAll",
        getWorkType: 'at/record/workrecord/worktype/get/',
        register: 'at/record/workrecord/worktype/register'
    }

    export function findWorkType(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.findWorkType);
    }

    export function getWorkTypes(employmentCode: string): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.getWorkType + employmentCode);
    }

    export function register(employmentCode: string, groups1: any, groups2 : any): JQueryPromise<Array<any>> {
        var groups = [];
        _.forEach(groups1 , function( group) {
            groups.push({
                no : group.no,
                name : group.name,
                workTypeList : group.workTypeCodes
            });
        });
        _.forEach(groups2 , function( group) {
            groups.push({
                no : group.no,
                name : group.name,
                workTypeList : group.workTypeCodes
            });
        });
        var command = {
            employmentCode: employmentCode,
            groups : groups
        };
        return nts.uk.request.ajax("at", paths.register, command);
    }


}
