module nts.uk.at.view.kaf002.m3 {
    import service = nts.uk.at.view.kaf002.shr.service;
    import vmbase = nts.uk.at.view.kaf002.shr.vmbase;
    export module viewmodel {
        export class ScreenModel {
            appStampList: KnockoutObservableArray<vmbase.AppStampCancel> = ko.observableArray([]); 
            supFrameNo: number = 1;
            stampPlaceDisplay: KnockoutObservable<number> = ko.observable(0);
            constructor(){
                
            }
            
            start(appStampData: any, data: vmbase.StampRequestSettingDto, listWorkLocation: Array<any>){
                var self = this;    
                self.supFrameNo = data.supFrameDispNO;
                for(let i=1;i<=self.supFrameNo;i++) {
                    self.appStampList.push(new vmbase.AppStampCancel(4,i,0));    
                } 
                self.stampPlaceDisplay(data.stampPlaceDisp);
                if(!nts.uk.util.isNullOrUndefined(appStampData)){
                    self.appStampList.removeAll();
                    _.forEach(appStampData, item => {
                        self.appStampList.push(
                            new vmbase.AppStampCancel(
                                item.stampAtr,
                                item.stampFrameNo,
                                item.cancelAtr
                        ));        
                    });
                }
            }
            
            register(application : vmbase.Application, approvalList: Array<vmbase.AppApprovalPhase>){
                var self = this;
                let command = {
                    appID: "",
                    inputDate: application.inputDate(),
                    enteredPerson: application.enteredPerson(),
                    applicationDate: application.appDate(),
                    titleReason: application.titleReason(), 
                    detailReason: application.contentReason(),
                    employeeID: application.employeeID(),
                    stampRequestMode: 2,
                    appStampGoOutPermitCmds: null,
                    appStampWorkCmds: null, 
                    appStampCancelCmds: ko.mapping.toJS(self.appStampList()),
                    appStampOnlineRecordCmd: null,
                    appApprovalPhaseCmds: approvalList   
                }
                service.insert(command)
                .done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function(){
                        $('.cm-memo').focus();
                        nts.uk.ui.block.clear();
                    });     
                })
                .fail(function(res) { 
                    nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});
                });
            }
            
            update(application : vmbase.Application, approvalList: Array<vmbase.AppApprovalPhase>){
                var self = this;
                let command = {
                    version: application.version,
                    appID: application.applicationID(),
                    inputDate: application.inputDate(),
                    enteredPerson: application.enteredPerson(),
                    applicationDate: application.appDate(),
                    titleReason: application.titleReason(), 
                    detailReason: application.contentReason(),
                    employeeID: application.employeeID(),
                    stampRequestMode: 2,
                    appStampGoOutPermitCmds: null,
                    appStampWorkCmds: null, 
                    appStampCancelCmds: ko.mapping.toJS(self.appStampList()),
                    appStampOnlineRecordCmd: null,
                    appApprovalPhaseCmds: approvalList   
                }
                service.update(command)
                .done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function(){
                        $('.cm-memo').focus();
                        nts.uk.ui.block.clear();
                    });     
                })
                .fail(function(res) { 
                    if(res.optimisticLock == true){
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_197" }).then(function(){nts.uk.ui.block.clear();});    
                    } else {
                        nts.uk.ui.dialog.alertError(res.message).then(function(){nts.uk.ui.block.clear();});    
                    }
                });
            }
        }
    }
}