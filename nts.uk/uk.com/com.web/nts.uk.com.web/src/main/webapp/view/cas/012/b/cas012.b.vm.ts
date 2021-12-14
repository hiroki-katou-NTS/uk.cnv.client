module nts.uk.com.view.cas012.b.viewmodel {


    export class ScreenModel {
        dataSource: any;
        columns: KnockoutObservableArray<any>;
        //search
        searchValue: KnockoutObservable<string>;
        //user
        selectUserID: KnockoutObservable<string>;
        userName: KnockoutObservable<string>;

        constructor() {
            var self = this;

            self.searchValue = ko.observable('');
            self.dataSource = ko.observableArray([]);
            self.columns = ko.observableArray([
                { headerText: 'userID', key: 'userID', width: 100, hidden: true },
                { headerText: nts.uk.resource.getText("CAS012_25"), key: 'loginID', width: 120, columnCssClass: "colStyle" },
                { headerText: nts.uk.resource.getText("CAS012_26"), key: 'userName', width: 230, columnCssClass: "colStyle" }

            ]);
            self.selectUserID = ko.observable('');
        }

        search() {
            let self = this;
            if (nts.uk.text.isNullOrEmpty(self.searchValue())) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_438" , messageParams: [nts.uk.resource.getText("CAS012_20")]});
                return;
            }
            if(self.searchValue().trim().length>3000){
                return;
            }
            service.searchUser(self.searchValue().trim()).done(function(data) {
                self.dataSource(data);
            });
        }
        
        enterPress() {
            this.search();
        }

        decision() {
            var self = this;
            if (nts.uk.text.isNullOrEmpty(self.selectUserID())) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_218" , messageParams: [nts.uk.resource.getText("CAS012_24")]});
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