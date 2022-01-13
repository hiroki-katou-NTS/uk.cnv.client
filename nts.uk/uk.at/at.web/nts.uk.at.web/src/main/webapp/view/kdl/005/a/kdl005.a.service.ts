module nts.uk.at.view.kdl005.a {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getHolidaySub: "at/request/employment/getHolidaySub"
		};
		
		export function getHolidaySub(param : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getHolidaySub, param);
        }
	}
}