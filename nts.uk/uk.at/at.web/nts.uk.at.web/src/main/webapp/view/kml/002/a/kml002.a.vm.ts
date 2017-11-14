module nts.uk.at.view.kml002.a.viewmodel {
    export class ScreenModel {
        settingItems: KnockoutObservableArray<SettingItemModel>;
        settingColumns: KnockoutObservable<any>;
        singleSelectedCode: KnockoutObservable<any>;
        code: KnockoutObservable<string>;
        editMode: KnockoutObservable<boolean>;
        name: KnockoutObservable<string>;
        useCls: KnockoutObservableArray<any>;
        useClsSelected: any;
        workSchedule: KnockoutObservableArray<any>;
        workScheduleSelected: any;
        units: KnockoutObservableArray<any>;
        unitSelected: any;
        calculatorItems: KnockoutObservableArray<CalculatorItem>;
        cbxAttribute: KnockoutObservableArray<any>;
        itemName: KnockoutObservable<string>;
        methods: KnockoutObservableArray<any>;
        cbxDisplayAtr: KnockoutObservableArray<any>;
        cbxTotal: KnockoutObservableArray<any>;
        cbxRounding: KnockoutObservableArray<any>;
        cbxFraction: KnockoutObservableArray<any>;
        allSelectedItems: KnockoutObservable<boolean>;
        workScheduleEnable: KnockoutObservable<boolean>;
        unitEnable: KnockoutObservable<boolean>;
        addLineEnable: KnockoutObservable<boolean>;
        deleteLineEnable: KnockoutObservable<boolean>;
        dataB: any;
        dataC: any;
        dataD: any;
        dataE: any;
        dataF: any;
        dataG: any;
        
        constructor() {
            var self = this;
            
            self.allSelectedItems = ko.observable(false);
            
            self.workScheduleEnable = ko.observable(true);
            self.unitEnable = ko.observable(true);
            
            self.addLineEnable = ko.observable(true);
            self.deleteLineEnable = ko.observable(true);
            
            self.settingItems = ko.observableArray([]);
            
            self.settingColumns = ko.observableArray([
                { headerText: nts.uk.resource.getText("KML002_6"), prop: 'code', width: 50 },
                { headerText: nts.uk.resource.getText("KML002_7"), prop: 'name', width: 200, formatter: _.escape }
            ]);
            
            self.singleSelectedCode = ko.observable("");
            
            self.code = ko.observable("");
            self.editMode = ko.observable(true);  
            self.name = ko.observable(""); 
            
            //A6_3 + A6_4
            self.useCls = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("Enum_UseAtr_NotUse") },
                { code: '1', name: nts.uk.resource.getText("Enum_UseAtr_Use") }
            ]);
            
            self.useClsSelected = ko.observable(0); 
            
            //A3_10 + A3_11
            self.workSchedule = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("Enum_Include") },
                { code: '1', name: nts.uk.resource.getText("Enum_Exclude") }
            ]);
            
            self.workScheduleSelected = ko.observable(0); 
            
            //A3_6 + A3_7
            self.units = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("Enum_Daily") },
                { code: '1', name: nts.uk.resource.getText("Enum_Time_Zone") }
            ]);
            
            self.unitSelected = ko.observable(0); 
            
            self.calculatorItems = ko.observableArray([]);
            
            self.cbxAttribute = ko.observableArray([
                { attrCode: 0, attrName: nts.uk.resource.getText("Enum_Time") },
                { attrCode: 1, attrName: nts.uk.resource.getText("Enum_Amount_Of_Money") },                
                { attrCode: 2, attrName: nts.uk.resource.getText("Enum_Number_Of_People") },
                { attrCode: 3, attrName: nts.uk.resource.getText("Enum_Number") },
                { attrCode: 4, attrName: nts.uk.resource.getText("Enum_Avarege_Price") }
            ]);
            
            self.itemName = ko.observable("");
            
            self.methods = ko.observableArray([
                { methodCode: 0, methodName: nts.uk.resource.getText("KML002_25") },
                { methodCode: 1, methodName: nts.uk.resource.getText("KML002_26") }
            ]);
            
            self.cbxDisplayAtr = ko.observableArray([
                { displayAttrCode: 0, displayAttrName: nts.uk.resource.getText("KML002_21") },
                { displayAttrCode: 1, displayAttrName: nts.uk.resource.getText("KML002_22") }
            ]);
            
            self.cbxTotal = ko.observableArray([
                { totalCode: 0, totalName: nts.uk.resource.getText("KML002_22") },
                { totalCode: 1, totalName: nts.uk.resource.getText("KML002_23") }
            ]);
            
            $('#popup-area').ntsPopup({
                position: {
                    my: 'left top',
                    at: 'left bottom',
                    of: $('#toggle-popup')
                },
                dismissible: false
            });
            
            // Show or hide
            $('#toggle-popup').click(function () {
                $(this).siblings('#popup-area').ntsPopup('toggle');              
            });
            
            self.allSelectedItems.subscribe(function(value) {
                var items = [];
                if(value == true) {
                    for(var i = 0; i < self.calculatorItems().length; i++) {
                        var item : ICalculatorItem = {
                            isChecked: true,
                            itemCd: self.calculatorItems()[i].itemCd(),
                            attribute: self.calculatorItems()[i].attribute(),
                            itemName: self.calculatorItems()[i].itemName(),
                            settingMethod: self.calculatorItems()[i].settingMethod(),
                            formula: self.calculatorItems()[i].formula(),
                            displayAtr: self.calculatorItems()[i].displayAtr(),
                            total: self.calculatorItems()[i].total(),
                            rounding: self.calculatorItems()[i].rounding(),
                            fraction: self.calculatorItems()[i].fraction(),
                            order: self.calculatorItems()[i].order(),
                            formBuilt: self.calculatorItems()[i].formBuilt,
                            formTime: self.calculatorItems()[i].formTime,
                            formPeople: self.calculatorItems()[i].formPeople,
                            amount: self.calculatorItems()[i].amount,
                            numerical: self.calculatorItems()[i].numerical,
                            unitPrice: self.calculatorItems()[i].unitPrice
                        };
                        
                        items.push(new CalculatorItem(item));
                    }
                    
                    self.calculatorItems([]);
                    self.calculatorItems(items);
                } else {
                    //Just remove check all checkbox
                    var flag = self.checkSelectedItems();
                    
                    if(!flag) {
                        //Remove all selected checkbox
                        for(var i = 0; i < self.calculatorItems().length; i++) {
                            var item : ICalculatorItem = {
                                isChecked: false,
                                itemCd: self.calculatorItems()[i].itemCd(),
                                attribute: self.calculatorItems()[i].attribute(),
                                itemName: self.calculatorItems()[i].itemName(),
                                settingMethod: self.calculatorItems()[i].settingMethod(),
                                formula: self.calculatorItems()[i].formula(),
                                displayAtr: self.calculatorItems()[i].displayAtr(),
                                total: self.calculatorItems()[i].total(),
                                rounding: self.calculatorItems()[i].rounding(),
                                fraction: self.calculatorItems()[i].fraction(),
                                order: self.calculatorItems()[i].order(),
                                formBuilt: self.calculatorItems()[i].formBuilt,
                                formTime: self.calculatorItems()[i].formTime,
                                formPeople: self.calculatorItems()[i].formPeople,
                                amount: self.calculatorItems()[i].amount,
                                numerical: self.calculatorItems()[i].numerical,
                                unitPrice: self.calculatorItems()[i].unitPrice
                            };
                            
                            items.push(new CalculatorItem(item));
                        }
                        
                        self.calculatorItems([]);
                        self.calculatorItems(items);
                    }
                }
            });  
            
            //Bind data to from when user select item on grid
            self.singleSelectedCode.subscribe(function(value) {
                // clear all error
                nts.uk.ui.errors.clearAll();
                
                if(value.length > 0){
                    self.calculatorItems.removeAll();
                    
                    service.getVerticalCalSetByCode(value).done(function(data) {
                        var items = [];
                        
                        self.editMode(false);
                        self.code(data.verticalCalCd);
                        self.name(data.verticalCalName);
                        self.unitSelected(data.unit);
                        self.useClsSelected(data.useAtr);
                        self.workScheduleSelected(data.assistanceTabulationAtr);  
                        self.workScheduleEnable(false);
                        self.unitEnable(false);
                        
                        for(var i = 0; i < data.verticalCalItems.length; i++) {
                            var item : ICalculatorItem = {
                                isChecked: false,
                                itemCd: data.verticalCalItems[i].itemId,
                                attribute: data.verticalCalItems[i].attributes,
                                itemName: data.verticalCalItems[i].itemName,
                                settingMethod: data.verticalCalItems[i].calculateAtr,
                                formula: "",
                                displayAtr: data.verticalCalItems[i].displayAtr,
                                total: 0,
                                rounding: data.verticalCalItems[i].rounding,
                                fraction: 0,
                                order: data.verticalCalItems[i].dispOrder,
                                attrEnable: false,
                                settingMethodEnable: false,
                                totalEnable: data.verticalCalItems[i].attributes == 4 ? false : true,
                                formBuilt: data.verticalCalItems[i].formBuilt,
                                formTime: data.verticalCalItems[i].formTime,
                                formPeople: data.verticalCalItems[i].formPeople,
                                amount: data.verticalCalItems[i].amount,
                                numerical: data.verticalCalItems[i].numerical,
                                unitPrice: data.verticalCalItems[i].unitPrice
                            };

                            items.push(new CalculatorItem(item));
                        }
                        
                        self.calculatorItems([]);
                        var sortedItems = _.sortBy(items, [function(o) { return o.order(); }]);
                        self.calculatorItems(sortedItems);
                    }).fail(function(res) {
                          
                    });
                }
            });
        }

        /**
         * Start page.
         */
        start(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            
            $.when(self.getData()).done(function() {
                                
                if (self.settingItems().length > 0) {
                    self.singleSelectedCode(self.settingItems()[0].code);
                }
                
                if(self.calculatorItems().length == 0) {
                    self.bindCalculatorItems();
                }
                
                dfd.resolve();
            }).fail(function(res) {
                dfd.reject(res);    
            });
            
            dfd.resolve();
            return dfd.promise();
        }
        
        /**
         * Get data from db.
         */
        getData(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            self.settingItems([]);
            service.findAllVerticalCalSet().done(function(data) {
                _.forEach(data, function(item) {
                    self.settingItems.push(new SettingItemModel(item.verticalCalCd, item.verticalCalName));
                });
                
                dfd.resolve(data);
            }).fail(function(res) {
                dfd.reject(res);    
            });
            
            return dfd.promise();
        }
        
        /**
         * Check selected items to set value for all selected checkbox.
         */
        checkSelectedItems() {
            var self = this;
            var selectedItems = 0;
            
            for(var i = 0; i < self.calculatorItems().length; i++) {
                if(self.calculatorItems()[i].isChecked()) {
                    selectedItems = selectedItems + 1;
                }
            }
            
            if(selectedItems < self.calculatorItems().length && selectedItems > 0) {
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Bind calculator items by vertical cal set.
         */
        bindCalculatorItems() {
            var self = this;
            
            var item : ICalculatorItem = {
                isChecked: false,
                itemCd: nts.uk.util.randomId(),
                attribute: 0,
                itemName: '',
                settingMethod: 0,
                formula: 'A + B + C',
                displayAtr: 0,
                total: 0,
                rounding: 0,
                fraction: 0,
                order: 1,
                attrEnable: true,
                settingMethodEnable: true,
                totalEnable: true,
                formBuilt: null,
                formTime: null,
                formPeople: null,
                amount: null,
                numerical: null,
                unitPrice: null
            };
            
            self.calculatorItems.push(new CalculatorItem(item)); 
        }
        
        /**
         * Clear form data to new mode.
         */
        newBtn() {
            var self = this;
            
            self.singleSelectedCode("");
            self.code("");
            self.editMode(true);
            self.name("");
            self.unitSelected(0);
            self.useClsSelected(0);
            self.workScheduleSelected(0);
            self.calculatorItems([]);
            self.bindCalculatorItems();
            
            self.workScheduleEnable(true);
            self.unitEnable(true);
        }
        
        /**
         * Add or Update data to db.
         */
        registrationBtn() {
            var self = this;
            
            // clear all error
            nts.uk.ui.errors.clearAll();
            
            // validate
            $(".input-code").trigger("validate");
            $(".input-name").trigger("validate");
            
            if (nts.uk.ui.errors.hasError()) {
                return;    
            }
            
            if(self.editMode()) {
                var filter = _.filter(self.settingItems(), function(o) { return o.code == self.code(); });
                
                if(filter.length > 0) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_3" });
                    return;
                }
            }
            
            var code = self.code();
            var name = self.name();
            var unit = self.unitSelected();
            var useAtr = self.useClsSelected();
            var assistanceTabulationAtr = self.workScheduleSelected();
            var verticalCalItems = new Array<VerticalCalItemDto>();
            
            for(var i = 0; i < self.calculatorItems().length; i++) {
                var dataB = self.dataB == null ? self.calculatorItems()[i].formBuilt : self.dataB;
                var dataC = self.dataC == null ? self.calculatorItems()[i].formTime : self.dataC;
                var dataD = self.dataD == null ? self.calculatorItems()[i].formPeople : self.dataD;
                var dataE = self.dataE == null ? self.calculatorItems()[i].amount : self.dataE;
                var dataF = self.dataF == null ? self.calculatorItems()[i].numerical : self.dataF;
                var dataG = self.dataG == null ? self.calculatorItems()[i].unitPrice : self.dataG;
                
                var item = {
                    verticalCalCd: code,
                    itemId: self.calculatorItems()[i].itemCd(),
                    itemName: self.calculatorItems()[i].itemName(),
                    calculateAtr: self.calculatorItems()[i].settingMethod(),
                    displayAtr: self.calculatorItems()[i].displayAtr(),
                    cumulativeAtr: self.calculatorItems()[i].fraction(),
                    attributes: self.calculatorItems()[i].attribute(),
                    rounding: self.calculatorItems()[i].rounding(),
                    dispOrder: self.calculatorItems()[i].order(),
                    //for B screen
                    formBuilt: self.calculatorItems()[i].settingMethod() == 1 ? dataB : null,
                    //for C screen
                    formTime: self.calculatorItems()[i].settingMethod() == 0 && self.calculatorItems()[i].attribute() == 0 ? dataC : null,
                    //for D screen
                    formPeople: self.calculatorItems()[i].settingMethod() == 0 && self.calculatorItems()[i].attribute() == 2 ? dataD : null,
                    //for E screen
                    amount: self.calculatorItems()[i].settingMethod() == 0 && self.calculatorItems()[i].attribute() == 1 ? dataE : null,
                    //for F screen
                    numerical: self.calculatorItems()[i].settingMethod() == 0 && self.calculatorItems()[i].attribute() == 3 ? dataF : null,
                    //for G screen
                    unitPrice: self.calculatorItems()[i].settingMethod() == 0 && self.calculatorItems()[i].attribute() == 4 ? dataG : null
                };
                
                verticalCalItems.push(item);
            }
            
            if(verticalCalItems.length > 0) {
                var data = new VerticalSettingDto(code, name, unit, useAtr, assistanceTabulationAtr, verticalCalItems);
            
                service.addVerticalCalSet(data).done(function() {
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    self.getData();
                    self.singleSelectedCode(data.verticalCalCd);
                    self.singleSelectedCode.valueHasMutated();
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError(error.message);
                }).always(function() {
                    nts.uk.ui.block.clear();      
                });
            } else {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_110" });
            }
        }
        
        /**
         * Open setting dialog.
         */
        settingBtn() {
            var self = this;
            
        }
        
        /**
         * Delete vertical cal set.
         */
        deleteBtn() {
            var self = this;
            
            let count = 0;
            for (let i = 0; i <= self.settingItems().length; i++){
                if(self.settingItems()[i].code == self.singleSelectedCode()){
                    count = i;
                    break;
                }
            }
            
            service.deleteVerticalCalSet(self.singleSelectedCode()).done(function() {
                self.getData().done(function(){
                    // if number of item from list after delete == 0 
                    if(self.settingItems().length==0){
                        self.newBtn();
                        return;
                    }
                    // delete the last item
                    if(count == ((self.settingItems().length))){
                        self.singleSelectedCode(self.settingItems()[count-1].code);
                        return;
                    }
                    // delete the first item
                    if(count == 0 ){
                        self.singleSelectedCode(self.settingItems()[0].code);
                        return;
                    }
                    // delete item at mediate list 
                    else if(count > 0 && count < self.settingItems().length){
                        self.singleSelectedCode(self.settingItems()[count].code);    
                        return;
                    }
                });
                
                nts.uk.ui.dialog.info({ messageId: "Msg_16" });
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError(error.message);
            }).always(function() {
                nts.uk.ui.block.clear();      
            });
        }
        
        /**
         * Add new calculator item.
         */
        addLineBtn() {
            var self = this;
            
            var item : ICalculatorItem = {
                isChecked: false,
                itemCd: nts.uk.util.randomId(),
                attribute: 0,
                itemName: '',
                settingMethod: 0,
                formula: 'A + B + C',
                displayAtr: 0,
                total: 0,
                rounding: 0,
                fraction: 0,
                order: self.calculatorItems().length + 1,
                attrEnable: true,
                settingMethodEnable: true,
                totalEnable: true,
                formBuilt: null,
                formTime: null,
                formPeople: null,
                amount: null,
                numerical: null,
                unitPrice: null
            };
            
            self.calculatorItems.push(new CalculatorItem(item));
            
            if(self.calculatorItems().length < 50) {
                self.addLineEnable(true);
            } else {
                self.addLineEnable(false);
            }
            
            if(self.calculatorItems().length > 0) {                
                self.deleteLineEnable(true);
            }
        }
        
        /**
         * Delete calculator item.
         */
        deleteLineBtn() {
            var self = this;
            var selectedItems = [];
            
            for(var i = 0; i < self.calculatorItems().length; i++) {
                if(!self.calculatorItems()[i].isChecked()) {
                    var item : ICalculatorItem = {
                        isChecked: self.calculatorItems()[i].isChecked(),
                        itemCd: nts.uk.util.randomId(),
                        attribute: self.calculatorItems()[i].attribute(),
                        itemName: self.calculatorItems()[i].itemName(),
                        settingMethod: self.calculatorItems()[i].settingMethod(),
                        formula: self.calculatorItems()[i].formula(),
                        displayAtr: self.calculatorItems()[i].displayAtr(),
                        total: self.calculatorItems()[i].total(),
                        rounding: self.calculatorItems()[i].rounding(),
                        fraction: self.calculatorItems()[i].fraction(),
                        order: i + 1,
                        attrEnable: self.calculatorItems()[i].attrEnable(),
                        settingMethodEnable: self.calculatorItems()[i].settingMethodEnable(),
                        totalEnable: self.calculatorItems()[i].totalEnable(),
                        formBuilt: self.calculatorItems()[i].formBuilt,
                        formTime: self.calculatorItems()[i].formTime,
                        formPeople: self.calculatorItems()[i].formPeople,
                        amount: self.calculatorItems()[i].amount,
                        numerical: self.calculatorItems()[i].numerical,
                        unitPrice: self.calculatorItems()[i].unitPrice
                    };
                    
                    selectedItems.push(new CalculatorItem(item));
                }
            }
            
            self.calculatorItems([]);
            
            for(var i = 0; i < selectedItems.length; i++) {
                var newItem : ICalculatorItem = {
                    isChecked: selectedItems[i].isChecked(),
                    itemCd: nts.uk.util.randomId(),
                    attribute: selectedItems[i].attribute(),
                    itemName: selectedItems[i].itemName(),
                    settingMethod: selectedItems[i].settingMethod(),
                    formula: selectedItems[i].formula(),
                    displayAtr: selectedItems[i].displayAtr(),
                    total: selectedItems[i].total(),
                    rounding: selectedItems[i].rounding(),
                    fraction: selectedItems[i].fraction(),
                    order: i + 1,
                    attrEnable: selectedItems[i].attrEnable(),
                    settingMethodEnable: selectedItems[i].settingMethodEnable(),
                    totalEnable: selectedItems[i].totalEnable(),
                    formBuilt: selectedItems[i].formBuilt,
                    formTime: selectedItems[i].formTime,
                    formPeople: selectedItems[i].formPeople,
                    amount: selectedItems[i].amount,
                    numerical: selectedItems[i].numerical,
                    unitPrice: selectedItems[i].unitPrice
                };
                
                self.calculatorItems.push(new CalculatorItem(newItem));
            }
            
            self.allSelectedItems(false);
            
            if(self.calculatorItems().length == 0) {                
                self.deleteLineEnable(false);
            }
        }
        
        /**
         * Get selected calculator items.
         */
        getSelectedCalculatorItems() {
            var self = this;
            var selectedItems = [];
            
            for(var i = 0; i < self.calculatorItems().length; i++) {
                if(self.calculatorItems()[i].isChecked()) {
                    selectedItems.push(self.calculatorItems()[i]);
                }
            }
            
            return selectedItems;
        }
        
        /**
         * Move up calculator item.
         */
        upBtn() {
            var self = this;
            
        }
        
        /**
         * Move down calculator item.
         */
        downBtn() {
            var self = this;
            
        }
        
        /**
         * Pass data to dialog.
         */
        passDataToDialogs(itemCd: number, attribute: number, itemName: string) {
            var self = this;
            var attrValue = "";
            
            if(attribute == 0) {
                attrValue = nts.uk.resource.getText("Enum_Time");
            } else if(attribute == 1) {
                attrValue = nts.uk.resource.getText("Enum_Amount_Of_Money");
            } else if(attribute == 2) {
                attrValue = nts.uk.resource.getText("Enum_Number_Of_People");
            } else if(attribute == 3) {
                attrValue = nts.uk.resource.getText("Enum_Number");
            } else if(attribute == 4) {
                attrValue = nts.uk.resource.getText("Enum_Avarege_Price");
            }
            
            // Get all items before current selected item if setting method = 1 to binding for dropdownlist
            var verticalCalItems = new Array<VerticalCalItemDto>();
            var currentItem = _.find(self.calculatorItems(), function(o) { return o.itemCd() == itemCd; });
            
            for(var i = 0; i < currentItem.order() - 1; i++) {
                var item = {
                    verticalCalCd: self.code(),
                    itemId: self.calculatorItems()[i].itemCd(),
                    itemName: self.calculatorItems()[i].itemName(),
                    calculateAtr: self.calculatorItems()[i].settingMethod(),
                    displayAtr: self.calculatorItems()[i].displayAtr(),
                    cumulativeAtr: self.calculatorItems()[i].fraction(),
                    attributes: self.calculatorItems()[i].attribute(),
                    rounding: self.calculatorItems()[i].rounding(),
                    dispOrder: self.calculatorItems()[i].order()
                };
                
                verticalCalItems.push(item);
            }
            
            // Get data form db to display on Dialog
            var dataTranfer = _.find(self.calculatorItems(), function(o) { return o.itemName() == itemName; });
            
            var data = {
                verticalCalCd: self.code(),
                itemId: itemCd,
                attributeId: attribute,
                attribute: attrValue,
                unit : self.unitSelected(),
                itemName: itemName,
                verticalItems: currentItem.settingMethod() == 1 ? verticalCalItems : null,
                formBuilt: dataTranfer.formBuilt,
                formTime: dataTranfer.formTime,
                formPeople: dataTranfer.formPeople,
                amount: dataTranfer.amount,
                numerical: dataTranfer.numerical,
                unitPrice: dataTranfer.unitPrice
            };
            
            nts.uk.ui.windows.setShared("KML002_A_DATA", data);
        }
        
        /**
         * Check conditions to open dialog.
         */
        openDialog(itemCd: number, settingMethod: number, attribute: number, itemName: string) {
            var self = this;
            
            if(itemName === "") {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_271" });
                return;
            }

            if(settingMethod == 1) {
                self.passDataToDialogs(itemCd, attribute, itemName);            
                nts.uk.ui.windows.sub.modal("/view/kml/002/b/index.xhtml").onClosed(() => {
                    self.dataB = nts.uk.ui.windows.getShared("KML002_B_DATA");
                }); 
            } else {
                if(attribute == 0) {
                    self.passDataToDialogs(itemCd, attribute, itemName);            
                    nts.uk.ui.windows.sub.modal("/view/kml/002/c/index.xhtml").onClosed(() => {
                        self.dataC = nts.uk.ui.windows.getShared("KML002_C_DATA");
                    }); 
                } else if(attribute == 1) {
                    self.passDataToDialogs(itemCd, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/e/index.xhtml").onClosed(() => {
                        self.dataE = nts.uk.ui.windows.getShared("KML002_E_DATA");
                    }); 
                } else if(attribute == 2) {
                    self.passDataToDialogs(itemCd, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/d/index.xhtml").onClosed(() => {
                        self.dataD = nts.uk.ui.windows.getShared("KML002_D_Budget");
                    }); 
                } else if(attribute == 3) {
                    self.passDataToDialogs(itemCd, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/f/index.xhtml").onClosed(() => {
                        self.dataF = nts.uk.ui.windows.getShared("KML002_F_DATA");
                    }); 
                } else if(attribute == 4) {
                    self.passDataToDialogs(itemCd, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/g/index.xhtml").onClosed(() => {
                        self.dataG = nts.uk.ui.windows.getShared("KML002_G_DATA");
                    }); 
                }
            }
        }
    }
    
    export class SettingItemModel {
        code: string;
        name: string;
        
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;       
        }
    } 
    
    class VerticalSettingDto {
        verticalCalCd: string;
        verticalCalName: string;
        unit: number;
        useAtr: number;
        assistanceTabulationAtr: number; 
        verticalCalItems: Array<VerticalCalItemDto>;  
        
        constructor(verticalCalCd: string, verticalCalName: string, unit: number, useAtr: number, assistanceTabulationAtr: number, verticalCalItems: Array<VerticalCalItemDto>) {
            this.verticalCalCd = verticalCalCd;
            this.verticalCalName = verticalCalName;     
            this.unit = unit;
            this.useAtr = useAtr; 
            this.assistanceTabulationAtr = assistanceTabulationAtr;  
            this.verticalCalItems = verticalCalItems;
        }
    }
    
    class VerticalCalItemDto {
        verticalCalCd: string;
        itemId: string;
        itemName: string;
        calculateAtr: number;
        displayAtr: number;
        cumulativeAtr: number;
        attributes: number;
        rounding: number;
        dispOrder: number;
        
        constructor(verticalCalCd: string, itemId: string, itemName: string, calculateAtr: number, displayAtr: number, 
                cumulativeAtr: number, attributes: number, rounding: number, dispOrder: number) {
            this.verticalCalCd = verticalCalCd;
            this.itemId = itemId;     
            this.itemName = itemName;
            this.calculateAtr = calculateAtr;
            this.displayAtr = displayAtr;  
            this.cumulativeAtr = cumulativeAtr;  
            this.attributes = attributes;
            this.rounding = rounding;
            this.dispOrder = dispOrder;
        }
    }
    
    export class CalculatorItem {
        isChecked: KnockoutObservable<boolean>;
        itemCd: KnockoutObservable<string>;
        attribute: KnockoutObservable<number>;
        itemName: KnockoutObservable<string>;
        settingMethod: KnockoutObservable<number>;
        formula: KnockoutObservable<string>;
        displayAtr: KnockoutObservable<number>;
        total: KnockoutObservable<number>;
        rounding: KnockoutObservable<number>;
        fraction: KnockoutObservable<number>;
        order: KnockoutObservable<number>;
        roundingItems: KnockoutObservableArray<any>;
        fractionItems: KnockoutObservableArray<any>;
        attrEnable: KnockoutObservable<boolean>;
        settingMethodEnable: KnockoutObservable<boolean>;
        totalEnable: KnockoutObservable<boolean>;
        formBuilt: any;
        formTime: any;
        formPeople: any;
        amount: any;
        numerical: any;
        unitPrice: any;
        
        constructor(param: ICalculatorItem) {
            var self = this;
            self.isChecked = ko.observable(param.isChecked);
            self.itemCd = ko.observable(param.itemCd);
            self.attribute = ko.observable(param.attribute);
            self.itemName = ko.observable(param.itemName);
            self.settingMethod = ko.observable(param.settingMethod);
            self.formula = ko.observable(param.formula);
            self.displayAtr = ko.observable(param.displayAtr);
            self.total = ko.observable(param.total);
            self.rounding = ko.observable(param.rounding);
            self.fraction = ko.observable(param.fraction);
            self.order = ko.observable(param.order);
            self.roundingItems = ko.observableArray([
                { roundingCode: 0, roundingName: nts.uk.resource.getText("Enum_RoundingTime_1Min") },
                { roundingCode: 1, roundingName: nts.uk.resource.getText("Enum_RoundingTime_5Min") },
                { roundingCode: 2, roundingName: nts.uk.resource.getText("Enum_RoundingTime_6Min") },
                { roundingCode: 3, roundingName: nts.uk.resource.getText("Enum_RoundingTime_10Min") },
                { roundingCode: 4, roundingName: nts.uk.resource.getText("Enum_RoundingTime_15Min") },
                { roundingCode: 5, roundingName: nts.uk.resource.getText("Enum_RoundingTime_20Min") },
                { roundingCode: 6, roundingName: nts.uk.resource.getText("Enum_RoundingTime_30Min") },
                { roundingCode: 7, roundingName: nts.uk.resource.getText("Enum_RoundingTime_60Min") }
            ]);
            self.fractionItems = ko.observableArray([
                { fractionCode: 0, fractionName: nts.uk.resource.getText("Enum_Rounding_Down") },
                { fractionCode: 1, fractionName: nts.uk.resource.getText("Enum_Rounding_Up") },
                { fractionCode: 2, fractionName: nts.uk.resource.getText("Enum_Rounding_Down_Over") }
            ]);
            
            self.attrEnable = ko.observable(param.attrEnable);
            self.settingMethodEnable = ko.observable(param.settingMethodEnable);
            self.totalEnable = ko.observable(param.totalEnable);
            
            self.isChecked.subscribe(function(value) {
                if(!value) {
                    nts.uk.ui._viewModel.content.viewmodelA.allSelectedItems(false);
                }
            });  
            
            self.attribute.subscribe(function(value) {
                if(value == 0) {
                    self.roundingItems([
                        { roundingCode: 0, roundingName: nts.uk.resource.getText("Enum_RoundingTime_1Min") },
                        { roundingCode: 1, roundingName: nts.uk.resource.getText("Enum_RoundingTime_5Min") },
                        { roundingCode: 2, roundingName: nts.uk.resource.getText("Enum_RoundingTime_6Min") },
                        { roundingCode: 3, roundingName: nts.uk.resource.getText("Enum_RoundingTime_10Min") },
                        { roundingCode: 4, roundingName: nts.uk.resource.getText("Enum_RoundingTime_15Min") },
                        { roundingCode: 5, roundingName: nts.uk.resource.getText("Enum_RoundingTime_20Min") },
                        { roundingCode: 6, roundingName: nts.uk.resource.getText("Enum_RoundingTime_30Min") },
                        { roundingCode: 7, roundingName: nts.uk.resource.getText("Enum_RoundingTime_60Min") }
                    ]);
                    
                    self.fractionItems([
                        { fractionCode: 0, fractionName: nts.uk.resource.getText("Enum_Rounding_Down") },
                        { fractionCode: 1, fractionName: nts.uk.resource.getText("Enum_Rounding_Up") },
                        { fractionCode: 2, fractionName: nts.uk.resource.getText("Enum_Rounding_Down_Over") }
                    ]);
                } else {
                    self.roundingItems([
                        { roundingCode: 0, roundingName: nts.uk.resource.getText("Enum_Unit_NONE") },
                        { roundingCode: 1, roundingName: nts.uk.resource.getText("Enum_Unit_Int_1_Digits") },
                        { roundingCode: 2, roundingName: nts.uk.resource.getText("Enum_Unit_Int_2_Digits") },
                        { roundingCode: 3, roundingName: nts.uk.resource.getText("Enum_Unit_Int_3_Digits") },
                        { roundingCode: 4, roundingName: nts.uk.resource.getText("Enum_Unit_Int_4_Digits") },
                        { roundingCode: 5, roundingName: nts.uk.resource.getText("Enum_Unit_Int_5_Digits") },
                        { roundingCode: 6, roundingName: nts.uk.resource.getText("Enum_Unit_Int_6_Digits") },
                        { roundingCode: 7, roundingName: nts.uk.resource.getText("Enum_Unit_Int_7_Digits") },
                        { roundingCode: 8, roundingName: nts.uk.resource.getText("Enum_Unit_Int_8_Digits") },
                        { roundingCode: 9, roundingName: nts.uk.resource.getText("Enum_Unit_Int_9_Digits") },
                        { roundingCode: 10, roundingName: nts.uk.resource.getText("Enum_Unit_Int_10_Digits") },
                        { roundingCode: 11, roundingName: nts.uk.resource.getText("Enum_Unit_Int_11_Digits") },
                        { roundingCode: 12, roundingName: nts.uk.resource.getText("Enum_Unit_Decimal_1st") },
                        { roundingCode: 13, roundingName: nts.uk.resource.getText("Enum_Unit_Decimal_2nd") },
                        { roundingCode: 14, roundingName: nts.uk.resource.getText("Enum_Unit_Decimal_3rd") }
                    ]);
                    
                    self.fractionItems([
                        { fractionCode: 0, fractionName: nts.uk.resource.getText("Enum_Rounding_Truncation") },
                        { fractionCode: 1, fractionName: nts.uk.resource.getText("Enum_Rounding_Round_Up") },
                        { fractionCode: 2, fractionName: nts.uk.resource.getText("Enum_Rounding_Down_4_Up_5") }
                    ]);
                }
            });  
            
            self.formBuilt = param.formBuilt;
            self.formTime = param.formTime;
            self.formPeople = param.formPeople;
            self.amount = param.amount;
            self.numerical = param.numerical;
            self.unitPrice = param.unitPrice;
        }
    }
        
    export interface ICalculatorItem {
        isChecked: boolean;
        itemCd: string;
        attribute: number;
        itemName: string;
        settingMethod: number;
        formula: string;
        displayAtr: number;
        total: number;
        rounding: number;
        fraction: number;
        order: number;
        roundingItems: Array<any>;
        fractionItems: Array<any>;
        attrEnable: boolean;
        settingMethodEnable: boolean;
        totalEnable: boolean;
        formBuilt: any;
        formTime: any;
        formPeople: any;
        amount: any;
        numerical: any;
        unitPrice: any;
    }
}