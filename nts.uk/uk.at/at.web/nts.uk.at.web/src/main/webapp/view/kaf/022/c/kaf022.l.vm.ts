module nts.uk.at.view.kaf022.l.viewmodel {
    import setShared =  nts.uk.ui.windows.setShared;
    import clear = nts.uk.ui.block.clear;
    import isNullOrEmpty = nts.uk.text.isNullOrEmpty;
    import getText = nts.uk.resource.getText;

    export class ScreenModel {
        //Screen mode
        screenMode: KnockoutObservable<ScreenMode> = ko.observable(ScreenMode.INSERT);
        dateFormat: string = 'YYYY/MM/DD';
        listComponentOption: any;
        selectedCode: KnockoutObservable<string> = ko.observable('');
        alreadySettingList: KnockoutObservableArray<any>;
        employmentName: KnockoutObservable<string> = ko.observable('');
        workTypeList: Array<any>;
        // 申請別対象勤務種類
        appSetData: KnockoutObservable<PreBeforeAppSetData> = ko.observable(new PreBeforeAppSetData(''));
        alreadySettingData: Array<any>;

        listWTShareKDL002: KnockoutObservableArray<any> = ko.observableArray([]);
        allowRegister: KnockoutObservable<boolean> = ko.observable(true);

        itemListD13: KnockoutObservableArray<any> = ko.observableArray([
            {code: 0, name: getText("KAF022_100")},
            {code: 1, name: getText("KAF022_101")}
        ]);

        constructor() {
            let self = this;
            self.alreadySettingList = ko.observableArray([]);
            self.listComponentOption = {
                isShowAlreadySet: true,
                isMultiSelect: false,
                listType: ListType.EMPLOYMENT,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: false,
                isShowNoSelectRow: false,
                alreadySettingList: self.alreadySettingList,
                maxRows: 12
            };

            //Employment code change listener
            self.selectedCode.subscribe(value => {
                nts.uk.ui.errors.clearAll();
                if (!isNullOrEmpty(value) && value != "undefined" && value != undefined) {
                    self.allowRegister(true);
                    //Get employment name  
                    let employmentList: Array<UnitModel> = $('#empt-list-setting').getDataList();
                    let selectedEmp = _.find(employmentList, {'code': value});
                    if (!nts.uk.util.isNullOrUndefined(selectedEmp)) {
                        self.employmentName(selectedEmp.name);
                    } else {
                        self.employmentName('');
                    }
                    //Get data setting            
                    self.changeEmploymentCode(value);
                } else {
                    self.allowRegister(false);
                    self.employmentName('');
                    self.changeEmploymentCode(value);
                }
            });

            $("#fixed-table-l4").ntsFixedTable({});
            $("#fixed-table-l5").ntsFixedTable({});
        }

        start(): JQueryPromise<any> {
            nts.uk.ui.block.grayout();
            const self = this;
            const dfd = $.Deferred();
            $('#empt-list-setting').ntsListComponent(self.listComponentOption).done(() => {
                $.when(
                    service.findAllWorktype(),
                    self.reloadData()
                ).done((worktypes, data) => {
                    self.workTypeList = worktypes;
                    self.updateWorkTypeName();
                    let employmentList: Array<UnitModel> = $('#empt-list-setting').getDataList();
                    if (_.size(employmentList))
                        self.selectedCode(employmentList[0].code);
                    else
                        self.selectedCode.valueHasMutated();
                    dfd.resolve();
                }).fail(error => {
                    dfd.reject();
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    clear();
                });
            }).fail((error) => {
                dfd.reject();
                clear();
                nts.uk.ui.dialog.alertError(error).then(function () {
                    nts.uk.request.jump("com", "view/ccg/008/a/index.xhtml");
                });
            });
            return dfd.promise();
        }

        reloadData(): JQueryPromise<any> {
            let self = this;
            const dfd = $.Deferred();
            service.findEmploymentSetByCid().done((data: Array<any>) => {
                let lstEmp = [];
                if (data != null && data.length > 0) {
                    for (let i = 0; i < data.length; i++) {
                        let dataI = data[i];
                        let listWTOAH = dataI.targetWorkTypeByAppLst;
                        if (listWTOAH != null && listWTOAH.length >= 1) {
                            for (let j = 0; j < listWTOAH.length; j++) {
                                let lstWork = [];
                                let workTypeListArr = listWTOAH[j];
                                if (workTypeListArr.workTypeLst != null && workTypeListArr.workTypeLst.length >= 1) {
                                    for (let k = 0; k < workTypeListArr.workTypeLst.length; k++) {
                                        let workTypeCode = workTypeListArr.workTypeLst[k];
                                        lstWork.push({
                                            companyID: dataI.companyID,
                                            employmentCode: dataI.employmentCD,
                                            appType: workTypeListArr.appType,
                                            holidayOrPauseType: workTypeListArr.appType == 10 ? workTypeListArr.opBreakOrRestTime : (workTypeListArr.appType == 1 ? workTypeListArr.opHolidayAppType : (workTypeListArr.appType == 3 ? workTypeListArr.opBusinessTripAppWorkType : null)),
                                            workTypeCode: workTypeCode
                                        })
                                    }
                                }
                                lstEmp.push({
                                    companyID: dataI.companyID,
                                    employmentCode: dataI.employmentCD,
                                    appType: workTypeListArr.appType,
                                    holidayOrPauseType: workTypeListArr.appType == 10 ? workTypeListArr.opBreakOrRestTime : (workTypeListArr.appType == 1 ? workTypeListArr.opHolidayAppType : (workTypeListArr.appType == 3 ? workTypeListArr.opBusinessTripAppWorkType : null)),
                                    holidayTypeUseFlg: workTypeListArr.opHolidayTypeUse,
                                    displayFlag: workTypeListArr.displayWorkType,
                                    lstWorkType: lstWork
                                })
                            }
                        }
                    }
                }

                self.alreadySettingList(_.map(lstEmp, item => ({
                    code: item.employmentCode,
                    isAlreadySetting: true
                })));
                //Store for preview process
                self.alreadySettingData = lstEmp;

                dfd.resolve();
            }).fail((res) => {
                dfd.reject();
            });
            return dfd.promise();
        }

        updateWorkTypeName() {
            let self = this;
            //Update work type name
//            if(nts.uk.util.isNullOrUndefined(self.alreadySettingData) || nts.uk.util.isNullOrUndefined(self.workTypeList)){
//                return;
//            }
            self.alreadySettingData = _.map(self.alreadySettingData, function (i: any) {
                i.lstWorkType = _.map(i.lstWorkType, function (wkType: any) {
                    let foundEmployment = _.find(self.workTypeList, function (item: any) {
                        return wkType.workTypeCode === item.workTypeCode;
                    });
                    if (!nts.uk.util.isNullOrUndefined(foundEmployment)) {
                        wkType.workTypeName = foundEmployment.name
                    } else {
                        wkType.workTypeName = wkType.workTypeCode + "マスタ未登録";
                    }
                    return wkType;
                });
                return i;
            });
        }

        /**
         * Load & binding data
         */
        changeEmploymentCode(empCode: string) {
            let self = this;
//            if(empCode === self.previewCode) return;
//            if(self.checkSaveChanged()){
//                 nts.uk.ui.dialog.confirm({ messageId: 'Msg_19' })
//                 .ifNo(function() { //Back to preview data
//                    self.selectedCode(self.previewCode);
//                    return;
//                });   
//             } 
            //return if no selected employment code
            if (isNullOrEmpty(empCode)) {
                self.screenMode(ScreenMode.INSERT);
                self.appSetData(new PreBeforeAppSetData(empCode));
                return;
            }
            nts.uk.ui.block.invisible();
            self.screenMode(ScreenMode.INSERT);
            self.appSetData(new PreBeforeAppSetData(empCode));
            //Find employment code
            let data = _.filter(self.alreadySettingData, function (item: any) {
                return item.employmentCode === empCode;
            });

            if (data != null && data.length > 0) {
                //残業申請
                let overTimeData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.OVER_TIME_APPLICATION;
                });
                if (overTimeData != null && overTimeData.length > 0) {
                    self.initDataSetting(self.appSetData().overTimeSet(), overTimeData[0]);
                }
                //休暇申請
                let absenceData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.ABSENCE_APPLICATION;
                });
                if (absenceData != null && absenceData.length > 0) {
                    self.initDataSettingWithListCode(self.appSetData().absenceSet(), absenceData);
                }
                //2:勤務変更申請
                let workChangeData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.WORK_CHANGE_APPLICATION;
                });
                if (workChangeData != null && workChangeData.length > 0) {
                    self.initDataSetting(self.appSetData().workChangeSet(), workChangeData[0]);
                }
                //3:出張申請
                let businessTripData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.BUSINESS_TRIP_APPLICATION;
                });
                if (businessTripData != null && businessTripData.length > 0) {
                    self.initDataSettingWithListCode(self.appSetData().businessTripSet(), businessTripData);
                }
                //4:直行直帰申請
                let goReturndirectData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.GO_RETURN_DIRECTLY_APPLICATION;
                });
                if (goReturndirectData != null && goReturndirectData.length > 0) {
                    self.initDataSetting(self.appSetData().goReturndirectSet(), goReturndirectData[0]);
                }
                //6:休出時間申請
                let breakTimeData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.BREAK_TIME_APPLICATION;
                });
                if (breakTimeData != null && breakTimeData.length > 0) {
                    self.initDataSetting(self.appSetData().breakTimeSet(), breakTimeData[0]);
                }
                //7:打刻申請(打刻申請)
                // let stampData = _.filter(data, function (item: any) {
                //     return item.appType === ApplicationType.STAMP_APPLICATION;
                // });
                // if (stampData != null && stampData.length > 0) {
                //     self.initDataSetting(self.appSetData().stampSet(), stampData[0]);
                // }
                //8:時間年休申請
                // let annualHolidayData = _.filter(data, function (item: any) {
                //     return item.appType === ApplicationType.ANNUAL_HOLIDAY_APPLICATION;
                // });
                // if (annualHolidayData != null && annualHolidayData.length > 0) {
                //     self.initDataSetting(self.appSetData().annualHolidaySet(), annualHolidayData[0]);
                // }
                //9:遅刻早退取消申請
                // let earlyLeaveData = _.filter(data, function (item: any) {
                //     return item.appType === ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION;
                // });
                // if (earlyLeaveData != null && earlyLeaveData.length > 0) {
                //     self.initDataSetting(self.appSetData().earlyLeaveSet(), earlyLeaveData[0]);
                // }
                //10:振休振出申請        
                let complementLeaveData = _.filter(data, function (item: any) {
                    return item.appType === ApplicationType.COMPLEMENT_LEAVE_APPLICATION;
                });
                if (complementLeaveData != null && complementLeaveData.length > 0) {
                    self.initDataSettingWithListCode(self.appSetData().complementLeaveSet(), complementLeaveData);
                }
                // //11:打刻申請（NR形式）
                // let stampNRData = _.filter(data, function(item:any) { return item.appType === ApplicationType.STAMP_NR_APPLICATION; });
                // if(stampNRData!= null && stampNRData.length > 0){
                //     self.initDataSetting(self.appSetData().stampNRSet(), stampNRData[0]);
                // }
                // //14:３６協定時間申請
                // let application36Data = _.filter(data, function(item:any) { return item.appType === ApplicationType.APPLICATION_36; });
                // if(application36Data!= null && application36Data.length > 0){
                //     self.initDataSetting(self.appSetData().application36Set(), application36Data[0]);
                // }
                self.screenMode(ScreenMode.UPDATE);
                //self.previewData = ko.mapping.toJS(self.appSetData());
                //self.previewCode = empCode;
            }
            clear();
        }

        /**
         * 登録処理
         */
        registerEmploymentSet() {
            nts.uk.ui.errors.clearAll();
            nts.uk.ui.block.grayout();
            let self = this;
            let commands = [];
            if (!self.allowRegister()) {
                clear();
                return;
            }

            // OVER_TIME_APPLICATION = 0
            const overTimeSet = ko.mapping.toJS(self.appSetData().overTimeSet());
            commands.push(overTimeSet);

            // ABSENCE_APPLICATION = 1
            _.forEach(self.appSetData().absenceSet(), function (item: any) {
                let noKo = ko.mapping.toJS(item);
                commands.push(noKo);
            });

            // WORK_CHANGE_APPLICATION = 2
            let wSet = ko.mapping.toJS(self.appSetData().workChangeSet());
            commands.push(wSet);

            // BUSINESS_TRIP_APPLICATION = 3
            _.forEach(self.appSetData().businessTripSet(), (item: any, index: number) => {
                let leave = ko.mapping.toJS(item);
                commands.push(leave);
            });

            // GO_RETURN_DIRECTLY_APPLICATION = 4
            let returnDirect = ko.mapping.toJS(self.appSetData().goReturndirectSet());
            commands.push(returnDirect);

            // BREAK_TIME_APPLICATION = 6
            let breakTime = ko.mapping.toJS(self.appSetData().breakTimeSet());
            commands.push(breakTime);

            // commands.push(ko.mapping.toJS(self.appSetData().stampSet()));
            // commands.push(ko.mapping.toJS(self.appSetData().annualHolidaySet()));
            // commands.push(ko.mapping.toJS(self.appSetData().earlyLeaveSet()));

            // COMPLEMENT_LEAVE_APPLICATION = 10
            _.forEach(self.appSetData().complementLeaveSet(), function (item: any) {
                let leave = ko.mapping.toJS(item);
                commands.push(leave);
            });
            // commands.push(ko.mapping.toJS(self.appSetData().stampNRSet()));
            // commands.push(ko.mapping.toJS(self.appSetData().application36Set()));
            commands.forEach(c => {
                if (c.displayFlag) {
                    if (!_.size(c.lstWorkType) || isNullOrEmpty(c.lstWorkType[0].workTypeCode) || isNullOrEmpty(c.displayWorkTypes)) {
                        switch (c.appType) {
                            case ApplicationType.OVER_TIME_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1377",
                                    messageParams: [getText("KAF022_3")]
                                });
                                break;
                            case ApplicationType.ABSENCE_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1378",
                                    messageParams: [getText("KAF022_4"), c.optionName]
                                });
                                break;
                            case ApplicationType.WORK_CHANGE_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1377",
                                    messageParams: [getText("KAF022_5")]
                                });
                                break;
                            case ApplicationType.BUSINESS_TRIP_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1378",
                                    messageParams: [getText("KAF022_6"), c.optionName]
                                });
                                break;
                            case ApplicationType.GO_RETURN_DIRECTLY_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1377",
                                    messageParams: [getText("KAF022_7")]
                                });
                                break;
                            case ApplicationType.BREAK_TIME_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1377",
                                    messageParams: [getText("KAF022_8")]
                                });
                                break;
                            case ApplicationType.COMPLEMENT_LEAVE_APPLICATION:
                                $("#" + c.inputId).ntsError("set", {
                                    messageId: "Msg_1378",
                                    messageParams: [getText("KAF022_12"), c.optionName]
                                });
                                break;
                        }
                    }
                } else {
                    c.lstWorkType = [];
                }
            });

            if (nts.uk.ui.errors.hasError()) {
                clear();
            } else {
                //parse to old object
                let list = [];
                for (let i = 0; i < commands.length; i++) {
                    let listWorkType = [];
                    let commandI = commands[i];
                    if (commandI.lstWorkType != null && commandI.lstWorkType.length >= 1) {
                        for (let j = 0; j < commandI.lstWorkType.length; j++) {
                            listWorkType.push(commandI.lstWorkType[j].workTypeCode);
                        }
                    }
                    list.push({
                        workTypeList: listWorkType,
                        appType: commandI.appType,
                        workTypeSetDisplayFlg: commandI.displayFlag,
                        holidayAppType: commandI.appType == 1 ? commandI.holidayOrPauseType : null,
                        holidayTypeUseFlg: commandI.holidayTypeUseFlg == null ? null : commandI.holidayTypeUseFlg,
                        swingOutAtr: commandI.appType == 10 ? commandI.holidayOrPauseType : null,
                        businessTripAtr: commandI.appType == 3 ? commandI.holidayOrPauseType : null
                    });
                }

                if (self.screenMode() === ScreenMode.INSERT) {
                    service.addEmploymentSet({
                        companyID: '',
                        employmentCode: self.selectedCode(),
                        listWTOAH: list
                    }).done(() => {
                        //マスタリストを更新。マスタ設定済みとする 
                        //let alreadyList: UnitAlreadySettingModel = {code: self.selectedCode(), isAlreadySetting: true};
                        //self.alreadySettingList.push(alreadyList);
                        //self.alreadySettingList.valueHasMutated();
                        //情報メッセージ（Msg_15）を表示する
                        nts.uk.ui.dialog.info({messageId: "Msg_15"}).then(() => {
                            nts.uk.ui.block.invisible();
                            //Load data setting
                            self.reloadData().done(() => {
                                self.updateWorkTypeName();
                                self.selectedCode.valueHasMutated();
                            }).fail(error => {
                                nts.uk.ui.dialog.alertError(error);
                            }).always(() => {
                                clear();
                            });
                        });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        clear();
                    });
                } else {
                    //Array To AppEmploymentSetComman
                    service.updateEmploymentSet({
                        companyID: '',
                        employmentCode: self.selectedCode(),
                        listWTOAH: list
                    }).done(() => {
                        //情報メッセージ（Msg_15）を表示する
                        nts.uk.ui.dialog.info({messageId: "Msg_15"}).then(() => {
                            nts.uk.ui.block.invisible();
                            self.reloadData().done(() => {
                                self.updateWorkTypeName();
                                self.selectedCode.valueHasMutated();
                            }).fail(error => {
                                nts.uk.ui.dialog.alertError(error);
                            }).always(() => {
                                clear();
                            });
                        });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        clear();
                    });
                }
            }
        }

        /**
         * 申請の前準備の削除処理
         */
        deleteEmploymentSet() {
            let self = this;
            ////画面を更新モードにする  
            if (self.screenMode() === ScreenMode.UPDATE) {
                nts.uk.ui.dialog.confirm({messageId: 'Msg_18'}).ifYes(function () {
                    nts.uk.ui.block.invisible();
                    let command = {
                        employmentCode: self.selectedCode()
                    };
                    service.deleteEmploymentSet(command).done(() => {
                        nts.uk.ui.dialog.info({messageId: 'Msg_16'});
                        //Remove already setting data
                        self.alreadySettingList.remove(function (item) {
                            return item.code === self.selectedCode();
                        });
                        self.alreadySettingList.valueHasMutated();
                        //Remove model data
                        self.appSetData(new PreBeforeAppSetData(self.selectedCode()));
                        //Remove DB data
                        _.remove(self.alreadySettingData, function (currentData: any) {
                            return currentData.employmentCode === self.selectedCode();
                        });
                        self.selectedCode.valueHasMutated();
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        clear();
                    });
                }).ifNo(function () {

                });
            }
        }

        /**
         * 申請の前準備を複写する
         */
        copyEmploymentSet() {
            let self = this;
            if (self.screenMode() === ScreenMode.UPDATE) {
                let listSetting = [];
                listSetting = _.map(self.alreadySettingList(), function (item: any) {
                    return item.code;
                });
                //CDL023：複写ダイアログを起動する
                let param = {
                    code: self.selectedCode(),
                    name: self.employmentName(),
                    targetType: 1,// 雇用
                    itemListSetting: listSetting,
                    baseDate: moment(new Date()).format(self.dateFormat)
                };
                setShared("CDL023Input", param);
                nts.uk.ui.windows.sub.modal("com", "/view/cdl/023/a/index.xhtml").onClosed(() => {
                    let data = nts.uk.ui.windows.getShared("CDL023Output");
                    if (!nts.uk.util.isNullOrUndefined(data)) {
                        nts.uk.ui.block.invisible();
                        //check overide mode
                        let isOverride: boolean = false;
                        let alreadyLst: Array<any> = _.filter(data,
                            function (emp) {
                                let foundEmployment = _.find(self.alreadySettingList(), function (item: any) {
                                    return item.code === emp;
                                });
                                return !nts.uk.util.isNullOrUndefined(foundEmployment);
                            });
                        //Overide mode
                        if (!nts.uk.util.isNullOrUndefined(alreadyLst) && alreadyLst.length > 0) {
                            isOverride = true;
                        }
                        let command = {
                            targetEmploymentCodes: data,
                            override: isOverride,
                            employmentCode: self.selectedCode()
                        };
                        service.copyEmploymentSet(command).done(() => {
                            //情報メッセージ（Msg_15）を表示する
                            nts.uk.ui.dialog.info({messageId: "Msg_15"}).then(() => {
                                nts.uk.ui.block.invisible();
                                self.reloadData().done(() => {
                                    self.updateWorkTypeName();
                                    self.selectedCode.valueHasMutated();
                                }).fail(error => {
                                    nts.uk.ui.dialog.alertError(error);
                                }).always(() => {
                                    clear();
                                });
                            });
                        }).fail(error => {
                            nts.uk.ui.dialog.alertError(error);
                        }).always(() => {
                            clear();
                        });
                    }
                });
            }
        }

        // get work type if apptype = 0
        findOtKaf022(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findOtKaf022().done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if apptype = 1
        findAbsenceKaf022(absenceKAF022: any): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findAbsenceKaf022(absenceKAF022).done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        //special
        findAbsenceSpecialKaf022(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findAbsenceSpecialKaf022().done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if app type = 2
        findWkChangeKaf022(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findWkChangeKaf022().done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if app type = 3
        findBusinessTripKaf022(command): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findBusinessTripKaf022(command).done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if app type = 4
        findBounceKaf022(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            let haplfDay = [11, 7, 2, 0, 4, 5, 6, 9];
            service.findBounceKaf022(haplfDay).done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if app type = 6
        findHdTimeKaf022(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findHdTimeKaf022().done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        // get work type if app type = 10
        findHdShipKaf022(hdShip): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            service.findHdShipKaf022(hdShip).done(data => {
                //Find already setting list
                self.listWTShareKDL002(data || []);
                dfd.resolve();
            }).fail(error => {
                dfd.reject();
                nts.uk.ui.dialog.alertError(error);
            });
            return dfd.promise();
        }

        getListWTShareKDL002(appType: number, holidayOrPauseType: number): JQueryPromise<any> {
            const self = this;
            self.listWTShareKDL002([]);
            switch (appType) {
                case ApplicationType.OVER_TIME_APPLICATION:
                    return self.findOtKaf022();
                case ApplicationType.ABSENCE_APPLICATION:
                    let absenceKAF022;
                    switch (holidayOrPauseType) {
                        case HolidayAppType.ANNUAL_PAID_LEAVE:
                            absenceKAF022 = {
                                oneDayAtr: 2,
                                morningAtr: 2,
                                afternoonAtr: 2,
                            };
                            break;
                        case HolidayAppType.SUBSTITUTE_HOLIDAY:
                            absenceKAF022 = {
                                oneDayAtr: 6,
                                morningAtr: 6,
                                afternoonAtr: 6,
                            };
                            break;
                        case HolidayAppType.ABSENCE:
                            absenceKAF022 = {
                                oneDayAtr: 5,
                                morningAtr: 5,
                                afternoonAtr: 5,
                            };
                            break;
                        case HolidayAppType.SPECIAL_HOLIDAY:
                            absenceKAF022 = {
                                oneDayAtr: 4,
                                morningAtr: 4,
                                afternoonAtr: 4,
                            };
                            break;
                        case HolidayAppType.YEARLY_RESERVE:
                            absenceKAF022 = {
                                oneDayAtr: 3,
                                morningAtr: 3,
                                afternoonAtr: 3,
                            };
                            break;
                        case HolidayAppType.HOLIDAY:
                            absenceKAF022 = {
                                oneDayAtr: 1,
                                morningAtr: 1,
                                afternoonAtr: 1,
                            };
                            break;
                        case HolidayAppType.DIGESTION_TIME:
                            absenceKAF022 = {
                                oneDayAtr: 9,
                                morningAtr: 9,
                                afternoonAtr: 9,
                            };
                            break;
                        default:
                            break;
                    }
                    if (holidayOrPauseType == HolidayAppType.SPECIAL_HOLIDAY) {
                        return self.findAbsenceSpecialKaf022();
                    } else {
                        return self.findAbsenceKaf022(absenceKAF022);
                    }
                case ApplicationType.WORK_CHANGE_APPLICATION:
                    return self.findWkChangeKaf022();
                case ApplicationType.BUSINESS_TRIP_APPLICATION:
                    let command;
                    switch (holidayOrPauseType) {
                        case 0:
                            command = [0];
                            break;
                        case 1:
                            command = [1, 7, 11];
                            break;
                        default:
                            break;
                    }
                    return self.findBusinessTripKaf022(command);
                case ApplicationType.GO_RETURN_DIRECTLY_APPLICATION:
                    return self.findBounceKaf022();
                case ApplicationType.BREAK_TIME_APPLICATION:
                    return self.findHdTimeKaf022();
                case ApplicationType.COMPLEMENT_LEAVE_APPLICATION:
                    let hdShip;
                    switch (holidayOrPauseType) {
                        case BreakOrRestTime.BREAKTIME:
                            hdShip = {
                                oneDayAtr: 7,
                                morningAtr2: 7,
                                afternoon2: [1, 2, 3, 4, 5, 6, 8, 9],
                                morningAtr3: [1, 2, 3, 4, 5, 6, 8, 9],
                                afternoon3: 7,
                                morningAtr4: 7,
                                afternoon4: [0],
                                morningAtr5: [0],
                                afternoon5: 7,
                            };
                            break;
                        case BreakOrRestTime.RESTTIME:
                            hdShip = {
                                oneDayAtr: 8,
                                morningAtr2: 8,
                                afternoon2: [0],
                                morningAtr3: [0],
                                afternoon3: 8,
                                morningAtr4: 8,
                                afternoon4: [1, 2, 3, 4, 5, 6, 8, 9],
                                morningAtr5: [1, 2, 3, 4, 5, 6, 8, 9],
                                afternoon5: 8,
                            };
                            break;
                        default:
                            break;
                    }
                    return self.findHdShipKaf022(hdShip);
                default:
                    let dfd = $.Deferred();
                    dfd.resolve();
                    return dfd.promise();
            }
        }

        /**
         * KDL002-勤務種類選択（ダイアログ）を起動する
         */
        openKDL002Dialog(itemSet: DataSetting) {
            nts.uk.ui.block.invisible();
            let self = this;
            // nts.uk.ui.errors.clearAll();
            let workTypeCodes = [];
            let selectedWorkTypes = _.map(itemSet.lstWorkType(), function (item: any) {
                return item.workTypeCode;
            });
            setShared('KDL002_Multiple', true);
            setShared('KDL002_SelectedItemId', selectedWorkTypes);
            self.getListWTShareKDL002(itemSet.appType, itemSet.holidayOrPauseType).done(() => {
                workTypeCodes = _.map(self.listWTShareKDL002(), function (item: any) {
                    return item.workTypeCode;
                });
                setShared('KDL002_AllItemObj', workTypeCodes);

                nts.uk.ui.windows.sub.modal('/view/kdl/002/a/index.xhtml').onClosed(function (): any {
                    let data = nts.uk.ui.windows.getShared('KDL002_SelectedNewItem');
                    let isCancel = nts.uk.ui.windows.getShared('KDL002_IsCancel');
                    if (!nts.uk.util.isNullOrUndefined(isCancel)) {
                        if (!isCancel) {
                            let newSelectedCodes = [];
                            if (data != null && data.length > 0) {
                                _.forEach(data, function (item: any) {
                                    let name = nts.uk.util.isNullOrUndefined(item.name) || nts.uk.util.isNullOrEmpty(item.name) ? 'マスタ未登録' : item.name;
                                    let newSelectedCode = {
                                        companyID: itemSet.companyId,
                                        employmentCode: itemSet.employmentCode,
                                        appType: itemSet.appType,
                                        holidayOrPauseType: itemSet.holidayOrPauseType,
                                        workTypeCode: item.code,
                                        workTypeName: name
                                    };
                                    newSelectedCodes.push(newSelectedCode);
                                });
                                itemSet.lstWorkType(newSelectedCodes);
                            }
                        }
                    }
                    clear();
                });
            }).always(() => {
                clear();
            });
        }

//        checkSaveChanged() : boolean{
//            let self = this;
//            if(nts.uk.util.isNullOrUndefined(self.previewData)) return false;
//            if(self.previewData.overTimeSet.holidayTypeUseFlg != self.appSetData().overTimeSet().holidayTypeUseFlg()) return true;
//            if(self.previewData.overTimeSet.displayWorkTypes != self.appSetData().overTimeSet().displayWorkTypes()) return true;
//            return false;
//        }
//        listenerSaveNotify(){
//            let self = this;
//            self.appSetData().overTimeSet().holidayTypeUseFlg.subscribe((value) =>{
//                if(self.previewData.overTimeSet.holidayTypeUseFlg != value) self.saveNotify(true);
//            });
//            self.appSetData().overTimeSet().displayWorkTypes.subscribe((value) =>{
//                if(self.previewData.overTimeSet.displayWorkTypes != value) self.saveNotify(true);
//            });
//        }
        initDataSetting(itemSet, data) {
            itemSet.companyId = data.companyID;
            itemSet.employmentCode = data.employmentCode;
            itemSet.appType = data.appType;
            itemSet.holidayOrPauseType = data.holidayOrPauseType;
            itemSet.displayFlag(data.displayFlag);
            itemSet.holidayTypeUseFlg(data.holidayTypeUseFlg);
            let sortLst = _.orderBy(data.lstWorkType, ['workTypeCode'], ['asc']);
            itemSet.lstWorkType(sortLst);
        }

        initDataSettingWithListCode(itemSet, datas: any[]) {
            let self = this;
            _.forEach(datas, function (item) {
                let index: number;
                index = _.findIndex(itemSet, function (o: any) {
                    return o.holidayOrPauseType == item.holidayOrPauseType;
                });
                if (index >= 0) {
                    itemSet[index].employmentCode = item.employmentCode;
                    itemSet[index].appType = item.appType;
                    itemSet[index].holidayOrPauseType = item.holidayOrPauseType;
                    itemSet[index].displayFlag(item.displayFlag);
                    itemSet[index].holidayTypeUseFlg(item.holidayTypeUseFlg ? 1 : 0);
                    let sortLst = _.orderBy(item.lstWorkType, ['workTypeCode'], ['asc']);
                    itemSet[index].lstWorkType(sortLst);
                }
            });
        }

    }

    class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    interface UnitModel {
        code: string;
        name?: string;
        workplaceName?: string;
        isAlreadySetting?: boolean;
    }

    class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

    interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }

    class ScreenMode {
        //新規モード
        static INSERT = 0;
        //更新モード
        static UPDATE = 1;
    }

    class PreBeforeAppSetData {
        //0:残業申請
        overTimeSet: KnockoutObservable<DataSetting>;
        //1:休暇申請
        absenceSet: KnockoutObservableArray<DataSetting>;
        //2:勤務変更申請
        workChangeSet: KnockoutObservable<DataSetting>;
        //3:出張申請
        businessTripSet: KnockoutObservableArray<DataSetting>;
        //4:直行直帰申請
        goReturndirectSet: KnockoutObservable<DataSetting>;
        //6:休出時間申請
        breakTimeSet: KnockoutObservable<DataSetting>;
        //7:打刻申請(打刻申請)
        // stampSet: KnockoutObservable<DataSetting>;
        //8:時間年休申請
        // annualHolidaySet: KnockoutObservable<DataSetting>;
        //9:遅刻早退取消申請
        // earlyLeaveSet: KnockoutObservable<DataSetting>;
        //10:振休振出申請
        complementLeaveSet: KnockoutObservableArray<DataSetting>;
        //11:打刻申請（NR形式）
        // stampNRSet: KnockoutObservable<DataSetting>;
        //14:３６協定時間申請
        // application36Set: KnockoutObservable<DataSetting>;

        constructor(employmentCode: string) {
            let self = this;
            this.overTimeSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.OVER_TIME_APPLICATION, 9));
            this.absenceSet = self.initDefaultAbsenceSet(employmentCode);
            this.workChangeSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.WORK_CHANGE_APPLICATION, 9));
            this.businessTripSet = self.initDefaultBusinessTripSet(employmentCode);
            this.goReturndirectSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.GO_RETURN_DIRECTLY_APPLICATION, 9));
            this.breakTimeSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.BREAK_TIME_APPLICATION, 9));
            // this.stampSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.STAMP_APPLICATION, 9));
            // this.annualHolidaySet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.ANNUAL_HOLIDAY_APPLICATION, 9));
            // this.earlyLeaveSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION, 9));
            this.complementLeaveSet = self.initDefaultComplementLeaveSet(employmentCode);
            // this.stampNRSet = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.STAMP_NR_APPLICATION, 9));
            // this.application36Set = ko.observable(self.initDefauleDataSetting(employmentCode, ApplicationType.APPLICATION_36, 9));
        }

        initDefauleDataSetting(employmentCode: string, appType: number, holidayOrPauseType: number): DataSetting {
            let workTypeData = {
                appType: appType,
                companyID: '',
                employmentCode: employmentCode,
                holidayOrPauseType: holidayOrPauseType,
                workTypeCode: '',
            };
            return new DataSetting('', employmentCode, appType, holidayOrPauseType, false, null, [workTypeData]);
        }

        initDefaultAbsenceSet(employmentCode: string): KnockoutObservableArray<DataSetting> {
            let self = this, absenceSet: KnockoutObservableArray<DataSetting> = ko.observableArray([]);
            const optionNames1 = ["KAF022_47", "KAF022_48", "KAF022_49", "KAF022_50", "KAF022_51", "KAF022_52", "KAF022_53"];
            const optionNames2 = ["KAF022_730", "KAF022_731", "KAF022_732", "KAF022_733", "KAF022_734", "KAF022_735", "KAF022_736"];
            for (let i = 0; i < 7; i++) {
                let resId: number = 47 + i;
                let dataSetting = self.initDefauleDataSetting(employmentCode, ApplicationType.ABSENCE_APPLICATION, i);
                if (dataSetting.displayFlag) {
                    dataSetting.holidayTypeUseFlg(1);
                }
                dataSetting.optionName(getText(optionNames1[i]));
                dataSetting.optionName2(getText(optionNames2[i]));
                absenceSet.push(dataSetting);
            }
            return absenceSet;
        }

        initDefaultComplementLeaveSet(employmentCode: string): KnockoutObservableArray<DataSetting> {
            let self = this,
                complementLeaveSet: KnockoutObservableArray<DataSetting> = ko.observableArray([]);
            let resId: Array<string> = ["KAF022_279", "KAF022_553"];
            for (let i = 0; i < 2; i++) {
                let dataSetting = self.initDefauleDataSetting(employmentCode, ApplicationType.COMPLEMENT_LEAVE_APPLICATION, (resId.length - (i + 1)));
                dataSetting.optionName(getText(resId[i]));
                complementLeaveSet.push(dataSetting);
            }
            return complementLeaveSet;
        }

        initDefaultBusinessTripSet(employmentCode: string): KnockoutObservableArray<DataSetting> {
            let self = this,
                businessTripSet: KnockoutObservableArray<DataSetting> = ko.observableArray([]);
            let resId: Array<string> = ["KAF022_737", "KAF022_738"];
            for (let i = 0; i < 2; i++) {
                let dataSetting = self.initDefauleDataSetting(employmentCode, ApplicationType.BUSINESS_TRIP_APPLICATION, i);
                dataSetting.optionName(getText(resId[i]));
                businessTripSet.push(dataSetting);
            }
            return businessTripSet;
        }
    }

    class DataSetting {
        companyId: string;
        employmentCode: string;
        appType: number;
        holidayOrPauseType: number;
        // 表示する勤務種類を設定する
        displayFlag: KnockoutObservable<boolean> = ko.observable(false);
        // 休暇種類を利用しない
        holidayTypeUseFlg: KnockoutObservable<number> = ko.observable(null);
        // 勤務種類リスト
        lstWorkType: KnockoutObservableArray<any> = ko.observableArray();
        displayWorkTypes: KnockoutObservable<string> = ko.observable('');
        optionName: KnockoutObservable<string> = ko.observable('');
        optionName2: KnockoutObservable<string> = ko.observable('');
        // enableButton: KnockoutObservable<boolean>;
        inputId: string;

        constructor(companyId: string, employmentCode: string, appType: number, holidayOrPauseType: number, displayFlag: boolean, holidayTypeUseFlg: boolean, lstWorkType: Array<any>) {
            // this.enableButton = ko.observable(displayFlag);
            this.inputId = "L5_" + appType + "_" + (holidayOrPauseType || "");
            this.companyId = companyId;
            this.employmentCode = employmentCode;
            this.appType = appType;
            this.holidayOrPauseType = holidayOrPauseType;
            this.displayFlag(displayFlag);
            this.holidayTypeUseFlg(holidayTypeUseFlg ? 1 : 0);
            this.lstWorkType(lstWorkType);
            this.lstWorkType.extend({rateLimit: 50});
            this.displayWorkTypes = ko.observable(lstWorkType.map(function (elem) {
                return elem.workTypeName;
            }).join(" + "));
            this.lstWorkType.subscribe(value => {
                this.displayWorkTypes(_.map(this.lstWorkType(), item => {
                    return nts.uk.util.isNullOrUndefined(item.workTypeName) || nts.uk.util.isNullOrEmpty(item.workTypeName) ? 'マスタ未登録' : item.workTypeName;
                }).join(" + "));
                if (_.size(value))
                    $("#" + this.inputId).ntsError("clear");
            });
            // this.holidayTypeUseFlg.subscribe(value => {
            //     if (value == 0 && this.displayFlag() == true) {
            //         this.enableButton(true);
            //     } else {
            //         this.enableButton(false);
            //     }
            // });
            this.displayFlag.subscribe(value => {
                // this.holidayTypeUseFlg.valueHasMutated();
                // this.enableButton(value);
                if (value == false) {
                    $("#" + this.inputId).ntsError("clear");
                }
            });
        }
    }

    enum ApplicationType {
        /** 残業申請*/
        OVER_TIME_APPLICATION = 0,
        /** 休暇申請*/
        ABSENCE_APPLICATION = 1,
        /** 勤務変更申請*/
        WORK_CHANGE_APPLICATION = 2,
        /** 出張申請*/
        BUSINESS_TRIP_APPLICATION = 3,
        /** 直行直帰申請*/
        GO_RETURN_DIRECTLY_APPLICATION = 4,
        /** 休出時間申請*/
        BREAK_TIME_APPLICATION = 6,
        /** 打刻申請*/
        // STAMP_APPLICATION = 7,
        /** 時間年休申請*/
        // ANNUAL_HOLIDAY_APPLICATION = 8,
        /** 遅刻早退取消申請*/
        // EARLY_LEAVE_CANCEL_APPLICATION = 9,
        /** 振休振出申請*/
        COMPLEMENT_LEAVE_APPLICATION = 10,
        /** 打刻申請（NR形式）*/
        // STAMP_NR_APPLICATION = 11,
        /** 連続出張申請*/
        // LONG_BUSINESS_TRIP_APPLICATION = 12,
        /** 出張申請オフィスヘルパー*/
        // BUSINESS_TRIP_APPLICATION_OFFICE_HELPER = 13,
        /** ３６協定時間申請*/
        // APPLICATION_36 = 14,
        /**任意項目申請*/
        // OPTIONAL_ITEM_APPLICATION = 15
    }

    enum HolidayAppType {
        /**
         * 年次有休
         */
        ANNUAL_PAID_LEAVE = 0,
        /**
         * 代休
         */
        SUBSTITUTE_HOLIDAY = 1,
        /**
         * 欠勤
         */
        ABSENCE = 2,
        /**
         * 特別休暇
         */
        SPECIAL_HOLIDAY = 3,
        /**
         * 積立年休
         */
        YEARLY_RESERVE = 4,
        /**
         * 休日
         */
        HOLIDAY = 5,
        /**
         * 時間消化
         */
        DIGESTION_TIME = 6
    }

    enum BreakOrRestTime {

        /**
         * 振休
         */
        RESTTIME = 0,

        /**
         * 振出
         */
        BREAKTIME = 1
    }

}