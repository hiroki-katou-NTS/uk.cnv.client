module nts.uk.at.view.kdw007.a.viewmodel {
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import block = nts.uk.ui.block;
    import alertError = nts.uk.ui.dialog.alertError;
    import getText = nts.uk.resource.getText;

    enum ScreenMode {
        Daily = 0,
        Monthly = 1
    }
    export class ScreenModel {
        screenMode: KnockoutObservable<number> = ko.observable(ScreenMode.Daily);
        isNewMode: KnockoutObservable<boolean> = ko.observable(false);
        // ver22
        isDuplicate: KnockoutObservable<boolean> = ko.observable(false);
        isAtdItemColor: KnockoutObservable<boolean> = ko.observable(true);
        enumShowTypeAtr: KnockoutObservableArray<any> = ko.observableArray([
            //fix bug 98671
            //{ code: 0, name: "全てを表示する" },
            { code: 0, name: "追加した項目を表示する" },
            { code: 1, name: "システム固定項目を表示する" }
        ]);
        showTypeAtr: KnockoutObservable<number> = ko.observable(0);
        listUseAtr: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText("Enum_UseAtr_Use") },
            { code: 0, name: getText("Enum_UseAtr_NotUse") }
        ]);
        listRemarkCancelErrorInput: KnockoutObservableArray<any> = ko.observableArray([
            { code: 1, name: getText("KDW007_109") },
            { code: 0, name: getText("KDW007_110") }
        ]);
        listRemarkColumnNo: KnockoutObservableArray<any> = ko.observableArray([]);
        listTypeAtr: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: getText("Enum_ErrorAlarmClassification_Error") },
            { code: 1, name: getText("Enum_ErrorAlarmClassification_Alarm") },
            // { code: '2', name: getText("Enum_ErrorAlarmClassification_Other") }
            { code: 2, name: getText("Enum_ErrorAlarmClassification_ExCondition") }
           
        ]);
        gridListColumns: KnockoutObservableArray<any> = ko.observableArray([
            { headerText: getText("KDW007_6"), key: 'code', width: 45 },
            { headerText: getText("KDW007_7"), key: 'name', width: 280 ,formatter: _.escape}
        ]);
        lstFilteredData: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedErrorAlarm: KnockoutObservable<any>;
        selectedErrorAlarmCode: KnockoutObservable<string> = ko.observable(null);
        tabs: KnockoutObservableArray<any> = ko.observableArray([
            { id: 'tab-1', title: getText("KDW007_9"), content: '.settingTab', enable: ko.observable(true), visible: ko.observable(true) },
            { id: 'tab-2', title: getText("KDW007_83"), content: '.checkScopeTab', enable: ko.observable(true), visible: ko.observable(true) },
            { id: 'tab-3', title: getText("KDW007_84"), content: '.conditionSettingTab1', enable: ko.observable(true), visible: ko.observable(true) },
            { id: 'tab-4', title: getText("KDW007_85"), content: '.conditionSettingTab2', enable: ko.observable(true), visible: ko.observable(true) },
            { id: 'tab-5', title: getText("KDW007_86"), content: '.applicationTab', enable: ko.observable(true), visible: ko.observable(true) }
        ]);
        selectedTab: KnockoutObservable<string> = ko.observable('tab-1');
        errorDisplayItemName: KnockoutObservable<string> = ko.observable("");
        //Tab3
        enumLogicalOperator: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: 'AND' },
            { code: 1, name: 'OR' }
        ]);
        enumFilterByCompare: KnockoutObservableArray<any> = ko.observableArray([
            { code: 0, name: getText("Enum_FilterByCompare_NotCompare") },
            { code: 1, name: getText("Enum_FilterByCompare_Extract_Same") },
            { code: 2, name: getText("Enum_FilterByCompare_Extract_Different") }
        ]);
        // Tab 5: ApplicationType_ 実績で使用する申請種類
        lstApplicationType = ko.observableArray([
            { code: 0, name: getText("Enum_ApplicationType_OVERTIME_APPLICATION") }, //OVERTIME_APPLICATION
            { code: 1, name: getText("Enum_ApplicationType_OVERTIME_APPLICATION_NORMAL") }, //OVERTIME_APPLICATION_NORMAL
            { code: 2, name: getText("Enum_ApplicationType_OVERTIME_APPLICATION_EARLY_REGULAR") }, //  OVERTIME_APPLICATION_EARLY_REGULAR
            { code: 3, name: getText("Enum_ApplicationType_ABSENCE_APPLICATION") }, //ABSENCE_APPLICATION
            { code: 4, name: getText("Enum_ApplicationType_WORK_CHANGE_APPLICATION") }, // WORK_CHANGE_APPLICATION
            { code: 5, name: getText("Enum_ApplicationType_BUSINESS_TRIP_APPLICATION") }, // BUSINESS_TRIP_APPLICATION
            { code: 6, name: getText("Enum_ApplicationType_GO_RETURN_DIRECTLY_APPLICATION") }, // GO_RETURN_DIRECTLY_APPLICATION
            { code: 7, name: getText("Enum_ApplicationType_LEAVE_TIME_APPLICATION") }, // LEAVE_TIME_APPLICATION
            { code: 8, name: getText("Enum_ApplicationType_STAMP_APPLICATION") }, // STAMP_APPLICATION
            { code: 10, name: getText("Enum_ApplicationType_ANNUAL_HD_APPLICATION") }, // ANNUAL_HD_APPLICATION
            { code: 11, name: getText("Enum_ApplicationType_EARLY_LEAVE_CANCEL_APPLICATION") }, // EARLY_LEAVE_CANCEL_APPLICATION
            { code: 12, name: getText("Enum_ApplicationType_COMPLEMENT_LEAVE_APPLICATION") }, // COMPLEMENT_LEAVE_APPLICATION
            { code: 13, name: getText("Enum_ApplicationType_OPTIONAL_APPLICATION") }, // OPTIONAL_APPLICATION
        ]);
        appTypeGridlistColumns = ko.observableArray([
            { headerText: 'コード', key: 'code', width: 100, hidden: true },
            { headerText: getText("KDW007_82"), key: 'name', width: 300 },
        ]);

        sideBar: KnockoutObservable<number>;
        codeToSelect: KnockoutObservable<string> = ko.observable(null);
        //  ver22: ◆大塚オプション
        ootsukaOption: KnockoutObservable<boolean> = ko.observable(false);
        
        constructor(isDaily:any) {
            let self = this;
            self.sideBar = ko.observable(2);
            if (isDaily) { //daily
                self.screenMode(ScreenMode.Daily);
            } else {
                self.screenMode(ScreenMode.Monthly);
            }
            self.selectedErrorAlarm = ko.observable(new ErrorAlarmWorkRecord(self.screenMode()));
            self.showTypeAtr.subscribe((val) => {
                nts.uk.ui.block.invisible();
                service.getAll(val).done((data: any) => {
                    let lstData: Array<any> = data ? data.errorAlarmWorkRecordList : [];
                    self.ootsukaOption(data ? data.ootsukaOption : false);
                    if (lstData && lstData.length > 0) {
                        let sortedData: Array<any> = _.orderBy(lstData, ['code'], ['asc']);
                        self.lstFilteredData(sortedData);
                        if (!_.find(self.lstFilteredData(), {'code':self.codeToSelect()})) {
                            if (self.selectedErrorAlarmCode() == self.lstFilteredData()[0].code)
                                self.selectedErrorAlarmCode.valueHasMutated();
                            else 
                                self.selectedErrorAlarmCode(self.lstFilteredData()[0].code);
                        } else {
                            if (self.selectedErrorAlarmCode() == self.codeToSelect())
                                self.selectedErrorAlarmCode.valueHasMutated();
                            else 
                                self.selectedErrorAlarmCode(self.codeToSelect());
                        }
                        self.isNewMode(false);
                        self.selectedTab('tab-1');
                    } else {
                        if(val == 0){
                        self.setNewMode();
                        self.newTab()   
                        }
                        self.lstFilteredData([]);
                        self.selectedErrorAlarmCode(null);
                        self.reSetData(self.selectedErrorAlarm(), null);
                        self.isNewMode(true);
                        self.selectedTab('tab-1');
                    }
                    if (val == 1 && self.screenMode() == ScreenMode.Daily) {
                        self.updateTab();
                    }
                    nts.uk.ui.block.clear();
                });
            });
            setTimeout(function() {
            self.selectedErrorAlarmCode.subscribe((code) => {
                
                    nts.uk.ui.block.invisible();
                    if (code) {
                        self.codeToSelect(null);
                        let foundItem: ErrorAlarmWorkRecord = _.find(self.lstFilteredData(), (item) => {
                            return item.code == code;
                        });
                        if (foundItem) {
                            self.isNewMode(false);
                            self.changeSelectedErrorAlarm(foundItem);
                            if (self.screenMode() == ScreenMode.Daily && self.showTypeAtr() == 1) {
                                self.updateTab();
                            }
                            if (self.screenMode() == ScreenMode.Daily && self.showTypeAtr() == 0) {
                                self.newTab();
                            }
                            if (self.screenMode() == ScreenMode.Daily && self.selectedErrorAlarm().typeAtr() == '2') {
                                self.isAtdItemColor(false);
                            }
                        }

                        if (self.screenMode() == ScreenMode.Daily && self.isNewMode() == true) {
                            self.newTab();
                        }
                    }
                setTimeout(() => {
                    nts.uk.ui.block.clear();
                }, 500);
            });
                 },100);

            self.selectedErrorAlarm().typeAtr.subscribe((val: any) => {
                if (self.screenMode() == ScreenMode.Daily && self.selectedErrorAlarm().typeAtr() == '2') {
                    self.isAtdItemColor(false);
                } else {
                    self.isAtdItemColor(true);

                }
            });

            self.screenMode.subscribe((val) => {
                if (val == ScreenMode.Monthly) {
                    self.tabs()[0].visible(false);
                    self.tabs()[1].visible(false);
                    self.tabs()[2].visible(false);
                    self.tabs()[4].visible(false);

                } else if (val == ScreenMode.Daily) {
                    self.tabs()[0].visible(true);
                    self.tabs()[1].visible(true);
                    self.tabs()[2].visible(true);
                    self.tabs()[4].visible(true);

                }
                self.tabs.valueHasMutated();
            });
            self.screenMode.valueHasMutated();
        }

        changeSelectedErrorAlarm(foundItem: any) {
            let self = this;
            nts.uk.ui.errors.clearAll();
            if (self.screenMode() == ScreenMode.Daily) {
                self.reSetData(self.selectedErrorAlarm(), foundItem);
            } else if (self.screenMode() == ScreenMode.Monthly) {
                block.invisible();
                self.reSetData(self.selectedErrorAlarm(), foundItem);
                service.findMonthlyCondition(foundItem.errorAlarmCheckID, foundItem.code).done((data) => {
                    self.resetMonthlyConditon(self.selectedErrorAlarm(), data);
                }).always(() => {
                    block.clear();
                });
            }
            self.selectedTab('tab-1');
            $("#errorAlarmWorkRecordName").focus();
        }

        startPage(code: any): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            nts.uk.ui.block.grayout();
            if (self.screenMode() == ScreenMode.Daily) {
                self.sideBar(1);
                service.getAttendanceItemByCodes([833, 834, 835, 836, 837], self.screenMode()).done((lstItems: any) => {
                    if (lstItems && lstItems.length > 0) {
                        let lstItemCode = lstItems.map((item: any) => { return { code: item.attendanceItemId, name: item.attendanceItemName }; });
                        self.listRemarkColumnNo(lstItemCode);
                    }
                });
                service.getAll(self.showTypeAtr()).done((data: any) => {
                    let lstData: Array<any> = data ? data.errorAlarmWorkRecordList : [];
                    self.ootsukaOption(data ? data.ootsukaOption : false);
                    if (lstData && lstData.length > 0) {
                        let sortedData: Array<any> = _.orderBy(lstData, ['code'], ['asc']);
                        self.lstFilteredData(sortedData);
                        self.selectedErrorAlarmCode(code !== null ? code : sortedData[0].code);
                        self.isNewMode(false);
                        self.selectedTab('tab-1');
                    } else {
                        self.lstFilteredData([]);
                        self.selectedErrorAlarmCode(null);
                        self.reSetData(self.selectedErrorAlarm(), null);
                        self.isNewMode(true);
                        self.selectedTab('tab-1');
                    }
                    nts.uk.ui.block.clear();
                    dfd.resolve();
                });
            } else if (self.screenMode() == ScreenMode.Monthly) {
                $('#pg-name').text('KDW007A ' + getText("KDW007_41"));
                self.sideBar(2);
                service.getAllMonthlyCondition().done((lstData: Array<any>) => {
                    if (lstData && lstData.length > 0) {
                        let sortedData: Array<any> = _.orderBy(lstData, ['code'], ['asc']);
                        self.lstFilteredData(sortedData);
                        self.selectedErrorAlarmCode(code !== null ? code : sortedData[0].code);
                        self.isNewMode(false);
                    } else {
                        self.lstFilteredData([]);
                        self.selectedErrorAlarmCode(null);
                        self.reSetData(self.selectedErrorAlarm(), null);
                        self.isNewMode(true);
                    }

                    nts.uk.ui.block.clear();
                    dfd.resolve();
                });
            }
            return dfd.promise();
        }

        /* Function Area */

        jumpTo() {
            let self = this;
            nts.uk.request.jump("/view/kdw/006/a/index.xhtml");
        }

        setNewMode() {
            let self = this;
            self.selectedErrorAlarmCode(null);
            self.reSetData(self.selectedErrorAlarm(), null);
            self.isNewMode(true);
            self.selectedTab('tab-1');
            nts.uk.ui.errors.clearAll();
            $("#errorAlarmWorkRecordCode").focus();
            if (self.screenMode() == ScreenMode.Daily) {
                self.newTab();
            }
        }
        duplicate(){
            let self = this;
            self.isDuplicate(true);
            self.isNewMode(true);
            self.selectedErrorAlarmCode(null);
            self.selectedErrorAlarm().code("");
            self.selectedErrorAlarm().name("");
        }

        resetMonthlyConditon(selectedErrorAlarm: any, param: any) {
            selectedErrorAlarm.operatorBetweenPlanActual(param && param.operatorBetweenPlanActual ? param.operatorBetweenPlanActual : 0);
            selectedErrorAlarm.operatorBetweenGroups(param && param.operatorBetweenGroups ? param.operatorBetweenGroups : 0);
            selectedErrorAlarm.operatorGroup1(param && param.operatorGroup1 ? param.operatorGroup1 : 0);
            selectedErrorAlarm.operatorGroup2(param && param.operatorGroup2 ? param.operatorGroup2 : 0);
            selectedErrorAlarm.group2UseAtr(param && param.group2UseAtr ? param.group2UseAtr : false);
            selectedErrorAlarm.erAlAtdItemConditionGroup1.forEach((condition: any) => {
                if (param && param.erAlAtdItemConditionGroup1 && param.erAlAtdItemConditionGroup1.length > 0) {
                    param.erAlAtdItemConditionGroup1.forEach((conditionParam: any) => {
                        if (conditionParam.targetNO == condition.targetNO()) {
                            condition.setData(conditionParam.targetNO, conditionParam);
                        }
                    });
                } else {
                    condition.setData(condition.targetNO(), null);
                }
            });
            selectedErrorAlarm.erAlAtdItemConditionGroup2.forEach((condition) => {
                if (param && param.erAlAtdItemConditionGroup2 && param.erAlAtdItemConditionGroup2.length > 0) {
                    param.erAlAtdItemConditionGroup2.forEach((conditionParam) => {
                        if (conditionParam.targetNO == condition.targetNO()) {
                            condition.setData(conditionParam.targetNO, conditionParam);
                        }
                    });
                } else {
                    condition.setData(condition.targetNO(), null);
                }
            });
        }

        reSetData(selectedErrorAlarm: ErrorAlarmWorkRecord, param: any) {
            selectedErrorAlarm.companyId(param && param.companyId ? param.companyId : '');
            selectedErrorAlarm.errorAlarmCheckID(param && param.errorAlarmCheckID ? param.errorAlarmCheckID : '');
            selectedErrorAlarm.code(param && param.code ? param.code : '');
            selectedErrorAlarm.name(param && param.name ? param.name : '');
            selectedErrorAlarm.fixedAtr(param && param.fixedAtr ? param.fixedAtr : 0);
            selectedErrorAlarm.useAtr(param && nts.uk.ntsNumber.isNumber(param.useAtr, false) ? param.useAtr : 1);
            selectedErrorAlarm.remarkCancelErrorInput(param && param.remarkCancelErrorInput ? param.remarkCancelErrorInput : 0);
            selectedErrorAlarm.remarkColumnNo(param && param.remarkColumnNo ? param.remarkColumnNo : 833);
            selectedErrorAlarm.typeAtr(param && param.typeAtr ? param.typeAtr : 0);
            selectedErrorAlarm.displayMessage(param && param.displayMessage ? param.displayMessage : '');
            selectedErrorAlarm.boldAtr(param && param.boldAtr ? param.boldAtr : 0);
            selectedErrorAlarm.messageColor(param && param.messageColor ? param.messageColor : null);
            selectedErrorAlarm.cancelableAtr(param && param.cancelableAtr ? param.cancelableAtr : 0);
            selectedErrorAlarm.errorDisplayItem(param && param.errorDisplayItem ? param.errorDisplayItem : null);
            selectedErrorAlarm.errorDisplayItem.valueHasMutated();
            selectedErrorAlarm.errorDisplayItemName("");
            selectedErrorAlarm.alCheckTargetCondition.setData(param && param.alCheckTargetCondition ? param.alCheckTargetCondition : null);
            selectedErrorAlarm.workTypeCondition.setData(param && param.workTypeCondition ? param.workTypeCondition : null);
            selectedErrorAlarm.workTimeCondition.setData(param && param.workTimeCondition ? param.workTimeCondition : null);
            selectedErrorAlarm.operatorBetweenPlanActual(param && param.operatorBetweenPlanActual ? param.operatorBetweenPlanActual : 0);
            selectedErrorAlarm.lstApplicationTypeCode(param && param.lstApplicationTypeCode ? param.lstApplicationTypeCode : []);
            selectedErrorAlarm.operatorBetweenGroups(param && param.operatorBetweenGroups ? param.operatorBetweenGroups : 0);
            selectedErrorAlarm.operatorGroup1(param && param.operatorGroup1 ? param.operatorGroup1 : 0);
            selectedErrorAlarm.operatorGroup2(param && param.operatorGroup2 ? param.operatorGroup2 : 0);
            selectedErrorAlarm.group2UseAtr(param && param.group2UseAtr ? param.group2UseAtr : false);
            selectedErrorAlarm.erAlAtdItemConditionGroup1.forEach((condition) => {
                if (param && param.erAlAtdItemConditionGroup1 && param.erAlAtdItemConditionGroup1.length > 0) {
                    param.erAlAtdItemConditionGroup1.forEach((conditionParam) => {
                        if (conditionParam.targetNO == condition.targetNO()) {
                            condition.setData(conditionParam.targetNO, conditionParam);
                            condition.inputCheckCondition(conditionParam.inputCheckCondition);
                            if(conditionParam.conditionType == 2){
                                if(condition.inputCheckCondition() == 0){
                                    condition.displayLeftCompare(getText("KDW007_108"));
                                }else{
                                    condition.displayLeftCompare(getText("KDW007_107"));
                                }    
                            }
                        }
                    });
                } else {
                    condition.setData(condition.targetNO(), null);
                }
            });
            selectedErrorAlarm.erAlAtdItemConditionGroup2.forEach((condition) => {
                if (param && param.erAlAtdItemConditionGroup2 && param.erAlAtdItemConditionGroup2.length > 0) {
                    param.erAlAtdItemConditionGroup2.forEach((conditionParam) => {
                        if (conditionParam.targetNO == condition.targetNO()) {
                            condition.setData(conditionParam.targetNO, conditionParam);
                            condition.inputCheckCondition(conditionParam.inputCheckCondition);
                            if(conditionParam.conditionType == 2){
                                if(condition.inputCheckCondition() == 0){
                                    condition.displayLeftCompare(getText("KDW007_108"));
                                }else{
                                    condition.displayLeftCompare(getText("KDW007_107"));
                                }    
                            }
                        }
                    });
                } else {
                    condition.setData(condition.targetNO(), null);
                }
            });
        }
// duplicate Method
        // resetMonthlyConditon(selectedErrorAlarm, param) {
        //     selectedErrorAlarm.operatorBetweenPlanActual(param && param.operatorBetweenPlanActual ? param.operatorBetweenPlanActual : 0);
        //     selectedErrorAlarm.operatorBetweenGroups(param && param.operatorBetweenGroups ? param.operatorBetweenGroups : 0);
        //     selectedErrorAlarm.operatorGroup1(param && param.operatorGroup1 ? param.operatorGroup1 : 0);
        //     selectedErrorAlarm.operatorGroup2(param && param.operatorGroup2 ? param.operatorGroup2 : 0);
        //     selectedErrorAlarm.group2UseAtr(param && param.group2UseAtr ? param.group2UseAtr : false);
        //     selectedErrorAlarm.erAlAtdItemConditionGroup1.forEach((condition) => {
        //         if (param && param.erAlAtdItemConditionGroup1 && param.erAlAtdItemConditionGroup1.length > 0) {
        //             param.erAlAtdItemConditionGroup1.forEach((conditionParam) => {
        //                 if (conditionParam.targetNO == condition.targetNO()) {
        //                     condition.setData(conditionParam.targetNO, conditionParam);
        //                 }
        //             });
        //         } else {
        //             condition.setData(condition.targetNO(), null);
        //         }
        //     });
        //     selectedErrorAlarm.erAlAtdItemConditionGroup2.forEach((condition) => {
        //         if (param && param.erAlAtdItemConditionGroup2 && param.erAlAtdItemConditionGroup2.length > 0) {
        //             param.erAlAtdItemConditionGroup2.forEach((conditionParam) => {
        //                 if (conditionParam.targetNO == condition.targetNO()) {
        //                     condition.setData(conditionParam.targetNO, conditionParam);
        //                 }
        //             });
        //         } else {
        //             condition.setData(condition.targetNO(), null);
        //         }
        //     });
        // }

        newTab() {
            let self = this;
            self.tabs()[0].visible(true);
            self.tabs()[1].visible(true);
            self.tabs()[1].enable(true);
            self.tabs()[2].visible(true);
            self.tabs()[3].visible(true);
            self.tabs()[4].visible(true);
            self.isAtdItemColor(true);
        }

        updateTab() {
            let self = this;
            self.tabs()[0].visible(true);
            self.tabs()[1].visible(true);
            self.tabs()[1].enable(true);
            self.tabs()[2].visible(false);
            self.tabs()[3].visible(false);
            if (self.screenMode() == ScreenMode.Daily && self.selectedErrorAlarm().typeAtr == 2) {
                self.isAtdItemColor(false);
            }
        }

        update() {
            let self = this;
       


            
            $(".need-check").trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                let data = ko.mapping.toJS(self.selectedErrorAlarm());
                data.boldAtr = data.boldAtr ? 1 : 0;
                data.alCheckTargetCondition.filterByBusinessType = data.alCheckTargetCondition.filterByBusinessType ? 1 : 0;
                data.alCheckTargetCondition.filterByEmployment = data.alCheckTargetCondition.filterByEmployment ? 1 : 0;
                data.alCheckTargetCondition.filterByJobTitle = data.alCheckTargetCondition.filterByJobTitle ? 1 : 0;
                data.alCheckTargetCondition.filterByClassification = data.alCheckTargetCondition.filterByClassification ? 1 : 0;
                data.workTypeCondition.planFilterAtr = data.workTypeCondition.planFilterAtr ? 1 : 0;
                data.workTypeCondition.actualFilterAtr = data.workTypeCondition.actualFilterAtr ? 1 : 0;
                data.workTimeCondition.planFilterAtr = data.workTimeCondition.planFilterAtr ? 1 : 0;
                data.workTimeCondition.actualFilterAtr = data.workTimeCondition.actualFilterAtr ? 1 : 0;
                data.alCheckTargetCondition.lstBusinessType = _.values(data.alCheckTargetCondition.lstBusinessType ? data.alCheckTargetCondition.lstBusinessType : []);
                data.alCheckTargetCondition.lstJobTitle = _.values(data.alCheckTargetCondition.lstJobTitle ? data.alCheckTargetCondition.lstJobTitle : []);
                data.alCheckTargetCondition.lstEmployment = _.values(data.alCheckTargetCondition.lstEmployment ? data.alCheckTargetCondition.lstEmployment : []);
                data.alCheckTargetCondition.lstClassification = _.values(data.alCheckTargetCondition.lstClassification ? data.alCheckTargetCondition.lstClassification : []);
                data.workTypeCondition.planLstWorkType = _.values(data.workTypeCondition.planLstWorkType ? data.workTypeCondition.planLstWorkType : []);
                data.workTypeCondition.actualLstWorkType = _.values(data.workTypeCondition.actualLstWorkType ? data.workTypeCondition.actualLstWorkType : []);
                data.workTimeCondition.planLstWorkTime = _.values(data.workTimeCondition.planLstWorkTime ? data.workTimeCondition.planLstWorkTime : []);
                data.workTimeCondition.actualLstWorkTime = _.values(data.workTimeCondition.actualLstWorkTime ? data.workTimeCondition.actualLstWorkTime : []);
                data.lstApplicationTypeCode = _.values(data.lstApplicationTypeCode ? data.lstApplicationTypeCode : []);
                data.erAlAtdItemConditionGroup1 = _.values(data.erAlAtdItemConditionGroup1 ? data.erAlAtdItemConditionGroup1 : []);
                data.erAlAtdItemConditionGroup2 = _.values(data.erAlAtdItemConditionGroup2 ? data.erAlAtdItemConditionGroup2 : []);
                _.forEach(data.erAlAtdItemConditionGroup1, (item) => {
                    item.countableAddAtdItems = _.values(item.countableAddAtdItems);
                    item.countableSubAtdItems = _.values(item.countableSubAtdItems);
                });
                _.forEach(data.erAlAtdItemConditionGroup2, (item) => {
                    item.countableAddAtdItems = _.values(item.countableAddAtdItems);
                    item.countableSubAtdItems = _.values(item.countableSubAtdItems);
                });
                ko.utils.extend(data, {newMode: self.isNewMode() ? 1 : 0});
                if (self.screenMode() == ScreenMode.Daily) {
                    nts.uk.ui.block.invisible();
                    service.update(data).done(() => {
                        self.codeToSelect(data.code);
                        self.isDuplicate(false);
                        if (data.fixedAtr == 1)
                            self.showTypeAtr.valueHasMutated();
                        else {
                            if (self.showTypeAtr() == 0)
                                self.showTypeAtr.valueHasMutated();
                            else
                                self.showTypeAtr(0);
                        }
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            if (self.lstFilteredData().length > 0) {
                                $("#errorAlarmWorkRecordName").focus();
                            } else {
                                $("#errorAlarmWorkRecordCode").focus();
                            }
                        });
                    }).fail(err => {
                        nts.uk.ui.dialog.alert(err).then(() => {
                            $("#errorAlarmWorkRecordCode").focus();
                        });
                    });
                    nts.uk.ui.block.clear();
                } else if (self.screenMode() == ScreenMode.Monthly) {
                    nts.uk.ui.block.invisible();
                    service.updateMonthlyCondition(data).done(() => {
                        self.codeToSelect(data.code);
                        service.getAllMonthlyCondition().done((lstData: Array<any>) => {
                            if (lstData && lstData.length > 0) {
                                let sortedData: Array<any> = _.orderBy(lstData, ['code'], ['asc']);
                                self.lstFilteredData(sortedData);
                                self.selectedErrorAlarmCode.valueHasMutated();
                                self.isNewMode(false);
                            } else {
                                self.lstFilteredData([]);
                                self.selectedErrorAlarmCode(null);
                                self.reSetData(self.selectedErrorAlarm(), null);
                                self.isNewMode(true);
                            }
                            nts.uk.ui.block.clear();
                            nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                if (self.lstFilteredData().length > 0) {
                                    $("#errorAlarmWorkRecordName").focus();
                                } else {
                                    $("#errorAlarmWorkRecordCode").focus();
                                }
                            });
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alert(error);
                        }).always(() => {
                            block.clear();
                        });
                    }).fail(err => {
                        nts.uk.ui.dialog.alert(err).then(() => {
                            $("#errorAlarmWorkRecordCode").focus();
                        });
                    });
                    nts.uk.ui.block.clear();
                }
            }

        }

        remove() {
            let self = this;
            let data = self.selectedErrorAlarm().code();
            if (self.screenMode() == ScreenMode.Daily) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_618" }).ifYes(() => {
                    let idx = _.findIndex(self.lstFilteredData(), e => {return e.code == data});
                    if (self.lstFilteredData().length - 1 <= idx) {
                        if (self.lstFilteredData().length < 2)
                            self.codeToSelect(null);
                        else 
                            self.codeToSelect(self.lstFilteredData()[idx - 1].code);
                    } else {
                        self.codeToSelect(self.lstFilteredData()[idx + 1].code);
                    }
                    service.remove(data).done(() => {
                        if (self.showTypeAtr() == 0)
                            self.showTypeAtr.valueHasMutated();
                        else
                            self.showTypeAtr(0);
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                            if (self.lstFilteredData().length > 0) {
                                $("#errorAlarmWorkRecordName").focus();
                            } else {
                                $("#errorAlarmWorkRecordCode").focus();
                            }
                        });
                    });
                });
            } else if (self.screenMode() == ScreenMode.Monthly) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_618" }).ifYes(() => {
                    let idx = _.findIndex(self.lstFilteredData(), e => {return e.code == data});
                    if (self.lstFilteredData().length - 1 <= idx) {
                        if (self.lstFilteredData().length < 2)
                            self.codeToSelect(null);
                        else 
                            self.codeToSelect(self.lstFilteredData()[idx - 1].code);
                    } else {
                        self.codeToSelect(self.lstFilteredData()[idx + 1].code);
                    }
                    service.removeMonthlyCondition(data).done(() => {
                        service.getAllMonthlyCondition().done((lstData: Array<any>) => {
                            if (lstData && lstData.length > 0) {
                                let sortedData: Array<any> = _.orderBy(lstData, ['code'], ['asc']);
                                self.lstFilteredData(sortedData);
                                self.selectedErrorAlarmCode(self.codeToSelect() !== null ? self.codeToSelect() : sortedData[0].code);
                                self.isNewMode(false);
                            } else {
                                self.lstFilteredData([]);
                                self.selectedErrorAlarmCode(null);
                                self.reSetData(self.selectedErrorAlarm(), null);
                                self.isNewMode(true);
                            }
                            nts.uk.ui.block.clear();
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                                if (self.lstFilteredData().length > 0) {
                                    $("#errorAlarmWorkRecordName").focus();
                                } else {
                                    $("#errorAlarmWorkRecordCode").focus();
                                }
                            });
                        });
                    });
                });
            }
        }

        /* Tab 1 */
        openSelectAtdItemColorDialog() {
            let self = this;
            //Open dialog KDL021
            nts.uk.ui.block.invisible();
            service.getAllAttendanceItem().done((lstItem) => {
                nts.uk.ui.block.clear();
                let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                nts.uk.ui.windows.setShared('Multiple', false);
                // example wait
                nts.uk.ui.windows.setShared('AllAttendanceObj', lstItemCode);
                nts.uk.ui.windows.setShared('SelectedAttendanceId', [self.selectedErrorAlarm().errorDisplayItem()]);
                nts.uk.ui.windows.sub.modal("at", "/view/kdl/021/a/index.xhtml").onClosed(() => {
                    let output = nts.uk.ui.windows.getShared("selectedChildAttendace");
                    if (!nts.uk.util.isNullOrUndefined(output) && output !== "") {
                        self.selectedErrorAlarm().errorDisplayItem(parseInt(output));
                    } else if (!nts.uk.util.isNullOrUndefined(output) && output == "") {
                        self.selectedErrorAlarm().errorDisplayItem(null);
                    }
                });
            });
        }

        /* Tab 2 */
        chooseEmployment() {
            let self = this;
            setShared('CDL002Params', {
                isMultiple: true,
                selectedCodes: self.selectedErrorAlarm().alCheckTargetCondition.lstEmployment(),
                showNoSelection: false,
                isShowWorkClosure: false

            }, true);

            nts.uk.ui.windows.sub.modal("com", "/view/cdl/002/a/index.xhtml").onClosed(function() {
                var output = getShared('CDL002Output');
                if (output) {
                    output.sort();
                    self.selectedErrorAlarm().alCheckTargetCondition.lstEmployment(output);
                }
            });
        }

        chooseClassification() {
            let self = this;
            setShared('inputCDL003', {
                selectedCodes: self.selectedErrorAlarm().alCheckTargetCondition.lstClassification(),
                showNoSelection: false,
                isMultiple: true
            }, true);

            nts.uk.ui.windows.sub.modal("com", '/view/cdl/003/a/index.xhtml').onClosed(function(): any {
                var output = getShared('outputCDL003');
                if (output) {
                    output.sort();
                    self.selectedErrorAlarm().alCheckTargetCondition.lstClassification(output);
                }
            })
        }

        chooseJobTitle() {
            let self = this;
            nts.uk.ui.windows.setShared('inputCDL004', {
                baseDate: moment()._d,
                selectedCodes: self.selectedErrorAlarm().alCheckTargetCondition.lstJobTitle(),
                showNoSelection: false,
                isMultiple: true
            }, true);

            nts.uk.ui.windows.sub.modal("com", '/view/cdl/004/a/index.xhtml').onClosed(function(): any {
                let output = nts.uk.ui.windows.getShared('outputCDL004');
                if (output) {
                    output.sort();
                    self.selectedErrorAlarm().alCheckTargetCondition.lstJobTitle(output);
                }
            })
        }

        chooseBusinessType() {
            let self = this,
                param = {
                    codeList: self.selectedErrorAlarm().alCheckTargetCondition.lstBusinessType()
                };
            nts.uk.ui.windows.setShared("CDL024", param);
            nts.uk.ui.windows.sub.modal("com", "/view/cdl/024/index.xhtml").onClosed(() => {
                let output = getShared("currentCodeList");
                if (output) {
                    output.sort();
                    self.selectedErrorAlarm().alCheckTargetCondition.lstBusinessType(output);
                }
            });
        }
        /* End Tab 2 */
        /* Tab 3 */
        chooseWorkType(planOrActual) {
            service.getAllWorkType().done((lstWorkType: Array<WorkTypeDto>) => {
                let self = this;
                let workTypeCondition = self.selectedErrorAlarm().workTypeCondition;
                let lstSelectedCode = planOrActual === "plan" ? workTypeCondition.planLstWorkType() : workTypeCondition.actualLstWorkType();
                setShared('KDL002_Multiple', true);
                setShared('KDL002_AllItemObj', _.map(lstWorkType, (workType) => { return workType.workTypeCode; }));
                setShared('KDL002_SelectedItemId', lstSelectedCode);
                nts.uk.ui.windows.sub.modal('/view/kdl/002/a/index.xhtml', { title: '乖離時間の登録＞対象項目', }).onClosed(() => {
                    let results = getShared('KDL002_SelectedNewItem');
                    if (results) {
                        if (planOrActual === "plan") {
                            self.selectedErrorAlarm().workTypeCondition.planLstWorkType(results.map((result) => { return result.code; }));
                        } else {
                            self.selectedErrorAlarm().workTypeCondition.actualLstWorkType(results.map((result) => { return result.code; }));
                        }
                    }
                });
            });
        }

        chooseWorkTime(planOrActual) {
            let self = this;
            service.getAllWorkTime().done((lstWorkTime) => {
                let workTimeCondition = self.selectedErrorAlarm().workTimeCondition;
                let lstSelectedCode = planOrActual === "plan" ? workTimeCondition.planLstWorkTime() : workTimeCondition.actualLstWorkTime();
                setShared('kml001multiSelectMode', true);
                nts.uk.ui.windows.setShared('kml001selectAbleCodeList', lstWorkTime.map((worktime) => { return worktime.worktimeCode; }));
                nts.uk.ui.windows.setShared('kml001selectedCodeList', lstSelectedCode);
                nts.uk.ui.windows.sub.modal("/view/kdl/001/a/index.xhtml", { title: "割増項目の設定" }).onClosed(function() {
                    let results = getShared("kml001selectedCodeList");
                    if (results) {
                        if (planOrActual === "plan") {
                            if(results.sort()[0] == ""){
                                results.shift()
                                self.selectedErrorAlarm().workTimeCondition.planLstWorkTime(results.sort());
                            }else {
                                self.selectedErrorAlarm().workTimeCondition.planLstWorkTime(results.sort());
                            }
                        } else {
                            if(results.sort()[0] == ""){
                                results.shift()
                                self.selectedErrorAlarm().workTimeCondition.actualLstWorkTime(results.sort());
                            }else {
                            self.selectedErrorAlarm().workTimeCondition.actualLstWorkTime(results.sort());
                            }    
                        }
                    }
                });
            });
        }
        /* End Tab 3 */
        /* Tab 4 */
        openAtdItemConditionDialog() {
            let self = this,
                param = {
                };
            nts.uk.ui.windows.setShared("kdw007B", param);
            nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/b/index.xhtml").onClosed(() => {
                let output = getShared("kdw007BResult");
                if (output) {
                }
            });
        }
        /* End Tab 4 */
        
    }

    export class ErrorAlarmWorkRecord {
        errorAlarmCheckID: KnockoutObservable<string>;
        /* 会社ID */
        companyId: KnockoutObservable<string>;
        /* コード */
        code: KnockoutObservable<string>;
        /* 名称 */
        name: KnockoutObservable<string>;
        /* システム固定とする */
        fixedAtr: KnockoutObservable<number>;
        /* 使用する */
        useAtr: KnockoutObservable<number>;

        remarkCancelErrorInput: KnockoutObservable<number>;

        remarkColumnNo: KnockoutObservable<number>;
        /* 区分 */
        typeAtr: KnockoutObservable<number>;
        /* 表示メッセージ */
        displayMessage: KnockoutObservable<string>;
        /* メッセージを太字にする */
        boldAtr: KnockoutObservable<number>;
        /* メッセージの色 */
        messageColor: KnockoutObservable<string>;
        /* エラーアラームを解除できる */
        cancelableAtr: KnockoutObservable<number>;
        /* エラー表示項目 */
        errorDisplayItem: KnockoutObservable<number>;
        errorDisplayItemName: KnockoutObservable<string>;
        /* チェック条件 */
        alCheckTargetCondition: AlarmCheckTargetCondition;
        /* 勤務種類の条件*/
        workTypeCondition: WorkTypeCondition;
        /* 就業時間帯の条件*/
        workTimeCondition: WorkTimeCondition;
        operatorBetweenPlanActual: KnockoutObservable<number>;
        lstApplicationTypeCode: KnockoutObservableArray<number>;
        operatorBetweenGroups: KnockoutObservable<number>;
        operatorGroup1: KnockoutObservable<number>;
        operatorGroup2: KnockoutObservable<number>;
        group2UseAtr: KnockoutObservable<boolean>;
        erAlAtdItemConditionGroup1: Array<ErAlAtdItemCondition>;
        erAlAtdItemConditionGroup2: Array<ErAlAtdItemCondition>;
        screenMode: number;

        constructor(screenMode: number, param?: ErrorAlarmWorkRecord) {
            this.screenMode = screenMode;
            this.companyId = param && param.companyId ? ko.observable(param.companyId()) : ko.observable('');
            this.errorAlarmCheckID = param && param.errorAlarmCheckID ? ko.observable(param.errorAlarmCheckID()) : ko.observable('');
            this.code = param && param.code ? ko.observable(param.code()) : ko.observable('');
            this.name = param && param.name ? ko.observable(param.name()) : ko.observable('');
            this.fixedAtr = param && param.fixedAtr ? ko.observable(param.fixedAtr()) : ko.observable(0);
            this.useAtr = param && param.useAtr ? ko.observable(param.useAtr()) : ko.observable(1);
            this.remarkCancelErrorInput = param && param.remarkCancelErrorInput ? ko.observable(param.remarkCancelErrorInput()) : ko.observable(0);
            this.remarkColumnNo = param && param.remarkColumnNo ? ko.observable(param.remarkColumnNo()) : ko.observable(833);
            this.typeAtr = param && param.typeAtr ? ko.observable(param.typeAtr()) : ko.observable(0);
            this.displayMessage = param && param.displayMessage ? ko.observable(param.displayMessage()) : ko.observable('');
            this.boldAtr = param && param.boldAtr ? ko.observable(param.boldAtr()) : ko.observable(0);
            this.messageColor = param && param.messageColor ? ko.observable(param.messageColor()) : ko.observable('');
            this.cancelableAtr = param && param.cancelableAtr ? ko.observable(param.cancelableAtr()) : ko.observable(0);
            this.errorDisplayItem = param && param.errorDisplayItem ? ko.observable(param.errorDisplayItem) : ko.observable(null);
            this.errorDisplayItemName = ko.observable("");
            this.alCheckTargetCondition = param && param.alCheckTargetCondition ? new AlarmCheckTargetCondition(param.alCheckTargetCondition) : new AlarmCheckTargetCondition(null);
            this.workTypeCondition = param && param.workTypeCondition ? new WorkTypeCondition(param.workTypeCondition) : new WorkTypeCondition(null);
            this.workTimeCondition = param && param.workTimeCondition ? new WorkTimeCondition(param.workTimeCondition) : new WorkTimeCondition(null);
            this.operatorBetweenPlanActual = param && param.operatorBetweenPlanActual ? ko.observable(param.operatorBetweenPlanActual()) : ko.observable(0);
            this.lstApplicationTypeCode = param && param.lstApplicationTypeCode ? ko.observableArray(param.lstApplicationTypeCode()) : ko.observableArray([]);
            this.operatorBetweenGroups = param && param.operatorBetweenGroups ? ko.observable(param.operatorBetweenGroups()) : ko.observable(0);
            this.operatorGroup1 = param && param.operatorGroup1 ? ko.observable(param.operatorGroup1()) : ko.observable(0);
            this.operatorGroup2 = param && param.operatorGroup2 ? ko.observable(param.operatorGroup2()) : ko.observable(0);
            this.group2UseAtr = param && param.group2UseAtr ? ko.observable(param.group2UseAtr()) : ko.observable(false);
            this.erAlAtdItemConditionGroup1 = param && param.erAlAtdItemConditionGroup1 ? param.erAlAtdItemConditionGroup1.map((con) => { return new ErAlAtdItemCondition(con.targetNO, con, this.screenMode); }) : this.initListAtdItemCondition();
            this.erAlAtdItemConditionGroup2 = param && param.erAlAtdItemConditionGroup2 ? param.erAlAtdItemConditionGroup2.map((con) => { return new ErAlAtdItemCondition(con.targetNO, con, this.screenMode); }) : this.initListAtdItemCondition();
            this.errorDisplayItem.subscribe((itemCode) => {
                if (itemCode) {
                    service.getAttendanceItemByCodes([itemCode], this.screenMode).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            this.errorDisplayItemName(lstItems[0].attendanceItemName);
                        }
                    });
                } else {
                    this.errorDisplayItemName("");
                }
            });
            this.errorDisplayItem.valueHasMutated();
        }

        initListAtdItemCondition() {
            let resultList = [];
            resultList.push(new ErAlAtdItemCondition(0, null, this.screenMode));
            resultList.push(new ErAlAtdItemCondition(1, null, this.screenMode));
            resultList.push(new ErAlAtdItemCondition(2, null, this.screenMode));
            return resultList;
        }
    }

    export class AlarmCheckTargetCondition {
        /* 勤務種別でしぼり込む */
        filterByBusinessType: KnockoutObservable<boolean>;
        /* 職位でしぼり込む */
        filterByJobTitle: KnockoutObservable<boolean>;
        /* 雇用でしぼり込む */
        filterByEmployment: KnockoutObservable<boolean>;
        /* 分類でしぼり込む */
        filterByClassification: KnockoutObservable<boolean>;
        /* 対象勤務種別*/
        lstBusinessType: KnockoutObservableArray<string>;
        /* 対象職位 */
        lstJobTitle: KnockoutObservableArray<string>;
        /* 対象雇用*/
        lstEmployment: KnockoutObservableArray<string>;
        /* 対象分類 */
        lstClassification: KnockoutObservableArray<string>;
        displayLstBusinessType: KnockoutObservable<string>;
        displayLstJobTitle: KnockoutObservable<string>;
        displayLstEmployment: KnockoutObservable<string>;
        displayLstClassification: KnockoutObservable<string>;

        constructor(param) {
            this.filterByBusinessType = param ? ko.observable(param.filterByBusinessType) : ko.observable(false);
            this.filterByJobTitle = param ? ko.observable(param.filterByJobTitle) : ko.observable(false);
            this.filterByEmployment = param ? ko.observable(param.filterByEmployment) : ko.observable(false);
            this.filterByClassification = param ? ko.observable(param.filterByClassification) : ko.observable(false);
            this.lstBusinessType = param ? ko.observableArray(param.lstBusinessType) : ko.observableArray([]);
            this.lstJobTitle = param ? ko.observableArray(param.lstJobTitle) : ko.observableArray([]);
            this.lstEmployment = param ? ko.observableArray(param.lstEmployment) : ko.observableArray([]);
            this.lstClassification = param ? ko.observableArray(param.lstClassification) : ko.observableArray([]);
            this.filterByBusinessType.subscribe((val) => {
                if (!val) {
                    $("#displayLstBusinessType").trigger("validate");
                }
            });
            this.filterByJobTitle.subscribe((val) => {
                if (!val) {
                    $("#displayLstJobTitle").trigger("validate");
                }
            });
            this.filterByEmployment.subscribe((val) => {
                if (!val) {
                    $("#displayLstEmployment").trigger("validate");
                }
            });
            this.filterByClassification.subscribe((val) => {
                if (!val) {
                    $("#displayLstClassification").trigger("validate");
                }
            });
            this.displayLstBusinessType = ko.observable("");
            this.displayLstJobTitle = ko.observable("");
            this.displayLstEmployment = ko.observable("");
            this.displayLstClassification = ko.observable("");
            this.lstBusinessType.subscribe((lstBussinessType) => {
                if (lstBussinessType && lstBussinessType.length > 0) {
                    let displayText = "";
                    let lstItem = [];
                    let dfd = $.Deferred();
                    for (let i = 0; i < lstBussinessType.length; i++) {
                        service.getBusinessTypeByCode(lstBussinessType[i]).done((businessType) => {
                            if (businessType) {
                                lstItem.push({ order: lstBussinessType[i], text: businessType.businessTypeName });
                            } else {
                                lstItem.push({ order: lstBussinessType[i], text: lstBussinessType[i] + getText("KDW007_111") });  
                            }
                            if (lstItem.length == lstBussinessType.length) {
                                dfd.resolve();
                            }
                        });
                    }
                    dfd.done(() => {
                        lstItem = _.orderBy(lstItem, ['order'], ['asc']);
                        for (let i = 0; i < lstItem.length; i++) {
                            if (displayText !== "") {
                                displayText = displayText + " + " + lstItem[i].text;
                            } else {
                                displayText = displayText + lstItem[i].text;
                            }
                            if (i === lstItem.length - 1) {
                                this.displayLstBusinessType(displayText);
                                $("#displayLstBusinessType").trigger('validate');
                            }
                        }
                    });
                } else {
                    this.displayLstBusinessType("");
                }
            });
            this.lstJobTitle.subscribe((lstJobTitle) => {
                let displayText = "";
                let allJobTitle = [];
                if (lstJobTitle && lstJobTitle.length > 0) {
                    service.findAllJobTitle().done((data) => {
                        if (data && data.length > 0) {
                            allJobTitle = _.sortBy(data, 'code');
                        }
                    }).then(() => {
                        for (let jobTitle of allJobTitle) {
                            let result = _.find(lstJobTitle, (el) => {
                                return el === jobTitle.id;
                            })
                            if(result) {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + jobTitle.name;
                                } else {
                                    displayText = displayText + jobTitle.name;
                                }
                            }
                        }
                        this.displayLstJobTitle(displayText);
                        $("#displayLstJobTitle").trigger('validate');
                    });
                } else {
                    this.displayLstJobTitle("");
                }
            });
            this.lstEmployment.subscribe((lstEmpt) => {
                let displayText = "";
                if (lstEmpt && lstEmpt.length > 0) {
                    let lstItem = [];
                    let dfd = $.Deferred();
                    for (let i = 0; i < lstEmpt.length; i++) {
                        service.getEmploymentByCode(lstEmpt[i]).done((empt) => {
                            if (empt) {
                                lstItem.push({ order: lstEmpt[i], text: empt.name });
                            } else {
                                lstItem.push({ order: lstEmpt[i], text: lstEmpt[i] + getText("KDW007_111") });  
                            }
                            if (lstItem.length == lstEmpt.length) {
                                dfd.resolve();
                            }
                        });
                    }
                    dfd.done(() => {
                        lstItem = _.orderBy(lstItem, ['order'], ['asc']);
                        for (let i = 0; i < lstItem.length; i++) {
                            if (displayText !== "") {
                                displayText = displayText + " + " + lstItem[i].text;
                            } else {
                                displayText = displayText + lstItem[i].text;
                            }
                            if (i === lstItem.length - 1) {
                                this.displayLstEmployment(displayText);
                                $("#displayLstEmployment").trigger('validate');
                            }
                        }
                    });
                } else {
                    this.displayLstEmployment("");
                }
            });
            this.lstClassification.subscribe((lstClss) => {
                let displayText = "";
                if (lstClss && lstClss.length > 0) {
                    let lstItem = [];
                    let dfd = $.Deferred();
                    for (let i = 0; i < lstClss.length; i++) {
                        service.getClassificationByCode(lstClss[i]).done((clss) => {
                            if (clss) {
                                lstItem.push({ order: lstClss[i], text: clss.name });
                            } else {
                                lstItem.push({ order: lstClss[i], text: lstClss[i] + getText("KDW007_111") });  
                            }
                            if (lstItem.length == lstClss.length) {
                                dfd.resolve();
                            }
                        });
                    }
                    dfd.done(() => {
                        lstItem = _.orderBy(lstItem, ['order'], ['asc']);
                        for (let i = 0; i < lstItem.length; i++) {
                            if (displayText !== "") {
                                displayText = displayText + " + " + lstItem[i].text;
                            } else {
                                displayText = displayText + lstItem[i].text;
                            }
                            if (i === lstItem.length - 1) {
                                this.displayLstClassification(displayText);
                                $("#displayLstClassification").trigger('validate');
                            }
                        }
                    });
                } else {
                    this.displayLstClassification("");
                }
            });
            this.lstBusinessType.valueHasMutated();
            this.lstJobTitle.valueHasMutated();
            this.lstEmployment.valueHasMutated();
            this.lstClassification.valueHasMutated();
        }

        setData(param) {
            this.filterByBusinessType(param ? param.filterByBusinessType : false);
            this.filterByJobTitle(param ? param.filterByJobTitle : false);
            this.filterByEmployment(param ? param.filterByEmployment : false);
            this.filterByClassification(param ? param.filterByClassification : false);
            this.lstBusinessType(param ? param.lstBusinessType : []);
            this.lstJobTitle(param ? param.lstJobTitle : []);
            this.lstEmployment(param ? param.lstEmployment : []);
            this.lstClassification(param ? param.lstClassification : []);
        }
    }

    export class WorkTypeDto {
        workTypeCode: string;
        name: string;
    }

    export class WorkTypeCondition {

        useAtr: KnockoutObservable<boolean>;
        comparePlanAndActual: KnockoutObservable<number>;
        planFilterAtr: KnockoutObservable<boolean>;
        planLstWorkType: KnockoutObservableArray<string>;
        actualFilterAtr: KnockoutObservable<boolean>;
        actualLstWorkType: KnockoutObservableArray<string>;
        displayLstWorkTypePlan: KnockoutObservable<string>;
        displayLstWorkTypeActual: KnockoutObservable<string>;
        requireWorkTypeActual: any;

        constructor(param) {
            this.useAtr = param ? ko.observable(param.useAtr) : ko.observable(false);
            this.comparePlanAndActual = param ? ko.observable(param.comparePlanAndActual) : ko.observable(0);
            this.planFilterAtr = param ? ko.observable(param.planFilterAtr) : ko.observable(false);
            this.planLstWorkType = param ? ko.observable(param.planLstWorkType) : ko.observableArray([]);
            this.actualFilterAtr = param ? ko.observable(param.actualFilterAtr) : ko.observable(false);
            this.actualLstWorkType = param ? ko.observable(param.actualLstWorkType) : ko.observableArray([]);
            this.requireWorkTypeActual = ko.computed(() => {
                return this.comparePlanAndActual() != 1 && this.actualFilterAtr();
            });
            this.planFilterAtr.subscribe((val) => {
                if (!val) {
                    $("#displayLstWorkTypePlan").ntsError("clear");
                }
            });
            this.actualFilterAtr.subscribe((val) => {
                if (!val) {
                    $("#displayLstWorkTypeActual").ntsError("clear");
                }
            });
            this.displayLstWorkTypePlan = ko.observable("");
            this.displayLstWorkTypeActual = ko.observable("");
            this.planLstWorkType.subscribe((lstWorkTypeCode) => {
                if (lstWorkTypeCode && lstWorkTypeCode.length > 0) {
                    let displayText = "";
                    let lstWorkType = [];
                    service.getWorkTypeByListCode(lstWorkTypeCode).done((data) => {
                        if (data && data.length > 0) {
                            lstWorkType = data;
                        }
                    }).then(() => {
                        for (let i = 0; i < lstWorkTypeCode.length; i++) {
                            let workType = _.find(lstWorkType, ['workTypeCode', lstWorkTypeCode[i]]);
                            if (workType) {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + workType.name;
                                } else {
                                    displayText = displayText + workType.name;
                                }
                            } else {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + lstWorkTypeCode[i] + getText("KDW007_111");
                                } else {
                                    displayText = displayText + lstWorkTypeCode[i] + getText("KDW007_111");
                                }
                            }
                        }
                        this.displayLstWorkTypePlan(displayText);
                        $("#displayLstWorkTypePlan").ntsError("clear");
                    });
                } else {
                    this.displayLstWorkTypePlan("");
                }
            });
            this.actualLstWorkType.subscribe((lstWorkTypeCode) => {
                if (lstWorkTypeCode && lstWorkTypeCode.length > 0) {
                    let displayText = "";
                    let lstWorkType = [];
                    service.getWorkTypeByListCode(lstWorkTypeCode).done((data) => {
                        if (data && data.length > 0) {
                            lstWorkType = data;
                        }
                    }).then(() => {
                        for (let i = 0; i < lstWorkTypeCode.length; i++) {
                            let workType = _.find(lstWorkType, ['workTypeCode', lstWorkTypeCode[i]]);
                            if (workType) {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + workType.name;
                                } else {
                                    displayText = displayText + workType.name;
                                }
                            } else {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + lstWorkTypeCode[i] + getText("KDW007_111");
                                } else {
                                    displayText = displayText + lstWorkTypeCode[i] + getText("KDW007_111");
                                }
                            }
                        }
                        this.displayLstWorkTypeActual(displayText);
                        $("#displayLstWorkTypeActual").ntsError("clear");
                    });
                } else {
                    this.displayLstWorkTypeActual("");
                }
            });
            this.planLstWorkType.valueHasMutated();
            this.actualLstWorkType.valueHasMutated();
        }

        setData(param) {
            this.useAtr(param ? param.useAtr : false);
            this.comparePlanAndActual(param ? param.comparePlanAndActual : 0);
            this.planFilterAtr(param ? param.planFilterAtr : false);
            this.planLstWorkType(param ? param.planLstWorkType : []);
            this.actualFilterAtr(param ? param.actualFilterAtr : false);
            this.actualLstWorkType(param && param.actualLstWorkType ? param.actualLstWorkType : []);
        }
    }

    export class WorkTimeCondition {

        useAtr: KnockoutObservable<boolean>;
        comparePlanAndActual: KnockoutObservable<number>;
        planFilterAtr: KnockoutObservable<boolean>;
        planLstWorkTime: KnockoutObservableArray<string>;
        actualFilterAtr: KnockoutObservable<boolean>;
        actualLstWorkTime: KnockoutObservableArray<string>;
        displayLstWorkTimePlan: KnockoutObservable<string>;
        displayLstWorkTimeActual: KnockoutObservable<string>;
        requireWorkTimeActual: any;

        constructor(param) {
            this.useAtr = param ? ko.observable(param.useAtr) : ko.observable(false);
            this.comparePlanAndActual = param ? ko.observable(param.comparePlanAndActual) : ko.observable(0);
            this.planFilterAtr = param ? ko.observable(param.planFilterAtr) : ko.observable(false);
            this.planLstWorkTime = param ? ko.observable(param.planLstWorkTime) : ko.observableArray([]);
            this.actualFilterAtr = param ? ko.observable(param.actualFilterAtr) : ko.observable(false);
            this.actualLstWorkTime = param ? ko.observable(param.actualLstWorkTime) : ko.observableArray([]);
            this.requireWorkTimeActual = ko.computed(() => {
                return this.comparePlanAndActual() != 1 && this.actualFilterAtr();
            });
            this.planFilterAtr.subscribe((val) => {
                if (!val) {
                    $("#displayLstWorkTimePlan").ntsError("clear");
                }
            });
            this.actualFilterAtr.subscribe((val) => {
                if (!val) {
                    $("#displayLstWorkTimeActual").ntsError("clear");
                }
            });
            this.displayLstWorkTimePlan = ko.observable("");
            this.displayLstWorkTimeActual = ko.observable("");
            this.planLstWorkTime.subscribe((lstWorkTimeCode) => {
                if (lstWorkTimeCode && lstWorkTimeCode.length > 0) {
                    let displayText = "";
                    let lstWorkTime = [];
                    service.getAllWorkTime().done((data) => {
                        if (data && data.length > 0) {
                            lstWorkTime = data;
                        }
                    }).then(() => {
                        for (let i = 0; i < lstWorkTimeCode.length; i++) {
                            let workTime = _.find(lstWorkTime, ['worktimeCode', lstWorkTimeCode[i]]);
                            if (workTime) {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + workTime.workTimeName;
                                } else {
                                    displayText = displayText + workTime.workTimeName;
                                }
                            } else {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + lstWorkTimeCode[i] + getText("KDW007_111");
                                } else {
                                    displayText = displayText + lstWorkTimeCode[i] + getText("KDW007_111");
                                }
                            }
                        }
                        this.displayLstWorkTimePlan(displayText);
                        $("#displayLstWorkTimePlan").ntsError("clear");
                    });
                } else {
                    this.displayLstWorkTimePlan("");
                }
            });
            this.actualLstWorkTime.subscribe((lstWorkTimeCode) => {
                if (lstWorkTimeCode && lstWorkTimeCode.length > 0) {
                    let displayText = "";
                    let lstWorkTime = [];
                    service.getAllWorkTime().done((data) => {
                        if (data && data.length > 0) {
                            lstWorkTime = data;
                        }
                    }).then(() => {
                        for (let i = 0; i < lstWorkTimeCode.length; i++) {
                            let workTime = _.find(lstWorkTime, ['worktimeCode', lstWorkTimeCode[i]]);
                            if (workTime) {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + workTime.workTimeName;
                                } else {
                                    displayText = displayText + workTime.workTimeName;
                                }
                            } else {
                                if (displayText !== "") {
                                    displayText = displayText + " + " + lstWorkTimeCode[i] + getText("KDW007_111");
                                } else {
                                    displayText = displayText + lstWorkTimeCode[i] + getText("KDW007_111");
                                }
                            }
                        }
                        this.displayLstWorkTimeActual(displayText);
                        $("#displayLstWorkTimeActual").ntsError("clear");
                    });
                } else {
                    this.displayLstWorkTimeActual("");
                }
            });
            this.planLstWorkTime.valueHasMutated();
            this.actualLstWorkTime.valueHasMutated();
        }

        setData(param) {
            this.useAtr(param ? param.useAtr : false);
            this.comparePlanAndActual(param ? param.comparePlanAndActual : 0);
            this.planFilterAtr(param ? param.planFilterAtr : false);
            this.planLstWorkTime(param ? param.planLstWorkTime : []);
            this.actualFilterAtr(param ? param.actualFilterAtr : false);
            this.actualLstWorkTime(param && param.actualLstWorkTime ? param.actualLstWorkTime : []);
        }
    }

    export class ErAlAtdItemCondition {

        targetNO: KnockoutObservable<number>;
        conditionAtr: KnockoutObservable<number>;
        useAtr: KnockoutObservable<boolean>;
        inputCheckCondition: KnockoutObservable<number>;
        uncountableAtdItem: KnockoutObservable<number>;
        countableAddAtdItems: KnockoutObservableArray<number>;
        countableSubAtdItems: KnockoutObservableArray<number>;
        conditionType: KnockoutObservable<number>;
        compareOperator: KnockoutObservable<number>;
        singleAtdItem: KnockoutObservable<number>;
        compareStartValue: KnockoutObservable<number>;
        compareEndValue: KnockoutObservable<number>;

        displayLeftCompare: KnockoutObservable<string>;
        displayLeftOperator: KnockoutObservable<string>;
        displayTarget: KnockoutObservable<string>;
        displayRightCompare: KnockoutObservable<string>;
        displayRightOperator: KnockoutObservable<string>;
        screenMode: number;

        constructor(NO, param, screenMode: number) {
            let self = this;
            self.screenMode = screenMode;
            self.targetNO = ko.observable(NO);
            self.conditionAtr = param ? ko.observable(param.conditionAtr) : ko.observable(0);
            self.useAtr = param ? ko.observable(param.useAtr) : ko.observable(false);
            self.uncountableAtdItem = param ? ko.observable(param.uncountableAtdItem) : ko.observable(null);
            self.countableAddAtdItems = param && param.countableAddAtdItems ? ko.observableArray(param.countableAddAtdItems) : ko.observableArray([]);
            self.countableSubAtdItems = param && param.countableSubAtdItems ? ko.observableArray(param.countableSubAtdItems) : ko.observableArray([]);
            self.conditionType = param ? ko.observable(param.conditionType) : ko.observable(0);
            self.singleAtdItem = param ? ko.observable(param.singleAtdItem) : ko.observable(null);
            self.compareStartValue = param ? ko.observable(param.compareStartValue) : ko.observable(null);
            self.compareEndValue = param ? ko.observable(param.compareEndValue) : ko.observable(null);
            self.compareOperator = param ? ko.observable(param.compareOperator) : ko.observable(1);
            self.inputCheckCondition = param && param.inputCheckCondition ? ko.observable(param.inputCheckCondition) : ko.observable(0);
            self.displayLeftCompare = ko.observable("");
            self.displayLeftOperator = ko.observable("");
            self.displayTarget = ko.observable("");
            self.displayRightCompare = ko.observable("");
            self.displayRightOperator = ko.observable("");
            self.setTextDisplay();
        }

        setTextDisplay() {
            let self = this;
            if (self.useAtr()) {
                self.setDisplayTarget();
                self.setDisplayOperator();
                self.setDisplayCompare();
            } else {
                self.displayLeftCompare("");
                self.displayLeftOperator("");
                self.displayTarget("");
                self.displayRightCompare("");
                self.displayRightOperator("");
            }
        }

        setDisplayOperator() {
            let self = this;
            self.displayLeftOperator("");
            self.displayRightOperator("");
            switch (self.compareOperator()) {
                case 1:
                    self.displayLeftOperator("＝");
                    break;
                case 0:
                    self.displayLeftOperator("≠");
                    break;
                case 5:
                    self.displayLeftOperator("＞");
                    break;
                case 3:
                    self.displayLeftOperator("≧");
                    break;
                case 4:
                    self.displayLeftOperator("＜");
                    break;
                case 2:
                    self.displayLeftOperator("≦");
                    break;
                case 6:
                    self.displayLeftOperator("＜");
                    self.displayRightOperator("＜");
                    break;
                case 7:
                    self.displayLeftOperator("≦");
                    self.displayRightOperator("≦");
                    break;
                case 8:
                    self.displayLeftOperator("＜");
                    self.displayRightOperator("＜");
                    break;
                case 9:
                    self.displayLeftOperator("≦");
                    self.displayRightOperator("≦");
                    break;
            }
        }

        setDisplayCompare() {
            let self = this;
            let conditionAtr = self.conditionAtr();
            if (self.conditionType() < 2) {
                if (self.compareOperator() > 5) {
                    // Compare with a range
                    let rawStartValue = self.compareStartValue();
                    let rawEndValue = self.compareEndValue();
                    let textDisplayLeftCompare: string = (conditionAtr === 0 || conditionAtr === 3 || conditionAtr === 4) ? rawStartValue.toString() : nts.uk.time.parseTime(parseInt(rawStartValue.toString()), true).format();
                    let textDisplayRightCompare: string = (conditionAtr === 0 || conditionAtr === 3 || conditionAtr === 4) ? rawEndValue.toString() : nts.uk.time.parseTime(parseInt(rawEndValue.toString()), true).format();
                    self.displayLeftCompare(textDisplayLeftCompare);
                    self.displayRightCompare(textDisplayRightCompare);
                } else {
                    // Compare with single value
                    if (self.conditionType() === 0) {
                        // If is compare with a fixed value
                        let rawValue = self.compareStartValue();
                        let textDisplayLeftCompare = rawValue == null ? "" : (conditionAtr === 0 || conditionAtr === 3 || conditionAtr === 4) ? rawValue.toString() : nts.uk.time.parseTime(parseInt(rawValue.toString()), true).format();
                        self.displayLeftCompare(textDisplayLeftCompare);
                        self.displayRightCompare("");
                    } else {
                        // If is compare with a attendance item
                        if (self.singleAtdItem()) {
                            service.getAttendanceItemByCodes([self.singleAtdItem()], self.screenMode).done((lstItems) => {
                                if (lstItems && lstItems.length > 0) {
                                    self.displayLeftCompare(lstItems[0].attendanceItemName);
                                    self.displayRightCompare("");
                                }
                            });
                        }
                    }
                }
            } else {
                self.displayLeftCompare(self.inputCheckCondition() == 0 ? getText("KDW007_108") : getText("KDW007_107"));
            }
        }

        setDisplayTarget() {
            let self = this;
            self.displayTarget("");
            if (self.conditionAtr() === 2 || self.conditionType() === 2) {
                if (self.uncountableAtdItem()) {
                    service.getAttendanceItemByCodes([self.uncountableAtdItem()], self.screenMode).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            self.displayTarget(lstItems[0].attendanceItemName);
                        }
                    });
                }
            } else {
                if (self.countableAddAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(self.countableAddAtdItems(), self.screenMode).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " + ";
                                self.displayTarget(self.displayTarget() + lstItems[i].attendanceItemName + operator);
                            }
                        }
                    }).then(() => {
                        if (self.countableSubAtdItems().length > 0) {
                            service.getAttendanceItemByCodes(self.countableSubAtdItems(), self.screenMode).done((lstItems) => {
                                if (lstItems && lstItems.length > 0) {
                                    for (let i = 0; i < lstItems.length; i++) {
                                        let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                        let beforeOperator = (i === 0) ? " - " : "";
                                        self.displayTarget(self.displayTarget() + beforeOperator + lstItems[i].attendanceItemName + operator);
                                    }
                                }
                            })
                        }
                    });
                } else if (self.countableSubAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(self.countableSubAtdItems(), self.screenMode).done((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                let beforeOperator = (i === 0) ? " - " : "";
                                self.displayTarget(self.displayTarget() + beforeOperator + lstItems[i].attendanceItemName + operator);
                            }
                        }
                    })
                }

            }
        }

        openAtdItemConditionDialog(mode) {
            let self = this;
            let data = ko.mapping.toJS(this);
            nts.uk.ui.windows.setShared("KDW007BParams", { 'mode': mode, 'data': data }, true);
            nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/b/index.xhtml").onClosed(() => {
                let output = getShared("KDW007BResult");
                if (output) {
                    self.targetNO(output.targetNO);
                    self.conditionAtr(output.conditionAtr);
                    self.useAtr(true);
                    self.uncountableAtdItem(output.uncountableAtdItem);
                    self.countableAddAtdItems(output.countableAddAtdItems);
                    self.countableSubAtdItems(output.countableSubAtdItems);
                    self.conditionType(output.conditionType);
                    self.singleAtdItem(output.singleAtdItem);
                    self.compareStartValue(output.compareStartValue);
                    self.compareEndValue(output.compareEndValue);
                    self.compareOperator(output.compareOperator);
                    self.inputCheckCondition(output.inputCheckCondition);
                }
                self.setTextDisplay();
            });
        }

        clear() {
            let self = this;
            self.setData(self.targetNO(), null);
        }

        setData(NO, param) {
            let self = this;
            self.targetNO(NO);
            self.conditionAtr(param ? param.conditionAtr : 0);
            self.useAtr(param ? param.useAtr : false);
            self.uncountableAtdItem(param ? param.uncountableAtdItem : null);
            self.countableAddAtdItems(param && param.countableAddAtdItems ? param.countableAddAtdItems : []);
            self.countableSubAtdItems(param && param.countableSubAtdItems ? param.countableSubAtdItems : []);
            self.conditionType(param ? param.conditionType : 0);
            self.singleAtdItem(param ? param.singleAtdItem : null);
            self.compareStartValue(param && nts.uk.ntsNumber.isNumber(param.compareStartValue, true) ? param.compareStartValue : null);
            self.compareEndValue(param && nts.uk.ntsNumber.isNumber(param.compareEndValue, true) ? param.compareEndValue : null);
            self.compareOperator(param ? param.compareOperator : 1);
            self.setTextDisplay();
        }
    }

}
