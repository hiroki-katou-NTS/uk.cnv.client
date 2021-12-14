module nts.uk.com.view.cas003.a {
    export module service {
        var paths: any = {
            
            getAccountLockPolicy:"ctx/sys/gateway/securitypolicy/getAccountLockPolicy",
            getPasswordPolicy:"ctx/sys/gateway/securitypolicy/getPasswordPolicy",
            updateAccountPolicy:"ctx/sys/gateway/securitypolicy/updateAccountPolicy"
            }

     
        
         export function getAccountLockPolicy(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getAccountLockPolicy);
        }
           export function getPasswordPolicy(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getPasswordPolicy);
        }
         export function updateAccountPolicy(command): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.updateAccountPolicy, command);
        }
        //Export common excel
        export function saveAsExcel(languageId: String): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
            let domainType = "CAS003";
            if (program.length > 1) {
                program.shift();
                domainType = domainType + program.join(" ");
            }
            return nts.uk.request.exportFile('/masterlist/report/print', { domainId: "SecuritySetting", domainType: domainType, languageId: languageId, reportType: 0 });
        }
        
    }
}
