module nts.uk.at.view.kdl029.test {
    export module service {
        var paths: any = {
            getEmployeeList : "at/request/dialog/suspensionholidays/getSid"
        }
        
        export function getEmployeeList(): JQueryPromise<Array<any>> {
            return nts.uk.request.ajax("at", paths.getEmployeeList);
        } 
    }   

}