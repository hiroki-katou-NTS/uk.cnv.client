/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kdr001.b.viewmodel {
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import errors = nts.uk.ui.errors;
    import block = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    import alertError = nts.uk.ui.dialog.alertError;

    export class ScreenModel {
        lstHolidays: KnockoutObservableArray<HolidayRemaining> = ko.observableArray([]);
        currentCode: KnockoutObservable<string>;
        layoutId: KnockoutObservable<string>;
        currentHoliday: KnockoutObservable<HolidayRemaining> = ko.observable(new HolidayRemaining(null));
        isNewMode: KnockoutObservable<boolean> = ko.observable(false);
        allSpecialHolidays: KnockoutObservableArray<SpecialHoliday> = ko.observableArray([]);
        listSpecialHoliday: KnockoutObservableArray<any> = ko.observableArray([]);
        listSpecialHolidayEnable: KnockoutObservableArray<any> = ko.observableArray([]);
        vacationControl: IVariousVacationControl;

        constructor() {
            let self = this;
            let params = getShared("KDR001Params");
            self.currentCode = ko.observable(params || '');
            self.layoutId = ko.observable(params.layOutId || '');
            self.isNewMode(false);
            self.layoutId.subscribe((code) => {
                if (code) {
                    block.invisible();
                    service.findByLayOutId(code).done(function (data: HolidayRemaining) {
                        if (data) {
                            let item = new HolidayRemaining(data);
                            self.currentHoliday(item);
                            self.setData();
                            self.setFocus();
                            self.setSpecialHolidayStyle();
                        }
                        _.defer(() => {
                            errors.clearAll()
                        });
                    }).fail(function (error) {
                        alertError(error);
                    }).always(() => {
                        block.clear();
                    });
                }
            });
        }

        /**
         * 開始
         **/
        start(): JQueryPromise<any> {
            let params = getShared("KDR001Params");
            let self = this,
                dfd = $.Deferred();
            block.invisible();
            let settingId = params.settingId;
            let layOutId = params.layOutId;
            let listSpecialHoliday: Array<number> = [];
            $.when(service.getVariousVacationControl(),
                service.findBySettingId(settingId)
            ).done((vacationControl: IVariousVacationControl,
                    holidayRemainings: Array<HolidayRemaining>,) => {
                self.vacationControl = vacationControl;
                if (!vacationControl || vacationControl.annualHolidaySetting == false) {
                    $('#rowYearlyHoliday').addClass("hidden");
                    $('#rowInsideHalfDay').addClass("hidden");
                    $('#rowInsideHours').addClass("hidden");
                }
                if (!vacationControl || vacationControl.halfDayYearlySetting == false) {
                    $('#rowInsideHalfDay').addClass("hidden");
                }
                if (!vacationControl || vacationControl.hourlyLeaveSetting == false) {
                    $('#rowInsideHours').addClass("hidden");
                }
                if (!vacationControl || vacationControl.yearlyReservedSetting == false) {
                    $('#rowYearlyReserved').addClass("hidden");
                }

                if (!vacationControl || vacationControl.substituteHolidaySetting == false) {
                    $('#rowSubstituteHoliday').addClass("hidden");

                }

                if (!vacationControl || vacationControl.pauseItemHolidaySettingCompany == false) {
                    $('#rowPauseItemHoliday').addClass("hidden");
                }

                if (!vacationControl || vacationControl.childNursingSetting == false) {
                    $('#rowChildNursingHoliday').addClass("hidden");
                }

                if (!vacationControl || vacationControl.nursingCareSetting == false) {
                    $('#rowNursingCareHoliday').addClass("hidden");
                }
                // if (!vacationControl || vacationControl.publicHolidaySetting == false) {
                //     $('#publicHolidaySetting').addClass("hidden");
                // }
                if (!vacationControl || vacationControl.com60HourVacationSetting == false) {
                    $('#rowHD60HItemCheck').addClass("hidden");
                }
                if (!vacationControl || vacationControl.listSpecialHoliday.length == 0) {
                    $('#rowSpecialHoliday').addClass("hidden");
                }
                else {
                    for (let i = 1; i < 21; i++) {
                        let item = _.find(vacationControl.listSpecialHoliday, x => {
                            return x.specialHolidayCode == i;
                        });
                        if (item) {
                            self.allSpecialHolidays.push(new SpecialHoliday({
                                specialHolidayCode: i,
                                specialHolidayName: item.specialHolidayName,
                                enable: true
                            }));
                            self.listSpecialHolidayEnable.push(i);
                            listSpecialHoliday.push(i);
                        }
                    }
                    if(holidayRemainings.length != 0){
                        self.currentHoliday().listSpecialHoliday(listSpecialHoliday);
                    }
                }
                if (holidayRemainings && holidayRemainings.length) {
                    holidayRemainings = _.sortBy(holidayRemainings, ['cd']);

                    let _rsList: Array<HolidayRemaining> = _.map(holidayRemainings, result => {
                        return new HolidayRemaining(result);
                    });
                    self.lstHolidays(_rsList);
                    if (!nts.uk.util.isNullOrEmpty(layOutId)) {
                        let item = _.find(_rsList, function (o) {
                            return o.layoutId == layOutId
                        });
                        if (!nts.uk.util.isNullOrUndefined(item)) {
                            self.currentHoliday(item);
                            let listSpecial: Array<number> = [];
                        let listCheck =  self.currentHoliday().listSpecialHoliday();
                          for (let i = 0;i<listSpecialHoliday.length;i++){
                              let item = listSpecialHoliday[i];
                              let sub =_.first(_.filter(listCheck,x=>x==item));
                              if(!nts.uk.util.isNullOrEmpty(sub)){
                                  listSpecial.push(sub);
                              }
                          }
                            self.currentHoliday().listSpecialHoliday(listSpecial);
                        }
                    }
                    else {
                        self.currentCode.valueHasMutated();
                    }
                }
                if(holidayRemainings.length == 0){
                    self.settingCreateMode();
                }
                dfd.resolve(self);
            }).fail(function (res) {
                alertError({messageId: res.messageId});
                dfd.reject();
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }
        setData() {
            let self = this;
            let vacationControl = self.vacationControl;
            if (!vacationControl || vacationControl.annualHolidaySetting == false) {
                self.currentHoliday().yearlyHoliday(false);
                self.currentHoliday().insideHalfDay(false);
                self.currentHoliday().insideHours(false);
            }
            if (!vacationControl || vacationControl.yearlyReservedSetting == false) {
                self.currentHoliday().yearlyReserved(false);
            }
            if (!vacationControl || vacationControl.halfDayYearlySetting == false) {
                self.currentHoliday().insideHalfDay(false);
            }
            if (!vacationControl || vacationControl.hourlyLeaveSetting == false) {
                self.currentHoliday().insideHours(false);
            }
            if (!vacationControl || vacationControl.substituteHolidaySetting == false) {
                self.currentHoliday().outputItemSubstitute(false);
                self.currentHoliday().representSubstitute(false);
                self.currentHoliday().remainingChargeSubstitute(false);
            }

            if (!vacationControl || vacationControl.pauseItemHolidaySettingCompany == false) {
                self.currentHoliday().pauseItem(false);
                self.currentHoliday().unDigestedPause(false);
                self.currentHoliday().numberRemainingPause(false);
            }

            if (!vacationControl || vacationControl.com60HourVacationSetting == false) {
                self.currentHoliday().hd60HItem(false);
                self.currentHoliday().hd60HUndigested(false);
                self.currentHoliday().hd60HRemain(false);
            }
            if (!vacationControl || vacationControl.childNursingSetting == false) {
                self.currentHoliday().childNursingLeave(false);
            }
            if (!vacationControl || vacationControl.nursingCareSetting == false) {
                self.currentHoliday().nursingLeave(false);
            }
            // if (!vacationControl || vacationControl.publicHolidaySetting == false) {
            //     self.currentHoliday().outputItemsHolidays(false);
            //     self.currentHoliday().outputHolidayForward(false);
            //     self.currentHoliday().monthlyPublic(false);
            // }
            if (!vacationControl || vacationControl.listSpecialHoliday.length == 0) {
                self.currentHoliday().listSpecialHoliday([]);
            }
            else {
                let listSpecialHoliday: Array<number> = [];
                _.forEach(self.currentHoliday().listSpecialHoliday(), function (item) {
                    if (_.find(vacationControl.listSpecialHoliday, x => {
                            return x.specialHolidayCode == item;
                        }))
                        listSpecialHoliday.push(item);
                });
                self.currentHoliday().listSpecialHoliday(listSpecialHoliday);
            }
            self.isNewMode(false)
        }
        getAllData(code?: string): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            block.invisible();
            self.lstHolidays.removeAll();
            let params = getShared("KDR001Params");
            let settingId = params.settingId;
            let layOutId = params.layOutId;
            service.findBySettingId(settingId).done(function (data: Array<HolidayRemaining>) {
                if (data && data.length) {
                    data = _.sortBy(data, ['cd']);
                    let _rsList: Array<HolidayRemaining> = _.map(data, result => {
                        return new HolidayRemaining(result);
                    });
                    if (code) {
                        if (code == self.currentCode())
                            self.currentCode.valueHasMutated();
                        else
                            self.currentCode(code);

                        let sub = _.find(_rsList, x => x.cd() == code);
                        if (!nts.uk.util.isNullOrUndefined(sub)) {
                            self.layoutId(sub.layoutId);
                            self.currentHoliday(sub)
                        }
                    }
                    else {
                        self.currentCode(_rsList[0].cd());
                    }
                    self.lstHolidays(_rsList);
                }
                else {
                    nts.uk.ui.errors.clearAll();
                    self.currentCode('');
                    self.currentHoliday(new HolidayRemaining(null));
                    self.isNewMode(true);
                }
                block.clear();
                dfd.resolve(self);
            }).fail(function (res) {
                alertError({messageId: res.messageId});
                block.clear();
                dfd.reject();
            });
            return dfd.promise();
        }
        /**
         * create a new Holiday Remaining
         *
         */
        settingCreateMode() {
            let self = this;
            let vacationControl = self.vacationControl;
            // clear selected holiday set
            self.currentCode('');
            // clear holiday setting
            self.currentHoliday(new HolidayRemaining(null));
            self.layoutId(null);
            self.allSpecialHolidays([]);
            let check = vacationControl && vacationControl.listSpecialHoliday.length > 0;
            // Set new mode
            self.isNewMode(true);
            //$('#rowSpecialHoliday').addClass("hidden");
            if (check) {
                for (let i = 1; i < 21; i++) {
                    let item = _.find(vacationControl.listSpecialHoliday, x => {
                        return x.specialHolidayCode == i;
                    });
                    if (item) {
                        self.allSpecialHolidays.push(new SpecialHoliday({
                            specialHolidayCode: i,
                            specialHolidayName: item.specialHolidayName,
                            enable: true
                        }));
                        self.listSpecialHolidayEnable.push(i);
                    }
                }
            }
            self.currentHoliday().listSpecialHoliday([]);
            //focus
            self.setFocus();
            self.setSpecialHolidayStyle();
            $('.nts-input').ntsError('clear');
            nts.uk.ui.errors.clearAll();
            nts.uk.ui.block.clear();
        }
        setFocus() {
            let self = this;
            if (self.isNewMode()) {
                $('#holidayCode').focus();
            } else {
                $('#holidayName').focus();
            }
        }
        setSpecialHolidayStyle() {
            $("#rowSpecialHoliday > td > div >label > span").addClass("limited-label");
        }
        /**
         * Save
         */
        saveHoliday() {
            let self = this,
                currentHoliday: HolidayRemaining = self.currentHoliday();
            let params = getShared("KDR001Params");
            currentHoliday.itemSelType = params.settingId;
            let vacationControl = self.vacationControl;
            $('.nts-input').trigger("validate");
            if (errors.hasError() === false) {
                block.invisible();
                if (!vacationControl || vacationControl.annualHolidaySetting == false) {
                    self.currentHoliday().yearlyHoliday(false);
                    self.currentHoliday().insideHalfDay(false);
                    self.currentHoliday().insideHours(false);
                }
                if (!vacationControl || vacationControl.yearlyReservedSetting == false) {
                    self.currentHoliday().yearlyReserved(false);
                }
                if (!vacationControl || vacationControl.halfDayYearlySetting == false) {
                    self.currentHoliday().insideHalfDay(false);
                }
                if (!vacationControl || vacationControl.hourlyLeaveSetting == false) {
                    self.currentHoliday().insideHours(false);
                }
                if (!vacationControl || vacationControl.substituteHolidaySetting == false) {
                    self.currentHoliday().outputItemSubstitute(false);
                    self.currentHoliday().representSubstitute(false);
                    self.currentHoliday().remainingChargeSubstitute(false);
                }
                if (!vacationControl || vacationControl.pauseItemHolidaySettingCompany == false) {
                    self.currentHoliday().pauseItem(false);
                    self.currentHoliday().unDigestedPause(false);
                    self.currentHoliday().numberRemainingPause(false);
                }
                if (!vacationControl || vacationControl.com60HourVacationSetting == false) {
                    self.currentHoliday().hd60HItem(false);
                    self.currentHoliday().hd60HUndigested(false);
                    self.currentHoliday().hd60HRemain(false);
                }
                if (!vacationControl || vacationControl.childNursingSetting == false) {
                    self.currentHoliday().childNursingLeave(false);
                }
                if (!vacationControl || vacationControl.nursingCareSetting == false) {
                    self.currentHoliday().nursingLeave(false);
                }


                // if (!vacationControl || vacationControl.publicHolidaySetting == false) {
                //     self.currentHoliday().outputItemsHolidays(false);
                //     self.currentHoliday().outputHolidayForward(false);
                //     self.currentHoliday().monthlyPublic(false);
                // }
                if (self.isNewMode()) {

                    // create new holiday
                    service.addHolidayRemaining(ko.toJS(currentHoliday)).done(() => {
                        self.getAllData(currentHoliday.cd()).done(() => {
                            dialog.info({messageId: "Msg_15"}).then(() => {
                                self.setFocus();
                                self.isNewMode(false);
                            });
                        });
                    }).fail(function (error) {
                        if (error.messageId == 'Msg_880') {
                            $('#outputItems').ntsError('set', error);
                            $('#outputItems').focus();
                            $('#residence-code').ntsError('set', {messageId: error.messageId});

                        } else if (error.messageId == 'Msg_3') {
                            $('#holidayCode').ntsError('set', error);
                            $('#holidayName').focus();
                            $('#residence-code').ntsError('set', {messageId: error.messageId});
                        }
                    }).always(function () {
                        self.setFocus();
                        block.clear();
                    });
                } else {
                    // update
                    service.updateHolidayRemaining(ko.toJS(currentHoliday)).done(() => {
                        self.getAllData(currentHoliday.cd()).done(() => {
                            dialog.info({messageId: "Msg_15"}).then(() => {
                                self.setFocus();
                                self.isNewMode(false);
                            });
                        });
                    }).fail(function (error) {
                        $('#outputItems').ntsError('set', error);
                        $('#outputItems').focus();
                        $('#residence-code').ntsError('set', {messageId: error.messageId});
                    }).always(function () {
                        self.setFocus();
                        block.clear();
                    });
                }
            }
        }
        /**
         * delete the Holoday Remaining
         */
        deleteHoliday() {
            let self = this,
                lstHolidays = self.lstHolidays,
                currentHoliday: HolidayRemaining = self.currentHoliday();
            block.invisible();
            dialog.confirm({messageId: "Msg_18"}).ifYes(() => {
                if (currentHoliday.cd()) {
                    let index: number = _.findIndex(lstHolidays(), function (x) {
                        return x.cd() == currentHoliday.cd()
                    });
                    service.removeHolidayRemaining(new HdDeleteRemainManageCommand(currentHoliday.layoutId)).done(function () {
                        self.getAllData(self.currentCode()).done(() => {
                            self.setSpecialHolidayStyle();
                            dialog.info({messageId: "Msg_16"}).then(() => {
                                if (self.lstHolidays().length == 0) {
                                    self.currentCode('');
                                    self.isNewMode(true);
                                    self.setFocus();
                                    nts.uk.ui.errors.clearAll();
                                } else {
                                    let sub: HolidayRemaining;
                                    if (index == self.lstHolidays().length) {
                                        let code = self.lstHolidays()[index - 1].cd();
                                        sub = self.lstHolidays()[index - 1];
                                        self.currentCode(code);

                                    } else {
                                        let code = self.lstHolidays()[index].cd();
                                        sub = self.lstHolidays()[index];
                                        self.currentCode(code);
                                    }
                                    self.layoutId(sub.layoutId)
                                    self.currentHoliday(sub);
                                    self.setData();
                                    self.isNewMode(false);
                                    self.setFocus();
                                }
                            });
                        });

                    }).fail(function (error) {
                        dialog.alertError({messageId: error.messageId});
                        block.clear();
                    }).always(function () {
                        block.clear();
                    });
                } else {
                    block.clear();
                }
            }).then(() => {
                $('.nts-input').ntsError('clear');
                nts.uk.ui.errors.clearAll();
                block.clear();
            });
            ;
        }
        /**
         * close the dialog
         */
        closeDialog() {
            let self = this;
            setShared('KDR001B2A_cd', self.layoutId());
            nts.uk.ui.windows.close()
        }
    }
    export class HolidayRemaining {

        /**
         * 会社ID
         */
        cid: KnockoutObservable<string>;
        /**
         * コード
         */
        cd: KnockoutObservable<string>;
        /**
         * 名称
         */
        name: KnockoutObservable<string>;

        displayCd: string;

        displayName: string;

        layoutId: string;

        itemSelType: number;
        /**
         * 介護休暇の項目を出力する
         */
        nursingLeave: KnockoutObservable<boolean>;
        /**
         * 代休残数を出力する
         */
        remainingChargeSubstitute: KnockoutObservable<boolean>;

        /**
         * 代休未消化出力する
         */
        representSubstitute: KnockoutObservable<boolean>;
        /**
         * 代休の項目を出力する
         */
        outputItemSubstitute: KnockoutObservable<boolean>;

        /**
         * 公休繰越数を出力する
         */
        outputHolidayForward: KnockoutObservable<boolean>;
        /**
         * 公休月度残を出力する
         */
        monthlyPublic: KnockoutObservable<boolean>;
        /**
         * 公休の項目を出力する
         */
        outputItemsHolidays: KnockoutObservable<boolean>;

        /**
         * childNursingLeave
         */
        childNursingLeave: KnockoutObservable<boolean>;

        /**
         * 年休の項目出力する
         */
        yearlyHoliday: KnockoutObservable<boolean>;

        /**
         * 内時間年休残数を出力する
         */
        insideHours: KnockoutObservable<boolean>;

        /**
         * ★内半日年休を出力する
         */
        insideHalfDay: KnockoutObservable<boolean>;

        /**
         * 振休残数を出力する
         */
        numberRemainingPause: KnockoutObservable<boolean>;
        /**
         * 振休未消化を出力する
         */
        unDigestedPause: KnockoutObservable<boolean>;
        /**
         * 振休の項目を出力する
         */
        pauseItem: KnockoutObservable<boolean>;
        /**
         * 時間外超過項目を出力する
         */
        hd60HItem: KnockoutObservable<boolean>;
        /**
         * 時間外超過項目を出力する
         */
        hd60HUndigested: KnockoutObservable<boolean>;
        /**
         * 時間外超過項目を出力する
         */
        hd60HRemain: KnockoutObservable<boolean>;

        /**
         * 積立年休の項目を出力する
         */
        yearlyReserved: KnockoutObservable<boolean>;

        listSpecialHoliday: KnockoutObservableArray<number>;

        constructor(param: any) {
            let self = this;
            self.cid = ko.observable(param ? param.cid || '' : '');
            self.cd = ko.observable(param ? param.cd || '' : '');
            self.name = ko.observable(param ? param.name || '' : '');
            self.displayCd = param ? param.cd || '' : '';
            self.itemSelType = param ? param.itemSelType : 0;
            self.displayName = param ? param.name || '' : '';
            self.layoutId = param ? param.layoutId || '' : '';
            self.nursingLeave = ko.observable(param ? param.nursingLeave || false : false);
            self.remainingChargeSubstitute = ko.observable(param ? param.remainingChargeSubstitute || false : false);
            self.representSubstitute = ko.observable(param ? param.representSubstitute || false : false);
            self.outputItemSubstitute = ko.observable(param ? param.outputItemSubstitute || false : false);
            //self.outputHolidayForward = ko.observable(param ? param.outputHolidayForward || false : false);
            self.outputHolidayForward = ko.observable(false);
            //self.monthlyPublic = ko.observable(param ? param.monthlyPublic || false : false);
            self.monthlyPublic = ko.observable(false);
            //self.outputItemsHolidays = ko.observable(param ? param.outputItemsHolidays || false : false);
            self.outputItemsHolidays = ko.observable(false);
            self.childNursingLeave = ko.observable(param ? param.childNursingLeave || false : false);
            self.yearlyHoliday = ko.observable(param ? param.yearlyHoliday || false : false);
            //self.insideHours = ko.observable(param ? param.insideHours || false : false);
            self.insideHours = ko.observable(false);
            //self.insideHalfDay = ko.observable(param ? param.insideHalfDay || false : false);
            self.insideHalfDay = ko.observable(false);
            self.numberRemainingPause = ko.observable(param ? param.numberRemainingPause || false : false);
            self.unDigestedPause = ko.observable(param ? param.unDigestedPause || false : false);
            self.pauseItem = ko.observable(param ? param.pauseItem || false : false);
            self.hd60HItem = ko.observable(param ? param.hd60HItem || false : false);
            self.hd60HRemain = ko.observable(param ? param.hd60HRemain || false : false);
            self.hd60HUndigested = ko.observable(param ? param.hd60HUndigested || false : false);
            self.yearlyReserved = ko.observable(param ? param.yearlyReserved || false : false);
            self.listSpecialHoliday = ko.observableArray(param ? param.listSpecialHoliday || [] : []);

            self.outputItemSubstitute.subscribe((isCheck) => {
                if (isCheck === false) {
                    self.representSubstitute(false);
                    self.remainingChargeSubstitute(false);
                }
                nts.uk.ui.errors.clearAll();
            });

            self.pauseItem.subscribe((isCheck) => {
                if (isCheck === false) {
                    self.unDigestedPause(false);
                    self.numberRemainingPause(false);
                }
                nts.uk.ui.errors.clearAll();
            });
            self.yearlyHoliday.subscribe((isCheck) => {
                if (isCheck === false) {
                    self.insideHours(false);
                    self.insideHalfDay(false);
                }
                nts.uk.ui.errors.clearAll();
            });

            self.hd60HItem.subscribe((isCheck) => {
                if (isCheck === false) {
                    self.hd60HUndigested(false);
                    self.hd60HRemain(false);
                }
                nts.uk.ui.errors.clearAll();
            });

            self.outputItemsHolidays.subscribe((isCheck) => {
                if (isCheck === false) {
                    self.outputHolidayForward(false);
                    self.monthlyPublic(false);
                }
                nts.uk.ui.errors.clearAll();
            });
            self.nursingLeave.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });
            self.childNursingLeave.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });
            self.insideHours.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });
            self.insideHalfDay.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });
            self.yearlyReserved.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });
            self.listSpecialHoliday.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });

        }

    }

    export class SpecialHoliday {

        /*特別休暇コード*/
        specialHolidayCode: number;

        /*特別休暇名称*/
        specialHolidayName: string;

        enable: boolean;

        constructor(param: any) {
            let self = this;
            self.specialHolidayCode = param ? param.specialHolidayCode || null : null;
            self.specialHolidayName = param ? param.specialHolidayName || '' : '';
            self.enable = param ? param.enable || false : false;
        }
    }

    export interface ISpecialHoliday {

        /*特別休暇コード*/
        specialHolidayCode: number;

        /*特別休暇名称*/
        specialHolidayName: string;
    }

    export interface IVariousVacationControl {

        annualHolidaySetting: boolean;
        yearlyReservedSetting: boolean;
        substituteHolidaySetting: boolean;
        pauseItemHolidaySetting: boolean;
        childNursingSetting: boolean;
        nursingCareSetting: boolean;
        com60HourVacationSetting: boolean;
        publicHolidaySetting: boolean;
        halfDayYearlySetting:boolean;
        hourlyLeaveSetting:boolean;
        pauseItemHolidaySettingCompany:boolean;
        listSpecialHoliday: Array<ISpecialHoliday>;
    }

    export class HdDeleteRemainManageCommand {
        layOutId: string;

        constructor(layOutId: string) {
            this.layOutId = layOutId;

        }
    }
}