module nts.uk.at.view.kdl029.a {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			findSuspensionHoliday: "at/request/dialog/suspensionholidays/findSuspensionHoliday"
		};
		
		export function findSuspensionHoliday(param : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.findSuspensionHoliday, param);
        }
	}
}