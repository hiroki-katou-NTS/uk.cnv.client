module nts.uk.at.view.kmf003.a.viewmodel {
    export class ScreenModel {
        //Grid data
        columns: KnockoutObservable<any>;
        singleSelectedCode: KnockoutObservableArray<any>;;
        items: KnockoutObservableArray<ItemModel>;
        currentCode: KnockoutObservable<any>;
        
        //Top input form
        code: KnockoutObservable<string>;
        name: KnockoutObservable<string>;
        useConditionCls: KnockoutObservable<boolean>;  
        grantDate: KnockoutObservable<string>;
        A6_2Data: KnockoutObservableArray<any>;
        A6_2SelectedRuleCode: any;
        A7_4Data: KnockoutObservableArray<any>;
        A7_4SelectedRuleCode: any;
        symbols: KnockoutObservable<string>;
        limitedValue01: KnockoutObservable<string>;
        limitedValue02: KnockoutObservable<string>;
        limitedValue03: KnockoutObservable<string>;
        limitedValue04: KnockoutObservable<string>;
        limitedValue05: KnockoutObservable<string>;
        
        //Bottom input form
        useCls02: KnockoutObservable<boolean>;
        useCls03: KnockoutObservable<boolean>;
        useCls04: KnockoutObservable<boolean>;
        useCls05: KnockoutObservable<boolean>;            
        conditionValue01: KnockoutObservable<string>;
        conditionValue02: KnockoutObservable<string>;
        conditionValue03: KnockoutObservable<string>;
        conditionValue04: KnockoutObservable<string>;
        conditionValue05: KnockoutObservable<string>;
        note: KnockoutObservable<string>;
        conditionValue02Enable: KnockoutObservable<boolean>;
        conditionValue03Enable: KnockoutObservable<boolean>;
        conditionValue04Enable: KnockoutObservable<boolean>;
        conditionValue05Enable: KnockoutObservable<boolean>;
        btnSetting02Enable: KnockoutObservable<boolean>;
        btnSetting03Enable: KnockoutObservable<boolean>;
        btnSetting04Enable: KnockoutObservable<boolean>;
        btnSetting05Enable: KnockoutObservable<boolean>;

        constructor() {
            var self = this;
            
            //Grid data
            self.items = ko.observableArray([
                new ItemModel('01', 'qwe'),
                new ItemModel('02', 'www'),
                new ItemModel('03', 'sasdsa'),
                new ItemModel('04', 'asdasd'),
                new ItemModel('05', '324w'),
                new ItemModel('06', 'bbbb'),
                new ItemModel('07', 'qwe'),
                new ItemModel('08', 'www'),
                new ItemModel('09', 'sasdsa'),
                new ItemModel('10', 'asdasd'),
                new ItemModel('11', '324w'),
                new ItemModel('12', 'bbbb')
            ]);
            self.columns = ko.observableArray([
                { headerText: nts.uk.resource.getText("KMF003_8"), prop: 'code', width: 50 },
                { headerText: nts.uk.resource.getText("KMF003_9"), prop: 'name', width: 200 }
            ]);
            self.singleSelectedCode = ko.observableArray([]);
            self.currentCode = ko.observable();
            
            //Controls display
            self.controlsDisplay();
            
            //Enable or disable for setting form
            self.conditionSettingForm();
        }

        /**
         * Start page.
         */
        start(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            
            dfd.resolve();

            return dfd.promise();
        }
        
        /**
         * Clear data input on form
         */
        cleanForm(){
            
        }
        
        /**
         * Save data to db
         */
        addFunction(){
            
        }
        
        /**
         * Delete data by code
         */
        deleteFunction(){
            
        }
        
        /**
         * 
         */
        openBDialog(conditionNo: number) {
            let self = this;
            nts.uk.ui.windows.setShared("KMF003_CONDITION_NO", conditionNo);
            nts.uk.ui.windows.sub.modal("/view/kmf/003/b/index.xhtml").onClosed(() => {
                
            });    
        }
        
        /**
         * Form controls display.
         */
        controlsDisplay() {
            var self = this;
            
            //Top input form
            self.code = ko.observable("");
            self.name = ko.observable("");              
            self.useConditionCls = ko.observable(false);            
            self.grantDate = ko.observable("");            
            self.A6_2Data = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("KMF003_17") },
                { code: '1', name: nts.uk.resource.getText("KMF003_18") }
            ]);
            self.A6_2SelectedRuleCode = ko.observable(0);            
            self.A7_4Data = ko.observableArray([
                { code: '0', name: nts.uk.resource.getText("KMF003_21") },
                { code: '1', name: nts.uk.resource.getText("KMF003_22") }
            ]);
            self.A7_4SelectedRuleCode = ko.observable(0);
            self.symbols = ko.observable("%");
            self.limitedValue01 = ko.observable("100");
            self.limitedValue02 = ko.observable("");
            self.limitedValue03 = ko.observable("");
            self.limitedValue04 = ko.observable("");
            self.limitedValue05 = ko.observable("");
            
            //Bottom input form
            self.useCls02 = ko.observable(false);
            self.useCls03 = ko.observable(false);
            self.useCls04 = ko.observable(false);
            self.useCls05 = ko.observable(false);         
            self.conditionValue01 = ko.observable("");
            self.conditionValue02 = ko.observable("");
            self.conditionValue03 = ko.observable("");
            self.conditionValue04 = ko.observable("");
            self.conditionValue05 = ko.observable("");
            self.note = ko.observable("");
            self.conditionValue02Enable = ko.observable(false);
            self.conditionValue03Enable = ko.observable(false);
            self.conditionValue04Enable = ko.observable(false);
            self.conditionValue05Enable = ko.observable(false);
            self.btnSetting02Enable = ko.observable(false);
            self.btnSetting03Enable = ko.observable(false);
            self.btnSetting04Enable = ko.observable(false);
            self.btnSetting05Enable = ko.observable(false); 
        }
        
        /**
         * Enable or disable for setting form.
         */
        conditionSettingForm() {
            var self = this;
            
            self.A7_4SelectedRuleCode.subscribe(function(value) {
                if(value == 0){
                    self.symbols("%");
                    self.limitedValue01("100");
                    self.limitedValue02("");
                    self.limitedValue03("");
                    self.limitedValue04("");
                    self.limitedValue05("");
                    self.setConditionValueChanges();
                } else if(value == 1) {
                    self.symbols("日");
                    self.limitedValue01("366");
                    self.limitedValue02("");
                    self.limitedValue03("");
                    self.limitedValue04("");
                    self.limitedValue05("");
                    self.setConditionValueChanges();
                }
            });
            
            self.useCls02.subscribe(function(value) {
                if(value == true){
                    self.conditionValue02Enable(true);
                    self.btnSetting02Enable(true);
                    self.setConditionValues(Number(self.conditionValue01()), 2);
                } else {
                    self.conditionValue02Enable(false);
                    self.btnSetting02Enable(false);
                }
            });
            
            self.useCls03.subscribe(function(value) {
                if(value == true){
                    self.conditionValue03Enable(true);
                    self.btnSetting03Enable(true);
                    self.setConditionValues(Number(self.conditionValue02()), 3);
                } else {
                    self.conditionValue03Enable(false);
                    self.btnSetting03Enable(false);
                }
            });
            
            self.useCls04.subscribe(function(value) {
                if(value == true){
                    self.conditionValue04Enable(true);
                    self.btnSetting04Enable(true);
                    self.setConditionValues(Number(self.conditionValue03()), 4);
                } else {
                    self.conditionValue04Enable(false);
                    self.btnSetting04Enable(false);
                }
            });
            
            self.useCls05.subscribe(function(value) {
                if(value == true){
                    self.conditionValue05Enable(true);
                    self.btnSetting05Enable(true);
                    self.setConditionValues(Number(self.conditionValue04()), 5);
                } else {
                    self.conditionValue05Enable(false);
                    self.btnSetting05Enable(false);
                }
            });
        }
        
        /**
         * Set condition values.
         */
        setConditionValues(value: number, position: number) {
            var self = this;
            var result = value - 1;
            
            if(position == 2) {
                self.limitedValue02(result.toString());
            } else if (position == 3) {
                self.limitedValue03(result.toString());
            } else if (position == 4) {
                self.limitedValue04(result.toString());
            } else if (position == 5) {
                self.limitedValue05(result.toString());
            }
        }
        
        /**
         * Set condition values when change condition type.
         */
        setConditionValueChanges() {
            var self = this;
            var result02 = Number(self.conditionValue01()) - 1;
            var result03 = Number(self.conditionValue02()) - 1;
            var result04 = Number(self.conditionValue03()) - 1;
            var result05 = Number(self.conditionValue04()) - 1;
            
            self.limitedValue02(result02 <= 0 ? "" : result02.toString());
            self.limitedValue03(result03 <= 0 ? "" : result03.toString());
            self.limitedValue04(result04 <= 0 ? "" : result04.toString());
            self.limitedValue05(result05 <= 0 ? "" : result05.toString());
        }
    }
    
    class ItemModel {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;       
        }
    } 
}
