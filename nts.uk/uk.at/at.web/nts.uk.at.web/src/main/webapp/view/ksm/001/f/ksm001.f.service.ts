module nts.uk.at.view.ksm001.f {
    
    import UsageSettingDto = nts.uk.at.view.ksm001.a.service.model.UsageSettingDto;
    
    export module service {
        var paths = {
            findCommonGuidelineSetting: "ctx/at/schedule/shift/estimate/guideline/find",
            saveCommonGuidelineSetting: "ctx/at/schedule/shift/estimate/guideline/save",
            findEstimateComparison: "ctx/at/schedule/shift/estimate/estcomparison/find",
//            saveEstimateComparison: "ctx/at/schedule/shift/estimate/estcomparison/save"
        }

        /**
         * call service find setting
         */
        export function findCommonGuidelineSetting(): JQueryPromise<model.CommonGuidelineSettingDto> {
            return nts.uk.request.ajax('at', paths.findCommonGuidelineSetting);
        }

        export function saveCommonGuidelineSetting(command: model.CommonGuidelineSettingDto): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.saveCommonGuidelineSetting, command);
        }
        
        export function findEstimateComparison(): JQueryPromise<model.EstimateComparisonFindDto> {
            return nts.uk.request.ajax('at', paths.findEstimateComparison);
        }
        

        export module model {
            
            
            export interface EstimatedAlarmColorDto {
                guidelineCondition: number;
                color: string;
            }
            
            export interface ReferenceConditionDto {
                yearlyDisplayCondition: number;
                monthlyDisplayCondition: number;
//                alarmCheckCondition: number;
                yearlyAlarmCkCondition: number;
                monthlyAlarmCkCondition: number;
            }
            
            export interface CommonGuidelineSettingDto {
                /** The alarm colors. */
                alarmColors: EstimatedAlarmColorDto[];

                /** The estimate time. */
                estimateTime: ReferenceConditionDto;

                /** The estimate price. */
                estimatePrice: ReferenceConditionDto;

                /** The estimate number of days. */
                estimateNumberOfDays: ReferenceConditionDto;
                
                /** The estimate comparison. */
                estimateComparison: number;
            }
            
            export interface EstimateComparisonFindDto {
                comparisonAtr: number;
            }
            
            
            export interface EstimatedConditionDto {
                code: number;
                name: string;
            }
        }
    }
}