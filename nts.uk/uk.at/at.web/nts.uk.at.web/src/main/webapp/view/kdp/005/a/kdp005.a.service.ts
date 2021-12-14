module nts.uk.at.view.kdp005.a {

	import ajax = nts.uk.request.ajax;

	export module service {
		let url = {
			startPage: 'at/record/stamp/ICCardStamp/get-iccard-stamp-setting',
			addCheckCard: 'at/record/stamp/ICCardStamp/checks',
			confirmUseOfStampInput: 'at/record/stamp/employment_system/confirm_use_of_stamp_input',
			loginAdminMode: 'ctx/sys/gateway/kdp/login/adminmode',
			loginEmployeeMode: 'ctx/sys/gateway/kdp/login/employeemode',
			getError: 'at/record/stamp/employment_system/get_omission_contents',
			getStampToSuppress: 'at/record/stamp/employment_system/get_stamp_to_suppress',
            getEmployeeIdByICCard: 'at/record/stamp/ICCardStamp/getEmployeeIdByICCard',
            authenticateOnlyStamped: 'at/record/stamp/ICCardStamp/authenticateOnlyStamped',
            getLogginSetting: 'ctx/sys/gateway/kdp/login/getLogginSetting'
		}

		export function startPage(): JQueryPromise<any> {
			return ajax("at", url.startPage);
		}

		export function addCheckCard(data): JQueryPromise<any> {
			return ajax("at", url.addCheckCard, data);
		}
        
        export function getEmployeeIdByICCard(data): JQueryPromise<any> {
            return ajax("at", url.getEmployeeIdByICCard, data);
        }

        export function authenticateOnlyStamped(data): JQueryPromise<any> {
            return ajax("at", url.authenticateOnlyStamped, data);
        }
        
		export function confirmUseOfStampInput(data): JQueryPromise<any> {
			return ajax("at", url.confirmUseOfStampInput, data);
		}

		export function login(isAdmin, data) {
			return ajax("at", isAdmin ? url.loginAdminMode : url.loginEmployeeMode, data);
		}

		export function getError(data): JQueryPromise<any> {
			return ajax("at", url.getError, data);
		}

		export function getStampToSuppress(): JQueryPromise<any> {
			return ajax("at", url.getStampToSuppress);
		}
        export function getLogginSetting(param): JQueryPromise<any> {
            return ajax("at", url.getLogginSetting, {contractCode: param});
        }
	}

}

