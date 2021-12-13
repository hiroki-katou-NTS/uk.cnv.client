module nts.uk.at.view.kdl051.a {
  export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getChildNursingLeave: "at/record/monthly/nursingleave/getChildNursingLeave",
			getDeitalInfoNursingByEmp: "at/record/monthly/nursingleave/getDeitalInfoNursingByEmp"
		};
		
		export function getChildNursingLeave(listEmpId : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getChildNursingLeave,listEmpId);
        }

		export function getDeitalInfoNursingByEmp(empId : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getDeitalInfoNursingByEmp,empId);
        }
	}
}