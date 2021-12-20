module nts.uk.com.view.cas003.a {
    export module viewmodel {
        import getText = nts.uk.resource.getText;
        import infor = nts.uk.ui.dialog.info;
        import alert = nts.uk.ui.dialog.alert;
        import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
        import blockUI = nts.uk.ui.block;
        export class ScreenModel {
            passPolicyCheck: KnockoutObservable<boolean>;
            firstTimeCheck: KnockoutObservable<boolean>;
            violationPassCheck: KnockoutObservable<boolean>;
            accLockCheck: KnockoutObservable<boolean>;
            simpleValue: KnockoutObservable<string>;
            lowestDigits: KnockoutObservable<number>;
            alphabetDigit: KnockoutObservable<number>;
            numberOfDigits: KnockoutObservable<number>;
            symbolCharacters: KnockoutObservable<number>;
            historyCount: KnockoutObservable<number>;
            validityPeriod: KnockoutObservable<number>;
            notificationPasswordChange: KnockoutObservable<number>;
            lockInterval: KnockoutObservable<number>;
            errorCount: KnockoutObservable<number>;
            lockOutMessage: KnockoutObservable<string>;


            constructor() {
                var self = this;
                self.simpleValue = ko.observable("");
                self.lowestDigits = ko.observable(null);
                self.alphabetDigit = ko.observable(null);
                self.numberOfDigits = ko.observable(null);
                self.symbolCharacters = ko.observable(null);
                self.historyCount = ko.observable(null);
                self.validityPeriod = ko.observable(null);
                self.notificationPasswordChange = ko.observable(null);
                self.lockInterval = ko.observable(null);
                self.errorCount = ko.observable(null);
                self.lockOutMessage = ko.observable("");
                self.passPolicyCheck = ko.observable(true);
                self.violationPassCheck = ko.observable(false);
                self.firstTimeCheck = ko.observable(false);
                self.accLockCheck = ko.observable(true);
                self.passPolicyCheck.subscribe(x => {
                    if (!x) {
                        if ($("#txtlowestDigit").ntsError("hasError")) {
                            $("#txtlowestDigit").ntsError("clear");
                        }
                        if ($("#txtAlphabetDigit").ntsError("hasError")) {
                            $("#txtAlphabetDigit").ntsError("clear");
                        }
                        if ($("#txtNumberOfDigit").ntsError("hasError")) {
                            $("#txtNumberOfDigit").ntsError("clear");
                        }
                        if ($("#txtSymbolCharacters").ntsError("hasError")) {
                            $("#txtSymbolCharacters").ntsError("clear");
                        }
                        if ($("#txtHistoryCount").ntsError("hasError")) {
                            $("#txtHistoryCount").ntsError("clear");
                        }
                        if ($("#txtValidityPeriod").ntsError("hasError")) {
                            $("#txtValidityPeriod").ntsError("clear");
                        }
                        if ($("#txtPasswordChange").ntsError("hasError")) {
                            $("#txtPasswordChange").ntsError("clear");
                        }

                    }
                });
                self.accLockCheck.subscribe(x => {
                    if (!x) {
                        if ($("#txtLockInterval").ntsError("hasError")) {
                            $("#txtLockInterval").ntsError("clear");
                        }
                        if($("#txtErrorCount").ntsError("hasError")){
                            $("#txtErrorCount").ntsError("clear");
                        }
                        if($("#txtLockOutMessage").ntsError("hasError")){
                            $("#txtLockOutMessage").ntsError("clear");
                        }
                    }
                });
            }



            exportExcel(): void {
                service.saveAsExcel().done(function(data) {

                }).fail(function(res: any) {
                    nts.uk.ui.dialog.alertError(res).then(function() { nts.uk.ui.block.clear(); });
                }).always(()=>{
                    block.clear();
                });
             }
            
            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                let dfdPasswordPolicy = service.getPasswordPolicy();
                let dfdAccountLock = service.getAccountLockPolicy();
                $.when(dfdPasswordPolicy, dfdAccountLock)
                    .done((dfdPasswordPolicyData, dfdAccountLockData) => {
                        if (!isNullOrUndefined(dfdPasswordPolicyData)) {
                            if (dfdPasswordPolicyData.isUse) {
                                self.passPolicyCheck(dfdPasswordPolicyData.isUse);
                                self.notificationPasswordChange(dfdPasswordPolicyData.notificationPasswordChange);
                                self.firstTimeCheck(dfdPasswordPolicyData.initialPasswordChange);
                                self.violationPassCheck(dfdPasswordPolicyData.loginCheck);
                                self.historyCount(dfdPasswordPolicyData.historyCount);
                                self.lowestDigits(dfdPasswordPolicyData.lowestDigits);
                                self.validityPeriod(dfdPasswordPolicyData.validityPeriod);
                                self.numberOfDigits(dfdPasswordPolicyData.numberOfDigits);
                                self.symbolCharacters(dfdPasswordPolicyData.symbolCharacters);
                                self.alphabetDigit(dfdPasswordPolicyData.alphabetDigit);
                            } else {
                                self.passPolicyCheck(dfdPasswordPolicyData.isUse);
                            }
                        }
                        if (!isNullOrUndefined(dfdAccountLockData)) {
                            if (dfdAccountLockData.isUse) {
                                self.accLockCheck(dfdAccountLockData.isUse);
                                self.errorCount(dfdAccountLockData.errorCount);
                                self.lockInterval(dfdAccountLockData.lockInterval);
                                self.lockOutMessage(dfdAccountLockData.lockOutMessage);
                            } else {
                                self.accLockCheck(dfdAccountLockData.isUse);
                            }
                        }
                        dfd.resolve();
                    }).fail((x1, x2) => {
                        alert(x1.message + '\n' + x2.message);
                        dfd.reject();
                    });

                return dfd.promise();
            }

            submitData(): void {
                var self = this;
                if (self.passPolicyCheck()) {
                    $("#txtlowestDigit").trigger("validate");
                    $("#txtAlphabetDigit").trigger("validate");
                    $("#txtNumberOfDigit").trigger("validate");
                    $("#txtSymbolCharacters").trigger("validate");
                    $("#txtHistoryCount").trigger("validate");
                    $("#txtValidityPeriod").trigger("validate");
                    $("#txtPasswordChange").trigger("validate");
                }
                if (self.accLockCheck()) {
                    $("#txtLockInterval").trigger("validate");
                    $("#txtErrorCount").trigger("validate");
                    $("#txtLockOutMessage").trigger("validate");
                }


                if (nts.uk.ui.errors.hasError()) {
                    return;
                }
                var accountPolicy = null;
                if (!self.passPolicyCheck() && self.accLockCheck()) {

                    accountPolicy = new AccountPolicy({ errorCount: self.errorCount(), lockInterval: self.lockInterval(), lockOutMessage: self.lockOutMessage(), isAccLockUse: self.accLockCheck(), notificationPasswordChange: 0, loginCheck: false, initialPasswordChange: false, isPasswordUse: self.passPolicyCheck(), historyCount: 0, lowestDigits: 1, validityPeriod: 0, numberOfDigits: 0, symbolCharacters: 0, alphabetDigit: 0 });

                } else if (self.passPolicyCheck() && !self.accLockCheck()) {
                    accountPolicy = new AccountPolicy({ errorCount: 1, lockInterval: 0, lockOutMessage: self.lockOutMessage(), isAccLockUse: self.accLockCheck(), notificationPasswordChange: self.notificationPasswordChange(), loginCheck: self.violationPassCheck(), initialPasswordChange: self.firstTimeCheck(), isPasswordUse: self.passPolicyCheck(), historyCount: self.historyCount(), lowestDigits: self.lowestDigits(), validityPeriod: self.validityPeriod(), numberOfDigits: self.numberOfDigits(), symbolCharacters: self.symbolCharacters(), alphabetDigit: self.alphabetDigit() });
                } else if (!self.passPolicyCheck() && !self.accLockCheck()) {
                    accountPolicy = new AccountPolicy({ errorCount: 1, lockInterval: 0, lockOutMessage: self.lockOutMessage(), isAccLockUse: self.accLockCheck(), notificationPasswordChange: 0, loginCheck: false, initialPasswordChange: false, isPasswordUse: self.passPolicyCheck(), historyCount: 0, lowestDigits: 1, validityPeriod: 0, numberOfDigits: 0, symbolCharacters: 0, alphabetDigit: 0 });
                }
                else {
                    accountPolicy = new AccountPolicy({ errorCount: self.errorCount(), lockInterval: self.lockInterval(), lockOutMessage: self.lockOutMessage(), isAccLockUse: self.accLockCheck(), notificationPasswordChange: self.notificationPasswordChange(), loginCheck: self.violationPassCheck(), initialPasswordChange: self.firstTimeCheck(), isPasswordUse: self.passPolicyCheck(), historyCount: self.historyCount(), lowestDigits: self.lowestDigits(), validityPeriod: self.validityPeriod(), numberOfDigits: self.numberOfDigits(), symbolCharacters: self.symbolCharacters(), alphabetDigit: self.alphabetDigit() });
                }
                blockUI.grayout();
                service.updateAccountPolicy(accountPolicy).done(() => {
                    infor(nts.uk.resource.getMessage("Msg_15", []));
                }).always(()=> blockUI.clear());

            }


        }


        interface IAccountPolicy {
            errorCount: number;
            lockInterval: number;
            lockOutMessage: string;
            isAccLockUse: boolean;

            notificationPasswordChange: number;
            loginCheck: boolean;
            initialPasswordChange: boolean;
            isPasswordUse: boolean;
            historyCount: number;
            lowestDigits: number;
            validityPeriod: number;
            numberOfDigits: number;
            symbolCharacters: number;
            alphabetDigit: number;
        }


        export class AccountPolicy {
            errorCount: number;
            lockInterval: number;
            lockOutMessage: string;
            isAccLockUse: boolean;

            notificationPasswordChange: number;
            loginCheck: boolean;
            initialPasswordChange: boolean;
            isPasswordUse: boolean;
            historyCount: number;
            lowestDigits: number;
            validityPeriod: number;
            numberOfDigits: number;
            symbolCharacters: number;
            alphabetDigit: number;

            constructor(param: IAccountPolicy) {
                this.errorCount = param.errorCount;
                this.lockInterval = param.lockInterval;
                this.lockOutMessage = param.lockOutMessage;
                this.isAccLockUse = param.isAccLockUse;
                this.notificationPasswordChange = param.notificationPasswordChange;
                this.loginCheck = param.loginCheck;
                this.initialPasswordChange = param.initialPasswordChange;
                this.isPasswordUse = param.isPasswordUse;
                this.historyCount = param.historyCount;
                this.lowestDigits = param.lowestDigits;
                this.validityPeriod = param.validityPeriod;
                this.numberOfDigits = param.numberOfDigits;
                this.symbolCharacters = param.symbolCharacters;
                this.alphabetDigit = param.alphabetDigit;
            }
        }







    }
}



