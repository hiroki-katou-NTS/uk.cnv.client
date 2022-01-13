/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf000.a.viewmodel {
    import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
	import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;

    export abstract class Kaf000AViewModel extends ko.ViewModel {
    	appDispInfoStartupOutput: KnockoutObservable<any> = ko.observable(CommonProcess.initCommonSetting());
    	
        loadData(param: KAF000Param) {
            const vm = this;
            let empLst = param.empLst,
				dateLst = param.dateLst,
				appType = param.appType,
				opHolidayAppType = param.opHolidayAppType,
				opOvertimeAppAtr = param.opOvertimeAppAtr,
				command = { empLst, dateLst, appType, opHolidayAppType, opOvertimeAppAtr };
            return vm.$ajax(API.startNew, command)
            .then((data: any) => {
				if(param.screenCode===0 || param.screenCode===1) {
					if(data.appDispInfoNoDateOutput.applicationSetting.appDisplaySetting.prePostDisplayAtr == 1) {
						data.appDispInfoWithDateOutput.prePostAtr = 1;
					}
				}
                vm.appDispInfoStartupOutput(data);
                return CommonProcess.checkUsage(true, "#kaf000-a-component4-singleDate", vm);
            },(res: any) => {
                if (res.messageId == "Msg_426") {
                    vm.$dialog.error({ messageId: "Msg_426" }).then(() => {
                        vm.$jump("com", "/view/ccg/008/a/index.xhtml"); 
                    });    
                } else {
                    vm.$dialog.error(res.message).then(() => {
                        vm.$jump("com", "/view/ccg/008/a/index.xhtml"); 
                    }); 
                }
                return false;
            });
        }

		sendMailAfterRegister(mailServerSet: boolean, sendMailHandler: boolean) {
			const vm = this;
			return vm.$ajax(API.sendMailAfterRegister)
			.then((data: any) => {
				if(data) {
					if(data.isAutoSendMail) {
						let mailResult = [];
						mailResult.push({ value: data.autoSuccessMail, type: 'info' });
						mailResult.push({ value: data.autoFailMail, type: 'error' });
						mailResult.push({ value: data.autoFailServer, type: 'error' });
						CommonProcess.showMailResult(_.slice(mailResult, 1), vm).then(() => window.location.reload());
					}
					return data;
				}
			}).then((data: any) => {
				if(data) {
					if(sendMailHandler) {
						let command = { appID: data.appID };
						vm.$window.modal('/view/screen/a/index.xhtml', command)
	            		.then(() => window.location.reload());	
					}
				}
			});	
		}
		
		handleErrorCommon(failData: any) {
			const vm = this;
			if(_.isEmpty(failData.messageId)) {
				vm.$dialog.error(failData.message);
				return;
			}
			switch(failData.messageId) {
				case 'Msg_197':
				case 'Msg_198':
				case 'Msg_235':
				case 'Msg_391':
				case 'Msg_1518':
				case 'Msg_236':
				case 'Msg_324':
				case 'Msg_237':
				case 'Msg_238':
				case 'Msg_327':
				case 'Msg_328':
				case 'Msg_1530':
				case 'Msg_426':
				case 'Msg_448':
				case 'Msg_449':
				case 'Msg_450':
				case 'Msg_451':
				case 'Msg_768':
				case 'Msg_1715':
				case 'Msg_1521':
				case 'Msg_1648':
				default:
					vm.$dialog.error({ messageId: failData.messageId, messageParams: failData.parameterIds });	
			}
		}
		
		abstract register(): any;
    }

    const API = {
        startNew: "at/request/application/getStartPC",
		sendMailAfterRegister: ""
    }

	interface KAF000Param {
		empLst: Array<string>;
		dateLst: Array<string>;
		appType: AppType;
		opHolidayAppType?: number;
		opOvertimeAppAtr?: number;
		screenCode?: number;	
	}
}