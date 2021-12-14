module nts.uk.at.view.kal003.b.viewmodel {
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    import dialog = nts.uk.ui.dialog;
    import windows = nts.uk.ui.windows;
    import resource = nts.uk.resource;
    import sharemodel = nts.uk.at.view.kal003.share.model;
    import shareutils = nts.uk.at.view.kal003.share.kal003utils;
    import NumberEditorOption = nts.uk.ui.option.NumberEditorOption;


    export class ScreenModel {
        workRecordExtractingCondition: KnockoutObservable<sharemodel.WorkRecordExtractingCondition>;
        // list item check
        listTypeCheckWorkRecords: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listSingleValueCompareTypes: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listRangeCompareTypes: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listCompareTypes: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        itemListTargetServiceType_BA1_2: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        itemListTargetSelectionRange_BA1_5: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listCheckTimeType: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listTimeZoneTargetRange: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listTypeOfContrast: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listTypeOfDays: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listTypeOfTime: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listTypeOfVacations: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        listAllWorkType: Array<string> = ([]);
        listAllAttdItem: Array<string> = ([]);
        listAllWorkingTime: Array<string> = ([]);

        displayWorkTypeSelections_BA1_4: KnockoutObservable<string> = ko.observable('');
        displayAttendanceItemSelections_BA2_3: KnockoutObservable<string> = ko.observable('');
        displayWorkingTimeSelections_BA5_3: KnockoutObservable<string> = ko.observable('');
        required_BA1_4: KnockoutObservable<boolean>;

        private setting: sharemodel.WorkRecordExtractingCondition;

        swANDOR_B5_3: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        swANDOR_B6_3: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        swANDOR_B7_2: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        enableComparisonMaxValue: KnockoutObservable<boolean> = ko.observable(false);
        comparisonRange: KnockoutObservable<model.ComparisonValueRange>;
        checkItemTemp: KnockoutObservable<number> = ko.observable(null);

        category: KnockoutObservable<number> = ko.observable(0);
        //monthly
        listEnumRoleType: KnockoutObservableArray<any>;
        listTypeCheckVacation: KnockoutObservableArray<any>;
        listSpecialholidayframe: Array<any> = ([]);
        private settingExtraMon: sharemodel.ExtraResultMonthly;
        extraResultMonthly: KnockoutObservable<sharemodel.ExtraResultMonthly>;

        // list item check Multiple Months MinhVV
        listTypeCheckWorkRecordMultipleMonths: KnockoutObservableArray<model.EnumModel> = ko.observableArray([]);
        modeScreen: KnockoutObservable<number> = ko.observable(0);
        mulMonCondExtra: KnockoutObservable<sharemodel.MulMonCheckCondExtra>;
        //MinhVV 
        mulMonCheckCondSet: KnockoutObservable<sharemodel.MulMonCheckCondSet>;
        private setting: sharemodel.MulMonCheckCondSet;
        
        isPattern: KnockoutObservable<boolean> = ko.observable(false);
        isPatternA: KnockoutObservable<boolean> = ko.observable(false);
        isPatternB: KnockoutObservable<boolean> = ko.observable(false);
        isPatternD: KnockoutObservable<boolean> = ko.observable(false);
        isPatternE: KnockoutObservable<boolean> = ko.observable(false);
        isPatternG: KnockoutObservable<boolean> = ko.observable(false);
        isPatternForContinuousWork: KnockoutObservable<boolean> = ko.observable(false);
        isPatterForContinuousTimeZone: KnockoutObservable<boolean> = ko.observable(false);
        isPatternForRemainingNumber: KnockoutObservable<boolean> = ko.observable(false);
        
        isTimeEditor: KnockoutObservable<boolean> = ko.observable(false);
        isNumberEditor: KnockoutObservable<boolean> = ko.observable(false);
        isDayEditor: KnockoutObservable<boolean> = ko.observable(false);
        
        constraint: KnockoutObservable<string> = ko.observable("");
        numberEditorOption: KnockoutObservable<NumberEditorOption> = ko.observable(new NumberEditorOption({
            numberGroup: true,
            decimallength: 2,
            placeholder: "",
            width: "",
            textalign: "left"
        }));
        
        scheCheckTypeCondition: KnockoutObservable<number> = ko.observable(0);
        
        constructor(isDoNothing) {
            let self = this;
            let option = windows.getShared('inputKal003b');
            if (isDoNothing) {
                return;
            }
            self.category(option.category);
            switch (self.category()) {
                case sharemodel.CATEGORY.DAILY: {
                    self.setting = $.extend({}, shareutils.getDefaultWorkRecordExtractingCondition(0), option.data);

                    let workRecordExtractingCond = shareutils.convertTransferDataToWorkRecordExtractingCondition(self.setting);
                    self.workRecordExtractingCondition = ko.observable(workRecordExtractingCond);
                    // setting comparison value range

                    self.comparisonRange = ko.observable(self.initComparisonValueRange());

                    self.checkItemTemp = ko.observable(self.workRecordExtractingCondition().checkItem());

                    // change select item check
                    self.workRecordExtractingCondition().checkItem.subscribe((itemCheck) => {
                        errors.clearAll();
                        //fix bug 100145
                        self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().planLstWorkType([]);
                        self.comparisonRange().minAmountOfMoneyValue(null);
                        self.comparisonRange().maxAmountOfMoneyValue(null);
                        self.comparisonRange().minTimeValue(null);
                        self.comparisonRange().maxTimeValue(null);
                        self.comparisonRange().minTimesValue(null);
                        self.comparisonRange().maxTimesValue(null);
                        self.comparisonRange().maxTimeWithinDayValue(null);
                        self.comparisonRange().minTimeWithinDayValue(null);
                        if ((itemCheck && itemCheck != undefined) || itemCheck === 0) {
                            self.initialScreen().then(function() {
                                self.settingEnableComparisonMaxValueField(false);
                                if ((self.checkItemTemp() || self.checkItemTemp() == 0) && self.checkItemTemp() != itemCheck) {
                                    setTimeout(function() { self.displayAttendanceItemSelections_BA2_3(""); }, 200);

                                }
                            });
                        }
                        $(".nts-input").ntsError("clear");
                    });
                    self.comparisonRange().comparisonOperator.subscribe((operN) => {
                        self.settingEnableComparisonMaxValueField(false);
                        if (self.comparisonRange().comparisonOperator() > 5) {
                            $(".nts-input").ntsError("clear");
                            if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
                                setTimeout(() => {
                                    if (parseInt(self.comparisonRange().minValue()) > parseInt(self.comparisonRange().maxValue())) {
                                        $('#endValue').ntsError('set', { messageId: "Msg_836" });
                                    }
                                }, 25);
                            }
                            if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
                                setTimeout(() => {
                                    if (parseInt(self.comparisonRange().minValue()) >= parseInt(self.comparisonRange().maxValue())) {
                                        $('#endValue').ntsError('set', { messageId: "Msg_836" });
                                    }
                                }, 25);
                            }
                        } else {
                            $(".nts-input").ntsError("clear");
                        }
                    });
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual = ko.observable(self.setting.errorAlarmCondition.workTypeCondition.comparePlanAndActual);
                    self.required_BA1_4 = ko.observable(self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual() > 0);
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual.subscribe((newV) => {
                        self.required_BA1_4(newV > 0);
                        $(".nts-input").ntsError("clear");
                    }); 
                    break;
                }
                case sharemodel.CATEGORY.MONTHLY: {
                    self.modeScreen(1);
                    //monthly
                    self.listEnumRoleType = ko.observableArray(__viewContext.enums.TypeMonCheckItem);
//                    self.listTypeCheckVacation = ko.observableArray(__viewContext.enums.TypeCheckVacation);
                    self.listTypeCheckVacation = ko.observableArray([
                        new sharemodel.ItemModel(0, resource.getText('KAL003_112')),
                        new sharemodel.ItemModel(1, resource.getText('KAL003_113')),
                        new sharemodel.ItemModel(2, resource.getText('KAL003_114')),
                        new sharemodel.ItemModel(3, resource.getText('KAL003_115')),
                        new sharemodel.ItemModel(6, resource.getText('KAL003_118'))
                    ]);
                                        
                    self.extraResultMonthly = ko.observable(sharemodel.ExtraResultMonthly.clone(option.data));
                    break;
                }
                //MinhVV add
                case sharemodel.CATEGORY.MULTIPLE_MONTHS: {
                    self.setting = $.extend({}, shareutils.getDefaultMulMonCheckCondSet(0), option.data);
                    let mulMonCheckCondSet = shareutils.convertTransferDataToMulMonCheckCondSet(self.setting);
                    self.mulMonCheckCondSet = ko.observable(mulMonCheckCondSet);
                    // setting comparison value range

                    self.comparisonRange = ko.observable(self.initComparisonValueRangeMulMon());
                    self.checkItemTemp = ko.observable(self.mulMonCheckCondSet().typeCheckItem());

                    // change select item check
                    self.mulMonCheckCondSet().typeCheckItem.subscribe((itemCheck) => {
                        errors.clearAll();
                        // fix bug 100050 save data
                        self.mulMonCheckCondSet().erAlAtdItem().countableAddAtdItems([])
                        self.mulMonCheckCondSet().erAlAtdItem().countableSubAtdItems([]);

                        // fix khoi tao khi typecheck thay doi
                        self.comparisonRange().minAmountOfMoneyValue(null);
                        self.comparisonRange().maxAmountOfMoneyValue(null);
                        self.comparisonRange().minTimeValue(null);
                        self.comparisonRange().maxTimeValue(null);
                        self.comparisonRange().minTimesValue(null);
                        self.comparisonRange().maxTimesValue(null);
                        //日数
                        self.comparisonRange().minTimesValueDay(null);
                        self.comparisonRange().maxTimesValueDay(null);
                        self.mulMonCheckCondSet().times(0);
                        if ((itemCheck && itemCheck != undefined) || itemCheck === TYPECHECKWORKRECORDMULTIPLEMONTH.TIME) {
                            self.initialMultiMonthScreen().then(function() {
                                if ((self.checkItemTemp() || self.checkItemTemp() == TYPECHECKWORKRECORDMULTIPLEMONTH.TIME) && self.checkItemTemp() != itemCheck) {
                                    setTimeout(function() { self.displayAttendanceItemSelections_BA2_3(""); }, 200);
                                }
                            });
                        }
                        $(".nts-input").ntsError("clear");
                    });

                    self.comparisonRange().comparisonOperator.subscribe((operN) => {
                         $(".nts-input").ntsError("clear");
                        self.settingEnableComparisonMaxValueFieldExtra();
                        
                    });
                    break;
                }
                case sharemodel.CATEGORY.SCHEDULE_DAILY:
                    self.processScheduleDaily(option);                    
                    break;
                case sharemodel.CATEGORY.SCHEDULE_MONTHLY:
                    self.processScheduleMonthly(option);
                    break;
                case sharemodel.CATEGORY.SCHEDULE_YEAR:
                    self.processScheduleYear(option);
                    break;
                case sharemodel.CATEGORY.WEEKLY:
                    self.processScheduleWeekly(option);
                    break;
                default: break;
            }

        }

        //initial screen
        start(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();

            errors.clearAll();
            switch (self.category()) {
                case sharemodel.CATEGORY.DAILY:
                    $.when(self.getAllEnums(), self.initialScreen()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                case sharemodel.CATEGORY.MONTHLY:

                    $.when(self.getAllEnums(), self.getSpecialHoliday()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                // MinhVV Edit
                case sharemodel.CATEGORY.MULTIPLE_MONTHS:
                    $.when(self.getAllEnums(), self.initialMultiMonthScreen()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                case sharemodel.CATEGORY.SCHEDULE_DAILY:
                    $.when(self.getEnumScheduleDaily()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                case sharemodel.CATEGORY.SCHEDULE_MONTHLY:
                    $.when(self.getEnumScheduleMonthly()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                case sharemodel.CATEGORY.SCHEDULE_YEAR:
                    $.when(self.getEnumScheduleYear()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                case sharemodel.CATEGORY.WEEKLY:
                    $.when(self.getEnumScheduleWeekly()).done(function() {
                        dfd.resolve();
                    }).fail(() => {
                        dfd.reject();
                    });
                    break;
                default: break;
            }

            return dfd.promise();
        }

        /**
         * setting Enable/Disable Comparison of Max Value Field
         */
        private settingEnableComparisonMaxValueField(isStart: boolean) {
            let self = this;
            if (isStart) {
                self.enableComparisonMaxValue(self.workRecordExtractingCondition().errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon()[0].compareOperator() > 5);
            } else {
                self.enableComparisonMaxValue(self.comparisonRange().comparisonOperator() > 5);
            }
        }

        private settingEnableComparisonMaxValueFieldExtra() {
            let self = this;
            self.enableComparisonMaxValue(self.mulMonCheckCondSet().erAlAtdItem().compareOperator() > 5);
            //>5 thi tra ve  ban dau 30/07
            if (!self.enableComparisonMaxValue()) {
                let mulMonCheckType = self.mulMonCheckCondSet().typeCheckItem();
            }
            //日数
//            self.comparisonRange().minTimesValueDay.subscribe((value) => {
//                if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
//                    if (self.comparisonRange().minTimesValueDay() > self.comparisonRange().maxTimesValueDay()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//                if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
//                    if (self.comparisonRange().minTimesValueDay() >= self.comparisonRange().maxTimesValueDay()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                } 
//                  
//           });
//           // 金額
//            self.comparisonRange().minTimeValue.subscribe((value) => {
//                if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
//                    if (self.comparisonRange().minTimeValue() > self.comparisonRange().maxTimeValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//                if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
//                    if (self.comparisonRange().minTimeValue() >= self.comparisonRange().maxTimeValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//            
//           });
//            //時間
//            self.comparisonRange().minTimesValue.subscribe((value) => {
//                if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
//                    if (self.comparisonRange().minTimesValue() > self.comparisonRange().maxTimesValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//                if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
//                    if (self.comparisonRange().minTimesValue() >= self.comparisonRange().maxTimesValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//           });
//            self.comparisonRange().minAmountOfMoneyValue.subscribe((value) => {
//                if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
//                    if (self.comparisonRange().minAmountOfMoneyValue() > self.comparisonRange().minAmountOfMoneyValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//                if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
//                    if (self.comparisonRange().minAmountOfMoneyValue() >= self.comparisonRange().minAmountOfMoneyValue()) {
//                        setTimeout(() => {
//                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_927');
//                            $('#endValue').ntsError('set', { messageId: "Msg_927" });
//                        }, 25);
//                    }
//                }
//           });

        }
        
        private initComparisonValueRange(): model.ComparisonValueRange {
            let self = this;
            
            if (self.category() == sharemodel.CATEGORY.SCHEDULE_DAILY) {
                return new model.ComparisonValueRange(
                    self.workRecordExtractingCondition().checkItem
                    , self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparisonOperator
                    , self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareStartValue()
                    , self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareEndValue());
            }
            
            if (self.category() == sharemodel.CATEGORY.SCHEDULE_MONTHLY || self.category() == sharemodel.CATEGORY.SCHEDULE_YEAR || self.category() == sharemodel.CATEGORY.WEEKLY) {
                let startValue = self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue();
                let endValue = self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue();
                
                if (self.category() == sharemodel.CATEGORY.SCHEDULE_MONTHLY && self.workRecordExtractingCondition().checkItem() == 3) {
                    let operator = self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparisonOperator();
                    let defaultInputs = [
                        new sharemodel.InputModel(0, true, startValue, true, true, nts.uk.resource.getText("KAL003_80")), 
                        new sharemodel.InputModel(0, true, endValue, operator > 5, true, nts.uk.resource.getText("KAL003_83"))];
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs(defaultInputs);
                }
                var comparisonValueRange = new model.ComparisonValueRange(
                    self.workRecordExtractingCondition().checkItem
                    , self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().comparisonOperator
                    , startValue
                    , endValue);
                comparisonValueRange.minValue(startValue);
                comparisonValueRange.maxValue(endValue);
                return comparisonValueRange;
            }

            let erAlAtdItemCondition = self.workRecordExtractingCondition().errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon()[0];

            let comparisonValueRange;

            if (self.workRecordExtractingCondition().checkItem() == enItemCheck.Time          //時間
                || self.workRecordExtractingCondition().checkItem() == enItemCheck.Times      //回数
                || self.workRecordExtractingCondition().checkItem() == enItemCheck.AmountOfMoney //金額
                || self.workRecordExtractingCondition().checkItem() == enItemCheck.TimeOfDate    //時刻の場合
                || self.workRecordExtractingCondition().checkItem() == enItemCheck.CountinuousTime   //連続時間
            ) {
                if (erAlAtdItemCondition.compareOperator() > 5
                    || erAlAtdItemCondition.conditionType() == ConditionType.FIXED_VALUE
                ) {
                    comparisonValueRange = new model.ComparisonValueRange(
                        self.workRecordExtractingCondition().checkItem
                        , erAlAtdItemCondition.compareOperator
                        , erAlAtdItemCondition.compareStartValue()
                        , erAlAtdItemCondition.compareEndValue());
                } else {
                    comparisonValueRange = new model.ComparisonValueRange(
                        self.workRecordExtractingCondition().checkItem
                        , erAlAtdItemCondition.compareOperator
                        , erAlAtdItemCondition.singleAtdItem()
                        , erAlAtdItemCondition.singleAtdItem());
                }
            } else {
                comparisonValueRange = new model.ComparisonValueRange(
                    self.workRecordExtractingCondition().checkItem
                    , erAlAtdItemCondition.compareOperator
                    , 0
                    , 0);
            }
            return comparisonValueRange;
        }
        /**
         * initial screen
         */
        private initialScreen(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.initialDaily().done(() => {
                self.settingEnableComparisonMaxValueField(true);
                dfd.resolve();
            });
            return dfd.promise();
        }

        // ===========common begin ===================
        private getAllEnums(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();

            $.when(service.getEnumSingleValueCompareTypse(),
                // MinhVV ADD
                service.getEnumTypeCheckWorkRecordMultipleMonth(),
                service.getEnumRangeCompareType(),
                service.getEnumTypeCheckWorkRecord(),
                service.getEnumTargetSelectionRange(),
                service.getEnumTargetServiceType(),
                service.getEnumLogicalOperator()).done((
                    listSingleValueCompareTypse: Array<model.EnumModel>,
                    //Minh add
                    listTypeCheckWorkRecordMultipleMonth: Array<model.EnumModel>,
                    lstRangeCompareType: Array<model.EnumModel>,
                    listTypeCheckWorkRecord: Array<model.EnumModel>,
                    listTargetSelectionRange: Array<model.EnumModel>,
                    listTargetServiceType: Array<model.EnumModel>,
                    listLogicalOperator: Array<model.EnumModel>) => {
                    self.listSingleValueCompareTypes(self.getLocalizedNameForEnum(listSingleValueCompareTypse));
                    //MinhVV add
                    self.listTypeCheckWorkRecordMultipleMonths(self.getLocalizedNameForEnum(listTypeCheckWorkRecordMultipleMonth));
                    self.listRangeCompareTypes(self.getLocalizedNameForEnum(lstRangeCompareType));
                    self.listTypeCheckWorkRecords(self.getLocalizedNameForEnum(listTypeCheckWorkRecord));
                    //remove 3 enum : 4 5 6 as required ( ohashi)
                    // _.remove(self.listTypeCheckWorkRecords(), function(n) {
                    //     return (n.value == 5 || n.value == 6 || n.value == 4);
                    // });
                    let listTargetRangeWithName = self.getLocalizedNameForEnum(listTargetSelectionRange);
                    self.itemListTargetSelectionRange_BA1_5(listTargetRangeWithName);
                    self.itemListTargetServiceType_BA1_2(self.getLocalizedNameForEnum(listTargetServiceType));
                    self.buildListCompareTypes();
                    let listANDOR = self.getLocalizedNameForEnum(listLogicalOperator)
                    //ENUM 論理演算子
                    self.swANDOR_B5_3 = ko.observableArray(listANDOR);
                    //ENUM 論理演算子
                    self.swANDOR_B6_3 = ko.observableArray(listANDOR);
                    //ENUM 論理演算子
                    self.swANDOR_B7_2 = ko.observableArray(listANDOR);
                    dfd.resolve();

                }).always(() => {
                    dfd.resolve();
                });
            return dfd.promise();
        }
        
        /**
         * Get enum for all category schedule: daily
         */
        private getEnumScheduleDaily(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.listTypeCheckWorkRecords([]);
            $.when(service.getEnumDaiCheckItemType(), 
                    service.getCheckTimeType(), 
                    service.getTimeZoneTargetRange(),
                    service.getEnumSingleValueCompareTypse(),
                    service.getEnumRangeCompareType(),
                    service.getEnumTargetSelectionRange(),
                    service.getEnumTargetServiceType(),
                    service.getEnumLogicalOperator())
                .done((listDaiCheckItemType: Array<model.EnumModel>, 
                        listCheckTimeType: Array<model.EnumModel>, 
                        listTimeZoneTargetRange: Array<model.EnumModel>,
                        listSingleValueCompareTypse: Array<model.EnumModel>,
                        lstRangeCompareType: Array<model.EnumModel>,
                        listTargetSelectionRange: Array<model.EnumModel>,
                        listTargetServiceType: Array<model.EnumModel>,
                        listLogicalOperator: Array<model.EnumModel>) => {
                self.listSingleValueCompareTypes(self.getLocalizedNameForEnum(listSingleValueCompareTypse));
                self.listRangeCompareTypes(self.getLocalizedNameForEnum(lstRangeCompareType));
                
                self.listTypeCheckWorkRecords(self.getLocalizedNameForEnum(listDaiCheckItemType));
                self.listCheckTimeType(self.getLocalizedNameForEnum(listCheckTimeType));
                self.listTimeZoneTargetRange(self.getLocalizedNameForEnum(listTimeZoneTargetRange));
                    
                let listTargetRangeWithName = self.getLocalizedNameForEnum(listTargetSelectionRange);
                self.itemListTargetSelectionRange_BA1_5(listTargetRangeWithName);
                self.itemListTargetServiceType_BA1_2(self.getLocalizedNameForEnum(listTargetServiceType));
                self.buildListCompareTypes();
                let listANDOR = self.getLocalizedNameForEnum(listLogicalOperator)
                    
                dfd.resolve();

            }).always(() => {
                dfd.resolve();
            });
            
            return dfd.promise();
        }
        
        /**
         * Get enum for all category schedule: monthly
         */
        private getEnumScheduleMonthly(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            $.when(service.getEnumMonCheckItemType(),
                   service.getEnumTypeOfContrast(),
                   service.getEnumTypeOfDays(),
                   service.getEnumTypeOfTime(),
                   service.getEnumTypeOfVacations(),
                   service.getEnumSingleValueCompareTypse(),
                   service.getEnumRangeCompareType(),
                   service.getSpecialHoliday()).done((
                        listMonCheckItemType: Array<model.EnumModel>, 
                        listTypeOfContrast: Array<model.EnumModel>, 
                        listTypeOfDays: Array<model.EnumModel>,
                        listTypeOfTime: Array<model.EnumModel>,
                        listTypeOfVacations: Array<model.EnumModel>,
                        listSingleValueCompareTypse: Array<model.EnumModel>,
                        listRangeCompareType: Array<model.EnumModel>,
                        listSpecialCode) => {
                self.listTypeCheckWorkRecords(self.getLocalizedNameForEnum(listMonCheckItemType));
                self.listCheckTimeType(self.getLocalizedNameForEnum(listTypeOfContrast));
               
                self.listTypeOfContrast(self.getLocalizedNameForEnum(listTypeOfContrast));
                self.listTypeOfDays(self.getLocalizedNameForEnum(listTypeOfDays));
                self.listTypeOfTime(self.getLocalizedNameForEnum(listTypeOfTime));
                self.listTypeOfVacations(self.getLocalizedNameForEnum(listTypeOfVacations));
                       
                self.getChangeListCheckTimeType(self.checkItemTemp());
                       
                self.listSingleValueCompareTypes(self.getLocalizedNameForEnum(listSingleValueCompareTypse));
                self.listRangeCompareTypes(self.getLocalizedNameForEnum(listRangeCompareType));
                self.buildListCompareTypes();
                       
                self.listSpecialholidayframe = listSpecialCode;
                dfd.resolve();

            }).always(() => {
                dfd.resolve();
            });
            
            return dfd.promise();
        }
        
        private getEnumScheduleYear(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            $.when(service.getEnumYearCheckItemType(),
                   service.getEnumTypeOfDays(),
                   service.getEnumTypeOfTime(),
                   service.getEnumSingleValueCompareTypse(),
                   service.getEnumRangeCompareType()).done((
                        listYearCheckItemType: Array<model.EnumModel>, 
                        listTypeOfDays: Array<model.EnumModel>,
                        listTypeOfTime: Array<model.EnumModel>,
                        listSingleValueCompareTypse: Array<model.EnumModel>,
                        listRangeCompareType: Array<model.EnumModel>) => {
                self.listTypeCheckWorkRecords(self.getLocalizedNameForEnum(listYearCheckItemType));
                self.listCheckTimeType(self.getLocalizedNameForEnum(listTypeOfDays));
               
                self.listTypeOfDays(self.getLocalizedNameForEnum(listTypeOfDays));
                self.listTypeOfTime(self.getLocalizedNameForEnum(listTypeOfTime));
                       
                self.getChangeListCheckTimeTypeScheduleYear(self.checkItemTemp());
                       
                self.listSingleValueCompareTypes(self.getLocalizedNameForEnum(listSingleValueCompareTypse));
                self.listRangeCompareTypes(self.getLocalizedNameForEnum(listRangeCompareType));
                self.buildListCompareTypes();
                       
                dfd.resolve();

            }).always(() => {
                dfd.resolve();
            });
            
            return dfd.promise();
        }
        
        private getEnumScheduleWeekly(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            $.when(service.getEnumWeeklyCheckItemType(),
                   service.getEnumSingleValueCompareTypse(),
                   service.getEnumRangeCompareType()).done((
                        listWeeklyCheckItemType: Array<model.EnumModel>, 
                        listSingleValueCompareTypse: Array<model.EnumModel>,
                        listRangeCompareType: Array<model.EnumModel>) => {
                self.listTypeCheckWorkRecords(self.getLocalizedNameForEnum(listWeeklyCheckItemType));
                       
                self.listSingleValueCompareTypes(self.getLocalizedNameForEnum(listSingleValueCompareTypse));
                self.listRangeCompareTypes(self.getLocalizedNameForEnum(listRangeCompareType));
                self.buildListCompareTypes();
                       
                dfd.resolve();

            }).always(() => {
                dfd.resolve();
            });
            
            return dfd.promise();
        }

        /**
         * build List of Compare Types
         */
        private buildListCompareTypes() {
            let self = this;
            var listCompareTypes = self.listSingleValueCompareTypes().concat(self.listRangeCompareTypes());
            self.listCompareTypes(listCompareTypes);
        }

        /**
         * get Localize name by Enum
         * @param listEnum
         */
        private getLocalizedNameForEnum(listEnum: Array<model.EnumModel>): Array<model.EnumModel> {
            if (listEnum) {
                for (var i = 0; i < listEnum.length; i++) {
                    if (listEnum[i].localizedName) {
                        listEnum[i].localizedName = resource.getText(listEnum[i].localizedName);
                    }
                }
                return listEnum;
            }
            return [];
        }

        /**
         * Initial Group Condition
         * @param listGroupCondition
         */
        private initGroupCondition(listGroupCondition: Array<sharemodel.ErAlAtdItemCondition>): Array<sharemodel.ErAlAtdItemCondition> {
            let listCondition: Array<sharemodel.ErAlAtdItemCondition> = [];
            let maxRow = 3;
            if (listGroupCondition && listGroupCondition != undefined) {
                for (var i = 0; i < listGroupCondition.length && i < maxRow; i++) {
                    listGroupCondition[i].targetNO(i);
                    listCondition.push(listGroupCondition[i]);
                }
            }
            if (listCondition.length < maxRow) {
                for (var i = listCondition.length; i < maxRow; i++) {
                    listCondition.push(shareutils.getDefaultCondition(i - 1));
                }
            }
            return listCondition;
        }

        /**
         * Initial Compound Group Condition
         */
        private initCompoundGroupCondition(dfd) {
            let self = this,
                errorAlarmCondition = self.workRecordExtractingCondition().errorAlarmCondition();
            let compoundCondition = errorAlarmCondition.atdItemCondition();
            if (!compoundCondition || compoundCondition == undefined) {
                compoundCondition = shareutils.getDefaultAttendanceItemCondition();
                errorAlarmCondition.atdItemCondition(compoundCondition);
            }
            let listGr1 = self.initGroupCondition(compoundCondition.group1().lstErAlAtdItemCon());
            compoundCondition.group1().lstErAlAtdItemCon(listGr1);
            let listGr2 = self.initGroupCondition(compoundCondition.group2().lstErAlAtdItemCon());
            compoundCondition.group2().lstErAlAtdItemCon(listGr2);
        }
        // ============build enum for combobox BA2-5: end ==============
        // ===========common end =====================
        //==========Daily section Begin====================
        /**
         * Initial Daily
         */
        private initialDaily(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            switch (self.workRecordExtractingCondition().checkItem()) {
                case enItemCheck.Time:          //時間
                case enItemCheck.Times:         //回数
                case enItemCheck.AmountOfMoney: //金額
                case enItemCheck.TimeOfDate:    //時刻の場合
                    self.initialDailyItemChkItemComparison(dfd);
                    break;
                case enItemCheck.CountinuousTime:   //連続時間
                    self.initialDailyItemChkCountinuousTime(dfd);
                    break;
                case enItemCheck.CountinuousWork:   //連続時間帯
                    self.initialDailyItemChkCountinuousWork(dfd);
                    break;
                case enItemCheck.CountinuousTimeZone: //連続勤務
                    self.initialDailyItemChkCountinuousTimeZone(dfd);
                    break;
                case enItemCheck.CompoundCondition: //複合条件
                    self.initCompoundGroupCondition(dfd);
                    break;
                default:
                    break;
            }
            dfd.resolve();
            return dfd.promise();
        }

        /**
         * initial in case check item : Time, Times, Amount of money, Time of day
         */
        private initialDailyItemChkItemComparison(defered) {
            let self = this,
                workRecordExtractingCondition = self.workRecordExtractingCondition();
            //ドメインモデル「日次の勤怠項目」を取得する - Acquire domain model "DailyAttendanceItem"
            service.getDailyItemChkItemComparison(workRecordExtractingCondition.checkItem()).then((itemAttendances: Array<any>) => {
                self.listAllAttdItem = self.getListAttendanceIdFromDtos(itemAttendances);

                // build name of Attendance Item
                let currentAtdItemCondition = workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon()[0];
                self.fillTextDisplayTarget(defered, currentAtdItemCondition);
                /*
               let listAttendanceItemSelectedCode = self.getListAttendanceItemCode();//勤怠項目の加算減算式
                self.generateNameCorrespondingToAttendanceItem(listAttendanceItemSelectedCode).done((names) => {
                            self.displayAttendanceItemSelections_BA2_3(names);
                });
                */
                // initial default data of ErAlAtdItemCon
                //self.initialDataOfErAlAtdItemCon();
                //ドメインモデル「勤務種類」を取得する - Acquire domain model "WorkType"
                self.initialWorkTypes(defered);
            }, function(rejected) {
                defered.resolve();
            });
        }

        /**
         * Initial in case Daily Item Check Continuous Time
         */
        private initialDailyItemChkCountinuousTime(defered) {
            let self = this;
            //ドメインモデル「日次の勤怠項目」を取得する - Acquire domain model "DailyAttendanceItem"
            service.getAttendCoutinousTime().then((itemAttendances) => {
                self.listAllAttdItem = self.getListAttendanceIdFromDtos(itemAttendances);

                // build name of Attendance Item
                let currentAtdItemCondition = self.workRecordExtractingCondition().errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon()[0];
                self.fillTextDisplayTarget(defered, currentAtdItemCondition);

                /*let listWorkTimeItemSelectedCode = self.getListAttendanceItemCode();//勤怠項目の加算減算式
                self.generateNameCorrespondingToAttendanceItem(listWorkTimeItemSelectedCode).done((names) => {
                    self.displayAttendanceItemSelections_BA2_3(names);
                });
                */
                // initial default data of ErAlAtdItemCon
                //self.initialDataOfErAlAtdItemCon();
                //ドメインモデル「勤務種類」を取得する - Acquire domain model "WorkType"
                self.initialWorkTypes(defered);
            }, function(rejected) {
                defered.resolve();
            });
        }

        /**
         * Initial in case Daily Item Check Continuous Work
         */
        private initialDailyItemChkCountinuousWork(defered) {
            let self = this;
            //ドメインモデル「勤務種類」を取得する - Acquire domain model "WorkType"
            self.initialWorkTypes(defered);
        }

        /**
         * Initial in case Daily Item Check Continuous Time zone
         */
        private initialDailyItemChkCountinuousTimeZone(defered) {
            let self = this;
            //ドメインモデル「就業時間帯の設定」を取得する - Acquire domain model "WorkTimeSetting"
            service.getAttendCoutinousTimeZone().done((settingTimeZones) => {
                self.listAllWorkingTime = self.getListWorkTimeCdFromDtos(settingTimeZones);
                //get name
                let listItems = self.workRecordExtractingCondition().errorAlarmCondition().workTimeCondition().planLstWorkTime();
                var strWt = "";
                for (var i = 0; i < listItems.length; i++) {
                    var wt = _.filter(settingTimeZones, function(o) { return o.worktimeCode == listItems[i]});
                    if(wt != null || wt != undefined){
                        strWt += ', ' + wt[0].workTimeName;
                    }
                }
                if(strWt != ""){
                    self.displayWorkingTimeSelections_BA5_3(strWt.substring(2));    
                } else {
                    self.displayWorkingTimeSelections_BA5_3(strWt);
                }
                
                
                //ドメインモデル「勤務種類」を取得する - Acquire domain model "WorkType"
                self.initialWorkTypes(defered);
            }, function(rejected) {
                defered.resolve();
            });
        }

        private getListAttendanceItemCode(): Array<any> {
            let self = this,
                workRecordExtractingCondition = self.workRecordExtractingCondition();
            let lstErAlAtdItemCon = workRecordExtractingCondition.errorAlarmCondition()
                .atdItemCondition().group1().lstErAlAtdItemCon();
            let listAttendanceItemSelectedCode = lstErAlAtdItemCon[0].countableAddAtdItems() || [];//勤怠項目の加算減算式
            return listAttendanceItemSelectedCode;
        }
        private setListAttendanceItemCode(listWorkTimeItemSelectedCode: Array<any>) {
            let self = this,
                workRecordExtractingCondition = self.workRecordExtractingCondition();
            workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1()
                .lstErAlAtdItemCon()[0].countableAddAtdItems(listWorkTimeItemSelectedCode || []);//勤怠項目の加算減算式
        }
        /**
         * アルゴリズム「勤怠項目に対応する名称を生成する」を実行する - Execute algorithm "Generate name corresponding to attendance item"
         * @param List<itemAttendanceId>
         */
        private generateNameCorrespondingToAttendanceItem(listAttendanceItemCode: Array<any>): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            if (listAttendanceItemCode && listAttendanceItemCode.length > 0) {
                service.getAttendNameByIds(listAttendanceItemCode).done((dailyAttendanceItemNames) => {
                    if (dailyAttendanceItemNames && dailyAttendanceItemNames.length > 0) {
                        var attendanceName: string = '';
                        for (var i = 0; i < dailyAttendanceItemNames.length; i++) {
                            if (attendanceName) {
                                attendanceName = attendanceName + "," + dailyAttendanceItemNames[i].attendanceItemName;
                            } else {
                                attendanceName = dailyAttendanceItemNames[i].attendanceItemName;
                            }
                        }
                        dfd.resolve(attendanceName);
                    } else {
                        dfd.resolve('');
                    }
                }).always(() => {
                    dfd.resolve('');
                });
            } else {
                dfd.resolve('');
            }
            return dfd.promise();
        }

        /**
         * Build list of Attendance Item Name from List<AttdItemDto>
         * @param listAttendanceItemCode
         */
        private buildItemName(listItem: Array<any>): string {
            let self = this, retNames: string = '';
            if (listItem) {
                for (var i = 0; i < listItem.length; i++) {
                    if (retNames) {
                        retNames = retNames + "," + listItem[i].name;
                    } else {
                        retNames = listItem[i].name;
                    }
                }
            }
            return retNames;
        }

        /**
         * //ドメインモデル「勤務種類」を取得する - Acquire domain model "WorkType"
         * 
         */
        private initialWorkTypes(defered): void {
            let self = this,
                workTypeCondition = self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition();
            self.listAllWorkType = [];
            // get all Work type
            service.getAttendCoutinousWork().then((workTypes) => {
                if (workTypes && workTypes != undefined) {
                    for (var i = 0; i < workTypes.length; i++) {
                        self.listAllWorkType.push(workTypes[i].workTypeCode);
                    }
                }

                // get Name of selected work type.
                let wkTypeSelected = workTypeCondition.planLstWorkType();
                if (wkTypeSelected && wkTypeSelected.length > 0) {
                    service.findWorkTypeByCodes(wkTypeSelected).then((listWrkTypes) => {
                        let names: string = self.buildItemName(listWrkTypes);
                        self.displayWorkTypeSelections_BA1_4(names);
                    });
                } else {
                    self.displayWorkTypeSelections_BA1_4("");
                }
            }, function(rejected) {
                defered.resolve();
            });
        }

        //initial default data of ErAlAtdItemCon
        private initialDataOfErAlAtdItemCon() {
            let self = this, workRecordExtractingCondition = self.workRecordExtractingCondition();
            let checkItem = workRecordExtractingCondition.checkItem();
            if (!(checkItem == enItemCheck.Time
                || checkItem == enItemCheck.CountinuousTime
                || checkItem == enItemCheck.Times
                || checkItem == enItemCheck.AmountOfMoney
                || checkItem == enItemCheck.TimeOfDate)) {
                return;
            }
            let conditionAtr = 0;
            let lstErAlAtdItemCon1 = workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon();
            switch (checkItem) {
                case enItemCheck.Time:          //時間
                case enItemCheck.CountinuousTime:   //連続時間
                    conditionAtr = 1;
                    break;
                case enItemCheck.Times:         //回数
                    conditionAtr = 0;
                    break;
                case enItemCheck.AmountOfMoney: //金額
                    conditionAtr = 3;
                    break;
                case enItemCheck.TimeOfDate:    //時刻の場合
                    conditionAtr = 2;
                    break;
                default:
                    return;
            }

            for (var i = 0; i < lstErAlAtdItemCon1.length; i++) {
                lstErAlAtdItemCon1[i].conditionAtr(conditionAtr);
                lstErAlAtdItemCon1[i].conditionType(ConditionType.FIXED_VALUE); //1: 勤怠項目 - AttendanceItem, 0: fix
            }
            let lstErAlAtdItemCon2 = workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group2().lstErAlAtdItemCon();
            for (var i = 0; i < lstErAlAtdItemCon2.length; i++) {
                lstErAlAtdItemCon2[i].conditionAtr(conditionAtr);
                lstErAlAtdItemCon2[i].conditionType(ConditionType.FIXED_VALUE); //1: 勤怠項目 - AttendanceItem, 0: fix
            }
        }
        /**
         * Get list of attendance id from list Dtos
         * @param itemAttendances
         */
        private getListAttendanceIdFromDtos(itemAttendances: Array<any>): Array<string> {
            let listAllAttdItemCode: Array<string> = [];
            if (itemAttendances && itemAttendances != undefined) {
                for (var i = 0; i < itemAttendances.length; i++) {
                    listAllAttdItemCode.push(itemAttendances[i].attendanceItemId);
                }
            }
            return listAllAttdItemCode;
        }

        /**
         * Get list of work time id from list Dtos
         * @param itemAttendances
         */
        private getListWorkTimeCdFromDtos(workTimes: Array<any>): Array<string> {
            let listWorkTimesCode: Array<string> = [];
            if (workTimes && workTimes != undefined) {
                for (var i = 0; i < workTimes.length; i++) {
                    listWorkTimesCode.push(workTimes[i].worktimeCode);
                }
            }
            return listWorkTimesCode;
        }
        //==========Daily session End====================

        /**
         * Get list code from list codes that return from selected dialog
         * @param listKdl002Model
         */
        private getListCode(listKdl002Model: Array<model.ItemModelKdl002>): Array<string> {
            let retListCode: Array<string> = [];
            if (listKdl002Model == null || listKdl002Model == undefined) {
                return retListCode;
            }
            for (var i = 0; i < listKdl002Model.length; i++) {
                retListCode.push(listKdl002Model[i].code);
            }
            return retListCode;
        }
        /**
         * open dialog for select working type
         */
        btnSettingBA1_3_click() {
            let self = this,
                workTypeCondition = self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition();

            block.invisible();
            let lstSelectedCode = workTypeCondition.planLstWorkType();
            windows.setShared("KDL002_Multiple", true);
            //all possible items
            windows.setShared("KDL002_AllItemObj", self.listAllWorkType);
            //selected items
            windows.setShared("KDL002_SelectedItemId", lstSelectedCode);
            windows.sub.modal("/view/kdl/002/a/index.xhtml",
                { title: "乖離時間の登録＞対象項目", dialogClass: "no-close" }).onClosed(function(): any {

                    $(".nts-input").ntsError("clear");
                    //get data from share window
                    let listItems: Array<any> = windows.getShared("KDL002_SelectedNewItem");
                    if (listItems != null && listItems != undefined) {
                        let listCodes: Array<string> = self.getListCode(listItems);
                        workTypeCondition.planLstWorkType(listCodes);
                        workTypeCondition.planFilterAtr = true;
                        // get name
                        let names: string = self.buildItemName(listItems);
                        self.displayWorkTypeSelections_BA1_4(names);

                    }
                    block.clear();
                });
        }

        /**
         * open dialog for select working time zone (KDL002)
         */
        btnSettingBA5_2_click() {
            let self = this,
                workTimeCondition = self.workRecordExtractingCondition().errorAlarmCondition().workTimeCondition();

            block.invisible();
            let lstSelectedCode = workTimeCondition.planLstWorkTime();
            windows.setShared("kml001multiSelectMode", true);
            //all possible items
            windows.setShared("kml001selectAbleCodeList", self.listAllWorkingTime);
            //selected items
            windows.setShared("kml001selectedCodeList", lstSelectedCode);
            windows.setShared("kdl00showNoSelectionRow", false);
            windows.sub.modal("/view/kdl/001/a/index.xhtml",
                { title: "割増項目の設定", dialogClass: "no-close" }).onClosed(function(): any {
                    $(".nts-input").ntsError("clear");
                    //get data from share window
                    let listItems: Array<any> = windows.getShared("kml001selectedCodeList");
                    if (listItems != null && listItems != undefined) {
                        workTimeCondition.planLstWorkTime(listItems);
                        var dfd = $.Deferred();
                        service.getAttendCoutinousTimeZone().done((settingTimeZones) => {
                            //get name
                            var strWt = "";
                            for (var i = 0; i < listItems.length; i++) {
                                var wt = _.filter(settingTimeZones, function(o) { return o.worktimeCode === listItems[i]});
                                if(wt != null || wt != undefined){
                                    strWt += ', ' + wt[0].workTimeName;
                                }
                            }
                            if(strWt != ""){
                                self.displayWorkingTimeSelections_BA5_3(strWt.substring(2));    
                            } else {
                                self.displayWorkingTimeSelections_BA5_3(strWt);
                            }
                        }, function(rejected) {
                            dfd.resolve();
                        });                        
                    }
                    block.clear();
                });
        }


        //openSelectAtdItemDialogTarget() {
        btnSettingBA2_2_click() {
            let self = this;
            let dfd = $.Deferred();
            switch (self.category()) {
                case sharemodel.CATEGORY.DAILY: {
                    let currentAtdItemCondition = self.workRecordExtractingCondition().errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon()[0];
                    if (self.workRecordExtractingCondition().checkItem() == 3) {
                        self.getListItemByAtr(6).done((lstItem) => {
                            let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                            //Open dialog KDL021
                            nts.uk.ui.windows.setShared('Multiple', false);
                            nts.uk.ui.windows.setShared('AllAttendanceObj', lstItemCode);
                            nts.uk.ui.windows.setShared('SelectedAttendanceId', [currentAtdItemCondition.uncountableAtdItem()]);
                            nts.uk.ui.windows.sub.modal("at", "/view/kdl/021/a/index.xhtml").onClosed(() => {
                                $(".nts-input").ntsError("clear");
                                let output = nts.uk.ui.windows.getShared("selectedChildAttendace");
                                if (output) {
                                    currentAtdItemCondition.uncountableAtdItem(parseInt(output));
                                    self.fillTextDisplayTarget(dfd, currentAtdItemCondition);
                                } else if (output === "") {
                                    currentAtdItemCondition.uncountableAtdItem(0);
                                    self.displayAttendanceItemSelections_BA2_3("");
                                }
                            });

                        });
                    } else {
                        self.getListItemByAtrDaily(self.workRecordExtractingCondition().checkItem(), 0).done((lstItem) => {
                            let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                            //Open dialog KDW007C
                            let param = {
                                //                                attr: 1,
                                lstAllItems: lstItemCode,
                                lstAddItems: currentAtdItemCondition.countableAddAtdItems(),
                                lstSubItems: currentAtdItemCondition.countableSubAtdItems()
                            };
                            nts.uk.ui.windows.setShared("KDW007Params", param);
                            nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/c/index.xhtml").onClosed(() => {
                                $(".nts-input").ntsError("clear");
                                let output = nts.uk.ui.windows.getShared("KDW007CResults");
                                if (output) {
                                    currentAtdItemCondition.countableAddAtdItems(output.lstAddItems.map((item) => { return parseInt(item); }));
                                    currentAtdItemCondition.countableSubAtdItems(output.lstSubItems.map((item) => { return parseInt(item); }));
                                    self.fillTextDisplayTarget(dfd, currentAtdItemCondition);
                                }
                            });
                        });

                    }
                    break;
                }
                case sharemodel.CATEGORY.MONTHLY: {

                    let currentAtdItemConMon = self.extraResultMonthly().currentConditions()[0].group1().lstErAlAtdItemCon()[0];
                    self.getListItemByAtrDailyAndMonthly(self.extraResultMonthly().typeCheckItem(), 1).done((lstItem) => {
                        let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                        //Open dialog KDW007C
                        let param = {
                            attr: 1,
                            lstAllItems: lstItemCode,
                            lstAddItems: currentAtdItemConMon.countableAddAtdItems(),
                            lstSubItems: currentAtdItemConMon.countableSubAtdItems()
                        };

                        //                        if ((self.checkItemTemp() || self.checkItemTemp() == 0) && self.checkItemTemp() != self.workRecordExtractingCondition().checkItem()) {
                        //                            param.lstAddItems = [];
                        //              ems = [];
                        //                        }

                        nts.uk.ui.windows.setShared("KDW007Params", param);
                        nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/c/index.xhtml").onClosed(() => {
                            $(".nts-input").ntsError("clear");
                            let output = nts.uk.ui.windows.getShared("KDW007CResults");
                            if (output) {
                                currentAtdItemConMon.countableAddAtdItems(output.lstAddItems.map((item) => { return parseInt(item); }));
                                currentAtdItemConMon.countableSubAtdItems(output.lstSubItems.map((item) => { return parseInt(item); }));
                                //self.fillTextDisplayTarget(dfd, currentAtdItemCondition);
                            }
                        });

                    });
                    break;
                }
                //MinhVV add
                case sharemodel.CATEGORY.MULTIPLE_MONTHS: {
                    let attdAtr = CONDITIONATR.TIMES;
                    let mulMonCheckItem = self.mulMonCheckCondSet().typeCheckItem();
                    self.getListItemByAtrMultipleMonth(mulMonCheckItem, 1).done((lstItem) => {
                        let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                        //Open dialog KDW007C
                        let param = {
                            attr: 1,
                            lstAllItems: lstItemCode,
                            lstAddItems: self.mulMonCheckCondSet().erAlAtdItem().countableAddAtdItems(),
                            lstSubItems: self.mulMonCheckCondSet().erAlAtdItem().countableSubAtdItems()
                        };
                        nts.uk.ui.windows.setShared("KDW007Params", param);
                        nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/c/index.xhtml").onClosed(() => {
                            $(".nts-input").ntsError("clear");
                            let output = nts.uk.ui.windows.getShared("KDW007CResults");
                            if (output) {
                                self.mulMonCheckCondSet().erAlAtdItem().countableAddAtdItems(output.lstAddItems.map((item) => { return parseInt(item); }));
                                self.mulMonCheckCondSet().erAlAtdItem().countableSubAtdItems(output.lstSubItems.map((item) => { return parseInt(item); }));
                                self.fillTextDisplayTargetMulMon(dfd, self.mulMonCheckCondSet().erAlAtdItem());
                            }
                        });

                    });
                    break;
                }
                    
                case sharemodel.CATEGORY.WEEKLY:
                    let currentAtdItemConMon = self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition();
                    self.getListItemByAtrWeekly(self.workRecordExtractingCondition().checkItem(), 1).done((lstItem) => {
                        let lstItemCode = lstItem.map((item) => { return item.attendanceItemId; });
                        //Open dialog KDW007C
                        let param = {
                            attr: 1,
                            lstAllItems: lstItemCode,
                            lstAddItems: currentAtdItemConMon.countableAddAtdItems(),
                            lstSubItems: currentAtdItemConMon.countableSubAtdItems()
                        };

                        nts.uk.ui.windows.setShared("KDW007Params", param);
                        nts.uk.ui.windows.sub.modal("at", "/view/kdw/007/c/index.xhtml").onClosed(() => {
                            $(".nts-input").ntsError("clear");
                            let output = nts.uk.ui.windows.getShared("KDW007CResults");
                            if (output) {
                                console.log(output);
                                currentAtdItemConMon.countableAddAtdItems(output.lstAddItems.map((item) => { return parseInt(item); }));
                                currentAtdItemConMon.countableSubAtdItems(output.lstSubItems.map((item) => { return parseInt(item); }));
                                self.fillTextDisplayTargetWeekly(lstItem, currentAtdItemConMon);
                            }
                        });

                    });
                    break;
                default: break;
            }

        }

        getListItemByAtr(conditionAtr) {
            let self = this;
            return service.getAttendanceItemByAtr(conditionAtr);
        }

        //GET ALL DAILY
        getListItemByAtrDaily(typeCheck: number, mode: number) {
            let self = this;
            let dfd = $.Deferred<any>();
            if (typeCheck == 1) {
                //With type 回数 - Times , Number  = 2
                service.getAttendanceItemByAtrNew(DAILYATTENDANCEITEMATR.NumberOfTime, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == 0 || typeCheck == 4) {
                //With type 時間 - Time , 連続期間 - ContinuousTime
                service.getAttendanceItemByAtrNew(DAILYATTENDANCEITEMATR.Time, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == 2) {
                //With type 金額 - AmountMoney
                service.getAttendanceItemByAtrNew(DAILYATTENDANCEITEMATR.AmountOfMoney, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else {
                dfd.resolve([]);
            }
            return dfd.promise();
        }


        getListItemByAtrMultipleMonth(mulMonCheckItem: number, mode: number) {
            let self = this;
            let dfd = $.Deferred<any>();
            if ((mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIME)) {
                //時間
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.TIME, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIMES) {
                //回数
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.NUMBER, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_AMOUNT) {
                //金額   
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.AMOUNT, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_DAYS) {
                //回数
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.DAYS, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else {
                dfd.resolve([]);
            }
            return dfd.promise();
        }

        //GET ALL MONTHLY
        getListItemByAtrDailyAndMonthly(typeCheck: number, mode: number) {
            let self = this;
            let dfd = $.Deferred<any>();
            if (typeCheck == 6) { //combobox select
                //With type 回数 - Times , Number  = 2
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.NUMBER, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == 4) {
                //With type 時間 - Time
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.TIME, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == 7) {
                //With type 金額 - AmountMoney
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.AMOUNT, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == 5) { // 日数
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.DAYS, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else {
                dfd.resolve([]);
            }
            return dfd.promise();
        }
        
        /**
         * <CATEGORY=WEEKLY>
         * Get attendance item
         */
        getListItemByAtrWeekly(typeCheck: number, mode: number) {
            let self = this;
            let dfd = $.Deferred<any>();
            if (typeCheck == WEEKLYCHECKITEMTYPE.TIMES || typeCheck == WEEKLYCHECKITEMTYPE.CONTINUOUS_TIMES) { //combobox select
                //With type 回数 - Times ,
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.NUMBER, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == WEEKLYCHECKITEMTYPE.TIME || typeCheck == WEEKLYCHECKITEMTYPE.CONTINUOUS_TIME) {
                //With type 時間 - Time
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.TIME, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else if (typeCheck == WEEKLYCHECKITEMTYPE.DAYS || typeCheck == WEEKLYCHECKITEMTYPE.CONTINUOUS_DAY) { // 日数
                service.getAttendanceItemByAtrNew(MONTHLYATTENDANCEITEMATR.DAYS, mode).done((lstAtdItem) => {
                    dfd.resolve(lstAtdItem);
                });
            } else {
                dfd.resolve([]);
            }
            return dfd.promise();
        }

        //monthly
        getAttdItemMonByAtr(atr) {
            let self = this;
            return service.getAttdItemMonByAtr(atr);
        }

        //Update ticket #100187
        //        getSpecialholidayframe(): JQueryPromise<any> {
        //            let self = this,
        //                dfd = $.Deferred<any>();
        //            service.getSpecialholidayframe().done(function(data) {
        //                self.listSpecialholidayframe = data;
        //                dfd.resolve();
        //                 //            return dfd.promise();
        //        }
        
        getSpecialHoliday(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred<any>();
            service.getSpecialHoliday().done(function(data) {
                let holidayCode;
                _.map(self.extraResultMonthly().conditions(), (d) => {
                    if (d.haveComboboxFrame()) {
                        if (!nts.uk.util.isNullOrUndefined(d.listItemID()) && d.listItemID() > 0) {
                            holidayCode = d.listItemID()[0];
                        }
                    }
                });
                if (!nts.uk.util.isNullOrUndefined(holidayCode)) {
                    let newdata = [];
                    let haveInList = false;
                    let index = 0;
                    if (!nts.uk.util.isNullOrUndefined(data) && data.length > 0) {
                        let length = data.length;
                        _.map(data, (d) => {
                            if (index == 0 && holidayCode < d.specialHolidayCode) {
                                newdata.push({ specialHolidayCode: holidayCode, specialHolidayName: resource.getText('KAL003_120') });
                                newdata.push(d);
                            } else if ((holidayCode > d.specialHolidayCode && index == length - 1)
                                || (holidayCode > d.specialHolidayCode && index < length - 1 && holidayCode < data[index + 1].specialHolidayCode)) {
                                newdata.push(d);
                                newdata.push({ specialHolidayCode: holidayCode, specialHolidayName: resource.getText('KAL003_120') });
                            } else {
                                newdata.push(d);
                            }

                            if (d.specialHolidayCode === holidayCode) {
                                haveInList = true;
                            }
                            index++;
                        });
                    } else {
                        newdata.push({ specialHolidayCode: holidayCode, specialHolidayName: resource.getText('KAL003_120') });
                    }

                    if (!haveInList) {
                        self.listSpecialholidayframe = newdata;
                    } else {
                        self.listSpecialholidayframe = data;
                    }
                } else {
                    self.listSpecialholidayframe = data;
                }

                dfd.resolve();
            });
            return dfd.promise();
        }
        //End update ticket #100187

        fillTextDisplayTarget(defered, currentAtdItemCondition) {

            let self = this;
            self.displayAttendanceItemSelections_BA2_3("");
            if (self.workRecordExtractingCondition().checkItem() === 3) {
                if (currentAtdItemCondition.uncountableAtdItem()) {
                    service.getAttendanceItemByCodes([currentAtdItemCondition.uncountableAtdItem()]).then((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            self.displayAttendanceItemSelections_BA2_3(lstItems[0].attendanceItemName);
                            $("#display-target-item").trigger("validate");
                        }
                    }, function(rejected) {
                        defered.resolve();
                    });
                }
            } else {
                if (currentAtdItemCondition.countableAddAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(currentAtdItemCondition.countableAddAtdItems()).then((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " + ";
                                self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + lstItems[i].attendanceItemName + operator);
                            }
                            $("#display-target-item").trigger("validate");
                        }

                        if (currentAtdItemCondition.countableSubAtdItems().length > 0) {
                            service.getAttendanceItemByCodes(currentAtdItemCondition.countableSubAtdItems()).then((lstItems) => {
                                if (lstItems && lstItems.length > 0) {
                                    for (let i = 0; i < lstItems.length; i++) {
                                        let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                        let beforeOperator = (i === 0) ? " - " : "";
                                        self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + beforeOperator + lstItems[i].attendanceItemName + operator);
                                    }
                                    $("#display-target-item").trigger("validate");
                                }
                            });
                        }
                    }, function(rejected) {
                        defered.resolve();
                    });
                } else if (currentAtdItemCondition.countableSubAtdItems().length > 0) {
                    service.getAttendanceItemByCodes(currentAtdItemCondition.countableSubAtdItems()).then((lstItems) => {
                        if (lstItems && lstItems.length > 0) {
                            for (let i = 0; i < lstItems.length; i++) {
                                let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                let beforeOperator = (i === 0) ? " - " : "";
                                self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + beforeOperator + lstItems[i].attendanceItemName + operator);
                            }
                            $("#display-target-item").trigger("validate");
                        }
                    }, function(rejected) {
                        defered.resolve();
                    });
                }

            }

            return defered.promise();
        }
        
        /**
         * <CATEGORY=WEEKLY>
         * File text display for element B2_3 of pattern A
         */
        fillTextDisplayTargetWeekly(sourceName, currentAtdItemCondition) {
            let self = this;
            let countableAddAtdItems = currentAtdItemCondition.countableAddAtdItems(),
                countableSubAtdItems = currentAtdItemCondition.countableSubAtdItems();
            if (sourceName) {
                self.convertToText(sourceName, countableAddAtdItems, countableSubAtdItems);
                return;
            }
            let itemIds = _.concat(countableAddAtdItems, countableSubAtdItems);
            if (itemIds.length === 0) {
                return;
            }
            service.getAttendanceItemByCodesNew(itemIds, 1).done((lstItems) => {
                self.convertToText(lstItems, countableAddAtdItems, countableSubAtdItems);
            });  
        }
        
        /**
         * <CATEGORY=WEEKLY>
         */
        convertToText(sourceName: Array<any>, countableAddAtdItems: Array<number>, countableSubAtdItems: Array<number>) {
            let self = this;
            let addText = "", subText = "";
            if (countableAddAtdItems && countableAddAtdItems.length > 0) {
                addText = "" + _.map(countableAddAtdItems, (id) => {
                    let finded = _.find(sourceName, (item) => { return id === item.attendanceItemId; });
                    return finded === undefined ? "" : finded.attendanceItemName
                }).join("+");
            }
            if (countableSubAtdItems && countableSubAtdItems.length > 0) {
                subText = '-' + _.map(countableSubAtdItems, (id) => {
                    let finded = _.find(sourceName, (item) => { return id === item.attendanceItemId; });
                    return finded === undefined ? "" : finded.attendanceItemName
                }).join("-");
            }
            self.displayAttendanceItemSelections_BA2_3(addText + subText);
        }

        /**
         * close dialog B and return result
         */
        btnDecision() {
            let self = this;
            $('.nts-input').filter(":enabled").trigger("validate");
            if (errors.hasError() === true) {
                return;
            }
            
            switch (self.category()) {
                case sharemodel.CATEGORY.DAILY: {
                    let workRecordExtractingCondition = self.workRecordExtractingCondition();
                    let isOk: boolean = true;
                    if (workRecordExtractingCondition.checkItem() == enItemCheck.Time
                        || workRecordExtractingCondition.checkItem() == enItemCheck.Times
                        || workRecordExtractingCondition.checkItem() == enItemCheck.AmountOfMoney
                        || workRecordExtractingCondition.checkItem() == enItemCheck.TimeOfDate
                        || workRecordExtractingCondition.checkItem() == enItemCheck.CountinuousTime
                    ) {
                        self.initialDataOfErAlAtdItemCon();
                        // validate comparison range
                        let group1 = workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1();
                        let listErAlAtdItemCondition = group1.lstErAlAtdItemCon();
                        let erAlAtdItemCondition = listErAlAtdItemCondition[0];
                        if (self.comparisonRange().checkValidOfRange(
                            workRecordExtractingCondition.checkItem()
                            , 1)) {
                            erAlAtdItemCondition.compareOperator(self.comparisonRange().comparisonOperator());
                            erAlAtdItemCondition.compareStartValue(self.comparisonRange().minValue());
                            erAlAtdItemCondition.compareEndValue(self.comparisonRange().maxValue());
                            erAlAtdItemCondition.singleAtdItem(self.comparisonRange().minValue());
                            //clear 
                            //listErAlAtdItemCondition = listErAlAtdItemCondition.splice(0, 1);
                            // workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon(listErAlAtdItemCondition);
                            //workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group2().lstErAlAtdItemCon([]);

                        } else {
                            isOk = false;
                        }
                    } else if (workRecordExtractingCondition.checkItem() == enItemCheck.CountinuousWork
                        || workRecordExtractingCondition.checkItem() == enItemCheck.CountinuousTimeZone) {
                        // workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group1().lstErAlAtdItemCon([]);
                        //workRecordExtractingCondition.errorAlarmCondition().atdItemCondition().group2().lstErAlAtdItemCon([]);
                    }
                    if (isOk) {
                        let retData = ko.toJS(workRecordExtractingCondition);
                        retData = shareutils.convertArrayOfWorkRecordExtractingConditionToJS(retData, workRecordExtractingCondition);
                        windows.setShared('outputKal003b', retData);
                        windows.close();
                    }
                    break;
                }
                case sharemodel.CATEGORY.MONTHLY: {
                    let retData = ko.toJS(self.extraResultMonthly());
                    windows.setShared('outputKal003b', retData);
                    windows.close();
                    break;
                }
                //MinhVV add
                case sharemodel.CATEGORY.MULTIPLE_MONTHS: {
                    let isOk: boolean = true;  

                    let mulMonCheckItem = self.mulMonCheckCondSet().typeCheckItem();
                    if (mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIME
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIME
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIMES
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_AMOUNT
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIME
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIMES
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_AMOUNT
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_DAYS
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS
                        || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_DAYS
                    ) {
                        // khoi tao du lieu mac dinh ban dau 
                        self.initialDataOfErAlAtdItemConMultipleMonth()
                        if (self.comparisonRange().checkValidOfRangeCategory9(mulMonCheckItem, 1)) {
                            self.mulMonCheckCondSet().erAlAtdItem().compareOperator(self.comparisonRange().comparisonOperator());
                            self.mulMonCheckCondSet().erAlAtdItem().compareStartValue(self.comparisonRange().minValue());
                            self.mulMonCheckCondSet().erAlAtdItem().compareEndValue(self.comparisonRange().maxValue());
                            self.mulMonCheckCondSet().erAlAtdItem().singleAtdItem(self.comparisonRange().minValue());
                        } else {
                            isOk = false;
                        }
                    }
                    if (isOk) {
                        let self = this;
                        let retData = ko.toJS(self.mulMonCheckCondSet());
                        retData = shareutils.convertArrayOfMulMonCheckCondSetToJS(retData, self.mulMonCheckCondSet());
                        windows.setShared('outputKal003b', retData);
                        windows.close();
                    }
                    break;
                }
                case sharemodel.CATEGORY.SCHEDULE_DAILY:
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparisonOperator(self.comparisonRange().comparisonOperator());
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareStartValue(self.comparisonRange().minValue());
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareEndValue(self.comparisonRange().maxValue());
                    let alchecktargetcondition = {
                        filterByBusinessType: false,
                        filterByJobTitle: false,
                        filterByEmployment: false,
                        filterByClassification: false,
                        lstBusinessTypeCode: [],
                        lstJobTitleId: [],
                        lstEmploymentCode: [],
                        lstClassificationCode: [],    
                    };
                    if (self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition == null) {
                        self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition = ko.observable();
                    }
                    self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition(alchecktargetcondition);

                    let retData = ko.toJS(self.workRecordExtractingCondition());
                    retData = shareutils.convertArrayOfWorkRecordExtractingConditionToJS(retData, self.workRecordExtractingCondition());
                    windows.setShared('outputKal003b', retData);
                    windows.close();
                    break;
                case sharemodel.CATEGORY.SCHEDULE_MONTHLY:
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition(self.scheCheckTypeCondition());
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().comparisonOperator(self.comparisonRange().comparisonOperator());
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue(self.comparisonRange().minValue());
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue(self.comparisonRange().maxValue());
                    if (self.workRecordExtractingCondition().checkItem() == 3) {
                        let startValue = self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[0].value;
                        let endValue = self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[1].value;
                        self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue(startValue);
                        self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue(endValue);
                    }
                    let alchecktargetcondition = {
                        filterByBusinessType: false,
                        filterByJobTitle: false,
                        filterByEmployment: false,
                        filterByClassification: false,
                        lstBusinessTypeCode: [],
                        lstJobTitleId: [],
                        lstEmploymentCode: [],
                        lstClassificationCode: [],    
                    };
                    if (self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition == null) {
                        self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition = ko.observable();
                    }
                    self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition(alchecktargetcondition);

                    let retData = ko.toJS(self.workRecordExtractingCondition());
                    retData = shareutils.convertArrayOfWorkRecordExtractingConditionToJS(retData, self.workRecordExtractingCondition());
                    windows.setShared('outputKal003b', retData);
                    windows.close();
                    break;
                case sharemodel.CATEGORY.SCHEDULE_YEAR:
                case sharemodel.CATEGORY.WEEKLY:
                    let alchecktargetcondition = {
                        filterByBusinessType: false,
                        filterByJobTitle: false,
                        filterByEmployment: false,
                        filterByClassification: false,
                        lstBusinessTypeCode: [],
                        lstJobTitleId: [],
                        lstEmploymentCode: [],
                        lstClassificationCode: [],    
                    };
                    if (self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition == null) {
                        self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition = ko.observable();
                    }
                    self.workRecordExtractingCondition().errorAlarmCondition().alCheckTargetCondition(alchecktargetcondition);
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition(self.scheCheckTypeCondition());                 
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().comparisonOperator(self.comparisonRange().comparisonOperator());
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue(self.comparisonRange().minValue());
                    self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue(self.comparisonRange().maxValue());
                    let retData = ko.toJS(self.workRecordExtractingCondition());
                    retData = shareutils.convertArrayOfWorkRecordExtractingConditionToJS(retData, self.workRecordExtractingCondition());
                    windows.setShared('outputKal003b', retData);
                    windows.close();
                    break;
                default: break;
            }

        }
        /**
         * close dialog B and return result
         */
        closeDialog() {
            windows.setShared('outputKal003b', undefined);
            windows.close();
        }
        //MinhVV start 
        fillTextDisplayTargetMulMon(defered, currentAtdItemCondition) {
            let self = this;
            self.displayAttendanceItemSelections_BA2_3("");
            if (currentAtdItemCondition.countableAddAtdItems().length > 0) {
                service.getMonthlyAttendanceItemByCodes(currentAtdItemCondition.countableAddAtdItems()).then((lstItems) => {
                    if (lstItems && lstItems.length > 0) {
                        for (let i = 0; i < lstItems.length; i++) {
                            let operator = (i === (lstItems.length - 1)) ? "" : " + ";
                            self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + lstItems[i].attendanceItemName + operator);
                        }
                        $("#display-target-item-category9").trigger("validate");
                    }
                    if (currentAtdItemCondition.countableSubAtdItems().length > 0) {
                        service.getMonthlyAttendanceItemByCodes(currentAtdItemCondition.countableSubAtdItems()).then((lstItems) => {
                            if (lstItems && lstItems.length > 0) {
                                for (let i = 0; i < lstItems.length; i++) {
                                    let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                                    let beforeOperator = (i === 0) ? " - " : "";
                                    self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + beforeOperator + lstItems[i].attendanceItemName + operator);
                                }
                                $("#display-target-item-category9").trigger("validate");
                            }
                        });
                    }
                }, function(rejected) {
                    defered.resolve();
                });
            } else if (currentAtdItemCondition.countableSubAtdItems().length > 0) {
                service.getMonthlyAttendanceItemByCodes(currentAtdItemCondition.countableSubAtdItems()).then((lstItems) => {
                    if (lstItems && lstItems.length > 0) {
                        for (let i = 0; i < lstItems.length; i++) {
                            let operator = (i === (lstItems.length - 1)) ? "" : " - ";
                            let beforeOperator = (i === 0) ? " - " : "";
                            self.displayAttendanceItemSelections_BA2_3(self.displayAttendanceItemSelections_BA2_3() + beforeOperator + lstItems[i].attendanceItemName + operator);
                        }
                        $("#display-target-item-category9").trigger("validate");
                    }
                }, function(rejected) {
                    defered.resolve();
                });
            }

            //            }
            return defered.promise();
        }

        private initialMultiMonthScreen(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.initialMulMon().done(() => {
                self.settingEnableComparisonMaxValueFieldExtra();
                dfd.resolve();
            });

            dfd.resolve();
            return dfd.promise();
        }

        private initialMulMonItemChkItemComparison(defered) {
            let self = this;
            self.fillTextDisplayTargetMulMon(defered, self.mulMonCheckCondSet().erAlAtdItem());
        }
        //MinhVV
        private initialMulMon(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            self.initialMulMonItemChkItemComparison(dfd);
            dfd.resolve();
            return dfd.promise();
        }
        //MinhVV initial default data of ErAlAtdItemConMultipleMonth
        private initialDataOfErAlAtdItemConMultipleMonth() {
            let self = this, mulMonCheckCond = self.mulMonCheckCondSet();
            let checkItem = mulMonCheckCond.typeCheckItem();
            if (!(checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIME
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIME
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIMES
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_AMOUNT
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIME
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIMES
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_AMOUNT
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_DAYS
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS
                || checkItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_DAYS)) {
                return;
            }
            let conditionAtr = CONDITIONATR.TIMES;
            switch (checkItem) {
                case TYPECHECKWORKRECORDMULTIPLEMONTH.TIME:          //時間
                case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIME:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIME: {
                    conditionAtr = CONDITIONATR.TIME;
                    break;
                }
                case TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIMES:      //回数
                case TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIMES: {
                    conditionAtr = CONDITIONATR.TIMES;
                    break;
                }
                case TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT: //金額
                case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_AMOUNT:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_AMOUNT: {
                    conditionAtr = CONDITIONATR.AMOUNT;
                    break;
                }
                case TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS:                 //日数
                case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_DAYS:
                case TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_DAYS: {
                    conditionAtr = CONDITIONATR.DAYS;
                    break;
                }
                default:
                    return;
            }

            self.mulMonCheckCondSet().erAlAtdItem().conditionAtr(conditionAtr);
            self.mulMonCheckCondSet().erAlAtdItem().conditionType(ConditionType.FIXED_VALUE);

        }
        private initComparisonValueRangeMulMon(): model.ComparisonValueRange {
            let self = this;
            let erAlAtdItemCondition = self.mulMonCheckCondSet().erAlAtdItem();

            let comparisonValueRange;
            let mulMonCheckItem = self.mulMonCheckCondSet().typeCheckItem();
            if (mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIME
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_TIMES
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_AMOUNT
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.CONTINUOUS_DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS
                || mulMonCheckItem == TYPECHECKWORKRECORDMULTIPLEMONTH.NUMBER_DAYS) {
                if (erAlAtdItemCondition.compareOperator() > COMPARETYPE.LESS_OR_EQUAL
                    || erAlAtdItemCondition.conditionType() == ConditionType.FIXED_VALUE) {
                    comparisonValueRange = new model.ComparisonValueRange(
                        self.mulMonCheckCondSet().typeCheckItem
                        , erAlAtdItemCondition.compareOperator
                        , erAlAtdItemCondition.compareStartValue()
                        , erAlAtdItemCondition.compareEndValue());
                } else {
                    comparisonValueRange = new model.ComparisonValueRange(
                        self.mulMonCheckCondSet().typeCheckItem
                        , erAlAtdItemCondition.compareOperator
                        , erAlAtdItemCondition.singleAtdItem()
                        , erAlAtdItemCondition.singleAtdItem());
                }
            } else {
                comparisonValueRange = new model.ComparisonValueRange(
                    self.mulMonCheckCondSet().typeCheckItem
                    , erAlAtdItemCondition.compareOperator
                    , 0
                    , 0);
            }
            return comparisonValueRange;
        }
        
        /**
         * Process with category is schedule daily
         */
        private processScheduleDaily(option): void {
            let self = this;
            
            self.setting = $.extend({}, shareutils.getDefaultWorkRecordExtractingCondition(0), option.data);

            let workRecordExtractingCond = shareutils.convertTransferDataToWorkRecordExtractingCondition(self.setting);
            self.workRecordExtractingCondition = ko.observable(workRecordExtractingCond);
            // setting comparison value range
    
            self.comparisonRange = ko.observable(self.initComparisonValueRange());
            self.resetComparisonRangeMinMaxValueNull();
            self.checkItemTemp = ko.observable(self.workRecordExtractingCondition().checkItem());
            
            self.controlShowPattern(self.workRecordExtractingCondition().checkItem());
            self.scheduleDailyShowTimeEditor(self.workRecordExtractingCondition().checkItem());
            
            self.settingEnableComparisonMaxValueField(false);
            
            self.initialScheduleDaily();
            
            // change select item check
            self.workRecordExtractingCondition().checkItem.subscribe((itemCheck) => {
                errors.clearAll();
                
                self.settingEnableComparisonMaxValueField(false);
                self.controlShowPattern(itemCheck);
                self.scheduleDailyShowTimeEditor(itemCheck);
                
                self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual(0);
                if (itemCheck == 2) {
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual(1);    
                }
                self.workRecordExtractingCondition().errorAlarmCondition().workTimeCondition().comparePlanAndActual(0);
                self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().planLstWorkType([]);
                self.workRecordExtractingCondition().errorAlarmCondition().workTimeCondition().planLstWorkTime([]);
                self.workRecordExtractingCondition().errorAlarmCondition().displayMessage("");
                self.workRecordExtractingCondition().errorAlarmCondition().continuousPeriod(null);
                self.comparisonRange().comparisonOperator(0);
                self.comparisonRange().minAmountOfMoneyValue(null);
                self.comparisonRange().maxAmountOfMoneyValue(null);
                self.comparisonRange().minTimeValue(null);
                self.comparisonRange().maxTimeValue(null);
                self.comparisonRange().minTimesValue(null);
                self.comparisonRange().maxTimesValue(null);
                self.comparisonRange().maxTimeWithinDayValue(null);
                self.comparisonRange().minTimeWithinDayValue(null);
                if ((itemCheck && itemCheck != undefined) || itemCheck === 0) {
                    self.initialScheduleDaily().then(function() {
                        self.settingEnableComparisonMaxValueField(false);
                        if ((self.checkItemTemp() || self.checkItemTemp() == 0) && self.checkItemTemp() != itemCheck) {
                            setTimeout(function() { self.displayAttendanceItemSelections_BA2_3(""); }, 200);
                        }
                    }); 
                }
                $(".nts-input").ntsError("clear");
            });
            self.comparisonRange().comparisonOperator.subscribe((operN) => {
                self.settingEnableComparisonMaxValueField(false);
                $(".nts-input").ntsError("clear");
                self.validateStartEnd();
            });
            self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual = ko.observable(self.setting.errorAlarmCondition.workTypeCondition.comparePlanAndActual);
            self.required_BA1_4 = ko.observable(self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual() > 0);
            self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().comparePlanAndActual.subscribe((newV) => {
                self.required_BA1_4(newV > 0);
                $(".nts-input").ntsError("clear");
            });
            
            self.registerEventChangeCompareValue();
        }
        
        /**
         * Process with category is schedule monthly
         */
        private processScheduleMonthly(option): void {
            let self = this;
            
            self.setting = $.extend({}, shareutils.getDefaultWorkRecordExtractingCondition(0), option.data);

            let workRecordExtractingCond = shareutils.convertTransferDataToWorkRecordExtractingCondition(self.setting);
            self.workRecordExtractingCondition = ko.observable(workRecordExtractingCond);
            
            // setting comparison value range
            self.comparisonRange = ko.observable(self.initComparisonValueRange());
            self.resetComparisonRangeMinMaxValueNull();
            self.checkItemTemp = ko.observable(self.workRecordExtractingCondition().checkItem());
            self.scheduleMonthlyControlShowPattern(self.workRecordExtractingCondition().checkItem());
            self.scheduleMonthlyShowTimeEditor(self.workRecordExtractingCondition().checkItem());
            self.getChangeListCheckTimeType(self.workRecordExtractingCondition().checkItem());
            
            self.settingEnableComparisonMaxValueField(false);
            self.enableRemainNumberDay2(self.workRecordExtractingCondition().checkItem());
            
            self.scheCheckTypeCondition(self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition());
            self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition.subscribe((value) => {
                self.scheCheckTypeCondition(value);
            });
            
            // change select item check
            self.workRecordExtractingCondition().checkItem.subscribe((itemCheck) => {
                errors.clearAll();
                
                self.settingEnableComparisonMaxValueField(false);
                self.enableRemainNumberDay2(itemCheck);
                self.getChangeListCheckTimeType(itemCheck);
                self.scheduleMonthlyControlShowPattern(itemCheck);
                self.scheduleMonthlyShowTimeEditor(itemCheck);
                
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition(new sharemodel.ScheMonCond());
                self.workRecordExtractingCondition().errorAlarmCondition().displayMessage("");
                self.workRecordExtractingCondition().errorAlarmCondition().continuousPeriod(null);
                self.comparisonRange().comparisonOperator(0);
                self.comparisonRange().minAmountOfMoneyValue(null);
                self.comparisonRange().maxAmountOfMoneyValue(null);
                self.comparisonRange().minTimeValue(null);
                self.comparisonRange().maxTimeValue(null);
                self.comparisonRange().minTimesValue(null);
                self.comparisonRange().maxTimesValue(null);
                self.comparisonRange().maxTimeWithinDayValue(null);
                self.comparisonRange().minTimeWithinDayValue(null);
                self.comparisonRange().minValue(null);
                self.comparisonRange().maxValue(null);
                $(".nts-input").ntsError("clear");
            });
            self.comparisonRange().comparisonOperator.subscribe((operN) => {
                self.settingEnableComparisonMaxValueField(false);
                self.enableRemainNumberDay2(self.workRecordExtractingCondition().checkItem());
                $(".nts-input").ntsError("clear");
                self.validateStartEnd();
            });
            
            self.registerEventChangeCompareValue();
        }
        
        /**
         * Process with category is schedule Year
         */
        private processScheduleYear(option): void {
            let self = this;
            
            self.setting = $.extend({}, shareutils.getDefaultWorkRecordExtractingCondition(0), option.data);

            let workRecordExtractingCond = shareutils.convertTransferDataToWorkRecordExtractingCondition(self.setting);
            self.workRecordExtractingCondition = ko.observable(workRecordExtractingCond);
            
            // setting comparison value range
            self.comparisonRange = ko.observable(self.initComparisonValueRange());
            self.resetComparisonRangeMinMaxValueNull();
            self.checkItemTemp = ko.observable(self.workRecordExtractingCondition().checkItem());
            self.scheduleYearShowTimeEditor(self.workRecordExtractingCondition().checkItem());
            self.scheduleYearControlShowPattern(self.workRecordExtractingCondition().checkItem());
            self.getChangeListCheckTimeTypeScheduleYear(self.workRecordExtractingCondition().checkItem());
            
            self.settingEnableComparisonMaxValueField(false);
            
            self.scheCheckTypeCondition(self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition());
            self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().scheCheckCondition.subscribe((value) => {
                self.scheCheckTypeCondition(value);
            });
            
            // change select item check
            self.workRecordExtractingCondition().checkItem.subscribe((itemCheck) => {
                errors.clearAll();
                
                self.settingEnableComparisonMaxValueField(false);
                self.scheduleYearShowTimeEditor(itemCheck);
                self.getChangeListCheckTimeTypeScheduleYear(itemCheck);
                self.scheduleYearControlShowPattern(itemCheck);
                
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition(new sharemodel.ScheMonCond());
                self.workRecordExtractingCondition().errorAlarmCondition().displayMessage("");
                self.workRecordExtractingCondition().errorAlarmCondition().continuousPeriod(null);
                self.comparisonRange().comparisonOperator(0);
                self.comparisonRange().minAmountOfMoneyValue(null);
                self.comparisonRange().maxAmountOfMoneyValue(null);
                self.comparisonRange().minTimeValue(null);
                self.comparisonRange().maxTimeValue(null);
                self.comparisonRange().minTimesValue(null);
                self.comparisonRange().maxTimesValue(null);
                self.comparisonRange().maxTimeWithinDayValue(null);
                self.comparisonRange().minTimeWithinDayValue(null);
                self.comparisonRange().minValue(null);
                self.comparisonRange().maxValue(null);
                $(".nts-input").ntsError("clear");
            });
            self.comparisonRange().comparisonOperator.subscribe((operN) => {
                self.settingEnableComparisonMaxValueField(false);
                $(".nts-input").ntsError("clear");
                self.validateStartEnd();
            });
            
            self.registerEventChangeCompareValue();
        }
        
        /**
         * Process with category is schedule Weekly
         */
        private processScheduleWeekly(option): void {
            let self = this;
            
            let defaultSetting = shareutils.getDefaultWorkRecordExtractingCondition(0)
            defaultSetting.errorAlarmCondition().continuousPeriod(null);
            self.setting = $.extend({}, defaultSetting, option.data);

            let workRecordExtractingCond = shareutils.convertTransferDataToWorkRecordExtractingCondition(self.setting);
            self.workRecordExtractingCondition = ko.observable(workRecordExtractingCond);
            
            // setting comparison value range
            self.comparisonRange = ko.observable(self.initComparisonValueRange());
            self.resetComparisonRangeMinMaxValueNull();
            self.checkItemTemp = ko.observable(self.workRecordExtractingCondition().checkItem());
            self.weeklyShowTimeEditor(self.workRecordExtractingCondition().checkItem());
            self.scheduleWeeklyControlShowPattern(self.workRecordExtractingCondition().checkItem());
            
            self.settingEnableComparisonMaxValueField(false);
            self.fillTextDisplayTargetWeekly(null, self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition());
            
            // change select item check
            self.workRecordExtractingCondition().checkItem.subscribe((itemCheck) => {
                errors.clearAll();
                
                self.settingEnableComparisonMaxValueField(false);
                self.weeklyShowTimeEditor(itemCheck);
                self.scheduleWeeklyControlShowPattern(itemCheck);
                
                self.workRecordExtractingCondition().errorAlarmCondition().continuousPeriod(null);
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition(new sharemodel.ScheMonCond());
                self.workRecordExtractingCondition().errorAlarmCondition().displayMessage("");
                self.workRecordExtractingCondition().errorAlarmCondition().continuousPeriod(null);
                self.comparisonRange().comparisonOperator(0);
                self.comparisonRange().minAmountOfMoneyValue(null);
                self.comparisonRange().maxAmountOfMoneyValue(null);
                self.comparisonRange().minTimeValue(null);
                self.comparisonRange().maxTimeValue(null);
                self.comparisonRange().minTimesValue(null);
                self.comparisonRange().maxTimesValue(null);
                self.comparisonRange().maxTimeWithinDayValue(null);
                self.comparisonRange().minTimeWithinDayValue(null);
                self.comparisonRange().minValue(null);
                self.comparisonRange().maxValue(null);
                self.displayAttendanceItemSelections_BA2_3("");
                $(".nts-input").ntsError("clear");
            });
            self.comparisonRange().comparisonOperator.subscribe((operN) => {
                self.settingEnableComparisonMaxValueField(false);
                $(".nts-input").ntsError("clear");
                self.validateStartEnd();
            });
            
            self.registerEventChangeCompareValue();
        }
        
        private registerEventChangeCompareValue(): void {
            let self = this;
            self.enableComparisonMaxValue.subscribe((val) => {
                if (!val) {
                    self.comparisonRange().maxValue(null); 
                }
            });
            
            self.comparisonRange().minValue.subscribe((val) => {
               setTimeout(() => {
                   self.validateStartEnd();
               }, 25);
            });
            
            self.comparisonRange().maxValue.subscribe((val) => {
               setTimeout(() => {
                    self.validateStartEnd();
               }, 25);
            });
        }
        
        /**
         * Validate comparison when operator > 5
         */
        private validateComparison(): void {
            let self = this,
            if (self.comparisonRange().comparisonOperator() <= 5) {
                return;
            }
            
            if (self.category() == sharemodel.CATEGORY.SCHEDULE_MONTHLY) {
                if (self.workRecordExtractingCondition().checkItem() == 3) {
                    self.comparisonRange().minValue(self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[0].value());
                    self.comparisonRange().maxValue(self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[1].value());
                }
            }
            
            if (self.comparisonRange().comparisonOperator() == 7 || self.comparisonRange().comparisonOperator() == 9) {
                setTimeout(() => {
                    if (parseInt(self.comparisonRange().minValue()) > parseInt(self.comparisonRange().maxValue())) {
                        $('.endValue').ntsError('set', { messageId: "Msg_836" });
                    }
                }, 25);
            }
            if (self.comparisonRange().comparisonOperator() == 6 || self.comparisonRange().comparisonOperator() == 8) {
                setTimeout(() => {
                    if (parseInt(self.comparisonRange().minValue()) >= parseInt(self.comparisonRange().maxValue())) {
                        $('.endValue').ntsError('set', { messageId: "Msg_836" });
                    }
                }, 25);
            }
        }
        
        private validateStartEnd(el: string = '.endValue'): void {
            $(el).ntsError("clear");
            
            const vm = this;
            let maxValue = parseInt(vm.comparisonRange().maxValue());
            let minValue = parseInt(vm.comparisonRange().minValue());
            let operator = vm.comparisonRange().comparisonOperator();
            if (_.isNil(maxValue) ){
                return;
            }

            if  (( _.indexOf([RangeCompareType.BETWEEN_RANGE_OPEN, RangeCompareType.OUTSIDE_RANGE_OPEN], operator) != -1
                    && minValue >= maxValue )
                || ( _.indexOf([RangeCompareType.BETWEEN_RANGE_OPEN, RangeCompareType.OUTSIDE_RANGE_OPEN], operator) == -1
                    && minValue > maxValue ))
            {
                $(el).ntsError('set', { messageId: "Msg_927" });
            }
            return;
        }
        
        private resetComparisonRangeMinMaxValueNull() {
            let self = this;
            if (self.category() == sharemodel.CATEGORY.SCHEDULE_DAILY) {
                if (self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareStartValue == null) {
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareStartValue = ko.observable(null);
                }
                if (self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareEndValue == null) {
                    self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareEndValue = ko.observable(null);
                }
                
                self.comparisonRange().minValue(self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareStartValue());
                self.comparisonRange().maxValue(self.workRecordExtractingCondition().errorAlarmCondition().workTypeCondition().compareEndValue());
                return;
            }
            
            if (self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue == null
                || self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareStartValue() == null) {
                self.comparisonRange().minValue(null);  
            }
            if (self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue == null
                || self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().compareEndValue() == null) {
                self.comparisonRange().maxValue(null); 
            }
        }
        
        private initialScheduleDaily(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            switch (self.workRecordExtractingCondition().checkItem()) {
                case 0:          //時間
                    self.initialWorkTypes(dfd);
                    break;
                case 1:   //連続時間
                    self.initialWorkTypes(dfd);
                    break;
                case 2:   //連続時間帯
                    self.initialWorkTypes(dfd);
                    break;
                case 3: //連続勤務
                    self.initialDailyItemChkCountinuousTimeZone(dfd);
                    break;
                default:
                    break;
            }
            dfd.resolve();
            return dfd.promise();
        }
        
        /**
         * <Category=SCHEDULE_MONTHLY>
         * enable/disable input day 2
         */
        private enableRemainNumberDay2(checkItem): void {
            let self = this;
            if (checkItem != 3) {
                return;    
            }
            
            if (self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()) {
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[1].enable(self.comparisonRange().comparisonOperator() > 5);
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[1].required(self.comparisonRange().comparisonOperator() > 5);
                
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[0].value.subscribe((val) => {
                   self.comparisonRange().minValue(val);
                });
                
                self.workRecordExtractingCondition().errorAlarmCondition().monthlyCondition().inputs()[1].value.subscribe((val) => {
                   self.comparisonRange().maxValue(val);;
                });
            } 
        }
        
        /**
         * <Category=SCHEDULE_MONTHLY>
         * Get dropdow check condition by Monthly Check Item Type
         */
        private getChangeListCheckTimeType(itemCheck): void {
            let self = this;
            switch(itemCheck) {
                case 0:
                    self.listCheckTimeType(self.listTypeOfContrast());
                    break;
                case 1:
                    self.listCheckTimeType(self.listTypeOfTime());
                    break;
                case 2:
                    self.listCheckTimeType(self.listTypeOfDays());
                    break;
                case 3:
                    self.listCheckTimeType(self.listTypeOfVacations());
                    break;
                default:
                    self.listCheckTimeType(self.listTypeOfContrast());
                    break;    
            }
        }
        
        /**
         * <Category=SCHEDULE_DAILY>
         * show time editor or number editor by check item (チェック項目) 
         */
        private scheduleDailyShowTimeEditor(checkItem): void {
            let self = this;
            self.resetTimeAndNumberEditor();
            self.isTimeEditor(true);
        }
        
        /**
         * <Category=SCHEDULE_MONTHLY>
         * show time editor or number editor by check item (チェック項目) 
         */
        private scheduleMonthlyShowTimeEditor(checkItem): void {
            let self = this;
            self.resetTimeAndNumberEditor();
            switch(checkItem) {
                case 0:
                    self.isNumberEditor(true);
                    self.constraint("CheckUpperLimitComparison");
                    self.numberEditorOption(new NumberEditorOption({
                        grouplength: 0,
                        decimallength: 0,
                        placeholder: ''
                    }));
                    break;
                case 1:
                    self.isTimeEditor(true);
                    break;
                case 2:
                    self.isNumberEditor(true);
                    self.constraint("CheckUpperLimitDay");
                    self.numberEditorOption(new NumberEditorOption({
                        grouplength: 0,
                        decimallength: 1,
                        placeholder: ''
                    }));
                    break;
                
                default:
                    self.isNumberEditor(true);
                    break;    
            }
        }
        
        /**
         * <Category=SCHEDULE_YEAR>
         * show time editor or number editor by check item (チェック項目) 
         */
        private scheduleYearShowTimeEditor(checkItem): void {
            let self = this;
            self.resetTimeAndNumberEditor();
            switch(checkItem) {
                case 0:
                    self.isTimeEditor(true);
                    break;
                case 1:
                    self.isDayEditor(true);
                    break;
                default:
                    self.isTimeEditor(true);
                    break;    
            } 
        }
        
        /**
         * <Category=WEEKLY>
         * show time editor or number editor by check item (チェック項目) 
         */
        private weeklyShowTimeEditor(checkItem): void {
            let self = this;
            self.resetTimeAndNumberEditor();
            switch(checkItem) {
                case 1:
                case 4:
                    self.isTimeEditor(true);
                    break;
                case 2:
                case 5:
                    self.isDayEditor(true);
                    break;
                case 3:
                case 6:
                    self.isNumberEditor(true);
                    break;
                default:
                    self.isTimeEditor(true);
                    break;    
            } 
        }
        
        /**
         * <Category=SCHEDULE_YEAR>
         * Get dropdow check condition by Monthly Check Item Type
         */
        private getChangeListCheckTimeTypeScheduleYear(itemCheck): void {
            let self = this;
            switch(itemCheck) {
                case 0:
                    self.listCheckTimeType(self.listTypeOfTime());
                    break;
                case 1:
                    self.listCheckTimeType(self.listTypeOfDays());
                    break;
                default:
                    self.listCheckTimeType(self.listTypeOfTime());
                    break;    
            }
        }
        
        /**
         * <Category=SCHEDULE_DAILY>
         * The control show/hide screen pattern when change チェック項目
         */
        private controlShowPattern(itemCheck): void {
            let self = this;
            self.isPattern(true);
            switch(itemCheck) {
                case 0:
                    self.showPatternD();
                    break;
                case 1:
                    self.showPatternG();
                    break;
                case 2:
                    self.showPatternForContinuousWork();
                    break;
                case 3:
                    self.showPatternForContinuousTimeZone();
                    break;
                default:
                    self.resetPattern();
                    break;    
            }    
        }
        
        /**
         * <Category=SCHEDULE_MONTHLY>
         * The control show/hide screen pattern when change チェック項目
         */
        private scheduleMonthlyControlShowPattern(itemCheck): void {
            let self = this;
            self.isPattern(true);
            switch(itemCheck) {
                case 0:
                case 1:
                case 2:
                    self.showPatternB();
                    break;
                case 3:
                    self.showPatternForRemainingNumber();
                    break;
                default:
                    self.resetPattern();
                    break;    
            }    
        }
        
        /**
         * <Category=SCHEDULE_YEAR>
         * The control show/hide screen pattern when change チェック項目
         */
        private scheduleYearControlShowPattern(itemCheck): void {
            let self = this;
            self.isPattern(true);
            self.showPatternB();    
        }
        
        /**
         * <Category=WEEKLY>
         * The control show/hide screen pattern when change チェック項目
         */
        private scheduleWeeklyControlShowPattern(itemCheck): void {
            let self = this;
            self.isPattern(true);
            switch(itemCheck) {
                case 1:
                case 2:
                case 3:
                    self.showPatternA();
                    break;
                case 4:
                case 5:
                case 6:
                    self.showPatternE();
                    break;
                default:
                    self.resetPattern();
                    break;    
            }        
        }
        
        /**
         * Show screen pattern A
         */
        private showPatternA(): void {
            let self = this;
            self.resetPattern();
            self.isPatternA(true);
        }
        
        /**
         * Show screen pattern B
         */
        private showPatternB(): void {
            let self = this;
            self.resetPattern();
            self.isPatternB(true);
        }
        
        /**
         * Show screen pattern D
         */
        private showPatternD(): void {
            let self = this;
            self.resetPattern();
            self.isPatternD(true);
        }
        
        /**
         * Show screen pattern E
         */
        private showPatternE(): void {
            let self = this;
            self.resetPattern();
            self.isPatternE(true);
        }
        
        /**
         * Show screen pattern G
         */
        private showPatternG(): void {
            let self = this;
            self.resetPattern();
            self.isPatternG(true);
        }
        
        /**
         * For continuous work (連続勤務の場合)
         */
        private showPatternForContinuousWork(): void {
            let self = this;
            self.resetPattern();
            self.isPatternForContinuousWork(true);
        }
        
        /**
         * For continuous time zones (連続時間帯の場合)
         */
        private showPatternForContinuousTimeZone(): void {
            let self = this;
            self.resetPattern();
            self.isPatterForContinuousTimeZone(true);
        }
        
        private showPatternForRemainingNumber(): void {
            let self = this;
            self.resetPattern();
            self.isPatternForRemainingNumber(true);    
        }
        
        /**
         * Hide all screen pattern
         */
        private resetPattern(): void {
            let self = this;
            self.isPatternA(false);
            self.isPatternB(false);
            self.isPatternD(false);
            self.isPatternE(false);
            self.isPatternG(false);
            self.isPatternForContinuousWork(false);
            self.isPatterForContinuousTimeZone(false);
            self.isPatternForRemainingNumber(false);  
        }
        
        /**
         * Hide time and number editor
         */
        private resetTimeAndNumberEditor(): void {
            let self = this;
            self.isTimeEditor(false);
            self.isNumberEditor(false);
            self.isDayEditor(false);
        }
    }
    //MinhVV Add
    export enum CONDITIONATR {

        TIMES = 0,

        TIME = 1,

        TIME_WITH_DAY = 2,

        AMOUNT = 3,

        DAYS = 4
    }
    export enum MONTHLYATTENDANCEITEMATR {
        TIME = 1,
        /* 回数 */
        NUMBER = 2,
        /* 日数 */
        DAYS = 3,
        /* 金額 */
        AMOUNT = 4,
        /* マスタを参照する */
        REFER_TO_MASTER = 5


    }
    
    export enum WEEKLYCHECKITEMTYPE {
        TIME = 1,
        /* 日数 */
        DAYS = 2,
        /* 回数 */
        TIMES = 3,
        /* 連続時間 */
        CONTINUOUS_TIME = 4,
        /* 連続時間 */
        CONTINUOUS_DAY = 5,
        /* 連続回数 */
        CONTINUOUS_TIMES = 6
    }


    enum DAILYATTENDANCEITEMATR {
        /* コード */
        Code = 0,
        /* マスタを参照する */
        ReferToMaster = 1,
        /* 回数*/
        NumberOfTime = 2,
        /* 金額*/
        AmountOfMoney = 3,
        /* 区分 */
        Classification = 4,
        /* 時間 */
        Time = 5,
        /* 時刻*/
        TimeOfDay = 6,
        /* 文字 */
        Character = 7
    }


    export enum COMPARETYPE {
        LESS_OR_EQUAL = 5 //(5, "Enum_SingleValueCompareType_LessOrEqual");
    }

    export enum TYPECHECKWORKRECORDMULTIPLEMONTH {
        TIME = 0,

        TIMES = 1,

        AMOUNT = 2,

        AVERAGE_TIME = 3,

        AVERAGE_TIMES = 4,

        AVERAGE_AMOUNT = 5,

        CONTINUOUS_TIME = 6,

        CONTINUOUS_TIMES = 7,

        CONTINUOUS_AMOUNT = 8,

        NUMBER_TIME = 9,

        NUMBER_TIMES = 10,

        NUMBER_AMOUNT = 11,

        DAYS = 12,

        AVERAGE_DAYS = 13,

        CONTINUOUS_DAYS = 14,

        NUMBER_DAYS = 15
    }

    /**
     * The enum of ROLE TYPE 
     */
    export enum enCategory {
        Daily = 0,
        Weekly = 1,
        Monthly = 2

    }
    export enum enItemCheck {
        Time = 0, // 時間
        Times = 1, // 回数
        AmountOfMoney = 2, // 金額
        TimeOfDate = 3, // 時刻
        CountinuousTime = 4, // 連続時間
        CountinuousWork = 5, // 連続勤務
        CountinuousTimeZone = 6, // 連続時間帯
        CompoundCondition = 7// 複合条件
    }

    export enum ConditionType {

        /* 固定値 */
        FIXED_VALUE = 0, //, "Enum_ConditionType_FixedValue"),
        /* 勤怠項目 */
        ATTENDANCE_ITEM = 1 //, "Enum_ConditionType_AttendanceItem");
    }
    
    export enum RangeCompareType{
        // 範囲の間（境界値を含まない）（＜＞）
        BETWEEN_RANGE_OPEN = 6,
        /* 範囲の間（境界値を含む）（≦≧） */
        BETWEEN_RANGE_CLOSED = 7,
        /* 範囲の外（境界値を含まない）（＞＜） */
        OUTSIDE_RANGE_OPEN = 8,
        /* 範囲の外（境界値を含む）（≧≦） */
        OUTSIDE_RANGE_CLOSED = 9,
    }

    module model {
        export class ItemModelKdl002 {
            code: string;
            name: string;
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
        export interface IEnumModel {
            value: number;
            fieldName: string;
            localizedName: string;
        }

        export class EnumModel {
            value: KnockoutObservable<number>;
            fieldName: string;
            localizedName: string;
            constructor(param: IEnumModel) {
                let self = this;
                self.value = ko.observable(param.value || -1);
                self.fieldName = param.fieldName || '';
                self.localizedName = param.localizedName || '';
            }
        }

        /**
         * ComparisonValueRange
         */
        export class ComparisonValueRange {
            minTimeValue: KnockoutObservable<number> = ko.observable(0);
            maxTimeValue: KnockoutObservable<number> = ko.observable(0);

            minTimesValue: KnockoutObservable<number> = ko.observable(0);
            maxTimesValue: KnockoutObservable<number> = ko.observable(0);

            minAmountOfMoneyValue: KnockoutObservable<number> = ko.observable(null);
            maxAmountOfMoneyValue: KnockoutObservable<number> = ko.observable(null);

            minTimeWithinDayValue: KnockoutObservable<number> = ko.observable(0);
            maxTimeWithinDayValue: KnockoutObservable<number> = ko.observable(0);
            minValue: KnockoutObservable<number> = ko.observable(0);
            maxValue: KnockoutObservable<number> = ko.observable(0);

            checkItem: KnockoutObservable<number> = ko.observable(0);
            comparisonOperator: KnockoutObservable<number> = ko.observable(0);

            isChecking: boolean = false;
            minTimesValueDay: KnockoutObservable<number> = ko.observable(0);
            maxTimesValueDay: KnockoutObservable<number> = ko.observable(0);
            constructor(checkItem: KnockoutObservable<number>, comOper: KnockoutObservable<number>, minVal: number, maxVal: number) {
                let self = this;
                minVal = self.convertToNumber(minVal);
                maxVal = self.convertToNumber(maxVal);
                self.minValue(minVal || 0);
                self.maxValue(maxVal || 0);
                self.checkItem = checkItem;
                self.comparisonOperator = comOper;
                //時間 - 0: check time
                //連続時間 - 4:  check time
                self.minTimeValue(minVal);
                self.maxTimeValue(maxVal);
                //回数 - 1: check times
                self.minTimesValue(minVal);
                self.maxTimesValue(maxVal);
                //金額 - 2: check amount of money
                self.minAmountOfMoneyValue(minVal);
                self.maxAmountOfMoneyValue(maxVal);
                //時刻の場合 - 3: time within day
                self.minTimeWithinDayValue(minVal);
                self.maxTimeWithinDayValue(maxVal);

                //時間 - 0: check time
                //連続時間 - 4:  check time
                self.minTimeValue.subscribe((value) => {
                    self.settingMinValue(value);
                });
                self.maxTimeValue.subscribe((value) => {
                    self.settingMaxValue(value);
                });

                //回数 - 1: check times
                self.minTimesValue.subscribe((value) => {
                    self.settingMinValue(value);
                });
                self.maxTimesValue.subscribe((value) => {
                    self.settingMaxValue(value);
                });

                //金額 - 2: check amount of money
                self.minAmountOfMoneyValue.subscribe((value) => {
                    self.settingMinValue(value);
                });
                self.maxAmountOfMoneyValue.subscribe((value) => {
                    self.settingMaxValue(value);
                });

                //時刻の場合 - 3: time within day
                self.minTimeWithinDayValue.subscribe((value) => {
                    self.settingMinValue(value);
                });
                self.maxTimeWithinDayValue.subscribe((value) => {
                    self.settingMaxValue(value);
                });
                //日数 Times Value Day
                self.minTimesValueDay(minVal);
                self.maxTimesValueDay(maxVal);
                self.minTimesValueDay.subscribe((value) => {
                    self.settingMinValue(value);
                });
                self.maxTimesValueDay.subscribe((value) => {
                    self.settingMaxValue(value);
                });
            }

            private settingMinValue(val) {
                let self = this;
                if (self.minValue() == val) {
                    return;
                }
                self.minValue(val);
                if (self.category == 9) {
                    self.checkValidOfRangeCategory9(self.checkItem(), 0); //min
                } else {
                    self.checkValidOfRange(self.checkItem(), 0); //min
                }
            }
            private settingMaxValue(val) {
                let self = this;
                if (self.maxValue() == val) {
                    return;
                }

                self.maxValue(val);
                if (self.category == 9) {
                    self.checkValidOfRangeCategory9(self.checkItem(), 1); //min
                } else {
                    self.checkValidOfRange(self.checkItem(), 1);//max
                }

            }

            private convertToNumber(value: string | number): number {
                if (typeof value === "string") {
                    return parseInt(value);
                } else {
                    return value; // We know its a number 
                }
            }
            /**
            * valid range of comparison 
            */
            checkValidOfRange(checkItem: number, textBoxFocus: number): boolean {
                let self = this;
                let isValid: boolean = true;

                if (self.comparisonOperator() > 5) {
                    let mnValue: number = undefined;
                    let mxValue: number = undefined;
                    switch (checkItem) {
                        case enItemCheck.Time:          //時間 - 0: check time
                        case enItemCheck.CountinuousTime:   //連続時間 - 4:  check time
                            mnValue = self.minTimeValue();
                            mxValue = self.maxTimeValue();
                            break;
                        case enItemCheck.Times:       //回数 - 1: check times
                            mnValue = self.minTimesValue();
                            mxValue = self.maxTimesValue();
                            break;
                        case enItemCheck.AmountOfMoney: //金額 - 2: check amount of money
                            mnValue = self.minAmountOfMoneyValue();
                            mxValue = self.maxAmountOfMoneyValue();
                            break;
                        case enItemCheck.TimeOfDate:   //時刻の場合 - 3: time within day
                            mnValue = self.minTimeWithinDayValue();
                            mxValue = self.maxTimeWithinDayValue();
                            break;
                        default:
                            break;
                    }

                    if (mnValue != undefined && mxValue != undefined) {
                        if(typeof mnValue === "string" || typeof mxValue === "string"){
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            return;
                        }
                        isValid = self.compareValid(self.comparisonOperator(), mnValue, mxValue);
                    }
                }
                if (!isValid) {
                    if (textBoxFocus === 1) {
                        //max
                        setTimeout(() => {
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            $('#endValue').ntsError('set', { messageId: "Msg_836" });

                        }, 25);
                    } else {
                        setTimeout(() => {
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            $('#startValue').ntsError('set', { messageId: "Msg_836" });
                        }, 25);
                    }
                } else {
                    nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                    nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                }
                return isValid;
            }
            checkValidOfRangeCategory9(checkItem: number, textBoxFocus: number): boolean {
                let self = this;
                let isValid: boolean = true;

                if (self.comparisonOperator() > 5) {
                    let mnValue: number = undefined;
                    let mxValue: number = undefined;
                    switch (checkItem) {
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.TIME:
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIME:          //時間 - 0: check time
                            mnValue = self.minTimeValue();
                            mxValue = self.maxTimeValue();
                            break;
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.TIMES:       //回数 - 1: check times
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_TIMES:
                            mnValue = self.minTimesValue();
                            mxValue = self.maxTimesValue();
                            break;
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.AMOUNT: //金額 - 2: check amount of money
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_AMOUNT:
                            mnValue = self.minAmountOfMoneyValue();
                            mxValue = self.maxAmountOfMoneyValue();
                            break;
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.DAYS:        //日数 
                        case TYPECHECKWORKRECORDMULTIPLEMONTH.AVERAGE_DAYS:
                            mnValue = self.minTimesValueDay();
                            mxValue = self.maxTimesValueDay();
                            break;
                        default:
                            break;
                    }

                    if (mnValue != undefined && mxValue != undefined) {
                        if(typeof mnValue === "string" || typeof mxValue === "string"){
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            return;
                        }
                        isValid = self.compareValid(self.comparisonOperator(), mnValue, mxValue);
                    }
                }
                if (!isValid) {
                    if (textBoxFocus === 1) {
                        //max
                        setTimeout(() => {
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            $('#endValue').ntsError('set', { messageId: "Msg_836" });

                        }, 25);
                    } else {
                        setTimeout(() => {
                            nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                            nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                            $('#startValue').ntsError('set', { messageId: "Msg_836" });
                        }, 25);
                    }
                } else {
                    nts.uk.ui.errors.removeByCode($('#startValue'), 'Msg_836');
                    nts.uk.ui.errors.removeByCode($('#endValue'), 'Msg_836');
                }
                return isValid;
            }
            /**
             * execute check valid of range
             */
            private compareValid(comOper: number, minValue: number, maxValue: number): boolean {
                let rs = true;
                switch (comOper) {
                    case 6: // 範囲の間（境界値を含まない）（＜＞）
                    case 8: {// 範囲の外（境界値を含まない）（＞＜）
                        rs = Number(minValue) < Number(maxValue);
                        break;
                    }
                    case 7: // 範囲の間（境界値を含む）（≦≧）
                    case 9: {// 範囲の外（境界値を含む）（≧≦）
                        rs = Number(minValue) <= Number(maxValue);
                        break;
                    }
                    default:
                        break;
                }
                return rs;
            }
        }
    }
}
