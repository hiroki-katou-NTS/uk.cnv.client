module nts.uk.at.view.kaf022.k.viewmodel {
    import text = nts.uk.resource.getText;

    export class ScreenModelK {
        itemListC27: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_100')},
            {code: 0, name: text('KAF022_101')},
            {code: 2, name: text('KAF022_171')}
        ]);
        itemListC48: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_420')},
            {code: 0, name: text('KAF022_421')}
        ]);
        itemListD15: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 0, name: text('KAF022_391')},
            {code: 1, name: text('KAF022_392')}
        ]);
        itemListD13: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_389')},
            {code: 0, name: text('KAF022_390')}
        ]);
        itemListCC1: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_389')},
            {code: 0, name: text('KAF022_390')},
        ]);
        itemListK13: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_292')},
            {code: 0, name: text('KAF022_291')},
        ]);
        itemListK14: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_272')},
            {code: 0, name: text('KAF022_273')},
        ]);

        // 就業時間帯を反映する
        reflectWorkHour: KnockoutObservable<number>;
        // 出退勤を反映する
        // reflectAttendance: KnockoutObservable<number>;
        // 1日休暇の場合は出退勤を削除
        oneDayLeaveDeleteAttendance: KnockoutObservable<number>;

        // 同時申請必須
        simultaneousApplyRequired: KnockoutObservable<number>;
        // allowanceForAbsence: KnockoutObservable<number>;

        // 出退勤を反映する
        reflectAttendanceAtr: KnockoutObservable<number>;

        // コメント
        texteditorD9: KnockoutObservable<string>;
        // 文字色
        valueD10: KnockoutObservable<string>;
        // 太字
        enableD11: KnockoutObservable<boolean>;

        // コメント
        texteditorD12: KnockoutObservable<string>;
        // 文字色
        valueD10_1: KnockoutObservable<string>;
        // 太字
        enableD11_1: KnockoutObservable<boolean>;

        constructor() {
            const self = this;

            self.oneDayLeaveDeleteAttendance = ko.observable(1);
            // self.reflectAttendance = ko.observable(1);
            self.reflectWorkHour = ko.observable(1);
            
            self.simultaneousApplyRequired = ko.observable(1);
            // self.allowanceForAbsence = ko.observable(0);

            self.reflectAttendanceAtr = ko.observable(1);

            self.texteditorD9 = ko.observable(null);
            self.valueD10 = ko.observable("#000000");
            self.enableD11 = ko.observable(false);

            self.texteditorD12 = ko.observable(null);
            self.valueD10_1 = ko.observable("#000000");
            self.enableD11_1 = ko.observable(false);

            $("#fixed-table-k1").ntsFixedTable({});
            $("#fixed-table-k2").ntsFixedTable({});
            $("#fixed-table-k3").ntsFixedTable({});
            $("#fixed-table-k4").ntsFixedTable({});
        }

        initData(allData: any): void {
            const self = this;
            if (allData.substituteWorkApplicationReflect) {
                self.reflectAttendanceAtr(allData.substituteWorkApplicationReflect.reflectAttendanceAtr || 0);
            }
            if (allData.substituteLeaveApplicationReflect) {
                self.reflectWorkHour(allData.substituteLeaveApplicationReflect.reflectWorkHour || 0);
                // self.reflectAttendance(allData.substituteLeaveApplicationReflect.reflectAttendance || 0);
                self.oneDayLeaveDeleteAttendance(allData.substituteLeaveApplicationReflect.oneDayLeaveDeleteAttendance);
            }
            if (allData.substituteHdWorkApplicationSetting) {
                const data = allData.substituteHdWorkApplicationSetting;
                self.simultaneousApplyRequired(data.simultaneousApplyRequired || 0);
                // self.allowanceForAbsence(data.allowanceForAbsence || 0);

                self.texteditorD12(data.subHolidayComment || "");
                self.valueD10_1(data.subHolidayColor);
                self.enableD11_1(data.subHolidayBold);

                self.texteditorD9(data.subWorkComment || "");
                self.valueD10(data.subWorkColor);
                self.enableD11(data.subWorkBold);
            }
        }

        collectData(): any {
            const self = this;
            return {
                drawOutApplicationReflect: {
                    reflectAttendanceAtr: self.reflectAttendanceAtr()
                },
                suspenseApplicationReflect: {
                    reflectWorkHour: self.reflectWorkHour(),
                    // reflectAttendance: self.reflectAttendance(),
                    oneDayLeaveDeleteAttendance: self.oneDayLeaveDeleteAttendance()
                },
                suspenseDrawOutApplicationSetting: {
                    simultaneousApplyRequired: self.simultaneousApplyRequired(),
                    // allowanceForAbsence: self.allowanceForAbsence(),
                    subHolidayComment: self.texteditorD12(),
                    subHolidayColor: self.valueD10_1(),
                    subHolidayBold: self.enableD11_1(),
                    subWorkComment: self.texteditorD9(),
                    subWorkColor: self.valueD10(),
                    subWorkBold: self.enableD11()
                }
            };
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

}