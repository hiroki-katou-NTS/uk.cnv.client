module kdl009.test {
    export module service {
        var paths: any = {
            getEmployee : "at/request/dialog/employmentsystem/getEmployee"
        }
            
        export function getEmployee(param: EmployeeParam): JQueryPromise<Array<EmployeeBasicInfoDto>> {
            return nts.uk.request.ajax("at", paths.getEmployee, param);
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