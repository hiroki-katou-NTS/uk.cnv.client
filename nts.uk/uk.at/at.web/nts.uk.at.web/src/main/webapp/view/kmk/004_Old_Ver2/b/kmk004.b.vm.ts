module nts.uk.at.view.kmk004.b {
    export module viewmodel {
        import Common = nts.uk.at.view.kmk004.shared.model;
        import UsageUnitSetting = nts.uk.at.view.kmk004.shared.model.UsageUnitSetting;
        import UsageUnitSettingDto =  nts.uk.at.view.kmk004.e.service.model.UsageUnitSettingDto
        import WorktimeSettingVM = nts.uk.at.view.kmk004.shr.worktime.setting.viewmodel;
        import DeformationLaborSetting = nts.uk.at.view.kmk004.shared.model.DeformationLaborSetting;   
        import FlexSetting = nts.uk.at.view.kmk004.shared.model.FlexSetting;   
        import FlexDaily = nts.uk.at.view.kmk004.shared.model.FlexDaily;   
        import FlexMonth = nts.uk.at.view.kmk004.shared.model.FlexMonth;   
        import NormalSetting = nts.uk.at.view.kmk004.shared.model.NormalSetting;   
        import WorkingTimeSetting = nts.uk.at.view.kmk004.shared.model.WorkingTimeSetting; 
        import Monthly = nts.uk.at.view.kmk004.shared.model.Monthly;   
        import WorktimeSettingDto = nts.uk.at.view.kmk004.shared.model.WorktimeSettingDto; 
        import WorktimeNormalDeformSetting = nts.uk.at.view.kmk004.shared.model.WorktimeNormalDeformSetting;   
        import WorktimeFlexSetting1 = nts.uk.at.view.kmk004.shared.model.WorktimeFlexSetting1; 
        import NormalWorktime = nts.uk.at.view.kmk004.shared.model.NormalWorktime; 
        import FlexWorktimeAggrSetting = nts.uk.at.view.kmk004.shared.model.FlexWorktimeAggrSetting;
        import WorktimeSettingDtoSaveCommand = nts.uk.at.view.kmk004.shared.model.WorktimeSettingDtoSaveCommand;
        import WorktimeNormalDeformSettingDto = nts.uk.at.view.kmk004.shared.model.WorktimeNormalDeformSettingDto;
        import WorktimeFlexSetting1Dto = nts.uk.at.view.kmk004.shared.model.WorktimeFlexSetting1Dto;
        import StatutoryWorktimeSettingDto = nts.uk.at.view.kmk004.shared.model.StatutoryWorktimeSettingDto;
        import MonthlyCalSettingDto = nts.uk.at.view.kmk004.shared.model.MonthlyCalSettingDto;
        import WorktimeSetting = nts.uk.at.view.kmk004.shr.worktime.setting.viewmodel.WorktimeSetting;
        import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
        import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
        import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
        
        import setShared = nts.uk.ui.windows.setShared;
        import getShared = nts.uk.ui.windows.getShared;
        
        export class ScreenModel {
            
            langId: KnockoutObservable<string> = ko.observable('ja');
            tabs: KnockoutObservableArray<NtsTabPanelModel>;
            
            worktimeVM: WorktimeSettingVM.ScreenModel;
            
            isLoading: KnockoutObservable<boolean>;
            
            usageUnitSetting: UsageUnitSetting;
            
            startMonth: KnockoutObservable<number>;
            
            selectedEmployeeId: KnockoutObservable<string>;
            displayEmployeeCode: KnockoutObservable<string>;
            displayEmployeeName: KnockoutObservable<string>;
            
            // Employee tab
            lstPersonComponentOption: any;
            selectedEmployeeCode: KnockoutObservable<string>;
            employeeName: KnockoutObservable<string>;
            employeeList: KnockoutObservableArray<UnitModel>;
            alreadySettingPersonal: KnockoutObservableArray<any>;
            ccgcomponentPerson: GroupOption;
            
            ccgcomponent: GroupOption;
            // Options
            isQuickSearchTab: KnockoutObservable<boolean>;
            isAdvancedSearchTab: KnockoutObservable<boolean>;
            isAllReferableEmployee: KnockoutObservable<boolean>;
            isOnlyMe: KnockoutObservable<boolean>;
            isEmployeeOfWorkplace: KnockoutObservable<boolean>;
            isEmployeeWorkplaceFollow: KnockoutObservable<boolean>;
            isMutipleCheck: KnockoutObservable<boolean>;
            isSelectAllEmployee: KnockoutObservable<boolean>;
            periodStartDate: KnockoutObservable<moment.Moment>;
            periodEndDate: KnockoutObservable<moment.Moment>;
            baseDate: KnockoutObservable<moment.Moment>;
            selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;
            showEmployment: KnockoutObservable<boolean>; // ????????????
            showWorkplace: KnockoutObservable<boolean>; // ????????????
            showClassification: KnockoutObservable<boolean>; // ????????????
            showJobTitle: KnockoutObservable<boolean>; // ????????????
            showWorktype: KnockoutObservable<boolean>; // ????????????
            inService: KnockoutObservable<boolean>; // ????????????
            leaveOfAbsence: KnockoutObservable<boolean>; // ????????????
            closed: KnockoutObservable<boolean>; // ????????????
            retirement: KnockoutObservable<boolean>; // ????????????
            systemType: KnockoutObservable<number>;
            showClosure: KnockoutObservable<boolean>; // ?????????????????????
            showBaseDate: KnockoutObservable<boolean>; // ???????????????
            showAllClosure: KnockoutObservable<boolean>; // ???????????????
            showPeriod: KnockoutObservable<boolean>; // ??????????????????
            periodFormatYM: KnockoutObservable<boolean>; // ??????????????????
            
            constructor() {
                let self = this;
                self.isLoading = ko.observable(true);
                
                self.usageUnitSetting = new UsageUnitSetting();
                // Datasource.
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText("KMK004_3"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText("KMK004_4"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-3', title: nts.uk.resource.getText("KMK004_5"), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                
                self.worktimeVM = new WorktimeSettingVM.ScreenModel();
                self.selectedEmployee = ko.observableArray([]);
                self.selectedEmployeeCode = ko.observable('');
                self.alreadySettingPersonal = ko.observableArray([]);
                
                self.selectedEmployeeId = ko.observable('');
                self.displayEmployeeCode = ko.observable('');
                self.displayEmployeeName = ko.observable('');
                
                // initial ccg options
                self.setDefaultCcg001Option();
                
                // Init component.
                self.reloadCcg001();
                
                self.selectedEmployeeCode.subscribe(function(code){
                    if (code != null && code.length > 0) {
                        let empt = _.find(self.selectedEmployee(), item => item.employeeCode == code);
                        if (empt) {
                            self.selectedEmployeeId(empt.employeeId);
                            self.displayEmployeeCode(code);
                            self.displayEmployeeName(empt.employeeName);
                            self.worktimeVM.isNewMode( !(_.find(self.alreadySettingPersonal(), item => item.code == code ) != null) );
                            return;
                        }
                    }
                    self.selectedEmployeeId('');
                    self.displayEmployeeCode('');
                    self.displayEmployeeName('');
                    self.worktimeVM.isNewMode(true);
                });
                
                self.worktimeVM.groupYear.subscribe(val => {
                    // Validate
                    if ($('#worktimeYearPicker').ntsError('hasError')) {
                        self.clearError();
                        // Reset year if has error.
                        self.worktimeVM.groupYear(new Date().getFullYear());
                        return;
                    } else {
                        self.loadEmployeeSetting();
                    }
                });
            }

            public startPage(): JQueryPromise<void> {
                nts.uk.ui.block.invisible();
                let self = this;
                let dfd = $.Deferred<void>();
                
                self.isLoading(true);

                // Load component.
                Common.loadUsageUnitSetting().done((res: UsageUnitSettingDto) => {
                    self.usageUnitSetting.employee(res.employee);
                    self.usageUnitSetting.employment(res.employment);
                    self.usageUnitSetting.workplace(res.workPlace);
                    
                    self.worktimeVM.initialize().done(() => {
                        WorktimeSettingVM.getStartMonth().done((month) => {
                            self.startMonth = ko.observable(month);
                            
                            nts.uk.characteristics.restore('KMK004RefFlexPred').done(vl => {
                                if (!_.isNil(vl)) {
                                    self.worktimeVM.worktimeSetting.setReferenceFlexPred(vl);
                                }
                            });
                            dfd.resolve();
                        }).fail(() => {
                            nts.uk.ui.block.clear();
                        });
                    }).fail(() => {
                        nts.uk.ui.block.clear();
                    });
                }).fail(() => {
                    nts.uk.ui.block.clear();
                });
                return dfd.promise();
            }
            
            public postBindingProcess(): void {
                let self = this;
                
                $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent).done(function() {
                    self.employeeList = ko.observableArray<UnitModel>([]);
                    self.applyKCP005ContentSearch([]);
                    
                    // Load employee list component
                    $('#list-employee').ntsListComponent(self.lstPersonComponentOption).done(function() {
                        if(self.employeeList().length <= 0){
                            $('#ccg001-btn-search-drawer').trigger('click');  
                        }
                        
                        self.isLoading(false);
                        
                        self.loadEmployeeSetting(); // load setting for initial selection
                        
                        // subscribe to furture selection
                        self.selectedEmployeeId.subscribe(function(Id) {
                            self.loadEmployeeSetting();
                        });
                        
                        ko.applyBindingsToNode($('#lblEmployeeCode')[0], { text: self.displayEmployeeCode });
                        ko.applyBindingsToNode($('#lblEmployeeName')[0], { text: self.displayEmployeeName });
                        
                        self.worktimeVM.postBindingHandler();
                    });
                });
            }
            
            /**
             * Set default ccg001 options
             */
            public setDefaultCcg001Option(): void {
                let self = this;
                self.isQuickSearchTab = ko.observable(true);
                self.isAdvancedSearchTab = ko.observable(true);
                self.isAllReferableEmployee = ko.observable(true);
                self.isOnlyMe = ko.observable(true);
                self.isEmployeeOfWorkplace = ko.observable(true);
                self.isEmployeeWorkplaceFollow = ko.observable(true);
                self.isMutipleCheck = ko.observable(true);
                self.isSelectAllEmployee = ko.observable(true);
                self.baseDate = ko.observable(moment());
                self.periodStartDate = ko.observable(moment());
                self.periodEndDate = ko.observable(moment());
                self.showEmployment = ko.observable(true); // ????????????
                self.showWorkplace = ko.observable(true); // ????????????
                self.showClassification = ko.observable(true); // ????????????
                self.showJobTitle = ko.observable(true); // ????????????
                self.showWorktype = ko.observable(true); // ????????????
                self.inService = ko.observable(true); // ????????????
                self.leaveOfAbsence = ko.observable(true); // ????????????
                self.closed = ko.observable(true); // ????????????
                self.retirement = ko.observable(true); // ????????????
                self.systemType = ko.observable(2); // ??????
                self.showClosure = ko.observable(true); // ?????????????????????
                self.showBaseDate = ko.observable(true); // ???????????????
                self.showAllClosure = ko.observable(true); // ???????????????
                self.showPeriod = ko.observable(true); // ??????????????????
                self.periodFormatYM = ko.observable(false); // ??????????????????
            }
            
            /**
             * Reload component CCG001
             */
            public reloadCcg001(): void {
                var self = this;
                var periodStartDate, periodEndDate : string;
                if (self.showBaseDate()){
                    periodStartDate = moment(self.periodStartDate()).format("YYYY-MM-DD");
                    periodEndDate = moment(self.periodEndDate()).format("YYYY-MM-DD");
                } else {
                    periodStartDate = moment(self.periodStartDate()).format("YYYY-MM");
                    periodEndDate = moment(self.periodEndDate()).format("YYYY-MM"); // ?????????????????????
                }
                
                if (!self.showBaseDate() && !self.showClosure() && !self.showPeriod()){
                    nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!" );
                    return;
                }
                self.ccgcomponent = {
                    /** Common properties */
                    systemType: self.systemType(), // ??????????????????
                    showEmployeeSelection: self.isSelectAllEmployee(), // ???????????????
                    showQuickSearchTab: self.isQuickSearchTab(), // ??????????????????
                    showAdvancedSearchTab: self.isAdvancedSearchTab(), // ????????????
                    showBaseDate: self.showBaseDate(), // ???????????????
                    showClosure: self.showClosure(), // ?????????????????????
                    showAllClosure: self.showAllClosure(), // ???????????????
                    showPeriod: self.showPeriod(), // ??????????????????
                    periodFormatYM: self.periodFormatYM(), // ??????????????????

                    /** Required parameter */
                    baseDate: moment(self.baseDate()).format("YYYY-MM-DD"), // ?????????
                    periodStartDate: periodStartDate, // ?????????????????????
                    periodEndDate: periodEndDate, // ?????????????????????
                    inService: self.inService(), // ????????????
                    leaveOfAbsence: self.leaveOfAbsence(), // ????????????
                    closed: self.closed(), // ????????????
                    retirement: self.retirement(), // ????????????

                    /** Quick search tab options */
                    showAllReferableEmployee: self.isAllReferableEmployee(), // ??????????????????????????????
                    showOnlyMe: self.isOnlyMe(), // ????????????
                    showSameWorkplace: self.isEmployeeOfWorkplace(), // ?????????????????????
                    showSameWorkplaceAndChild: self.isEmployeeWorkplaceFollow(), // ????????????????????????????????????

                    /** Advanced search properties */
                    showEmployment: self.showEmployment(), // ????????????
                    showWorkplace: self.showWorkplace(), // ????????????
                    showClassification: self.showClassification(), // ????????????
                    showJobTitle: self.showJobTitle(), // ????????????
                    showWorktype: self.showWorktype(), // ????????????
                    isMutipleCheck: self.isMutipleCheck(), // ???????????????

                    /** Return data */
                    returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                        self.selectedEmployee(data.listEmployee);
                        
                        service.findAlreadySetting().done((settingData) => {
                            let listCode = _.map(self.selectedEmployee(), item => {
                                if ( _.find(settingData, it => { return it.employeeId == item.employeeId; } )) return item.employeeCode;
                                return null; 
                            });
                            listCode = _.filter(listCode, item => { return item != null });
                            let listSelected = [];
                            
                            _.each(listCode, item => {
                                listSelected.push( { code: item, isAlreadySetting: true } );
                            });
                            
                            self.alreadySettingPersonal(listSelected);
                            self.applyKCP005ContentSearch(self.selectedEmployee());
                        });
                    }
                }
            }
            
            /**
            * apply ccg001 search data to kcp005
            */
            public applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): void {
                var self = this;
                self.employeeList([]);
                var employeeSearchs: UnitModel[] = [];
                for (var employeeSearch of dataList) {
                    var employee: UnitModel = {
                        code: employeeSearch.employeeCode,
                        name: employeeSearch.employeeName,
                        affiliationName: employeeSearch.affiliationName
                    };
                    employeeSearchs.push(employee);
                }
                self.employeeList(employeeSearchs);
                self.selectedEmployeeCode(employeeSearchs.length > 0 ? employeeSearchs[0].code : '');
                self.lstPersonComponentOption = {
                    isShowAlreadySet: true,
                    isMultiSelect: false,
                    listType: ListType.EMPLOYEE,
                    employeeInputList: self.employeeList,
                    selectType: SelectType.SELECT_FIRST_ITEM,
                    selectedCode: self.selectedEmployeeCode,
                    isDialog: false,
                    isShowNoSelectRow: false,
                    alreadySettingList: self.alreadySettingPersonal,
                    isShowWorkPlaceName: true,
                    isShowSelectAllButton: true,
                    maxWidth: 385,
                    maxRows: 15
                };
            }
            
            private loadEmployeeSetting(): void {
                let self = this;
                
                let year = self.worktimeVM.worktimeSetting.normalSetting().year();
                if(nts.uk.util.isNullOrEmpty(year)) {
                    return;
                }
                
                let empId = self.selectedEmployeeId();
                if (nts.uk.text.isNullOrEmpty(empId)) {
                    self.resetFieldsToNewMode();
                    return;
                }
                
                nts.uk.ui.block.invisible();
                service.findEmployeeSetting(self.worktimeVM.worktimeSetting.normalSetting().year(), empId).done((data) => {
                    self.clearError();
                    let resultData: WorktimeSettingDto = data;
                    // update mode.
                    // Check condition: ???????????????????????????????????????????????????????????????????????????
                    if (!nts.uk.util.isNullOrEmpty(data.statWorkTimeSetDto) 
                    && !nts.uk.util.isNullOrEmpty(data.statWorkTimeSetDto.regularLaborTime)) {
                        let isModeNew = false;
                        
                        if (nts.uk.util.isNullOrEmpty(data.statWorkTimeSetDto.normalSetting)) {
                            resultData.statWorkTimeSetDto.normalSetting = new WorktimeNormalDeformSettingDto();
                            
                            isModeNew = true;
                        }
                        if (nts.uk.util.isNullOrEmpty(data.statWorkTimeSetDto.flexSetting)) {
                            resultData.statWorkTimeSetDto.flexSetting = new WorktimeFlexSetting1Dto();
                        }
                        if (nts.uk.util.isNullOrEmpty(data.statWorkTimeSetDto.deforLaborSetting)) {
                            resultData.statWorkTimeSetDto.deforLaborSetting = new WorktimeNormalDeformSettingDto();
                        }
                        // Update Full Data
                        self.worktimeVM.worktimeSetting.updateFullData(resultData);
                        self.worktimeVM.worktimeSetting.updateYear(data.statWorkTimeSetDto.year);
                        
                        // Sort month.
                        self.worktimeVM.worktimeSetting.sortMonth(self.worktimeVM.startMonth());
                        
                        self.worktimeVM.isNewMode(isModeNew);
                    }
                    else {
                        self.resetFieldsToNewMode();
                    }
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }
            
            private resetFieldsToNewMode(): void {
                let self = this;
                
                // new mode.
                self.worktimeVM.isNewMode(true);
                let newSetting = new WorktimeSettingDto();
                // Reserve selected year.
                newSetting.updateYear(self.worktimeVM.worktimeSetting.normalSetting().year());
                // Update Full Data
                self.worktimeVM.worktimeSetting.updateFullData(ko.toJS(newSetting));
                
                // Sort month.
                self.worktimeVM.worktimeSetting.sortMonth(self.worktimeVM.startMonth());
            }
            
            public saveEmployeeSetting(): void {
                let self = this;
                // Validate
                if (self.worktimeVM.hasError()) {
                    return;
                }
                nts.uk.ui.block.invisible();
                
                let saveCommand: EmployeeWorktimeSettingDtoSaveCommand = new EmployeeWorktimeSettingDtoSaveCommand();
                saveCommand.updateData(self.selectedEmployeeId(), self.worktimeVM.worktimeSetting);
                
                service.saveEmployeeSetting(ko.toJS(saveCommand)).done(() => {
                    self.worktimeVM.isNewMode(false);
                    self.addAlreadySettingPersonal(self.displayEmployeeCode());
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alertError(error);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }
            
            public removeEmployeeSetting(): void {
                let self = this;
                nts.uk.ui.block.invisible();
                
                let sid = self.selectedEmployeeId();
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    service.removeEmployeeSetting({ year: self.worktimeVM.worktimeSetting.normalSetting().year(), sid: sid }).done((res) => {
                        
                        // new mode.
                        self.worktimeVM.isNewMode(true);
                        let newSetting = new WorktimeSettingDto();
                        // Reserve selected year.
                        newSetting.updateYear(self.worktimeVM.worktimeSetting.normalSetting().year());
                        
                        //check common is remove
                        if (res.wtsettingCommonRemove) {
                            self.removeAlreadySettingPersonal(self.displayEmployeeCode());
                            
                            // Update Full Data
                            self.worktimeVM.worktimeSetting.updateFullData(ko.toJS(newSetting));
                        } else {
                            newSetting.statWorkTimeSetDto.regularLaborTime.updateData(self.worktimeVM.worktimeSetting.normalWorktime());
                            newSetting.statWorkTimeSetDto.transLaborTime.updateData(self.worktimeVM.worktimeSetting.deformLaborWorktime());
                            self.worktimeVM.worktimeSetting.updateDataDependOnYear(newSetting.statWorkTimeSetDto);
                        }
                        
                        self.worktimeVM.worktimeSetting.sortMonth(self.worktimeVM.startMonth());
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                })
            }
            
            /**
             * Add alreadySetting employment.
             */
            private addAlreadySettingPersonal(code:string): void {
                let self = this;
                let l = self.alreadySettingPersonal().filter(i => code == i.code);
                if (l[0]) {
                    return;
                }
                self.alreadySettingPersonal.push({ code: code, isAlreadySetting: true });
            }
            
            /**
             * Remove alreadySetting employment.
             */
            private removeAlreadySettingPersonal(code: string): void {
                let self = this;
                let ase = self.alreadySettingPersonal().filter(i => code == i.code)[0];
                self.alreadySettingPersonal.remove(ase);
            }
            
            /**
             * Clear all errors.
             */
            private clearError(): void {
                let self = this;
                if (nts.uk.ui._viewModel) {
                    // Clear error inputs
                    $('.nts-input').ntsError('clear');
                }
            }
            
            saveAsExcel(): void {
                let self = this;
                let params = {
                    date: null,
                    mode: 5
                };
                if (!nts.uk.ui.windows.getShared("CDL028_INPUT")) {
                    nts.uk.ui.windows.setShared("CDL028_INPUT", params);
                }  
               nts.uk.ui.windows.sub.modal("../../../../../nts.uk.com.web/view/cdl/028/a/index.xhtml").onClosed(function() {
                   var result = getShared('CDL028_A_PARAMS');
                   if (result.status) {
                        nts.uk.ui.block.grayout();
                        service.findprogramName("KMK004", "A").done(function(res:String ) {
                           let domainType = "KMK004";
                           res = res.split(" ");
                           if (res.length > 1) {
                               res.shift();
                               domainType = domainType + res.join(" ");
                           }
                            let startDate = moment.utc(result.startDateFiscalYear ,"YYYY/MM/DD");
                            let endDate = moment.utc(result.endDateFiscalYear ,"YYYY/MM/DD") ;
                            service.saveAsExcel(domainType, startDate, endDate).done(function() {
                            }).fail(function(error) {
                                nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                            }).always(function() {
                                nts.uk.ui.block.clear();
                            });
                       
                       }).always(function() {
                            nts.uk.ui.block.clear();
                       });
                   }           
               });
            }
        } // --- end ScreenModel
        
        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export interface UnitModel {
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
 
        class EmployeeStatutoryWorktimeSettingDto extends StatutoryWorktimeSettingDto {
            employeeId: string;
        }
        
        class EmployeeMonthlyCalSettingDto extends MonthlyCalSettingDto {
            employeeId: string;
        }
        
        export class EmployeeWorktimeSettingDtoSaveCommand {

            /** The save com stat work time set command. */
            saveStatCommand: EmployeeStatutoryWorktimeSettingDto;
    
            /** The save com flex command. */
            saveMonthCommand: EmployeeMonthlyCalSettingDto;
    
            constructor() {
                let self = this;
                self.saveStatCommand = new EmployeeStatutoryWorktimeSettingDto();
                self.saveMonthCommand = new EmployeeMonthlyCalSettingDto();
            }
    
            public updateYear(year: number): void {
                let self = this;
                self.saveStatCommand.year = year;
            }
    
            public updateData(employeeId: string, model: WorktimeSetting): void {
                let self = this;
                self.saveStatCommand.employeeId = employeeId;
                self.saveStatCommand.updateData(model);
                self.saveMonthCommand.employeeId = employeeId;
                self.saveMonthCommand.updateData(model);
            }
        }
    }
}