module nts.uk.at.view.kaf022.a.viewmodel {
    import getText = nts.uk.resource.getText;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    const __viewContext: any = window["__viewContext"] || {};

    export class ScreenModelA {
        /*A4 - Deadline Settings Table*/
        dataDeadlineSettings: KnockoutObservableArray<DeadlineSetting>;
        itemListA4_7: KnockoutObservableArray<ItemModel>;
        itemListA4_8: KnockoutObservableArray<ItemModel>;

        /*A7 - Reception Restriction Settings Table*/
        dataReceptionRestrictionSettings: KnockoutObservableArray<ReceptionRestrictionSetting>;
        itemListA7_20: KnockoutObservableArray<ItemModel>;

        /* A11 - Application Limit Settings Table*/
        itemListA11_8: KnockoutObservableArray<ItemModel>;
        itemListA20: KnockoutObservableArray<ItemModel>;
        appLimitSetting: KnockoutObservable<AppLimitSetting>; // A20 also

        /* A17 Approval settings Table*/
        itemListA17_4: KnockoutObservableArray<ItemModel>;
        itemListA17_7: KnockoutObservableArray<ItemModel>;
        itemListA14_3: KnockoutObservableArray<ItemModel>;
        approvalSetting: KnockoutObservable<ItemA17>;

        /* A19 HolidatOvertimeWorkApplicationReflect Table*/
        itemListA11_12: KnockoutObservableArray<ItemModel>;
        // 時間外深夜時間を反映する
        nightOvertimeReflect: KnockoutObservable<number>;

        /*A12 Table*/
        // 事前事後区分表示
        prePostDisplayAtr: KnockoutObservable<number>;
        itemListA12_5: KnockoutObservableArray<ItemModel>;

        /* A22 Application Type Setting Table*/
        appTypeSettings: KnockoutObservableArray<AppTypeSetting>;
        itemListA22_2: KnockoutObservableArray<ItemModel>;

        /*A8*/
        listDataA8: KnockoutObservableArray<DisplayReasonSetting>;

        /*A21*/
        itemListA21_2: KnockoutObservableArray<ItemModel>;
        itemListA21_4: KnockoutObservableArray<ItemModel>;
        appReflectCondition: KnockoutObservable<AppReflectExeCondition>;

        appTypeLabels = ["KAF022_3", "KAF022_4", "KAF022_5", "KAF022_6", "KAF022_7", "KAF022_8", "KAF022_11", "KAF022_707", "KAF022_10", "KAF022_12", "KAF022_705"];

        constructor() {
            const self = this;

            // A4
            self.dataDeadlineSettings = ko.observableArray([]);
            self.itemListA4_7 = ko.observableArray([
                new ItemModel(0, getText('KAF022_321')),
                new ItemModel(1, getText('KAF022_322')),
            ]);
            self.itemListA4_8 = ko.observableArray([
                new ItemModel(0, getText('KAF022_323')),
                new ItemModel(1, getText('KAF022_324')),
                new ItemModel(2, getText('KAF022_325')),
                new ItemModel(3, getText('KAF022_326')),
                new ItemModel(4, getText('KAF022_327')),
                new ItemModel(5, getText('KAF022_328')),
                new ItemModel(6, getText('KAF022_329')),
                new ItemModel(7, getText('KAF022_330')),
                new ItemModel(8, getText('KAF022_331')),
                new ItemModel(9, getText('KAF022_332')),
                new ItemModel(10, getText('KAF022_333')),
                new ItemModel(11, getText('KAF022_334')),
                new ItemModel(12, getText('KAF022_335')),
                new ItemModel(13, getText('KAF022_336')),
                new ItemModel(14, getText('KAF022_337')),
                new ItemModel(15, getText('KAF022_338')),
                new ItemModel(16, getText('KAF022_339')),
                new ItemModel(17, getText('KAF022_340')),
                new ItemModel(18, getText('KAF022_341')),
                new ItemModel(19, getText('KAF022_342')),
                new ItemModel(20, getText('KAF022_343')),
                new ItemModel(21, getText('KAF022_344')),
                new ItemModel(22, getText('KAF022_345')),
                new ItemModel(23, getText('KAF022_346')),
                new ItemModel(24, getText('KAF022_347')),
                new ItemModel(25, getText('KAF022_348')),
                new ItemModel(26, getText('KAF022_349')),
                new ItemModel(27, getText('KAF022_350')),
                new ItemModel(28, getText('KAF022_351')),
                new ItemModel(29, getText('KAF022_352')),
                new ItemModel(30, getText('KAF022_353')),
                new ItemModel(31, getText('KAF022_354'))
            ]);

            // A7
            self.dataReceptionRestrictionSettings = ko.observableArray([]);
            self.itemListA7_20 = ko.observableArray([
                new ItemModel(0, getText('KAF022_323')),
                new ItemModel(1, getText('KAF022_357')),
                new ItemModel(2, getText('KAF022_358')),
                new ItemModel(3, getText('KAF022_359')),
                new ItemModel(4, getText('KAF022_360')),
                new ItemModel(5, getText('KAF022_361')),
                new ItemModel(6, getText('KAF022_362')),
                new ItemModel(7, getText('KAF022_363')),
            ]);

            // A11
            self.itemListA11_8 = ko.observableArray([
                new ItemModel(1, getText('KAF022_437')),
                new ItemModel(0, getText('KAF022_438'))
            ]);
            self.itemListA20 = ko.observableArray([
                new ItemModel(1, getText('KAF022_75')),
                new ItemModel(0, getText('KAF022_82'))
            ]);
            self.appLimitSetting = ko.observable(new AppLimitSetting(null));

            // A17
            self.itemListA17_4 = ko.observableArray([
                new ItemModel(1, getText('KAF022_272')),
                new ItemModel(0, getText('KAF022_273')),
            ]);
            self.itemListA17_7 = ko.observableArray([
                new ItemModel(0, getText('KAF022_403')),
                new ItemModel(1, getText('KAF022_404')),
            ]);
            self.itemListA14_3 = ko.observableArray([
                new ItemModel(1, getText('KAF022_75')),
                new ItemModel(0, getText('KAF022_82'))
            ]);
            self.approvalSetting = ko.observable(new ItemA17(0, 0));

            // A19
            self.nightOvertimeReflect = ko.observable(0);
            self.itemListA11_12 = ko.observableArray([
                new ItemModel(1, getText('KAF022_100')),
                new ItemModel(0, getText('KAF022_101'))
            ]);

            // A12
            self.prePostDisplayAtr = ko.observable(0);
            self.itemListA12_5 = ko.observableArray([
                new ItemModel(1, getText('KAF022_36')),
                new ItemModel(0, getText('KAF022_37'))
            ]);

            // A22
            self.appTypeSettings = ko.observableArray([]);
            self.itemListA22_2 = ko.observableArray([
                new ItemModel(0, getText('KAF022_745')),
                new ItemModel(1, getText('KAF022_746')),
                new ItemModel(2, getText('KAF022_747'))
            ]);

            // A8
            self.listDataA8 = ko.observableArray([]);


            // A21
            self.itemListA21_2 = ko.observableArray([
                new ItemModel(1, getText('KAF022_44')),
                new ItemModel(0, getText('KAF022_396'))
            ]);
            self.itemListA21_4 = ko.observableArray([
                new ItemModel(0, getText('KAF022_781')),
                new ItemModel(1, getText('KAF022_782')),
                new ItemModel(2, getText('KAF022_783'))
            ]);
            self.appReflectCondition = ko.observable(new AppReflectExeCondition(null));

            $("#fixed-table-a4").ntsFixedTable({});
            $("#fixed-table-a7").ntsFixedTable({});
            $("#fixed-table-a11").ntsFixedTable({});
            $("#fixed-table-a17").ntsFixedTable({});
            $("#fixed-table-a19").ntsFixedTable({});
            $("#fixed-table-a12").ntsFixedTable({});
            $("#fixed-table-a22").ntsFixedTable({});
            $("#fixed-table-a20").ntsFixedTable({});
            $("#fixed-table-a8").ntsFixedTable({});
            $("#fixed-table-a21").ntsFixedTable({});
        }

        initData(allData: any): void {
            const self = this;
            self.initDataA4(allData);
            self.initDataA7(allData);
            self.initDataA11(allData);
            self.initDataA17(allData);
            self.nightOvertimeReflect(allData.nightOvertimeReflectAtr || 0);
            self.prePostDisplayAtr(allData.applicationSetting ? allData.applicationSetting.appDisplaySetting.prePostDisplayAtr : 0);
            self.initDataA22(allData);
            self.initDataA8(allData);
            self.initDataA21(allData);
        }

        initDataA4(allData: any): void {
            let self = this;
            // init data A4
            self.dataDeadlineSettings([]);
            for (let i = 1; i <= 5; i++) {
                const data: Array<Closure> = allData.allClosure || [];
                const appDeadlineSetLst: Array<any> = allData.applicationSetting ? allData.applicationSetting.appDeadlineSetLst : [];
                const closure = _.find(data, ['id', i]);
                const deadline: any = _.find(appDeadlineSetLst, ['closureId', i]);
                self.dataDeadlineSettings.push(new DeadlineSetting(
                    i,
                    i + "." + (closure ? closure.name : ""),
                    deadline ? deadline.useAtr : 0,
                    deadline ? deadline.deadlineCriteria : 0,
                    deadline ? deadline.deadline : 0
                ));
            }
        }

        initDataA7(allData: any): void {
            let self = this;
            let listAppType = __viewContext.enums.ApplicationType;
            self.dataReceptionRestrictionSettings([]);
            let data: Array<any> = allData.applicationSetting ? allData.applicationSetting.receptionRestrictionSetting : [];

            listAppType.forEach( (appType: any, index: number) => {
                let obj: any = _.find(data, ['appType', appType.value]);
                if (obj) {
                    self.dataReceptionRestrictionSettings.push(
                        new ReceptionRestrictionSetting(
                            getText(self.appTypeLabels[index]),
                            appType.value,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.toUse : obj.beforehandRestriction.toUse,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.dateBeforehandRestrictions : obj.beforehandRestriction.dateBeforehandRestrictions,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.methodCheck : null,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.opEarlyOvertime : null,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.opNormalOvertime : null,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.opEarlyNormalOvertime : null,
                            obj.appType == 0 ? obj.otAppBeforeAccepRestric.opMultipleOvertime : null,
                            obj.afterhandRestriction.allowFutureDay
                        )
                    );
                } else {
                    self.dataReceptionRestrictionSettings.push(new ReceptionRestrictionSetting(getText(self.appTypeLabels[index]), appType.value, 0, 0, 1, null, null, null, null, 0));
                }
            });

        }

        initDataA11(allData: any): void {
            const self = this;
            self.appLimitSetting(new AppLimitSetting(allData.applicationSetting ? allData.applicationSetting.appLimitSetting : null));
        }

        initDataA17(allData: any): void {
            let self = this;
            self.approvalSetting(new ItemA17(
                allData.applicationSetting ? allData.applicationSetting.recordDate : 0,
                allData.approvalSettingDto ? allData.approvalSettingDto.prinFlg : 0
            ));
        }

        initDataA22(allData: any): void {
            const self = this;
            let listAppType = __viewContext.enums.ApplicationType;
            self.appTypeSettings([]);
            let data: Array<any> = allData.applicationSetting ? allData.applicationSetting.appTypeSetting : [];

            listAppType.forEach( (appType: any, index: number) => {
                let obj: any = _.find(data, ['appType', appType.value]);
                if (obj) {
                    self.appTypeSettings.push(
                        new AppTypeSetting(
                            appType.value,
                            getText(self.appTypeLabels[index]),
                            obj.canClassificationChange,
                            obj.displayInitialSegment
                        )
                    );
                } else {
                    self.appTypeSettings.push(new AppTypeSetting(appType.value, getText(self.appTypeLabels[index]),  true, 0));
                }
            });

        }

        initDataA8(allData: any): void {
            let self = this;
            let listAppType = __viewContext.enums.ApplicationType;
            let listHdType = __viewContext.enums.HolidayAppType;
            self.listDataA8([]);
            let data: Array<any> = allData.reasonDisplaySettings || [];

            listAppType.forEach( (appType: any, index: number) => {
                if (appType.value == 1) {
                    self.listDataA8.push(new DisplayReasonSetting(appType.value, getText(self.appTypeLabels[index]), null, null, null));
                    _.forEach(listHdType, (hdType: any)=>{
                        if (hdType.value < 7) {
                            let obj2: any = _.find(data, ['holidayAppType', hdType.value]);
                            if (obj2) {
                                self.listDataA8.push(new DisplayReasonSetting(appType.value, hdType.name, obj2.displayFixedReason, obj2.displayAppReason, hdType.value));
                            } else {
                                self.listDataA8.push(new DisplayReasonSetting(appType.value, hdType.name,0, 0, hdType.value));
                            }
                        }
                    });
                } else {
                    let obj1: any = _.find(data, ['appType', appType.value]);
                    if (obj1) {
                        self.listDataA8.push(new DisplayReasonSetting(appType.value, getText(self.appTypeLabels[index]), obj1.displayFixedReason, obj1.displayAppReason, null));
                    } else {
                        self.listDataA8.push(new DisplayReasonSetting(appType.value, getText(self.appTypeLabels[index]),0, 0, null));
                    }
                }
            });
        }

        initDataA21(allData: any): void {
            const self = this;
            self.appReflectCondition(new AppReflectExeCondition(allData.appReflectCondition));
        }

        collectData(): any {
            const self = this;
            return {
                appDeadlineSettings: ko.toJS(self.dataDeadlineSettings),
                receptionRestrictionSettings: ko.toJS(self.dataReceptionRestrictionSettings),
                appLimitSetting: ko.toJS(self.appLimitSetting),
                appTypeSettings: ko.toJS(self.appTypeSettings),
                prePostDisplayAtr: self.prePostDisplayAtr(),
                recordDate: self.approvalSetting().baseDateAtr(),
                approvalByPersonAtr: self.approvalSetting().approvalByPersonAtr(),

                nightOvertimeReflectAtr: self.nightOvertimeReflect(),

                reasonDisplaySettings: ko.toJS(self.listDataA8)
                    .filter((r: any) => r.appType != 1 || r.holidayAppType != null)
                    .map((r: any) => ({
                        appType: r.appType,
                        displayAppReason: r.displayAppReason ? 1 : 0,
                        displayFixedReason: r.displayFixedReason ? 1 : 0,
                        holidayAppType: r.holidayAppType
                    })),

                appReflectCondition: ko.toJS(self.appReflectCondition)
            };
        }

        openScreenS(): void {
            modal('/view/kaf/022/s/index.xhtml').onClosed(() => {
                setShared("KAF022S_PARAMS", undefined);
            });
        }
    }

    class ItemModel {
        code: number;
        name: string;

        constructor(code: number, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    class DeadlineSetting {
        // 締めID
        closureId: number;
        closureName: string;
        // 利用区分
        useAtr: KnockoutObservable<boolean>;
        // 締切基準
        deadlineCriteria: KnockoutObservable<number>;
        // 締切日数
        deadline: KnockoutObservable<number>;
        constructor(index: number, closureName: string, useAtr: number, deadlineCriteria: number, deadline: number) {
            this.closureId = index;
            this.closureName = closureName;
            this.useAtr = ko.observable(useAtr == 1);
            this.deadlineCriteria = ko.observable(deadlineCriteria);
            this.deadline = ko.observable(deadline);
        }
    }

    interface Closure {
        id: string;
        name: string;
    }

    class ReceptionRestrictionSetting {
        appType: number;
        appTypeName: string;
        // 日数
        dateBeforehandRestrictions: KnockoutObservable<number>;
        // 利用する
        useAtr: KnockoutObservable<boolean>;
        // 未来日許可しない
        allowFutureDay: KnockoutObservable<boolean>;

        // チェック方法
        methodCheck: KnockoutObservable<number>;
        // 時刻（早出残業）
        earlyOvertime: KnockoutObservable<number>;
        // 時刻（通常残業）
        normalOvertime: KnockoutObservable<number>;
        // 時刻（早出残業・通常残業）
        earlyNormalOvertime: KnockoutObservable<number>;
        // 時刻（複数回残業）
        multipleOvertime: KnockoutObservable<number>;

        requiredA7_23: KnockoutObservable<boolean>;
        constructor(appTypeName: string, appType: number, useAtr: number, dateBeforehandRestrictions: number,
                    methodCheck: number, earlyOvertime: number, normalOvertime: number, earlyNormalOvertime: number, multipleOvertime: number, allowFutureDay: number) {
            this.appTypeName = appTypeName;
            this.appType = appType;
            this.useAtr = ko.observable(useAtr == 1);
            this.dateBeforehandRestrictions = ko.observable(dateBeforehandRestrictions);
            this.methodCheck = ko.observable(methodCheck);
            this.earlyOvertime = ko.observable(earlyOvertime);
            this.normalOvertime = ko.observable(normalOvertime);
            this.earlyNormalOvertime = ko.observable(earlyNormalOvertime);
            this.multipleOvertime = ko.observable(multipleOvertime);
            this.allowFutureDay = ko.observable(allowFutureDay == 1);

            this.requiredA7_23 = ko.observable(methodCheck == 0);

            this.methodCheck.subscribe((value) => {
                if (value == 1) {
                    nts.uk.ui.errors.clearAll();
                    this.requiredA7_23(false);
                } else {
                    nts.uk.ui.errors.clearAll();
                    this.requiredA7_23(true);
                }
            });
        }
    }

    class AppLimitSetting {
        // 月別実績が確認済なら申請できない
        canAppAchievementMonthConfirm: KnockoutObservable<number>;
        canAppAchievementLock: KnockoutObservable<number>;
        canAppFinishWork: KnockoutObservable<number>;
        // 日別実績が確認済なら申請できない
        canAppAchievementConfirm: KnockoutObservable<number>;
        // 定型理由が必須
        standardReasonRequired: KnockoutObservable<number>;
        // 申請理由が必須
        requiredAppReason: KnockoutObservable<number>;

        constructor(appLimitSetting: any) {
            this.canAppAchievementMonthConfirm = ko.observable(appLimitSetting && appLimitSetting.canAppAchievementMonthConfirm ? 1 : 0);
            this.standardReasonRequired = ko.observable(appLimitSetting && appLimitSetting.standardReasonRequired ? 1 : 0);
            this.canAppAchievementLock = ko.observable(appLimitSetting && appLimitSetting.canAppAchievementLock ? 1 : 0);
            this.canAppFinishWork = ko.observable(appLimitSetting && appLimitSetting.canAppFinishWork ? 1 : 0);
            this.requiredAppReason = ko.observable(appLimitSetting && appLimitSetting.requiredAppReason ? 1 : 0);
            this.canAppAchievementConfirm = ko.observable(appLimitSetting && appLimitSetting.canAppAchievementConfirm ? 1 : 0);
        }
    }

    class ItemA17 {
        // 承認ルートの基準日
        baseDateAtr: KnockoutObservable<number>;
        // 本人による承認
        approvalByPersonAtr: KnockoutObservable<number>;
        constructor(baseDateAtr: number, approvalByPersonAtr: number) {
            this.baseDateAtr = ko.observable(baseDateAtr);
            this.approvalByPersonAtr = ko.observable(approvalByPersonAtr);
        }
    }

    class AppTypeSetting {
        appType: number;
        appTypeName: string;
        // 事前事後区分を変更できる
        canClassificationChange: KnockoutObservable<boolean>;
        // 事前事後区分の初期表示
        displayInitialSegment: KnockoutObservable<number>;
        constructor(appType: number, appName: string, canChangeCls: boolean, displayInitSegment: number) {
            this.appType = appType;
            this.appTypeName = appName;
            this.canClassificationChange = ko.observable(canChangeCls);
            this.displayInitialSegment = ko.observable(displayInitSegment);

            this.displayInitialSegment.subscribe(value => {
                if (value == 2) {
                    this.canClassificationChange(true);
                }
            });
        }
    }

    class DisplayReasonSetting {
        appType: number;
        // 定型理由の表示
        displayFixedReason: KnockoutObservable<boolean>;
        // 申請理由の表示
        displayAppReason: KnockoutObservable<boolean>;
        appTypeName: KnockoutObservable<string>;
        holidayAppType: number;
        constructor(appType: number, appTypeName: string, displayFixedReason: number, displayAppReason: number, holidayAppType: number) {
            this.appType = appType;
            this.displayFixedReason = ko.observable(displayFixedReason == 1);
            this.displayAppReason = ko.observable(displayAppReason == 1);
            this.appTypeName = ko.observable(appTypeName);
            this.holidayAppType = holidayAppType;
        }
    }

    class AppReflectExeCondition {
        applyBeforeSchedule: KnockoutObservable<number>;
        evenIfScheduleConfirmed: KnockoutObservable<number>;
        evenIfRecordConfirmed: KnockoutObservable<number>;

        constructor (appReflectExeCondition: any) {
            this.applyBeforeSchedule = ko.observable(appReflectExeCondition ? appReflectExeCondition.applyBeforeSchedule : 0);
            this.evenIfScheduleConfirmed = ko.observable(appReflectExeCondition ? appReflectExeCondition.evenIfScheduleConfirmed : 0);
            this.evenIfRecordConfirmed = ko.observable(appReflectExeCondition ? appReflectExeCondition.evenIfRecordConfirmed : 0);
        }
    }

}