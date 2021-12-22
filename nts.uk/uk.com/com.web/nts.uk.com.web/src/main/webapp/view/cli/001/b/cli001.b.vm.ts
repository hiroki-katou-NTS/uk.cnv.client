module cli001.b.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;

    export class ScreenModel {
    	searchText: KnockoutObservable<any>;
        items: KnockoutObservableArray<ItemModel>;
        columns: KnockoutObservableArray<NtsGridListColumn>;
        currentId: KnockoutObservable<any>;
        count: number = 100;
        //rows: KnockoutObservable<number> = ko.observable(20);

        constructor() {
            this.items = ko.observableArray([]);
            this.columns = ko.observableArray([
                { key: 'userID', hidden: true },
                { headerText: getText('CLI001_21'), key: 'loginID', width: 140 },
                { headerText: getText('CLI001_27'), key: 'companyCD', width: 50 },
                { headerText: getText('CLI001_28'), key: 'empCD', width: 160 },
                { headerText: getText('CLI001_22'), key: 'userName', width: 220 },
            ]);
            this.searchText = ko.observable();
            this.currentId = ko.observable();
            //let tableHeight = (window.innerHeight - 150) >= 480 ? 480 : window.innerHeight - 150; 
            //this.rows(Math.round(tableHeight/24));
        }

        search(): void {
            let self = this;
            self.searchText(self.searchText().trim());
            if (_.isEmpty(self.searchText())) {
                nts.uk.ui.dialog.error({ messageId: "Msg_438", messageParams: [getText('CLI001_16')] });
                return;
            }
            block.invisible();
            service.findUserByUserIDName(self.searchText()).done(function(data) {
                self.items(data);
            }).fail(error => {
                alertError(error);
            }).always(() => {
                block.clear();
            });
        }

        lockData(): void {
            let self = this;
            if (_.isEmpty(self.currentId())) {
                nts.uk.ui.dialog.error({ messageId: "Msg_218", messageParams: [getText('CLI001_26')] });
                return;
            }

            block.invisible();
            service.lockUserByID(self.currentId()).done(function(data) {
                nts.uk.ui.windows.setShared("dataCd001.a", data);
                nts.uk.ui.windows.close();
            }).fail(error => {
                alertError(error);
            }).always(() => {
                block.clear();
            });
        }

        cancel() {
            nts.uk.ui.windows.close();
        }
    }

    class ItemModel {
        userID: string;
        loginID: string;
        companyCD: string;
        empCD: string;
        userName: string;
        constructor(userID: string, loginID: string, companyId: string, employeeId:string,  userName: string) {
            this.userID = userID;
            this.loginID = loginID;
            this.companyCD = companyId;
            this.empCD = employeeId;
            this.userName = userName;
        }
    }
}
