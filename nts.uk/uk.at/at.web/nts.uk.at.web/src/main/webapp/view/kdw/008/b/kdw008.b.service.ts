module nts.uk.at.view.kdw008.b.service {
    let BASE_MOBILE_PATH = 'at/function/businesstype/mobile/';
    var paths = {
        addDailyDetail: "at/record/businesstype/addBusTypeFormat",
        updateDailyDetail: "at/record/businesstype/updateBusTypeFormat",
        //                addMonthlyDetail: "at/record/businesstype/addBusinessTypeMonthlyDetail",
        //                updateMonthlyDetail: "at/record/businesstype/updateBusinessTypeM                  
        getBusinessType: "at/record/businesstype/findAll",
        getDailyDetail: "at/record/businesstype/findBusinessTypeDailyDetail/{0}/{1}",
        getMonthlyDetail: "at/record/businesstype/findBusinessTypeMonthlyDetail/{0}",

        //monthly
        getListMonthlyAttdItem: "at/record/attendanceitem/monthly/findall",
        getNameMonthly: "screen/at/correctionofdailyperformance/getNameMonthlyAttItem",

        // monthly tab3
        getListMonthRight: "at/function/monthlycorrection/findbycode/{0}",
        updateMonthly: "at/function/monthlycorrection/updatemonthly",
        

        //delete by sheet
        deleteBusiFormatBySheet: "at/record/businesstype/deletebysheet",

        //DailyAttendanceItem
        getDailyAttItem: "at/shared/scherec/attitem/getDailyAttItem",

        //MonthlyAttendanceItem
        getMonthlyAttItem: "at/shared/scherec/attitem/getMonthlyAttItem",

        // mobile 
        addMobileDailyDetail: BASE_MOBILE_PATH + "addBusinessTypeDailyDetail",
        updateMobileDailyDetail: BASE_MOBILE_PATH + "updateBusTypeFormat",
        getMobileDailyDetail: BASE_MOBILE_PATH + "findBusinessTypeDailyDetail/{0}",
        getMobileMonthlyDetail: BASE_MOBILE_PATH + "findBusinessTypeMonthlyDetail/{0}",

        // copy
        getListMonthlyRecordWorkType: "at/function/monthlycorrection/findall",
        copyMonthly: "at/function/monthlycorrection/copy",
        copyDaily: "at/record/businesstype/copy",
        checkMode: "at/record/businesstype/checkDailyMode/{0}"

    }

    export function checkMode(businessTypeCode: string): JQueryPromise <any> {
        let _path = nts.uk.text.format(paths.checkMode, businessTypeCode);
        return nts.uk.request.ajax("at", _path);
    }

    export function getListMonthlyRecordWorkType(): JQueryPromise<any> {
        return nts.uk.request.ajax(paths.getListMonthlyRecordWorkType);
    };

    export function copyMonthly(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax(paths.copyMonthly, command);
    };

    export function copyDaily(command: any): JQueryPromise <any> {
        return nts.uk.request.ajax(paths.copyDaily, command);
    };

    export function addDailyDetail(AddBusTypeCommand: any, isMobile: boolean): JQueryPromise < any > {
        let _path = !isMobile ? paths.addDailyDetail : paths.addMobileDailyDetail;
        return nts.uk.request.ajax("at", _path, AddBusTypeCommand);
    };

    export function updateDailyDetail(UpdateBusTypeCommand: any, isMobile: boolean): JQueryPromise < any > {
        let _path = !isMobile ? paths.updateDailyDetail : paths.updateMobileDailyDetail;
        return nts.uk.request.ajax("at", _path, UpdateBusTypeCommand);
    };
    //monthly
    export function updateMonthly(command: any): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.updateMonthly, command);
    };

    //delete by sheet 
    export function deleteBusiFormatBySheet(command: any): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.deleteBusiFormatBySheet, command);
    };
    //            addMonthlyDetail(AddBusinessTypeMonthlyCommand: any): JQueryPromise<any> {
    //                return nts.uk.request.ajax("at", paths.addMonthlyDetail, AddBusinessTypeMonthlyCommand);
    //            };

    //            updateMonthlyDetail(UpdateBusinessTypeMonthlyCommand: any): JQueryPromise<any> {
    //                return nts.uk.request.ajax("at", paths.updateMonthlyDetail, UpdateBusinessTypeMonthlyCommand);
    //            };

    export function getBusinessType(): JQueryPromise < any > {
        let _path = nts.uk.text.format(paths.getBusinessType);
        return nts.uk.request.ajax("at", _path);
    };

    export function getDailyDetail(businessTypeCode: string, sheetNo: number, isMobile: boolean): JQueryPromise < any > {
        let _path = '';
        if(!isMobile) {
            _path = nts.uk.text.format(paths.getDailyDetail, businessTypeCode, sheetNo);
        } else {
            _path = nts.uk.text.format(paths.getMobileDailyDetail, businessTypeCode);
        }
        return nts.uk.request.ajax("at", _path);
    };

    export function getMonthlyDetail(businessTypeCode: string, isMobile: boolean): JQueryPromise < any > {
        let url = !isMobile ? paths.getMonthlyDetail : paths.getMobileMonthlyDetail;
        let _path = nts.uk.text.format(url, businessTypeCode);
        return nts.uk.request.ajax("at", _path);
        
    };

    export function getListMonthRight(businessTypeCode: string): JQueryPromise < any > {
        let _path = nts.uk.text.format(paths.getListMonthRight, businessTypeCode);
        return nts.uk.request.ajax("at", _path);
    };

    // monthly
    export function getListMonthlyAttdItem(): JQueryPromise < any > {
        return nts.uk.request.ajax("at", nts.uk.text.format(paths.getListMonthlyAttdItem));
    };
    export function getNameMonthly(listID : any): JQueryPromise < any > {
        return   nts.uk.request.ajax("at", paths.getNameMonthly, listID);
    }

    export function getDailyAttItem(): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.getDailyAttItem);
    }
    export function getMonthlyAttItem(): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.getMonthlyAttItem);
    }

}
