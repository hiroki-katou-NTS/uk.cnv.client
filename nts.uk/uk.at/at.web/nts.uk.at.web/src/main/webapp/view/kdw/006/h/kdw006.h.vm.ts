module nts.uk.at.view.kdw006.h.viewmodel {
    let __viewContext: any = window["__viewContext"] || {};
    export class ScreenModel {
        items: KnockoutObservableArray<ItemModel>;
        columns: KnockoutObservableArray<NtsGridListColumn>;
        currentCodeList: KnockoutObservableArray<any>;
        multicheck: KnockoutObservable<boolean>;

        constructor() {
            let self = this;
            self.multicheck = ko.observable(false);
            self.items = ko.observableArray([]);
            this.columns = ko.observableArray([
                { headerText: 'コード', key: 'code', width: 100, hidden: true},
                { headerText: '名称', key: 'name', width: 250}, 
            ]); 
            
            this.currentCodeList = ko.observableArray([]);
            
            let listAppType = __viewContext.enums.ApplicationType;
            _.forEach(listAppType, (obj) => {
                self.items.push(new ItemModel(obj.value, obj.name));
            });
        }

        returnData() {
            let self = this;
            nts.uk.ui.windows.setShared('kdw006HResult', self.currentCodeList());
            nts.uk.ui.windows.close();
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }

        startPage(): JQueryPromise<any> {
            let self = this;
            var dfd = $.Deferred();
            let getData = nts.uk.ui.windows.getShared("kdw006CResult");
            self.currentCodeList(getData.appTypes);
            self.multicheck(getData.multi);
            dfd.resolve();
            return dfd.promise();
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