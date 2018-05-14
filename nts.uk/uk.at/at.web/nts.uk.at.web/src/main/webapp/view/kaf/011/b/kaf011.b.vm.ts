module nts.uk.at.view.kaf011.b.viewmodel {

    import dialog = nts.uk.ui.dialog.info;
    import text = nts.uk.resource.getText;
    import formatDate = nts.uk.time.formatDate;
    import common = nts.uk.at.view.kaf011.shr.common;
    import service = nts.uk.at.view.kaf011.shr.service;
    import block = nts.uk.ui.block;
    import jump = nts.uk.request.jump;
    import confirm = nts.uk.ui.dialog.confirm;
    import alError = nts.uk.ui.dialog.alertError;

    export class ScreenModel extends kaf000.b.viewmodel.ScreenModel {

        screenModeNew: KnockoutObservable<boolean> = ko.observable(false);

        employeeName: KnockoutObservable<string> = ko.observable('');

        drawalReqSet: KnockoutObservable<common.DrawalReqSet> = ko.observable(new common.DrawalReqSet(null));

        recWk: KnockoutObservable<common.AppItems> = ko.observable(new common.AppItems());

        absWk: KnockoutObservable<common.AppItems> = ko.observable(new common.AppItems());

        prePostTypes = ko.observableArray([
            { code: 0, text: text('KAF011_14') },
            { code: 1, text: text('KAF011_15') }]);

        prePostSelectedCode: KnockoutObservable<number> = ko.observable(0);

        appComItems = ko.observableArray([
            { code: 0, text: text('KAF011_19') },
            { code: 1, text: text('KAF011_20') },
            { code: 2, text: text('KAF011_21') },
        ]);

        appComSelectedCode: KnockoutObservable<number> = ko.observable(0);

        appReasons = ko.observableArray([]);

        appReasonSelectedID: KnockoutObservable<string> = ko.observable('');

        reason: KnockoutObservable<string> = ko.observable('');

        showReason: KnockoutObservable<number> = ko.observable(0);

        employeeID: KnockoutObservable<string> = ko.observable('');

        version: KnockoutObservable<number> = ko.observable(0);

        displayPrePostFlg: KnockoutObservable<number> = ko.observable(0);

        appTypeSet: KnockoutObservable<common.AppTypeSet> = ko.observable(new common.AppTypeSet(null));

        genSaveCmd(): common.ISaveHolidayShipmentCommand {
            let self = this, returnCmd: common.ISaveHolidayShipmentCommand = {
                recCmd: ko.mapping.toJS(self.recWk()),
                absCmd: ko.mapping.toJS(self.absWk()),
                comType: self.appComSelectedCode(),
                usedDays: 1,
                appCmd: {
                    appReasonText: self.appReasonSelectedID(),
                    applicationReason: self.reason(),
                    prePostAtr: self.prePostSelectedCode(),
                    enteredPersonSID: self.employeeID(),
                    appVersion: self.version(),
                }
            }, selectedReason = self.appReasonSelectedID() ? _.find(self.appReasons(), { 'reasonID': self.appReasonSelectedID() }) : null;
            returnCmd.absCmd.changeWorkHoursType = returnCmd.absCmd.changeWorkHoursType ? 1 : 0;
            if (selectedReason) {
                returnCmd.appCmd.appReasonText = selectedReason.reasonTemp;
            }
            return returnCmd;

        }

        enablePrepost() {
            let self = this;
            return self.screenModeNew() && self.appTypeSet().canClassificationChange() != 0;
        }
        showAppReason(): boolean {
            let self = this;
            if (self.screenModeNew()) {
                return self.appTypeSet().displayAppReason() != 0;
            } else {

                return self.appTypeSet().displayAppReason() != 0 || self.appTypeSet().displayFixedReason() != 0;
            }

        }
        update() {
            let self = this,
                saveCmd = self.genSaveCmd();
            self.validateControl();
            if (nts.uk.ui.errors.hasError()) { return; }

            let isCheckReasonError = !self.checkReason();
            if (isCheckReasonError) {
                return;
            }

            block.invisible();
            service.update(saveCmd).done(() => {
                dialog({ messageId: 'Msg_15' }).then(function() {
                    location.reload();
                });

            }).fail((error) => {
                alError({ messageId: error.messageId, messageParams: error.parameterIds });

            }).always(() => {
                block.clear();
            });
        }

        checkReason(): boolean {
            let self = this,
                appReason = self.getReason();
            let appReasonError = !nts.uk.at.view.kaf000.shr.model.CommonProcess.checkAppReason(true, self.appTypeSet().displayFixedReason() != 0, self.appTypeSet().displayAppReason() != 0, appReason);
            if (appReasonError) {
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_115' });
                return false;
            }
            let isCheckLengthError: boolean = !nts.uk.at.view.kaf000.shr.model.CommonProcess.checklenghtReason(appReason, "#appReason");
            if (isCheckLengthError) {
                return false;
            }
            return true;

        }
        validateControl() {
            $(".kaf-011-combo-box ,.nts-input").trigger("validate");
        }
        getReason(): string {
            let appReason = '',
                self = this,
                inputReasonID = self.appReasonSelectedID(),
                inputReasonList = self.appReasons(),
                detailReason = self.reason();
            let inputReason: string = '';
            if (!nts.uk.util.isNullOrEmpty(inputReasonID)) {
                inputReason = _.find(inputReasonList, { 'reasonID': inputReasonID }).reasonTemp;
            }
            if (!nts.uk.util.isNullOrEmpty(inputReason) && !nts.uk.util.isNullOrEmpty(detailReason)) {
                appReason = inputReason + ":" + detailReason;
            } else if (!nts.uk.util.isNullOrEmpty(inputReason) && nts.uk.util.isNullOrEmpty(detailReason)) {
                appReason = inputReason;
            } else if (nts.uk.util.isNullOrEmpty(inputReason) && !nts.uk.util.isNullOrEmpty(detailReason)) {
                appReason = detailReason;
            }
            return appReason;
        }

        constructor(listAppMetadata: Array<model.ApplicationMetadata>, currentApp: model.ApplicationMetadata) {
            super(listAppMetadata, currentApp);
            let self = this;

            self.startPage(self.appID());

            self.appReasons.subscribe((appReasons) => {
                if (appReasons) {
                    let defaultReason = _.find(appReasons, { 'defaultFlg': 1 });
                    if (defaultReason) {
                        self.appReasonSelectedID(defaultReason.reasonID);
                    }
                }

            });
        }

        startPage(appID: string): JQueryPromise<any> {
            var self = this,
                dfd = $.Deferred(),
                appParam = { appID: appID };
            block.invisible();
            service.findById(appParam).done((data) => {
                self.setDataFromStart(data);

            }).fail((error) => {
                alError({ messageId: error.messageId, messageParams: error.parameterIds });
            }).always(() => {
                block.clear();
                dfd.resolve();

            });

            return dfd.promise();

        }
        setDataFromStart(data: common.IHolidayShipment) {
            let self = this;
            if (data) {
                self.drawalReqSet(new common.DrawalReqSet(data.drawalReqSet || null));
                self.employeeName(data.employeeName || null);
                self.employeeID(data.employeeID || null);
                self.version(data.application.version || 0);
                self.displayPrePostFlg(data.applicationSetting.displayPrePostFlg);
                self.appTypeSet(new common.AppTypeSet(data.appTypeSet || null));
                self.recWk().setWkTypes(data.recWkTypes || []);
                self.absWk().setWkTypes(data.absWkTypes || []);
                if (data.application) {
                    self.setDataCommon(data);
                }

                if (data.absApp && data.recApp) {
                    self.setDataBothApp(data);

                } else {
                    if (data.recApp) {
                        self.setDataApp(self.recWk(), data.recApp, 1);

                    }
                    if (data.absApp) {
                        self.setDataApp(self.absWk(), data.absApp, 2);

                    }

                }
            }
        }

        setDataCommon(data) {
            let self = this,
                app = data.application;
            self.appReasons(data.appReasonComboItems || []);
            self.prePostSelectedCode(app.prePostAtr);
            self.showReason(data.applicationSetting.appReasonDispAtr);
            if (self.appTypeSet().displayAppReason() != 0) {
                self.reason(data.application.applicationReason);
            }
        }

        setDataBothApp(data) {
            let self = this;
            self.appComSelectedCode(0);
            self.setDataApp(self.absWk(), data.absApp);
            self.setDataApp(self.recWk(), data.recApp);
        }

        removeAbs() {
            let self = __viewContext['viewModel'],
                removeCmd = self.getHolidayCmd();
            confirm({ messageId: 'Msg_18' }).ifYes(function() {
                block.invisible();
                service.removeAbs(removeCmd).done(function(data) {
                    location.reload();
                }).fail(function(res: any) {
                    alError({ messageId: res.messageId, messageParams: res.parameterIds }).then(function() {
                    });
                }).always(() => {
                    block.clear();
                });
            });
        }

        cancelAbs() {
            let self = __viewContext['viewModel'],
                cancelCmd = self.getHolidayCmd();
            confirm({ messageId: 'Msg_249' }).ifYes(function() {
                block.invisible();
                service.cancelAbs(cancelCmd).done(function(data) {
                    location.reload();
                }).fail(function(res: any) {
                    alError({ messageId: res.messageId, messageParams: res.parameterIds }).then(function() {
                    });
                }).always(() => {
                    block.clear();
                });
            });
        }


        getHolidayCmd() {
            let self = this,
                shipmentCmd;

            shipmentCmd = {
                absAppID: self.absWk().appID(),
                recAppID: null,
                appVersion: self.version(),
                memo: ""
            }

            return shipmentCmd;

        }




        setDataApp(control: common.AppItems, data, comType?) {
            let self = this;
            if (data) {
                control.wkTypeCD(data.workTypeCD);
                control.wkTimeCD(data.workTimeCD);
                control.changeWorkHoursType(data.changeWorkHoursType);
                control.appDate(data.appDate);
                control.appID(data.appID);
                control.wkTimeName(data.workTimeName);
                if (data.wkTime1) {
                    control.wkTime1().startTime(data.wkTime1.startTime);
                    control.wkTime1().endTime(data.wkTime1.endTime);
                    control.wkTime1().startType(data.wkTime1.startUseAtr);
                    control.wkTime1().endType(data.wkTime1.endUseAtr);

                }

                if (data.timeZoneUseDtos && data.timeZoneUseDtos.length) {
                    let timeZone1 = data.timeZoneUseDtos[0];
                    control.wkTime1().startTimeDisplay(timeZone1.startTime);
                    control.wkTime1().endTimeDisplay(timeZone1.endTime);
                }
                if (comType) {
                    self.appComSelectedCode(comType);
                }
                control.updateWorkingText();
            }
        }

    }


}