module nts.uk.at.view.kdw003.a.service {
    var paths: any = {
        startScreen: "screen/at/correctionofdailyperformance/startScreen",
        saveColumnWidth: "screen/at/correctionofdailyperformance/updatecolumnwidth",
        selectErrorCode: "screen/at/correctionofdailyperformance/errorCode",
        selectFormatCode: "screen/at/correctionofdailyperformance/selectCode",
        findCodeName: "screen/at/correctionofdailyperformance/findCodeName",
        findAllCodeName: "screen/at/correctionofdailyperformance/findAllCodeName",
        addAndUpdate: "screen/at/correctionofdailyperformance/addAndUpdate",
        getApplication: "screen/at/correctionofdailyperformance/getApplication",
        addClosure: "screen/at/correctionofdailyperformance/insertClosure",
        releaseClosure: "screen/at/correctionofdailyperformance/releaseClosure",
        findFlexCheck: "screen/at/correctionofdailyperformance/getFlexCheck",
        loadRow: "screen/at/correctionofdailyperformance/loadRow",
        loadVerRow: "screen/at/correctionofdailyperformance/loadVerData",
        getNameMonthlyAttItem: "screen/at/correctionofdailyperformance/getNameMonthlyAttItem",
        searchEmployee: 'screen/at/correctionofdailyperformance/get-info',
        calcTime: 'screen/at/correctionofdailyperformance/calcTime',
        calculation: 'screen/at/correctionofdailyperformance/calculation',
        getRemainNum: 'screen/at/correctionofdailyperformance/getRemainNum',
        lock: 'screen/at/correctionofdailyperformance/lock',
        loadMonth: "screen/at/correctionofdailyperformance/loadMonth",
        initParam: "screen/at/correctionofdailyperformance/initParam",
        genDate: "screen/at/correctionofdailyperformance/gendate", 
        findWplIDByCode: "screen/at/correctionofdailyperformance/findWplIDByCode"
    }

    export function startScreen(param) {
        return nts.uk.request.ajax(paths.startScreen, param);
    }

    export function saveColumnWidth(param) {
        return nts.uk.request.ajax(paths.saveColumnWidth, param);
    }

    export function selectErrorCode(param) {
        return nts.uk.request.ajax(paths.selectErrorCode, param);
    }

    export function selectFormatCode(param) {
        return nts.uk.request.ajax(paths.selectFormatCode, param);
    }

    export function findCodeName(param) {
        return nts.uk.request.ajax(paths.findCodeName, param);
    }

    export function findAllCodeName(param) {
        return nts.uk.request.ajax(paths.findAllCodeName, param);
    }

    export function findWplIDByCode(param) {
        return nts.uk.request.ajax(paths.findWplIDByCode, param);
    }

    export function addAndUpdate(param) {
        return nts.uk.request.ajax(paths.addAndUpdate, param);
    }

    export function getApplication() {
        return nts.uk.request.ajax(paths.getApplication);
    }

    export function addClosure(param) {
        return nts.uk.request.ajax(paths.addClosure, param);
    }

    export function releaseClosure(param) {
        return nts.uk.request.ajax(paths.releaseClosure, param);
    }

    export function findFlexCheck(param) {
        return nts.uk.request.ajax(paths.findFlexCheck, param);
    }

    export function loadRow(param) {
        return nts.uk.request.ajax(paths.loadRow, param);
    }

    export function loadVerRow(param) {
        return nts.uk.request.ajax(paths.loadVerRow, param);
    }

    export function getNameMonthlyAttItem(data: any) {
        return nts.uk.request.ajax(paths.getNameMonthlyAttItem, data);
    }

    export function searchEmployee(employeeCode: string) {
        return nts.uk.request.ajax(paths.searchEmployee + "/" + employeeCode);
    }

    export function calcTime(param: any) {
        return nts.uk.request.ajax(paths.calcTime, param);
    }

    export function calculation(param: any) {
        return nts.uk.request.ajax(paths.calculation, param);
    }

    export function getRemainNum(param: any) {
        return nts.uk.request.ajax(paths.getRemainNum, param);
    }

    export function lock(param: any) {
        return nts.uk.request.ajax(paths.lock, param);

    }

    export function loadMonth(param) {
        return nts.uk.request.ajax(paths.loadMonth, param);
    }

    export function initParam(param) {
        return nts.uk.request.ajax(paths.initParam, param);
    }
    
    export function genDate(param) {
        return nts.uk.request.ajax(paths.genDate, param);
    }
}