module nts.uk.pr.view.kmf001.d {
    export module service {
        /**
         *  Service paths
         */
        var paths: any = {
            findRetentionYearlyByCompany: 'ctx/at/shared/vacation/setting/retentionyearly/find',
            saveRetentionYearly: 'ctx/at/shared/vacation/setting/retentionyearly/save',
            findByEmployment: 'ctx/at/shared/vacation/setting/employmentsetting/find',
            saveByEmployment: 'ctx/at/shared/vacation/setting/employmentsetting/save',
            deleteByEmployment: 'ctx/at/shared/vacation/setting/employmentsetting/delete',
            findIsManaged: 'ctx/at/share/vacation/setting/annualpaidleave/find/setting',
            findAllByEmployment: 'ctx/at/shared/vacation/setting/employmentsetting/findAll',
            findLeaveCount: "at/shared/scherec/leaveCount/get",
            registerLeaveCount: "at/shared/scherec/leaveCount/register"
        };

        
        export function findRetentionYearly(): JQueryPromise<model.RetentionYearlyFindDto> {
            return nts.uk.request.ajax(paths.findRetentionYearlyByCompany);
        }
        
        export function saveRetentionYearly(retentionYearly: model.RetentionYearlyDto):  JQueryPromise<void> {
            var data = {retentionYearly: retentionYearly};
            return nts.uk.request.ajax(paths.saveRetentionYearly, data);
        }
        
        export function findByEmployment(empCode: string): JQueryPromise<model.EmploymentSettingFindDto> {
            return nts.uk.request.ajax(paths.findByEmployment + "/" + empCode);
        }
        
        export function deleteByEmployment(empCode: string): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.deleteByEmployment + "/" + empCode);
        }
        
        export function saveByEmployment(employmentSetting: model.EmploymentSettingDto):  JQueryPromise<void> {
            var data = {employmentSetting: employmentSetting};
            return nts.uk.request.ajax(paths.saveByEmployment, data);
        }
        
        export function findIsManaged(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.findIsManaged);
        }
        
        export function findAllByEmployment(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.findAllByEmployment);
        }
        
        export function findLeaveCount(): JQueryPromise<any> {
          return nts.uk.request.ajax(paths.findLeaveCount);
        }

        export function registerLeaveCount(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(paths.registerLeaveCount, param);
        }

        /**
        * Model namespace.
        */
        export module model {
            
            export class UpperLimitSettingFindDto{
                retentionYearsAmount: number;
                maxDaysCumulation: number; 
            }
            
            export class RetentionYearlyFindDto {
                upperLimitSetting: UpperLimitSettingFindDto;
                leaveAsWorkDays: boolean;
                managementCategory: number;
            }
            
            export class UpperLimitSettingDto {
                retentionYearsAmount: number;
                maxDaysCumulation: number;
            }
            
            export class RetentionYearlyDto {
                upperLimitSettingDto: UpperLimitSettingDto;
                leaveAsWorkDays: boolean;
                managementCategory: number;
            }
            
            export class EmploymentSettingDto {
                upperLimitSetting: UpperLimitSettingDto;
                employmentCode: string;
                managementCategory: number;
            }
            
            export class EmploymentSettingFindDto {
                upperLimitSetting: UpperLimitSettingDto;
                employmentCode: string;
                managementCategory: number;
            }
        }
    }
}
