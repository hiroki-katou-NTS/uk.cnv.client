module nts.uk.at.view.kdw003.g.test {
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;
	import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
	import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
	// Import

	const Paths = {
        GET_AVAILABLE_WORK_SCHEDULE: "at/schedule/task/taskschedule/getAvailableEmpWorkSchedule"       
    };
	@bean()
	class Kdw003gTestViewModel  extends ko.ViewModel{
			currentScreen: any = null;
            date: KnockoutObservable<string>;
			
			//Declare kcp005 list properties
			listComponentOption: any;
			selectedCode: KnockoutObservable<string>;
			multiSelectedCode: KnockoutObservableArray<string>;
			isShowAlreadySet: KnockoutObservable<boolean>;
			isDialog: KnockoutObservable<boolean>;
			isShowNoSelectRow: KnockoutObservable<boolean>;
			isMultiSelect: KnockoutObservable<boolean>;
			isShowWorkPlaceName: KnockoutObservable<boolean>;
			isShowSelectAllButton: KnockoutObservable<boolean>;
			employeeList: KnockoutObservableArray<UnitModel>;

			// startDate for validate
			startDateValidate: KnockoutObservable<string>;
			alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;

			dataFromKsu003c: KnockoutObservable<string> = ko.observable('');

			//Declare employee filter component
			ccg001ComponentOption: any;
			showinfoSelectedEmployee: KnockoutObservable<boolean> = ko.observable(false);
			// Options
			baseDate: KnockoutObservable<Date>;
			selectedEmployee: KnockoutObservableArray<EmployeeSearchDto> = ko.observableArray([]);



			// startDateString: KnockoutObservable<string> = ko.observable('');
			// endDateString: KnockoutObservable<string> = ko.observable('');
			// dateValue: KnockoutObservable<any> = ko.observable({});
			constructor() {
				super();
				var self = this;
				self.baseDate = ko.observable(new Date());
                self.date = ko.observable(new Date().toString());
				self.selectedCode = ko.observable(null);
				self.multiSelectedCode = ko.observableArray([]);
				self.isShowAlreadySet = ko.observable(false);
				self.alreadySettingList = ko.observableArray([
					{ code: '1', isAlreadySetting: true },
					{ code: '2', isAlreadySetting: true }
				]);
				self.isDialog = ko.observable(true);
				self.isShowNoSelectRow = ko.observable(false);
				self.isMultiSelect = ko.observable(true);
				self.isShowWorkPlaceName = ko.observable(true);
				self.isShowSelectAllButton = ko.observable(true);

				this.employeeList = ko.observableArray<UnitModel>([]);

				let ccg001ComponentOption: any = {

					/** Common properties */
					systemType: 2, // ??????????????????
					showEmployeeSelection: false, // ???????????????
					showQuickSearchTab: true, // ??????????????????
					showAdvancedSearchTab: true, // ????????????
					showBaseDate: false, // ???????????????
					showClosure: true, // ?????????????????????
					showAllClosure: true, // ???????????????
					showPeriod: true, // ??????????????????
					periodFormatYM: false, // ??????????????????

					/** Required parameter */
					baseDate: moment().toISOString(), // ?????????
					//periodEndDate: self.dateValue().endDate,
					dateRangePickerValue: self.dateValue,
					inService: true, // ????????????
					leaveOfAbsence: true, // ????????????
					closed: true, // ????????????
					retirement: false, // ????????????

					/** Quick search tab options */
					showAllReferableEmployee: true, // ??????????????????????????????
					showOnlyMe: true, // ????????????
					showSameWorkplace: true, // ?????????????????????
					showSameWorkplaceAndChild: true, // ????????????????????????????????????

					/** Advanced search properties */
					showEmployment: false, // ????????????
					showWorkplace: true, // ????????????
					showClassification: true, // ????????????
					showJobTitle: true, // ????????????
					showWorktype: true, // ????????????
					isMutipleCheck: true, // ???????????????

					/** Return data */
					returnDataFromCcg001: function (data: Ccg001ReturnedData) {
						self.showinfoSelectedEmployee(true);
						self.selectedEmployee(data.listEmployee);

						//Convert list Object from server to view model list
						let items = _.map(data.listEmployee, item => {
							return {
								id: item.employeeId,
								code: item.employeeCode,
								name: item.employeeName,
								affiliationName: item.affiliationName,
								isAlreadySetting: true
							}
						});
						self.employeeList(items);

						//Fix bug 42, bug 43
						let selectList = _.map(data.listEmployee, item => {
							return item.employeeId;
						});
						self.multiSelectedCode(selectList);
					}
				}

				self.listComponentOption = {
					isShowAlreadySet: self.isShowAlreadySet(),
					isMultiSelect: true,
					listType: ListType.EMPLOYEE,
					employeeInputList: self.employeeList,
					selectType: SelectType.SELECT_ALL,
					selectedCode: self.selectedCode,
					isDialog: false,
					isShowNoSelectRow: self.isShowNoSelectRow(),
					alreadySettingList: self.alreadySettingList,
					isShowWorkPlaceName: self.isShowWorkPlaceName(),
					isShowSelectAllButton: false,
					maxRows: 10,
					isSelectAllAfterReload: true
				};

				$('#ccgcomponent').ntsGroupComponent(ccg001ComponentOption);
				$('#component-items-list').ntsListComponent(self.listComponentOption);

			}

		// mounted(){
		// 	$('#daterangepicker').focus();
		// }
		openDialog(): void {
			let self = this;
			let dataShare: any = {}, lisId: Array<any> = [], temp: Array<any> =[];

			_.each(self.selectedCode(), (code) => {
				_.map(self.employeeList(), function (item) {
					if (item.code === code) {
						temp.push(item);
					}
				});
			});
			var listEmpSort = _.sortBy(temp, function (x) { return x.id; });
			_.each(listEmpSort, function (x) {
				lisId.push(x.id);
			});
			if(_.isEmpty(lisId)) {
				self.$dialog.alert("Please choose employee !");
				return;
			}

			dataShare.baseDate = moment(self.date()).format('YYYY/MM/DD');
			dataShare.employeeIds = lisId;

			setShared('dataShareKdw003g', dataShare);
			self.currentScreen = nts.uk.ui.windows.sub.modal("/view/kdw/003/g/index.xhtml").onClosed(() => {
				// self.dataFromKsu003c(getShared('dataShareFromKsu003c'));
			});
		}
	}
		export interface UnitAlreadySettingModel {
			code: string;
			isAlreadySetting: boolean;
		}
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
		export class UnitModel {
			id: string;
			code: string;
			name: string;
			affiliationName: string;
			isAlreadySetting: boolean;
			constructor(x: EmployeeSearchDto) {
				let self = this;
				if (x) {
					self.code = x.employeeCode;
					self.name = x.employeeName;
					self.affiliationName = x.workplaceName;
					self.isAlreadySetting = false;
				} else {
					self.code = "";
					self.name = "";
					self.affiliationName = "";
					self.isAlreadySetting = false;
				}
			}
		}
	}
