module nts.uk.at.view.kdl005.a.viewmodel {
	export class ScreenModel {
		date: KnockoutObservable<any>;
		empList: KnockoutObservableArray<string> = ko.observableArray([]);
		dataHoliday: KnockoutObservable<any> = ko.observable();
		dataEmpImport : any = [];

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
		paramData: any = nts.uk.ui.windows.getShared('KDL005_DATA');

		// search
		items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

		// area left
		employeeCodeName: KnockoutObservable<string> = ko.observable('');

		//
		columns: KnockoutObservableArray<any>;
		currentCode: KnockoutObservable<any>;
		currentCodeList: KnockoutObservableArray<any>;
		count: number = 100;
		switchOptions: KnockoutObservableArray<any>;
		holidayData: KnockoutObservableArray<HolidayInfo> = ko.observableArray([]);
		holidayDataOld: KnockoutObservableArray<HolidayInfo> = ko.observableArray([]);

		//
		itemList: KnockoutObservableArray<ItemModel>;
		selectedCodeCbb: KnockoutObservable<string>;
		isEnable: KnockoutObservable<boolean>;
		isEditable: KnockoutObservable<boolean>;
		checkBindData: boolean = true;

		checkSolid: number = 0;

		//
		currentRemainNumber: KnockoutObservable<string> = ko.observable('');
		expiredWithinMonth: KnockoutObservable<string> = ko.observable('');
		dayCloseDeadline: KnockoutObservable<string> = ko.observable('');

		searchText: KnockoutObservable<string> = ko.observable('');

		constructor() {
			let self = this;
			self.date = ko.observable(new Date());
			self.empList = ko.observableArray([]);

			//_____KCP005________
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

			//
			self.columns = ko.observableArray([
				{ headerText: '', key: 'occurrenceDateStatus', width: 200,hidden: true } ,
				{ headerText: nts.uk.resource.getText('KDL005_61'), key: 'accrualDate', width: 205 ,formatter: function (accrualDate : any, record : any) {
                       return "<div style='margin-left: 5px;display: flex;'><div style='width: 20px;' >"+record.occurrenceDateStatus.toString()+"</div> <div style='width: 155px;float:right;'> " + accrualDate + " </div></div>";   
                } },
				{ headerText: nts.uk.resource.getText('KDL005_64'), key: 'digestionStatus', width: 115 },
				{ headerText: nts.uk.resource.getText('KDL005_53'), key: 'deadline', width: 160 },
				{ headerText: '', key: 'digestionDateStatus', width: 200,hidden: true } ,
				{ headerText: nts.uk.resource.getText('KDL005_54'), key: 'digestionDate', width: 215 ,formatter: function (digestionDate : any, record : any) {
					let htmlStatus = record.digestionDateStatus.toString().length == 1 ? "<div style='margin-left: 10px;display: flex;'><div style='width: 25px;' >" : "<div style='margin-left: 5px;display: flex;'><div style='width: 31px;' >";
                    if (record.digestionDateStatus.toString().length == 0) {
						htmlStatus = "<div style='margin-left: 15px;display: flex;'><div style='width: 20px;' >";
					}
					return htmlStatus +record.digestionDateStatus.toString()+"</div> <div style='width: 155px;float:right;'> " + digestionDate + " </div></div>";   
                } }
			]);

			self.switchOptions = ko.observableArray([
				{ code: "1", name: '四捨五入' },
				{ code: "2", name: '切り上げ' },
				{ code: "3", name: '切り捨て' }
			]);
			self.currentCode = ko.observable(0);
			self.currentCodeList = ko.observableArray([]);

			if (self.paramData.length > 1) {
				$("#area-right").show();
			} else {
				$("#area-right").hide();
			}

			//
			self.listComponentOption = {
				isShowAlreadySet: self.isShowAlreadySet(),
				isMultiSelect: false,
				listType: ListType.EMPLOYEE,
				employeeInputList: self.employeeList,
				selectType: SelectType.SELECT_FIRST_ITEM,
				selectedCode: self.selectedCode,
				isDialog: self.isDialog(),
				isShowNoSelectRow: self.isShowNoSelectRow(),
				alreadySettingList: self.alreadySettingList,
				isShowWorkPlaceName: self.isShowWorkPlaceName(),
				isShowSelectAllButton: self.isShowSelectAllButton(),
				disableSelection: self.disableSelection(),
				maxRows: 15.51
			};

			$('#kcp005component').ntsListComponent(self.listComponentOption);

			self.listComponentOption.selectedCode.subscribe((value: any) => {
				if (value == null) return;
				self.checkSolid = 0;
				let name: any = _.filter(self.employeeList(), (x: any) => {
					return _.isEqual(x.code, value);
				});
				self.employeeCodeName(name[0].code + " " + name[0].name);
				self.bindDataToGrid(value).done(() => {
					self.selectedCodeCbb("1");
				});
			})

			//
			self.itemList = ko.observableArray([
				new ItemModel(1, nts.uk.resource.getText('KDL005_63')),
				new ItemModel(2, nts.uk.resource.getText('KDL005_48'))
			]);

			self.selectedCodeCbb = ko.observable('1');
			self.isEnable = ko.observable(true);
			self.isEditable = ko.observable(true);

			self.selectedCodeCbb.subscribe((value: any) => {
				self.holidayData([]);
				if (value == 1) {
					_.each(self.holidayDataOld(), (x: any) => {
						self.holidayData.push(x);
					});
					console.log(self.holidayData());
				} else {
					let data = (_.filter(self.dataHoliday().remainNumConfirmDto.detailRemainingNumbers, (x: any) => {
						return _.includes(x.digestionStatus, "残り");
					}));
					if (data.length > 0) {
						_.forEach(data, (z: any, index: number) => {
							self.bindDataToText(z, index);
						});
					}
				}

				if (self.checkSolid != 0) {
					$("#single-list > tbody > tr:nth-child(" + self.checkSolid + ") > td").css("border-bottom", "1px #CCC solid");
				}
			})

			$("#search-btn").on({
				"click": function() {
					console.log("");
				}
			})

		}

		startPage(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			nts.uk.ui.block.grayout();
			let param = {
				employeeIds: self.paramData,
				baseDate: ""
			};
			service.getHolidaySub(param).done((data: any) => {
				self.dataHoliday(data);
				self.dataEmpImport = data.empImport;
				if (data.remainNumConfirmDto != null) {
					self.currentRemainNumber(data.remainNumConfirmDto.currentRemainNumber);
					self.expiredWithinMonth(data.remainNumConfirmDto.expiredWithinMonth);
					self.dayCloseDeadline(data.remainNumConfirmDto.dayCloseDeadline);
				}

				_.forEach(data.empImport, (a: any, ind) => {
					self.employeeList.push({ id: ind, code: a.employeeCode, name: a.employeeName, workplaceName: 'HN' })
				});
				self.listComponentOption.selectedCode(self.employeeList()[0].code);
				$(".search-btn").hide();
				if (self.checkSolid != 0) {
					$("#single-list > tbody > tr:nth-child(" + self.checkSolid + ") > td").css("border-bottom", "1px #CCC solid");
				}
			});
			dfd.resolve();
			return dfd.promise();
		}

		bindDataToGrid(value: any): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			if (_.isNil(self.dataHoliday())) return;
			self.holidayData([]);
			if (self.dataHoliday().remainNumConfirmDto == null || (self.dataHoliday().remainNumConfirmDto != null && !_.includes(self.dataHoliday().remainNumConfirmDto.employeeId.slice(-12), value))) {
				let sid = _.filter(self.dataEmpImport, (x : any) => {
					return _.isEqual(x.employeeCode, value);
				});
				
				let empIDs : any = [];
				
				if (sid.length > 1) {
					_.forEach(sid, (y : any) => {
						let checkID = _.filter(self.paramData, (x : any) => {
							return _.isEqual(y.employeeId, x);
						})
						
						if (checkID.length > 0){
							empIDs = [checkID[0]];
							return;
						}
					})
				} else {
					empIDs = [sid[0].employeeId]
				}
				
				
				let param = {
					employeeIds: empIDs,
					baseDate: ""
				};
				
				service.getHolidaySub(param).done((data: any) => {
					self.dataHoliday(data);

					if (data.remainNumConfirmDto != null) {
						self.currentRemainNumber(data.remainNumConfirmDto.currentRemainNumber);
						self.expiredWithinMonth(data.remainNumConfirmDto.expiredWithinMonth);
						self.dayCloseDeadline(data.remainNumConfirmDto.dayCloseDeadline);

						_.forEach(data.remainNumConfirmDto.detailRemainingNumbers, (z: any, index: number) => {
							self.bindDataToText(z, index);
						});
						self.holidayDataOld({ ...self.holidayData() });
						dfd.resolve();
					}
					self.showHideItem(data);
					$('#cancel-btn').focus();
					
					let id = _.filter($("div > div > div > div"), (x: any) => {
							return _.includes(x.id, "container") && !_.includes(x.id, "single-list");
					})
					
					if (id.length > 0){
						if (id[0].id != "")
						$("#" + id[0].id).attr('tabindex', -1);
					}
					
					
					if (self.checkSolid != 0) {
						$("#single-list > tbody > tr:nth-child(" + self.checkSolid + ") > td").css("border-bottom", "1px #CCC solid");
					}
				});
			} else {
				_.forEach(self.dataHoliday().remainNumConfirmDto.detailRemainingNumbers, (z: any, index: number) => {
					self.bindDataToText(z, index);
				});
				self.showHideItem(self.dataHoliday());
				$('#cancel-btn').focus();
				self.holidayDataOld({ ...self.holidayData() });
				dfd.resolve();
			}

			return dfd.promise();
		}

		bindDataToText(z: any, index: number) {
			let self = this, textA3_11_12_13 = "", text_A3_31_32 = "", text_A4_41_42_43 = "",
				text45 = "<span style='color:#FF2D2D;'>" + z.dueDateStatus + "</span>";

			textA3_11_12_13 = z.accrualDate + z.numberOccurrences;
			
			if (z.dueDateStatus.length > 0){
				text_A3_31_32 = "<span>" + text45 + " " + z.deadline; + "</span>";
			} else {
				text_A3_31_32 = "<span style='margin-left:15px;'>" + text45 + " " + z.deadline; + "</span>";
			}

			if (z.listDigestion.length > 0){
				_.forEach(z.listDigestion, (a : any, index : any) => {
					text_A4_41_42_43 = a.digestionDate + a.digestionCount
					if (index == 0) {
						self.holidayData.push(new HolidayInfo(z.occurrenceDateStatus, textA3_11_12_13, z.digestionStatus, text_A3_31_32, a.digestionDateStatus, text_A4_41_42_43));
					} else {
						self.holidayData.push(new HolidayInfo("", "", "", "",a.digestionDateStatus, text_A4_41_42_43));
					}
					
				})
			} else {
				self.holidayData.push(new HolidayInfo(z.occurrenceDateStatus, textA3_11_12_13, z.digestionStatus, text_A3_31_32,"", text_A4_41_42_43));
			}
			
			if ((_.includes(z.occurrenceDateStatus, nts.uk.resource.getText('KDL005_40'))
				|| _.includes(z.digestionDateStatus, nts.uk.resource.getText('KDL005_40')))) {
				if (self.checkSolid == 0) {
					self.checkSolid = index;
				}
			}
		}

		showHideItem(data: any) {
			let remainNumConfirmDto = data.remainNumConfirmDto;

			// ※１ && ※4
			if (remainNumConfirmDto != null && remainNumConfirmDto.management == 1 &&
				remainNumConfirmDto.dayCloseDeadline.length > 0) {
				$("#A2_10").show();
				$("#A2_21").show();
			} else {
				$("#A2_10").hide();
				$("#A2_21").hide();
			}

			// ※2
			if (remainNumConfirmDto != null && remainNumConfirmDto.management == 1) {
				$("#area-info").show();
				$("#area-non-info").hide();
			} else {
				$("#area-info").hide();
				$("#area-non-info").show();
			}

			// ※3
			if (remainNumConfirmDto != null && remainNumConfirmDto.unit == 0) {
				$("#A6_1").hide();
				$("#A6_2").hide();
				$("#A6_3").hide();
				$("#A6_4").hide();
				$("#A2_5").css("margin-left","30px");
				$("#A2_23").css("margin-right","57px");
				$("#A2_21").css("margin-right","42px");
			} else {
				$("#A6_1").show();
				$("#A6_2").show();
				$("#A6_3").show();
				$("A6_4").show();
				$("#A2_5").css("margin-left","10px");
				
			}
			nts.uk.ui.block.clear();
		}

		findData(data: any) {
			let self = this;

			let text = $("input.ntsSearchBox.nts-editor.ntsSearchBox_Component").val()
			if (text == "") {
				nts.uk.ui.dialog.info({ messageId: "MsgB_24" });
			} else {
				let lstFil = _.filter(self.employeeList(), (z: any) => {
					return _.includes(z.code, text);
				})

				if (lstFil.length > 0) {
					let index = _.findIndex(lstFil, (z: any) => _.isEqual(z.code, self.listComponentOption.selectedCode()));
					if (index == -1 || index == lstFil.length - 1) {
						self.listComponentOption.selectedCode(lstFil[0].code);
					} else {
						self.listComponentOption.selectedCode(lstFil[index + 1].code);
					}
					let scroll: any = _.findIndex(self.employeeList(), (z: any) => _.isEqual(z.code, self.listComponentOption.selectedCode())),
						id = _.filter($("table > tbody > tr > td > div"), (x: any) => {
							return _.includes(x.id, "_scrollContainer") && !_.includes(x.id, "single-list");
						})
					$("#" + id[0].id).scrollTop(scroll * 24);
				} else {
					nts.uk.ui.dialog.info({ messageId: "MsgB_25" });
				}
			}
		}

		cancel() {
			nts.uk.ui.windows.close();
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

	/** 残数確認ダイアログDTO */
	export class RemainNumberConfirmDto {
		expiredWithinMonth: KnockoutObservable<string>; // 1ヶ月以内期限切れ数 - A2_9
		unit: KnockoutObservable<number>; // 単位  0 : 日, 1 : 時間
		dayCloseDeadline: KnockoutObservable<string>; // 期限の一番近い日 - A2_21
		detailRemainingNumbers: KnockoutObservableArray<RemainNumberDetailedInfo>; // 残数詳細一覧 - 残数詳細情報
		currentRemainNumber: KnockoutObservable<string>; // 現時点残数 - A2_5
		employeeId: KnockoutObservable<string>; // 社員ID
		management: KnockoutObservable<number>; // 管理する
		constructor(
			expiredWithinMonth: string,
			unit: number,
			dayCloseDeadline: string,
			detailRemainingNumbers: Array<RemainNumberDetailedInfo>,
			currentRemainNumber: string,
			employeeId: string,
			management: number
		) {
			let self = this;
			self.expiredWithinMonth = ko.observable(expiredWithinMonth);
			self.unit = ko.observable(unit);
			self.dayCloseDeadline = ko.observable(dayCloseDeadline);
			self.detailRemainingNumbers = ko.observableArray(detailRemainingNumbers);
			self.currentRemainNumber = ko.observable(currentRemainNumber);
			self.employeeId = ko.observable(employeeId);
			self.management = ko.observable(management);
		}
	}

	/** 残数詳細情報 */
	export class RemainNumberDetailedInfo {
		deadline: KnockoutObservable<string>; // 期限日 - A3_32
		dueDateStatus: KnockoutObservable<string>; // 期限日状況 - A3_31
		digestionCount: KnockoutObservable<string>; // 消化数 - A3_43
		digestionDate: KnockoutObservable<string>; // 消化日  - A3_42
		digestionDateStatus: KnockoutObservable<string>; // 消化日状況  - A3_41
		digestionStatus: KnockoutObservable<string>; // 消化状況  - A3_21
		numberOccurrences: KnockoutObservable<string>; // 発生数 - A3_13
		accrualDate: KnockoutObservable<string>; // 発生日 - A3_12
		occurrenceDateStatus: KnockoutObservable<string>; // 発生日状況 - A3_11
		constructor(
			deadline: string,
			dueDateStatus: string,
			digestionCount: string,
			digestionDate: string,
			digestionDateStatus: string,
			digestionStatus: string,
			numberOccurrences: string,
			accrualDate: string,
			occurrenceDateStatus: string
		) {
			let self = this;
			self.deadline = ko.observable(deadline);
			self.dueDateStatus = ko.observable(dueDateStatus);
			self.digestionCount = ko.observable(digestionCount);
			self.digestionDate = ko.observable(digestionDate);
			self.digestionDateStatus = ko.observable(digestionDateStatus);
			self.digestionStatus = ko.observable(digestionStatus);
			self.numberOccurrences = ko.observable(numberOccurrences);
			self.accrualDate = ko.observable(accrualDate);
			self.occurrenceDateStatus = ko.observable(occurrenceDateStatus);
		}
	}

	export class ItemModel {
		code: number;
		name: string;
		displayName?: string;
		constructor(code: number, name: string, displayName?: string) {
			this.code = code;
			this.name = name;
			this.displayName = displayName;
		}
	}

	class HolidayInfo {
		occurrenceDateStatus : string;
		accrualDate: string;
		digestionStatus: string;
		deadline: string;
		digestionDateStatus : string;
		digestionDate: string;
		constructor(occurrenceDateStatus : string, accrualDate: string, digestionStatus: string, deadline: string,digestionDateStatus : string, digestionDate: string) {
			this.occurrenceDateStatus = occurrenceDateStatus;
			this.accrualDate = accrualDate;
			this.digestionStatus = digestionStatus;
			this.deadline = deadline;
			this.digestionDateStatus = digestionDateStatus;
			this.digestionDate = digestionDate;
		}
	}

}