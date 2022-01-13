module nts.uk.at.view.kdw007.a.service {

    var paths: any = {
    	//  ErrorAlarmWrWebservice
        getAll: "ctx/at/record/workrecord/erroralarm/getall",
        update: "ctx/at/record/workrecord/erroralarm/update",
        remove: "ctx/at/record/workrecord/erroralarm/remove",
        //  EmploymentWebService
        getEmploymentByCode: "bs/employee/employment/findByCode/",
        //  ClassificationWs
        getClassificationByCode: "bs/employee/classification/find/",
        //  JobTitleWebService
        findAllJobTitle: "bs/employee/jobtitle/findAll",
        //  BusinessTypeWebService
        getBusinessTypeByCode: "at/record/businesstype/findByCode",
        //  WorkTypeWebService
        getAllWorkType: "at/share/worktype/findAll",
        //  WorkTypeWebService
        getWorkTypeByListCode: "at/share/worktype/getpossibleworktype",
        //  WorkTimeSettingWS
        getAllWorkTime: "at/shared/worktimeset/findAll",
        //  not use
        getWorkTimeByListCode: "at/shared/worktime/findByCodes",
        //  DivergenceTimeWebService
        getAttendanceItemByCodes: "at/record/divergencetime/setting/AttendanceDivergenceName",
        // not use
        getMonthlyAttendanceItemByCodes: "at/record/divergencetime/setting/getMonthlyAttendanceDivergenceName",
        // not use
        getAllAttendanceItem: "at/record/businesstype/attendanceItem/getAttendanceItems",
        // MonthlyCorrectConditionWs
        getAllMonthlyCondition: "ctx/at/record/workrecord/erroralarm/monthly/getall",
        updateMonthlyCondition: "ctx/at/record/workrecord/erroralarm/monthly/update",
        findMonthlyCondition: "ctx/at/record/workrecord/erroralarm/monthly/findbycheckid/",
        removeMonthlyCondition: "ctx/at/record/workrecord/erroralarm/monthly/remove"
    }

    export function getAll(showTypeAtr: number) {
        return nts.uk.request.ajax(paths.getAll + "/" + showTypeAtr);
    }

    export function update(command) {
        return nts.uk.request.ajax(paths.update, command);
    }
    
    export function remove(code) {
        return nts.uk.request.ajax("at",paths.remove, code);
    }

    export function getEmploymentByCode(code) {
        return nts.uk.request.ajax("com", paths.getEmploymentByCode + code);
    }

    export function getClassificationByCode(code) {
        return nts.uk.request.ajax("com", paths.getClassificationByCode + code);
    }

    export function findAllJobTitle() {
        return nts.uk.request.ajax("com", paths.findAllJobTitle, { baseDate: moment().utc().toISOString() });
    }

    export function getBusinessTypeByCode(code) {
        return nts.uk.request.ajax("at", paths.getBusinessTypeByCode, code);
    }

    export function getAllWorkType() {
        return nts.uk.request.ajax("at", paths.getAllWorkType);
    }

    export function getWorkTypeByListCode(lstCode) {
        return nts.uk.request.ajax("at", paths.getWorkTypeByListCode, lstCode);
    }

    export function getAllWorkTime() {
        return nts.uk.request.ajax("at", paths.getAllWorkTime);
    }
    
     export function getWorkTimeByListCode(lstCode) {
        return nts.uk.request.ajax("at", paths.getWorkTimeByListCode, lstCode);
    }
    
    export function getAttendanceItemByCodes(codes, mode) {
        if (mode == 1) // monthly
            return nts.uk.request.ajax("at", paths.getMonthlyAttendanceItemByCodes, codes);
        else // daily
            return nts.uk.request.ajax("at", paths.getAttendanceItemByCodes, codes);
    }
    
    export function getAllAttendanceItem() {
        return nts.uk.request.ajax("at", paths.getAllAttendanceItem);
    }
    
    export function getAllMonthlyCondition() {
        return nts.uk.request.ajax("at", paths.getAllMonthlyCondition);
    }
    
    export function updateMonthlyCondition(param){
        return nts.uk.request.ajax("at", paths.updateMonthlyCondition, param);
    }
    
    export function findMonthlyCondition(checkId, errorCode) {
        return nts.uk.request.ajax("at", paths.findMonthlyCondition + checkId + "/" + errorCode);
    }
    
    export function removeMonthlyCondition(code) {
        return nts.uk.request.ajax("at",paths.removeMonthlyCondition, code);
    }
    
}