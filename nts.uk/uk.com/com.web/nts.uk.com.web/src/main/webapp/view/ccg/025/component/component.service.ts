module nts.uk.com.view.ccg025.a.component {
    export module service {
        var paths = {
            getListRoleByRoleType: "ctx/sys/auth/role/get-list-roles"
        }

        /** get getListRoleByRoleType by roleType */
        export function getListRoleByRoleType(roleType: number, roleAtr: number): JQueryPromise<Array<any>> {
            let param = { 'roleType': roleType, 'roleAtr': roleAtr };
            return nts.uk.request.ajax("com", paths.getListRoleByRoleType, param);
        }

    }
}
