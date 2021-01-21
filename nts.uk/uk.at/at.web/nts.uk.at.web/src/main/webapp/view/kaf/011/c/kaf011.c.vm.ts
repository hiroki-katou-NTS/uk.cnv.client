module nts.uk.at.view.kaf011.c.viewmodel {
	import windows = nts.uk.ui.windows;
	let dataTransfer = windows.getShared('KAF011C');
   	import ajax = nts.uk.request.ajax;
	import block = nts.uk.ui.block;
	import dialog = nts.uk.ui.dialog;
	
	export class KAF011C {
		displayInforWhenStarting: any;
		appDate: KnockoutObservable<string>;
		reasonTypeItemLst: KnockoutObservableArray<any> = ko.observableArray([]);
		appStandardReasonCD: KnockoutObservable<number> = ko.observable();
		appReason: KnockoutObservable<string> = ko.observable("");
		constructor(dataTransfer: any){
			let self = this;
			self.displayInforWhenStarting = dataTransfer;
			let appReasonStandardLst: any = _.find(dataTransfer.appDispInfoStartup.appDispInfoNoDateOutput.appReasonStandardLst, {'applicationType':10});
			self.appDate = ko.observable(dataTransfer.abs.application.appDate);
			if(appReasonStandardLst){
				self.reasonTypeItemLst(appReasonStandardLst.reasonTypeItemLst);	
			}
			self.appDate.subscribe((value:any) => {
				block.invisible();
				ajax('at/request/application/holidayshipment/changeDateScreenC',{appDateNew: new Date(value), displayInforWhenStarting: self.displayInforWhenStarting}).then((data: any) =>{
					self.displayInforWhenStarting.appDispInfoStartup.appDispInfoWithDateOutput = data.appDispInfoStartup.appDispInfoWithDateOutput;
				}).fail((fail: any) => {
					dialog.error({ messageId: fail.messageId});
				}).always(() => {
                    block.clear();
                });
			});
		}
		save(){
			let self = this;
			block.invisible();
			if(self.displayInforWhenStarting.rec){
				self.displayInforWhenStarting.rec.applicationInsert = self.displayInforWhenStarting.rec.applicationUpdate = self.displayInforWhenStarting.rec.application;	
			}
			if(self.displayInforWhenStarting.abs){
				self.displayInforWhenStarting.abs.applicationInsert = self.displayInforWhenStarting.abs.applicationUpdate = self.displayInforWhenStarting.abs.application;
			}
			ajax('at/request/application/holidayshipment/saveChangeDateScreenC',{appDateNew: new Date(self.appDate()), displayInforWhenStarting: self.displayInforWhenStarting, appReason: self.appReason(), appStandardReasonCD: self.appStandardReasonCD()}).then((data: any) =>{
				dialog.info({ messageId: "Msg_15"});
				windows.setShared("KAF011C_RESLUT", moment(this.appDate()).format('YYYY/MM/DD'));
			}).fail((fail: any) => {
				dialog.error({ messageId: fail.messageId, messageParams: fail.parameterIds});
			}).always(() => {
                block.clear();
            });
			
		}
		closeDialog(){
			windows.close();
		}
	}
	
	let __viewContext: any = window["__viewContext"] || {};
    __viewContext.ready(function() {
        __viewContext.bind(new KAF011C(dataTransfer));
    });
}