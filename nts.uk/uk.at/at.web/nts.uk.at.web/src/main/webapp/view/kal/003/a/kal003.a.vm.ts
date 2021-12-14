/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kal003.a.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import model = kal003.share.model;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import tab = nts.uk.at.view.kal003.a.tab;
    import shareutils = nts.uk.at.view.kal003.share.kal003utils;

    export class ScreenModel extends ko.ViewModel {
        tabs: KnockoutObservableArray<any>;
        selectedTab: KnockoutObservable<string>;

        cbbItemList: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getListCategory());
        selectedCategory: KnockoutObservable<number> = ko.observable(model.CATEGORY.SCHEDULE_DAILY);

        radioItemList: KnockoutObservableArray<model.ItemModel> = ko.observableArray(model.getConditionToExtractDaily());
        selectedDataCondition: KnockoutObservable<number> = ko.observable(model.DATA_CONDITION_TO_EXTRACT.ALL);

        screenMode: KnockoutObservable<number> = ko.observable(model.SCREEN_MODE.NEW);

        listAlarmCheckCondition: KnockoutObservableArray<model.AlarmCheckConditionByCategory> = ko.observableArray([]);
        selectedAlarmCheckConditionCode: KnockoutObservable<string> = ko.observable('');
        selectedAlarmCheckCondition: KnockoutObservable<model.AlarmCheckConditionByCategory>;
        dupItem: KnockoutObservable<any> = ko.observable();

        //scope check tab
        tabScopeCheck: tab.ScopeCheckTab;
        //daily tab
        tabDailyErrorAlarm: tab.DailyPerformanceTab;
        //check condition tab
        tabCheckCondition: tab.CheckConditionTab;
        //tab fixed check condition
        tabFixedCondition: tab.FixedCheckConditionTab;

        // tab approval fixed check condition
        tabAppFixCondition: tab.AppFixedCheckConditionTab;

        tabAlarmcheck: tab.AlarmcheckTab;
        tabCheckAlarm: tab.CheckAlarmTab;
        tabAgreementError: tab.AgreementErrorTab;
        tabAgreementHour: tab.AgreementHourTab;
        tabAnnualHolidaySubCon: tab.AnnualHolidaySubCon;
        tabAnnualHolidayCon: tab.AnnualHolidayCon;
        tabMasterCheckFixedCon: tab.MasterCheckFixedConTab;

        selectCategoryFromDialog: KnockoutObservable<boolean> = ko.observable(false);
        afterDelete: KnockoutObservable<boolean> = ko.observable(false);

        tabScheduleCheckConditions: KnockoutObservable<any> = ko.observable(null);
        tabScheduleFixedCheckConditions: KnockoutObservable<any> = ko.observable(null);
        // 固有のチェック条件
        tabScheUniqueCheckCondition: tab.ScheUniqueCheckConditionTab;
        errAlFixedCheckId: KnockoutObservable<string> = ko.observable(null);
        errAlOptionalCheckId: KnockoutObservable<string> = ko.observable(null);

        constructor() {
            super();

            var self = this;

            self.tabs = ko.observableArray([
                { id: 'tab-1', title: getText('KAL003_15'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'tab-2', title: getText('KAL003_51'), content: '.tab-content-2', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY }, this) },
                { id: 'tab-3', title: getText('KAL003_16'), content: '.tab-content-3', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY || self.selectedCategory() == model.CATEGORY.SCHEDULE_4_WEEK || self.selectedCategory() == model.CATEGORY.MULTIPLE_MONTHS || self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY || self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY || self.selectedCategory() == model.CATEGORY.SCHEDULE_YEAR || self.selectedCategory() == model.CATEGORY.WEEKLY }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY || self.selectedCategory() == model.CATEGORY.SCHEDULE_4_WEEK || self.selectedCategory() == model.CATEGORY.MULTIPLE_MONTHS || self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY|| self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY || self.selectedCategory() == model.CATEGORY.SCHEDULE_YEAR || self.selectedCategory() == model.CATEGORY.WEEKLY }, this) },
                { id: 'tab-4', title: getText('KAL003_67'), content: '.tab-content-4', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY}, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.DAILY}, this) },
                { id: 'tab-5', title: getText('KAL003_67'), content: '.tab-content-5', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MONTHLY }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MONTHLY }, this) },
                { id: 'tab-6', title: 'アラームリストのチェック条件', content: '.tab-content-6', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MONTHLY }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MONTHLY }, this) },
                { id: 'tab-7', title: getText('KAL003_210'), content: '.tab-content-7', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY._36_AGREEMENT }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY._36_AGREEMENT }, this) },
                { id: 'tab-8', title: getText('KAL003_211'), content: '.tab-content-8', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY._36_AGREEMENT }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY._36_AGREEMENT }, this) },
                { id: 'tab-9', title: getText('KAL003_212'), content: '.tab-content-9', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS }, this) },
                { id: 'tab-10', title: getText('KAL003_213'), content: '.tab-content-10', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS }, this) },
                { id: 'tab-11', title: getText('KAL003_67'), content: '.tab-content-11', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MASTER_CHECK }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.MASTER_CHECK }, this) },
                { id: 'tab-12', title: getText('KAL003_67'), content: '.tab-content-12', enable: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.APPLICATION_APPROVAL }, this), visible: ko.computed(() => { return self.selectedCategory() == model.CATEGORY.APPLICATION_APPROVAL }, this) },

                //-----------------
                //02 スケジュール月次 - 3 tabs
                {
                    id: 'tab-13', title: getText('KAL003_16'), content: '.tab-content-13', enable: ko.computed(() => {
                        return false
                    }, this), visible: ko.computed(() => {
                        return false
                    }, this)
                },
                {
                    id: 'tab-14', title: getText('KAL003_67'), content: '.tab-content-14', enable: ko.computed(() => {
                        return false
                    }, this), visible: ko.computed(() => {
                        return false
                    }, this)
                },
                {
                    id: 'tab-15', title: getText('KAL003_67'), content: '.tab-content-15', enable: ko.computed(() => {
                        return self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY 
                            || self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY
                    }, this), visible: ko.computed(() => {
                        return self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY
                            || self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY
                    }, this)
                },
            ]);
            self.selectedTab = ko.observable('tab-1');

            self.tabScopeCheck = new tab.ScopeCheckTab();
            self.tabDailyErrorAlarm = new tab.DailyPerformanceTab();
            self.tabCheckCondition = new tab.CheckConditionTab(self.selectedCategory());
            self.tabFixedCondition = new tab.FixedCheckConditionTab();
            self.tabAppFixCondition = new tab.AppFixedCheckConditionTab();
            self.tabAlarmcheck = new tab.AlarmcheckTab();
            self.tabCheckAlarm = new tab.CheckAlarmTab();
            self.tabAgreementError = new tab.AgreementErrorTab(self.selectedCategory());
            self.tabAgreementHour = new tab.AgreementHourTab(self.selectedCategory());
            self.tabAnnualHolidaySubCon = new tab.AnnualHolidaySubCon();
            self.tabAnnualHolidayCon = new tab.AnnualHolidayCon();
            self.tabMasterCheckFixedCon = new tab.MasterCheckFixedConTab();
            self.tabScheUniqueCheckCondition = new tab.ScheUniqueCheckConditionTab();

            self.tabScheduleCheckConditions({
                categoryId: model.CATEGORY.SCHEDULE_DAILY,
                dataToSave: ko.observable(null)
            });

            self.selectedCategory.subscribe((data) => {
                self.switchCategory(data);
                self.tabScheUniqueCheckCondition.initData(data);
                self.checkCurrentTab();
            });

            self.selectedAlarmCheckCondition = ko.observable(new model.AlarmCheckConditionByCategory('', '', new model.ItemModel(0, ""), [], new model.AlarmCheckTargetCondition(false, false, false, false, [], [], [], [])));

            self.selectedAlarmCheckConditionCode.subscribe(function(data: any) {
                self.selectCondition(data);
            });

            self.tabAnnualHolidaySubCon.narrowUntilNext.subscribe(function(data: any) {
                //               if (data == true) {
                //                   $("#check-sub-period").trigger("validate");
                //               } else {
                //                   $("#check-sub-period").ntsError("clear");
                //               }
            });

            self.tabAnnualHolidaySubCon.narrowLastDay.subscribe(function(data: any) {
                //               if (data == true) {
                //                   $("#check-sub-last").trigger("validate");
                //               } else {
                //                   $("#check-sub-last").ntsError("clear");
                //               }
            });

        }

        startPage(code?: string): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            block.invisible();
            self.listAlarmCheckCondition.removeAll();

            service.getAllData(self.selectedCategory()).done(function(data: Array<any>) {

                if (data && data.length) {
                    let _accList: Array<model.AlarmCheckConditionByCategory> = _.map(data, acc => {
                        let category = _.find(ko.toJS(self.cbbItemList), (x: model.ItemModel) => x.code == acc.category);
                        let item = new model.AlarmCheckConditionByCategory(acc.code, acc.name, category, [], null);
                        return item;
                    });

                    _.each(_.sortBy(_accList, item => { return item.displayCode }), acc => self.listAlarmCheckCondition.push(acc));
                    if (self.selectCategoryFromDialog()) {
                        self.createNewAlarmCheckCondition();
                    } else {
                        if (code) {
                            self.selectedAlarmCheckConditionCode(code);
                        } else {
                            self.selectedAlarmCheckConditionCode(self.listAlarmCheckCondition()[0].code());
                        }
                        self.selectedAlarmCheckConditionCode.valueHasMutated();
                    }

                } else {
                    self.createNewAlarmCheckCondition();
                }
                dfd.resolve();
            }).fail(function(error) {
                alertError(error);
                dfd.reject();
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }

        private createNewAlarmCheckCondition() {
            let self = this;
            nts.uk.ui.errors.clearAll();
            self.selectedAlarmCheckConditionCode('');
            let category = _.find(ko.toJS(self.cbbItemList), (x: model.ItemModel) => x.code == self.selectedCategory());
            self.selectedAlarmCheckCondition(new model.AlarmCheckConditionByCategory('', '', category, [], new model.AlarmCheckTargetCondition(false, false, false, false, [], [], [], [])));
            self.selectedDataCondition(model.DATA_CONDITION_TO_EXTRACT.ALL);
            self.tabScopeCheck.targetCondition(new model.AlarmCheckTargetCondition(false, false, false, false, [], [], [], []));
            self.tabCheckCondition.category(self.selectedCategory());

            if (self.selectedCategory() == model.CATEGORY.DAILY) {
                self.tabCheckCondition.listWorkRecordExtractingConditions([]);
                self.tabDailyErrorAlarm.currentCodeList([]);
                self.tabDailyErrorAlarm.addApplication(false);

                service.getAllFixedConData().done((data: Array<any>) => {
                    if (data && data.length) {
                        let _list: Array<model.FixedConditionWorkRecord> = _.map(data, acc => {
                            return new model.FixedConditionWorkRecord({ dailyAlarmConID: "", checkName: acc.fixConWorkRecordName, fixConWorkRecordNo: acc.fixConWorkRecordNo, message: acc.message, useAtr: false, eralarmAtr: acc.eralarmAtr });
                        });
                        self.tabFixedCondition.listFixedConditionWorkRecord(_list);
                    }
                });
            }

            if (self.selectedCategory() == model.CATEGORY.APPLICATION_APPROVAL) {
                service.getAllFixedApprovalItem().done((data: Array<any>) => {
                    if (data && data.length) {
                        let _list: Array<model.ApprovalFixedConditionWorkRecord> = _.map(data, acc => {
                            return new model.ApprovalFixedConditionWorkRecord({ appAlarmConId: "", name: acc.name, no: acc.no, displayMessage: acc.displayMessage, useAtr: false, erAlAtr: acc.erAlAtr });
                        });
                        self.tabAppFixCondition.listFixedConditionWorkRecord(_list);
                    }
                });
            }

            if (self.selectedCategory() == model.CATEGORY.SCHEDULE_4_WEEK) {
                self.tabCheckCondition.schedule4WeekCheckCondition(0);
            }

            if (self.selectedCategory() == model.CATEGORY.MONTHLY) {
                service.getAllFixedExtraItemMon().done((data: Array<any>) => {
                    if (data && data.length) {
                        let _list: Array<model.FixedExtraMonFun> = _.map(data, acc => {
                            return new model.FixedExtraMonFun({ monAlarmCheckID: "", monAlarmCheckName: acc.fixedExtraItemMonName, fixedExtraItemMonNo: acc.fixedExtraItemMonNo, message: acc.message, useAtr: false });
                        });
                        // 
                        let orderList = _.orderBy(_list, ['sortBy'], ['asc']);
                        self.tabAlarmcheck.listFixedExtraMonFun(_list);
                    }
                });
                self.tabCheckAlarm.listExtraResultMonthly([]);
            }

            if (self.selectedCategory() == model.CATEGORY._36_AGREEMENT) {
                self.tabAgreementHour.listAgreementHour([]);
                //                self.tabAgreementError.listAgreementError([]);
                let listName = [];
                let i = 0;
                service.getName().done((data: Array<any>) => {
                    _.forEach(data, value => {
                        let temp = {
                            category: self.selectedCategory(),
                            code: self.selectedAlarmCheckConditionCode(),
                            id: i + 1,
                            useAtr: 0,
                            period: value.period,
                            errorAlarm: value.errorAlarm,
                            messageDisp: null,
                            name: value.name,
                        }
                        let conError = new model.AgreeConditionErrorDto(temp);
                        listName.push(conError);
                    });
                    self.tabAgreementError.listAgreementError(_.orderBy(listName, ['errorAlarm', 'period'], ['asc', 'asc']));
                    i = 0;
                });
            }
            // MinhVV add
            if (self.selectedCategory() == model.CATEGORY.MULTIPLE_MONTHS) {
                self.tabCheckCondition.listMulMonCheckSet([]);
            }

            if (self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS) {
                self.tabAnnualHolidayCon.loadData();
                self.tabAnnualHolidaySubCon.loadData();
            }

            if (self.selectedCategory() == model.CATEGORY.MASTER_CHECK) {
                service.getAllFixedMasterCheckItem().done((data: Array<any>) => {
                    if (data && data.length) {
                        let _list: Array<model.MasterCheckFixedCon> = _.map(data, acc => {
                            return new model.MasterCheckFixedCon({ errorAlarmCheckId: "", name: acc.name, no: acc.no, message: acc.message, useAtr: false, erAlAtr: acc.erAlAtr });
                        });
                        self.tabMasterCheckFixedCon.listFixedMasterCheckCondition(_list);
                    }
                });
            }
            
            if (self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY || self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY) {
                self.tabCheckCondition.listWorkRecordExtractingConditions([]);
                self.tabScheUniqueCheckCondition.setListFixedConditionWorkRecord([]);
            }
            
            if (self.selectedCategory() == model.CATEGORY.SCHEDULE_YEAR || self.selectedCategory() == model.CATEGORY.WEEKLY) {
                self.tabCheckCondition.listWorkRecordExtractingConditions([]);
            }

            //ver 2

            /*
            SCHEDULE_DAILY = 0,//スケジュール日次
            SCHEDULE_WEEKLY = 1, //週次            
            SCHEDULE_MONTHLY = 3,//スケジュール月次                        
            SCHEDULE_YEAR = 4, //スケジュール年間 */
            /* switch (self.selectedCategory()) {
                //スケジュール日次
                case model.CATEGORY.SCHEDULE_DAILY:
                    self.tabScheduleCheckConditions({
                        categoryId: model.CATEGORY.SCHEDULE_DAILY,
                        dataToSave: ko.observable(111)
                    });

                    break;
                //週次
                case model.CATEGORY.SCHEDULE_WEEKLY:
                    //self.tabScheduleWeekly;
                    break;
                //スケジュール月次   
                case model.CATEGORY.SCHEDULE_MONTHLY:
                    //self.tabScheduleMonthly;
                    break;
                //スケジュール年間
                case model.CATEGORY.SCHEDULE_YEAR:
                    //self.tabScheduleYear;
                    break;
            } */


            self.screenMode(model.SCREEN_MODE.NEW);
            if (self.afterDelete()) {
                self.afterDelete(false);
            } else {
                $("#A3_2").focus();
            }

        }

        duplicateAlarmCheckCondition() {
            let self = this;
            self.selectedAlarmCheckConditionCode('');
            let result = ko.toJS(self.dupItem);
            let category = _.find(ko.toJS(self.cbbItemList), (x: model.ItemModel) => x.code == result.category);
            let item = new model.AlarmCheckConditionByCategory(
                '',
                '',
                category,
                result.availableRoles,
                new model.AlarmCheckTargetCondition(
                    result.targetCondition.filterByEmployment,
                    result.targetCondition.filterByClassification,
                    result.targetCondition.filterByJobTitle,
                    result.targetCondition.filterByBusinessType,
                    result.targetCondition.targetEmployment,
                    result.targetCondition.targetClassification,
                    result.targetCondition.targetJobTitle,
                    result.targetCondition.targetBusinessType));
            self.selectedAlarmCheckCondition(item);
            self.tabScopeCheck.targetCondition(item.targetCondition());
            self.screenMode(model.SCREEN_MODE.NEW);
            $('#A3_2').focus();
        }

        registerAlarmCheckCondition() {
            let self = this,
                data: model.AlarmCheckConditionByCategory = new model.AlarmCheckConditionByCategory(self.selectedAlarmCheckCondition().code(),
                    self.selectedAlarmCheckCondition().name(),
                    new model.ItemModel(self.selectedAlarmCheckCondition().category(),
                        self.selectedAlarmCheckCondition().displayCategory),
                    self.selectedAlarmCheckCondition().availableRoles(),
                    self.selectedAlarmCheckCondition().targetCondition());
            // validate selected scopes check
            $('[data-bind="with: tabScopeCheck"] .nts-input').trigger('validate');

            if ($('[data-bind="with: tabScopeCheck"] .nts-input').ntsError("hasError")) {
                return;
            }

            if (data.category() == model.CATEGORY.DAILY) {
                $(".nameAlarmDailyM").trigger("validate");
                $("#A3_2").trigger("validate");
                $("#A3_4").trigger("validate");
                $(".fixedcheckID").ntsError("clear");
                $("#check-condition-table .nts-editor.nts-input").trigger("validate");
                if ($(".nameAlarmDailyM").ntsError("hasError")) {
                    return;
                }
                if ($("#A3_2").ntsError("hasError") || $("#A3_4").ntsError("hasError")) {
                    return;
                }
                //$(".nameWKRecordIDDaily").ntsError("clear");
            } else if (data.category() == model.CATEGORY.SCHEDULE_4_WEEK) {
                $("#A3_2").trigger("validate");
                $("#A3_4").trigger("validate");
                $(".nameWKRecordIDDaily").ntsError("clear");
                $(".fixedcheckID").ntsError("clear");
                if ($("#A3_2").ntsError("hasError") || $("#A3_4").ntsError("hasError")) {
                    return;
                }
            } else if (data.category() == model.CATEGORY.MONTHLY) {
                //fixed-table2
                $(".nameAlarm").trigger("validate");
                $(".fixedcheckID").ntsError("clear");
                $("#check-condition-table .nts-editor.nts-input").ntsError("clear");
                $("#A3_2").trigger("validate");
                $("#A3_4").trigger("validate");
                if ($(".nameAlarm").ntsError("hasError")) {
                    return;
                }
                if ($("#A3_2").ntsError("hasError") || $("#A3_4").ntsError("hasError")) {
                    return;
                }
            } else if (data.category() == model.CATEGORY.MULTIPLE_MONTHS) {
                $(".nameWKRecordID").trigger("validate");
                $(".fixedcheckID").ntsError("clear");
                $("#check-condition-table_category9 .nts-editor.nts-input").ntsError("clear");
                $("#A3_2").trigger("validate");
                $("#A3_4").trigger("validate");
                if ($(".nameWKRecordID").ntsError("hasError")) {
                    return;
                }
                if ($("#A3_2").ntsError("hasError") || $("#A3_4").ntsError("hasError")) {
                    return;
                }
            } else if (data.category() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS) {
                $("#con_usage_obli_day").trigger("validate");
                $("#A3_2").trigger("validate");
                $("#A3_4").trigger("validate");
                if ($("#con_usage_obli_day").ntsError("hasError") || $("#A3_2").ntsError("hasError") || $("#A3_4").ntsError("hasError")) {
                    return;
                }
            } else if (data.category() == model.CATEGORY.SCHEDULE_DAILY || data.category() == model.CATEGORY.SCHEDULE_MONTHLY || data.category() == model.CATEGORY.SCHEDULE_YEAR || data.category() == model.CATEGORY.WEEKLY) {
                $("#check-condition-table .nts-editor.nts-input").trigger("validate");
                if ($(".nameAlarmDailyM").ntsError("hasError")) {
                    return;
                }
            }

            //block.invisible();
            data.targetCondition(self.tabScopeCheck.targetCondition());
            data.action(self.screenMode());
            if (data.category() == model.CATEGORY.DAILY) {
                data.dailyAlarmCheckCondition().conditionToExtractDaily(self.selectedDataCondition());
                data.dailyAlarmCheckCondition().addApplication(self.tabDailyErrorAlarm.addApplication());
                data.dailyAlarmCheckCondition().listErrorAlarmCode(self.tabDailyErrorAlarm.currentCodeList());
                self.tabCheckCondition.listWorkRecordExtractingConditions().forEach((x: model.WorkRecordExtractingCondition) => {
                    if (_.isEmpty(x.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon())) {
                        let e: model.ErAlAtdItemCondition = shareutils.getDefaultCondition(0);
                        e.compareStartValue(0);
                        x.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon([e]);
                    }
                });
                data.dailyAlarmCheckCondition().listExtractConditionWorkRecork(self.tabCheckCondition.listWorkRecordExtractingConditions());
                data.dailyAlarmCheckCondition().listFixedExtractConditionWorkRecord(self.tabFixedCondition.listFixedConditionWorkRecord());
                data.dailyAlarmCheckCondition().listExtractConditionWorkRecork().forEach((x: model.WorkRecordExtractingCondition) => {
                    x.errorAlarmCondition().alCheckTargetCondition().filterByBusinessType = data.targetCondition().filterByBusinessType();
                    x.errorAlarmCondition().alCheckTargetCondition().filterByJobTitle = data.targetCondition().filterByJobTitle();
                    x.errorAlarmCondition().alCheckTargetCondition().filterByEmployment = data.targetCondition().filterByEmployment();
                    x.errorAlarmCondition().alCheckTargetCondition().filterByClassification = data.targetCondition().filterByClassification();
                    x.errorAlarmCondition().alCheckTargetCondition().lstBusinessTypeCode = data.targetCondition().targetBusinessType();
                    x.errorAlarmCondition().alCheckTargetCondition().lstJobTitleId = data.targetCondition().targetJobTitle();
                    x.errorAlarmCondition().alCheckTargetCondition().lstEmploymentCode = data.targetCondition().targetEmployment();
                    x.errorAlarmCondition().alCheckTargetCondition().lstClassificationCode = data.targetCondition().targetClassification();
                    x.sortOrderBy = x.rowId;
                });

            }

            if (data.category() == model.CATEGORY.APPLICATION_APPROVAL) {
                data.approvalAlarmCheckConDto().listFixedExtractConditionWorkRecord(self.tabAppFixCondition.listFixedConditionWorkRecord());
            }

            if (data.category() == model.CATEGORY.SCHEDULE_4_WEEK) {
                data.schedule4WeekAlarmCheckCondition().schedule4WeekCheckCondition(self.tabCheckCondition.schedule4WeekCheckCondition());
            }

            if (data.category() == model.CATEGORY.MONTHLY) {
                let i = -1;
                data.monAlarmCheckCon().listFixExtraMon(self.tabAlarmcheck.listFixedExtraMonFun());
                data.monAlarmCheckCon().arbExtraCon(
                    _.map(self.tabCheckAlarm.listExtraResultMonthly(), acc => {
                        i++;
                        return shareutils.convertTransferDataToExtraResultMonthly(acc, i + 1);
                    }));
            }

            if (data.category() == model.CATEGORY._36_AGREEMENT) {
                data.condAgree36().listCondOt(self.tabAgreementHour.listAgreementHour());
                data.condAgree36().listCondError(self.tabAgreementError.listAgreementError());
                _.each(data.condAgree36().listCondOt(), function(item) {
                    item.category = self.selectedAlarmCheckCondition().category();
                    item.code = self.selectedAlarmCheckCondition().code();
                });
                _.each(data.condAgree36().listCondError(), function(obj) {
                    obj.category = self.selectedAlarmCheckCondition().category();
                    obj.code = self.selectedAlarmCheckCondition().code();
                    obj.useAtr(+obj.useAtr());
                });

            }
            // MinhVV add
            if (self.selectedCategory() == model.CATEGORY.MULTIPLE_MONTHS) {
                self.tabCheckCondition.listMulMonCheckSet().forEach((x: model.MulMonCheckCondSet) => {
                    if (_.isNil(x.erAlAtdItem())) {
                        let e: model.ErAlAtdItemCondition = shareutils.getDefaultCondition(x.rowId());
                        e.compareStartValue(0);
                        e.compareEndValue(0);
                        x.erAlAtdItem(e);
                    }
                });
                data.mulMonCheckCond().listMulMonCheckConds(self.tabCheckCondition.listMulMonCheckSet());
            }

            if (self.selectedCategory() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS) {
                data.annualHolidayAlCon().alarmCheckSubConAgr(self.tabAnnualHolidaySubCon == null ? null : self.tabAnnualHolidaySubCon);
                data.annualHolidayAlCon().alarmCheckConAgr(self.tabAnnualHolidayCon == null ? null : self.tabAnnualHolidayCon);
            }

            if (self.selectedCategory() == model.CATEGORY.MASTER_CHECK) {
                data.masterCheckAlarmCheckCondition().listFixedMasterCheckCondition(self.tabMasterCheckFixedCon.listFixedMasterCheckCondition());
            }
            
            // schedule
            if (self.selectedCategory() == model.CATEGORY.SCHEDULE_DAILY || self.selectedCategory() == model.CATEGORY.SCHEDULE_MONTHLY || self.selectedCategory() == model.CATEGORY.SCHEDULE_YEAR) {
                data.scheFixCondDay().erAlCheckLinkId(self.errAlFixedCheckId());
                data.scheAnyCondDay().erAlCheckLinkId(self.errAlOptionalCheckId());
                data.scheFixCondDay().sheFixItemDays(self.tabScheUniqueCheckCondition.listFixedConditionWorkRecord());
                data.scheAnyCondDay().scheAnyCondDays(self.tabCheckCondition.listWorkRecordExtractingConditions());
            }
            
            if (self.selectedCategory() == model.CATEGORY.WEEKLY) {
                data.scheAnyCondDay().erAlCheckLinkId(self.errAlOptionalCheckId());
                data.scheAnyCondDay().scheAnyCondDays(self.tabCheckCondition.listWorkRecordExtractingConditions());
            }

            let command: any = ko.toJS(data);
            $("#A3_4").trigger("validate");
            if ($("#A3_4").ntsError("hasError")) {
                return;
            }

            block.invisible();
            service.registerData(command).done(function() {
                self.startPage(data.code()).done(() => {
                    info({ messageId: "Msg_15" }).then(() => {
                        if (self.screenMode() == nts.uk.at.view.kal003.share.model.SCREEN_MODE.UPDATE) {
                            $("#A3_4").focus();
                        } else {
                            $("#A3_2").focus();
                        }
                    });
                });
            }).fail(error => {
                nts.uk.ui.dialog.error({ messageId: error.messageId }).then(() => {
                    $("#A3_2").focus();
                });
            }).always(() => {
                block.clear();
            });

        }

        deleteAlarmCheckCondition() {
            let self = this;
            let data: model.AlarmCheckConditionByCategory = self.selectedAlarmCheckCondition();
            nts.uk.ui.errors.clearAll();
            let command: any = ko.toJS(data);
            if (command.dailyAlarmCheckCondition) {
                if (command.dailyAlarmCheckCondition.listExtractConditionWorkRecork && command.dailyAlarmCheckCondition.listExtractConditionWorkRecork.length > 0) {
                    for (let i = 0; i < command.dailyAlarmCheckCondition.listExtractConditionWorkRecork.length; i++) {
                        if (command.dailyAlarmCheckCondition.listExtractConditionWorkRecork[i].errorAlarmCheckID == '') {
                            command.dailyAlarmCheckCondition.listExtractConditionWorkRecork.splice(i, 1);
                        };
                    }

                }
            }

            confirm({ messageId: "Msg_18" }).ifYes(() => {
                block.invisible();
                let indexItemDelete = _.findIndex(self.listAlarmCheckCondition(), (item: model.AlarmCheckConditionByCategory) => { return item.code() == data.code(); });
                service.deleteData(command).done(function() {
                    self.startPage().done(() => {
                        self.listAlarmCheckCondition.remove(function(item) { return item.code() == data.code(); });
                        if (self.listAlarmCheckCondition().length == 0) {
                            self.afterDelete(true);
                            self.createNewAlarmCheckCondition();
                        } else {
                            if (indexItemDelete == self.listAlarmCheckCondition().length) {
                                self.selectedAlarmCheckConditionCode(self.listAlarmCheckCondition()[indexItemDelete - 1].code());
                            } else {
                                self.selectedAlarmCheckConditionCode(self.listAlarmCheckCondition()[indexItemDelete].code());
                            }
                        }
                        info({ messageId: "Msg_16" }).then(() => {
                            if (self.screenMode() == nts.uk.at.view.kal003.share.model.SCREEN_MODE.UPDATE) {
                                $("#A3_4").focus();
                            } else {
                                $("#A3_2").focus();
                            }
                        });
                    });
                }).fail(error => {
                    alertError(error);
                }).always(() => {
                    block.clear();
                });

            }).ifNo(() => {
            });
        }

        openKAL003dDialog() {
            let self = this;
            nts.uk.ui.errors.clearAll();

            modal("/view/kal/003/d/index.xhtml").onClosed(() => {

                var output = getShared("outputKAL003d");
                if (!nts.uk.util.isNullOrUndefined(output)) {
                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY._36_AGREEMENT) {
                        self.tabAgreementHour.listAgreementHour([]);
                    }
                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY.MONTHLY) {
                        self.tabCheckAlarm.listExtraResultMonthly([]);
                        self.tabAlarmcheck.listFixedExtraMonFun([]);
                    }
                    // MinhVV add
                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY.MULTIPLE_MONTHS) {
                        self.tabCheckCondition.listMulMonCheckSet([]);
                    }

                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS) {
                        self.tabAnnualHolidayCon.loadData();
                        self.tabAnnualHolidaySubCon.loadData();
                    }

                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY.MASTER_CHECK) {
                        self.tabMasterCheckFixedCon.listFixedMasterCheckCondition([]);
                    }

                    if (self.selectedAlarmCheckCondition().category() == model.CATEGORY.APPLICATION_APPROVAL) {
                        self.tabAppFixCondition.listFixedConditionWorkRecord([]);
                    }
                    
                    // reset form
                    if (output == model.CATEGORY.SCHEDULE_DAILY || output == model.CATEGORY.SCHEDULE_MONTHLY || output == model.CATEGORY.SCHEDULE_YEAR) {
                        self.tabCheckCondition.listWorkRecordExtractingConditions([]);
                        self.tabScheUniqueCheckCondition.setListFixedConditionWorkRecord([]);
                    }

                    self.selectCategoryFromDialog(true);
                    if (self.selectedCategory() != output)
                        self.selectedCategory(output);
                    else
                        self.selectedCategory.valueHasMutated();
                }
                _.defer(function() { return nts.uk.ui.errors.clearAll(); });
            });
        }

        private switchCategory(category: number) {
            let self = this;
            self.tabCheckCondition.category(category);

            self.tabScheduleCheckConditions({
                categoryId: category,
                dataToSave: ko.observable(null)
            });

            self.tabScheduleFixedCheckConditions({
                categoryId: category,
                dataToSave: ko.observable(null)
            });

            self.startPage().done(() => {
                self.selectCategoryFromDialog(false);
            });
        }

        private selectCondition(data) {
            let self = this;
            if (data) {
                block.invisible();
                service.getOneData(self.selectedCategory(), data).done(function(result: any) {
                    if (result) {
                        self.dupItem(result);
                        let category = _.find(ko.toJS(self.cbbItemList), (x: model.ItemModel) => x.code == result.category);
                        let item = new model.AlarmCheckConditionByCategory(
                            result.code,
                            result.name,
                            category,
                            result.availableRoles,
                            new model.AlarmCheckTargetCondition(
                                result.targetCondition.filterByEmployment,
                                result.targetCondition.filterByClassification,
                                result.targetCondition.filterByJobTitle,
                                result.targetCondition.filterByBusinessType,
                                result.targetCondition.targetEmployment,
                                result.targetCondition.targetClassification,
                                result.targetCondition.targetJobTitle,
                                result.targetCondition.targetBusinessType));
                        let _fixedList: Array<model.FixedConditionWorkRecord> = _.map(result.dailyAlarmCheckCondition.listFixedExtractConditionWorkRecord, (fix: model.IFixedConditionWorkRecord) => { return new model.FixedConditionWorkRecord(fix); });
                        let _appFixedList: Array<model.ApprovalFixedConditionWorkRecord> = _.map(result.approvalAlarmCheckConDto.listFixedExtractConditionWorkRecord, (fix: model.IAppFixedConditionWorkRecord) => { return new model.ApprovalFixedConditionWorkRecord(fix) });
                        let _checkList: Array<model.WorkRecordExtractingCondition> = _.map(result.dailyAlarmCheckCondition.listExtractConditionWorkRecork, (c: model.IWorkRecordExtractingCondition) => { return shareutils.convertTransferDataToWorkRecordExtractingCondition(c); });
                        item.dailyAlarmCheckCondition(
                            new model.DailyAlarmCheckCondition(
                                result.dailyAlarmCheckCondition.conditionToExtractDaily,
                                result.dailyAlarmCheckCondition.addApplication,
                                result.dailyAlarmCheckCondition.listErrorAlarmCode,
                                _checkList,
                                _fixedList));
                        item.approvalAlarmCheckConDto(new model.ApprovalAlarmCheckConditon(_appFixedList));
                        item.schedule4WeekAlarmCheckCondition().schedule4WeekCheckCondition(result.schedule4WeekCondition);
                        item.condAgree36(
                            new model.Agreement36(
                                result.condAgree36.listCondOt,
                                result.condAgree36.listCondError));
                        item.annualHolidayAlCon(new model.AnnualHolidayAlarmCondition(result.annualHolidayAlConDto.alCheckSubConAgrDto, result.annualHolidayAlConDto.alCheckConAgrDto));
                        let _listFixExtraMon: Array<model.FixedExtraMonFun> = _.map(result.monAlarmCheckConDto.listFixExtraMon, acc => {
                            return new model.FixedExtraMonFun(acc);
                        });
                        item.monAlarmCheckCon().listFixExtraMon(_listFixExtraMon);

                        let _listExtraMon: Array<model.ExtraResultMonthly> = _.map(result.monAlarmCheckConDto.arbExtraCon, acc => {
                            return shareutils.getDefaultExtraResultMonthly(acc);
                        });
                        item.monAlarmCheckCon().arbExtraCon(_listExtraMon);
                        let _masterCheckFixedList: Array<model.MasterCheckFixedCon> = _.map(result.masterCheckAlarmCheckConDto.listMasterCheckFixedCon, (fix: model.IMasterCheckFixedCon) => { return new model.MasterCheckFixedCon(fix); });
                        item.masterCheckAlarmCheckCondition(
                            new model.MasterCheckCondition(_masterCheckFixedList)
                        );

                        self.selectedAlarmCheckCondition(item);
                        self.tabScopeCheck.targetCondition(item.targetCondition());
                        if (item.category() == model.CATEGORY.SCHEDULE_4_WEEK) {
                            self.tabCheckCondition.schedule4WeekCheckCondition(item.schedule4WeekAlarmCheckCondition().schedule4WeekCheckCondition());
                        }

                        if (item.category() == model.CATEGORY.DAILY) {
                            self.tabCheckCondition.listWorkRecordExtractingConditions(item.dailyAlarmCheckCondition().listExtractConditionWorkRecork());
                            self.selectedDataCondition(item.dailyAlarmCheckCondition().conditionToExtractDaily());
                            self.tabFixedCondition.listFixedConditionWorkRecord(item.dailyAlarmCheckCondition().listFixedExtractConditionWorkRecord());
                            self.tabDailyErrorAlarm.currentCodeList(item.dailyAlarmCheckCondition().listErrorAlarmCode());
                            self.tabDailyErrorAlarm.addApplication(item.dailyAlarmCheckCondition().addApplication());
                        }

                        if (item.category() == model.CATEGORY.APPLICATION_APPROVAL) {
                            self.tabAppFixCondition.listFixedConditionWorkRecord(item.approvalAlarmCheckConDto().listFixedExtractConditionWorkRecord());
                        }

                        if (item.category() == model.CATEGORY._36_AGREEMENT) {
                            let listAgreementErrorKnockout = _.map(item.condAgree36().listCondError(), x => {
                                return new model.AgreeConditionErrorDto(x);
                            });
                            let listAgreementHourKnockout = _.map(item.condAgree36().listCondOt(), y => {
                                return new model.AgreeCondOt(y);
                            });

                            //                            self.tabAgreementError.listAgreementError(listAgreementErrorKnockout);
                            self.tabAgreementError.listAgreementError(_.orderBy(listAgreementErrorKnockout, ['errorAlarm', 'period'], ['asc', 'asc']));
                            self.tabAgreementHour.listAgreementHour(_.sortBy(listAgreementHourKnockout, ['no']));
                        }

                        if (item.category() == model.CATEGORY.MONTHLY) {
                            //tab extraResult
                            let list = ko.toJS(item.monAlarmCheckCon().arbExtraCon());
                            let orderList = _.orderBy(list, ['sortBy'], ['asc']);
                            let listNew = [];
                            for (let i = 0; i < orderList.length; i++) {
                                listNew.push(ko.mapping.fromJS(orderList[i]));
                            }
                            self.tabCheckAlarm.listExtraResultMonthly(listNew);
                            //tab fix
                            if (item.monAlarmCheckCon().listFixExtraMon().length > 0) {
                                self.tabAlarmcheck.listFixedExtraMonFun(item.monAlarmCheckCon().listFixExtraMon());
                            }
                        }

                        if (item.category() == model.CATEGORY.ATTENDANCE_RATE_FOR_ANNUAL_HOLIDAYS) {
                            self.tabAnnualHolidaySubCon.loadData(item.annualHolidayAlCon().alarmCheckSubConAgr());
                            self.tabAnnualHolidayCon.loadData(item.annualHolidayAlCon().alarmCheckConAgr());
                        }

                        let _listMulmonCheckCond: Array<model.MulMonCheckCond> = _.map(result.mulMonAlarmCheckConDto.arbExtraCon, (mm: model.IMulMonCheckCond) => { return shareutils.convertTransferDataToMulMonCheckCondSet(mm); });
                        // MinhVV add
                        if (item.category() == model.CATEGORY.MULTIPLE_MONTHS) {
                            item.mulMonCheckCond().listMulMonCheckConds(_listMulmonCheckCond);
                            self.tabCheckCondition.listMulMonCheckSet(item.mulMonCheckCond().listMulMonCheckConds());
                        }

                        if (item.category() == model.CATEGORY.MASTER_CHECK) {
                            self.tabMasterCheckFixedCon.listFixedMasterCheckCondition(item.masterCheckAlarmCheckCondition().listFixedMasterCheckCondition());
                        }
                        
                        if (item.category() == model.CATEGORY.SCHEDULE_DAILY || item.category() == model.CATEGORY.SCHEDULE_MONTHLY || item.category() == model.CATEGORY.SCHEDULE_YEAR || item.category() == model.CATEGORY.WEEKLY) {
                            // tab2
                            let _checkList: Array<model.WorkRecordExtractingCondition> = _.map(result.scheAnyCondDay.scheAnyItemDays, (c: model.IWorkRecordExtractingCondition) => { return shareutils.convertTransferDataToWorkRecordExtractingCondition(c); });
                            self.tabCheckCondition.listWorkRecordExtractingConditions(_checkList);
                            self.errAlOptionalCheckId(result.scheAnyCondDay.erAlCheckLinkId);
                            item.scheAnyCondDay().scheAnyCondDays(_checkList);
                            item.scheAnyCondDay().erAlCheckLinkId(result.scheAnyCondDay.erAlCheckLinkId);
                            
                            // tab3
                            if (result.scheFixCondDay) {
                                self.errAlFixedCheckId(result.scheFixCondDay.erAlCheckLinkId);
                                item.scheFixCondDay().sheFixItemDays(result.scheFixCondDay.sheFixItemDays);
                                item.scheFixCondDay().erAlCheckLinkId(result.scheFixCondDay.erAlCheckLinkId);
                                self.tabScheUniqueCheckCondition.setListFixedConditionWorkRecord(item.scheFixCondDay().sheFixItemDays());
                            }
                            
                            self.selectedAlarmCheckCondition(item);
                        }

                        self.screenMode(model.SCREEN_MODE.UPDATE);
                        //                        $("#A3_4").focus();
                        setTimeout(function() { $("#A3_4").focus(); }, 500);
                    }
                }).fail(function(error) {

                    alertError(error);
                }).always(() => {
                    nts.uk.ui.errors.clearAll();
                    block.clear();
                });
            } else {
                
            }
        }


        /**
         * Print file excel
         */
        exportExcel(): void {
            var self = this;
            nts.uk.ui.block.grayout();
            service.saveAsExcel().done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }

        checkParentValueFromSub() {
            const vm = this;

            //console.log(vm.tabScheduleCheckConditions());
            //console.log(vm.tabScheduleFixedCheckConditions());
        }

        private checkCurrentTab() {
          const vm = this;
          const currentTab = _.find(vm.tabs(), { 'id': vm.selectedTab() });
          if (!currentTab.enable()) {
            vm.selectedTab('tab-1');
          }
        }
    }
}












