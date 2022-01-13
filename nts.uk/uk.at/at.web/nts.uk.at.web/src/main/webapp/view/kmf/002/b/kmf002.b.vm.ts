module nts.uk.at.view.kmf002.b {
    
    import service = nts.uk.at.view.kmf002.b.service;
    import blockUI = nts.uk.ui.block;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import CommonTableMonthDaySet = nts.uk.at.view.kmf002.viewmodel.CommonTableMonthDaySet;
    export module viewmodel {
        export class ScreenModel {
            commonTableMonthDaySet: KnockoutObservable<CommonTableMonthDaySet>;
            multiSelectedWorkplaceId: KnockoutObservableArray<any>;
            baseDate: KnockoutObservable<Date>;
            alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel[]>;
            treeGrid: TreeComponentOption;
            workplaceNameSelected: KnockoutObservable<string>;
            enableSave: KnockoutObservable<boolean>;
            enableDelete: KnockoutObservable<boolean>;
            isContinueRecurFindName: KnockoutObservable<boolean>;
            startMonth: number;

            constructor(){
                let self = this;
                self.commonTableMonthDaySet = ko.observable(new CommonTableMonthDaySet());
                self.commonTableMonthDaySet().visibleInfoSelect(true);
                self.commonTableMonthDaySet().infoSelect1(nts.uk.resource.getText("Com_Workplace"));
                self.enableSave = ko.observable(true);
                self.enableDelete = ko.observable(true)
                self.workplaceNameSelected = ko.observable("");
                self.isContinueRecurFindName = ko.observable(true);
                
                self.baseDate = ko.observable(new Date());
                self.multiSelectedWorkplaceId = ko.observableArray([]);
                self.alreadySettingList = ko.observableArray([]);
                self.treeGrid = {
                        isShowAlreadySet: true,
                        isMultiSelect: false,
                        treeType: TreeType.WORK_PLACE,
                        selectedId: self.multiSelectedWorkplaceId,
                        baseDate: self.baseDate,
                        selectType: SelectionType.SELECT_FIRST_ITEM,
                        isShowSelectButton: true,
                        isDialog: false,
                        alreadySettingList: self.alreadySettingList,
                        maxRows: 20,
                        tabindex: 3,
                        systemType : SystemType.EMPLOYMENT
                };
                
                self.multiSelectedWorkplaceId.subscribe(function(newValue) {
                    try {
                        if (_.isUndefined(newValue)) {
                            self.commonTableMonthDaySet().infoSelect2('');
                            self.commonTableMonthDaySet().infoSelect3('');
                            self.enableSave(false);
                            self.enableDelete(false);     
                            self.setDefaultMonthDay();
                        } else {
                            self.commonTableMonthDaySet().infoSelect2($('#tree-grid').getRowSelected()[0].workplaceCode);
                            self.getNameWkpSelect($('#tree-grid').getDataList(), 
                                                   $('#tree-grid').getRowSelected()[0].workplaceCode);
                            
                            self.commonTableMonthDaySet().infoSelect3(self.workplaceNameSelected());
                            self.getDataFromService();
                            self.isContinueRecurFindName(true);
                            self.enableSave(true);
                        }
                    }
                    catch (e){
                    } 
                });
                
                self.commonTableMonthDaySet().fiscalYear.subscribe(function(newValue) {
                    // change year
                    if (!nts.uk.ui.errors.hasError()) {
                        self.getDataFromService();  
                        $.when(service.findAll(self.commonTableMonthDaySet().fiscalYear())).done(function(data3: any) {
                                self.alreadySettingList.removeAll();
                                _.forEach(data3, function(wkpID) {
                                self.alreadySettingList.push({'workplaceId': wkpID, 'isAlreadySetting': true});
                            });        
                        })
                    }
                });                
                // self.getHolidayConfig();
            }
            
            private setDefaultMonthDay(): void {
                let self = this;
                for (let i=0; i<self.commonTableMonthDaySet().arrMonth().length; i++) {
                    self.commonTableMonthDaySet().arrMonth()[i].day(0); 
                }     
            }
            
            private getNameWkpSelect(data: any, codeSelect: string): void {
                let self = this;
                if (self.isContinueRecurFindName() == false) {
                    return;
                }
                _.forEach(data, function(obj: any) {
                    if (obj.code == codeSelect) {
                        self.workplaceNameSelected(obj.name);
                        self.isContinueRecurFindName(false);
                        return false;
                    } else {
                        if (typeof obj.childs !== "undefined" && obj.childs.length > 0) {
                            if (obj.code == codeSelect) {
                                self.workplaceNameSelected(obj.name);
                                self.isContinueRecurFindName(false);
                                return;
                            } else {
                               self.getNameWkpSelect(obj.childs, codeSelect);    
                            }
                        }    
                    }
                });                       
            }
            
            /**
             * init default data when start page
             */
            public start_page(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                var self = this;
                 $.when($('#tree-grid').ntsTreeComponent(self.treeGrid), service.findAll(self.commonTableMonthDaySet().fiscalYear())).done(function(data: any, data2: any) {
                    self.alreadySettingList.removeAll();
                    _.forEach(data2, function(wkpID) {
                        self.alreadySettingList.push({'workplaceId': wkpID, 'isAlreadySetting': true});
                    });        
                   self.getDataFromService();
                   self.baseDate(new Date(self.commonTableMonthDaySet().fiscalYear(), self.commonTableMonthDaySet().arrMonth()[0].month()-1, 2));
                    
                   if (_.isEmpty($('#tree-grid').getRowSelected())) {
                        self.enableSave(false);
                   } else {
                       self.multiSelectedWorkplaceId.valueHasMutated();
                   }
                  
                   dfd.resolve();
                })
               return dfd.promise();   
            }
            
            // public saveWorkpalce(): void {
            //     let self = this;
                
            //     if (!nts.uk.ui.errors.hasError()) {
            //         self.enableSave(false);
            //         blockUI.invisible();
            //         service.save(self.commonTableMonthDaySet().fiscalYear(), 
            //                         self.commonTableMonthDaySet().arrMonth(), 
            //                         $('#tree-grid').getRowSelected()[0].workplaceId).done((data) => {
            //         self.getDataFromService();
            //         self.alreadySettingList.push({'workplaceId': self.multiSelectedWorkplaceId(), 'isAlreadySetting': true});
                                             
            //         nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
            //                 self.enableSave(true);
            //             });
            //     }).always(()=> blockUI.clear());    
            //     }  
            // }
            
            public saveWorkpalce(): void {
                let self = this;
                
                if (!nts.uk.ui.errors.hasError()) {
                    self.enableSave(false);
                    blockUI.invisible();
                    service.save(self.commonTableMonthDaySet().fiscalYear(), 
                                    self.commonTableMonthDaySet().arrMonth(), 
                                    self.multiSelectedWorkplaceId()).done((data) => {
                    self.getDataFromService();
                    self.alreadySettingList.push({'workplaceId': self.multiSelectedWorkplaceId(), 'isAlreadySetting': true});
                                             
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            self.enableSave(true);
                        });
                }).always(()=> blockUI.clear());    
                }  
            }
            public removeWorkplace(): void {
                let self = this;
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                    service.remove(self.commonTableMonthDaySet().fiscalYear(), 
                      self.startMonth, self.multiSelectedWorkplaceId()).done(() => {
                        self.getDataFromService();     
                        self.alreadySettingList.remove(function(s) { return s.workplaceId == self.multiSelectedWorkplaceId() });           
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    });  
                }).ifNo(() => {
                }).ifCancel(() => {
                }).then(() => {
                });   
            }
            
            
            public getDataFromService(): void {
                let self = this;
                if (nts.uk.ui.errors.hasError()) {
                    self.dataDefault();
                    return;
                }
                if (!_.isNull(self.multiSelectedWorkplaceId()) && !_.isEmpty(self.multiSelectedWorkplaceId())) {
                    if (_.isEmpty(self.commonTableMonthDaySet().fiscalYear())) {
                        self.commonTableMonthDaySet().fiscalYear(moment().format('YYYY'));
                    }
                    const currentYear = self.commonTableMonthDaySet().fiscalYear();
                const nextYear = moment.utc(currentYear, "YYYY").add(1, 'year').format("YYYY");
                    $.when(service.find(currentYear, self.multiSelectedWorkplaceId()), 
                            service.find(nextYear, self.multiSelectedWorkplaceId()),
                            service.findFirstMonth()).done(function(data: any, data1: any, data2: any) {
                      self.startMonth = data2.startMonth;
                      // Get current year data
                      let currentYearData: model.PublicHolidayMonthSettingDto[] = _.filter(data?.publicHolidayMonthSettings || [], item => item.month >= data2.startMonth);
                      const hasCurrentData = !_.isEmpty(currentYearData);
                      // Add default values if not exists
                      for (let i = data2.startMonth; i <= 12; i++) {
                        if (!_.find(currentYearData, item => item.month === i)) {
                          currentYearData.push(new model.PublicHolidayMonthSettingDto(currentYear, i, 0));
                        }
                      }
                      currentYearData = _.orderBy(currentYearData, "month");
                      // Get next year data
                      let nextYearData: model.PublicHolidayMonthSettingDto[] = _.filter(data1?.publicHolidayMonthSettings || [], item => item.month < data2.startMonth);
                      const hasNextData = !_.isEmpty(nextYearData);
                      // Add default values if not exists
                      for (let i = 1; i < data2.startMonth; i++) {
                        if (!_.find(nextYearData, item => item.month === i)) {
                          nextYearData.push(new model.PublicHolidayMonthSettingDto(nextYear, i, 0));
                        }
                      }
                      nextYearData = _.orderBy(nextYearData, "month");
                      const arr = _.chain(currentYearData).concat(nextYearData)
                      .map(item => {
                        return {
                          'month': ko.observable(item.month),
                          'day': ko.observable(item.inLegalHoliday),
                          'enable': ko.observable(true)
                        };
                      }).value();
                      self.commonTableMonthDaySet().arrMonth(arr);  
                      self.enableDelete(hasCurrentData || hasNextData);
                    });
                    
                } else {
                    self.dataDefault();
                    self.enableDelete(false);
                }
            }
            
            private dataDefault(): void {
                let self = this;
                _.forEach(self.commonTableMonthDaySet().arrMonth(), function(value: any) {
                    value.day(0);
                });
                self.commonTableMonthDaySet().infoSelect2('');
                self.commonTableMonthDaySet().infoSelect3('');
            }
            
             // excle
           public opencdl028Dialog2() {
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
    
        class SystemType {
            // 個人情報
            static PERSONAL_INFORMATION: number = 1;
             // 就業
            static EMPLOYMENT: number = 2;
             // 給与
            static SALARY: number = 3;
            // 人事
            static HUMAN_RESOURCES: number = 4;
            // 管理者
            static ADMINISTRATOR: number = 5;
        }  

        export class TreeType {
             static WORK_PLACE = 1;
         }

        export class SelectionType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }
    }
}