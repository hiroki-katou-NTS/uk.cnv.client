module nts.uk.com.view.cmf003.f {
    
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    
    export module service {
        
        var paths = {
            findDataStorageMng: "ctx/sys/assist/app/findDataStorageMng/{0}",
            findResultOfSaving: "ctx/sys/assist/app/findResultOfSaving/{0}",
            setInterruptSaving: "ctx/sys/assist/app/setInterruptSaving",
            deleteDataStorageMng: "ctx/sys/assist/app/deleteDataStorageMng"
        }   
    
        /**
         * get DataStorageMng
         */
        export function findDataStorageMng(storeProcessingId: string): JQueryPromise<any> {
            let _path = format(paths.findDataStorageMng, storeProcessingId);
            return ajax('com', _path);
        }
        
        /**
         * delete DataStorageMng when interupt/error/done
         */
        export function deleteDataStorageMng(command: any): JQueryPromise<any> {
            return ajax("com", paths.deleteDataStorageMng, command);
        }
        
        /**
         * find ResultOfSaving to get fileID
         */
        export function findResultOfSaving(storeProcessingId: string): JQueryPromise<any> {
            let _path = format(paths.findResultOfSaving, storeProcessingId);
            return ajax('com', _path);
        }
        
        /**
         * update interrupt process
         */
        export function setInterruptSaving(command: any): JQueryPromise<any> {
            return ajax("com", paths.setInterruptSaving, command);
        }
    }
}