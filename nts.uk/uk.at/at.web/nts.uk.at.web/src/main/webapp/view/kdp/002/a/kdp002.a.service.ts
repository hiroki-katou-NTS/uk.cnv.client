module nts.uk.at.view.kdp002.a {

	import ajax = nts.uk.request.ajax;

	export module service {
		let url = {
			startPage: 'at/record/stamp/management/personal/startPage',
			getStampData: 'at/record/stamp/management/personal/stamp/getStampData',
			getTimeCard: 'at/record/stamp/management/personal/stamp/getTimeCard',
			stampInput: 'at/record/stamp/management/personal/stamp/input',
			getError: 'at/record/stamp/management/personal/getDailyError',
			workManagementMultiple: 'at/record/stamp/work_management_multiple',
			createDaily: 'at/record/stamp/craeteDaily',
			getHighlightSetting: 'at/record/stamp/management/personal/stamp/getHighlightSetting',
			getSettingStampCommon: 'at/record/stamp/settings_stamp_common',
			getEmployeeWorkByStamping: 'at/record/stamp/employee_work_by_stamping'

		}

		export function startPage(): JQueryPromise<any> {
			return ajax("at", url.startPage);
		}

		export function getStampData(data): JQueryPromise<any> {
			return ajax("at", url.getStampData, data);
		}

		export function getTimeCardData(data): JQueryPromise<any> {
			return ajax("at", url.getTimeCard, data);
		}


		export function stampInput(data): JQueryPromise<any> {
			return ajax("at", url.stampInput, data);
		}

		export function getError(data): JQueryPromise<any> {
			return ajax("at", url.getError + "/" + data.pageNo + "/" + data.buttonDisNo + "/" + 3);
		}

		export function getWorkManagementMultiple(): JQueryPromise<any> {
			return ajax("at", url.workManagementMultiple);
		}

		export function getHighlightSetting(): JQueryPromise<any> {
			return ajax("at", url.getHighlightSetting);
		}

		export function getSettingStampCommon(): JQueryPromise<any> {
			return ajax("at", url.getSettingStampCommon);
		}

		export function getEmployeeWorkByStamping(param: any): JQueryPromise<any> {
			return ajax("at", url.getEmployeeWorkByStamping, param);
		}
	}

}

