module nts.uk.at.view.kdw002.a {
    export module service {
        var paths: any = {
            // getAttendanceItems: "at/share/attendanceitem/getAttendanceItems",
            //  
            getAttendanceItems: "at/record/businesstype/attendanceItem/getAttendanceItems",
            getListDailyAttdItem: "at/shared/scherec/dailyattditem/getalldailyattd",
            getListMonthlyAttdItem: "at/record/attendanceitem/monthly/findall",
            //  ControlOfDailyWS
            getControlOfDailyItem: "at/shared/scherec/daily/findById/",
            //  ControlOfMonthlyWs
            getControlOfMonthlyItem: "at/shared/scherec/monthly/findById/",
            //  ControlOfDailyWS
            updateDaily: "at/shared/scherec/daily/update",
            //  ControlOfMonthlyWs
            updateMonthly: "at/shared/scherec/monthly/update",
            
            //name
            getNameDaily  :"screen/at/correctionofdailyperformance/getNamedailyAttItem",
            getNameMonthly  :"screen/at/correctionofdailyperformance/getNameMonthlyAttItem",
            
            //  AttItemWS
            getDailyAttItem: "at/shared/scherec/attitem/getDailyAttItemUsed",
            //  AttItemWS
            getMontlyAttItem: "at/shared/scherec/attitem/getMonthlyAttItemUsed"
        }
        export function getListDailyAttdItem(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getListDailyAttdItem);
        }
        export function getListMonthlyAttdItem(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getListMonthlyAttdItem);
        }

        export function getControlOfDailyItem(attendanceItemId: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getControlOfDailyItem + attendanceItemId);
        }
        export function getControlOfMonthlyItem(attendanceItemId: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getControlOfMonthlyItem + attendanceItemId);
        }
        export function updateDaily(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.updateDaily, command);
        }

        export function updateMonthly(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.updateMonthly, command);
        }
        //
        export function getNameDaily(listID : any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getNameDaily,listID);
        }
        export function getNameMonthly(listID : any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getNameMonthly,listID);
        }
        export function getDailyAttdItem(roleID?: string): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getDailyAttItem);
        }
        export function getMonthlyAttdItem(roleID?: string): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getMontlyAttItem);
        }
    }
}
