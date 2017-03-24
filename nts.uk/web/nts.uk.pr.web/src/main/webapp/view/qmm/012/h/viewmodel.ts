module qmm012.h.viewmodel {
    export class ScreenModel {
        //textediter
       
        selectedCode: KnockoutObservable<string>;
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        //gridlist
        gridListItems: KnockoutObservableArray<GridItemModel>;
        columns: KnockoutObservableArray<any>;
        gridListCurrentCode: KnockoutObservable<any>;
        currentCodeList: KnockoutObservableArray<any>;
        //Switch
        roundingRules_H_SEL_001: KnockoutObservableArray<any>;
        roundingRules_H_SEL_002: KnockoutObservableArray<any>;

        enable: KnockoutObservable<boolean> = ko.observable(true);

        CurrentItemMaster: KnockoutObservable<qmm012.b.service.model.ItemMaster> = ko.observable(null);
        CurrentCategoryAtrName: KnockoutObservable<string> = ko.observable('');
        CurrentItem: KnockoutObservable<service.model.ItemSalaryPeriod> = ko.observable(null);
        CurrentItemCode: KnockoutObservable<string> = ko.observable('');
        CurrentPeriodAtr: KnockoutObservable<number> = ko.observable(0);
        CurrentStrY: KnockoutObservable<number> = ko.observable(0);
        CurrentEndY: KnockoutObservable<number> = ko.observable(0);
        CurrentCycleAtr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle01Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle02Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle03Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle04Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle05Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle06Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle07Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle08Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle09Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle10Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle11Atr: KnockoutObservable<number> = ko.observable(0);
        CurrentCycle12Atr: KnockoutObservable<number> = ko.observable(0);
        constructor() {
            var self = this;
            //set Switch Data
            self.roundingRules_H_SEL_001 = ko.observableArray([
                { code: 1, name: '設定する' },
                { code: 0, name: '設定しない' }
            ]);
            //005 006 007 008 009 010

            self.roundingRules_H_SEL_002 = ko.observableArray([
                { code: 1, name: 'する' },
                { code: 0, name: 'しない' }
            ]);

            self.CurrentItemMaster(nts.uk.ui.windows.getShared('itemMaster'));
            if (self.CurrentItemMaster()) {
                if (self.CurrentItemMaster().categoryAtr == 0) {
                    service.findItemSalaryPeriod(self.CurrentItemMaster().itemCode).done(function(ItemSalary: service.model.ItemSalaryPeriod) {
                        self.CurrentItem(ItemSalary);
                    }).fail(function(res) {
                        // Alert message
                        alert(res);
                    });
                }
                if (self.CurrentItemMaster().categoryAtr == 1) {
                    service.findItemDeductPeriod(self.CurrentItemMaster().itemCode).done(function(ItemDeduct: service.model.ItemDeductPeriod) {
                        self.CurrentItem(ItemDeduct);
                    }).fail(function(res) {
                        // Alert message
                        alert(res);
                    });
                }
                self.CurrentCategoryAtrName(self.CurrentItemMaster().categoryAtrName);
            }
            self.CurrentItem.subscribe(function(ItemSalaryPeriod: service.model.ItemSalaryPeriod) {
                self.CurrentItemCode(ItemSalaryPeriod ? ItemSalaryPeriod.itemCd : '');
                self.CurrentPeriodAtr(ItemSalaryPeriod ? ItemSalaryPeriod.periodAtr : 0);
                self.CurrentStrY(ItemSalaryPeriod ? ItemSalaryPeriod.strY : 0);
                self.CurrentEndY(ItemSalaryPeriod ? ItemSalaryPeriod.endY : 0);
                self.CurrentCycleAtr(ItemSalaryPeriod ? ItemSalaryPeriod.cycleAtr : 0);

                self.CurrentCycle01Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle01Atr : 0);
                self.CurrentCycle02Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle02Atr : 0);
                self.CurrentCycle03Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle03Atr : 0);
                self.CurrentCycle04Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle04Atr : 0);
                self.CurrentCycle05Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle05Atr : 0);
                self.CurrentCycle06Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle06Atr : 0);
                self.CurrentCycle07Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle07Atr : 0);
                self.CurrentCycle08Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle08Atr : 0);
                self.CurrentCycle09Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle09Atr : 0);
                self.CurrentCycle10Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle10Atr : 0);
                self.CurrentCycle11Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle11Atr : 0);
                self.CurrentCycle12Atr(ItemSalaryPeriod ? ItemSalaryPeriod.cycle12Atr : 0);

            });

        }

        SubmitDialog() {
            nts.uk.ui.windows.close();
        }
        CloseDialog() {
            nts.uk.ui.windows.close();
        }
    }
    class GridItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }

    }
    class ComboboxItemModel {
        code: string;
        name: string;
        label: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
    class BoxModel {
        id: number;
        name: string;
        constructor(id, name) {
            var self = this;
            self.id = id;
            self.name = name;
        }
    }


}