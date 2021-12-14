module nts.uk.at.view.kdr001.a {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                findAll: "at/function/holidaysremaining/findAll",
                saveAsExcel: "at/function/holidaysremaining/report",
                getDate: "at/function/holidaysremaining/getDate",
                getCurrentLoginerRole: "at/function/holidaysremaining/getCurrentLoginerRole",
                getBreakSelection: "at/function/holidaysremaining/getBreakSelection"
            };
        
        
        export function findAll() : JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findAll);
        }
        
        export function saveAsExcel(data){
            return nts.uk.request.exportFile("at", path.saveAsExcel, data);
        }
        
        export function getDate() : JQueryPromise<any>{
            return nts.uk.request.ajax("at", path.getDate);
        }
        
        export function getCurrentLoginerRole() : JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.getCurrentLoginerRole);
        }
        export function getBreakSelection() : JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.getBreakSelection);
        }
    }
}