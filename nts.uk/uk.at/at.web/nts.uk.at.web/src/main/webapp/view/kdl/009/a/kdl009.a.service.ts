module nts.uk.at.view.kdl009.a {
  export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getStartHolidayConf: "at/request/employment/getStartHolidayConf",
			getHolidayConfByEmp: "at/request/employment/getHolidayConfByEmp",
		};
		
		export function getStartHolidayConf(listEmpId : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getStartHolidayConf,listEmpId);
        }

		export function getHolidayConfByEmp(empId : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getHolidayConfByEmp,empId);
        }
	}
}