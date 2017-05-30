module nts.uk.pr.view.kmf001.b {
    export module viewmodel {

        import EnumertionModel = service.model.EnumerationModel;

        export class ScreenModel {

            priority: KnockoutObservableArray<EnumertionModel>;
            selectedPriority: KnockoutObservable<number>;
            enableInputPriority: KnockoutObservable<boolean>;

            annualPaidLeave: KnockoutObservable<number>;
            compensatoryDayOff: KnockoutObservable<number>;
            substituteHoliday: KnockoutObservable<number>;
            fundedPaidHoliday: KnockoutObservable<number>;
            exsessHoliday: KnockoutObservable<number>;
            specialHoliday: KnockoutObservable<number>;

            enableHelpButton: KnockoutObservable<boolean>;

            constructor() {

                let self = this;
                self.priority = ko.observableArray([
                    { value: 0, name: "設定する" },
                    { value: 1, name: "設定しない" }
                ]);

                self.selectedPriority = ko.observable(0);
                self.enableInputPriority = ko.computed(function() {
                    return self.selectedPriority() == 0;
                }, self);

                self.annualPaidLeave = ko.observable(null);
                self.compensatoryDayOff = ko.observable(null);
                self.substituteHoliday = ko.observable(null);
                self.fundedPaidHoliday = ko.observable(null);
                self.exsessHoliday = ko.observable(null);
                self.specialHoliday = ko.observable(null);

                self.enableHelpButton = ko.observable(true);
            }
            
            public startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred<void>();
                $.when(self.loadAcquisitionRule()).done(function(res) {
                    if (res) {
                        self.initUI(res);
                    }
                    dfd.resolve();
                });
                return dfd.promise();
            }
            //CLOSE DIALOG
            public closeDialog(): void {
                nts.uk.ui.windows.close();
            }

            //LOAD ACQUISITION RULE WHEN START DIALOG
            private loadAcquisitionRule(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                //  find data on db
                service.findAcquisitionRule().done(function(res: any) {
                    self.initUI(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alert(res.message);
                });
                return dfd.promise();
            }

            //get data to dialog
            private initUI(res: any): any {
                let self = this;
                //if find data exist
                if (res) {
                    //if use Priority
                    if (res.category == "Setting") {
                        self.selectedPriority(0);
                    } else {
                        //if not use Priority
                        self.selectedPriority(1);
                    }
                    //set list priority
                    res.vaAcOrders.forEach(item => {
                        if (item.vacationType == 1) {
                            self.annualPaidLeave(item.priority);
                        }
                        if (item.vacationType == 2) {
                            self.compensatoryDayOff(item.priority);
                        }
                        if (item.vacationType == 3) {
                            self.substituteHoliday(item.priority);
                        }
                        if (item.vacationType == 4) {
                            self.fundedPaidHoliday(item.priority);
                        }
                        if (item.vacationType == 5) {
                            self.exsessHoliday(item.priority);
                        }
                        if (item.vacationType == 6) {
                            self.specialHoliday(item.priority);
                        }
                    });
                } else {
                    //if find data null
                    //Selected default button is "No Setting"
                    self.selectedPriority(1);

                    //List priority default all value = 1.
                    self.annualPaidLeave(1);
                    self.compensatoryDayOff(1);
                    self.substituteHoliday(1);
                    self.fundedPaidHoliday(1);
                    self.exsessHoliday(1);
                    self.specialHoliday(1);
                }

                //when change button Select
                self.selectedPriority.subscribe(function(value) {
                    //if click button "No Setting"
                    if (value == 1) {
                        nts.uk.ui.errors.clearAll();
                    }
                });
            }

            //CLICK SAVE TO DB
            public saveToDb(): void {
                let self = this;
                let dfd = $.Deferred();
                //validate input priority
                if (self.validate()) {
                    return;
                } else {
                    let command = self.setList();
                    service.updateAcquisitionRule(command).done(function() {
                        self.loadAcquisitionRule().done(function(res) {
                            // Msg_15
                            nts.uk.ui.dialog.alert("登録しました。").then(function() {
                                self.closeDialog();
                            });
                            dfd.resolve();
                        });
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alert(res.message);
                    });

                }
            }
            //GET DATA
            private setList(): void {
                let self = this;
                let command: any = {};
                if (self.selectedPriority() == 0) {
                    command.category = "Setting";
                } else {
                    command.category = "NoSetting";
                }
                //Create Acquisition List
                let acquisitionOrderList = new Array();

                //Set data Acquisition Annua lPaid Leave
                let acquisitionAnnualPaidLeave: any = {};
                acquisitionAnnualPaidLeave.vacationType = "AnnualPaidLeave";
                acquisitionAnnualPaidLeave.priority = +self.annualPaidLeave();
                acquisitionOrderList.push(acquisitionAnnualPaidLeave);

                //Set data Acquisition Compensatory Day Off
                let acquisitionCompensatoryDayOff: any = {};
                acquisitionCompensatoryDayOff.vacationType = "CompensatoryDayOff";
                acquisitionCompensatoryDayOff.priority = +self.compensatoryDayOff();
                acquisitionOrderList.push(acquisitionCompensatoryDayOff);

                //Set data Acquisition Substitute Holiday
                let acquisitionSubstituteHoliday: any = {};
                acquisitionSubstituteHoliday.vacationType = "SubstituteHoliday";
                acquisitionSubstituteHoliday.priority = +self.substituteHoliday();
                acquisitionOrderList.push(acquisitionSubstituteHoliday);
    
                //Set data Acquisition Funded Paid Holiday
                let acquisitionFundedPaidHoliday: any = {};
                acquisitionFundedPaidHoliday.vacationType = "FundedPaidHoliday";
                acquisitionFundedPaidHoliday.priority = +self.fundedPaidHoliday();
                acquisitionOrderList.push(acquisitionFundedPaidHoliday);

                //Set data Acquisition Exsess Holiday
                let acquisitionExsessHoliday: any = {};
                acquisitionExsessHoliday.vacationType = "ExsessHoliday";
                acquisitionExsessHoliday.priority = +self.exsessHoliday();
                acquisitionOrderList.push(acquisitionExsessHoliday);

                //Set data Acquisition Special Holiday
                let acquisitionSpecialHoliday: any = {};
                acquisitionSpecialHoliday.vacationType = "SpecialHoliday";
                acquisitionSpecialHoliday.priority = +self.specialHoliday();
                acquisitionOrderList.push(acquisitionSpecialHoliday);

                command.vaAcRule = acquisitionOrderList;
                return command;
            }

            private validate(): boolean {
                let self = this;
                $('#annual-paid-leave').ntsEditor('validate');
                $('#compensatory-day-off').ntsEditor('validate');
                $('#substitute-holiday').ntsEditor('validate');
                $('#funded-paid-holiday').ntsEditor('validate');
                $('#exsess-holiday').ntsEditor('validate');
                $('#special-holiday').ntsEditor('validate');
                return $('.nts-input').ntsError('hasError');
            }
            private clearError(): void {
                if (!$('.nts-input').ntsError('hasError')) {
                    return;
                }
                $('#annual-paid-leave').ntsError('clear');
                $('#compensatory-day-off').ntsError('clear');
                $('#substitute-holiday').ntsError('clear');
                $('#funded-paid-holiday').ntsError('clear');
                $('#exsess-holiday').ntsError('clear');
                $('#special-holiday').ntsError('clear');
            }

        }
        export class AcquisitionOrder {
            vacationType: number;
            priority: number;
        }
    }
}