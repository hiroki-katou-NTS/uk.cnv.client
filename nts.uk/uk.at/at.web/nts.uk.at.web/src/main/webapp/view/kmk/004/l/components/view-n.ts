/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmk004.l {
	import IParam = nts.uk.at.view.kmk004.p.IParam;
	import SIDEBAR_TYPE = nts.uk.at.view.kmk004.p.SIDEBAR_TYPE;
	import IYear = nts.uk.at.view.kmk004.components.transform.IYear;

	const template = `
		<div class="sidebar-content-header">
		<div class="title" data-bind="i18n: 'Com_Employment'"></div>
		<a class="goback" data-bind="ntsLinkButton: { jump: '/view/kmk/004/a/index.xhtml' },i18n: 'KMK004_224'"></a>
		<button class="proceed" data-bind="i18n: 'KMK004_225'"></button>
		<button data-bind="visible: true, i18n: 'KMK004_226'"></button>
		<button class="danger" data-bind="i18n: 'KMK004_227'"></button>
	</div>
	
	<div class="view-n">
	<table>
		<tr>
			<td id="view-n-left-side">
				<div id="empt-list-setting"></div>
			</td>
			
			<td id="right-side">
				<div class="view-l-common">
					<div class="header-l">
						<div id="title", data-bind="i18n: 'KMK004_307'"></div>
						<hr></hr>
						<div class="selected-item">
							<label data-bind= "text: currentItemName"></label>
						</div>
						
						<div class="header_title">
							<div data-bind="ntsFormLabel: {inline: true}, i18n: 'KMK004_229'"></div>
							<button data-bind="i18n: btn_text, click: openViewP"></button>
						</div>
						<div class="header_content">
							<div data-bind="component: {
								name: 'view-l-basic-setting',
								params: paramL
							}"></div>
						</div>
						<div data-bind="ntsFormLabel: {inline: true}, i18n: 'KMK004_232'"></div>
					</div>
					<div class="content">
							<div class="div_row"> 
								<div class= "box-year" data-bind="component: {
									name: 'box-year',
									params:{ 
										selectedYear: selectedYear,
										type: type,
										selectedId: selectedId
									}
								}"></div>
								
								<div id= class= "time-work" data-bind="component: {
									name: 'time-work',
									params:{
										selectedYear: selectedYear,
										years: years
									}
								}"></div>
							</div>
					</div>
				</div>
			</td>
		</tr>
	</table>
	`;

	export class ListType {
		static EMPLOYMENT = 1;
		static Classification = 2;
		static JOB_TITLE = 3;
		static EMPLOYEE = 4;
	}
	
	export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

	interface UnitModel {
		code: string;
		name?: string;
		workplaceName?: string;
		isAlreadySetting?: boolean;
	}

	interface UnitAlreadySettingModel {
		id: string;
		isAlreadySetting: boolean;
	}

	@component({
		name: 'view-n',
		template
	})

	export class ViewNComponent extends ko.ViewModel {
		listComponentOption: any;
		selectedCode: KnockoutObservable<string>;
		selectedId: KnockoutObservable<string> = ko.observable(''); ;
		multiSelectedCode: KnockoutObservableArray<string>;
		isShowAlreadySet: KnockoutObservable<boolean>;
		alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel> = ko.observableArray([]);
		isDialog: KnockoutObservable<boolean>;
		isShowNoSelectRow: KnockoutObservable<boolean>;
		isMultiSelect: KnockoutObservable<boolean>;
		employeeList: KnockoutObservableArray<UnitModel>;
		isDisplayClosureSelection: KnockoutObservable<boolean>;
		isDisplayFullClosureOption: KnockoutObservable<boolean>;
		closureSelectionType: KnockoutObservable<number>;
		selectClosureTypeList: KnockoutObservableArray<any>;
		currentItemName: KnockoutObservable<string>;

		public selectedYear: KnockoutObservable<number | null> = ko.observable(null);
		public years: KnockoutObservableArray<IYear> = ko.observableArray([]);
		public checkEmployee: KnockoutObservable<boolean> = ko.observable(false);
		public existYear: KnockoutObservable<boolean> = ko.observable(false);
		public type: SIDEBAR_TYPE = 'Com_Employment';
		paramL: IParam;
		isLoadData: KnockoutObservable<boolean> = ko.observable(false);
		//isLoadInitData: KnockoutObservable<boolean>;
		btn_text: KnockoutObservable<string> = ko.observable('');

		constructor(private params: IParam) {
			super();
			const vm = this;
			
		/*	vm.isLoadInitData = vm.params.isLoadInitData; 
			vm.isLoadInitData.subscribe((value: boolean) => {
				if (value) {
					vm.reloadInitData();
					vm.isLoadInitData(false);
				}
			});*/
		}

		created() {
			let vm = this;
			vm.selectedCode = ko.observable('1');
			vm.multiSelectedCode = ko.observableArray(['0', '1', '4']);
			vm.isShowAlreadySet = ko.observable(true);
			vm.isDialog = ko.observable(false);
			vm.isShowNoSelectRow = ko.observable(false);
			vm.isMultiSelect = ko.observable(false);
			vm.isDisplayClosureSelection = ko.observable(true);
			vm.isDisplayFullClosureOption = ko.observable(false);
			vm.closureSelectionType = ko.observable(1);
			vm.selectClosureTypeList = ko.observableArray([
				{ code: 1, name: 'Select Full Closure option' },
				{ code: 2, name: 'Select by selected closure code' },
				{ code: 3, name: 'Nothing (Select first option)' },
			]);

			vm.listComponentOption = {
				isShowAlreadySet: vm.isShowAlreadySet(),
				isMultiSelect: vm.isMultiSelect(),
				listType: ListType.EMPLOYMENT,
				selectType: SelectType.SELECT_FIRST_ITEM,
				selectedCode: vm.selectedCode,
				isDialog: vm.isDialog(),
				isShowNoSelectRow: vm.isShowNoSelectRow(),
				alreadySettingList: vm.alreadySettingList,
				maxRows: 12,
				isDisplayClosureSelection: vm.isDisplayClosureSelection(),
			};

			vm.reloadInitData();

			vm.employeeList = ko.observableArray<UnitModel>([]);
			vm.currentItemName = ko.observable('');
			vm.paramL = {isLoadData: vm.isLoadData, sidebarType: "Com_Employment", wkpId: ko.observable(''), empCode: ko.observable(''), empId: ko.observable(''), titleName: '', deforLaborTimeComDto: null, settingDto: null }
			vm.selectedYear
				.subscribe(() => {
					if (vm.selectedYear != null) {
						vm.existYear(true);
					}
				});

			vm.selectedCode.subscribe((newValue) => {
				let selectedItem = _.find(vm.employeeList(), emp => {
					return emp.code == newValue;
				});
				vm.currentItemName(selectedItem ? selectedItem.name : '');
				vm.paramL.empCode(newValue);
				vm.paramL.titleName = vm.currentItemName();
				vm.selectedId(newValue);
				vm.btn_text(
					vm.alreadySettingList().filter(i => newValue == i.id).length == 0 ? 'KMK004_340' : 'KMK004_341');
			});

			$('#empt-list-setting').ntsListComponent(vm.listComponentOption).done(() => {
				vm.employeeList($('#empt-list-setting').getDataList());
				vm.selectedCode(vm.employeeList()[0].code);
			});
		}

		mounted() {
		}

		openViewP() {
			let vm = this;
			vm.$window.modal('at', '/view/kmk/004/p/index.xhtml', ko.toJS(vm.paramL)).then(() => {
				vm.isLoadData(true);
				//vm.isLoadInitData(true);
				
			});
		}
		
		reloadInitData() {
			let vm = this;
			vm.$ajax(KMK004_API.EMP_INIT_SCREEN)
				.done((data: any) => {
					let settings: UnitAlreadySettingModel[] = [];
					_.forEach(data.employmentCds, ((value) => {
						let s: UnitAlreadySettingModel = { id: value.employmentCode, isAlreadySetting: true };
						settings.push(s);
					}));
					vm.alreadySettingList(settings);
				})
		}
	}
}