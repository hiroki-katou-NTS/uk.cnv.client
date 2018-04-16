module nts.uk.at.view.kaf006.a.viewmodel {
    import common = nts.uk.at.view.kaf006.share.common;
    import service = nts.uk.at.view.kaf006.shr.service;
    import dialog = nts.uk.ui.dialog;
    import appcommon = nts.uk.at.view.kaf000.shr.model;
    export class ScreenModel {
        DATE_FORMAT: string = "YYYY/MM/DD";
        //kaf000
        kaf000_a: kaf000.a.viewmodel.ScreenModel;
        manualSendMailAtr: KnockoutObservable<boolean> = ko.observable(true);
        mailFlag: KnockoutObservable<boolean> = ko.observable(true);
        screenModeNew: KnockoutObservable<boolean> = ko.observable(true);
        displayEndDateFlg: KnockoutObservable<boolean> = ko.observable(false);
        enableDisplayEndDate: KnockoutObservable<boolean> = ko.observable(true);
        //current Data
        //        curentGoBackDirect: KnockoutObservable<common.GoBackDirectData>;
        //申請者
        employeeName: KnockoutObservable<string> = ko.observable("");
        //Pre-POST
        prePostSelected: KnockoutObservable<number> = ko.observable(3);
        workState: KnockoutObservable<boolean> = ko.observable(true);
        typeSiftVisible: KnockoutObservable<boolean> = ko.observable(true);
        // 申請日付
        startAppDate: KnockoutObservable<string> = ko.observable('');
        // 申請日付
        endAppDate: KnockoutObservable<string> = ko.observable('');
        dateValue: KnockoutObservable<any> = ko.observable({ startDate: '', endDate: '' });
        appDate: KnockoutObservable<string> = ko.observable('');
        selectedAllDayHalfDayValue: KnockoutObservable<number> = ko.observable(0);
        holidayTypes: KnockoutObservableArray<common.HolidayType> = ko.observableArray([]);
        holidayTypeCode: KnockoutObservable<number> = ko.observable(0);
        typeOfDutys: KnockoutObservableArray<common.TypeOfDuty> = ko.observableArray([]);
        selectedTypeOfDuty: KnockoutObservable<number> = ko.observable(null);
        displayHalfDayValue: KnockoutObservable<boolean> = ko.observable(false);
        changeWorkHourValue: KnockoutObservable<boolean> = ko.observable(false);
        changeWorkHourValueFlg: KnockoutObservable<boolean> = ko.observable(false);
        //        displayChangeWorkHour:  KnockoutObservable<boolean> = ko.observable(false);
        displayStartFlg: KnockoutObservable<boolean> = ko.observable(false);
        contentFlg: KnockoutObservable<boolean> = ko.observable(true);
        eblTimeStart1: KnockoutObservable<boolean> = ko.observable(false);
        eblTimeEnd1: KnockoutObservable<boolean> = ko.observable(false);
        workTimeCodes: KnockoutObservableArray<string> = ko.observableArray([]);
        workTypecodes: KnockoutObservableArray<string> = ko.observableArray([]);
        displayWorkTimeName: KnockoutObservable<string> = ko.observable(null);
        //TIME LINE 1
        timeStart1: KnockoutObservable<number> = ko.observable(null);
        timeEnd1: KnockoutObservable<number> = ko.observable(null);
        //TIME LINE 2
        timeStart2: KnockoutObservable<number> = ko.observable(null);
        timeEnd2: KnockoutObservable<number> = ko.observable(null);
        //勤務種類
        workTimeCode: KnockoutObservable<string> = ko.observable('');
        workTimeName: KnockoutObservable<string> = ko.observable('');
        //comboBox 定型理由
        reasonCombo: KnockoutObservableArray<common.ComboReason> = ko.observableArray([]);
        selectedReason: KnockoutObservable<string> = ko.observable('');
        //MultilineEditor
        requiredReason: KnockoutObservable<boolean> = ko.observable(false);
        multilContent: KnockoutObservable<string> = ko.observable('');

        //Approval 
        approvalSource: Array<common.AppApprovalPhase> = [];
        employeeID: KnockoutObservable<string> = ko.observable('');
        //menu-bar 
        enableSendMail: KnockoutObservable<boolean> = ko.observable(true);
        prePostDisp: KnockoutObservable<boolean> = ko.observable(true);
        prePostEnable: KnockoutObservable<boolean> = ko.observable(true);
        useMulti: KnockoutObservable<boolean> = ko.observable(true);

        displayPrePostFlg: KnockoutObservable<boolean> = ko.observable(true);

        typicalReasonDisplayFlg: KnockoutObservable<boolean> = ko.observable(true);
        displayAppReasonContentFlg: KnockoutObservable<boolean> = ko.observable(true);
        // enable
        enbAllDayHalfDayFlg: KnockoutObservable<boolean> = ko.observable(true);
        enbWorkType: KnockoutObservable<boolean> = ko.observable(true);
        enbHalfDayFlg: KnockoutObservable<boolean> = ko.observable(true);
        enbChangeWorkHourFlg: KnockoutObservable<boolean> = ko.observable(true);
        enbbtnWorkTime: KnockoutObservable<boolean> = ko.observable(true);
        enbReasonCombo: KnockoutObservable<boolean> = ko.observable(true);
        enbContentReason: KnockoutObservable<boolean> = ko.observable(true);
        constructor() {

            let self = this;
            //KAF000_A
            self.kaf000_a = new kaf000.a.viewmodel.ScreenModel();
            //startPage 006a AFTER start 000_A
            self.startPage().done(function() {
                self.kaf000_a.start(self.employeeID, 1, 1, moment(new Date()).format("YYYY/MM/DD")).done(function() {
                    self.approvalSource = self.kaf000_a.approvalList;

                })
            })

        }
        /**
         * 
         */
        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            service.getAppForLeaveStart({
                appDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                employeeID: null
            }).done((data) => {
                $("#inputdate").focus();
                self.initData(data);
                self.holidayTypeCode.subscribe(function(value) {
                    self.checkDisplayEndDate(self.displayEndDateFlg());
                    if (self.checkStartDate()) {
                        return;
                    }
                    if (!nts.uk.util.isNullOrEmpty(self.selectedAllDayHalfDayValue())) {
                        var dfd = $.Deferred();
                        service.getAllAppForLeave({
                            startAppDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                            endAppDate: nts.uk.util.isNullOrEmpty(self.endAppDate()) ? null : moment(self.endAppDate()).format(self.DATE_FORMAT),
                            employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
                            displayHalfDayValue: self.displayHalfDayValue(),
                            holidayType: value,
                            alldayHalfDay: self.selectedAllDayHalfDayValue()
                        }).done((data) => {

                            self.displayStartFlg(true);
                            self.changeWorkHourValueFlg(data.changeWorkHourFlg);
                            if (nts.uk.util.isNullOrEmpty(data.workTypes)) {
                                self.typeOfDutys([]);
                                self.workTypecodes([]);
                                self.selectedTypeOfDuty(null);
                            } else {
                                self.typeOfDutys.removeAll();
                                self.workTypecodes.removeAll();
                                for (let i = 0; i < data.workTypes.length; i++) {
                                    self.typeOfDutys.push(new common.TypeOfDuty(data.workTypes[i].workTypeCode, data.workTypes[i].displayName));
                                    self.workTypecodes.push(data.workTypes[i].workTypeCode);
                                }
                                if (nts.uk.util.isNullOrEmpty(self.selectedTypeOfDuty)){
                                    self.selectedTypeOfDuty(data.workTypeCode);
                                }
                                
                            }
                            $("#workTypes").find("input:first").focus();
                            dfd.resolve(data);
                        }).fail((res) => {
                            dfd.reject(res);
                        });
                        return dfd.promise();
                    }
                });
                self.selectedAllDayHalfDayValue.subscribe(function(value) {
                    if (value == 0) {
                        self.enbHalfDayFlg(true);
                    } else {
                        self.enbHalfDayFlg(false);
                    }
                });


                //find by change AllDayHalfDay
                self.selectedAllDayHalfDayValue.subscribe((value) => {
                    self.findChangeAllDayHalfDay(value);
                });
                // find change value A5_3
                self.displayHalfDayValue.subscribe((value) => {
                    self.findChangeDisplayHalfDay(value);
                });
                self.selectedTypeOfDuty.subscribe((value) => {
                    self.findChangeWorkType(value);
                });
                self.displayWorkTimeName.subscribe((value) => {
                    self.changeDisplayWorkime();
                });
                // find changeDate
                self.appDate.subscribe(function(value) {
                    self.findChangeAppDate(value);
                });
                self.displayEndDateFlg.subscribe((value) => {
                    nts.uk.ui.errors.clearAll();
                    if (value) {
                        $('.ntsStartDatePicker').focus();
                        self.dateValue({ startDate: self.appDate(), endDate: "" });
                        self.dateValue.subscribe(function() {
                            self.findChangeAppDate(self.dateValue().startDate);
                        })
                    } else {
                        self.appDate(self.dateValue().startDate);
                        self.endAppDate('');
                        $("#inputdate").focus();
                    }
                });
                self.changeWorkHourValue.subscribe((value) =>{
                    self.changeDisplayWorkime();
                });
                nts.uk.ui.block.clear();
                dfd.resolve(data);
            }).fail((res) => {
                if (res.messageId == 'Msg_426') {
                    dialog.alertError({ messageId: res.messageId }).then(function() {
                        nts.uk.ui.block.clear();
                    });
                } else if (res.messageId == 'Msg_473') {
                    dialog.alertError({ messageId: res.messageId }).then(function() {
                        nts.uk.ui.block.clear();
                    });
                } else {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId }).then(function() {
                        nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml");
                        nts.uk.ui.block.clear();
                    });
                }
                dfd.reject(res);
            });
            return dfd.promise();

        }
        // change by appDate
        findChangeAppDate(data: any) {
            let self = this;
            self.checkDisplayEndDate(self.displayEndDateFlg());
            if (self.checkStartDate()) {
                return;
            }
            let dfd = $.Deferred();
            service.findByChangeAppDate({
                startAppDate: nts.uk.util.isNullOrEmpty(data) ? null : moment(data).format(self.DATE_FORMAT),
                employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
                displayHalfDayValue: self.displayHalfDayValue(),
                holidayType: self.holidayTypeCode(),
                prePostAtr: self.prePostSelected(),
                workTypeCode: self.selectedTypeOfDuty(),
                alldayHalfDay: self.selectedAllDayHalfDayValue()
            }).done((result) => {
                if (!nts.uk.util.isNullOrEmpty(result.workTypes)) {
                    self.typeOfDutys.removeAll();
                    self.workTypecodes.removeAll();
                    for (let i = 0; i < result.workTypes.length; i++) {
                        self.typeOfDutys.push(new common.TypeOfDuty(result.workTypes[i].workTypeCode, result.workTypes[i].displayName));
                        self.workTypecodes.push(result.workTypes[i].workTypeCode);
                    }
                    if (nts.uk.util.isNullOrEmpty(self.selectedTypeOfDuty)) {
                        self.selectedTypeOfDuty(data.workTypeCode);
                    }
                }
                self.prePostSelected(result.application.prePostAtr);
                self.displayPrePostFlg(result.prePostFlg);
                if (!nts.uk.util.isNullOrEmpty(self.startAppDate())) {
                    self.kaf000_a.getAppDataDate(1, moment(self.startAppDate()).format(self.DATE_FORMAT), false);
                }
                dfd.resolve(result);
            }).fail((res) => {
                dfd.reject(res);
            });
            return dfd.promise();
        }
        // change by switch button AllDayHalfDay(A3_12)
        findChangeAllDayHalfDay(value: any) {
            let self = this;
            self.checkDisplayEndDate(self.displayEndDateFlg());
            if (self.checkStartDate()) {
                return;
            }
            let dfd = $.Deferred();
            service.findChangeAllDayHalfDay({
                startAppDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                endAppDate: nts.uk.util.isNullOrEmpty(self.endAppDate()) ? null : moment(self.endAppDate()).format(self.DATE_FORMAT),
                employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
                displayHalfDayValue: self.displayHalfDayValue(),
                holidayType: nts.uk.util.isNullOrEmpty(self.holidayTypeCode()) ? null : self.holidayTypeCode(),
                alldayHalfDay: value
            }).done((result) => {
                self.changeWorkHourValueFlg(result.changeWorkHourFlg);
                if (nts.uk.util.isNullOrEmpty(result.workTypes)) {
                    self.typeOfDutys([]);
                    self.workTypecodes([]);
                    self.selectedTypeOfDuty(null);
                } else {
                    self.typeOfDutys.removeAll();
                    self.workTypecodes.removeAll();
                    for (let i = 0; i < result.workTypes.length; i++) {
                        self.typeOfDutys.push(new common.TypeOfDuty(result.workTypes[i].workTypeCode, result.workTypes[i].displayName));
                        self.workTypecodes.push(result.workTypes[i].workTypeCode);
                    }
                    if (nts.uk.util.isNullOrEmpty(self.selectedTypeOfDuty)) {
                        self.selectedTypeOfDuty(result.workTypeCode);
                    }
                }
                if (!nts.uk.util.isNullOrEmpty(result.workTimeCodes)) {
                    self.workTimeCodes.removeAll();
                    self.workTimeCodes(result.workTimeCodes);
                }
                dfd.resolve(result);
            }).fail((res) => {
                dfd.reject(res);
            });
            return dfd.promise();
        }
        // change by switch button DisplayHalfDay(A5_3)
        findChangeDisplayHalfDay(value: any) {
            let self = this;
            self.checkDisplayEndDate(self.displayEndDateFlg());
            if (self.checkStartDate()) {
                return;
            }
            let dfd = $.Deferred();
            service.getChangeDisplayHalfDay({
                startAppDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                endAppDate: nts.uk.util.isNullOrEmpty(self.endAppDate()) ? null : moment(self.endAppDate()).format(self.DATE_FORMAT),
                employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
                displayHalfDayValue: self.displayHalfDayValue(),
                holidayType: nts.uk.util.isNullOrEmpty(self.holidayTypeCode()) ? null : self.holidayTypeCode(),
                workTypeCode: self.selectedTypeOfDuty(),
                alldayHalfDay: self.selectedAllDayHalfDayValue()
            }).done((result) => {
                self.changeWorkHourValueFlg(result.changeWorkHourFlg);
                if (nts.uk.util.isNullOrEmpty(result.workTypes)) {
                    self.typeOfDutys([]);
                    self.workTypecodes([]);
                    self.selectedTypeOfDuty(null);
                } else {
                    self.typeOfDutys.removeAll();
                    self.workTypecodes.removeAll();
                    for (let i = 0; i < result.workTypes.length; i++) {
                        self.typeOfDutys.push(new common.TypeOfDuty(result.workTypes[i].workTypeCode, result.workTypes[i].displayName));
                        self.workTypecodes.push(result.workTypes[i].workTypeCode);
                    }
                    if (nts.uk.util.isNullOrEmpty(self.selectedTypeOfDuty)) {
                        self.selectedTypeOfDuty(result.workTypeCode);
                    }
                }
                if (!nts.uk.util.isNullOrEmpty(result.workTimeCodes)) {
                    self.workTimeCodes.removeAll();
                    self.workTimeCodes(result.workTimeCodes);
                }
                dfd.resolve(result);
            }).fail((res) => {
                dfd.reject(res);
            });
            return dfd.promise();
        }
        // change by workType
        findChangeWorkType(value: any) {
            let self = this;
            self.checkDisplayEndDate(self.displayEndDateFlg());
            if (self.checkStartDate()) {
                return;
            }
            let dfd = $.Deferred();
            service.getChangeWorkType({
                startAppDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
                holidayType: nts.uk.util.isNullOrEmpty(self.holidayTypeCode()) ? null : self.holidayTypeCode(),
                workTypeCode: self.selectedTypeOfDuty(),
                workTimeCode: self.workTimeCode()
            }).done((result) => {
                self.changeWorkHourValueFlg(result.changeWorkHourFlg);
                if (result.startTime1 != null) {
                    self.timeStart1(result.startTime1);
                }
                if (result.endTime1 != null) {
                    self.timeEnd1(result.endTime1);
                }
                dfd.resolve(result);
            }).fail((res) => {
                dfd.reject(res);
            });
            return dfd.promise();
        }
        initData(data: any) {
            let self = this;
            self.manualSendMailAtr(data.manualSendMailFlg);
            self.employeeName(data.employeeName);
            self.employeeID(data.employeeID);
            self.prePostSelected(data.application.prePostAtr);
            self.convertListHolidayType(data.holidayAppTypeName);
            self.holidayTypeCode(null);
            self.displayPrePostFlg(data.prePostFlg);
            self.displayWorkTimeName(nts.uk.resource.getText("KAF006_21"));
            self.mailFlag(data.mailFlg);
            if (data.applicationReasonDtos != null && data.applicationReasonDtos.length > 0) {
                let lstReasonCombo = _.map(data.applicationReasonDtos, o => { return new common.ComboReason(o.reasonID, o.reasonTemp); });
                self.reasonCombo(lstReasonCombo);
                let reasonID = _.find(data.applicationReasonDtos, o => { return o.defaultFlg == 1 }).reasonID;
                self.selectedReason(reasonID);

                self.multilContent(data.application.applicationReason);
            }
        }
        registerClick() {
            let self = this;
            self.checkDisplayEndDate(self.displayEndDateFlg());
            $("#workTypes").trigger('validate');
            if (self.displayEndDateFlg()) {
                $(".ntsStartDatePicker").trigger("validate");
                $(".ntsEndDatePicker").trigger("validate");
            } else {
                $("#inputdate").trigger("validate");
            }
            if (!self.validate()) { return; }
            if (nts.uk.ui.errors.hasError()) { return; }
            nts.uk.ui.block.invisible();
            let appReason: string;
            appReason = self.getReason(
                self.selectedReason(),
                self.reasonCombo(),
                self.multilContent()
            );
            let appReasonError = !appcommon.CommonProcess.checkAppReason(true, self.typicalReasonDisplayFlg(), self.displayAppReasonContentFlg(), appReason);
            if (appReasonError) {
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_115' }).then(function() { nts.uk.ui.block.clear(); });
                return;
            }
            if (!appcommon.CommonProcess.checklenghtReason(appReason, "#appReason")) {
                return;
            }
            if (!self.changeWorkHourValueFlg()) {
                self.changeWorkHourValue(false);
                self.timeStart1(null);
                self.timeEnd1(null);
                self.timeStart2(null);
                self.timeEnd2(null);
                self.workTimeCode(null);
            }
            let paramInsert = {
                prePostAtr: self.prePostSelected(),
                startDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                endDate: nts.uk.util.isNullOrEmpty(self.endAppDate()) ? moment(self.startAppDate()).format(self.DATE_FORMAT) : moment(self.endAppDate()).format(self.DATE_FORMAT),
                employeeID: self.employeeID(),
                applicationReason: appReason,
                holidayAppType: nts.uk.util.isNullOrEmpty(self.holidayTypeCode()) ? null : self.holidayTypeCode(),
                workTypeCode: self.selectedTypeOfDuty(),
                workTimeCode: nts.uk.util.isNullOrEmpty(self.workTimeCode()) ? null : self.workTimeCode(),
                halfDayFlg: self.displayHalfDayValue(),
                changeWorkHour: self.changeWorkHourValue(),
                allDayHalfDayLeaveAtr: self.selectedAllDayHalfDayValue(),
                startTime1: self.timeStart1(),
                endTime1: self.timeEnd1(),
                startTime2: self.timeStart2(),
                endTime2: self.timeEnd2(),
                displayEndDateFlg: self.displayEndDateFlg()
            };
            service.createAbsence(paramInsert).done((data) => {
                dialog.info({ messageId: "Msg_15" }).then(function() {
                    location.reload();
                });
            }).fail((res) => {
                dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds })
                    .then(function() { nts.uk.ui.block.clear(); });
            });
        }
        getReason(inputReasonID: string, inputReasonList: Array<common.ComboReason>, detailReason: string): string {
            let appReason = '';
            let inputReason: string = '';
            if (!nts.uk.util.isNullOrEmpty(inputReasonID)) {
                inputReason = _.find(inputReasonList, o => { return o.reasonId == inputReasonID; }).reasonName;
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
        btnSelectWorkTimeZone() {
            let self = this;
            self.getListWorkTime().done(() => {
                nts.uk.ui.windows.setShared('parentCodes', {
                    workTypeCodes: self.workTypecodes(),
                    selectedWorkTypeCode: self.selectedTypeOfDuty(),
                    workTimeCodes: self.workTimeCodes(),
                    selectedWorkTimeCode: self.workTimeCode()
                }, true);

                nts.uk.ui.windows.sub.modal('/view/kdl/003/a/index.xhtml').onClosed(function(): any {
                    //view all code of selected item 
                    var childData = nts.uk.ui.windows.getShared('childData');
                    if (childData) {
                        self.selectedTypeOfDuty(childData.selectedWorkTypeCode);
                        self.workTimeCode(childData.selectedWorkTimeCode);
                        self.workTimeName(childData.selectedWorkTimeName);
                        self.displayWorkTimeName(childData.selectedWorkTimeCode + "　" + childData.selectedWorkTimeName);
                        service.getWorkingHours(
                            {
                                holidayType: nts.uk.util.isNullOrEmpty(self.holidayTypeCode()) ? null : self.holidayTypeCode(),
                                workTypeCode: self.selectedTypeOfDuty(),
                                workTimeCode: self.workTimeCode()
                            }
                        ).done(data => {
                            self.timeStart1(data.startTime1 == null ? null : data.startTime1);
                            self.timeEnd1(data.endTime1 == null ? null : data.endTime1);
                        });
                    }
                });
            });
        }
        changeDisplayWorkime() {
            let self = this;
            self.eblTimeStart1(self.changeWorkHourValue() && (self.displayWorkTimeName() != nts.uk.resource.getText('KAF006_21')));
            self.eblTimeEnd1(self.changeWorkHourValue() && (self.displayWorkTimeName() != nts.uk.resource.getText('KAF006_21')));
        }
        getListWorkTime() {
            let self = this;
            let dfd = $.Deferred();
            service.getListWorkTime({
                startAppDate: nts.uk.util.isNullOrEmpty(self.startAppDate()) ? null : moment(self.startAppDate()).format(self.DATE_FORMAT),
                employeeID: nts.uk.util.isNullOrEmpty(self.employeeID()) ? null : self.employeeID(),
            }).done((value) => {
                self.workTimeCodes(value);
                dfd.resolve(value);
            }).fail((res) => {
                dfd.reject(res);
            })
            return dfd.promise();
        }
        convertListHolidayType(data: any) {
            let self = this;
            for (let i = 0; i < data.length; i++) {
                self.holidayTypes.push(new common.HolidayType(data[i].holidayAppTypeCode, data[i].holidayAppTypeName));
            }
        }
        checkStartDate(): boolean {
            let self = this;
            if (!nts.uk.util.isNullOrEmpty(self.startAppDate())) {
                if (!self.displayEndDateFlg()) {
                    nts.uk.ui.errors.clearAll();
                    $('#inputdate').trigger("validate");
                    if (nts.uk.ui.errors.hasError()) { return true; }
                } else {
                    nts.uk.ui.errors.clearAll();
                    $('.ntsStartDatePicker').trigger("validate");
                    if (nts.uk.ui.errors.hasError()) { return true; }
                }
            }
            return false;
        }
        private checkDisplayEndDate(data) {
            let self = this;
            if (data) {
                self.startAppDate(self.dateValue().startDate);
                self.endAppDate(self.dateValue().endDate);
            } else {
                self.startAppDate(self.appDate());
            }
        }
        validate(): boolean {
            let self = this;
            //勤務時間
            if (!nts.uk.util.isNullOrEmpty(self.timeStart1())) {
                if (!self.validateTime(self.timeStart1(), self.timeEnd1(), '#inpStartTime1')) {
                    return false;
                };
            }
            //            if ( !nts.uk.util.isNullOrEmpty(self.timeStart2()) && self.timeStart2() != "") {
            //                if ( !self.validateTime( self.timeStart2(), self.timeEnd2(), '#inpStartTime2' ) ) {
            //                    return false;
            //                };
            //            }   
            return true;
        }
        //Validate input time
        validateTime(startTime: number, endTime: number, elementId: string): boolean {
            if (startTime >= endTime) {
                dialog.alertError({ messageId: "Msg_307" })
                $(elementId).focus();
                return false;
            }
            return true;
        }

        /**
         * Jump to CMM018 Screen
         */
        openCMM018() {
            let self = this;
            nts.uk.request.jump("com", "/view/cmm/018/a/index.xhtml", { screen: 'Application', employeeId: self.employeeID });
        }
    }

}

