module nts.uk.com.view.cps005.a {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import textUK = nts.uk.text;
    export module viewmodel {
        export class ScreenModel {
            currentData: KnockoutObservable<DataModel>;
            isUpdate: boolean = false;
            isEnableButtonProceedA: KnockoutObservable<boolean>;
            constructor() {
                let self = this,
                    dataModel = new DataModel(null);
                self.currentData = ko.observable(dataModel);
                self.isEnableButtonProceedA = ko.observable(true);
            }

            startPage(): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
                new service.Service().getAllPerInfoCtg().done(function(data: IData) {
                    self.isUpdate = false;
                    self.currentData(new DataModel(data));
                    if (data && data.categoryList && data.categoryList.length > 0) {
                        self.isUpdate = true;
                        self.currentData().perInfoCtgSelectCode(data.categoryList[0].id);
                        self.currentData().isEnableButtonProceed(true);
                    } else {
                        self.register();
                    }
                    dfd.resolve();
                })

                return dfd.promise();
            }

            reloadData(newCtgName?: string) {
                let self = this,
                    dfd = $.Deferred();
                new service.Service().getAllPerInfoCtg().done(function(data: IData) {
                    self.isUpdate = false;
                    if (data && data.categoryList && data.categoryList.length > 0) {
                        self.currentData().categoryList(_.map(data.categoryList, item => { return new PerInfoCtgModel(item) }));
                        self.isUpdate = true;
                        self.currentData().isEnableButtonProceed(true);
                        if (newCtgName) {
                            let newCtg = _.find(data.categoryList, item => { return item.categoryName == newCtgName });
                            self.currentData().perInfoCtgSelectCode(newCtg ? newCtg.id : "");
                        }
                    } else {
                        self.register();
                    }
                    dfd.resolve();
                })
                return dfd.promise();
            }

            register() {
                let self = this;
                nts.uk.ui.errors.clearAll();
                self.currentData().perInfoCtgSelectCode("");
                self.currentData().currentCtgSelected(new PerInfoCtgModel(null));
                self.isUpdate = false;
                $("#category-name-control").focus();
                self.currentData().isEnableButtonProceed(true);
                self.currentData().isEnableButtonOpenDialog(false);
                self.currentData().isHisTypeUpdateModel(false);
            }

            addUpdateData() {
                let self = this;
                if (!self.currentData().currentCtgSelected().perInfoCtgName()) {
                    return;
                }
                if (self.isUpdate) {
                    let updateCategory = new UpdatePerInfoCtgModel(self.currentData().currentCtgSelected());
                    new service.Service().updatePerInfoCtg(updateCategory).done(function() {
                        self.reloadData();
                        info({ messageId: "Msg_15" });
                    }).fail(error => {
                        alertError({ messageId: error.message });
                    });
                } else {
                    let newCategory = new AddPerInfoCtgModel(self.currentData().currentCtgSelected());
                    new service.Service().addPerInfoCtg(newCategory).done(() => {
                        self.reloadData(newCategory.categoryName);
                        info({ messageId: "Msg_15" }).then(() => {
                            confirm({ messageId: "Msg_213" }).ifYes(() => {
                                setShared('categoryId', self.currentData().perInfoCtgSelectCode());
                                modal("/view/cps/005/b/index.xhtml").onClosed(() => { });
                            }).ifNo(() => {
                                return;
                            })
                        });
                    }).fail(error => {
                        alertError({ messageId: error.message });
                    });
                }
            }

            openDialogB() {
                let self = this;
                setShared('categoryId', self.currentData().perInfoCtgSelectCode());
                modal("/view/cps/005/b/index.xhtml").onClosed(() => { });
            }
        }
    }

    export class DataModel {
        categoryList: KnockoutObservableArray<PerInfoCtgModel> = ko.observableArray([]);
        perInfoCtgSelectCode: KnockoutObservable<string> = ko.observable("");
        currentCtgSelected: KnockoutObservable<PerInfoCtgModel> = ko.observable(new PerInfoCtgModel(null));
        isEnableButtonProceed: KnockoutObservable<boolean> = ko.observable(true);
        isEnableButtonOpenDialog: KnockoutObservable<boolean> = ko.observable(false);
        isHisTypeUpdateModel: KnockoutObservable<boolean> = ko.observable(false);
        historyClassification: Array<any> = [
            { code: 1, name: getText("CPS005_53") },
            { code: 2, name: getText("CPS005_54") },
        ];
        //<!-- mapping CategoryType enum value = 3 or 4 or 5 . But using enum HistoryType to display -->
        historyTypes: any = new Array<any>();
        //mapping CategoryType enum value = 1 or 2. Theo thiết kế không lấy từ enum CategoryType
        singleMultipleType: Array<any> = [
            { value: 1, name: getText("CPS005_55") },
            { value: 2, name: getText("CPS005_56") },
        ];
        constructor(data: IData) {
            let self = this;
            if (data) {
                self.categoryList(_.map(data.categoryList, item => { return new PerInfoCtgModel(item) }));
                self.historyTypes = data.historyTypes ? data.historyTypes : [];
            }
            //subscribe select category code
            self.perInfoCtgSelectCode.subscribe(newId => {
                if (textUK.isNullOrEmpty(newId)) return;
                nts.uk.ui.errors.clearAll();
                new service.Service().getPerInfoCtgWithItemsName(newId).done(function(data: IPersonInfoCtg) {
                    self.currentCtgSelected(new PerInfoCtgModel(data));
                    self.isHisTypeUpdateModel(true);
                    self.isEnableButtonProceed(true);
                    self.isEnableButtonOpenDialog(true);
                    if (self.currentCtgSelected().fixedIsSelected()) {
                        self.isEnableButtonProceed(false);
                    }
                });
            });
        }
    }

    export class PerInfoCtgModel {
        id: string = "";
        categoryName: string = "";
        perInfoCtgName: KnockoutObservable<string> = ko.observable("");
        historyFixedName: string = "";
        categoryType: number = 1;
        categoryTypeName: string = "";
        historyClassSelected: KnockoutObservable<number> = ko.observable(1);
        historyClassSelectedText: KnockoutObservable<string> = ko.observable("");
        // historyTypesSelected and singleMulTypeSelected == categoryType
        historyTypesSelected: KnockoutObservable<number> = ko.observable(1);
        singleMulTypeSelected: KnockoutObservable<number> = ko.observable(1);
        itemNameList: KnockoutObservableArray<PerInfoItemModel> = ko.observableArray([]);
        //all visiable
        historyTypesDisplay: KnockoutObservable<boolean> = ko.observable(true);
        fixedIsSelected: KnockoutObservable<boolean> = ko.observable(false);
        constructor(data: IPersonInfoCtg) {
            let self = this;
            if (data) {
                self.id = data.id || "";
                self.categoryName = data.categoryName || "";
                self.perInfoCtgName(data.categoryName || "");
                self.itemNameList(_.map(data.itemNameList, item => { return new PerInfoItemModel(item) }));
                self.historyFixedName = (data.categoryType == 1 || data.categoryType == 2) ? getText("CPS005_54") : getText("CPS005_53");
                self.categoryType = data.categoryType;
                switch (self.categoryType) {
                    case 1:
                        self.categoryTypeName = getText("CPS005_55");
                        break;
                    case 2:
                        self.categoryTypeName = getText("CPS005_56");
                        break;
                    case 3:
                        self.categoryTypeName = getText("Enum_HistoryTypes_CONTINUOUS");
                        break;
                    case 4:
                        self.categoryTypeName = getText("Enum_HistoryTypes_NO_DUPLICATE");
                        break;
                    case 5:
                        self.categoryTypeName = getText("Enum_HistoryTypes_DUPLICATE");
                        break;
                }
                self.historyClassSelected((data.categoryType == 1 || data.categoryType == 2) ? 2 : 1);
                self.singleMulTypeSelected(data.categoryType || 1);
                self.historyTypesDisplay(false);
                if (self.historyClassSelected() == 1) {
                    self.historyTypesSelected(data.categoryType - 2);
                    self.singleMulTypeSelected(1);
                    self.historyTypesDisplay(true);
                    self.historyClassSelectedText(getText("CPS005_53"));
                } else {
                    self.historyClassSelectedText(getText("CPS005_54"));    
                }
                self.fixedIsSelected(data.isFixed == 1 ? true : false);
            }
            //subscribe select history type (1: history, 2: not history)
            self.historyClassSelected.subscribe(newHisClassification => {
                if (textUK.isNullOrEmpty(newHisClassification)) return;
                self.historyTypesDisplay(false);
                if (newHisClassification == 1) {
                    self.historyTypesDisplay(true);
                }
            });
        }
    }

    export class PerInfoItemModel {
        itemName: string;
        constructor(itemName: string) {
            let self = this;
            self.itemName = itemName;
        }
    }

    export class AddPerInfoCtgModel {
        categoryName: string;
        categoryType: number;
        constructor(data: PerInfoCtgModel) {
            let self = this;
            self.categoryName = data.perInfoCtgName();
            if (data.historyClassSelected() == 2) {
                self.categoryType = data.singleMulTypeSelected();
            } else {
                self.categoryType = data.historyTypesSelected() + 2;
            }
        }
    }

    export class UpdatePerInfoCtgModel {
        id: string
        categoryName: string;
        categoryType: number;
        constructor(data: PerInfoCtgModel) {
            let self = this;
            self.id = data.id;
            self.categoryName = data.perInfoCtgName();
            if (data.historyClassSelected() == 2) {
                self.categoryType = data.singleMulTypeSelected();
            } else {
                self.categoryType = data.historyTypesSelected() + 2;
            }
        }
    }

    interface IData {
        historyTypes: any;
        categoryList: Array<IPersonInfoCtg>;
    }

    interface IPersonInfoCtg {
        id: string;
        categoryName: string;
        isFixed?: number;
        categoryType?: number;
        itemNameList?: Array<string>;
    }
}

