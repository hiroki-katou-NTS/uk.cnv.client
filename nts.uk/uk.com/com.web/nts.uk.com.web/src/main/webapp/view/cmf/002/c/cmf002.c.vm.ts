module nts.uk.com.view.cmf002.c.viewmodel {
    import block = nts.uk.ui.block;
    import model = cmf002.share.model;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import errors = nts.uk.ui.errors;

    export class ScreenModel {
        isNewMode: KnockoutObservable<boolean> = ko.observable(true);
        currentStandardOutputItem: KnockoutObservable<model.StandardOutputItem>;
        selectedStandardOutputItemCode: KnockoutObservable<string> = ko.observable("");
        listStandardOutputItem: KnockoutObservableArray<model.StandardOutputItem> = ko.observableArray([]);
        listStandardOutputItemTemp: KnockoutObservableArray<any> = ko.observableArray([]);
        itemTypes: KnockoutObservableArray<model.ItemModel> = ko.observableArray([]);
        itemType: KnockoutObservable<number> = ko.observable(0);
        conditionCode: KnockoutObservable<string>;
        dispConditionName: KnockoutObservable<string>;
        conditionName: KnockoutObservable<string>;
        categoryId: KnockoutObservable<number>;
        categoryName: KnockoutObservable<string>;
        formulaResult: KnockoutObservable<string> = ko.observable("");

        atWorkDataOutputItem: KnockoutObservable<model.AtWorkDataOutputItem>;
        characterDataFormatSetting: KnockoutObservable<model.CharacterDataFormatSetting>;
        dateDataFormatSetting: KnockoutObservable<model.DateDataFormatSetting>;
        inTimeDataFormatSetting: KnockoutObservable<model.InTimeDataFormatSetting>;
        numberDataFormatSetting: KnockoutObservable<model.NumberDataFormatSetting>;
        timeDataFormatSetting: KnockoutObservable<model.TimeDataFormatSetting>;

        selectedExOutputCateItemDatas: KnockoutObservableArray<any> = ko.observableArray([]);
        listExOutCateItemData: KnockoutObservableArray<model.ExternalOutputCategoryItemData> = ko.observableArray([]);

        selectedCategoryItems: KnockoutObservableArray<any> = ko.observableArray([]);
        categoryItems: KnockoutObservableArray<model.CategoryItem> = ko.observableArray([]);

        isUpdateExecution: KnockoutObservable<boolean> = ko.observable(false);
        constructor() {
            let self = this;
            let params = getShared("CMF002_C_PARAMS_FROM_B");
            let _rsList: Array<model.ItemModel> = model.getItemTypes();
            self.itemTypes(_rsList);

            self.dispConditionName = ko.observable(params.conditionSetCode + "　" + params.conditionSetName);
            self.conditionName = ko.observable(params.conditionSetName);
            self.categoryName = ko.observable(params.categoryName);
            self.categoryId = ko.observable(params.categoryId);
            self.conditionCode = ko.observable(params.conditionSetCode);
            self.currentStandardOutputItem = ko.observable(new model.StandardOutputItem(null, null, self.conditionCode(), 0, null));

            self.selectedStandardOutputItemCode.subscribe(code => {
                self.clearSetting();
                if (code) {
                    block.invisible();
                    let currentOutputItem = _.find(self.listStandardOutputItem(), item => {
                        return item.outItemCd() == code;
                    });
                    if (currentOutputItem) {
                        self.currentStandardOutputItem(currentOutputItem);
                        self.itemType(currentOutputItem.itemType());
                        let categoryItems = _.map(currentOutputItem.categoryItems(), x => {
                            return new model.CategoryItem(x.categoryId(), x.categoryItemNo(), x.categoryItemName(), x.operationSymbol(), x.displayOrder);
                        });
                        categoryItems = _.sortBy(categoryItems, function (item) { return parseInt(item.displayOrder); });
                        self.categoryItems(categoryItems);
                        self.selectedCategoryItems([]);
                        self.isNewMode(false);

                        service.getDataFormatSetting(self.itemType(), self.conditionCode(), code).done((data) => {
                            if (data) {
                                switch (self.itemType()) {
                                    case model.ITEM_TYPE.NUMERIC:
                                        self.numberDataFormatSetting = ko.observable(new model.NumberDataFormatSetting(data))
                                        break;
                                    case model.ITEM_TYPE.CHARACTER:
                                        self.characterDataFormatSetting = ko.observable(new model.CharacterDataFormatSetting(data))
                                        break;
                                    case model.ITEM_TYPE.DATE:
                                        self.dateDataFormatSetting = ko.observable(new model.DateDataFormatSetting(data))
                                        break;
                                    case model.ITEM_TYPE.TIME:
                                        self.timeDataFormatSetting = ko.observable(new model.TimeDataFormatSetting(data))
                                        break;
                                    case model.ITEM_TYPE.INS_TIME:
                                        self.inTimeDataFormatSetting = ko.observable(new model.InTimeDataFormatSetting(data))
                                        break;
                                    case model.ITEM_TYPE.AT_WORK_CLS:
                                        self.atWorkDataOutputItem = ko.observable(new model.AtWorkDataOutputItem(data))
                                        break;
                                }
                            }
                        }).fail((error) => {
                            alertError(error);
                        }).always(() => {
                            block.clear();
                        });
                    }
                    else {
                        self.settingNewMode();
                    }
                    self.setFocus();
                    _.defer(() => { errors.clearAll() });
                } else {
                    self.settingNewMode();
                }
            });

            self.itemType.subscribe(code => {
                self.categoryItems([]);
                self.selectedExOutputCateItemDatas([]);
                self.clearSetting();

                service.getAllCategoryItem(self.categoryId(), self.itemType()).done((listExOutCateItemData: Array<any>) => {
                    if (listExOutCateItemData && listExOutCateItemData.length) {
                        listExOutCateItemData = _.sortBy(listExOutCateItemData, ['itemNo']);
                        let rsCategoryItems: Array<model.ExternalOutputCategoryItemData> = _.map(listExOutCateItemData, x => {
                            // [ver62] ドメインモデル「外部出力カテゴリ項目データ.予約語区分」の値から予約語に変換するかどうか判断する
                            const itemName: string = x.keywordAtr === 1
                                ? self.reverseWord(x.itemName)
                                : x.itemName;
                            return new model.ExternalOutputCategoryItemData(x.itemNo, itemName, x.keywordAtr);
                        });
                        self.listExOutCateItemData(rsCategoryItems);
                        $('#C8_3').ntsError('clear');
                    }
                    else {
                        self.listExOutCateItemData([]);
                    }
                });
            });

            self.categoryItems.subscribe(function(values: Array<model.CategoryItem>) {
                let newFormulaResult = "";
                if (values && values.length) {
                    _.forEach(values, item => {
                        newFormulaResult = newFormulaResult + item.dispOperationSymbol + item.categoryItemName();
                    });
                }
                self.formulaResult(newFormulaResult);
            });
        }

        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.invisible();
            $.when(
                service.getAllCategoryItem(self.categoryId(), self.itemType()),
                self.getAllOutputItem(null)
            ).done((
                listExOutCateItemData: Array<any>) => {
                if (listExOutCateItemData && listExOutCateItemData.length) {
                    listExOutCateItemData = _.sortBy(listExOutCateItemData, ['itemNo']);
                    let rsCategoryItems: Array<model.ExternalOutputCategoryItemData> = _.map(listExOutCateItemData, x => {
                        // [ver62] ドメインモデル「外部出力カテゴリ項目データ.予約語区分」の値から予約語に変換するかどうか判断する
                        const itemName: string = x.keywordAtr === 1
                            ? self.reverseWord(x.itemName)
                            : x.itemName;
                        return new model.ExternalOutputCategoryItemData(x.itemNo, itemName, x.keywordAtr);
                    });
                    self.listExOutCateItemData(rsCategoryItems);
                }
                dfd.resolve(self);
            }).fail((error) => {
                alertError(error);
                dfd.reject();
            }).always(() => {
                block.clear();
            });

            return dfd.promise();
        }

        clearSetting() {
            let self = this;
            self.atWorkDataOutputItem = undefined;
            self.characterDataFormatSetting = undefined;
            self.dateDataFormatSetting = undefined;
            self.inTimeDataFormatSetting = undefined;
            self.numberDataFormatSetting = undefined;
            self.timeDataFormatSetting = undefined;
        }

        setFocus() {
            let self = this;
            if (self.isNewMode()) {
                $('#C4_1').focus();
            } else {
                $('#C7_2_container').focus();
            }
        }

        // 新規登録を実行する
        createNew(){
            let self = this;
            self.selectedStandardOutputItemCode("");
        }
        settingNewMode() {
            let self = this;
            self.isNewMode(true);
            self.currentStandardOutputItem(new model.StandardOutputItem(null, null, self.conditionCode(), 0, null));
            self.itemType(0);
            self.categoryItems([]);
            self.setFocus();
            setTimeout(function() { errors.clearAll(); }, 50);
        }

        isActiveSymbolAnd() {
            let self = this;
            if (self.itemType() === model.ITEM_TYPE.CHARACTER) {
                return true;
            }

            if (self.itemType() === model.ITEM_TYPE.AT_WORK_CLS
                && self.categoryItems().length === 0) {
                return true;
            }
            return false;
        }

        isActiveSymbolPlus() {
            let self = this;
            if (self.itemType() === model.ITEM_TYPE.NUMERIC) {
                return true;
            }
            if (self.itemType() === model.ITEM_TYPE.DATE
                && self.categoryItems().length === 0) {
                return true;
            }
            if (self.itemType() === model.ITEM_TYPE.TIME) {
                return true;
            }
            if (self.itemType() === model.ITEM_TYPE.INS_TIME
                && self.categoryItems().length === 0) {
                return true;
            }
            return false;
        }

        isActiveSymbolMinus() {
            let self = this;
            if (self.itemType() === model.ITEM_TYPE.NUMERIC
                && self.categoryItems().length > 0) {
                return true;
            }
            if (self.itemType() === model.ITEM_TYPE.TIME
                && self.categoryItems().length > 0) {
                return true;
            }
            return false;
        }

        clickSymbolAnd() {
            let self = this;
            self.addCategoryItem(model.SYMBOL.AND);
        }

        clickSymbolPlus() {
            let self = this;
            self.addCategoryItem(model.SYMBOL.PLUS);
        }

        clickSymbolMinus() {
            let self = this;
            self.addCategoryItem(model.SYMBOL.MINUS);
        }

        addCategoryItem(operatorSymbol: any): void {
            let self = this;

            if ((self.itemType() === model.ITEM_TYPE.DATE || self.itemType() === model.ITEM_TYPE.INS_TIME
                || self.itemType() === model.ITEM_TYPE.AT_WORK_CLS) && self.selectedExOutputCateItemDatas().length > 1) {
                let itemType = _.find(self.itemTypes(), item => {
                    return item.code == self.itemType();
                });

                alertError({ messageId: "Msg_1336", messageParams: [itemType.name] });
                return;
            }

            let categoryItems: Array<model.CategoryItem> = self.categoryItems();
            let maxDisplayOrder = _.maxBy(categoryItems, item => {
                return parseInt(item.displayOrder) ;
            });
            let nextDisplayOrder = maxDisplayOrder ? parseInt(maxDisplayOrder.displayOrder) + 1 : 1;
            for (let i = 0; i < self.selectedExOutputCateItemDatas().length; i++) {
                let exOutCateItemData = _.find(self.listExOutCateItemData(), item => {
                    return item.itemNo() == self.selectedExOutputCateItemDatas()[i];
                });
                if (categoryItems.length > 0) {
                    categoryItems.push(new model.CategoryItem(self.categoryId(), self.selectedExOutputCateItemDatas()[i],
                        exOutCateItemData.itemName(), operatorSymbol, nextDisplayOrder + i));
                }
                else {
                    categoryItems.push(new model.CategoryItem(self.categoryId(), self.selectedExOutputCateItemDatas()[i],
                        exOutCateItemData.itemName(), null, nextDisplayOrder + i));
                }
            }
            categoryItems = _.sortBy(categoryItems, function (item) { return parseInt(item.displayOrder); });
            if (categoryItems.length > 0) {
                $('#C10_1').ntsError('clear');
            }
            self.categoryItems(categoryItems);
        }

        clickRemoveCtgItem() {
            let self = this;
            let categoryItems: Array<model.CategoryItem> = self.categoryItems();
            _.each(self.selectedCategoryItems(), key => {
                _.remove(categoryItems, item => {
                    return item.displayOrder == key;
                });
            });
            categoryItems = _.sortBy(categoryItems, function (item) { return parseInt(item.displayOrder); });
            if (categoryItems.length > 0) {
                categoryItems[0].operationSymbol(null);
            }
            self.categoryItems(categoryItems);
            self.selectedCategoryItems([]);
        }

        getAllOutputItem(code?: string): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.invisible();
            self.listStandardOutputItem.removeAll();
            service.getOutItems(self.conditionCode()).done((outputItems: Array<any>) => {
                //  time = performance.now();
                if (outputItems && outputItems.length) {
                    let rsOutputItems: Array<model.StandardOutputItem> = _.map(outputItems, x => {
                        let listCategoryItem: Array<model.CategoryItem> = _.map(x.categoryItems, (y : model.ICategoryItem) => {
                            return new model.CategoryItem(self.categoryId(), y.categoryItemNo,
                                y.categoryItemName, y.operationSymbol, y.displayOrder);
                        });
                        return new model.StandardOutputItem(x.outItemCd, x.outItemName, x.condSetCd,
                            x.itemType, listCategoryItem);
                    });

                     let rsOutputItemTemp: Array<any> = _.map(outputItems, x => {
                        return {dispOutputItemCode: x.outItemCd, dispOutputItemName:x.outItemName};
                    });

                    self.listStandardOutputItemTemp(rsOutputItemTemp);
                    self.listStandardOutputItem(rsOutputItems);

                    if (code) {
                        if (code == self.selectedStandardOutputItemCode())
                            self.selectedStandardOutputItemCode.valueHasMutated();
                        else
                            self.selectedStandardOutputItemCode(code);
                    }
                    else {
                        self.selectedStandardOutputItemCode(rsOutputItems[0].outItemCd());
                    }
                }
                else {
                    errors.clearAll();
                    self.listStandardOutputItem([]);
                    self.listStandardOutputItemTemp([]);
                    self.settingNewMode();
                }
                dfd.resolve();
            }).fail(function(res) {
                alertError({ messageId: res.messageId });
                dfd.reject();
            }).always(function() {
                block.clear();
            });

            return dfd.promise();
        }

        // 出力項目を登録する
        registerOutputItem() {
            let self = this;
            errors.clearAll();
            let currentStandardOutputItem: model.StandardOutputItem = self.currentStandardOutputItem();

            $('.nts-input').trigger("validate");
            currentStandardOutputItem.itemType(self.itemType());
            currentStandardOutputItem.categoryItems(self.categoryItems());
            switch (self.itemType()) {
                case model.ITEM_TYPE.NUMERIC:
                    if (self.numberDataFormatSetting != undefined) {
                        currentStandardOutputItem.numberDataFormatSetting(self.numberDataFormatSetting());
                    }
                    break;
                case model.ITEM_TYPE.CHARACTER:
                    if (self.characterDataFormatSetting != undefined) {
                        currentStandardOutputItem.characterDataFormatSetting(self.characterDataFormatSetting());
                    }
                    break;
                case model.ITEM_TYPE.DATE:
                    if (self.dateDataFormatSetting != undefined) {
                        currentStandardOutputItem.dateDataFormatSetting(self.dateDataFormatSetting());
                    }
                    break;
                case model.ITEM_TYPE.TIME:
                    if (self.timeDataFormatSetting != undefined) {
                        currentStandardOutputItem.timeDataFormatSetting(self.timeDataFormatSetting());
                    }
                    break;
                case model.ITEM_TYPE.INS_TIME:
                    if (self.inTimeDataFormatSetting != undefined) {
                        currentStandardOutputItem.inTimeDataFormatSetting(self.inTimeDataFormatSetting());
                    }
                    break;
                case model.ITEM_TYPE.AT_WORK_CLS:
                    if (self.atWorkDataOutputItem != undefined) {
                        currentStandardOutputItem.atWorkDataOutputItem(self.atWorkDataOutputItem());
                    }
                    break;
            }
            if (errors.hasError() === false && self.isValid()) {
                block.invisible();
                for(let i = 0;i<currentStandardOutputItem.categoryItems().length;i++){
                    currentStandardOutputItem.categoryItems()[i].displayOrder = (i+1);
                }
                currentStandardOutputItem.isNewMode = self.isNewMode();
                currentStandardOutputItem.dispOrder = self.listStandardOutputItem().length > 0 ? self.listStandardOutputItem().length + 1 : 1;
                // register
                service.registerOutputItem(ko.toJS(currentStandardOutputItem)).done(() => {
                    info({ messageId: "Msg_15" });
                    self.getAllOutputItem(currentStandardOutputItem.outItemCd()).done(() => {
                        self.setFocus();
                        self.isUpdateExecution(true);
                    });
                }).fail(function(error) {
                    alertError({ messageId: error.messageId });
                }).always(function() {
                    block.clear();
                });
            }
        }

        deleteOutputItem() {
            let self = this;
            let currentStandardOutputItem: model.StandardOutputItem = self.currentStandardOutputItem();
            let listOutputItem = self.listStandardOutputItem;
            block.invisible();
            confirm({ messageId: "Msg_18" }).ifYes(() => {
                if (currentStandardOutputItem.outItemCd()) {
                    let index: number = _.findIndex(listOutputItem(), function(x) {
                        return x.outItemCd() == currentStandardOutputItem.outItemCd()
                    });

                    service.removeOutputItem(ko.toJS(currentStandardOutputItem)).done(function() {
                        info({ messageId: "Msg_16" });
                        self.getAllOutputItem(currentStandardOutputItem.outItemCd()).done(() => {
                            if (self.listStandardOutputItem().length == 0) {
                                self.selectedStandardOutputItemCode("");
                            } else {
                                if (index == self.listStandardOutputItem().length) {
                                    self.selectedStandardOutputItemCode(self.listStandardOutputItem()[index - 1].outItemCd());
                                } else {
                                    self.selectedStandardOutputItemCode(self.listStandardOutputItem()[index].outItemCd());
                                }
                            }
                            self.isUpdateExecution(true);
                        });
                    }).fail(function(error) {
                        alertError({ messageId: error.messageId });
                    }).always(function() {
                        block.clear();
                    });
                }

            }).then(() => {
                $('.nts-input').ntsError('clear');
                errors.clearAll();
                block.clear();
            });
        }

        // 外部出力項目登録確認
        isValid() {
            let self = this;
            if (self.categoryItems().length === 0) {
                $('#C10_1').ntsError('set', { messageId: "Msg_656" });
                return false;
            }
            if (!self.isNewMode()) {
                return true;
            }

            let stdOutItem = _.find(self.listStandardOutputItem(), x => {
                return x.outItemCd() === self.currentStandardOutputItem().outItemCd();
            });

            if (stdOutItem) {
                alertError({ messageId: "Msg_3" });
                return false;
            }
            return true;
        }


        openCMF002g() {
            modal("/view/cmf/002/g/index.xhtml").onClosed(function() {

            });
        }

        openCMF002h() {
            modal("/view/cmf/002/h/index.xhtml").onClosed(function() {

            });
        }

        openCMF002f() {
            let self = this;
            setShared('CMF002_F_PARAMS', {
                conditionSetCode: self.conditionCode(),
                conditionSetName: self.conditionName(),
                categoryId: self.categoryId(),
                categoryName: self.categoryName()
            });
            modal("/view/cmf/002/f/index.xhtml").onClosed(function() {
                let output = getShared('CMF002_C_PARAMS_FROM_F');
                if (output) {
                    if (!self.isUpdateExecution()) {
                        self.isUpdateExecution(output.isUpdateExecution);
                    }
                    if (output.isUpdateExecution) {
                        self.getAllOutputItem(self.selectedStandardOutputItemCode()).done(() => { });
                    }
                }
            });
        }

        openItemTypeSetting() {
            let self = this;
            let url = "";
            let paramName = "";
            let formatSetting = null;
            switch (self.itemType()) {
                case model.ITEM_TYPE.NUMERIC:
                    url = "/view/cmf/002/i/index.xhtml";
                    paramName = "CMF002_I_PARAMS";
                    if (self.numberDataFormatSetting != undefined) {
                        formatSetting = ko.toJS(self.numberDataFormatSetting);
                    }
                    break;
                case model.ITEM_TYPE.CHARACTER:
                    url = "/view/cmf/002/j/index.xhtml";
                    paramName = "CMF002_J_PARAMS";
                    if (self.characterDataFormatSetting != undefined) {
                        formatSetting = ko.toJS(self.characterDataFormatSetting);
                    }
                    break;
                case model.ITEM_TYPE.DATE:
                    url = "/view/cmf/002/k/index.xhtml";
                    paramName = "CMF002_K_PARAMS";
                    if (self.dateDataFormatSetting != undefined) {
                        formatSetting = ko.toJS(self.dateDataFormatSetting);
                    }
                    break;
                case model.ITEM_TYPE.TIME:
                    url = "/view/cmf/002/l/index.xhtml";
                    paramName = "CMF002_L_PARAMS";
                    if (self.timeDataFormatSetting != undefined) {
                        formatSetting = ko.toJS(self.timeDataFormatSetting);
                    }
                    break;
                case model.ITEM_TYPE.INS_TIME:
                    url = "/view/cmf/002/m/index.xhtml";
                    paramName = "CMF002_M_PARAMS";
                    if (self.inTimeDataFormatSetting != undefined) {
                        formatSetting = ko.toJS(self.inTimeDataFormatSetting);
                    }
                    break;
                case model.ITEM_TYPE.AT_WORK_CLS:
                    url = "/view/cmf/002/n/index.xhtml";
                    paramName = "CMF002_N_PARAMS";
                    if (self.atWorkDataOutputItem != undefined) {
                        formatSetting = ko.toJS(self.atWorkDataOutputItem);
                    }
                    break;
            }
            setShared(paramName, { screenMode: model.DATA_FORMAT_SETTING_SCREEN_MODE.INDIVIDUAL, formatSetting: formatSetting });
            modal(url).onClosed(function() {
                let output = getShared('CMF002_C_PARAMS');
                if (output) {
                    let fs = output.formatSetting;
                    switch (self.itemType()) {
                        case model.ITEM_TYPE.NUMERIC:
                            if (self.numberDataFormatSetting != undefined) {
                                self.numberDataFormatSetting(fs);
                            }
                            else {
                                self.numberDataFormatSetting = ko.observable(new model.NumberDataFormatSetting(fs))
                            }
                            break;
                        case model.ITEM_TYPE.CHARACTER:
                            if (self.characterDataFormatSetting != undefined) {
                                self.characterDataFormatSetting(fs);
                            }
                            else {
                                self.characterDataFormatSetting = ko.observable(new model.CharacterDataFormatSetting(fs))
                            }
                            break;
                        case model.ITEM_TYPE.DATE:
                            if (self.dateDataFormatSetting != undefined) {
                                self.dateDataFormatSetting(fs);
                            }
                            else {
                                self.dateDataFormatSetting = ko.observable(new model.DateDataFormatSetting(fs))
                            }
                            break;
                        case model.ITEM_TYPE.TIME:
                            if (self.timeDataFormatSetting != undefined) {
                                self.timeDataFormatSetting(fs);
                            }
                            else {
                                self.timeDataFormatSetting = ko.observable(new model.TimeDataFormatSetting(fs))
                            }
                            break;
                        case model.ITEM_TYPE.INS_TIME:
                            if (self.inTimeDataFormatSetting != undefined) {
                                self.inTimeDataFormatSetting(fs);
                            }
                            else {
                                self.inTimeDataFormatSetting = ko.observable(new model.InTimeDataFormatSetting(fs))
                            }
                            break;
                        case model.ITEM_TYPE.AT_WORK_CLS:
                            if (self.atWorkDataOutputItem != undefined) {
                                self.atWorkDataOutputItem(fs);
                            }
                            else {
                                self.atWorkDataOutputItem = ko.observable(new model.AtWorkDataOutputItem(fs))
                            }
                            break;
                    }
                }
            });
        }

        // Close dialog
        closeSetting() {
            let self = this;
            setShared('CMF002_B_PARAMS_FROM_C', { isUpdateExecution: self.isUpdateExecution() });
            nts.uk.ui.windows.close();
        }

        // Reverse word
        private reverseWord(word: string): string {
            const mapReveseWord = {
                employment: '雇用呼称',
                department: '部門呼称',
                class: '分類呼称',
                jobTitle: '職位呼称',
                person: '社員呼称',
                office: '事業所呼称',
                work: '作業呼称',
                workPlace: '職場呼称',
                project: 'プロジェクト',
                adHocWork: '臨時勤務',
                substituteHoliday: '振休',
                substituteWork: '振出',
                compensationHoliday: '代休',
                exsessHoliday: '60H超過休暇',
                bindingTime: '拘束時間',
                payAbsenseDays: '給与欠勤日数',
                payAttendanceDays: '給与出勤日数',
                import: '取込',
                toppage: 'トップページ',
                code: 'コード',
                name: '名称',
            };
            const keyword: string = word.substring(
                word.lastIndexOf("{#") + 2,
                word.lastIndexOf("#}")
            );
            const reveseWord: string = mapReveseWord[keyword];
            if (!reveseWord) {
                return word;
            }
            return word.replace(`{#${keyword}#}`, reveseWord);
        }
    }
}