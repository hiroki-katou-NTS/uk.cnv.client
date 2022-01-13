module nts.uk.com.view.cmm018.k.service{
    import format = nts.uk.text.format;
    // Service paths.
    var servicePath = {
        searchModeEmployee: "screen/approvermanagement/workroot/getEmployeesInfo",
        personInfor: "workflow/approvermanagement/workroot/getInforPerson",
        jobGroup: "bs/employee/jobtitle/group/approver/getAll",
        jobGroupName: "bs/employee/jobtitle/group/approver/findByCd",
    }    
    /**
     * search data mode employee
     */
    export function searchModeEmployee(input: model.EmployeeSearchInDto) {
        return nts.uk.request.ajax('com', servicePath.searchModeEmployee, input);
    }
    
    export function getPersonInfor(SID: string){
        return nts.uk.request.ajax('com', servicePath.personInfor, SID);
    }
    
    export function jobGroup(){
        return  nts.uk.request.ajax('com', servicePath.jobGroup);   
    }
    export function jobGroupName(){
        return  nts.uk.request.ajax('com', servicePath.jobGroupName);   
    }
    
    export module model{
        export class EmployeeSearchInDto {
                baseDate: Date;
                workplaceIds: string[];
                sysAtr: number;
            }    
        export class EmployeeSearchDto {
                companyId: string;
                pid: string;
                sid: string;
                scd: string;
                pname: string;

                employeeName: string;

                workplaceCode: string;

                workplaceId: string;

                workplaceName: string;
            }
        
        export class PersonInfor{
            sID: string;
            employeeCode: string;
            employeeName: string;
            companyMail: string;    
        }
        
        export class JobtitleInfor{
            /** The company id. */
            companyId: string;
            /** The position id. */
            positionId: string;
            /** The position code. */
            positionCode: string;
            /** The position name. */
            positionName: string;
             /** The sequence code. */
            sequenceCode: string;
            startDate: Date;
            /** The end date. */
            endDate: Date; 
        }
    }
}