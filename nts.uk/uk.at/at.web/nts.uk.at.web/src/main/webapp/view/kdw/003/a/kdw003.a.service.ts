module nts.uk.at.view.kdw003.a.service {
    var paths: any = {
        startScreen: "screen/at/correctionofdailyperformance/startScreen",
        saveColumnWidth: "screen/at/correctionofdailyperformance/updatecolumnwidth",
        selectErrorCode: "screen/at/correctionofdailyperformance/errorCode",
        selectFormatCode: "screen/at/correctionofdailyperformance/selectCode",
        findCodeName: "screen/at/correctionofdailyperformance/findCodeName",
        findAllCodeName: "screen/at/correctionofdailyperformance/findAllCodeName"
    }
    
    export function startScreen(param){
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
    
}