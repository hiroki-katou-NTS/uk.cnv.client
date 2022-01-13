module nts.uk.at.view.kaf022.q.viewmodel {
    import text = nts.uk.resource.getText;

    export class ScreenModelQ {
        itemListD15: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_75')},
            {code: 0, name: text('KAF022_82')}
        ]);
        itemListD13: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: text('KAF022_36')},
            {code: 0, name: text('KAF022_37')}
        ]);
        itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 0, name: "0"},
            {code: 1, name: "1"},
            {code: 2, name: "2"},
            {code: 3, name: "3"},
            {code: 4, name: "4"},
            {code: 5, name: "5"},
            {code: 6, name: "6"},
            {code: 7, name: "7"},
        ]);

        // 申請理由
        appReasonDispAtr: KnockoutObservable<number>;
        // 事前申請の超過メッセージ
        preExcessAtr: KnockoutObservable<number>;
        // 実績超過メッセージ
        atdExcessAtr: KnockoutObservable<number>;
        // 申請対象日に対して警告表示
        warningDays: KnockoutObservable<number>;
        // 所属職場名表示
        dispWorkplaceNameAtr: KnockoutObservable<number>;
        // 承認機能を利用する
        useApprovalFunction: KnockoutObservable<number>;

        constructor() {
            const self = this;
            self.appReasonDispAtr = ko.observable(1);
            self.preExcessAtr = ko.observable(1);
            self.atdExcessAtr = ko.observable(1);
            self.warningDays = ko.observable(0);
            self.dispWorkplaceNameAtr = ko.observable(1);
            self.useApprovalFunction = ko.observable(1);

            $("#fixed-table-q1").ntsFixedTable({});
            $("#fixed-table-q2").ntsFixedTable({});
        }

        initData(allData: any): void {
            const self = this;
            let data = allData.approvalListDisplaySetting;
            if (data) {
                self.appReasonDispAtr(data.appReasonDispAtr);
                self.preExcessAtr(data.preExcessAtr);
                self.atdExcessAtr(data.atdExcessAtr);
                self.warningDays(data.warningDays);
                self.dispWorkplaceNameAtr(data.dispWorkplaceNameAtr);
                self.useApprovalFunction(data.useApprovalFunction)
            }
        }

        collectData(): any {
            const self = this;
            return {
                appReasonDispAtr: self.appReasonDispAtr(),
                preExcessAtr: self.preExcessAtr(),
                atdExcessAtr: self.atdExcessAtr(),
                warningDays: self.warningDays(),
                dispWorkplaceNameAtr: self.dispWorkplaceNameAtr(),
                useApprovalFunction: self.useApprovalFunction()
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