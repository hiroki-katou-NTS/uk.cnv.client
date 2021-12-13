module nts.uk.at.view.kdl020.a {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			findAnnualHolidays: "at/request/dialog/annualholidays/findAnnualHolidays"
		};
		
		export function findAnnualHolidays(param : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.findAnnualHolidays, param);
        }
	}
}