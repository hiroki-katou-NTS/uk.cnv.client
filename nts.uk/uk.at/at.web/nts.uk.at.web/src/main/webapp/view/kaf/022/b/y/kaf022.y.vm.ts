module nts.uk.at.view.kaf022.y.viewmodel {
    import getText = nts.uk.resource.getText;
    let __viewContext: any = window["__viewContext"] || {};

    export class ScreenModelY {
        itemListD15: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: getText('KAF022_75')},
            {code: 0, name: getText('KAF022_82')}
        ]);
        itemListD13: KnockoutObservableArray<ItemModel> = ko.observableArray([
            {code: 1, name: getText('KAF022_100')},
            {code: 0, name: getText('KAF022_101')}
        ]);
        appTypeLabels = ["KAF022_3", "KAF022_4", "KAF022_5", "KAF022_6", "KAF022_7", "KAF022_8", "KAF022_11", "KAF022_707", "KAF022_10", "KAF022_12", "KAF022_705"];

        // 申請設定.種類別設定
        appTypeSettings: KnockoutObservableArray<AppTypeSetting>;

        // 登録時の手動メール送信の初期値
        manualSendMailAtr: KnockoutObservable<number>;

        // 申請メール設定
        appMailSetting: KnockoutObservable<AppMailSetting>;

        constructor() {
            const self = this;
            self.appTypeSettings = ko.observableArray([]);
            self.manualSendMailAtr = ko.observable(1);
            self.appMailSetting = ko.observable(new AppMailSetting(1, [
                new EmailContent(Division.LEAVE_INSTRUCTION, getText("KAF022_111"), "", getText("KAF022_112"),""),
                new EmailContent(Division.OVERTIME_INSTRUCTION, getText("KAF022_113"), "", getText("KAF022_114"),""),
                new EmailContent(Division.APPLICATION_APPROVAL, getText("KAF022_115"), "", getText("KAF022_116"),""),
                new EmailContent(Division.REMAND, getText("KAF022_368"), "", getText("KAF022_369"), "")
            ]));
            $("#fixed-table-y1").ntsFixedTable({});
            $("#fixed-table-y2").ntsFixedTable({});
        }

        initData(allData: any): void {
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
                            obj.sendMailWhenApproval,
                            obj.sendMailWhenRegister
                        )
                    );
                } else {
                    self.appTypeSettings.push(new AppTypeSetting(appType.value, getText(self.appTypeLabels[index]),  true, true));
                }
            });

            if (allData.applicationSetting && allData.applicationSetting.appDisplaySetting) {
                self.manualSendMailAtr(allData.applicationSetting.appDisplaySetting.manualSendMailAtr);
            }

            if (allData.appMailSetting) {
                self.appMailSetting().urlReason(allData.appMailSetting.urlReason);
                const emailList = allData.appMailSetting.emailList || [];
                self.appMailSetting().emailList().forEach(e => {
                    const email = _.find(emailList, i => i.division == e.division);
                    e.emailSubject(email ? email.emailSubject : "");
                    e.emailText(email ? email.emailText : "");
                });
            }
        }

        collectData(): any {
            const self = this;
            return {
                appTypeSettings: ko.toJS(self.appTypeSettings),
                manualSendMailAtr: self.manualSendMailAtr(),
                appMailSetting: ko.toJS(self.appMailSetting)
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

    class AppTypeSetting {
        appType: number;
        appTypeName: string;
        // 承認処理時に自動でメールを送信する
        sendMailWhenApproval: KnockoutObservable<boolean>;
        // 新規登録時に自動でメールを送信する
        sendMailWhenRegister: KnockoutObservable<boolean>;
        constructor(appType:number, appTypeName: string, sendMailWhenApproval: boolean, sendMailWhenRegister: boolean) {
            this.appType = appType;
            this.appTypeName = appTypeName;
            this.sendMailWhenApproval = ko.observable(sendMailWhenApproval);
            this.sendMailWhenRegister = ko.observable(sendMailWhenRegister);
        }
    }

    class AppMailSetting {
        // URL理込
        urlReason: KnockoutObservable<number>;
        // メールの内容
        emailList: KnockoutObservableArray<EmailContent>;
        constructor(urlReason: number, emailList: Array<EmailContent>) {
            this.urlReason = ko.observable(urlReason);
            this.emailList = ko.observableArray(emailList);
        }
    }

    class EmailContent {
        division: number;
        labelSubject: string;
        // 件名
        emailSubject: KnockoutObservable<string>;
        labelContent: string;
        // 本文
        emailText: KnockoutObservable<string>;

        constructor(division: number, labelSubject: string, subject: string, labelContent: string, text: string) {
            this.division = division;
            this.labelSubject = labelSubject;
            this.emailSubject = ko.observable(subject);
            this.labelContent = labelContent;
            this.emailText = ko.observable(text);
        }
    }

    enum Division {
        /**
         * 申請承認
         */
        APPLICATION_APPROVAL = 0,

        /**
         * 差し戻し
         */
        REMAND = 1,

        /**
         * 残業指示
         */
        OVERTIME_INSTRUCTION = 2,

        /**
         * 休出指示
         */
        LEAVE_INSTRUCTION = 3
    }
}