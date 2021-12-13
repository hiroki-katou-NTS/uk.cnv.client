module nts.uk.at.view.ksu001.a.service {
    let paths: any = {
        getSendingPeriod: "screen/at/schedule/schedule/start/getSendingPeriod",
        getDataStartScreen: "screen/at/schedule/start",
        getDataOfShiftMode: "screen/at/schedule/shift",
        getDataOfShortNameMode: "screen/at/schedule/shortname",
        getDataOfTimeMode: "screen/at/schedule/time",
        getDataChangeMonth: "screen/at/schedule/change-month",
        getDataWhenChangeModePeriod: "screen/at/schedule/change-mode-period",
        orderEmployee: "screen/at/schedule/order-employee",
        validWhenPaste: "screen/at/schedule/valid-when-paste",
        validWhenEditTime: "screen/at/schedule/valid-when-edit-time",
        changeWokPlace: "screen/at/schedule/change-workplace",
        getEvent: "screen/at/schedule/get-event",
        regWorkSchedule: "screen/at/schedule/reg-workschedule",
        checkCorrectHalfday: "screen/at/schedule/correct-worktime-halfday",
        checkTimeIsIncorrect: "ctx/at/shared/workrule/workinghours/checkTimeIsIncorrect",
        changeConfirmedState: "screen/at/schedule/change-confirmed-state",
        getAggregatedInfo: "screen/at/schedule/get-aggregated-info", // get data A11, A12
        get28DayPeriod: "screen/at/schedule/get-28day-period",
        changemode: "screen/at/schedule/change-mode"
    }
    
    export function getDataStartScreen(param): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataStartScreen, param);
    }

    export function getDataOfShiftMode(param): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataOfShiftMode, param);
    }

    export function getDataOfShortNameMode(param): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataOfShortNameMode, param);
    }

    export function getDataOfTimeMode(param): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataOfTimeMode, param);
    }

    export function getSendingPeriod(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getSendingPeriod, obj);
    }

    export function getDataChangeMonth(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataChangeMonth, obj);
    }

    export function getDataWhenChangeModePeriod(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getDataWhenChangeModePeriod, obj);
    }

    export function getListEmpIdSorted(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.orderEmployee, obj);
    }

    export function validWhenPaste(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.validWhenPaste, obj);
    }

    export function validWhenEditTime(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.validWhenEditTime, obj);
    }

    export function changeWokPlace(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.changeWokPlace, obj);
    }

    export function getEvent(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getEvent, obj);
    }
    
    export function regWorkSchedule(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.regWorkSchedule, obj);
    }
    
    export function checkCorrectHalfday(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.checkCorrectHalfday, obj);
    }
    
    export function checkTimeIsIncorrect(obj: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.checkTimeIsIncorrect, obj);
    }

    export function changeConfirmedState(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.changeConfirmedState, obj);
    }
    
    export function getAggregatedInfo(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getAggregatedInfo, obj);
    } 
    
    export function get28DayPeriod(obj): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.get28DayPeriod, obj);
    }
    
    export function changemode(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.changemode);
    }
}