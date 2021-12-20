module nts.uk.pr.view.ccg007.g {

    export module service {

        // Service paths.
        var servicePath = {
            submitSendMail: "ctx/sys/gateway/sendmail/submit2",
            submitSendMailCCG007D: "ctx/sys/gateway/sendmail/submitCCG007D"
        }

        /**
          * Function is used to check contract.
          */
        export function submitSendMail(data : SendMailInfoFormGCommand): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.submitSendMail, data);
        }

        /**
          * Function is used to check contract.
          */
         export function submitSendMailCCG007D(data : SendMailInfoFormGCommand): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.submitSendMailCCG007D, data);
        }
        
        export interface CallerParameter {
            companyCode: string;
            companyName: string;
            contractCode: string;
            employeeCode : string;
            contractPassword: string;
            isFromD: boolean;
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