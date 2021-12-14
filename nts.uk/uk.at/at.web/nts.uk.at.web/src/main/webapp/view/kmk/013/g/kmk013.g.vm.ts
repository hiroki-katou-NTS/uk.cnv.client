module nts.uk.at.view.kmk013.g {
    
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        export class ScreenModel {
            settingOptions: KnockoutObservableArray<ItemModel>;
            daysOfWeekOptions: KnockoutObservableArray<ItemModel>;
            goOutReasonOptions: KnockoutObservableArray<ItemModel>;
            
            flexSetting: KnockoutObservable<number>;
            aggWorkSet: KnockoutObservable<number>;
            multipleWorkSet: KnockoutObservable<number>;

            tempWorkSet: KnockoutObservable<number>;
            tempMaxUsage: KnockoutObservable<number>;
            timeTreatTemporarySame: KnockoutObservable<number>;

            midNightStartTime: KnockoutObservable<number>;
            midNightEndTime: KnockoutObservable<number>;

            startOfWeek: KnockoutObservable<number>;

            goOutUsage: KnockoutObservable<number>;
            goOutMaxUsage: KnockoutObservable<number>;
            initValueReasonGoOut: KnockoutObservable<number>;

            entranceExitUse: KnockoutObservable<number>;

            tempMaxUsageRequired: KnockoutObservable<boolean>;
            goOutMaxUsageRequired: KnockoutObservable<boolean>;
            
            constructor() {
                const self = this;
                self.settingOptions = ko.observableArray<ItemModel>([
                    new ItemModel(1, nts.uk.resource.getText("KMK013_209")),
                    new ItemModel(0, nts.uk.resource.getText("KMK013_210"))
                ]);
                self.daysOfWeekOptions = ko.observableArray([
                    new ItemModel(1, nts.uk.resource.getText("Enum_DayOfWeek_Monday")),
                    new ItemModel(2, nts.uk.resource.getText("Enum_DayOfWeek_Tuesday")),
                    new ItemModel(3, nts.uk.resource.getText("Enum_DayOfWeek_Wednesday")),
                    new ItemModel(4, nts.uk.resource.getText("Enum_DayOfWeek_Thursday")),
                    new ItemModel(5, nts.uk.resource.getText("Enum_DayOfWeek_Friday")),
                    new ItemModel(6, nts.uk.resource.getText("Enum_DayOfWeek_Saturday")),
                    new ItemModel(7, nts.uk.resource.getText("Enum_DayOfWeek_Sunday")),
                ]);
                self.goOutReasonOptions = ko.observableArray<ItemModel>([
                    new ItemModel(0, nts.uk.resource.getText("Enum_Private")),
                    new ItemModel(1, nts.uk.resource.getText("Enum_Public")),
                    new ItemModel(2, nts.uk.resource.getText("Enum_Compensation")),
                    new ItemModel(3, nts.uk.resource.getText("Enum_Union"))
                ]);
                
                // Set default setting to Not use
                self.flexSetting = ko.observable(0);
                self.aggWorkSet = ko.observable(0);
                self.tempWorkSet = ko.observable(0);
                self.multipleWorkSet = ko.observable(0);
                self.tempMaxUsage = ko.observable(0);
                self.timeTreatTemporarySame = ko.observable(0);

                self.midNightStartTime = ko.observable(0);
                self.midNightEndTime = ko.observable(60);

                self.startOfWeek = ko.observable(0);

                self.goOutUsage = ko.observable(0);
                self.goOutMaxUsage = ko.observable(3);
                self.initValueReasonGoOut = ko.observable(0);

                self.entranceExitUse = ko.observable(0);

                self.tempMaxUsageRequired = ko.computed(() => {
                    return self.tempWorkSet() == 1;
                });
                self.goOutMaxUsageRequired = ko.computed(() => {
                    return self.goOutUsage() == 1;
                });
            }

            // Start Page
            public startPage(): JQueryPromise<void> {
                const dfd = $.Deferred<void>();
                const self = this;
                blockUI.invisible();
                $.when(
                    service.loadAllSetting(),
                    service.loadTmpWorkSetting(),
                    service.loadMidnightTime(),
                    service.loadWeekManage(),
                    service.loadGoOutManage(),
                    service.loadEntranceExit()
                ).done((allData, tmpWorkMng, midnightTime, weekManage, goOutManage, exit) => {
                    if (allData) {
                        self.flexSetting(allData.flexWorkManagement);
                        self.aggWorkSet(allData.useAggDeformedSetting);
                        self.tempWorkSet(allData.useTempWorkUse);
                        self.multipleWorkSet(allData.useWorkManagementMultiple);
                    }
                    if (tmpWorkMng) {
                        self.tempMaxUsage(tmpWorkMng.maxUsage);
                        self.timeTreatTemporarySame(tmpWorkMng.timeTreatTemporarySame);
                    }
                    if (!_.isEmpty(midnightTime)) {
                        self.midNightStartTime(midnightTime[0].startTime);
                        self.midNightEndTime(midnightTime[0].endTime);
                    }
                    if (weekManage) {
                        self.startOfWeek(weekManage.dayOfWeek);
                    }
                    if (goOutManage) {
                        self.goOutMaxUsage(goOutManage.maxUsage);
                        self.initValueReasonGoOut(goOutManage.initValueReasonGoOut);
                    }
                    if (exit) {
                        self.entranceExitUse(exit.useClassification);
                    }
                    dfd.resolve();
                }).fail(error => {
                    dfd.reject();
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                    $('#flex-radio').focus();
                    self.midNightStartTime.subscribe(value => {
                        const control = $('#midnightstart');
                        if (self.midNightEndTime() < value) {
                            if (nts.uk.ui.errors.errorsViewModel().errors().filter(e => e.$control[0] == control[0]).length == 0)
                                control.ntsError('set', {messageId:"Msg_1022"});
                        } else {
                            control.ntsError('clear');
                            $('#midnightend').ntsError('clear');
                        }
                    });
                    self.midNightEndTime.subscribe(value => {
                        const control = $('#midnightend');
                        if (value < self.midNightStartTime()) {
                            if (nts.uk.ui.errors.errorsViewModel().errors().filter(e => e.$control[0] == control[0]).length == 0)
                                control.ntsError('set', {messageId:"Msg_1022"});
                        } else {
                            control.ntsError('clear');
                            $('#midnightstart').ntsError('clear');
                        }
                    });
                    self.tempWorkSet.subscribe(value => {
                        if (value == 0) {
                            $('#tempmaxuse').ntsError('clear');
                            $('#temptime').ntsError('clear');
                        }
                    });
                    self.goOutUsage.subscribe(value => {
                        if (value == 0) {
                            $('#gooutmaxuse').ntsError('clear');
                        }
                    });
                });
                return dfd.promise();
            }
            
            saveData(): void {
                const self = this;
                $('.nts-input').trigger('validate');
                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                blockUI.invisible();
                // Register to DB
                $.when(
                    service.regAgg({useDeformedSetting : self.aggWorkSet()}),
                    service.regFlexWorkSet({managingFlexWork: self.flexSetting()}),
                    service.regTempWork({useClassification: self.tempWorkSet()}),
                    service.regWorkMulti({useAtr: self.multipleWorkSet()}),
                    service.regTmpWorkMng(self.tempWorkSet() == 1, {maxUsage: self.tempMaxUsage(), timeTreatTemporarySame: self.timeTreatTemporarySame()}),
                    service.regMidnightTimeMng({start: self.midNightStartTime(), end: self.midNightEndTime()}),
                    service.regWeekManage({dayOfWeek: self.startOfWeek()}),
                    service.regGoOutManage(self.goOutUsage() == 1, {maxUsage: self.goOutMaxUsage(), initValueReasonGoOut: self.initValueReasonGoOut()}),
                    service.regEntranceExit({useClassification1: self.entranceExitUse()})
                ).done(() => {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        $('#flex-radio').focus();
                    });
                }).fail(error => {
                    nts.uk.ui.dialog.alert(error);
                }).always(() => {
                    blockUI.clear();
                });
            }
        }
        
        // Class ItemModel
        class ItemModel {
            id: number;
            name: string;
            constructor(id: number, name: string) {
                this.id = id;
                this.name = name;
            }
        }
    }
}