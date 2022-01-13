module nts.uk.at.view.kdl005.test {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getSid: "at/request/employment/getSid"
		};
		
		export function getSid(): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.getSid);
        }
	}
}