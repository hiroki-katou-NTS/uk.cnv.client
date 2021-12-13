module nts.uk.at.view.ksu001.testR {
    export module service {
       let paths: any = {
            findWorkPlaceById: "bs/employee/workplace/info/findDetail" 
        }

       export function getWorkPlaceById(data: any): JQueryPromise<any> {
        return nts.uk.request.ajax("com", paths.findWorkPlaceById, data);
    }

      
    }
}