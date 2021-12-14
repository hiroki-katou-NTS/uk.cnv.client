module nts.uk.com.view.cas012.c.service {
   var paths: any = {
        getAll: "ctx/sys/auth/grant/roleindividual/getmetadata",
    }
    
    export function getAll() :JQueryPromise<any>{
         return nts.uk.request.ajax("com", paths.getAll);}
    
}