module nts.uk.at.view.kmf002.b {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                save: "at/shared/holidaysetting/workplace/save",
                find: "at/shared/holidaysetting/workplace/findWorkplaceMonthDaySetting",
                remove: "at/shared/holidaysetting/workplace/remove",
                findFirstMonth: "at/shared/holidaysetting/companycommon/getFirstMonth",
                findAll: "at/shared/holidaysetting/workplace/findWorkplaceMonthDaySetting"                
            };
        
         export function save(year: string, data: any, workplaceId: string): JQueryPromise<any> {
            let workplaceMonthDaySetting: model.WorkplaceMonthDaySetting = new model.WorkplaceMonthDaySetting(year, workplaceId, []);
            workplaceMonthDaySetting.toDto(data);
            let command: any = {};
            command.year = year;
            command.publicHolidayMonthSettings = workplaceMonthDaySetting.publicHolidayMonthSettingDto;
            command.workplaceId = workplaceId;
            return nts.uk.request.ajax("at", path.save, command);
        }
        
        export function find(year: string, workplaceId: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.find + "/" + year + "/" + workplaceId);
        }
        
        export function remove(year: string, startMonth: number, workplaceId: string): JQueryPromise<any> {
            let command: any = {};
            command.year = year;
            command.startMonth = startMonth;
            command.workplaceId = workplaceId;
            return nts.uk.request.ajax("at", path.remove, command);
        }
        
        export function findFirstMonth(): JQueryPromise<any>{
            return nts.uk.request.ajax("at", path.findFirstMonth);
        }
        
        /**
         * Get all workplace was register according to year
         */    
        export function findAll(year: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findAll + "/" + year );
        }

        export function findHolidayConfig(): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findHolidayConfig);
        }
        
        export function saveAsExcel(mode: string, startDate: string, endDate: string): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
            let domainType = "KMF002";
            if (program.length > 1) {
                program.shift();
                domainType = domainType + program.join(" ");
            }
            return nts.uk.request.exportFile('/masterlist/report/print',
                {
                    domainId: "HolidaySetting",
                    domainType: domainType,
                    languageId: 'ja', reportType: 0,
                    startDate: moment.utc(startDate).format(),
                    endDate: moment.utc(endDate).format()
                });
        }
        
    }
    
    /**
     * Model define.
     */
    export module model {
        export class WorkplaceMonthDaySetting {
            year: string;
            publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[];
            workplaceId: string;
            
            constructor(year: string, workplaceId: string, publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[]){
                let _self = this;
                _self.year = year;
                _self.publicHolidayMonthSettingDto = publicHolidayMonthSettingDto;
                _self.workplaceId = workplaceId;
            }
            
            toDto(data: any): void {
                let _self = this;
                _.forEach(data, function(newValue) {
                    _self.publicHolidayMonthSettingDto.push(new PublicHolidayMonthSettingDto(_self.year, newValue.month(), newValue.day()));
                    // Increment year
                    if (newValue.month() === 12) {
                      _self.year = String(Number(_self.year) + 1);
                    }
                });
            }
        }
        
        export class WorkplaceMonthDaySettingRemoveCommand {
            year: number;
            workplaceId: string;
            
            constructor(year: number, workplaceId: string){
                let _self = this;
                _self.year = year;
                _self.workplaceId = workplaceId;
            }
        }
        
        export class PublicHolidayMonthSettingDto{
            publicHdManagementYear: string;
            month: number;
            inLegalHoliday: number;
            
            constructor(publicHdManagementYear: string, month: number, inLegalHoliday: number) {
                this.publicHdManagementYear = publicHdManagementYear;
                this.month = month;
                this.inLegalHoliday = inLegalHoliday;
            }
        }
    }
    
}