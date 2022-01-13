module nts.uk.pr.view.ccg007.d {
    export module viewmodel {
        import SystemConfigDto = service.SystemConfigDto;
        import ContractDto = service.ContractDto;
        import blockUI = nts.uk.ui.block;
        import CheckChangePassDto = service.CheckChangePassDto;
        import character = nts.uk.characteristics;

        @bean()
        export class ScreenModel extends ko.ViewModel {
            employeeCode: KnockoutObservable<string>;
            password: KnockoutObservable<string>;
            companyList: KnockoutObservableArray<CompanyItemModel>;
            selectedCompanyCode: KnockoutObservable<string>;
            companyName: KnockoutObservable<string>;
            isSaveLoginInfo: KnockoutObservable<boolean>;
            contractCode: KnockoutObservable<string>;
            contractPassword: KnockoutObservable<string>;
            isSignOn: KnockoutObservable<boolean> = ko.observable(false);
            displayLogin: KnockoutObservable<boolean> = ko.observable(false);

            constructor() {
                super();

                const vm = this;

                vm.employeeCode = ko.observable('');
                vm.password = ko.observable('');
                vm.companyList = ko.observableArray([]);
                vm.selectedCompanyCode = ko.observable('');
                vm.companyName = ko.observable('');
                vm.isSaveLoginInfo = ko.observable(true);
                vm.contractCode = ko.observable('');
                vm.contractPassword = ko.observable('');

                vm.selectedCompanyCode.subscribe(function (code) {
                    _.each(vm.companyList(), function (item, index) {
                        if ((item.companyCode == code)) {
                            vm.companyName(item.companyName);
                        }
                    });
                });

                vm.$window.header(false);
            }

            created() {
                var self = this;
                //get url
                let url = _.toLower(_.trim(_.trim($(location).attr('href')), '%20'));
                let isSignOn = url.indexOf('signon=on') >= 0 || url.indexOf('signon=oN') >= 0 || url.indexOf('signon=On') >= 0
                    || url.indexOf('signon=ON') >= 0;

                self.isSignOn(isSignOn);

                if (!isSignOn) {
                    self.displayLogin(true);
                }

                var dfd = $.Deferred<void>();
                let defaultContractCode: string = __viewContext.env.isOnPremise 
                                    		? __viewContext.user.contractCode
                                    		: "000000000000";
                //get system config
                blockUI.invisible();
    
                nts.uk.characteristics.restore("contractInfo").done(function (data: any) {
                    self.contractCode(data ? data.contractCode : "");
                    self.contractPassword(data ? data.contractPassword : "");
                    service.checkContract({ contractCode: data ? data.contractCode : "", contractPassword: data ? data.contractPassword : "" })
                        .done(function (showContractData: any) {
                            //check ShowContract
                            if (__viewContext.env.isOnPremise) {
                                nts.uk.characteristics.remove("contractInfo");
                                nts.uk.characteristics.save("contractInfo", { contractCode:defaultContractCode, contractPassword: self.contractPassword() });
                                self.contractCode(defaultContractCode);
                                self.contractPassword(null);
                                self.getEmployeeLoginSetting(defaultContractCode);
                            }
                            else {
                                if (showContractData.showContract && !showContractData.onpre) {
                                    self.openContractAuthDialog();
                                }
                                else {
                                    self.getEmployeeLoginSetting(data ? data.contractCode : null);
                                }
                            }
                            dfd.resolve();
                            blockUI.clear();
                        }).fail(function () {
                            dfd.resolve();
                            blockUI.clear();
                        });
                });

                service.ver().done(data => {
                    $("#ver").html(data.ver);
                });
            }

            mounted() {
                const vm = this;

                if ($('#contents-area').data('loaded')) {
                    $('[id=contents-area]:eq(1)').remove();
                    return;
                }
                $('#contents-area').data('loaded', true);


                nts.uk.characteristics.restore("form3LoginInfo").done(function (loginInfo: any) {
                    if (!loginInfo || !loginInfo.companyCode) {
                        $('#company-code-select').focus();
                    }
                    else {
                        if (!loginInfo.employeeCode) {
                            $('#employee-code-inp').focus();
                        }
                        else {
                            $('#password-input').focus();
                        }
                    }
                });
            }

            //when invalid contract 
            private openContractAuthDialog() {
                var self = this;
                nts.uk.ui.windows.sub.modal("/view/ccg/007/a/index.xhtml", {
                    height: 300,
                    width: 400,
                    title: nts.uk.resource.getText("CCG007_9"),
                    dialogClass: 'no-close'
                }).onClosed(() => {
                    var contractCode = nts.uk.ui.windows.getShared('contractCode');
                    var contractPassword = nts.uk.ui.windows.getShared('contractPassword');
                    var isSubmit = nts.uk.ui.windows.getShared('isSubmit');
                    self.contractCode(contractCode);
                    self.contractPassword(contractPassword);

                    //get url
                    let url = _.toLower(_.trim(_.trim($(location).attr('href')), '%20'));
                    let isSignOn = url.indexOf('signon=on') >= 0;

                    //Check signon
                    if (isSubmit && isSignOn) {
                        self.submitLogin(isSignOn);
                    } else {
                        if (isSubmit) {
                            self.getEmployeeLoginSetting(self.contractCode());
                        }
                        service.getAllCompany(self.contractCode()).done(function (data: Array<CompanyItemModel>) {
                            //get list company from server
                            self.companyList(data);
                            if (data.length > 0) {
                                self.selectedCompanyCode(self.companyList()[0].companyCode);
                            }
                        });
                    }
                });
            }

            private getEmployeeLoginSetting(contractCode: string): JQueryPromise<void> {
                var self = this;
                //get check signon
                let url = _.toLower(_.trim(_.trim($(location).attr('href')), '%20'));
                let isSignOn = url.indexOf('signon=on') >= 0;
                service.getEmployeeLoginSetting(contractCode).done(function (data: any) {
                    if (data.gotoForm1) {
                        nts.uk.request.jump("/view/ccg/007/b/index.xhtml");
                    }
                    else {
                        //シングルサインオン（Active DirectorySSO）かをチェックする
                        if (isSignOn) {
                            self.submitLogin(isSignOn);
                        }
                        else {
                            service.getAllCompany(contractCode).done(function (data: Array<CompanyItemModel>) {
                                //get list company from server 
                                self.companyList(data);
                                if (data.length > 0) {
                                    self.selectedCompanyCode(self.companyList()[0].companyCode);
                                    self.companyName(self.companyList()[0].companyName);
                                }
                                //get local storage info and set here
                                nts.uk.characteristics.restore("form3LoginInfo").done(function (loginInfo: any) {
                                    if (loginInfo) {
                                        self.selectedCompanyCode(loginInfo.companyCode);
                                        self.employeeCode(loginInfo.employeeCode);
                                    }
                                });
                            });
                        }
                    }
                });
            }

            private submitLogin(isSignOn: boolean) {
                var self = this;
                var submitData: any = {};
                submitData.companyCode = self.selectedCompanyCode();
                submitData.employeeCode = self.employeeCode();
                submitData.password = self.password();
                submitData.contractCode = self.contractCode();
                submitData.contractPassword = self.contractPassword();
                blockUI.invisible();
                service.submitLogin(submitData).done(function (messError: CheckChangePassDto) {
                    if (!nts.uk.util.isNullOrUndefined(messError.successMsg) && !nts.uk.util.isNullOrEmpty(messError.successMsg)) {
                        nts.uk.ui.dialog.info({ messageId: messError.successMsg }).then(() => {
                            self.doSuccessLogin(messError);
                        });
                    } else {
                        self.doSuccessLogin(messError);
                    }
                    blockUI.clear();
                }).fail(function (res: any) {
                    blockUI.clear();
                    if (self.isSignOn()) {//SIGNON
                        if (!nts.uk.util.isNullOrEmpty(res.messageId)) {
                            if (res.messageId == 'Msg_876') {//ActiveDirectory変換マスタが見つかりません
                                nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds }).then(() => {
                                    nts.uk.request.jump("/view/ccg/007/sso/adsso.xhtml", { 'errAcc': true, 'errMsg': res.message });
                                });
                            } else {
                                nts.uk.request.jump("/view/ccg/007/sso/adsso.xhtml", { 'errAcc': false, 'errMsg': res.message });
                            }
                        } else {
                            nts.uk.request.jump("/view/ccg/007/sso/adsso.xhtml", { 'errAcc': false, 'errMsg': res.message });
                        }
                    } else {//NORMAL
                        if (nts.uk.util.isNullOrEmpty(res.messageId)) {
                            nts.uk.ui.dialog.alertError(res.message);
                        } else {
                            nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                        }
                    }
                });
            }

            private SamlLogin() {
                var self = this;
                var samlData: any = {};
                samlData.tenantCode = self.contractCode();
                samlData.tenantPassword = self.contractPassword();
                samlData.issueUrl = location.href;
                samlData.requestUrl = "";
                
                blockUI.invisible();
                service.samlLogin(samlData).done(authenticateInfo => {
                    if(!nts.uk.util.isNullOrUndefined(authenticateInfo.errorMessage)&&!nts.uk.util.isNullOrEmpty(authenticateInfo.errorMessage)){
                        nts.uk.ui.dialog.info({ messageId: authenticateInfo.errorMessage });
                    }
                    blockUI.clear();
                    if(authenticateInfo.useSamlSso){
                        // SSO運用している場合
                        location.href = authenticateInfo.authenUrl;
                    } else {
                        // SSO運用していない場合
                    }
                })
            }

            
            private doSuccessLogin(messError){
                var self = this;
                self.$window.storage('notification', '');
                if (messError.showContract) {
                    self.openContractAuthDialog();
                }
                else {
                    if (!nts.uk.util.isNullOrEmpty(messError.msgErrorId) && messError.msgErrorId == 'Msg_1517') {
                        //確認メッセージ（Msg_1517）を表示する{0}【残り何日】
                        nts.uk.ui.dialog.confirm({ messageId: messError.msgErrorId, messageParams: [messError.spanDays] })
                            .ifYes(() => {
                                messError.changePassReason = 'Msg_1523';
                                self.OpenDialogE(messError);
                            })
                            .ifNo(() => {
                                nts.uk.request.login.keepUsedLoginPage("/nts.uk.com.web/view/ccg/007/d/index.xhtml");
                                //Save LoginInfo
                                nts.uk.characteristics.save("form3LoginInfo", { companyCode: self.selectedCompanyCode(), employeeCode: self.employeeCode() }).done(function () {
                                    nts.uk.request.jump("/view/ccg/008/a/index.xhtml", { screen: 'login' });
                                });
                            });
                    } else
                        //check MsgError
                        if (!nts.uk.util.isNullOrEmpty(messError.msgErrorId) || messError.showChangePass) {
                            if (messError.showChangePass) {
                                self.OpenDialogE(messError);
                            } else {
                                nts.uk.ui.dialog.alertError({ messageId: messError.msgErrorId });
                                self.password("");
                            }
                        } else {
                            if (self.isSignOn()) {
                                nts.uk.request.login.keepUsedLoginPage("/nts.uk.com.web/view/ccg/007/d/index.xhtml?signon=on");
                            } else {
                                nts.uk.request.login.keepUsedLoginPage("/nts.uk.com.web/view/ccg/007/d/index.xhtml");
                            }
                            //set mode login
                            character.remove("loginMode").done(function () {
                                //                                loginMode: true - sign on
                                //                                loginMode: false - normal
                                character.save("loginMode", self.isSignOn());
                            })
                            //Remove LoginInfo
                            nts.uk.characteristics.remove("form3LoginInfo").done(function () {
                                //check SaveLoginInfo
                                if (self.isSaveLoginInfo()) {
                                    //Save LoginInfo
                                    nts.uk.characteristics.save("form3LoginInfo", { companyCode: self.selectedCompanyCode(), employeeCode: self.employeeCode() })
                                        .done(function () {
                                            nts.uk.request.jump("/view/ccg/008/a/index.xhtml", { screen: 'login' });
                                        });
                                } else {
                                    nts.uk.request.jump("/view/ccg/008/a/index.xhtml", { screen: 'login' });
                                }
                            });
                        }
                }
            }

            //open dialog E 
            private OpenDialogE(messError) {
                let self = this;

                //set LoginId to dialog
                nts.uk.ui.windows.setShared('parentCodes', {
                    form1: false,
                    contractCode: self.contractCode(),
                    employeeCode: self.employeeCode(),
                    companyCode: self.selectedCompanyCode()
                }, true);
                nts.uk.ui.windows.setShared("changePw", {
                    reasonUpdatePw: messError.changePassReason,
                    spanDays: messError.spanDays
                });
                nts.uk.ui.windows.sub.modal('/view/ccg/007/e/index.xhtml', {
                    width: 520,
                    height: 360
                }).onClosed(function (): any {
                    var changePwDone = nts.uk.ui.windows.getShared('changePwDone');
                    if (changePwDone) {
                        nts.uk.request.login.keepUsedLoginPage("/nts.uk.com.web/view/ccg/007/d/index.xhtml");
                        //Save LoginInfo
                        nts.uk.characteristics.save("form3LoginInfo", { companyCode: self.selectedCompanyCode(), employeeCode: self.employeeCode() }).done(function () {
                            nts.uk.request.jump("/view/ccg/008/a/index.xhtml", { screen: 'login' });
                        });
                    }
                })
            }

            //open dialog G
            OpenDialogG() {
                const vm = this;

                //set LoginId to dialog
                nts.uk.ui.windows.setShared('parentCodes', {
                    form1: false,
                    companyCode: vm.selectedCompanyCode(),
                    companyName: vm.companyName(),
                    contractCode: vm.contractCode(),
                    employeeCode: vm.employeeCode(),
                    isFromD: true
                }, true);

                vm.$window
                    .modal('/view/ccg/007/g/index.xhtml', {
                        form1: false,
                        companyCode: vm.selectedCompanyCode(),
                        companyName: vm.companyName(),
                        contractCode: vm.contractCode(),
                        employeeCode: vm.employeeCode(),
                        isFromD: true
                    }, {
                        width: 500,
                        height: 400
                    })
                    .then(() => { })
            }

            private account() {
                service.account().done(data => {
                    alert('domain: ' + data.domain + '\n' + 'user name: ' + data.userName)
                });
            }
        }

        export class CompanyItemModel {
            companyId: string;
            companyCode: string;
            companyName: string;
        }
    }
}