module nts.uk.com.view.cps001.g {
       
    export module service {
        import format = nts.uk.text.format;
        import ajax = nts.uk.request.ajax;
        /**
         *  Service paths
         */
        var servicePath: any = {
            getAllList: "bs/employee/temporaryabsence/frame/getList",
            getAllListByCheckState: "at/record/remainnumber/annlea/getAnnLeaByCheckState",
            getDetails: "bs/employee/temporaryabsence/frame/getDetail",
            lostFocus: "at/record/remainnumber/annlea/lostFocus",
            add: "at/record/remainnumber/annlea/add",
            update: "at/record/remainnumber/annlea/update",
            deleteLeav: "at/record/remainnumber/annlea/delete",
        };
        
        export function getAllList(): JQueryPromise<any> {
            return ajax(servicePath.getAllList);
        }
        
        export function getAllListByCheckState(employeeId: string, checkState: boolean): JQueryPromise<any> {
            return ajax('at', servicePath.getAllListByCheckState,employeeId,checkState);
        }
        
        export function lostFocus(grantDate: Date): JQueryPromise<any> {
            return ajax('at', servicePath.lostFocus, moment.utc(grantDate,"YYYY/MM/DD"));
        }
        
        export function getDetail(grantDate: Date) {
            return ajax(servicePath.getDetails,moment.utc(grantDate, "YYYY/MM/DD"));
        }
        
        export function add(command: any) {
            return ajax('at', servicePath.add, command);
        }
        export function update(command: any) {
            return ajax('at', servicePath.update, command);
        }
        export function deleteLeav(command: any) {
            return ajax('at', servicePath.deleteLeav, command);
        }
    }
}
