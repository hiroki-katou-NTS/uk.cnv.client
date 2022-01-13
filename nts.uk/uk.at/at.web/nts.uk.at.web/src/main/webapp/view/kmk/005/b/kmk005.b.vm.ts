module nts.uk.at.view.kmk005.b {
    export module viewmodel {
        export class ScreenModel {
            tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;
            timeItemSpecList: KnockoutObservableArray<TimeItem>;
            timeItemList: KnockoutObservableArray<TimeItem>;            
            
            /** Bonus pay time */
            useAtr1: KnockoutObservable<number> = ko.observable(0);
            timeItemId1: KnockoutObservable<string> = ko.observable('');
            timeItemName1: KnockoutObservable<string> = ko.observable('');
            isRequired1: KnockoutObservable<boolean> = ko.observable(false);
            
            useAtr2: KnockoutObservable<number> = ko.observable(0);
            timeItemId2 : KnockoutObservable<string> = ko.observable('');
            timeItemName2 : KnockoutObservable<string> = ko.observable('');
            isRequired2: KnockoutObservable<boolean> = ko.observable(false);

            useAtr3: KnockoutObservable<number> = ko.observable(0);
            timeItemId3: KnockoutObservable<string> = ko.observable('');
            timeItemName3: KnockoutObservable<string> = ko.observable('');
            isRequired3: KnockoutObservable<boolean> = ko.observable(false);

            useAtr4: KnockoutObservable<number> = ko.observable(0);
            timeItemId4: KnockoutObservable<string> = ko.observable('');
            timeItemName4: KnockoutObservable<string> = ko.observable('');
            isRequired4: KnockoutObservable<boolean> = ko.observable(false);

            useAtr5: KnockoutObservable<number> = ko.observable(0);
            timeItemId5: KnockoutObservable<string> = ko.observable('');
            timeItemName5: KnockoutObservable<string> = ko.observable('');
            isRequired5: KnockoutObservable<boolean> = ko.observable(false);

            useAtr6: KnockoutObservable<number> = ko.observable(0);
            timeItemId6: KnockoutObservable<string> = ko.observable('');
            timeItemName6: KnockoutObservable<string> = ko.observable('');
            isRequired6: KnockoutObservable<boolean> = ko.observable(false);

            useAtr7: KnockoutObservable<number> = ko.observable(0);
            timeItemId7: KnockoutObservable<string> = ko.observable('');
            timeItemName7: KnockoutObservable<string> = ko.observable('');
            isRequired7: KnockoutObservable<boolean> = ko.observable(false);

            useAtr8: KnockoutObservable<number> = ko.observable(0);
            timeItemId8: KnockoutObservable<string> = ko.observable('');
            timeItemName8: KnockoutObservable<string> = ko.observable('');
            isRequired8: KnockoutObservable<boolean> = ko.observable(false);

            useAtr9: KnockoutObservable<number> = ko.observable(0);
            timeItemId9: KnockoutObservable<string> = ko.observable('');
            timeItemName9: KnockoutObservable<string> = ko.observable('');
            isRequired9: KnockoutObservable<boolean> = ko.observable(false);

            useAtr10: KnockoutObservable<number> = ko.observable(0);
            timeItemId10: KnockoutObservable<string> = ko.observable('');
            timeItemName10: KnockoutObservable<string> = ko.observable('');
            isRequired10: KnockoutObservable<boolean> = ko.observable(false);

             /** Bonus pay special time */
             useSpecAtr1: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId1: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName1: KnockoutObservable<string> = ko.observable('');
             isSpecRequired1: KnockoutObservable<boolean> = ko.observable(false);
             
             useSpecAtr2: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId2: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName2: KnockoutObservable<string> = ko.observable('');
             isSpecRequired2: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr3: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId3: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName3: KnockoutObservable<string> = ko.observable('');
             isSpecRequired3: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr4: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId4: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName4: KnockoutObservable<string> = ko.observable('');
             isSpecRequired4: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr5: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId5: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName5: KnockoutObservable<string> = ko.observable('');
             isSpecRequired5: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr6: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId6: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName6: KnockoutObservable<string> = ko.observable('');
             isSpecRequired6: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr7: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId7: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName7: KnockoutObservable<string> = ko.observable('');
             isSpecRequired7: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr8: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId8: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName8: KnockoutObservable<string> = ko.observable('');
             isSpecRequired8: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr9: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId9: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName9: KnockoutObservable<string> = ko.observable('');
             isSpecRequired9: KnockoutObservable<boolean> = ko.observable(false);
 
             useSpecAtr10: KnockoutObservable<number> = ko.observable(0);
             timeItemSpecId10: KnockoutObservable<string> = ko.observable('');
             timeItemSpecName10: KnockoutObservable<string> = ko.observable('');
             isSpecRequired10: KnockoutObservable<boolean> = ko.observable(false);

            constructor() {
                var self = this;

                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText('KMK005_17'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText('KMK005_18'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                self.selectedTab = ko.observable('tab-1');
                self.timeItemList = ko.observableArray([]);
                self.timeItemSpecList = ko.observableArray([]);
                self.selectedTab.subscribe((tabId) => {
                    if (tabId === "tab-1") {
                        $(document).ready(() => {
                            $($(".itemName[disabled!='disabled']")[0]).focus();
                        });
                    } else if (tabId === "tab-2") {
                        $(document).ready(() => {
                            $($(".specialItemName[disabled!='disabled']")[0]).focus();
                        });
                    }
                });

                self.useAtr1.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired1(false);
                        $('#itemName1').ntsError('clear');
                    } else {
                        self.isRequired1(true);
                    }
                });

                self.useAtr2.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired2(false);
                        $('#itemName2').ntsError('clear');
                    } else {
                        self.isRequired2(true);
                    }
                });

                self.useAtr3.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired3(false);
                        $('#itemName3').ntsError('clear');
                    } else {
                        self.isRequired3(true);
                    }
                });

                self.useAtr4.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired4(false);
                        $('#itemName4').ntsError('clear');
                    } else {
                        self.isRequired4(true);
                    }
                });

                self.useAtr5.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired5(false);
                        $('#itemName5').ntsError('clear');
                    } else {
                        self.isRequired5(true);
                    }
                });

                self.useAtr6.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired6(false);
                        $('#itemName6').ntsError('clear');
                    } else {
                        self.isRequired6(true);
                    }
                });

                self.useAtr7.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired7(false);
                        $('#itemName7').ntsError('clear');
                    } else {
                        self.isRequired7(true);
                    }
                });

                self.useAtr8.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired8(false);
                        $('#itemName8').ntsError('clear');
                    } else {
                        self.isRequired8(true);
                    }
                });

                self.useAtr9.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired9(false);
                        $('#itemName9').ntsError('clear');
                    } else {
                        self.isRequired9(true);
                    }
                });

                self.useAtr10.subscribe((val) => {
                    if (val == 0) {
                        self.isRequired10(false);
                        $('#itemName10').ntsError('clear');
                    } else {
                        self.isRequired10(true);
                    }
                });

                self.useSpecAtr1.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired1(false);
                        $('#itemSpecName1').ntsError('clear');
                    } else {
                        self.isSpecRequired1(true);
                    }
                });

                self.useSpecAtr2.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired2(false);
                        $('#itemSpecName2').ntsError('clear');
                    } else {
                        self.isSpecRequired2(true);
                    }
                });

                self.useSpecAtr3.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired3(false);
                        $('#itemSpecName3').ntsError('clear');
                    } else {
                        self.isSpecRequired3(true);
                    }
                });

                self.useSpecAtr4.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired4(false);
                        $('#itemSpecName4').ntsError('clear');
                    } else {
                        self.isSpecRequired4(true);
                    }
                });

                self.useSpecAtr5.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired5(false);
                        $('#itemSpecName5').ntsError('clear');
                    } else {
                        self.isSpecRequired5(true);
                    }
                });

                self.useSpecAtr6.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired6(false);
                        $('#itemSpecName6').ntsError('clear');
                    } else {
                        self.isSpecRequired6(true);
                    }
                });

                self.useSpecAtr7.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired7(false);
                        $('#itemSpecName7').ntsError('clear');
                    } else {
                        self.isSpecRequired7(true);
                    }
                });

                self.useSpecAtr8.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired8(false);
                        $('#itemSpecName8').ntsError('clear');
                    } else {
                        self.isSpecRequired8(true);
                    }
                });

                self.useSpecAtr9.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired9(false);
                        $('#itemSpecName9').ntsError('clear');
                    } else {
                        self.isSpecRequired9(true);
                    }
                });

                self.useSpecAtr10.subscribe((val) => {
                    if (val == 0) {
                        self.isSpecRequired10(false);
                        $('#itemSpecName10').ntsError('clear');
                    } else {
                        self.isSpecRequired10(true);
                    }
                });
            }

            startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                service.getListBonusPTimeItem().done(function(item: Array<any>) {
                    if(!_.isUndefined(item) || !_.isEmpty(item)) {
                        for (let i = 0; i < item.length; i++){
                            if(i == 0) {
                                self.useAtr1(item[i].useAtr);
                                self.timeItemName1(item[i].timeItemName);
                                self.timeItemId1(item[i].timeItemId);
                                self.isRequired1(item[i].useAtr == 1);
                            }

                            if(i == 1) {
                                self.useAtr2(item[i].useAtr);
                                self.timeItemName2(item[i].timeItemName);
                                self.timeItemId2(item[i].timeItemId);
                                self.isRequired2(item[i].useAtr == 1);
                            }

                            if(i == 2) {
                                self.useAtr3(item[i].useAtr);
                                self.timeItemName3(item[i].timeItemName);
                                self.timeItemId3(item[i].timeItemId);
                                self.isRequired3(item[i].useAtr == 1);
                            }

                            if(i == 3) {
                                self.useAtr4(item[i].useAtr);
                                self.timeItemName4(item[i].timeItemName);
                                self.timeItemId4(item[i].timeItemId);
                                self.isRequired4(item[i].useAtr == 1);
                            }

                            if(i == 4) {
                                self.useAtr5(item[i].useAtr);
                                self.timeItemName5(item[i].timeItemName);
                                self.timeItemId5(item[i].timeItemId);
                                self.isRequired5(item[i].useAtr == 1);
                            }

                            if(i == 5) {
                                self.useAtr6(item[i].useAtr);
                                self.timeItemName6(item[i].timeItemName);
                                self.timeItemId6(item[i].timeItemId);
                                self.isRequired6(item[i].useAtr == 1);
                            }

                            if(i == 6) {
                                self.useAtr7(item[i].useAtr);
                                self.timeItemName7(item[i].timeItemName);
                                self.timeItemId7(item[i].timeItemId);
                                self.isRequired7(item[i].useAtr == 1);
                            }

                            if(i == 7) {
                                self.useAtr8(item[i].useAtr);
                                self.timeItemName8(item[i].timeItemName);
                                self.timeItemId8(item[i].timeItemId);
                                self.isRequired8(item[i].useAtr == 1);
                            }

                            if(i == 8) {
                                self.useAtr9(item[i].useAtr);
                                self.timeItemName9(item[i].timeItemName);
                                self.timeItemId9(item[i].timeItemId);
                                self.isRequired9(item[i].useAtr == 1);
                            }

                            if(i == 9) {
                                self.useAtr10(item[i].useAtr);
                                self.timeItemName10(item[i].timeItemName);
                                self.timeItemId10(item[i].timeItemId);
                                self.isRequired10(item[i].useAtr == 1);
                            }                                
                        }                        
                        $($(".itemName[disabled!='disabled']")[0]).focus();
                    }
                });

                service.getListSpecialBonusPayTimeItem().done(function(item: Array<any>) {                    
                    if(!_.isUndefined(item) || !_.isEmpty(item)) {
                        for (let i = 0; i < item.length; i++){
                            if(i == 0) {
                                self.useSpecAtr1(item[i].useAtr);
                                self.timeItemSpecName1(item[i].timeItemName);
                                self.timeItemSpecId1(item[i].timeItemId);
                                self.isSpecRequired1(item[i].useAtr == 1);
                            }

                            if(i == 1) {
                                self.useSpecAtr2(item[i].useAtr);
                                self.timeItemSpecName2(item[i].timeItemName);
                                self.timeItemSpecId2(item[i].timeItemId);
                                self.isSpecRequired2(item[i].useAtr == 1);
                            }

                            if(i == 2) {
                                self.useSpecAtr3(item[i].useAtr);
                                self.timeItemSpecName3(item[i].timeItemName);
                                self.timeItemSpecId3(item[i].timeItemId);
                                self.isSpecRequired3(item[i].useAtr == 1);
                            }

                            if(i == 3) {
                                self.useSpecAtr4(item[i].useAtr);
                                self.timeItemSpecName4(item[i].timeItemName);
                                self.timeItemSpecId4(item[i].timeItemId);
                                self.isSpecRequired4(item[i].useAtr == 1);
                            }

                            if(i == 4) {
                                self.useSpecAtr5(item[i].useAtr);
                                self.timeItemSpecName5(item[i].timeItemName);
                                self.timeItemSpecId5(item[i].timeItemId);
                                self.isSpecRequired5(item[i].useAtr == 1);
                            }

                            if(i == 5) {
                                self.useSpecAtr6(item[i].useAtr);
                                self.timeItemSpecName6(item[i].timeItemName);
                                self.timeItemSpecId6(item[i].timeItemId);
                                self.isSpecRequired6(item[i].useAtr == 1);
                            }

                            if(i == 6) {
                                self.useSpecAtr7(item[i].useAtr);
                                self.timeItemSpecName7(item[i].timeItemName);
                                self.timeItemSpecId7(item[i].timeItemId);
                                self.isSpecRequired7(item[i].useAtr == 1);
                            }

                            if(i == 7) {
                                self.useSpecAtr8(item[i].useAtr);
                                self.timeItemSpecName8(item[i].timeItemName);
                                self.timeItemSpecId8(item[i].timeItemId);
                                self.isSpecRequired8(item[i].useAtr == 1);
                            }

                            if(i == 8) {
                                self.useSpecAtr9(item[i].useAtr);
                                self.timeItemSpecName9(item[i].timeItemName);
                                self.timeItemSpecId9(item[i].timeItemId);
                                self.isSpecRequired9(item[i].useAtr == 1);
                            }

                            if(i == 9) {
                                self.useSpecAtr10(item[i].useAtr);
                                self.timeItemSpecName10(item[i].timeItemName);
                                self.timeItemSpecId10(item[i].timeItemId);
                                self.isSpecRequired10(item[i].useAtr == 1);
                            }                                
                        }                        
                    }
                });
                dfd.resolve();
                return dfd.promise();
            }
            //content
            /**
            * save data and close dialog
            */
            submitAndCloseDialog(): void {
                let self = this;
                $(".premiumName").trigger("validate");
                if (!nts.uk.ui.errors.hasError()) {
                    let bonusPayTimeItemListCommand: Array<any> = [], bonusPayTimeItemSpecListCommand: Array<any> = [],
                         lstUseArt: Array<boolean> = [], lstUseSpecArt: Array<boolean> = [];
                    lstUseArt.push(self.useAtr1() == 1 ? true : false);
                    lstUseArt.push(self.useAtr2() == 1 ? true : false);
                    lstUseArt.push(self.useAtr3() == 1 ? true : false);
                    lstUseArt.push(self.useAtr4() == 1 ? true : false);
                    lstUseArt.push(self.useAtr5() == 1 ? true : false);
                    lstUseArt.push(self.useAtr6() == 1 ? true : false);
                    lstUseArt.push(self.useAtr7() == 1 ? true : false);
                    lstUseArt.push(self.useAtr8() == 1 ? true : false);
                    lstUseArt.push(self.useAtr9() == 1 ? true : false);
                    lstUseArt.push(self.useAtr10() == 1 ? true : false);                    

                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName1(), useAtr: self.useAtr1(),timeItemNo: 1, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });                    
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName2(), useAtr: self.useAtr2(),timeItemNo: 2, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName3(), useAtr: self.useAtr3(),timeItemNo: 3, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName4(), useAtr: self.useAtr4(),timeItemNo: 4, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName5(), useAtr: self.useAtr5(),timeItemNo: 5, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName6(), useAtr: self.useAtr6(),timeItemNo: 6, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName7(), useAtr: self.useAtr7(),timeItemNo: 7, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName8(), useAtr: self.useAtr8(),timeItemNo: 8, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName9(), useAtr: self.useAtr9(),timeItemNo: 9, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                    bonusPayTimeItemListCommand.push({ timeItemName:self.timeItemName10(), useAtr: self.useAtr10(),timeItemNo: 10, timeItemTypeAtr: 0, timeItemId:self.timeItemId1() });
                   
                    lstUseSpecArt.push(self.useSpecAtr1() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr2() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr3() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr4() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr5() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr6() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr7() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr8() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr9() == 1 ? true : false);
                    lstUseSpecArt.push(self.useSpecAtr10() == 1 ? true : false);                    

                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName1(), useAtr: self.useSpecAtr1(),timeItemNo: 1, timeItemTypeAtr: 1 });                    
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName2(), useAtr: self.useSpecAtr2(),timeItemNo: 2, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName3(), useAtr: self.useSpecAtr3(),timeItemNo: 3, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName4(), useAtr: self.useSpecAtr4(),timeItemNo: 4, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName5(), useAtr: self.useSpecAtr5(),timeItemNo: 5, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName6(), useAtr: self.useSpecAtr6(),timeItemNo: 6, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName7(), useAtr: self.useSpecAtr7(),timeItemNo: 7, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName8(), useAtr: self.useSpecAtr8(),timeItemNo: 8, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName9(), useAtr: self.useSpecAtr9(),timeItemNo: 9, timeItemTypeAtr: 1 });
                    bonusPayTimeItemSpecListCommand.push({ timeItemName:self.timeItemSpecName10(), useAtr: self.useSpecAtr10(),timeItemNo: 10, timeItemTypeAtr: 1 });


                    service.checkUseArt(lstUseArt).done(function() {
                        service.getListBonusPTimeItem().done(function(res: Array<any>) {
                            if (res === undefined || res.length == 0) {
                                service.addListBonusPayTimeItem(bonusPayTimeItemListCommand);
                            } else {
                                service.updateListBonusPayTimeItem(bonusPayTimeItemListCommand);
                            }
                            self.closeDialog();
                        })

                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    });

                    service.checkUseArt(lstUseSpecArt).done(function() {
                        service.getListSpecialBonusPayTimeItem().done(function(res: Array<any>) {
                            if (res === undefined || res.length == 0) {
                                service.addListBonusPayTimeItem(bonusPayTimeItemSpecListCommand);
                            } else {
                                service.updateListBonusPayTimeItem(bonusPayTimeItemSpecListCommand);
                            }
                            self.closeDialog();
                        })

                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    });
                }
            }           

            /**
             * close dialog and do nothing
             */
            closeDialog(): void {
                nts.uk.ui.windows.close();
            }
        }

        export class TimeItem {
            timeItemName: KnockoutObservable<string>;
            useAtr: KnockoutObservable<number>;
            timeItemNo: KnockoutObservable<number>;
            timeItemTypeAtr: KnockoutObservable<number>;
            timeItemId: KnockoutObservable<string>;
            constructor(timeItemName: string, useAtr: number, timeItemNo: number, 
                        timeItemTypeAtr: number, timeItemId: string) {
                var self = this;
                self.timeItemName = ko.observable(timeItemName);
                self.useAtr = ko.observable(useAtr);
                self.timeItemNo = ko.observable(timeItemNo);
                self.timeItemTypeAtr = ko.observable(timeItemTypeAtr);
                self.timeItemId = ko.observable(timeItemId);
            }
        }
    }
}