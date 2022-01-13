module nts.uk.at.kdl013.a {
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import getText = nts.uk.resource.getText;

    const PATH = {        
        GET_ALL_TASK_BY_ATTENDANCE_AND_DATE: "at/record/task/management/supplementinfo/getByAtdIdAndDate"
    }
    @bean()
    class Kdl013aViewModel extends ko.ViewModel {
        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        columns: KnockoutObservableArray<any> = ko.observableArray([]);
        currentCodeList: KnockoutObservableArray<any> = ko.observableArray([]);
        dataSoure: Array<ItemModel> = [];
        posibleItems: Array<number> = [];
        dataShare: any;
        constructor(params: any) {
            super();
            let self = this;
            self.dataShare = getShared('KDL013Params');

            self.columns = ko.observableArray([
                { headerText: getText("KDL013_3"), prop: 'code', width: 90 },
                { headerText: getText("KDL013_4"), prop: 'name', width: 200, formatter: _.escape }
            ]);
            self.loadData();
            _.defer(() => { $(".ntsSearchBox").focus(); });
        }

        loadData(): void {
            let self = this;
            self.dataSoure.push(new ItemModel(" ", getText("KDL013_7")));
            self.currentCodeList( (!_.isUndefined(self.dataShare.selectedCode) && (self.dataShare.selectedCode.length > 0))  ? self.dataShare.selectedCode : " ");
            self.$blockui("invisible");
            
            self.$ajax(PATH.GET_ALL_TASK_BY_ATTENDANCE_AND_DATE, self.dataShare ).done((lstItem: Array<any>) => {
                let data = _.map(lstItem, item => { return new ItemModel(item.code, item.name) });
                self.dataSoure = self.dataSoure.concat(_.sortBy(data, ['code']));
                self.items(self.dataSoure);
                $(".ntsSearchBox").focus();               
            }).fail((res) => {
                self.$dialog.alert({ messageId: res.messageId }).then(() => {
                    self.closeDialog();
                });
            }).always(() => {
                self.$blockui("hide");
            });            
        }

        register(): void {
            let self = this;
            if (self.currentCodeList().length == 0) {            
                self.$dialog.error({ messageId: 'Msg_2305' });
            } else {
                setShared('KDL013ParamsReturn', self.currentCodeList()[0] == " " ? "" : self.currentCodeList());
                nts.uk.ui.windows.close();
            }
        }

        //Close Dialog
        closeDialog(): void {            
            nts.uk.ui.windows.close();
        }
    }    

    class ItemModel {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
}
