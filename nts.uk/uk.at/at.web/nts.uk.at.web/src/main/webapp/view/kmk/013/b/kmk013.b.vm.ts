module nts.uk.at.view.kmk013.b_ref {
    
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        export class ScreenModel {
            itemsB23: KnockoutObservableArray<any>;
            itemsB29: KnockoutObservableArray<any>;
            itemsB215: KnockoutObservableArray<any>;
            selectedB23: KnockoutObservable<number>;
            selectedB29: KnockoutObservable<number>;
            selectedB215: KnockoutObservable<number>;
            enableB217: KnockoutObservable<boolean>;
            readonly: KnockoutObservable<boolean>;
            timeOfDay: KnockoutObservable<number>;
            timeB219: KnockoutObservable<number>;
            timeB221: KnockoutObservable<number>;
            timeB223: KnockoutObservable<number>;
            time2: KnockoutObservable<number>;
            inline: KnockoutObservable<boolean>;
            tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;

            checkedB33: KnockoutObservable<boolean>;
            checkedB34: KnockoutObservable<boolean>;
            checkedB35: KnockoutObservable<boolean>;
            enableB29: KnockoutObservable<boolean>;
            enableB215: KnockoutObservable<boolean>;


            oldData:KnockoutObservable<any>;


            itemListB3_8: KnockoutObservableArray<any>;
            selectedIdB3_8: KnockoutObservable<number>;
            enableB3_8: KnockoutObservable<boolean>;
            itemListB2_28: KnockoutObservableArray<any>;
            selectedIdB2_28: KnockoutObservable<number>;
            enableB2_28: KnockoutObservable<boolean>;
            itemListB2_33: KnockoutObservableArray<any>;
            selectedIdB2_33: KnockoutObservable<number>;
            enableB2_33: KnockoutObservable<boolean>;

            workClass1: KnockoutObservable<any>;
            workClass2: KnockoutObservable<any>;

            conditionDisplay15: KnockoutObservable<boolean>;
            conditionDisplay26: KnockoutObservable<boolean>;
            
            isLoadAfterGetData: KnockoutObservable<boolean>;
            initFlagDone: KnockoutObservable<boolean>;

            // Ver 27
            //Tab 1
            itemsB5_4: KnockoutObservableArray<any> = ko.observableArray([
                new BoxModel(0, nts.uk.resource.getText('KMK013_521')),
                new BoxModel(1, nts.uk.resource.getText('KMK013_522'))
            ]);
            itemsB7_4: KnockoutObservableArray<any> = ko.observableArray([
                new BoxModel(0, nts.uk.resource.getText('KMK013_521')),
                new BoxModel(1, nts.uk.resource.getText('KMK013_522'))
            ]);
            itemsB7_7: KnockoutObservableArray<any> = ko.observableArray([
                new BoxModel(0, nts.uk.resource.getText('KMK013_532')),
                new BoxModel(1, nts.uk.resource.getText('KMK013_533'))
            ]);
            selectedB54: KnockoutObservable<number> = ko.observable(0);
            enableB54: KnockoutObservable<boolean> = ko.observable(true);

            checkedB57: KnockoutObservable<boolean> = ko.observable(false);
            enableB57: KnockoutObservable<boolean> = ko.observable(true);

            checkedB58: KnockoutObservable<boolean> = ko.observable(false);
            enableB58: KnockoutObservable<boolean> = ko.observable(true);

            checkedB510: KnockoutObservable<boolean> = ko.observable(false);
            enableB510: KnockoutObservable<boolean> = ko.observable(true);

            checkedB59: KnockoutObservable<boolean> = ko.observable(false);
            enableB59: KnockoutObservable<boolean> = ko.observable(true);

            checkedB591: KnockoutObservable<boolean> = ko.observable(false);
            enableB591: KnockoutObservable<boolean> = ko.observable(true);

            checkedB61: KnockoutObservable<boolean> = ko.observable(false);
            enableB61: KnockoutObservable<boolean> = ko.observable(true);

            selectedB74: KnockoutObservable<number> = ko.observable(0);
            enableB74: KnockoutObservable<boolean> = ko.observable(true);

            checkedB77: KnockoutObservable<boolean> = ko.observable(false);
            enableB77: KnockoutObservable<boolean> = ko.observable(true);

            selectedB772: KnockoutObservable<number> = ko.observable(0);
            enableB772: KnockoutObservable<boolean> = ko.observable(true);

            checkedB78: KnockoutObservable<boolean> = ko.observable(false);
            enableB78: KnockoutObservable<boolean> = ko.observable(true);

            checkedB710: KnockoutObservable<boolean> = ko.observable(false);
            enableB710: KnockoutObservable<boolean> = ko.observable(true);

            checkedB79: KnockoutObservable<boolean> = ko.observable(false);
            enableB79: KnockoutObservable<boolean> = ko.observable(true);

            selectedB84: KnockoutObservable<number> = ko.observable(0);
            enableB84: KnockoutObservable<boolean> = ko.observable(true);

            checkedB87: KnockoutObservable<boolean> = ko.observable(false);
            enableB87: KnockoutObservable<boolean> = ko.observable(true);

            checkedB88: KnockoutObservable<boolean> = ko.observable(false);
            enableB88: KnockoutObservable<boolean> = ko.observable(true);

            checkedB89: KnockoutObservable<boolean> = ko.observable(false);
            enableB89: KnockoutObservable<boolean> = ko.observable(true);

            checkedB810: KnockoutObservable<boolean> = ko.observable(false);
            enableB810: KnockoutObservable<boolean> = ko.observable(true);

            checkedB891: KnockoutObservable<boolean> = ko.observable(false);
            enableB891: KnockoutObservable<boolean> = ko.observable(true);

            checkedB91: KnockoutObservable<boolean> = ko.observable(false);
            enableB91: KnockoutObservable<boolean> = ko.observable(true);

            selectedB104: KnockoutObservable<number> = ko.observable(0);
            enableB104: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1071: KnockoutObservable<boolean> = ko.observable(false);
            enableB1071: KnockoutObservable<boolean> = ko.observable(true);

            selectedB1072: KnockoutObservable<number> = ko.observable(0);
            enableB1072: KnockoutObservable<boolean> = ko.observable(true);

            checkedB108: KnockoutObservable<boolean> = ko.observable(false);
            enableB108: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1010: KnockoutObservable<boolean> = ko.observable(false);
            enableB1010: KnockoutObservable<boolean> = ko.observable(true);

            checkedB109: KnockoutObservable<boolean> = ko.observable(false);
            enableB109: KnockoutObservable<boolean> = ko.observable(true);

            selectedB114: KnockoutObservable<number> = ko.observable(0);
            enableB114: KnockoutObservable<boolean> = ko.observable(true);

            checkedB117: KnockoutObservable<boolean> = ko.observable(false);
            enableB117: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1171: KnockoutObservable<boolean> = ko.observable(false);
            enableB1171: KnockoutObservable<boolean> = ko.observable(true);

            checkedB118: KnockoutObservable<boolean> = ko.observable(false);
            enableB118: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1110: KnockoutObservable<boolean> = ko.observable(false);
            enableB1110: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1111: KnockoutObservable<boolean> = ko.observable(false);
            enableB1111: KnockoutObservable<boolean> = ko.observable(true);

            checkedB119: KnockoutObservable<boolean> = ko.observable(false);
            enableB119: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1191: KnockoutObservable<boolean> = ko.observable(false);
            enableB1191: KnockoutObservable<boolean> = ko.observable(true);

            checkedB121: KnockoutObservable<boolean> = ko.observable(false);
            enableB121: KnockoutObservable<boolean> = ko.observable(true);

            selectedB134: KnockoutObservable<number> = ko.observable(0);
            enableB134: KnockoutObservable<boolean> = ko.observable(true);

            checkedB137: KnockoutObservable<boolean> = ko.observable(false);
            enableB137: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1371: KnockoutObservable<boolean> = ko.observable(false);
            enableB1371: KnockoutObservable<boolean> = ko.observable(true);

            checkedB138: KnockoutObservable<boolean> = ko.observable(false);
            enableB138: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1310: KnockoutObservable<boolean> = ko.observable(false);
            enableB1310: KnockoutObservable<boolean> = ko.observable(true);

            checkedB139: KnockoutObservable<boolean> = ko.observable(false);
            enableB139: KnockoutObservable<boolean> = ko.observable(true);

            selectedB144: KnockoutObservable<number> = ko.observable(0);
            enableB144: KnockoutObservable<boolean> = ko.observable(true);

            checkedB147: KnockoutObservable<boolean> = ko.observable(false);
            enableB147: KnockoutObservable<boolean> = ko.observable(true);

            checkedB148: KnockoutObservable<boolean> = ko.observable(false);
            enableB148: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1410: KnockoutObservable<boolean> = ko.observable(false);
            enableB1410: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1411: KnockoutObservable<boolean> = ko.observable(false);
            enableB1411: KnockoutObservable<boolean> = ko.observable(true);

            checkedB149: KnockoutObservable<boolean> = ko.observable(false);
            enableB149: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1491: KnockoutObservable<boolean> = ko.observable(false);
            enableB1491: KnockoutObservable<boolean> = ko.observable(true);

            checkedB151: KnockoutObservable<boolean> = ko.observable(false);
            enableB151: KnockoutObservable<boolean> = ko.observable(true);

            selectedB164: KnockoutObservable<number> = ko.observable(0);
            enableB164: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1671: KnockoutObservable<boolean> = ko.observable(false);
            enableB1671: KnockoutObservable<boolean> = ko.observable(true);

            selectedB1672: KnockoutObservable<number> = ko.observable(0);
            enableB1672: KnockoutObservable<boolean> = ko.observable(true);

            checkedB168: KnockoutObservable<boolean> = ko.observable(false);
            enableB168: KnockoutObservable<boolean> = ko.observable(true);

            checkedB1610: KnockoutObservable<boolean> = ko.observable(false);
            enableB1610: KnockoutObservable<boolean> = ko.observable(true);

            checkedB169: KnockoutObservable<boolean> = ko.observable(false);
            enableB169: KnockoutObservable<boolean> = ko.observable(true);


            itemsB8_4: KnockoutObservableArray<any> = ko.observableArray([
                new BoxModel(0, nts.uk.resource.getText('KMK013_521')),
                new BoxModel(1, nts.uk.resource.getText('KMK013_522'))
            ]);

            itemsB10_4: KnockoutObservableArray<any> = ko.observableArray([
                new BoxModel(0, nts.uk.resource.getText('KMK013_521')),
                new BoxModel(1, nts.uk.resource.getText('KMK013_522'))
            ]);
            
            constructor() {
                var self = this;
                
                self.initFlagDone = ko.observable(false);
                
                self.isLoadAfterGetData = ko.observable(true);
                
                self.conditionDisplay15 = ko.observable(true);
                self.conditionDisplay26 = ko.observable(true);
                
                self.workClass1 = ko.observable(0);
                self.workClass2 = ko.observable(1);


                self.itemListB3_8 = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText('KMK013_255')),
                    new BoxModel(1, nts.uk.resource.getText('KMK013_574'))
                ]);
                self.selectedIdB3_8 = ko.observable(0);
                self.enableB3_8 = ko.observable(true);
                
                self.itemListB2_28 = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText('KMK013_248')),
                    new BoxModel(1, nts.uk.resource.getText('KMK013_249'))
                ]);
                self.selectedIdB2_28 = ko.observable(0);
                self.enableB2_28 = ko.observable(true);
                
                self.itemListB2_33 = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText('KMK013_248')),
                    new BoxModel(1, nts.uk.resource.getText('KMK013_249'))
                ]);
                self.selectedIdB2_33 = ko.observable(0);
                self.enableB2_33 = ko.observable(true);
                
                
                self.oldData = ko.observable();
                self.itemsB23 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_5')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_6')),
                ]);
                self.itemsB29 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_9')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_10')),
                ]);
                self.itemsB215 = ko.observableArray([
                    new BoxModel(1, nts.uk.resource.getText('KMK013_13')),
                    new BoxModel(0, nts.uk.resource.getText('KMK013_14')),
                ]);
                self.selectedB23 = ko.observable(1);
                self.selectedB29 = ko.observable(1);
                self.selectedB215 = ko.observable(1);
                self.enableB29 = ko.observable(false);
                self.enableB215 = ko.observable(false);
                self.enableB217 = ko.observable(false);
                self.readonly = ko.observable(false);

                self.timeOfDay = ko.observable(0);
                self.timeB219 = ko.observable(0);
                self.timeB221 = ko.observable(0);
                self.timeB223 = ko.observable(0);
                self.inline = ko.observable(false);
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText("KMK013_25"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText("KMK013_422"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-3', title: nts.uk.resource.getText("KMK013_26"), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-4', title: nts.uk.resource.getText("KMK013_27"), content: '.tab-content-4', enable: ko.observable(true), visible: ko.observable(true) },
                ]);
                self.selectedTab = ko.observable('tab-1');
                self.checkedB33 = ko.observable(false);
                self.checkedB34 = ko.observable(false);
                self.checkedB35 = ko.observable(false);

                // ================ ver 27 ===========================
                self.enableB57 = ko.computed(() => {
                    return self.selectedB54() == 1;
                });
                self.enableB58 = ko.computed(() => {
                    return self.selectedB54() == 1;
                });
                self.enableB510 = ko.computed(() => {
                    return self.selectedB54() == 1;
                });
                self.enableB59 = ko.computed(() => {
                    return self.selectedB54() == 1;
                });
                self.enableB591 = ko.computed(() => {
                    return self.enableB59() == true && self.checkedB59() == true;
                });
                self.enableB61 = ko.computed(() => {
                    return self.selectedB54() == 1;
                });
                self.enableB74 = ko.computed(() => {
                    return self.enableB61() == true && self.checkedB61() == true;
                });
                self.enableB77 = ko.computed(() => {
                    return self.enableB74() == true && self.selectedB74() == 1 && self.checkedB57() == true;
                });
                self.enableB772 = ko.computed(() => {
                    return self.enableB77() == true && self.checkedB77() == true && self.checkedB57() == true;
                });
                self.enableB78 = ko.computed(() => {
                    return self.enableB74() == true && self.selectedB74() == 1 && self.checkedB58() == true;
                });
                self.enableB710 = ko.computed(() => {
                    return self.enableB74() == true && self.selectedB74() == 1 && self.checkedB510() == true;
                });
                self.enableB79 = ko.computed(() => {
                    return self.enableB74() == true && self.selectedB74() == 1 && self.checkedB591() == false && self.checkedB59() == true;
                });

                self.enableB87 = ko.computed(() => {
                    return self.selectedB84() == 1;
                });
                self.enableB88 = ko.computed(() => {
                    return self.selectedB84() == 1;
                });
                self.enableB810 = ko.computed(() => {
                    return self.selectedB84() == 1;
                });
                self.enableB89 = ko.computed(() => {
                    return self.selectedB84() == 1;
                });
                self.enableB891 = ko.computed(() => {
                    return self.selectedB84() == 1 && self.checkedB89() == true;
                });
                self.enableB91 = ko.computed(() => {
                    return self.selectedB84() == 1;
                });
                self.enableB104 = ko.computed(() => {
                    return self.checkedB91() == true && self.enableB91() == true;
                });
                self.enableB1071 = ko.computed(() => {
                    return self.checkedB91() == true && self.selectedB104() == 1 && self.enableB104() == true && self.checkedB87() == true
                });
                self.enableB1072 = ko.computed(() => {
                    return self.checkedB91() == true && self.selectedB104() == 1 && self.enableB104() == true && self.checkedB87() == true && self.checkedB1071() == true;
                });
                self.enableB108 = ko.computed(() => {
                    return self.checkedB91() == true && self.selectedB104() == 1 && self.enableB104() == true && self.checkedB88() == true;
                });
                self.enableB1010 = ko.computed(() => {
                    return self.checkedB91() == true && self.selectedB104() == 1 && self.enableB104() == true &&  self.checkedB810() == true;
                });
                self.enableB109 = ko.computed(() => {
                    return self.checkedB91() == true && self.selectedB104() == 1 && self.enableB104() == true && self.checkedB89() == true && self.checkedB891() == false;
                });
                self.enableB117 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB1171 = ko.computed(() => {
                    return self.selectedB114() == 1 && self.checkedB117() == true;
                });
                self.enableB118 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB118 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB1110 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB1111 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB119 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB1191 = ko.computed(() => {
                    return self.selectedB114() == 1 && self.checkedB119() == true;
                });
                self.enableB121 = ko.computed(() => {
                    return self.selectedB114() == 1;
                });
                self.enableB134 = ko.computed(() => {
                   return self.checkedB121() == true && self.enableB121() == true;
                });
                self.enableB137 = ko.computed(() => {
                    return self.checkedB121() == true && self.selectedB134() == 1 && self.checkedB117() == true && self.enableB134() == true;
                });
                self.enableB1371 = ko.computed(() => {
                    return self.checkedB121() == true && self.selectedB134() == 1 && self.checkedB117() == true && self.checkedB137() == true && self.enableB134() == true;
                });
                self.enableB138 = ko.computed(() => {
                    return self.checkedB121() == true && self.selectedB134() == 1 && self.checkedB118() == true && self.enableB134() == true;
                });
                self.enableB1310 = ko.computed(() => {
                    return self.checkedB121() == true && self.selectedB134() == 1 && self.checkedB1110() == true && self.enableB134() == true;
                });
                self.enableB139 = ko.computed(() => {
                    return self.checkedB121() == true && self.selectedB134() == 1 && self.checkedB119() == true && self.checkedB1191() == false && self.enableB134() == true;
                });
                self.enableB147 = ko.computed(() => {
                   return self.selectedB144() == 1;
                });
                self.enableB148 = ko.computed(() => {
                    return self.selectedB144() == 1;
                });
                self.enableB1410 = ko.computed(() => {
                    return self.selectedB144() == 1;
                });
                self.enableB1411 = ko.computed(() => {
                    return self.selectedB144() == 1;
                });
                self.enableB149 = ko.computed(() => {
                    return self.selectedB144() == 1;
                });
                self.enableB1491 = ko.computed(() => {
                    return self.selectedB144() == 1 && self.checkedB149() == true;
                });
                self.enableB151 = ko.computed(() => {
                    return self.selectedB144() == 1;
                });
                self.enableB164 = ko.computed(() => {
                   return self.checkedB151() == true && self.enableB151() == true;
                });
                self.enableB1671 = ko.computed(() => {
                    return self.checkedB151() == true && self.selectedB164() == 1 && self.checkedB147() == true && self.enableB164() == true;
                });
                self.enableB1672 = ko.computed(() => {
                    return self.checkedB151() == true && self.selectedB164() == 1 && self.checkedB147() == true && self.checkedB1671() == true && self.enableB164() == true;
                });
                self.enableB168 = ko.computed(() => {
                    return self.checkedB151() == true && self.selectedB164() == 1 && self.checkedB148() == true && self.enableB164() == true;
                });
                self.enableB1610 = ko.computed(() => {
                    return self.checkedB151() == true && self.selectedB164() == 1 && self.checkedB1410() == true && self.enableB164() == true;
                });
                self.enableB169 = ko.computed(() => {
                    return self.checkedB151() == true && self.selectedB164() == 1 && self.checkedB149() == true && self.checkedB1491() == false && self.enableB164() == true;
                });

                // Init B4_9
                $("#B4_10").ntsPopup({
                  trigger: "#B4_9",
                  position: {
                    my: "left top",
                    at: "left bottom",
                    of: "#B4_9"
                  },
                  showOnStart: false,
                  dismissible: true
                });

                self.changeTabPanel();
                
            }
            
            changeTabPanel(): void {
                let self = this;
                $( document ).keydown(function( event ) {
                    // catch event press tab button
                    if (event.which == 9) {
                       switch(_.toNumber($( "*:focus" ).attr("tabindex"))) { 
                           case lastTabIndexTabPanel1: { 
                                self.selectedTab("tab-2");
                                break; 
                           } 
                           case lastTabIndexTabPanel2 : { 
                                self.selectedTab("tab-3"); 
                                break; 
                           }
                           case lastTabIndexTabPanel3 : { 
                                self.selectedTab("tab-4"); 
                              break; 
                           }
                           case lastTabIndexBeforeTabPanel1 : { 
                                self.selectedTab("tab-1"); 
                                break; 
                           }
                           default: { 
                           } 
                        }
                    }
                });
            }
            
            startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                self.initData().done(() => {
                    dfd.resolve(); 
                });
                return dfd.promise();
            }
            
            initData(): JQueryPromise<any> {
                var dfd = $.Deferred();
                let self = this;
                self.isLoadAfterGetData(true);
                $.when(service.findByCompanyId(), service.getDomainSet()).done(function(data, dataDomainSet){
                    if (dataDomainSet != null) {
                        // condition 15
                        self.conditionDisplay15(dataDomainSet.flexWorkManagement == 1? true : false);
                        
                        // condition 26
                        self.conditionDisplay26(dataDomainSet.useAggDeformedSetting == 1? true : false);

                        // #120188
                        if (!self.conditionDisplay15()) {
                          _.remove(self.tabs(), { id: 'tab-3' });
                        }
                        if (!self.conditionDisplay26()) {
                          _.remove(self.tabs(), { id: 'tab-4' });
                        }
                        self.tabs.valueHasMutated();
                    }
                    
                    if (data[0] == null) {
                        self.oldData({  "companyId":null, "referComHolidayTime":0, "oneDay":0, "morning":0, "afternoon":0, "referActualWorkHours":0, "notReferringAch":0,
                                        "annualHoliday":0, "specialHoliday":0, "yearlyReserved":0,
                                        "regularWork":{
                                                "companyId":null, "calcActualOperationPre":0, "exemptTaxTimePre":0, "incChildNursingCarePre":0, "additionTimePre":0, 
                                                "notDeductLateleavePre":0, "deformatExcValuePre":0, "exemptTaxTimeWork":0, "calcActualOperationWork":0, "incChildNursingCareWork":0, 
                                                "notDeductLateleaveWork":0, "additionTimeWork":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0, "useAtr":0
                                                },
                                        "flexWork":{
                                                "companyId":null, "calcActualOperationPre":0, "exemptTaxTimePre":0, "incChildNursingCarePre":0, "predeterminedOvertimePre":0,
                                                "additionTimePre":0, "notDeductLateleavePre":0, "exemptTaxTimeWork":0, "minusAbsenceTimeWork":0, "calcActualOperationWork":0, 
                                                "incChildNursingCareWork":0,"notDeductLateleaveWork":0, "predeterminDeficiencyWork":0,"additionTimeWork":0, "enableSetPerWorkHour1":0, 
                                                "enableSetPerWorkHour2":0, "additionWithinMonthlyStatutory":0, "useAtr":0
                                                },
                                        "irregularWork":{
                                                "companyId":null, "calcActualOperationPre":0, "exemptTaxTimePre":0, "incChildNursingCarePre":0, "predeterminedOvertimePre":0,
                                                "additionTimePre":0, "notDeductLateleavePre":0, "deformatExcValue":0, "exemptTaxTimeWork":0, "minusAbsenceTimeWork":0,
                                                "calcActualOperationWork":0, "incChildNursingCareWork":0, "notDeductLateleaveWork":0, "predeterminDeficiencyWork":0,
                                                "additionTimeWork":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0, "useAtr":0
                                                },
                                        "hourlyPaymentAdditionSet":{
                                                "companyId":null, "calcPremiumVacation":0, "addition1":0, "deformatExcValue":0, "incChildNursingCare":0, "deduct":0,
                                                "calculateIncludeIntervalExemptionTime1":0, "calcWorkHourVacation":0, "addition2":0, "calculateIncludCareTime":0,
                                                "notDeductLateLeaveEarly":0, "calculateIncludeIntervalExemptionTime2":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0,
                                                "useAtr":0
                                                },
                                        "addSetManageWorkHour":0, "addingMethod1":0, "workClass1":0, "addingMethod2":0, "workClass2":1
                                        });
                        return;
                    }
                    
                    if (_.isNull(data[0].hourlyPaymentAdditionSet)) {
                        data[0].hourlyPaymentAdditionSet = <any>{};
                        data[0].hourlyPaymentAdditionSet = {
                                                "companyId":null, "calcPremiumVacation":0, "addition1":0, "deformatExcValue":0, "incChildNursingCare":0, "deduct":0,
                                                "calculateIncludeIntervalExemptionTime1":0, "calcWorkHourVacation":0, "addition2":0, "calculateIncludCareTime":0,
                                                "notDeductLateLeaveEarly":0, "calculateIncludeIntervalExemptionTime2":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0,
                                                "useAtr":0
                                                };
                    }
                    
                    if (_.isNull(data[0].irregularWork)) {
                        data[0].irregularWork = <any>{};
                        data[0].irregularWork = {
                                                "companyId":null, "calcActualOperationPre":0, "exemptTaxTimePre":0, "incChildNursingCarePre":0, "predeterminedOvertimePre":0,
                                                "additionTimePre":0, "notDeductLateleavePre":0, "exemptTaxTimeWork":0, "minusAbsenceTimeWork":0, "calcActualOperationWork":0, 
                                                "incChildNursingCareWork":0,"notDeductLateleaveWork":0, "predeterminDeficiencyWork":0,"additionTimeWork":0, "enableSetPerWorkHour1":0, 
                                                "enableSetPerWorkHour2":0, "additionWithinMonthlyStatutory":0, "useAtr":0
                                                };
                    }
                    
                    if (_.isNull(data[0].flexWork)) {
                        data[0].flexWork = <any>{};
                        data[0].flexWork = {
                                                "companyId":null, "calcPremiumVacation":0, "addition1":0, "deformatExcValue":0, "incChildNursingCare":0, "deduct":0,
                                                "calculateIncludeIntervalExemptionTime1":0, "calcWorkHourVacation":0, "addition2":0, "calculateIncludCareTime":0,
                                                "notDeductLateLeaveEarly":0, "calculateIncludeIntervalExemptionTime2":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0,
                                                "useAtr":0
                                                };
                    }
                    
                    if (_.isNull(data[0].hourlyPaymentAdditionSet)) {
                        data[0].hourlyPaymentAdditionSet = <any>{};
                        data[0].hourlyPaymentAdditionSet = {
                                                "companyId":null, "calcPremiumVacation":0, "addition1":0, "deformatExcValue":0, "incChildNursingCare":0, "deduct":0,
                                                "calculateIncludeIntervalExemptionTime1":0, "calcWorkHourVacation":0, "addition2":0, "calculateIncludCareTime":0,
                                                "notDeductLateLeaveEarly":0, "calculateIncludeIntervalExemptionTime2":0, "enableSetPerWorkHour1":0, "enableSetPerWorkHour2":0,
                                                "useAtr":0
                                                };
                    }
                    
                    self.oldData(data[0]);
                    let obj = data[0];

                    //加算休暇設定.年休
                    self.checkedB33(convertToBoolean(obj.annualHoliday));
                    //加算休暇設定.積立年休
                    self.checkedB34(convertToBoolean(obj.yearlyReserved));
                    //加算休暇設定.特別休暇
                    self.checkedB35(convertToBoolean(obj.specialHoliday));
                    /** 時間休暇加算.加算方法 */
                    self.selectedIdB2_28(obj.addingMethod1);
                    /** 時間休暇加算.加算方法 */
                    self.selectedIdB2_33(obj.addingMethod2);
                    /** 時間外超過の加算設定 */
                    self.selectedIdB3_8(obj.addSetManageWorkHour);
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                    self.selectedB54(obj.regularWork.calcActualOperationWork);
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB57(convertToBoolean(obj.regularWork.additionTimeWork));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB58(convertToBoolean(obj.regularWork.incChildNursingCareWork));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB510(convertToBoolean(obj.regularWork.exemptTaxTimeWork));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB59(convertToBoolean(obj.regularWork.notDeductLateleaveWork));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                    self.checkedB591(convertToBoolean(obj.regularWork.enableSetPerWorkHour1));
                    // 通常勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                    self.checkedB61(convertToBoolean(obj.regularWork.useAtr));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                    self.selectedB74(obj.regularWork.calcActualOperationPre);
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB77(convertToBoolean(obj.regularWork.additionTimePre));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時
                    self.selectedB772(obj.regularWork.deformatExcValuePre);
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB78(convertToBoolean(obj.regularWork.incChildNursingCarePre));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB710(convertToBoolean(obj.regularWork.exemptTaxTimePre));
                    // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB79(convertToBoolean(obj.regularWork.notDeductLateleavePre));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                    self.selectedB84(obj.hourlyPaymentAdditionSet.calcWorkHourVacation);
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB87(convertToBoolean(obj.hourlyPaymentAdditionSet.addition2));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB88(convertToBoolean(obj.hourlyPaymentAdditionSet.calculateIncludCareTime));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB810(convertToBoolean(obj.hourlyPaymentAdditionSet.calculateIncludeIntervalExemptionTime2));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB89(convertToBoolean(obj.hourlyPaymentAdditionSet.notDeductLateLeaveEarly));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                    self.checkedB891(convertToBoolean(obj.hourlyPaymentAdditionSet.enableSetPerWorkHour2));
                    // 時給者の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                    self.checkedB91(convertToBoolean(obj.hourlyPaymentAdditionSet.useAtr));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                    self.selectedB104(obj.hourlyPaymentAdditionSet.calcPremiumVacation);
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB1071(convertToBoolean(obj.hourlyPaymentAdditionSet.addition1));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時
                    self.selectedB1072(obj.hourlyPaymentAdditionSet.deformatExcValue);
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB108(convertToBoolean(obj.hourlyPaymentAdditionSet.incChildNursingCare));
                    // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB1010(convertToBoolean(obj.hourlyPaymentAdditionSet.calculateIncludeIntervalExemptionTime1));
                    //時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB109(convertToBoolean(obj.hourlyPaymentAdditionSet.deduct));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                    self.selectedB114(obj.flexWork.calcActualOperationWork);
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB117(convertToBoolean(obj.flexWork.additionTimeWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.フレックスの所定不足時
                    self.checkedB1171(convertToBoolean(obj.flexWork.predeterminDeficiencyWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB118(convertToBoolean(obj.flexWork.incChildNursingCareWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB1110(convertToBoolean(obj.flexWork.exemptTaxTimeWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.欠勤をマイナスせず所定から控除する
                    self.checkedB1111(convertToBoolean(obj.flexWork.minusAbsenceTimeWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB119(convertToBoolean(obj.flexWork.notDeductLateleaveWork));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                    self.checkedB1191(convertToBoolean(obj.flexWork.enableSetPerWorkHour2));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                    self.checkedB121(convertToBoolean(obj.flexWork.useAtr));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                    self.selectedB134(obj.flexWork.calcActualOperationPre);
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                    self.checkedB137(convertToBoolean(obj.flexWork.additionTimePre));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.フレックスの所定超過時
                    self.checkedB1371(convertToBoolean(obj.flexWork.predeterminedOvertimePre));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                    self.checkedB138(convertToBoolean(obj.flexWork.incChildNursingCarePre));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                    self.checkedB1310(convertToBoolean(obj.flexWork.exemptTaxTimePre));
                    // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB139(convertToBoolean(obj.flexWork.notDeductLateleavePre));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する */
                    self.selectedB144(obj.irregularWork.calcActualOperationWork);
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する */
                    self.checkedB147(convertToBoolean(obj.irregularWork.additionTimeWork));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する */
                    self.checkedB148(convertToBoolean(obj.irregularWork.incChildNursingCareWork));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する */
                    self.checkedB1410(convertToBoolean(obj.irregularWork.exemptTaxTimeWork));
                    // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.欠勤をマイナスせず所定から控除する
                    self.checkedB1411(convertToBoolean(obj.irregularWork.minusAbsenceTimeWork));
                    // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB149(convertToBoolean(obj.irregularWork.notDeductLateleaveWork));
                    // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                    self.checkedB1491(convertToBoolean(obj.irregularWork.enableSetPerWorkHour2));
                    // 変形労働勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                    self.checkedB151(convertToBoolean(obj.irregularWork.useAtr));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する */
                    self.selectedB164(obj.irregularWork.calcActualOperationPre);
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する */
                    self.checkedB1671(convertToBoolean(obj.irregularWork.additionTimePre));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時 */
                    self.selectedB1672(obj.irregularWork.deformatExcValue);
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する */
                    self.checkedB168(convertToBoolean(obj.irregularWork.incChildNursingCarePre));
                    /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する */
                    self.checkedB1610(convertToBoolean(obj.irregularWork.exemptTaxTimePre));
                    // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                    self.checkedB169(convertToBoolean(obj.irregularWork.notDeductLateleavePre));


                    self.isLoadAfterGetData(false);
                    self.initFlagDone(true);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            
            save(): void {
                let self = this;
                let obj :any = {};

                obj.regularWork = {};
                obj.flexWork = {};
                obj.irregularWork = {};
                obj.hourlyPaymentAddCommand = {};

                //加算休暇設定.年休
                obj.annualHoliday = convertToInt(self.checkedB33());
                //加算休暇設定.積立年休
                obj.yearlyReserved = convertToInt(self.checkedB34());
                //加算休暇設定.特別休暇
                obj.specialHoliday = convertToInt(self.checkedB35());
                /** 時間休暇加算.加算方法 */
                obj.addingMethod1 = self.selectedIdB2_28();
                /** 時間休暇加算.加算方法 */
                obj.addingMethod2 = self.selectedIdB2_33();
                /** 時間外超過の加算設定 */
                obj.addSetManageWorkHour = self.selectedIdB3_8();
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                obj.regularWork.calcActualOperationWork = self.selectedB54();
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                obj.regularWork.additionTimeWork = self.enableB57() ? convertToInt(self.checkedB57()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.regularWork.incChildNursingCareWork = self.enableB58() ? convertToInt(self.checkedB58()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.regularWork.exemptTaxTimeWork = self.enableB510() ? convertToInt(self.checkedB510()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.regularWork.notDeductLateleaveWork = self.enableB59() ? convertToInt(self.checkedB59()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                obj.regularWork.enableSetPerWorkHour1 = self.enableB591() ? convertToInt(self.checkedB591()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                obj.regularWork.enableSetPerWorkHour2 = self.enableB591() ? convertToInt(self.checkedB591()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                obj.regularWork.useAtr = self.enableB61() ? convertToInt(self.checkedB61()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                obj.regularWork.calcActualOperationPre = self.enableB74() ? self.selectedB74() : self.selectedB54();
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                obj.regularWork.additionTimePre = self.enableB77() ? convertToInt(self.checkedB77()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時
                obj.regularWork.deformatExcValuePre = self.enableB772() ? self.selectedB772() : null;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.regularWork.incChildNursingCarePre = self.enableB78() ? convertToInt(self.checkedB78()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.regularWork.exemptTaxTimePre = self.enableB710() ? convertToInt(self.checkedB710()) : 0;
                // 通常勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.regularWork.notDeductLateleavePre = self.enableB79() ? convertToInt(self.checkedB79()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                obj.hourlyPaymentAddCommand.calcWorkHourVacation = self.selectedB84();
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                obj.hourlyPaymentAddCommand.addition2 = self.enableB87() ? convertToInt(self.checkedB87()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.hourlyPaymentAddCommand.calculateIncludCareTime = self.enableB88() ? convertToInt(self.checkedB88()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.hourlyPaymentAddCommand.calculateIncludeIntervalExemptionTime2 = self.enableB810() ? convertToInt(self.checkedB810()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.hourlyPaymentAddCommand.notDeductLateLeaveEarly = self.enableB89() ? convertToInt(self.checkedB89()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                obj.hourlyPaymentAddCommand.enableSetPerWorkHour2 = self.enableB891() ? convertToInt(self.checkedB891()) : 0;
                obj.hourlyPaymentAddCommand.enableSetPerWorkHour1 = self.enableB891() ? convertToInt(self.checkedB891()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                obj.hourlyPaymentAddCommand.useAtr = self.enableB91() ? convertToInt(self.checkedB91()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                obj.hourlyPaymentAddCommand.calcPremiumVacation = self.enableB104() ? self.selectedB104() : self.selectedB84();
                // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                obj.hourlyPaymentAddCommand.addition1 = self.enableB1071() ? convertToInt(self.checkedB1071()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時
                obj.hourlyPaymentAddCommand.deformatExcValue = self.enableB1072() ? self.selectedB1072() : null;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.hourlyPaymentAddCommand.incChildNursingCare = self.enableB108() ? convertToInt(self.checkedB108()) : 0;
                // 時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.hourlyPaymentAddCommand.calculateIncludeIntervalExemptionTime1 = self.enableB1010() ? convertToInt(self.checkedB1010()) : 0;
                //時給者の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.hourlyPaymentAddCommand.deduct = self.enableB109() ? convertToInt(self.checkedB109()) : 0;

                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する
                obj.flexWork.calcActualOperationWork = self.selectedB114();
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する
                obj.flexWork.additionTimeWork = self.enableB117() ? convertToInt(self.checkedB117()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.フレックスの所定不足時
                obj.flexWork.predeterminDeficiencyWork = self.enableB1171() ? convertToInt(self.checkedB1171()) : null;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.flexWork.incChildNursingCareWork = self.enableB118() ?convertToInt( self.checkedB118()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.flexWork.exemptTaxTimeWork = self.enableB1110() ? convertToInt(self.checkedB1110()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.欠勤をマイナスせず所定から控除する
                obj.flexWork.minusAbsenceTimeWork = self.enableB1111() ? convertToInt(self.checkedB1111()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.flexWork.notDeductLateleaveWork = self.enableB119() ? convertToInt(self.checkedB119()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                obj.flexWork.enableSetPerWorkHour1 = self.enableB1191() ? convertToInt(self.checkedB1191()) : 0;
                obj.flexWork.enableSetPerWorkHour2 = self.enableB1191() ? convertToInt(self.checkedB1191()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                obj.flexWork.useAtr = self.enableB121() ? convertToInt(self.checkedB121()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する
                obj.flexWork.calcActualOperationPre = self.enableB134() ? self.selectedB134() : self.selectedB114();
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する
                obj.flexWork.additionTimePre = self.enableB137() ? convertToInt(self.checkedB137()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.フレックスの所定超過時
                obj.flexWork.predeterminedOvertimePre = self.enableB1371() ? convertToInt(self.checkedB1371()) : null;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する
                obj.flexWork.incChildNursingCarePre = self.enableB138() ? convertToInt(self.checkedB138()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する
                obj.flexWork.exemptTaxTimePre = self.enableB1310() ? convertToInt(self.checkedB1310()) : 0;
                // フレックス勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.flexWork.notDeductLateleavePre = self.enableB139() ? convertToInt(self.checkedB139()) : 0;

                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.実働のみで計算する */
                obj.irregularWork.calcActualOperationWork = self.selectedB144();
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.休暇分を含める設定.加算する */
                obj.irregularWork.additionTimeWork = self.enableB147() ? convertToInt(self.checkedB147()) : 0;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.育児・介護時間を含めて計算する */
                obj.irregularWork.incChildNursingCareWork = self.enableB148() ? convertToInt(self.checkedB148()) : 0;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.インターバル免除時間を含めて計算する */
                obj.irregularWork.exemptTaxTimeWork = self.enableB1410() ? convertToInt(self.checkedB1410()) : 0;
                // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.欠勤をマイナスせず所定から控除する
                obj.irregularWork.minusAbsenceTimeWork = self.enableB1411() ? convertToInt(self.checkedB1411()) : 0;
                // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.irregularWork.notDeductLateleaveWork = self.enableB149() ? convertToInt(self.checkedB149()) : 0;
                // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の就業時間計算方法.詳細設定.遅刻・早退を控除する.就業時間帯毎の設定を可能とする
                obj.irregularWork.enableSetPerWorkHour1 = self.enableB1491() ? convertToInt(self.checkedB1491()) : 0;
                obj.irregularWork.enableSetPerWorkHour2 = self.enableB1491() ? convertToInt(self.checkedB1491()) : 0;
                // 変形労働勤務の加算設定.休暇の計算方法の設定.割増計算方法を設定する
                obj.irregularWork.useAtr = self.enableB151() ? convertToInt(self.checkedB151()) : 0;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.実働のみで計算する */
                obj.irregularWork.calcActualOperationPre = self.enableB164() ? self.selectedB164() : self.selectedB144();
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.加算する */
                obj.irregularWork.additionTimePre = self.enableB1671() ? convertToInt(self.checkedB1671()) : 0;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.休暇分を含める設定.通常、変形の所定超過時 */
                obj.irregularWork.deformatExcValue = self.enableB1672() ? self.selectedB1672() : null;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.育児・介護時間を含めて計算する */
                obj.irregularWork.incChildNursingCarePre = self.enableB168() ? convertToInt(self.checkedB168()) : 0;
                /** 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.インターバル免除時間を含めて計算する */
                obj.irregularWork.exemptTaxTimePre = self.enableB1610() ? convertToInt(self.checkedB1610()) : 0;
                // 変形労働勤務の加算設定.休暇の計算方法の設定.休暇の割増計算方法.詳細設定.遅刻・早退を控除する.就業時間から控除する設定.就業時間から控除する
                obj.irregularWork.notDeductLateleavePre = self.enableB169() ? convertToInt(self.checkedB169()) : 0;

                obj.morning = self.oldData().morning;
                obj.afternoon = self.oldData().afternoon;
                obj.oneDay = self.oldData().oneDay;
                obj.workClass1 = self.oldData().workClass1;
                obj.workClass2 = self.oldData().workClass2;
                obj.refAtrCom = self.oldData().refAtrCom;
                obj.refAtrEmp = self.oldData().refAtrEmp;


                if (nts.uk.ui.errors.hasError()) {
                    return;
                }

                if (!self.validate()) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_2041" });
                    return;
                }
                
                blockUI.grayout();
                
                service.save(obj).done(() => {
                    self.initData();
                    nts.uk.ui.dialog.info({messageId: 'Msg_15'}).then(() => {
                        $( "#B3_3" ).focus();
                    });
                }).fail((error) => {
                   nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    blockUI.clear();
                });
            }

            openScreenR() {
                let self = this;

                nts.uk.ui.windows.sub.modal("/view/kmk/013/r/index.xhtml").onClosed(() => {
                    self.initData();
                    nts.uk.ui.block.clear();
                });
            }

            validate() {
                const self = this;
                if (!self.screenBCheckTime(self.checkedB510(), self.checkedB59(), self.checkedB710(), self.checkedB79(), self.enableB510(), self.enableB59(), self.enableB710(), self.enableB79())) {
                    return false;
                }
                if (!self.screenBCheckTime(self.checkedB810(), self.checkedB89(), self.checkedB1010(), self.checkedB109(), self.enableB810(), self.enableB89(), self.enableB1010(), self.enableB109())) {
                    return false;
                }
                if (!self.screenBCheckTime(self.checkedB1110(), self.checkedB119(), self.checkedB1310(), self.checkedB139(), self.enableB1110(), self.enableB119(), self.enableB1310(), self.enableB139())) {
                    return false;
                }
                if (!self.screenBCheckTime(self.checkedB1410(), self.checkedB149(), self.checkedB1610(), self.checkedB169(), self.enableB1410(), self.enableB149(), self.enableB1610(), self.enableB169())) {
                    return false;
                }
                return true;
            }

            /*
                「画面B：就業時間の加算設定」の「B4_1 労働時間に加算する時間」の登録時チェックについて
             */
            screenBCheckTime(intervalTime: boolean,
                             deductLaveAndEarly: boolean,
                             intervalTime2: boolean,
                             deductLaveAndEarly2: boolean,
                             enableIntervalTime: boolean,
                             enableDeductLateAndEarly: boolean,
                             enableIntervalTime2: boolean,
                             enableDeductLateAndEarly2: boolean) {
                // a）「遅刻・早退を控除する」＝☑チェックありで、「インターバル免除時間」＝□チェックなしの場合、
                if (enableIntervalTime && enableDeductLateAndEarly) {
                    if (!intervalTime && deductLaveAndEarly) {
                        return false;
                    }
                }
                // b）割増時間のみ、「遅刻・早退を控除する」＝☑チェックありで、「インターバル免除時間」＝□チェックなしの場合、
                if (enableIntervalTime2 && enableDeductLateAndEarly2) {
                    if (!intervalTime2 && deductLaveAndEarly2) {
                        return false;
                    }
                }

                return true;
            }


        }
        
        const lastTabIndexTabPanel1 = 28;
        const lastTabIndexTabPanel2 = 41;
        const lastTabIndexTabPanel3 = 57;
        const lastTabIndexBeforeTabPanel1  = 14;
        
        class BoxModel {
            id: number;
            name: string;
            constructor(id, name) {
                var self = this;
                self.id = id;
                self.name = name;
            }
        }
        function convertToBoolean(x: number) {
            if (x == 1)
                return true;
            else
                return false;
        }
        function convertToInt(x: boolean) {
            if (x == true) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}