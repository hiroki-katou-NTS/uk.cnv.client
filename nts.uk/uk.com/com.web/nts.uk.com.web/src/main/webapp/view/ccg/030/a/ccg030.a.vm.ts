module ccg030.a.viewmodel {
    import windows = nts.uk.ui.windows;
    import errors = nts.uk.ui.errors;
    import util = nts.uk.util;
    import text = nts.uk.text;
    import resource = nts.uk.resource;
    import model = nts.uk.com.view.ccg.model;

    export class ScreenModel {
        // FlowMenu
        listFlowMenu: KnockoutObservableArray<model.PlacementPartDto>;
        columns: KnockoutObservableArray<any>;
        selectedFlowMenuCD: KnockoutObservable<string>;
        selectedFlowMenu: KnockoutObservable<model.FlowMenu>;
        // UI Binding
        isCreate: KnockoutObservable<boolean>;
        enablePreview: KnockoutComputed<boolean>;

        constructor() {
            var self = this;
            // list
            self.listFlowMenu = ko.observableArray([]);
            self.selectedFlowMenuCD = ko.observable(null);
            self.selectedFlowMenuCD.subscribe((value) => {
                self.findFlowMenu(value);
            });
            self.columns = ko.observableArray([
                // { headerText: nts.uk.resource.getText("CCG030_19"), key: 'defClassAtr', width: 40 , columnCssClass: 'halign-center', template: '{{if ${defClassAtr} == 1 }}<i class="icon icon-dot "></i>{{/if}}' , hidden: true },
                { headerText: nts.uk.resource.getText("CCG030_9"), key: 'topPageCode', width: 50 },
                { headerText: nts.uk.resource.getText("CCG030_10"), key: 'topPageName', width: 260 }
            ]);
            // Details
            self.selectedFlowMenu = ko.observable(new model.FlowMenu());
            self.isCreate = ko.observable(null);
            // Enable
            self.enablePreview = ko.pureComputed(() => {
                return !text.isNullOrEmpty(self.selectedFlowMenu().fileID());
            });
        }

        /** Start page */
        startPage(): JQueryPromise<any> {
            var self = this;
            nts.uk.ui.block.invisible();
            var dfd = self.reloadData();
            dfd.done(() => {
                nts.uk.ui.block.clear();
                self.selectFlowMenuByIndex(0);
            });
            return dfd;
        }

        /** Creat new FlowMenu */
        createNewFlowMenu() {
            var self = this;
            self.isCreate(true);
            var topPageCode = ko.mapping.toJS('');
            self.selectedFlowMenuCD(null);
            self.selectedFlowMenu().topPageCode("");
            self.selectedFlowMenu().topPageName("");
            self.selectedFlowMenu().fileName("");
            self.selectedFlowMenu().fileID("");
            self.focusToInput();
            errors.clearAll();

        }

        /** Click Registry button */
        registryFlowMenu() {
            var self = this;
            $(".nts-input").trigger("validate");
            if (util.isNullOrEmpty(self.selectedFlowMenu().fileID())) {
                let CCG030_26 = resource.getText("CCG030_26");
                $('#file_upload').ntsError('set', resource.getMessage("MsgB_2", [CCG030_26]), "MsgB_2");
            }
            if (!errors.hasError()) {
                self.selectedFlowMenu().topPageCode(text.padLeft($("#inpCode").val(), '0', 4));
                var flowMenu = ko.mapping.toJS(self.selectedFlowMenu);
                var topPageCode = flowMenu.topPageCode;
                nts.uk.ui.block.invisible();
                if (self.isCreate() === true) {
                    service.createFlowMenu(flowMenu).done((data) => {
                        self.reloadData().done(() => {
                            self.selectedFlowMenuCD(topPageCode);
                            nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                                self.focusToInput();
                            });
                        });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                }
                else {
                    service.updateFlowMenu(flowMenu).done((data) => {
                        self.reloadData();
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.focusToInput();
                        });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError(res);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                }
            }
        }

        /** Delete FlowMenu */
        deleteFlowMenu() {
            var self = this;
            if (self.selectedFlowMenuCD() !== null) {
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(function() {
                    nts.uk.ui.block.invisible();
                    service.deleteFlowMenu(self.selectedFlowMenu().topPageCode()).done(() => {
                        var index = _.findIndex(self.listFlowMenu(), ['topPageCode', self.selectedFlowMenu().topPageCode()]);
                        index = _.min([self.listFlowMenu().length - 2, index]);
                        self.reloadData().done(() => {
                            self.selectFlowMenuByIndex(index);
                            nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(() => {
                                self.focusToInput();
                                errors.clearAll();
                            });
                        });
                    }).fail((res) => {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_76" });
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                });
            }
        }

        /** When upload file is finished */
        uploadFinish(fileInfo: any): void {
            var self = this;
            self.selectedFlowMenu().fileID(fileInfo.id);
            self.selectedFlowMenu().fileName(fileInfo.originalName.length === 0 ? resource.getText("CCG030_26") : fileInfo.originalName);
            $('#file_upload').ntsError("clear");
        }

        /** Download zip file */
        download() {
            nts.uk.request.specials.donwloadFile(this.selectedFlowMenu().fileID());

        }

        /** Close Dialog */
        closeDialog(): void {
            nts.uk.ui.windows.close();
        }

        /** Open ccg030 B Dialog */
        open030B_Dialog() {
            var self = this;
            nts.uk.ui.windows.setShared("flowmenu", ko.mapping.toJS(self.selectedFlowMenu()), false);
            nts.uk.ui.windows.setShared("fileID", self.selectedFlowMenu().fileID(), false);
            nts.uk.ui.windows.sub.modal("/view/ccg/030/b/index.xhtml");
        }

        /** Find Current FlowMenu by ID */
        private findFlowMenu(flowmenuCD: string): void {
            var self = this;
            var selectedFlowmenu = _.find(self.listFlowMenu(), ['topPageCode', flowmenuCD]);
            if (selectedFlowmenu !== undefined) {
                self.selectedFlowMenu(new model.FlowMenu(selectedFlowmenu));
                self.isCreate(false);
            }
            else {
                self.selectedFlowMenu(new model.FlowMenu());
                self.createNewFlowMenu();
            }
            self.focusToInput();
            _.defer(() => { errors.clearAll(); });
        }

        /** Select FlowMenu by Index: Start & Delete case */
        private selectFlowMenuByIndex(index: number) {
            var self = this;
            var selectFlowMenuByIndex = _.nth(self.listFlowMenu(), index);
            if (selectFlowMenuByIndex !== undefined)
                self.selectedFlowMenuCD(selectFlowMenuByIndex.topPageCode);
            else
                self.selectedFlowMenuCD(null);
        }

        /** Reload Data */
        private reloadData(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            // Get list FlowMenu
            service.fillAllFlowMenu().done((listFlowMenu: Array<any>) => {
                listFlowMenu = _.orderBy(listFlowMenu, ["topPageCode"], ["asc"]);
                self.listFlowMenu(listFlowMenu);
                if (listFlowMenu.length > 0) {
                    self.isCreate(false);
                }
                else {
                    self.findFlowMenu(null);
                    self.createNewFlowMenu();
                }
                dfd.resolve();
            }).fail((error) => {
                dfd.fail();
                alert(error);
            });
            return dfd.promise();
        }

        /** Focus to input */
        focusToInput() {
            if (this.isCreate() == true) {
                $("#inpCode").focus();
            }
            else {
                $("#inpName").focus();
            }
        }
    }
}