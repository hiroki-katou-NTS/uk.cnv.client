module nts.uk.at.view.kdw008.a.service {
    var BASE_DAILY_PATH = 'at/function/dailyperformanceformat/';
    var paths = {
        addDailyDetail: "at/function/dailyperformanceformat/addAuthorityDailyFormat",
        updateDailyDetail: "at/function/dailyperformanceformat/updateAuthorityDailyFormat",
        duplicateDailyDetail: "at/function/dailyperformanceformat/duplicateAuthorityDailyFormat",
        getAllByCIDAndCode: "at/function/dailyperformanceformat/findAllByCompanyIdAndCode/{0}",


        getListAuthorityDailyFormatCode: "at/function/dailyperformanceformat/getAuthorityDailyFormatCode",
        getDailyPerformance: "at/function/dailyperformanceformat/getAuthorityDailyFormat/{0}/{1}",
        removeAuthorityDailyFormat: "at/function/dailyperformanceformat/removeAuthorityFormat",

        //monthly
        getListMonthlyAttdItem: "at/record/attendanceitem/monthly/findall",
        getListMonPfmCorrectionFormat: "at/function/monthlycorrection/kdw008a/findall",
        getMonPfmCorrectionFormat: "at/function/monthlycorrection/kdw008a/findbycode/{0}",
        addMonPfmCorrectionFormat: "at/function/monthlycorrection/kdw008a/add",
        updateMonPfmCorrectionFormat: "at/function/monthlycorrection/kdw008a/update",
        deleteMonPfmCorrectionFormat: "at/function/monthlycorrection/kdw008a/delete",

        //delete by sheet no
        deleteAuthBySheet: "at/function/dailyperformanceformat/deletebysheet",
        
        //DailyAttendanceItem
        getDailyAttItem: "at/shared/scherec/attitem/getDailyAttItem",

        //MonthlyAttendanceItem
        getMonthlyAttItem: "at/shared/scherec/attitem/getMonthlyAttItem",
        
        getDailyDetail: "at/function/dailyperformanceformat/getAuthorityDailyFormat/{0}/{1}",
        getMonthlyDetail: "at/function/dailyperformanceformat/getAuthorityMonthlyFormat/{0}",

        //mobile
        getDailyMobileDetail: BASE_DAILY_PATH + "mobile/getAuthorityDailyFormat/{0}",
        getMobileListAuthorityDailyFormatCode: BASE_DAILY_PATH + "mobile/getAuthorityDailyFormatCode",
        getMobileDailyPerformance: BASE_DAILY_PATH + "mobile/getAuthorityDailyFormat/{0}",
        addMobileDailyDetail: BASE_DAILY_PATH + "mobile/addAuthorityDailyFormat",
        updateMobileDailyDetail: BASE_DAILY_PATH + "mobile/updateAuthorityDailyFormat",
        removeMobileAuthorityDailyFormat: BASE_DAILY_PATH + "mobile/removeAuthorityFormat",
        getMonthlyMobileDetail: BASE_DAILY_PATH + "mobile/getAuthorityMonthlyFormat/{0}",

    }

    export function duplicateDailyDetail(DuplicateDailyDetailCmd: any): JQueryPromise <any>{
        return nts.uk.request.ajax("at", paths.duplicateDailyDetail, DuplicateDailyDetailCmd);
    }

    export function addDailyDetail(AddAuthorityDailyFormatCommand: any, isMobile: boolean): JQueryPromise<any> {
        let _path = !isMobile ? paths.addDailyDetail : paths.addMobileDailyDetail;
        return nts.uk.request.ajax("at", _path, AddAuthorityDailyFormatCommand);
    };

    export function updateDailyDetail(UpdateAuthorityDailyFormatCommand: any, isMobile: boolean): JQueryPromise<any> {
        let _path = !isMobile ? paths.updateDailyDetail : paths.updateMobileDailyDetail;
        return nts.uk.request.ajax("at", _path, UpdateAuthorityDailyFormatCommand);
    };

    export function getAllByCIDAndCode(formatCode: string): JQueryPromise<any> {
        let _path = nts.uk.text.format(paths.getAllByCIDAndCode, formatCode);
        return nts.uk.request.ajax("at", _path);
    }



    export function getListAuthorityDailyFormatCode(isMobile: boolean): JQueryPromise<any> {
        let _path = nts.uk.text.format(!isMobile ? paths.getListAuthorityDailyFormatCode : paths.getMobileListAuthorityDailyFormatCode );
        return nts.uk.request.ajax("at", _path);
    };

    export function getDailyPerformance(businessTypeCode: string, sheetNo: number, isMobile: boolean): JQueryPromise<any> {

        let _path = '';
        if(!isMobile) {
            _path = nts.uk.text.format(paths.getDailyPerformance, businessTypeCode, sheetNo);
        } else {
            _path = nts.uk.text.format(paths.getMobileDailyPerformance, businessTypeCode);
        }
        
        return nts.uk.request.ajax("at", _path);
    };

    export function removeAuthorityDailyFormat(RemoveAuthorityCommand: any, isMobile: boolean): JQueryPromise<any> {
        let _path = !isMobile ? paths.removeAuthorityDailyFormat : paths.removeMobileAuthorityDailyFormat;
        return nts.uk.request.ajax("at", _path, RemoveAuthorityCommand);
    };

    // monthly
    export function getListMonthlyAttdItem(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", nts.uk.text.format(paths.getListMonthlyAttdItem));
    };

    export function getListMonPfmCorrectionFormat(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", nts.uk.text.format(paths.getListMonPfmCorrectionFormat));
    };

    export function getMonPfmCorrectionFormat(monthlyPfmFormatCode: string): JQueryPromise<any> {
        return nts.uk.request.ajax("at", nts.uk.text.format(paths.getMonPfmCorrectionFormat, monthlyPfmFormatCode));
    };

    export function addMonPfmCorrectionFormat(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.addMonPfmCorrectionFormat, command);
    };

    export function updateMonPfmCorrectionFormat(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.updateMonPfmCorrectionFormat, command);
    };

    export function deleteMonPfmCorrectionFormat(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.deleteMonPfmCorrectionFormat, command);
    };
    //delete by sheet
    export function deleteAuthBySheet(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.deleteAuthBySheet, command);
    };
    
    export function getDailyAttItem(): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.getDailyAttItem);
    }
    export function getMonthlyAttItem(): JQueryPromise < any > {
        return nts.uk.request.ajax("at", paths.getMonthlyAttItem);
    }
    export function getDailyDetail(code: string, sheetNo: number, isMobile: boolean): JQueryPromise < any > {
        let _path = '';
        if(!isMobile) {
            _path = nts.uk.text.format(paths.getDailyDetail, code, sheetNo); 
        } else {
            _path = nts.uk.text.format(paths.getDailyMobileDetail, code);
        }
        
        return nts.uk.request.ajax("at", _path);
    };

    export function getMonthlyDetail(code: string, isMobile: boolean): JQueryPromise < any > {
        let url = !isMobile ? paths.getMonthlyDetail : paths.getMonthlyMobileDetail;
        let _path = nts.uk.text.format(url, code);
        return nts.uk.request.ajax("at", _path);
    };
    
}
