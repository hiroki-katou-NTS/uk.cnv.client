module nts.uk.at.view.kdw002.c {
    export module service {
        var paths: any = {
            //daily
            getEmpRole: "at/record/workrecord/authfuncrest/find-emp-roles",
            getDailyAttItem: "at/shared/scherec/attitem/getDailyAttItem",
            getDailyAttdItemByRoleID : "at/shared/scherec/dailyattditem/auth/getdailyattd",
            getListDailyAttdItem : "at/shared/scherec/dailyattditem/getalldailyattd",
            updateDailyAttdItem :"at/shared/scherec/dailyattditem/auth/updatedailyattd",
            getNameDaily  :"screen/at/correctionofdailyperformance/getNamedailyAttItem", 
            //monthly
            getListMonthlyAttdItem:"at/record/attendanceitem/monthly/findall",
            getMontlyAttItem: "at/shared/scherec/attitem/getMonthlyAttItem",
            getMonthlyAttdItemByRoleID : "at/shared/scherec/monthlyattditem/auth/getmonthlyattd",
            updateMonthlyAttdItem :"at/shared/scherec/monthlyattditem/auth/updatemonthlyattd",
            getNameMonthly  :"screen/at/correctionofdailyperformance/getNameMonthlyAttItem",
            //getAll by type
            getNameAttItemByType  :"screen/at/correctionofdailyperformance/getnameattItembytype", // DailyPerformanceCorrectionWebService
            getDailyAttItemNew: "at/shared/scherec/attitem/getdailyattitembyid",
            getMontlyAttItemNew: "at/shared/scherec/attitem/getmonthlyattitembyid",

            // ver8
            getMonthlytRolesByCid: "at/shared/scherec/monthlyattditem/auth/getmonthlyrolesbycid",
            copyMonthlyAttd: "at/shared/scherec/monthlyattditem/auth/copymonthlyattd",

            getDailytRolesByCid: "at/shared/scherec/dailyattditem/auth/getdailyrolesbycid",
            copyDailyAttd: "at/shared/scherec/dailyattditem/auth/copydailyattd"
        }
        //daily
        export function getEmpRole(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getEmpRole);
        }
        export function updateDailyAttdItem(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.updateDailyAttdItem,command);
        }
        
        export function getListDailyAttdItem(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getListDailyAttdItem);
        }
        
        export function getDailyAttdItemByRoleID(roleID:string): JQueryPromise<any> {
            // return nts.uk.request.ajax(paths.getDailyAttdItemByRoleID +"/" +roleID);
            return nts.uk.request.ajax(paths.getDailyAttItem, {roleId: roleID});
        }
        
        export function getNameDaily(listID : any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getNameDaily,listID);
        }
        
        // monthly
        export function getListMonthlyAttdItem(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getListMonthlyAttdItem);
        }
        export function getMonthlyAttdItemByRoleID(roleID:string): JQueryPromise<any> {
            // return nts.uk.request.ajax(paths.getMonthlyAttdItemByRoleID +"/" +roleID);
            return nts.uk.request.ajax(paths.getMontlyAttItem, {roleId: roleID});
        }
        
        export function updateMonthlyAttdItem(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.updateMonthlyAttdItem,command);
        }
        export function getNameMonthly(listID : any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getNameMonthly,listID);
        }
        //get all item by type to ver8
        export function getNameAttItemByType(typeInput : number): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getNameAttItemByType +"/" +typeInput);
        }
        
        export function getDailyAttItemNew(authorityId : string): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getDailyAttItemNew +"/" +authorityId);
        }
        
        export function getMontlyAttItemNew(authorityId : string): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getMontlyAttItemNew +"/" +authorityId);
        }
        // ver8
        export function getMonthlytRolesByCid(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getMonthlytRolesByCid);
        }

        // ver8
        export function getDailytRolesByCid(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getDailytRolesByCid);
        }

        export function copyMonthlyAttd(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.copyMonthlyAttd,command);
        }

        export function copyDailyAttd(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.copyDailyAttd,command);
        }
    }
}
