module nts.uk.at.view.kaf022.p.viewmodel {
    import isNullOrEmpty = nts.uk.text.isNullOrEmpty;
    import dialog = nts.uk.ui.dialog;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;

    export class ScreenModel {
        settings: KnockoutObservableArray<IOptionalItemAppSet> = ko.observableArray([]);
        selectedSetting: KnockoutObservable<OptionalItemAppSet> = ko.observable(new OptionalItemAppSet());
        columns: KnockoutObservableArray<any>;
        swapColumns: KnockoutObservableArray<any>;
        optionalItems: KnockoutObservableArray<any>;
        optionalItemsBak: Array<any> = [];
        selectedCode: KnockoutObservable<string> = ko.observable(null);
        // bien theo doi update mode hay new mode
        isUpdate: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this;
            self.columns = ko.observableArray([
                {headerText: getText("KAF022_681"), key: 'code', width: 50, formatter: _.escape},
                {headerText: getText("KAF022_629"), key: 'name', width: 180, formatter: _.escape},
                {headerText: getText("KAF022_99"), key: 'useAtr', width: 70, formatter: makeIcon}
            ]);

            self.swapColumns = ko.observableArray([
                {headerText: getText("KAF022_686"), key: 'no', width: 50, columnCssClass: "grid-col-text-right", formatter: _.escape},
                {headerText: getText("KAF022_704"), key: 'name', width: 120, columnCssClass: 'limited-label', formatter: _.escape}
            ]);

            self.optionalItems = ko.observableArray([]);

            // subscribe a item in the list
            self.selectedCode.subscribe((value) => {
                if (!isNullOrEmpty(value)) {
                    const setting = _.find(self.settings(), s => s.code == value);
                    self.selectedSetting(new OptionalItemAppSet(_.cloneDeep(setting)));
                    self.isUpdate(true);
                    nts.uk.ui.errors.clearAll();
                    $("#optItemAppTypeName").focus();
                } else {
                    self.selectedSetting(new OptionalItemAppSet());
                    self.isUpdate(false);
                    nts.uk.ui.errors.clearAll();
                    $("#optItemAppTypeCode").focus();
                }
                self.optionalItems(_.cloneDeep(self.optionalItemsBak));
            });
        }

        /** get data to list **/
        getData(currentCode?: string): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.invisible();
            service.getAll().done((lstData: Array<any>) => {
                if (lstData.length > 0) {
                    lstData.forEach(setting => {
                        setting.settingItems = setting.settingItems.map(item => {
                            const tmp = _.find(self.optionalItemsBak, i => i.no == item.no);
                            if (tmp) {
                                item['name'] = tmp.name;
                                return item;
                            } else {
                                return item;
                            }
                        });
                    });
                    self.settings(_.sortBy(lstData, ['code']));
                    if (!isNullOrEmpty(currentCode)) {
                        if (currentCode == self.selectedCode())
                            self.selectedCode.valueHasMutated();
                        else
                            self.selectedCode(currentCode);
                    } else {
                        if (self.settings()[0].code == self.selectedCode())
                            self.selectedCode.valueHasMutated();
                        else
                            self.selectedCode(self.settings()[0].code);
                    }
                    self.isUpdate(true);
                } else {
                    self.settings([]);
                    if (isNullOrEmpty(self.selectedCode()))
                        self.selectedCode.valueHasMutated();
                    else
                        self.selectedCode(null);
                }
                dfd.resolve();
            }).fail(function (error) {
                dfd.reject();
                dialog.alertError(error);
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }

        /** get data when start dialog **/
        startPage(): JQueryPromise<any> {
            const self = this;
            const dfd = $.Deferred();
            block.invisible();
            service.getOptionalItems().done((data: Array<any>) => {
                const items = data || [];
                self.optionalItemsBak = items.filter(i => i.usageAtr == 1 && i.calcAtr == 0 && i.performanceAtr == 1).map(i => ({no: i.itemNo, name: i.itemName}));
                self.optionalItems(_.cloneDeep(self.optionalItemsBak));
                self.getData().done(() => {
                    dfd.resolve();
                }).fail(() => {
                    dfd.reject();
                })
            }).fail(error => {
                dfd.reject();
                dialog.alertError(error);
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }

        // new button
        createNew() {
            let self = this;
            self.selectedCode(null);
        }

        // close dialog
        closeDialog() {
            nts.uk.ui.windows.close();
        }

        /** update or insert data when click button register **/
        register() {
            const self = this;
            $('input').trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                const currentCode = self.selectedSetting().code();
                const data = ko.toJS(self.selectedSetting);
                if (!self.isUpdate() && _.find(self.settings(), i => i.code == data.code)) {
                    dialog.alertError({ messageId: "Msg_3" });
                    return;
                }
                if (data.settingItems.length == 0) {
                    dialog.alertError({ messageId: "Msg_1752" });
                    return;
                }
                data.settingItems.forEach((obj, index) => {
                    obj['dispOrder'] = index + 1;
                });
                service.saveSetting(data).done(() => {
                    dialog.info({ messageId: "Msg_15" }).then(() => {
                        self.getData(currentCode);
                    });
                }).fail(error => {
                    dialog.alertError(error);
                }).always(() => {
                    block.clear();
                });
            }
        }

        /** remove item from list **/
        remove() {
            const self = this;
            dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                const current = self.selectedCode();
                const currentIndex = _.findIndex(self.settings(), i => i.code == current);
                let nextIndex = currentIndex + 1;
                if (currentIndex == self.settings().length - 1)
                    nextIndex = currentIndex - 1;
                service.deleteSetting(current).done(() => {
                    dialog.info({ messageId: "Msg_16" }).then(() => {
                        const nextCode = nextIndex >= self.settings().length || nextIndex < 0 ? null : self.settings()[nextIndex].code;
                        self.getData(nextCode);
                    });
                }).fail(error => {
                    dialog.alertError(error);
                }).always(() => {
                    block.clear();
                });
            }).ifNo(function () {
            });
        }

        afterMoveLeft() {
            const self = this;
            self.optionalItems(_.cloneDeep(self.optionalItemsBak));
        }

    }

    interface IOptionalItemAppSet {
        code: string;
        name: string
        useAtr: number;
        note: string;
        settingItems: Array<any>;
    }

    class OptionalItemAppSet {
        // コード
        code: KnockoutObservable<string>;
        // 申請種類名
        name: KnockoutObservable<string>;
        // 利用区分
        useAtr: KnockoutObservable<number>;
        // 任意項目申請の説明文
        note: KnockoutObservable<string>;
        // 任意項目
        settingItems: KnockoutObservableArray<any>;

        constructor(param?: IOptionalItemAppSet) {
            this.code = ko.observable(param ? param.code : "");
            this.name = ko.observable(param ? param.name : "");
            this.useAtr = ko.observable(param ? param.useAtr : 1);
            this.note = ko.observable(param ? param.note : "");
            this.settingItems = ko.observableArray(param ? param.settingItems : []);
        }

        beforeMoveRight(arg1, selected: Array<any>, toBeMoved: Array<any>) {
            if (selected.length + toBeMoved.length > 10) {
                dialog.alertError({messageId: "Msg_1741", messageParams: [10]});
                return false;
            }
            return true;
        }
    }

    function makeIcon(value) {
        if (value == "1") {
            return '<i class="icon icon-dot" style="margin: auto; display: block;"/>';
        }
        return '';
    }
}