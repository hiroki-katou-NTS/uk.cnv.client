module nts.uk.pr.view.kmf001.c {
    export module viewmodel {
        
        import EnumertionModel = service.model.EnumerationModel;
        
        export class ScreenModel {
            manageDistinctList: KnockoutObservableArray<EnumertionModel>;
            selectedAnnualManage: KnockoutObservable<number>;
            enableAnnualVacation: KnockoutObservable<boolean>;
            
            selectedAddAttendanceDay: KnockoutObservable<number>;
            selectedMaxManageSemiVacation: KnockoutObservable<number>;
            maxDayReferenceList: KnockoutObservableArray<EnumertionModel>;
            C531List: KnockoutObservableArray<C531Model>;
            selectC531: KnockoutObservable<number>;
            C535List: KnockoutObservableArray<C535Model>;
            selectC535: KnockoutObservable<number>;
            uniformTime: KnockoutObservable<string>;
            selectedMaxNumberSemiVacation: KnockoutObservable<number>;
            maxNumberCompany: KnockoutObservable<string>;
            requiredMaxNumberCompany: KnockoutObservable<boolean>;
            maxGrantDay: KnockoutObservable<string>;
            maxRemainingDay: KnockoutObservable<string>;
            numberYearRetain: KnockoutObservable<string>;
            enableMaxNumberCompany: KnockoutObservable<boolean>;
            roundProcessClassificationList: KnockoutObservableArray<EnumertionModel>;
            selectedRoundProcessCla: KnockoutObservable<number>;
            
            annualPriorityList: KnockoutObservableArray<EnumertionModel>;
            selectedAnnualPriority: KnockoutObservable<number>;
            
            displayDivisionList: KnockoutObservableArray<EnumertionModel>;
            selectedNumberRemainingYearly: KnockoutObservable<number>;
            selectedNextAnunalVacation: KnockoutObservable<number>;
            yearlyOfNumberDays: KnockoutObservable<string>;
            
            selectedTimeManagement: KnockoutObservable<number>;
            vacationTimeUnitList: KnockoutObservableArray<EnumertionModel>;
            enableTimeSetting: KnockoutObservable<boolean>;
            selectedVacationTimeUnit: KnockoutObservable<number>;
            selectedManageUpperLimitDayVacation: KnockoutObservable<number>;
            selectedMaxDayVacation: KnockoutObservable<number>;
            timeMaxNumberCompany: KnockoutObservable<string>;
            requiredTimeMaxNumberCompany: KnockoutObservable<boolean>;
            enableTimeMaxNumberCompany: KnockoutObservable<boolean>;            
            selectedroundProcessClassific: KnockoutObservable<number>;
            roundProcessClassificList: KnockoutObservableArray<EnumertionModel>;
            enableC222: KnockoutObservable<boolean>; 
            inputOption: KnockoutObservable<any>; 
            requiredC533: KnockoutObservable<boolean>; 
            requiredC535: KnockoutObservable<boolean>; 
            
            enableC5_31 : KnockoutObservable<boolean>;
            
            // Data backup
            dataBackup: KnockoutObservable<any>;
            
            constructor() {
                let self = this;
                // 年休の管理
                self.manageDistinctList = ko.observableArray([]);
                self.selectedAnnualManage = ko.observable(1);
                self.inputOption = ko.mapping.fromJS(new nts.uk.ui.option.TextEditorOption({
                    filldirection: "right",
                    fillcharacter: "0",
                    width: "50"
                }));
                // 年次有給休暇の扱い
                self.selectedAddAttendanceDay = ko.observable(1);
                self.selectedMaxManageSemiVacation = ko.observable(1);
                self.maxDayReferenceList = ko.observableArray([]);
                self.selectedMaxNumberSemiVacation = ko.observable(0);
                self.maxNumberCompany = ko.observable("5");
                
                self.maxGrantDay = ko.observable("0");
                self.maxRemainingDay = ko.observable("");
                self.numberYearRetain = ko.observable("2");
                self.yearlyOfNumberDays = ko.observable("0");
                self.roundProcessClassificationList = ko.observableArray([]);
                self.selectedRoundProcessCla = ko.observable(0);
                //C531
                self.C531List = ([
                    new C531Model(0, nts.uk.resource.getText('KMF001_339')),
                    new C531Model(1, nts.uk.resource.getText('KMF001_340'))
                ]);
                self.selectC531  = ko.observable(0);
                 self.C535List = ([
                    new C535Model(0, nts.uk.resource.getText('KMF001_343')),
                    new C535Model(1, nts.uk.resource.getText('KMF001_344'))
                ]);
                 self.selectC535  = ko.observable(0);
                self.uniformTime = ko.observable('0:00');
                
                // 年休取得の設定
                self.annualPriorityList = ko.observableArray([]);
                self.selectedAnnualPriority = ko.observable(0);
                
                // 表示設定
                self.displayDivisionList = ko.observableArray([]);
                self.selectedNumberRemainingYearly = ko.observable(1);
                self.selectedNextAnunalVacation = ko.observable(1);
                
                // 時間年休
                self.selectedTimeManagement = ko.observable(1);
                self.vacationTimeUnitList = ko.observableArray([]);
                
                self.selectedVacationTimeUnit = ko.observable(0);
                self.selectedMaxDayVacation = ko.observable(0);
                self.selectedManageUpperLimitDayVacation = ko.observable(1);
                self.timeMaxNumberCompany = ko.observable("5");
                
                self.roundProcessClassificList = ko.observableArray([]);
                self.selectedroundProcessClassific = ko.observable(0);
                
                // Data backup
                self.dataBackup = ko.observable(null);
                
                self.enableAnnualVacation = ko.computed(function() {
                    return self.selectedAnnualManage() == 1;
                }, self);
                self.enableMaxNumberCompany = ko.computed(function() {
                    return self.selectedMaxNumberSemiVacation() == 1 && self.enableAnnualVacation();
                }, self);
                self.enableTimeSetting = ko.computed(function() {
                    return self.selectedTimeManagement() == 1 && self.enableAnnualVacation();
                }, self);
                self.requiredMaxNumberCompany = ko.computed(function() {
                    return self.enableMaxNumberCompany() && self.selectedMaxManageSemiVacation() == 1;
                });
                self.requiredTimeMaxNumberCompany = ko.computed(function() {
                    return self.enableTimeSetting() && self.selectedManageUpperLimitDayVacation() == 1;
                });
                self.enableTimeMaxNumberCompany = ko.computed(function() {
                    return self.enableTimeSetting() && self.selectedMaxDayVacation() == 0;
                });
                self.enableC222 = ko.computed(function() {
                    return self.selectedMaxNumberSemiVacation() == 0 && self.selectedAnnualManage() == 1;
                });
                self.requiredC535 = ko.computed(function() {
                    return self.selectC531() == 1 && self.selectedAnnualManage() == 1;
                });
                self.requiredC533 = ko.computed(function() {
                    return self.selectC531() == 1 && self.selectedAnnualManage() == 1;
                }); 
                
                self.enableC5_31 = ko.computed(function() {
                    return self.selectedAnnualManage() == 1;
                }); 
                
                
                // subscribe
                self.selectedMaxManageSemiVacation.subscribe(function(value) {
                    if (value == 0) {
                        $('#max-number-company').ntsError('clear');
                    }
                });
                self.selectedManageUpperLimitDayVacation.subscribe(function(value) {
                    if (value == 0) {
                        $('#time-max-day-company').ntsError('clear');
                    }
                });
                self.enableTimeSetting.subscribe(function(value) {
                    if (value == 0) {
                        $('#yearLy-number-days').ntsError('clear');
                    }
                });
                self.enableTimeMaxNumberCompany.subscribe(function(value) {
                    if (value == 0) {
                        $('#time-max-day-company').ntsError('clear');
                    }
                });
                self.selectedAnnualManage.subscribe(function(value) {
                    self.clearError();
                });
            }
            
            public startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred<any>();
                $.when(self.loadManageDistinctEnums(), self.loadPreemptionPermitEnums(),
                        self.loadDisplayDivisionEnums(), self.loadTimeUnitEnums(), self.loadMaxDayReferenceEnums(), 
                        self.loadRoundProcessClassificationEnums(), self.loadRoundProcessClassificEnums()).done(function() {
                    self.loadSetting().done(() => {
                        $('#annual-manage').focus();
                    });
                    dfd.resolve();
                });
                return dfd.promise();
            }
            
            private save(): void {
                let self = this;
                let dfd = $.Deferred();
                if (!self.validate()) {
                    return;
                }
                let command = self.toJsObject();
                
                nts.uk.ui.block.grayout();

                nts.uk.pr.view.kmf001.c.service.save(command).done(function() {
                    self.loadSetting().done(function() {
                        $('#annual-manage').focus();
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        dfd.resolve();
                    });
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }
            
            private loadSetting(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findSetting().done(function(res: any) {
                    if (res) {
                        self.initUI(res);
                    }
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
            private toJsObject(): any {
                let self = this;
                let dataBackup = self.dataBackup();
                if (!dataBackup) {
                    dataBackup = self.defaultValue();
                }
                let command: any = {};
                
                command.annualManage = self.selectedAnnualManage();
                
                // Annual Setting
                command.addAttendanceDay = self.enableAnnualVacation() ? self.selectedAddAttendanceDay() : dataBackup.addAttendanceDay;
                command.maxManageSemiVacation = self.enableAnnualVacation() ? self.selectedMaxManageSemiVacation() : dataBackup.maxManageSemiVacation;
                command.maxNumberSemiVacation = self.enableAnnualVacation() ? self.selectedMaxNumberSemiVacation() : dataBackup.maxNumberSemiVacation;
                command.maxNumberCompany = self.enableC222() ? self.maxNumberCompany() : dataBackup.maxNumberCompany;
                command.maxGrantDay = self.enableAnnualVacation() ? self.maxGrantDay() : dataBackup.maxGrantDay;
                command.maxRemainingDay = self.enableAnnualVacation() ? self.maxRemainingDay() : dataBackup.maxRemainingDay;
                command.numberYearRetain = self.enableAnnualVacation() ? self.numberYearRetain() : dataBackup.numberYearRetain;
                command.annualPriority = self.enableAnnualVacation() ? self.selectedAnnualPriority() : dataBackup.annualPriority;
                command.remainingNumberDisplay = self.enableAnnualVacation() ? self.selectedNumberRemainingYearly() : dataBackup.remainingNumberDisplay;
                command.nextGrantDayDisplay = self.enableAnnualVacation() ? self.selectedNextAnunalVacation() : dataBackup.nextGrantDayDisplay;
                command.yearlyOfDays = self.enableAnnualVacation() ? self.yearlyOfNumberDays() : dataBackup.yearlyOfDays;
                command.roundProcessCla = self.enableAnnualVacation() ? self.selectedRoundProcessCla() : dataBackup.roundProcessCla;
                // Time Leave Setting
                command.timeManageType = self.enableAnnualVacation() ? self.selectedTimeManagement() : dataBackup.timeManageType;
                command.timeUnit = self.enableTimeSetting() ? self.selectedVacationTimeUnit() : dataBackup.timeUnit;
                command.manageMaxDayVacation = self.enableTimeSetting() ? self.selectedManageUpperLimitDayVacation() : dataBackup.manageMaxDayVacation;
                command.reference = self.enableTimeSetting() ? self.selectedMaxDayVacation() : dataBackup.reference;
                command.maxTimeDay = self.enableTimeMaxNumberCompany() ? self.timeMaxNumberCompany() : dataBackup.maxTimeDay;
                command.roundProcessClassific = self.enableTimeMaxNumberCompany() ? self.selectedroundProcessClassific() : dataBackup.roundProcessClassific;
                //http://localhost:8080/nts.uk.at.web/view/ksm/007/a/index.xhtml
                command.timeOfDayReference =  self.selectC531();
                command.uniformTime =  !isNaN(self.uniformTime()) ? self.uniformTime() : parseInt(self.uniformTime().split(':')[0])*60 + parseInt(self.uniformTime().split(':')[1]);
                command.contractTimeRound =  self.selectC535() ;
                
                return command;
            }
            
            private initUI(res: any): any {
                let self = this;
                
                // backup
                self.dataBackup(res);
                
                // set UI
                self.selectedAnnualManage(res.annualManage);
                
                // Annual Setting
                self.selectedAddAttendanceDay(res.addAttendanceDay);
                self.selectedMaxManageSemiVacation(res.maxManageSemiVacation);
                self.selectedMaxNumberSemiVacation(res.maxNumberSemiVacation);
                self.maxNumberCompany(res.maxNumberCompany);
                self.maxGrantDay(res.maxGrantDay);
                self.maxRemainingDay(res.maxRemainingDay);
                self.numberYearRetain(res.numberYearRetain);
                self.selectedAnnualPriority(res.annualPriority);
                self.selectedNumberRemainingYearly(res.remainingNumberDisplay);
                self.selectedNextAnunalVacation(res.nextGrantDayDisplay);
                self.yearlyOfNumberDays(res.yearlyOfDays);
                self.selectedRoundProcessCla(res.roundProcessCla);
                
                // Time Leave Setting
                self.selectedTimeManagement(res.timeManageType);
                self.selectedVacationTimeUnit(res.timeUnit);
                self.selectedManageUpperLimitDayVacation(res.manageMaxDayVacation);
                self.selectedMaxDayVacation(res.reference);
                self.timeMaxNumberCompany(res.maxTimeDay == null ? 5 : res.maxTimeDay);
                self.selectedroundProcessClassific(res.roundProcessClassific);
                self.selectC531(res.timeOfDayReference);
                self.uniformTime(res.unifromTime ==  null ? '0:00' :  res.unifromTime);
                self.selectC535(res.contractTimeRound);
                
            }
            
            private defaultValue(): any {
                let backup: any = {};
                backup.annualManage = 1;
                // Annual Setting
                backup.addAttendanceDay = 1;
                backup.maxManageSemiVacation = 1;
                backup.maxNumberSemiVacation = 0;
                backup.maxNumberCompany = 5;
                backup.maxGrantDay = 0;
                //backup.maxRemainingDay = '';
                backup.maxRemainingDay = 0;
                backup.numberYearRetain = 2;
                backup.permitType = 1;
                backup.annualPriority = 0;
                backup.remainingNumberDisplay = 1;
                backup.nextGrantDayDisplay = 1;
                backup.yearlyOfDays = 0;
                backup.roundProcessCla = 0;
                
                // Time Leave Setting
                backup.timeManageType = 1;
                backup.timeUnit = 0;
                backup.manageMaxDayVacation = 1;
                backup.reference = 0;
                backup.maxTimeDay = 5;
                backup.selectedEnoughTimeOneDay = 0;
                
                return backup;
            }
            
            private validate(): boolean {
                let self = this;
                self.clearError();
                if (self.enableAnnualVacation()) {
                    if (self.enableMaxNumberCompany() && (self.requiredMaxNumberCompany()
                        || (self.requiredMaxNumberCompany() == false && self.maxNumberCompany()))) {
                        $('#max-number-company').ntsEditor('validate');
                    }
                    $('#max-grant-day').ntsEditor('validate');
                    $('#max-remaining-day').ntsEditor('validate');
                    $('#number-year-retain').ntsEditor('validate');
                    if (self.enableAnnualVacation()) $('#yearLy-number-days').ntsEditor('validate');
                    if (self.enableTimeMaxNumberCompany() && (self.requiredTimeMaxNumberCompany()
                        || (!self.requiredTimeMaxNumberCompany() && self.timeMaxNumberCompany()))) {
                        $('#time-max-day-company').ntsEditor('validate');
                    }
                }
                if ($('.nts-input').ntsError('hasError')) {
                    return false;
                }
                return true;
            }
            
            private clearError(): void {
                $('#max-number-company').ntsError('clear');
                $('#max-grant-day').ntsError('clear');
                $('#max-remaining-day').ntsError('clear');
                $('#number-year-retain').ntsError('clear');
                $('#time-max-day-company').ntsError('clear');
                $('#yearLy-number-days').ntsError('clear');
                 $('#time-max-day-company').ntsError('clear');
                
            }
            
            // find enumeration ManageDistinct
            private loadManageDistinctEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findManageDistinct().done(function(res: Array<EnumertionModel>) {
                    self.manageDistinctList(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
            // find enumeration PreemptionPermit
            private loadPreemptionPermitEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findPreemptionPermit().done(function(res: Array<EnumertionModel>) {
                    self.annualPriorityList(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
            // find enumeration DisplayDivision
            private loadDisplayDivisionEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findDisplayDivision().done(function(res: Array<EnumertionModel>) {
                    self.displayDivisionList(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
            // find enumeration TimeUnit
            private loadTimeUnitEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findTimeUnit().done(function(res: Array<EnumertionModel>) {
                    self.vacationTimeUnitList(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
            // find enumeration MaxDayReference
            private loadMaxDayReferenceEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.findMaxDayReference().done(function(res: Array<EnumertionModel>) {
                    self.maxDayReferenceList(res);
                    dfd.resolve();
                }).fail(function(res) {
                   nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }            
            
            // find enumeration roundProcessClassification
            private loadRoundProcessClassificationEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.roundProcessClassification().done(function(res: Array<EnumertionModel>) {
                    self.roundProcessClassificationList(res);
                    dfd.resolve();
                }).fail(function(res) {
                   nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
             // find enumeration roundProcessClassification
            private loadRoundProcessClassificEnums(): JQueryPromise<Array<EnumertionModel>> {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.pr.view.kmf001.c.service.roundProcessClassific().done(function(res: Array<EnumertionModel>) {
                    self.roundProcessClassificList(res);
                    dfd.resolve();
                }).fail(function(res) {
                   nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }

        }
    }
}
class C531Model {
    code: number;
    name: string;
    constructor(code: number, name: string) {
        this.code = code;
        this.name = name;
      
    }
}
class C535Model {
    code: number;
    name: string;
    constructor(code: number, name: string) {
        this.code = code;
        this.name = name;
      
    }
}