module nts.uk.com.view.cas011.a.service {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;

    var paths = {
            getCompanyIdOfLoginUser:    "ctx/sys/auth/roleset/companyidofloginuser",
            getAllRoleSet:              "ctx/sys/auth/roleset/findallroleset",
            getRoleSetByRoleSetCd:      "ctx/sys/auth/roleset/findroleset/{0}",
            addRoleSet:                 "screen/sys/auth/cas011/addroleset",
            updateRoleSet:              "screen/sys/auth/cas011/updateroleset",
            removeRoleSet:              "screen/sys/auth/cas011/deleteroleset",
            getAllWebMenu:              "sys/portal/webmenu/findallwithnomenubar",
            getRoleById:                "ctx/sys/auth/role/getrolebyroleid/{0}",
            getRoleNameByListId:        "ctx/sys/auth/role/get/rolename/by/roleids",
            saveAsExcel: "file/at/worktypereport/saveAsExcel"
    }

    //get all role set
    export function getAllRoleSet() : JQueryPromise<any>{
        return ajax(paths.getAllRoleSet);
    }

    //get role set
    export function getRoleSetByRoleSetCd(roleSetCd) : JQueryPromise<any>{
        return ajax(format(paths.getRoleSetByRoleSetCd, roleSetCd));
    }

    //insert
    export function addRoleSet(command) : JQueryPromise<any> {
        return ajax(paths.addRoleSet, command);
    }

    //update
    export function updateRoleSet(command) : JQueryPromise<any> {
        return ajax(paths.updateRoleSet, command);
    }

    //delete
    export function removeRoleSet(command) : JQueryPromise<any> {
        return ajax(paths.removeRoleSet, command);
    }

    //get all web menu
    export function getAllWebMenu() : JQueryPromise<any>{
        
        return nts.uk.request.ajax(paths.getAllWebMenu);
    }

    //get company id of login user
    export function getCompanyIdOfLoginUser() : JQueryPromise<any>{
        return ajax(paths.getCompanyIdOfLoginUser);
    }

    //get Role By Id
    export function getRoleById(command) : JQueryPromise<any>{
        return ajax(format(paths.getRoleById, command));
    }
    
    export function getRoleNameByListId(command): JQueryPromise<any>{
        return ajax(paths.getRoleNameByListId, command);
    }
    
    export function saveAsExcel(languageId: string): JQueryPromise<any> {
        let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
        let domainType = "CAS011";
        if (program.length > 1){
            program.shift();
            domainType = domainType + program.join(" ");
        }
        return nts.uk.request.exportFile('/masterlist/report/print', {domainId: "RoleSetMenu", domainType: domainType, languageId: languageId, reportType: 0});
    }
    
}
