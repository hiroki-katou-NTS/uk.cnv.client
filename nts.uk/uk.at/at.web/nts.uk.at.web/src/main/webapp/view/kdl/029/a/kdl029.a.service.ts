import screenModel = nts.uk.at.view.kdl029.a.screenModel;

module nts.uk.at.view.kdl029.a.service {
    const paths: any = { 
        findAllEmploymentSystem: "at/request/application/employment/system",
        findByEmployee: "at/request/application/employment/getByEmployee"
    }
    
    export function findAllEmploymentSystem(param: any): JQueryPromise<screenModel.EmpRsvLeaveInforDto> {
        return nts.uk.request.ajax('at', paths.findAllEmploymentSystem, param);
    }

    export function findByEmployee(param: any): JQueryPromise<screenModel.EmpRsvLeaveInforDto> {
        return nts.uk.request.ajax('at', paths.findByEmployee, param);
    }

}
