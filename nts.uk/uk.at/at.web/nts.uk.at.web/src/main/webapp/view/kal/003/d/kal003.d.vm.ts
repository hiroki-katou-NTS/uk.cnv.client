module nts.uk.at.view.kal003.d.viewmodel {
    import model = kal003.share.model;

    export class ScreenModel {
        itemList: KnockoutObservableArray<model.ItemModel>;
        selectedCode: KnockoutObservable<number>;

        constructor() {
            var self = this;
            self.itemList = ko.observableArray(model.getListCategory());
            
            var selectedCode = nts.uk.ui.windows.getShared("inputKAL003d");
            self.selectedCode = ko.observable(selectedCode ? selectedCode : 0);
        }

        private selectCategory(): void {
            var self = this;
            nts.uk.ui.windows.setShared('outputKAL003d', self.selectedCode());
            nts.uk.ui.windows.close();
        }

        private closeWindows(): void {
            nts.uk.ui.windows.setShared('KAL003dCancel', true);
            nts.uk.ui.windows.close();
        }
    }

}