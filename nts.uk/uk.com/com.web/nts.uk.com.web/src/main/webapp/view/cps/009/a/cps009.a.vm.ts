    // blockui all ajax request on layout
//    $(document)
//        .ajaxStart(() => {
//            $.blockUI({
//                message: null,
//                overlayCSS: { opacity: 0.1 }
//            });
//        }).ajaxStop(() => {
//            $.unblockUI();
//        });

module nts.uk.com.view.cps009.a.viewmodel {
    import error = nts.uk.ui.errors;
    import text = nts.uk.resource.getText;
    import close = nts.uk.ui.windows.close;
    import dialog = nts.uk.ui.dialog;
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import modal = nts.uk.ui.windows.sub.modal;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import formatDate = nts.uk.time.formatDate;
    import validation = validationcps009;

    import primitiveConst = CPS009Constraint.primitiveConst;

    export class ViewModel {
        initValSettingLst: KnockoutObservableArray<any> = ko.observableArray([]);
        initValueList: KnockoutObservableArray<any> = ko.observableArray([]);
        initSettingId: KnockoutObservable<string> = ko.observable('');
        settingColums: KnockoutObservableArray<any>;
        itemValueLst: KnockoutObservableArray<any>;
        selectionColumns: any;
        currentCategory: KnockoutObservable<InitValueSettingDetail>;
        comboItems: any;
        comboColumns: any;
        isUpdate: boolean = false;
        //History reference date
        baseDate: KnockoutObservable<Date> = ko.observable(new Date());
        lstItemFilter: Array<any> = [];
        ctgIdUpdate: KnockoutObservable<boolean> = ko.observable(false);
        currentItemId: KnockoutObservable<string> = ko.observable('');
        errorList: KnockoutObservableArray<any> = ko.observableArray([]);
        dataSourceFilter: Array<any> = [];

        constructor() {

            let self = this;
            
            self.initValue();
            self.start(undefined);
            
            self.initSettingId.subscribe(function(value: string) {
                error.clearAll();
                self.currentCategory().setData({
                    settingCode: null,
                    settingName: " ",
                    ctgList: []
                });
                self.currentCategory().itemList.removeAll();
                if (nts.uk.text.isNullOrEmpty(value)) return;
                self.getDetail(value); 
        
            });

            self.currentItemId.subscribe(function(value: string) {
                nts.uk.ui.errors.clearAll();
                if (nts.uk.text.isNullOrEmpty(value))  return; 
                $('#date1').trigger('validate');
                self.getItemList(self.initSettingId(), value);

            })
            $( window ).resize(function() {
                let subrightTbodyHeightResize = window.innerHeight - 320;
                if(subrightTbodyHeightResize <= 32) {   
                    $('#sub-right>table>tbody').css('max-height', '32px');
                } else {
                    $('#sub-right>table>tbody').css('max-height', subrightTbodyHeightResize + 'px');
                }
            }).trigger('resize');
        }
        
        getDetail(value: string): any{
            let self = this;
            
            block.grayout();
            service.getAllCtg(value).done((data: any) => {
                self.currentCategory().setData({
                    settingCode: data.settingCode,
                    settingName: data.settingName,
                    ctgList: data.ctgList
                });
                if (!self.ctgIdUpdate()) {
                    //perInfoCtgId
                    if (data.ctgList.length > 0) {
                        self.currentItemId(data.ctgList[0].perInfoCtgId);
                    } else {
                        self.currentItemId(undefined);
                    }
                }
                else {
                    self.ctgIdUpdate(false);
                }
                self.getItemList(value, self.currentItemId());
            }).always(() => {
                block.clear();
            });    
        }

        getTitleName(itemName: string) {
            return ko.computed(() => {
                return itemName.length > 5 ? itemName : "";
            });
        }

        // get item list
        getItemList(settingId: string, ctgId: string) {
            let self = this,
                i: number = 0,
                currentCtg: any;
            currentCtg = self.findCtg(self.currentCategory().ctgList(), ctgId);
            
            if (currentCtg === undefined) { return; }
            
            self.currentCategory().itemList.removeAll();
            
            block.invisible();
            
            service.getAllItemByCtgId(settingId, ctgId, error.hasError()== true? null : moment(self.baseDate()).format('YYYY-MM-DD')).done((item: Array<any>) => {
                if (item.length > 0) {
                    let itemConvert = _.map(item, function(obj: any) {
                        primitiveConst(obj);
                        i = i + 1;
                        return new PerInfoInitValueSettingItemDto({
                            categoryType: currentCtg.categoryType,
                            indexItem: i,
                            fixedItem: obj.fixedItem,
                            perInfoItemDefId: obj.perInfoItemDefId,
                            settingId: obj.settingId,
                            perInfoCtgId: obj.perInfoCtgId,
                            itemName: obj.itemName,
                            isRequired: obj.isRequired,
                            refMethodType: obj.refMethodType,
                            saveDataType: obj.saveDataType,
                            stringValue: obj.stringValue,
                            intValue: obj.intValue,
                            dateValue: obj.dateValue,
                            itemType: obj.itemType,
                            dataType: obj.dataType,
                            itemCode: obj.itemCode,
                            ctgCode: obj.ctgCode,
                            numberIntegerPart: obj.numberIntegerPart,
                            numberDecimalPart: obj.numberDecimalPart,
                            timeItemMin: obj.timeItemMin,
                            timeItemMax: obj.timeItemMax,
                            selectionItemId: obj.selectionItemId,
                            selection: obj.selection,
                            selectionItemRefType: obj.selectionItemRefType,
                            dateType: obj.dateType,
                            timepointItemMin: obj.timepointItemMin,
                            timepointItemMax: obj.timepointItemMax,
                            dateWithDay: obj.intValue,
                            numericItemMin: obj.numericItemMin,
                            numericItemMax: obj.numericItemMax,
                            stringItemType: obj.stringItemType,
                            stringItemLength: obj.stringItemLength,
                            stringItemDataType: obj.stringItemDataType,
                            disableCombox: obj.disableCombox,
                            enableControl: obj.enableControl,
                            initValue: obj.initValue
                        });

                    });
                    _.defer(() => {
                        self.currentCategory().itemList.removeAll();
                        self.currentCategory().itemList(itemConvert);
                        self.lstItemFilter = itemConvert;
                        _.defer(() => {
                             let ctrl = $("#ctgName"),
                                str = ctrl.val();
                            if ($('input.ntsSearchBox.nts-editor.ntsSearchBox_Component:focus').length == 0) {
                                ctrl.focus().val('').val(str);
                                $("#ctgName").trigger("validate");
                            }
                        });
                    });
                } else {
                    _.defer(() => {
                        self.currentCategory().itemList.removeAll();
                        self.currentCategory().itemList([]);
                        _.defer(() => {
                             let ctrl = $("#ctgName"),
                                str = ctrl.val();

                            if ($('input.ntsSearchBox.nts-editor.ntsSearchBox_Component:focus').length == 0) {
                                ctrl.focus().val('').val(str);
                                $("#ctgName").trigger("validate");
                            }
                        });
                    });
                }
            }).always(() => { block.clear() });
        }

        start(id: string): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            block.grayout();
            service.getAll().done((data: Array<IPerInfoInitValueSettingDto>) => {
                self.ctgIdUpdate(false);
                if (data.length > 0) {
                    self.isUpdate = true;
                    self.initValSettingLst.removeAll();
                    self.initValSettingLst(data);
                    if (id === undefined) {
                        if (self.initValSettingLst().length > 0) {
                            self.initSettingId(self.initValSettingLst()[0].settingId);
                        } else {
                            self.initSettingId("");
                        }
                    } else {
                        self.initSettingId(id);
                    }
                } else {
                    self.isUpdate = false;
                    self.openDDialog();
                    self.refresh(undefined);
                }
            });
            return dfd.promise();
        }

        refresh(id: string) {
            let self = this;
            block.invisible();
            service.getAll().done((data: Array<any>) => {
                self.initValSettingLst.removeAll();
                self.initValSettingLst(data);
                if (self.initValSettingLst().length > 0) {
                    if (id === undefined) {
                        self.initSettingId(self.initValSettingLst()[0].settingId);
                    } else {
                        self.initSettingId(id);
                    }
                } else {
                    self.initSettingId("");

                }
                block.clear();

            });;
        }

        initValue() {
            let self = this;

            self.settingColums = ko.observableArray([
                { headerText: 'settingId', key: 'settingId', width: 100, hidden: true },
                { headerText: text('CPS009_10'), key: 'settingCode', width: 80 },
                { headerText: text('CPS009_11'), key: 'settingName', width: 160, formatter: _.escape }
            ]);

            self.itemValueLst = ko.observableArray([
                new ItemModel('1', '基本給'),
                new ItemModel('2', '役職手当'),
                new ItemModel('3', '基本給2')]);

            self.selectionColumns = [{ prop: 'id', length: 4 },
                { prop: 'itemName', length: 8 }];

            self.currentCategory = ko.observable(new InitValueSettingDetail({
                settingCode: '', settingName: '', ctgList: [], itemList: []
            }));

            self.comboItems = [new ItemModel('1', '基本給'),
                new ItemModel('2', '役職手当'),
                new ItemModel('3', '基本給2')];

            self.comboColumns = [{ prop: 'code', length: 4 },
                { prop: 'name', length: 8 }];

        }

        // thiet lap item hang loat
        openBDialog() {
            let self = this,
                ctgCurrent = self.findCtg(self.currentCategory().ctgList(), self.currentItemId()),
                params = {
                    settingId: self.initSettingId(),
                    ctgName: ctgCurrent != undefined ? ko.toJS(ctgCurrent.categoryName) : '',
                    categoryId: self.currentItemId(),
                    categoryType: ctgCurrent.categoryType
                };
            self.ctgIdUpdate(false);
            setShared('CPS009B_PARAMS', params);
            block.invisible();
            modal('/view/cps/009/b/index.xhtml', { title: '' }).onClosed(function(): any {
                $('#ctgName').focus();
                let itemSelected = getShared('CPS009B_DATA');
                if (itemSelected.isCancel) {
                    return;
                } else {
                    let itemLst: Array<any> = _.map(ko.toJS(self.currentCategory().itemList()), function(obj: PerInfoInitValueSettingItemDto) {
                        return obj.perInfoItemDefId;
                    });
                    if (itemSelected.lstItem.length > 0) {
                        _.each(itemSelected.lstItem, function(item) {
                            let i: number = _.indexOf(itemLst, item);
                            if (i > -1) {
                                self.currentCategory().itemList()[i].selectedRuleCode(Number(itemSelected.refMethodType));
                                self.currentCategory().itemList()[i].selectedRuleCode.valueHasMutated();
                            }
                        });
                    }
                }
                block.clear();
            });

        }

        // copy initVal
        openCDialog() {

            let self = this,
                params = {
                    settingId: ko.toJS(self.initSettingId()),
                    settingCode: ko.toJS(self.currentCategory().settingCode),
                    settingName: ko.toJS(self.currentCategory().settingName)
                };
            self.ctgIdUpdate(false);
            setShared('CPS009C_PARAMS', params);

            block.invisible();

            modal('/view/cps/009/c/index.xhtml', { title: '' }).onClosed(function(): any {
                $('#ctgName').focus();
                let initSetId: string = getShared('CPS009C_COPY');
                if (initSetId !== undefined) {
                    self.refresh(initSetId);
                }

                block.clear();
            });

        }

        // new initVal
        openDDialog() {

            let self = this;
            self.ctgIdUpdate(false);
            block.invisible();
            modal('/view/cps/009/d/index.xhtml', { title: '' }).onClosed(function(): any {
                let id: string = getShared('CPS009D_PARAMS');
                if (id !== undefined) {
                    self.refresh(id);
                }
                block.clear();
            });

        }

        //delete init value

        deleteInitValue() {

            let self = this,
                objDelete = {
                    settingId: self.initSettingId(),
                    settingCode: self.currentCategory().settingCode()
                };
            self.ctgIdUpdate(false);
           
            confirm({ messageId: "Msg_18" }).ifYes(() => { 
                service.deleteInitVal(objDelete).done(function(data) {
                    dialog.info({ messageId: "Msg_16" }).then(function() {
                        $('#ctgName').focus();
                        var sourceLength = self.initValSettingLst().length;
                        var i = _.findIndex(self.initValSettingLst(), function(init: IPerInfoInitValueSettingDto) { return init.settingId === self.initSettingId(); });
                        var evens = _.remove(self.initValSettingLst(), function(init: IPerInfoInitValueSettingDto) {
                            return init.settingId !== self.initSettingId();
                        });
                        var newLength = evens.length;

                        if (newLength > 0) {
                            if (i === (sourceLength - 1)) {
                                i = newLength - 1;
                            }

                            self.start(evens[i].settingId);
                        } else {
                            self.start(undefined);

                        }
                    });
                })
            }).ifNo(() => {
                $('#ctgName').focus();
                return;
            });
        }

        // cap nhat init value
        update() {
            let self = this,
                currentCtg = self.findCtg(self.currentCategory().ctgList(), self.currentItemId()),
                updateObj = {
                    settingId: self.initSettingId(),
                    settingName: self.currentCategory().settingName(),
                    perInfoCtgId: self.currentItemId(),
                    isSetting: currentCtg.setting,
                    itemLst: _.map(ko.toJS(self.currentCategory().itemList()), function(obj: PerInfoInitValueSettingItemDto) {
                        return {
                            ctgCode: obj.ctgCode,
                            perInfoItemDefId: obj.perInfoItemDefId,
                            itemName: obj.itemName,
                            isRequired: obj.isRequired == true? 1: 0,
                            refMethodType: obj.refMethodType,
                            itemType: obj.itemType,
                            dataType: obj.dataType,
                            saveDataType: obj.saveDataType,
                            stringValue: obj.stringValue,
                            intValue: obj.intValue,
                            dateVal: obj.dateValue,
                            dateWithDay: obj.dateWithDay,
                            timePoint: obj.timePoint,
                            value: obj.value,
                            selectedRuleCode: obj.selectedRuleCode,
                            selectionId: obj.selectedCode,
                            numberValue: obj.numbereditor == null? 0 : obj.numbereditor.value,
                            dateType: obj.dateType,
                            time: obj.dateWithDay

                        };
                    })
                },
                itemListSetting: Array<any> = _.filter(self.currentCategory().itemList(), function(item) {
                    return item.selectedRuleCode() == 2;
                });
            $('#date1').trigger('validate');
            $('.ntsDatepicker.nts-input.reset-element.sub-input-units:not(:disabled)').trigger('validate');
            $('.sub-input-units:not(:disabled)').trigger('validate');
            validation.initCheckError(itemListSetting);
            validation.checkError(itemListSetting);
            
            if(error.hasError()){ return;}

            block.invisible();
            service.update(updateObj).done(function(data) {
                dialog.info({ messageId: "Msg_15" }).then(function() {
                    $('#ctgName').focus();
                    self.initSettingId("");
                    self.refresh(updateObj.settingId);
                    self.initSettingId(updateObj.settingId);
                    self.currentItemId("");
                    self.currentItemId(updateObj.perInfoCtgId);
                    self.ctgIdUpdate(true);

                });
                self.currentItemId(updateObj.perInfoCtgId);
                block.clear();
            }).fail(function(res: any) {
                self.errorList(res);
                nts.uk.ui.dialog.bundledErrors(self.errorList());
                block.clear();
            });

        }

        //履歴参照基準日を適用する (Áp dụng ngày chuẩn để tham chiếu lịch sử)
        historyFilter() {
            if(error.hasError()) return;
            let self = this,
                baseDate = moment(self.baseDate()).format('YYYY-MM-DD'),
                vm = self.currentCategory().itemList(),
                itemSelection: Array<PerInfoInitValueSettingItemDto> = _.filter(vm,
                    function(item: PerInfoInitValueSettingItemDto) {
                        return item.selectedRuleCode() == 2 && ((item.dataType() == 6 && (item.selectionItemRefType == 2 || item.selectionItemRefType == 1)) || item.itemCode() == "IS00084" || item.itemCode() == "IS00085");
                    }),
                itemIdLst = _.map(itemSelection, function(obj: IPerInfoInitValueSettingItemDto) {
                    return {
                        dataType: obj.dataType(),
                        selectionItemId: obj.selectionItemId,
                        selectionItemRefType: obj.selectionItemRefType,
                        baseDate: baseDate
                    };
                });

            if (itemIdLst.length > 0) {
                _.each(itemIdLst, function(item) {

                    let itemList: Array<any> = ko.toJS(vm),
                        indexList: Array<any> = [],
                        itemIndex: number = 0;
                    _.each(itemList, function(obj: PerInfoInitValueSettingItemDto) {
                        if (obj.selectionItemId === item.selectionItemId) {
                            indexList.push(itemIndex);
                        }
                        itemIndex++;
                    });


                    if (indexList.length > 0) {
                        service.getAllComboxByHistory(item).done(function(data: Array<any>) {
                            if (data) {
                                _.each(indexList, function(index) {
                                    vm[index].selection([]);
                                    vm[index].selection(data);
                                    vm[index].selection.valueHasMutated();
                                    if (item.dataType === ITEM_SINGLE_TYPE.SEL_BUTTON) {
                                        let objSel: any =  _.find(vm[index].selection(), function(c) { if (c.optionValue == vm[index].selectedCode()) { return c } });
                                        vm[index].selectionName(objSel == undefined ? (vm[index].selectedCode()=="" || vm[index].selectedCode()== undefined? "":(vm[index].ctgCode() === "CS00016" || vm[index].ctgCode() === "CS00017") ? text("CPS001_107") : (vm[index].selectedCode() + "　" + text("CPS001_107"))) : objSel.optionText);
                                        vm[index].selectionName.valueHasMutated();
                                    }else{
                                        let value: string = vm[index].stringValue();
                                        vm[index].selectedCode(value);
                                        vm[index].selectedCode.valueHasMutated();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

        /**
         * find item by perInfoItemDefId
         */
        findItem(lstITem: Array<any>, perInfoItemDefId: string): PerInfoInitValueSettingItemDto {
            return _.find(lstITem, function(obj) {
                return obj.perInfoItemDefId == perInfoItemDefId;
            });
        }

        /**
         * find category is selected
         */
        findCtg(lstCtg: Array<any>, ctgId: string): any {
            return _.find(lstCtg, function(obj) {
                return obj.perInfoCtgId == ctgId;
            });
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }

        checkError(itemList: Array<any>) {

        }
        
        /**
         * export excel
         */
        exportExcel() {
            let self = this;
            nts.uk.ui.block.grayout();
            service.saveAsExcel().done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }
    }
    export class InitValueSettingDetail {
        settingCode: KnockoutObservable<string>;
        settingName: KnockoutObservable<string>;
        ctgList: KnockoutObservableArray<any>;
        currentItemId: KnockoutObservable<string> = ko.observable('');
        ctgColums: KnockoutObservableArray<any> = ko.observableArray([
            { headerText: '', key: 'perInfoCtgId', width: 100, hidden: true },
            { headerText: text('CPS009_15'), key: 'setting', dataType: 'string', width: 50, formatter: makeIcon },
            { headerText: text('CPS009_16'), key: 'categoryName', width: 200 }
        ]);
        itemList: KnockoutObservableArray<PerInfoInitValueSettingItemDto>;
        constructor(params: IInitValueSettingDetail) {
            let self = this;
            self.settingCode = ko.observable(params.settingCode);
            self.settingName = ko.observable(params.settingName);
            self.ctgList = ko.observableArray(params.ctgList);
            self.itemList = ko.observableArray(params.itemList || []);
        }

        setData(params: IInitValueSettingDetail) {
            let self = this;
            self.settingCode(params.settingCode);
            self.settingName(params.settingName);
            self.ctgList(params.ctgList);
        }
    }


    // obj list bên trái
    export interface IInitValueSetting {
        companyId?: string;
        settingId: string;
        settingCode: string;
        settingName: string;
    }

    export class InitValueSetting {
        companyId: string;
        settingId: string;
        settingCode: string;
        settingName: string;
        constructor(params: IInitValueSetting) {
            this.settingId = params.settingId;
            this.settingCode = params.settingCode;
            this.settingName = params.settingName;
        }

    }

    export interface ICategoryInfo {
        perInfoCtgId: string;
        categoryName: string;
        setting: boolean;
    }

    export class CategoryInfo {
        perInfoCtgId: string;
        categoryName: string;
        setting: boolean;
        constructor(params: ICategoryInfo) {
            this.perInfoCtgId = params.perInfoCtgId;
            this.categoryName = params.categoryName;
            this.setting = params.setting;
        }
    }

    export interface IInitValue {
        id: string;
        itemName: string;
        comboxValue: string;
        value: string;
    }

    export class InitValue {
        id: string;
        itemName: string;
        comboxValue: string;
        value: string;
        constructor(params: IInitValue) {
            this.id = params.id;
            this.itemName = params.itemName;
            this.comboxValue = params.comboxValue;
            this.value = params.value;
        }
    }

    export interface IInitValueSettingDetail {
        settingCode: string;
        settingName: string;
        ctgList?: Array<any>;
        itemList?: Array<any>;
    }

    export class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    export interface IPerInfoInitValueSettingItemDto {

        // đoạn này dùng để  hiển thị
        perInfoItemDefId: string;
        settingId?: string;
        perInfoCtgId: string;
        itemName: string;
        isRequired: number;
        refMethodType: number;
        //dành cho cột 2 - combo
        itemType: number; //日付　型-1; 統合ログインコード-2; 口座名１～口座名５-3; .....
        listComboItem?: Array<any>;

        //trường này dùng để phân biệt item đó thuộc kiểu dữ liệu nào number or string
        dataType: number;

        // đoạn này dùng để lưu dữ liệu        
        saveDataType: number;
        stringValue?: string;
        intValue?: number;
        dateValue?: string;
        dateWithDay?: number;
        timePoint?: string;

        // xác định contraint của item đó
        itemCode: string;
        ctgCode: string;
        // xác định nếu item thuộc kiểu number thì thuộc loại integer hay decimal
        numberDecimalPart: number;
        numberIntegerPart: number;
        // timepoint
        timeItemMin?: number;

        timeItemMax?: number;

        // lưu giá trị của integer value or decimal value of numberic type
        numbereditor?: any;

        // selectionItemId để kết nối với bảng SelectionItem
        selectionItemId?: string;

        selectionItemRefType?: number;

        selection?: Array<any>;

        // xác định dateType thuộc kiểu ngày tháng năm hay năm tháng hay năm
        dateType?: number;

        timepointItemMin?: number;
        timepointItemMax?: number;

        numericItemMin?: number;
        numericItemMax?: number;        
        numberItemAmount?: number;
        numberItemMinus?: number;

        stringItemType?: number;
        stringItemLength?: number;
        stringItemDataType?: number;
        fixedItem: boolean;
        // dung de phan biet category thuoc dang lich su lien tuc thi cot 2
        // cua itemList se ko hoat dong
        categoryType: number;

        // index dùng để phân biệt endate
        indexItem: number;

        // radioId của kiểu item radio
        radioId?: string;
        radioLst?: Array<any>;

        // disable combox
        disableCombox: boolean;

        // enable A23 xu li cho ctg CS00020
        enableControl: boolean;
        
        initValue: string;


    }

    export class PerInfoInitValueSettingItemDto {
        fixedItem: boolean;
        perInfoItemDefId: KnockoutObservable<string>;
        settingId: KnockoutObservable<string>;
        perInfoCtgId: KnockoutObservable<string>;
        itemName: KnockoutObservable<string>;
        isRequired: KnockoutObservable<boolean>;

        refMethodType: KnockoutObservable<number>;
        itemType: KnockoutObservable<number>;
        listComboItem: KnockoutObservableArray<any>;
        selectedRuleCode: KnockoutObservable<number>;

        dataType: KnockoutObservable<number>;

        //lưu giá trị của item trong bảng init item
        saveDataType: KnockoutObservable<number>;
        stringValue: KnockoutObservable<string>;
        intValue: KnockoutObservable<number>;

        //dateType
        dateType: number;
        dateValue: KnockoutObservable<String>;

        dateWithDay: KnockoutObservable<number>;
        timePoint: KnockoutObservable<string>;

        // trường hợp datatype là kiểu selection
        selection: KnockoutObservableArray<any>;
        selectedCode: KnockoutObservable<string>;
        selectionName: KnockoutObservable<string>;

        //constraint
        itemCode: KnockoutObservable<string>;
        ctgCode: KnockoutObservable<string>;

        // kiểu number có 2 loại là số nguyên với số thực
        numbericItem: NumbericItem;
        numbereditor: any;

        // time
        timeItemMin: number;
        timeItemMax: number;

        //selectionItemId? : string;
        selectionItemId: string;
        selectionItemRefType: number;

        //timepoint
        timepointItemMin: number;
        timepointItemMax: number;

        //number
        numericItemMin: number;
        numericItemMax: number;
        numberItemAmount: number;
        numberItemMinus: number;

        //string
        stringItemType: number;
        stringItemLength: number;
        stringItemDataType: number;

        // phân biệt endate của category lịch sử liên tục để disable
        categoryType: number;
        indexItem: number = 0;

        radioId: string;
        radioCode: string;
        radioLst: Array<any> = [];

        // xử lý disable or enable cho A22 && A23
        disableCombox: KnockoutObservable<boolean> = ko.observable(true);
        enableControl: KnockoutObservable<boolean> = ko.observable(true);

        itemLstTimePoint =
        [
            new ItemCode("IS00131", "IS00133", "IS00134", "IS00136", "IS00137"),
            new ItemCode("IS00140", "IS00142", "IS00143", "IS00145", "IS00146"),
            new ItemCode("IS00158", "IS00160", "IS00161", "IS00163", "IS00164"),
            new ItemCode("IS00167", "IS00169", "IS00170", "IS00172", "IS00173"),
            new ItemCode("IS00176", "IS00178", "IS00179", "IS00181", "IS00182"),
            new ItemCode("IS00149", "IS00151", "IS00152", "IS00154", "IS00155"),
            new ItemCode("IS00194", "IS00196", "IS00197", "IS00199", "IS00200"),
            new ItemCode("IS00203", "IS00205", "IS00206", "IS00208", "IS00209"),
            new ItemCode("IS00212", "IS00214", "IS00215", "IS00217", "IS00218"),
            new ItemCode("IS00221", "IS00223", "IS00224", "IS00226", "IS00227"),
            new ItemCode("IS00230", "IS00232", "IS00233", "IS00235", "IS00236"),
            new ItemCode("IS00212", "IS00214", "IS00215", "IS00217", "IS00218"),
            new ItemCode("IS00239", "IS00241", "IS00242", "IS00244", "IS00245"),
            new ItemCode("IS00185", "IS00187", "IS00188", "IS00190", "IS00191"),
        ];
//        isFirstSelected : number = 0;


        constructor(params: IPerInfoInitValueSettingItemDto) {
            let self = this;

            self.categoryType = params.categoryType;
            self.indexItem = params.indexItem;
            self.fixedItem = params.fixedItem;
            self.perInfoItemDefId = ko.observable(params.perInfoItemDefId || "");
            self.settingId = ko.observable(params.settingId || "");
            self.perInfoCtgId = ko.observable(params.perInfoCtgId || "");
            self.itemCode = ko.observable(params.itemCode || "");
            self.ctgCode = ko.observable(params.ctgCode || "");
            self.itemName = ko.observable(params.itemName || "");

            self.isRequired = ko.observable(!!params.isRequired || false);
            self.refMethodType = ko.observable(params.refMethodType || 0);

            self.saveDataType = ko.observable(params.saveDataType || 0);
            self.stringValue = ko.observable(params.stringValue || params.initValue);

            self.intValue = ko.observable(params.intValue || (params.intValue === 0 ? 0 : params.initValue));
            self.dateWithDay = ko.observable(params.dateWithDay || (params.dateWithDay === 0 ? 0 : params.initValue));
            self.timePoint = ko.observable(params.timePoint || params.initValue);

            self.timeItemMin = params.timeItemMin || undefined;
            self.timeItemMax = params.timeItemMax || undefined;

            self.timepointItemMin = params.timepointItemMin || undefined;
            self.timepointItemMax = params.timepointItemMax || undefined;

            self.numericItemMin = params.numericItemMin || undefined;
            self.numericItemMax = params.numericItemMax || undefined;

            self.itemType = ko.observable(params.itemType || undefined);
            self.dataType = ko.observable(params.dataType || undefined);
            self.disableCombox(params.disableCombox == true ? false : true);
            self.enableControl(params.enableControl);
            self.selectedRuleCode = ko.observable(params.refMethodType || 1);
            self.selectedCode = ko.observable();
            
            switch (params.dataType) {
                case ITEM_SINGLE_TYPE.STRING:
                    self.stringItemType = params.stringItemType || undefined;
                    self.stringItemLength = params.stringItemLength || undefined;
                    self.stringItemDataType = params.stringItemDataType || undefined;
                    self.numericItemMin = params.numericItemMin || undefined;
                    self.numericItemMax = params.numericItemMax || undefined;
                    break;
                    
                case ITEM_SINGLE_TYPE.NUMERIC:
                    self.numbericItem = new NumbericItem(params.dataType,
                        {
                            numberDecimalPart: params.numberDecimalPart,
                            numberIntegerPart: params.numberIntegerPart
                        }) || null;

                    if (params.numberDecimalPart === 0 && (params.numberIntegerPart === 0 || params.numberIntegerPart === null)) {
                        self.numbereditor = {
                            value: ko.observable(params.intValue || (params.intValue === 0 ? 0 : params.initValue)),
                            constraint: params.itemCode,
                            option: new nts.uk.ui.option.NumberEditorOption({
                                grouplength: params.numberItemMinus && 3,
                                decimallength: 0,
                                textalign: "left"
                            }),
                            enable: ko.observable(true),
                            readonly: ko.observable(false)
                        };
                        break;
                    } else {

                        self.numbereditor = {
                            value: ko.observable(params.intValue || (params.intValue === 0 ? 0 : params.initValue)),
                            constraint: params.itemCode,
                            option: new nts.uk.ui.option.NumberEditorOption({
                                grouplength: params.numberItemMinus && 3,
                                decimallength: params.numberDecimalPart,
                                textalign: "left"
                            }),
                            enable: ko.observable(true),
                            readonly: ko.observable(false)
                        };
                        break;
                    }
                   
                case ITEM_SINGLE_TYPE.DATE:
                    self.dateType = params.dateType || undefined;
                    switch (params.dateType) {
                        case DATE_TYPE.YEAR_MONTH_DAY:
                            self.dateValue = ko.observable(params.dateValue || undefined); break;
                        case DATE_TYPE.YEAR_MONTH:
                            if (params.dateValue === null) {
                                self.dateValue = ko.observable(params.initValue== null? undefined: params.initValue);
                                break;
                            } else {
                                self.dateValue = ko.observable(formatDate(new Date(params.dateValue), "yyyy/MM"));
                                break;
                            }

                        case DATE_TYPE.YEAR:
                            if (params.dateValue === null) {
                                self.dateValue = ko.observable(params.initValue== null? undefined: params.initValue);
                                break;
                            } else {
                                self.dateValue = ko.observable(formatDate(new Date(params.dateValue), "yyyy") || undefined);
                                break;
                            }
                    }

                case ITEM_SINGLE_TYPE.TIME:
                    break;
                case ITEM_SINGLE_TYPE.TIMEPOINT:
                    break;
                case ITEM_SINGLE_TYPE.SELECTION:
                    self.selectionItemId = params.selectionItemId || undefined;
                    self.selectionItemRefType = params.selectionItemRefType || undefined;
                    self.selection = ko.observableArray(params.selection || []);
                    self.selectedCode = ko.observable(params.stringValue == null ? (params.initValue== null? undefined: params.initValue) : params.stringValue);
                    break;
                case ITEM_SINGLE_TYPE.SEL_RADIO:
                
                    self.radioId = params.selectionItemId || undefined;
                    self.selectionItemRefType = params.selectionItemRefType || undefined;
                    self.selection = ko.observableArray(params.selection || []);
                    self.selectedCode = ko.observable(params.stringValue || (params.initValue== null? "1": params.initValue));
                    break;
                case ITEM_SINGLE_TYPE.SEL_BUTTON:
                
                    self.selectionItemId = params.selectionItemId || undefined;
                    self.selectionItemRefType = params.selectionItemRefType || undefined;
                    self.selection = ko.observableArray(params.selection || []);
                    self.selectedCode = ko.observable(params.stringValue == null ? (params.initValue== null? undefined : params.initValue) : params.stringValue);
                    let objSel: any = _.find(params.selection, function(c) { if (c.optionValue == self.selectedCode()) { return c } });
                    self.selectionName = ko.observable(params.stringValue == null? "": (objSel == undefined ? ((self.ctgCode() === "CS00016" || self.ctgCode() === "CS00017") ? text("CPS001_107"): (self.selectedCode() + "　" + text("CPS001_107"))) : objSel.optionText));
                    break;
            }
            
            switch (params.dataType) {
                case ITEM_SINGLE_TYPE.DATE:
                    self.listComboItem = ko.observableArray([
                        { code: 1, name: ReferenceMethodType.NOSETTING },
                        { code: 2, name: ReferenceMethodType.FIXEDVALUE },
                        { code: 3, name: ReferenceMethodType.SAMEASLOGIN },
                        { code: 4, name: ReferenceMethodType.SAMEASEMPLOYMENTDATE },
                        { code: 6, name: ReferenceMethodType.SAMEASSYSTEMDATE }]);
                    break;
                
                default:
                    self.listComboItem = ko.observableArray([
                        { code: 1, name: ReferenceMethodType.NOSETTING },
                        { code: 2, name: ReferenceMethodType.FIXEDVALUE },
                        { code: 3, name: ReferenceMethodType.SAMEASLOGIN }]);
                    break;
            }

            self.selectedRuleCode.subscribe(value => {
                
                if (value !== 2) {
                    error.clearAll();
                }
                
                if(value == 2 && self.enableControl() === true){
                    setTimeout(function(c) {
                        let x = "#" + self.perInfoItemDefId(), content: string = $("#" + self.perInfoItemDefId()).val();
                        if (!_.isNil(content) && content !=="") {
                            $("#" + self.perInfoItemDefId()).trigger("validate");
                        }
                    }, 100);
                }
                
                if (self.ctgCode() === "CS00020" || self.ctgCode() === "CS00070") {
                    self.createItemTimePointOfCS00020(value, self.itemCode());
                }
            });

        }

        createItemTimePointOfCS00020(value: any, itemCode: string) {
            let vm: Array<any> = __viewContext["viewModel"].currentCategory().itemList(),
                self = this,
                itemLst: Array<any> = [],
                itemSelected: any;

            itemSelected = _.filter(self.itemLstTimePoint, { itemCodeParent: itemCode });
            if (itemSelected.length > 0) {
                itemLst = _.filter(ko.toJS(vm), function(i) {
                    if ((i.itemCode == itemSelected[0].itemCode1) || (i.itemCode == itemSelected[0].itemCode2) || (i.itemCode == itemSelected[0].itemCode3) || (i.itemCode == itemSelected[0].itemCode4)) { return i; }
                })
                _.each(itemLst, function(x) {
                    vm[x.indexItem > 0 ? (x.indexItem - 1) : 0].selectedRuleCode(value);
                });
            }
        }

        button() {
            let self = this,
                groups: Array<IGroupControl> = [
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00128'
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00130',
                        workTime: 'IS00131',
                        firstTimes: {
                            start: 'IS00133',
                            end: 'IS00134'
                        },
                        secondTimes: {
                            start: 'IS00136',
                            end: 'IS00137'
                        }
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00139',
                        workTime: 'IS00140',
                        firstTimes: {
                            start: 'IS00142',
                            end: 'IS00143'
                        },
                        secondTimes: {
                            start: 'IS00145',
                            end: 'IS00146'
                        }
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00148',
                        workTime: 'IS00149',
                        firstTimes: {
                            start: 'IS00151',
                            end: 'IS00152'
                        },
                        secondTimes: {
                            start: 'IS00154',
                            end: 'IS00155'
                        }
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00157',
                        workTime: 'IS00158',
                        firstTimes: {
                            start: 'IS00160',
                            end: 'IS00161'
                        },
                        secondTimes: {
                            start: 'IS00163',
                            end: 'IS00164'
                        }
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00166',
                        workTime: 'IS00167',
                        firstTimes: {
                            start: 'IS00169',
                            end: 'IS00170'
                        },
                        secondTimes: {
                            start: 'IS00172',
                            end: 'IS00173'
                        }
                    },
                    {
                        ctgCode: 'CS00020',
                        workType: 'IS00175',
                        workTime: 'IS00176',
                        firstTimes: {
                            start: 'IS00178',
                            end: 'IS00179'
                        },
                        secondTimes: {
                            start: 'IS00181',
                            end: 'IS00182'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00193',
                        workTime: 'IS00194',
                        firstTimes: {
                            start: 'IS00196',
                            end: 'IS00197'
                        },
                        secondTimes: {
                            start: 'IS00199',
                            end: 'IS00200'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00202',
                        workTime: 'IS00203',
                        firstTimes: {
                            start: 'IS00205',
                            end: 'IS00206'
                        },
                        secondTimes: {
                            start: 'IS00208',
                            end: 'IS00209'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00211',
                        workTime: 'IS00212',
                        firstTimes: {
                            start: 'IS00214',
                            end: 'IS00215'
                        },
                        secondTimes: {
                            start: 'IS00217',
                            end: 'IS00218'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00220',
                        workTime: 'IS00221',
                        firstTimes: {
                            start: 'IS00223',
                            end: 'IS00224'
                        },
                        secondTimes: {
                            start: 'IS00226',
                            end: 'IS00227'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00229',
                        workTime: 'IS00230',
                        firstTimes: {
                            start: 'IS00232',
                            end: 'IS00233'
                        },
                        secondTimes: {
                            start: 'IS00235',
                            end: 'IS00236'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00238',
                        workTime: 'IS00239',
                        firstTimes: {
                            start: 'IS00241',
                            end: 'IS00242'
                        },
                        secondTimes: {
                            start: 'IS00244',
                            end: 'IS00245'
                        }
                    },
                    {
                        ctgCode: 'CS00070',
                        workType: 'IS00184',
                        workTime: 'IS00185',
                        firstTimes: {
                            start: 'IS00187',
                            end: 'IS00188'
                        },
                        secondTimes: {
                            start: 'IS00190',
                            end: 'IS00191'
                        }
                    }
                ],
                vm: Array<any> = __viewContext["viewModel"].currentCategory().itemList(),
                workType: any = _.find(groups, { workType: self.itemCode() }),
                workTime: any = _.find(groups, { workTime: self.itemCode() }),
                isKdl002: boolean = self.itemCode() == "IS00128" ? true : false,
                isWorkType: boolean = workType !== undefined ? true : false,
                isWorkTime: boolean = workTime !== undefined ? true : false,
                itemWorkTime: any = _.find(ko.toJS(vm), function(obj) {
                    if (isWorkType)
                    { if (obj.itemCode === workType.workTime) { return obj; } }
                }),
                itemWorkType: any = _.find(ko.toJS(vm), function(obj) {
                    if (isWorkTime) {
                        if (obj.itemCode === workTime.workType)
                        { return obj; }
                    }
                });
            if (self.ctgCode() == "CS00020" || self.ctgCode() == "CS00070") {

                if (isKdl002) {
                    setShared("KDL002_Multiple", false, true);
                    setShared("KDL002_SelectedItemId", _.isNil(self.selectedCode()) ? []: [self.selectedCode()], true);
                    setShared("KDL002_AllItemObj", _.map(ko.toJS(self.selection), x => x.optionValue), true);

                    modal('at', '/view/kdl/002/a/index.xhtml').onClosed(() => {
                        let childData: Array<any> = getShared('KDL002_SelectedNewItem');

                        if (childData[0]) {
                            self.selectionName(childData[0].code + "　" + childData[0].name);
                            self.selectedCode(childData[0].code);
                        }
                    });
                } else {
                    if (['IS00130', 'IS00131', 'IS00139', 'IS00140'].indexOf(self.itemCode()) > - 1) {
                        let objShare: any = {};
                        if (isWorkType) {
                            objShare = {
                                workTypeCodes: workType && _.map(self.selection(), x => x.optionValue),
                                selectedWorkTypeCode: self.selectedCode() && ko.toJS(self.selectedCode),
                                workTimeCodes: _.map(itemWorkTime != undefined ? itemWorkTime.selection : [], x => x.optionValue),
                                selectedWorkTimeCode: ko.toJS(itemWorkTime.selectedCode),
                                showNone: false
                            };
                        } else {
                            objShare = {
                                workTypeCodes: _.map(itemWorkType != undefined ? itemWorkType.selection : [], x => x.optionValue),
                                selectedWorkTypeCode: ko.toJS(itemWorkType.selectedCode),
                                workTimeCodes: workTime && _.map(self.selection(), x => x.optionValue),
                                selectedWorkTimeCode: self.selectedCode() && ko.toJS(self.selectedCode),
                                showNone: false
                            };
                        }

                        setShared('parentCodes', objShare, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            let childData: IChildData = getShared('childData');
                            self.setValueOfCS00020(childData, isWorkType, isWorkTime,
                                workType, workTime,
                                itemWorkTime, itemWorkType, true);
                        });

                    } else {
                        if (isWorkType) {
                            setShared("KDL002_Multiple", false, true);
                            setShared("KDL002_SelectedItemId", _.isNil(self.selectedCode()) ? []: [self.selectedCode()], true);
                            setShared('kdl002isSelection', true, true);
                            setShared("KDL002_AllItemObj", _.map(ko.toJS(self.selection), x => x.optionValue), true);

                            modal('at', '/view/kdl/002/a/index.xhtml').onClosed(() => {
                                let childData: Array<any> = getShared('KDL002_SelectedNewItem');
                                if (childData.length > 0) {
                                    if (childData[0].code == "") {
                                        self.selectionName(undefined);
                                        self.selectedCode(undefined);
                                    } else {
                                        self.selectionName(childData[0].code + "　" + childData[0].name);
                                        self.selectedCode(childData[0].code);
                                    }
                                }
                            });

                        } else {
                            setShared("kml001multiSelectMode", false);
                            setShared("kml001selectedCodeList", _.isNil(ko.toJS(self.selectedCode)) ? [] : [ko.toJS(self.selectedCode)]);
                            setShared("kml001isSelection", true);
                            setShared("kml001selectAbleCodeList", _.map(self.selection(), x => x.optionValue), true);

                            modal('at', '/view/kdl/001/a/index.xhtml').onClosed(() => {
                                let childData: Array<any> = getShared('kml001selectedTimes');
                                if (childData) {
                                    if (childData.length > 0) {
                                        self.setValueOfCS00020(childData[0], isWorkType, isWorkTime,
                                            workType, workTime,
                                            itemWorkTime, itemWorkType, false);
                                    }
                                }
                            });
                        }

                    }
                }
            }
            if (self.ctgCode() == "CS00017") {
                self.clickButtonCS0017();
            }
        }

        setValueOfCS00020(childData: any, isWorkType: boolean, isWorkTime: boolean,
            workType: any, workTime: any,
            itemWorkTime: any, itemWorkType: any, isKdl003: boolean) {
            let self = this,
                vm: Array<any> = __viewContext["viewModel"].currentCategory().itemList();;
            if (isWorkType) {
                let itemChilds: Array<any> = _.filter(ko.toJS(vm), function(obj) {
                    if (obj.itemCode === workType.firstTimes.start || obj.itemCode === workType.firstTimes.end || obj.itemCode === workType.secondTimes.start || obj.itemCode === workType.secondTimes.end)
                    { return obj; }
                });
                if (childData.selectedWorkTypeCode == "") {
                    self.selectionName(undefined);
                   
                } else {
                    self.selectionName(childData.selectedWorkTypeCode + "　" + childData.selectedWorkTypeName);
                    self.selectedCode(childData.selectedWorkTypeCode);
                }
                
                 self.selectedCode(childData.selectedWorkTypeCode == "" ? undefined:childData.selectedWorkTypeCode);

                vm[itemWorkTime.indexItem - 1].selectionName(childData.selectedWorkTimeCode + "　" + childData.selectedWorkTimeName);
                vm[itemWorkTime.indexItem - 1].selectedCode(childData.selectedWorkTimeCode);

                let params: ICheckParam = { workTimeCode: childData.selectedWorkTimeCode };
                service.checkStartEnd(params).done(function(data) {
                    service.checkMutiTime(params).done(function(data1) {
                        self.setData(childData, itemChilds, data, data1);
                    });
                });
            } else {

                let itemChilds: Array<any> = _.filter(ko.toJS(vm), function(obj) {
                    if (obj.itemCode === workTime.firstTimes.start || obj.itemCode === workTime.firstTimes.end || obj.itemCode === workTime.secondTimes.start || obj.itemCode === workTime.secondTimes.end) {
                        return obj;
                    }
                });
                if (childData.selectedWorkTimeCode == "") {
                    self.selectionName(undefined);
                } else {
                    self.selectionName(childData.selectedWorkTimeCode + "　" + childData.selectedWorkTimeName);
                }
                self.selectedCode(childData.selectedWorkTimeCode == "" ? undefined : childData.selectedWorkTimeCode);
                if (isKdl003) {
                    vm[itemWorkType.indexItem - 1].selectionName(childData.selectedWorkTypeCode + "　" + childData.selectedWorkTypeName);
                    vm[itemWorkType.indexItem - 1].selectedCode(childData.selectedWorkTypeCode);
                }
                let params: ICheckParam = { workTimeCode: childData.selectedWorkTimeCode };
                service.checkStartEnd(params).done(function(data) {
                    service.checkMutiTime(params).done(function(data1) {
                        self.setData(childData, itemChilds, data, data1);
                    });
                });
            }
        }

        setData(childData: IChildData, itemChilds: Array<any>, checkStartEnd: boolean, mutiTime: boolean) {
            let vm: Array<any> = __viewContext["viewModel"].currentCategory().itemList(),
                itemlength: number = itemChilds.length; 
            
            for (let i: number = 0; i < itemlength; i++) {
                if (itemlength <= 2) {
                    vm[itemChilds[i].indexItem - 1].enableControl(checkStartEnd);
                    vm[itemChilds[i + 1].indexItem - 1].enableControl(checkStartEnd);
                    vm[itemChilds[i].indexItem - 1].dateWithDay(childData.first.start);
                    vm[itemChilds[i + 1].indexItem - 1].dateWithDay(childData.first.end);
                    i = i + 1;
                    
                } else {
                    vm[itemChilds[i].indexItem - 1].enableControl(checkStartEnd);
                    vm[itemChilds[i + 1].indexItem - 1].enableControl(checkStartEnd);
                    vm[itemChilds[i].indexItem - 1].dateWithDay(childData.first.start);
                    vm[itemChilds[i + 1].indexItem - 1].dateWithDay(childData.first.end);
                    vm[itemChilds[i + 2].indexItem - 1].enableControl(mutiTime && checkStartEnd);
                    vm[itemChilds[i + 3].indexItem - 1].enableControl(mutiTime && checkStartEnd);
                    vm[itemChilds[i + 2].indexItem - 1].dateWithDay(childData.second.start);
                    vm[itemChilds[i + 3].indexItem - 1].dateWithDay(childData.second.end);
                    i = i + 3;
                }
            }

        }

        clickButtonCS0017() {
            let self = this,
                baseDate = moment.utc(__viewContext["viewModel"].baseDate());
            if(baseDate._isValid){
                service.checkFunctionNo().done(data => {
                    setShared('inputCDL008', {
                        selectedCodes: [self.selectedCode()],
                        baseDate: baseDate.toDate(),
                        isMultiple: false,
                        selectedSystemType: 1,// 1 : 個人情報 , 2 : 就業 , 3 :給与 , 4 :人事 ,  5 : 管理者
                        isrestrictionOfReferenceRange: data.available,
                        isShowBaseDate: false
                    }, true);
                    modal('com', '/view/cdl/008/a/index.xhtml').onClosed(() => {
                        // Check is cancel.
                        if (getShared('CDL008Cancel')) {
                            return;
                        }

                        //view all code of selected item 
                        let output = getShared('outputCDL008');
                        if (output) {
                            let objSel: any = _.find(self.selection(), function(c) { if (c.optionValue == output) { return c; } });
                            self.selectionName(objSel == undefined ? "" : objSel.optionText);
                            self.selectedCode(output);
                        }

                    });
                });
            }
        }
        
        
    }

    export interface IPerInfoInitValueSettingDto {
        companyId?: string;
        settingId: string;
        settingCode: string;
        settingName: string;

    }

    export interface INumbericItem {
        numberDecimalPart: number;
        numberIntegerPart: number;

    }

    export class NumbericItem {
        numberDecimalPart: number;
        numberIntegerPart: number;
        constructor(params: number, params2: INumbericItem) {
            let self = this;
            if (params === 2) {

                this.numberIntegerPart = params2.numberIntegerPart;
                this.numberDecimalPart = params2.numberDecimalPart;
            }
        }

    }


    export class ItemCode {
        itemCodeParent: string;
        itemCode1: string;
        itemCode2: string;
        itemCode3: string;
        itemCode4: string;
        constructor(itemCodeParent: string, itemCode1: string, itemCode2: string, itemCode3: string, itemCode4: string) {
            let self = this;
            self.itemCodeParent = itemCodeParent;
            self.itemCode1 = itemCode1;
            self.itemCode2 = itemCode2;
            self.itemCode3 = itemCode3;
            self.itemCode4 = itemCode4;
        }

    }

    function makeIcon(value, row) {
        if (value == "false")
            return '';
        return '●';
    }

    export interface IGroupControl {
        ctgCode: string;
        workType?: string;
        workTime?: string;
        firstTimes?: ITimeRange;
        secondTimes?: ITimeRange;
    }
    export enum ReferenceMethodType {
        /** (設定なし):1 */
        NOSETTING = '設定なし',
        /** 固定値): 2 **/
        FIXEDVALUE = '固定値',
        /** (ログイン者と同じ):3 */
        SAMEASLOGIN = 'ログイン者と同じ',
        /** (入社日と同じ): 4*/
        SAMEASEMPLOYMENTDATE = '入社日と同じ',
        /** (社員コードと同じ):5 */
        SAMEASEMPLOYEECODE = '社員コードと同じ',
        /** (システム日付):6 */
        SAMEASSYSTEMDATE = 'システム日付と同じ',
        /** (氏名と同じ ):7 */
        SAMEASNAME = '氏名と同じ ',
        /** (氏名（カナ）と同じ):8 */
        SAMEASKANANAME = '氏名（カナ）と同じ'
    }
    
    export enum ITEM_SINGLE_TYPE {
        STRING = 1,
        NUMERIC = 2,
        DATE = 3,
        TIME = 4,
        TIMEPOINT = 5,
        SELECTION = 6,
        SEL_RADIO = 7,
        SEL_BUTTON = 8,
        READONLY = 9,
        RELATE_CATEGORY = 10,
        NUMBERIC_BUTTON = 11,
        READONLY_BUTTON = 12
    }
    
    export enum DATE_TYPE{
        YEAR_MONTH_DAY = 1,
        YEAR_MONTH = 2,
        YEAR = 3
    }

    export interface IChildData {
        selectedWorkTypeCode: string;
        selectedWorkTypeName: string;
        selectedWorkTimeCode: string;
        selectedWorkTimeName: string;
        first: IDateRange;
        second: IDateRange;
    }

    export interface IDateRange {
        start: number;
        end: number;
    }

    export interface ICheckParam {
        workTimeCode?: string;
    }
}