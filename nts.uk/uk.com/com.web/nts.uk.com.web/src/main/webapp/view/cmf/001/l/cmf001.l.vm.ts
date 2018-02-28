module nts.uk.com.view.cmf001.l.viewmodel {
    import model = nts.uk.com.view.cmf001.share.model;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    
    export class ScreenModel {
        constraint: KnockoutObservable<string>;
        inline: KnockoutObservable<boolean>;
        required: KnockoutObservable<boolean>;
        enable: KnockoutObservable<boolean>;
        readonly: KnockoutObservable<boolean>;
        itemList: KnockoutObservableArray<model.ItemModel>;
        selectedCode: KnockoutObservable<number>;
        simpleValue1: KnockoutObservable<any>;
        simpleValue2: KnockoutObservable<any>;
        
        acceptScreenConditionSetting: KnockoutObservable<model.AcceptScreenConditionSetting> = ko.observable(new model.AcceptScreenConditionSetting('条件値', 0 , 20134010102, 25601010134,201801010102, 201801010134, '2018010102', '201501010134', '201401010134', '2012310134', 201877102, 2018019074));
        compareCondition: KnockoutObservable<number>;
        csvItemName: KnockoutObservable<string>;
        timeVal1: KnockoutObservable<string>;
        timeVal2: KnockoutObservable<string>;
        selectedDataType: number;
        screenMode: number;
        
        constructor() {
            var self = this;  
            self.constraint = ko.observable('LayoutCode');
            self.inline = ko.observable(true);
            self.required = ko.observable(true)
            self.enable = ko.observable(true);
            self.readonly = ko.observable(true);
            
            self.itemList = ko.observableArray(model.getCompareListItem());
            
            self.acceptScreenConditionSetting = ko.observable(new model.AcceptScreenConditionSetting('条件値', 0 , null, null, null, null, null, null,null, null, null, null));
            self.selectedCode = ko.observable(0);
            self.csvItemName = getShared("csvItemName");
            let params = getShared("selectedDataType");
            self.selectedDataType = params.dataType;
            self.screenMode = params.screenMode;
            switch(self.selectedDataType) { 
               case 0: { 
                  self.simpleValue1 = ko.observable(self.acceptScreenConditionSetting().numberConditionValue1());
                  self.simpleValue2 = ko.observable(self.acceptScreenConditionSetting().numberConditionValue2());
                  break; 
               } 
               case 1: { 
                  self.simpleValue1 = ko.observable(self.acceptScreenConditionSetting().characterConditionValue1());
                  self.simpleValue2 = ko.observable(self.acceptScreenConditionSetting().characterConditionValue2());
                  break; 
               } 
               case 2: { 
                  self.simpleValue1 = ko.observable(self.acceptScreenConditionSetting().dateConditionValue1());
                  self.simpleValue2 = ko.observable(self.acceptScreenConditionSetting().dateConditionValue2());
                  break; 
               } 
               case 3: { 
                  self.simpleValue1 = ko.observable(self.acceptScreenConditionSetting().timeMomentConditionValue1());
                  self.simpleValue2 = ko.observable(self.acceptScreenConditionSetting().timeMomentConditionValue2());
                  break; 
               }
               case 4: { 
                  self.simpleValue1 = ko.observable(self.acceptScreenConditionSetting().timeConditionValue1());
                  self.simpleValue2 = ko.observable(self.acceptScreenConditionSetting().timeConditionValue2());
                  break; 
               }  
               default: { 
                  break; 
               } 
            }    
        }
        
         /**
         * Close dialog.
         */
        cancelSetting(): void {
            setShared('CMF001lCancel', true);
            nts.uk.ui.windows.close();
        }
        
        saveSetting(){
            alert('tranfer D');
        }
    }
}