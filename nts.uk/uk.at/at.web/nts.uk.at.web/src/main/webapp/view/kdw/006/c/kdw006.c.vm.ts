module nts.uk.at.view.kdw006.c.viewmodel {
    let __viewContext: any = window["__viewContext"] || {};

    export class ScreenModelC extends ko.ViewModel {
        daiPerformanceFunDto: KnockoutObservable<DaiPerformanceFunDto>;
        approvalProcessDto: KnockoutObservable<ApprovalProcessDto>;
        identityProcessDto: KnockoutObservable<IdentityProcessDto>
        monPerformanceFunDto: KnockoutObservable<MonPerformanceFunDto>;
        appTypeDto: KnockoutObservable<AppTypeDto>;
        restrictConfirmEmploymentDto: KnockoutObservable<RestrictConfirmEmploymentDto>;

        itemList: KnockoutObservableArray<any>;

        appType: KnockoutObservable<string>;
        appTypeEnum : KnockoutObservableArray<any>;

        checkLicense: KnockoutObservable<boolean>;
        yourselfConfirmErrorOldValue: number;
        supervisorConfirmErrorOldValue: number;

        constructor() {
            super();

            let self = this;
            self.checkLicense = ko.observable(false);
            self.itemList = ko.observableArray([]);

            let yourSelf = __viewContext.enums.YourselfConfirmError;
            yourSelf.reverse();
            _.forEach(yourSelf, (item) => {
                self.itemList.push(new ItemModel(item.value, item.value == 0 ? self.$i18n('KDW006_299') : self.$i18n('KDW006_300')));
            });

            self.appTypeDto = ko.observable(new AppTypeDto({
                appTypes: [],
            }));

            self.daiPerformanceFunDto = ko.observable(new DaiPerformanceFunDto({
                cid: '',
                comment: null,
                disp36Atr: 0,
                flexDispAtr: 0,
                checkErrRefDisp: 0,
            }));
            self.approvalProcessDto = ko.observable(new ApprovalProcessDto({
                cid: '',
                useDailyBossChk: 0,
                useMonthBossChk: 0,
                supervisorConfirmError: 1,
            }));
            self.supervisorConfirmErrorOldValue = 1;
            self.identityProcessDto = ko.observable(new IdentityProcessDto({
                cid: '',
                useDailySelfCk: 0,
                useMonthSelfCK: 0,
                yourselfConfirmError: 1,
            }));
            self.yourselfConfirmErrorOldValue = 1;
            self.monPerformanceFunDto = ko.observable(new MonPerformanceFunDto({
                cid: '',
                comment: null,
                dailySelfChkDispAtr: 0,
            }));
            self.restrictConfirmEmploymentDto = ko.observable(new RestrictConfirmEmploymentDto({
                companyID: '',
                confirmEmployment: false,
            }));

            self.appTypeEnum = ko.observableArray([]);
            
            let appTypeEnum = __viewContext.enums.ApplicationType;
            _.forEach(appTypeEnum, (item) => {
                self.appTypeEnum().push({
                    value: item.value,
                    name: item.name
                })
            });
            self.appType = ko.observable('');
            self.appTypeDto.subscribe((value) => {
                let valueSort = _.sortBy(value.appTypes());

                let appType = valueSort.map((item) => {
                    let itemModel = _.find(self.appTypeEnum(), function(obj) {
                        return obj.value == item;
                    });
                    if(!_.isEmpty(itemModel)) {
                        return itemModel.name;
                    }
                }).join("、");

                self.appType(appType);
            });
        }


        start(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();

            self.$blockui("grayout");
            $.when(self.getIdentity(), self.getApproval(), self.getDaily(), self.getMonthly(), self.getAppType(), self.getRestrict(),self.licenseCheck()).done(() => {
                dfd.resolve();
            }).always(() => {
                nts.uk.ui.errors.clearAll();
                self.$blockui("hide");
            });
            return dfd.promise();
        }

        jumpTo() {
            let self = this;
            nts.uk.request.jump("/view/kdw/006/a/index.xhtml");
        }

        //get ApplicationCall 呼び出す申請一覧
        getAppType(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getAppType().done(function(data: IAppTypeDto) {
                if (data) {
                    self.appTypeDto(new AppTypeDto(data));
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }

        //Get IdentityProcess 本人確認処理の利用設定
        getIdentity(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getIdentity().done(function(data: IIdentityProcessDto) {
                if (data) {
                    self.identityProcessDto(new IdentityProcessDto(data));
                    self.yourselfConfirmErrorOldValue = self.identityProcessDto().yourselfConfirmError();
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }

        // get ApprovalProcess 承認処理の利用設定 
        getApproval(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getApproval().done(function(data: IApprovalProcessDto) {
                if (data) {
                    self.approvalProcessDto(new ApprovalProcessDto(data));
                    self.supervisorConfirmErrorOldValue = self.approvalProcessDto().supervisorConfirmError();
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }

        // get DaiPerformanceFun 日別実績の修正の機能 
        getDaily(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getDaily().done(function(data: IDaiPerformanceFunDto) {
                if (data) {
                    self.daiPerformanceFunDto(new DaiPerformanceFunDto(data));
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }

        // get MonPerformanceFun 月別実績の修正の機能
        getMonthly(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getMonthly().done(function(data: IMonPerformanceFunDto) {
                if (data) {
                    self.monPerformanceFunDto(new MonPerformanceFunDto(data));
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }
        
         licenseCheck(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.licenseCheck().done(function(data: any) {
                 if(data == true){
                     self.checkLicense(true);
                 }   
                dfd.resolve();
            });
            return dfd.promise();
        }

        // get RestrictConfirmEmployment 就業確定の機能制限
        getRestrict(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            service.getRestrictConfirmEmp().done(function(data: IRestrictConfirmEmploymentDto) {
                if (data) {
                    self.restrictConfirmEmploymentDto(new RestrictConfirmEmploymentDto(data));
                    dfd.resolve();
                } else {
                    dfd.resolve();
                }
            });
            return dfd.promise();
        }

        // when click register button 
        saveData() {
            let self = this;

            let dayFuncControl: IDayFuncControl = self.toDayFuncControl();
            self.monPerformanceFunDto().cid(__viewContext.user.companyId);
            self.restrictConfirmEmploymentDto().companyID(__viewContext.user.companyId);
            self.$blockui("grayout");
            self.$validate().then((valid: boolean) => {
                if (valid) {
                    service.updateDayFuncControl(dayFuncControl).done(function() {
                        service.updateMonthly(ko.toJS(self.monPerformanceFunDto)).done(function() {
                            service.updateAppType(ko.toJS(self.appTypeDto)).done(function() {
                                service.updateRestrictConfirmEmp(ko.toJS(self.restrictConfirmEmploymentDto)).done(function() {
                                    self.$blockui("show");
                                    self.$dialog.info({ messageId: "Msg_15" }).then(() => {
                                        location.reload();
                                    });
                                    self.$blockui("hide");
                                })
                            });
                        });
                    }).always(() => {
                        self.$blockui("hide");
                    });
                }
            }).always(() => {
                self.$blockui("hide");
            });
        }

        toDayFuncControl(): IDayFuncControl{
            let self = this;

            let dayFuncControl: IDayFuncControl = {} as IDayFuncControl;
            dayFuncControl.cid = __viewContext.user.companyId;
            dayFuncControl.comment = self.daiPerformanceFunDto().comment();
            dayFuncControl.disp36Atr = self.daiPerformanceFunDto().disp36Atr();
            dayFuncControl.flexDispAtr = self.daiPerformanceFunDto().flexDispAtr();
            dayFuncControl.checkErrRefDisp = self.daiPerformanceFunDto().checkErrRefDisp();
            dayFuncControl.daySelfChk = self.identityProcessDto().useDailySelfCk();
            dayFuncControl.monSelfChK = self.identityProcessDto().useMonthSelfCK();
            if (self.identityProcessDto().useDailySelfCk()) {
                dayFuncControl.daySelfChkError = self.identityProcessDto().yourselfConfirmError();
            } else {
                dayFuncControl.daySelfChkError = self.yourselfConfirmErrorOldValue;
            }
            dayFuncControl.dayBossChk = self.approvalProcessDto().useDailyBossChk();
            dayFuncControl.monBossChk = self.approvalProcessDto().useMonthBossChk();
            if (self.approvalProcessDto().useDailyBossChk()) {
                dayFuncControl.dayBossChkError = self.approvalProcessDto().supervisorConfirmError();
            } else {
                dayFuncControl.dayBossChkError = self.supervisorConfirmErrorOldValue;
            }

            return dayFuncControl;
        }

        openHDialog() {
            let self = this;
            let share = {
                appTypes: self.appTypeDto().appTypes(),
                multi: true,
            }
            nts.uk.ui.windows.setShared('kdw006CResult', share);
            nts.uk.ui.windows.sub.modal("../h/index.xhtml", { title: "計算式の設定" }).onClosed(() => {
                let temp = nts.uk.ui.windows.getShared("kdw006HResult");
                if (temp) {
                    let output = {
                        appTypes: temp.map((item: any) => parseInt(item, 10)),
                    }
                    self.appTypeDto(new AppTypeDto(output));
                }
            });
        }
    }

    interface IDayFuncControl {
        cid: string;
        comment: string;
        disp36Atr: boolean;
        flexDispAtr: boolean;
        checkErrRefDisp : boolean;
        daySelfChk: boolean;
        monSelfChK: boolean;
        daySelfChkError: number;
        dayBossChk: boolean;
        monBossChk: boolean;
        dayBossChkError: number;
    }

    interface IDaiPerformanceFunDto {
        cid: string;
        comment: string;
        disp36Atr: number;
        flexDispAtr: number;
        checkErrRefDisp : number;
    }

    class DaiPerformanceFunDto {
        cid: KnockoutObservable<string>;
        comment: KnockoutObservable<string>;
        disp36Atr: KnockoutObservable<boolean>;
        flexDispAtr: KnockoutObservable<boolean>;
        checkErrRefDisp : KnockoutObservable<boolean>;

        constructor(param: IDaiPerformanceFunDto) {
            let self = this;
            self.cid = ko.observable(param.cid);
            self.comment = ko.observable(param.comment);
            self.disp36Atr = ko.observable(param.disp36Atr == 1 ? true : false);
            self.flexDispAtr = ko.observable(param.flexDispAtr == 1 ? true : false);
            self.checkErrRefDisp =  ko.observable(param.checkErrRefDisp == 1 ? true : false);
        }
    }

    interface IApprovalProcessDto {
        cid: string;
        useDailyBossChk: number;
        useMonthBossChk: number;
        supervisorConfirmError: number;
    }
    class ApprovalProcessDto {
        cid: KnockoutObservable<string>;
        useDailyBossChk: KnockoutObservable<boolean>;
        useMonthBossChk: KnockoutObservable<boolean>;
        supervisorConfirmError: KnockoutObservable<number>;

        constructor(param: IApprovalProcessDto) {
            let self = this;
            self.cid = ko.observable(param.cid);
            self.useDailyBossChk = ko.observable(param.useDailyBossChk == 1 ? true : false);
            self.useMonthBossChk = ko.observable(param.useMonthBossChk == 1 ? true : false);
            self.supervisorConfirmError = ko.observable(param.supervisorConfirmError);
        }
    }

    interface IAppTypeDto {
        appTypes: number[];
    }

    class AppTypeDto {
        appTypes: KnockoutObservableArray<number>;
        constructor(param: IAppTypeDto) {
            let self = this;
            self.appTypes = ko.observableArray(param.appTypes);
        }
    }

    interface IIdentityProcessDto {
        cid: string;
        useDailySelfCk: number;
        useMonthSelfCK: number;
        yourselfConfirmError: number;
    }
    class IdentityProcessDto {
        cid: KnockoutObservable<string>;
        useDailySelfCk: KnockoutObservable<boolean>;
        useMonthSelfCK: KnockoutObservable<boolean>;
        yourselfConfirmError: KnockoutObservable<number>;

        constructor(param: IIdentityProcessDto) {
            let self = this;
            self.cid = ko.observable(param.cid);
            self.useDailySelfCk = ko.observable(param.useDailySelfCk == 1 ? true : false);
            self.useMonthSelfCK = ko.observable(param.useMonthSelfCK == 1 ? true : false);
            self.yourselfConfirmError = ko.observable(param.yourselfConfirmError);
        }
    }

    interface IRestrictConfirmEmploymentDto {
        companyID: string;
        confirmEmployment: boolean;
    }
    
    class RestrictConfirmEmploymentDto {
        companyID: KnockoutObservable<string>;
        confirmEmployment: KnockoutObservable<boolean>;

        constructor(param: IRestrictConfirmEmploymentDto) {
            let self = this;
            self.companyID = ko.observable(param.companyID);
            self.confirmEmployment = ko.observable(param.confirmEmployment);
        }
    }

    interface IMonPerformanceFunDto {
        cid: string;
        comment: string;
        dailySelfChkDispAtr: number;
    }
    class MonPerformanceFunDto {
        cid: KnockoutObservable<string>;
        comment: KnockoutObservable<string>;
        dailySelfChkDispAtr: KnockoutObservable<boolean>;
        constructor(param: IMonPerformanceFunDto) {
            let self = this;
            self.cid = ko.observable(param.cid);
            self.comment = ko.observable(param.comment);
            self.dailySelfChkDispAtr = ko.observable(param.dailySelfChkDispAtr == 1 ? true : false);
        }
    }

    class ItemModel {
        code: number;
        name: string;

        constructor(code: number, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    function getNames(): string {
        let arr = [1, 3];
        let numArr = [new ItemModel(1, 'name1'), new ItemModel(2, 'name2'), new ItemModel(3, 'name3')];
        let result = "";
        arr.forEach((code) => {
            let itemModel = _.find(numArr, (item) => { return item.code == code });
            result += itemModel.name;
        });
        return result;

    }
}
