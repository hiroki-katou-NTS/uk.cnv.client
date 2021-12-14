module nts.uk.at.view.kwr001.a {
    import message = nts.uk.resource.getMessage;
    import ComponentOption = kcp.share.list.ComponentOption;
    import service = nts.uk.at.view.kwr001.a.service;
    import blockUI = nts.uk.ui.block;
    
    export module viewmodel {
        
        const DATE_FORMAT_YYYY_MM_DD = "YYYY/MM/DD";
        const STRING_EMPTY = "";
        const SHOW_CHARACTERISTIC = "SHOW_CHARACTERISTIC";
        const OPEN_SCREEN_C = "Open_ScrC";
        
        export class ScreenModel {
            dataOutputType: KnockoutObservableArray<any>;
            
            // datepicker A1_6
            requiredDatePicker: KnockoutObservable<boolean>; 
            enableDatePicker: KnockoutObservable<boolean>; 
            datepickerValue: KnockoutObservable<any>;
            startDatepicker: KnockoutObservable<string>;
            endDatepicker: KnockoutObservable<string>; 
            // datepicker A1_6
            
            // switch button A6_2
            selectedDataOutputType: KnockoutObservable<number>;

            // radio button group A7_2
            selectionType: KnockoutObservable<number> = ko.observable(0);
            standardSettingLayoutId: string;
            freeSettingLayoutId: string;
            freeSettingEnabled: KnockoutObservable<boolean> = ko.observable(false);

            // switch button A8_2
            dataZeroDisplayType: KnockoutObservableArray<any> = ko.observableArray([
                { code: 0, name: nts.uk.resource.getText("KWR001_149") },
                { code: 1, name: nts.uk.resource.getText("KWR001_150") }
            ]);
            selectedDataZeroDisplayType: KnockoutObservable<number> = ko.observable(1);

            // switch button A11_2
            dataDisplayItemType: KnockoutObservableArray<any> = ko.observableArray([
                { code: 0, name: nts.uk.resource.getText("KWR001_151") },
                { code: 1, name: nts.uk.resource.getText("KWR001_152") }
            ]);
            selectedDataDisplayItemType: KnockoutObservable<number> = ko.observable(0);

            // dropdownlist A7_3
            itemListCodeTemplate: KnockoutObservableArray<OutputItemSettingDto>;
            selectedCodeA7_3: KnockoutObservable<string>;

            // dropdownlist A7_8
            outputItemDailyWorkSchedules: KnockoutObservableArray<OutputItemSettingDto>;
            selectedCodeA7_8: KnockoutObservable<string>;

            // dropdownlist A9_2
            itemListTypePageBrake: KnockoutObservableArray<ItemModel>;
            selectedCodeA9_2: KnockoutObservable<string>;
            
            // radio button group A13_1
            itemListConditionSet: KnockoutObservableArray<any>;
            selectedCodeA13_1: KnockoutObservable<number>;
            
            // start variable of CCG001
            ccg001ComponentOption: GroupOption;
            // end variable of CCG001
            
            // start declare KCP005
            listComponentOption: any;
            multiSelectedCode: KnockoutObservableArray<string>;
            isShowAlreadySet: KnockoutObservable<boolean>;
            alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
            isDialog: KnockoutObservable<boolean>;
            isShowNoSelectRow: KnockoutObservable<boolean>;
            isMultiSelect: KnockoutObservable<boolean>;
            isShowWorkPlaceName: KnockoutObservable<boolean>;
            isShowSelectAllButton: KnockoutObservable<boolean>;
            employeeList: KnockoutObservableArray<UnitModel>;
            // end KCP005
            
            checkedA10_2: KnockoutObservable<boolean>;
            checkedA10_3: KnockoutObservable<boolean>;
            checkedA10_4: KnockoutObservable<boolean>;
            checkedA10_5: KnockoutObservable<boolean>;
            checkedA10_6: KnockoutObservable<boolean>;
            checkedA10_7: KnockoutObservable<boolean>;
            
            checkedA10_10: KnockoutObservable<boolean>;
            checkedA10_11: KnockoutObservable<boolean>;
            checkedA10_12: KnockoutObservable<boolean>;
            checkedA10_13: KnockoutObservable<boolean>;
            checkedA10_14: KnockoutObservable<boolean>;
            checkedA10_15: KnockoutObservable<boolean>;
            checkedA10_16: KnockoutObservable<boolean>;
            checkedA10_17: KnockoutObservable<boolean>;
            checkedA10_18: KnockoutObservable<boolean>;
            
            enableByCumulativeWkp: KnockoutObservable<boolean>;
            enableByOutputFormat: KnockoutObservable<boolean>;
            enableBtnConfigure: KnockoutObservable<boolean>;
            enableConfigErrCode: KnockoutObservable<boolean>;
            isEmployeeCharge: KnockoutObservable<boolean>;
            enableA7_2: KnockoutObservable<boolean>;
            enableA7_8: KnockoutObservable<boolean>;
            
            taskId: KnockoutObservable<string>;
            errorLogs : KnockoutObservableArray<EmployeeError>;
            errorLogsNoWorkplace : KnockoutObservableArray<EmployeeError>;
            
            constructor() {
                let self = this;
                
                self.isEmployeeCharge = ko.observable(true);
                self.enableConfigErrCode = ko.observable(true);
                self.enableByOutputFormat = ko.observable(true);
                self.enableBtnConfigure = ko.observable(true);
                self.enableA7_2 = ko.observable(true);
                self.enableA7_8 = ko.observable(false);
                
                self.checkedA10_2 = ko.observable(false);
                self.checkedA10_3 = ko.observable(false);
                self.checkedA10_4 = ko.observable(false);
                self.checkedA10_5 = ko.observable(false);
                self.checkedA10_6 = ko.observable(false);
                self.checkedA10_7 = ko.observable(false);
                
                self.checkedA10_10 = ko.observable(false);
                self.checkedA10_11 = ko.observable(false);
                self.checkedA10_12 = ko.observable(false);
                self.checkedA10_13 = ko.observable(false);
                self.checkedA10_14 = ko.observable(false);
                self.checkedA10_15 = ko.observable(false);
                self.checkedA10_16 = ko.observable(false);
                self.checkedA10_17 = ko.observable(false);
                self.checkedA10_18 = ko.observable(false);
                
                self.enableByCumulativeWkp = ko.observable(true);
                
                self.checkedA10_7.subscribe(function(value) {
                    if (value == true) {
                        self.enableByCumulativeWkp(true);        
                    } else {
                        self.enableByCumulativeWkp(false);    
                    }
                })
                self.checkedA10_7.valueHasMutated();
                
                // start set variable for datepicker A1_6
                self.enableDatePicker = ko.observable(true);
                self.requiredDatePicker = ko.observable(true);
                
                self.startDatepicker = ko.observable("");
                self.endDatepicker = ko.observable("");
                self.datepickerValue = ko.observable({});
                
                self.startDatepicker.subscribe(function(value){
                    self.datepickerValue().startDate = value;
                    self.datepickerValue.valueHasMutated();        
                });
                
                self.endDatepicker.subscribe(function(value){
                    self.datepickerValue().endDate = value;   
                    self.datepickerValue.valueHasMutated();      
                });
                
                self.datepickerValue.subscribe((value) => {
//                    console.log(value);
                })
                // end set variable for datepicker A1_6
                
                self.taskId = ko.observable('');
                self.errorLogs = ko.observableArray([]);
                self.errorLogsNoWorkplace = ko.observableArray([]);
                self.selectionType.subscribe((value) => {
                    self.enableA7_2(value === 0);
                    self.enableA7_8(value === 1);
                    nts.uk.ui.errors.clearAll();

                });
                
                // start set variable for CCG001
                self.ccg001ComponentOption = {
                    /** Common properties */
                    systemType: 2,
                    showEmployeeSelection: false,
                    showQuickSearchTab: true,
                    showAdvancedSearchTab: true,
                    showBaseDate: false,
                    showClosure: true,
                    showAllClosure: true,
                    showPeriod: true,
                    periodFormatYM: false,
                    
                    /** Required parameter */
                    baseDate: moment().toISOString(),
//                    periodStartDate: moment().toISOString(),
//                    periodEndDate: moment().toISOString(),
                    dateRangePickerValue: self.datepickerValue,
                    inService: true,
                    leaveOfAbsence: true,
                    closed: true,
                    retirement: true,
                    
                    /** Quick search tab options */
                    showAllReferableEmployee: true,
                    showOnlyMe: true,
                    showSameWorkplace: true,
                    showSameWorkplaceAndChild: true,
                    
                    /** Advanced search properties */
                    showEmployment: true,
                    showWorkplace: true,
                    showClassification: true,
                    showJobTitle: true,
                    showWorktype: true,
                    isMutipleCheck: true,
                    
                    tabindex: -1,
                    /**
                    * Self-defined function: Return data from CCG001
                    * @param: data: the data return from CCG001
                    */
                    returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                        //画面項目「A1_7」「A1_8」を更新する
                        self.datepickerValue().startDate = moment(data.periodStart).format("YYYY/MM/DD");
                        self.datepickerValue().endDate = moment(data.periodEnd).format("YYYY/MM/DD");
                        self.datepickerValue.valueHasMutated();
                        
                        //self.employeeList.removeAll();
                        var employeeSearchs: UnitModel[] = [];
                        _.forEach(data.listEmployee, function(value) {
                            var employee: UnitModel = {
                                id: value.employeeId,
                                code: value.employeeCode,
                                name: value.employeeName,
                                affiliationName: value.affiliationName
                            };
//                            if (!_.isEmpty(value.workplaceId) && !_.isNil(value.workplaceId)) {
                                employeeSearchs.push(employee);    
//                            }
                        });
                        self.employeeList(employeeSearchs);
                    }
                }
                // end set variable for CCG001
                
                // TODO: hoangdd - goi service lay enum thay cho viec set cung resource
                self.dataOutputType = ko.observableArray([
                    { code: 0, name: nts.uk.resource.getText("KWR001_10") },
                    { code: 1, name: nts.uk.resource.getText("KWR001_11") }
                ]);
                self.selectedDataOutputType = ko.observable(0);
                self.selectedDataOutputType.subscribe(function(value) {
                    if (value == 0) {
                        self.enableByOutputFormat(true);                        
                    } else {
                        self.enableByOutputFormat(false);    
                    }
                })
                self.selectedDataOutputType.valueHasMutated();

                self.itemListCodeTemplate = ko.observableArray([]);
                self.outputItemDailyWorkSchedules = ko.observableArray([]);
                
                // TODO: hoangdd - lay du lieu tu service
                self.itemListTypePageBrake = ko.observableArray([
                    new ItemModel('0', nts.uk.resource.getText("KWR001_161")),
                    new ItemModel('1', nts.uk.resource.getText('KWR001_162')),
                    new ItemModel('2', nts.uk.resource.getText('KWR001_163'))
                ]);
                
                self.selectedCodeA9_2 = ko.observable('0');
                
                self.selectedCodeA7_3 = ko.observable(''); 
                self.selectedCodeA7_8 = ko.observable(''); 
                
                // TODO: hoangdd - lay du lieu tu service
                self.itemListConditionSet = ko.observableArray([
                    new BoxModel(0, nts.uk.resource.getText("KWR001_38")),
                    new BoxModel(1, nts.uk.resource.getText("KWR001_39"))
                ]);
                
                self.selectedCodeA13_1 = ko.observable(0);
                self.selectedCodeA13_1.subscribe(function(value) {
                    if (value==1) {
                        self.enableConfigErrCode(true);
                    } else {
                        self.enableConfigErrCode(false);
                    }
                })
                self.selectedCodeA13_1.valueHasMutated();
                
                self.isEmployeeCharge.subscribe(function(value) {
                    self.enableBtnConfigure(value);
                })
                
                // start define KCP005
                self.multiSelectedCode = ko.observableArray([]);
                self.isShowAlreadySet = ko.observable(false);
                self.alreadySettingList = ko.observableArray([]);
                self.isDialog = ko.observable(true);
                self.isShowNoSelectRow = ko.observable(false);
                self.isMultiSelect = ko.observable(true);
                self.isShowWorkPlaceName = ko.observable(true);
                self.isShowSelectAllButton = ko.observable(false);
                self.employeeList = ko.observableArray<UnitModel>([]);
                self.listComponentOption = {
                    isShowAlreadySet: self.isShowAlreadySet(),
                    isMultiSelect: self.isMultiSelect(),
                    listType: ListType.EMPLOYEE,
                    employeeInputList: self.employeeList,
                    selectType: SelectType.SELECT_ALL,
                    selectedCode: self.multiSelectedCode,
                    isDialog: self.isDialog(),
                    isShowNoSelectRow: self.isShowNoSelectRow(),
                    alreadySettingList: self.alreadySettingList,
                    isShowWorkPlaceName: self.isShowWorkPlaceName(),
                    isShowSelectAllButton: self.isShowSelectAllButton(),
                    isSelectAllAfterReload: true,
                    tabindex: 5,
                    maxRows: 20
                };
                // end define KCP005
            }
            
            public startPage(): JQueryPromise<void>  {
                var dfd = $.Deferred<void>();
                var self = this;
                
                $.when(self.getDataCharateristic()).done(function(dataCharacteristic: any) {
                    let isExist = !(_.isUndefined(dataCharacteristic) || _.isNull(dataCharacteristic));
                    self.getDataStartPageService(isExist).done(function(dataService: any) {
                        if (dataService.standardSetting) {
                            self.itemListCodeTemplate(_.sortBy(dataService.standardSetting.outputItemDailyWorkSchedules, (item: any) => item.code));
                        }
                        if (dataService.freeSetting) {
                            self.outputItemDailyWorkSchedules(_.sortBy(dataService.freeSetting.outputItemDailyWorkSchedules, (item: any) => item.code));
                        }
                        self.isEmployeeCharge(dataService.employeeCharge);
                        self.freeSettingEnabled(dataService.configFreeSetting);
                        switch (dataService.strReturn) {
                            // return screen A, show data from characteristic
                            case SHOW_CHARACTERISTIC:
                                self.renewDataPage();
                                break;
                            // return screen A, don't have data characteristic  
                            case STRING_EMPTY:
                                break;
                            default:
                                break;
                        }
                        if (!_.isNil(dataService.setMsgErrClosingPeriod)) {
                            nts.uk.ui.dialog.alertError({ messageId: dataService.msgErrClosingPeriod});
                        }
                        dfd.resolve();
                    }).fail(function(error) {
                       nts.uk.ui.dialog.alertError(error);
                       dfd.reject();     
                    });
                });
                
                return dfd.promise();
            }
            
            private renewDataPage(): void {
                let self = this;
                let companyId: string = __viewContext.user.companyId;
                let userId: string = __viewContext.user.employeeId;
                service.restoreCharacteristic(companyId, userId).done(function(data: any) {
                    let workScheduleOutputCondition: WorkScheduleOutputConditionDto = data;
                    self.selectedDataOutputType(workScheduleOutputCondition.outputType);
                    self.selectedCodeA7_3(workScheduleOutputCondition.code);
                    self.selectedCodeA9_2(workScheduleOutputCondition.pageBreakIndicator.toString());
                    self.checkedA10_2(workScheduleOutputCondition.settingDetailTotalOutput.details);
                    self.checkedA10_3(workScheduleOutputCondition.settingDetailTotalOutput.personalTotal);
                    self.checkedA10_4(workScheduleOutputCondition.settingDetailTotalOutput.workplaceTotal);
                    // update spec ver 25, only hidden temporary
                    // 2020/08/13 update specs ver 34
                    self.checkedA10_5(workScheduleOutputCondition.settingDetailTotalOutput.totalNumberDay);
                    self.checkedA10_6(workScheduleOutputCondition.settingDetailTotalOutput.grossTotal);
                    self.checkedA10_7(workScheduleOutputCondition.settingDetailTotalOutput.cumulativeWorkplace);
                    if (workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal) {
                        self.checkedA10_10(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.firstLevel);
                        self.checkedA10_11(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.secondLevel);
                        self.checkedA10_12(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.thirdLevel);
                        self.checkedA10_13(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.fourthLevel);
                        self.checkedA10_14(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.fifthLevel);
                        self.checkedA10_15(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.sixthLevel);
                        self.checkedA10_16(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.seventhLevel);
                        self.checkedA10_17(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.eighthLevel);
                        self.checkedA10_18(workScheduleOutputCondition.settingDetailTotalOutput.workplaceHierarchyTotal.ninthLevel);
                    }
                    self.selectedCodeA13_1(workScheduleOutputCondition.conditionSetting);
                    self.freeSettingLayoutId = workScheduleOutputCondition.freeSettingLayoutId;
                    self.standardSettingLayoutId = workScheduleOutputCondition.standardSelectionLayoutId;
                    self.selectedCodeA7_8(workScheduleOutputCondition.freeSettingCode);
                    self.selectedDataZeroDisplayType(workScheduleOutputCondition.zeroDisplayType);
                    self.selectedDataDisplayItemType(workScheduleOutputCondition.switchItemDisplay);
                });
            }
            
            // get data from service
            private getDataStartPageService(isExist: boolean): JQueryPromise<any> {
                var dfd = $.Deferred<any>();
                let self = this;
                service.getDataStartPage(isExist).done(function(data: any) {
                    self.startDatepicker(data.startDate);
                    self.endDatepicker(data.endDate);
                    self.ccg001ComponentOption.baseDate = moment.utc(self.endDatepicker(), DATE_FORMAT_YYYY_MM_DD).toISOString();
                    dfd.resolve(data);
                }).fail(function(error) {
                   nts.uk.ui.dialog.alertError(error);
                   dfd.reject();
                });
                return dfd.promise();
            }
            
            // run after create success html 
            public executeBindingComponent(): void {
                let self = this;
                //re-set value of component
                //対象期間：画面項目「A1_7とA1_8」にセットされている期間
                self.ccg001ComponentOption.periodStartDate =  self.datepickerValue().startDate;
                self.ccg001ComponentOption.periodEndDate =  self.datepickerValue().endDate;
                
                // start component CCG001
                // start component KCP005
                $.when($('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption), 
                    $('#component-items-list').ntsListComponent(self.listComponentOption)).done(function() {
                    $('.ntsStartDatePicker').focus();
                    blockUI.clear();
                });
            }
            
            // get data from characteristic
            private getDataCharateristic(): JQueryPromise<any> {
                var dfd = $.Deferred<any>();
                let self = this;
                
                let companyId: string = __viewContext.user.companyId;
                let userId: string = __viewContext.user.employeeId;

                $.when(service.restoreCharacteristic(companyId, userId)).done(function(data: WorkScheduleOutputConditionDto) {
                if (_.isUndefined(data)) {
                    let totalWorkplaceHierachy = new TotalWorkplaceHierachy(false, false, false, false, false, false, false, false, false);
                    let workScheduleSettingTotalOutput = new WorkScheduleSettingTotalOutput(false, false, false, false, false, false, totalWorkplaceHierachy);
                    let workScheduleOutputCondition = new WorkScheduleOutputConditionDto(companyId // companyId
                                                        , userId                                   // userId
                                                        , 0                                        // ouptType
                                                        , ''                                       // standard code selected
                                                        , 0                                        // pageBreakIndicator
                                                        , workScheduleSettingTotalOutput           // settingDetailTotalOutput
                                                        , 0                                        // conditionSetting
                                                        , 0                                        // selectionType
                                                        , ''                                       // standardSelectionLayoutId
                                                        , ''                                       // freeSettingLayoutId
                                                        , ''                                       // freeSettingCode
                                                        , 0                                        // zeroDisplayType
                                                        , 0                                        // switchItemDisplay
                                                        , null                                     // outputConditionSpecification
                                                        , []);                                     // errorAlarmCode

                    service.saveCharacteristic(companyId, userId, workScheduleOutputCondition);
                }
                
                    dfd.resolve(data);
                });
                
                return dfd.promise();
            }
            
            openScreenB (): void {
                let self = this;
                let companyId: string = __viewContext.user.companyId;
                let userId: string = __viewContext.user.employeeId;
                service.restoreCharacteristic(companyId, userId).done(function(data: any) {
                    let workScheduleOutputCondition: WorkScheduleOutputConditionDto = data;
                    nts.uk.ui.windows.setShared('KWR001_B_errorAlarmCode', _.isUndefined(data) ? [] : workScheduleOutputCondition.errorAlarmCode, true);
                    nts.uk.ui.windows.sub.modal('/view/kwr/001/b/index.xhtml').onClosed(function(): any {
                        workScheduleOutputCondition.errorAlarmCode = nts.uk.ui.windows.getShared('KWR001_B_errorAlarmCode');
                        service.saveCharacteristic(companyId, userId, workScheduleOutputCondition);
                    });  
                })
            }
            openScreenC (): void {
                let self = this;
                let dataTransfer = {
                    codeChoose: undefined,
                    selection: self.selectionType(),
                    layoutId: '',
                };
                if (self.selectionType() === ItemSelectionType.STANDARD_SELECTION) {
                    dataTransfer.codeChoose = self.getCodeFromListCode(self.selectedCodeA7_3(), self.itemListCodeTemplate());
                    dataTransfer.layoutId = self.standardSettingLayoutId;
                } else {
                    dataTransfer.codeChoose = self.getCodeFromListCode(self.selectedCodeA7_8(), self.outputItemDailyWorkSchedules());
                    dataTransfer.layoutId = self.freeSettingLayoutId;
                }
                nts.uk.ui.windows.setShared('KWR001_C', dataTransfer, true);
                nts.uk.ui.windows.sub.modal('/view/kwr/001/c/index.xhtml').onClosed(function(): any {
                    $.when(self.getDataCharateristic()).done(function(dataCharacteristic: any) {
                        let isExist = !(_.isUndefined(dataCharacteristic) || _.isNull(dataCharacteristic));
                        self.getDataStartPageService(isExist).done(function(dataService: any) {
                            if (self.selectionType() === ItemSelectionType.STANDARD_SELECTION) {
                                if (dataService.standardSetting) {
                                    self.itemListCodeTemplate(_.sortBy(dataService.standardSetting.outputItemDailyWorkSchedules, (item: any) => item.code));
                                    if (_.isEmpty(dataService.standardSetting.outputItemDailyWorkSchedules)) {
                                        self.selectedCodeA7_3('');
                                    } else {
                                        self.selectedCodeA7_3(nts.uk.ui.windows.getShared('KWR001_C'));
                                    }
                                } else {
                                    self.itemListCodeTemplate([]);
                                    self.selectedCodeA7_3('');
                                }
                            } else {
                                if (dataService.freeSetting) {
                                    self.outputItemDailyWorkSchedules(_.sortBy(dataService.freeSetting.outputItemDailyWorkSchedules, (item: any) => item.code));
                                    if (_.isEmpty(dataService.freeSetting.outputItemDailyWorkSchedules)) {
                                        self.selectedCodeA7_8('');
                                    } else {
                                        self.selectedCodeA7_8(nts.uk.ui.windows.getShared('KWR001_C'));
                                    }
                                } else {
                                    self.outputItemDailyWorkSchedules([]);
                                    self.selectedCodeA7_8('');
                                }
                            }
                        }).fail(function(error) {
                           nts.uk.ui.dialog.alertError(error);     
                        });
                    });
                });
            }
            
            private getCodeFromListCode(pos: string, lstCode: ItemModel[]): string {
                let self = this;
                let codeChoose = _.find(lstCode, function(o) { 
                    return pos == o.code; 
                });
                if (_.isUndefined(codeChoose)) {
                    return null;
                }
                return codeChoose.code;
            }

            private getLayoutIdFromLst(pos: string, lstCode: OutputItemSettingDto[]): string {
                let self = this;
                let codeChoose = _.find(lstCode, function(o) { 
                    return pos == o.code; 
                });
                if (_.isUndefined(codeChoose)) {
                    return null;
                }
                return codeChoose.layoutId;
            }
            /**
             * find employee id in selected
             */
            private findEmployeeIdByCode(employeeCode: string): string {
                var self = this;
                var employeeId = '';
                for (var employee of self.employeeList()) {
                    if (employee.code === employeeCode) {
                        employeeId = employee.id;
                    }
                }
                return employeeId;
            }
            
            /**
             * find employee id in selected
             */
            private findEmployeeIdsByCodes(employeeCodes: string[]): string[] {
                var self = this;
                var employeeIds: string[] = [];
                for (var employeeCode of employeeCodes) {
                    employeeIds.push(self.findEmployeeIdByCode(employeeCode));
                }
                return employeeIds;
            }
            
            processExcel(): void {
                let self = this;
                if (self.validateMinimumOneChecked()) {
                    self.saveWorkScheduleOutputCondition().done(function() {
                       let companyId: string = __viewContext.user.companyId;
                        let userId: string = __viewContext.user.employeeId;
                        service.restoreCharacteristic(companyId, userId).done(function(data: WorkScheduleOutputConditionDto) {
                            var user: any = __viewContext.user;
                            var dto: WorkScheduleOutputQueryDto = {
                                lstEmployeeId: self.findEmployeeIdsByCodes(self.multiSelectedCode()),
                                startDate: self.toDate(self.datepickerValue().startDate),
                                endDate: self.toDate(self.datepickerValue().endDate),
                                fileType: 0,
                                condition: data,
                                baseDate: self.toDate(self.datepickerValue().endDate)
                            };
                            nts.uk.ui.block.grayout();
                            service.exportExcel(dto).done(function(response){
                                var employeeStr = "";
                                self.errorLogs.removeAll();
                                self.errorLogsNoWorkplace.removeAll();
                                _.forEach(response.taskDatas, item => {
                                    if (item.key.substring(0, 5) == "DATA_") {
                                        var errors = JSON.parse(item.valueAsString);
                                        _.forEach(errors, error => {
                                            var errorEmployee : EmployeeError = {
                                                employeeCode : error.employeeCode,
                                                employeeName : error.employeeName
                                            }   
                                            employeeStr += "\n" + error.employeeCode + " " + error.employeeName;
                                            self.errorLogs.push(errorEmployee);
                                        });
                                    }
                                    else if (item.key.substring(0, 6) == "NOWPK_") {
                                        var errors = JSON.parse(item.valueAsString);
                                        _.forEach(errors, error => {
                                            var errorEmployee : EmployeeError = {
                                                employeeCode : error.employeeCode,
                                                employeeName : error.employeeName
                                            }   
                                            employeeStr += "\n" + error.employeeCode + " " + error.employeeName;
                                            self.errorLogsNoWorkplace.push(errorEmployee);
                                        });
                                    }
                                });
                                // Show error in msg_1344
//                                if (self.errorLogs().length > 0)
//                                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1344", message: message("Msg_1344") + employeeStr, messageParams: [self.errorLogs().length]});
                                if (self.errorLogsNoWorkplace().length > 0)
                                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1396", message: message("Msg_1396") + employeeStr, messageParams: [self.errorLogs().length]});
                            }).fail(function(error){
                                nts.uk.ui.dialog.alertError({ messageId: error.message, messageParams: null});
                            }).always(function() {
                               nts.uk.ui.block.clear(); 
                            });
                            
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alertError(error);
                        }); 
                    });
                }
                
            }
            
            private processPdf(): void {
                let self = this;
                if (self.validateMinimumOneChecked()) {
                    self.saveWorkScheduleOutputCondition().done(function() {
                       let companyId: string = __viewContext.user.companyId;
                        let userId: string = __viewContext.user.employeeId;
                        service.restoreCharacteristic(companyId, userId).done(function(data: WorkScheduleOutputConditionDto) {
                            var user: any = __viewContext.user;
                            var dto: WorkScheduleOutputQueryDto = {
                                lstEmployeeId: self.findEmployeeIdsByCodes(self.multiSelectedCode()),
                                startDate: self.toDate(self.datepickerValue().startDate),
                                endDate: self.toDate(self.datepickerValue().endDate),
                                fileType: 1,
                                condition: data,
                                baseDate: self.toDate(self.datepickerValue().endDate)
                            };
                            nts.uk.ui.block.grayout();
                            service.exportExcel(dto).done(function(response){
                                var employeeStr = "";
                                self.errorLogs.removeAll();
                                self.errorLogsNoWorkplace.removeAll();
                                _.forEach(response.taskDatas, item => {
                                    if (item.key.substring(0, 5) == "DATA_") {
                                        var errors = JSON.parse(item.valueAsString);
                                        _.forEach(errors, error => {
                                            var errorEmployee : EmployeeError = {
                                                employeeCode : error.employeeCode,
                                                employeeName : error.employeeName
                                            }   
                                            employeeStr += "\n" + error.employeeCode + " " + error.employeeName;
                                            self.errorLogs.push(errorEmployee);
                                        });
                                    }
                                    else if (item.key.substring(0, 6) == "NOWPK_") {
                                        var errors = JSON.parse(item.valueAsString);
                                        _.forEach(errors, error => {
                                            var errorEmployee : EmployeeError = {
                                                employeeCode : error.employeeCode,
                                                employeeName : error.employeeName
                                            }   
                                            employeeStr += "\n" + error.employeeCode + " " + error.employeeName;
                                            self.errorLogsNoWorkplace.push(errorEmployee);
                                        });
                                    }
                                });
                                // Show error in msg_1344
//                                if (self.errorLogs().length > 0)
//                                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1344", message: message("Msg_1344") + employeeStr, messageParams: [self.errorLogs().length]});
                                if (self.errorLogsNoWorkplace().length > 0)
                                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1396", message: message("Msg_1396") + employeeStr, messageParams: [self.errorLogs().length]});
                            }).fail(function(error){
                                nts.uk.ui.dialog.alertError({ messageId: error.message, messageParams: null});
                            }).always(function() {
                               nts.uk.ui.block.clear(); 
                            });
                        }); 
                    });
                }
            }
            
            private validateMinimumOneChecked(): boolean {
                let self = this;
                if (self.selectionType() === ItemSelectionType.STANDARD_SELECTION) {
                    $('#combo-box').ntsError('check');
                    if (nts.uk.ui.errors.hasError()) {
                        return false;
                    }
                }

                if (self.selectionType() === ItemSelectionType.FREE_SETTING) {
                    $('#combo-box-free-setting').ntsError('check');
                    if (nts.uk.ui.errors.hasError()) {
                        return false;
                    }
                }
                
                if (self.selectedDataOutputType() == 0) {
                    if (!self.checkedA10_2() && !self.checkedA10_3() && !self.checkedA10_4() 
                    // update spec ver 25, only hidden temporary
                    //  && !self.checkedA10_5()
                        && !self.checkedA10_6() && !self.checkedA10_7()) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_2309" });
                        return false;
                    }    
                } else {
                    if (!self.checkedA10_2() && !self.checkedA10_4() && !self.checkedA10_6() && !self.checkedA10_7()) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_2309" });
                        return false;
                    }
                }
                //職場階層累計をチェック
                if (!self.checkCumulativeWorkHierarchy()) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1184" });
                    return false;
                }
                
                if (this.selectionType() === 0) {
                    if (_.isEmpty(self.selectedCodeA7_3())) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_1141" });
                        return false;
                    }
                } else {
                    if (_.isEmpty(self.selectedCodeA7_8())) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_1141" });
                        return false;
                    }
                }
               
                
                if (_.isEmpty(self.multiSelectedCode())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_884" });
                    return false;
                }
                
                return true;
            }
            
            // 職場階層累計をチェック(Check cumulative work hierarchy)
            private checkCumulativeWorkHierarchy(): boolean {
                let sum: number = 0;
                let self = this;
                if (!self.checkedA10_7()) {
                    return true;
                }
                
                if (self.checkedA10_10()) {
                    sum += 1;
                }
                
                if (self.checkedA10_11()) {
                    sum += 1;
                }
                
                if (self.checkedA10_12()) {
                    sum += 1;
                }
                
                if (self.checkedA10_13()) {
                    sum += 1;
                }
                
                if (self.checkedA10_14()) {
                    sum += 1;
                }
                
                if (self.checkedA10_15()) {
                    sum += 1;
                }
                
                if (self.checkedA10_16()) {
                    sum += 1;
                }
                
                if (self.checkedA10_17()) {
                    sum += 1;
                }
                
                if (self.checkedA10_18()) {
                    sum += 1;
                }
                
                if (sum == 0 || sum >= 6) {
                    return false;
                }
                return true;
            }
            
            private saveWorkScheduleOutputCondition(): JQueryPromise<void> {
                let self = this,
                    dfd = $.Deferred<void>(),
                    companyId: string = __viewContext.user.companyId,
                    userId: string = __viewContext.user.employeeId;
                service.restoreCharacteristic(companyId, userId).done(function(data: any) {
                    // return data default
                    if (!self.checkedA10_7()) {
                        self.checkedA10_10(false);
                        self.checkedA10_11(false);
                        self.checkedA10_12(false);
                        self.checkedA10_13(false);
                        self.checkedA10_14(false);
                        self.checkedA10_15(false);
                        self.checkedA10_16(false);
                        self.checkedA10_17(false);
                        self.checkedA10_18(false);
                    }
                    
                    if (self.selectedDataOutputType() == 1) {
                        self.checkedA10_3(false);
                        // update spec ver 25, only hidden temporary 
                        // self.checkedA10_5(false);
                    }
                    
                    let totalWorkplaceHierachy = new TotalWorkplaceHierachy(self.checkedA10_10(), self.checkedA10_11(), 
                                                                            self.checkedA10_12(), self.checkedA10_13(), 
                                                                            self.checkedA10_14(), self.checkedA10_15(), 
                                                                            self.checkedA10_16(), self.checkedA10_17(), 
                                                                            self.checkedA10_18());
                    let workScheduleSettingTotalOutput = new WorkScheduleSettingTotalOutput(self.checkedA10_2(),
                                                                                            self.checkedA10_3(), 
                                                                                            self.checkedA10_4(),
                                                                                            self.checkedA10_5(), 
                                                                                            self.checkedA10_6(),
                                                                                            self.checkedA10_7(), 
                                                                                            totalWorkplaceHierachy);
                    
                    let codeChoose = self.getCodeFromListCode(self.selectedCodeA7_3(), self.itemListCodeTemplate());
                    let errorAlarmCode: any[];
                    
                    errorAlarmCode = data.errorAlarmCode;
                    let selectionType = self.selectionType();
                    let standardSelectionLayoutId = self.getLayoutIdFromLst(self.selectedCodeA7_3(), self.itemListCodeTemplate());
                    let freeSettingLayoutId = self.getLayoutIdFromLst(self.selectedCodeA7_8(), self.outputItemDailyWorkSchedules());
                    let freeSettingCode = self.getCodeFromListCode(self.selectedCodeA7_8(), self.outputItemDailyWorkSchedules());
                    let zeroDisplayType = self.selectedDataZeroDisplayType();
                    let switchItemDisplay = self.selectedDataDisplayItemType();
                    let outputConditionSpecification = null;

                    let workScheduleOutputCondition = new WorkScheduleOutputConditionDto(companyId
                                                                                        , userId
                                                                                        , self.selectedDataOutputType()
                                                                                        , codeChoose
                                                                                        , Number(self.selectedCodeA9_2())
                                                                                        , workScheduleSettingTotalOutput
                                                                                        , self.selectedCodeA13_1()
                                                                                        , selectionType
                                                                                        , standardSelectionLayoutId
                                                                                        , freeSettingLayoutId
                                                                                        , freeSettingCode
                                                                                        , zeroDisplayType
                                                                                        , switchItemDisplay
                                                                                        , outputConditionSpecification
                                                                                        , errorAlarmCode);
                    service.saveCharacteristic(companyId, userId, workScheduleOutputCondition); 
                    dfd.resolve();
                })
                return dfd.promise();
            }
            
            private toDate(strDate: string): Date {
                return moment(strDate, 'YYYY/MM/DD').toDate();
            }
        }
        
        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export interface UnitModel {
            id: string;
            code: string;
            name?: string;
            affiliationName?: string;
            isAlreadySetting?: boolean;
        }

        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }
        
        class ItemModel {
            code: string;
            name: string;
        
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
        class OutputItemSettingDto {
            code: string;
            name: string;
            layoutId: string;
        
            constructor(code: string, name: string, layoutId: string) {
                this.code = code;
                this.name = name;
                this.layoutId = layoutId;
            }
        }
        
        class BoxModel {
            id: number;
            name: string;
            constructor(id, name){
                var self = this;
                self.id = id;
                self.name = name;
            }
        }

        // start CCG001
        export interface GroupOption {
            /** Common properties */
            showEmployeeSelection: boolean; // 検索タイプ
            systemType: number; // システム区分
            showQuickSearchTab: boolean; // クイック検索
            showAdvancedSearchTab: boolean; // 詳細検索
            showBaseDate: boolean; // 基準日利用
            showClosure: boolean; // 就業締め日利用
            showAllClosure: boolean; // 全締め表示
            showPeriod: boolean; // 対象期間利用
            periodFormatYM: boolean; // 対象期間精度
            isInDialog?: boolean;
        
            /** Required parameter */
            baseDate?: any; // 基準日
            periodStartDate?: string; // 対象期間開始日
            periodEndDate?: string; // 対象期間終了日
            inService: boolean; // 在職区分
            leaveOfAbsence: boolean; // 休職区分
            closed: boolean; // 休業区分
            retirement: boolean; // 退職区分
        
            /** Quick search tab options */
            showAllReferableEmployee: boolean; // 参照可能な社員すべて
            showOnlyMe: boolean; // 自分だけ
            showSameWorkplace: boolean; // 同じ職場の社員
            showSameWorkplaceAndChild: boolean; // 同じ職場とその配下の社員
        
            /** Advanced search properties */
            showEmployment: boolean; // 雇用条件
            showWorkplace: boolean; // 職場条件
            showClassification: boolean; // 分類条件
            showJobTitle: boolean; // 職位条件
            showWorktype: boolean; // 勤種条件
            isMutipleCheck: boolean; // 選択モード
        
            /** Data returned */
            returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
        }

        export interface EmployeeSearchDto {
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            workplaceId: string;
            workplaceName: string;
        }
        export interface Ccg001ReturnedData {
            baseDate: string; // 基準日
            closureId?: number; // 締めID
            periodStart: string; // 対象期間（開始)
            periodEnd: string; // 対象期間（終了）
            listEmployee: Array<EmployeeSearchDto>; // 検索結果
        }
        // end CCG001
        
        class WorkScheduleOutputConditionDto {
            companyId: string;
            userId: string;
            outputType: number;
            code: string;
            pageBreakIndicator: number;
            settingDetailTotalOutput: WorkScheduleSettingTotalOutput;
            conditionSetting: number;
            errorAlarmCode: string[];
            // 項目選択区分
            selectionType: number;
            // 定型選択_出力レイアウトID
            standardSelectionLayoutId: string;
            // 自由設定_出力レイアウトID
            freeSettingLayoutId: string;
            // 自由設定_コード
            freeSettingCode: string;
            // ゼロ表示区分
            zeroDisplayType: number;
            // 項目表示切替
            switchItemDisplay: number;
            // 条件指定
            outputConditionSpecification: OutputConditionSpecification;

            constructor(companyId: string
                , userId: string
                , outputType: number
                , code: string
                , pageBreakIndicator: number
                , settingDetailTotalOuput: WorkScheduleSettingTotalOutput
                , conditionSetting: number
                , selectionType: number
                , standardSelectionLayoutId: string
                , freeSettingLayoutId: string
                , freeSettingCode: string
                , zeroDisplayType: number
                , switchItemDisplay: number
                , outputConditionSpecification?: OutputConditionSpecification
                , errorAlarmCode?: string[]) {
                this.companyId = companyId;
                this.userId = userId;
                this.outputType = outputType;
                this.code = code;
                this.pageBreakIndicator = pageBreakIndicator;
                this.settingDetailTotalOutput = settingDetailTotalOuput;
                this.conditionSetting =  conditionSetting;
                if (errorAlarmCode) {
                    this.errorAlarmCode = errorAlarmCode;
                }
                this.selectionType = selectionType;
                this.standardSelectionLayoutId = standardSelectionLayoutId;
                this.freeSettingLayoutId = freeSettingLayoutId,
                this.freeSettingCode = freeSettingCode;
                this.zeroDisplayType = zeroDisplayType;
                this.switchItemDisplay = switchItemDisplay;
                if (outputConditionSpecification) {
                    this.outputConditionSpecification = outputConditionSpecification;
                }
            }
        }
        
        class WorkScheduleSettingTotalOutput {
            details: boolean;
            personalTotal: boolean;
            workplaceTotal: boolean;
            totalNumberDay: boolean;
            grossTotal: boolean;
            cumulativeWorkplace: boolean;
            
            workplaceHierarchyTotal: TotalWorkplaceHierachy;
            
            constructor( details: boolean, personalTotal: boolean, workplaceTotal: boolean, totalNumberDay: boolean,
                            grossTotal: boolean, cumulativeWorkplace: boolean, workplaceHierarchyTotal?: TotalWorkplaceHierachy) {
                this.details = details;
                this.personalTotal = personalTotal;
                this.workplaceTotal = workplaceTotal;
                this.totalNumberDay = totalNumberDay;
                this.grossTotal = grossTotal;
                this.cumulativeWorkplace = cumulativeWorkplace;
                if (workplaceHierarchyTotal) {
                    this.workplaceHierarchyTotal = workplaceHierarchyTotal;
                }
            }
        }
        
        class WorkScheduleOutputQueryDto {
            lstEmployeeId: Array<string>;
            startDate: Date;
            endDate: Date;
            fileType: number;
            condition: WorkScheduleOutputConditionDto;
            
            constructor(lstEmployeeId: Array<string>, startDate: Date, endDate: Date, condition: WorkScheduleOutputConditionDto, fileType: number) {
                this.lstEmployeeId = lstEmployeeId;
                this.startDate = startDate;
                this.endDate = endDate;
                this.condition = condition;
                this.fileType = fileType;
            }
        }
        
        class TotalWorkplaceHierachy {
            firstLevel: boolean;
            secondLevel: boolean;
            thirdLevel: boolean;
            fourthLevel: boolean;
            fifthLevel: boolean;
            sixthLevel: boolean;
            seventhLevel: boolean;
            eighthLevel: boolean;
            ninthLevel: boolean; 
            
            constructor(firstLevel?: boolean, secondLevel?: boolean, thirdLevel?: boolean, fourthLevel?: boolean,
                            fifthLevel?: boolean, sixthLevel?: boolean, seventhLevel?: boolean, 
                            eighthLevel?: boolean, ninthLevel?: boolean ) {
                if (firstLevel) {
                    this.firstLevel = firstLevel;
                }
                
                if (secondLevel) {
                    this.secondLevel = secondLevel;
                }
                
                if (thirdLevel) {
                    this.thirdLevel = thirdLevel;
                }
                
                if (fourthLevel) {
                    this.fourthLevel = fourthLevel;
                }
                
                if (fifthLevel) {
                    this.fifthLevel = fifthLevel;
                }
                
                if (sixthLevel) {
                    this.sixthLevel = sixthLevel;
                }
                
                if (seventhLevel) {
                    this.seventhLevel = seventhLevel;
                }
                
                if (eighthLevel) {
                    this.eighthLevel = eighthLevel;
                }
                
                if (ninthLevel) {
                    this.ninthLevel = ninthLevel;
                }
            }
        }

        class WorkConditionSpecification {
            // 複数回勤務
            workMultipleTimes: boolean;
            // 臨時勤務
            temporaryService: boolean;
            // 特定日
            specificDay: boolean;
        }

        class ExtractSupportWorkCondition {
            // 通常勤務場所以外
            otherNormalLocation: boolean;
            // 所属職場以外
            otherWorkplace: boolean;
        }

        export class ConfirmedData{
            static EXTRACT = 0;
            static NOT_EXTRACT = 1;
        }

        class OutputConditionSpecification {
            // エラー・アラームの抽出
            alarmExtraction: boolean;
            // 確認済みのデータ抽出
            verifiedDataExtraction: boolean;
            // 勤務条件指定
            workingConditionSpecification: boolean;
            // 応援勤務を抽出
            extractSupportWork: boolean;
            // 勤務種類参照
            workTypeReference: boolean;
            // 就業時間帯参照
            referToWorkingHours: boolean;
            // 確認済みのデータ抽出区分
            confirmedData: ConfirmedData;
            // 勤務条件指定情報
            workConditionSpecification: WorkConditionSpecification;
            // 応援勤務を抽出条件
            extractSupportWorkCondition: ExtractSupportWorkCondition;
            // 勤務種類参照コード
            workTypeReferenceCode: string[];
            // 就業時間帯参照コード
            workHoursReferenceCode: string[];
            // 抽出条件のエラー・アラームコード
            alarmCodeOfExtractionCondition: string[];
        }

        export class ItemSelectionType {
            /** 定型選択 */
            static STANDARD_SELECTION = 0;
            /** 目由設定 */
            static FREE_SETTING = 1;
        }

        class EmployeeError {
            employeeCode: string;
            employeeName: string;
            
            constructor(employeeCode: string, employeeName: string) {
                this.employeeCode = employeeCode;
                this.employeeName = employeeName;
            }
        }
    }
} 