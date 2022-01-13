module cmm045.a.service {
     var paths = {
     	 getApplicationList: "at/request/application/applist/getapplist",
         // getApplicationList: "at/request/application/applist/getapplisttest",

         getApplicationDisplayAtr: "at/request/application/applist/get/appdisplayatr",
         approvalListApp: "at/request/application/applist/approval",
         getHdSetInfo: "at/request/vacation/setting/hdapp",
         reflectListApp: "at/request/application/applist/reflect-list",
         writeLog: "at/request/application/write-log",
		 getAppNameInAppList: "at/request/application/screen/applist/getAppNameInAppList",
		findByPeriod: "at/request/application/applist/findByPeriod",
        findByEmpIDLst: "at/request/application/applist/findByEmpIDLst",
        print: "at/request/application/applist/print",
		approveCheck: "at/request/application/applist/approve",
		approverAfterConfirm: "at/request/application/applist/approverAfterConfirm",
		getMenu: "sys/portal/webmenu/program"
    }

    /**
    * get List Application
    */
    export function getApplicationList(param: any): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.getApplicationList,param);
    }
    /**
     * get list Application display atr
     */
    export function getApplicationDisplayAtr(): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.getApplicationDisplayAtr);
    }

    /**
     * -PhuongDV- for test
     */
    export function getScheduleList(): JQueryPromise<Array<any>>{
        return nts.uk.schedule.ajax("at", paths.getApplicationDisplayAtr);
    }
    /**
     * approval list Application
     */
    export function approvalListApp(data: any): JQueryPromise<any>{
        return nts.uk.request.ajax("at", paths.approvalListApp, data);
    }
    export function reflectListApp(lstID: any): JQueryPromise<any>{
        return nts.uk.request.ajax("at", paths.reflectListApp, lstID);
    }
    //write log
    export function writeLog(paramLog: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.writeLog, paramLog);
    }

	export function getAppNameInAppList(): JQueryPromise<any> {
		return nts.uk.request.ajax("at", paths.getAppNameInAppList).then(((data) => {
			return data;
		}), ((fail) => {
			return [];
		}));
	}

	export function findByPeriod(param: any): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.findByPeriod, param);
    }

	export function findByEmpIDLst(param: any): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.findByEmpIDLst, param);
    }

    export function print(param: any): JQueryPromise<any> {
        return nts.uk.request.exportFile("at", paths.print, param)
    }

	export function approveCheck(param: any): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.approveCheck, param);
    }

	export function approverAfterConfirm(param: any): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("at", paths.approverAfterConfirm, param);
    }
    export function getMenu(): JQueryPromise<Array<any>>{
        return nts.uk.request.ajax("com", paths.getMenu);
    }
}