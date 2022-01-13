/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf011.a.viewmodel {
	
	import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import Application = nts.uk.at.view.kaf011.Application;
	import RecruitmentApp = nts.uk.at.view.kaf011.RecruitmentApp;
	import AbsenceLeaveApp = nts.uk.at.view.kaf011.AbsenceLeaveApp;
	import Comment = nts.uk.at.view.kaf011.Comment;

	@bean()
	export class Kaf011AViewModel extends Kaf000AViewModel {
		params: AppInitParam;
		
		appType: KnockoutObservable<number> = ko.observable(AppType.COMPLEMENT_LEAVE_APPLICATION);
		applicationCommon: KnockoutObservable<Application> = ko.observable(new Application());
		
		displayInforWhenStarting = ko.observable(null);
		
		isSendMail = ko.observable(false);
		remainDays = ko.observable('');
		comment = new Comment();

		remainDayList: KnockoutObservableArray<RemainDays> = ko.observableArray([]);
		
		appCombinaSelected = ko.observable(0);
		appCombinaDipslay = ko.observable(false);
		
		recruitmentApp = new RecruitmentApp(0, ko.observable(false));
		absenceLeaveApp = new AbsenceLeaveApp(1, ko.observable(false));
		
		isAgentMode = ko.observable(false);
		
		settingCheck = ko.observable(true);
		isFromOther: boolean = false;

		bindRemainDays() {
			const vm = this;

			const remainDayList = [] as RemainDays[];
			
			const item = {
				label: vm.$i18n('KAF011_64'),
				name: vm.$i18n('KAF011_65'),
				content: vm.remainDays()
			} as RemainDays;

			remainDayList.push(item);
			vm.remainDayList(remainDayList);
			
		}
		
		created(params?: AppInitParam) {
			const vm = this;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					params = __viewContext.transferred.value;
					vm.params = params;
				}
			}
			let paramDate,
				screenCode: number = null;
			if(vm.params){
				if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
					screenCode = params.screenCode;
				}
				if (!_.isEmpty(vm.params.baseDate)) {
					paramDate = moment(vm.params.baseDate).format('YYYY/MM/DD');
					vm.absenceLeaveApp.application.appDate(paramDate);
					vm.absenceLeaveApp.application.opAppStartDate(paramDate);
                    vm.absenceLeaveApp.application.opAppEndDate(paramDate);
					vm.recruitmentApp.application.appDate(paramDate);
					vm.recruitmentApp.application.opAppStartDate(paramDate);
                    vm.recruitmentApp.application.opAppEndDate(paramDate);
				}
				if (vm.params.isAgentMode) {
					vm.isAgentMode(vm.params.isAgentMode);
				}
			}
			let paramKAF000 = {
				empLst: vm.params?vm.params.employeeIds:[], 
				dateLst: paramDate?[paramDate]:[], 
				appType: vm.appType(),
				screenCode
			};
			vm.$blockui("grayout");
			vm.loadData(paramKAF000).then((loadDataFlag: any) => {
				if(loadDataFlag) {
					vm.$blockui("grayout");
					return vm.$ajax('at/request/application/holidayshipment/startPageARefactor',{sIDs: [], appDate: [], appDispInfoStartup: vm.appDispInfoStartupOutput()});
				}
			}).then((data: any) =>{
				if(data) {
					vm.displayInforWhenStarting(data);
					vm.isSendMail(data.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.manualSendMailAtr == 1);
					vm.remainDays(data.remainingHolidayInfor.remainDays + '日');
					vm.bindRemainDays();
					vm.appCombinaDipslay(data.substituteHdWorkAppSet.simultaneousApplyRequired == 0);
					vm.recruitmentApp.bindingScreenA(data.applicationForWorkingDay, data);
					vm.absenceLeaveApp.bindingScreenA(data.applicationForHoliday, data);
					vm.comment.update(data.substituteHdWorkAppSet);
					$('#isSendMail').css({'display': 'inline-block'});
					$('#contents-area').css({'display': ''});
					$('#functions-area').css({'opacity': ''});
					// CommonProcess.checkUsage(true, "#recAppDate", vm);
					$("#recAppDate").focus();	
				}
				vm.$blockui("hide"); 
			}).fail((failData: any) => {
				console.log(failData);
				if (failData.messageId === "Msg_43") {
					vm.$dialog.error(failData).then(() => { vm.$jump("com", "/view/ccg/008/a/index.xhtml"); });
				} else {
					vm.$dialog.error({ messageId: failData.messageId, messageParams: failData.parameterIds }).then(() => { vm.$jump("com", "/view/ccg/008/a/index.xhtml"); });
				}
				vm.$blockui("hide"); 
			});
			
			// refer Object common to rec and abs
			vm.absenceLeaveApp.application.prePostAtr = vm.recruitmentApp.application.prePostAtr = vm.applicationCommon().prePostAtr;
			vm.absenceLeaveApp.application.employeeIDLst = vm.recruitmentApp.application.employeeIDLst = vm.applicationCommon().employeeIDLst;
			vm.absenceLeaveApp.application.opAppStandardReasonCD = vm.recruitmentApp.application.opAppStandardReasonCD = vm.applicationCommon().opAppStandardReasonCD;
			vm.absenceLeaveApp.application.opAppReason = vm.recruitmentApp.application.opAppReason = vm.applicationCommon().opAppReason;
			
			vm.recruitmentApp.application.appDate.subscribe(value =>{
				const recDateMoment = moment(value, 'YYYY/MM/DD') as any;
				if(recDateMoment._isValid && vm.recruitmentApp.started){
					vm.$blockui("grayout");
					vm.$errors('clear');
					const absDate = vm.absenceLeaveApp.application.appDate() as any;
					const absDateMoment = moment(absDate, 'YYYY/MM/DD') as any;
					let holidayDate = (vm.appCombinaSelected() != 1 && vm.absenceLeaveApp.application.appDate() && absDateMoment._isValid) ? absDateMoment.format('YYYY/MM/DD') : null;
					let displayInforWhenStartingdto = vm.displayInforWhenStarting();
					displayInforWhenStartingdto.rec = null;
					displayInforWhenStartingdto.abs = null;
					
					vm.$ajax('at/request/application/holidayshipment/changeRecDate',{workingDate: moment(value).format('YYYY/MM/DD'), holidayDate: holidayDate, displayInforWhenStarting: displayInforWhenStartingdto}).then((data: any) =>{
						vm.appDispInfoStartupOutput(data.appDispInfoStartup);
						vm.displayInforWhenStarting(data);
						if(data.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.recordDate == 1){
							vm.recruitmentApp.bindingScreenA(data.applicationForWorkingDay, data);					
						}
						vm.absenceLeaveApp.workInformation.workType(data.applicationForHoliday.workType);
						if (recDateMoment.format('YYYY/MM/DD') === data.appDispInfoStartup.appDispInfoWithDateOutput.baseDate) {
							CommonProcess.checkUsage(true, "#recAppDate", vm);							
						} else {
							CommonProcess.checkUsage(true, "#absAppDate", vm);	
						}
					}).always(() => {
						vm.$blockui("hide"); 
					});
				}
			});
			
			vm.absenceLeaveApp.application.appDate.subscribe(value =>{
				const absDateMoment = moment(value, 'YYYY/MM/DD') as any;
				if(absDateMoment._isValid && vm.recruitmentApp.started){
					vm.$blockui("grayout");
					vm.$errors('clear');
					let displayInforWhenStartingdto = vm.displayInforWhenStarting();
					displayInforWhenStartingdto.rec = null;
					displayInforWhenStartingdto.abs = null;
					const recDate = vm.recruitmentApp.application.appDate() as any;
					const recDateMoment = moment(recDate, 'YYYY/MM/DD') as any;
					let workingDate = (vm.appCombinaSelected() != 2 && vm.recruitmentApp.application.appDate() && recDateMoment._isValid) ? recDateMoment.format('YYYY/MM/DD') : null;
					vm.$ajax('at/request/application/holidayshipment/changeAbsDate',{workingDate: workingDate, holidayDate: moment(value).format('YYYY/MM/DD'), displayInforWhenStarting: displayInforWhenStartingdto}).then((data: any) =>{
						vm.appDispInfoStartupOutput(data.appDispInfoStartup)
						vm.displayInforWhenStarting(data);
						if(data.appDispInfoStartup.appDispInfoNoDateOutput.applicationSetting.recordDate == 1){
							vm.absenceLeaveApp.bindingScreenA(data.applicationForHoliday, data);
						}
						if (absDateMoment.format('YYYY/MM/DD') === data.appDispInfoStartup.appDispInfoWithDateOutput.baseDate) {
							CommonProcess.checkUsage(true, "#absAppDate", vm);							
						} else {
							CommonProcess.checkUsage(true, "#recAppDate", vm);		
						}
					}).always(() => {
						vm.$blockui("hide"); 
					});
				}
				
			});
			
			vm.appCombinaSelected.subscribe(value => {
				nts.uk.ui.errors.clearAll();
				if(value == 0 || value == 1){
					vm.recruitmentApp.application.appDate.valueHasMutated();
				}else{
					vm.absenceLeaveApp.application.appDate.valueHasMutated();
				}
			});
		}
		
		
		mounted(){
			
		}
		
		triggerValidate(): boolean {
			$('#kaf000-a-component3-prePost').trigger("validate");
			$('.nts-input').trigger("validate");
			$('input').trigger("validate");
			return nts.uk.ui.errors.hasError();
		}
		
		register() {
			const vm = this;
			if(!vm.triggerValidate()){
				let data = vm.displayInforWhenStarting();
					data.rec = vm.appCombinaSelected() != 2 ? ko.toJS(vm.recruitmentApp): null;
					if(data.rec){
						data.rec.application.opAppStartDate = data.rec.application.opAppEndDate = data.rec.application.appDate = moment(data.rec.application.appDate).format('YYYY/MM/DD');
						_.remove(data.rec.workingHours, function(n: any) {
							return n.timeZone.startTime == undefined || n.timeZone.startTime == undefined;  
						}); 
					}
					data.abs = vm.appCombinaSelected() != 1 ? ko.toJS(vm.absenceLeaveApp): null;
					if(data.abs){
						data.abs.application.opAppStartDate = data.abs.application.opAppEndDate = data.abs.application.appDate = moment(data.abs.application.appDate).format('YYYY/MM/DD');
						_.remove(data.abs.workingHours, function(n: any) {
							return n.timeZone.startTime == undefined || n.timeZone.startTime == undefined;  
						}); 
					}
					let checkFlag = false;
					vm.$blockui("show");
					
				if (!data.rec) {
					let initParam = {
						actualContentDisplayList: data.appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst,
						daysUnit: data.abs.workTypeSelected.workAtr == 0 ? 1: 0.5, 
						employeeId: data.abs.application.employeeIDLst[0], 
						startDate: moment(data.abs.application.appDate),
						endDate: moment(data.abs.application.appDate), 
						managementData: data.abs.payoutSubofHDManagements, 
						targetSelectionAtr: 1
					}
					vm.$ajax('screen/at/kdl035/init', initParam).then((result: any) => {
						if (result && result.substituteWorkInfoList.length > 0) {
							checkFlag = true;
						}
						data.checkFlag = checkFlag;
						vm.processRegister(data);
					});
				} else {
					data.checkFlag = checkFlag;
					vm.processRegister(data);
				}
			}
			
			
		}
		
		processRegister(data: any) {
			const vm = this;
			vm.$ajax('at/request/application/holidayshipment/save', data).then((result) => {
				vm.$blockui("hide");
				vm.$dialog.info({messageId: "Msg_15"}).done(() => {
					nts.uk.request.ajax("at", "at/request/application/reflect-app", result.reflectAppIdLst);
					CommonProcess.handleAfterRegister(result, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
				});
			}).fail((failData) => {
				vm.$dialog.error({messageId: failData.messageId, messageParams: failData.parameterIds});
				vm.$blockui("hide");
			});

		}
		
		openKDL009() {
			let self = this;
			nts.uk.ui.windows.setShared('KDL009_DATA', (self.params ? self.params.employeeIds : [__viewContext.user.employeeId]));
			if(self.params && self.params.employeeIds.length > 1){
				nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 1100, height: 650});	
			}else{
				nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
			}
			
		}
		
	}
	export interface KAF011Param {
		employeeIDLst: string[];
		recAppDate: string; 
		absAppDate: string;
		
	}
	export interface RemainDays {
		label: string;
		name: string;
		content: string;
	}

}