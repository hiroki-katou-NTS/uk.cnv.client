module nts.uk.at.view.kdl020.test {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getSid: "at/request/dialog/annualholidays/getSid"
		};
		
		export function getSid(): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getSid);
        }
	}
}