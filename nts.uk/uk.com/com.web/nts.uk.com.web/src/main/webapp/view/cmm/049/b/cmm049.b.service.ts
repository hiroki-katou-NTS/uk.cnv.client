module nts.uk.com.view.cmm049.b {
    
    export module service {
         /**
            *  Service paths
            */
        var servicePath: any = {
            getPCMailCompany: "sys/env/userinfoset/pcmail/company",
            getPCMailPerson: "sys/env/userinfoset/pcmail/person",
            getMobileMailCompany: "sys/env/userinfoset/mobilemail/company",
            getMobileMailPerson: "sys/env/userinfoset/mobilemail/person"
            
        }

        export function getPCMailCompany(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.getPCMailCompany);
        }
        
        export function getPCMailPerson(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.getPCMailPerson);
        }
        
        export function getMobileMailCompany(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.getMobileMailCompany);
        }
        
        export function getMobileMailPerson(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.getMobileMailPerson);
        }
    }
}