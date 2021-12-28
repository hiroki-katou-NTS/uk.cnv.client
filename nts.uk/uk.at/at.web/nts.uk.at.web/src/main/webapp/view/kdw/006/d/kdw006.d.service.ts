module nts.uk.at.view.kdw006.d.service {
    let paths: any = {
        getRoles: 'at/record/workrecord/authfuncrest/find-emp-roles',
        findFuncRest: 'at/record/workrecord/authfuncrest/find/',
        register: 'at/record/workrecord/authfuncrest/register',
        getRoleIds: 'at/record/workrecord/authfuncrest/getRoleIds',
        copyDaiPerfAuth: 'at/record/workrecord/authfuncrest/copyDaiPerfAuth'
    }

    export function getRoleList(): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.getRoles);
    }

    export function findFuncRest(roleId: String): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.findFuncRest + roleId);
    }

    export function getRoleIds(): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.getRoleIds);
    }

    export function copyDaiPerfAuth(command: any): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax(paths.copyDaiPerfAuth, command);
    }

    export function register(roleId: string, functionalRestriction: any): JQueryPromise<Array<any>> {
        let command = {
            roleId : roleId,
            authFuncRests : functionalRestriction
        };
        return nts.uk.request.ajax("at", paths.register, command);
    }


}
