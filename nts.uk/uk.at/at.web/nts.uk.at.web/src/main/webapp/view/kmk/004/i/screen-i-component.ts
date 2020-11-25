/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />


const template = `
					<div class="sidebar-content-header">
								<div
									data-bind="component: {
								name: 'sidebar-button',
								params: {isShowCopyButton: true , header:header }
							}"></div>
					</div>
					<div style="padding: 10px; display: flex;">
					
						<div style="display:inline-block"> 
							<div id="empt-list-setting"></div>
						</div>
						<div style="display:inline-block">
							<label id="flex-title" data-bind="i18n:'KMK004_268'"></label>
							<hr/>
							<label id="selected-work-place" data-bind="i18n:employmentName"></label>
							<div style="margin-top: 20px;" data-bind="component: {
								name: 'basic-settings-company',
								params: {
											screenData:screenData,
											screenMode:header
										}
								}">
						</div>
						<div data-bind="component: {
								name: 'monthly-working-hours',
								params: {
											screenData:screenData,
											isShowCheckbox:false
										}
								}">
						</div>
						</div>
					</div>
	`;
const COMPONENT_NAME = 'screen-i-component';

const API_URL = {
	START_PAGE: 'screen/at/kmk004/i/start-page'
};

@component({
	name: COMPONENT_NAME,
	template
})

class ScreenIComponent extends ko.ViewModel {

	screenData: KnockoutObservable<FlexScreenData> = ko.observable(new FlexScreenData());

	header = '';

	selectedCode: KnockoutObservable<string> = ko.observable();

	employmentName: KnockoutObservable<string> = ko.observable('');

	alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);

	created(params: any) {
		let vm = this;

		vm.header = params.header;
		vm.initEmploymentList();
		/*	vm.$blockui('invisible')
				.then(() => vm.$ajax(API_URL.START_PAGE))
				.then((data: IScreenData) => {
					vm.screenData(new FlexScreenData(data));
				})
				.then(() => vm.$blockui('clear'));*/

	}

	initEmploymentList() {
		const vm = this,
			ListType = {
				EMPLOYMENT: 1, // 雇用
				Classification: 2, // 分類
				JOB_TITLE: 3, // 職位
				EMPLOYEE: 4 // 職場
			},
			SelectType = {
				SELECT_BY_SELECTED_CODE: 1,
				SELECT_ALL: 2,
				SELECT_FIRST_ITEM: 3,
				NO_SELECT: 4
			},
			listComponentOption = {
				isShowAlreadySet: true,
				isMultiSelect: false,
				listType: ListType.EMPLOYMENT,
				selectType: SelectType.SELECT_FIRST_ITEM,
				selectedCode: vm.selectedCode,
				isDialog: true,
				isShowNoSelectRow: false,
				alreadySettingList: vm.alreadySettingList,
				maxRows: 12
			};
		vm.$blockui('grayout');
		$('#empt-list-setting').ntsListComponent(listComponentOption).done(() => {

			vm.selectedCode.subscribe((value) => {
				let datas: Array<EmploymentUnitModel> = $('#empt-list-setting').getDataList(),

					selectedItem: EmploymentUnitModel = _.find(datas, ['code', value]);

				vm.employmentName(selectedItem ? selectedItem.name : '');
			});
			vm.$blockui("hide");
			vm.selectedCode.valueHasMutated();
		});
	}

}
class EmploymentUnitModel {
	code: string;
	name?: string;
	isAlreadySetting?: boolean;
}

