module test.viewmodel {
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import modal = nts.uk.ui.windows.sub.modal;
    import block = nts.uk.ui.block;
    export class ScreenModel {
        options: Option;
        currentIds: KnockoutObservable<any> = ko.observable([]);
        currentCodes: KnockoutObservable<any> = ko.observable([]);
        currentNames: KnockoutObservable<any> = ko.observable([]);
        alreadySettingList: KnockoutObservableArray<any> = ko.observable(['1']);
        multiple: KnockoutObservable<any> = ko.observable(true);
        isAlreadySetting: KnockoutObservable<any> = ko.observable(true);
        showEmptyItem: KnockoutObservable<any> = ko.observable(true);
        selectedMode: KnockoutObservable<any> = ko.observable(1);
        constructor() {
            let self = this;
            self.options = {
                // neu muon lay code ra tu trong list thi bind gia tri nay vao
                currentCodes: self.currentCodes,
                currentNames: self.currentNames,
                // tuong tu voi id
                currentIds: self.currentIds,
                //
                multiple: self.multiple(),
                tabindex:2,
                isAlreadySetting: self.isAlreadySetting(),
                alreadySettingList: self.alreadySettingList,
                // show o tim kiem
                showSearch: true,
                // show empty item
                showEmptyItem: self.showEmptyItem(),
                // trigger reload lai data cua component
                reloadData: ko.observable(''),
                reloadComponent: ko.observable({}),
                height: 400,
                // NONE = 0, FIRST = 1, ALL = 2
                selectedMode: self.selectedMode()
            };
        }

        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            dfd.resolve();
            return dfd.promise();
        }

        testAlreadySetting() {
            let self = this;
            let data = {
                multiple: self.multiple(),
                isAlreadySetting: self.isAlreadySetting(),
                showEmptyItem: self.showEmptyItem(),
                selectedMode: self.selectedMode()
            }
            setShared('KCP011_TEST', data);
            modal("/view/kcp/011/test2/index.xhtml").onClosed(() => {

            });
        }

    }
}