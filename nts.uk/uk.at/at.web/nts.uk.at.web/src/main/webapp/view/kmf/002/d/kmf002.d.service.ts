module nts.uk.at.view.kmf002.d {
    export module service {
        /**
         * define path to service
         */
        var path: any = {
                save: "at/shared/holidaysetting/employment/save",
                find: "at/shared/holidaysetting/employment/findEmploymentMonthDaySetting",
                remove: "at/shared/holidaysetting/employment/remove",
                findFirstMonth: "at/shared/holidaysetting/companycommon/getFirstMonth",
                findAllEmpRegister: "at/shared/holidaysetting/employment/findEmploymentMonthDaySetting/findAllEmpRegister"
            };
        
        /**
         * 
         */
        export function save(year: string, data: any, empCd: string): JQueryPromise<any> {
            let employmentMonthDaySetting: model.EmploymentMonthDaySetting= new model.EmploymentMonthDaySetting(year, empCd, []);
            employmentMonthDaySetting.toDto(data);
            let command: any = {};
            command.year = year;
            command.publicHolidayMonthSettings = employmentMonthDaySetting.publicHolidayMonthSettingDto;
            command.empCd = empCd;
            return nts.uk.request.ajax("at", path.save, command);
        }
        
        export function find(year: string, employmentCode: string): JQueryPromise<any> {
            employmentCode = _.isNull(employmentCode) || _.isEmpty(employmentCode) ? null : employmentCode;
            return nts.uk.request.ajax("at", path.find + "/" + year + "/" + employmentCode);
        }
        
        export function findAllEmpRegister(year: string): JQueryPromise<any> {
            return nts.uk.request.ajax("at", path.findAllEmpRegister + "/" + year);
        }
        
        export function remove(year: string, employmentCode: string, startMonth: number): JQueryPromise<any> {
            let employmentMonthDaySettingRemoveCommand: model.EmploymentMonthDaySettingRemoveCommand= new model.EmploymentMonthDaySettingRemoveCommand(year, employmentCode);
            let command: any = {};
            command.year = year;
            command.empCd = employmentCode;
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
        export class EmploymentMonthDaySetting {
            year: string;
            publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[];
            empCd: string;
            
            constructor(year: string, empCd: string, publicHolidayMonthSettingDto: PublicHolidayMonthSettingDto[]){
                let _self = this;
                _self.year = year;
                _self.publicHolidayMonthSettingDto = publicHolidayMonthSettingDto;
                _self.empCd = empCd;
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
        
        export class EmploymentMonthDaySettingRemoveCommand {
            year: string;
            empCd: string;
            
            constructor(year: string, empCd: string){
                let _self = this;
                _self.year = year;
                _self.empCd = empCd;
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