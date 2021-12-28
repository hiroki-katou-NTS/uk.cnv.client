module nts.uk.at.view.kdl014.a.service {
    var paths = {
        getInfo : "screen/at/kdl014/get",
        getEmployeeData: "screen/at/kdl014/getEmployeeData",
        findStampMeanSmartPhone : "screen/at/kdl014/find/stampMeans/smart-phone",
        findstampMeanTimeClock : "screen/at/kdl014/find/stampMeans/time-clock", 
    }
        
    export function getInfo(param: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getInfo, param);
    }

    export function getEmployeeData(param: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getEmployeeData, param);
    }
    
       export function findStampMeanSmartPhone(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.findStampMeanSmartPhone);
    }
    
       export function findstampMeanTimeClock(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.findstampMeanTimeClock);
    }
}

