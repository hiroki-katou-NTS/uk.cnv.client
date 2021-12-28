/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />


module nts.uk.at.view.kaf010.a.viewmodel {
	import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;
	import OvertimeWork = nts.uk.at.view.kaf010.shr.header.viewmodel.OvertimeWork;
	import WorkInfo = nts.uk.at.view.kaf010.shr.work_info.viewmodel.WorkInfo;
	import Work = nts.uk.at.view.kaf010.shr.work_info.viewmodel.Work;
	import WorkHours = nts.uk.at.view.kaf010.shr.work_info.viewmodel.WorkHours;
	import OverTime = nts.uk.at.view.kaf010.shr.time.viewmodel.OverTime;
	import RestTime = nts.uk.at.view.kaf010.shr.time.viewmodel.RestTime;
	import HolidayTime = nts.uk.at.view.kaf010.shr.time.viewmodel.HolidayTime;
	import formatTime = nts.uk.time.format.byId;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;

	@bean()
	export class Kaf010ViewModel extends Kaf000AViewModel {

		appType: KnockoutObservable<number> = ko.observable(AppType.HOLIDAY_WORK_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
		application: KnockoutObservable<Application> = ko.observable(new Application(this.appType()));
		isSendMail: KnockoutObservable<boolean>;
		overTimeWork: KnockoutObservableArray<OvertimeWork> = ko.observableArray([]);
		workInfo: KnockoutObservable<WorkInfo> = ko.observable(null);
		overTime: KnockoutObservableArray<OverTime> = ko.observableArray([]);
		restTime: KnockoutObservableArray<RestTime> = ko.observableArray([]);
		holidayTime: KnockoutObservableArray<HolidayTime> = ko.observableArray([]);
		comboDivergenceReason: KnockoutObservableArray<ComboDivergenceReason> = ko.observableArray([]);
		dataSource: AppHdWorkDispInfo;
		managementMultipleWorkCyclescheck: KnockoutObservable<boolean> = ko.observable(false);
		inputReflectDivergenceCheck : KnockoutObservable<boolean> = ko.observable(false);
		selectReflectDivergenceCheck: KnockoutObservable<boolean> = ko.observable(false);
		workTimeCheckBoxVisible: KnockoutObservable<boolean> = ko.observable(false);
		referenceButton: KnockoutObservable<boolean> = ko.observable(false);
		inputEnable: KnockoutObservable<boolean> = ko.observable(false);
		nightOvertimeReflectAtrCheck: KnockoutObservable<boolean> = ko.observable(true);
		restTimeTableVisible: KnockoutObservable<boolean> = ko.observable(false);
		overTimeTableVisible: KnockoutObservable<boolean> = ko.observable(false);
		restTimeTableVisible2: KnockoutObservable<boolean> = ko.observable(false);
		overTimeWorkVisible: KnockoutObservable<boolean> = ko.observable(false);
		isGoWorkAtr: KnockoutObservable<boolean> = ko.observable(false);
		isBackHomeAtr: KnockoutObservable<boolean> = ko.observable(false);
		selectedDivergenceReasonCode: KnockoutObservable<string> = ko.observable();
		divergenceReasonText: KnockoutObservable<string> = ko.observable();
		mode: KnockoutObservable<number> = ko.observable(MODE.NORMAL);
		employeeIdLst: Array<string>;
		isFromOther: boolean = false;

		constructor() {
			super();
			const vm = this;
			
		}

		created(params: AppInitParam) {
			const vm = this;

			let dataTransfer: DataTransfer;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					dataTransfer = __viewContext.transferred.value; // from spr		
					params = __viewContext.transferred.value;
				}
			}
			// __viewContext.transferred.value = undefined;

			vm.createRestTime();
			vm.setMode(params);
			// vm.createOverTime();

			let empList: Array<string> = [],
				dateList: Array<string> = [];
			if (!_.isNil(dataTransfer)) {
				if(!_.isNil(dataTransfer.appDate)){
					dateList.push(dataTransfer.appDate);
				}
			}	
			let screenCode: number = null;
			if (!_.isEmpty(params)) {
				if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
					screenCode = params.screenCode;
				}
				if (!_.isEmpty(params.employeeIds)) {
					empList = params.employeeIds;
				}
				if (!_.isEmpty(params.baseDate)) {
					let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
					dateList = [paramDate];
					vm.application().appDate(paramDate);
					vm.application().opAppStartDate(paramDate);
                    vm.application().opAppEndDate(paramDate);
				}
				if (params.isAgentMode) {
					vm.isAgentMode(params.isAgentMode);
				}
			}
			vm.isSendMail = ko.observable(false);
			if (!_.isNil(dataTransfer)) {
				if(!_.isNil(dataTransfer.appDate)){
					vm.application().appDate(dataTransfer.appDate);
					vm.application().opAppStartDate(dataTransfer.appDate);
					vm.application().opAppEndDate(dataTransfer.appDate);
				}
			}
			if (!_.isNil(params)) {
				if (!_.isNil(params.baseDate)) {
					vm.application().appDate(moment(params.baseDate).format('YYYY/MM/DD'));					
				}
			}
			
			let paramKAF000 = {
				empLst: empList, 
				dateLst: dateList, 
				appType: vm.appType(),
				screenCode
			};
			vm.$blockui("show");
			vm.loadData(paramKAF000)
				.then((loadDataFlag: any) => {
					vm.application().appDate.subscribe(value => {
						if (value) {
							vm.changeDate();
						}
					});
					// ※17
					vm.application().prePostAtr.subscribe(value => {
						if(vm.mode() == MODE.MULTiPLE_AGENT){
							return;
						}
						vm.itemControlHandler();
						if (value == 0) {
							$('.table-time2 .nts-fixed-header-wrapper').width(224);
							if (vm.holidayTime().length > 3) {
								$('.table-time2 .nts-fixed-body-wrapper').width(208);
							} else {
								$('.table-time2 .nts-fixed-body-wrapper').width(225);
							}
							
							$('.table-time3 .nts-fixed-header-wrapper').width(224);
							// if (vm.overTime().length > 6) {
								$('.table-time3 .nts-fixed-body-wrapper').width(208);
							// } else {
							// 	$('.table-time3 .nts-fixed-body-wrapper').width(225);
							// }
							
						} else {
							$('.table-time2 .nts-fixed-header-wrapper').width(455);
							$('.table-time2 .nts-fixed-body-wrapper').width(455);
							$('.table-time3 .nts-fixed-header-wrapper').width(455);
							$('.table-time3 .nts-fixed-body-wrapper').width(455);
						}
					});
					let workHours1 = {} as WorkHours;
					let workHours2 = {} as WorkHours;
					workHours1.start = ko.observable(null);
					workHours1.end = ko.observable(null);
					workHours2.start = ko.observable(null);
					workHours2.end = ko.observable(null);
					let workInfo = new WorkInfo();
					workInfo.workType({code: null, name: null});
					workInfo.workTime({code: null, name: null});
					workInfo.workHours1 = workHours1;
					workInfo.workHours2 = workHours2;
					vm.workInfo(workInfo);

					vm.workInfo().workHours1.start.subscribe((value) => {
						if (_.isNumber(value)) {
							vm.getBreakTimes();
						}
					});

					vm.workInfo().workHours1.end.subscribe((value) => {
						if (_.isNumber(value)) {
							vm.getBreakTimes();
						}
					});

					if (loadDataFlag) {
						let appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput),
							command = { 
									empList,
								 	dateList,
							 		appDispInfoStartupOutput,
									isAgent: vm.isAgentNew() 
								};
						return vm.$ajax(API.startNew, command);
					}
					
				}).then((successData: any) => {
					if (successData) {
						vm.dataSource = successData;
						vm.itemControlHandler();
						vm.bindOverTimeWorks(vm.dataSource);
						vm.bindWorkInfo(vm.dataSource, true);
						vm.bindRestTime(vm.dataSource);
						vm.bindHolidayTime(vm.dataSource, 1);
						vm.bindOverTime(vm.dataSource, 1);
						vm.setComboDivergenceReason(vm.dataSource);
						
						//prePostAtr = 2 when none is selected
						if (vm.application().prePostAtr() == 0 || vm.mode() == MODE.MULTiPLE_AGENT || _.isNil(vm.application().prePostAtr())) {
							$('.table-time2 .nts-fixed-header-wrapper').width(224);
							if (vm.holidayTime().length > 3) {
								$('.table-time2 .nts-fixed-body-wrapper').width(208);
							} else {
								$('.table-time2 .nts-fixed-body-wrapper').width(225);
							}
							
							$('.table-time3 .nts-fixed-header-wrapper').width(224);
							// if (vm.overTime().length > 6) {
								$('.table-time3 .nts-fixed-body-wrapper').width(208);
							// } else {
							// 	$('.table-time3 .nts-fixed-body-wrapper').width(225);
							// }
						} else {
							$('.table-time2 .nts-fixed-header-wrapper').width(455);
							$('.table-time2 .nts-fixed-body-wrapper').width(455);
							$('.table-time3 .nts-fixed-header-wrapper').width(455);
							$('.table-time3 .nts-fixed-body-wrapper').width(455);
						}
						if (!_.isNil(dataTransfer)) {
							if(!_.isNil(dataTransfer.appDate)){
								vm.application().prePostAtr(1);
							}
						}
					}
				}).fail((failData: any) => {
					if (failData.messageId === "Msg_43") {
						vm.$dialog.error(failData).then(() => { vm.$jump("com", "/view/ccg/008/a/index.xhtml"); });

					} else {
						vm.$dialog.error(failData);
					}
				}).always(() => {vm.$blockui("hide"); $('#kaf000-a-component4-singleDate').focus();});
		}

		mounted() {
			const self = this;
		}
		// ※20	
		setMode(params: any) {
			const self = this;
			if (_.isNil(params)) {
				self.mode(MODE.NORMAL);
			} else {
				if (params.isAgentMode) {
					if (!_.isEmpty(params.employeeIds)) {
						self.employeeIdLst = params.employeeIds;
						if (params.employeeIds.length == 1) {
							self.mode(MODE.SINGLE_AGENT);
						} else {
							self.mode(MODE.MULTiPLE_AGENT);
						}
					}
				}
			}
		}

		itemControlHandler() {
			const self = this;
			// ※28
			self.managementMultipleWorkCyclescheck(self.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles);
			
			if(self.application().prePostAtr() == 1){
				// ※27
				if (self.dataSource.hdWorkOvertimeReflect.holidayWorkAppReflect.after.othersReflect.reflectDivergentReasonAtr == 1 && 
					(self.dataSource.divergenceReasonInputMethod.length > 0 && self.dataSource.divergenceReasonInputMethod[0].divergenceReasonInputed == true)) {
					self.inputReflectDivergenceCheck(true);
				} else {
					self.inputReflectDivergenceCheck(false);
				}
				// ※26
				if (self.dataSource.hdWorkOvertimeReflect.holidayWorkAppReflect.after.othersReflect.reflectDivergentReasonAtr == 1 && 
					(self.dataSource.divergenceReasonInputMethod.length > 0 && self.dataSource.divergenceReasonInputMethod[0].divergenceReasonSelected == true)) {
					self.selectReflectDivergenceCheck(true);
				} else {
					self.selectReflectDivergenceCheck(false);
				}
			} else {
				self.inputReflectDivergenceCheck(false);
				self.selectReflectDivergenceCheck(false);
			}
			// ※23
			if (self.dataSource.holidayWorkAppSet.useDirectBounceFunction == 1 && self.dataSource.holidayWorkAppSet.applicationDetailSetting.timeCalUse == 1) {
				self.workTimeCheckBoxVisible(true);
			} else {
				self.workTimeCheckBoxVisible(false);
			}
			// ※22
			self.referenceButton(self.dataSource.hdWorkDispInfoWithDateOutput.subHdManage);
			// ※21
			if (self.dataSource.holidayWorkAppSet.applicationDetailSetting.timeInputUse == 1) { // co van de
				self.inputEnable(true)
			} else {
				self.inputEnable(false)
			}
			// ※18
			if (self.dataSource.hdWorkOvertimeReflect.nightOvertimeReflectAtr == 0) {
				self.nightOvertimeReflectAtrCheck(false);
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length})`).hide();
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length-1})`).hide();
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length-2})`).hide();
				// $(`#fixed-overtime-hour-table tr:nth-child(${self.overTime().length})`).hide();
			} else {
				self.nightOvertimeReflectAtrCheck(true);
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length})`).show();
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length-1})`).show();
				// $(`#fixed-table-holiday tr:nth-child(${self.holidayTime().length-2})`).show();
				// $(`#fixed-overtime-hour-table tr:nth-child(${self.overTime().length})`).show();
			}
			// ※16
			if (self.dataSource.hdWorkOvertimeReflect.holidayWorkAppReflect.after.breakLeaveApplication.breakReflectAtr == 1) {
				self.restTimeTableVisible(true);
			} else {
				self.restTimeTableVisible(false);
			}
			// ※15
			if (!_.isNil(self.dataSource.calculationResult)) {
				if (!_.isNil(self.dataSource.calculationResult.applicationTime)){
					let applicationTimes = self.dataSource.calculationResult.applicationTime.applicationTime;
					if (!_.isEmpty(applicationTimes)
					 && applicationTimes.filter(applicationTime => applicationTime.attendanceType == AttendanceType.NORMALOVERTIME && applicationTime.applicationTime > 0).length > 0) {
						self.overTimeTableVisible(true);
					} else {
						self.overTimeTableVisible(false);
					}
				} else {
					self.overTimeTableVisible(false);
				}
			} else {
				self.overTimeTableVisible(false);
			}
			// ※7
			if (self.dataSource.holidayWorkAppSet.applicationDetailSetting.timeCalUse == 1) {
				self.restTimeTableVisible2(true);
			} else {
				self.restTimeTableVisible2(false);
			}
			// ※6
			const c6 = 
					self.dataSource.holidayWorkAppSet.overtimeLeaveAppCommonSet.extratimeDisplayAtr == 1 
						&& self.mode() != MODE.MULTiPLE_AGENT;
			self.overTimeWorkVisible(c6);
		}

		toAppHolidayWork(){
			let vm = this;
			let appHolidayWork = {} as AppHolidayWorkCmd;
			let listApplicationTime = [] as Array<OvertimeApplicationSetting>
			let listMidNightHolidayTimes = [];
			let overTimeMidNight = null;
			for (let i = 0; i < vm.holidayTime().length; i++) {
				if (vm.holidayTime()[i].type() == 1 && !_.isNil(vm.holidayTime()[i].start()) && vm.holidayTime()[i].start() > 0) {
					let holidayTime = {} as OvertimeApplicationSetting;
					holidayTime.frameNo = vm.holidayTime()[i].frameNo();
					holidayTime.attendanceType = 1;
					holidayTime.applicationTime = vm.holidayTime()[i].start();
					listApplicationTime.push(holidayTime);
				}
				if (!_.isNil(vm.holidayTime()[i].legalClf()) && !_.isNil(vm.holidayTime()[i].start()) && vm.holidayTime()[i].start() > 0) {
					listMidNightHolidayTimes.push({attendanceTime: vm.holidayTime()[i].start(), legalClf: vm.holidayTime()[i].legalClf()});
				}
			}
			for (let i = 0; i < vm.overTime().length; i++) {
				if (vm.overTime()[i].type() == 0 && !_.isNil(vm.overTime()[i].applicationTime()) && vm.overTime()[i].applicationTime() > 0) {
					let overTime = {} as OvertimeApplicationSetting;
					overTime.frameNo = vm.overTime()[i].frameNo();
					overTime.attendanceType = 0;
					overTime.applicationTime = vm.overTime()[i].applicationTime();
					listApplicationTime.push(overTime);
				}
				if (vm.overTime()[i].type() === 100 && !_.isNil(vm.overTime()[i].applicationTime()) && vm.overTime()[i].applicationTime() > 0) {
					overTimeMidNight = vm.overTime()[i].applicationTime();
				}
			}
			let reasonDissociation = {} as ReasonDivergence;
			if(vm.selectedDivergenceReasonCode()){
				reasonDissociation.reasonCode = vm.selectedDivergenceReasonCode();
			}
			if(vm.divergenceReasonText()){
				reasonDissociation.reason = vm.divergenceReasonText();
			}
			reasonDissociation.diviationTime = 3;

			appHolidayWork.workInformation = {} as WorkInformationCommand;
			appHolidayWork.workInformation.workType = vm.workInfo().workType().code;
			appHolidayWork.workInformation.workTime = vm.workInfo().workTime().code;

			appHolidayWork.applicationTime = {} as ApplicationTime;
			appHolidayWork.applicationTime.applicationTime = listApplicationTime;
			appHolidayWork.applicationTime.reasonDissociation = [reasonDissociation];
			appHolidayWork.applicationTime.overTimeShiftNight = {} as OverTimeShiftNight;
			appHolidayWork.applicationTime.overTimeShiftNight.midNightHolidayTimes = listMidNightHolidayTimes;
			appHolidayWork.applicationTime.overTimeShiftNight.overTimeMidNight = overTimeMidNight;
			if(!_.isNil(vm.dataSource.calculationResult)){
				if(!_.isNil(vm.dataSource.calculationResult.applicationTime)){
					if(!_.isNil(vm.dataSource.calculationResult.applicationTime.overTimeShiftNight)){
						appHolidayWork.applicationTime.overTimeShiftNight.midNightOutSide 
							= vm.dataSource.calculationResult.applicationTime.overTimeShiftNight.midNightOutSide;
					}
				}
			}

			appHolidayWork.application = ko.toJS(vm.application);

			appHolidayWork.workingTimeList = [] as Array<TimeZoneWithWorkNoCommand>;
			if(!_.isNil(vm.workInfo().workHours1) && _.isNumber(vm.workInfo().workHours1.start()) && _.isNumber(vm.workInfo().workHours1.end())){
				let workingTime1 = {} as TimeZoneWithWorkNoCommand;
				workingTime1.workNo = 1;
				workingTime1.timeZone = {} as TimeZoneNewDto;
				workingTime1.timeZone.startTime = vm.workInfo().workHours1.start();
				workingTime1.timeZone.endTime = vm.workInfo().workHours1.end();
				appHolidayWork.workingTimeList.push(workingTime1);
			}
			
			if(!_.isNil(vm.workInfo().workHours2) && _.isNumber(vm.workInfo().workHours2.start()) && _.isNumber(vm.workInfo().workHours2.end())){
				let workingTime2 = {} as TimeZoneWithWorkNoCommand;
				workingTime2.workNo = 2;
				workingTime2.timeZone = {} as TimeZoneNewDto;
				workingTime2.timeZone.startTime = vm.workInfo().workHours2.start();
				workingTime2.timeZone.endTime = vm.workInfo().workHours2.end();
				appHolidayWork.workingTimeList.push(workingTime2);
			}

			appHolidayWork.goWorkAtr = vm.isGoWorkAtr();
			appHolidayWork.backHomeAtr = vm.isBackHomeAtr();

			appHolidayWork.breakTimeList = [] as Array<TimeZoneWithWorkNoCommand>;
			for (let i = 0; i < vm.restTime().length && i < 10; i++) {
				if(_.isNumber(vm.restTime()[i].start()) && _.isNumber(vm.restTime()[i].end())){
					let restTime = {} as TimeZoneWithWorkNoCommand;
					restTime.workNo = i+1;
					restTime.timeZone = {} as TimeZoneNewDto;
					restTime.timeZone.startTime = vm.restTime()[i].start();
					restTime.timeZone.endTime = vm.restTime()[i].end();
					appHolidayWork.breakTimeList.push(restTime);
				}		
			}
			return appHolidayWork;
		}
		
		register() {
			const vm = this;

			if(!_.isNil(vm.workInfo().workHours2) 
				&& ((!_.isNumber(vm.workInfo().workHours2.start()) && _.isNumber(vm.workInfo().workHours2.end()))
					 || (_.isNumber(vm.workInfo().workHours2.start()) && !_.isNumber(vm.workInfo().workHours2.end())))){
				vm.handleErrorCustom({messageId : "Msg_307"});
				return;
			}
			
			if (!vm.isAgentNew()){
				vm.registerSingle();
			} else {
				vm.registerMulti();
			}
		}
		
		isAgentNew() {
			const vm = this;
			let isMultipleEmp = true;
			if (_.isEmpty(vm.employeeIdLst)) {
				isMultipleEmp = false;
			} else {
				isMultipleEmp = vm.employeeIdLst.length != 1;
			}
			return isMultipleEmp;
		}

		registerSingle(){
			const vm = this;

			vm.$blockui("show");
			let appHolidayWork = vm.toAppHolidayWork();

			vm.$validate(
				'#kaf000-a-component4 .nts-input', 
				'#kaf000-a-component3-prePost', 
				'#kaf000-a-component5-comboReason', 
				'#kaf000-a-component5-textReason', 
				'#inpStartTime1', 
				'#inpEndTime1')
				.then(isValid => {
					if (isValid) {
						return true;
					}
				})
				.then(result => {
					// check before 
					let commandCheck = {
						require: true,
						companyId: vm.$user.companyId,
						appHdWorkDispInfo: vm.dataSource, 
						appHolidayWork: appHolidayWork,
						isProxy: false
					}
					
					if(result) {
						return vm.$ajax(API.checkBeforeRegister, commandCheck);
					}
				}).then((result: CheckBeforeOutput) => {
					if (!_.isNil(result)) {
						// xử lý confirmMsg
						return vm.handleConfirmMessage(result.confirmMsgOutputs);
					}
				}).then((result) => {
					if (result) {
						let commandRegister = {
							companyId: vm.$user.companyId,
							appHolidayWork: appHolidayWork,
							appHdWorkDispInfo: vm.dataSource, 
							appTypeSetting : vm.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting[0],
						};
						return vm.$ajax('at', API.register, commandRegister).then((successData) => {
							return vm.$dialog.info({ messageId: "Msg_15" }).then(() => {
								nts.uk.request.ajax("at", API.reflectApp, successData.reflectAppIdLst);
								CommonProcess.handleAfterRegister(successData, vm.isSendMail(), vm, false, vm.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.employeeInfoLst);
							});
						});
					}
					
				}).fail((failData: any) => {
					// xử lý lỗi nghiệp vụ riêng
					vm.handleErrorCustom(failData).then((result: any) => {
						if (result) {
							// xử lý lỗi nghiệp vụ chung
							vm.handleErrorCommon(failData);
						}
					});
				})
				.always(() => vm.$blockui("hide"));
		}

		handleErrorCustom(failData: any): any {
			const vm = this;
			if (!_.isEmpty(failData.errors)) {
				
				_.forEach(_.reverse(failData.errors), item => {
					
					vm.$dialog.error({ messageId: item.messageId, messageParams: item.parameterIds })
					.then(() => {
					});
				})
				return $.Deferred().resolve(false);	
			}
			if(failData.messageId == "Msg_750"
			|| failData.messageId == "Msg_1654"
			|| failData.messageId == "Msg_1508"
			|| failData.messageId == "Msg_424"
			|| failData.messageId == "Msg_1746"
			|| failData.messageId == "Msg_1745"
			|| failData.messageId == "Msg_1748"
			|| failData.messageId == "Msg_1747"
			|| failData.messageId == "Msg_1748"
			|| failData.messageId == "Msg_1656"
			|| failData.messageId == "Msg_1535"
			|| failData.messageId == "Msg_1536"
			|| failData.messageId == "Msg_1537"
			|| failData.messageId == "Msg_1538"
			|| failData.messageId == "Msg_1995"
			|| failData.messageId == "Msg_1996"
			|| failData.messageId == "Msg_1997"
			|| failData.messageId == "Msg_1998"
			|| failData.messageId == "Msg_1999"
			|| failData.messageId == "Msg_2000"
			|| failData.messageId == "Msg_2001"
			|| failData.messageId == "Msg_2002"
			|| failData.messageId == "Msg_2003"
			|| failData.messageId == "Msg_2004"
			|| failData.messageId == "Msg_2005"
			|| failData.messageId == "Msg_2008"
			|| failData.messageId == "Msg_2009"
			|| failData.messageId == "Msg_2010"
			|| failData.messageId == "Msg_2011"
			|| failData.messageId == "Msg_2012"
			|| failData.messageId == "Msg_2013"
			|| failData.messageId == "Msg_2014"
			|| failData.messageId == "Msg_2015"
			|| failData.messageId == "Msg_2019"
			|| failData.messageId == "Msg_2057") {
				return vm.$dialog.error({ messageId: failData.messageId, messageParams: failData.parameterIds })
				.then(() => {
					return $.Deferred().resolve(false);	
				});	
			}
			if(failData.messageId == "Msg_307"){
				return vm.$dialog.error({ messageId: failData.messageId})
				.then(() => {
					return $.Deferred().resolve(false);	
				});	
			}
			return $.Deferred().resolve(true);
		}

		registerMulti(){
			const vm = this;

			vm.$blockui("show");
			let appHolidayWork = vm.toAppHolidayWork();

			let approvalRootContentMap: any = null;

			vm.$validate(
				'#kaf000-a-component4 .nts-input', 
				'#kaf000-a-component3-prePost', 
				'#kaf000-a-component5-comboReason', 
				'#kaf000-a-component5-textReason', 
				'#inpStartTime1', 
				'#inpEndTime1')
				.then(isValid => {
					if (isValid) {
						return true;
					}
				})
				.then(result => {
					// check before 
					let commandCheck = {
						require: true,
						companyId: vm.$user.companyId,
						empList: vm.employeeIdLst,
						appHdWorkDispInfo: vm.dataSource, 
						appHolidayWork: appHolidayWork,
					}
					if(result) {
						return vm.$ajax(API.checkBeforeRegisterMulti, commandCheck);
					}
				}).then((result: CheckBeforeOutputMulti) => {
					if (!_.isNil(result)) {
						// xử lý confirmMsg
						approvalRootContentMap = result.approvalRootContentMap;
						return vm.handleConfirmMessageMap(result.confirmMsgOutputMap);
					}
				}).then((result) => {
					if (result) {
						let commandRegister = {
							companyId: vm.$user.companyId,
							empList: vm.employeeIdLst,
							appHdWorkDispInfo: vm.dataSource,
							appHolidayWork: appHolidayWork,
							approvalRootContentMap,
							appOvertimeDetailMap: {} as Map<string, any>,
							appTypeSetting : vm.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.applicationSetting.appTypeSetting[0],
						};
						return vm.$ajax('at', API.registerMulti, commandRegister).then((successData) => {
							return vm.$dialog.info({ messageId: "Msg_15" }).then(() => {
								nts.uk.request.ajax("at", API.reflectApp, successData.reflectAppIdLst);
								CommonProcess.handleAfterRegister(successData, vm.isSendMail(), vm, true, vm.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.employeeInfoLst);
							});
						});
					}
					
				})
				.done(result => {
					if(result){
						location.reload();
					}
				})
				.fail((failData: any) => {
					// xử lý lỗi nghiệp vụ riêng
					vm.handleErrorCustom(failData).then((result: any) => {
						if (result) {
							// xử lý lỗi nghiệp vụ chung
							vm.handleErrorCommon(failData);
						}
					});
				})
				.always(() => vm.$blockui("hide"));
		}

		setComboDivergenceReason(res: AppHdWorkDispInfo) {
			const self = this;
			res.divergenceReasonInputMethod
			if(res.divergenceReasonInputMethod.length > 0 && res.divergenceReasonInputMethod[0].reasons.length > 0){
				let comboBoxOptions: Array<ComboDivergenceReason> = res.divergenceReasonInputMethod[0].reasons
						.map(reason => {
							reason.comboBoxText = reason.divergenceReasonCode + ' ' + reason.reason;
							return reason;
						});
				self.comboDivergenceReason(comboBoxOptions);
			}
			let defaultReasonTypeItem = _.find(res.appDispInfoStartupOutput.appDispInfoNoDateOutput.reasonTypeItemLst, (o) => o.defaultValue);
			if(_.isUndefined(defaultReasonTypeItem)) {
				let dataLst = [{
					divergenceReasonCode: '',
					reason: self.$i18n('KAFS00_23'),
					reasonRequired: 1,
					comboBoxText: self.$i18n('KAFS00_23'),
				}];
				self.comboDivergenceReason(_.concat(dataLst, self.comboDivergenceReason()));
				self.selectedDivergenceReasonCode(_.head(self.comboDivergenceReason()).divergenceReasonCode);
			} else {
				self.selectedDivergenceReasonCode(defaultReasonTypeItem.divergenceReasonCode);
			}
		}

		bindOverTimeWorks(res: AppHdWorkDispInfo) { // dummy data
			const self = this;
			if(!self.overTimeWorkVisible()){
				return;
			}
			const { otWorkHoursForApplication } = res;
			let overTimeWorks = [];
			{
				let item = new OvertimeWork();
				item.yearMonth = ko.observable(otWorkHoursForApplication.currentMonth);
				if (otWorkHoursForApplication.isCurrentMonth) {
					item.limitTime = ko.observable(otWorkHoursForApplication.currentTimeMonth.legalMaxTime.threshold.erAlTime.error);
					item.actualTime = ko.observable(otWorkHoursForApplication.currentTimeMonth.legalMaxTime.agreementTime);
				} else {
					item.limitTime = ko.observable(otWorkHoursForApplication.currentTimeMonth.agreementTime.threshold.erAlTime.error);
					item.actualTime = ko.observable(otWorkHoursForApplication.currentTimeMonth.agreementTime.agreementTime);
				}
				const currentTimeMonth = otWorkHoursForApplication.currentTimeMonth
				
				// 正常
				if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.NORMAL) {
					
				// 限度アラーム時間超過	
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM) {
					item.backgroundColor(COLOR_36.alarm);
					item.textColor(COLOR_36.alarm_character);
				// 限度エラー時間超過	
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR) {
					item.backgroundColor(COLOR_36.error);
					item.textColor(COLOR_36.error_letter);
				// 正常（特例あり）	
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.NORMAL_SPECIAL) {
					
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM_SP) {
					item.backgroundColor(COLOR_36.exceptions);
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP) {
					item.backgroundColor(COLOR_36.exceptions);
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM) {
					item.backgroundColor(COLOR_36.alarm);
					item.textColor(COLOR_36.alarm_character);
				} else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR) {
					item.backgroundColor(COLOR_36.error);
					item.textColor(COLOR_36.error_letter);
				}  else if (currentTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY) {
					item.backgroundColor(COLOR_36.bg_upper_limit);
					item.textColor(COLOR_36.color_upper_limit);
				}
				
				
				
				
				
				
				
				overTimeWorks.push(item);
			}
			{
				let item = new OvertimeWork();
				item.yearMonth = ko.observable(otWorkHoursForApplication.nextMonth);
				if (otWorkHoursForApplication.isNextMonth) {
					item.limitTime = ko.observable(otWorkHoursForApplication.nextTimeMonth.legalMaxTime.threshold.erAlTime.error);
					item.actualTime = ko.observable(otWorkHoursForApplication.nextTimeMonth.legalMaxTime.agreementTime);
				} else {
					item.limitTime = ko.observable(otWorkHoursForApplication.nextTimeMonth.agreementTime.threshold.erAlTime.error);
					item.actualTime = ko.observable(otWorkHoursForApplication.nextTimeMonth.agreementTime.agreementTime);
				}
				
				
				const nextTimeMonth = otWorkHoursForApplication.nextTimeMonth;
				// 正常
				if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.NORMAL) {
					
				// 限度アラーム時間超過	
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM) {
					item.backgroundColor(COLOR_36.alarm);
					item.textColor(COLOR_36.alarm_character);
				// 限度エラー時間超過	
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR) {
					item.backgroundColor(COLOR_36.error);
					item.textColor(COLOR_36.error_letter);
				// 正常（特例あり）	
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.NORMAL_SPECIAL) {
					
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM_SP) {
					item.backgroundColor(COLOR_36.exceptions);
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP) {
					item.backgroundColor(COLOR_36.exceptions);
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM) {
					item.backgroundColor(COLOR_36.alarm);
					item.textColor(COLOR_36.alarm_character);
				} else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR) {
					item.backgroundColor(COLOR_36.error);
					item.textColor(COLOR_36.error_letter);
				}   else if (nextTimeMonth.status == AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY) {
					item.backgroundColor(COLOR_36.bg_upper_limit);
					item.textColor(COLOR_36.color_upper_limit);
				}
				
				
				overTimeWorks.push(item);
			}
			self.overTimeWork(overTimeWorks);
		}

		bindWorkInfo(res: AppHdWorkDispInfo, check?: boolean) { // dummy data 
			const self = this;
			const { hdWorkDispInfoWithDateOutput } = res;
			
			// let workHours1 = {} as WorkHours;
			// let workHours2 = {} as WorkHours;
			// if (!ko.toJS(self.workInfo)) {
			// 	workHours1.start = ko.observable(null);
			// 	workHours1.end = ko.observable(null);
			// 	workHours2.start = ko.observable(null);
			// 	workHours2.end = ko.observable(null);
			// 	if(hdWorkDispInfoWithDateOutput && hdWorkDispInfoWithDateOutput.workHours){
			// 		workHours1.start(hdWorkDispInfoWithDateOutput.workHours.startTimeOp1);
			// 		workHours1.end(hdWorkDispInfoWithDateOutput.workHours.endTimeOp1);
			// 		workHours2.start(hdWorkDispInfoWithDateOutput.workHours.startTimeOp2);
			// 		workHours2.end(hdWorkDispInfoWithDateOutput.workHours.endTimeOp2);
			// 	}
			// } else {
				
			// }
			
			let workInfo = new WorkInfo();
			if (check) {
				workInfo.workType({code: hdWorkDispInfoWithDateOutput.initWorkType, name: hdWorkDispInfoWithDateOutput.initWorkTypeName});
				workInfo.workTime({code: hdWorkDispInfoWithDateOutput.initWorkTime, name: hdWorkDispInfoWithDateOutput.initWorkTimeName});
			} else {
				workInfo = self.workInfo();
			}
			let workHours1 = self.workInfo().workHours1;
				let workHours2 = self.workInfo().workHours2;
				if(hdWorkDispInfoWithDateOutput && hdWorkDispInfoWithDateOutput.workHours){
					workHours1.start(hdWorkDispInfoWithDateOutput.workHours.startTimeOp1);
					workHours1.end(hdWorkDispInfoWithDateOutput.workHours.endTimeOp1);
					workHours2.start(hdWorkDispInfoWithDateOutput.workHours.startTimeOp2);
					workHours2.end(hdWorkDispInfoWithDateOutput.workHours.endTimeOp2);
				}
			workInfo.workHours1 = workHours1;
			workInfo.workHours2 = workHours2;
			self.workInfo(workInfo);
		}

		bindRestTime(res: AppHdWorkDispInfo) {
			const self = this;
			const { hdWorkDispInfoWithDateOutput } = res;
			let restTimeArray = self.restTime() as Array<RestTime>;
			_.forEach(restTimeArray, (item: RestTime) => {
				item.start(null);
				item.end(null);
			})
			if (!_.isNil(hdWorkDispInfoWithDateOutput)) {
				let breakTime = hdWorkDispInfoWithDateOutput.breakTimeZoneSettingList;
				if (!_.isNil(breakTime)) {
					if (!_.isEmpty(breakTime.timeZones)) {
						_.forEach(breakTime.timeZones, (item: TimeZone, index) => {
							if (Number(index) < 10) {
								let restItem = restTimeArray[index] as RestTime;
								restItem.start(item.start);
								restItem.end(item.end);
							}
						})
					}
				}
			}
			self.restTime(_.clone(restTimeArray));
		}

		bindHolidayTime(res: AppHdWorkDispInfo, mode?: number) {
			const self = this;
			let holidayTimeArray = [] as Array<HolidayTime>;

			for (let i = 0; i < res.workdayoffFrameList.length; i++) {
				let item = {} as HolidayTime;
				item.frameNo = ko.observable(res.workdayoffFrameList[i].workdayoffFrNo);
				item.frameName = ko.observable(res.workdayoffFrameList[i].workdayoffFrName);
				item.start = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
				item.preApp = ko.observable(null);
				item.actualTime = ko.observable(null);
				item.type = ko.observable(1);
				item.legalClf = ko.observable(null);
				item.backgroundColor = ko.observable('');
				holidayTimeArray.push(item);
			}

			if(self.nightOvertimeReflectAtrCheck()){
				{	// A6_27
					let item = {} as HolidayTime;
					item.frameNo = ko.observable(holidayTimeArray.length + 1);
					item.frameName = ko.observable(self.$i18n('KAF010_342'));
					item.start = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
					item.preApp = ko.observable(null);
					item.actualTime = ko.observable(null);
					item.legalClf = ko.observable(0);
					item.type = ko.observable(6);
					item.backgroundColor = ko.observable('');
					holidayTimeArray.push(item);
				}
	
				{
					let item = {} as HolidayTime;
					item.frameNo = ko.observable(holidayTimeArray.length + 1);
					item.frameName = ko.observable(self.$i18n('KAF010_343'));
					item.start = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
					item.preApp = ko.observable(null);
					item.actualTime = ko.observable(null);
					item.legalClf = ko.observable(1);
					item.type = ko.observable(7);
					item.backgroundColor = ko.observable('');
					holidayTimeArray.push(item);
				}
	
				{
					let item = {} as HolidayTime;
					item.frameNo = ko.observable(holidayTimeArray.length + 1);
					item.frameName = ko.observable(self.$i18n('KAF010_344'));
					item.start = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
					item.preApp = ko.observable(null);
					item.actualTime = ko.observable(null);
					item.legalClf = ko.observable(2);
					item.type = ko.observable(8);
					item.backgroundColor = ko.observable('');
					holidayTimeArray.push(item);
				}
			}

			if (mode == 1) {
				let calculationResultOp = res.calculationResult;
					// A6_8
				if (!_.isEmpty(calculationResultOp)) {
						let applicationTime = calculationResultOp.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {	
							_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
								holidayTimeArray
									.filter(holidayTime => holidayTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.BREAKTIME)
									.map(holidayTime => holidayTime.start(item.applicationTime));
							})	
						}
						// A6_28 , A6_35, A6_40
						if(self.nightOvertimeReflectAtrCheck()){
							let appRoot = calculationResultOp.applicationTime;
							if (!_.isNil(appRoot.overTimeShiftNight)) {
								let midNightHolidayTimes = appRoot.overTimeShiftNight.midNightHolidayTimes;
								if (!_.isEmpty(midNightHolidayTimes)) {
									_.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
										if (item.legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_BREAK_TIME)
												.map(holidayTime => holidayTime.start(item.attendanceTime));
										} else if (item.legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY)
												.map(holidayTime => holidayTime.start(item.attendanceTime));
										} else if (item.legalClf == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY)
												.map(holidayTime => holidayTime.start(item.attendanceTime));
										}
									});
								}
							}
						}
				}
				// A6_9
				// 申請表示情報．申請表示情報(基準日関係あり)．表示する事前申請内容．残業申請．申請時間．申請時間．申請時間
				let opPreAppContentDisplayLst = res.appDispInfoStartupOutput.appDispInfoWithDateOutput.opPreAppContentDispDtoLst;
				if (!_.isEmpty(opPreAppContentDisplayLst)) {
					let appHolidayWork = opPreAppContentDisplayLst[0].appHolidayWork;
					if (appHolidayWork) {
						let applicationTime = appHolidayWork.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
								holidayTimeArray
									.filter(holidayTime => holidayTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.BREAKTIME)
									.map(holidayTime => holidayTime.preApp(item.applicationTime));
							})
						}
						// A6_29 A6_36 A6_41
						if(self.nightOvertimeReflectAtrCheck()){
							let appRoot = appHolidayWork.applicationTime;
							if (!_.isNil(appRoot.overTimeShiftNight)) {
								let midNightHolidayTimes = appRoot.overTimeShiftNight.midNightHolidayTimes;
								if (!_.isEmpty(midNightHolidayTimes)) {
									_.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
										if (item.legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_BREAK_TIME)
												.map(holidayTime => holidayTime.preApp(item.attendanceTime));
										} else if (item.legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY)
												.map(holidayTime => holidayTime.preApp(item.attendanceTime));
										} else if (item.legalClf == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
											holidayTimeArray
												.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY)
												.map(holidayTime => holidayTime.preApp(item.attendanceTime));
										}
									});
								}
							}
						}
					}	
				}
				// A6_11
				if (!_.isNil(res.hdWorkDispInfoWithDateOutput)) {
					if (!_.isEmpty(res.hdWorkDispInfoWithDateOutput.actualApplicationTime)) {
						let actualApplicationTime = res.hdWorkDispInfoWithDateOutput.actualApplicationTime;
						if (actualApplicationTime) {
							let applicationTime = actualApplicationTime.applicationTime;
							if (!_.isEmpty(applicationTime)) {
								_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
									holidayTimeArray
										.filter(holidayTime => holidayTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.BREAKTIME)
										.map(holidayTime => holidayTime.actualTime(item.applicationTime));
								})
							}
						}
						// A6_30 A6_37 A6_42
						if(self.nightOvertimeReflectAtrCheck()){
							let appRoot = res.hdWorkDispInfoWithDateOutput.actualApplicationTime;
								if (!_.isNil(appRoot.overTimeShiftNight)) {
									let midNightHolidayTimes = appRoot.overTimeShiftNight.midNightHolidayTimes;
									if (!_.isEmpty(midNightHolidayTimes)) {
										_.forEach(midNightHolidayTimes, (item: HolidayMidNightTime) => {
											if (item.legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
												holidayTimeArray
													.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_BREAK_TIME)
													.map(holidayTime => holidayTime.actualTime(item.attendanceTime));
											} else if (item.legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
												holidayTimeArray
													.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY)
													.map(holidayTime => holidayTime.actualTime(item.attendanceTime));
											} else if (item.legalClf == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
												holidayTimeArray
													.filter(holidayTime => holidayTime.type() == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY)
													.map(holidayTime => holidayTime.actualTime(item.attendanceTime));
											}
										});
									}
								}
							}
						}
				}
			}

			self.holidayTime(holidayTimeArray);
			self.setColorForHolidayTime(self.dataSource.calculationResult && self.dataSource.calculationResult.calculatedFlag == 0, self.dataSource);
		}

		setColorForHolidayTime(isCalculation: Boolean, dataSource: AppHdWorkDispInfo) {
			const self = this;
			if (!isCalculation || _.isNil(dataSource.calculationResult)) {
				return;
			}
			let holidayTimes = self.holidayTime() as Array<HolidayTime>;
			let overStateOutput = dataSource.calculationResult.actualOvertimeStatus;
			_.forEach(holidayTimes, (item: HolidayTime) => {
				let backgroundColor = '';
				if (item.type() == AttendanceType.BREAKTIME) {
					// ・計算値：「残業申請の表示情報．計算結果」を確認する
					if (!_.isNil(dataSource.calculationResult.applicationTime)) {
						let applicationTime = dataSource.calculationResult.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							let result = applicationTime
								.filter((appTime : OvertimeApplicationSetting) => appTime.frameNo == item.frameNo() && appTime.attendanceType == AttendanceType.BREAKTIME);
							if (!_.isEmpty(result)) {
								if (result[0].applicationTime > 0) {
									backgroundColor = BACKGROUND_COLOR.bgC1;									
								}
							}
						}
					}
					if (!_.isNil(overStateOutput)) {
						// ・事前申請超過：「残業申請の表示情報．計算結果．事前申請・実績の超過状態」を確認する
						if (!_.isNil(overStateOutput.advanceExcess)) {
							let excessStateDetail = overStateOutput.advanceExcess.excessStateDetail;
							if (!_.isEmpty(excessStateDetail)) {
								let result = excessStateDetail
									.filter((excessDetail : ExcessStateDetail) => excessDetail.frame == item.frameNo() && excessDetail.type == AttendanceType.BREAKTIME);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC4;									
									}
								}
							}
							
						}
						// ・実績申請超過：「残業申請の表示情報．計算結果．事前申請・実績の超過状態」を確認する
						if (!_.isNil(overStateOutput.achivementExcess)) {
							let excessStateDetail = overStateOutput.achivementExcess.excessStateDetail;
							if (!_.isEmpty(excessStateDetail)) {
								let result = excessStateDetail
									.filter((excessDetail : ExcessStateDetail) => excessDetail.frame == item.frameNo() && excessDetail.type == AttendanceType.BREAKTIME);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ERROR) {
										backgroundColor = BACKGROUND_COLOR.bgC2;									
									} else if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC3;
									}
								}
							}
							
						}
						
					}
				} else if (item.type() == AttendanceType.MIDDLE_BREAK_TIME) {
					// 計算結果．申請時間．就業時間外深夜時間．休出深夜時間．時間 > 0
					// 法定区分 = 法定内休出
					if (!_.isNil(overStateOutput)) {
						if (!_.isEmpty(dataSource.calculationResult.applicationTime)) {
							let overTimeShiftNight = dataSource.calculationResult.applicationTime.overTimeShiftNight;
							if (!_.isNil(overTimeShiftNight)) {
								if (!_.isEmpty(overTimeShiftNight.midNightHolidayTimes)) {
									let result = overTimeShiftNight.midNightHolidayTimes
										.filter((midNightHolidayTime : HolidayMidNightTime) => midNightHolidayTime.legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork);
									if (!_.isEmpty(result)) {
										if (result[0].attendanceTime > 0) {
											backgroundColor = BACKGROUND_COLOR.bgC1;							
										}
									}
								}
							}
						}
						
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．法定区分 = 法定内休出
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．超過状態 = 超過アラーム
						if (!_.isNil(_.get(overStateOutput, 'advanceExcess'))) {
							let excessStateMidnight = overStateOutput.advanceExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC4;							
									}
								}
							}
						}
						if (!_.isNil(overStateOutput.achivementExcess)) {
							let excessStateMidnight = overStateOutput.achivementExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC3;							
									} else if (result[0].excessState == ExcessState.EXCESS_ERROR){
										backgroundColor = BACKGROUND_COLOR.bgC2;
									}
								}
							}
						}
						
					}
				} else if (item.type() == AttendanceType.MIDDLE_EXORBITANT_HOLIDAY) {
					// 計算結果．申請時間．就業時間外深夜時間．休出深夜時間．時間 > 0
					// 法定区分 = 法定内休出
					if (!_.isNil(overStateOutput)) {
						if (!_.isEmpty(dataSource.calculationResult.applicationTime)) {
							let overTimeShiftNight = dataSource.calculationResult.applicationTime.overTimeShiftNight;
							if (!_.isNil(overTimeShiftNight)) {
								if (!_.isEmpty(overTimeShiftNight.midNightHolidayTimes)) {
									let result = overTimeShiftNight.midNightHolidayTimes
										.filter((midNightHolidayTime : HolidayMidNightTime) => midNightHolidayTime.legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork);
									if (!_.isEmpty(result)) {
										if (result[0].attendanceTime > 0) {
											backgroundColor = BACKGROUND_COLOR.bgC1;							
										}
									}
								}
							}
						}
						
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．法定区分 = 法定内休出
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．超過状態 = 超過アラーム
						if (!_.isNil(overStateOutput.advanceExcess)) {
							let excessStateMidnight = overStateOutput.advanceExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC4;							
									}
								}
							}
						}
						
						if (!_.isNil(overStateOutput.achivementExcess)) {
							let excessStateMidnight = overStateOutput.achivementExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC3;							
									} else if (result[0].excessState == ExcessState.EXCESS_ERROR){
										backgroundColor = BACKGROUND_COLOR.bgC2;
									}
								}
							}
						}
						
					}
					
				} else if (item.type() == AttendanceType.MIDDLE_HOLIDAY_HOLIDAY) {
					// 計算結果．申請時間．就業時間外深夜時間．休出深夜時間．時間 > 0
					// 法定区分 = 法定内休出
					if (!_.isNil(overStateOutput)) {
						if (!_.isEmpty(dataSource.calculationResult.applicationTime)) {
							let overTimeShiftNight = dataSource.calculationResult.applicationTime.overTimeShiftNight;
							if (!_.isNil(overTimeShiftNight)) {
								if (!_.isEmpty(overTimeShiftNight.midNightHolidayTimes)) {
									let result = overTimeShiftNight.midNightHolidayTimes
										.filter((midNightHolidayTime : HolidayMidNightTime) => midNightHolidayTime.legalClf == StaturoryAtrOfHolidayWork.PublicHolidayWork);
									if (!_.isEmpty(result)) {
										if (result[0].attendanceTime > 0) {
											backgroundColor = BACKGROUND_COLOR.bgC1;							
										}
									}
								}
							}
						}
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．法定区分 = 法定内休出
						// 事前申請・実績の超過状態．事前超過．休出深夜時間．超過状態 = 超過アラーム
						if (!_.isNil(overStateOutput.advanceExcess)) {
							let excessStateMidnight = overStateOutput.advanceExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.PublicHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC4;							
									}
								}
							}
						}
						
						if (!_.isNil(overStateOutput.achivementExcess)) {
							let excessStateMidnight = overStateOutput.achivementExcess.excessStateMidnight;
							if (!_.isEmpty(excessStateMidnight)) {
								let result = excessStateMidnight
										.filter((excessMidnight : ExcessStateMidnight) => excessMidnight.legalCfl == StaturoryAtrOfHolidayWork.PublicHolidayWork);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC3;							
									} else if (result[0].excessState == ExcessState.EXCESS_ERROR){
										backgroundColor = BACKGROUND_COLOR.bgC2;
									}
								}
							}
						}
						
					}
					
				}
				if (item.start() > 0) {
					item.backgroundColor(backgroundColor);				
				}

			});
		}

		bindOverTime(res: AppHdWorkDispInfo, mode?: number) {
			const self = this;
			let overTimeArray = [] as Array<OverTime>;

			for (let i = 0; i < res.overtimeFrameList.length; i++) {
				let item = {} as OverTime;
				item.frameNo = ko.observable(res.overtimeFrameList[i].overtimeWorkFrNo);
				item.frameName = ko.observable(res.overtimeFrameList[i].overtimeWorkFrName);
				item.applicationTime = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
				item.preTime = ko.observable(null);
				item.actualTime = ko.observable(null);
				item.type = ko.observable(0);
				item.backgroundColor = ko.observable('');
				overTimeArray.push(item);
			}

			if(self.nightOvertimeReflectAtrCheck()){
				let item = {} as OverTime;
				item.frameNo = ko.observable(overTimeArray.length + 1);
				item.frameName = ko.observable(self.$i18n('KAF005_63'));
				item.applicationTime = ko.observable(res.calculationResult && res.calculationResult.calculatedFlag == 0 ? 0 : null);
				item.preTime = ko.observable(null);
				item.actualTime = ko.observable(null);
				item.type = ko.observable(null);
				item.type = ko.observable(100);
				item.backgroundColor = ko.observable('');
				overTimeArray.push(item);
			}
			// A7_8
			let calculationResultOp = res.calculationResult;
			if (!_.isNil(calculationResultOp)) {
				if (!_.isEmpty(calculationResultOp.applicationTime)) {
					let applicationTime = calculationResultOp.applicationTime.applicationTime;
					if (!_.isEmpty(applicationTime)) {
						_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
							overTimeArray
									.filter(overTime => overTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.NORMALOVERTIME)
									.map(overTime => overTime.applicationTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? item.applicationTime : null));
						});
					}
				}
			}

			if (mode == 1) {
					
				let calculationResultOp = res.calculationResult;
				if (!_.isNil(calculationResultOp)) {
					if (!_.isEmpty(calculationResultOp.applicationTime)) {
						// A7_8
						let applicationTime = calculationResultOp.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
								overTimeArray
									.filter(overTime => overTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.NORMALOVERTIME)
									.map(overTime => overTime.applicationTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? item.applicationTime : null));
							});
						}
						// A7_13
						// 計算結果．申請時間．就業時間外深夜時間 
						if(self.nightOvertimeReflectAtrCheck()){
							let overTimeShiftNight = res.calculationResult.applicationTime.overTimeShiftNight;
							if (!_.isNil(overTimeShiftNight)) {
								overTimeArray
									.filter(overTime => overTime.type() == AttendanceType.MIDNIGHT_OUTSIDE)
									.map(overTime => overTime.applicationTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? overTimeShiftNight.overTimeMidNight : null));													
							}
						}
					}
				}

				// A7_9
				let opPreAppContentDisplayLst = res.appDispInfoStartupOutput.appDispInfoWithDateOutput.opPreAppContentDispDtoLst;
				if (!_.isEmpty(opPreAppContentDisplayLst)) {
					let appHolidayWork = opPreAppContentDisplayLst[0].appHolidayWork;
					if (appHolidayWork) {
						let applicationTime = appHolidayWork.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
								overTimeArray
									.filter(overTime => overTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.NORMALOVERTIME)
									.map(overTime => overTime.preTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? item.applicationTime : null));
							})
							// A7_14
							if(self.nightOvertimeReflectAtrCheck()){
								let overTimeShiftNight = appHolidayWork.applicationTime.overTimeShiftNight;
								if (!_.isNil(overTimeShiftNight)) {
									overTimeArray
										.filter(overTime => overTime.type() == AttendanceType.MIDNIGHT_OUTSIDE)
										.map(overTime => overTime.preTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? overTimeShiftNight.overTimeMidNight : null));													
								}
							}
						}
					}
				}
				// A7_11
				if (!_.isNil(res.hdWorkDispInfoWithDateOutput)) {
					if (!_.isNil(res.hdWorkDispInfoWithDateOutput.actualApplicationTime)) {
						let applicationTimeRoot = res.hdWorkDispInfoWithDateOutput.actualApplicationTime;
						let applicationTime = res.hdWorkDispInfoWithDateOutput.actualApplicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							_.forEach(applicationTime, (item: OvertimeApplicationSetting) => {
								overTimeArray
									.filter(overTime => overTime.frameNo() == item.frameNo && item.attendanceType == AttendanceType.NORMALOVERTIME)
									.map(overTime => overTime.actualTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? item.applicationTime : null));
							})
						}
						if (!_.isNil(applicationTimeRoot)) {
							// A7_15
							// 申請日に関係する情報．実績の申請時間．就業時間外深夜時間．残業深夜時間
							if(self.nightOvertimeReflectAtrCheck()){
								let overTimeShiftNight = applicationTimeRoot.overTimeShiftNight;
								if (!_.isNil(overTimeShiftNight)) {
									overTimeArray
										.filter(overTime => overTime.type() == AttendanceType.MIDNIGHT_OUTSIDE)
										.map(overTime => overTime.actualTime(calculationResultOp && calculationResultOp.calculatedFlag == 0 ? overTimeShiftNight.overTimeMidNight : null));	
								}		
							}		
						}
					}
				}	
			}

			self.overTime(overTimeArray);
			self.setColorForOverTime(self.dataSource.calculationResult && self.dataSource.calculationResult.calculatedFlag == 0, self.dataSource);
		}

		setColorForOverTime(isCalculation: Boolean, dataSource: AppHdWorkDispInfo) {
			const self = this;
			if (!isCalculation || _.isNil(dataSource.calculationResult)) {
				return;
			}
			let overTimes = self.overTime() as Array<OverTime>;
			let overStateOutput = dataSource.calculationResult.actualOvertimeStatus;
			
			_.forEach(overTimes, (item: OverTime) => {
				let backgroundColor = '';
				if (item.type() == AttendanceType.NORMALOVERTIME) {
					// ・計算値：「残業申請の表示情報．計算結果」を確認する
					if (!_.isNil(dataSource.calculationResult.applicationTime)) {
						let applicationTime = dataSource.calculationResult.applicationTime.applicationTime;
						if (!_.isEmpty(applicationTime)) {
							let result = applicationTime
								.filter((appTime : OvertimeApplicationSetting) => appTime.frameNo == item.frameNo() && appTime.attendanceType == AttendanceType.NORMALOVERTIME);
							if (!_.isEmpty(result)) {
								if (result[0].applicationTime > 0) {
									backgroundColor = BACKGROUND_COLOR.bgC1;									
								}
							}
						}
					}
					if (!_.isNil(overStateOutput)) {
						// ・事前申請超過：「残業申請の表示情報．計算結果．事前申請・実績の超過状態」を確認する
						if (!_.isNil(overStateOutput.advanceExcess)) {
							let excessStateDetail = overStateOutput.advanceExcess.excessStateDetail;
							if (!_.isEmpty(excessStateDetail)) {
								let result = excessStateDetail
									.filter((excessDetail : ExcessStateDetail) => excessDetail.frame == item.frameNo() && excessDetail.type == AttendanceType.NORMALOVERTIME);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC4;									
									}
								}
							}
						}
						// ・実績申請超過：「残業申請の表示情報．計算結果．事前申請・実績の超過状態」を確認する
						if (!_.isNil(overStateOutput.achivementExcess)) {
							let excessStateDetail = overStateOutput.achivementExcess.excessStateDetail;
							if (!_.isEmpty(excessStateDetail)) {
								let result = excessStateDetail
									.filter((excessDetail : ExcessStateDetail) => excessDetail.frame == item.frameNo() && excessDetail.type == AttendanceType.NORMALOVERTIME);
								if (!_.isEmpty(result)) {
									if (result[0].excessState == ExcessState.EXCESS_ERROR) {
										backgroundColor = BACKGROUND_COLOR.bgC2;									
									} else if (result[0].excessState == ExcessState.EXCESS_ALARM) {
										backgroundColor = BACKGROUND_COLOR.bgC3;
									}
								}
							}
						}
					}
				} else if (item.type() == AttendanceType.MIDNIGHT_OUTSIDE) {
					
					// 事前申請・実績の超過状態．事前超過．残業深夜の超過状態 = 超過アラーム
					if (!_.isNil(overStateOutput)) {
						
						if (!_.isNil(dataSource.calculationResult.applicationTime)) {
							let applicationTime = dataSource.calculationResult.applicationTime;
							if (!_.isEmpty(applicationTime)) {
								if (!_.isNil(applicationTime.overTimeShiftNight)) {
									if (applicationTime.overTimeShiftNight.overTimeMidNight > 0) {
										backgroundColor = BACKGROUND_COLOR.bgC1;
									}
								}
							}
						}
						if (!_.isNil(overStateOutput.advanceExcess)) {
							if (overStateOutput.advanceExcess.overTimeLate == ExcessState.EXCESS_ALARM) {								
								backgroundColor = BACKGROUND_COLOR.bgC4;
							}							
						}
						if (!_.isNil(overStateOutput.achivementExcess)) {
							// 事前申請・実績の超過状態．実績超過．残業深夜の超過状態 = 超過アラーム
							if (overStateOutput.achivementExcess.overTimeLate == ExcessState.EXCESS_ALARM) {								
								backgroundColor = BACKGROUND_COLOR.bgC3;
							}
							// 事前申請・実績の超過状態．実績超過．残業深夜の超過状態 = 超過エラー
							if (overStateOutput.achivementExcess.overTimeLate == ExcessState.EXCESS_ERROR) {								
								backgroundColor = BACKGROUND_COLOR.bgC2;
							}
						}
					}
				}
				if (item.applicationTime() > 0) {
					item.backgroundColor(backgroundColor);				
				}
			});
		}

		createRestTime() {
			const self = this;
			let restTimeArray = [];
			for (let i = 1; i <= 10; i++) {
				let item = {} as RestTime;
				item.frameNo = i.toString();
				item.start = ko.observable(null);
				item.end = ko.observable(null);
				restTimeArray.push(item);
			}
			self.restTime(restTimeArray);
		}

		openDialogKdl005() {
			const self = this;
			let employeeIdList = [];
			if(self.employeeIdLst && self.employeeIdLst.length > 0){
				employeeIdList = self.employeeIdLst;
			} else {
				employeeIdList = [self.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.employeeInfoLst[0].sid];
			}
			nts.uk.ui.windows.setShared( 'KDL005_DATA', employeeIdList);

			if (employeeIdList.length > 1) {
				nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml", {  width: 1160, height: 640 });
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml",{  width: 860, height: 640 });
            }
		}

		openDialogKdl003() {
			const self = this;
			nts.uk.ui.windows.setShared( 'parentCodes', {
                workTypeCodes: _.map( _.uniqBy( self.dataSource.hdWorkDispInfoWithDateOutput.workTypeList, e => e.workTypeCode ), (item: any) => item.workTypeCode ),
                selectedWorkTypeCode: self.workInfo().workType().code,
                workTimeCodes: _.map( self.dataSource.appDispInfoStartupOutput.appDispInfoWithDateOutput.opWorkTimeLst, (item: any) => item.worktimeCode ),
                selectedWorkTimeCode: self.workInfo().workTime().code
            }, true);

			nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed( function(): any {
                //view all code of selected item 
                let childData = nts.uk.ui.windows.getShared('childData');
                if (childData) {
					let workType = {} as Work;
					workType.code = childData.selectedWorkTypeCode;
					workType.name = childData.selectedWorkTypeName;
					self.workInfo().workType(workType);
					let workTime = {} as Work;
                    workTime.code = childData.selectedWorkTimeCode;
					workTime.name = childData.selectedWorkTimeName;
					self.workInfo().workTime(workTime);

					let dateList = self.application().appDate().trim() ? [self.application().appDate().trim()] : [];
					let command = {
						companyId: self.$user.companyId,
						dateList,
						workTypeCode: workType.code,
						workTimeCode: workTime.code,
						startTime: 0,
						endTime: 0,
						appHdWorkDispInfoDto: self.dataSource,
						isAgent: self.isAgentNew()
					};

					self.$blockui('show');
					self.$ajax(API.selectWorkInfo, command)
						.done((res: AppHdWorkDispInfo) => {
						
							self.dataSource.appDispInfoStartupOutput = res.appDispInfoStartupOutput;
							self.dataSource.calculationResult = res.calculationResult;
							self.dataSource.dispFlexTime = res.dispFlexTime;
							self.dataSource.hdWorkDispInfoWithDateOutput = res.hdWorkDispInfoWithDateOutput;
							self.dataSource.holidayWorkAppSet = res.holidayWorkAppSet;
							self.dataSource.hdWorkOvertimeReflect = res.hdWorkOvertimeReflect;
							self.dataSource.otWorkHoursForApplication = res.otWorkHoursForApplication;
							self.dataSource.overtimeFrameList = res.overtimeFrameList;
							self.dataSource.divergenceReasonInputMethod = res.divergenceReasonInputMethod;
							self.dataSource.divergenceTimeRoots = res.divergenceTimeRoots;
							self.dataSource.workdayoffFrameList = res.workdayoffFrameList;

							self.bindOverTimeWorks(self.dataSource);
							self.bindWorkInfo(self.dataSource);
							self.bindRestTime(self.dataSource);
							self.bindHolidayTime(self.dataSource, 1);
							self.bindOverTime(self.dataSource, 1);

							
						})
						.fail((res) =>{

						})
						.always(() =>{
							self.$blockui('hide');
						})

                }
            })
		}

		getBreakTimes() {
			const self = this;
			let command = {
				companyId: self.$user.companyId,
				dateList: [self.application().appDate()],
				workTypeCode: self.workInfo().workType().code,
				workTimeCode: self.workInfo().workTime().code,
				startTime: self.workInfo().workHours1.start(),
				endTime: self.workInfo().workHours1.end(),
				appHdWorkDispInfoDto: self.dataSource
			};
			self.$blockui('show');
			self.$ajax(API.changeWorkHours, command)
				.done((res: AppHdWorkDispInfo) =>{
					
					self.dataSource.appDispInfoStartupOutput = res.appDispInfoStartupOutput;
					self.dataSource.calculationResult = res.calculationResult;
					self.dataSource.dispFlexTime = res.dispFlexTime;
					self.dataSource.hdWorkDispInfoWithDateOutput = res.hdWorkDispInfoWithDateOutput;
					self.dataSource.holidayWorkAppSet = res.holidayWorkAppSet;
					self.dataSource.hdWorkOvertimeReflect = res.hdWorkOvertimeReflect;
					self.dataSource.otWorkHoursForApplication = res.otWorkHoursForApplication;
					self.dataSource.overtimeFrameList = res.overtimeFrameList;
					self.dataSource.divergenceReasonInputMethod = res.divergenceReasonInputMethod;
					self.dataSource.divergenceTimeRoots = res.divergenceTimeRoots;
					self.dataSource.workdayoffFrameList = res.workdayoffFrameList;

					self.bindRestTime(self.dataSource);
				})
				.fail(() =>{})
				.always(() => self.$blockui('hide'));
		}

		

		changeDate() {
			const self = this;
			let param = {} as ParamHolidayWorkChangeDate;
			param.companyId = self.$user.companyId;
			param.dateList = [self.application().appDate()];
			param.applicationType = AppType.HOLIDAY_WORK_APPLICATION;
			self.dataSource.appDispInfoStartupOutput.appDispInfoWithDateOutput.prePostAtr = self.application().prePostAtr;
			param.appHdWorkDispInfoDto = ko.toJS(self.dataSource);
			param.isAgent = self.isAgentNew();
			self.$blockui('show');
			self.$ajax(API.changeAppDate, param)
				.done((res: AppHdWorkDispInfo) => { 
					self.dataSource = res;
					self.itemControlHandler();
					self.bindOverTimeWorks(self.dataSource);
					self.bindWorkInfo(self.dataSource, true);
					self.bindRestTime(self.dataSource);
					self.bindHolidayTime(self.dataSource, 1);
					self.bindOverTime(self.dataSource, 1);
				})
				.fail(() => {})
				.always(() => self.$blockui('hide'));
		}

		calculate() {
			const self = this;
			
			self.$validate('#kaf000-a-component4 .nts-input', '#kaf000-a-component3-prePost', '#inpStartTime1', '#inpEndTime1')
				.then(isValid => {
					if (isValid) {
						let command = {} as ParamCalculationHolidayWork;
						command.isAgent = self.isAgentNew();
						let workContent = {} as WorkContent;
						let workInfo = self.workInfo() as WorkInfo;
						workContent.workTypeCode = workInfo.workType().code as string;
						workContent.workTimeCode = workInfo.workTime().code as string;
						let breakTimeArray = [] as Array<BreakTime>;
						let restTime = self.restTime() as Array<RestTime>;
						let timeZoneArray = [] as Array<TimeZone>;

						command.companyId = self.$user.companyId;
						command.employeeId = self.dataSource.appDispInfoStartupOutput.appDispInfoNoDateOutput.employeeInfoLst[0].sid;
						command.date = self.application().appDate();
						command.prePostAtr = self.application().prePostAtr();
						command.overtimeLeaveAppCommonSet = self.dataSource.holidayWorkAppSet.overtimeLeaveAppCommonSet;
						if (!_.isNil(self.dataSource.appDispInfoStartupOutput.appDispInfoWithDateOutput.opPreAppContentDispDtoLst)) {
							command.preApplicationTime = self.dataSource.appDispInfoStartupOutput.appDispInfoWithDateOutput.opPreAppContentDispDtoLst[0].appHolidayWork && 
								self.dataSource.appDispInfoStartupOutput.appDispInfoWithDateOutput.opPreAppContentDispDtoLst[0].appHolidayWork.applicationTime;
						} 
						command.actualApplicationTime = self.dataSource.hdWorkDispInfoWithDateOutput.actualApplicationTime;

						let timeZone1 = {} as TimeZone;
						if (!_.isNil(workInfo.workHours1) && _.isNumber(workInfo.workHours1.start()) && _.isNumber(workInfo.workHours1.end())) {
							timeZone1.frameNo = 1;
							timeZone1.start = workInfo.workHours1.start();
							timeZone1.end = workInfo.workHours1.end();
							timeZoneArray.push(timeZone1);
						}
						if(self.managementMultipleWorkCyclescheck()){
							let timeZone2 = {} as TimeZone;
							if (!_.isNil(workInfo.workHours2) && _.isNumber(workInfo.workHours2.start()) && _.isNumber(workInfo.workHours2.end())) {
								timeZone2.frameNo = 2;
								timeZone2.start = workInfo.workHours2.start();
								timeZone2.end = workInfo.workHours2.end();
								timeZoneArray.push(timeZone2);
							}
						}
						restTime.forEach(item => {
							if (_.isNumber(ko.toJS(item.start)) && _.isNumber(ko.toJS(item.end))) {
								let breakTime = {} as BreakTime;
								breakTime.breakFrameNo = Number(item.frameNo);
								breakTime.startTime = ko.toJS(item.start);
								breakTime.endTime = ko.toJS(item.end);
								breakTime.breakTime = 0;
								breakTimeArray.push(breakTime);
							}
						})
						workContent.breakTimes = breakTimeArray;
						workContent.timeZones = timeZoneArray;
						command.workContent = workContent;

						self.$ajax(API.calculate, command)
							.done((res: HolidayWorkCalculationResult) => {
								if (res) {
									self.dataSource.calculationResult = res;
									self.itemControlHandler();
									self.bindOverTime(self.dataSource, 1);
									self.bindHolidayTime(self.dataSource, 1);
									// let appTimeList = res.applicationTime.applicationTime;
									// let holidayTimeArray = self.holidayTime();
									// let overTimeArray = self.overTime();
									// appTimeList.forEach((appTime: OvertimeApplicationSetting) => {
									// 	holidayTimeArray.forEach((holidayTime: HolidayTime) => {
									// 		// A6_8
									// 		if (appTime.frameNo === holidayTime.frameNo() && appTime.attendanceType === 1) {
									// 			holidayTime.start(appTime.applicationTime);
									// 		}
									// 	})
									// 	overTimeArray.forEach((overTime: OverTime) => {
									// 		// A7_8
									// 		if (appTime.frameNo === overTime.frameNo() && appTime.attendanceType === 0) {
									// 			overTime.applicationTime(appTime.applicationTime);
									// 		} 
									// 	})
									// })
									// //A7_13
									// let length = overTimeArray.length;
									// overTimeArray[length - 1].applicationTime(res.applicationTime.overTimeShiftNight.overTimeMidNight);
									// //A6_28
									// for (let item of res.applicationTime.overTimeShiftNight.midNightHolidayTimes) {
									// 	switch (item.legalClf) {
									// 		case 0:
									// 			holidayTimeArray[length-3].start(item.attendanceTime);
									// 			break;
									// 		case 1:
									// 			holidayTimeArray[length-2].start(item.attendanceTime);
									// 			break;
									// 		case 2: 
									// 			holidayTimeArray[length-1].start(item.attendanceTime);
									// 			break;
									// 	}
										
									// } 
									// self.holidayTime(holidayTimeArray);
									// self.overTime(overTimeArray);
								}
							})
							.fail((res) => { 

							})
							.always(() => {
								self.$blockui("hide");
							});
					}
				});
		}
		
		getFormatTime(number: number) {
			if (_.isNil(number)) return '';
			return String(formatTime("Clock_Short_HM", number));
		}

		handleConfirmMessage(listMes: any): any {
			const vm = this;
			if (_.isEmpty(listMes)) {
				return $.Deferred().resolve(true);
			}
			let msg = listMes[0];

			return vm.$dialog.confirm({ messageId: msg.msgID, messageParams: msg.paramLst })
				.then((value) => {
					if (value === 'yes') {
						return vm.handleConfirmMessage(_.drop(listMes));
					} else {
						return $.Deferred().resolve(false);
					}
				});
		}

		handleConfirmMessageMap(mapMes: Map<string, Array<any>>): any {
			const vm = this;
			if (_.isEmpty(mapMes)) {
				return $.Deferred().resolve(true);
			}
			let keys = Object.keys(mapMes);
			let listMes = (mapMes as any)[keys[0]];
			
			if (_.isEmpty(listMes)) {
				return $.Deferred().resolve(true);
			}
			let msg = listMes[0];
			msg.paramLst.unshift(keys[0]); //add empName to top of array;

			return vm.$dialog.confirm({ messageId: msg.msgID, messageParams: msg.paramLst })
				.then((value) => {
					if (value === 'yes') {
						return vm.handleConfirmMessage(_.drop(listMes));
					} else {
						return $.Deferred().resolve(false);
					}
				});
		}

		
	}
	
	const COLOR_36 = {
		// 36協定エラー
		error: 'bg-36contract-error',
		// 36協定アラーム
		alarm: 'bg-36contract-alarm',
		// 36協定特例
		exceptions: 'bg-36contract-exception',
		// 36協定エラー文字
		error_letter: 'color-36contract-error',
		// 36協定アラーム文字
		alarm_character: 'color-36contract-alarm',
		// 特条上限超過背景色
		bg_upper_limit: 'bg-exceed-special-upperlimit',
		// 特条上限超過文字色
		color_upper_limit: 'color-exceed-special-upperlimit'
		
		
	}

	const API = {
		startNew: "at/request/application/holidaywork/startNew",
		changeAppDate: "at/request/application/holidaywork/changeAppDate",
		calculate: "at/request/application/holidaywork/calculate",
		selectWorkInfo: "at/request/application/holidaywork/selectWork",
		changeWorkHours:  "at/request/application/holidaywork/changeWorkHours",
		checkBeforeRegister: "at/request/application/holidaywork/checkBeforeRegister",
		register: "at/request/application/holidaywork/register",
		checkBeforeRegisterMulti: "at/request/application/holidaywork/checkBeforeRegisterMulti",
		registerMulti: "at/request/application/holidaywork/registerMulti",
		reflectApp: "at/request/application/reflect-app"
	}
	interface AppHdWorkDispInfo {
		dispFlexTime: boolean;
		divergenceTimeRoots: any
		workdayoffFrameList: Array<WorkdayoffFrame>;
		otWorkHoursForApplication: AgreeOverTimeOutput;
		hdWorkDispInfoWithDateOutput: HdWorkDispInfoWithDateOutput;
		calculationResult: CalculationResult;
		appDispInfoStartupOutput: any;
		overtimeFrameList: Array<OvertimeWorkFrame>;
		holidayWorkAppSet: any;
		divergenceReasonInputMethod: Array<DivergenceReasonInputMethod>;
		hdWorkOvertimeReflect: any;
	}
	interface DivergenceReasonInputMethod{
		divergenceTimeNo: number;
		companyId: string;
		divergenceReasonInputed: boolean;
		divergenceReasonSelected: boolean;
		reasons: Array<ComboDivergenceReason>;
	}
	interface OvertimeWorkFrame {
		overtimeWorkFrName: string;
		overtimeWorkFrNo: number;
	}
	interface CalculationResult {
		actualOvertimeStatus: any;
		calculatedFlag: number;
		applicationTime: ApplicationTime;
	}
	interface ApplicationTime {
		applicationTime: Array<OvertimeApplicationSetting>; //  申請時間
		flexOverTime: number; // フレックス超過時間
		overTimeShiftNight: OverTimeShiftNight; // 就業時間外深夜時間
		anyItem: Array<AnyItemValue>; // 任意項目
		reasonDissociation: Array<ReasonDivergence>; // 乖離理由
	}
	interface ReasonDivergence{
		reason: string;
		reasonCode: string;
		diviationTime: number;
	}
	interface AnyItemValue {
		itemNo: number;
		times: number;
		amount: number;
		time: number
	}
	interface OverTimeShiftNight {
		midNightHolidayTimes: Array<any>;
		midNightOutSide: number;
		overTimeMidNight: number;
	}
	interface OvertimeApplicationSetting {
		frameNo: number; 
		attendanceType: number;
		applicationTime: number
	}
	interface HdWorkDispInfoWithDateOutput {
		initWorkType: string;
		initWorkTypeName: string;
		initWorkTime: string;
		initWorkTimeName: string;
		workHours: WorkHoursDTO;
		breakTimeZoneSettingList: BreakTimeZoneSetting;
		actualApplicationTime: ApplicationTime;
		workTypeList: Array<WorkType>;
		overtimeStatus: OvertimeStatusCommand;
		subHdManage: boolean;
	}
	interface OvertimeStatusCommand {
		isPreApplicationOvertime: boolean;
		attendanceType: number;
		isActualOvertime: boolean;
		frameNo: number;
		isInputCalculationDiff: boolean;
	}
	interface WorkType {
		workTypeCode: string;
		name: string;
	}
	interface BreakTimeZoneSetting {
		timeZones?: Array<TimeZone>;
	}
	interface TimeZone {
		frameNo: number;
		start: number;
		end: number;
	}
	interface WorkHoursDTO {
		startTimeOp1: number;
		startTimeOp2: number;
		endTimeOp1: number;
		endTimeOp2: number;
	}
	interface WorkdayoffFrame {
		workdayoffFrNo: number;
		workdayoffFrName: string;
	}
	interface AgreeOverTimeOutput {
		isCurrentMonth: boolean;
		currentTimeMonth: any;
		currentMonth: string
		isNextMonth: boolean;
		nextTimeMonth: any;
		nextMonth: string;
	}
	interface AgreeOverTime {
		detailCurrentMonth: AgreementTime;
		detailNextMonth: AgreementTime;
		currentMonth: string;
		nextMonth: string;
	}
	interface AgreementTime {
		employeeId: string;
		confirmed: AgreeTimeOfMonth;
		afterAppReflect: AgreeTimeOfMonth;
		errorMessage: string;
	}
	interface AgreeTimeOfMonth {
		agreementTime: number;
		limitErrorTime: number;
		limitAlarmTime: number;
		exceptionLimitErrorTime: number;
		exceptionLimitAlarmTime: number;
		status: number;
	}
	interface ParamHolidayWorkChangeDate {
		companyId: string;
		dateList: Array<string>;
		applicationType: number;
		appHdWorkDispInfoDto: AppHdWorkDispInfo;
		isAgent: boolean
	}
	interface ParamCalculationHolidayWork {
		companyId: string;
		employeeId: string;
		date: string;
		prePostAtr: number;
		overtimeLeaveAppCommonSet: OvertimeLeaveAppCommonSetCommand;
		preApplicationTime: ApplicationTime;
		actualApplicationTime: ApplicationTime;
		workContent: WorkContent;
		isAgent: boolean;
	}
	interface OvertimeLeaveAppCommonSetCommand {
		preExcessDisplaySetting: number;
		extratimeExcessAtr: number;
		extratimeDisplayAtr: number;
		performanceExcessAtr: number;
		checkOvertimeInstructionRegister: number;
		checkDeviationRegister: number;
		overrideSet: number;
	}
	interface BreakTime {
		breakFrameNo: number;
		startTime: number;
		endTime: number;
		breakTime: number;
	}
	interface WorkContent {
		workTypeCode: string;
		workTimeCode: string;
		timeZones: Array<TimeZone>;
		breakTimes: Array<BreakTime>;
	}
	interface OverStateOutputDto {
		isExistApp: boolean;
		advanceExcess: any;
		achivementStatus: number;
		achivementExcess: any;
	}
	interface HolidayWorkCalculationResult {
		actualOvertimeStatus: OverStateOutputDto;
		applicationTime: ApplicationTime;
		calculatedFlag: number;
	}
	interface AppHolidayWorkCmd {
		workInformation: WorkInformationCommand;
		applicationTime: ApplicationTime;
		backHomeAtr: boolean;
		goWorkAtr: boolean;
		breakTimeList: Array<TimeZoneWithWorkNoCommand>;
		workingTimeList: Array<TimeZoneWithWorkNoCommand>;
		appOvertimeDetail: any;
		application: any;
	}
	interface WorkInformationCommand {
		workType: string;
		workTime: string;
	}
	interface TimeZoneWithWorkNoCommand {
		workNo: number;
		timeZone: TimeZoneNewDto;
	}
	interface TimeZoneNewDto {
		startTime: number;
		endTime: number;
	}
	interface CheckBeforeOutput {
		appOverTimeDetail: any;
		confirmMsgOutputs: Array<any>;
	}
	interface CheckBeforeOutputMulti {
		confirmMsgOutputMap: Map<string, Array<any>>;
		approvalRootContentMap: Map<string, any>;
		errorEmpBusinessName: string;
	}
	interface ComboDivergenceReason {
		divergenceReasonCode: string;
		reason: string;
		reasonRequired: number;
		comboBoxText: string;
	}
	enum MODE {
		NORMAL,
		SINGLE_AGENT,
		MULTiPLE_AGENT,
		VIEW,
		EDIT
	}
	enum AttendanceType {
		NORMALOVERTIME,
		BREAKTIME,
		BONUSPAYTIME,
		BONUSSPECIALDAYTIME,
		MIDNIGHT,
		SHIFTNIGHT,
		MIDDLE_BREAK_TIME,
		MIDDLE_EXORBITANT_HOLIDAY,
		MIDDLE_HOLIDAY_HOLIDAY,
		FLEX_OVERTIME,
		MIDNIGHT_OUTSIDE = 100
	}
	interface HolidayMidNightTime {
		attendanceTime: number;
		legalClf: number;
	}
	enum StaturoryAtrOfHolidayWork {
		WithinPrescribedHolidayWork,
		ExcessOfStatutoryHolidayWork,
		PublicHolidayWork
	}
	const BACKGROUND_COLOR = {
		// 計算値
		bgC1: '#F69164',
		// 超過エラー
		bgC2: '#FD4D4D',
		// 超過アラーム
		bgC3: '#F6F636',
		// 事前超過
		bgC4: '#ffc0cb'
	}
	interface ExcessStateDetail {
		frame: number;
		type: number;
		excessState: number
	}
	enum ExcessState {
		NO_EXCESS,
		EXCESS_ALARM,
		EXCESS_ERROR
	}
	interface ExcessStateMidnight {
		excessState: number;
		legalCfl: number;
	}
	interface DataTransfer {
		startTime: number;
		endTime: number;
		employeeID: string;
		appDate: string;
		applicationReason: string;
	}
	enum AgreementTimeStatusOfMonthly {
		/** 正常 */
		NORMAL,
		/** 限度エラー時間超過 */
		EXCESS_LIMIT_ERROR,
		/** 限度アラーム時間超過 */
		EXCESS_LIMIT_ALARM,
		/** 特例限度エラー時間超過 */
		EXCESS_EXCEPTION_LIMIT_ERROR,
		/** 特例限度アラーム時間超過 */
		EXCESS_EXCEPTION_LIMIT_ALARM,
		/** 正常（特例あり） */
		NORMAL_SPECIAL,
		/** 限度エラー時間超過（特例あり） */
		EXCESS_LIMIT_ERROR_SP,
		/** 限度アラーム時間超過（特例あり） */
		EXCESS_LIMIT_ALARM_SP,
		/** 特別条項の上限時間超過 */
		EXCESS_BG_GRAY
	}
}