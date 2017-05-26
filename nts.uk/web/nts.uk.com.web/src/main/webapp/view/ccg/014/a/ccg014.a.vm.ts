module ccg014.a.viewmodel {
    import commonModel = ccg.model;
    import windows = nts.uk.ui.windows;
    import errors = nts.uk.ui.errors;
    import resource = nts.uk.resource;
    import util = nts.uk.util;

    export class ScreenModel {
        // TitleMenu List
        listTitleMenu: KnockoutObservableArray<any>;
        selectedTitleMenuCD: KnockoutObservable<string>;
        columns: KnockoutObservableArray<any>;
        // TitleMenu Details
        selectedTitleMenu: KnockoutObservable<model.TitleMenu>;
        isCreate: KnockoutObservable<boolean>;
        // Enable Copy
        enableCopy: KnockoutComputed<boolean>;

        constructor() {
            var self = this;
            // TitleMenu List
            self.listTitleMenu = ko.observableArray([]);
            self.selectedTitleMenuCD = ko.observable(null);
            self.selectedTitleMenuCD.subscribe((value) => {
                self.findSelectedTitleMenu(value);
                self.changePreviewIframe(self.selectedTitleMenu().layoutID());
            });
            self.columns = ko.observableArray([
                { headerText: resource.getText('CCG014_11'), key: 'titleMenuCD', width: 70 },
                { headerText: resource.getText('CCG014_12'), key: 'name', width: 260 }
            ]);
            // TitleMenu Details
            self.selectedTitleMenu = ko.observable(null);
            self.isCreate = ko.observable(null);
            self.isCreate.subscribe((value) => {
                self.changeInitMode(value);
            });
            // Enable Copy
            self.enableCopy = ko.computed(() => {
                return (!self.isCreate() && !util.isNullOrEmpty(self.selectedTitleMenuCD()));
            });
        }

        /** Start Page */
        startPage(): JQueryPromise<any> {
            var dfd = this.reloadData();
            dfd.done(() => {
                this.selectTitleMenuByIndex(0);
            });
            return dfd;
        }

        /** Create Button Click */
        createButtonClick() {
            var self = this;
            self.isCreate(true);
            self.selectedTitleMenuCD(null);
            self.selectedTitleMenu(new model.TitleMenu("", "", ""));
            errors.clearAll();
            $(".nts-input").ntsError("clear");
        }

        /** Registry Button Click */
        registryButtonClick() {
            var self = this;
            var titleMenu = ko.mapping.toJS(self.selectedTitleMenu);
            var titleMenuCD = titleMenu.titleMenuCD;
            $(".nts-input").trigger("validate");
            _.defer(() => {
                if (!$(".nts-input").ntsError("hasError")) {
                    if (self.isCreate() === true) {
                        service.createTitleMenu(titleMenu).done((data) => {
                            nts.uk.ui.dialog.alert({ messageId: "Msg_15" });
                            self.reloadData().done(() => {
                                self.selectTitleMenuByIndexByCode(titleMenuCD);
                            });
                        }).fail((res) => {
                            nts.uk.ui.dialog.alert({ messageId: "Msg_3" });
                        });
                    }
                    else {
                        service.updateTitleMenu(titleMenu).done((data) => {
                            self.reloadData();
                            nts.uk.ui.dialog.alert({ messageId: "Msg_15" });
                        });
                    }
                }
            });
        }

        /**Delete Button Click */
        removeTitleMenu() {
            var self = this;
            if (self.selectedTitleMenuCD() !== null) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(function() {
                    service.deleteTitleMenu(self.selectedTitleMenuCD()).done(() => {
                        var index = _.findIndex(self.listTitleMenu(), ['titleMenuCD', self.selectedTitleMenu().titleMenuCD()]);
                        index = _.min([self.listTitleMenu().length - 2, index]);
                        self.reloadData().done(() => {
                            self.selectTitleMenuByIndex(index);
                        });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alert({ messageId: "Msg_16" })
                    });
                })
            }
        }

        /** Open Copy TitleMenu (CCG 014B Dialog) */
        openBDialog() {
            var self = this;
            var selectTitleMenu = _.find(self.listTitleMenu(), ['titleMenuCD', self.selectedTitleMenu().titleMenuCD()]);
            windows.setShared("copyData", selectTitleMenu);
            windows.sub.modal("/view/ccg/014/b/index.xhtml", { title: 'タイトルメニューのコピー', dialogClass: "no-close" }).onClosed(() => {
                var copiedTitleMenuCD = windows.getShared("copyTitleMenuCD");
                if (copiedTitleMenuCD) {
                    self.reloadData().done(() => {
                        self.selectTitleMenuByIndexByCode(copiedTitleMenuCD);
                        nts.uk.ui.dialog.alert({ messageId: "Msg_20" });
                    });
                }
            });
        }

        /** Open  Layout Setting(CCG 031_1) Dialog */
        open031_1Dialog() {
            var self = this;
            var layoutInfo: commonModel.TransferLayoutInfo = {
                parentCode: self.selectedTitleMenuCD(),
                layoutID: self.selectedTitleMenu().layoutID(),
                pgType: 1
            };
            windows.setShared("layout", layoutInfo, false);
            windows.sub.modal("/view/ccg/031/a/index.xhtml", { title: 'レイアウト設定', dialogClass: "no-close" }).onClosed(() => {
                let returnInfo: commonModel.TransferLayoutInfo = windows.getShared("layout");
                self.selectedTitleMenu().layoutID(returnInfo.layoutID);
                _.find(self.listTitleMenu(), ["titleMenuCD", returnInfo.parentCode]).layoutID = returnInfo.layoutID;
                self.changePreviewIframe(returnInfo.layoutID);
            });
        }

        /** Open FlowMenu Setting(030A Dialog) */
        open030A_Dialog() {
            windows.sub.modal("/view/ccg/030/a/index.xhtml", { title: 'フローメニューの設定', dialogClass: "no-close" });
        }

        /** Get Selected TitleMenu */
        private findSelectedTitleMenu(titleMenuCD: string) {
            var self = this;
            $(".nts-input").ntsError("clear");
            var selectedTitleMenu = _.find(self.listTitleMenu(), ['titleMenuCD', titleMenuCD]);
            if (selectedTitleMenu !== undefined) {
                self.isCreate(false);
                self.selectedTitleMenu(new model.TitleMenu(selectedTitleMenu.titleMenuCD, selectedTitleMenu.name, selectedTitleMenu.layoutID));
            }
            else {
                self.isCreate(true);
                self.selectedTitleMenu(new model.TitleMenu("","",""));
            }
        }

        /** Init Mode */
        private changeInitMode(isCreate: boolean) {
            var self = this;
            if (isCreate === true) {
                self.selectedTitleMenuCD(null);
            }
        }

        clearError(): any {
            var self = this;
            if (self.selectedTitleMenu().titleMenuCD !== null) { errors.clearAll(); }
            if (self.selectedTitleMenu().name !== null) { errors.clearAll(); }
        }

        /** Reload data from server */
        private reloadData(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            /** Get list TitleMenu*/
            service.getAllTitleMenu().done(function(listTitleMenu: Array<model.TitleMenu>) {
                self.listTitleMenu(listTitleMenu);
                if (listTitleMenu.length > 0) {
                    self.isCreate(false);
                }
                else {
                    self.findSelectedTitleMenu(null);
                    self.isCreate(true);
                }
                dfd.resolve();
            }).fail(function(error) {
                dfd.fail();
                alert(error.message);
            });
            return dfd.promise();
        }

        /** Select TitleMenu by Code: Create & Update case*/
        private selectTitleMenuByIndexByCode(code: string) {
            this.selectedTitleMenuCD(code);
            
        }

        /** Select TitleMenu by Index: Start & Delete case */
        private selectTitleMenuByIndex(index: number) {
            var self = this;
            var selectTitleMenuByIndex = _.nth(self.listTitleMenu(), index);
            if (selectTitleMenuByIndex !== undefined)
                self.selectedTitleMenuCD(selectTitleMenuByIndex.titleMenuCD);
            else
                self.selectedTitleMenuCD(null);
        }

        private changePreviewIframe(layoutID: string): void {
            $("#preview-iframe").attr("src", "/nts.uk.com.web/view/ccg/common/previewWidget/index.xhtml?layoutid=" + layoutID);
        }

    }

    export module model {
        export class TitleMenu {
            titleMenuCD: KnockoutObservable<string>;
            name: KnockoutObservable<string>;
            layoutID: KnockoutObservable<string>;
            constructor(titleMenuCD: string, name: string, layoutID: string) {
                this.titleMenuCD = ko.observable(titleMenuCD);
                this.name = ko.observable(name);
                this.layoutID = ko.observable(layoutID);
            }
        }
    }
}