module nts.uk.at.view.kmf002.e.sidebar {
    
    import viewModelTabB = nts.uk.at.view.kmf002.b.viewmodel;
    import viewModelTabC = nts.uk.at.view.kmf002.c.viewmodel;
    import viewModelTabD = nts.uk.at.view.kmf002.d.viewmodel;
    import viewModelTabA = nts.uk.at.view.kmf002.a;
    import viewModelTabE = nts.uk.at.view.kmf002.e.viewmodel;
    
    import viewModelTabF = nts.uk.at.view.kmf002.f.viewmodel;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import blockUI = nts.uk.ui.block;
    
    import service = nts.uk.at.view.kmf002.e.sidebar.service;
    
    export module viewmodel {
        export class SidebarScreenModel {
            screenB: KnockoutObservable<any>;
            screenC: KnockoutObservable<any>;
            screenD: KnockoutObservable<any>;
            screenE: KnockoutObservable<any>;
            
            /* main define variable code */
            managePublicHoliday: KnockoutObservable<number> = ko.observable(null);
            publicHdCarryOverDeadline: KnockoutObservable<number> = ko.observable(null);
            carryOverNumberOfPublicHdIsNegative: KnockoutObservable<number> = ko.observable(0);
            publicHolidayPeriod: KnockoutObservable<number> = ko.observable(null);

            companyManageClassification: KnockoutObservableArray<any> = ko.observableArray([]);
            lstManagementPeriod: KnockoutObservableArray<any> = ko.observableArray([]);         
            dayOfPublicHoliday: KnockoutObservableArray<any> = ko.observableArray([]);
            
            // enableFullDate: KnockoutObservable<boolean> = ko.observable(true); 
            lstCarryOverDeadline: KnockoutObservableArray<any> = ko.observableArray([]);
            
            lstStartDayOfWeek: KnockoutObservableArray<any> = ko.observableArray([]); 
            
            isDisableSetUnitBtn: KnockoutObservable<boolean> = ko.observable(false);
            enablePubHDPeriod: KnockoutObservable<boolean> = ko.observable(true);            
            enableCarryOverDeadline: KnockoutObservable<boolean> = ko.observable(true);
            enableCarryOverNumberOfPublicHdIsNegative: KnockoutObservable<boolean> = ko.observable(true);

            
            isManageEmployeePublicHd: KnockoutObservable<number>;
            isManageWkpPublicHd: KnockoutObservable<number>;
            isManageEmpPublicHd: KnockoutObservable<number>;
            
            date: KnockoutObservable<string> = ko.observable('20000101');
            
            constructor(){
                let self = this;
                self.screenC = ko.observable(new viewModelTabC.ScreenModel());
                self.screenD = ko.observable(new viewModelTabD.ScreenModel());
                self.screenB = ko.observable(new viewModelTabB.ScreenModel());
                self.screenE = ko.observable(new viewModelTabE.ScreenModel());   

                self.isManageEmployeePublicHd = ko.observable(1);
                self.isManageWkpPublicHd = ko.observable(1);
                self.isManageEmpPublicHd = ko.observable(1);
                
                // start subscribe
                self.managePublicHoliday.subscribe( (newValue: number) => {
                    self.condition();
                    self.conditionSideBar5();
                });

                self.carryOverNumberOfPublicHdIsNegative.subscribe(function(newValue) {
                    if (newValue == 1) {
                        self.carryOverNumberOfPublicHdIsNegative(BoolValue.TRUE);        
                    } else {
                        self.carryOverNumberOfPublicHdIsNegative(BoolValue.FALSE);
                    }
                });                
                
                self.isManageEmpPublicHd.subscribe(function(newValue) {
                    self.conditionSideBar3();
                });
                
                self.isManageEmployeePublicHd.subscribe(function(newValue) {
                    self.conditionSideBar4();
                });
                
                self.isManageWkpPublicHd.subscribe(function(newValue) {
                    self.conditionSideBar2();
                });
            }
            
            public startPage(typeStart: number): JQueryPromise<any> {                
                var dfd = $.Deferred<any>();
                
                let self = this;
                $.when(self.getAllData(), self.findAllManageUseUnit()).then(() => {
                  // load all
                  if (typeStart == SideBarTabIndex.SECOND) {
                    nts.uk.ui.errors.clearAll()
                    blockUI.grayout();
                    // self.screenB(new viewModelTabB.ScreenModel());
                    setTimeout(() => {
                      $.when(self.screenB().start_page()).done(function() {
                        dfd.resolve(self);
                        blockUI.clear();
                        $( "#scrB .datePickerYear" ).focus();
                      });    
                    }, 500);
                } else if (typeStart == SideBarTabIndex.FIRST) {
                    nts.uk.ui.errors.clearAll()
                    blockUI.grayout();
                    // self.screenE(new viewModelTabE.ScreenModel());
                    setTimeout(() => {
                      $.when(self.screenE().start_page()).done(function() {
                        dfd.resolve(self);
                        blockUI.clear();
                        $( "#scrE .datePickerYear" ).focus();
                      });
                    }, 500);
                } else if (typeStart == SideBarTabIndex.THIRD) {
                    nts.uk.ui.errors.clearAll()
                    blockUI.grayout();
                    // self.screenD(new viewModelTabD.ScreenModel());
                    setTimeout(() => {
                      $.when(self.screenD().start_page()).done(function() {
                          dfd.resolve(self);
                          blockUI.clear();
                          $( "#scrD .datePickerYear" ).focus();
                      });
                    }, 500);
                } else if (typeStart == SideBarTabIndex.FOURTH) {
                    nts.uk.ui.errors.clearAll();
                    blockUI.grayout();
                    // self.screenC(new viewModelTabC.ScreenModel());
                    setTimeout(() => {
                      $.when(self.screenC().start_page()).done(function() {
                          dfd.resolve(self);
                          blockUI.clear();
                          $( "#scrC .datePickerYear" ).focus();
                      });    
                    }, 500);
                  }
                })
                
                return dfd.promise();
            }
            
            /**
             * on select tab handle
             */
            
            public onSelectTabB(): void {
                let self = this;
                if (self.isDisableTab() == false) {
                    $("#sidebar").ntsSideBar("init", {
                        active: SideBarTabIndex.SECOND,
                        activate: (event, info) => {
                            self.startPage(SideBarTabIndex.SECOND);
                        }
                    });
                }
            }
            
            public onSelectTabC(): void {
                let self = this;
                if (self.isDisableTab() == false) {
                    $("#sidebar").ntsSideBar("init", {
                        active: SideBarTabIndex.FOURTH,
                        activate: (event, info) => {
                            self.startPage(SideBarTabIndex.FOURTH);
                        }
                    });
                }
            }
            
            public onSelectTabD(): void {
                let self = this;
                if (self.isDisableTab() == false) {
                    $("#sidebar").ntsSideBar("init", {
                        active: SideBarTabIndex.THIRD,
                        activate: (event, info) => {
                            self.startPage(SideBarTabIndex.THIRD);
                        }
                    });    
                }
            }
            
            public onSelectTabE(): void {
                let self = this;
                if (self.isDisableTab() == false) {
                    $("#sidebar").ntsSideBar("init", {
                        active: SideBarTabIndex.FIRST,
                        activate: (event, info) => {
                            self.startPage(SideBarTabIndex.FIRST);
                        }
                    });        
                }
            }
            
            private isDisableTab(): boolean {
                let self = this;
                if (self.managePublicHoliday() == ManagePubHD.MANAGE) {
                    return false;
                }
                return true;
            }
            
            /**
             *  process for screen A
             */
            public save(): void {
                let self = this,
                data: any = {};
                data.managePublicHoliday = self.managePublicHoliday();
                data.publicHdCarryOverDeadline = self.publicHdCarryOverDeadline();
                data.carryOverNumberOfPublicHdIsNegative = self.carryOverNumberOfPublicHdIsNegative();
                data.publicHolidayPeriod = self.publicHolidayPeriod();

                nts.uk.ui.errors.clearAll();
                self.validateInput();
                self.checkValidate();
               
                if (!nts.uk.ui.errors.hasError()) {
                    blockUI.invisible();
                    $.when(service.save(data).done(function() {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                        self.getAllData();
                    }).fail(function(res) {
                        nts.uk.ui.dialog.alertError(res);
                    }).always(()=> blockUI.clear()));    
                }
            }
            
            private checkValidate(): void {
                let self = this;
                if ( self.managePublicHoliday() == ManagePubHD.NOT_MANAGE ) {
                    nts.uk.ui.errors.clearAll();    
                } 
            }
            
            private validateInput(): void {
                $('#inLegalHoliday16').ntsEditor("validate");
                $('#outLegalHoliday17').ntsEditor("validate");
                $('#inLegalHoliday18').ntsEditor("validate");
                $('#outLegalHoliday19').ntsEditor("validate");
                $('#inLegalHoliday20').ntsEditor("validate");
                $('#outLegalHoliday21').ntsEditor("validate");
                $('#inLegalHoliday22').ntsEditor("validate");
                $('#outLegalHoliday23').ntsEditor("validate");
                $('#inLegalHoliday24').ntsEditor("validate");
                $('#outLegalHoliday25').ntsEditor("validate");
            }
            
            private getAllData(): JQueryPromise<any> {
                let self = this;
                var dfd = $.Deferred();
                $.when(service.findAll()).done(function(data: any) {  
//                    if (!_.isUndefined(data) && !_.isNull(data) && !_.isEmpty(data)) {
//                        self.managePublicHoliday(data.managePublicHoliday);
//                        
//                        self.carryOverNumberOfPublicHdIsNegative(data.carryOverNumberOfPublicHdIsNegative);
//                        self.publicHolidayPeriod(data.publicHolidayPeriod);
//                        self.publicHdCarryOverDeadline(data.publicHdCarryOverDeadline);                       
//                    } else {
//                        self.managePublicHoliday(ManagePubHD.MANAGE);
//                        self.enableCarryOverDeadline(true);    
//                        self.enableCarryOverNumberOfPublicHdIsNegative(true);
//                        self.enablePubHDPeriod(true);       
//                        self.isDisableSetUnitBtn(false) ;
//                        
//                    }
                    if (_.isNil(data)) {
                      dfd.resolve();
                      return;
                    }
                    if (data.managePublicHoliday == null) {
                        self.managePublicHoliday(ManagePubHD.MANAGE);
                        self.isDisableSetUnitBtn(false) ;
                        
                    } else {
                        self.managePublicHoliday(data.managePublicHoliday)
                    }
                    if (data.carryOverNumberOfPublicHdIsNegative == null) {
                        self.enableCarryOverNumberOfPublicHdIsNegative(true);
                    }
                    else {
                        self.carryOverNumberOfPublicHdIsNegative(data.carryOverNumberOfPublicHdIsNegative);
                    }
                    if (data.publicHolidayPeriod == null) {
                        self.enablePubHDPeriod(true);
                    } else {
                        self.publicHolidayPeriod(data.publicHolidayPeriod);
                    }
                    if (data.publicHdCarryOverDeadline == null) {
                        self.enableCarryOverDeadline(true);
                    }
                    else {
                        self.publicHdCarryOverDeadline(data.publicHdCarryOverDeadline);
                    }
                    // notify variable observable
                    // self.managePublicHoliday.valueHasMutated();
                    // self.publicHolidayPeriod.valueHasMutated();
                    // self.carryOverNumberOfPublicHdIsNegative.valueHasMutated();  
                    dfd.resolve();   
                });
                return dfd.promise();
            }
            private conditionSideBar2(): void {
                let self = this;
                if (self.isManageWkpPublicHd() == BoolValue.TRUE ) {
                    $("#sidebar").ntsSideBar("show", SideBarTabIndex.SECOND);
                } else {
                    $("#sidebar").ntsSideBar("hide", SideBarTabIndex.SECOND);
                }
            }
            
            private conditionSideBar3(): void {
                let self = this;
                if (self.isManageEmpPublicHd() == BoolValue.TRUE) {
                    $("#sidebar").ntsSideBar("show", SideBarTabIndex.THIRD);
                } else {
                    $("#sidebar").ntsSideBar("hide", SideBarTabIndex.THIRD);
                }
            }
            
            private conditionSideBar4(): void {
                let self = this;
                if (self.isManageEmployeePublicHd() == BoolValue.TRUE) {
                    $("#sidebar").ntsSideBar("show", SideBarTabIndex.FOURTH);
                } else {
                    $("#sidebar").ntsSideBar("hide", SideBarTabIndex.FOURTH);
                }
            }
            
            
            private conditionSideBar5(): void {
                let self = this;
                if (self.managePublicHoliday() == ManagePubHD.MANAGE) {
                    $("#sidebar").ntsSideBar("enable", SideBarTabIndex.FIRST);
                    $("#sidebar").ntsSideBar("enable", SideBarTabIndex.SECOND);
                    $("#sidebar").ntsSideBar("enable", SideBarTabIndex.THIRD);
                    $("#sidebar").ntsSideBar("enable", SideBarTabIndex.FOURTH);
                    setShared('conditionSidebar5', true);
                } else {
                    $("#sidebar").ntsSideBar("disable", SideBarTabIndex.FIRST);
                    $("#sidebar").ntsSideBar("disable", SideBarTabIndex.SECOND);
                    $("#sidebar").ntsSideBar("disable", SideBarTabIndex.THIRD);
                    $("#sidebar").ntsSideBar("disable", SideBarTabIndex.FOURTH);
                    setShared('conditionSidebar5', false);
                }
            }
            
            private condition(): void {
                let self = this;
                if (self.managePublicHoliday() == ManagePubHD.MANAGE) {
                    self.enableCarryOverDeadline(true);    
                    self.enableCarryOverNumberOfPublicHdIsNegative(true);
                    self.enablePubHDPeriod(true);       
                    self.isDisableSetUnitBtn(false)             
                } else {
                    self.enableCarryOverDeadline(false);    
                    self.enableCarryOverNumberOfPublicHdIsNegative(false);
                    self.enablePubHDPeriod(false);     
                    self.isDisableSetUnitBtn(true);                   
                }    
            }            
            
            private getAllEnum(): JQueryPromise<any> {
                let self = this;
                var dfd = $.Deferred();
                $.when(service.getPubHDPeriodEnum(), service.getDayOfPubHDEnum(), service.getPublicHolidayCarryOverDeadline(),
                        service.getDaysOfTheWeek(), service.getManageEnum()).done(function(pubHDPeriodEnum: any, 
                                                                    dayOfPubHDEnum: any,                                                                    
                                                                    publicHolidayCarryOverDeadline: any, 
                                                                    daySOfTheWeek: any,
                                                                    manage: any) {
                    // todo: set enum
                    self.setPubHDPeriodEnum(pubHDPeriodEnum);
                    self.setDayOfPubHDEnum(dayOfPubHDEnum);
                    self.setPublicHolidayCarryOverDeadlineEnum(publicHolidayCarryOverDeadline);
                    self.setDaysOfTheWeekEnum(daySOfTheWeek);
                    self.setManageEnum(manage);
                    dfd.resolve();
                });    
                return dfd.promise();
            }
                
            private setPubHDPeriodEnum(pubHDPeriodEnum: any): void {
                let self = this;
                if (pubHDPeriodEnum.length > 0 && pubHDPeriodEnum.length != self.lstManagementPeriod.length) {
                    self.lstManagementPeriod.removeAll();
                    _.forEach(pubHDPeriodEnum, function(obj) {
                        self.lstManagementPeriod.push({"code":obj.value , "name":obj.localizedName});  
                    });    
                }
            }
                
            private setDayOfPubHDEnum(dayOfPubHDEnum: any): void {
                let self = this;
                if (dayOfPubHDEnum.length > 0 && dayOfPubHDEnum.length != self.dayOfPublicHoliday.length) {
                  self.dayOfPublicHoliday.removeAll();
                    _.forEach(dayOfPubHDEnum, function(obj) {
                        self.dayOfPublicHoliday.push({"id":obj.value , "name":obj.localizedName, "enable":true});  
                    });                    
                }
            }
            private setPublicHolidayCarryOverDeadlineEnum(pubHDCarryOverDeadline: any): void {
                let self = this;
                if (pubHDCarryOverDeadline.length > 0 && pubHDCarryOverDeadline.length != self.lstCarryOverDeadline.length) {
                    self.lstCarryOverDeadline.removeAll();
                    _.forEach(pubHDCarryOverDeadline, function(obj) {
                        self.lstCarryOverDeadline.push({"code":obj.value , "name":obj.localizedName});  
                    });    
                }
            }    
                
            private setDaysOfTheWeekEnum(daySOfTheWeek: any): void {
                let self = this;
                if (daySOfTheWeek.length > 0 && daySOfTheWeek.length != self.lstStartDayOfWeek.length) {
                    self.lstStartDayOfWeek.removeAll();
                    _.forEach(daySOfTheWeek, function(obj) {
                        self.lstStartDayOfWeek.push({"code":obj.value , "name":obj.localizedName});  
                    });    
                }
            }    
            
            private setManageEnum(manage: any): void {
                let self = this;
                self.companyManageClassification.removeAll();
                    _.forEachRight(manage, function(obj) {
                        self.companyManageClassification.push({"id":obj.value , "name":obj.localizedName});  
                    });    
            }
            
            private settingOfUsageUnit(): void {
                let self = this;
                $.when(self.findAllManageUseUnit()).done(function() {
                    setShared('valScreenF', "valScreenF", true);
                    setShared('isManageEmpPublicHd', self.isManageEmpPublicHd());
                    setShared('isManageEmployeePublicHd', self.isManageEmployeePublicHd());
                    setShared('isManageWkpPublicHd', self.isManageWkpPublicHd());
                    nts.uk.ui.windows.sub.modal("/view/kmf/002/f/index.xhtml").onClosed(function() {
                        if (getShared('saveManageUnit') == true) {
                            // F2_6
                            self.isManageEmployeePublicHd(getShared('isManageEmployeePublicHd'));
                            // F2_5
                            self.isManageWkpPublicHd(getShared('isManageWkpPublicHd'));
                            // F2_4
                            self.isManageEmpPublicHd(getShared('isManageEmpPublicHd'));
                            self.isManageEmployeePublicHd.valueHasMutated();
                            self.isManageWkpPublicHd.valueHasMutated();
                            self.isManageEmpPublicHd.valueHasMutated();
                            $.when(service.saveManageUnit(self.isManageEmployeePublicHd(), self.isManageWkpPublicHd(), 
                                                            self.isManageEmpPublicHd())).done(function(data: any) {
                            });    
                        }
                    });    
                });
            } 
            
            private findAllManageUseUnit(): JQueryPromise<any> {
                let self = this;
                var dfd = $.Deferred<any>();
                $.when(service.findAllManageUseUnit()).done(function(data: any) {                     
                    self.isManageEmployeePublicHd(data.isManageEmployeePublicHd);
                    self.isManageWkpPublicHd(data.isManageWkpPublicHd);
                    self.isManageEmpPublicHd(data.isManageEmpPublicHd);
                    dfd.resolve(self);
                });
                return dfd.promise();    
            }
            
            
            // excle
           public opencdl028Dialog() {
                var self = this;
                let params = {
                    date: moment(new Date()).toDate(),
                    mode: 5
                };
    
                nts.uk.ui.windows.setShared("CDL028_INPUT", params);
    
                nts.uk.ui.windows.sub.modal("com", "/view/cdl/028/a/index.xhtml").onClosed(function() {
                    var params = nts.uk.ui.windows.getShared("CDL028_A_PARAMS");
                    console.log(params);
                    
                    if (params.status) {
                        let startDate = moment.utc(params.startDateFiscalYear ,"YYYY/MM/DD");
                        let endDate = moment.utc(params.endDateFiscalYear ,"YYYY/MM/DD") ;
                        self.exportExcel(params.mode, startDate, endDate);
                     }
                });
            }                                                           
        
            /**
             * Print file excel
             */
            exportExcel(mode: string, startDate: string, endDate: string) : void {
                var self = this;
                nts.uk.ui.block.grayout();
                service.saveAsExcel(mode, startDate, endDate).done(function() {
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
            }
            
            
       }
        
        export enum ManagePubHD {
            MANAGE = 1,
            NOT_MANAGE = 0,
        }
        
        export enum PublicHDPeriod {
            ONE_MONTH = 0,
            FOUR_WEEK_FOUR_DAYOFF = 1
        }
        
        export enum BoolValue {
            TRUE = 1,
            FALSE = 0    
        }
        
        export enum TypeDate {
            YEAR_MONTH_DAY = 0,
            MONTH_DAY = 1    
        }
    }
    
    module SideBarTabIndex {                     
        export const FIRST = 0;
        export const SECOND = 1;
        export const THIRD = 2;
        export const FOURTH = 3;
    }
}