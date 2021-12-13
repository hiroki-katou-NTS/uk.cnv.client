module nts.uk.at.view.kmk009.a.viewmodel {

    import EnumUse = service.model.Enum;
    import Enum = service.model.Enum;
    import WorkTypeDto = service.model.WorkTypeDto;
    import WorkTimeDto = service.model.WorkTimeDto;
    import TotalTimesDto = service.model.TotalTimesDto;
    import TotalConditionDto = service.model.TotalConditionDto;
    import TotalSubjectsDto = service.model.TotalSubjectsDto;
    import TotalTimesDetailDto = service.model.TotalTimesDetailDto;
    import DailyAttendanceItemDto = service.model.DailyAttendanceItemDto;
    import text = nts.uk.resource.getText;


    export class ScreenModel {
        itemTotalTimesEng: KnockoutObservableArray<any>;
        itemTotalTimes: KnockoutObservableArray<TotalTimesModel>;
        itemTotalTimesDetail: TotalTimesDetailModel;
        stash: TotalTimesDetailModel;
        totalClsEnums: Array<Enum>;
        totalClsEnumsUse: Array<EnumUse>;
        valueEnum: KnockoutObservable<number>;
        currentCode: KnockoutObservable<number>;
        columns: KnockoutObservableArray<any>;
        useSet: KnockoutObservableArray<any>;
        selectUse: KnockoutObservable<number>;
        enableUse: KnockoutObservable<boolean>;
        enableName: KnockoutObservable<boolean>;
        enableUpper: KnockoutObservable<boolean>;
        selectUppper: KnockoutObservable<number>;
        selectUnder: KnockoutObservable<number>;
        enableUnder: KnockoutObservable<boolean>;
        enableWorkType: KnockoutObservable<boolean>;
        enableWorkTime: KnockoutObservable<boolean>;
        enableSave: KnockoutObservable<boolean>;
        enableSwitch: KnockoutObservable<boolean>;
        //        atdItem: DailyAttendanceItemDto;
        attendanceModel: AttendanceModel;
        enableAtdBtn: KnockoutObservable<boolean>;
        checkedCountAtr: KnockoutObservable<boolean>;
        langId: KnockoutObservable<string> = ko.observable('ja');
        totalTimesNameEn: KnockoutObservable<string> = ko.observable(null);

        selectedCode: KnockoutObservable<string> = ko.observable(null);
        isAllowShowAttendance: KnockoutObservable<boolean>= ko.observable(false);
        enableSelectUpper: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            var self = this;
            self.itemTotalTimesEng = ko.observableArray([]);
            self.itemTotalTimes = ko.observableArray([]);
            self.itemTotalTimesDetail = new TotalTimesDetailModel();
            self.stash = new TotalTimesDetailModel();
            self.totalClsEnums = [];
            self.totalClsEnumsUse = [];
            self.valueEnum = ko.observable(null);
            self.currentCode = ko.observable(null);
            self.columns = ko.observableArray([
                { headerText: nts.uk.resource.getText('KMK009_4'), key: 'totalCountNo', formatter: _.escape, width: 30 },
                { headerText: nts.uk.resource.getText('KMK009_5'), key: 'useAtrName', formatter: _.escape, width: 80 },
                { headerText: nts.uk.resource.getText('KMK009_6'), key: 'totalTimesName', formatter: _.escape, width: 100 },
                //{ headerText: nts.uk.resource.getText('KMK009_6'), key: 'totalTimesName', formatter: _.escape, width: 110 },
                { headerText: nts.uk.resource.getText('KMK009_14'), key: 'summaryAtrName', formatter: _.escape, width: 65 }
            ]);
            self.useSet = ko.observableArray([
                { code: 1, name: nts.uk.resource.getText("KMK009_12") },
                { code: 0, name: nts.uk.resource.getText("KMK009_13") },
            ]);
            self.selectUse = ko.observable(0);
            self.selectUppper = ko.observable(0);
            self.selectUnder = ko.observable(0);
            self.enableUse = ko.observable(false);
            self.enableName = ko.observable(false);
            self.enableUpper = ko.observable(false);
            self.enableUnder = ko.observable(false);
            self.enableSave = ko.observable(true);
            self.enableSwitch = ko.observable(true);
            self.enableWorkType = ko.computed(function () {
                return self.enableUse() && ((self.valueEnum() == self.totalClsEnums[0].value) || (self.valueEnum() == self.totalClsEnums[2].value));
            });

            self.enableWorkTime = ko.computed(function () {
                return self.enableUse() && ((self.valueEnum() == self.totalClsEnums[1].value) || (self.valueEnum() == self.totalClsEnums[2].value));
            });

            self.attendanceModel = new AttendanceModel();
            self.enableAtdBtn = ko.observable(true);

            //subscribe currentCode
            self.currentCode.subscribe(function (codeChanged) {
                if (!codeChanged || codeChanged < 1) {
                    self.enableSave(false);
                    self.enableSwitch(false);
                    self.resetData();
                    //self.enableAtdBtn(false);
                    self.attendanceModel.attendanceItemName('');
                    return;
                }
				self.attendanceModel.update(null, null);
                self.enableSave(true);
                self.enableSwitch(true);
                //self.enableAtdBtn(true);
                self.clearError();
                if (codeChanged === 0) { return; }
                self.selectUse(null);
                self.loadAllTotalTimesDetail(codeChanged);

                $('#switch-use').focus();
                if (self.langId() === 'en') {
                    $("#itemname").focus();
                }
            });
            //subscribe selectUse
            self.selectUse.subscribe(function (codeChanged) {
                self.loadBySelectUse(self.checkSelectUse(), self.selectUnder(), self.selectUppper());
                self.clearError();
            });

            //subscribe upper Limit
            self.selectUppper.subscribe(function (isUpper) {
                self.loadBySelectUse(self.checkSelectUse(), self.selectUnder(), isUpper);
                self.setRequiredTargetItem();
            });

            //subscribe under Limit
            self.selectUnder.subscribe(function (isUnder) {
                self.loadBySelectUse(self.checkSelectUse(), isUnder, self.selectUppper());
                self.setRequiredTargetItem();
            });

            self.checkedCountAtr = ko.observable();
            self.checkedCountAtr.subscribe(function () {
                if (typeof self.checkedCountAtr() != "undefined") {
                    if (self.checkedCountAtr() == true) {
                        self.itemTotalTimesDetail.countAtr(0);
                    } else {
                        self.itemTotalTimesDetail.countAtr(1);
                    }
                } else {
                    self.switchCheckbox(self.itemTotalTimesDetail.countAtr());
                }
            });
            self.langId.subscribe(() => {
                self.changeLanguage();
            });
        }

        /**
         * set required text editor
         */
        private setRequiredTargetItem(): void {
            let self = this;
            // set label is optional
            if (self.selectUppper() == 0 && self.selectUnder() == 0) {
                $('#targetItemLb').css("border-color", "#97D155");
            }
            // set label is required
            else {
                $('#targetItemLb').css("border-color", "#FAC002");
            }
        }

        /**
         * check select use by use
         */
        private checkSelectUse(): boolean {
            var self = this;
            return ((self.selectUse() === "1") || self.selectUse() === 1);
        }
        /**
         * load data by select use
         */
        private loadBySelectUse(isUse: boolean, isUnder: boolean, isUpper: boolean): void {
            var self = this;
            if (isUse) {
                self.enableUse(true);
                self.enableName(true);
                self.itemTotalTimesDetail.useAtr(1);
            }
            else {
                self.enableUse(false);
                self.enableName(false);
                self.itemTotalTimesDetail.useAtr(0);
            }
            if (isUnder && self.checkSelectUse()) {
                self.enableUnder(true);
                self.itemTotalTimesDetail.totalCondition.lowerLimitSettingAtr(1);
            } else {
                self.enableUnder(false);
                self.itemTotalTimesDetail.totalCondition.lowerLimitSettingAtr(0);
            }
            if (isUpper && self.checkSelectUse()) {
                self.enableUpper(true);
                self.itemTotalTimesDetail.totalCondition.upperLimitSettingAtr(1);
            } else {
                self.enableUpper(false);
                self.itemTotalTimesDetail.totalCondition.upperLimitSettingAtr(0);
            }

            /* if (isUse == true)  && (isUnder == true || isUpper == true)) {
                // enable btn
                self.enableAtdBtn(true);
            } else {
                self.enableAtdBtn(false);
            } */
            //condition 6, ver7
            self.enableAtdBtn(isUse); //ver 7
            //A3_17 && A3_20                                 
            self.enableSelectUpper(isUse && !_.isNil(self.attendanceModel.attendanceItemName()));            
        }

        /**
         * start page
         * get all divergence time
         * get all divergence name
         */
        public startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            // load all data  Enum
            $("#switch-language")['ntsSwitchMasterLanguage']();
            $("#switch-language").on("selectionChanged", (event: any) => self.langId(event['detail']['languageId']));
            self.loadTotalClsEnum().done(function () {
                self.loadTotalUseEnum().done(function () {
                    self.loadAllTotalTimes().done(() => {
                        self.currentCode(self.itemTotalTimes()[0].totalCountNo);
                        self.loadAllTotalTimesDetail(self.currentCode()).done(() => {
                            if (self.totalClsEnums.length > 0) {
                                self.valueEnum(self.totalClsEnums[self.itemTotalTimesDetail.summaryAtr()].value);
                                self.switchCheckbox(self.itemTotalTimesDetail.countAtr());
                            }

                            $(document).on("keydown", function (e) {
                                var x = document.getElementsByClassName("check-focus");
                                if (e.which === 8 && !$(e.target).is("input, textarea")) {
                                    $('.check-focus').focus();
                                    e.preventDefault();
                                }
                            });

                            $('.check-focus').keydown(function (event) { return cancelBackspace(event) });
                            function cancelBackspace(event) {
                                if (event.keyCode == 8) {
                                    return false;
                                }
                            }

                            function no_backspaces(event) {
                                backspace = 8;
                                if (event.keyCode == backspace) event.preventDefault();
                            }
                            dfd.resolve();
                        });
                    });

                });
            });

            return dfd.promise();
        }

        private loadAllTotalTimes(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();
            self.findLanguageDefault();
            service.getAllTotalTimes().done(function (data) {
                nts.uk.ui.block.clear();
                self.itemTotalTimes([]);
                var models: TotalTimesModel[] = [];
                for (var dto of data) {
                    var model = new TotalTimesModel();

                    var item = _.find(self.itemTotalTimesEng, ['totalCountNo', dto.totalCountNo]);
                    var index = _.findIndex(self.itemTotalTimesEng, ['totalCountNo', dto.totalCountNo]);
                    if (item && self.itemTotalTimesEng[index] != undefined && item.totalCountNo == dto.totalCountNo) {
                        dto.totalTimesNameEn = self.itemTotalTimesEng[index].totalTimesNameEng;
                    } else {
                        dto.totalTimesNameEn = null;
                    }
                    model.updateData(dto);
                    model.summaryAtrName = self.totalClsEnums[dto.summaryAtr].localizedName;
                    model.useAtrName = self.totalClsEnumsUse[dto.useAtr].localizedName;
                    models.push(model);
                }
                self.itemTotalTimes(models);

                self.itemTotalTimes.valueHasMutated();

                dfd.resolve();
            }).fail(function (res) {
                nts.uk.ui.dialog.alertError(res);
            }).always(function () {
                nts.uk.ui.block.clear();
            });

            return dfd.promise();
        }

        private findLanguageDefault(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            service.findByLangId('en').done((data) => {
                self.itemTotalTimesEng = data;
                _.each(self.itemTotalTimesEng, (x) => {
                    let item = _.find(self.itemTotalTimes(), ['totalCountNo', parseInt(self.currentCode())]);
                    if (item) {
                        self.totalTimesNameEn = x.totalTimesNameEng;
                    } else {
                        self.totalTimesNameEn = "";
                    }
                });

                dfd.resolve();
            }).fail(() => {
                dfd.reject();
            });
            return dfd.promise();
        }

        // loadAllTotalTimesDetail
        private loadAllTotalTimesDetail(codeChanged): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();

            service.getAllTotalTimesDetail(codeChanged).done(function (data) {
                //                nts.uk.ui.block.clear();
                if (data) {
                    if (self.langId() == 'en') {
                        _.each(self.itemTotalTimes(), (x) => {
                            let item = _.find(self.itemTotalTimesEng, ['totalCountNo', parseInt(self.currentCode())]);
                            if (item) {
                                self.totalTimesNameEn = item.totalTimesNameEng;
                            } else {
                                self.totalTimesNameEn = "";
                            }
                        });
                        data.totalTimesName = self.totalTimesNameEn;
                        self.itemTotalTimes.totalTimesNameEn = self.totalTimesNameEn;
                    }
                    self.stash.updateData(data);
                    self.itemTotalTimesDetail.updateData(data);
                    self.selectUse(self.itemTotalTimesDetail.useAtr());                    

                    // disable or enable Upper limit and under limit
                    self.selectUppper(data.totalCondition.upperLimitSettingAtr);
                    if (self.selectUppper() == 1 && self.checkSelectUse()) {
                        self.enableUpper(true);
                    } else {
                        self.enableUpper(false);
                    }
                    self.selectUnder(data.totalCondition.lowerLimitSettingAtr);
                    if (self.selectUnder() == 1 && self.checkSelectUse()) {
                        self.enableUnder(true);
                    } else {
                        self.enableUnder(false);
                    }

                    self.attendanceModel.attendanceItemId(data.totalCondition.attendanceItemId);
					if(data.totalCondition.attendanceItemId) {
						self.isAllowShowAttendance(data.totalCondition.attendanceItemId < 426 || data.totalCondition.attendanceItemId > 435);	
					} else {
						self.isAllowShowAttendance(false);	
					}
      
                    self.switchCheckbox(data.countAtr);

                    self.loadListWorkType().done(function () {
                        self.loadListWorkTimes().done(function () {
                            $.when(self.loadTotalClsEnum(), service.findAllDailyAttendanceItem()).done(function (a1, dataRes: Array<DailyAttendanceItemDto>) {
                                // load all data  Enum
                                if (self.totalClsEnums.length > 0) {
                                    self.valueEnum(self.totalClsEnums[self.itemTotalTimesDetail.summaryAtr()].value);
                                }
                                if (_.isUndefined(self.attendanceModel.attendanceItemId())
                                    || _.isNull(self.attendanceModel.attendanceItemId())) {
                                    self.attendanceModel.update(null, null);
                                    nts.uk.ui.windows.setShared('SelectedAttendanceId', "", true);
                                } else {
                                    let selectID: Array<any> = _.filter(dataRes, function (item) {
                                        return item.attendanceItemId == self.attendanceModel.attendanceItemId();
                                    })
                                    if (!_.isEmpty(selectID)) {
                                        self.attendanceModel.update(selectID[0].attendanceItemId, selectID[0].attendanceItemName);
                                        nts.uk.ui.windows.setShared('SelectedAttendanceId', selectID[0].attendanceItemId, true);
                                        if (self.checkSelectUse() && selectID[0].attendanceItemName) self.enableUse(true);
                                        self.isAllowShowAttendance(selectID[0].attendanceItemId < 426 || selectID[0].attendanceItemId > 435);
                                    }
                                }

                                self.enableSelectUpper(self.checkSelectUse() && !_.isNil(self.attendanceModel.attendanceItemName()));  
                                nts.uk.ui.block.clear();    
                            });                            
                            dfd.resolve();
                        });
                    });
                }

                if (self.langId() == 'en') {
                    $("#itemname").focus();
                    if ($('.nts-validate').ntsError("hasError") == true) {
                        self.enableSave(false);
                        self.enableSwitch(false);
                    }
                    if ($('.nts-editor').ntsError("hasError") == true) {
                        self.enableSave(false);
                        self.enableSwitch(false);
                    }
                    self.enableName(true);
                    self.enableUse(false);
                    self.enableSwitch(false);
                    if (self.selectUse() == 0) {
                        self.enableName(false);
                    }
                    self.enableUnder(false);
                    self.enableAtdBtn(false);
                }
            });

            return dfd.promise();
        }

        // loadListWorkTypes
        private loadListWorkType(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();

            let lstWorkTypeCd: Array<string> = _.filter(self.itemTotalTimesDetail.listTotalSubjects(), (item) => item.workTypeAtr() == 0)
                .map((item) => item.workTypeCode());

            service.findListByIdWorkTypes(lstWorkTypeCd).done(function (res: Array<WorkTypeDto>) {
                nts.uk.ui.block.clear();

                if (res && res.length > 0) {
                    self.itemTotalTimesDetail.workTypeInfo(res.map(item => item.workTypeCode + ' ' + item.name || text("KAL003_120")).join(" ＋ "));
                    self.stash.workTypeInfo(res.map(item => item.workTypeCode + ' ' + item.name || text("KAL003_120")).join(" ＋ "));
                } else {

                    lstWorkTypeCd = _.filter(lstWorkTypeCd, cd => { return cd; });
                    let cdLst = _.map(lstWorkTypeCd, cd => { return cd + " " + text("KAL003_120") });
                    self.itemTotalTimesDetail.workTypeInfo(cdLst.join(" ＋ "));
                    self.stash.workTypeInfo(cdLst.join(" ＋ "));
                }
                dfd.resolve();
            });

            return dfd.promise();
        }


        // loadListWorkTimes
        private loadListWorkTimes(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();

            let lstWorkTypeCd: Array<string> = _.filter(self.itemTotalTimesDetail.listTotalSubjects(), (item) => item.workTypeAtr() == 1)
                .map((item) => item.workTypeCode());

            service.findListByIdWorkTimes(lstWorkTypeCd).done(function (res: Array<WorkTimeDto>) {
                nts.uk.ui.block.clear();

                if (res && res.length > 0) {
                    self.itemTotalTimesDetail.workingInfo(res.map(item => item.code + ' ' + item.name || text("KAL003_120")).join(" ＋ "));
                    self.stash.workingInfo(res.map(item => item.code + ' ' + item.name || text("KAL003_120")).join(" ＋ "));
                } else {
                    let cdLst = _.map(lstWorkTypeCd, cd => { return cd + " " + text("KAL003_120") });
                    self.itemTotalTimesDetail.workingInfo(cdLst.join(" ＋ "));
                    self.stash.workingInfo(cdLst.join(" ＋ "));
                }
                dfd.resolve();
            });

            return dfd.promise();
        }

        // load enum
        private loadTotalClsEnum(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();

            // get setting
            service.getTotalClsEnum().done(function (dataRes: Array<Enum>) {

                self.totalClsEnums = dataRes;

                nts.uk.ui.block.clear();

                dfd.resolve();
            });

            return dfd.promise();
        }

        // load enum enum
        private loadTotalUseEnum(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred<any>();

            nts.uk.ui.block.invisible();

            // get setting
            service.getTotalUseEnum().done(function (dataRes: Array<EnumUse>) {

                self.totalClsEnumsUse = dataRes;

                nts.uk.ui.block.clear();

                dfd.resolve();
            });

            return dfd.promise();
        }



        // save Daily Pattern in database
        public save() {
            let self = this;
            $('.nts-input').ntsError('check');

            if ($('.nts-input').ntsError('hasError')) {
                return;
            };
            nts.uk.ui.block.invisible();
            if (self.langId() == 'en') {
                var param = {
                    totalCountNo: self.currentCode(),
                    langId: self.langId(),
                    totalTimesNameEn: self.itemTotalTimesDetail.totalTimesName()
                }
                service.saveTotalLang(param).done(function () {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function () {
                        // Focus grid list
                        $('#single-list-dataSource_container').focus();
                        self.loadAllTotalTimes().done(function () {
                            self.loadAllTotalTimesDetail(self.currentCode()).done(function () {
                            });
                        });
                    });
                });
                nts.uk.ui.block.clear();
            } else {
                //trim() name
                self.itemTotalTimesDetail.totalTimesName($.trim(self.itemTotalTimesDetail.totalTimesName()));
                self.itemTotalTimesDetail.totalTimesABName($.trim(self.itemTotalTimesDetail.totalTimesABName()));
                // save enum
                self.itemTotalTimesDetail.summaryAtr(self.valueEnum());
                // define dataDto

                // Get data to save
                self.getSaveData();
                if (!self.itemTotalTimesDetail.workTypeInfo() && self.enableWorkType()) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_10' });
                    nts.uk.ui.block.clear();
                    return;
                }
                if (!self.itemTotalTimesDetail.workingInfo() && self.enableWorkTime()) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_29' });
                    nts.uk.ui.block.clear();
                    return;
                }

                service.saveAllTotalTimes(self.itemTotalTimesDetail.toDto()).done(function () {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function () {
                        // Focus grid list
                        $('#single-list-dataSource_container').focus();
                        self.loadAllTotalTimes().done(function () {
                            self.loadAllTotalTimesDetail(self.currentCode()).done(function () {
                            });
                        });
                    });


                }).fail(function (res) {
                    nts.uk.ui.dialog.alertError(res).then(() => {
                        if (res.messageId == "Msg_2217") {
                            $("#inpAlarmTime").focus();
                        }
                    });
                }).always(function () {
                    nts.uk.ui.block.clear();
                });
            }
        }
        // openKDL001Dialog
        public openKDL001Dialog() {
            var self = this;
            nts.uk.ui.block.invisible();
            // check worktype or worktime send to KDL001Dialog
            var listWorkType = [];
            var listWorkCode = [];
            for (let i = 0; i < self.itemTotalTimesDetail.listTotalSubjects().length; i++) {
                if (self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeAtr() == 0) {
                    listWorkType[i] = self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeCode();
                } else {
                    listWorkCode[i] = self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeCode();
                }
            }
            service.findAllWorkTimes().done(function (dataRes: Array<WorkTimeDto>) {
                //list All workTime
                let list: Array<string> = dataRes.map(item => item.code);

                nts.uk.ui.windows.setShared('kml001multiSelectMode', true);
                nts.uk.ui.windows.setShared('kml001selectAbleCodeList', list);
                nts.uk.ui.windows.setShared('kml001selectedCodeList', listWorkCode, true);
                nts.uk.ui.windows.sub.modal('/view/kdl/001/a/index.xhtml', { title: nts.uk.resource.getText('KDL001') }).onClosed(function (): any {
                    nts.uk.ui.block.clear();

                    let isCancel = nts.uk.ui.windows.getShared('KDL001_IsCancel');
                    if (!isCancel) {
                        var shareWorkCocde: Array<string> = nts.uk.ui.windows.getShared('kml001selectedCodeList');

                        // deleted data worktype
                        self.itemTotalTimesDetail.listTotalSubjects(_.filter(self.itemTotalTimesDetail.listTotalSubjects(), (item) => item.workTypeAtr() == 0));
                        // insert data worktype
                        for (var item of shareWorkCocde) {
                            if (!_.isNull(item) && !_.isEmpty(item) && !_.isUndefined(item) && item != 'null') {
                                self.itemTotalTimesDetail.listTotalSubjects().push(self.toSubjectModel(item, 1));
                            }
                        }
                        self.loadListWorkType();
                        self.loadListWorkTimes();
                    }
                    if ($('#inpDialog').ntsError("hasError") == true) {
                        $('#inpDialog').ntsError('clear');
                    }
                    $("#itemname").focus();
                });
            });
        }
        //        open KDL002Dialog()
        public openKDL002Dialog() {
            var self = this;
            nts.uk.ui.block.invisible();
            // check worktype or worktime send to KDL002Dialog
            var listWorkType = [];
            var listWorkCode = [];
            for (let i = 0; i < self.itemTotalTimesDetail.listTotalSubjects().length; i++) {
                if (self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeAtr() == 0) {
                    listWorkType[i] = self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeCode();
                } else {
                    listWorkCode[i] = self.itemTotalTimesDetail.listTotalSubjects()[i].workTypeCode();
                }
            }

            service.findAllWorkTypes().done(function (dataRes: Array<WorkTypeDto>) {
                //list All workType
                let list: Array<string> = dataRes.map(item => item.workTypeCode);
                nts.uk.ui.windows.setShared('KDL002_Multiple', true);
                nts.uk.ui.windows.setShared('KDL002_AllItemObj', list);
                nts.uk.ui.windows.setShared('KDL002_SelectedItemId', listWorkType, true);
                nts.uk.ui.windows.sub.modal('/view/kdl/002/a/index.xhtml', { title: nts.uk.resource.getText('KDL002') }).onClosed(function (): any {
                    nts.uk.ui.block.clear();
                    let isCancel = nts.uk.ui.windows.getShared('KDL002_IsCancel');
                    if (!isCancel) {
                        var shareWorkType: Array<any> = nts.uk.ui.windows.getShared('KDL002_SelectedNewItem');
                        // deleted data worktype
                        self.itemTotalTimesDetail.listTotalSubjects(_.filter(self.itemTotalTimesDetail.listTotalSubjects(), (item) => item.workTypeAtr() == 1));
                        // insert data worktype
                        for (let i = 0; i < shareWorkType.length; i++) {
                            self.itemTotalTimesDetail.listTotalSubjects().push(self.toSubjectModel(shareWorkType[i].code, 0));
                        }
                        self.loadListWorkType();
                        self.loadListWorkTimes();
                    }
                    if ($('#inpDialog').ntsError("hasError") == true) {
                        $('#inpDialog').ntsError('clear');
                    }

                    $("#itemname").focus();
                });
            });
        }

        // openKDL002Dialog_A3_28
        public openKDL002Dialog_A3_28() {
            var self = this;
            nts.uk.ui.block.invisible();

            service.findAllAttendanceItem().done(function (dataRes: Array<number>) {
                // nts.uk.ui.windows.setShared('KDL002_Multiple', false);
                nts.uk.ui.windows.setShared('AllAttendanceObj', dataRes);
                if (_.isNull(self.attendanceModel.attendanceItemId())) {
                    nts.uk.ui.windows.setShared('SelectedAttendanceId', "", true);
                } else {
                    nts.uk.ui.windows.setShared('SelectedAttendanceId', [self.attendanceModel.attendanceItemId()], true);
                }

                nts.uk.ui.windows.sub.modal('/view/kdl/021/a/index.xhtml', { title: nts.uk.resource.getText('KDL021') }).onClosed(function (): any {
                    let atdSelected: Array<any> = nts.uk.ui.windows.getShared('selectedChildAttendace');
                    if (!_.isNil(atdSelected)) {
                        nts.uk.ui.block.invisible();
                        $.when(service.findAllDailyAttendanceItem()).done(function (dataRes: Array<DailyAttendanceItemDto>) {
                            let dailyAttendanceItem: Array<DailyAttendanceItemDto> = _.filter(dataRes, function (obj) { return obj.attendanceItemId == atdSelected });
                            if (_.isUndefined(atdSelected) || _.isEmpty(atdSelected) || _.isUndefined(dailyAttendanceItem) || !atdSelected.length) {
                                self.attendanceModel.update(null, null);
                                self.isAllowShowAttendance(false);
                                if (self.checkSelectUse()){
                                    self.enableUse(true);
                                    self.enableSelectUpper(false);
                                    self.enableUnder(false);
                                    self.enableUpper(false);
                                    // self.enableWorkType(true);
                                    // self.enableWorkType(true);
                                }
                            } else {
                                self.attendanceModel.update(dailyAttendanceItem[0].attendanceItemId, dailyAttendanceItem[0].attendanceItemName);
                                self.isAllowShowAttendance(dailyAttendanceItem[0].attendanceItemId < 426 || dailyAttendanceItem[0].attendanceItemId > 435);
                                self.enableUse(self.checkSelectUse());
                                self.enableSelectUpper(self.checkSelectUse());
                            }
                            // self.enableUse(self.checkSelectUse() && _.isNull(self.attendanceModel.attendanceItemName()));
                            // self.enableSelectUpper(self.checkSelectUse() && _.isNull(self.attendanceModel.attendanceItemName()));
                            nts.uk.ui.block.clear();
                        }).fail(() => {
                            nts.uk.ui.block.clear();
                        });
                    }
                    // if ( !atdSelected.length) {
                    //     self.enableUse(true);
                    //     self.enableWorkType(true);
                    //     self.attendanceModel.update(null, null);
                    //     self.isAllowShowAttendance(false);
                    // }
                });
            });
        }

        clearError(): void {
            if ($('.nts-validate').ntsError("hasError") == true) {
                $('.nts-validate').ntsError('clear');
            }
            if ($('.nts-editor').ntsError("hasError") == true) {
                $('.nts-input').ntsError('clear');
            }
        }
        /**
         * to init model
         */

        private toSubjectModel(workTypeCode: string, workTypeAtr): TotalSubjectsModel {
            var model: TotalSubjectsModel = new TotalSubjectsModel();
            var dto: TotalSubjectsDto = { workTypeCode: workTypeCode, workTypeAtr: workTypeAtr };
            model.updateData(dto);
            return model;
        }

        /**
         * reset data by not selected 
         */
        private resetData(): void {
            var self = this;
            self.enableUse(false);
            self.enableName(false);
            self.itemTotalTimesDetail.resetData();
        }

        /**
         * Get save data
         */
        private getSaveData(): void {
            let self = this;

            // Get data from current data model.
            let saveData: TotalTimesDetailModel = self.itemTotalTimesDetail;
            if (self.langId() == 'en') {
                self.itemTotalTimesDetail.totalTimesName = self.totalTimesNameEn;
            }
            // Get reserved data from stash if input is disabled.
            if (!self.enableWorkType()) {

                let lstWorkCd: Array<TotalSubjectsModel> = _.filter(saveData.listTotalSubjects(), (item) => item.workTypeAtr() == 1);

                saveData.listTotalSubjects(_.filter(self.stash.listTotalSubjects(), (item) => item.workTypeAtr() == 0));

                for (var i = 0; i < lstWorkCd.length; i++) {
                    saveData.listTotalSubjects.push(lstWorkCd[i]);
                }
                saveData.workTypeInfo(self.stash.workTypeInfo());
            }
            if (!self.enableWorkTime()) {
                let lstWorkCd: Array<TotalSubjectsModel> = _.filter(saveData.listTotalSubjects(), (item) => item.workTypeAtr() == 0);

                saveData.listTotalSubjects(_.filter(self.stash.listTotalSubjects(), (item) => item.workTypeAtr() == 1));

                for (var i = 0; i < lstWorkCd.length; i++) {
                    saveData.listTotalSubjects.push(lstWorkCd[i]);
                }

                saveData.workingInfo(self.stash.workingInfo());
            }
            if (!self.enableUpper()) {
                saveData.totalCondition.thresoldUpperLimit(self.stash.totalCondition.thresoldUpperLimit());
            }
            if (!self.enableUnder()) {
                saveData.totalCondition.thresoldLowerLimit(self.stash.totalCondition.thresoldLowerLimit());
            }
            if (!self.enableUse()) {
                saveData.updateData(self.stash.toDto());
                saveData.useAtr(0);
            }
			if (self.selectUse() == SelectUseConst.Use) {
				if(_.isNumber(self.attendanceModel.attendanceItemId())) {
					saveData.totalCondition.attendanceItemId(self.attendanceModel.attendanceItemId());	
				} else {
	                saveData.totalCondition.attendanceItemId(null);
					saveData.totalCondition.upperLimitSettingAtr(0);
			        saveData.totalCondition.lowerLimitSettingAtr(0);
			        saveData.totalCondition.thresoldUpperLimit(null);
			        saveData.totalCondition.thresoldLowerLimit(null);
	            }
			}
        }

        private switchCheckbox(value: number): void {
            let self = this;
            if (value == 0) {
                self.checkedCountAtr(true);
            } else {
                self.checkedCountAtr(false);
            }
        }
        private exportExcel(): void {
            var self = this;
            nts.uk.ui.block.grayout();
            service.saveAsExcel(self.langId()).done(function () {
            }).fail(function (error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
            }).always(function () {
                nts.uk.ui.block.clear();
            });
        }

        private changeLanguage(): void {
            let self = this,
                grid = $("#single-list"),
                lang: string = ko.toJS(self.langId);
            self.itemTotalTimesDetail.langId = self.langId();
            if (lang == 'ja') {

                $("#left-content").css('width', '320');
                $("#single-list-dataSource").igGrid("option", "width", "330px");

                //remove columns otherLanguageName
                var cols = $("#single-list-dataSource").igGrid("option", "columns");
                cols.splice(3, 1);
                $("#single-list-dataSource").igGrid("option", "columns", cols);
                self.currentCode.valueHasMutated();
            } else {
                self.findWorkTypeLanguage();
            }
        }

        private findWorkTypeLanguage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.loadAllTotalTimes();
            service.findByLangId(self.langId()).done((data) => {
                self.itemTotalTimesEng = data;
                _.each(self.itemTotalTimesEng, (x) => {
                    let item = _.find(self.itemTotalTimes(), ['totalCountNo', parseInt(self.currentCode())]);
                    if (item) {
                        self.totalTimesNameEn = x.totalTimesNameEng;
                    } else {
                        self.totalTimesNameEn = "";
                    }
                });
                $("#single-list-dataSource").igGrid("option", "width", "395px");
                $("#left-content").css('width', '380');

                var cols = $("#single-list-dataSource").igGrid("option", "columns");
                if ($("#single-list-dataSource").igGrid("option", "columns").length == 4) {
                    //add columns otherLanguageName   
                    var newColumn = { headerText: nts.uk.resource.getText('KMK007_9'), key: 'totalTimesNameEn', width: 100, formatter: _.escape };
                    cols.splice(3, 0, newColumn);
                    $("#single-list-dataSource").igGrid("option", "columns", cols);
                }
                self.currentCode.valueHasMutated();
                dfd.resolve();
            }).fail(() => {
                dfd.reject();
            });
            return dfd.promise();
        }

    }

    export class TotalTimesModel {
        totalCountNo: number;
        summaryAtr: number;
        useAtr: number;
        totalTimesName: string;
        summaryAtrName: string;
        useAtrName: string;
        totalTimesNameEn: string;

        constructor() {
            this.totalCountNo = 1;
            this.summaryAtr = 1;
            this.useAtr = 1;
            this.totalTimesName = null;
            this.summaryAtrName = null;
            this.useAtrName = null;
            this.totalTimesNameEn = null;
        }

        updateData(dto: TotalTimesDto) {
            this.totalCountNo = dto.totalCountNo;
            this.summaryAtr = dto.summaryAtr;
            this.useAtr = dto.useAtr;
            this.totalTimesName = dto.totalTimesName;
            this.totalTimesNameEn = dto.totalTimesNameEn;
        }
    }

    export class AttendanceModel {
        attendanceItemId: KnockoutObservable<number>;
        attendanceItemName: KnockoutObservable<string>;

        constructor() {
            this.attendanceItemId = ko.observable();
            this.attendanceItemName = ko.observable();
        }

        update(attendanceItemId: number, attendanceItemName: string) {
            this.attendanceItemId(attendanceItemId);
            this.attendanceItemName(attendanceItemName);
        }
    }

    export class TotalTimesDetailModel {
        totalCountNo: number;
        countAtr: KnockoutObservable<number>;
        useAtr: KnockoutObservable<number>;
        totalTimesName: KnockoutObservable<string>;
        totalTimesABName: KnockoutObservable<string>;
        summaryAtr: KnockoutObservable<number>;
        totalCondition: TotalConditionModel;
        listTotalSubjects: KnockoutObservableArray<TotalSubjectsModel>;
        workTypeInfo: KnockoutObservable<string>;
        workingInfo: KnockoutObservable<string>;
        totalTimesNameEn: KnockoutObservable<string>;
        langId: KnockoutObservable<string>;

        constructor() {
            this.totalCountNo = 1;
            this.countAtr = ko.observable(1);
            this.useAtr = ko.observable(1);
            this.totalTimesName = ko.observable('');
            this.totalTimesABName = ko.observable('');
            this.summaryAtr = ko.observable(1);
            this.totalCondition = new TotalConditionModel();
            this.listTotalSubjects = ko.observableArray([]);
            this.workTypeInfo = ko.observable(null);
            this.workingInfo = ko.observable(null);
            this.totalTimesNameEn = ko.observable(null);
            this.langId = ko.observable(null);
        }

        updateData(dto: TotalTimesDetailDto) {
            this.totalCountNo = dto.totalCountNo;
            this.countAtr(dto.countAtr);
            this.useAtr(dto.useAtr);
            this.totalTimesName(dto.totalTimesName);
            this.totalTimesABName(dto.totalTimesABName);
            this.totalCondition.updateData(dto.totalCondition);
            this.summaryAtr(dto.summaryAtr);
            this.listTotalSubjects([]);
            var listTotalSubjectsUpdate: TotalSubjectsModel[] = [];
            for (var item of dto.listTotalSubjects) {
                var model: TotalSubjectsModel = new TotalSubjectsModel();
                model.updateData(item);
                listTotalSubjectsUpdate.push(model);
            }
            this.listTotalSubjects(listTotalSubjectsUpdate);

        }

        toDto(): TotalTimesDetailDto {
            var listTotalSubjectsDto: TotalSubjectsDto[] = [];
            for (var model of this.listTotalSubjects()) {
                listTotalSubjectsDto.push(model.toDto());
            }
            var dto: TotalTimesDetailDto = {
                totalCountNo: this.totalCountNo,
                countAtr: this.countAtr(),
                useAtr: this.useAtr(),
                totalTimesName: this.totalTimesName(),
                totalTimesABName: this.totalTimesABName(),
                totalCondition: this.totalCondition.toDto(),
                summaryAtr: this.summaryAtr(),
                listTotalSubjects: listTotalSubjectsDto
            };
            return dto;
        }
        resetData() {
            this.totalCountNo = 0;
            this.countAtr(0);
            this.useAtr(0);
            this.totalTimesName('');
            this.totalTimesABName('');
            this.totalCondition.resetData();
            this.summaryAtr(0);
            this.listTotalSubjects([]);
            this.workTypeInfo('');
            this.workingInfo('');
        }
    }

    export class TotalConditionModel {
        upperLimitSettingAtr: KnockoutObservable<number>;
        lowerLimitSettingAtr: KnockoutObservable<number>;
        thresoldUpperLimit: KnockoutObservable<number>;
        thresoldLowerLimit: KnockoutObservable<number>;
        attendanceItemId: KnockoutObservable<number>;
        constructor() {
            this.upperLimitSettingAtr = ko.observable(1);
            this.lowerLimitSettingAtr = ko.observable(1);
            this.thresoldUpperLimit = ko.observable(1);
            this.thresoldLowerLimit = ko.observable(1);
            this.attendanceItemId = ko.observable(1);
        }
        updateData(dto: TotalConditionDto) {
            this.upperLimitSettingAtr(dto.upperLimitSettingAtr);
            this.lowerLimitSettingAtr(dto.lowerLimitSettingAtr);
            this.thresoldUpperLimit(dto.thresoldUpperLimit);
            this.thresoldLowerLimit(dto.thresoldLowerLimit);
            this.attendanceItemId(dto.attendanceItemId);
        }

        toDto(): TotalConditionDto {
            var dto: TotalConditionDto = {
                upperLimitSettingAtr: this.upperLimitSettingAtr(),
                lowerLimitSettingAtr: this.lowerLimitSettingAtr(),
                thresoldUpperLimit: this.thresoldUpperLimit(),
                thresoldLowerLimit: this.thresoldLowerLimit(),
                attendanceItemId: this.attendanceItemId()
            };
            return dto;
        }
        resetData() {
            this.upperLimitSettingAtr(0);
            this.lowerLimitSettingAtr(0);
            this.thresoldUpperLimit(0);
            this.thresoldLowerLimit(0);
            this.attendanceItemId(0);
        }
    }

    export class TotalSubjectsModel {
        workTypeCode: KnockoutObservable<string>;
        workTypeAtr: KnockoutObservable<number>;

        constructor() {
            this.workTypeCode = ko.observable('');
            this.workTypeAtr = ko.observable(1);
        }
        updateData(dto: TotalSubjectsDto) {
            this.workTypeCode(dto.workTypeCode);
            this.workTypeAtr(dto.workTypeAtr);
        }
        toDto(): TotalSubjectsDto {
            var dto: TotalSubjectsDto = {
                workTypeCode: this.workTypeCode(),
                workTypeAtr: this.workTypeAtr()
            };
            return dto;
        }
    }

    export enum SelectUseConst {
        Use = "1",
        NoUse = "0",
        NO_SELECT = -1,
    }
}