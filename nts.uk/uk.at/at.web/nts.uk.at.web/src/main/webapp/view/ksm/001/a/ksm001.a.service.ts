module nts.uk.at.view.ksm001.a {
    export module service {
        var paths = {
            findCompanySettingEstimate: "ctx/at/schedule/shift/estimate/usagesetting/find",
            findCompanyEstablishment: "ctx/at/schedule/shift/estimate/company/find",
            saveCompanyEstablishment: "ctx/at/schedule/shift/estimate/company/save",
            deleteCompanyEstablishment: "ctx/at/schedule/shift/estimate/company/delete",
            findEmploymentEstablishment: "ctx/at/schedule/shift/estimate/employment/find",
            saveEmploymentEstablishment: "ctx/at/schedule/shift/estimate/employment/save",
            deleteEmploymentEstablishment: "ctx/at/schedule/shift/estimate/employment/delete",
            findAllEmploymentSetting: "ctx/at/schedule/shift/estimate/employment/findAll",
            findPersonalEstablishment: "ctx/at/schedule/shift/estimate/personal/find",
            findAllPersonalSetting: "ctx/at/schedule/shift/estimate/personal/findAll",
            savePersonalEstablishment: "ctx/at/schedule/shift/estimate/personal/save",
            deletePersonalEstablishment: "ctx/at/schedule/shift/estimate/personal/delete"
        }

        export function saveAsExcel(languageId: string, startDate: any, endDate: any): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
            let domainType = "KSM001";
            if (program.length > 1){
                program.shift();
                domainType = domainType + program.join(" ");
            }
            let _params = {
                domainId: "ShiftEstimate",
                domainType: domainType,
                languageId: languageId,
                reportType: 0,              
                startDate: startDate,
                endDate: endDate
            };
            return nts.uk.request.exportFile('/masterlist/report/print', _params);
        }
        
        /**
         * call service get all monthly estimate time of company
         */
        export function findCompanyEstablishment(targetYear: number): JQueryPromise<model.CompanyEstablishmentDto> {
            return nts.uk.request.ajax('at', paths.findCompanyEstablishment + '/' + targetYear);
        }

        /**
        * call service save estimate company
        */
        export function saveCompanyEstablishment(targetYear: number, dto: model.CompanyEstablishmentDto): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.saveCompanyEstablishment, {
                estimateTime: dto.estimateTime,
                estimatePrice: dto.estimatePrice,
                estimateNumberOfDay: dto.estimateNumberOfDay,
                targetYear: targetYear
            });
        }
        
        /**
        * call service delete estimate company
        */
        export function deleteCompanyEstablishment(targetYear: number): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.deleteCompanyEstablishment, {
                targetYear: targetYear
            });
        }
        
        /**
         * call service get all monthly estimate time of employment
         */
        export function findEmploymentEstablishment(targetYear: number, employmentCode: string): JQueryPromise<model.CompanyEstablishmentDto> {
            return nts.uk.request.ajax('at', paths.findEmploymentEstablishment + '/' + employmentCode + '/' + targetYear);
        }

        /**
        * call service save estimate employment
        */
        export function saveEmploymentEstablishment(targetYear: number, dto: model.EmploymentEstablishmentDto): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.saveEmploymentEstablishment, {
                estimateTime: dto.estimateTime,
                estimatePrice: dto.estimatePrice,
                estimateNumberOfDay: dto.estimateNumberOfDay,
                employmentName: dto.employmentName,
                employmentCode: dto.employmentCode,
                targetYear: targetYear
            });
        }
        /**
        * call service delete estimate employment
        */
        export function deleteEmploymentEstablishment(targetYear: number, employmentCode: string): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.deleteEmploymentEstablishment, {
                targetYear: targetYear,
                employmentCode: employmentCode
            });
        }
        
        /**
         * call service get all monthly estimate time of personal
         */
        export function findPersonalEstablishment(targetYear: number, employeeId: string): JQueryPromise<model.PersonalEstablishmentDto> {
            return nts.uk.request.ajax('at', paths.findPersonalEstablishment + '/' + targetYear + '/' + employeeId);
        }

        /**
        * call service save estimate personal
        */
        export function savePersonalEstablishment(targetYear: number, dto: model.PersonalEstablishmentDto): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.savePersonalEstablishment, {
                estimateTime: dto.estimateTime,
                estimatePrice: dto.estimatePrice,
                estimateNumberOfDay: dto.estimateNumberOfDay,
                employeeId: dto.employeeId,
                targetYear: targetYear
            });
        }
        
        /**
        * call service delete estimate personal
        */
        export function deletePersonalEstablishment(targetYear: number, employeeId: string): JQueryPromise<void> {
            return nts.uk.request.ajax('at', paths.deletePersonalEstablishment, {
                employeeId: employeeId,
                targetYear: targetYear
            });
        }
        
        /**
         * call service find setting estimate 
         */
        export function findCompanySettingEstimate(): JQueryPromise<model.UsageSettingDto> {
            return nts.uk.request.ajax('at', paths.findCompanySettingEstimate);
        }

        /**
         * call service find all setting employment
         */
        export function findAllEmploymentSetting(targetYear: number): JQueryPromise<model.EmploymentEstablishmentDto[]>{
            return nts.uk.request.ajax('at', paths.findAllEmploymentSetting+'/'+targetYear);
        }
        
        /**
         * call service find all setting personal
         */
        export function findAllPersonalSetting(targetYear: number): JQueryPromise<model.PersonalEstablishmentDto[]> {
            return nts.uk.request.ajax('at', paths.findAllPersonalSetting + '/' + targetYear);
        }
        
        export module model {
            
            export interface UsageSettingDto {
                employmentSetting: boolean;
                personalSetting: boolean;
            }

            export interface EstimateTimeDto {
                month: number;
                time1st: number;
                time2nd: number;
                time3rd: number;
                time4th: number;
                time5th: number;

            }
            export interface EstimatePriceDto {
                month: number;
                price1st: number;
                price2nd: number;
                price3rd: number;
                price4th: number;
                price5th: number;

            }
            
            export interface EstimateDaysDto {
                month: number;
                days1st: number;
                days2nd: number;
                days3rd: number;
                days4th: number;
                days5th: number;

            }

            export interface EstablishmentTimeDto {
                monthlyEstimates: EstimateTimeDto[];
                yearlyEstimate: EstimateTimeDto;
            }
            

            export interface EstablishmentPriceDto {
                monthlyEstimates: EstimatePriceDto[];
                yearlyEstimate: EstimatePriceDto;
            }
            
            export interface EstablishmentDaysDto {
                monthlyEstimates: EstimateDaysDto[];
                yearlyEstimate: EstimateDaysDto;
            }

            export interface CompanyEstablishmentDto {
                estimateTime: EstablishmentTimeDto;
                estimatePrice: EstablishmentPriceDto;
                estimateNumberOfDay: EstablishmentDaysDto;
                setting: boolean;
            }
            

            export interface EmploymentEstablishmentDto {
                employmentCode: string;
                employmentName: string;
                estimateTime: EstablishmentTimeDto;
                estimatePrice: EstablishmentPriceDto;
                estimateNumberOfDay: EstablishmentDaysDto;
            }
            export interface PersonalEstablishmentDto {
                employeeId: string;
                estimateTime: EstablishmentTimeDto;
                estimatePrice: EstablishmentPriceDto;
                estimateNumberOfDay: EstablishmentDaysDto;
            }

        }
    }
    


}