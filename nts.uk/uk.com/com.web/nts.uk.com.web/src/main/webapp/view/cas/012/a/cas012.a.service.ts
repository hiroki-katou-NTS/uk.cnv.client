module nts.uk.com.view.cas012.a.service {
    var paths: any = {
        getMetadata: "ctx/sys/auth/grant/roleindividual/getmetadata",
        getAll: "ctx/sys/auth/grant/roleindividual/findall",
        createRoleIndividual: "ctx/sys/auth/grant/roleindividual/create",
        updateRoleIndividual: "ctx/sys/auth/grant/roleindividual/update",
        deleteRoleIndividual: "ctx/sys/auth/grant/roleindividual/delete"
    }

    export function getMetadata(): JQueryPromise<any> {
        return nts.uk.request.ajax("com", paths.getMetadata);
    }
    
    export function getAll(selectedRoleType: number, selectedCompany: string): JQueryPromise<any> {
        var data = {
            selectedCompany: selectedCompany,
            selectedRoleType: selectedRoleType
        };
        return nts.uk.request.ajax("com", paths.getAll, data);
    }

    export function create(roleIndividual: viewmodel.RoleIndividualGrantBaseCommand): JQueryPromise<void> {
        return nts.uk.request.ajax("com", paths.createRoleIndividual, roleIndividual);
    }
    
    export function update(roleIndividual: viewmodel.RoleIndividualGrantBaseCommand): JQueryPromise<void> {
        return nts.uk.request.ajax("com", paths.updateRoleIndividual, roleIndividual);
    }

    export function deleteRoleIndividual(roleIndividual: viewmodel.RoleIndividualGrantBaseCommand): JQueryPromise<any> {
        return nts.uk.request.ajax("com", paths.deleteRoleIndividual, roleIndividual);
    }

    export function exportExcel(date: string): JQueryPromise<any> {
        let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
        let domainType = "CAS012";
        if (program.length > 1){
            program.shift();
            domainType = domainType + program.join(" ");
        }
        let _params = {domainId: "GrantAdminRole",
            domainType: domainType,
            languageId: 'ja',
            reportType: 0,
            mode: 1,
            baseDate: date
        };
        return nts.uk.request.exportFile('/masterlist/report/print', _params);
    }

}