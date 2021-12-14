module nts.uk.pr.view.ccg007.g {

    export module service {

        // Service paths.
        var servicePath = {
            submitSendMail: "ctx/sys/gateway/sendmail/submit2"
        }

        /**
          * Function is used to check contract.
          */
        export function submitSendMail(data : SendMailInfoFormGCommand): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.submitSendMail, data);
        }
        
        export interface CallerParameter {
            companyCode: string;
            companyName: string;
            contractCode: string;
            employeeCode : string;
            contractPassword: string;
        }
        
        export class SendMailInfoFormGCommand {
            companyCode: string;
            employeeCode : string;
            contractCode: string;
            
            constructor(companyCode: string, employeeCode: string, contractCode: string) {
                this.companyCode = companyCode;
                this.employeeCode = employeeCode;
                this.contractCode = contractCode;
            }
        }
    }
}