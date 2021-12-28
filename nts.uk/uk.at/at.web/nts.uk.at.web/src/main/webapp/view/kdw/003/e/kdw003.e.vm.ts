module nts.uk.at.view.kdw003.e.viewmodel {
    import setShared = nts.uk.ui.windows.setShared;
    import windows = nts.uk.ui.windows;

    export class ScreenModel {
        dateRefer: KnockoutObservable<any>;
        itemList: KnockoutObservableArray<ItemModel>;
        selectedCode: KnockoutObservable<number>;
        dateJump:KnockoutObservable<any>;

        constructor() {
            let self = this;
            self.dateRefer = ko.observable(moment(new Date(), "YYYY/MM/DD").format("YYYY/MM/DD(dd)"));
            self.itemList = ko.observableArray([]);
            self.selectedCode = ko.observable(1905);
            self.dateJump = ko.observable(null);
        }

        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            let dataShare = nts.uk.ui.windows.getShared("shareToKdw003e");
           // dataShare.listValue.push({value: 14, fieldName:"申請一覧"});
            var temp = [];
            for (var i = 0; i < dataShare.listValue.length; i++) {
                temp.push(new ItemModel(dataShare.listValue[i].value, dataShare.listValue[i].fieldName, ""));
            }
            self.itemList(temp);
            self.dateRefer(moment(dataShare.date, "YYYY/MM/DD").format("YYYY/MM/DD(dd)"));
            self.dateJump(moment(dataShare.date, "YYYY/MM/DD"))
            dfd.resolve();
            return dfd.promise();
        }

        extract() {
            let self = this;
            // set return value
             nts.uk.ui.windows.setShared("shareToKdw003a", Number(self.selectedCode())); 
             windows.close();
        }

        closeDialog(): void {
            windows.close();
        }
    }

    class ItemModel {
        code: number;
        name: string;
        description: string;

        constructor(code: number, name: string, description: string) {
            this.code = code;
            this.name = name;
            this.description = description;
        }
    }


}