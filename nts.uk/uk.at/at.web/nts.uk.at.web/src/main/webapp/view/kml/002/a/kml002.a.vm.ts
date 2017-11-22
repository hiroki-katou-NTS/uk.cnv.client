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
        allItemsData: any;
        dailyItems: any;
        peopleItems: any;
        
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
                { code: '0', name: nts.uk.resource.getText("Enum_IncludeAtr_Include") },
                { code: '1', name: nts.uk.resource.getText("Enum_IncludeAtr_Exclude") }
            ]);
            
            self.workScheduleSelected = ko.observable(0); 
            
            //A3_6 + A3_7
            self.units = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("Enum_Unit_DAILY") },
                { code: '1', name: nts.uk.resource.getText("Enum_Unit_BY_TIME_ZONE") }
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
            
            self.dailyItems = [];
            self.peopleItems = [];
            
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
                                total: data.verticalCalItems[i].cumulativeAtr,
                                rounding: data.verticalCalItems[i].rounding,
                                fraction: data.verticalCalItems[i].roundingProcessing,
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
                        self.allItemsData = sortedItems;
                        
                        for(var i = 0; i < sortedItems.length; i++) {
                            var curDataItem = null;
                            var beforeFormula = "";
                            
                            if(sortedItems[i].formBuilt != null) {
                                curDataItem = sortedItems[i].formBuilt
                            } else if(sortedItems[i].formTime != null) {
                                curDataItem = sortedItems[i].formTime
                            } else if(sortedItems[i].formPeople != null) {
                                curDataItem = sortedItems[i].formPeople
                            } else if(sortedItems[i].amount != null) {
                                curDataItem = sortedItems[i].amount
                            } else if(sortedItems[i].numerical != null) {
                                curDataItem = sortedItems[i].numerical
                            } else if(sortedItems[i].unitPrice != null) {
                                curDataItem = sortedItems[i].unitPrice
                            }
                            
                            if(i > 0) {
                                beforeFormula = sortedItems[i - 1].formula();
                            }
                            
                            var formulaResult = self.formulaGeneration(sortedItems[i].itemName(), sortedItems[i].settingMethod(), sortedItems[i].attribute(), 
                                                    i, curDataItem, beforeFormula, true);
                            sortedItems[i].formula(formulaResult);
                        }
                        
                        self.calculatorItems(sortedItems);
                    }).fail(function(res) {
                          
                    });
                }
            });
            
            self.allSelectedItems.subscribe(function(value) {
                var items = [];
                var flag = false;
                
                if(value == true) {
                    for(var i = 0; i < self.calculatorItems().length; i++) {
                        if(i == 0) {
                            flag = true;
                        }
                        
                        var item : ICalculatorItem = {
                            isChecked: true,
                            itemCd: self.calculatorItems()[i].itemCd(),
                            attribute: self.calculatorItems()[i].attribute(),
                            itemName: self.calculatorItems()[i].itemName(),
                            settingMethod: self.calculatorItems()[i].settingMethod(),
                            settingMethodEnable: flag == false ? false : self.calculatorItems()[i].settingMethodEnable(),
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
                            unitPrice: self.calculatorItems()[i].unitPrice,
                            attrEnable: self.calculatorItems()[i].attrEnable(),
                            totalEnable: self.calculatorItems()[i].totalEnable()
                        };
                        
                        items.push(new CalculatorItem(item));
                    }
                    
                    self.calculatorItems([]);
                    self.calculatorItems(items);
                } else {
                    //Just remove check all checkbox
                    var flag = self.checkSelectedItems();
                    var isFirst = false;
                    
                    if(!flag) {
                        //Remove all selected checkbox
                        for(var i = 0; i < self.calculatorItems().length; i++) {
                            if(i == 0) {
                                isFirst = true;
                            }
                                
                            var item : ICalculatorItem = {
                                isChecked: false,
                                itemCd: self.calculatorItems()[i].itemCd(),
                                attribute: self.calculatorItems()[i].attribute(),
                                itemName: self.calculatorItems()[i].itemName(),
                                settingMethod: self.calculatorItems()[i].settingMethod(),
                                settingMethodEnable: isFirst == false ? false : self.calculatorItems()[i].settingMethodEnable(),
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
                                unitPrice: self.calculatorItems()[i].unitPrice,
                                attrEnable: self.calculatorItems()[i].attrEnable(),
                                totalEnable: self.calculatorItems()[i].totalEnable()
                            };
                            
                            items.push(new CalculatorItem(item));
                        }
                        
                        self.calculatorItems([]);
                        self.calculatorItems(items);
                    }
                }
            });  
        }

        /**
         * Start page.
         */
        start(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            
            $.when(self.getData(), self.getDailyItems(), self.getPeopleItems()).done(function() {
                                
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
         * Get C screen data.
         */
        getDailyItems() {
            var self = this;
            var dfd = $.Deferred();
            self.dailyItems = [];
            
            var dailyAttendanceAtrs = [];
            dailyAttendanceAtrs.push(5);
            var param = {
                dailyAttendanceItemAtrs: dailyAttendanceAtrs ,
                scheduleAtr: 0,
                budgetAtr: 0,
                unitAtr: 0
            };
            service.getDailyItems(param).done(function(data) {
                let temp = [];
                let items = _.sortBy(data, ['companyId', 'dispOrder']);
                
                _.forEach(items, function(item: service.BaseItemsDto) {
                    var name = item.itemName + nts.uk.resource.getText("KML002_43");
                    temp.push({id: item.id, name: name, itemType: item.itemType});
                });
                
                self.dailyItems = temp;
                
                dfd.resolve(data);
            }).fail(function(res) {
                dfd.reject(res);    
            });
            
            return dfd.promise();    
        }
        
        /**
         * Get D screen data.
         */
        getPeopleItems() {
            var self = this;
            var dfd = $.Deferred();
            
            let param = {
                budgetAtr: 1,   
                // received from mother screen 0: day or 1: time
                unitAtr: 0
            }
            
            service.getByAtr(param).done((data) => {  
                var temp = [];
                let a = {
                    budgetAtr: 1,
                    externalBudgetCode: (data.length+1).toString(),
                    externalBudgetName: nts.uk.resource.getText("KML002_109"),
                    unitAtr: 0
                }
                let b = {
                    budgetAtr: 1,
                    externalBudgetCode: (data.length+2).toString(),
                    externalBudgetName: nts.uk.resource.getText("KML002_110"),
                    unitAtr: 0
                }
                
                temp.push(a);
                temp.push(b);
                
                _.forEach(data, function(item){
                    temp.push(item);
                });
                
                self.peopleItems = temp;
                
                dfd.resolve(data);
            }).fail(function(res) {
                dfd.reject(res);    
            });
            
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
                formula: '',
                displayAtr: 0,
                total: 0,
                rounding: 0,
                fraction: 0,
                order: 1,
                attrEnable: true,
                settingMethodEnable: false,
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
         * Formula filter.
         */
        formularFilter() {
            let self = this;
            
            
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
                    cumulativeAtr: self.calculatorItems()[i].total(),
                    attributes: self.calculatorItems()[i].attribute(),
                    rounding: self.calculatorItems()[i].rounding(),
                    roundingProcessing: self.calculatorItems()[i].fraction(),
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
            var flag = false;
            
            if(self.calculatorItems().length > 0) {
                flag = true;
            }
            
            var item : ICalculatorItem = {
                isChecked: false,
                itemCd: nts.uk.util.randomId(),
                attribute: 0,
                itemName: '',
                settingMethod: 0,
                formula: '',
                displayAtr: 0,
                total: 0,
                rounding: 0,
                fraction: 0,
                order: self.calculatorItems().length + 1,
                attrEnable: true,
                settingMethodEnable: flag,
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
            var flag = false;
            
            for(var i = 0; i < selectedItems.length; i++) {
                if(i > 0) {
                    flag = true;
                }
                
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
                    settingMethodEnable: flag == false ? false : selectedItems[i].settingMethodEnable(),
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
            var prevIdx = -1;
            var $scope = _.clone(self.calculatorItems());
            var temp = [];
            
            var multiRowSelected = _.filter($scope, function(item:CalculatorItem) {
                return item.isChecked();    
            });
            
            _.forEach(multiRowSelected, function(item) {
                var idx = _.findIndex($scope, function(o:CalculatorItem) { return o.itemCd() == item.itemCd(); });
                if (idx-1 === prevIdx) {
                    prevIdx = idx
                } else if (idx > 0) {
                    var itemToMove = $scope.splice(idx, 1)
                    $scope.splice(idx-1, 0, itemToMove[0]);
                }
            });
            
            _.forEach($scope, function(item, index) {
                item.order(index + 1);
                temp.push(item);
            });
            
            self.calculatorItems(temp);
        }
        
        /**
         * Move down calculator item.
         */
        downBtn() {
            var self = this;
            var $scope = _.clone(self.calculatorItems());
            var prevIdx = $scope.length;
            var temp = [];
            
            var multiRowSelected = _.filter($scope, function(item:CalculatorItem) {
                return item.isChecked();    
            });
            
            var revPerson = multiRowSelected.concat();
            revPerson.reverse();
            
            for(var i = 0; i < revPerson.length; i++) {
                var item = revPerson[i];
                var idx = _.findIndex($scope, function(o:CalculatorItem) { return o.itemCd() == item.itemCd(); });
                if (idx + 1 === prevIdx) {
                    prevIdx = idx
                } else if (idx < $scope.length - 1) {
                    var itemToMove = $scope.splice(idx, 1)
                    $scope.splice(idx + 1, 0, itemToMove[0]);
                }
            }
            
            _.forEach($scope, function(item, index) {
                item.order(index + 1);
                temp.push(item);
            });
            
            self.calculatorItems(temp);
        }
        
        /**
         * Pass data to dialog.
         */
        passDataToDialogs(itemCd: number, settingMethod: number, attribute: number, itemName: string) {
            var self = this;
            var attrValue = "";
            
            if(attribute == 0 && settingMethod == 0) {
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
                self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);
                nts.uk.ui.windows.sub.modal("/view/kml/002/b/index.xhtml").onClosed(() => {                    
                    self.dataB = nts.uk.ui.windows.getShared("KML002_B_DATA");
                    
                    for(var i = 0; i < self.calculatorItems().length; i++){
                        if(self.dataB == null) {
                            return;
                        }
                        
                        if(self.calculatorItems()[i].itemCd() == self.dataB.verticalCalItemId) {
                            self.calculatorItems()[i].formBuilt = self.dataB;
                            var formulaResult = self.formulaGeneration(itemName, settingMethod, attribute, i, self.dataB, "", false);
                            self.calculatorItems()[i].formula(formulaResult);
                        }
                    }
                }); 
            } else {
                if(attribute == 0) {
                    self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);            
                    nts.uk.ui.windows.sub.modal("/view/kml/002/c/index.xhtml").onClosed(() => {
                        self.dataC = nts.uk.ui.windows.getShared("KML002_C_DATA");
                        
                        for(var i = 0; i < self.calculatorItems().length; i++){
                            if(self.dataC == null) {
                                return;
                            }
                            
                            if(self.calculatorItems()[i].itemCd() == self.dataC.verticalCalItemId) {
                                self.calculatorItems()[i].formTime = self.dataC;
                                var formulaResult = self.formulaGeneration(itemName, settingMethod, attribute, i, self.dataC, "", false);
                                self.calculatorItems()[i].formula(formulaResult);
                            }
                        }
                    }); 
                } else if(attribute == 1) {
                    self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/e/index.xhtml").onClosed(() => {
                        self.dataE = nts.uk.ui.windows.getShared("KML002_E_DATA");
                        
                        for(var i = 0; i < self.calculatorItems().length; i++){
                            if(self.dataE == null) {
                                return;
                            }
                            
                            if(self.calculatorItems()[i].itemCd() == self.dataE.verticalCalItemId) {
                                self.calculatorItems()[i].amount = self.dataE;
                            }
                        }
                    }); 
                } else if(attribute == 2) {
                    self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/d/index.xhtml").onClosed(() => {
                        self.dataD = nts.uk.ui.windows.getShared("KML002_D_Budget");
                        
                        for(var i = 0; i < self.calculatorItems().length; i++){
                            if(self.dataD == null) {
                                return;
                            }
                            
                            if(self.calculatorItems()[i].itemCd() == self.dataD.verticalCalItemId) {
                                self.calculatorItems()[i].formPeople = self.dataD;
                                var formulaResult = self.formulaGeneration(itemName, settingMethod, attribute, i, self.dataD, "", false);
                                self.calculatorItems()[i].formula(formulaResult);
                            }
                        }
                    }); 
                } else if(attribute == 3) {
                    self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/f/index.xhtml").onClosed(() => {
                        self.dataF = nts.uk.ui.windows.getShared("KML002_F_DATA");
                        
                        for(var i = 0; i < self.calculatorItems().length; i++){
                            if(self.dataF == null) {
                                return;
                            }
                            
                            if(self.calculatorItems()[i].itemCd() == self.dataF.verticalCalItemId) {
                                self.calculatorItems()[i].numerical = self.dataF;
                            }
                        }
                    }); 
                } else if(attribute == 4) {
                    self.passDataToDialogs(itemCd, settingMethod, attribute, itemName);
                    nts.uk.ui.windows.sub.modal("/view/kml/002/g/index.xhtml").onClosed(() => {
                        self.dataG = nts.uk.ui.windows.getShared("KML002_G_DATA");
                        for(var i = 0; i < self.calculatorItems().length; i++){
                            if(self.dataG == null) {
                                return;
                            }
                            
                            if(self.calculatorItems()[i].itemCd() == self.dataG.verticalCalItemId) {
                                self.calculatorItems()[i].unitPrice = self.dataG;
                            }
                        }
                        
                    }); 
                }
            }
        }
        
        /**
         * Generate the formula when the dialog setting passed data to A screen.
         */
        formulaGeneration(itemName: string, settingMethod: number, attribute: number, index: number, data: any, beforeFormula: string, isFirstLoad: boolean) {
            let self = this;
            var formulaResult = "";
            
            // 計算式設定
            if(settingMethod == 1) {
                if(data != null) {
                    var operator = "";
                    var text1 = "";
                    var text2 = "";
                    
                    if(data.operatorAtr == 0) { operator = nts.uk.resource.getText("Enum_OperatorAtr_ADD"); }
                    if(data.operatorAtr == 1) { operator = nts.uk.resource.getText("Enum_OperatorAtr_SUBTRACT"); }
                    if(data.operatorAtr == 2) { operator = nts.uk.resource.getText("Enum_OperatorAtr_MULTIPLY"); }
                    if(data.operatorAtr == 3) { operator = nts.uk.resource.getText("Enum_OperatorAtr_DIVIDE"); }
                                        
                    if(data.settingMethod1 == 0) {  
                        if(data.verticalCalItemName1 != null) {
                            text1 = data.verticalCalItemName1;
                        } else {
                            var item = _.find(self.allItemsData, function(o) { return o.itemCd() == data.verticalCalItem1; });
                            text1 = item.itemName();
                        }
                    } else {
                        text1 = data.verticalInputItem1;
                    }
                    
                    if(data.settingMethod2 == 0) {                        
                        if(data.verticalCalItemName2 != null) {
                            text2 = data.verticalCalItemName2;
                        } else {
                            var item = _.find(self.allItemsData, function(o) { return o.itemCd() == data.verticalCalItem2; });
                            text2 = item.itemName();
                        }
                    } else {
                        text2 = data.verticalInputItem2;
                    }
                    
                    if(beforeFormula != "") {
                        formulaResult = beforeFormula + " " + text1 + " " + operator + " " + text2;
                    } else {
                        formulaResult = self.calculatorItems()[index - 1].formula() + " " + text1 + " " + operator + " " + text2;
                    }                    
                } else {
                    formulaResult = nts.uk.resource.getText("KML002_153");
                }
            } else { // 項目選択
                // 平均単価
                if(attribute == 4) {
                    
                } else { // Other attributes
                    // If is first item
                    if(index == 0) {
                        if(attribute == 0) {  
                            if(data.lstFormTimeFunc.length <= 0) {
                                formulaResult = nts.uk.resource.getText("KML002_153");
                            } else {
                                for(var i = 0; i < data.lstFormTimeFunc.length; i++) {
                                    var operator = data.lstFormTimeFunc[i].operatorAtr == 0 ? nts.uk.resource.getText("KML002_37") : nts.uk.resource.getText("KML002_38");
                                    var attendanceName = data.lstFormTimeFunc[i].name != null ? data.lstFormTimeFunc[i].name : "";
                                    var attendanceItem = _.find(self.dailyItems, function(o) { return o.id.slice(0, -1) == data.lstFormTimeFunc[i].attendanceItemId; });
                                    var presetName = data.lstFormTimeFunc[i].name != null ? data.lstFormTimeFunc[i].name : "";
                                    var presetItem = _.find(self.dailyItems, function(o) { return o.id.slice(0, -1) == data.lstFormTimeFunc[i].presetItemId; });
                                    var externalName = data.lstFormTimeFunc[i].name != null ? data.lstFormTimeFunc[i].name : "";
                                    var externalItem = _.find(self.dailyItems, function(o) { return o.id.slice(0, -1) == data.lstFormTimeFunc[i].externalBudgetCd; });
                                    
                                    if(attendanceName != "") {
                                        formulaResult += operator + " " + attendanceName + " ";
                                    } else if(attendanceItem != null) {
                                        formulaResult += operator + " " + attendanceItem.name + " ";
                                    }
                                        
                                    if(presetName != "") {
                                        formulaResult += operator + " " + presetName + " ";
                                    } else if(presetItem != null) {
                                        formulaResult += operator + " " + presetItem.name + " ";
                                    }
                                    
                                    if(externalName != "") {
                                        formulaResult += operator + " " + externalName + " ";
                                    } else if(externalItem != null) {
                                        formulaResult += operator + " " + externalItem.name + " ";
                                    }
                                } 
                            }
                        } else if (attribute == 1) {
                            
                        } else if (attribute == 2) {
                            if(data.lstPeopleFunc.length <= 0) {
                                formulaResult = nts.uk.resource.getText("KML002_153");
                            } else {
                                for(var i = 0; i < data.lstPeopleFunc.length; i++) {
                                    var operator = data.lstPeopleFunc[i].operatorAtr == 0 ? nts.uk.resource.getText("KML002_37") : nts.uk.resource.getText("KML002_38");
                                    var name = data.lstPeopleFunc[i].name != null ? data.lstPeopleFunc[i].name : ""; 
                                    var item = _.find(self.peopleItems, function(o) { return o.externalBudgetCode == data.lstPeopleFunc[i].externalBudgetCd; });
                                    
                                    if(name != "") {
                                        formulaResult += operator + " " + name + " ";
                                    } else if(item != null) {
                                        formulaResult += operator + " " + item.externalBudgetName + " ";
                                    }
                                } 
                            }
                        } else if (attribute == 3) {
                            
                        }
                        
                        if(_.startsWith(formulaResult, nts.uk.resource.getText("KML002_37"))) {
                            formulaResult = formulaResult.substr(1);
                        }
                    } else {
                        var before = "";
                        
                        if(isFirstLoad) {
                            before = beforeFormula;
                        } else {
                            before = self.calculatorItems()[index - 1].formula();
                        }
                        
                        if(attribute == 0) {  
                            if(data.lstFormTimeFunc.length <= 0) {
                                formulaResult = nts.uk.resource.getText("KML002_153");
                            } else {
                                for(var i = 0; i < data.lstFormTimeFunc.length; i++) {
                                    var operator = data.lstFormTimeFunc[i].operatorAtr == 0 ? nts.uk.resource.getText("KML002_37") : nts.uk.resource.getText("KML002_38");
                                    formulaResult += operator + " " + data.lstFormTimeFunc[i].name + " ";
                                } 
                            }
                        } else if (attribute == 1) {
                            
                        } else if (attribute == 2) {
                            if(data.lstPeopleFunc.length <= 0) {
                                formulaResult = nts.uk.resource.getText("KML002_153");
                            } else {
                                for(var i = 0; i < data.lstPeopleFunc.length; i++) {
                                    var operator = data.lstPeopleFunc[i].operatorAtr == 0 ? nts.uk.resource.getText("KML002_37") : nts.uk.resource.getText("KML002_38");
                                    var name = data.lstPeopleFunc[i].name != null ? data.lstPeopleFunc[i].name : ""; 
                                    var item = _.find(self.peopleItems, function(o) { return o.externalBudgetCode == data.lstPeopleFunc[i].externalBudgetCd; });
                                    
                                    if(name != "") {
                                        formulaResult += name + " " + operator + " ";
                                    } else if(item != null) {
                                        formulaResult += item.externalBudgetName + " " + operator + " ";
                                    }
                                } 
                            }
                        } else if (attribute == 3) {
                            
                        }
                        
                        formulaResult = before + " " + formulaResult;
                    }
                }
            }

            return formulaResult.trim();       
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
            self.attribute = ko.observable(0);
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
            self.attribute(param.attribute);
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
