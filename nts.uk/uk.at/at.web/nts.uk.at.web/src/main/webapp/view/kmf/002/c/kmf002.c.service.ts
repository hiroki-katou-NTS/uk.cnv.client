module nts.uk.at.view.kmf002.c {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                save: "at/shared/holidaysetting/employee/save",
                find: "at/shared/holidaysetting/employee/findEmployeeMonthDaySetting",
                remove: "at/shared/holidaysetting/employee/remove",
                findFirstMonth: "at/shared/holidaysetting/companycommon/getFirstMonth",
                findAllEmployeeRegister: "at/shared/holidaysetting/employee/findEmployeeMonthDaySetting/findAllEmployeeRegister",
            };
        
        /**
         * 
         */
        export function save(year: string, data: any, sId: string): JQueryPromise<any> {
            let employeeMonthDaySetting: model.EmployeeMonthDaySetting= new model.EmployeeMonthDaySetting(year, sId, []);
            employeeMonthDaySetting.toDto(data);
            let command: any = {};
            command.year = year;
            command.publicHolidayMonthSettings = employeeMonthDaySetting.publicHolidayMonthSettingDto;
            command.employeeID = sId;
            return nts.uk.request.ajax("at", path.save, command);
        }
        
        export function find(year: string, employeeId: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.find + "/" + year + "/" + employeeId);
        }
        
        export function findAllEmployeeRegister(year: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findAllEmployeeRegister + "/" + year);
        }
        
        export function remove(year: string, sId: string, startMonth: number): JQueryPromise<any> {
            let command: any = {};
            command.year = year;
            command.employeeId = sId;
            command.startMonth = startMonth;
            return nts.uk.request.ajax("at", path.remove, command);
        }
        
        export function findFirstMonth(): JQueryPromise<any>{
            return nts.uk.request.ajax("at", path.findFirstMonth);
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
        export class EmployeeMonthDaySetting {
            year: string;
            publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[];
            sId: string;
            
            constructor(year: string, sId: string, publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[]){
                let _self = this;
                _self.year = year;
                _self.publicHolidayMonthSettingDto = publicHolidayMonthSettingDto;
                _self.sId = sId;
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
        
        export class EmployeeMonthDaySettingRemoveCommand {
            year: number;
            sId: number;
            
            constructor(year: number, sId: number){
                let _self = this;
                _self.year = year;
                _self.sId = sId;
            }
        }
        
        export class PublicHolidayMonthSettingDto{
            publicHdManagementYear: number;
            month: number;
            inLegalHoliday: number;
            
            constructor(publicHdManagementYear: number, month: number, inLegalHoliday: number) {
                this.publicHdManagementYear = publicHdManagementYear;
                this.month = month;
                this.inLegalHoliday = inLegalHoliday;
            }
        }
    }
    
}