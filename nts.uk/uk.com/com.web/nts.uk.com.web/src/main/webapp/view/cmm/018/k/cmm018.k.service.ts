module nts.uk.com.view.cmm018.k.service{
    import format = nts.uk.text.format;
    // Service paths.
    var servicePath = {
        searchModeEmployee: "workflow/approvermanagement/workroot/getEmployeesInfo",
        personInfor: "workflow/approvermanagement/workroot/getInforPerson",
        JobTitleInfor: "workflow/approvermanagement/workroot/getInforJobTitle",
        jobTitleName: "workflow/approvermanagement/workroot/getJobtitleName"
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
    
    export function getJobTitleInfor(baseDate: any){
        return nts.uk.request.ajax('com', servicePath.JobTitleInfor, baseDate);
    }
    
    export function getJobTitleName(job: any){
        return  nts.uk.request.ajax('com', servicePath.jobTitleName, job);   
    }
    
    export module model{
        export class EmployeeSearchInDto {
                baseDate: Date;
                workplaceIds: string[];
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