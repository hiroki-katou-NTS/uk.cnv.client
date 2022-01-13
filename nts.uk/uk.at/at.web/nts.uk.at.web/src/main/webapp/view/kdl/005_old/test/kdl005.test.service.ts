module kdl005.test {
    export module service {
        var paths: any = {
            getEmployeeList : "at/request/dialog/employmentsystem/getEmployeeList"
        }
        
        export function getEmployeeList(param: EmployeeParam): JQueryPromise<Array<EmployeeBasicInfoDto>> {
            return nts.uk.request.ajax("at", paths.getEmployeeList, param);
        } 
    }
    
    export interface EmployeeParam {
        employeeIds: Array<string>,
        baseDate: string
    }
    
    export interface EmployeeBasicInfoDto {
        personId: string,
        employeeId: string,
        businessName: string,
        gender: number,
        birthday: string,
        employeeCode: string,
        jobEntryDate: string,
        retirementDate: string
    }
}