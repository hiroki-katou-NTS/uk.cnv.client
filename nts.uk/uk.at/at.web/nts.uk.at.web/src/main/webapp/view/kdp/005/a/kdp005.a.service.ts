module nts.uk.at.view.kdp005.a {

	import ajax = nts.uk.request.ajax;

	export module service {
		let url = {
			startPage: 'at/record/stamp/ICCardStamp/get-iccard-stamp-setting',
			addCheckCard: 'at/record/stamp/ICCardStamp/checks',
			confirmUseOfStampInput: 'at/record/stamp/employment_system/confirm_use_of_stamp_input',
			// loginAdminMode: 'ctx/sys/gateway/kdp/login/adminmode',
			loginAdminMode: 'ctx/sys/gateway/login/password' + location.search,
			loginEmployeeMode: 'ctx/sys/gateway/kdp/login/employeemode',
			getError: 'at/record/stamp/employment_system/get_omission_contents',
			getStampToSuppress: 'at/record/stamp/employment_system/get_stamp_to_suppress',
            getEmployeeIdByICCard: 'at/record/stamp/ICCardStamp/getEmployeeIdByICCard',

            getLogginSetting: 'ctx/sys/gateway/kdp/login/getLogginSetting',
			createDaily: 'at/record/stamp/craeteDaily',
			getEmployeeWorkByStamping: 'at/record/stamp/employee_work_by_stamping'

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
        
		export function confirmUseOfStampInput(data): JQueryPromise<any> {
			return ajax("at", url.confirmUseOfStampInput, data);
		}

		export function login(data) {
			return ajax("com", url.loginAdminMode, data);
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

		export function createDaily(data): JQueryPromise<any> {
			return ajax("at", url.createDaily, data);
		}

		export function getEmployeeWorkByStamping(param: any): JQueryPromise<any> {
			return ajax("at", url.getEmployeeWorkByStamping, param);
		}

	}

}

