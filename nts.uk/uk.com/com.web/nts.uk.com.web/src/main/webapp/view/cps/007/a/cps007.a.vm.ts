module cps007.a.vm {
    import alert = nts.uk.ui.dialog.alert;
    
    let __viewContext: any = window['__viewContext'] || {},
        block = window["nts"]["uk"]["ui"]["block"]["grayout"],
        unblock = window["nts"]["uk"]["ui"]["block"]["clear"];

    export class ViewModel {
        layout: KnockoutObservable<Layout> = ko.observable(new Layout({ id: '', code: '', name: '' }));

        constructor() {
            let self = this,
                layout = self.layout();

            self.start();
        }

        start() {
            let self = this,
                layout = self.layout();

            // get layout info on startup
            service.getData().done((x: ILayout) => {
                layout.id(x.id);
                layout.code(x.code);
                layout.name(x.name);
                layout.itemsClassification(x.itemsClassification);
            });
        }

        saveData() {
            let self = this,
                layout: ILayout = ko.toJS(self.layout),
                command: any = {
                    layoutID: layout.id,
                    layoutCode: layout.code,
                    layoutName: layout.name,
                    itemsClassification: _(layout.itemsClassification || []).map((item, i) => {
                        return {
                            dispOrder: i + 1,
                            personInfoCategoryID: item.personInfoCategoryID,
                            layoutItemType: item.layoutItemType,
                            listItemClsDf: _(item.listItemDf || []).map((def, j) => {
                                return {
                                    dispOrder: j + 1,
                                    personInfoItemDefinitionID: def.id
                                };
                            }).value()
                        };
                    }).value()
                };

            // push data layout to webservice
            block();
            service.saveData(command).done(() => {
                self.start();
                unblock();
            }).fail((mes) => {
                unblock();
                alert(mes.message);
            });
        }
    }

    interface IItemClassification {
        layoutID?: string;
        dispOrder?: number;
        className?: string;
        personInfoCategoryID?: string;
        layoutItemType: number;
        listItemDf: Array<IItemDefinition>;
    }

    interface IItemDefinition {
        id: string;
        perInfoCtgId?: string;
        itemCode?: string;
        itemName: string;
    }

    interface ILayout {
        id: string;
        code: string;
        name: string;
        editable?: boolean;
        itemsClassification?: Array<IItemClassification>;
    }

    class Layout {
        id: KnockoutObservable<string> = ko.observable('');
        code: KnockoutObservable<string> = ko.observable('');
        name: KnockoutObservable<string> = ko.observable('');
        editable: KnockoutObservable<boolean> = ko.observable(true);
        itemsClassification: KnockoutObservableArray<IItemClassification> = ko.observableArray([]);

        constructor(param: ILayout) {
            let self = this;

            self.id(param.id);
            self.code(param.code);
            self.name(param.name);

            if (param.editable != undefined) {
                self.editable(param.editable);
            }

            // replace x by class that implement this interface
            self.itemsClassification(param.itemsClassification || []);
        }
    }
}