module nts.uk.at.view.ksu003.c.test {
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;
	import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
	import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
	// Import

	const Paths = {
        GET_AVAILABLE_WORK_SCHEDULE: "at/schedule/task/taskschedule/getAvailableEmpWorkSchedule"       
    };
	@bean()
	class Ksu003cTestViewModel  extends ko.ViewModel{
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
					systemType: 2, // システム区分
					showEmployeeSelection: false, // 検索タイプ
					showQuickSearchTab: true, // クイック検索
					showAdvancedSearchTab: true, // 詳細検索
					showBaseDate: false, // 基準日利用
					showClosure: true, // 就業締め日利用
					showAllClosure: true, // 全締め表示
					showPeriod: true, // 対象期間利用
					periodFormatYM: false, // 対象期間精度

					/** Required parameter */
					baseDate: moment().toISOString(), // 基準日
					//periodEndDate: self.dateValue().endDate,
					dateRangePickerValue: self.dateValue,
					inService: true, // 在職区分
					leaveOfAbsence: true, // 休職区分
					closed: true, // 休業区分
					retirement: false, // 退職区分

					/** Quick search tab options */
					showAllReferableEmployee: true, // 参照可能な社員すべて
					showOnlyMe: true, // 自分だけ
					showSameWorkplace: true, // 同じ職場の社員
					showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員

					/** Advanced search properties */
					showEmployment: false, // 雇用条件
					showWorkplace: true, // 職場条件
					showClassification: true, // 分類条件
					showJobTitle: true, // 職位条件
					showWorktype: true, // 勤種条件
					isMutipleCheck: true, // 選択モード

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
			openDialog(): void {
				let self = this;
				let request: any = {}, request1: any = {};
				request.date = moment(self.date()).format('YYYY/MM/DD');
				let lisId: Array<string> = [], listName: Array<string> = [], listCode: Array<string> = [], lisIdAvai: Array<string> = [],; 
				let temp: Array<any> = [], temp1: Array<any> = [];

			
				_.forEach(self.selectedCode(), code => {
					_.map(self.employeeList(), item =>{
						if(item.code === code){
							temp.push(item);										
						}
					});				
				})
				let listEmpSort = _.sortBy(temp, x => { return x.id });
				_.each(listEmpSort, x =>{
					lisId.push(x.id);					
				})
				request1.empIds = lisId;
				request1.ymd = moment(self.date()).format('YYYY/MM/DD');
				self.$blockui("invisible");
				self.$ajax(Paths.GET_AVAILABLE_WORK_SCHEDULE, request1).done((data: Array<any>) => {
					if (!_.isNull(data) && !_.isEmpty(data)) {
						_.each(data, id => {
							_.each(listEmpSort, x =>{
								if(x.id == id){
									lisIdAvai.push(x.id);
									listCode.push(x.code);
									listName.push(x.name);
								}								
							})
						
						});
					
						request.employeeIds = lisIdAvai;						
						request.employeeCodes = listCode;
						request.employeeNames = listName;
						setShared('dataShareKsu003c', request);
						self.currentScreen = nts.uk.ui.windows.sub.modal("/view/ksu/003/c/index.xhtml").onClosed(() => {
							self.dataFromKsu003c(getShared('dataShareFromKsu003c'));
						});
					} else {
						self.$dialog.info("Have no work shedule at " +  moment(self.date()).format('YYYY/MM/DD'));
					}
				}).always(() => {
					self.$blockui("hide");
				});
			}
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();

				dfd.resolve();
				return dfd.promise();
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
