module nts.uk.at.view.kaf011.b.viewmodel {
    import PrintContentOfEachAppDto = nts.uk.at.view.kaf000.shr.viewmodel.PrintContentOfEachAppDto;
	
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
	import Application = nts.uk.at.view.kaf011.Application;
	import RecruitmentApp = nts.uk.at.view.kaf011.RecruitmentApp;
	import AbsenceLeaveApp = nts.uk.at.view.kaf011.AbsenceLeaveApp;
	import Comment = nts.uk.at.view.kaf011.Comment;
	import block = nts.uk.ui.block;
	import ajax = nts.uk.request.ajax;
	import dialog = nts.uk.ui.dialog;
	import DisplayInforWhenStarting = nts.uk.at.view.kaf011.DisplayInforWhenStarting;
	import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
	import RemainDays = nts.uk.at.view.kaf011.a.viewmodel.RemainDays;
	import getText = nts.uk.resource.getText;


    export class Kaf011BViewModel{

        appType: KnockoutObservable<number> = ko.observable(AppType.COMPLEMENT_LEAVE_APPLICATION);
		applicationCommon: KnockoutObservable<Application> = ko.observable(new Application());
        appDispInfoStartupOutput: any;
        application: KnockoutObservable<Application>;
        approvalReason: KnockoutObservable<string>;
        printContentOfEachAppDto: KnockoutObservable<PrintContentOfEachAppDto>;
		time: KnockoutObservable<number> = ko.observable(1);
		appCombinaSelected = ko.observable(0);
		recruitmentApp = new RecruitmentApp(0, ko.observable(true), false);
		absenceLeaveApp = new AbsenceLeaveApp(1, ko.observable(true), false);
		comment = new Comment();
		displayInforWhenStarting: KnockoutObservable<DisplayInforWhenStarting> = ko.observable(null);
		remainDays = ko.observable('');
		remainDayList: KnockoutObservableArray<RemainDays> = ko.observableArray([]);
		
		bindRemainDays() {
			const vm = this;

			const remainDayList = [] as RemainDays[];
			
			const item = {
				label: getText('KAF011_64'),
				name: getText('KAF011_65'),
				content: vm.remainDays()
			} as RemainDays;

			remainDayList.push(item);
			vm.remainDayList(remainDayList);
			
		}
        constructor(
            params: {
                appType: any,
                application: any,
                printContentOfEachAppDto: PrintContentOfEachAppDto,
                approvalReason: any,
                appDispInfoStartupOutput: any,
                eventUpdate: (evt: () => void) => void,
                eventReload: (evt: () => void) => void
            }
        ) {
            const vm = this;
			if(params.application().appID()!= null && params.appType() == AppType.COMPLEMENT_LEAVE_APPLICATION){	
				vm.appType = params.appType;
				vm.application = params.application;
	            vm.printContentOfEachAppDto = ko.observable(params.printContentOfEachAppDto);
	            vm.approvalReason = params.approvalReason;
				vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
	            params.eventUpdate(vm.update.bind(vm));
	            params.eventReload(vm.reload.bind(vm));
				// vm.loadData();
			}			
        }
		
		loadData(){
			const vm = this;
			block.invisible();
				ajax('at/request/application/holidayshipment/startPageBRefactor',{appID: vm.application().appID(), appDispInfoStartupDto: vm.appDispInfoStartupOutput()}).then((data: any) =>{
					console.log(data);
					vm.printContentOfEachAppDto().optHolidayShipment = data;
					vm.remainDays(data.remainingHolidayInfor.remainDays + '日');
					vm.bindRemainDays();
					if(data.rec && data.abs){
						vm.recruitmentApp.bindingScreenB(data.rec, data.applicationForWorkingDay.workTypeList, data);
						vm.absenceLeaveApp.bindingScreenBAbs(data.abs, data.applicationForHoliday.workTypeList, data);	
						vm.applicationCommon().update(data.rec.application);
					}else if(data.rec){
						vm.appCombinaSelected(1);
						vm.recruitmentApp.bindingScreenB(data.rec, data.applicationForWorkingDay.workTypeList, data);
						vm.applicationCommon().update(data.rec.application);
					}else if(data.abs){
						vm.appCombinaSelected(2);
						vm.absenceLeaveApp.bindingScreenBAbs(data.abs, data.applicationForHoliday.workTypeList, data);
						vm.applicationCommon().update(data.abs.application);
					}
					vm.displayInforWhenStarting(new DisplayInforWhenStarting(data));
					vm.comment.update(data.substituteHdWorkAppSet);
				}).fail((fail: any) => {
					dialog.error({ messageId: fail.messageId, messageParams: fail.parameterIds });
				}).always(() => {
					vm.recruitmentApp.isInit(false);
					vm.absenceLeaveApp.isInit(false);
                    block.clear();
                });
		}

        reload() {
            const vm = this;
            if (vm.appType() == AppType.COMPLEMENT_LEAVE_APPLICATION) {
				vm.loadData();
            }
        }
		
		triggerValidate(): boolean{
			$('.nts-input').trigger("validate");
			$('input').trigger("validate");
			return nts.uk.ui.errors.hasError();
		}

		openKDL009() {
			const vm = this;
			nts.uk.ui.windows.setShared('KDL009_DATA', [vm.appDispInfoStartupOutput().appDetailScreenInfo.application.employeeID]);
			
			nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
			
			
		}

        // event update cần gọi lại ở button của view cha
        update() {
            const vm = this;
			let dfd = $.Deferred();
			if(!vm.triggerValidate()) {
				let data: any = {};
					data.represent = vm.displayInforWhenStarting().represent;
					data.displayInforWhenStarting = ko.toJS(vm.displayInforWhenStarting);
					data.displayInforWhenStarting.rec = null;
					data.displayInforWhenStarting.abs = null;
					data.rec = vm.appCombinaSelected() != 2 ? ko.toJS(vm.recruitmentApp): null;
					if(data.rec){
						data.rec.applicationUpdate = vm.displayInforWhenStarting().rec.application;
						data.rec.applicationUpdate.opAppReason = vm.applicationCommon().opAppReason();
						data.rec.applicationUpdate.opAppStandardReasonCD = vm.applicationCommon().opAppStandardReasonCD();
						_.remove(data.rec.workingHours, function(n: any) {
							return n.timeZone.startTime == undefined || n.timeZone.startTime == undefined;  
						}); 
					}
					data.abs = vm.appCombinaSelected() != 1 ? ko.toJS(vm.absenceLeaveApp): null;
					if(data.abs){
						data.abs.applicationUpdate = vm.displayInforWhenStarting().abs.application;
						data.abs.applicationUpdate.opAppReason = vm.applicationCommon().opAppReason();
						data.abs.applicationUpdate.opAppStandardReasonCD = vm.applicationCommon().opAppStandardReasonCD();
						_.remove(data.abs.workingHours, function(n: any) {
							return n.timeZone.startTime == undefined || n.timeZone.startTime == undefined;  
						}); 
					}
				console.log(data);	
				let checkFlag = false;
				block.invisible();
				
				if (!data.rec && data.abs.workAtrOld != data.abs.workAtrNew) {
					let initParam = {
						actualContentDisplayList: data.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput.opActualContentDisplayLst,
						daysUnit: data.abs.workTypeSelected.workAtr == 0 ? 1: 0.5, 
						employeeId: data.abs.application.employeeIDLst[0], 
						startDate: moment(data.abs.application.appDate),
						endDate: moment(data.abs.application.appDate), 
						managementData: data.abs.payoutSubofHDManagements, 
						targetSelectionAtr: 1
					}
					ajax('screen/at/kdl035/init', initParam).then((result: any) => {
						if (result && result.substituteWorkInfoList.length > 0) {
							checkFlag = true;
						}
						data.checkFlag = checkFlag;
						ajax('at/request/application/holidayshipment/update', data).done((result: any) =>{
							dialog.info({ messageId: "Msg_15" }).then(()=>{
								CommonProcess.handleMailResult(result, vm).then(() => {
									dfd.resolve(true);	
								});
							});
						}).fail((res:any)=>{
							dialog.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(()=>{
								dfd.resolve(false);	
							});
						}).always(()=>{
							block.clear();
						});
					});
				} else {
					data.checkFlag = checkFlag;
					ajax('at/request/application/holidayshipment/update', data).done((result: any) =>{
						dialog.info({ messageId: "Msg_15" }).then(()=>{
							CommonProcess.handleMailResult(result, vm).then(() => {
								dfd.resolve(true);	
							});
						});
					}).fail((res:any)=>{
						dialog.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(()=>{
							dfd.resolve(false);	
						});
					}).always(()=>{
						block.clear();
					});
				}
				
				// ajax('at/request/application/holidayshipment/update', data).done((result: any) =>{
				// 	dialog.info({ messageId: "Msg_15" }).then(()=>{
				// 		CommonProcess.handleMailResult(result, vm).then(() => {
				// 			dfd.resolve(true);	
				// 		});
				// 	});
				// }).fail((res:any)=>{
				// 	dialog.error({ messageId: res.messageId, messageParams: res.parameterIds }).then(()=>{
				// 		dfd.resolve(false);	
				// 	});
				// }).always(()=>{
				// 	block.clear();
				// });
	        }else{
				dfd.resolve(false);
			}
			return dfd.promise();
		}
    }

}