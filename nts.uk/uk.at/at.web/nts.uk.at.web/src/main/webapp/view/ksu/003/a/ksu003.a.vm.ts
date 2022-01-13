module nts.uk.at.view.ksu003.a.viewmodel {
	import getText = nts.uk.resource.getText;
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;
	import errorDialog = nts.uk.ui.dialog.alertError;
	import dialog = nts.uk.ui.dialog;
	import bundledErrors = nts.uk.ui.dialog.bundledErrors;
	import block = nts.uk.ui.block;
	import exTable = nts.uk.ui.exTable;
	import formatById = nts.uk.time.format.byId;
	import duration = nts.uk.time.minutesBased.duration; // convert time 
	import characteristics = nts.uk.characteristics;
	import alertError = nts.uk.ui.dialog.alertError
	var ruler: any;
	export class ScreenModel {
		KEY: string = 'USER_KSU003_INFOR';

		employeeIdLogin: string = __viewContext.user.employeeId; // Employee login
		targetDate: KnockoutObservable<string> = ko.observable('2020/05/02'); // A2_1_3
		targetDateDay: KnockoutObservable<string> = ko.observable('');

		organizationName: KnockoutObservable<string> = ko.observable(''); // A2_2
		selectedDisplayPeriod: KnockoutObservable<number> = ko.observable(1); // A2_5
		itemList: KnockoutObservableArray<model.ItemModel>; /*A3_2*/
		selectOperationUnit: KnockoutObservable<string> = ko.observable('0');
		sortList: KnockoutObservableArray<model.ItemModel>; /*A3_4*/
		checked: KnockoutObservable<string> = ko.observable('1');
		checkedName: KnockoutObservable<boolean> = ko.observable(false); // A3_3

		showA9: boolean;
		checkNext: KnockoutObservable<boolean> = ko.observable(true); // xử lý next day
		checkPrv: KnockoutObservable<boolean> = ko.observable(true);
		indexBtnToLeft: KnockoutObservable<number> = ko.observable(0);// xử lý của botton toLeft, toRight
		indexBtnToDown: KnockoutObservable<number> = ko.observable(0);// xử lý của botton toDown, toUp

		dataFromA: KnockoutObservable<model.InforScreenADto> = ko.observable(); //Data get from Screen A
		dataScreen003A: KnockoutObservable<model.DataScreenA> = ko.observable();
		dataScreen003AFirst: any = [];
		dataScreen045A: KnockoutObservable<model.DataFrom045> = ko.observable();
		dataInitStartKsu003Dto: KnockoutObservable<model.GetInfoInitStartKsu003Dto> = ko.observable();
		fixedWorkInformationDto: Array<model.FixedWork> = [];
		employeeScheduleInfo: Array<any> = [];

		operationUnit: KnockoutObservable<number> = ko.observable(3.5); // A3_2 pixel (mỗi ô đang để 40px)
		localStore: model.ILocalStore = {};
		lstEmpId: Array<model.IEmpidName> = [];
		timeRange: number = 0; // tổng số cột ở phần detail (24 or 48)
		initDispStart: number = 0; // thời gian bắt đầu của scroll phần detail
		dispStart: number = 0; // bắt đầu ở phần detail
		dispStartHours: number = 0; // thời gian bắt đầu ở header phần detail
		checkGetInfo: boolean = false;

		dataOfGantChart: Array<model.ITimeGantChart> = []; // data của từng phần trên extable
		midDataGC: Array<any> = [];
		disableDs: any = []; // gantt chart bị disable, không dùng schedule
		leftDs: any = [];
		allGcShow: any = []; // lưu gant chart và data để tạo lại
		gcShowChart: any = []; // tất cả chart break, holiday, short show trên màn hình
		allTimeChart: any = [];
		allTimeBrk: any = [];

		checkClearTime: boolean = true; // check when change work time, work type
		checkUpdateMidChart: boolean = true;
		checkUpdateTime: any = [];
		lstDis: any = [];
		lstBreakSum: any = []; // list break time xuất hiện trên màn hình
		lstHolidayShort: any = [];
		checkDisByDate: boolean = true; // check modify KSCMT_AUTH_MODIFYDEADLINE
		lstAllChildShow: any = [];// tất cả thanh gant chart con show trên màn hình
		checkDragDrop: boolean = false; // phân biệt resize = false vs drop = true
		holidayShort: any = [];
		checkMes: number = 0;
		checkNeedTime: string = "";
		workTypeName: string = "";
		check045003: boolean = true;
		timesOfInput: number = 0;
		timesOfInputTime: number = 0;

		enableSave: KnockoutObservable<boolean> = ko.observable(false); // ver 2
		checkEnableSave: boolean = true;
		checkEnableWork: boolean = true;
		checkOpenDialog: boolean = true; // check open dialog when have error
		breakChangeCore: any = [];
		checkRetained: boolean = true;
		checkErrorTime: boolean = true;
		lstErr: any = [];
		index045: number = -1;
		colorBreak45: boolean = true;
		checkCloseKsu003: boolean = false;
		checkCalcSum: boolean = true;
		checkDrop: boolean = false;
		checkFocus: any = [];
		// ver 4
		dataAb: KnockoutObservableArray<any> = ko.observableArray([]);
		displayRangeSelect: KnockoutObservable<number> = ko.observable(1); // A14 表示範囲
		rangeList: KnockoutObservableArray<model.RangeModel>;
		initDispStartChecked: KnockoutObservable<number> = ko.observable(0); // A14_3 開始時刻
		dispStartChecked: KnockoutObservable<number> = ko.observable(0); // A14_5 初期表示の開始時刻	
		selectedTimeRange: KnockoutObservable<number> = ko.observable(1); // A14_8

		fixedType: any;
		flexType: any;
		changeableType: any;
		breakType: any;
		otType: any;
		holidayType: any;
		shortType: any;
		coreType: any;
		taskType: any;
		taskTypes: any = [];
		taskPasteData: any = {};
		taskData: any = [];
		taskSaveData: any = [];
		lstTaskScheduleDetailEmp: any = [];
		typeTask: any = [];
		checkSaveDataMode: any = true;
		lstChartTask: any = [];
		taskTypeDel: any = [];
		dataToAb: any = {};

		modes = ko.observableArray(["normal", "paste", "pasteFlex"].map(c => ({ code: c, name: c })));

        taskId = null;
        
		constructor(data: any) {
			let self = this;
			// get data from sc A
			localStorage.removeItem(nts.uk.request.location.siteRoot.rawUrl + "nts.uk.at.web/view/ksu/003/a/index.xhtml/extable-ksu003/areawidths");

			self.localStore = data;
			self.showA9 = true;

			self.dataFromA = ko.observable(getShared('dataFromA'));
			if ((self.dataFromA().daySelect < self.dataFromA().dayEdit && self.dataFromA().dayEdit !== "1900/01/01") ||
				(self.dataFromA().daySelect < moment(new Date()).format('YYYY/MM/DD') && self.dataFromA().dayEdit !== "1900/01/01")) {
				self.checkDisByDate = false;
			}
			self.lstEmpId = _.flatMap(self.dataFromA().listEmp, c => [{ empId: c.id, name: c.name, code: c.code }]);

			self.targetDate(self.dataFromA().daySelect);

			if (self.targetDate() === (self.dataFromA().startDate)) {
				self.checkPrv(false);
			}

			if (self.targetDate() === (self.dataFromA().endDate)) {
				self.checkNext(false);
			}

			let shortW = moment(self.targetDate()).format('(ddd)');
			if (shortW == "(土)") {
				shortW = "<span style='color:#0000ff;'>" + shortW + "</span>";
			} else if (shortW == "(日)") {
				shortW = "<span style='color:#ff0000;'>" + shortW + "</span>";
			}

			self.targetDateDay(self.targetDate() + shortW);

			self.itemList = ko.observableArray([
				new model.ItemModel('0', getText('KSU003_13')),
				new model.ItemModel('1', getText('KSU003_14')),
				new model.ItemModel('2', getText('KSU003_15')),
				new model.ItemModel('3', getText('KSU003_16')),
				new model.ItemModel('4', getText('KSU003_17'))
			]);

			self.sortList = ko.observableArray([
				new model.ItemModel('0', getText('KSU003_59')),
				new model.ItemModel('1', getText('KSU003_60'))
			]);

			if (!_.isNil(data)) {
				self.indexBtnToLeft(data.showHide);
				self.selectOperationUnit(data.operationUnit);
				self.selectedDisplayPeriod(data.displayFormat);
				self.checked(data.startTimeSort);
				self.checkedName(data.showWplName);
			};

			/* 開始時刻順に並び替える(A3_3)はチェックされている */
			self.checked.subscribe((value) => {
				let checkSort = [];
				if (!_.isNil(data)) {
					checkSort = $("#extable-ksu003").exTable('updatedCells');
				}
				if (checkSort.length > 0) {
					dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
						self.sortEmployee(value);
					}).ifNo(() => { return; })
				} else {
					self.sortEmployee(value);
				}
			});

			/** A3_4 */
			self.checkedName.subscribe((value) => {
				self.localStore.showWplName = value;
				characteristics.save(self.KEY, self.localStore);
			});

			/** 操作単位選択に選択する A3_2 */
			self.selectOperationUnit.subscribe((value) => {
				let c = parseInt(value) + 1;
				if (value == '3') c = 6
				if (value == '4') c = 12;
				if (!_.isNil(ruler)) ruler.setSnatchInterval(c);

				self.localStore.operationUnit = value;
				characteristics.save(self.KEY, self.localStore);
			});

			self.selectedDisplayPeriod.subscribe((value) => {
				self.subFormatDate(value);
				self.localStore.displayFormat = value;
				characteristics.save(self.KEY, self.localStore);
			});

			self.checkUpdateTime = { name: "", id: 0 };

			$('#content').on({
				"click": function(a: any) {
					$(".x-error-message").css("z-index", "2000");
					if ((a.target.cellIndex == 0 || a.target.cellIndex == 2) && a.target.outerText == "") {
						$(".x-error-message").text("");
						$(".x-error-message").css("left", "2000px");
					}
				}
			});

			$("#extable-ksu003").on("extablecellupdated", (dataCell: any) => {
				self.inputMid(dataCell);
			});

			// ver4 
			self.rangeList = ko.observableArray([
				new model.RangeModel('0', 0), new model.RangeModel('1', 1),
				new model.RangeModel('2', 2), new model.RangeModel('3', 3),
				new model.RangeModel('4', 4), new model.RangeModel('5', 5),
				new model.RangeModel('6', 6), new model.RangeModel('7', 7),
				new model.RangeModel('8', 8), new model.RangeModel('9', 9),
				new model.RangeModel('10', 10), new model.RangeModel('11', 11),
				new model.RangeModel('12', 12)
			]);
		}

		//startPage
		public startPage(): JQueryPromise<any> {
			block.grayout();
			let self = this, dfd = $.Deferred<any>();
			self.getData().done(() => {
				self.hoverEvent();
				dfd.resolve();
				block.clear();
				model.showHide(self.showA9, self.indexBtnToLeft, self.dataScreen003A().targetInfor);

				if (self.initDispStart != 0)
					$("#extable-ksu003").exTable("scrollBack", 0, { h: (self.initDispStart * 42 - self.dispStartHours * 42) + 5 });
				else
					$("#extable-ksu003").exTable("scrollBack", 0, { h: 0 });

				let date = new Date(),
					firstDay = moment(new Date(date.getFullYear(), date.getMonth(), 1)).format('YYYY/MM/DD'),
					lastDay = moment(new Date(date.getFullYear(), date.getMonth() + 1, 0)).format('YYYY/MM/DD');

				if (firstDay == self.targetDate()) {
					$("#prev-all").addClass("dis-pointer-none");
					$("#prev-one").addClass("dis-pointer-none");
					$("#next-all").removeClass("dis-pointer-none");
					$("#next-one").removeClass("dis-pointer-none");
				}

				if (lastDay == self.targetDate()) {
					$("#next-one").addClass("dis-pointer-none");
					$("#next-all").addClass("dis-pointer-none");
					$("#prev-one").removeClass("dis-pointer-none");
					$("#prev-all").removeClass("dis-pointer-none");
				}
			});

			return dfd.promise();
		}

		public subFormatDate(value: any) {
			let self = this;
			if (self.checkSaveDataMode == false) {
				self.checkSaveDataMode = true;
				return;
			}
			if (self.enableSave() == true) {
				dialog.confirm({ messageId: "Msg_1732" }).ifYes(() => {
					self.destroyAndCreateGrid(self.lstEmpId, 0);
					self.enableSave(false);
					__viewContext.viewModel.viewmodelAb.taskChecked.valueHasMutated();
					self.changeModeColor(value);
					return;
				}).ifNo(() => {
					self.checkSaveDataMode = false;
					if (value == 2)
						self.selectedDisplayPeriod(1);
					else
						self.selectedDisplayPeriod(2);
					return;
				})
			}

			if (self.enableSave() == false)
				self.changeModeColor(value);
		}

		// change color chart when change mode
		public changeModeColor(value: any) {
			let self = this, mode = "normal";
			let leftmostContent = {};
			if (value == 1) {
				if (_.isNil(self.fixedType)) return;
				self.fixedType.color("#ccccff")
				self.changeableType.color("#ffc000")
				self.flexType.color("#ccccff")
				self.otType.color("#ffff00")
				self.otType.hide(false);
				self.coreType.color("#00ffcc")
				self.coreType.hide(false);
				self.breakType.zIndex(1001);
				self.holidayType.zIndex(1103);
				self.shortType.zIndex(1052);
				if (self.taskTypes.length > 0) {
					for (let i = 0; i < self.taskTypes.length; i++)
						self.taskTypes[i].hide(true);
				}
				self.checkSaveDataMode = true;
				$("#ab1-viewModel").hide();
				$(".ab-view").addClass('hide-pallet');

				$(".ex-body-detail").removeClass("disable-css");
				$(".ex-body-middle").removeClass("dis-pointer");
				$(".ex-body-detail").removeClass("dis-pointer-none");
			} else {
				// mode task
				if (_.isNil(self.fixedType)) return;
				self.fixedType.color("#fff")
				self.changeableType.color("#fff")
				self.flexType.color("#fff")
				self.otType.color("#fff")
				self.otType.hide(true);
				self.coreType.color("#fff")
				self.coreType.hide(true);
				self.breakType.zIndex(1999);
				self.holidayType.zIndex(1996);
				self.shortType.zIndex(1995);
				if (self.taskTypes.length > 0) {
					for (let i = 0; i < self.taskTypes.length; i++)
						self.taskTypes[i].hide(false);
				}
				self.checkSaveDataMode = true;
				$("#ab1-viewModel").show();
				$(".ab-view").removeClass('hide-pallet');

				$(".ex-body-detail").addClass("disable-css");
				$(".ex-body-middle").addClass("dis-pointer");
				//$(".ex-body-detail").addClass("dis-pointer-none");

				if (!_.isNil(self.localStore.workSelection)) {
					if (self.localStore.workSelection == 0) {
						mode = "paste";
						__viewContext.viewModel.viewmodelAb.taskChecked.valueHasMutated();
					}

					if (self.localStore.workSelection == 1) {
						mode = "pasteFlex";
						__viewContext.viewModel.viewmodelAb.selectedButton.valueHasMutated();
					}

				}
			}
			self.createLeftAndMidExtable(self.dataOfGantChart, self.leftDs, self.midDataGC, self.disableDs, 1);
			ruler.setMode(mode);
		}

		// Check and bind data when input worktype & worktime
		public inputWorkInfo(dataMid: any, index: number, dataCell: any, dataFixed: any, empId: string, columnKey: string) {
			let self = this, color = "",
				workTimeCode = $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode,
				css = model.getCss(index, self.dataScreen003A().targetInfor);

			if (self.check045003 == false) return;

			let lstType = self.dataScreen003A().scheCorrection
			let fixCheck = _.filter(lstType, (x: any) => { return x === 0 }),
				flexCheck = _.filter(lstType, (x: any) => { return x === 1 }),
				flowCheck = _.filter(lstType, (x: any) => { return x === 2 });

			if (empId === self.employeeIdLogin) {
				color = "#94b7fe";
			} else {
				color = "#cee6ff";
			}

			self.getChangeWorkType(columnKey, empId, index).done((data) => {
				self.getEmpWorkFixedWorkInfo(columnKey, empId, index).done(() => {
					$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", workTimeCode);
					$(css.cssbreakTime).css("background-color", color);
					if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.isHoliday == true) {
						model.setDisableCell("disableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
					} else {
						if (columnKey === "worktypeCode") {
							$(css.cssWorkType).css("background-color", color);
							$(css.cssWorkTypeName).css("background-color", color);
						}
						if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
							self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null) {

							if ((fixCheck.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED) ||
								(flexCheck.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) ||
								(flowCheck.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW)) {
								$(css.cssStartTime2).addClass("xseal");
								$(css.cssEndTime2).addClass("xseal");
								$(css.cssStartTime1).addClass("xseal");
								$(css.cssEndTime1).addClass("xseal");
								model.setDisBackCell("#DDDDD2", css, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
								model.setDisableCell("disableCell", empId, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");

								for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
									$("#extable-ksu003").exTable("disableCell", "detail", empId, z.toString() + "_");
								}

							} else {
								model.setDisableCell("enableCell", empId, "", "", "", "", "startTime1", "endTime1", "", "");
								for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
									$("#extable-ksu003").exTable("enableCell", "detail", empId, z.toString() + "_");
								}

								$(css.cssStartTime1).css("background-color", color);
								$(css.cssEndTime1).css("background-color", color);
								if (!_.isEqual($(css.cssStartTime2).css("background-color"), "rgb(221, 221, 210)") ||
									$("#extable-ksu003").exTable('dataSource', 'middle').body[index].startTime2 != "") {
									if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
										self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null &&
										self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX &&
										self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime2 != null) {

										$(css.cssStartTime2).css("background-color", color);
										$(css.cssStartTime2).removeClass("xseal");
									}
								}

								if (!_.isEqual($(css.cssEndTime2).css("background-color"), "rgb(221, 221, 210)") ||
									$("#extable-ksu003").exTable('dataSource', 'middle').body[index].endTime2 != "") {

									if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
										self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null &&
										self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX &&
										self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime2 != null) {
										$(css.cssEndTime2).css("background-color", color);
										$(css.cssEndTime2).removeClass("xseal");
									}
								}
								if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
									self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null) {

									if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) {
										$(css.cssEndTime2).css("background-color", "#DDDDD2");
										$(css.cssStartTime2).css("background-color", "#DDDDD2");
										model.setDisableCell("disableCell", empId, "", "", "", "", "", "", "startTime2", "endTime2");
									} else {

										if (self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime2 != null) {
											$(css.cssEndTime2).css("background-color", color);
											$("#extable-ksu003").exTable("enableCell", "middle", empId, "startTime2");
											$(css.cssEndTime2).removeClass("xseal");
										}

										if (self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime2 == null) {
											$(css.cssEndTime2).css("background-color", "#DDDDD2");
											$("#extable-ksu003").exTable("disableCell", "middle", empId, "startTime2");
										}

										if (self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime2 != null) {
											$(css.cssStartTime2).css("background-color", color);
											$("#extable-ksu003").exTable("enableCell", "middle", empId, "endTime2");
											$(css.cssStartTime2).removeClass("xseal");
										}

										if (self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime2 == null) {
											$(css.cssStartTime2).css("background-color", "#DDDDD2");
											$("#extable-ksu003").exTable("disableCell", "middle", empId, "endTime2");

										}
									}
								}
							}
						}
						if (columnKey === "worktimeCode") {
							$(css.cssWorkTime).css("background-color", color);
							$(css.cssWorkTName).css("background-color", color);
						}
					}
					if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.isNeedWorkTime == "NOT_REQUIRED") {
						$("#extable-ksu003").exTable("disableCell", "middle", empId, "worktimeCode");
					}
					self.removeChart(index);
					if (dataCell.originalEvent.detail.value != "" &&
						dataCell.originalEvent.detail.value != null) {
						self.checkClearTime = true;

						let lstBrkTime = dataFixed[0].workScheduleDto.listBreakTimeZoneDto, totalBrkTime: any = null;

						let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5);
						self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workScheduleDto.listBreakTimeZoneDto,
							timeRangeLimit, lstTime, "BREAK", index);
						for (let e = 0; e < dataFixed[0].workInfoDto.listTimeVacationAndType.length; e++) {
							let y = dataFixed[0].workInfoDto.listTimeVacationAndType[e];
							lstTime = self.calcChartTypeTime(dataFixed[0], y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", index);
						}
						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workInfoDto.shortTime,
							timeRangeLimit, lstTime, "SHORT", index);
						let dataFixInfo = _.filter(self.fixedWorkInformationDto, x => { return x.empId === empId });
						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixInfo[0].fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", index);
						let totalTime = model.calcAllTime(dataFixed[0], lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);

						totalBrkTime = self.calcAllBrk(lstTime);
						totalBrkTime = totalBrkTime != null ? formatById("Clock_Short_HM", Math.round(totalBrkTime * 5)) : "";

						let schedule: model.EmployeeWorkScheduleDto = dataFixed[0].workScheduleDto,
							fixed: model.FixedWorkInforDto = dataFixed[0].fixedWorkInforDto,
							info: model.EmployeeWorkInfoDto = dataFixed[0].workInfoDto;

						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeCode", schedule.workTypeCode);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", fixed.workTypeName);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", schedule.workTimeCode);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", fixed.workTimeName);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime1", formatById("Clock_Short_HM", (schedule.startTime1)));
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime1", formatById("Clock_Short_HM", (schedule.endTime1)));

						if (schedule.startTime2 != null)
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime2", formatById("Clock_Short_HM", (schedule.startTime2)));
						else
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime2", "");

						if (schedule.endTime2 != null)
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime2", formatById("Clock_Short_HM", (schedule.endTime2)));
						else
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime2", "");

						if (self.checkCalcSum == true)
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "totalTime", totalTime);

						let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ?
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(9)" :
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)";

						if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
							$(cssTotalTime).css("background-color", "#ffffff");

						if (totalBrkTime == totalBrkTime + " ") {
							totalBrkTime = _.trim(totalBrkTime);
						} else {
							totalBrkTime = totalBrkTime + " "
						}

						$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", totalBrkTime);
						let colorTime = "";
						if (self.dataScreen003A().employeeInfo[index].empId === self.employeeIdLogin)
							colorTime = "#94b7fe";
						else
							colorTime = "#cee6ff";

						if (self.colorBreak45 == true)
							$(css.cssbreakTime).css("background-color", colorTime);

						self.dataScreen003A().employeeInfo[index].workScheduleDto.workTypeCode = schedule.workTypeCode;
						self.dataScreen003A().employeeInfo[index].workScheduleDto.workTimeCode = schedule.workTimeCode;
						self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime1 = schedule.startTime1;
						self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime1 = schedule.endTime1;
						self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime2 = schedule.startTime2;
						self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime2 = schedule.endTime2;
						let dataMid = $("#extable-ksu003").exTable('dataSource', 'middle').body[index];

						self.checkTimeInfo(index, dataMid.worktypeCode, dataMid.worktimeCode, dataMid.startTime1,
							dataMid.startTime2, dataMid.endTime1, dataMid.endTime2, columnKey);

						if (lstBrkTime != null && lstBrkTime.length > 0) {
							self.dataScreen003A().employeeInfo[index].workScheduleDto.listBreakTimeZoneDto = lstBrkTime;
						}

						self.dataScreen003A().employeeInfo[index].workInfoDto.directAtr = info.directAtr;
						self.dataScreen003A().employeeInfo[index].workInfoDto.bounceAtr = info.bounceAtr;
						self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workTypeName = fixed.workTypeName;
						self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workTimeName = fixed.workTimeName;
						self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType = fixed.workType;
						self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.fixBreakTime = fixed.fixBreakTime;

						self.convertDataIntoExtable(index);

						let datafilter: Array<model.ITimeGantChart> = _.filter(self.dataOfGantChart, (x: any) => { return x.empId === empId });
						if (datafilter.length > 0) {
							//self.updateGantChart(datafilter, lineNo, fixedGc, lstBreak, indexS, indexF);
							self.addAllChart(datafilter, index, [], "");
							ruler.replaceAt(index, [
								...self.allGcShow
							]);
						}

						self.checkUpdateTime.name = "";
						self.checkUpdateTime.id = 0;
						if (_.isEmpty($("#extable-ksu003").data("errors"))) {
							$(".xcell").removeClass("x-error");
							self.enableSave(true);
						}

						// set work time name
						if ((schedule.workTimeCode == null || schedule.workTimeCode == "")) {
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
						} else {
							if (fixed != null && (fixed.workTimeName == null || fixed.workTimeName == "")) {
								$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", schedule.workTimeCode + getText('KSU003_54'));
							}
						}

						if (dataFixed[0].fixedWorkInforDto.isNeedWorkTime == false) {
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
							$("#extable-ksu003").exTable("disableCell", "middle", empId, "worktimeCode");
						} else {
							$("#extable-ksu003").exTable("enableCell", "middle", empId, "worktimeCode");
						}
					}
				})
			});
		}

		// Set data for middle of extable (worktypeCode, worktimeCode ....)
		setDataToMidExtable(index: number, empId: string, schedule: model.EmployeeWorkScheduleDto, fixed: model.FixedWorkInforDto) {
			let workTypeName = "", workTimeName = "", self = this, dataFixed = self.dataScreen003A().employeeInfo[index];
			if (schedule == null) {
				workTimeName = getText('KSU003_55');
			}
			if (fixed != null) {
				if (fixed.workTypeName != null && fixed.workTypeName != "") {
					workTypeName = fixed.workTypeName;
				}
				if (fixed.workTimeName != null && fixed.workTimeName != "") {
					workTimeName = fixed.workTimeName;
				}
			}
			if (schedule.workTypeCode != null && schedule.workTypeCode != "" && fixed != null
				&& (fixed.workTypeName == null || fixed.workTypeName == "")) {
				workTypeName = schedule.workTypeCode + getText('KSU003_54')
			}
			// set work time name
			if ((schedule.workTimeCode == null || schedule.workTimeCode == "")) {
				workTimeName = getText('KSU003_55');
			} else {
				if (fixed != null && (fixed.workTimeName == null || fixed.workTimeName == "")) {
					workTimeName = schedule.workTimeCode + getText('KSU003_54');
				}
			}
			$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", workTypeName);
			$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", workTimeName);
			$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime1",
				schedule.startTime1 == null ? "" : formatById("Clock_Short_HM", (schedule.startTime1)));
			$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime1",
				schedule.endTime1 == null ? "" : formatById("Clock_Short_HM", (schedule.endTime1)));

			if (schedule.startTime2 != null)
				$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime2", formatById("Clock_Short_HM", (schedule.startTime2)));
			else
				$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime2", "");

			if (schedule.endTime2 != null)
				$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime2", formatById("Clock_Short_HM", (schedule.endTime2)));
			else
				$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime2", "");

			$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeCode", schedule.workTypeCode);
			$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", schedule.workTimeCode);

			if (self.checkCalcSum == false) return;
			let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5), totalBrkTime: any = null;
			self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
			lstTime = self.calcChartTypeTime(dataFixed, dataFixed.workScheduleDto.listBreakTimeZoneDto,
				timeRangeLimit, lstTime, "BREAK", index);
			for (let e = 0; e < dataFixed.workInfoDto.listTimeVacationAndType.length; e++) {
				let y = dataFixed.workInfoDto.listTimeVacationAndType[e];
				lstTime = self.calcChartTypeTime(dataFixed, y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", index);
			}
			lstTime = self.calcChartTypeTime(dataFixed, dataFixed.workInfoDto.shortTime, timeRangeLimit, lstTime, "SHORT", index);
			lstTime = self.calcChartTypeTime(dataFixed, dataFixed.fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", index);
			let totalTime = model.calcAllTime(dataFixed, lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);
			totalBrkTime = self.calcAllBrk(lstTime);
			totalBrkTime = totalBrkTime != null ? formatById("Clock_Short_HM", Math.round(totalBrkTime * 5)) : "";

			$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", totalBrkTime);
			let cssbreakTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(10)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(8)";
			let colorTime = "";
			if (self.dataScreen003A().employeeInfo[index].empId === self.employeeIdLogin) {
				colorTime = "#94b7fe";
			} else {
				colorTime = "#cee6ff";
			}
			if (self.colorBreak45 == true)
				$(cssbreakTime).css("background-color", colorTime);

			let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(9)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)";

			if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
				$(cssTotalTime).css("background-color", "#ffffff");

			$("#extable-ksu003").exTable("cellValue", "middle", empId, "totalTime", totalTime != null ? totalTime : "");
		}

		// xóa chart khi là ngày nghỉ
		removeChart(index: number) {
			ruler.replaceAt(index, [{
				type: "Flex",
				options: {
					id: `lgc` + index,
					start: -1000,
					end: -1000,
					lineNo: index
				}
			}]);
		}

		// Get all data for KSU003
		public getData(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			let canModified = 0;
			// 修正可能 - Check ngày có thể chỉnh sửa
			// 対象年月日(A2_1_3)>＝パラメータ.いつから編集可能か.修正可能年月日 
			if ((self.targetDate() >= self.dataFromA().dayEdit)) {
				canModified = 1;
			}
			let data003A: model.DataScreenA = {
				startDate: self.dataFromA().startDate, // 開始日		
				endDate: self.dataFromA().endDate, // 終了日
				unit: self.dataFromA().unit,
				id: self.dataFromA().unit == 0 ? self.dataFromA().workplaceId : self.dataFromA().workplaceGroupId,
				name: self.dataFromA().workplaceName,
				timeCanEdit: self.dataFromA().dayEdit, //いつから編集可能か
				targetInfor: 0,//対象情報 : 複数回勤務 (1 :true,0:false)
				canModified: canModified,//修正可能 CanModified
				scheCorrection: [],//スケジュール修正の機能制御  WorkTimeForm
				employeeInfo: [],
			}
			self.dataScreen003A(data003A);
			self.getInforFirt();
			// tạo localStore lần đầu tiên
			let local: model.ILocalStore = {
				startTimeSort: self.checked(),
				showWplName: self.checkedName() == true,
				operationUnit: self.selectOperationUnit(),
				displayFormat: self.selectedDisplayPeriod(),
				showHide: self.indexBtnToLeft(),
				lstEmpIdSort: _.map(self.lstEmpId, (x: model.IEmpidName) => { return x.empId }),
				pageNo: !_.isNil(self.localStore) ? self.localStore.pageNo : 0,
				work1Selection: !_.isNil(self.localStore) ? self.localStore.work1Selection : '',
				workPalletDetails: !_.isNil(self.localStore) ? self.localStore.workPalletDetails : { column: 0, data: { text: "storeNull", tooltip: "storeNull", page: 1 }, row: 0 },
				workSelection: !_.isNil(self.localStore) ? self.localStore.workSelection : 0
			}
			self.localStore = local;
			self.getWorkingByDate(self.targetDate(), 1).done(() => {
				self.getTask().done(() => {
					self.convertDataIntoExtable();
					self.initExtableData().done(() => {
						if (self.selectedDisplayPeriod() == 2 && self.disableDs.length > 0) {
							for (let i = 0; i < self.disableDs.length; i++) {
								$("#extable-ksu003 > .ex-body-detail > table > tbody tr:nth-child" + "(" + (self.disableDs[i].index + 2).toString() + ")").css("background-color", self.disableDs[i].color);
							}
						}
						dfd.resolve();
					});
				});

			});
			return dfd.promise();
		}

		// ①<<ScreenQuery>> 初期起動の情報取得
		public getInforFirt() {
			let self = this;
			let dfd = $.Deferred<any>();
			let targetOrgDto = {
				unit: self.dataFromA().unit,
				workplaceId: self.dataFromA().workplaceId,
				workplaceGroupId: self.dataFromA().workplaceGroupId
			}
			service.getDataStartScreen(targetOrgDto)
				.done((data: model.GetInfoInitStartKsu003Dto) => {
					self.dataInitStartKsu003Dto(data);
					self.organizationName(self.dataInitStartKsu003Dto().displayInforOrganization.displayName);
					self.dataToAb = {
						targetDate: self.targetDate(),
						dataScreen003A: self.dataScreen003A(),
						localStore: self.localStore,
						organizationName: self.organizationName()
					}
					setShared("dataShareAbFromA", self.dataToAb);
					self.dataScreen003A().targetInfor = data.manageMultiDto.useATR;
					self.timeRange = self.dataInitStartKsu003Dto().byDateDto.dispRange == 0 ? 24 : 48;
					self.selectedTimeRange(self.dataInitStartKsu003Dto().byDateDto.dispRange + 1);
					self.initDispStart = self.dataInitStartKsu003Dto().byDateDto.initDispStart;
					self.initDispStartChecked(self.initDispStart);
					self.dispStart = (self.dataInitStartKsu003Dto().byDateDto.dispStart * 60) / 5;
					self.dispStartHours = self.dataInitStartKsu003Dto().byDateDto.dispStart;
					self.dispStartChecked(self.dispStartHours);
					self.dataScreen003A().targetInfor = self.dataInitStartKsu003Dto().manageMultiDto.useATR;
					self.dataScreen003A().scheCorrection = self.dataInitStartKsu003Dto().functionControlDto != null
						? self.dataInitStartKsu003Dto().functionControlDto.changeableWorks : [];

					if (data.taskOperationMethod != 0) {
						$("#label-display").hide()
						$("#hai-cham").hide()
						$("#display-format").hide()
						self.selectedDisplayPeriod(1);
					} else {
						$("#label-display").show();
						$("#hai-cham").show();
						$("#display-format").show();
						self.selectedDisplayPeriod(self.localStore.displayFormat);
					}
				}).fail(function(error) {
					errorDialog({ messageId: error.messageId });
					dfd.reject();
				}).always(function() {
				});
		}

		// ②<<ScreenQuery>> 日付別勤務情報で表示する
		public getWorkingByDate(targetDate: any, check?: number): JQueryPromise<any> {
			let self = this,
				dfd = $.Deferred<any>(),
				lstEmpId = _.flatMap(self.dataFromA().listEmp, c => [c.id]),
				param = {
					lstEmpId: lstEmpId,
					date: targetDate,
					selectedDisplayPeriod: self.selectedDisplayPeriod()
				};

			service.displayDataKsu003(param).done((data: any) => {
				const dataFirt = data;
				if (self.checked() === "0") {
					let dataSort = self.sortEmpByTime(data);
					dataSort = _.sortBy(dataSort, ['startTime', 'empCode']);
					self.lstEmpId = self.lstEmpId.sort(function(a: any, b: any) {
						return _.findIndex(dataSort, x => { return x.empId == a.empId }) - _.findIndex(dataSort, x => { return x.empId == b.empId });
					});
				}
				data = data.sort(function(a: any, b: any) {
					return _.findIndex(self.lstEmpId, x => { return x.empId == a.empId }) - _.findIndex(self.lstEmpId, x => { return x.empId == b.empId });
				});
				self.dataScreen003AFirst = dataFirt;
				self.dataScreen003A().employeeInfo = data;

				self.fixedWorkInformationDto = _.map(self.dataScreen003A().employeeInfo, (z) => ({
					empId: z.empId,
					fixedWorkInforDto: z.fixedWorkInforDto
				}))
				self.employeeScheduleInfo = _.map(self.dataScreen003A().employeeInfo, (x) => ({
					empId: x.empId,
					startTime1: x.workScheduleDto != null && x.workScheduleDto.startTime1 != null &&
						x.workScheduleDto.startTime1 != 0 ? x.workScheduleDto.startTime1 : "",
					endTime1: x.workScheduleDto != null && x.workScheduleDto.endTime1 != null &&
						x.workScheduleDto.endTime1 != 0 ? x.workScheduleDto.endTime1 : "",
					startTime2: x.workScheduleDto != null && x.workScheduleDto.startTime2 != null &&
						x.workScheduleDto.startTime2 != 0 ? x.workScheduleDto.startTime2 : "",
					endTime2: x.workScheduleDto != null && x.workScheduleDto.endTime2 != null &&
						x.workScheduleDto.endTime2 != 0 ? x.workScheduleDto.endTime2 : "",
					listBreakTimeZoneDto: x.workScheduleDto != null &&
						x.workScheduleDto.listBreakTimeZoneDto != null ? x.workScheduleDto.listBreakTimeZoneDto : ""
				}));

				self.employeeScheduleInfo = self.employeeScheduleInfo.sort(function(a, b) {
					return _.findIndex(self.lstEmpId, x => { return x.empId == a.empId }) - _.findIndex(self.lstEmpId, x => { return x.empId == b.empId });
				});
				dfd.resolve();
			}).fail(function(error) {
				errorDialog({ messageId: error.messageId });
				dfd.reject();
			}).always(function() {
				//block.clear();
			});
			return dfd.promise();
		}

		// Check when change worktypeCode
		public getChangeWorkType(columnKey: string, empId: string, index: number): JQueryPromise<any> {
			let self = this,
				dfd = $.Deferred<any>(),
				css = model.getCss(index, self.dataScreen003A().targetInfor),
				color = "",
				targetOrgDto = {
					workTypeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktypeCode,
					workTimeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode
				};

			if (empId === self.employeeIdLogin)
				color = "#94b7fe";
			else
				color = "#cee6ff";

			if (columnKey == "worktypeCode") {
				service.checkWorkType(targetOrgDto).done((data: any) => {
					self.checkNeedTime = data.typeWork;
					if (!_.isNil(data) && data.typeWork == "NOT_REQUIRED" && self.workTypeName != "") {

						if (_.isEmpty($("#extable-ksu003").data("errors")))
							self.enableSave(true);

						ruler.replaceAt(index, [{ // xóa chart khi là ngày nghỉ
							type: "Flex",
							options: {
								id: `lgc` + index,
								start: -1000,
								end: -1000,
								lineNo: index
							}
						}]);
						self.changeWorkType(columnKey, empId, index);
						model.setCellValue(empId);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", "0:00");
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", "");
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
						model.setDisBackCell(color, css, "worktypeCode", "worktypeName", "", "worktimeName", "", "", "", "");
						model.setDisBackCell("#DDDDD2", css, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
						model.setDisableCell("disableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");

						let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ?
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(9)" :
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)";

						if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") &&
							!_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)")) {
							$(cssTotalTime).css("background-color", "#ffffff");
						}

						$(".xcell").removeClass("x-error");
					} else if (!_.isNil(data) && data.typeWork == model.SetupType.REQUIRED && self.workTypeName != "") {
						$(css.cssWorkTime).removeClass("xseal");

						if (_.isEqual($(css.cssWorkTime).css("background-color"), "rgb(221, 221, 210)"))
							$(css.cssWorkTime).css("background-color", "#FFFFFF");

						model.setDisableCell("enableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "", "");

						if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
							self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null &&
							self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX &&
							self.dataScreen003A().employeeInfo[index].workScheduleDto.endTime2 != null) {

							model.setDisableCell("enableCell", empId, "", "", "", "", "", "", "startTime2", "endTime2");
							$(css.cssStartTime2).removeClass("xseal");
							$(css.cssEndTime2).removeClass("xseal");

							if (_.isEqual($(css.cssStartTime2).css("background-color"), "rgb(221, 221, 210)"))
								$(css.cssStartTime2).css("background-color", "#FFFFFF");

							if (_.isEqual($(css.cssEndTime2).css("background-color"), "rgb(221, 221, 210)"))
								$(css.cssEndTime2).css("background-color", "#FFFFFF");
						}
					} else if (!_.isNil(data) && data.typeWork == model.SetupType.OPTIONAL && self.workTypeName != "") {
						if (_.isEmpty($("#extable-ksu003").data("errors")))
							self.enableSave(true);
						ruler.replaceAt(index, [{ // xóa chart khi là ngày nghỉ
							type: "Flex",
							options: {
								id: `lgc` + index,
								start: -1000,
								end: -1000,
								lineNo: index
							}
						}]);

						self.check045003 = false;
						model.setCellValue(empId);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", "0:00");
						$(css.cssWorkTime).removeClass("xseal");
						$(css.cssStartTime1).removeClass("xseal");
						$(css.cssEndTime1).removeClass("xseal");
						model.setDisBackCell(color, css, "worktypeCode", "worktypeName", "worktimeCode", "worktimeName", "", "", "", "");
						$(css.cssTotalTime).css("background-color", "#FFFFFF");

						if ($("#extable-ksu003").exTable('dataSource', 'middle').body[index].breaktime != "0:00")
							$(css.cssbreakTime).css("background-color", color);

						model.setDisBackCell("#DDDDD2", css, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
						model.setDisableCell("disableCell", empId, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
						self.check045003 = true;
						$(".xcell").removeClass("x-error");
					}
					let lstType = self.dataScreen003A().scheCorrection,
						fix = _.filter(lstType, (x: any) => { return x === 0 }),
						flex = _.filter(lstType, (x: any) => { return x === 1 }),
						flow = _.filter(lstType, (x: any) => { return x === 2 });

					if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto != null &&
						self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType != null) {

						if ((fix.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED) ||
							(flex.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) ||
							(flow.length == 0 && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW)) {
							model.setDisBackCell("#DDDDD2", css, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
							model.setDisableCell("disableCell", empId, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
						}
					}

					if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto == null) {
						model.setDisBackCell("#DDDDD2", css, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
						model.setDisableCell("disableCell", empId, "", "", "", "", "startTime1", "endTime1", "startTime2", "endTime2");
					}
					dfd.resolve(data);
				})
			} else {
				dfd.resolve();
			}
			return dfd.promise();
		}

		changeWorkType(columnKey: string, empId?: string, index?: number, type?: string): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>(),
				targetOrgDto = {
					workTypeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktypeCode,
					workTimeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode
				};
			service.changeWorkType(targetOrgDto).done((data: any) => {
				if (!_.isNil(data)) {
					self.workTypeName = (data.workTypeName == null || data.workTypeName == "") ?
						$("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktypeCode + getText('KSU003_54') : data.workTypeName;
				} else {
					self.workTypeName = "";
				}
				$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", self.workTypeName);
			})
			return dfd.promise();
		}

		// 社員勤務予定と勤務固定情報を取得する
		getEmpWorkFixedWorkInfo(columnKey: string, empId?: string, index?: number, type?: string): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>(), indexEmp = 0;
			let targetOrgDto = [{
				workTypeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktypeCode,
				workTimeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode
			}];
			indexEmp = _.findIndex(self.dataScreen003A().employeeInfo, x => { return x.empId === empId });
			service.getEmpWorkFixedWorkInfo(targetOrgDto)
				.done((data: model.DisplayWorkInfoByDateDto) => {
					self.checkOpenDialog = true;

					if (self.checkNeedTime === "NOT_REQUIRED")
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));

					self.dataScreen003A().employeeInfo[indexEmp].fixedWorkInforDto = data.fixedWorkInforDto;
					self.dataScreen003A().employeeInfo[indexEmp].workScheduleDto = data.workScheduleDto;
					self.fixedWorkInformationDto[indexEmp].fixedWorkInforDto = data.fixedWorkInforDto;

					if (data.fixedWorkInforDto.workType == null) return; // kiểm tra có dữ liệu không
					// ver 2
					self.checkEnableSave = true;
					self.checkEnableWork = true;
					// ver 2
					let checkSort = $("#extable-ksu003").exTable('updatedCells');
					if (checkSort.length > 0 && (_.isNil($("#extable-ksu003").data("errors")) || (!_.isNil($("#extable-ksu003").data("errors")) &&
						$("#extable-ksu003").data("errors").length == 0)) && self.checkEnableSave == true) {

						$(".xcell").removeClass("x-error");
						self.enableSave(true);
					}
					dfd.resolve();
				}).fail(function(error) {
					block.invisible();
					self.enableSave(false);
					self.checkCalcSum = false;
					if (error.messageId == "Msg_590") {
						ruler.replaceAt(index, [{ // xóa chart khi là ngày nghỉ
							type: "Flex",
							options: {
								id: `lgc` + index,
								start: -1000,
								end: -1000,
								lineNo: index
							}
						}]);
					}
					if (error.messageId == "Msg_591" || error.messageId == "Msg_590") self.checkOpenDialog = false;
					if ((columnKey == "worktimeCode" || (error.messageId != "Msg_434" && columnKey == "worktypeCode")) && self.checkMes != 10) {
						self.checkMes = 0;
						if (self.timesOfInput > 0 && columnKey === "worktypeCode") return;
						if (self.timesOfInputTime > 0 && columnKey === "worktimeCode") return;
						if (columnKey === "worktypeCode") self.timesOfInput += 1;
						if (columnKey === "worktimeCode") self.timesOfInputTime += 1;
						if (self.checkCloseKsu003 == true) return;
						errorDialog({ messageId: error.messageId }).then(() => {
							self.checkOpenDialog = false;
							self.checkCalcSum = true;
							block.clear();
							let css = model.getCss(index, self.dataScreen003A().targetInfor);

							if (columnKey == "worktypeCode" && error.messageId != "Msg_29" && error.messageId != "Msg_591") {
								$(css.cssWorkType).click();
								$(css.cssWorkType).click();
							}
							// nhập worktypeCode và show ra mess 29 thì focus vào work time code
							if (((columnKey == "worktypeCode" && error.messageId == "Msg_29") ||
								(columnKey == "worktypeCode" && error.messageId == "Msg_591")) && self.checkNeedTime != "OPTIONAL") {
								$(css.cssWorkTime).click();
								$(css.cssWorkTime).click();
							}
							if (columnKey == "worktimeCode" && self.checkNeedTime != "OPTIONAL") {
								$(css.cssWorkTime).click();
								$(css.cssWorkTime).click();
							}
						});
					}

					if (error.messageId == "Msg_29" && columnKey == "worktypeCode") {
						let color = "";
						if (empId === self.employeeIdLogin) {
							color = "#94b7fe";
						} else {
							color = "#cee6ff";
						}
						let cssWorkType: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(1)",
							cssWorkTypeName: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(2)";
						$(cssWorkType).css("background-color", color);
						$(cssWorkTypeName).css("background-color", color);
					}

					if (error.messageId == "Msg_434" && columnKey == "worktypeCode") {
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", "");
						targetOrgDto = [{
							workTypeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktypeCode,
							workTimeCode: $("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode
						}]
						service.getEmpWorkFixedWorkInfo(targetOrgDto).done((data: model.DisplayWorkInfoByDateDto) => {
							self.check045003 = false;
							model.setCellValue(empId);
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", "");
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", data.fixedWorkInforDto.workTypeName);
							model.setDisableCell("disableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
							$(".xcell").removeClass("x-error");
							self.check045003 = false;
						})
					}
					if (self.checkMes == 101) self.checkMes = 0;
				}).always(function() {
					block.clear();
				});
			return dfd.promise();
		}

		// 勤務種類を変更する
		sortEmpByTime(employeeInfo: Array<model.DisplayWorkInfoByDateDto>) {
			let self = this;
			let dataSort = _.map(employeeInfo, (x: model.DisplayWorkInfoByDateDto) => ({
				empId: x.empId,
				empCode: _.filter(self.lstEmpId, y => { return y.empId == x.empId })[0].code,
				startTime: (x.workScheduleDto != null && (x.workScheduleDto.startTime1 != null && x.workScheduleDto.startTime1 != 0)) ? x.workScheduleDto.startTime1 : null
			}))
			return dataSort;
		}

		// 社員を並び替える
		sortEmployee(value: any) {
			block.grayout();
			let self = this;
			let dataSort = self.sortEmpByTime(self.dataScreen003A().employeeInfo);
			if (value === '0') {
				dataSort = _.sortBy(dataSort, ['startTime', 'empCode']);
				self.lstEmpId = self.lstEmpId.sort(function(a: any, b: any) {
					return _.findIndex(dataSort, x => { return x.empId == a.empId }) - _.findIndex(dataSort, x => { return x.empId == b.empId });
				});
				self.dataScreen003A().employeeInfo = self.dataScreen003A().employeeInfo.sort(function(a: any, b: any) {
					return _.findIndex(self.lstEmpId, x => { return x.empId == a.empId }) - _.findIndex(self.lstEmpId, x => { return x.empId == b.empId });
				});
				self.localStore.lstEmpIdSort = self.lstEmpId;
				characteristics.save(self.KEY, self.localStore);
				self.destroyAndCreateGrid(self.lstEmpId, 1);
			} else {
				self.lstEmpId = _.flatMap(self.dataFromA().listEmp, c => [{ empId: c.id, name: c.name, code: c.code }]);
				self.dataScreen003A().employeeInfo = self.dataScreen003A().employeeInfo.sort(function(a: any, b: any) {
					return _.findIndex(self.lstEmpId, x => { return x.empId == a.empId }) - _.findIndex(self.lstEmpId, x => { return x.empId == b.empId });
				});
				self.localStore.lstEmpIdSort = self.lstEmpId;
				characteristics.save(self.KEY, self.localStore);
				self.destroyAndCreateGrid(self.lstEmpId, 1);
			}
			self.localStore.startTimeSort = value;
			characteristics.save(self.KEY, self.localStore);
			block.clear();
		}
		// Convert data for extable
		public convertDataIntoExtable(index?: number) {
			let self = this, disableDs: any = [], leftDs: any = [], middleDs: any = [],
				timeGantChart: Array<model.ITimeGantChart> = [], typeOfTime: string, startTimeArr: any = [];
			let gcFixedWorkTime: Array<model.IFixedFlowFlexTime> = [],
				gcBreakTime: Array<model.IBreakTime> = [],
				gcOverTime: Array<model.IOverTime> = [],
				gcSupportTime: any = null,
				gcFlowTime: Array<model.IFixedFlowFlexTime> = [],
				gcFlexTime: Array<model.IFixedFlowFlexTime> = [],
				gcCoreTime: Array<model.ICoreTime> = [],
				gcHolidayTime: Array<model.IHolidayTime> = [],
				gcShortTime: Array<model.IShortTime> = [];
			_.forEach(_.isNil(index) ? self.dataScreen003A().employeeInfo : [self.dataScreen003A().employeeInfo[index]], (schedule: model.DisplayWorkInfoByDateDto, indx) => {
				gcFixedWorkTime = [], gcBreakTime = [], gcOverTime = [], gcSupportTime = null,
					gcFlowTime = [], gcFlexTime = [], gcCoreTime = [], gcHolidayTime = [],
					gcShortTime = [], typeOfTime = "", self.lstBreakSum = [], self.lstHolidayShort = [];
				let lineNo = _.findIndex(self.lstEmpId, (x) => { return x.empId === schedule.empId; });
				let color = model.setColorEmployee(schedule.workInfoDto.isNeedWorkSchedule, schedule.workInfoDto.isCheering);
				let colorA6 = model.setColorWorkingInfo(schedule.empId, schedule.workInfoDto.isConfirmed
					, schedule.workScheduleDto != null ? schedule.workScheduleDto : null, schedule.workInfoDto.isNeedWorkSchedule);
				if (schedule.workInfoDto.isConfirmed == 0 && schedule.workInfoDto.isNeedWorkSchedule == 0) {
					disableDs.push({
						empId: schedule.empId,
						color: "#ddddd2",
						index: indx
					});
				}
				leftDs.push({
					empId: schedule.empId,
					color: color,
					colorA6: colorA6
				});

				let startTime1 = "", startTime2 = "", endTime1 = "", endTime2 = "";
				if (schedule.workScheduleDto != null) {
					startTime1 = schedule.workScheduleDto.startTime1 != null && schedule.workScheduleDto.startTime1 != 0 ? formatById("Clock_Short_HM", schedule.workScheduleDto.startTime1) : "",
						startTime2 = schedule.workScheduleDto.startTime2 != null ? formatById("Clock_Short_HM", schedule.workScheduleDto.startTime2) : "",
						endTime1 = schedule.workScheduleDto.endTime1 != null && schedule.workScheduleDto.endTime1 != 0 ? formatById("Clock_Short_HM", schedule.workScheduleDto.endTime1) : "",
						endTime2 = schedule.workScheduleDto.endTime2 != null ? formatById("Clock_Short_HM", schedule.workScheduleDto.endTime2) : "";
				}
				let workTypeName = "", workTimeName = "";
				if (schedule.fixedWorkInforDto != null) {
					if (schedule.fixedWorkInforDto.workTypeName != null && schedule.fixedWorkInforDto.workTypeName != "") {
						workTypeName = schedule.fixedWorkInforDto.workTypeName;
					}
					if (schedule.fixedWorkInforDto.workTimeName != null && schedule.fixedWorkInforDto.workTimeName != "") {
						workTimeName = schedule.fixedWorkInforDto.workTimeName;
					}
				}
				if (schedule.workScheduleDto == null && schedule.fixedWorkInforDto == null) {
					workTimeName = getText('KSU003_55');
				}
				if (schedule.workScheduleDto != null && schedule.fixedWorkInforDto != null) {
					// Bind *worktypecode - *worktimecode
					if (schedule.workScheduleDto.workTypeCode == null || schedule.workScheduleDto.workTypeCode == "") {
						workTypeName = getText('KSU003_55');
					} else if (schedule.workScheduleDto.workTypeCode != null && schedule.workScheduleDto.workTypeCode != "" && schedule.fixedWorkInforDto != null
						&& (schedule.fixedWorkInforDto.workTypeName == null || schedule.fixedWorkInforDto.workTypeName == "")) {
						workTypeName = schedule.workScheduleDto.workTypeCode + getText('KSU003_54')
					}
					// set work time name
					if (schedule.workScheduleDto.workTimeCode == null || schedule.workScheduleDto.workTimeCode == "") {
						workTimeName = getText('KSU003_55');
					} else {
						if (schedule.fixedWorkInforDto != null && (schedule.fixedWorkInforDto.workTimeName == "" || schedule.fixedWorkInforDto.workTimeName == null)) {
							workTimeName = schedule.workScheduleDto.workTimeCode + getText('KSU003_54');
						}
					}
				}

				// Thời gian cố định
				// 勤務固定情報　dto．Optional<勤務タイプ>==固定勤務 && 社員勤務情報　dto．応援か＝＝時間帯応援元　or 応援ではない

				if (schedule.workInfoDto != null && schedule.workScheduleDto != null && schedule.fixedWorkInforDto != null) {
					if (schedule.fixedWorkInforDto.workType == model.WorkTimeForm.FIXED &&
						(schedule.workInfoDto.isCheering == model.SupportAtr.TIMEZONE_SUPPORTER || schedule.workInfoDto.isCheering == model.SupportAtr.NOT_CHEERING)) {
						gcFixedWorkTime.push({
							empId: schedule.workInfoDto.employeeId,
							timeNo: 1,
							color: "#ccccff",
							isCheering: schedule.workInfoDto.isCheering,
							workType: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.workType : null,
							startTime: schedule.workScheduleDto.startTime1,
							endTime: schedule.workScheduleDto.endTime1,
							startTimeRange: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.startTimeRange1 : null,
							endTimeRange: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.endTimeRange1 : null

						});
						startTimeArr.add(schedule.workScheduleDto.startTime1);
						// 複数回勤務管理.使用区別＝＝true
						// 社員勤務予定dto.Optional<開始時刻2>, 社員勤務予定dto.Optional<終了時刻2>
						if (self.dataScreen003A().targetInfor == 1) {
							gcFixedWorkTime.push({
								empId: schedule.workInfoDto.employeeId,
								timeNo: 2,
								color: "#ccccff",
								isCheering: schedule.workInfoDto.isCheering,
								workType: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.workType : null,

								startTime: schedule.workScheduleDto.startTime2,
								endTime: schedule.workScheduleDto.endTime2,
								startTimeRange: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.startTimeRange2 : null,
								endTimeRange: schedule.fixedWorkInforDto != null ? schedule.fixedWorkInforDto.endTimeRange2 : null
							});
							startTimeArr.add(schedule.workScheduleDto.startTime2);
						}
						typeOfTime = "Fixed"
					}
				}

				// Tính tổng thời gian làm việc
				let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5);
				if (schedule.fixedWorkInforDto != null && schedule.workScheduleDto != null) {
					// Thời gian break time
					// 勤務固定情報　dto.Optional<休憩時間帯を固定にする>＝falseの時に、休憩時間横棒が生成されない。 (defaut gcBreakTime = [])
					// 勤務固定情報　dto.Optional<休憩時間帯を固定にする>＝trueの時に、社員勤務予定 dto．Optional<List<休憩時間帯>>から休憩時間横棒を生成する。
					if (schedule.fixedWorkInforDto.fixBreakTime == 1) {

						gcBreakTime.push({
							empId: schedule.workInfoDto.employeeId,
							lstBreakTime: schedule.workScheduleDto.listBreakTimeZoneDto,
							color: "#ff9999",
							fixBreakTime: schedule.fixedWorkInforDto.fixBreakTime
						})

						// Tính tổng BREAKTIME khi khởi động
						lstTime = self.calcChartTypeTime(schedule, schedule.workScheduleDto.listBreakTimeZoneDto, timeRangeLimit, lstTime, "BREAK", lineNo);
					}

					// Thời gian làm thêm
					// 勤務固定情報 dto.Optional<List<残業時間帯>>から時間外労働時間横棒を生成する。
					gcOverTime.push({
						empId: schedule.workInfoDto.employeeId,
						lstOverTime: schedule.fixedWorkInforDto.overtimeHours,
						color: "#ffff00"
					})
					// Tính tổng thời gian làm thêm
					lstTime = self.calcChartTypeTime(schedule, schedule.fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", lineNo);

					// Thời gian lưu động 
					// 勤務固定情報　dto．Optional<勤務タイプ>== 流動勤務&&社員勤務情報　dto．応援か＝＝時間帯応援元　or 応援ではない
					if (schedule.fixedWorkInforDto.workType == model.WorkTimeForm.FLOW
						&& (schedule.workInfoDto.isCheering == model.SupportAtr.TIMEZONE_SUPPORTER || schedule.workInfoDto.isCheering == model.SupportAtr.NOT_CHEERING)) {
						gcFlowTime.push({
							timeNo: 1,
							empId: schedule.workInfoDto.employeeId,
							workType: schedule.fixedWorkInforDto.workType,
							color: "#ffc000",
							isCheering: schedule.workInfoDto.isCheering,
							startTime: schedule.workScheduleDto.startTime1,
							endTime: schedule.workScheduleDto.endTime1,
							startTimeRange: schedule.fixedWorkInforDto.startTimeRange1,
							endTimeRange: schedule.fixedWorkInforDto.endTimeRange1

						});
						startTimeArr.add(schedule.workScheduleDto.startTime2);
						// 複数回勤務管理.使用区別＝＝true
						if (self.dataScreen003A().targetInfor == 1) {
							gcFlowTime.push({
								timeNo: 1,
								empId: schedule.workInfoDto.employeeId,
								workType: schedule.fixedWorkInforDto.workType,
								color: "#ffc000",
								isCheering: schedule.workInfoDto.isCheering,
								startTime: schedule.workScheduleDto.startTime2,
								endTime: schedule.workScheduleDto.endTime2,
								startTimeRange: schedule.fixedWorkInforDto.startTimeRange2,
								endTimeRange: schedule.fixedWorkInforDto.endTimeRange2
							});
							startTimeArr.add(schedule.workScheduleDto.startTime2);
						}
						typeOfTime = "Changeable"
					}

					// Thời gian Flex time
					// 勤務固定情報　dto．Optional<勤務タイプ>==フレックス勤務 && 社員勤務情報　dto．応援か＝＝時間帯応援元　or 応援ではない
					if (schedule.fixedWorkInforDto.workType == model.WorkTimeForm.FLEX
						&& (schedule.workInfoDto.isCheering == model.SupportAtr.TIMEZONE_SUPPORTER || schedule.workInfoDto.isCheering == model.SupportAtr.NOT_CHEERING)) {
						// 社員勤務予定dto.Optional<開始時刻1>, 社員勤務予定dto.Optional<終了時刻1>
						gcFlexTime.push({
							timeNo: 1,
							empId: schedule.workInfoDto.employeeId,
							workType: schedule.fixedWorkInforDto.workType,
							color: "#ccccff",
							isCheering: schedule.workInfoDto.isCheering,
							startTime: schedule.workScheduleDto.startTime1,
							endTime: schedule.workScheduleDto.endTime1,
							startTimeRange: schedule.fixedWorkInforDto.startTimeRange1,
							endTimeRange: schedule.fixedWorkInforDto.endTimeRange1
						});
						startTimeArr.add(schedule.workScheduleDto.startTime1);
						typeOfTime = "Flex"
					}

					// Thời gian core time
					// 勤務固定情報　dto．Optional<勤務タイプ>== フレックス勤務 && 勤務固定情報　dto．Optional<コア開始時刻>とOptional<コア終了時刻>が存在する
					if (schedule.fixedWorkInforDto.workType == model.WorkTimeForm.FLEX
						&& schedule.fixedWorkInforDto.coreStartTime != null && schedule.fixedWorkInforDto.coreEndTime != null) {
						gcCoreTime.push({
							empId: schedule.workInfoDto.employeeId,
							color: "#00ffcc",
							workType: schedule.fixedWorkInforDto.workType,
							coreStartTime: schedule.fixedWorkInforDto.coreStartTime,
							coreEndTime: schedule.fixedWorkInforDto.coreEndTime
						});
					}

					// Thời gian holiday time
					// 社員勤務情報　dto．Optional<Map<時間休暇種類, 時間休暇>> 存在する
					if (schedule.workInfoDto.listTimeVacationAndType.length > 0) {
						gcHolidayTime.push({
							empId: schedule.workInfoDto.employeeId,
							color: "#c4bd97",
							listTimeVacationAndType: schedule.workInfoDto.listTimeVacationAndType
						})
					}

					// Tính tổng thời gian holiday time
					for (let e = 0; e < schedule.workInfoDto.listTimeVacationAndType.length; e++) {
						let y = schedule.workInfoDto.listTimeVacationAndType[e];
						lstTime = self.calcChartTypeTime(schedule, y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", lineNo);
					}

					// Thời gian chăm sóc / giữ trẻ (short time)
					// 社員勤務情報　dto．Optional<育児介護短時間> が存在する
					if (schedule.workInfoDto.shortTime != null && schedule.workInfoDto.shortTime.length > 0) {
						gcShortTime.push({
							empId: schedule.workInfoDto.employeeId,
							color: "#6fa527",
							listShortTime: schedule.workInfoDto.shortTime
						})
					}
					// Tính tổng thời gian short time
					lstTime = self.calcChartTypeTime(schedule, schedule.workInfoDto.shortTime, timeRangeLimit, lstTime, "SHORT", lineNo);
				}

				// Tổng thời gian break time
				let brkTotal: any = 0;
				brkTotal = self.calcAllBrk(lstTime);

				brkTotal = formatById("Clock_Short_HM", Math.round(brkTotal * 5));
				if (schedule.workScheduleDto != null) {
					middleDs.push({
						empId: schedule.empId, cert: getText('KSU003_22'),
						worktypeCode: schedule.workScheduleDto.workTypeCode == null ? "" : schedule.workScheduleDto.workTypeCode,
						worktype: workTypeName,
						worktimeCode: schedule.workScheduleDto.workTimeCode == null ? "" : schedule.workScheduleDto.workTimeCode,
						worktime: workTimeName,
						startTime1: startTime1, endTime1: endTime1,
						startTime2: startTime2, endTime2: endTime2,
						totalTime: model.calcAllTime(schedule, lstTime, timeRangeLimit, self.dispStart, self.dispStartHours), breaktime: brkTotal,
						color: colorA6
					});
				} else {
					middleDs.push({
						empId: schedule.empId, cert: getText('KSU003_22'), worktypeCode: "",
						worktype: workTypeName, worktimeCode: "", worktime: workTimeName,
						startTime1: "", endTime1: "",
						startTime2: "", endTime2: "",
						totalTime: "", breaktime: "0:00", color: ""
					});
				}
				// dữ liệu của chart
				timeGantChart.push({
					empId: schedule.empId,
					typeOfTime: typeOfTime,
					gantCharts: self.dataScreen003A().targetInfor,
					gcFixedWorkTime: gcFixedWorkTime,
					gcBreakTime: gcBreakTime,
					gcOverTime: gcOverTime,
					gcSupportTime: gcSupportTime,
					gcFlowTime: gcFlowTime,
					gcFlexTime: gcFlexTime,
					gcCoreTime: gcCoreTime,
					gcHolidayTime: gcHolidayTime,
					gcShortTime: gcShortTime,
					gcTaskTime: schedule.empTaskInfoDto == null ? null : schedule.empTaskInfoDto.taskScheduleDetail
				});
			});
			// tạo dữ liệu để truyền vào init extable
			self.leftDs = leftDs;
			self.disableDs = disableDs;
			self.dataOfGantChart = timeGantChart;
			self.midDataGC = middleDs;
			startTimeArr = _.sortBy(startTimeArr, [function(o: any) { return o; }]);
		}

		// Call to create extable
		initExtableData(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred();
			if (!_.isEmpty(self.leftDs))
				self.initExtableChart(self.dataOfGantChart, self.leftDs, self.midDataGC, self.disableDs).done(() => {
					dfd.resolve();
				});
			return dfd.promise();
		}

		// Destroy and recreate extable
		destroyAndCreateGrid(lstId: any, check: any) {
			let self = this;
			let leftDs: any = [];
			self.taskSaveData = [];
			self.taskPasteData = [];
			self.lstTaskScheduleDetailEmp = [];
			self.lstChartTask = [];
			self.gcShowChart = [];
			leftDs.push({
				empId: _.map(lstId, (x: model.IEmpidName) => { return x.empId }),
				color: ""
			});
			$("#extable-ksu003").children().remove();
			$("#extable-ksu003").removeData();

			if ((self.targetDate() < self.dataFromA().dayEdit && self.dataFromA().dayEdit !== "1900/01/01") ||
				(self.targetDate() < moment(new Date()).format('YYYY/MM/DD') && self.dataFromA().dayEdit !== "1900/01/01")) {
				self.checkDisByDate = false;
			} else {
				self.checkDisByDate = true;
				self.dataScreen003A().canModified = 1;
			}
			
			if (self.selectedDisplayPeriod() == 2) {
				self.dataToAb = {
					targetDate: self.targetDate(),
					dataScreen003A: self.dataScreen003A(),
					localStore: self.localStore,
					organizationName: self.organizationName()
				}
				setShared("dataShareAbFromA", self.dataToAb);
				
				__viewContext.viewModel.viewmodelAb.getTaskPallet().done(() => {
					__viewContext.viewModel.viewmodelAb.sourceCompany(__viewContext.viewModel.viewmodelAb.dataSourceCompany()[__viewContext.viewModel.viewmodelAb.selectedPage()]);
					__viewContext.viewModel.viewmodelAb.setDataPalletToButton(__viewContext.viewModel.viewmodelAb.selectedPage(),2);
				});
			}

			if (check == 1) {
				self.initExtableData();
				model.showHide(self.showA9, self.indexBtnToLeft, self.dataScreen003A().targetInfor);

				if (self.selectedDisplayPeriod() != 1) {
					self.subFormatDate(2);
					self.getTask().done(() => {
						if (self.selectedDisplayPeriod() == 2 && self.disableDs.length > 0) {
							for (let i = 0; i < self.disableDs.length; i++) {
								$("#extable-ksu003 > .ex-body-detail > table > tbody tr:nth-child" + "(" + (self.disableDs[i].index + 2).toString() + ")").css("background-color", self.disableDs[i].color);
							}
						}
					});
					__viewContext.viewModel.viewmodelAb.selectedButton.valueHasMutated();
					__viewContext.viewModel.viewmodelAb.taskChecked.valueHasMutated();
					
					block.clear();
				}
			block.clear();
				
			}
			else {
				self.getWorkingByDate(self.targetDate(), 1).done(() => {
					self.convertDataIntoExtable();
					self.initExtableData();
					model.showHide(self.showA9, self.indexBtnToLeft, self.dataScreen003A().targetInfor);

					if (self.selectedDisplayPeriod() != 1) {
						self.subFormatDate(2);
						self.getTask().done(() => {
							if (self.selectedDisplayPeriod() == 2 && self.disableDs.length > 0) {
								for (let i = 0; i < self.disableDs.length; i++) {
									$("#extable-ksu003 > .ex-body-detail > table > tbody tr:nth-child" + "(" + (self.disableDs[i].index + 2).toString() + ")").css("background-color", self.disableDs[i].color);
								}
							}
						});
						__viewContext.viewModel.viewmodelAb.selectedButton.valueHasMutated();
						__viewContext.viewModel.viewmodelAb.taskChecked.valueHasMutated();
						
						block.clear();
					}
					block.clear();

				});
			}
		}

		setShowHideCell(dataMid: any, checkColor: any, middleContentDeco: any, i: any, disableDSFilter: any, detailContentDeco: any) {
			let self = this, lstType = self.dataScreen003A().scheCorrection; // 確定済みか
			let fix = _.filter(lstType, (x: any) => { return x === 0 }),
				flex = _.filter(lstType, (x: any) => { return x === 1 }),
				flow = _.filter(lstType, (x: any) => { return x === 2 });
			// SET COLOR A6
			if (dataMid.color != "" && dataMid.color !== null && self.dataScreen003A().employeeInfo[i].workInfoDto.isNeedWorkSchedule == 1 && self.checkDisByDate != false) {
				if (checkColor.worktypeCode != 0)
					middleContentDeco.push(new model.CellColor("worktypeCode", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.workTypeColor));

				if (checkColor.worktypeName != 0)
					middleContentDeco.push(new model.CellColor("worktypeName", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.workTypeColor));

				if (checkColor.worktimeCode != 0 && dataMid.worktimeCode != "") {
					middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.workTimeColor));
				}

				if (checkColor.worktimeName != 0) {
					middleContentDeco.push(new model.CellColor("worktimeName", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.workTimeColor));
				}

				if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType != null) {
					if ((fix.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED) ||
						(flex.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) ||
						(flow.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW)) {
					} else {
						if (checkColor.startTime1 != 0)
							middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId,
								dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.startTime1Color));

						if (checkColor.endTime1 != 0)
							middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId,
								dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.endTime1Color));

						if (checkColor.startTime2 != 0) {

							if (self.dataScreen003A().employeeInfo[i].workScheduleDto != null && self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 != null &&
								self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 != 0) {
								middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId,
									dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.startTime2Color));
							} else {
								if (dataMid.color.workingInfoColor === "#eccefb") {
									middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "#eccefb"));
								} else {
									middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal"));
								}
							}
						}

						if (checkColor.endTime2 != 0) {
							if (self.dataScreen003A().employeeInfo[i].workScheduleDto != null && self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime2 != null &&
								self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime2 != 0) {
								middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId,
									dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.endTime2Color));
							} else {
								if (dataMid.color.workingInfoColor === "#eccefb") {
									middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "#eccefb"));
								} else {
									middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal"));
								}
							}
						}
					}
				}
				if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == null) {
					middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal"));
					checkColor.startTime2 = 0;
					checkColor.endTime2 = 0;

					middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal"));
					checkColor.startTime1 = 0;
					checkColor.endTime1 = 0;
				}

				if (checkColor.breaktime != 0)
					middleContentDeco.push(new model.CellColor("breaktime", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.breakTimeColor));

				if (checkColor.totalTime != 0)
					middleContentDeco.push(new model.CellColor("totalTime", self.lstEmpId[i].empId,
						dataMid.color.workingInfoColor === "#eccefb" ? "#eccefb" : dataMid.color.totalTimeColor));
			}

			if (disableDSFilter.length > 0) {
				//leftContentDeco.push(new model.CellColor("empName", disableDSFilter[0].empId, "xseal", leftDSFilter[0].color)); // set màu cho emp name khi bị dis
				//leftContentDeco.push(new model.CellColor("cert", disableDSFilter[0].empId, "xseal", leftDSFilter[0].color));
				middleContentDeco.push(new model.CellColor("worktypeCode", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("worktypeName", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("worktimeCode", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("worktimeName", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("startTime1", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("endTime1", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("startTime2", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("endTime2", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("totalTime", disableDSFilter[0].empId, "xseal"));
				middleContentDeco.push(new model.CellColor("breaktime", disableDSFilter[0].empId, "xseal"));
				$("#extable-ksu003").exTable("cellValue", "middle", disableDSFilter[0].empId, "worktimeName", getText('KSU003_55'));

				// set dis for detail DS
				for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
					detailContentDeco.push(new model.CellColor(z.toString() + "_", disableDSFilter[0].empId, disableDSFilter[0].color));
				}
			} else {
				let isNeedWorkSchedule = self.dataScreen003A().employeeInfo[i].workInfoDto.isNeedWorkSchedule,
					canModified = self.dataScreen003A().canModified, // 修正可能
					isConfirmed = self.dataScreen003A().employeeInfo[i].workInfoDto.isConfirmed;

				// set ẩn hiện A6, A7, A8

				if (self.dataScreen003A().employeeInfo[i].workScheduleDto == null || (self.dataScreen003A().employeeInfo[i].workScheduleDto != null &&
					self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 == null)) {
					middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
					checkColor.startTime2 = 0;
					checkColor.endTime2 = 0;
				}

				if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto == null || (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null &&
					self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.isHoliday == false)) {
					middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
					checkColor.startTime2 = 0;
					checkColor.endTime2 = 0;

					middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal", 0));
					checkColor.startTime1 = 0;
					checkColor.endTime1 = 0;
				}

				if (isNeedWorkSchedule != 1) { // ※2 & ※3
					middleContentDeco.push(new model.CellColor("worktypeCode", self.lstEmpId[i].empId, "xseal", 0)); // 4 
					middleContentDeco.push(new model.CellColor("worktypeName", self.lstEmpId[i].empId, "xseal", 0)); // 4
					middleContentDeco.push(new model.CellColor("worktimeName", self.lstEmpId[i].empId, "xseal", 0)); // 4
					checkColor.worktypeCode = 0;
					checkColor.worktypeName = 0;
					checkColor.worktimeName = 0;
					self.lstDis.push({
						empId: self.lstEmpId[i].empId,
						worktypeName: true,
						worktimeName: true
					})

				} else {// [※2]=〇
					if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null &&
						self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType != null &&
						self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType === model.WorkTimeForm.FIXED) { // 10
					}

					if (canModified == 0 || isConfirmed == 1) { // [※3]=〇 ※4 x x 確定済みか
						middleContentDeco.push(new model.CellColor("worktypeCode", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("worktypeName", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("worktimeName", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
						checkColor.worktypeCode = 0;
						checkColor.worktypeName = 0;
						checkColor.worktimeName = 0;
						checkColor.worktimeCode = 0;
						checkColor.startTime2 = 0;
						checkColor.endTime2 = 0;
						checkColor.startTime1 = 0;
						checkColor.endTime1 = 0;
						self.lstDis.push({
							empId: self.lstEmpId[i].empId,
							worktypeName: true,
							worktimeName: true
						})
					}

					if (canModified == 1) { // [※4]=〇 // 修正可能
						if (_.isNil(dataMid.worktimeCode) || dataMid.worktimeCode == "") {
							middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId, "xseal", 0)); // ※9 x
							checkColor.worktimeCode = 0;
							self.lstDis.push({
								empId: self.lstEmpId[i].empId,
								worktypeName: false,
								worktimeName: false
							})
						}
						if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType != null) {
							if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == 0 || // [※5]=〇
								self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == 1 ||
								self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == 2) {
								if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType != null &&
									self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType === model.WorkTimeForm.FLEX) { // ※7 x
									middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
									middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
									checkColor.startTime2 = 0;
									checkColor.endTime2 = 0;
								}
							} else {
								middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
								middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
								checkColor.startTime2 = 0;
								checkColor.endTime2 = 0;
							}
						}
					} else {

						middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
						checkColor.startTime2 = 0;
						checkColor.endTime2 = 0;

						middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal", 0));
						middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal", 0));
						checkColor.startTime1 = 0;
						checkColor.endTime1 = 0;

						middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId, "xseal", 0)); // ※9 x
						checkColor.worktimeCode = 0;
						self.lstDis.push({
							empId: self.lstEmpId[i].empId,
							worktypeName: false,
							worktimeName: false
						});
					}
				}

				if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType != null) {
					if ((fix.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED) ||
						(flex.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) ||
						(flow.length == 0 && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW)) {
						middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal"));
						middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal"));
						checkColor.startTime2 = 0;
						checkColor.endTime2 = 0;

						middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal"));
						middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal"));
						checkColor.startTime1 = 0;
						checkColor.endTime1 = 0;

						for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
							detailContentDeco.push(new model.CellColor(z.toString() + "_", self.dataScreen003A().employeeInfo[i].empId, "xseal"));
						}
					}
				}

				if (self.checkDisByDate == false) {
					middleContentDeco.push(new model.CellColor("worktypeCode", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("worktypeName", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("worktimeName", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("totalTime", self.lstEmpId[i].empId, "xseal"));
					middleContentDeco.push(new model.CellColor("breaktime", self.lstEmpId[i].empId, "xseal"));

					for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
						detailContentDeco.push(new model.CellColor(z.toString() + "_", self.lstEmpId[i].empId, "xseal"));
					}
				}
			}
		}

		createLeftAndMidExtable(timeGantChart: Array<model.ITimeGantChart>, leftDs: any, midData: any, disableDs: any, value: any) {
			let self = this, dfd = $.Deferred(), totalBreakTime = "0:00";
			let middleContentDeco: any = [], leftContentDeco: any = [], detailContentDeco: any = [];

			// left grid area
			let leftmostColumns = [], leftmostHeader = {}, leftmostContent = {}, disableDSFilter: any = [], leftmostDs = [], middleDs = [];

			leftmostColumns = [{
				key: "empName",
				icon: { for: "body", class: "icon-leftmost", width: "25px" },
				headerText: getText('KSU003_20'), width: "160px", control: "link"
			}, {
				key: "cert", headerText: getText('KSU003_21'), width: "40px", control: "button", handler: function(e: any) {
					self.openKdl045Dialog(e.empId)
				}
			}];

			leftmostHeader = {
				columns: leftmostColumns,
				rowHeight: "33px",
				width: "200px"
			};

			for (let i = 0; i < self.lstEmpId.length; i++) {
				let dataLeft: any = _.filter(self.lstEmpId, (x: any) => { return x.empId === self.lstEmpId[i].empId }),
					datafilter = _.filter(midData, (x: any) => { return x.empId === self.lstEmpId[i].empId }),
					dataMid = datafilter[0], eName = "";
				eName = dataLeft[0].code + " " + dataLeft[0].name;
				totalBreakTime = _.isNil(dataMid) ? "0:00" : dataMid.breaktime;
				let leftDSFilter = _.filter(leftDs, (x: any) => { return x.empId === self.lstEmpId[i].empId });
				disableDSFilter = _.filter(disableDs, (x: any) => { return x.empId === self.lstEmpId[i].empId }); // list không dùng schedule

				leftmostDs.push({ empId: self.lstEmpId[i].empId, empName: eName, cert: getText('KSU003_22') });
				if (!_.isEmpty(leftDSFilter)) {
					leftContentDeco.push(new model.CellColor("empName", leftDSFilter[0].empId, leftDSFilter[0].color)); // set màu cho emp name
					self.selectedDisplayPeriod() == 1 ? leftContentDeco.push(new model.CellColor("cert", leftDSFilter[0].empId, leftDSFilter[0].color)) :
						leftContentDeco.push(new model.CellColor("cert", leftDSFilter[0].empId, "xseal", "dis-color"));
				};

				let checkColor = ({
					worktypeCode: 1, worktypeName: 1, worktimeCode: 1, worktimeName: 1, startTime1: 1,
					endTime1: 1, startTime2: 1, endTime2: 1, totalTime: 1, breaktime: 1
				});

				if (disableDSFilter.length > 0) {
					leftContentDeco.push(new model.CellColor("empName", disableDSFilter[0].empId, "xseal", leftDSFilter[0].color)); // set màu cho emp name khi bị dis
					leftContentDeco.push(new model.CellColor("cert", disableDSFilter[0].empId, "xseal", leftDSFilter[0].color));
				}
				if (self.selectedDisplayPeriod() == 1)
					self.setShowHideCell(dataMid, checkColor, middleContentDeco, i, disableDSFilter, detailContentDeco);
				else {
					middleContentDeco.push(new model.CellColor("worktypeCode", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("worktypeName", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("worktimeName", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("worktimeCode", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("startTime1", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("endTime1", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("startTime2", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("endTime2", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("totalTime", self.lstEmpId[i].empId, "xseal", 0));
					middleContentDeco.push(new model.CellColor("breaktime", self.lstEmpId[i].empId, "xseal", 0));
				}

				middleDs.push({
					empId: dataMid.empId, worktypeCode: dataMid.worktypeCode,
					worktypeName: dataMid.worktype, worktimeCode: dataMid.worktimeCode, worktimeName: disableDSFilter.length > 0 ? "" : dataMid.worktime,
					startTime1: dataMid.startTime1 == "0:00" ? "" : dataMid.startTime1, endTime1: dataMid.endTime1 == "0:00" ? "" : dataMid.endTime1,
					startTime2: dataMid.startTime2 == "0:00" ? "" : dataMid.startTime2, endTime2: dataMid.endTime2 == "0:00" ? "" : dataMid.endTime2,
					totalTime: dataMid.totalTime, breaktime: disableDSFilter.length > 0 ? "" : totalBreakTime
				});
			}

			leftmostContent = {
				columns: leftmostColumns,
				dataSource: leftmostDs,
				primaryKey: "empId",
				features: [{
					name: "BodyCellStyle",
					decorator: leftContentDeco
				}]
			};
			// Phần middle
			let middleColumns = [], middleHeader = {}, middleContent = {};

			// Phần column middle
			middleColumns = model.createColumnMid();

			// create header for mid extable
			middleHeader = {
				columns: middleColumns,
				width: self.dataScreen003A().targetInfor == 1 ? "399px" : "316px",
				rowHeight: "33px"
			};

			// create data for mid extable
			middleContent = {
				columns: middleColumns,
				dataSource: middleDs,
				primaryKey: "empId",
				features: [{
					name: "BodyCellStyle",
					decorator: middleContentDeco
				}, {
					name: "Click",
					handler: function(ui: any) {
						if (ui.columnKey == "worktypeName" || ui.columnKey == "worktimeName") {
							self.openKdl003Dialog($("#extable-ksu003").exTable('dataSource', 'middle').body[ui.rowIdx].worktimeCode,
								$("#extable-ksu003").exTable('dataSource', 'middle').body[ui.rowIdx].worktypeCode, self.lstEmpId[ui.rowIdx].empId, ui.columnKey == "worktypeName" ? "WorkTypeName" : "WorkTimeName");
						}
					}
				}]
			};
			if (value == 1) {
				$("#extable-ksu003").exTable("updateTable", "leftmost", {}, leftmostContent);
				$("#extable-ksu003").exTable("updateTable", "middle", {}, middleContent);
			}

			let data: any = {
				middleHeader: middleHeader,
				middleContent: middleContent,
				detailContentDeco: detailContentDeco,
				leftmostHeader: leftmostHeader,
				leftmostContent: leftmostContent,
			}

			return data;
		}

		// Create EXTABLE-GANTCHART
		initExtableChart(timeGantChart: Array<model.ITimeGantChart>, leftDs: any, midData: any, disableDs: any, type?: any): JQueryPromise<any> {
			let self = this, dfd = $.Deferred(), displayRange = self.timeRange, startHours = self.dispStartHours,
				range = displayRange + self.dispStartHours, index = 1, width = "42px", detailColumns: any = [];

			let dataCre = self.createLeftAndMidExtable(timeGantChart, leftDs, midData, disableDs, 0);

			// detail extable
			let detailHeaders: any = {}, detailHeaderDs: any = [], detailContent = {}, detailHeader = {};

			// Phần detail
			detailHeaderDs = [{ empId: "" }];
			// A8_2 TQP
			detailColumns = [{ key: "empId", width: "0px", headerText: "ABC", visible: false }];

			for (let y = startHours; y < range; y++) {
				//detailColumns.push({ key: (y).toString() + "_", width: width });
				detailColumns[index++] = { key: (y).toString() + "_", width: width, headerText: "", visible: true };
				detailHeaderDs[0][y + "_"] = y.toString();
				detailHeaders[y + "_"] = "";
			}

			detailHeader = {
				columns: detailColumns,
				dataSource: detailHeaderDs,
				rowHeight: "33px",
				width: "1008px"
			};

			let detailContentDs = [];
			for (let z = 0; z < self.lstEmpId.length; z++) {
				let datafilter = _.filter(timeGantChart, (x: any) => { return x.empId === self.lstEmpId[z].empId });
				detailContentDs.push({ empId: datafilter[0].empId, ...detailHeaders });
			}

			if (detailColumns.length < 24) {
				model.addColumn(1, self.dispStartHours, detailColumns, detailHeaderDs, detailHeaders, width);
				if (self.timeRange > 24) {
					model.addColumn(25, self.dispStartHours, detailColumns, detailHeaderDs, detailHeaders, width);
				}
			}

			detailContent = {
				columns: detailColumns,
				dataSource: detailContentDs,
				primaryKey: "empId",
				features: [{
					name: "BodyCellStyle",
					decorator: dataCre.detailContentDeco
				}, {
					name: "RightClick",
					chartFilter: function() {
						return true;
					},
					handler: function(ui: any) {
						if (self.selectedDisplayPeriod() == 1) return;
						if (!_.includes(ui.id, "_") && ui.id.length < 10) return;
						if (ui.target.innerText == "") return;
						let items = [
							{
								id: "終日に拡大", text: "終日に拡大", selectHandler: function(id: any) {
									self.pasteAll(ui.rowIndex, ui.id);
								}
							},
							{
								id: "削除", text: "削除", selectHandler: function(id: any) {
									ruler.removeBy({ no: ui.rowIndex, id: ui.id });
									self.deleteTask(ui.rowIndex, ui.id);
								}
							}
						];

						ui.contextMenu(items);
						ui.contextMenu("show");
					}
				}]
			};

			if (detailColumns.length < 24) {
				for (let z = 0; z < self.lstEmpId.length; z++) {
					let datafilter = _.filter(timeGantChart, (x: any) => { return x.empId === self.lstEmpId[z].empId });
					detailContentDs.push({ empId: datafilter[0].empId, ...detailHeaders });
				}
				detailContent = {
					columns: detailColumns,
					dataSource: detailContentDs,
					primaryKey: "empId",
					features: [{
						name: "BodyCellStyle",
						decorator: dataCre.detailContentDeco
					}]
				};
			}

			let extable = new exTable.ExTable($("#extable-ksu003"), {
				headerHeight: "33px",
				bodyRowHeight: "30px",
				bodyHeight: "400px",
				horizontalSumHeaderHeight: "75px",
				horizontalSumBodyHeight: "140px",
				horizontalSumBodyRowHeight: "20px",
				areaResize: false,
				manipulatorId: self.employeeIdLogin,
				manipulatorKey: "empId",
				bodyHeightMode: "fixed",
				showTooltipIfOverflow: true,
				errorMessagePopup: true,
				windowXOccupation: 40,
				windowYOccupation: 200,
				columnVirtualization: true
			}).LeftmostHeader(dataCre.leftmostHeader).LeftmostContent(dataCre.leftmostContent)
				.MiddleHeader(dataCre.middleHeader).MiddleContent(dataCre.middleContent)
				.DetailHeader(detailHeader).DetailContent(detailContent);

			extable.create();
			ruler = extable.getChartRuler();

			console.log(detailContent);
			// 121227
			self.getTaskInfo().done(() => {
				self.initGantChart(self.dataOfGantChart, self.midDataGC);
				self.selectedDisplayPeriod.valueHasMutated();
			});

			$("#hr-row2").css("width", window.innerWidth - 40 + 'px');
			if (!_.isNil(self.localStore)) {
				if (self.localStore.operationUnit === "0") {
					ruler.setSnatchInterval(1);
				} else if (self.localStore.operationUnit === "1") {
					ruler.setSnatchInterval(2);
				} else if (self.localStore.operationUnit === "2") {
					ruler.setSnatchInterval(3);
				} else if (self.localStore.operationUnit === "3") {
					ruler.setSnatchInterval(6);
				} else if (self.localStore.operationUnit === "4") {
					ruler.setSnatchInterval(12);
				}
			};

			// set height grid theo localStorage đã lưu
			self.setPositionButonDownAndHeightGrid();

			dfd.resolve();
			return dfd.promise();
		}

		initGantChart(timeGantChart: any, midData: any) {
			let self = this;
			self.addTypeOfChart(ruler);

			let lstBreakTime: any = [], lstTimeChart: any = [], totalTimeBrk = 0;
			for (let p = 0; p < self.lstEmpId.length; p++) {
				let datafilter: Array<model.ITimeGantChart> = _.filter(timeGantChart, (x: any) => { return x.empId === self.lstEmpId[p].empId });
				self.addAllChart(datafilter, p, lstBreakTime, midData, "");
			}
			// Thay đổi gant chart khi thay đổi giờ
			let recharge = function(detail: any) {
				let index = detail.rowIndex;
				if (self.checkDisByDate == false || self.dataScreen003A().employeeInfo[index].workInfoDto.isConfirmed == 1)
					return;

				let empId = self.lstEmpId[detail.rowIndex].empId, time = null, timeChart: any = null, timeChart2: any = null, timeRangeLimit = ((self.timeRange * 60) / 5);
				lstTimeChart = _.filter(self.allTimeChart, (x: any) => { return x.empId === empId });
				let dataMid = $("#extable-ksu003").exTable('dataSource', 'middle').body[index];
				if (lstTimeChart.length > 0) {
					if (detail.columnKey === "startTime1" || detail.columnKey === "endTime1" || detail.columnKey === "startTime2" || detail.columnKey === "endTime2")
						if (((dataMid.startTime1 != "" && dataMid.endTime1 != "") && (dataMid.startTime2 != "" && dataMid.endTime2 != "")) ||
							((dataMid.startTime1 != "" && dataMid.endTime1 != "") && (dataMid.startTime2 == "" && dataMid.endTime2 == ""))) {
							if (!_.isNaN(duration.parseString(dataMid.startTime1).toValue()) && !_.isNaN(duration.parseString(dataMid.startTime2).toValue()) && !_.isNaN(duration.parseString(dataMid.endTime1).toValue()) && !_.isNaN(duration.parseString(dataMid.endTime2).toValue())) {
								self.checkOpenDialog = true;
								self.checkErrorTime = true;

								if (_.isEmpty($("#extable-ksu003").data("errors")))
									self.enableSave(true);
								time = duration.parseString(detail.value).toValue();

								if (duration.parseString(dataMid.startTime1).toValue() <= duration.parseString(dataMid.endTime1).toValue() && duration.parseString(dataMid.startTime2).toValue() <= duration.parseString(dataMid.endTime2).toValue()) {
									if (self.checkUpdateTime.id == 0) {
										self.checkTimeInfo(index, dataMid.worktypeCode, dataMid.worktimeCode, dataMid.startTime1,
											dataMid.startTime2, dataMid.endTime1, dataMid.endTime2, detail.columnKey);
									}
								}

								timeChart = lstTimeChart[lstTimeChart.length - 1].timeChart;
								timeChart2 = lstTimeChart[lstTimeChart.length - 1].timeChart2;
								time = _.endsWith(time.toString(), '9') == true ? time - 1 : time;
								if (detail.columnKey === "startTime1") {
									if (time == "") return;
									ruler.extend(detail.rowIndex, `lgc${detail.rowIndex}`, ((time / 5 - self.dispStart) > timeRangeLimit ? timeRangeLimit : ((time / 5 - self.dispStart))));
								} else if (detail.columnKey === "endTime1") {
									if (time == "") return;
									ruler.extend(detail.rowIndex, `lgc${detail.rowIndex}`, null, (time / 5 - self.dispStart));
								} else if (detail.columnKey === "startTime2" && timeChart2 != null) {
									if (time == "") return;
									ruler.extend(detail.rowIndex, `rgc${detail.rowIndex}`, ((time / 5 - self.dispStart) > timeRangeLimit ? timeRangeLimit : ((time / 5 - self.dispStart))));
								} else if (detail.columnKey === "endTime2" && timeChart2 != null) {
									if (time == "") return;
									ruler.extend(detail.rowIndex, `rgc${detail.rowIndex}`, null, (time / 5 - self.dispStart));
								}

							}
						}
				}
				if (_.isNaN(duration.parseString(dataMid.startTime1).toValue()) || _.isNaN(duration.parseString(dataMid.startTime2).toValue()) || _.isNaN(duration.parseString(dataMid.endTime1).toValue()) || _.isNaN(duration.parseString(dataMid.endTime2).toValue())) {
					self.checkErrorTime = false;
					self.checkOpenDialog = false;
					if (!_.isEmpty($("#extable-ksu003").data("errors")))
						self.enableSave(false);
					self.checkFocus.add(detail.columnKey);
				}
			};

			// update cell extable
			$("#extable-ksu003").on("extablecellupdated", (e: any) => {
				recharge(e.detail);
			});

			// update cell khi trở về giá trị cũ
			$("#extable-ksu003").on("extablecellretained", (dataCell: any) => {

				let index: number = dataCell.originalEvent.detail.rowIndex, color = "";
				let dataMid = $("#extable-ksu003").exTable('dataSource', 'middle').body[index];
				let empId = self.lstEmpId[dataCell.originalEvent.detail.rowIndex].empId;
				let dataFixed = _.filter(self.dataScreen003A().employeeInfo, x => { return x.empId === empId }),
					dataFixInfo = _.filter(self.fixedWorkInformationDto, x => { return x.empId === empId });
				let cssWorkType: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(1)",
					cssStartTime1: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(5)",
					cssEndTime1: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(6)",
					cssStartTime2: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)" : "",
					cssEndTime2: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(8)" : "";
				if (self.check045003 == false) return;
				if (self.dataScreen003AFirst[index].workScheduleDto != null && self.dataScreen003AFirst[index].workScheduleDto.workTypeCode == dataMid.worktypeCode &&
					self.dataScreen003AFirst[index].workScheduleDto.startTime1 == dataMid.startTime1 &&
					dataCell.originalEvent.detail.columnKey === "worktypeCode") {
					return;
				}

				if (self.dataScreen003AFirst[index].workScheduleDto != null && self.dataScreen003AFirst[index].workScheduleDto.workTimeCode == dataMid.worktimeCode &&
					self.dataScreen003AFirst[index].workScheduleDto.startTime1 == dataMid.startTime1 &&
					dataCell.originalEvent.detail.columnKey === "worktimeCode") return;

				let checkErr = _.filter($("#extable-ksu003").data("errors"), (x: any) => {
					return x.rowIndex === index;
				})

				let checkErr2 = _.filter(checkErr, (x: any) => {
					return x.columnKey === dataCell.detail.columnKey;
				})

				let columnKey = dataCell.originalEvent.detail.columnKey;
				if ((columnKey === "startTime1" || columnKey === "startTime2" ||
					columnKey === "endTime1" || columnKey === "endTime2") && self.checkUpdateTime.id == 0) {
					// kiểm tra startTime > endTime và time = null
					if ((columnKey === "startTime1" || columnKey === "endTime1") &&
						(dataMid.startTime1 == "" || duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue())) {
						if (self.checkDragDrop == true) return;
						if (self.checkCloseKsu003 == true) return;
						block.invisible();
						self.checkCalcSum = false;
						errorDialog({ messageId: "Msg_54" }).then(() => {
							self.checkCalcSum = true;
							self.enableSave(false);
							self.checkOpenDialog = false;
							block.clear();
							if (columnKey === "startTime1") {
								$(cssStartTime1).click();
								$(cssStartTime1).click();
								if (duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue()) {
									$(cssStartTime1).addClass("x-error");
								}
							}
							if (columnKey === "endTime1") {
								$(cssEndTime1).click();
								$(cssEndTime1).click();
								if (duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue()) {
									$(cssEndTime1).addClass("x-error");
								}
							}
						});
						return;
					}

					// kiểm tra startTime > endTime và time = null
					if ((columnKey === "startTime2" || columnKey === "endTime2") &&
						(dataMid.startTime2 == "" || duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue())) {
						if (self.checkDragDrop == true) return;
						if (self.checkCloseKsu003 == true) return;
						block.invisible();
						self.checkCalcSum = false;
						errorDialog({ messageId: "Msg_54" }).then(() => {
							self.checkCalcSum = true;
							self.checkOpenDialog = false;
							self.enableSave(false);
							block.clear();
							if (columnKey === "startTime2") {
								$(cssStartTime2).click();
								$(cssStartTime2).click();
								if (duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue()) {
									$(cssStartTime2).addClass("x-error");
								}
							}
							if (columnKey === "endTime2") {
								$(cssEndTime2).click();
								$(cssEndTime2).click();
								if (duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue()) {
									$(cssEndTime2).addClass("x-error");
								}
							}

						});
						return;
					}

					if (_.isNaN(duration.parseString(dataMid.startTime1).toValue()) || _.isNaN(duration.parseString(dataMid.startTime2).toValue()) || _.isNaN(duration.parseString(dataMid.endTime1).toValue()) || _.isNaN(duration.parseString(dataMid.endTime2).toValue())) {
						return
					}

					// delete class error when have not Msg_54
					model.removeError("", "", "", "", cssStartTime1, cssEndTime1, cssStartTime2, cssEndTime2, 0);
					self.checkOpenDialog = true;
					if (checkErr2.length > 0 && checkErr.length > 0 && $("#extable-ksu003").data("errors").length > 0 && dataCell.originalEvent.detail.value != "") {
						return;
					}
					self.checkRetained = false;
					// kiểm tra thời gian nhập
					self.checkTimeInfo(index, dataMid.worktypeCode, dataMid.worktimeCode, dataMid.startTime1, dataMid.startTime2, dataMid.endTime1, dataMid.endTime2, columnKey).done(() => {
						let timeConvert = model.convertTime(dataMid.startTime1, dataMid.endTime1, dataMid.startTime2, dataMid.endTime2);
						self.employeeScheduleInfo.forEach((x, i) => {
							if (i === dataCell.originalEvent.detail.rowIndex) {
								if (columnKey === "startTime1") {
									x.startTime1 = timeConvert.start;
									dataFixed[0].workScheduleDto.startTime1 = x.startTime1;
									if (x.startTime1 == "") self.checkMes += 1;
								}
								if (columnKey === "startTime2") {
									x.startTime2 = timeConvert.start2;
									dataFixed[0].workScheduleDto.startTime2 = x.startTime2;
									if (x.startTime2 == "") self.checkMes += 1;
								}
								if (columnKey === "endTime1") {
									x.endTime1 = timeConvert.end;
									dataFixed[0].workScheduleDto.endTime1 = x.endTime1;
									if (x.endTime1 == "") self.checkMes += 1000;
								}
								if (columnKey === "endTime2") {
									x.endTime2 = timeConvert.end2;
									dataFixed[0].workScheduleDto.endTime2 = x.endTime2;
									if (x.endTime2 == "") self.checkMes += 1000;
								}
							}
						})
						if ((timeConvert.start == "" && timeConvert.end != "") || (timeConvert.start != "" && timeConvert.end == "")
							|| (timeConvert.start2 == "" && timeConvert.end2 != "") || (timeConvert.start2 != "" && timeConvert.end2 == "")) {
							self.checkRetained = true;
							return;
						}

						// tính lại tổng time
						if (self.checkCalcSum == false) return;
						let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5), totalBrkTime: any = null;
						self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workScheduleDto.listBreakTimeZoneDto,
							timeRangeLimit, lstTime, "BREAK", index);
						for (let e = 0; e < dataFixed[0].workInfoDto.listTimeVacationAndType.length; e++) {
							let y = dataFixed[0].workInfoDto.listTimeVacationAndType[e];
							lstTime = self.calcChartTypeTime(dataFixed[0], y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", index);
						}
						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workInfoDto.shortTime,
							timeRangeLimit, lstTime, "SHORT", index);

						lstTime = self.calcChartTypeTime(dataFixed[0], dataFixInfo[0].fixedWorkInforDto == null ? [] : dataFixInfo[0].fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", index);
						let totalTime = model.calcAllTime(dataFixed[0], lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);

						totalBrkTime = self.calcAllBrk(lstTime);
						totalBrkTime = totalBrkTime != null ? formatById("Clock_Short_HM", Math.round(totalBrkTime * 5)) : "";

						let cssbreakTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(10)" :
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(8)";

						if (empId === self.employeeIdLogin) {
							color = "rgb(148, 183, 254)";
						} else {
							color = "rgb(206, 230, 255)";
						}

						if ($("#extable-ksu003").exTable('dataSource', 'middle').body[index].breaktime != _.trim(totalBrkTime)) {
							if ($(cssbreakTime).css("background-color") != color) {
								if (totalBrkTime == totalBrkTime + " ") {
									totalBrkTime = _.trim(totalBrkTime);
								} else {
									totalBrkTime = totalBrkTime + " "
								}
							}
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", totalBrkTime);
						}

						if (!self.checkClearTime == false) {
							let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(9)" :
								"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)";

							if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
								$(cssTotalTime).css("background-color", "#ffffff");

							if ($("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode != "" && totalTime == "") totalTime = "0:00";

							$("#extable-ksu003").exTable("cellValue", "middle", empId, "totalTime", totalTime != null ? totalTime : "");
						}
					});
					self.checkRetained = true;
					recharge(dataCell.detail);
					return;
				}

				let format = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/, errors = [];
				if (checkErr2.length > 0 && checkErr.length > 0 && $("#extable-ksu003").data("errors").length > 0 && format.test(dataCell.detail.value) == true) return;

				if ((dataCell.originalEvent.detail.columnKey === "worktypeCode" && dataMid.worktypeCode == "") /*|| (dataCell.originalEvent.detail.columnKey === "worktimeCode" && dataMid.worktimeCode == "")*/) {
					if (dataMid.worktypeName == "" && dataMid.worktimeCode == "") {
						$(cssWorkType).removeClass("x-error");
						return;
					}
					errors.push({
						message: nts.uk.resource.getMessage('Msg_1780', [dataCell.originalEvent.detail.columnKey === "worktypeCode" ? nts.uk.resource.getText("KSU003_24") : nts.uk.resource.getText("KSU003_26")]),
						messageId: "Msg_1780",
						supplements: {}
					});
					self.checkCalcSum = false;
					self.checkOpenDialog = false;
					if (dataMid.worktypeCode == "" && dataCell.originalEvent.detail.columnKey === "worktypeCode") {
						block.invisible();
						bundledErrors({ errors: errors }).then(() => {
							self.checkCalcSum = true;
							self.enableSave(false);
							if (dataMid.worktypeName != "" && dataMid.worktimeCode != "") {
								$(cssWorkType).click();
								$(cssWorkType).click();
							}
							block.clear();
						});
					}
					return;
				}

				self.checkOpenDialog = true;
				if ((dataCell.originalEvent.detail.columnKey === "worktypeCode" && dataMid.worktypeCode != "")) {
					self.changeWorkType(dataCell.originalEvent.detail.columnKey, empId, index);
				};

				if (empId === self.employeeIdLogin) color = "#94b7fe";
				else color = "#cee6ff";

				if (self.checkGetInfo == false && self.checkUpdateMidChart == true) {
					// 勤務種類を変更する (nhập thủ công worktype code)
					if ((dataCell.originalEvent.detail.columnKey === "worktypeCode")) {
						if (self.timesOfInput > 0) self.timesOfInput = 0;
						if (self.timesOfInputTime > 0) self.timesOfInputTime = 0;

						self.checkUpdateTime.name = "worktypeCode";
						self.checkUpdateTime.id = 2;
						self.inputWorkInfo(dataMid, index, dataCell, dataFixed, empId, dataCell.originalEvent.detail.columnKey);
						// Nếu là ngày nghỉ	
						if (dataFixed[0].fixedWorkInforDto != null && dataFixed[0].fixedWorkInforDto.isHoliday != null && dataFixed[0].fixedWorkInforDto.isHoliday == true) {
							self.checkClearTime = false;
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
							ruler.replaceAt(index, [{ // xóa chart khi là ngày nghỉ
								type: "Flex",
								options: {
									id: `lgc` + index,
									start: -1000,
									end: -1000,
									lineNo: index
								}
							}]);
							return;
						}
						// Nếu không phải ngày nghỉ
						if (dataFixed[0].fixedWorkInforDto != null && dataFixed[0].fixedWorkInforDto.isHoliday != null && dataFixed[0].fixedWorkInforDto.isHoliday == false) {
							self.checkMes += 1;
						}
					}

					if (dataCell.originalEvent.detail.columnKey === "worktimeCode") {
						if (self.timesOfInput > 0) self.timesOfInput = 0;
						if (self.timesOfInputTime > 0) self.timesOfInputTime = 0;
						self.checkUpdateTime.name = "worktimeCode";
						self.checkUpdateTime.id = 1;
						self.inputWorkInfo(dataMid, index, dataCell, dataFixed, empId, dataCell.originalEvent.detail.columnKey);
						self.checkMes += 100;
					}
				}
				recharge(dataCell.detail);
			});
		}

		// Save data ksu003
		saveData(type: any): JQueryPromise<any> {
			if (!_.isNil(window.parent) && window.parent.length > 1) {
				setTimeout(function() {
					$(window.parent[1].$('body').contents().find('#btnClose')).click()
				}, 10);
			}
			let self = this, dfd = $.Deferred(), updatedCells = $("#extable-ksu003").exTable("updatedCells"), params = [];
			if (updatedCells.length == 1){
				let checkUpdate = _.filter(self.disableDs, (z : any) => {
					return z.index ==  updatedCells[0].rowIndex && updatedCells[0].value === "なし";
				})
				
				if (checkUpdate.length > 0) {
					updatedCells = [];
				}
			}
			block.grayout();
			// đăng ký với mode bình thường
			if (self.selectedDisplayPeriod() == 1) {
				let dataReg = model.buidDataReg(updatedCells, self.dataScreen003A().targetInfor, self.dataScreen003A().employeeInfo,
					self.employeeIdLogin, self.colorBreak45, self.index045);
				service.regWorkSchedule(dataReg).done((rs: any) => {
                    self.taskId = rs.taskInfor.id;
                    self.checkStateAsyncTaskNormalMode();
                    
				}).fail(function(error: any) {
					block.clear();
					alertError(error);
					dfd.reject();
				});
			} else {
				_.forEach(self.taskPasteData.lstTaskScheduleDetailEmp, (x: any, index) => {
					self.taskPasteData.lstTaskScheduleDetailEmp[index].taskScheduleDetail = _.uniqWith(x.taskScheduleDetail, (arrV: any, newV: any) => {
						return arrV.timeSpanForCalcDto.start == newV.timeSpanForCalcDto.start && arrV.timeSpanForCalcDto.end == newV.timeSpanForCalcDto.end;
					})
				})
				self.taskPasteData.lstTaskScheduleDetailEmp = _.map(self.taskPasteData.lstTaskScheduleDetailEmp, (x : any) => {
					let taskScheduleDetail = _.map(x.taskScheduleDetail, (y : any) => {
						return {
							taskCode: y.taskCode,
							timeSpanForCalcDto : {
								start: y.timeSpanForCalcDto.start, 
								end:  y.timeSpanForCalcDto.end
							}
						}
					})
					return {
						empId : x.empId,
						taskScheduleDetail : taskScheduleDetail
					}
				})
				service.addTaskWorkSchedule(self.taskPasteData).done((rs: any) => {
                    if (type != 1) {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        block.grayout();
                        self.destroyAndCreateGrid(self.lstEmpId, 0);
                        //self.getTask();
                    }
                    block.grayout();
                    self.taskSaveData = [];
                    self.taskPasteData = [];
                    self.lstTaskScheduleDetailEmp = [];
                    self.lstChartTask = [];
                    self.enableSave(false);

                    //block.clear();
                }).fail(function(error: any) {
                    block.clear();
                    alertError(error);
                    dfd.reject();
                });
            }

            dfd.resolve();
            return dfd.promise();
		}

        checkStateAsyncTaskNormalMode(type : any) {
            let self = this;
            nts.uk.deferred.repeat(conf => conf
                .task(() => {
                    return nts.uk.request.asyncTask.getInfo(self.taskId).done(function(res: any) {
                        // finish task
                        if (res.succeeded || res.failed || res.cancelled) {
                            let arrayItems = [];
                            let dataResult: any = {};
                            dataResult.listErrorInfo = [];
                            dataResult.hasError = false;
                            dataResult.isRegistered = true; 
                            _.forEach(res.taskDatas, item => {
                                if (item.key == 'STATUS_REGISTER') {
                                    dataResult.isRegistered = item.valueAsBoolean;
                                } else if (item.key == 'STATUS_ERROR') {
                                    dataResult.hasError = item.valueAsBoolean;
                                } else {
                                    arrayItems.push(item);
                                }
                            });
                            
                            if (arrayItems.length > 0) {
                                let listErrorInfo = _.map(arrayItems, obj2 => {
                                    return new model.InforError(JSON.parse(obj2.valueAsString));
                                });
                                dataResult.listErrorInfo = listErrorInfo;
                            }
                            
                            block.clear();
                            self.colorBreak45 = true
                            if (dataResult.hasError == false) {
                                if (type != 1) {
                                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                        block.grayout();
                                        self.enableSave(false);
                                        let $grid = $('div.ex-body-detail');
                                        self.updateAfterSaveData($grid[0]).done(() => {
                                            self.destroyAndCreateGrid(self.lstEmpId, 0);
                                        });
                                    });
                                } else {
                                    self.enableSave(false);
                                    let $grid = $('div.ex-body-detail');
                                    self.updateAfterSaveData($grid[0]).done(() => {
                                        self.destroyAndCreateGrid(self.lstEmpId, 0);
                                    });
                                }
                            } else {
                                setTimeout(function() {
                                    self.openKDL053(dataResult);
                                }, 500);
                                block.clear();
                            }
                        }
                    });
                }).while(infor => {
                    return infor.pending || infor.running;
                }).pause(1000));    
        }

		updateAfterSaveData($grid: HTMLElement): JQueryPromise<any> {
			let self = this, dfd = $.Deferred();
			// Clear states
			$.data($grid, "copy-history", null);
			$.data($grid, "redo-stack", null);
			$.data($grid, "edit-history", null);
			$.data($grid, "edit-redo-stack", null);
			$.data($grid, "stick-history", null);
			$.data($grid, "stick-redo-stack", null);

			// remove cells updated
			$('#extable-ksu003').data('extable').modifications = null;
			dfd.resolve();
			return dfd.promise();
		}
		
		checkTimeRange(start : any, end : any, startRangeStart :any,startRangeEnd :any, endRangeStart: any, endRangeEnd: any){
			
			if (start >= startRangeStart && start <= startRangeEnd && end >= endRangeStart && end <= endRangeEnd) {
				return true;
			}
			
			return false;
		}

		// ADD CHART
		addAllChart(datafilter: Array<model.ITimeGantChart>, i: number, lstBreakTime: any, screen: string, lstBrkNew?: any) {
			let self = this, fixedGc: any = [], timeRangeLimit = ((self.timeRange * 60) / 5 + self.dispStart);
			let timeChart: any = null, timeChart2: any = null, lgc = null, rgc = null, timeChartOver: any = null, timeChartCore: any = null,
				timeChartBrk: any = null, timeChartHoliday: any = null, timeChartShort: any = null, indexLeft = 0, indexRight = 0,
				timeMinus: any = [], timeMinus2: any = [], start1 = null, start2 = null, end1 = null, end2 = null, dispStart = (self.dispStartHours * 60) / 5,
				fixedString = "None", slide = true, follow = true, isConfirmed = self.dataScreen003A().employeeInfo[i].workInfoDto.isConfirmed, isFixBr = 0, sliceBrk = true,
				lstType = self.dataScreen003A().scheCorrection,
				fixCheck = _.filter(lstType, (x: any) => { return x === 0 }),
				flexCheck = _.filter(lstType, (x: any) => { return x === 1 }),
				flowCheck = _.filter(lstType, (x: any) => { return x === 2 });
			if (self.checkDisByDate == false || isConfirmed == 1) {
				fixedString = "Both";
				slide = false;
				sliceBrk = false;
			}

			if (self.dataScreen003A().employeeInfo[i].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.fixBreakTime == 1) {
				if (datafilter[0].typeOfTime === "Changeable" || datafilter[0].typeOfTime === "Flex") {
					follow = false;
				}
			}

			if (datafilter != null) {
				if (datafilter[0].typeOfTime != "" && datafilter[0].typeOfTime != null) {
					if (datafilter[0].typeOfTime === "Fixed" && _.isEmpty(fixCheck)) {
						fixedString = "Both";
						slide = false;
						sliceBrk = false;
					}

					if (datafilter[0].typeOfTime === "Changeable" && _.isEmpty(flowCheck)) {
						fixedString = "Both";
						slide = false;
						sliceBrk = false;
					}

					if (datafilter[0].typeOfTime === "Flex" && _.isEmpty(flexCheck)) {
						fixedString = "Both";
						slide = false;
						sliceBrk = false;
					}
					// add chart for FIXED TIME - thời gian cố định
					if (datafilter[0].typeOfTime === "Fixed" || datafilter[0].gcFixedWorkTime.length > 0) {
						if (_.isEmpty(fixCheck))
							isFixBr = 1;
						else
							isFixBr = 0;

						let fixed = datafilter[0].gcFixedWorkTime;
						timeChart = model.convertTimeToChart(fixed[0].startTime, fixed[0].endTime);
						timeMinus.push({
							startTime: fixed[0].startTime,
							endTime: fixed[0].endTime
						})
						let limitTime = model.checkLimitTime(fixed, timeRangeLimit, 0, self.dispStartHours)

						if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && timeMinus[0].startTime < 1440 && timeMinus[0].startTime != null ||
							self.timeRange === 48 && timeMinus[0].startTime < 2880 && timeMinus[0].startTime != null)) {
							start1 = model.checkTimeChart(timeChart.startTime, timeRangeLimit, self.dispStartHours);
							end1 = model.checkTimeChart(timeChart.endTime, timeRangeLimit, self.dispStartHours);
						}
						let limitStartMin = (isConfirmed == 1 || isFixBr == 1) ? start1 : limitTime.limitStartMin,
							limitStartMax = (isConfirmed == 1 || isFixBr == 1) ? start1 : limitTime.limitStartMax,
							limitEndMin = (isConfirmed == 1 || isFixBr == 1) ? end1 : limitTime.limitEndMin,
							limitEndMax = (isConfirmed == 1 || isFixBr == 1) ? end1 : limitTime.limitEndMax, fixedFix = fixedString;

						if (start1 - dispStart == limitStartMin - dispStart && limitStartMin - dispStart == limitStartMax - dispStart
							&& end1 - dispStart == limitEndMin - dispStart && limitEndMin - dispStart == limitEndMax - dispStart) {
							fixedFix = "Both";
						}
						let checkRange = self.checkTimeRange(start1 - dispStart, end1 - dispStart, limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart);
						if (start1 != null && checkRange) {
							lgc = ruler.addChartWithType("Fixed", {
								id: `lgc${i}`,
								lineNo: i,
								start: start1 - dispStart,
								end: end1 - dispStart,
								limitStartMin: limitStartMin - dispStart,
								limitStartMax: limitStartMax - dispStart,
								limitEndMin: limitEndMin - dispStart,
								limitEndMax: limitEndMax - dispStart,
								canSlide: false,
								fixed: fixedFix
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "Fixed", `lgc${i}`, { startTime: timeChart.startTime - dispStart, endTime: timeChart.endTime - dispStart }, i, null,
								limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart));
							indexLeft = indexLeft++;
						}

						if (fixed.length > 1 && fixed[1].startTime != null && fixed[1].endTime != null && fixed[1].startTime != 0 && fixed[1].endTime != 0) {
							timeChart2 = model.convertTimeToChart(fixed[1].startTime, fixed[1].endTime);
							timeMinus2.push({
								startTime: fixed[1].startTime,
								endTime: fixed[1].endTime
							})

							if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && timeMinus2[0].startTime < 1440 && timeMinus2[0].startTime != null ||
								self.timeRange === 48 && timeMinus2[0].startTime < 2880 && timeMinus2[0].startTime != null)) {
								start2 = model.checkTimeOfChart(timeChart2.startTime, timeRangeLimit, self.dispStartHours);
								end2 = model.checkTimeOfChart(timeChart2.endTime, timeRangeLimit, self.dispStartHours);
							}
							//lgc = self.addChartWithTypes(ruler, "Fixed", `lgc${i}`, timeChart, i);
							let limitTime = model.checkLimitTime(fixed, timeRangeLimit, 1, self.dispStartHours),
								limitStartMin = (isConfirmed == 1 || isFixBr == 1) ? start2 : limitTime.limitStartMin,
								limitStartMax = (isConfirmed == 1 || isFixBr == 1) ? start2 : limitTime.limitStartMax,
								limitEndMin = (isConfirmed == 1 || isFixBr == 1) ? end2 : limitTime.limitEndMin,
								limitEndMax = (isConfirmed == 1 || isFixBr == 1) ? end2 : limitTime.limitEndMax, fixedFix = fixedString;
							if (start1 - dispStart == limitStartMin - dispStart && limitStartMin - dispStart == limitStartMax - dispStart
								&& end1 - dispStart == limitEndMin - dispStart && limitEndMin - dispStart == limitEndMax - dispStart) {
								fixedFix = "Both";
							}
							if (start2 != null) {
								let checkRange = self.checkTimeRange(start2 - dispStart, end2 - dispStart, limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart);
								if (checkRange) {
									rgc = ruler.addChartWithType("Fixed", {
										id: `rgc${i}`,
										lineNo: i,
										start: start2 - dispStart,
										end: end2 - dispStart,
										limitStartMin: limitStartMin - dispStart,
										limitStartMax: limitStartMax - dispStart,
										limitEndMin: limitEndMin - dispStart,
										limitEndMax: limitEndMax - dispStart,
										canSlide: false,
										fixed: fixedFix
									});
								
									fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "Fixed", `rgc${i}`, { startTime: timeChart2.startTime - dispStart, endTime: timeChart2.endTime - dispStart }, i, null,
									limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart));
									indexRight = indexRight++;
								}
							}
						}
					}
					// add CHANGEABLE TIME - thời gian lưu động
					if (datafilter[0].typeOfTime === "Changeable" || datafilter[0].gcFlowTime.length > 0) {
						let changeable = datafilter[0].gcFlowTime
						if (_.isEmpty(flowCheck)) isFixBr = 1;
						else isFixBr = 0;

						timeChart = model.convertTimeToChart(changeable[0].startTime, changeable[0].endTime);
						timeMinus.push({
							startTime: changeable[0].startTime,
							endTime: changeable[0].endTime
						});
						let limitTime = model.checkLimitTime(changeable, timeRangeLimit, 0, self.dispStartHours);
						if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && timeMinus[0].startTime < 1440 && timeMinus[0].startTime != null ||
							self.timeRange === 48 && timeMinus[0].startTime < 2880 && timeMinus[0].startTime != null)) {
							start1 = model.checkTimeChart(timeChart.startTime, timeRangeLimit, self.dispStartHours);
							end1 = model.checkTimeChartChang(timeChart.endTime, timeRangeLimit, self.dispStartHours);
						};

						if (changeable.length > 1 && changeable[1].startTime != null && changeable[1].endTime != null && changeable[1].startTime != 0 && changeable[1].endTime != 0) {
							timeChart2 = model.convertTimeToChart(changeable[1].startTime, changeable[1].endTime);
							if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && changeable[1].startTime < 1440 && changeable[1].startTime != null ||
								self.timeRange === 48 && changeable[1].startTime < 2880 && changeable[1].startTime != null)) {
								start2 = model.checkTimeChart(timeChart2.startTime, timeRangeLimit, self.dispStartHours);
								end2 = model.checkTimeChartChang(timeChart2.endTime, timeRangeLimit, self.dispStartHours);
							}
						};

						if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && timeMinus[0].startTime < 1440 && timeMinus[0].startTime != null ||
							self.timeRange === 48 && timeMinus[0].startTime < 2880 && timeMinus[0].startTime != null)) {
							let limitStartMin = (isConfirmed == 1 || isFixBr == 1) ? start1 : limitTime.limitStartMin,
								limitStartMax = (isConfirmed == 1 || isFixBr == 1) ? start1 : limitTime.limitStartMax,
								limitEndMin = (isConfirmed == 1 || isFixBr == 1) ? end1 : limitTime.limitEndMin,
								limitEndMax = (isConfirmed == 1 || isFixBr == 1) ? end1 : limitTime.limitEndMax, canSlideFix = slide, fixedFix = fixedString;
							if (start1 - dispStart == limitStartMin - dispStart && limitStartMin - dispStart == limitStartMax - dispStart
								&& end1 - dispStart == limitEndMin - dispStart && limitEndMin - dispStart == limitEndMax - dispStart) {
								fixedFix = "Both";
								canSlideFix = false;
							}
							
							let checkRange = self.checkTimeRange(start1 - dispStart, end1 - dispStart, 0, 1000, 0, 1000);
							if (checkRange){
								lgc = ruler.addChartWithType("Changeable", {
									id: `lgc${i}`,
									lineNo: i,
									start: start1 - dispStart,
									end: end1 - dispStart,
									limitStartMin: 0,
									limitStartMax: 1000,
									limitEndMin: 0,
									limitEndMax: 1000,
									resizeFinished: (b: any, e: any, p: any) => {
										if (self.checkDisByDate == false) return;
									},
									dropFinished: (b: any, e: any) => {
										if (self.checkDisByDate == false) return;
									},
									canSlide: canSlideFix,
									fixed: fixedFix,
									bePassedThrough: false
								});

								fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "Changeable", `lgc${i}`, { startTime: timeChart.startTime - dispStart, endTime: timeChart.endTime - dispStart }, i, null,
									limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart));
								indexLeft = indexLeft++;
								
							}
						}

						if (changeable.length > 1 && changeable[1].startTime != null && changeable[1].endTime != null && changeable[1].startTime != 0 && changeable[1].endTime != 0) {
							//timeChart2 = self.convertTimeToChart(changeable[1].startTime, changeable[1].endTime);
							timeMinus2.push({
								startTime: changeable[1].startTime,
								endTime: changeable[1].endTime
							})
							let limitTime = model.checkLimitTime(changeable, timeRangeLimit, 1, self.dispStartHours);

							if (timeMinus[0].startTime < timeMinus[0].endTime && (self.timeRange === 24 && timeMinus2[0].startTime < 1440 && timeMinus2[0].startTime != null ||
								self.timeRange === 48 && timeMinus2[0].startTime < 2880 && timeMinus2[0].startTime != null)) {
								start2 = model.checkTimeChart(timeChart2.startTime, timeRangeLimit, self.dispStartHours);
								end2 = model.checkTimeChartChang(timeChart2.endTime, timeRangeLimit, self.dispStartHours);
							}
							let limitStartMin = (isConfirmed == 1 || isFixBr == 1) ? start2 : limitTime.limitStartMin,
								limitStartMax = (isConfirmed == 1 || isFixBr == 1) ? start2 : limitTime.limitStartMax,
								limitEndMin = (isConfirmed == 1 || isFixBr == 1) ? end2 : limitTime.limitEndMin,
								limitEndMax = (isConfirmed == 1 || isFixBr == 1) ? end2 : limitTime.limitEndMax, canSlideFix = slide, fixedFix = fixedString;

							if (start1 - dispStart == limitStartMin - dispStart && limitStartMin - dispStart == limitStartMax - dispStart
								&& end1 - dispStart == limitEndMin - dispStart && limitEndMin - dispStart == limitEndMax - dispStart) {
								fixedFix = "Both";
								canSlideFix = false;
							}
							if (start2 != null) {
								let checkRange = self.checkTimeRange(start2 - dispStart, end2 - dispStart, limitStartMin - dispStart, 1000, limitEndMin - dispStart, 1000);
								if (checkRange){
									rgc = ruler.addChartWithType("Changeable", {
										id: `rgc${i}`,
										lineNo: i,
										start: start2 - dispStart,
										end: end2 - dispStart,
										limitStartMin: limitStartMin - dispStart,
										limitStartMax: 1000,
										limitEndMin: limitEndMin - dispStart,
										limitEndMax: 1000,
										resizeFinished: (b: any, e: any, p: any) => {
											if (self.checkDisByDate == false) return;
										},
										dropFinished: (b: any, e: any) => {
											if (self.checkDisByDate == false) return;
										},
										canSlide: canSlideFix,
										fixed: fixedFix,
										bePassedThrough: false,
										pruneOnSlide: true
									});
									fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "Changeable", `rgc${i}`, { startTime: timeChart2.startTime - dispStart, endTime: timeChart2.endTime - dispStart }, i, null,
										limitStartMin - dispStart, limitStartMax - dispStart, limitEndMin - dispStart, limitEndMax - dispStart));
									indexRight = indexRight++;
								}
								
							}
						}
					}

					// add FLEXTIME - thời gian flex
					if (datafilter[0].typeOfTime === "Flex" || datafilter[0].gcFlexTime.length > 0) {
						let flex = datafilter[0].gcFlexTime;
						let coreTime = datafilter[0].gcCoreTime;
						if (_.isEmpty(flexCheck)) isFixBr = 1;
						else isFixBr = 0;

						timeChart = model.convertTimeToChart(flex[0].startTime, flex[0].endTime);
						timeMinus.push({
							startTime: flex[0].startTime,
							endTime: flex[0].endTime
						})
						if (coreTime.length > 0) {
							timeChartCore = model.convertTimeToChart(coreTime[0].coreStartTime, coreTime[0].coreEndTime);
						}
						let limitTime = model.checkLimitTime(flex, timeRangeLimit, 0, self.dispStartHours),
							limitStartMin = (isConfirmed == 1 || isFixBr == 1) ? model.checkTimeOfChart(timeChart.startTime, timeRangeLimit, self.dispStartHours) - dispStart : limitTime.limitStartMin - dispStart,
							limitStartMax = (isConfirmed == 1 || isFixBr == 1) ? model.checkTimeOfChart(timeChart.startTime, timeRangeLimit, self.dispStartHours) - dispStart : limitTime.limitStartMax - dispStart,
							limitEndMin = (isConfirmed == 1 || isFixBr == 1) ? model.checkTimeOfChart(timeChart.endTime, timeRangeLimit, self.dispStartHours) - dispStart : limitTime.limitEndMin - dispStart,
							limitEndMax = (isConfirmed == 1 || isFixBr == 1) ? model.checkTimeOfChart(timeChart.endTime, timeRangeLimit, self.dispStartHours) - dispStart : limitTime.limitEndMax - dispStart;

						let timeStart = model.checkTimeChart(timeChart.startTime, timeRangeLimit, self.dispStartHours) - dispStart,
							timeEnd = model.checkTimeChartChang(timeChart.endTime, timeRangeLimit, self.dispStartHours) - dispStart;

						let canSlideFix = slide, fixedFix = fixedString;

						if (start1 - dispStart == limitStartMin && limitStartMin == limitStartMax
							&& end1 - dispStart == limitEndMin && limitEndMin == limitEndMax) {
							fixedFix = "Both";
							canSlideFix = false;
						}

						if (timeMinus[0].startTime < timeMinus[0].endTime && timeChart.startTime < timeRangeLimit) {
							let checkRange = self.checkTimeRange(timeStart, timeEnd, limitStartMin, limitStartMax, limitEndMin, limitEndMax);
							if (checkRange){
								lgc = ruler.addChartWithType("Flex", {
								id: `lgc${i}`,
								lineNo: i,
								start: timeStart,
								end: timeEnd,
								limitStartMin: limitStartMin,
								limitStartMax: limitStartMax,
								limitEndMin: limitEndMin,
								limitEndMax: limitEndMax,
								resizeFinished: (b: any, e: any, p: any) => {
									if (self.checkDisByDate == false) return;
								},
								dropFinished: (b: any, e: any) => {
									if (self.checkDisByDate == false) return;
								},
								canSlide: canSlideFix,
								fixed: fixedFix,
								pruneOnSlide: true
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "Flex", `lgc${i}`, { startTime: timeStart, endTime: timeEnd }, i, null,
								limitStartMin, limitStartMax, limitEndMin, limitEndMax));
							indexLeft = ++indexLeft;
							// Add CORETIME
							if (coreTime.length > 0 && timeChart != null && (_.inRange(coreTime[0].coreStartTime, timeMinus[0].startTime, timeMinus[0].endTime) ||
								_.inRange(coreTime[0].coreEndTime, timeMinus[0].startTime, timeMinus[0].endTime))) {
								ruler.addChartWithType("CoreTime", {
									id: `lgc${i}_` + indexLeft,
									parent: `lgc${i}`,
									lineNo: i,
									start: timeChartCore.startTime - dispStart,
									end: timeChartCore.endTime - dispStart,
									pin: true
								});
								fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "CoreTime", `lgc${i}_` + indexLeft, { startTime: timeChartCore.startTime - dispStart, endTime: timeChartCore.endTime - dispStart }, i, `lgc${i}`));
								indexLeft = ++indexLeft;
							}
							}
							
						}
					}
					if (timeChart != null) {
						self.allTimeChart.push({
							empId: datafilter[0].empId,
							timeChart: timeChart,
							timeChart2: timeChart2
						})
					}
				};

				// Lần này chưa đối ứng
				/*let suportTime = datafilter[0].gcSupportTime;
				if (suportTime != null) {
				}*/
				// add OVERTIME
				let overTime = datafilter[0].gcOverTime;
				if (overTime.length > 0 && timeChart != null && datafilter[0].typeOfTime !== "Changeable" && datafilter[0].typeOfTime != "Flex") {
					for (let o = 0; o < overTime[0].lstOverTime.length; o++) {
						let y = overTime[0].lstOverTime[o];
						timeChartOver = model.convertTimeToChart(y.startTime, y.endTime);
						let id = `lgc${i}_` + indexLeft, parent = `lgc${i}`;
						if ((timeMinus.length > 0 && timeMinus[0].startTime != null && timeMinus[0].endTime != null) &&
							(timeChart.startTime < (timeRangeLimit + self.dispStartHours * 12)) &&
							(_.inRange(timeChartOver.startTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)) ||
								_.inRange(timeChartOver.endTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)))) {
							ruler.addChartWithType("OT", {
								id: id,
								parent: parent,
								lineNo: i,
								start: timeChartOver.startTime - dispStart <= 0 ? dispStart : timeChartOver.startTime - dispStart,
								end: timeChartOver.endTime - dispStart > timeRangeLimit ? timeRangeLimit : timeChartOver.endTime - dispStart
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "OT", id, { startTime: timeChartOver.startTime - dispStart, endTime: timeChartOver.endTime - dispStart }, i, parent, 0, 9999, 0, 9999, 1000));
							indexLeft = ++indexLeft;
						}

						if (((timeMinus2.length > 0 && timeMinus2[0].startTime != null && timeMinus2[0].endTime != null) && (timeMinus2.length > 0 && timeMinus2[0].startTime != 0 && timeMinus2[0].endTime != 0)) &&
							(timeChart2.startTime < (timeRangeLimit - self.dispStartHours * 12)) &&
							(_.inRange(timeChartOver.startTime, 0, (timeRangeLimit - self.dispStartHours * 12)) ||
								_.inRange(timeChartOver.endTime, 0, (timeRangeLimit - self.dispStartHours * 12)))) {
							id = `rgc${i}_` + indexRight, parent = `rgc${i}`;
							ruler.addChartWithType("OT", {
								id: id,
								parent: parent,
								lineNo: i,
								start: timeChartOver.startTime - dispStart <= dispStart ? 0 : timeChartOver.startTime - dispStart,
								end: timeChartOver.endTime - dispStart > timeRangeLimit ? timeRangeLimit : timeChartOver.endTime - dispStart
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "OT", id, { startTime: timeChartOver.startTime - dispStart, endTime: timeChartOver.endTime - dispStart }, i, parent, 0, 9999, 0, 9999, 1000));
							indexRight = ++indexRight;
						}
					}
				};
				let startTime1 = 0, endTime1 = 0, breakTime: any = [];
				if (datafilter[0].gcBreakTime.length > 0) {
					breakTime = datafilter[0].gcBreakTime[0].lstBreakTime;
					if (screen === "KDL045") {
						breakTime = lstBrkNew;
						datafilter[0].gcBreakTime[0].lstBreakTime = lstBrkNew;
					}
				}

				// Add BREAK TIME
				if (breakTime.length > 0 && timeChart != null) {
					breakTime = _.sortBy(breakTime, [function(o: any) { return o.start; }])
					for (let o = 0; o < breakTime.length; o++) {
						let y = breakTime[o];
						timeChartBrk = model.convertTimeToChart(_.isNil(y.startTime) ? y.start : y.startTime, _.isNil(y.endTime) ? y.end : y.endTime);
						let parent = `lgc${i}`;
						startTime1 = model.checkTimeOfChart(timeChartBrk.startTime, timeRangeLimit, self.dispStartHours);
						endTime1 = model.checkTimeOfChart(timeChartBrk.endTime, timeRangeLimit, self.dispStartHours);
						const indexBrks = indexLeft;
						self.allTimeBrk.push(self.addChartWithType045(fixedGc, datafilter, "BreakTime", `lgc${i}_` + indexBrks, { startTime: timeChartBrk.startTime - dispStart, endTime: timeChartBrk.endTime - dispStart }, i, parent));
						self.breakChangeCore.push(self.addChartWithType045(fixedGc, datafilter, "BreakTime", `lgc${i}_` + indexBrks, { startTime: timeChartBrk.startTime - dispStart, endTime: timeChartBrk.endTime - dispStart }, i, parent));

						if (((timeChart2 != null) || (timeChart2 == null)) &&
							(timeMinus.length > 0 && timeMinus[0].startTime != null && timeMinus[0].endTime != null) &&
							(timeChart.startTime < (timeRangeLimit + self.dispStartHours * 12)) &&
							(_.inRange(timeChartBrk.startTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)) ||
								_.inRange(timeChartBrk.endTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)))) {
							let timeRange = model.checkRangeBreakTime(self.holidayShort, { start: startTime1, end: endTime1 }, i);
							ruler.addChartWithType("BreakTime", {
								id: `lgc${i}_` + indexBrks,
								parent: parent,
								lineNo: i,
								start: startTime1 - dispStart,
								end: endTime1 - dispStart,
								limitStartMin: isConfirmed == 1 ? startTime1 - dispStart : timeRange.start - dispStart,
								limitStartMax: isConfirmed == 1 ? startTime1 - dispStart : timeRange.end - dispStart,
								limitEndMin: isConfirmed == 1 ? endTime1 - dispStart : timeRange.start - dispStart,
								limitEndMax: isConfirmed == 1 ? endTime1 - dispStart : timeRange.end - dispStart,
								zIndex: 1001,
								canSlide: sliceBrk,
								fixed: "Both",
								followParent: follow,
								pruneOnSlide: true,
								dropFinished: (b: any, e: any) => {
									self.dropBreakTime(i, indexBrks, b, e, slide, fixedString, `lgc${i}_` + indexBrks);
								}
							});
							lstBreakTime.push({
								startTime: timeChartBrk.startTime,
								endTime: timeChartBrk.endTime,
								id: `lgc${i}_` + indexBrks,
								empId: datafilter[0].empId
							})
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "BreakTime", `lgc${i}_` + indexBrks, { startTime: timeChartBrk.startTime - dispStart, endTime: timeChartBrk.endTime - dispStart }, i, parent, timeRange.start, timeRange.end, timeRange.start, timeRange.end, 1001));

							self.gcShowChart.push({
								startTime: timeChartBrk.startTime,
								endTime: timeChartBrk.endTime,
								line: i,
								empId: datafilter[0].empId
							})

							indexLeft = ++indexLeft;
						}

						if (datafilter[0].gantCharts === 1) {
							const indexBrkr = indexRight;
							if ((timeMinus2.length > 0 && timeMinus2[0].startTime != null && timeMinus2[0].endTime != null) &&
								(timeMinus2.length > 0 && timeMinus2[0].startTime != 0 && timeMinus2[0].endTime != 0) &&
								(timeChart2.startTime < (timeRangeLimit + self.dispStartHours * 12)) &&
								(_.inRange(timeChartBrk.startTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)) ||
									_.inRange(timeChartBrk.endTime, self.dispStartHours * 12, (timeRangeLimit + self.dispStartHours * 12)))) {
								let timeRange = model.checkRangeBreakTime(self.holidayShort, { start: startTime1, end: endTime1 }, i);
								ruler.addChartWithType("BreakTime", {
									id: `rgc${i}_` + indexBrkr,
									parent: `rgc${i}`,
									lineNo: i,
									start: startTime1 - dispStart,
									end: endTime1 - dispStart,
									limitStartMin: isConfirmed == 1 ? startTime1 - dispStart : timeRange.start - dispStart,
									limitStartMax: isConfirmed == 1 ? startTime1 - dispStart : timeRange.end - dispStart,
									limitEndMin: isConfirmed == 1 ? endTime1 - dispStart : timeRange.start - dispStart,
									limitEndMax: isConfirmed == 1 ? endTime1 - dispStart : timeRange.end - dispStart,
									zIndex: 1001,
									followParent: follow,
									dropFinished: (b: any, e: any) => {
										self.dropBreakTime(i, indexBrks, b, e, slide, fixedString, `rgc${i}_` + indexBrkr);
									},
									canSlide: sliceBrk,
									fixed: "Both",
									pruneOnSlide: true
								});

								lstBreakTime.push({
									startTime: timeChartBrk.startTime,
									endTime: timeChartBrk.endTime,
									id: `rgc${i}_` + indexBrkr,
									empId: datafilter[0].empId
								})

								self.gcShowChart.push({
									startTime: timeChartBrk.startTime,
									endTime: timeChartBrk.endTime,
									line: i,
									empId: datafilter[0].empId
								})
								if (_.inRange(timeChartBrk.startTime * 5, timeMinus2[0].startTime, timeMinus2[0].endTime))
									fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "BreakTime", `rgc${i}_` + indexBrkr, { startTime: timeChartBrk.startTime - dispStart, endTime: timeChartBrk.endTime - dispStart }, i, `rgc${i}`, 0, 9999, 0, 9999, 1001));
								indexRight = ++indexRight;
							}
						}

					}
				}

				// Add short time
				let shortTime = datafilter[0].gcShortTime;
				if (shortTime.length > 0 && timeChart != null) {
					for (let o = 0; o < shortTime[0].listShortTime.length; o++) {
						let y = shortTime[0].listShortTime[o];
						//timeChartShort = model.convertTimeToChart(y.startTime, y.endTime);
						startTime1 = model.checkTimeOfChart(y.startTime, timeRangeLimit, self.dispStartHours);
						endTime1 = model.checkTimeOfChart(y.endTime, timeRangeLimit, self.dispStartHours);
						let id = `lgc${i}_` + indexLeft, parent = `lgc${i}`;
						if (timeMinus.length > 0 && (_.inRange(y.startTime, timeMinus[0].startTime, timeMinus[0].endTime) ||
							_.inRange(y.endTime, timeMinus[0].startTime, timeMinus[0].endTime))) {
							ruler.addChartWithType("ShortTime", {
								id: id,
								//parent: parent,
								lineNo: i,
								start: y.startTime / 5 - dispStart,
								end: y.endTime / 5 - dispStart,
								zIndex: 1052,
								pin: true,
								bePassedThrough: false
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "ShortTime", id, { startTime: y.startTime / 5 - dispStart, endTime: y.endTime / 5 - dispStart }, i, parent, 0, 9999, 0, 9999, 1052));
							indexLeft = ++indexLeft;
							self.gcShowChart.push({
								startTime: y.startTime / 5,
								endTime: y.endTime / 5,
								line: i,
								empId: datafilter[0].empId
							})
						}

						if (timeMinus2.length > 0 && (_.inRange(y.startTime, timeMinus2[0].startTime, timeMinus2[0].endTime) ||
							_.inRange(y.endTime, timeMinus2[0].startTime, timeMinus2[0].endTime))) {
							id = `rgc${i}_` + indexRight, parent = `rgc${i}`;
							ruler.addChartWithType("ShortTime", {
								id: id,
								//parent: parent,
								lineNo: i,
								start: y.startTime / 5 - dispStart,
								end: y.endTime / 5 - dispStart,
								zIndex: 1052,
								pin: true,
								bePassedThrough: false
							});
							fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "ShortTime", id, { startTime: y.startTime / 5 - dispStart, endTime: y.endTime / 5 - dispStart }, i, parent, 0, 9999, 0, 9999, 1052));
							indexRight = ++indexRight;

							self.gcShowChart.push({
								startTime: y.startTime / 5,
								endTime: y.endTime / 5,
								line: i,
								empId: datafilter[0].empId
							})
						}
					}
				}

				// Add holiday time
				let holidayTime = datafilter[0].gcHolidayTime;
				if (holidayTime.length > 0 && timeChart != null) {
					for (let o = 0; o < holidayTime[0].listTimeVacationAndType.length; o++) {
						let y = holidayTime[0].listTimeVacationAndType[o];
						for (let e = 0; e < y.timeVacation.timeZone.length; e++) {
							let hld = y.timeVacation.timeZone[e];
							timeChartHoliday = model.convertTimeToChart(hld.start, hld.end);

							startTime1 = hld.start; //model.checkTimeOfChart(timeChartHoliday.startTime, timeRangeLimit, self.dispStartHours);
							endTime1 = hld.end;//model.checkTimeOfChart(timeChartHoliday.endTime, timeRangeLimit, self.dispStartHours);
							let id = `lgc${i}_` + indexLeft, parent = `lgc${i}`;

							if (((timeChart2 != null && hld.end < timeChart2.startTime) || (timeChart2 == null)) && (timeMinus.length > 0 && (_.inRange(hld.start, timeMinus[0].startTime, timeMinus[0].endTime) ||
								_.inRange(hld.end, timeMinus[0].startTime, timeMinus[0].endTime)))) {
								if ((self.timeRange === 24 && hld.start < 1440 || self.timeRange === 48 && hld.end < 2880)) {
									ruler.addChartWithType("HolidayTime", {
										id: id,
										//parent: parent,
										lineNo: i,
										start: hld.start / 5 - dispStart,
										end: hld.end / 5 - dispStart,
										zIndex: 1103,
										bePassedThrough: false
									});
									fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "HolidayTime", id, { startTime: timeChartHoliday.startTime - dispStart, endTime: timeChartHoliday.endTime - dispStart }, i, parent, 0, 9999, 0, 9999, 1103));
									indexLeft = ++indexLeft;

									self.gcShowChart.push({
										startTime: hld.start / 5,
										endTime: hld.end / 5,
										line: i,
										empId: datafilter[0].empId
									})
								}
							}

							if (hld.start > timeChart.endTime && timeMinus2.length > 0 && (_.inRange(hld.start, timeMinus2[0].startTime, timeMinus2[0].endTime) ||
								_.inRange(hld.end, timeMinus2[0].startTime, timeMinus2[0].endTime))) {
								id = `rgc${i}_` + indexRight, parent = `rgc${i}`;
								if ((self.timeRange === 24 && hld.start < 1440 || self.timeRange === 48 && hld.end < 2880)) {
									ruler.addChartWithType("HolidayTime", {
										id: id,
										//parent: parent,
										lineNo: i,
										start: hld.start / 5 - dispStart,
										end: hld.end / 5 - dispStart,
										zIndex: 1103,
										bePassedThrough: false
									});
									fixedGc.push(self.addChartWithType045(fixedGc, datafilter, "HolidayTime", id, { startTime: timeChartHoliday.startTime - dispStart, endTime: timeChartHoliday.endTime - dispStart }, i, parent, 0, 9999, 0, 9999, 1103));
									indexRight = ++indexRight;

									self.gcShowChart.push({
										startTime: hld.start / 5,
										endTime: hld.end / 5,
										line: i,
										empId: datafilter[0].empId
									})
								}
							}
						}
					}
				}
				if (self.selectedDisplayPeriod() == 2) {

				}
				// add Task time
				let taskTime = datafilter[0].gcTaskTime;
				if (taskTime != null && taskTime.length > 0) {
					for (let o = 0; o < taskTime.length; o++) {

						if (taskTime[o].taskData == null) {
							let taskInfo: any = {
								data: {
									page: taskTime[o].taskCode,
									text: getText("KSU003_70"),
									tooltip: getText("KSU003_70"),
									color: "#000000"
								}
							}
							self.addTypeOfTask(taskInfo.data.color, taskInfo, false);
						}

						let id = `lgc${i}_` + indexLeft, parent = `lgc${i}`;
						if (((timeChart2 != null && taskTime[o].timeSpanForCalcDto.end / 5 < timeChart2.startTime) || (timeChart2 == null)) &&
							timeMinus.length > 0 && (_.inRange(taskTime[o].timeSpanForCalcDto.start, timeMinus[0].startTime, timeMinus[0].endTime) ||
								_.inRange(taskTime[o].timeSpanForCalcDto.end, timeMinus[0].startTime, timeMinus[0].endTime))) {

							let checkRuler = _.filter(ruler.definedType, (tsk: any) => {
								return _.includes(tsk.name, taskTime[o].taskCode);
							})

							if (checkRuler.length > 0) {
								ruler.addChartWithType(taskTime[o].taskCode + "TASK", {
									name: taskTime[o].taskData == null ? getText("KSU003_70") : taskTime[o].taskData.taskDisplayInfoDto.taskName,
									color: taskTime[o].taskData == null ? "#000000" : taskTime[o].taskData.taskDisplayInfoDto.color,
									lineWidth: 30,
									parent: parent,
									canSlide: false,
									unitToPx: 3.5,
									id: id,
									hide: ruler.loggable(self.selectedDisplayPeriod() == 1 ? false : true),
									canPaste: true,
									canPasteResize: true,
									lineNo: i,
									start: (taskTime[o].timeSpanForCalcDto.start / 5) - dispStart,
									end: (taskTime[o].timeSpanForCalcDto.end / 5) - dispStart,
									pastingResizeFinished: function(line: any, type: any, start: any, end: any) {
										let taskFilter = _.filter(ruler.gcChart[line], (tskF: any) => {
											return tskF.id === id;
										});

										if (_.isNil(start) || _.isNil(end)) {

											if (_.isNil(start))
												start = taskFilter[0].start;

											if (_.isNil(end))
												end = taskFilter[0].end;
										}

										if (_.isNil(start) && _.isNil(end)) {
											start = taskFilter[0].start;
											end = taskFilter[0].end;
										}
										console.log(line + "-" + type + "-" + start + "-" + end);
										self.addTaskResize(line, type, start, end, id);
									}
								});
								indexLeft = ++indexLeft;
								self.lstChartTask.push({
									start: taskTime[o].timeSpanForCalcDto.start,
									end: taskTime[o].timeSpanForCalcDto.end,
									id: id,
									line: i,
									code: taskTime[o].taskCode
								});
							}
						}

						if (timeMinus2.length > 0 && (_.inRange(taskTime[o].timeSpanForCalcDto.start, timeMinus2[0].startTime, timeMinus2[0].endTime) ||
							_.inRange(taskTime[o].timeSpanForCalcDto.end, timeMinus2[0].startTime, timeMinus2[0].endTime))) {
							id = `rgc${i}_` + indexRight, parent = `rgc${i}`;

							let checkRuler = _.filter(ruler.definedType, (tsk: any) => {
								return _.includes(tsk.name, taskTime[o].taskCode);
							})

							if (checkRuler.length > 0) {
								ruler.addChartWithType(taskTime[o].taskCode + "TASK", {
									name: taskTime[o].taskData == null ? getText("KSU003_70") : taskTime[o].taskData.taskDisplayInfoDto.taskName,
									color: taskTime[o].taskData == null ? "#000000" : taskTime[o].taskData.taskDisplayInfoDto.color,
									lineWidth: 30,
									parent: parent,
									canSlide: false,
									unitToPx: 3.5,
									id: id,
									hide: ruler.loggable(self.selectedDisplayPeriod() == 1 ? false : true),
									canPaste: true,
									canPasteResize: true,
									lineNo: i,
									start: (taskTime[o].timeSpanForCalcDto.start / 5) - dispStart,
									end: (taskTime[o].timeSpanForCalcDto.end / 5) - dispStart,
									pastingResizeFinished: function(line: any, type: any, start: any, end: any) {
										let taskFilter = _.filter(ruler.gcChart[line], (tskF: any) => {
											return tskF.id === id;
										});

										if (_.isNil(start) || _.isNil(end)) {

											if (_.isNil(start))
												start = taskFilter[0].start;

											if (_.isNil(end))
												end = taskFilter[0].end;
										}

										if (_.isNil(start) && _.isNil(end)) {
											start = taskFilter[0].start;
											end = taskFilter[0].end;
										}
										console.log(line + "-" + type + "-" + start + "-" + end);
										self.addTaskResize(line, type, start, end, id);
									}
								});
								self.lstChartTask.push({
									start: taskTime[o].timeSpanForCalcDto.start,
									end: taskTime[o].timeSpanForCalcDto.end,
									id: id,
									line: i,
									code: taskTime[o].taskCode
								});
								indexRight = ++indexRight;
							}
						}
					}
				}
			}

			// Resize thanh chart
			$(lgc).on("gcresize", (e: any) => {
				self.gcResize(fixedGc, e, datafilter, i, "lgc", 0);
			});

			$(rgc).on("gcresize", (e: any) => {
				self.gcResize(fixedGc, e, datafilter, i, "rgc", 0);
			});
			// Slide thanh chart
			$(lgc).on("gcdrop", (e: any) => {
				self.checkDrop = true;
				self.gcDrop(e, i, datafilter, "lgc", 0);
				self.checkDrop = false;
			});

			$(rgc).on("gcdrop", (e: any) => {
				self.checkDrop = true;
				self.gcDrop(e, i, datafilter, "rgc", 0);
				self.checkDrop = false;
			});

			self.allGcShow = fixedGc;
		}

		gcResize(fixedGc: any, e: any, datafilter: any, i: any, type: string, checkType: number) {
			let self = this;
			if (self.checkDisByDate == false || self.dataScreen003A().employeeInfo[i].workInfoDto.isConfirmed == 1) return;
			if (_.isEmpty($("#extable-ksu003").data("errors")))
				self.enableSave(true);
			let param = checkType == 0 ? e.detail : e,
				coreTimes = _.filter(fixedGc, (x: any) => { return x.type == "CoreTime" }), coreTime: any = [],
				breakTimes = _.filter(self.breakChangeCore, (x: any) => { return x.type == "BreakTime" }), breakT: any = [],
				indexBrStart = -1, indexBrEnd = -1, checkCore = 0, checkCore2 = 0, lstBrCoreStart: any = [], lstBrCoreEnd: any = [];
			if (coreTimes.length > 0) {
				coreTime = _.filter(coreTimes, (x: any) => { return _.includes(_.isNil(e.target) ? type + i : e.target.id, x.options.parent) });

				if (breakTimes.length > 0) {
					breakT = _.filter(breakTimes, (x: any) => { return _.includes(_.isNil(e.target) ? type + i : e.target.id, x.options.parent) });
				}

				if (breakT.length > 0 && coreTime.length > 0) {
					lstBrCoreStart = _.filter(breakT, (x: any) => { return (coreTime[0].options.start >= x.options.start && coreTime[0].options.end >= x.options.end) });
					lstBrCoreEnd = _.filter(breakT, (x: any) => { return (coreTime[0].options.end <= x.options.end && coreTime[0].options.end >= x.options.start) });
				}
			}
			self.checkDragDrop = false;
			let startMinute = duration.create(param[0] * 5 + self.dispStart * 5).text,
				endMinute = duration.create(param[1] * 5 + self.dispStart * 5).text,
				startTime = param[0] * 5,
				endTime = param[1] * 5;
			if (coreTimes.length > 0) {
				if ((type == "lgc" && param[0] > self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime1 / 5) ||
					(type == "rgc" && param[0] > self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 / 5)) {
					if (coreTime.length > 0 && lstBrCoreStart.length > 0 && param[0] >= lstBrCoreStart[0].options.start) {
						startMinute = duration.create(coreTime[0].options.start * 5 + self.dispStart * 5).text;
						startTime = coreTime[0].options.start * 5;
						checkCore = 1;
					}
				}

				if ((type == "lgc" && param[1] < self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime1 / 5) ||
					(type == "rgc" && param[1] < self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime2 / 5)) {
					if (coreTime.length > 0 && lstBrCoreEnd.length > 0 && param[1] <= lstBrCoreEnd[0].options.start) {
						endMinute = duration.create(coreTime[0].options.end * 5 + self.dispStart * 5).text;
						endTime = coreTime[0].options.end * 5;
						checkCore2 = 2;
					}
				}
			}
			if (param[2]) {
				if (type == "lgc") {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "startTime1", startMinute);
					self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime1 = startTime + self.dispStartHours * 60;
				}
				if (type == "rgc") {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "startTime2", startMinute);
					self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 = startTime + self.dispStartHours * 60;
				}
				if (coreTimes.length > 0) {
					if (lstBrCoreStart.length > 0) {
						indexBrStart = _.findIndex(self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto,
							(x: any) => { return x.start == lstBrCoreStart[0].options.start * 5 && x.end == lstBrCoreStart[0].options.end * 5 });
					}
					let indexAllBr = -1;
					if (indexBrStart != -1 && checkCore == 1) {
						self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexBrStart].start = startTime;
						indexAllBr = _.findIndex(self.allTimeBrk, (x: any) => { return x.options.id === lstBrCoreStart[0].options.id });
						self.allTimeBrk[indexAllBr].options.start = coreTime[0].options.start;
						ruler.gcChart[i][lstBrCoreStart[0].options.id].start = Math.floor(coreTime[0].options.start - self.dispStart * 12);
					}
					if ((indexBrStart == -1 || indexBrStart == 0) && checkCore == 0 && lstBrCoreStart.length > 0) {
						indexAllBr = _.findIndex(self.allTimeBrk, (x: any) => { return x.options.id === lstBrCoreStart[0].options.id });
						let indexNew = _.findIndex(self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto,
							(x: any) => { return self.allTimeBrk[indexAllBr].options.end === x.end / 5 }); // làm tiếp
						self.allTimeBrk[indexAllBr].options.start = self.breakChangeCore[indexAllBr].options.start;
						self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexNew].start = self.breakChangeCore[indexAllBr].options.start * 5;
						let pixelBrk = 0;
						if (param[0] * 5 < self.dataScreen003A().employeeInfo[i].fixedWorkInforDto.coreStartTime) {
							if (param[0] > self.breakChangeCore[indexAllBr].options.start) {
								pixelBrk = param[0];
							} else {
								pixelBrk = self.allTimeBrk[indexAllBr].options.start;
							}
						}
						ruler.extend(i, lstBrCoreStart[0].options.id, Math.floor(pixelBrk - self.dispStart * 12));
					}
				}
			} else {
				if (type == "lgc") {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "endTime1", endMinute);
					self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime1 = endTime + self.dispStartHours * 60;
				}

				if (type == "rgc") {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "endTime2", endMinute);
					self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime2 = endTime + self.dispStartHours * 60;
				}
				if (coreTimes.length > 0) {
					if (lstBrCoreEnd.length > 0) {
						indexBrEnd = _.findIndex(self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto,
							(x: any) => { return x.start == lstBrCoreEnd[0].options.start * 5 && x.end == lstBrCoreEnd[0].options.end * 5 });
					}

					let indexAllBrks = -1;
					if (indexBrEnd != -1 && checkCore2 == 2) {
						self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexBrEnd].end = endTime;
						indexAllBrks = _.findIndex(self.allTimeBrk, (x: any) => { return x.options.id === lstBrCoreEnd[0].options.id });
						self.allTimeBrk[indexAllBrks].options.end = coreTime[0].options.end;
						ruler.gcChart[i][lstBrCoreEnd[0].options.id].end = Math.floor(coreTime[0].options.end - self.dispStart * 12);
					}
					if (indexBrEnd == -1 && checkCore2 == 0 && lstBrCoreEnd.length > 0) {
						indexAllBrks = _.findIndex(self.allTimeBrk, (x: any) => { return x.options.id === lstBrCoreEnd[0].options.id });
						let indexNew = _.findIndex(self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto,
							(x: any) => { return self.allTimeBrk[indexAllBrks].options.start === x.start / 5 });
						self.allTimeBrk[indexAllBrks].options.end = self.breakChangeCore[indexAllBrks].options.end;
						self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexNew].end = self.breakChangeCore[indexAllBrks].options.end * 5;
						ruler.extend(i, lstBrCoreEnd[0].options.id, null, Math.floor(self.allTimeBrk[indexAllBrks].options.end - self.dispStart * 12));
					}
				}
			}
		}

		gcDrop(e: any, i: any, datafilter: any, type: string, checkType: number) {
			let param = checkType == 0 ? e.detail : e, self = this,
				cssbreakTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(10)" :
					"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(8)";
			if (param[0] * 5 == 0) return;

			if (type == "rgc") {
				if (self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 == param[0] * 5 + self.dispStartHours * 60) return;
			} else {
				if (self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime1 == param[0] * 5 + self.dispStartHours * 60) return;
			}

			if (self.checkDisByDate == false || self.dataScreen003A().employeeInfo[i].workInfoDto.isConfirmed == 1)
				return;

			if (_.isEmpty($("#extable-ksu003").data("errors"))) {
				self.enableSave(true);
			} else {
				$("#label-display").trigger("click");
			}

			let startMinute = duration.create(param[0] * 5 + self.dispStart * 5).text,
				endMinute = duration.create(param[1] * 5 + self.dispStart * 5).text,
				index = checkType == 0 ? e.currentTarget.id.split("-")[0] : param[2];
			self.checkDragDrop = true;

			if (type == "lgc") {
				if (param) {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "startTime1", startMinute);
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "endTime1", endMinute);

					self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime1 = param[0] * 5 + self.dispStartHours * 60;
					self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime1 = param[1] * 5 + self.dispStartHours * 60;
				}
			}
			if (type == "rgc") {
				if (param) {
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "startTime2", startMinute);
					$("#extable-ksu003").exTable("cellValue", "middle", datafilter[0].empId, "endTime2", endMinute);

					self.dataScreen003A().employeeInfo[i].workScheduleDto.startTime2 = param[0] * 5 + self.dispStartHours * 60;
					self.dataScreen003A().employeeInfo[i].workScheduleDto.endTime2 = param[1] * 5 + self.dispStartHours * 60;
				}
			}

			let allBrk = [];
			allBrk = _.filter(self.lstAllChildShow, (x: any) => { return x.type == "BREAK" && x.index == i });
			allBrk = _.filter(allBrk, (x: any) => { return x.start >= param[0] && x.start <= param[1] });
			let totalBrk = $("#extable-ksu003").exTable('dataSource', 'middle').body[i].breaktime;

			let dataFixInfo = _.filter(self.fixedWorkInformationDto, x => { return x.empId === self.dataScreen003A().employeeInfo[i].empId });
			if (self.checkCalcSum == false) return;
			let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5);
			self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
			lstTime = self.calcChartTypeTime(self.dataScreen003A().employeeInfo[i], self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto,
				timeRangeLimit, lstTime, "BREAK", index);
			for (let f = 0; f < self.dataScreen003A().employeeInfo[i].workInfoDto.listTimeVacationAndType.length; f++) {
				let y = self.dataScreen003A().employeeInfo[i].workInfoDto.listTimeVacationAndType[f];
				lstTime = self.calcChartTypeTime(self.dataScreen003A().employeeInfo[i], y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", index);
			}
			lstTime = self.calcChartTypeTime(self.dataScreen003A().employeeInfo[i], self.dataScreen003A().employeeInfo[i].workInfoDto.shortTime,
				timeRangeLimit, lstTime, "SHORT", index);

			lstTime = self.calcChartTypeTime(self.dataScreen003A().employeeInfo[i], dataFixInfo[0].fixedWorkInforDto == null ? [] : dataFixInfo[0].fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", index);
			let totalTimes = model.calcAllTime(self.dataScreen003A().employeeInfo[i], lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);
			let totalBreaktime = self.calcAllBrk(lstTime);
			totalBreaktime = totalBreaktime != 0 ? formatById("Clock_Short_HM", Math.round(totalBreaktime * 5)) : "0:00";
			let colorBr = "";
			if (self.dataScreen003A().employeeInfo[i].empId === self.employeeIdLogin) colorBr = "#94b7fe";
			else colorBr = "#cee6ff";

			if (totalBrk != totalBreaktime) $(cssbreakTime).css("background-color", colorBr);

			$("#extable-ksu003").exTable("cellValue", "middle", self.dataScreen003A().employeeInfo[i].empId, "breaktime", totalBreaktime);
			$("#extable-ksu003").exTable("cellValue", "middle", self.dataScreen003A().employeeInfo[i].empId, "totalTime", totalTimes);
			let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(9)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(7)";
			$(cssTotalTime).css("background-color", "#ffffff");

			self.checkDragDrop = false;
		}

		dropBreakTime(i: any, indexBrks: any, b: any, e: any, slide: any, fixed: any, id: any) {
			let self = this, color = "", idNew = indexBrks.length > 5 ? indexBrks : `lgc${i}_` + indexBrks, breakChange = _.filter(self.allTimeBrk, (x: any) => { return x.options.lineNo === i }),
				cssbreakTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(10)" :
					"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(8)";
			breakChange = _.sortBy(breakChange, [function(o: any) { return o.options.start; }]);
			breakChange = _.uniqWith(breakChange, function(arrVal: any, othVal: any) {
				return (arrVal.options.id == othVal.options.id);
			});

			self.allTimeBrk = _.sortBy(self.allTimeBrk, [function(o: any) { return o.options.start; }]);
			self.allTimeBrk = _.uniqWith(self.allTimeBrk, function(arrVal: any, othVal: any) {
				return (arrVal.options.id == othVal.options.id);
			});
			let indexBrk = _.findIndex(self.allTimeBrk, (x: any) => { return x.options.id === (idNew) });
			let indexBrk2 = _.findIndex(breakChange, (x: any) => { return x.options.id === (idNew) });
			if (self.dataScreen003A().employeeInfo[i].empId === self.employeeIdLogin) color = "#94b7fe";
			else color = "#cee6ff";

			if (self.allTimeBrk[indexBrk].options.start == b && self.allTimeBrk[indexBrk].options.end == e) {
				return;
			} else {
				if (_.isEmpty($("#extable-ksu003").data("errors")))
					self.enableSave(true);
				/*breakChange[indexBrk].options.start = b;
				breakChange[indexBrk].options.end = e;*/
				self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexBrk2].start = b * 5 + self.dispStartHours * 60;
				self.dataScreen003A().employeeInfo[i].workScheduleDto.listBreakTimeZoneDto[indexBrk2].end = e * 5 + self.dispStartHours * 60;
				self.allTimeBrk[indexBrk].options.start = b;
				self.allTimeBrk[indexBrk].options.end = e;
				self.breakChangeCore[indexBrk].options.start = b;
				self.breakChangeCore[indexBrk].options.end = e;

				let totalBrk = "";
				if ($("#extable-ksu003").exTable('dataSource', 'middle').body[i].breaktime == $("#extable-ksu003").exTable('dataSource', 'middle').body[i].breaktime + " ") {
					totalBrk = _.trim($("#extable-ksu003").exTable('dataSource', 'middle').body[i].breaktime);
				} else {
					totalBrk = $("#extable-ksu003").exTable('dataSource', 'middle').body[i].breaktime + " "
				}
				$("#extable-ksu003").exTable("cellValue", "middle", self.dataScreen003A().employeeInfo[i].empId, "breaktime", totalBrk); // + " " để phân biệt khi thay đổi vị trí nhưng không thay đổi giá trị
				$(cssbreakTime).css("background-color", color);
				let dataFixed = self.dataScreen003A().employeeInfo[i], lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5);
				self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
				lstTime = self.calcChartTypeTime(dataFixed, dataFixed.workScheduleDto.listBreakTimeZoneDto,
					timeRangeLimit, lstTime, "BREAK", i);
				for (let e = 0; e < dataFixed.workInfoDto.listTimeVacationAndType.length; e++) {
					let y = dataFixed.workInfoDto.listTimeVacationAndType[e];
					lstTime = self.calcChartTypeTime(dataFixed, y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", i);
				}
				lstTime = self.calcChartTypeTime(dataFixed, dataFixed.workInfoDto.shortTime, timeRangeLimit, lstTime, "SHORT", i);
				lstTime = self.calcChartTypeTime(dataFixed, dataFixed.fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", i);
				let totalTime = model.calcAllTime(dataFixed, lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);
				let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(9)" :
					"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (i + 2).toString() + ")" + " > td:nth-child(7)";

				if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
					$(cssTotalTime).css("background-color", "#ffffff");

				$("#extable-ksu003").exTable("cellValue", "middle", self.lstEmpId[i].empId, "totalTime", totalTime != null ? totalTime : "");
			}
		}

		checkChartHide(lstGcShow: any, index: any, param: any, type: string) {
			let self = this, timeMinus = duration.create(param[0] * 5 + self.dispStart * 5).asMinutes - self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime1;
			if (type == "rgc") {
				timeMinus = duration.create(param[0] * 5 + self.dispStart * 5).asMinutes - self.dataScreen003A().employeeInfo[index].workScheduleDto.startTime2;
			}
			if (self.dataScreen003A().employeeInfo[index].workScheduleDto.listBreakTimeZoneDto.length > 0) {
				_.forEach(self.dataScreen003A().employeeInfo[index].workScheduleDto.listBreakTimeZoneDto, (brk: any, idx) => {
					self.dataScreen003A().employeeInfo[index].workScheduleDto.listBreakTimeZoneDto[idx].start += timeMinus;
					self.dataScreen003A().employeeInfo[index].workScheduleDto.listBreakTimeZoneDto[idx].end += timeMinus;
				});
			}
			if (self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.overtimeHours.length > 0) {
				_.forEach(self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.overtimeHours, (brk: any, idx) => {
					self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.overtimeHours[idx].startTime += timeMinus;
					self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.overtimeHours[idx].endTime += timeMinus;
				});
			}
			if (self.dataScreen003A().employeeInfo[index].workInfoDto.shortTime.length > 0) {
				if (self.dataScreen003A().employeeInfo[index].workInfoDto.shortTime.length > 0) {
					_.forEach(self.dataScreen003A().employeeInfo[index].workInfoDto.shortTime, (brk: any, idx) => {
						self.dataScreen003A().employeeInfo[index].workInfoDto.shortTime[idx].startTime += timeMinus;
						self.dataScreen003A().employeeInfo[index].workInfoDto.shortTime[idx].endTime += timeMinus;
					});
				}
			}
			if (self.dataScreen003A().employeeInfo[index].workInfoDto.listTimeVacationAndType.length > 0) {
				_.forEach(self.dataScreen003A().employeeInfo[index].workInfoDto.listTimeVacationAndType, (brks: any, idxs) => {
					_.forEach(self.dataScreen003A().employeeInfo[index].workInfoDto.listTimeVacationAndType[idxs].timeVacation.timeZone, (brk: any, idx) => {
						self.dataScreen003A().employeeInfo[index].workInfoDto.listTimeVacationAndType[idxs].timeVacation.timeZone[idx].start += timeMinus;
						self.dataScreen003A().employeeInfo[index].workInfoDto.listTimeVacationAndType[idxs].timeVacation.timeZone[idx].end += timeMinus;
					})
				})
			}
		}
		// tạo lại chart
		addChartWithType045(fixedGc: any, datafilter: any, type: any, id: any, timeChart: any, lineNo: any, parent?: any,
			limitStartMin?: any, limitStartMax?: any, limitEndMin?: any, limitEndMax?: any, zIndex?: any) {
			let self = this, timeEnd = model.convertTimePixel(self.timeRange === 24 ? "24:00" : "48:00");
			let fixed = "None", canSlide = false, pin = false, followParent = false, rollup = false, roundEdge = false, bePassedThrough = true,
				isConfirmed = self.dataScreen003A().employeeInfo[lineNo].workInfoDto.isConfirmed, pruneOnSlide = false;
			let lstType = self.dataScreen003A().scheCorrection; // 確定済みか
			let fixCheck = _.filter(lstType, (x: any) => { return x === 0 }),
				flexCheck = _.filter(lstType, (x: any) => { return x === 1 }),
				flowCheck = _.filter(lstType, (x: any) => { return x === 2 });

			if (type == "Fixed" || type == "Changeable" || type == "Flex") {

				if (type == "Fixed") {
					canSlide = false;
				}
				if (type == "Changeable") {
					canSlide = true;
					bePassedThrough = false;
				}
				if (type == "Flex") {
					canSlide = true;
					bePassedThrough = false;
				}
				if (self.checkDisByDate == false || isConfirmed == 1) {
					fixed = "Both"
					canSlide = false;
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED && _.isEmpty(fixCheck)) {
					fixed = "Both"
					canSlide = false;
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW && _.isEmpty(flowCheck)) {
					fixed = "Both"
					canSlide = false;
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX && _.isEmpty(flexCheck)) {
					fixed = "Both"
					canSlide = false;
				}
			}

			if (type == "CoreTime" || type == "ShortTime" || type == "HolidayTime") {
				pin = true;
				fixed = "Both";
				if (type == "Holiday" || type == "ShortTime") {
					bePassedThrough = false;
				}
			}

			if (type == "BreakTime") {
				followParent = true;
				pruneOnSlide = true;
				canSlide = true;
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.fixBreakTime == 1) {
					if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW ||
						self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) {
						followParent = false;
					}
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto != null && self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.fixBreakTime == 0) {
					if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW ||
						self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) {
						followParent = true;
					}
				}
				bePassedThrough = false;
				roundEdge = true;
				fixed = "Both";
				rollup = true;
				pin = true;
				if (self.checkDisByDate == false || isConfirmed == 1) {
					canSlide = false;
				}

				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED && _.isEmpty(fixCheck)) {
					canSlide = false;
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW && _.isEmpty(flowCheck)) {
					canSlide = false;
				}
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX && _.isEmpty(flexCheck)) {
					canSlide = false;
				}
			}

			if (type == "OT") {
				rollup = true;
				pin = true;
				fixed = "Both";
				followParent = true;
			}
			return {
				type: type,
				options: {
					id: id,
					start: timeChart.startTime <= timeEnd ? timeChart.startTime : timeEnd,
					end: timeChart.endTime <= timeEnd ? timeChart.endTime : timeEnd,
					lineNo: lineNo,
					parent: parent,
					limitStartMin: limitStartMin,
					limitStartMax: limitStartMax,
					limitEndMin: limitEndMin,
					limitEndMax: limitEndMax,
					zIndex: !_.isNil(zIndex) ? zIndex : 1000,
					canSlide: canSlide,
					fixed: fixed,
					pin: pin,
					followParent: followParent,
					rollup: rollup,
					roundEdge: roundEdge,
					bePassedThrough: bePassedThrough,
					pruneOnSlide: pruneOnSlide,
					resizeFinished: (b: any, e: any, p: any) => {
						if (self.checkDisByDate == false || self.dataScreen003A().employeeInfo[lineNo].workInfoDto.isConfirmed == 1) return;

						if (_.isEmpty($("#extable-ksu003").data("errors")))
							self.enableSave(true);
						self.checkDragDrop = false;
						let param: any = [];
						param.push(b);
						param.push(e);
						param.push(p);
						if (!_.isNil(id) && _.includes(id, 'lgc')) {
							self.gcResize(fixedGc, param, datafilter, lineNo, "lgc", 1);
						} else {
							self.gcResize(fixedGc, param, datafilter, lineNo, "rgc", 1);
						}
					},
					// kéo lại chart
					dropFinished: (b: any, e: any) => {
						if (_.includes(id, '_')) {
							self.dropBreakTime(lineNo, id, b, e, null, fixed, id);
							return;
						};
						let dataDrop: any = [];
						dataDrop.push(b);
						dataDrop.push(e);
						dataDrop.push(lineNo);
						if (!_.isNil(id) && _.includes(id, 'lgc')) {
							self.gcDrop(dataDrop, lineNo, datafilter, "lgc", 1);



						} else {
							self.gcDrop(dataDrop, lineNo, datafilter, "rgc", 1);
						}
					}
				}
			}
		}

		/** ADD TYPE CHART */
		addTypeOfChart(ruler: any) {

			let self = this, fixed = "None";

			if (self.checkDisByDate == false) fixed = "Both";

			self.fixedType = ({
				name: "Fixed",
				color: ruler.loggable("#ccccff"),
				lineWidth: 30,
				canSlide: false,
				unitToPx: self.operationUnit(),
				fixed: fixed,
				canPaste: true
			});
			ruler.addType(self.fixedType)

			self.changeableType = ({
				name: "Changeable",
				color: ruler.loggable("#ffc000"),
				lineWidth: 30,
				canSlide: self.checkDisByDate == false ? false : true,
				unitToPx: self.operationUnit(),
				fixed: fixed,
				bePassedThrough: false, // 114290
				canPaste: true
			});
			ruler.addType(self.changeableType);

			self.flexType = ({
				name: "Flex",
				color: ruler.loggable("#ccccff"),
				lineWidth: 30,
				canSlide: self.checkDisByDate == false ? false : true,
				unitToPx: self.operationUnit(),
				fixed: fixed,
				canPaste: true
			});
			ruler.addType(self.flexType);

			self.breakType = ({
				name: "BreakTime",
				followParent: true, // đi theo chart cha khi kéo
				color: ruler.loggable("#ff9999"),
				lineWidth: 30,
				canSlide: self.checkDisByDate == false ? false : true, // có thể kéo thanh chart
				unitToPx: self.operationUnit(),
				pin: true, // ghim thanh chart
				rollup: true, // có thể cuộn thanh chart
				roundEdge: true,
				fixed: "Both",
				bePassedThrough: false, // 2 thanh không kéo qua nhau khi bằng false
				zIndex: ruler.loggable(1001)
			});
			ruler.addType(self.breakType);

			self.otType = ({
				name: "OT",
				followParent: true,
				color: ruler.loggable("#ffff00"),
				lineWidth: 30,
				canSlide: false,
				unitToPx: self.operationUnit(),
				pin: true,
				rollup: true,
				fixed: "Both",
				hide: ruler.loggable(false)
			});
			ruler.addType(self.otType);

			self.holidayType = ({
				name: "HolidayTime",
				color: ruler.loggable("#c4bd97"),
				lineWidth: 30,
				unitToPx: self.operationUnit(),
				pin: true,
				fixed: "Both",
				zIndex: ruler.loggable(1103)
			});
			ruler.addType(self.holidayType);

			self.shortType = ({
				name: "ShortTime",
				color: ruler.loggable("#6fa527"),
				lineWidth: 30,
				unitToPx: self.operationUnit(),
				pin: true,
				fixed: "Both",
				zIndex: ruler.loggable(1052)
			});
			ruler.addType(self.shortType);

			self.coreType = ({
				name: "CoreTime",
				color: ruler.loggable("#00ffcc"),
				lineWidth: 30,
				unitToPx: self.operationUnit(),
				fixed: "Both",
				pin: true, // cố định gant chart
				hide: ruler.loggable(false)
			});
			ruler.addType(self.coreType);
		}

		setPositionButonDownAndHeightGrid() {
			let self = this;
			//$("#extable-ksu003").exTable("setHeight", 10 * 30 + 18);
			$(".ex-body-leftmost").css("height", "300px");
			$(".ex-body-detail").css("height", "301px");
			$(".ex-body-detail-horz-scroll").css("top", "336px");
			$(".ex-body-middle").css('height', '318px');
			$(".toDown").css({ "margin-top": 10 * 30 + 10 + 'px' });

			if (self.initDispStart != 0)
				$("#extable-ksu003").exTable("scrollBack", 0, { h: (self.initDispStart * 42 - self.dispStartHours * 42) + 5 });
			else
				$("#extable-ksu003").exTable("scrollBack", 0, { h: 0 });

			if (window.innerWidth <= 1250)
				$("#contain-view-right-ksu003").css("width", "920px");
		}

		leftHide() {
			let self = this, margin = $(".ex-header-leftmost").width() - 12;

			if (self.showA9)
				$("#extable-ksu003").exTable("hideMiddle");

			$(".toLeft").css('margin-left', margin + 'px');

			if (window.innerHeight < 700) {
				//$(".ex-header-detail").css({ "width": 1008 + 'px' });
				//$(".ex-body-detail").css({ "width": 1025 + 'px' });
			}

			let x = $('.ex-header-leftmost').width() + $('.ex-header-detail').width();
			$("#setting-time-grid").css("margin-left", x + 16 + 'px');
		}

		leftShow() {
			let self = this, margin = $(".ex-header-leftmost").width() + $(".ex-header-middle").width() - 12;

			if (self.showA9)
				$("#extable-ksu003").exTable("showMiddle");

			$(".ex-body-middle").css('height', '318px');
			$(".toLeft").css('margin-left', margin + 'px');

			let x = $('.ex-header-leftmost').width() + $('.ex-header-middle').width() + $('.ex-header-detail').width() + 10 + 6;
			$("#setting-time-grid").css("margin-left", x + 'px');
		}

		// setting when click left button
		toLeft() {
			let self = this;

			if (self.checkErrorTime == false) return;

			if (self.indexBtnToLeft() % 2 == 0) {
				self.leftHide();
			} else {
				self.leftShow();
			}

			self.indexBtnToLeft(self.indexBtnToLeft() + 1);
			self.localStore.showHide = self.indexBtnToLeft();
			characteristics.save(self.KEY, self.localStore);

			if (self.initDispStart != 0)
				$("#extable-ksu003").exTable("scrollBack", 0, { h: (self.initDispStart * 42 - self.dispStartHours * 42) + 5 });
			else
				$("#extable-ksu003").exTable("scrollBack", 0, { h: 0 });
		}

		// setting when click down button
		toDown() {
			let self = this, exTableHeight = 17 * 30 + 18, margin = 0;;

			if (self.checkErrorTime == false) return;

			$("#master-wrapper").css({ 'overflow-x': 'hidden' });

			if (self.indexBtnToDown() % 2 == 0) {
				//$("#extable-ksu003").exTable("setHeight", exTableHeight);

				$(".toDown").css('margin-top', exTableHeight - 7 + 'px');
				$("#contents-area").css({ 'overflow-x': 'hidden' });
				$("#contents-area").css({ 'overflow-y': 'auto' });
				$("#note-color").css("margin-right", "6px")

				$(".ex-body-leftmost").css("height", "511px");
				$(".ex-body-detail").css("height", "512px");
				$(".ex-body-detail-horz-scroll").css("top", "547px");
				$(".ex-body-middle").css('height', '529px');

				if (window.innerWidth >= 1340) {
					$("#A1_4").css("margin-right", "0px")
					$("#note-color").css("margin-right", "6px")
				}
				setTimeout((x: any) => {
					$("#contents-area").css("height", "calc(100vh - 0px)")
				}, 10)
			} else {
				exTableHeight = 10 * 30 + 18;
				//$("#extable-ksu003").exTable("setHeight", exTableHeight);

				$(".toDown").css('margin-top', exTableHeight - 7 + 'px');
				$("#contents-area").css({ 'overflow-x': 'hidden' });
				$("#contents-area").css({ 'overflow-y': 'hidden' });
				$("#note-color").css("margin-right", "23px")

				$(".ex-body-leftmost").css("height", "300px");
				$(".ex-body-detail").css("height", "301px");
				$(".ex-body-detail-horz-scroll").css("top", "336px");
				$(".ex-body-middle").css('height', '318px');

				if (navigator.userAgent.indexOf("Chrome") == -1) {
					$("#master-wrapper").css({ 'overflow-y': 'hidden' });
				}

				if (window.innerWidth >= 1340) {
					/*$("#A1_4").css("margin-right", "20px")
					$("#note-color").css("margin-right", "23px")*/
				}
				setTimeout((x: any) => {
					$("#contents-area").css("height", "calc(100vh - -18px)")
				}, 10)
			}
			self.indexBtnToDown(self.indexBtnToDown() + 1);
		}

		calcAllBrk(lstTime: any) {
			let self = this, brkTotal = 0, lstTimeFilter: any = [];
			for (let br = 0; br < self.lstBreakSum.length; br++) {
				for (let ho = 0; ho < self.lstHolidayShort.length; ho++) {
					if (self.lstBreakSum.length > 0 && self.lstHolidayShort.length > 0) {
						if (self.lstBreakSum[br].start >= self.lstHolidayShort[ho].start && self.lstBreakSum[br].end <= self.lstHolidayShort[ho].end) {
							self.lstBreakSum[br].start = 0;
							self.lstBreakSum[br].end = 0;
						}
					}

					if (self.lstBreakSum[br].start >= self.lstHolidayShort[ho].start &&
						self.lstBreakSum[br].end >= self.lstHolidayShort[ho].end && self.lstBreakSum[br].start <= self.lstHolidayShort[ho].end) {
						self.lstBreakSum[br].start = self.lstHolidayShort[ho].end;
					}

					if (self.lstBreakSum[br].start <= self.lstHolidayShort[ho].start && self.lstBreakSum[br].end >= self.lstHolidayShort[ho].end) {
						self.lstBreakSum.push({
							start: self.lstHolidayShort[ho].end,
							end: self.lstBreakSum[br].end,
							type: "BREAK",
							index: self.lstBreakSum.length + 1
						})
						self.lstBreakSum[br].end = self.lstHolidayShort[ho].start;
					}
					if (self.lstBreakSum[br].start <= self.lstHolidayShort[ho].start &&
						self.lstBreakSum[br].end <= self.lstHolidayShort[ho].end && self.lstHolidayShort[ho].start <= self.lstBreakSum[br].end) {
						self.lstBreakSum[br].end = self.lstHolidayShort[ho].start;
					}
				}
				lstTimeFilter = _.filter(lstTime, (x: any) => {
					return (x.start == self.lstBreakSum[br].start && x.end < self.lstBreakSum[br].end) ||
						(x.start > self.lstBreakSum[br].start && x.end == self.lstBreakSum[br].end)
				});

				if (!_.isEmpty(lstTimeFilter)) {
					for (let brk = 0; brk < lstTimeFilter.length; brk++) {
						brkTotal += (self.lstBreakSum[br].end - self.lstBreakSum[br].start) - (lstTimeFilter[brk].end - lstTimeFilter[brk].start)
					}
				} else {
					brkTotal += self.lstBreakSum[br].end - self.lstBreakSum[br].start;
				}
			}
			return brkTotal;
		}
		// Tính tổng từng loại thời gian
		public calcChartTypeTime(schedule: any, typeChart: any, timeRangeLimit: any, lstTime: any, type: string, index: any) {
			let self = this, startCalc = 0, endCalc = 0, lstTimeFilter: any = [];

			if (type == "BREAK" && self.dataScreen003A().employeeInfo[index].fixedWorkInforDto.fixBreakTime == 0)
				return lstTime;

			for (let o = 0; o < typeChart.length; o++) {
				let brkT: any = typeChart[o],
					timeChartBrk = model.convertTimeToChart(
						_.isNil(brkT.startTime) ? Math.round(brkT.start) : (_.isNil(brkT.startTime.time) ? Math.round(brkT.startTime) : Math.round(brkT.startTime.time)),
						_.isNil(brkT.endTime) ? Math.round(brkT.end) : (_.isNil(brkT.endTime.time) ? Math.round(brkT.endTime) : Math.round(brkT.endTime.time))),
					timeChart = model.convertTimeToChart(schedule.workScheduleDto.startTime1, schedule.workScheduleDto.endTime1), timeChart2: any = null;

				startCalc = model.checkTimeOfChart(timeChartBrk.startTime, timeRangeLimit, self.dispStartHours);
				endCalc = model.checkTimeOfChart(timeChartBrk.endTime, timeRangeLimit, self.dispStartHours);

				if (_.inRange(startCalc, timeChart.startTime, timeChart.endTime) || _.inRange(endCalc, timeChart.startTime, timeChart.endTime)) {
					let duplicateTime = model.calcTimeDuplicate(startCalc, endCalc, timeChart.startTime, timeChart.endTime, 1);

					startCalc = duplicateTime.startTime;
					endCalc = duplicateTime.endTime;

					self.lstAllChildShow.push({
						start: startCalc,
						end: endCalc,
						type: type,
						index: index,
						position: o
					})

					if (startCalc != endCalc) {
						if ((timeChart != null && timeChart.startTime != null && timeChart.endTime != null) &&
							(timeChart.startTime < (timeRangeLimit - self.dispStartHours * 12)) &&
							(_.inRange(timeChart.startTime, 0, (timeRangeLimit - self.dispStartHours * 12)) ||
								_.inRange(timeChart.endTime, 0, (timeRangeLimit - self.dispStartHours * 12)))) {

							if (!_.isNil(type) && type == "BREAK" && (_.inRange(startCalc, timeChart.startTime, timeChart.endTime) ||
								_.inRange(endCalc, timeChart.startTime, timeChart.endTime))) {
								self.lstBreakSum.push({
									start: startCalc,
									end: endCalc,
									type: type,
									index: index
								})
							}

							if (!_.isNil(type) && (type == "SHORT" || type == "HOLIDAY") && (_.inRange(startCalc, timeChart.startTime, timeChart.endTime) ||
								_.inRange(endCalc, timeChart.startTime, timeChart.endTime))) {
								self.lstHolidayShort.push({
									start: startCalc,
									end: endCalc,
									type: type,
									index: index
								})

								self.holidayShort.push({
									start: startCalc,
									end: endCalc,
									type: type,
									index: index
								})
							}

							lstTimeFilter = _.filter(lstTime, (x: any) => {
								return (x.start == timeChartBrk.startTime && x.end < timeChartBrk.endTime) || (x.start < timeChartBrk.startTime && x.end == timeChartBrk.endTime)
							})

							if ((_.isEmpty(lstTimeFilter) && (startCalc != timeChart.endTime && endCalc != timeChart.startTime) &&
								(_.inRange(startCalc, timeChart.startTime, timeChart.endTime) || _.inRange(endCalc, timeChart.startTime, timeChart.endTime)))) {
								_.map(lstTime, (x: any) => {
									if (x.start >= startCalc && x.end <= endCalc) {
										x.start = startCalc;
										x.end = endCalc;
									}
									else if (x.start <= startCalc && x.end <= endCalc && _.inRange(startCalc, x.start, x.end)) {
										x.end = endCalc;
									}
									else if (x.start >= startCalc && x.end >= endCalc && _.inRange(endCalc, x.start, x.end)) {
										x.start = startCalc;
									}
									else if (startCalc == x.end) {
										x.end = endCalc;
									}
									else if (!_.inRange(x.start, startCalc, timeChartBrk.endTime) && !_.inRange(x.end, startCalc, endCalc)) {
										let lstTimeFilter2 = _.filter(lstTime, (x: any) => { return (x.start == startCalc) && (x.end == endCalc) })
										if (_.isEmpty(lstTimeFilter2)) {
											lstTime.push({
												start: startCalc,
												end: endCalc,
												type: type,
												index: index
											})
										}
									}
								})
							}

							if ((!_.isEmpty(lstTimeFilter) && (startCalc != timeChart.endTime && endCalc != timeChart.startTime)
								&& (_.inRange(startCalc, startCalc, timeChart.endTime) || _.inRange(endCalc, startCalc, timeChart.endTime)))) {
								_.map(lstTime, (x: any) => {
									if (x.start == startCalc && x.end < endCalc) {
										x.end = endCalc;
									}
								})
							}

							if (_.isEmpty(lstTime) && !_.isNil(type) && (startCalc != timeChart.endTime && endCalc != timeChart.startTime) && (_.inRange(startCalc, timeChart.startTime, timeChart.endTime) || _.inRange(endCalc, timeChart.startTime, timeChart.endTime))) {
								lstTime.push({
									start: startCalc,
									end: endCalc,
									type: type,
									index: index
								})
							}
						}
					}
				}

				if (self.dataScreen003A().targetInfor === 1) {
					timeChart2 = model.convertTimeToChart(schedule.workScheduleDto.startTime2, schedule.workScheduleDto.endTime2)
					if ((schedule.workScheduleDto.startTime2 != null && schedule.workScheduleDto.endTime2 != null) &&
						(timeChart2.startTime < (timeRangeLimit - self.dispStartHours * 12)) &&
						(_.inRange(timeChartBrk.startTime, 0, (timeRangeLimit - self.dispStartHours * 12)) ||
							_.inRange(timeChartBrk.endTime, 0, (timeRangeLimit - self.dispStartHours * 12)))) {
						let startCalc2 = model.checkTimeOfChart(timeChartBrk.startTime, timeRangeLimit, self.dispStartHours), endCalc2 = model.checkTimeOfChart(timeChartBrk.endTime, timeRangeLimit, self.dispStartHours);
						let duplicateTime2 = model.calcTimeDuplicate(startCalc2, endCalc2, timeChart2.startTime, timeChart2.endTime, 2);
						startCalc2 = duplicateTime2.startTime, endCalc2 = duplicateTime2.endTime;
						self.lstAllChildShow.push({
							start: startCalc2,
							end: endCalc2,
							type: type,
							index: index,
							position: o
						})

						if (startCalc2 != endCalc2) {

							if (!_.isNil(type) && type == "BREAK" && (_.inRange(startCalc2, timeChart2.startTime, timeChart2.endTime)
								|| _.inRange(endCalc2, timeChart2.startTime, timeChart2.endTime))) {
								self.lstBreakSum.push({
									start: startCalc2,
									end: endCalc2,
									type: type,
									index: index
								})
							}

							if (!_.isNil(type) && (type == "SHORT" || type == "HOLIDAY") && (_.inRange(startCalc2, timeChart2.startTime, timeChart2.endTime)
								|| _.inRange(endCalc2, timeChart2.startTime, timeChart2.endTime))) {
								self.lstHolidayShort.push({
									start: startCalc2,
									end: endCalc2,
									lstBreak: type,
									index: index
								})

								self.holidayShort.push({
									start: startCalc2,
									end: endCalc2,
									lstBreak: type,
									index: index
								})
							}

							lstTimeFilter = _.filter(lstTime, (x: any) => { return (x.start == timeChartBrk.startTime && x.end < timeChartBrk.endTime) || (x.start < timeChartBrk.startTime && x.end == timeChartBrk.endTime) });
							if ((_.isEmpty(lstTimeFilter) && (startCalc2 != timeChart2.endTime && endCalc2 != timeChart2.startTime)
								&& (_.inRange(startCalc2, timeChart2.startTime, timeChart2.endTime) || _.inRange(endCalc2, timeChart2.startTime, timeChart2.endTime)))) {
								_.map(lstTime, (x: any) => {
									// xs > tcs & xe < tce
									if (x.start >= startCalc2 && x.end <= endCalc2) {
										x.start = startCalc2;
										x.end = endCalc2;
									}
									else if (x.start <= startCalc2 && x.end <= endCalc2 && _.inRange(startCalc2, x.start, x.end)) {
										x.end = endCalc2;
									}
									else if (x.start >= startCalc2 && x.end >= endCalc2 && _.inRange(endCalc2, x.start, x.end)) {
										x.start = startCalc2;
									}
									else if (!_.inRange(x.start, startCalc2, timeChartBrk.endTime) && !_.inRange(x.end, startCalc2, endCalc2)) {
										let lstTimeFilter2 = _.filter(lstTime, (x: any) => { return (x.start == startCalc2) && (x.end == endCalc2) })
										if (_.isEmpty(lstTimeFilter2)) {

											lstTime.push({
												start: startCalc2,
												end: endCalc2,
												type: type,
												index: index
											})
										}
									}
								})
							}

							if ((!_.isEmpty(lstTimeFilter) && (startCalc2 != timeChart2.endTime && endCalc2 != timeChart2.startTime)
								&& (_.inRange(startCalc2, startCalc2, timeChart2.endTime) || _.inRange(endCalc2, startCalc2, timeChart2.endTime)))) {
								_.map(lstTime, (x: any) => {
									if (x.start == startCalc2 && x.end < endCalc2) {
										x.end = endCalc2;
									}
								})
							}

							if (_.isEmpty(lstTime) && !_.isNil(type) && (startCalc2 != timeChart2.endTime && endCalc2 != timeChart2.startTime)
								&& (_.inRange(startCalc2, timeChart2.startTime, timeChart2.endTime) || _.inRange(endCalc2, timeChart2.startTime, timeChart2.endTime))) {
								lstTime.push({
									start: startCalc2,
									end: endCalc2,
									type: type,
									index: index
								})
							}
						}
					}
				}
			}
			return lstTime;
		}

		public nextDay() {
			let self = this, checkSort = $("#extable-ksu003").exTable('updatedCells');
			if (checkSort.length > 0 || self.enableSave() == true) {
				dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
					self.saveData(1).done(() => {
						self.nextDayImpl();
					});
				}).ifNo(() => { self.nextDayImpl(); });
			} else {
				self.nextDayImpl();
			}
		}

		public nextDayImpl() {
			let self = this;
			block.grayout();
			self.changeTargetDate(1, 1);
			self.checkNext(true);
			self.checkPrv(true);
			if (self.dataFromA().endDate <= self.targetDate()) {
				self.targetDate(self.dataFromA().endDate);
				self.checkNext(false);
				$("#next-all").addClass("dis-pointer-none");
				$("#next-one").addClass("dis-pointer-none");
			}
			$("#prev-all").removeClass("dis-pointer-none");
			$("#prev-one").removeClass("dis-pointer-none");
			self.hoverEvent();
			self.destroyAndCreateGrid(self.lstEmpId, 0);
			self.enableSave(false);
		}

		public nextAllDay() {
			block.grayout();
			let self = this, i = 7, nextDay: any = moment(moment(self.targetDate()).add(7, 'd').format('YYYY/MM/DD')),
				checkSort = $("#extable-ksu003").exTable('updatedCells');
			if (checkSort.length > 0 || self.enableSave() == true) {
				dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
					self.saveData(1).done(() => {
						self.nextAllDayImpl(i, nextDay);
					})
				}).ifNo(() => { self.nextAllDayImpl(i, nextDay); });
			} else {
				self.nextAllDayImpl(i, nextDay);
			}
			block.clear();
		}

		public nextAllDayImpl(i: number, nextDay: any) {
			let self = this;
			block.grayout();
			self.checkNext(true);
			self.checkPrv(true);
			if (self.dataFromA().endDate <= nextDay._i) {
				//let date: number = moment(self.dataFromA().endDate).date() - moment(self.targetDate()).date();
				self.checkNext(false);
				$("#next-all").addClass("dis-pointer-none");
				$("#next-one").addClass("dis-pointer-none");
				self.changeTargetDate(1, moment(self.dataFromA().endDate), 0);
			} else {
				self.changeTargetDate(1, i, 1);
			}

			$("#prev-all").removeClass("dis-pointer-none");
			$("#prev-one").removeClass("dis-pointer-none");
			self.hoverEvent();
			self.destroyAndCreateGrid(self.lstEmpId, 0);
			self.enableSave(false);
		}

		public prevDay() {
			let self = this, checkSort = $("#extable-ksu003").exTable('updatedCells');
			if (checkSort.length > 0 || self.enableSave() == true) {
				dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
					self.saveData(1).done(() => {
						self.prevDayImpl();
					})
				}).ifNo(() => { self.prevDayImpl(); });
			} else {
				self.prevDayImpl();
			}
		}

		public prevDayImpl() {
			let self = this;
			block.grayout();
			self.changeTargetDate(0, 1);
			self.checkPrv(true);
			self.checkNext(true);
			if (self.dataFromA().startDate >= self.targetDate()) {
				self.targetDate(self.dataFromA().startDate);
				$("#prev-all").addClass("dis-pointer-none");
				$("#prev-one").addClass("dis-pointer-none");
			}
			$("#next-all").removeClass("dis-pointer-none");
			$("#next-one").removeClass("dis-pointer-none");
			self.hoverEvent();
			self.destroyAndCreateGrid(self.lstEmpId, 0);
			self.enableSave(false);
		}

		public prevAllDay() {
			let self = this, i = 7;
			let prvDay: any = moment(moment(self.targetDate()).subtract(7, 'd').format('YYYY/MM/DD'));
			let checkSort = $("#extable-ksu003").exTable('updatedCells');
			if (checkSort.length > 0 || self.enableSave() == true) {
				dialog.confirm({ messageId: "Msg_447" }).ifYes(() => {
					self.saveData(1).done(() => {
						self.prevAllDayImpl(i, prvDay);
					})
				}).ifNo(() => { self.prevAllDayImpl(i, prvDay) });
			} else {
				self.prevAllDayImpl(i, prvDay);
			}
		}

		public prevAllDayImpl(i: number, prvDay: any) {
			let self = this;
			block.grayout();
			self.checkPrv(true);
			self.checkNext(true);
			if (self.dataFromA().startDate >= prvDay._i) {
				self.checkPrv(false);
				$("#prev-all").addClass("dis-pointer-none");
				$("#prev-one").addClass("dis-pointer-none");
				self.changeTargetDate(0, moment(self.dataFromA().startDate), 0);
			} else {
				self.changeTargetDate(0, 7);
			}
			$("#next-all").removeClass("dis-pointer-none");
			$("#next-one").removeClass("dis-pointer-none");
			self.hoverEvent();
			self.destroyAndCreateGrid(self.lstEmpId, 0);
			self.enableSave(false);
		}

		public changeTargetDate(nextOrprev: number, index: any, limited?: number) {
			let self = this;
			if (!_.isNil(limited) && limited == 0) {
				self.targetDate(index._i)
			} else {
				if (nextOrprev === 1) {
					let time: any = moment(moment(self.targetDate()).add(index, 'd').format('YYYY/MM/DD'));
					self.targetDate(time._i);
				} else {
					let time: any = moment(moment(self.targetDate()).subtract(index, 'd').format('YYYY/MM/DD'));
					self.targetDate(time._i);
				}
			}

			let shortW = moment(self.targetDate()).format('(ddd)');
			if (shortW == "(土)") shortW = "<span style='color:#0000ff;'>" + shortW + "</span>";
			else if (shortW == "(日)") shortW = "<span style='color:#ff0000;'>" + shortW + "</span>";

			self.targetDateDay(self.targetDate() + shortW);
		}

		public checkTimeInfo(index: any, worktypeCode: any, worktimeCode: any, startTime1: any, startTime2: any, endTime1: any, endTime2: any, columnKey: string): JQueryPromise<any> {
			let self = this, css = model.getCss(index, self.dataScreen003A().targetInfor);
			let dfd = $.Deferred<any>(), command: any = {
				workType: worktypeCode,
				workTime: worktimeCode,
				workTime1: new model.TimeZoneDto(new model.TimeOfDayDto(0, (duration.parseString(startTime1).toValue())),
					new model.TimeOfDayDto(0, (duration.parseString(endTime1).toValue()))),
				workTime2: (startTime2 == null || startTime2 == 0 || startTime2 == "") ? null :
					new model.TimeZoneDto(new model.TimeOfDayDto(0, (duration.parseString(startTime2).toValue())),
						new model.TimeOfDayDto(0, (duration.parseString(endTime2).toValue()))),
			}
			if (self.check045003 == false) return;
			if ((columnKey === "startTime1") || /*&& startTime1 != "" && endTime1 != "") || */
				(columnKey === "startTime2" /*&& startTime2 != "" && endTime2 != ""*/) ||
				(columnKey === "endTime1" /*&& endTime1 != "" && startTime1 != ""*/) ||
				(columnKey === "endTime2" /*&& endTime2 != "" && startTime2 != ""*/)) {
				if (_.isNaN(command.workTime1.startTime.time) || _.isNaN(command.workTime1.endTime.time) || (command.workTime2 != null && (_.isNaN(command.workTime2.startTime.time) || _.isNaN(command.workTime2.endTime.time))))
					return;
				block.invisible();
				service.checkTimeIsIncorrect(command).done(function(result) {
					//block.clear();
					let errors = [];
					for (let i = 0; i < result.length; i++) {
						if (!result[i].check) {
							if (result[i].timeSpan == null) {
								errors.push({
									message: nts.uk.resource.getMessage('Msg_439', getText('KDL045_12')),
									messageId: "Msg_439",
									supplements: {}
								});
							} else {
								if (result[i].timeSpan.startTime == result[i].timeSpan.endTime) {
									errors.push({
										message: nts.uk.resource.getMessage('Msg_2058', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime)]),
										messageId: "Msg_2058",
										supplements: {}
									});
								} else {
									errors.push({
										message: nts.uk.resource.getMessage('Msg_1772', [result[i].nameError, formatById("Clock_Short_HM", result[i].timeSpan.startTime), formatById("Clock_Short_HM", result[i].timeSpan.endTime)]),
										messageId: "Msg_1772",
										supplements: {}
									});
								}
							}
						}
					}

					if (errors.length > 0) {
						if (self.checkNeedTime == "NOT_REQUIRED") return;
						if (self.lstErr.length == 0) {
							self.lstErr.push(errors);
							let errorsInfo: any = [];
							_.forEach(self.lstErr, x => {
								errorsInfo = _.uniqBy(x, (y: any) => { return y.message });
							});
							self.checkCalcSum = false;
							bundledErrors({ errors: errorsInfo }).then(() => {
								self.checkCalcSum = true;
								self.enableSave(false);
								block.clear();
								if (columnKey === "startTime1") {
									$(css.cssStartTime1).click();
									$(css.cssStartTime1).click();
									$(css.cssStartTime1).addClass("x-error");
								}
								if (columnKey === "startTime2") {
									$(css.cssStartTime2).click();
									$(css.cssStartTime2).click();
									$(css.cssStartTime2).addClass("x-error");
								}
								if (columnKey === "endTime1") {
									$(css.cssEndTime1).click();
									$(css.cssEndTime1).click();
									$(css.cssEndTime1).addClass("x-error");
								}
								if (columnKey === "endTime2") {
									$(css.cssEndTime2).click();
									$(css.cssEndTime2).click();
									$(css.cssEndTime2).addClass("x-error");
								}
								self.lstErr = [];
								self.checkOpenDialog = false;
							});
						}
						self.checkEnableSave = false;
						self.enableSave(false);
					} else {
						block.clear();
						self.checkOpenDialog = true;
						self.checkEnableSave = true;
						model.removeError("", "", "", "", css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 0);
						let checkSort = $("#extable-ksu003").exTable('updatedCells');
						if (checkSort.length > 0 && (_.isNil($("#extable-ksu003").data("errors")) || (!_.isNil($("#extable-ksu003").data("errors")) && $("#extable-ksu003").data("errors").length == 0))
							&& self.checkEnableSave == true && self.checkEnableWork == true) {
							self.enableSave(true);
						}
					}
					dfd.resolve();
				}).fail(function(res: any) {
					errorDialog({ messageId: res.messageId, messageParams: res.parameterIds });
				}).always(function() {
				});
			} else {
				dfd.resolve();
			}
			return dfd.promise();
		}

		/** 行事(A2_1_6)をクリックする  (Click "event"(A2_1_6) */
		hoverEvent() {
			let self = this, tool = getShared("dataTooltip"), tooltip = _.filter(tool, (x: any) => { return x.ymd === self.targetDate() }), check = 0;
			if (tooltip.length > 0 && tooltip[0].htmlTooltip != null) {
				let htmlToolTip = tooltip[0].htmlTooltip;
				$('#event').on({
					"click": function() {
						tooltip = _.filter(tool, (x: any) => { return x.ymd === self.targetDate() })
						if (tooltip[0].htmlTooltip != null) {
							$(this).tooltip({
								items: "#event", content: htmlToolTip != null ? htmlToolTip : "",
								tooltipClass: "tooltip-styling"
							});
							$(this).tooltip("open");
							check = 1;
						}
					},
					"mouseout": function(a: any) {
						if (check == 1)
							$(this).tooltip("disable");
					}
				});
			}
		}

		// open dialog kdl053
		openKDL053(dataReg: any) {
			let self = this;
			self.enableSave(false);
			let param = {
				employeeIds: _.map(self.lstEmpId, (x: model.IEmpidName) => { return x.empId }),// 社員の並び順
				isRegistered: dataReg.isRegistered == true ? 1 : 0,           // 登録されたか
				errorRegistrationList: dataReg.listErrorInfo, // エラー内容リスト 
			}
			setShared('dataShareDialogKDL053', param);

			nts.uk.ui.windows.sub.modeless('/view/kdl/053/a/index.xhtml').onClosed(function(): any {
				console.log('closed');
				block.clear();
			});
			block.clear();

			/*setTimeout(function() {
			if (!_.isNil(window.parent) && window.parent.length > 1){
				//$(window.parent.document).find('div > iframe').contents().find('#btnClose').trigger('click');
			}
			}, 1);*/
		}

		checkDialogErr() {
			let self = this;
			let css = model.getCss($("#extable-ksu003").data("errors")[0].rowIndex, self.dataScreen003A().targetInfor);
			if (!_.isEmpty($("#extable-ksu003").data("errors"))) {
				if ($("#extable-ksu003").data("errors")[0].columnKey === "startTime1") {
					setTimeout(function() {
						$(css.cssStartTime1).click();
						$(css.cssStartTime1).click();
					}, 1)
				}
				if ($("#extable-ksu003").data("errors")[0].columnKey === "startTime2") {
					setTimeout(function() {
						$(css.cssStartTime2).click();
						$(css.cssStartTime2).click();
					}, 1)
				}

				if ($("#extable-ksu003").data("errors")[0].columnKey === "endTime1") {
					setTimeout(function() {
						$(css.cssEndTime1).click();
						$(css.cssEndTime1).click();
					}, 1)
				}

				if ($("#extable-ksu003").data("errors")[0].columnKey === "endTime2") {
					setTimeout(function() {
						$(css.cssEndTime2).click();
						$(css.cssEndTime2).click();
					}, 1)
				}
			}
		}

		// open dialog kdl045
		public openKdl045Dialog(empId: string) {
			let self = this, lineNo = _.findIndex(self.lstEmpId, (x) => { return x.empId === empId; });
			self.index045 = lineNo;
			let css = model.getCss(lineNo, self.dataScreen003A().targetInfor);
			if (self.checkOpenDialog == false) {
				self.checkDialogErr();
				return;
			}
			block.grayout();
			let dataEmployee = _.filter(self.dataFromA().listEmp, (x: any) => { return x.id === empId; });
			let dataWorkType = [];
			dataWorkType.push(...self.dataInitStartKsu003Dto().functionControlDto.displayableWorkTypeCodeList);

			let dataShare: any = {
				employeeInfo: self.dataScreen003A().employeeInfo[lineNo],
				targetInfor: self.dataScreen003A().targetInfor,
				canModified: self.dataScreen003A().canModified, // comment tạm để test
				scheCorrection: self.dataScreen003A().scheCorrection,
				unit: self.dataFromA().unit,
				targetId: self.dataFromA().unit === 0 ? self.dataFromA().workplaceId : self.dataFromA().workplaceGroupId,
				workplaceName: self.dataFromA().workplaceName,
				disAbleWkTypeCodeLst : dataWorkType,
				disWkTypeCon : self.dataInitStartKsu003Dto().functionControlDto.displayWorkTypeControl
			};
			dataShare.employeeInfo.employeeCode = dataEmployee[0].code;
			dataShare.employeeInfo.employeeName = dataEmployee[0].name;
			
			if (dataShare.employeeInfo.workScheduleDto != null && dataShare.employeeInfo.workScheduleDto.workTypeCode != null )
				dataWorkType.push(dataShare.employeeInfo.workScheduleDto.workTypeCode);
			
			setShared('dataShareTo045', dataShare);
			nts.uk.ui.windows.sub.modal('/view/kdl/045/a/index.xhtml').onClosed(() => {
				model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 1);
				model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 0);
				_.remove($("#extable-ksu003").data("errors"), { rowIndex: lineNo })
				block.clear()
				setShared("targetInforKsu003", self.dataScreen003A().targetInfor); // fix tạm sau khi merge 5_3 thì xóa đi
				self.dataScreen045A(getShared('dataFromKdl045'));
				self.check045003 = false;
				if (!_.isNil(self.dataScreen045A())) {
					if (_.isEmpty($("#extable-ksu003").data("errors")))
						self.enableSave(true);
					self.checkGetInfo = true;
					let lstBrkTime = self.dataScreen045A().workScheduleDto.listBreakTimeZoneDto,
						lstBreak: any = lstBrkTime;

					let schedule = self.dataScreen045A().workScheduleDto,
						fixed = self.dataScreen045A().fixedWorkInforDto,
						info = self.dataScreen045A().workInfoDto;

					if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto == null) {
						self.dataScreen003A().employeeInfo[lineNo].workScheduleDto = {
							startTime1: null, startTime1Status: null, endTime1: null, endTime1Status: null,
							startTime2: null, startTime2Status: null, endTime2: null, endTime2Status: null,
							listBreakTimeZoneDto: [], workTypeCode: null, breakTimeStatus: null,
							workTypeStatus: null, workTimeCode: null, workTimeStatus: null
						}
					}

					if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto == null) {
						// _.isNull(fixed) || 
						self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto = {
							workTimeName: null, coreStartTime: null, coreEndTime: null,
							overtimeHours: [], startTimeRange1: null, endTimeRange1: null,
							workTypeName: null, startTimeRange2: null, endTimeRange2: null,
							fixBreakTime: null, workType: null, isHoliday: true, isNeedWorkTime: null
						}
					}

					let lstCheck: any = [];
					for (let y = 0; y < lstBrkTime.length; y++) {
						for (let i = 0; i < self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length; i++) {
							if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto[i].start == lstBrkTime[y].start &&
								self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto[i].end == lstBrkTime[y].end) {
								lstCheck.add({ check: false })
							} else {
								lstCheck.add({ check: true })
							}
						}
					}
					if (lstBrkTime.length == 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length == 0) {
						lstCheck.add({ check: false })
					}
					if (lstBrkTime.length == 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length > 0) {
						lstCheck.add({ check: true })
					}
					if (lstBrkTime.length > 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length == 0) {
						lstCheck.add({ check: true })
					}
					let lstCheckFalse = _.filter(lstCheck, (x: any) => { return x.check == false; }), lstCheckTrue = _.filter(lstCheck, (x: any) => { return x.check == true; });
					if (lstCheckFalse.length > 0 && lstCheckTrue.length == 0)
						self.colorBreak45 = false;
					else if (lstCheckTrue.length > 0 && lstCheckFalse.length != lstBrkTime.length && lstBrkTime.length == self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length)
						self.colorBreak45 = true;
					else if (lstCheckTrue.length > 0 && lstCheckFalse.length == lstBrkTime.length && lstBrkTime.length == self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length)
						self.colorBreak45 = false;

					let checkColorTime = {
						workTypeCode: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTypeCode == schedule.workTypeCode,
						workTimeCode: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTimeCode == schedule.workTimeCode,
						startTime1: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime1 == schedule.startTime1,
						endTime1: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime1 == schedule.endTime1,
						startTime2: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime2 == schedule.startTime2,
						endTime2: self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime2 == schedule.endTime2,
						workTimeName: fixed.workTimeName == null ? false : self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTimeName == fixed.workTimeName,
						workTypeName: self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTypeName == fixed.workTypeName

					}
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime1 = schedule.startTime1;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime1Status = schedule.endTime1;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime1 = schedule.endTime1;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime2 = schedule.startTime2;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime2 = schedule.endTime2;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto = lstBrkTime.length > 0 ? lstBrkTime : [];
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTypeCode = schedule.workTypeCode;
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTimeCode = schedule.workTimeCode;

					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTimeName = fixed.workTimeName;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTypeName = fixed.workTypeName;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.fixBreakTime = fixed.fixBreakTime == true ? 1 : 0;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType = fixed.workType;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isHoliday = fixed.isHoliday;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isNeedWorkTime = schedule.startTime1 == null || schedule.startTime1 == 0 ? "NOT_REQUIRED" : "REQUIRED";
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.coreStartTime = (fixed.coreTimeFlexSetting != null && fixed.coreTimeFlexSetting.coreTimeSheet != null) ? fixed.coreTimeFlexSetting.coreTimeSheet.startTime : 0;
					self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.coreEndTime = (fixed.coreTimeFlexSetting != null && fixed.coreTimeFlexSetting.coreTimeSheet != null) ? fixed.coreTimeFlexSetting.coreTimeSheet.endTime : 0;

					self.dataScreen003A().employeeInfo[lineNo].workInfoDto.directAtr = info.directAtr;
					self.dataScreen003A().employeeInfo[lineNo].workInfoDto.bounceAtr = info.bounceAtr;

					if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isNeedWorkTime == "NOT_REQUIRED") {
						$("#extable-ksu003").exTable("disableCell", "middle", empId, "worktimeCode");
					} else {
						$("#extable-ksu003").exTable("enableCell", "middle", empId, "worktimeCode");
					}

					if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isNeedWorkTime == "NOT_REQUIRED") {
						self.check045003 = false;
						ruler.replaceAt(lineNo, [{ // xóa chart khi là ngày nghỉ
							type: "Flex",
							options: {
								id: `lgc` + lineNo,
								start: -1000,
								end: -1000,
								lineNo: lineNo
							}
						}]);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", "");
						model.setCellValue(empId);
						let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(9)" :
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(7)";

						if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
							$(cssTotalTime).css("background-color", "#ffffff");

						model.setDisBackCell("#DDDDD2", css, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
						let breakTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(10)" :
							"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(8)";

						let color = "";
						if (empId === self.employeeIdLogin) {
							color = "#94b7fe";
						} else {
							color = "#cee6ff";
						}

						if (self.colorBreak45 == true)
							$(breakTime).css("background-color", color);

						if (!checkColorTime.workTimeName)
							$(css.cssWorkTName).css("background-color", color);

						if (!checkColorTime.workTypeCode)
							$(css.cssWorkType).css("background-color", color);

						if (!checkColorTime.workTypeName)
							$(css.cssWorkTypeName).css("background-color", color);

						$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", "0:00");
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTypeName);
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeCode", self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTypeCode);
						model.setDisableCell("disableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
						model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 1);
						self.checkGetInfo = false;
						self.check045003 = true;
						return;
					}

					self.setDataToMidExtable(lineNo, empId, schedule, fixed);
					self.convertDataIntoExtable();

					let datafilter: Array<model.ITimeGantChart> = _.filter(self.dataOfGantChart, (x: any) => { return x.empId === empId });
					if (datafilter.length > 0) {
						//self.updateGantChart(datafilter, lineNo, fixedGc, lstBreak, indexS, indexF);
						self.addAllChart(datafilter, lineNo, [], "KDL045", lstBreak);
						ruler.replaceAt(lineNo, [
							...self.allGcShow
						]);
					}
					self.setColorDialog(css.cssWorkType, css.cssWorkTypeName, css.cssWorkTime, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, empId, lineNo, checkColorTime);
				}
				self.checkGetInfo = false;
				self.check045003 = true;
				_.remove($("#extable-ksu003").data("errors"), { rowIndex: lineNo })
				block.clear();
			});
		}

		public checkOpenG() {
			let self = this;
			$(window.parent.document).contents().find('body').find('iframe').contents().find('#btnCloseG').trigger('click');
			if ($(window.parent.document).contents().find('body').find('iframe').length < 2) {
				self.openGDialog()
			}
			else {
				setTimeout(function() {
					self.openGDialog()
				}, 600);
			}
		}

		/** A1_3 - Open dialog G */
		public openGDialog() {
			let self = this;
			block.grayout();
			let dataShare = {
				employeeIDs: _.map(self.lstEmpId, (x: model.IEmpidName) => { return x.empId }),
				startDate: self.targetDate(),
				endDate: self.targetDate(),
				employeeInfo: self.dataFromA().listEmp
			};
			setShared('dataShareDialogG', dataShare);
			nts.uk.ui.windows.sub.modeless('/view/ksu/001/g/index.xhtml').onClosed(() => {
				block.clear();
			});
		}

		public openKdl003Dialog(workTypeCode: string, workTimeCode: string, empId: string, type: string) {
			let self = this,
				lineNo = _.findIndex(self.dataScreen003A().employeeInfo, (x) => { return x.empId === empId; }),
				css = model.getCss(lineNo, self.dataScreen003A().targetInfor);
			if (self.checkOpenDialog == false) {
				self.checkDialogErr();
				return;
			}
			self.index045 = lineNo;
			block.grayout();
			nts.uk.ui.windows.setShared('parentCodes', {
				workTypeCodes: [],
				selectedWorkTypeCode: workTypeCode,
				workTimeCodes: [],
				selectedWorkTimeCode: workTimeCode
			}, true);
			if (self.dataScreen003A().employeeInfo[lineNo].workInfoDto.isConfirmed == 1) {
				block.clear();
				return;
			}

			let checkOpen = _.filter(self.disableDs, (x: any) => { return x.empId === empId });
			let checkOpen2: any = [];
			if (type === "WorkTypeName")
				checkOpen2 = _.filter(self.lstDis, (x: any) => { return x.empId === empId && x.worktypeName == true });

			if (type === "WorkTimeName")
				checkOpen2 = _.filter(self.lstDis, (x: any) => { return x.empId === empId && x.worktimeName == true });
			let dataWorkType = [];
			if (checkOpen.length < 1 && checkOpen2.length < 1) {
				dataWorkType.push(...self.dataInitStartKsu003Dto().functionControlDto.displayableWorkTypeCodeList);

				if (workTypeCode != "") {
					dataWorkType.push(workTypeCode);
				}

				let param = {
					disWkTypeCon : self.dataInitStartKsu003Dto().functionControlDto.displayWorkTypeControl,
					disAbleWkTypeCodeLst : dataWorkType
				}
				setShared('paramKsu003Kdl003', param);
				nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(() => {
					model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 1);
					model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 0);
					_.remove($("#extable-ksu003").data("errors"), { rowIndex: lineNo })
					let dataShareKdl003 = getShared('childData');
					if (!_.isNil(dataShareKdl003)) {
						self.check045003 = false;
						if (_.isEmpty($("#extable-ksu003").data("errors")))
							self.enableSave(true);
						let param = [{
							workTypeCode: dataShareKdl003.selectedWorkTypeCode,
							workTimeCode: dataShareKdl003.selectedWorkTimeCode
						}]
						// 社員勤務予定と勤務固定情報を取得する
						service.getEmpWorkFixedWorkInfo(param).done((data: any) => {
							if (!_.isNil(data)) {

								let checkColorTime = {
									workTypeCode: (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto == null || data.workScheduleDto == null) ? false :
										self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTypeCode == data.workScheduleDto.workTypeCode,
									workTimeName: (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto == null || data.workScheduleDto == null) ? false :
										data.fixedWorkInforDto.workTimeName == null ? false :
											self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTimeName == data.fixedWorkInforDto.workTimeName,
									workTypeName: (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto == null || data.workScheduleDto == null) ? false :
										self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTypeName == data.fixedWorkInforDto.workTypeName
								}

								let lstCheck: any = [], colorBreak003 = false;
								if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto != null && data.workScheduleDto != null) {
									for (let i = 0; i < self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length; i++) {
										for (let y = 0; y < data.workScheduleDto.listBreakTimeZoneDto.length; y++) {
											if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto[i].start == data.workScheduleDto.listBreakTimeZoneDto[y].start &&
												self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto[i].end == data.workScheduleDto.listBreakTimeZoneDto[y].end) {
												lstCheck.add({ check: false })
											} else {
												lstCheck.add({ check: true })
											}
										}
									}
								}
								if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto != null && data.workScheduleDto != null) {
									if (data.workScheduleDto.length == 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length == 0) {
										lstCheck.add({ check: false })
									}

									if (data.workScheduleDto.length == 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length > 0) {
										lstCheck.add({ check: true })
									}

									if (data.workScheduleDto.length > 0 && self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.listBreakTimeZoneDto.length == 0) {
										lstCheck.add({ check: true })
									}
								}

								let lstCheckFalse = _.filter(lstCheck, (x: any) => { return x.check == false; });
								let lstCheckTrue = _.filter(lstCheck, (x: any) => { return x.check == true; });

								if (lstCheckFalse.length > 0 && lstCheckTrue.length == 0)
									colorBreak003 = false;
								else if (lstCheckTrue.length > 0)
									colorBreak003 = true;

								self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto = data.fixedWorkInforDto;
								self.dataScreen003A().employeeInfo[lineNo].workScheduleDto = data.workScheduleDto;

								if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isNeedWorkTime == "NOT_REQUIRED") {
									$("#extable-ksu003").exTable("disableCell", "middle", empId, "worktimeCode");
								}

								if (data.fixedWorkInforDto.workType == null) {

									ruler.replaceAt(lineNo, [{ // xóa chart khi là ngày nghỉ
										type: "Flex",
										options: {
											id: `lgc` + lineNo,
											start: -1000,
											end: -1000,
											lineNo: lineNo
										}
									}]);
									self.check045003 = false;
									$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeCode", "");
									model.setCellValue(empId);
									let cssTotalTime: string = self.dataScreen003A().targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(9)" :
										"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(7)";

									if (!_.isEqual($(cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
										$(cssTotalTime).css("background-color", "#ffffff");

									model.setDisBackCell("#DDDDD2", css, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");

									$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", "0:00");
									$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
									$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeName", self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workTypeName);
									$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktypeCode", self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.workTypeCode);
									model.setDisableCell("disableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "startTime2", "endTime2");
									let color = "";
									if (empId === self.employeeIdLogin)
										color = "#94b7fe";
									else
										color = "#cee6ff";

									if (colorBreak003 == true)
										$(css.cssbreakTime).css("background-color", color);

									if (!checkColorTime.workTimeName)
										$(css.cssWorkTName).css("background-color", color);

									if (!checkColorTime.workTypeCode)
										$(css.cssWorkType).css("background-color", color);

									if (!checkColorTime.workTypeName)
										$(css.cssWorkTypeName).css("background-color", color);
									model.removeError(css.cssWorkType, css.cssWorkTime, css.cssWorkTypeName, css.cssWorkTName, css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 1);

									self.check045003 = true;
									if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isHoliday == true) $(".xcell").removeClass("x-error");
									return;
								}

								if (data.workScheduleDto.startTime1) {
									self.checkClearTime = false;
									self.checkUpdateMidChart = false;
								}
								setShared("targetInforKsu003", self.dataScreen003A().targetInfor); // fix tạm sau khi merge 5_3 thì xóa đi
								self.setDataToMidExtable(lineNo, empId, self.dataScreen003A().employeeInfo[lineNo].workScheduleDto, self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto);
								self.convertDataIntoExtable(lineNo);

								let datafilter: Array<model.ITimeGantChart> = _.filter(self.dataOfGantChart, (x: any) => { return x.empId === empId });
								if (datafilter.length > 0) {
									//self.updateGantChart(datafilter, lineNo, fixedGc, lstBreak, indexS, indexF);
									self.addAllChart(datafilter, lineNo, [], "KDL003");
									if (self.allGcShow.length > 0) {
										ruler.replaceAt(lineNo, [
											...self.allGcShow
										]);
									} else {
										ruler.replaceAt(lineNo, [{ // xóa chart khi là ngày nghỉ
											type: "Flex",
											options: {
												id: `lgc` + lineNo,
												start: -1000,
												end: -1000,
												lineNo: lineNo
											}
										}]);
									}
								}
							}
							let checkColorTime = {
								workTypeCode: false, workTimeCode: false, startTime1: false, endTime1: false, startTime2: false, endTime2: false, workTimeName: false, workTypeName: false
							}
							self.setColorDialog(css.cssWorkType, css.cssWorkTypeName, css.cssWorkTime, css.cssWorkTName, css.cssStartTime1,
								css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, empId, lineNo, checkColorTime);

							block.clear();
						}).fail(function(error) {
							block.clear();
							errorDialog({ messageId: error.messageId });
						}).always(function() {
							self.checkUpdateMidChart = true;
							self.check045003 = true;
						});
					}
				});
			}
		}

		public setColorDialog(cssWorkType: string, cssWorkTypeName: string, cssWorkTime: string, cssWorkTName: string, cssStartTime1: string,
			cssEndTime1: string, cssStartTime2: string, cssEndTime2: string, empId: string, lineNo: number, checkColorTime: any) {
			let self = this, breakTime: string = self.dataScreen003A().targetInfor == 1 ?
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(10)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (lineNo + 2).toString() + ")" + " > td:nth-child(8)";
			let color = "";
			if (empId === self.employeeIdLogin) color = "#94b7fe";
			else color = "#cee6ff";

			if (self.colorBreak45 == true)
				$(breakTime).css("background-color", color);

			if (self.colorBreak45 == false) {
				if (breakTime != color)
					$(breakTime).css("background-color", "#FFFFFF");
			}

			if (!checkColorTime.workTimeCode)
				$(cssWorkTime).css("background-color", color);

			for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
				$("#extable-ksu003").exTable("disableCell", "detail", empId, z.toString() + "_");
			}
			if (!checkColorTime.workTimeName)
				$(cssWorkTName).css("background-color", color);

			if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.isHoliday == false) {
				model.setDisableCell("enableCell", empId, "", "", "worktimeCode", "", "startTime1", "endTime1", "", "");
				$(cssStartTime1).removeClass("xseal");
				$(cssEndTime1).removeClass("xseal");
				if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX &&
					self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime2 != null) {

					model.setDisableCell("enableCell", empId, "", "", "", "", "", "", "startTime2", "endTime2");
					$(cssStartTime2).removeClass("xseal");
					$(cssEndTime2).removeClass("xseal");
				} else {
					$(cssStartTime2).css("background-color", "#DDDDD2");
					$(cssEndTime2).css("background-color", "#DDDDD2");
					model.setDisableCell("disableCell", empId, "", "", "", "", "", "", "startTime2", "endTime2");
					$(cssStartTime2).addClass("xseal");
					$(cssEndTime2).addClass("xseal");
				}
			}
			if (!checkColorTime.workTypeCode)
				$(cssWorkType).css("background-color", color);

			if (!checkColorTime.workTypeName)
				$(cssWorkTypeName).css("background-color", color);

			if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.startTime1))
				$(cssStartTime1).css("background-color", color);

			if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.endTime1))
				$(cssEndTime1).css("background-color", color);

			if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX) {
				if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime2 != null) {
					if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.startTime2)) {
						$(cssStartTime2).css("background-color", color);
					}
				}
				if (!_.isEqual($(cssStartTime2).css("background-color"), "rgb(221, 221, 210)")) {
					if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.startTime2)) {
						$(cssStartTime2).css("background-color", color);
					}
				}
				if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.startTime2 == null) $(cssStartTime2).css("background-color", "#DDDDD2");
			}

			if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType != model.WorkTimeForm.FLEX) {
				if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime2 != null) {
					if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.endTime2)) {
						$(cssEndTime2).css("background-color", color);
					}
				}

				if (!_.isEqual($(cssEndTime2).css("background-color"), "rgb(221, 221, 210)")) {
					if (!checkColorTime.workTimeCode || (checkColorTime.workTimeCode && !checkColorTime.endTime2)) {
						$(cssEndTime2).css("background-color", color);
					}
				}

				if (self.dataScreen003A().employeeInfo[lineNo].workScheduleDto.endTime2 == null) $(cssEndTime2).css("background-color", "#DDDDD2");
			}
			let lstType = self.dataScreen003A().scheCorrection; // 確定済みか
			let fix = _.filter(lstType, (x: any) => { return x === 0 }),
				flex = _.filter(lstType, (x: any) => { return x === 1 }),
				flow = _.filter(lstType, (x: any) => { return x === 2 });

			if (self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto != null &&
				self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType != null) {
				if ((fix.length == 0 && self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FIXED) ||
					(flex.length == 0 && self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLEX) ||
					(flow.length == 0 && self.dataScreen003A().employeeInfo[lineNo].fixedWorkInforDto.workType == model.WorkTimeForm.FLOW)) {

					$(cssStartTime2).css("background-color", "#DDDDD2");
					$(cssEndTime2).css("background-color", "#DDDDD2");

					model.setDisableCell("disableCell", empId, "", "", "", "", "", "", "startTime2", "endTime2");

					$(cssStartTime2).addClass("xseal");
					$(cssEndTime2).addClass("xseal");
					$(cssStartTime1).css("background-color", "#DDDDD2");
					$(cssEndTime1).css("background-color", "#DDDDD2");

					model.setDisableCell("disableCell", empId, "", "", "", "", "startTime1", "endTime1", "", "");

					$(cssStartTime1).addClass("xseal");
					$(cssEndTime1).addClass("xseal");
				} else {
					for (let z = self.dispStartHours; z <= (self.timeRange + self.dispStartHours); z++) {
						$("#extable-ksu003").exTable("enableCell", "detail", empId, z.toString() + "_");
					}
				}
			}
			$(".xcell").removeClass("x-error");
		}

		public inputMid(dataCell: any) {
			let self = this;
			if (self.checkDrop == true) return;
			// input middle
			let index: number = dataCell.originalEvent.detail.rowIndex, color = "", dataMid = $("#extable-ksu003").exTable('dataSource', 'middle').body[index];
			let empId = self.lstEmpId[dataCell.originalEvent.detail.rowIndex].empId, css = model.getCss(index, self.dataScreen003A().targetInfor);;
			let dataFixed = _.filter(self.dataScreen003A().employeeInfo, x => { return x.empId === empId }),
				dataFixInfo = _.filter(self.fixedWorkInformationDto, x => { return x.empId === empId });

			if (self.check045003 == false) return;
			if (self.checkRetained == false) return;
			let columnKey = dataCell.originalEvent.detail.columnKey,
				checkErr = _.filter($("#extable-ksu003").data("errors"), (x: any) => { return x.rowIndex === index; }),
				checkErr2 = _.filter(checkErr, (x: any) => { return x.columnKey === dataCell.detail.columnKey; });

			if ((columnKey === "startTime1" || columnKey === "startTime2" ||
				columnKey === "endTime1" || columnKey === "endTime2") && self.checkUpdateTime.id == 0) {
				// kiểm tra startTime > endTime và time = null
				if ((columnKey === "startTime1" || columnKey === "endTime1") &&
					(dataMid.startTime1 == "" || duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue())) {
					if (self.checkDragDrop == true) return;
					if (self.checkCloseKsu003 == true) return;
					block.invisible();
					self.checkCalcSum = false;
					self.enableSave(false);
					errorDialog({ messageId: "Msg_54" }).then(() => {
						self.checkCalcSum = true;
						self.enableSave(false);
						self.checkOpenDialog = false;
						block.clear();
						if (columnKey === "startTime1") {
							$(css.cssStartTime1).click();
							$(css.cssStartTime1).click();
							if (duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue()) {
								$(css.cssStartTime1).addClass("x-error");
							}
						}
						if (columnKey === "endTime1") {
							$(css.cssEndTime1).click();
							$(css.cssEndTime1).click();
							if (duration.parseString(dataMid.startTime1).toValue() > duration.parseString(dataMid.endTime1).toValue()) {
								$(css.cssEndTime1).addClass("x-error");
							}
						}
					});
					return;
				}
				// kiểm tra startTime > endTime và time = null
				if ((columnKey === "startTime2" || columnKey === "endTime2") &&
					(dataMid.startTime2 == "" || duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue())) {
					if (self.checkDragDrop == true) return;
					if (self.checkCloseKsu003 == true) return;
					block.invisible();
					self.checkCalcSum = false;
					errorDialog({ messageId: "Msg_54" }).then(() => {
						self.checkCalcSum = true;
						self.enableSave(false);
						self.checkOpenDialog = false;
						block.clear();
						if (columnKey === "startTime2") {
							$(css.cssStartTime2).click();
							$(css.cssStartTime2).click();
							if (duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue()) {
								$(css.cssStartTime2).addClass("x-error");
							}
						}
						if (columnKey === "endTime2") {
							$(css.cssEndTime2).click();
							$(css.cssEndTime2).click();
							if (duration.parseString(dataMid.startTime2).toValue() > duration.parseString(dataMid.endTime2).toValue()) {
								$(css.cssEndTime2).addClass("x-error");
							}
						}
					});
					return;
				}

				if (checkErr2.length > 0 && checkErr.length > 0 && $("#extable-ksu003").data("errors").length > 0) {
					self.enableSave(false);
					return;
				}

				if (checkErr2.length > 0 && checkErr.length > 0 && $("#extable-ksu003").data("errors").length > 0 && dataCell.originalEvent.detail.value != "") return;
				if (_.isNaN(duration.parseString(dataMid.startTime1).toValue()) || _.isNaN(duration.parseString(dataMid.startTime2).toValue()) || _.isNaN(duration.parseString(dataMid.endTime1).toValue()) || _.isNaN(duration.parseString(dataMid.endTime2).toValue())) return;
				// delete class error when have not Msg_54
				model.removeError("", "", "", "", css.cssStartTime1, css.cssEndTime1, css.cssStartTime2, css.cssEndTime2, 0);
				self.checkOpenDialog = true;
				// kiểm tra thời gian nhập
				self.checkTimeInfo(index, dataMid.worktypeCode, dataMid.worktimeCode, dataMid.startTime1, dataMid.startTime2, dataMid.endTime1, dataMid.endTime2, columnKey).done(() => {
					let colorTime = "";
					if (empId === self.employeeIdLogin) {
						color = "rgb(148, 183, 254)";
						colorTime = "#94b7fe";
					} else {
						color = "rgb(206, 230, 255)";
						colorTime = "#cee6ff";
					}
					let timeConvert = model.convertTime(dataMid.startTime1, dataMid.endTime1, dataMid.startTime2, dataMid.endTime2);
					self.employeeScheduleInfo.forEach((x, i) => {

						if (i === dataCell.originalEvent.detail.rowIndex) {
							if (columnKey === "startTime1") {
								x.startTime1 = timeConvert.start;
								dataFixed[0].workScheduleDto.startTime1 = x.startTime1;
								if (x.startTime1 == "") self.checkMes += 1;
								$(css.cssStartTime1).css("background-color", colorTime);
							}
							if (columnKey === "startTime2") {
								x.startTime2 = timeConvert.start2;
								dataFixed[0].workScheduleDto.startTime2 = x.startTime2;
								if (x.startTime2 == "") self.checkMes += 1;
								$(css.cssStartTime2).css("background-color", colorTime);
							}
							if (columnKey === "endTime1") {
								x.endTime1 = timeConvert.end;
								dataFixed[0].workScheduleDto.endTime1 = x.endTime1;
								if (x.endTime1 == "") self.checkMes += 1000;
								$(css.cssEndTime1).css("background-color", colorTime);
							}
							if (columnKey === "endTime2") {
								x.endTime2 = timeConvert.end2;
								dataFixed[0].workScheduleDto.endTime2 = x.endTime2;
								if (x.endTime2 == "") self.checkMes += 1000;
								$(css.cssEndTime2).css("background-color", colorTime);
							}
						}
					})
					if ((timeConvert.start == "" && timeConvert.end != "") || (timeConvert.start != "" && timeConvert.end == "")
						|| (timeConvert.start2 == "" && timeConvert.end2 != "") || (timeConvert.start2 != "" && timeConvert.end2 == ""))
						return;
					// tính lại tổng time
					if (self.checkCalcSum == false) return;
					let lstTime: any = [], timeRangeLimit = ((self.timeRange * 60) / 5), totalBrkTime: any = null;
					self.lstBreakSum = [], self.lstAllChildShow = [], self.lstHolidayShort = [];
					lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workScheduleDto.listBreakTimeZoneDto,
						timeRangeLimit, lstTime, "BREAK", index);
					for (let e = 0; e < dataFixed[0].workInfoDto.listTimeVacationAndType.length; e++) {
						let y = dataFixed[0].workInfoDto.listTimeVacationAndType[e];
						lstTime = self.calcChartTypeTime(dataFixed[0], y.timeVacation.timeZone, timeRangeLimit, lstTime, "HOLIDAY", index);
					}
					lstTime = self.calcChartTypeTime(dataFixed[0], dataFixed[0].workInfoDto.shortTime, timeRangeLimit, lstTime, "SHORT", index);
					lstTime = self.calcChartTypeTime(dataFixed[0], dataFixInfo[0].fixedWorkInforDto == null ? [] : dataFixInfo[0].fixedWorkInforDto.overtimeHours, timeRangeLimit, lstTime, "OT", index);
					let totalTime = model.calcAllTime(dataFixed[0], lstTime, timeRangeLimit, self.dispStart, self.dispStartHours);
					if (self.checkDragDrop != true) {
						totalBrkTime = self.calcAllBrk(lstTime);
						totalBrkTime = totalBrkTime != null ? formatById("Clock_Short_HM", Math.round(totalBrkTime * 5)) : "";

						if ($("#extable-ksu003").exTable('dataSource', 'middle').body[index].breaktime != _.trim(totalBrkTime)) {
							if ($(css.cssbreakTime).css("background-color") != color) {
								if (totalBrkTime == totalBrkTime + " ")
									totalBrkTime = _.trim(totalBrkTime);
								else
									totalBrkTime = totalBrkTime + " "
							}
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "breaktime", totalBrkTime);
							let colorTime = "";
							if (self.dataScreen003A().employeeInfo[index].empId === self.employeeIdLogin)
								colorTime = "#94b7fe";
							else
								colorTime = "#cee6ff";

							if (self.colorBreak45 == true) $(css.cssbreakTime).css("background-color", colorTime);
						}
						if (!self.checkClearTime == false) {
							if (!_.isEqual($(css.cssTotalTime).css("background-color"), "rgb(221, 221, 210)") && !_.isEqual($(css.cssTotalTime).css("background-color"), "rgb(236, 206, 251)"))
								$(css.cssTotalTime).css("background-color", "#ffffff");

							if ($("#extable-ksu003").exTable('dataSource', 'middle').body[index].worktimeCode != "" && totalTime == "") {
								totalTime = "0:00"
							}
							$("#extable-ksu003").exTable("cellValue", "middle", empId, "totalTime", totalTime != null ? totalTime : "");
						}
					}
				});
				return;
			}

			let format = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
			if (checkErr2.length > 0 && checkErr.length > 0 && $("#extable-ksu003").data("errors").length > 0 && format.test(dataCell.detail.value) == true) return;

			if ((dataCell.originalEvent.detail.columnKey === "worktypeCode" && dataMid.worktypeCode == "")) {
				if (dataMid.worktypeName == "" && dataMid.worktimeCode == "") {
					$(css.cssWorkType).removeClass("x-error");
					return;
				}
				let errors = [];
				errors.push({
					message: nts.uk.resource.getMessage('Msg_1780', [dataCell.originalEvent.detail.columnKey === "worktypeCode" ? nts.uk.resource.getText("KSU003_24") : nts.uk.resource.getText("KSU003_26")]),
					messageId: "Msg_1780",
					supplements: {}
				});
				self.checkOpenDialog = false;
				if (dataMid.worktypeCode == "" && dataCell.originalEvent.detail.columnKey === "worktypeCode") {
					block.invisible();
					self.enableSave(false);
					self.checkCalcSum = false;
					bundledErrors({ errors: errors }).then(() => {
						self.checkCalcSum = true;
						if (dataMid.worktypeName != "" && dataMid.worktimeCode != "") {
							$(css.cssWorkType).click();
							$(css.cssWorkType).click();
						}
						block.clear();
					});
				}
				return;
			}

			self.checkOpenDialog = true;
			if ((dataCell.originalEvent.detail.columnKey === "worktypeCode" && dataMid.worktypeCode != "")) {
				self.changeWorkType(dataCell.originalEvent.detail.columnKey, empId, index);
			};

			if (empId === self.employeeIdLogin)
				color = "#94b7fe";
			else
				color = "#cee6ff";

			if (self.checkGetInfo == false && self.checkUpdateMidChart == true) {
				// 勤務種類を変更する (nhập thủ công worktype code)
				if ((dataCell.originalEvent.detail.columnKey === "worktypeCode")) {
					if (self.timesOfInput > 0) self.timesOfInput = 0;
					if (self.timesOfInputTime > 0) self.timesOfInputTime = 0;
					self.checkUpdateTime.name = "worktypeCode";
					self.checkUpdateTime.id = 2;
					self.inputWorkInfo(dataMid, index, dataCell, dataFixed, empId, dataCell.originalEvent.detail.columnKey);
					// Nếu là ngày nghỉ	
					if (dataFixed[0].fixedWorkInforDto != null && dataFixed[0].fixedWorkInforDto.isHoliday != null && dataFixed[0].fixedWorkInforDto.isHoliday == true) {
						self.checkClearTime = false;
						$("#extable-ksu003").exTable("cellValue", "middle", empId, "worktimeName", getText('KSU003_55'));
						ruler.replaceAt(index, [{ // xóa chart khi là ngày nghỉ
							type: "Flex",
							options: {
								id: `lgc` + index,
								start: -1000,
								end: -1000,
								lineNo: index
							}
						}]);
						return;
					}
					// Nếu không phải ngày nghỉ
					if (dataFixed[0].fixedWorkInforDto != null && dataFixed[0].fixedWorkInforDto.isHoliday != null && dataFixed[0].fixedWorkInforDto.isHoliday == false) {
						self.checkMes += 1;
					}
				}

				if (dataCell.originalEvent.detail.columnKey === "worktimeCode") {
					if (self.timesOfInput > 0) self.timesOfInput = 0;
					if (self.timesOfInputTime > 0) self.timesOfInputTime = 0;
					self.checkUpdateTime.name = "worktimeCode";
					self.checkUpdateTime.id = 1;
					self.inputWorkInfo(dataMid, index, dataCell, dataFixed, empId, dataCell.originalEvent.detail.columnKey);
					self.checkMes += 100;
				}
			}
		}

		// Xử lý ver 4

		// ①<<ScreenQuery>> 作業予定を登録する
		public saveTask() {
			let self = this,
				timeSpanForCalcDto = { start: 1, end: 2 },
				taskScheduleDetail = { taskCode: '1', timeSpanForCalcDto: timeSpanForCalcDto },
				taskScheduleDetailEmp = { empId: '1', taskScheduleDetail: taskScheduleDetail },
				lstTaskScheduleDetailEmp: any = [];

			lstTaskScheduleDetailEmp.push(taskScheduleDetailEmp);
			let param = {
				lstTaskScheduleDetailEmp: lstTaskScheduleDetailEmp,
				ymd: self.targetDate()
			}
			service.addTaskWorkSchedule(param).done(() => {

			}).fail(function(error) {
				errorDialog({ messageId: error.messageId });
			}).always(function() {
			});
		}

		// ①<<ScreenQuery>> 作業予定情報を取得する
		public getTask(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();

			let param = {
				lstEmpId: _.map(self.lstEmpId, (x: model.IEmpidName) => { return x.empId }),
				startDate: self.targetDate(),
				endDate: self.targetDate(),
				displayMode: 1
			}
			service.getTaskWorkSchedule(param).done((data: any) => {
				self.taskData = data.sort(function(a: any, b: any) {
					return _.findIndex(self.lstEmpId, x => { return x.empId == a.empID }) - _.findIndex(self.lstEmpId, x => { return x.empId == b.empID });
				});
				dfd.resolve();
			}).fail(function(error) {
				errorDialog({ messageId: error.messageId });
			}).always(function() {
			});
			return dfd.promise();
		}

		addTypeOfTask(color: any, value: any, checkRe?: any) {
			let self = this, lgcFil: any = [], rgcFil: any = [], noneFil: any = [], index = "";

			self.taskType = ({
				name: value.data.page + "TASK",
				color: ruler.loggable(color),
				lineWidth: 30,
				canSlide: false,
				unitToPx: 3.5,
				hide: self.selectedDisplayPeriod() == 1 ? ruler.loggable(false) : ruler.loggable(true),
				canPaste: true,
				canPasteResize: _.isNil(checkRe) ? true : false,
				title: value.data.text,
				pastingResizeFinished: (line: any, type: any, start: any, end: any) => {

					lgcFil = _.filter(ruler.gcChart[line], (rul: any) => {
						return _.includes(rul.id, "lgc");
					});

					rgcFil = _.filter(ruler.gcChart[line], (rul: any) => {
						return _.includes(rul.id, "rgc");
					});

					noneFil = _.filter(ruler.gcChart[line], (rul: any) => {
						return !_.includes(rul.id, "rgc") && !_.includes(rul.id, "lgc");
					});

					if (_.isNil(start) || _.isNil(end)) {
						if (_.isNil(start) || _.isNil(end)) {
							if (_.isNil(start))
								start = ruler.gcChart[line][noneFil[0].id].start;

							if (_.isNil(end))
								end = ruler.gcChart[line][noneFil[0].id].end;
						}

						if (_.isNil(start) && _.isNil(end)) {
							start = ruler.gcChart[line][noneFil[0].id].start;
							end = ruler.gcChart[line][noneFil[0].id].end;
						}
					}

					let noneChart = _.filter(noneFil, (x: any) => {
						return (x.start == start && x.end > end) || (x.start == start && x.end < end) || (x.start > start && x.end == end) || (x.start < start && x.end == end) || (x.start == start && x.end == end);
					})

					let checkDup = _.filter(noneFil, (x: any) => {
						return (_.inRange(x.start, start, end) && _.inRange(x.end, start, end)) || (_.inRange(start, x.start, x.end) && _.inRange(end, x.start, x.end));
					})

					if (noneChart.length == 0) {
						if (checkDup.length > 0) {
							if (self.enableSave() != true) {
								self.enableSave(false);
							}
						}
						return;
					}
					self.addTaskResize(line, type, start, end, ruler.gcChart[line][noneChart[0].id].id);
					console.log(`${line}-${type}-${start}-${end}`);
				}
			});

			self.taskTypes.add(self.taskType);
			ruler.addType(self.taskType);
		}
		
		// Add task when resize
		addTaskResize(line: any, type: any, start: any, end: any, id: any) {
			let self = this;
			if (start < end) {
				start = start * 5 + self.dispStart * 5;
				end = end * 5 + self.dispStart * 5;
			} else {
				start = end * 5 + self.dispStart * 5;
				end = start;
			}


			// kiem tra truong hop khong chon task nao de paste
			let taskInfo: any = _.filter(__viewContext.viewModel.viewmodelAb.dataTaskInfo.lstTaskDto, (x: any) => {
				return _.isEqual(type.substring(0,type.length - 4), x.code) && _.includes(type, "TASK");
			});

			if (taskInfo == null) return;

			// kiem tra trung code va trung thoi gian
			let filDuplicate = _.filter(self.lstChartTask, (x: any) => {
				return !_.isEqual(x.id, id) && x.line == line && x.start <= start && x.end >= end && taskInfo[0].code == x.code;
			});

			if (filDuplicate.length > 0) {
				let filShowChart = _.filter(self.gcShowChart, (x: any) => {
					return x.line == line && ((x.startTime * 5 == filDuplicate[0].end) || (x.endTime * 5 == start));
				});

				if (filShowChart.length > 0) {
					self.enableSave(true);
				} else {
					self.enableSave(false);
					return;
				}
			}

			let iOld = _.findIndex(self.taskData, (ind: any) => {
				return ind.empID === self.dataScreen003A().employeeInfo[line].empId;
			}),
				iNew = _.findIndex(self.lstTaskScheduleDetailEmp, (ind: any) => {
					return ind.empID === self.dataScreen003A().employeeInfo[line].empId;
				});
			self.taskTypeDel = _.uniq(self.taskTypeDel)
			_.forEach(self.taskTypeDel, (ru: any) => {
				if (iOld != -1) {
					let idxo = _.findIndex(self.taskData[iOld].taskScheduleDetail, (xx: any) => {
						return _.isEqual(xx.taskCode, ru);
					})

					if (idxo != -1)
						_.remove(self.taskData[iOld].taskScheduleDetail, (y: any) => {
							return _.isEqual(y.taskCode, ru);;
						});
				}

				if (iNew != -1) {
					let idxn = _.findIndex(self.lstTaskScheduleDetailEmp[iNew].taskScheduleDetail, (xx: any) => {
						return _.isEqual(xx.taskCode, ru);
					})

					if (idxn != -1)
						_.remove(self.lstTaskScheduleDetailEmp[iNew].taskScheduleDetail, (y: any) => {
							return _.isEqual(y.taskCode, ru);;
						});
				}
			})
	
			let newLstChartTsk = _.filter(self.lstChartTask, (x: any) => x.line == line), arrRemoveTask: any = [], arrRemoveTaskNew: any = [];
			let idResize = id;
			if (newLstChartTsk.length > 0) {
				for (let i = 0; i < newLstChartTsk.length; i++) { // kiem tra voi list task cu
					let task: any = newLstChartTsk[i],
						oldS = task.start, oldE = task.end,
						indOld = -1, indNew = -1,
						indTask = -1,
						indexTask = _.findIndex(self.taskData, (ind: any) => {
							return ind.empID === self.dataScreen003A().employeeInfo[line].empId;
						}),
						indexTaskNew = _.findIndex(self.lstTaskScheduleDetailEmp, (ind: any) => {
							return ind.empId === self.dataScreen003A().employeeInfo[line].empId;
						});

					indTask = _.findIndex(self.lstChartTask, (indtsk: any) => {
						return indtsk.start == task.start && indtsk.end == task.end
					})

					if (indexTask != -1)
						indOld = _.findIndex(self.taskData[indexTask].taskScheduleDetail, (inod: any) => {
							return inod.timeSpanForCalcDto.start == task.start && inod.timeSpanForCalcDto.end == task.end
						})

					if (indexTaskNew != -1)
						indNew = _.findIndex(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail, (inw: any) => {
							return inw.timeSpanForCalcDto.start == task.start && inw.timeSpanForCalcDto.end == task.end
						})

					if (_.inRange(start, oldS, oldE) || _.inRange(end, oldS, oldE) ||
						_.inRange(oldS, start, end) || _.inRange(oldE, start, end)) {
						if (_.includes(taskInfo[0].code, task.code)) {
							if ((start == oldS && end == oldE))
								return;

							if (indOld != -1)
								_.remove(self.taskData[indexTask].taskScheduleDetail, (y: any, index) => {
									return y.timeSpanForCalcDto.start == oldS && y.timeSpanForCalcDto.end == oldE;
								});

							if (indNew != -1) {
								_.remove(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail, (y: any, index) => {
									return y.timeSpanForCalcDto.start == oldS && y.timeSpanForCalcDto.end == oldE;
								});

								if (self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail.length == 0) {
									_.remove(self.lstTaskScheduleDetailEmp, (y: any, index) => {
										return y.empId == self.lstTaskScheduleDetailEmp[indexTaskNew].empId;
									});
								}
							}

							if ((start < oldS && end < oldE && end > oldS)) {
								end = oldE;
								continue;
							}

							if (start > oldS && end > oldE && start < oldE) {
								start = oldS;
								continue;
							}

							if (start == oldE) {
								start = oldS;
								continue;
							}

							if (end == oldS) {
								end = oldE;
							}

						} else {
							let taskOld = _.filter(self.taskData, (x: any) => {
								return self.dataScreen003A().employeeInfo[line].empId == x.empID && x.taskScheduleDetail.length > 0;
							});

							taskOld = _.map(taskOld, (map: any) => {
								let taskScheduleDetails: any = [];
								map.taskScheduleDetail.forEach((tsd: any) => {
									taskScheduleDetails.push({
										taskCode: tsd.taskCode,
										timeSpanForCalcDto: {
											start: tsd.timeSpanForCalcDto.start,
											end: tsd.timeSpanForCalcDto.end
										},
									id : idResize
									})
								})

								return {
									empId: map.empID,
									taskScheduleDetail: taskScheduleDetails
								}
							})

							if (self.lstTaskScheduleDetailEmp.length == 0)
								self.lstTaskScheduleDetailEmp = taskOld;

							indexTaskNew = _.findIndex(self.lstTaskScheduleDetailEmp, (ind: any) => {
								return ind.empId === self.dataScreen003A().employeeInfo[line].empId;
							});

							if (indexTaskNew != -1)
								indNew = _.findIndex(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail, (inw: any) => {
									return inw.timeSpanForCalcDto.start == task.start && inw.timeSpanForCalcDto.end == task.end
								})

							if (start == oldS && end < oldE) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.start = end;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.start = end;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.end == oldE && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].start = end;

								continue;
							}

							if (start < oldS && end < oldE && end > oldS) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.start = end;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.start = end;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.end == oldE && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].start = end;

								continue;
							}

							if (start > oldS && end > oldE && start < oldE) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end = start;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end = start;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.start == oldS && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].end = start;

								continue;
							}

							if (start > oldS && end == oldE) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end = start;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end = start;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.end == oldS && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].end = start;

								continue;
							}

							if (start > oldS && end < oldE) {
								if (indOld != -1) {

									let filTask = _.filter(self.lstChartTask, (tsk: any) => {
										return tsk.line == line && _.inRange(end, tsk.start, tsk.end) && _.inRange(self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end, tsk.start, tsk.end + 1)
									})

									self.bindDataToTask(filTask != null ? filTask[0].code : null, end, self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end, line, `pgc${util.randomId().split("-").join("")}`, "add", idResize);

									indexTaskNew = _.findIndex(self.lstTaskScheduleDetailEmp, (ind: any) => {
										return ind.empId === self.dataScreen003A().employeeInfo[line].empId;
									})

									if (indexTaskNew != -1)
										indNew = _.findIndex(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail, (inw: any) => {
											return inw.timeSpanForCalcDto.start == task.start && inw.timeSpanForCalcDto.end == task.end
										})

									let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
										return inlc.start == filTask[0].start && line == inlc.line;
									})

									if (indexLstChart != -1)
										self.lstChartTask[indexLstChart].end = start;

									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end = start;

								}

								if (indNew != -1) {
									let filTask = _.filter(self.lstChartTask, (tsk: any) => {
										return tsk.line == line && _.inRange(end, tsk.start, tsk.end) && _.inRange(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end, tsk.start, tsk.end + 1)
									})

									self.bindDataToTask(filTask != null ? filTask[0].code : null, end, self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end, line, `pgc${util.randomId().split("-").join("")}`, "add", idResize);
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end = start;
									continue;
								}

								continue;
							}

						}

						if (indexTask != -1 && !_.isNil(self.taskData[indexTask]))
							indOld = _.findIndex(self.taskData[indexTask].taskScheduleDetail, (inod: any) => {
								return inod.timeSpanForCalcDto.start == task.start && inod.timeSpanForCalcDto.end == task.end
							})

						if (indexTaskNew != -1 && !_.isNil(self.lstTaskScheduleDetailEmp[indexTaskNew]))
							indNew = _.findIndex(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail, (inw: any) => {
								return inw.timeSpanForCalcDto.start == task.start && inw.timeSpanForCalcDto.end == task.end
							})
						
						if (start != oldE && end != oldS && indexTask != -1 && indOld != -1 && !_.isNil(self.taskData[indexTask]) && !_.isNil(self.taskData[indexTask].taskScheduleDetail[indOld]))
							arrRemoveTask.push({
								index: i,
								line: indexTask,
								start: self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.start,
								end: self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end,
								empId: self.taskData[indexTask].empID
							});

						if (start != oldE && end != oldS && indexTaskNew != -1 && indNew != -1 && !_.isNil(self.lstTaskScheduleDetailEmp[indexTaskNew]) && !_.isNil(self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew]))
							arrRemoveTaskNew.push({
								index: i,
								line: indexTaskNew,
								start: self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.start,
								end: self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end,
								empId: self.lstTaskScheduleDetailEmp[indexTaskNew].empId
							});
					} else {
						// Kiểm tra data khi kéo thanh chart task
						let checkExist = _.filter(newLstChartTsk, (inod: any) => {
							return inod.start == start || inod.end == end;
						})

						if (checkExist.length > 0) {
							if (oldS == checkExist[0].end) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.start = end;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.start = end;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.start == oldE && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].start = end;

								continue;
							}

							if (oldE == checkExist[0].start) {
								if (indOld != -1)
									self.taskData[indexTask].taskScheduleDetail[indOld].timeSpanForCalcDto.end = start;

								if (indNew != -1)
									self.lstTaskScheduleDetailEmp[indexTaskNew].taskScheduleDetail[indNew].timeSpanForCalcDto.end = start;

								let indexLstChart = _.findIndex(self.lstChartTask, (inlc: any) => {
									return inlc.start == oldS && line == inlc.line;
								})

								if (indexLstChart != -1)
									self.lstChartTask[indexLstChart].end = start;

								continue;
							}
						}
					}
				}

			}

			// Xóa data task không còn trên thanh chart ở data lấy từ DB
			_.forEach(arrRemoveTask, x => {
				_.remove(self.taskData[x.line].taskScheduleDetail, (y: any, index) => {
					return y.timeSpanForCalcDto.start == x.start && y.timeSpanForCalcDto.end == x.end;
				});

				_.remove(self.lstChartTask, (y: any, index) => {
					return y.start == x.start && y.end == x.end && x.line == y.line;
				});
			})

			// Xóa data task không còn trên thanh chart ở data trên màn hình
			_.forEach(arrRemoveTaskNew, x => {
				let indexLstChart = _.findIndex(self.lstTaskScheduleDetailEmp, (inlc: any) => {
					return inlc.empId == x.empId;
				})
				_.remove(self.lstTaskScheduleDetailEmp[indexLstChart].taskScheduleDetail, (y: any, index) => {
					return y.timeSpanForCalcDto.start == x.start && y.timeSpanForCalcDto.end == x.end;
				});

				_.remove(self.lstChartTask, (y: any, index) => {
					return y.start == x.start && y.end == x.end && x.line == y.line;
				});
			})

			let filShowChart = _.filter(self.gcShowChart, (x: any) => {
				return x.line == line && (_.inRange(x.startTime * 5, start, end) || _.inRange(x.endTime * 5, start, end));
			});

			filShowChart = _.uniqWith(filShowChart, function(arrVal: any, othVal: any) {
				return (arrVal.startTime == othVal.startTime);
			});

			// Kiểm tra data tạo ra khi kéo có trùng với breaktime không.
			let endAfterFilShowChart = -1;
			if (filShowChart.length > 0) {
				for (let i = 0; i < filShowChart.length; i++) {
					let sTime = filShowChart[i].startTime * 5,
						eTime = filShowChart[i].endTime * 5,
						sTimeNew = 0, eTimeNew = 0;
					if (_.inRange(sTime, start, end) || _.inRange(eTime, start, end)) {
						
						let indDel = _.findIndex(self.lstTaskScheduleDetailEmp, (inlc: any) => {
							return inlc.empId == self.dataScreen003A().employeeInfo[line].empId;
						});
						
						if (sTime > start && sTime < end && eTime > end){
							end = sTime;
							if (indDel != -1){
								_.remove(self.lstTaskScheduleDetailEmp[indDel].taskScheduleDetail, (y: any) => {
									return  y.id == idResize && (y.timeSpanForCalcDto.start == eTime || y.timeSpanForCalcDto.end == sTime);
							});
							}
						}

						if (sTime < start && eTime > sTime && eTime < end){
							start = eTime
							if (indDel != -1){
								_.remove(self.lstTaskScheduleDetailEmp[indDel].taskScheduleDetail, (y: any) => {
									return y.id == idResize && (y.timeSpanForCalcDto.start == eTime || y.timeSpanForCalcDto.end == sTime);
							});
						}
						}

						if (sTime > start && eTime < end) {
							sTimeNew = eTime;
							eTimeNew = end;
							if (endAfterFilShowChart == -1) {
								endAfterFilShowChart = sTime;
							}

							if (!_.isNil(filShowChart[i + 1]) && (_.inRange(filShowChart[i + 1].startTime * 5, start, end))) {
								eTimeNew = filShowChart[i + 1].startTime * 5;
							}

							if (i + 1 == filShowChart.length) {
								end = endAfterFilShowChart;
							}

							self.bindDataToTask(taskInfo != null ? taskInfo[0].code : null, sTimeNew, eTimeNew, line, `pgc${util.randomId().split("-").join("")}`, "add", idResize);
						}
					}
				}
			}

			self.bindDataToTask(taskInfo != null ? taskInfo[0].code : null, start, end, line, id, "add", idResize);

			self.enableSave(true);
		}

		bindDataToTask(code: any, start: any, end: any, line: any, id: any, type: any, idResize : any) {
			let self = this;

			let taskScheduleDetail: any = [];
			if (code != null && start != end) {
				taskScheduleDetail.push({
					taskCode: code,
					timeSpanForCalcDto: {
						start: start,
						end: end
					},
					id : idResize
				});
			};

			// kiểm tra line hiện tại có data trong taskData chưa
			let taskOld: any = _.filter(self.taskData, (x: any) => {
				return self.dataScreen003A().employeeInfo[line].empId == x.empID && x.taskScheduleDetail.length > 0;
			});

			// kiểm tra data mới có trong list trả ra để đăng ký chưa
			let oldNewTask = _.filter(self.lstTaskScheduleDetailEmp, (x: any) => {
				return self.dataScreen003A().employeeInfo[line].empId == x.empId;
			});

			if (taskOld.length == 0) {
				if (oldNewTask.length == 0) {
					self.lstTaskScheduleDetailEmp.push({
						empId: self.dataScreen003A().employeeInfo[line].empId,
						taskScheduleDetail: taskScheduleDetail
					});
				} else {
					let indTask = _.findIndex(self.lstTaskScheduleDetailEmp, (x: any) => {
						return self.dataScreen003A().employeeInfo[line].empId == x.empId;
					});

					let indx = _.filter(self.lstTaskScheduleDetailEmp[indTask].taskScheduleDetail, (x: any) => {
						return (start == x.timeSpanForCalcDto.start) && (end == x.timeSpanForCalcDto.end);
					});

					if (indx.length == 0)
						self.lstTaskScheduleDetailEmp[indTask].taskScheduleDetail.add(taskScheduleDetail[0]);
				}

			} else {
				/*if (oldNewTask.length == 0){
					self.lstTaskScheduleDetailEmp.push({
						empId : self.dataScreen003A().employeeInfo[line].empId,
						taskScheduleDetail : taskScheduleDetail
					});
				}*/
				taskOld = _.map(taskOld, (map: any) => {
					let taskScheduleDetails: any = [];
					map.taskScheduleDetail.forEach((tsd: any) => {
						taskScheduleDetails.push({
							taskCode: tsd.taskCode,
							timeSpanForCalcDto: {
								start: tsd.timeSpanForCalcDto.start,
								end: tsd.timeSpanForCalcDto.end
							},
							id : idResize
						})
					})

					return {
						empId: map.empID,
						taskScheduleDetail: taskScheduleDetails
					}
				})
				if (self.lstTaskScheduleDetailEmp.length == 0)
					self.lstTaskScheduleDetailEmp = taskOld;

				let index = _.findIndex(self.lstTaskScheduleDetailEmp, (ind: any) => {
					return ind.empId === self.dataScreen003A().employeeInfo[line].empId;
				})

				if (index != -1 && type == "add") {
					if (start != end)
						self.lstTaskScheduleDetailEmp[index].taskScheduleDetail.push({
							taskCode: code,
							timeSpanForCalcDto: {
								start: start,
								end: end
							},
							id : idResize
						});
				}

				if (index == -1 && type == "add") {
					if (taskScheduleDetail.length > 0)
					taskOld[0].taskScheduleDetail.push(taskScheduleDetail[0]);
					
					self.lstTaskScheduleDetailEmp.push(taskOld[0]);
				}

			};
			
			self.lstChartTask.push({
				start: start,
				end: end,
				id: id,
				line: line,
				code: code,
				idR : idResize
			});

			_.forEach(self.taskPasteData.lstTaskScheduleDetailEmp, (emp: any) => {
				let indexT = _.findIndex(self.lstTaskScheduleDetailEmp, (x: any) => {
					return x.empId === emp.empId;
				})

				if (indexT == -1) {
					self.lstTaskScheduleDetailEmp.push(emp);
				} else {
					_.forEach(emp.taskScheduleDetail, (lst: any, i) => {
						let fillAdd = _.find(self.lstTaskScheduleDetailEmp[indexT].taskScheduleDetail, (z: any, y) => {
							return lst.timeSpanForCalcDto.start == z.timeSpanForCalcDto.start && lst.timeSpanForCalcDto.end == z.timeSpanForCalcDto.end;
						})

						if (fillAdd == null) {
							if ((!_.inRange(lst.timeSpanForCalcDto.start, start, end) &&  !_.inRange(lst.timeSpanForCalcDto.end, start, end)) &&
							(!_.inRange(start, lst.timeSpanForCalcDto.start, lst.timeSpanForCalcDto.end) &&  !_.inRange(end, lst.timeSpanForCalcDto.start, lst.timeSpanForCalcDto.end))){
								if (lst.timeSpanForCalcDto.start != start && lst.timeSpanForCalcDto.start != end && 
								lst.timeSpanForCalcDto.end != start && lst.timeSpanForCalcDto.end != end ){
									self.lstTaskScheduleDetailEmp[indexT].taskScheduleDetail.push(lst);
								}
							} 
						}
					})
				}
			})

			self.taskPasteData = ({
				lstTaskScheduleDetailEmp: self.lstTaskScheduleDetailEmp,
				ymd: self.targetDate()
			});
		}

		pasteTask(value: any, code: any) {
			ruler.pasteChart({ typeName: code + "TASK", zIndex: 1990 });
		}

		delPasteTask() {
			ruler.pasteBand = undefined;
		}

		deleteTask(index: any, id: any) {
			let self = this;
			let filDel = _.filter(self.lstChartTask, (x: any) => {
				return x.id === id && x.line == index; // tim task bi xoa
			});

			if (filDel.length == 0) {
				return;
			}
			let indTaskData = _.findIndex(self.taskData, (ind: any) => {
				return ind.empID === self.lstEmpId[index].empId;
			})
			let filTask = _.isNil(self.taskData[indTaskData]) ? [] : self.taskData[indTaskData].taskScheduleDetail, lstTaskScheduleDetailEmp: any = [];

			lstTaskScheduleDetailEmp.push({
				empId: self.dataScreen003A().employeeInfo[index].empId,
				taskScheduleDetail: []
			});

			if (_.isEmpty(self.taskPasteData)) {
				self.taskPasteData = ({
					lstTaskScheduleDetailEmp: lstTaskScheduleDetailEmp,
					ymd: self.targetDate()
				});
			}
			if (filTask.length > 0) {
				let taskFil = _.filter(filTask, (x: any) => {
					return x.timeSpanForCalcDto.start != filDel[0].start && x.timeSpanForCalcDto.end != filDel[0].end;
				});

				let taskScheduleDetail: any = [];
				_.forEach(taskFil, (x: any) => {
					taskScheduleDetail.push({
						taskCode: x.taskCode,
						timeSpanForCalcDto: {
							start: x.timeSpanForCalcDto.start,
							end: x.timeSpanForCalcDto.end
						}
					})
				})
				let ind = _.findIndex(self.taskPasteData.lstTaskScheduleDetailEmp, (ind: any) => {
					return ind.empId === self.dataScreen003A().employeeInfo[index].empId;
				})
				if (ind != -1) {

					self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail = taskScheduleDetail;
				} else {
					self.taskPasteData.lstTaskScheduleDetailEmp.push({
						empId: self.dataScreen003A().employeeInfo[index].empId,
						taskScheduleDetail: taskScheduleDetail
					});
				}
			}

			_.forEach(filDel, (x: any) => {
				_.remove(self.lstChartTask, (y: any) => {
					return y.start == x.start && y.end == x.end && id == x.id;
				});
			})

			let ind = _.findIndex(self.taskPasteData.lstTaskScheduleDetailEmp, (x: any) => {
				return x.empId === self.dataScreen003A().employeeInfo[index].empId;
			})

			_.forEach(filDel, (x: any) => {
				if (!_.isEmpty(self.taskPasteData) && ind != -1)
					_.remove(self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail, (y: any) => {
						return y.taskCode == x.code && y.timeSpanForCalcDto.start == x.start && y.timeSpanForCalcDto.end == x.end;
					})

				if (filTask.length > 0 && !_.isNil(self.taskData[indTaskData])) {
					_.remove(self.taskData[indTaskData].taskScheduleDetail, (y: any) => {
						return y.timeSpanForCalcDto.start == x.start && y.timeSpanForCalcDto.end == x.end;
					});
				}
			})

			if (_.isEmpty(self.taskPasteData)) {
				self.taskPasteData = ({
					lstTaskScheduleDetailEmp: lstTaskScheduleDetailEmp,
					ymd: self.targetDate()
				});
			}

			if (!_.isNil(self.taskData[indTaskData]) && _.isEmpty(self.taskData[indTaskData].taskScheduleDetail) && filTask.length > 0) {
				if (ind != -1)
					self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail.length = 0;
			}
			// thuc hien xoa khi da co data task tu truoc
			if (!_.isNil(self.taskData[indTaskData]) && self.taskData[indTaskData].taskScheduleDetail.length > 0 && filTask.length > 0 && ind != -1) {
				if (self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail.length > 0 &&
					(self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail.length != self.taskData[indTaskData].taskScheduleDetail.length)) {
					self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail.length = 0;
					_.forEach(self.taskData[indTaskData].taskScheduleDetail, (x: any) => {
						self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail.push({
							taskCode: x.taskCode,
							timeSpanForCalcDto: {
								start: x.timeSpanForCalcDto.start,
								end: x.timeSpanForCalcDto.end
							}
						})
					});
				}
			}

			self.enableSave(true);
		}

		public pasteAll(index: any, id: any) {
			let self = this;
			let filRuler = ruler.gcChart[index][`lgc` + index],
				filTask = _.filter(self.lstChartTask, (x: any) => x.line == index),
				filTime = _.filter(filTask, (x: any) => {
					return id == x.id;
				}),
				filRulerR = ruler.gcChart[index][`rgc` + index];

			const filRulerC = ruler.gcChart[index];

			let filTaskPaste = _.filter(filRulerC, (x: any) => {
				return id == x.id;
			});

			let rulerUse = null;

			if (_.includes(filTaskPaste[0].parent, "lgc")) rulerUse = filRuler;
			if (_.includes(filTaskPaste[0].parent, "rgc")) rulerUse = filRulerR;

			let filDel = _.filter(ruler.gcChart[index], (x: any) => {
				return x.id != id && _.includes(x.definedType, "TASK");
			})

			_.forEach(filDel, (x: any) => {
				ruler.removeBy({ no: index, id: x.id });
			})

			if (rulerUse != null) {
				ruler.extend(index, id, rulerUse.start);
				ruler.extend(index, id, null, rulerUse.end);
			}

			let rulerUseNew = null;
			if (_.includes(filTaskPaste[0].parent, "lgc")) rulerUseNew = filRulerR;
			if (_.includes(filTaskPaste[0].parent, "rgc")) rulerUseNew = filRuler;

			if (rulerUseNew != null) {

				let newID = `pgc${util.randomId().split("-").join("")}`;

				ruler.addChartWithType(filTaskPaste[0].definedType, {
					name: filTaskPaste[0].title,
					color: filTaskPaste[0].color,
					lineWidth: 30,
					parent: rulerUseNew.id,
					canSlide: false,
					unitToPx: 3.5,
					id: newID,
					hide: ruler.loggable(self.selectedDisplayPeriod() == 1 ? false : true),
					canPaste: true,
					canPasteResize: true,
					lineNo: index,
					start: rulerUseNew.start,
					end: rulerUseNew.end,
					pastingResizeFinished: function(line: any, type: any, start: any, end: any) {
						let taskFilter = _.filter(ruler.gcChart[line], (tskF: any) => {
							return tskF.id === id;
						});

						if (_.isNil(start) || _.isNil(end)) {

							if (_.isNil(start))
								start = taskFilter[0].start;

							if (_.isNil(end))
								end = taskFilter[0].end;
						}

						if (_.isNil(start) && _.isNil(end)) {
							start = taskFilter[0].start;
							end = taskFilter[0].end;
						}
						console.log(line + "-" + type + "-" + start + "-" + end);
						self.addTaskResize(line, type, start, end, id);
					}
				});
			}

			let taskScheduleDetail: any = [];
			taskScheduleDetail.push({
				taskCode: !_.isNil(filTime[0]) ? filTime[0].code : filTaskPaste[0].definedType.split("TASK").join(""),
				timeSpanForCalcDto: {
					start: rulerUse.start * 5 + self.dispStart * 5,
					end: rulerUse.end * 5 + self.dispStart * 5
				}
			})

			if (rulerUseNew != null) {
				taskScheduleDetail.push({
					taskCode: !_.isNil(filTime[0]) ? filTime[0].code : filTaskPaste[0].definedType.split("TASK").join(""),
					timeSpanForCalcDto: {
						start: rulerUseNew.start * 5 + self.dispStart * 5,
						end: rulerUseNew.end * 5 + self.dispStart * 5
					}
				})
			}

			let lstTaskScheduleDetailEmp: any = [];
			lstTaskScheduleDetailEmp.push({
				empId: self.dataScreen003A().employeeInfo[index].empId,
				taskScheduleDetail: taskScheduleDetail
			});

			let ind = _.findIndex(self.taskPasteData.lstTaskScheduleDetailEmp, (x: any) => {
				return x.empId === self.dataScreen003A().employeeInfo[index].empId;
			})

			if (_.isEmpty(self.taskPasteData)) {
				self.taskPasteData = ({
					lstTaskScheduleDetailEmp: lstTaskScheduleDetailEmp,
					ymd: self.targetDate()
				});
			} else {

				if (ind == -1) {
					self.taskPasteData.lstTaskScheduleDetailEmp.push({
						empId: self.dataScreen003A().employeeInfo[index].empId,
						taskScheduleDetail: taskScheduleDetail
					});
				} else {
					self.taskPasteData.lstTaskScheduleDetailEmp[ind].taskScheduleDetail = taskScheduleDetail;
				}
			}
			let filShowChart = _.filter(self.gcShowChart, (x: any) => {
				return x.line == index;
			});

			filShowChart = _.uniqWith(filShowChart, function(arrVal: any, othVal: any) {
				return (arrVal.startTime == othVal.startTime && arrVal.endTime == othVal.endTime);
			});

			let indx = _.findIndex(self.taskPasteData.lstTaskScheduleDetailEmp, (x: any) => {
				return x.empId === self.dataScreen003A().employeeInfo[index].empId;
			});

			if (filShowChart.length > 0) {
				let lstTimeSave: any = [];
				_.forEach(self.taskPasteData.lstTaskScheduleDetailEmp[indx].taskScheduleDetail, (tsk: any) => {

					let start = tsk.timeSpanForCalcDto.start,
						end = tsk.timeSpanForCalcDto.end;

					let fillAdd = _.filter(filShowChart, (z: any, y) => {
						return _.inRange(z.startTime * 5, start, end) || _.inRange(z.endTime * 5, start, end);
					})

					if (fillAdd.length > 0)
						fillAdd = _.sortBy(fillAdd, [function(o: any) { return o.startTime; }]);
					else
						lstTimeSave.push({
							start: start,
							end: end
						});

					_.forEach(fillAdd, (sv: any, index) => {
						if (index == 0) {
							if (start != sv.startTime * 5 && start < sv.startTime * 5)
								lstTimeSave.push({
									start: start,
									end: sv.startTime * 5
								});
						} else {
							lstTimeSave.push({
								start: fillAdd[index - 1].endTime * 5,
								end: sv.startTime * 5
							});
						}

						if (index == fillAdd.length - 1) {
							if (sv.endTime * 5 != end) {
								lstTimeSave.push({
									start: sv.endTime * 5,
									end: end
								});
							} else {
								lstTimeSave.push({
									start: fillAdd[index - 1].endTime * 5,
									end: sv.startTime * 5
								});
							}

						}
					});
				})

				lstTimeSave = _.uniqWith(lstTimeSave, function(arrVal: any, othVal: any) {
					return (arrVal.start == othVal.start && arrVal.end == othVal.end);
				});

				self.taskPasteData.lstTaskScheduleDetailEmp[indx].taskScheduleDetail = [];
				_.forEach(lstTimeSave, (sv: any) => {
					self.taskPasteData.lstTaskScheduleDetailEmp[indx].taskScheduleDetail.push({
						taskCode: filTime[0].code,
						timeSpanForCalcDto: {
							start: sv.start,
							end: sv.end
						}
					})
				})
			} else {
				self.taskPasteData.lstTaskScheduleDetailEmp[indx].taskScheduleDetail = _.uniqWith(self.taskPasteData.lstTaskScheduleDetailEmp[indx].taskScheduleDetail, function(arrVal: any, othVal: any) {
					return (arrVal.timeSpanForCalcDto.start == othVal.timeSpanForCalcDto.start && arrVal.timeSpanForCalcDto.end == othVal.timeSpanForCalcDto.end);
				});
			}

			self.enableSave(true);
		}

		public setTaskMode(mode: string) {
			let self = this;

			if (self.localStore.workSelection == 1 && self.selectedDisplayPeriod() == 2)
				ruler.setMode(mode);

			if (self.localStore.workSelection == 0 && self.selectedDisplayPeriod() == 2)
				ruler.setMode(mode);

		}

		public getTaskInfo(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			let page = 1;
			if (!_.isNil(self.localStore)) {
				if (!_.isNil(self.localStore.pageNo)) {
					page = self.localStore.pageNo + 1;
				}

			}
			let param = {
				baseDate: self.targetDate(),
				targetUnit: self.dataScreen003A().unit,
				page: page,
				organizationID: self.dataScreen003A().id
			}
			service.getTaskInfo(param)
				.done((data: any) => {
					self.taskTypes = [];
					_.forEach(data.lstTaskDto, (x: any) => {
						let taskInfo: any = {
							data: {
								page: x.code,
								text: x.taskDisplayInfoDto.taskAbName,
								tooltip: x.taskDisplayInfoDto.taskName,
								color: x.taskDisplayInfoDto.color
							}
						}
						self.addTypeOfTask(taskInfo.data.color, taskInfo);
						__viewContext.viewModel.viewmodelAb.dataTaskInfo = data;
						__viewContext.viewModel.viewmodelAb.textButtonArr([]);
						__viewContext.viewModel.viewmodelAb.setDataTaskInfo();
					});

					dfd.resolve();
				}).fail(function(error) {
					alertError({ messageId: error.messageId });
					dfd.reject();
				}).always(function() {
				});
			return dfd.promise();
		}

        public openKSU003D(): void {
            let self = this;
            let data = {
                targetOrg: {
                    unit: self.dataFromA().unit,
                    workplaceId: self.dataFromA().workplaceId,
                    workplaceGroupId: self.dataFromA().workplaceGroupId,
                },
                employeeIds: _.map(self.lstEmpId, (x: any) => { return x.empId }),
                targetPeriod: {
                    startDate: self.dataFromA().daySelect,
                    endDate: self.dataFromA().daySelect
                }
            };
            setShared('dataShareKsu003D', data);
            nts.uk.ui.windows.sub.modal("/view/ksu/003/d/index.xhtml");
        }
		
		// 決定（A14_11）をクリックする (click A14_11)
		public closePopupA14() {
			let self = this;
			$('#A14').ntsPopup("hide");
			self.timeRange = self.selectedTimeRange() == 1 ? 24 : 48;// tổng số cột ở phần detail (24 or 48)
			self.initDispStart = self.initDispStartChecked();// thời gian bắt đầu của scroll phần detail
			self.dispStart = (self.dispStartChecked() * 60) / 5;// thời gian bắt đầu ở header phần detail
			self.dispStartHours = self.dispStartChecked();
			self.destroyAndCreateGrid(self.lstEmpId, 0);

			let param = {
				dispRange: self.selectedTimeRange() == 1 ? 0 : 1,
				startTime: self.dispStartChecked(),
				initDispStart: self.initDispStartChecked(),
				targetUnit: self.dataScreen003A().unit,
				organizationID: self.dataScreen003A().id
			}
			// 組織別スケジュール修正日付別の表示設定を登録する
			service.addScheduleByDisplaySet(param).done(() => {

			}).fail(function(error) {
				errorDialog({ messageId: error.messageId });
			}).always(function() {
			});
		}

		public closeDialog(): void {
			let self = this;
			self.checkCloseKsu003 = true;
			nts.uk.ui.windows.close();
		}
	}
}