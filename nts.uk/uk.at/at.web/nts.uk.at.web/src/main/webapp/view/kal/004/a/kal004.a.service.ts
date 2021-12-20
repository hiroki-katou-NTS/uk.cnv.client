module nts.uk.at.view.kal004.a.service {
    import format = nts.uk.text.format;
    import share = nts.uk.at.view.kal004.share.model;
        var paths = {
            getAlarmPattern: "at/function/alarm/pattern/setting",
            getCheckConditionCode: "at/function/alarm/checkCodePattern",
            addAlarmPattern: "at/function/alarm/addPatternAlarm",
            updateAlarmPattern: "at/function/alarm/updatePatternAlarm",
            removeAlarmPattern: "at/function/alarm/removePatternSetting",
            getEnumAlarmCategory : "at/function/alarm/getEnumAlarmCategory",
            processingYm: "at/function/alarm/kal/001/processingym"
        }
        
        
        export function getAlarmPattern(): JQueryPromise<Array<share.AlarmPatternSettingDto>> {
            return nts.uk.request.ajax("at", paths.getAlarmPattern);
        }        

        export function getCheckConditionCode(): JQueryPromise<Array<share.AlarmCheckConditonCodeDto>> {                              
              return nts.uk.request.ajax("at", paths.getCheckConditionCode);
        }         
        

        /** add Alarm pattern setting */
        export function addAlarmPattern(alarm: share.AddAlarmPatternSettingCommand): JQueryPromise<void> {
            return nts.uk.request.ajax("at", paths.addAlarmPattern, alarm);
        }
    
        /** remove Alarm pattern setting */
        export function removeAlarmPattern(query: any): JQueryPromise<void> {
            return nts.uk.request.ajax("at", paths.removeAlarmPattern, query);
        }
        
        /** Update Alarm pattern setting */
        export function updateAlarmPattern(alarm: share.AddAlarmPatternSettingCommand): JQueryPromise<void> {
            return nts.uk.request.ajax("at", paths.updateAlarmPattern, alarm );
        }
        /** Get enum alarm category */
        export function getEnumAlarm(): JQueryPromise<Array<share.EnumConstantDto>> {
            return nts.uk.request.ajax(paths.getEnumAlarmCategory);
        }
    
        /** Get processing year month */
        export function getProcessingYm(): JQueryPromise<number> {
            return nts.uk.request.ajax(paths.processingYm);
        }

}
