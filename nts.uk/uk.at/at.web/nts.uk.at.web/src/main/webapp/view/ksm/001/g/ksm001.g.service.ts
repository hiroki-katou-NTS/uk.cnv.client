module nts.uk.at.view.ksm001.g {
    
    export module service {
        var paths = {
            findAggregateSetting: "at/schedule/shift/estimate/aggregateset/find",
            saveAggregateSetting: "at/schedule/shift/estimate/aggregateset/save",
            getListPremium: "at/schedule/shift/estimate/aggregateset/getListPremiumNo"
        }

        /**
         * call service find setting
         */
        export function findAggregateSetting(): JQueryPromise<model.AggregateSettingDto> {
            return nts.uk.request.ajax(paths.findAggregateSetting);
        }
        
        /**
         * call service save setting
         */
        export function saveAggregateSetting(command: model.AggregateSettingDto): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.saveAggregateSetting, command);
        }
        
        /**
         * call service get list premium
         */
        export function getListPremium(): JQueryPromise<Array<model.PremiumItemDto>> {
            return nts.uk.request.ajax(paths.getListPremium);
        }
        
        /**
         * define dto
         */
        export module model {
            // 
            export class AggregateSettingDto {
                premiumNo: number[];
                monthlyWorkingDaySettingDto: MonthlyWorkingDaySettingDto;
                
                constructor(premiumNo: number[], monthlyWorkingDaySettingDto: MonthlyWorkingDaySettingDto){
                    this.premiumNo = premiumNo;
                    this.monthlyWorkingDaySettingDto = monthlyWorkingDaySettingDto;
                }
            }
            
            export class MonthlyWorkingDaySettingDto {
                halfDayAtr: number;
                
                yearHdAtr: number;
                
                sphdAtr: number;
                
                havyHdAtr: number;
                
                constructor(halfDayAtr: number, yearHdAtr: number, sphdAtr: number, havyHdAtr: number) {
                    this.halfDayAtr = halfDayAtr;
                    this.yearHdAtr = yearHdAtr;
                    this.sphdAtr = sphdAtr;
                    this.havyHdAtr = havyHdAtr;
                }
            }
            
            export class PremiumItemDto {
                companyId: string;
    
                displayNumber: number;
                
                name: string;
            
                useAtr: number;
                
                constructor(companyId: string, displayNumber: number, name: string, useAtr: number) {
                    this.companyId = companyId;
                    this.displayNumber = displayNumber;
                    this.name = name;
                    this.useAtr = useAtr;
                }
            }
        }
    }
}