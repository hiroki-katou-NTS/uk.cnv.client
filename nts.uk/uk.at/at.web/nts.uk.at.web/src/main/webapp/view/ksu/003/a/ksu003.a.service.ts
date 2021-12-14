//ScreenQueryGetInforOfInitStartup
module nts.uk.at.view.ksu003.a {
	export module service {
        /**
         *  Service paths
         */
		var servicePath: any = {
			getDataStartScreen: "screen/at/schedule/getinfo-initstart",
			getFixedWorkInformation: "screen/at/schedule/getfixedworkinfo",
			displayDataKsu003: "screen/at/schedule/displayDataKsu003",
			sortEmployee: "screen/at/schedule/sortEmployee",
			getEmpWorkFixedWorkInfo: "screen/at/schedule/getEmpWorkFixedWorkInfo",
			changeWorkType: "screen/at/schedule/changeWorkType",
			checkWorkType: "screen/at/schedule/checkWorkType",
            checkTimeIsIncorrect: "ctx/at/shared/workrule/workinghours/checkTimeIsIncorrect",
        regWorkSchedule: "screen/at/schedule/registerKSU003",
		};
		
		export function checkTimeIsIncorrect(command : any): JQueryPromise<any> {
            return nts.uk.request.ajax( "at", servicePath.checkTimeIsIncorrect, command);
        }

        /**
         * ①<<ScreenQuery>> 初期起動の情報取得
         */
		export function getDataStartScreen(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.getDataStartScreen, command);
		}

        /**
         * ①<<ScreenQuery>> 勤務固定情報を取得する
         */
		export function getFixedWorkInformation(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.getFixedWorkInformation, command);
		}

        /**
         * ①<<ScreenQuery>> 日付別勤務情報で表示する
         */
		export function displayDataKsu003(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.displayDataKsu003, command);
		}
		
		/**
         * ①<<ScreenQuery>> 社員を並び替える
         */
		export function sortEmployee(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.sortEmployee, command);
		}
		
		/**
         * ①<<ScreenQuery>> 社員勤務予定と勤務固定情報を取得する
         */
		export function getEmpWorkFixedWorkInfo(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.getEmpWorkFixedWorkInfo, command);
		}
		
		/**
         * ①<<ScreenQuery>> 勤務種類を変更する
         */
		export function changeWorkType(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.changeWorkType, command);
		}
		
		export function checkWorkType(command: any): JQueryPromise<any> {
			return nts.uk.request.ajax(servicePath.checkWorkType, command);
		}
		
		export function regWorkSchedule(command: any): JQueryPromise<any> {
        	return nts.uk.request.ajax(servicePath.regWorkSchedule, command);
   		}
	}
}