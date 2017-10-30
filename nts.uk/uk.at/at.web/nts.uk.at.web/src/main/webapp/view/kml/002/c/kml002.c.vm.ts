module nts.uk.at.view.kml002.c.viewmodel {
    export class ScreenModel {
        columns: KnockoutObservable<any>;
        currentCodeList: KnockoutObservableArray<any>;
        allItem: KnockoutObservableArray<ItemModel>;
        items: KnockoutObservableArray<ItemModel>;
        categoryItems: KnockoutObservableArray<any>;
        catCode: KnockoutObservable<number>;
        checked: KnockoutObservable<boolean>;
        enable: KnockoutObservable<boolean>;
        rightItemcolumns: KnockoutObservable<any>;
        currentRightCodeList: KnockoutObservableArray<any>;
        rightItems: KnockoutObservableArray<NewItemModel>;
        attrLabel: KnockoutObservable<String>;
        itemNameLabel: KnockoutObservable<String>;
        enableReturn: KnockoutObservable<boolean>;
        
        constructor() {
            var self = this;
            
            var data = nts.uk.ui.windows.getShared("KML002_A_DATA");
            
            self.attrLabel = ko.observable(data.attribute);
            self.itemNameLabel = ko.observable(data.itemName);
            
            self.items = ko.observableArray([]);
            self.allItem = ko.observableArray([]);
            self.rightItems = ko.observableArray([]);
            
            self.columns = ko.observableArray([
                { headerText: nts.uk.resource.getText("KML002_7"), prop: 'code', width: 50, hidden: true },
                { headerText: nts.uk.resource.getText("KML002_7"), prop: 'name', width: 200, formatter: _.escape }
            ]);
            
            self.currentCodeList = ko.observableArray([]);
            
            self.categoryItems = ko.observableArray([
                { catCode: 0, catName: nts.uk.resource.getText("KML002_29") },
                { catCode: 1, catName: nts.uk.resource.getText("KML002_32") }
            ]);
            
            self.rightItemcolumns = ko.observableArray([
                { headerText: nts.uk.resource.getText("KML002_7"), prop: 'code', width: 50, hidden: true },
                { headerText: nts.uk.resource.getText("KML002_36"), prop: 'operatorAtr', width: 80 },
                { headerText: nts.uk.resource.getText("KML002_7"), prop: 'name', width: 200, formatter: _.escape }
            ]);
            
            self.currentRightCodeList = ko.observableArray([]);
            
            self.catCode = ko.observable(0);
            
            self.checked = ko.observable(true);
            self.enable = ko.observable(true);
            
            if(self.catCode() === 0) {
                self.enable(true);
            } else {
                self.enable(false);
            }
            
            self.checked.subscribe(function(value) {
                self.displayItemsRule(self.allItem(), self.catCode(), value);
            }); 
            
            self.catCode.subscribe(function(value) {
                self.displayItemsRule(self.allItem(), value, false);
            }); 
            
            self.enableReturn = ko.observable(true);
            
            if(self.rightItems().length >= 1) {
                self.enableReturn(true);
            } else {
                self.enableReturn(false);
            }
        }

        /**
         * Start page.
         */
        start(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            
            $.when(self.getData()).done(function() {
                 
                if (self.allItem().length > 0) {
                    self.displayItemsRule(_.clone(self.allItem()), self.catCode(), self.checked());
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
            self.allItem([]);
            
            var data = nts.uk.ui.windows.getShared("KML002_A_DATA");
            var dailyAttendanceAtrs = [];
            dailyAttendanceAtrs.push(5);
            var param = {
                dailyAttendanceItemAtrs: dailyAttendanceAtrs ,
                scheduleAtr: data.attributeId,
                budgetAtr: data.attributeId,
                unitAtr: 0
            };
            service.getDailyItems(param).done(function(data) {
                let temp = [];
                let items = _.sortBy(data, ['companyId', 'dispOrder']);
                
                _.forEach(items, function(item: service.BaseItemsDto) {
                    var name = item.itemName + nts.uk.resource.getText("KML002_43");
                    temp.push(new ItemModel(item.id, name, item.itemType));
                });
                
                self.allItem(temp);
                
                dfd.resolve(data);
            }).fail(function(res) {
                dfd.reject(res);    
            });
            
            return dfd.promise();
        }
        
        /**
         * Display items rule.
         */
        displayItemsRule(allItems: any, category: number, display: boolean) {
            let self = this;
            let temp = [];
            
            if(category == 0 && display) {
                self.items(_.filter(allItems, ['itemType', 0]));
            } else if (category == 0 && !display) {
                self.items(_.filter(allItems, function(item: ItemModel) {
                    return item.itemType == 0 || item.itemType == 1;
                }));
            } else if (category == 1) {
                self.items(_.filter(allItems, ['itemType', 2]));
            }      
        }
        
        /**
         * Addition function.
         */
        addition() {
            let self = this;
            
            if(self.currentCodeList().length + self.rightItems().length > 100) {
                nts.uk.ui.dialog.info({ messageId: "Msg_195" });
            } else {
                _.forEach(self.currentCodeList(), function(item){
                    var item = _.find(self.items(), function(o) { return o.code == item.toString(); });
                    
                    let i = self.rightItems().length;
                    
                    self.rightItems.push({
                        code: i.toString(),
                        operatorAtr: nts.uk.resource.getText("KML002_37"),
                        name: item.name,    
                    });
                    
                    i = i + 1;
                });
            }
            
            if(self.rightItems().length >= 1) {
                self.enableReturn(true);
            } else {
                self.enableReturn(false);
            }
        }
        
        /**
         * Subtraction function.
         */
        subtraction() {
            let self = this;
            
            if(self.currentCodeList().length + self.rightItems().length > 100) {
                nts.uk.ui.dialog.info({ messageId: "Msg_195" });
            } else {
                _.forEach(self.currentCodeList(), function(item){
                    var item = _.find(self.items(), function(o) { return o.code == Number(item); });
                    
                    let i = self.rightItems().length;
                    
                    self.rightItems.push({
                        code: i.toString(),
                        operatorAtr: nts.uk.resource.getText("KML002_38"),
                        name: item.name,    
                    });
                    
                    i = i + 1;
                });
            }
            
            if(self.rightItems().length >= 1) {
                self.enableReturn(true);
            } else {
                self.enableReturn(false);
            }
        }
        
        /**
         * Return items.
         */
        returnItems() {
            let self = this;
            let array =[];

            self.rightItems(_.filter(self.rightItems(), function(item) {
                return _.indexOf(self.currentRightCodeList(), item.code) < 0;
            }));
            
            for(let i = 0; i < self.rightItems().length; i++){
                self.rightItems()[i].code = i.toString();    
            }
            
            self.currentRightCodeList.removeAll();
            self.rightItems(self.rightItems());
            
            if(self.rightItems().length >= 1) {
                self.enableReturn(true);
            } else {
                self.enableReturn(false);
            }
        }
        
        /**
         * Submit data to A screen.
         */
        submit() {
            var self = this;
            
            nts.uk.ui.windows.close();
        }
        
        /**
         * Close dialog.
         */
        cancel() {
            nts.uk.ui.windows.close();
        }
    }
    
    class ItemModel {
        code: string;
        name: string;
        itemType: number;
        constructor(code: string, name: string, itemType: number) {
            this.code = code;
            this.name = name;  
            this.itemType = itemType;  
        }
    } 
    
    class NewItemModel {
        code: string;
        operatorAtr: string;
        name: string;
        constructor(code: string, operatorAtr: string, name: string) {
            this.code = code;
            this.operatorAtr = operatorAtr;
            this.name = name;       
        }
    } 
}