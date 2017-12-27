module nts.uk.at.view.kaf002.b {
    import service = nts.uk.at.view.kaf002.shr.service;
    import kaf002 = nts.uk.at.view.kaf002;
    import vmbase = nts.uk.at.view.kaf002.shr.vmbase; 
    export module viewmodel {
        const employmentRootAtr: number = 1; // EmploymentRootAtr: Application
        const applicationType: number = 7; // Application Type: Stamp Application
        export class ScreenModel {
            cm: kaf002.cm.viewmodel.ScreenModel;
            kaf000_a2: kaf000.a.viewmodel.ScreenModel;
            stampRequestMode: number = 0;
            screenMode: number = 0;
            employeeID: string = '';
            autoSendMail: KnockoutObservable<boolean> = ko.observable(false);
            constructor() {
                var self = this;
                __viewContext.transferred.ifPresent(data => {
                    self.stampRequestMode = data.stampRequestMode;
                    self.screenMode = data.screenMode;
                    return null;
                });
                self.cm = new kaf002.cm.viewmodel.ScreenModel(self.stampRequestMode, self.screenMode);
                self.kaf000_a2 = new kaf000.a.viewmodel.ScreenModel();
                self.startPage()
                .done((commonSet: vmbase.AppStampNewSetDto)=>{
                    self.autoSendMail(commonSet.appCommonSettingDto.appTypeDiscreteSettingDtos[0].sendMailWhenRegisterFlg == 1 ? true : false);
                    self.employeeID = commonSet.employeeID;
                    self.kaf000_a2.getAppDataDate(
                        applicationType, 
                        moment(new Date()).format("YYYY/MM/DD"),
                        true)
                    .done(()=>{
                        if(nts.uk.util.isNullOrEmpty(self.kaf000_a2.approvalRootState())){
                            nts.uk.request.jump("com", "/view/cmm/018/a/index.xhtml");
                        } else {
                            self.cm.start(commonSet, {'stampRequestMode': self.stampRequestMode });  
                        }  
                    }).fail(function(res) { 
                        nts.uk.ui.dialog.alertError(res.message).then(function(){
                            nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml"); 
                            nts.uk.ui.block.clear();
                        });
                    });   
                    self.kaf000_a2.getMessageDeadline(applicationType, moment(new Date()).format("YYYY/MM/DD"));
                }).fail(function(res) { 
                    nts.uk.ui.dialog.alertError(res.message).then(function(){
                        nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml"); 
                        nts.uk.ui.block.clear();
                    });
                }); 
                self.cm.application().appDate.subscribe(value => {
                    self.kaf000_a2.getAppDataDate(7, value, false);
                });
            }
            
            startPage(): JQueryPromise<any> {
                nts.uk.ui.block.invisible();
                var self = this;
                var dfd = $.Deferred();
                service.newScreenFind()
                    .done(function(commonSet: vmbase.AppStampNewSetDto) {
                        dfd.resolve(commonSet); 
                    })
                    .fail(function(res) { 
                        nts.uk.ui.dialog.alertError(res.message).then(function(){
                            nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml"); 
                            nts.uk.ui.block.clear();
                        });
                        dfd.reject(res); 
                    });
                return dfd.promise();
            }

            register() {
                var self = this;
                self.cm.register(self.kaf000_a2.errorFlag, self.kaf000_a2.errorMsg);
            }
            
            performanceReference(){
                alert('KDL004');   
            }
            
            changeAppDate(){
                var self = this;
                nts.uk.request.jump("com", "/view/cmm/018/a/index.xhtml", {screen: 'Application', employeeId: self.employeeID}); 
            }
            
        }
    }
}