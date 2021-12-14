module nts.uk.com.view.cas013.b.viewmodel {


    export class ScreenModel {
        dataSource: any;
        columns: Array<any>;
        //search
        searchValue: KnockoutObservable<string>;
        //user
        selectUserID: KnockoutObservable<string>;
        userName: KnockoutObservable<string>;
        roleTypeParam: number;

        special: KnockoutObservable<boolean>;
        multi: KnockoutObservable<boolean>;

        constructor() {
            var self = this;
            self.roleTypeParam = nts.uk.ui.windows.getShared("roleType");

            self.special = ko.observable(true);
            self.multi = ko.observable(true);

            self.searchValue = ko.observable('');
            self.dataSource = ko.observableArray([]);
            self.columns = [
                { headerText: nts.uk.resource.getText(""), key: 'userID', hidden: true },
                { headerText: nts.uk.resource.getText("CAS013_29"), key: 'loginID', width: 130 },
                { headerText: nts.uk.resource.getText("CAS013_30"), key: 'userName', width: 200 }
            ];
            self.selectUserID = ko.observable('');
        }

        search() {
            let self = this;
            if (nts.uk.text.isNullOrEmpty(self.searchValue())) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_438", messageParams: [nts.uk.resource.getText("CAS013_33")] });
                return;
            }
            var key = self.searchValue().trim();
            if(key.length>3000){
                return;
            }
            var Special = self.special();
            var Multi = self.multi();
            var roleType =  self.roleTypeParam;
            nts.uk.ui.block.invisible(); 
            service.searchUser(key, Special, Multi, roleType).done(function(data) {
                var items = [];
                items = _.sortBy(data, ["loginID"]);
                self.dataSource(items);
            }).always(() => {
                nts.uk.ui.block.clear();
            });
        }

        enterPress() {
            this.search();
        }

        decision() {
            var self = this;
            if (nts.uk.text.isNullOrEmpty(self.selectUserID())) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_218", messageParams: [nts.uk.resource.getText("CAS013_19")] });
                return;
            }

            var selectUser: any = _.find(self.dataSource(), (item: any) => {
                return item.userID == self.selectUserID()
            });

            let dataSetShare = {
                decisionUserID: selectUser.userID,
                decisionLoginID: selectUser.loginID,
                decisionName: selectUser.userName
            };
            nts.uk.ui.windows.setShared("UserInfo", dataSetShare);
            self.cancel_Dialog();
        }

        cancel_Dialog(): any {
            nts.uk.ui.windows.close();
        }

    }
}