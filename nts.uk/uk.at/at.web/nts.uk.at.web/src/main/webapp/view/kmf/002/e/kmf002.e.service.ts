module nts.uk.at.view.kmf002.e {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                find: "at/shared/holidaysetting/company/findCompanyMonthDaySetting",
                save: "at/shared/holidaysetting/company/save",
                findFirstMonth: "at/shared/holidaysetting/companycommon/getFirstMonth",
                remove: "at/shared/holidaysetting/company/remove"
            };
        
        /**
         * 
         */
        export function find(year: string): JQueryPromise<any>{
            return nts.uk.request.ajax("at",path.find + "/" + year);
        }

        
        
        export function save(year: string, data: any): JQueryPromise<any> {
            let sysResourceDto: model.SystemResourceDto= new model.SystemResourceDto(year, []);
//            data.sort(function (left, right) { 
//                return left.month == right.month ? 0 : (left.month < right.month ? -1 : 1) 
//            })
            sysResourceDto.toDto(data);
            let command: any = {};
            command.year = year;
            command.publicHolidayMonthSettings = sysResourceDto.publicHolidayMonthSettings
            return nts.uk.request.ajax("at", path.save, command);
        }
        
        export function findFirstMonth(): JQueryPromise<any>{
            return nts.uk.request.ajax("at", path.findFirstMonth);
        }
        
        export function remove(year: string, startMonth: number): JQueryPromise<any> {
            const command = {
              year: year,
              startMonth: startMonth
            };
            return nts.uk.request.ajax("at", path.remove, command);
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
        export class SystemResourceDto {
            year: string;
            publicHolidayMonthSettings: PublicHolidayMonthSettingDto[];
            
            constructor(year: string, publicHolidayMonthSettings: PublicHolidayMonthSettingDto[]){
                let _self = this;
                _self.year = year;
                _self.publicHolidayMonthSettings = publicHolidayMonthSettings;
            }
            
            toDto(data: any): void {
                let _self = this;
                _.forEach(data, function(newValue) {
                    _self.publicHolidayMonthSettings.push(new PublicHolidayMonthSettingDto(_self.year,newValue.month(),newValue.day()));
                    // Increment year
                    if (newValue.month() === 12) {
                      _self.year = String(Number(_self.year) + 1);
                    }
                });
            }
        }
        
        export class CompanyMonthDaySettingRemoveCommand{
            year: number;
            
            constructor(year: number) {
                this.year = year;                    
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