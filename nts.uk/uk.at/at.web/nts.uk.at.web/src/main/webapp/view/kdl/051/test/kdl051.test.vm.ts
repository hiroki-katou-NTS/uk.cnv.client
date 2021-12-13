module nts.uk.at.view.kdl051.test.viewmodel {
	export class ScreenModel {
		date: KnockoutObservable<any>;
		empList: KnockoutObservableArray<string> = ko.observableArray([]);
		//_____KCP005________
		listComponentOption: any = [];
		selectedCode: KnockoutObservable<string>;
		multiSelectedCode: KnockoutObservableArray<string>;
		isShowAlreadySet: KnockoutObservable<boolean>;
		alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
		isDialog: KnockoutObservable<boolean>;
		isShowNoSelectRow: KnockoutObservable<boolean>;
		isMultiSelect: KnockoutObservable<boolean>;
		isShowWorkPlaceName: KnockoutObservable<boolean>;
		isShowSelectAllButton: KnockoutObservable<boolean>;
		disableSelection: KnockoutObservable<boolean>;

		employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);

		constructor(data: any) {
			let self = this;

			self.date = ko.observable(new Date());
			self.empList = ko.observableArray([]);

			self.selectedCode = ko.observable('1');
			self.multiSelectedCode = ko.observableArray(['0', '1', '4']);
			self.isShowAlreadySet = ko.observable(false);
			self.alreadySettingList = ko.observableArray([
				{ code: '1', isAlreadySetting: true },
				{ code: '2', isAlreadySetting: true }
			]);
			self.isDialog = ko.observable(false);
			self.isShowNoSelectRow = ko.observable(false);
			self.isMultiSelect = ko.observable(false);
			self.isShowWorkPlaceName = ko.observable(false);
			self.isShowSelectAllButton = ko.observable(false);
			self.disableSelection = ko.observable(false);

			_.forEach(data, (a: any, ind) => {
				self.employeeList.push({ id: a, code: "00000000000" + (ind+1), name: a, workplaceName: 'HN' })
			});

			self.listComponentOption = {
				isShowAlreadySet: self.isShowAlreadySet(),
				isMultiSelect: true,
				listType: ListType.EMPLOYEE,
				employeeInputList: self.employeeList,
				selectType: SelectType.SELECT_BY_SELECTED_CODE,
				selectedCode: self.selectedCode,
				isDialog: self.isDialog(),
				isShowNoSelectRow: self.isShowNoSelectRow(),
				alreadySettingList: self.alreadySettingList,
				isShowWorkPlaceName: self.isShowWorkPlaceName(),
				isShowSelectAllButton: self.isShowSelectAllButton(),
				disableSelection: self.disableSelection(),
				maxRows : 15
			};

			$('#kcp005Com').ntsListComponent(self.listComponentOption);
		}

		public startPage(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			service.getSid().done((data: any) => {
				self.listComponentOption.employeeInputList = data;
			});
			return dfd.promise();
		}

		openDialog() {
			let self = this;

			let empIds: any = _.map(_.filter(self.employeeList(), (z: any) => {
				return self.listComponentOption.selectedCode().contains(z.code + "");
			}), (a: any) => a.name);

			nts.uk.ui.windows.setShared('KDL051A_PARAM', empIds);
			if(empIds.length == 0){
				nts.uk.ui.dialog.alertError({ messageId: "List Employee empty !!!" });
			}else if(empIds.length == 1){
				nts.uk.ui.windows.sub.modal("/view/kdl/051/a/index.xhtml",{width: 650, height: 530});
			}else{
				nts.uk.ui.windows.sub.modal("/view/kdl/051/a/index.xhtml",{width: 980, height: 570});
			}
			
		}
	}

	export class DataParam {
		employeeBasicInfo: Array<EmployeeBasicInfoDto>;
		baseDate: string;

		constructor(param: IDataParam) {
			var self = this;
			self.employeeBasicInfo = ko.observable(param.employeeBasicInfo);
			self.baseDate = ko.observable(param.baseDate);
		}
	}

	export interface IDataParam {
		employeeBasicInfo: Array<EmployeeBasicInfoDto>;
		baseDate: string;
	}

	export class EmployeeBasicInfoDto {
		personId: string;
		employeeId: string;
		businessName: string;
		gender: number;
		birthday: string;
		employeeCode: string;
		jobEntryDate: string;
		retirementDate: string;

		constructor(param: IEmployeeBasicInfoDto) {
			var self = this;
			self.personId = ko.observable(param.personId);
			self.employeeId = ko.observable(param.employeeId);
			self.businessName = ko.observable(param.businessName);
			self.gender = ko.observable(param.gender);
			self.birthday = ko.observable(param.birthday);
			self.employeeCode = ko.observable(param.employeeCode);
			self.jobEntryDate = ko.observable(param.jobEntryDate);
			self.retirementDate = ko.observable(param.retirementDate);
		}
	}

	export interface IEmployeeBasicInfoDto {
		personId: string;
		employeeId: string;
		businessName: string;
		gender: number;
		birthday: string;
		employeeCode: string;
		jobEntryDate: string;
		retirementDate: string;
	}

	export class EmployeeParam {
		employeeIds: Array<string>;
		baseDate: string;

		constructor(param: IEmployeeParam) {
			var self = this;
			self.employeeIds = ko.observable(param.employeeIds);
			self.baseDate = ko.observable(param.baseDate);
		}
	}

	export interface IEmployeeParam {
		employeeIds: Array<string>;
		baseDate: string;
	}

	export class ListType {
		static EMPLOYMENT = 1;
		static Classification = 2;
		static JOB_TITLE = 3;
		static EMPLOYEE = 4;
	}

	export interface UnitModel {
		id?: string;
		code: string;
		name?: string;
		workplaceName?: string;
		isAlreadySetting?: boolean;
		optionalColumn?: any;
	}

	export class SelectType {
		static SELECT_BY_SELECTED_CODE = 1;
		static SELECT_ALL = 2;
		static SELECT_FIRST_ITEM = 3;
		static NO_SELECT = 4;
	}

	export interface UnitAlreadySettingModel {
		code: string;
		isAlreadySetting: boolean;
	}
}