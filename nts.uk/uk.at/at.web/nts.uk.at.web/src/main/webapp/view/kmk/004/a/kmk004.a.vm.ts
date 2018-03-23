module nts.uk.at.view.kmk004.a {
    export module viewmodel {
        import UsageUnitSettingService = nts.uk.at.view.kmk004.e.service;
//        import ShareModel = nts.uk.at.view.kmk004.share.model;

        export class ScreenModel {
            // =============OLD===================
            tabs: KnockoutObservableArray<NtsTabPanelModel>;
//            itemList: KnockoutObservableArray<ItemModel>;

            companyWTSetting: CompanyWTSetting;
            employmentWTSetting: EmploymentWTSetting;
            workplaceWTSetting: WorkPlaceWTSetting;
            usageUnitSetting: UsageUnitSetting;

            // Employment list component.
            employmentComponentOption: any;
            alreadySettingEmployments: KnockoutObservableArray<any>;
            selectedEmploymentCode: KnockoutObservable<string>;

            // Workplace list component.
            workplaceComponentOption: any;
            selectedWorkplaceId: KnockoutObservable<string>;
            alreadySettingWorkplaces: KnockoutObservableArray<any>;
            baseDate: KnockoutObservable<Date>;

            // Flag.
            isNewMode: KnockoutObservable<boolean>;
            isLoading: KnockoutObservable<boolean>;
            isCompanySelected: KnockoutObservable<boolean>;
            isEmploymentSelected: KnockoutObservable<boolean>;
            isWorkplaceSelected: KnockoutObservable<boolean>;
            isEmployeeSelected: KnockoutObservable<boolean>;

            // Start month.
            startMonth: KnockoutObservable<number>;
            //===============NEW=================================
            // Update
            aggrSelectionItemList: KnockoutObservableArray<any>;// FlexAggregateMethod フレックス集計方法
            selectedAggrSelection: KnockoutObservable<number>;
            
            companyWTDetailSetting: CompanyWTDetailSetting;
            employeeWTDetailSetting: EmployeeWTDetailSetting;
            employmentWTDetailSetting: EmploymentWTDetailSetting;
            workplaceWTDetailSetting: WorkplaceWTDetailSetting;
            
            // =========DETAIL COMPANY SETTING (screen A)
            // Normal Setting
            companyNormalMonthlySet: CompanyNormalMonthlySet;
            // Flex Setting
            companyFlexMonthlySet: CompanyFlexMonthlySet;
            // Deform labor Setting
            companyDeformMonthlySet: CompanyDeformMonthlySet;
            
            // =========DETAIL EMPLOYEE SETTING (screen B)
            // Normal Setting
            employeeNormalMonthlySet: EmployeeNormalMonthlySet;
            // Flex Setting
            employeeFlexMonthlySet: EmployeeFlexMonthlySet;
            // Deform labor Setting
            employeeDeformMonthlySet: EmployeeDeformMonthlySet;
            
            // =========DETAIL EMPLOYMENT SETTING (screen C)
            // Normal Setting
            employmentNormalMonthlySet: EmploymentNormalMonthlySet;
            // Flex Setting
            employmentFlexMonthlySet: EmploymentFlexMonthlySet;
            // Deform labor Setting
            employmentDeformMonthlySet: EmploymentDeformMonthlySet;
            
            // =========DETAIL WORKPLACE SETTING (screen D)
            // Normal Setting
            workplaceNormalMonthlySet: WorkplaceNormalMonthlySet;
            // Flex Setting
            workplaceFlexMonthlySet: WorkplaceFlexMonthlySet;
            // Deform labor Setting
            workplaceDeformMonthlySet: WorkplaceDeformMonthlySet;
            
            //=========================NEWEST===========================
            // For Scr ABCD
            worktimeSetting: WorktimeSetting;
            // for Screen B
            employeeID: KnockoutObservable<string>;
            employeeCode: KnockoutObservable<string>;
            employeeName: KnockoutObservable<string>;
            // for Screen C
            employmentCode: KnockoutObservable<string>;
            employmentName: KnockoutObservable<string>;
            // for Screen D
            wkpID: KnockoutObservable<string>;
            wkpCode: KnockoutObservable<string>;
            wkpName: KnockoutObservable<string>;

            constructor() {
                let self = this;

                // Flag.
                self.isNewMode = ko.observable(true);
                self.isLoading = ko.observable(true);
                self.isCompanySelected = ko.observable(false);
                self.isEmploymentSelected = ko.observable(false);
                self.isWorkplaceSelected = ko.observable(false);
                self.isEmployeeSelected = ko.observable(false);

                // Datasource.
                self.tabs = ko.observableArray([
                    { id: 'tab-1', title: nts.uk.resource.getText("KMK004_3"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-2', title: nts.uk.resource.getText("KMK004_4"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                    { id: 'tab-3', title: nts.uk.resource.getText("KMK004_5"), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) }
                ]);
                self.baseDate = ko.observable(new Date());

                // Data model.
                self.usageUnitSetting = new UsageUnitSetting();
                self.companyWTSetting = new CompanyWTSetting();
                self.employmentWTSetting = new EmploymentWTSetting();
                self.workplaceWTSetting = new WorkPlaceWTSetting();

                // YEAR SUBSCRIBE

                // Employment list component.
                self.alreadySettingEmployments = ko.observableArray([]);
                self.selectedEmploymentCode = ko.observable('');
                self.setEmploymentComponentOption();
                self.selectedEmploymentCode.subscribe(code => {
                    if (code) {
                        self.loadEmploymentSetting(code);
                    }
                });

                // Workplace list component.
                self.alreadySettingWorkplaces = ko.observableArray([]);
                self.selectedWorkplaceId = ko.observable('');
                self.setWorkplaceComponentOption();
                self.selectedWorkplaceId.subscribe(code => {
                    if (code) {
                        self.loadWorkplaceSetting(code);
                    }
                });

                // Enable/Disable handler.
                (<any>ko.bindingHandlers).allowEdit = {
                    update: function(element, valueAccessor) {
                        if (valueAccessor()) {
                            element.disabled = false;
                            element.readOnly = false;

                            if (element.tagName === 'FIELDSET') {
                                $(':input', element).removeAttr('disabled');
                            }
                        } else {
                            element.disabled = true;
                            element.readOnly = true;

                            if (element.tagName === 'FIELDSET') {
                                $(':input', element).attr('disabled', 'disabled');
                            }
                        }
                    }
                };
                
                // Update
                self.aggrSelectionItemList = ko.observableArray([
                    { id: 1, name: nts.uk.resource.getText("KMK004_51")},
                    { id: 2, name: nts.uk.resource.getText("KMK004_52")}
                ]);
                self.selectedAggrSelection = ko.observable(1);
                
                //==================UPDATE==================
                self.companyWTDetailSetting = new CompanyWTDetailSetting();
                self.employeeWTDetailSetting = new EmployeeWTDetailSetting();
                self.employmentWTDetailSetting = new EmploymentWTDetailSetting();
                self.workplaceWTDetailSetting = new WorkplaceWTDetailSetting();
                
                // Company Detail Setting
                self.companyNormalMonthlySet = new CompanyNormalMonthlySet();
                self.companyFlexMonthlySet = new CompanyFlexMonthlySet();
                self.companyDeformMonthlySet = new CompanyDeformMonthlySet();
                // Employee Detail Setting
                self.employeeNormalMonthlySet = new EmployeeNormalMonthlySet();
                self.employeeFlexMonthlySet = new EmployeeFlexMonthlySet();
                self.employeeDeformMonthlySet = new EmployeeDeformMonthlySet();
                // Employment Detail Setting
                self.employmentNormalMonthlySet = new EmploymentNormalMonthlySet();
                self.employmentFlexMonthlySet = new EmploymentFlexMonthlySet();
                self.employmentDeformMonthlySet = new EmploymentDeformMonthlySet();
                // Workplace Detail Setting
                self.workplaceNormalMonthlySet = new WorkplaceNormalMonthlySet();
                self.workplaceFlexMonthlySet = new WorkplaceFlexMonthlySet();
                self.workplaceDeformMonthlySet = new WorkplaceDeformMonthlySet();
                
                self.companyWTDetailSetting.year.subscribe(val => {
                    // Validate
                    if ($('#companyYearPicker').ntsError('hasError')) {
                        return;
                    } else {
                        self.loadCompanySetting();
                    }
                });
                //=================================NEWEST===============================
                self.worktimeSetting = new WorktimeSetting();
                
                self.employeeID = ko.observable('');
                self.employeeCode = ko.observable('');
                self.employeeName = ko.observable('');
                
                self.employmentCode = ko.observable('');
                self.employmentName = ko.observable('');
                
                self.wkpID = ko.observable('');
                self.wkpCode = ko.observable('');
                self.wkpName = ko.observable('');
            }

            /**
             * Start page.
             */
            public startPage(): JQueryPromise<void> {
                nts.uk.ui.block.invisible();
                let self = this;
                let dfd = $.Deferred<void>();
                $.when(self.loadUsageUnitSetting(),
                    self.setStartMonth())
                    .done(() => {
                        self.onSelectCompany().done(() => dfd.resolve());
                    }).always(() => {
                        nts.uk.ui.block.clear();
                    });
                return dfd.promise();
            }

            /**
             * Handle tabindex in tabpanel control.
             */
            public initNextTabFeature() {
                let self = this;
                // Auto next tab when press tab key.
                $("[tabindex='22']").on('keydown', function(e) {
                    if (e.which == 9) {
                        if (self.isCompanySelected()) {
//                            self.companyWTSetting.selectedTab('tab-2');
                            // Update
//                            self.companyWTDetailSetting.selectedTab('tab-2');
                            // NEWEST
                            self.worktimeSetting.selectedTab('tab-2');
                        }
                        if (self.isEmploymentSelected()) {
//                            self.employmentWTSetting.selectedTab('tab-2');
                            // Update
                            self.employmentWTDetailSetting.selectedTab('tab-2');
                        }
                        if (self.isWorkplaceSelected()) {
//                            self.workplaceWTSetting.selectedTab('tab-2');
                            // Update
                            self.workplaceWTDetailSetting.selectedTab('tab-2');
                        }
                        if (self.isEmployeeSelected()) {
                            // Update
                            self.employeeWTDetailSetting.selectedTab('tab-2');
                        }
                    }
                });

                $("[tabindex='48']").on('keydown', function(e) {
                    if (e.which == 9) {
                        if (self.isCompanySelected()) {
//                            self.companyWTSetting.selectedTab('tab-3');
                            // Update
//                            self.companyWTDetailSetting.selectedTab('tab-3');
                            // NEWEST
                            self.worktimeSetting.selectedTab('tab-3');
                        }
                        if (self.isEmploymentSelected()) {
//                            self.employmentWTSetting.selectedTab('tab-3');
                            // Update
                            self.employmentWTDetailSetting.selectedTab('tab-3');
                        }
                        if (self.isWorkplaceSelected()) {
//                            self.workplaceWTSetting.selectedTab('tab-3');
                            // Update
                            self.workplaceWTDetailSetting.selectedTab('tab-3');
                        }
                        if (self.isEmployeeSelected()) {
                            // Update
                            self.employeeWTDetailSetting.selectedTab('tab-3');
                        }
                    }
                });
                $("[tabindex='7']").on('keydown', function(e) {
                    if (e.which == 9 && !$(e.target).parents("[tabindex='7']")[0]) {
                        if (self.isCompanySelected()) {
//                            self.companyWTSetting.selectedTab('tab-1');
                            // Update
//                            self.companyWTDetailSetting.selectedTab('tab-1');
                            // NEWEST
                            self.worktimeSetting.selectedTab('tab-1');
                        }
                        if (self.isEmploymentSelected()) {
//                            self.employmentWTSetting.selectedTab('tab-1');
                            // Update
                            self.employmentWTDetailSetting.selectedTab('tab-1');
                        }
                        if (self.isWorkplaceSelected()) {
//                            self.workplaceWTSetting.selectedTab('tab-1');
                            // Update
                            self.workplaceWTDetailSetting.selectedTab('tab-1');
                        }
                        if (self.isEmployeeSelected()) {
                            // Update
                            self.employeeWTDetailSetting.selectedTab('tab-1');
                        }
                    }
                });
            }

            /**
             * Event on select company.
             */
            public onSelectCompany(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                // Clear error.
                self.clearError();

                self.isLoading(true);
                
                // Update flag.
//                    self.isCompanySelected(true);
//                    self.isEmploymentSelected(false);
//                    self.isEmployeeSelected(false);
//                    self.isWorkplaceSelected(false);
//                    self.isLoading(false);
//                    $('#companyYearPicker').focus();
//                    self.initNextTabFeature();
                // Load data.
                self.loadCompanySettingNewest().done(() => {
//                    // Update flag.
                    self.isCompanySelected(true);
                    self.isEmploymentSelected(false);
                    self.isEmployeeSelected(false);
                    self.isWorkplaceSelected(false);
                    self.isLoading(false);
                    $('#companyYearPicker').focus();
                    self.initNextTabFeature();
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * Event on select employment.
             */
            public onSelectEmployment(): void {
                let self = this;
                // Clear error.
                self.clearError();

                // Update flag.
                self.isLoading(true);
                self.isCompanySelected(false);
                self.isEmploymentSelected(true);
                self.isEmployeeSelected(false);
                self.isWorkplaceSelected(false);
                self.employmentWTSetting.year(self.companyWTSetting.year());

                // Load component.
                $('#list-employment').ntsListComponent(this.employmentComponentOption).done(() => {
                    self.isLoading(false);

                    // Force to reload.
                    if (self.employmentWTSetting.employmentCode() === self.selectedEmploymentCode()) {
                        self.loadEmploymentSetting(self.selectedEmploymentCode());
                    }
                    $('#employmentYearPicker').focus();
                    // Set already setting list.
                    self.setAlreadySettingEmploymentList();
                    self.initNextTabFeature();
                    self.employmentWTSetting.selectedTab('tab-1');
                });
            }

            /**
             * Event on select workplace.
             */
            public onSelectWorkplace(): void {
                let self = this;
                // Reset base date
                self.baseDate(new Date());
                // Clear error.
                self.clearError();

                // Update flag.
                self.isLoading(true);
                self.isCompanySelected(false);
                self.isEmploymentSelected(false);
                self.isEmployeeSelected(false);
                self.isWorkplaceSelected(true);
                self.workplaceWTSetting.year(self.companyWTSetting.year());

                // Load component.
                $('#list-workplace').ntsTreeComponent(this.workplaceComponentOption).done(() => {
                    self.isLoading(false);

                    // Force to reload.
                    if (self.workplaceWTSetting.workplaceId() === self.selectedWorkplaceId()) {
                        self.loadWorkplaceSetting(self.selectedWorkplaceId());
                    }
                    $('#workplaceYearPicker').focus();
                    // Set already setting list.
                    self.setAlreadySettingWorkplaceList();
                    self.initNextTabFeature();
                    self.workplaceWTSetting.selectedTab('tab-1');
                });
            }

            /**
             * Go to screen E (usage unit setting).
             */
            public gotoE(): void {
                let self = this;
                nts.uk.ui.windows.sub.modal("/view/kmk/004/e/index.xhtml").onClosed(() => {
                    self.loadUsageUnitSetting();
                    $('#companyYearPicker').focus();
                });
            }
            
//            public gotoG(): void {
//                let self = this;
//                let params: FlexSetParams = new FlexSetParams();
//                params.isIncludeOverTime = self.worktimeSetting.flexAggrSetting().includeOT(); // G1_2
//                params.shortageSetting = self.worktimeSetting.flexAggrSetting().shortageSetting();// G2_2
//
//                nts.uk.ui.windows.setShared('FLEX_SET_PARAM', params, true);
//
//                nts.uk.ui.windows.sub.modal("/view/kmk/004/g/index.xhtml").onClosed(() => {
//                    $('#companyYearPicker').focus();
//
//                    // get params from dialog 
//                    var flexSetOutput: FlexSetParams = nts.uk.ui.windows.getShared('FLEX_SET_OUTPUT');
//                    // If FLEXSetOutput is undefined
//                    if (!flexSetOutput) {
//                        return;
//                    } else {
//                        self.worktimeSetting.flexAggrSetting().includeOT(flexSetOutput.isIncludeOverTime);
//                        self.worktimeSetting.flexAggrSetting().shortageSetting(flexSetOutput.shortageSetting);
//                    }
//
//                });
//            }
            
            

            /**
             * Save company setting.
             */
            public saveCompanySetting(): void {
                let self = this;
                // Validate
                if (self.hasError()) {
                    return;
                }
                service.saveCompanySetting(ko.toJS(self.companyWTSetting)).done(() => {
                    self.isNewMode(false);
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alertError(error);
                });
            }
            
            public saveCompanySettingNewest(): void {
                let self = this;
                // Validate
                if (self.hasError()) {
                    return;
                }
                service.saveCompanySetting(ko.toJS(self.worktimeSetting)).done(() => {
                    self.isNewMode(false);
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alertError(error);
                });
            }

            /**
             * Save employment setting.
             */
            public saveEmployment(): void {
                let self = this;
                // Validate
                if (self.hasError()) {
                    return;
                }
                service.saveEmploymentSetting(ko.toJS(self.employmentWTSetting)).done(() => {
                    self.isNewMode(false);
                    self.addAlreadySettingEmloyment(self.employmentWTSetting.employmentCode());
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alertError(error);
                });
            }

            /**
             * Save workplace setting.
             */
            public saveWorkplace(): void {
                let self = this;
                // Validate
                if (self.hasError()) {
                    return;
                }
                service.saveWorkplaceSetting(ko.toJS(self.workplaceWTSetting)).done(() => {
                    self.isNewMode(false);
                    self.addAlreadySettingWorkplace(self.workplaceWTSetting.workplaceId());
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).fail(error => {
                    nts.uk.ui.dialog.alertError(error);
                });
            }

            /**
             * Remove employment setting.
             */
            public removeEmployment(): void {
                let self = this;
                if ($('#employmentYearPicker').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    let empt = self.employmentWTSetting;
                    let command = { year: empt.year(), employmentCode: empt.employmentCode() }
                    service.removeEmploymentSetting(command).done(() => {
                        self.isNewMode(true);
                        self.removeAlreadySettingEmployment(empt.employmentCode());
                        // Reserve current code + name + year.
                        let newEmpt = new EmploymentWTSetting();
                        newEmpt.employmentCode(empt.employmentCode());
                        newEmpt.employmentName(empt.employmentName());
                        newEmpt.year(empt.year());
                        self.employmentWTSetting.updateData(ko.toJS(newEmpt));
                        // Sort month.
                        self.employmentWTSetting.sortMonth(self.startMonth());
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        self.clearError();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                })
            }

            /**
             * Remove workplace setting.
             */
            public removeWorkplace(): void {
                let self = this;
                if ($('#workplaceYearPicker').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    let workplace = self.workplaceWTSetting;
                    let command = { year: workplace.year(), workplaceId: workplace.workplaceId() }
                    service.removeWorkplaceSetting(command).done(() => {
                        self.isNewMode(true);
                        self.removeAlreadySettingWorkplace(workplace.workplaceId());
                        // Reserve current code + name + year + id.
                        let newSetting = new WorkPlaceWTSetting();
                        newSetting.year(workplace.year());
                        newSetting.workplaceId(workplace.workplaceId());
                        newSetting.workplaceCode(workplace.workplaceCode());
                        newSetting.workplaceName(workplace.workplaceName());
                        self.workplaceWTSetting.updateData(ko.toJS(newSetting));
                        // Sort month.
                        self.workplaceWTSetting.sortMonth(self.startMonth());
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        self.clearError();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                })
            }

            /**
             * Remove company setting.
             */
            public removeCompanySetting(): void {
                let self = this;
                if ($('#companyYearPicker').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    let selectedYear = self.companyWTSetting.year();
                    let command = { year: selectedYear }
                    service.removeCompanySetting(command).done(() => {
                        // Reserve current year.
                        let newSetting = new CompanyWTSetting();
                        newSetting.year(selectedYear);
                        self.companyWTSetting.updateData(ko.toJS(newSetting));
                        // Sort month.
                        self.companyWTSetting.sortMonth(self.startMonth());
                        self.isNewMode(true);
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        self.clearError();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                })
            }
            // NEWEST
            public removeCompanySettingNewest(): void {
                let self = this;
                if ($('#companyYearPicker').ntsError('hasError')) {
                    return;
                }
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(function() {
                    let selectedYear = self.worktimeSetting.normalSetting().year();
                    let command = { year: selectedYear }
                    service.removeCompanySetting(command).done(() => {
                        // Reserve current year.
                        let newSetting = new WorktimeSetting();
                        newSetting.updateYear(selectedYear);
                        self.worktimeSetting.updateFullData(ko.toJS(newSetting));
                        // Sort month.
                        self.worktimeSetting.sortMonth(self.startMonth());
                        self.isNewMode(true);
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    }).fail(error => {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => {
                        self.clearError();
                    });
                }).ifNo(function() {
                    nts.uk.ui.block.clear();
                    return;
                })
            }

            /**
             * Load company setting.
             */
            public loadCompanySetting(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.findCompanySetting(self.companyWTSetting.year())
                    .done(function(data) {
                        self.clearError();
                        // update mode.
                        if (data) {
                            self.isNewMode(false);
                            self.companyWTSetting.updateData(data);
                        }
                        else {
                            // new mode.
                            self.isNewMode(true);
                            let newSetting = new CompanyWTSetting();
                            // Reserve selected year.
                            newSetting.year(self.companyWTSetting.year());
                            self.companyWTSetting.updateData(ko.toJS(newSetting));
                        }
                        // Sort month.
                        self.companyWTSetting.sortMonth(self.startMonth());
                        dfd.resolve();
                    });
                return dfd.promise();
            }
            /**
             * LOAD WORKTIMESETTING (NEWEST)
             */
            public loadCompanySettingNewest(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
//                if (self.isCompanySelected()) {
                    // Find CompanySetting
                    service.findCompanySetting(self.worktimeSetting.normalSetting().year()).done(function(data: WorktimeSettingDto) {
                        // Clear Errors
                        self.clearError();
                        // update mode.
                        // Check condition: ドメインモデル「会社別通常勤務労働時間」を取得する
                        if (data.statutoryWorktimeSettingDto && data.statutoryWorktimeSettingDto.regularLaborTime) {
                            self.isNewMode(false);
                            // Update Full Data
                            self.worktimeSetting.updateFullData(data);
                        }
                        else {
                            // new mode.
                            self.isNewMode(true);
                            let newSetting = new WorktimeSettingDto();
                            // Reserve selected year.
                            newSetting.updateYear(self.worktimeSetting.normalSetting().year());
                            // Update Full Data
                            self.worktimeSetting.updateFullData(ko.toJS(newSetting));
                        }
                        // Sort month.
                        self.worktimeSetting.sortMonth(self.startMonth());
                        dfd.resolve();
                    });
//                }
                
                return dfd.promise();
            }

            /**
             * Load employment setting.
             */
            public loadEmploymentSetting(code?: string): void {
                let self = this;
                let currentSetting = self.employmentWTSetting;
                let request;
                // Code changed.
                if (code) {
                    request = { year: currentSetting.year(), employmentCode: code };
                }
                // Year changed. Code is unchanged
                else {
                    request = { year: currentSetting.year(), employmentCode: currentSetting.employmentCode() };
                    // Reload alreadySetting list.
                    self.setAlreadySettingEmploymentList();
                }
                service.findEmploymentSetting(request)
                    .done(function(data) {
                        self.clearError();
                        // update mode.
                        if (data) {
                            self.isNewMode(false);
                            self.employmentWTSetting.updateData(data);
                        }
                        // new mode.
                        else {
                            self.isNewMode(true);
                            let newSetting = new EmploymentWTSetting();
                            // reserve selected year.
                            newSetting.year(currentSetting.year());
                            self.employmentWTSetting.updateData(ko.toJS(newSetting));
                        }
                        // Set code + name.
                        self.employmentWTSetting.employmentCode(request.employmentCode);
                        self.setEmploymentName(request.employmentCode);
                        // Sort month.
                        self.employmentWTSetting.sortMonth(self.startMonth());
                    });
            }

            /**
             * Load workplace setting.
             */
            public loadWorkplaceSetting(id?: string): void {
                let self = this;
                let currentSetting = self.workplaceWTSetting;
                let request;
                // workplaceId changed.
                if (id) {
                    request = { year: currentSetting.year(), workplaceId: id };
                }
                // Year changed. workplaceId is unchanged
                else {
                    request = { year: currentSetting.year(), workplaceId: currentSetting.workplaceId() };
                    // Reload alreadySetting list.
                    self.setAlreadySettingWorkplaceList();
                }
                service.findWorkplaceSetting(request)
                    .done(function(data) {
                        self.clearError();
                        // update mode.
                        if (data) {
                            self.isNewMode(false);
                            self.workplaceWTSetting.updateData(data);
                        }
                        // new mode.
                        else {
                            self.isNewMode(true);
                            let newSetting = new WorkPlaceWTSetting();
                            // reserve selected year.
                            newSetting.year(currentSetting.year());
                            self.workplaceWTSetting.updateData(ko.toJS(newSetting));
                        }
                        // Set code + name + id.
                        let tree = $('#list-workplace').getDataList();
                        self.workplaceWTSetting.workplaceId(request.workplaceId);
                        self.setWorkplaceCodeName(tree, request.workplaceId);
                        // Sort month.
                        self.workplaceWTSetting.sortMonth(self.startMonth());
                    });
            }

            /**
             * Load usage unit setting.
             */
            public loadUsageUnitSetting(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred<any>();
                UsageUnitSettingService.findUsageUnitSetting().done(function(res: UsageUnitSettingService.model.UsageUnitSettingDto) {
                    self.usageUnitSetting.employee(res.employee);
                    self.usageUnitSetting.employment(res.employment);
                    self.usageUnitSetting.workplace(res.workPlace);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * Set the already setting employment list.
             */
            private setAlreadySettingEmploymentList(): void {
                let self = this;
                service.findAllEmploymentSetting(self.employmentWTSetting.year()).done(listCode => {
                    self.alreadySettingEmployments(_.map(listCode, function(code) {
                        return { code: code, isAlreadySetting: true };
                    }));
                });
            }

            /**
             * Set the already setting workplace list.
             */
            private setAlreadySettingWorkplaceList(): void {
                let self = this;
                service.findAllWorkplaceSetting(self.workplaceWTSetting.year()).done(listId => {
                    self.alreadySettingWorkplaces(_.map(listId, function(id) {
                        return { workplaceId: id, isAlreadySetting: true };
                    }));
                });
            }

            /**
             * Set start month.
             */
            private setStartMonth(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.getStartMonth().done(res => {
                    if (res.startMonth) {
                        self.startMonth = ko.observable(res.startMonth);
                    } else {
                        // Default startMonth..
                        self.startMonth = ko.observable(1);
                    }
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * Set employment component option.
             */
            private setEmploymentComponentOption(): void {
                let self = this;
                self.employmentComponentOption = {
                    isShowAlreadySet: true, // is show already setting column.
                    isMultiSelect: false, // is multiselect.
                    isShowNoSelectRow: false, // selected nothing.
                    listType: 1, // employment list.
                    selectType: 3, // select first item.
                    maxRows: 12, // maximum rows can be displayed.
                    selectedCode: this.selectedEmploymentCode,
                    isDialog: false,
                    alreadySettingList: self.alreadySettingEmployments
                };
            }

            /**
             * Set workplace component option.
             */
            private setWorkplaceComponentOption(): void {
                let self = this;
                self.workplaceComponentOption = {
                    isShowAlreadySet: true, // is show already setting column.
                    isMultiSelect: false, // is multiselect.
                    isShowSelectButton: false, // Show button select all and selected sub parent
                    treeType: 1, // workplace tree.
                    selectType: 2, // select first item.
                    maxRows: 12, // maximum rows can be displayed.
                    selectedWorkplaceId: self.selectedWorkplaceId,
                    baseDate: self.baseDate,
                    isDialog: false,
                    alreadySettingList: self.alreadySettingWorkplaces,
                    systemType: 2
                };
            }

            /**
             * Clear all errors.
             */
            private clearError(): void {
                let self = this;
                if (nts.uk.ui._viewModel) {
                    // Reset year if has error.
                    if ($('#companyYearPicker').ntsError('hasError')) {
                        self.companyWTSetting.year(new Date().getFullYear());
                    }
                    if ($('#employmentYearPicker').ntsError('hasError')) {
                        self.employmentWTSetting.year(new Date().getFullYear());
                    }
                    if ($('#workplaceYearPicker').ntsError('hasError')) {
                        self.workplaceWTSetting.year(new Date().getFullYear());
                    }
                    // Clear error inputs
                    $('.nts-input').ntsError('clear');
                }
            }

            /**
             * Check validate all input.
             */
            private hasError(): boolean {
                return $('.nts-editor').ntsError('hasError');
            }

            /**
             * Add alreadySetting employment.
             */
            private addAlreadySettingEmloyment(code: string): void {
                let self = this;
                let l = self.alreadySettingEmployments().filter(i => code == i.code);
                if (l[0]) {
                    return;
                }
                self.alreadySettingEmployments.push({ code: code, isAlreadySetting: true });
            }

            /**
             * Add alreadySetting workplace.
             */
            private addAlreadySettingWorkplace(id: string): void {
                let self = this;
                let l = self.alreadySettingWorkplaces().filter(i => id == i.workplaceId);
                if (l[0]) {
                    return;
                }
                self.alreadySettingWorkplaces.push({ workplaceId: id, isAlreadySetting: true });
            }

            /**
             * Remove alreadySetting employment.
             */
            private removeAlreadySettingEmployment(code: string): void {
                let self = this;
                let ase = self.alreadySettingEmployments().filter(i => code == i.code)[0];
                self.alreadySettingEmployments.remove(ase);
            }

            /**
             * Remove alreadySetting workplace.
             */
            private removeAlreadySettingWorkplace(id: string): void {
                let self = this;
                let asw = self.alreadySettingWorkplaces().filter(i => id == i.workplaceId)[0];
                self.alreadySettingWorkplaces.remove(asw);
            }

            /**
             * Set workplace code + name.
             */
            private setWorkplaceCodeName(treeData: Array<any>, workPlaceId: string) {
                let self = this;
                for (let data of treeData) {
                    // Found!
                    if (data.workplaceId == workPlaceId) {
                        self.workplaceWTSetting.workplaceCode(data.code);
                        self.workplaceWTSetting.workplaceName(data.name);
                    }
                    // Continue to find in childs.
                    if (data.childs.length > 0) {
                        this.setWorkplaceCodeName(data.childs, workPlaceId);
                    }
                }
            }

            /**
             * Set employment name
             */
            private setEmploymentName(code: string): void {
                let self = this;
                let list = $('#list-employment').getDataList();
                if (list) {
                    let empt = _.find(list, item => item.code == code);
                    self.employmentWTSetting.employmentName(empt.name);
                }
            }

        }//================================END OF SCREENMODEL=============================

        //=========================OLD MODELS============
        export class ItemModel {
            code: number;
            name: string;

            constructor(code: number, name: string) {
                this.code = code;
                this.name = name;
            }
        }
        export class CompanyWTSetting {
            deformationLaborSetting: DeformationLaborSetting;
            flexSetting: FlexSetting;
            normalSetting: NormalSetting;
            year: KnockoutObservable<number>;
            selectedTab: KnockoutObservable<string>;
            // Update
            

            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.year = ko.observable(new Date().getFullYear());
                self.deformationLaborSetting = new DeformationLaborSetting();
                self.flexSetting = new FlexSetting();
                self.normalSetting = new NormalSetting();
            }

            public updateData(dto: any): void {
                let self = this;
                self.year(dto.year);
                self.normalSetting.updateData(dto.normalSetting);
                self.deformationLaborSetting.updateData(dto.deformationLaborSetting);
                self.flexSetting.updateData(dto.flexSetting);
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                self.normalSetting.statutorySetting.sortMonth(startMonth);
                self.deformationLaborSetting.statutorySetting.sortMonth(startMonth);
                self.flexSetting.sortMonth(startMonth);
            }
        }
        export class WorkPlaceWTSetting {
            deformationLaborSetting: DeformationLaborSetting;
            flexSetting: FlexSetting;
            normalSetting: NormalSetting;
            year: KnockoutObservable<number>;
            workplaceId: KnockoutObservable<string>;
            workplaceCode: KnockoutObservable<string>;
            workplaceName: KnockoutObservable<string>;
            selectedTab: KnockoutObservable<string>;

            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.workplaceId = ko.observable('');
                self.workplaceCode = ko.observable('');
                self.workplaceName = ko.observable('');
                self.year = ko.observable(new Date().getFullYear());
                self.deformationLaborSetting = new DeformationLaborSetting();
                self.flexSetting = new FlexSetting();
                self.normalSetting = new NormalSetting();
            }
            public updateData(dto: any): void {
                let self = this;
                self.year(dto.year);
                self.normalSetting.updateData(dto.normalSetting);
                self.deformationLaborSetting.updateData(dto.deformationLaborSetting);
                self.flexSetting.updateData(dto.flexSetting);
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                self.normalSetting.statutorySetting.sortMonth(startMonth);
                self.deformationLaborSetting.statutorySetting.sortMonth(startMonth);
                self.flexSetting.sortMonth(startMonth);
            }
        }
        export class EmploymentWTSetting {
            deformationLaborSetting: DeformationLaborSetting;
            flexSetting: FlexSetting;
            normalSetting: NormalSetting;
            year: KnockoutObservable<number>;
            employmentCode: KnockoutObservable<string>;
            employmentName: KnockoutObservable<string>;
            selectedTab: KnockoutObservable<string>;

            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.employmentCode = ko.observable('');
                self.employmentName = ko.observable('');
                self.year = ko.observable(new Date().getFullYear());
                self.deformationLaborSetting = new DeformationLaborSetting();
                self.flexSetting = new FlexSetting();
                self.normalSetting = new NormalSetting();
            }
            public updateData(dto: any): void {
                let self = this;
                self.year(dto.year);
                self.normalSetting.updateData(dto.normalSetting);
                self.deformationLaborSetting.updateData(dto.deformationLaborSetting);
                self.flexSetting.updateData(dto.flexSetting);
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                self.normalSetting.statutorySetting.sortMonth(startMonth);
                self.deformationLaborSetting.statutorySetting.sortMonth(startMonth);
                self.flexSetting.sortMonth(startMonth);
            }

        }
        export class DeformationLaborSetting {
            statutorySetting: WorkingTimeSetting;
            weekStart: KnockoutObservable<number>;

            constructor() {
                let self = this;
                self.statutorySetting = new WorkingTimeSetting();
                self.weekStart = ko.observable(0);
            }

            public updateData(dto: any): void {
                let self = this;
                self.weekStart(dto.weekStart);
                self.statutorySetting.updateData(dto.statutorySetting);
            }
        }
        export class FlexSetting {
            flexDaily: FlexDaily;
            flexMonthly: KnockoutObservableArray<FlexMonth>;

            constructor() {
                let self = this;
                self.flexDaily = new FlexDaily();
                self.flexMonthly = ko.observableArray<FlexMonth>([]);
                for (let i = 1; i < 13; i++) {
                    let flm = new FlexMonth();
                    flm.speName(nts.uk.resource.getText("KMK004_21",[i]));
                    flm.staName(nts.uk.resource.getText("KMK004_22",[i]));
                    flm.month(i);
                    flm.statutoryTime(0);
                    flm.specifiedTime(0);
                    self.flexMonthly.push(flm);
                }
            }
            public updateData(dto: any): void {
                let self = this;
                self.flexDaily.updateData(dto.flexDaily);
                self.flexMonthly().forEach(i => {
                    let updatedData = dto.flexMonthly.filter(j => i.month() == j.month)[0];
                    i.updateData(updatedData.statutoryTime, updatedData.specifiedTime);
                });
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.flexMonthly().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    startMonth++;
                }
                self.flexMonthly(sortedList);
            }
        }
        export class FlexDaily {
            statutoryTime: KnockoutObservable<number>;
            specifiedTime: KnockoutObservable<number>;
            constructor() {
                let self = this;
                self.statutoryTime = ko.observable(0);
                self.specifiedTime = ko.observable(0);
            }
            public updateData(dto: any): void {
                let self = this;
                self.statutoryTime(dto.statutoryTime);
                self.specifiedTime(dto.specifiedTime);
            }
        }
        export class FlexMonth {
            month: KnockoutObservable<number>;
            speName: KnockoutObservable<string>;
            staName: KnockoutObservable<string>;
            statutoryTime: KnockoutObservable<number>;
            specifiedTime: KnockoutObservable<number>;
            constructor() {
                let self = this;
                self.month = ko.observable(0);
                self.speName = ko.observable('');
                self.staName = ko.observable('');
                self.statutoryTime = ko.observable(0);
                self.specifiedTime = ko.observable(0);
            }
            public updateData(statutoryTime: number, specifiedTime: number): void {
                let self = this;
                self.statutoryTime(statutoryTime);
                self.specifiedTime(specifiedTime);
            }
        }
        export class NormalSetting {
            statutorySetting: WorkingTimeSetting;
            weekStart: KnockoutObservable<number>;

            constructor() {
                let self = this;
                self.statutorySetting = new WorkingTimeSetting();
                self.weekStart = ko.observable(0);
            }

            public updateData(dto: any): void {
                let self = this;
                self.weekStart(dto.weekStart);
                self.statutorySetting.updateData(dto.statutorySetting);
            }
        }
        export class WorkingTimeSetting {
            daily: KnockoutObservable<number>;
            monthly: KnockoutObservableArray<Monthly>;
            weekly: KnockoutObservable<number>;

            constructor() {
                let self = this;
                self.daily = ko.observable(0);
                self.weekly = ko.observable(0);
                self.monthly = ko.observableArray<Monthly>([]);
                for (let i = 1; i < 13; i++) {
                    let m = new Monthly();
                    m.month(i);
                    m.normal(nts.uk.resource.getText("KMK004_14",[i]));
                    m.deformed(nts.uk.resource.getText("KMK004_26",[i]));
                    self.monthly.push(m);
                }
            }
            public updateData(dto: any): void {
                let self = this;
                self.daily(dto.daily);
                self.weekly(dto.weekly);
                self.monthly().forEach(i => {
                    let updatedData = dto.monthly.filter(j => i.month() == j.month)[0];
                    i.updateData(updatedData.time);
                });
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.monthly().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    startMonth++;
                }
                self.monthly(sortedList);
            }
        }

        export class Monthly {
            month: KnockoutObservable<number>;
            time: KnockoutObservable<number>;
            normal: KnockoutObservable<string>;
            deformed: KnockoutObservable<string>;

            constructor() {
                let self = this;
                self.time = ko.observable(0);
                self.month = ko.observable(0);
                self.normal = ko.observable('');
                self.deformed = ko.observable('');
            }

            public updateData(time: number): void {
                let self = this;
                self.time(time);
            }
        }
        export class UsageUnitSetting {
            employee: KnockoutObservable<boolean>;
            employment: KnockoutObservable<boolean>;
            workplace: KnockoutObservable<boolean>;

            constructor() {
                let self = this;
                self.employee = ko.observable(true);
                self.employment = ko.observable(true);
                self.workplace = ko.observable(true);
            }
        }
        
//        export class AggregateSelection {
//            id: number;
//            name: string;
//            constructor(id, name) {
//                var self = this;
//                self.id = id;
//                self.name = name;
//            }
//        }
        /**
         * Company Worktime Detail Setting
         */
        export class CompanyWTDetailSetting {
            year: KnockoutObservable<number>;
            // Normal
            normalStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Flex
//            flexStatutorySetting: KnockoutObservableArray<MonthlyTime>;
//            flexSpecifiedSetting: KnockoutObservableArray<MonthlyTime>;
            flexSetting: KnockoutObservableArray<FlexMonthlyTime>;
            
            // Deformed labor
            deformStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            
            // Selected Tab
            selectedTab: KnockoutObservable<string>;
            
            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.year = ko.observable(new Date().getFullYear());
                self.normalStatutorySetting = ko.observableArray([]);
//                self.flexStatutorySetting = ko.observableArray([]);
//                self.flexSpecifiedSetting = ko.observableArray([]);
                self.flexSetting = ko.observableArray([]);
                self.deformStatutorySetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    let mFlex = new FlexMonthlyTime();
                    mFlex.month(i);
                    mFlex.statutoryTime(0);
                    mFlex.specifiedTime(0);
                    self.normalStatutorySetting.push(m);
//                    self.flexStatutorySetting.push(m);
//                    self.flexSpecifiedSetting.push(m);
                    self.flexSetting.push(mFlex);
                    self.deformStatutorySetting.push(m);
                }
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.normalStatutorySetting().filter(m => startMonth == m.month())[0];
                    let flexValue = self.flexSetting().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    flexSortedList.push(flexValue);
                    startMonth++;
                }
                self.normalStatutorySetting(sortedList);
//                self.flexStatutorySetting(sortedList);
//                self.flexSpecifiedSetting(sortedList);
                self.deformStatutorySetting(sortedList);
                self.flexSetting(flexSortedList);
            }
            
        }
        
        /**
         * Employee WorkTime Detail Setting
         */
        export class EmployeeWTDetailSetting {
            year: KnockoutObservable<number>;
            empID: KnockoutObservable<string>;
            // Normal
            normalStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Flex
            flexSetting: KnockoutObservableArray<FlexMonthlyTime>;
            // Deformed labor
            deformStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Selected Tab
            selectedTab: KnockoutObservable<string>;
            
            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.year = ko.observable(new Date().getFullYear());
                self.empID = ko.observable("");
                self.normalStatutorySetting = ko.observableArray([]);
                self.flexSetting = ko.observableArray([]);
                self.deformStatutorySetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    let mFlex = new FlexMonthlyTime();
                    mFlex.month(i);
                    mFlex.statutoryTime(0);
                    mFlex.specifiedTime(0);
                    self.normalStatutorySetting.push(m);
//                    self.flexStatutorySetting.push(m);
//                    self.flexSpecifiedSetting.push(m);
                    self.flexSetting.push(mFlex);
                    self.deformStatutorySetting.push(m);
                }
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.normalStatutorySetting().filter(m => startMonth == m.month())[0];
                    let flexValue = self.flexSetting().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    flexSortedList.push(flexValue);
                    startMonth++;
                }
                self.normalStatutorySetting(sortedList);
                self.deformStatutorySetting(sortedList);
                self.flexSetting(flexSortedList);
            }
        }
        
        /**
         * Workplace Worktime Detail Setting
         */
        export class WorkplaceWTDetailSetting {
            year: KnockoutObservable<number>;
            wkpID: KnockoutObservable<string>;
            // Normal
            normalStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Flex
            flexSetting: KnockoutObservableArray<FlexMonthlyTime>;
            // Deformed labor
            deformStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Selected Tab
            selectedTab: KnockoutObservable<string>;
            
            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.year = ko.observable(new Date().getFullYear());
                self.wkpID = ko.observable("");
                self.normalStatutorySetting = ko.observableArray([]);
                self.flexSetting = ko.observableArray([]);
                self.deformStatutorySetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    let mFlex = new FlexMonthlyTime();
                    mFlex.month(i);
                    mFlex.statutoryTime(0);
                    mFlex.specifiedTime(0);
                    self.normalStatutorySetting.push(m);
//                    self.flexStatutorySetting.push(m);
//                    self.flexSpecifiedSetting.push(m);
                    self.flexSetting.push(mFlex);
                    self.deformStatutorySetting.push(m);
                }
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.normalStatutorySetting().filter(m => startMonth == m.month())[0];
                    let flexValue = self.flexSetting().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    flexSortedList.push(flexValue);
                    startMonth++;
                }
                self.normalStatutorySetting(sortedList);
                self.deformStatutorySetting(sortedList);
                self.flexSetting(flexSortedList);
            }
        }
        
        /**
         * Employment WorkTime Detail Setting
         */
        export class EmploymentWTDetailSetting {
            year: KnockoutObservable<number>;
            employmentCode: KnockoutObservable<string>;
            // Normal
            normalStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Flex
            flexSetting: KnockoutObservableArray<FlexMonthlyTime>;
            // Deformed labor
            deformStatutorySetting: KnockoutObservableArray<MonthlyTime>;
            // Selected Tab
            selectedTab: KnockoutObservable<string>;
            
            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.year = ko.observable(new Date().getFullYear());
                self.employmentCode = ko.observable("");
                self.normalStatutorySetting = ko.observableArray([]);
                self.flexSetting = ko.observableArray([]);
                self.deformStatutorySetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    let mFlex = new FlexMonthlyTime();
                    mFlex.month(i);
                    mFlex.statutoryTime(0);
                    mFlex.specifiedTime(0);
                    self.normalStatutorySetting.push(m);
//                    self.flexStatutorySetting.push(m);
//                    self.flexSpecifiedSetting.push(m);
                    self.flexSetting.push(mFlex);
                    self.deformStatutorySetting.push(m);
                }
            }
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.normalStatutorySetting().filter(m => startMonth == m.month())[0];
                    let flexValue = self.flexSetting().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    flexSortedList.push(flexValue);
                    startMonth++;
                }
                self.normalStatutorySetting(sortedList);
                self.deformStatutorySetting(sortedList);
                self.flexSetting(flexSortedList);
            }
        }
        
        
        
        //******************************Detail 3 cuc *****************
        /**
         * class CompanyMonthlyNormalSet 
         * (Screen A)
         */
        // Detail Cuc 1 Company: Normal Setting
        export class CompanyNormalMonthlySet {
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            constructor() {
                let self = this;
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
            }
        }
        // Detail cuc 3 Company: Deform Labor Setting
        export class CompanyDeformMonthlySet {
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            isDeformedOT: KnockoutObservable<boolean>;
            period: KnockoutObservable<number>;
            repeatCls: KnockoutObservable<boolean>;
            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
                
                self.isDeformedOT = ko.observable(false);
                self.period = ko.observable(1);
                self.repeatCls = ko.observable(false);
                self.startMonth = ko.observable(new Date().getMonth());
            }
        }
        
        // Detail cuc 2 Company: Flex Setting
        export class CompanyFlexMonthlySet {
            // 不足設定: フレックス不足設定//
            shortageSetting: KnockoutObservable<ShortageSetting>;
            // 残業時間を含める: するしない区分//
            includeOT: KnockoutObservable<boolean>;
            // 法定内集計設定: 法定内フレックス時間集計
            legalAggrSet: KnockoutObservable<AggrregateSetting>;
            // 集計方法: フレックス集計方法//
            aggregateMethod: KnockoutObservable<FlexAggregateMethod>;
            
            constructor() {
                let self = this;
                self.shortageSetting = ko.observable(ShortageSetting.CURRENT_MONTH_INTEGRATION);
                self.includeOT = ko.observable(false);
                self.legalAggrSet = ko.observable(AggrregateSetting.MANAGED_AS_FLEX_TIME);
                self.aggregateMethod = ko.observable(FlexAggregateMethod.PRINCIPLE);
            }
        }
        
        /**
         * class EmployeeMonthlyNormalSet
         */
        //(Screen B)
        // Detail Cuc 1 Employee: Normal Setting
        export class EmployeeNormalMonthlySet {
            employeeId: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            constructor() {
                let self = this;
                self.employeeId = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
            }
        }
        // Detail cuc 3 Employee: Deform Labor Setting
        export class EmployeeDeformMonthlySet {
            employeeId: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            isDeformedOT: KnockoutObservable<boolean>;
            period: KnockoutObservable<number>;
            repeatCls: KnockoutObservable<boolean>;
            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.employeeId = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
                
                self.isDeformedOT = ko.observable(false);
                self.period = ko.observable(1);
                self.repeatCls = ko.observable(false);
                self.startMonth = ko.observable(new Date().getMonth());
            }
        }
        
        // Detail cuc 2 Employee: Flex Setting
        export class EmployeeFlexMonthlySet {
            employeeId: KnockoutObservable<string>;
            shortageSetting: KnockoutObservable<ShortageSetting>;
            includeOT: KnockoutObservable<boolean>;
            legalAggrSet: KnockoutObservable<AggrregateSetting>;
            aggregateMethod: KnockoutObservable<FlexAggregateMethod>;
            
            constructor() {
                let self = this;
                self.employeeId = ko.observable("");
                self.shortageSetting = ko.observable(ShortageSetting.CURRENT_MONTH_INTEGRATION);
                self.includeOT = ko.observable(false);
                self.legalAggrSet = ko.observable(AggrregateSetting.MANAGED_AS_FLEX_TIME);
                self.aggregateMethod = ko.observable(FlexAggregateMethod.PRINCIPLE);
            }
        }
        
        /**
         * class EmploymentMonthlyNormalSet
         */
        //(Screen C)
        // Detail Cuc 1 Employment: Normal Setting
        export class EmploymentNormalMonthlySet {
            employmentCode: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            constructor() {
                let self = this;
                self.employmentCode = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
            }
        }
        // Detail cuc 3 Employment: Deform Labor Setting
        export class EmploymentDeformMonthlySet {
            employmentCode: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            isDeformedOT: KnockoutObservable<boolean>;
            period: KnockoutObservable<number>;
            repeatCls: KnockoutObservable<boolean>;
            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.employmentCode = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
                
                self.isDeformedOT = ko.observable(false);
                self.period = ko.observable(1);
                self.repeatCls = ko.observable(false);
                self.startMonth = ko.observable(new Date().getMonth());
            }
        }
        
        // Detail cuc 2 Employment: Flex Setting
        export class EmploymentFlexMonthlySet {
            employmentCode: KnockoutObservable<string>;
            shortageSetting: KnockoutObservable<ShortageSetting>;
            includeOT: KnockoutObservable<boolean>;
            legalAggrSet: KnockoutObservable<AggrregateSetting>;
            aggregateMethod: KnockoutObservable<FlexAggregateMethod>;
            
            constructor() {
                let self = this;
                self.employmentCode = ko.observable("");
                self.shortageSetting = ko.observable(ShortageSetting.CURRENT_MONTH_INTEGRATION);
                self.includeOT = ko.observable(false);
                self.legalAggrSet = ko.observable(AggrregateSetting.MANAGED_AS_FLEX_TIME);
                self.aggregateMethod = ko.observable(FlexAggregateMethod.PRINCIPLE);
            }
        }
        
        
        /**
         * class WorkplaceMonthlyNormalSet
         */
        //(Screen D)
        // Detail Cuc 1 Workplace: Normal Setting
        export class WorkplaceNormalMonthlySet {
            wkpId: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            constructor() {
                let self = this;
                self.wkpId = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
            }
        }
        // Detail cuc 3 Workplace: Deform Labor Setting
        export class WorkplaceDeformMonthlySet {
            wkpId: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            isDeformedOT: KnockoutObservable<boolean>;
            period: KnockoutObservable<number>;
            repeatCls: KnockoutObservable<boolean>;
            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.wkpId = ko.observable("");
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
                
                self.isDeformedOT = ko.observable(false);
                self.period = ko.observable(1);
                self.repeatCls = ko.observable(false);
                self.startMonth = ko.observable(new Date().getMonth());
            }
        }
        
        // Detail cuc 2 Workplace: Flex Setting
        export class WorkplaceFlexMonthlySet {
            wkpId: KnockoutObservable<string>;
            shortageSetting: KnockoutObservable<ShortageSetting>;
            includeOT: KnockoutObservable<boolean>;
            legalAggrSet: KnockoutObservable<AggrregateSetting>;
            aggregateMethod: KnockoutObservable<FlexAggregateMethod>;
            
            constructor() {
                let self = this;
                self.wkpId = ko.observable("");
                self.shortageSetting = ko.observable(ShortageSetting.CURRENT_MONTH_INTEGRATION);
                self.includeOT = ko.observable(false);
                self.legalAggrSet = ko.observable(AggrregateSetting.MANAGED_AS_FLEX_TIME);
                self.aggregateMethod = ko.observable(FlexAggregateMethod.PRINCIPLE);
            }
        }
        
        
        //=============NEWEST===================================
        /**
         * Company Worktime Setting (Tab 1)
         */
        export class WorktimeSetting {
            // Selected Tab
            selectedTab: KnockoutObservable<string>;
            // 会社別通常勤務労働時間
            normalWorktime: KnockoutObservable<NormalWorktime>;
            // 会社別変形労働労働時間
            deformLaborWorktime: KnockoutObservable<NormalWorktime>;
            
            // 会社別通常勤務月間労働時間
            normalSetting: KnockoutObservable<WorktimeNormalDeformSetting>;
            // 会社別フレックス勤務月間労働時間
//            flexSetting: KnockoutObservable<WorktimeFlexSetting>;
            flexSetting: KnockoutObservable<WorktimeFlexSetting1>;
            // 会社別変形労働月間労働時間
            deformLaborSetting: KnockoutObservable<WorktimeNormalDeformSetting>;
            
            
            // Details
            // 通常勤務労働会社別月別実績集計設定
            normalAggrSetting: KnockoutObservable<NormalWorktimeAggrSetting>;
            // 変形労働会社別月別実績集計設定
            deformAggrSetting: KnockoutObservable<DeformWorktimeAggrSetting>;
            // フレックス会社別月別実績集計設定
            flexAggrSetting: KnockoutObservable<FlexWorktimeAggrSetting>;
            
            constructor() {
                let self = this;
                self.selectedTab = ko.observable('tab-1');
                self.normalWorktime = ko.observable(new NormalWorktime());
                self.deformLaborWorktime = ko.observable(new NormalWorktime());
                
                self.normalSetting = ko.observable(new WorktimeNormalDeformSetting());
                self.flexSetting = ko.observable(new WorktimeFlexSetting1());
                self.deformLaborSetting = ko.observable(new WorktimeNormalDeformSetting());
                
                self.normalAggrSetting = ko.observable(new NormalWorktimeAggrSetting());
                self.deformAggrSetting = ko.observable(new DeformWorktimeAggrSetting());
                self.flexAggrSetting = ko.observable(new FlexWorktimeAggrSetting());
                
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                self.normalSetting().sortMonth(startMonth);
                self.flexSetting().sortMonth(startMonth);
                self.deformLaborSetting().sortMonth(startMonth);
            }
            //WorktimeSettingDto
            public updateDataDependOnYear(dto: StatutoryWorktimeSettingDto): void {
                let self = this;
                self.normalWorktime().updateData(dto.regularLaborTime);
                self.deformLaborWorktime().updateData(dto.transLaborTime);
                
                self.normalSetting().updateData(dto);
                self.flexSetting().updateData(dto);
                self.deformLaborSetting().updateData(dto);
            }
            
            public updateDetailData(dto: MonthlyCalSettingDto): void {
                let self = this;
                 // Update Detail Data
                self.normalAggrSetting().updateData(dto.regAggrSetting);
                self.deformAggrSetting().updateData(dto.deforAggrSetting);
                self.flexAggrSetting().updateData(dto.flexAggrSetting);
            }
            // Update Full Data: 8 Models
            public updateFullData(dto: WorktimeSettingDto): void {
                let self = this;
                self.updateDataDependOnYear(dto.statutoryWorktimeSettingDto);
                self.updateDetailData(dto.monthlyCalSettingDto);
            }
            
            public updateYear(year: number): void {
                let self = this;
                self.normalSetting().year(year);
                self.flexSetting().year(year);
                self.deformLaborSetting().year(year);
            }
            
            public gotoF(): void {
                let self = this;
                let params: NormalSetParams = new NormalSetParams();
                // F1_2
                params.startWeek = self.normalWorktime().startWeek();
                // F2_3
                params.isIncludeExtraAggr = self.normalAggrSetting().aggregateOutsideTimeSet.includeExtra();
                // F2_8
                params.isIncludeLegalAggr = self.normalAggrSetting().aggregateOutsideTimeSet.includeLegal();
                // F2_12
                params.isIncludeHolidayAggr = self.normalAggrSetting().aggregateOutsideTimeSet.includeHoliday();

                // F2_16
                params.isIncludeExtraExcessOutside = self.normalAggrSetting().excessOutsideTimeSet.includeExtra();
                // F2_21
                params.isIncludeLegalExcessOutside = self.normalAggrSetting().excessOutsideTimeSet.includeLegal();
                // F2_25
                params.isIncludeHolidayExcessOutside = self.normalAggrSetting().excessOutsideTimeSet.includeHoliday();

                nts.uk.ui.windows.setShared('NORMAL_SET_PARAM', params, true);
                
                nts.uk.ui.windows.sub.modal("/view/kmk/004/f/index.xhtml").onClosed(() => {
                    $('#companyYearPicker').focus();
                    
                    // Get params
                    var normalSetOutput: NormalSetParams = nts.uk.ui.windows.getShared('NORMAL_SET_OUTPUT');
                    // If normalSetOutput is undefined
                    if (!normalSetOutput) {
                        return;
                    } else {
                        self.normalWorktime().startWeek(normalSetOutput.startWeek);
                        self.normalAggrSetting().aggregateOutsideTimeSet.includeExtra(normalSetOutput.isIncludeExtraAggr);
                        self.normalAggrSetting().aggregateOutsideTimeSet.includeLegal(normalSetOutput.isIncludeLegalAggr);
                        self.normalAggrSetting().aggregateOutsideTimeSet.includeHoliday(normalSetOutput.isIncludeHolidayAggr);

                        // F2_16
                        self.normalAggrSetting().excessOutsideTimeSet.includeExtra(normalSetOutput.isIncludeExtraExcessOutside);
                        // F2_21
                        self.normalAggrSetting().excessOutsideTimeSet.includeLegal(normalSetOutput.isIncludeLegalExcessOutside);
                        // F2_25
                        self.normalAggrSetting().excessOutsideTimeSet.includeHoliday(normalSetOutput.isIncludeHolidayExcessOutside);
                    }
                });
            }
            
            public gotoG(): void {
                let self = this;
                let params: FlexSetParams = new FlexSetParams();
                params.isIncludeOverTime = self.flexAggrSetting().includeOT(); // G1_2
                params.shortageSetting = self.flexAggrSetting().shortageSetting();// G2_2
                
                nts.uk.ui.windows.setShared('FLEX_SET_PARAM', params, true);
                
                nts.uk.ui.windows.sub.modal("/view/kmk/004/g/index.xhtml").onClosed(() => {
                    $('#companyYearPicker').focus();
                    
                    // get params from dialog 
                    var flexSetOutput: FlexSetParams = nts.uk.ui.windows.getShared('FLEX_SET_OUTPUT');
                    // If FLEXSetOutput is undefined
                    if (!flexSetOutput) {
                        return;
                    } else {
                        self.flexAggrSetting().includeOT(flexSetOutput.isIncludeOverTime);
                        self.flexAggrSetting().shortageSetting(flexSetOutput.shortageSetting);
                    }
                    
                });
            }
            
            
            public gotoH(): void {
                
                let self = this;
                let params: DeformSetParams = new DeformSetParams();
                
                params.strMonth = self.deformAggrSetting().startMonth();
                params.period = self.deformAggrSetting().period();
                params.repeatCls = self.deformAggrSetting().repeatCls();
                // H1_1
                params.startWeek = self.deformLaborWorktime().startWeek();
                // // H3_3
                params.isIncludeExtraAggr = self.deformAggrSetting().aggregateOutsideTimeSet.includeExtra();
                // H3_8
                params.isIncludeLegalAggr = self.deformAggrSetting().aggregateOutsideTimeSet.includeLegal();
                // H3_12
                params.isIncludeHolidayAggr = self.deformAggrSetting().aggregateOutsideTimeSet.includeHoliday();

                // H3_16
                params.isIncludeExtraExcessOutside = self.deformAggrSetting().excessOutsideTimeSet.includeExtra();
                // H3_21
                params.isIncludeLegalExcessOutside = self.deformAggrSetting().excessOutsideTimeSet.includeLegal();
                // H3_25
                params.isIncludeHolidayExcessOutside = self.deformAggrSetting().excessOutsideTimeSet.includeHoliday();

                nts.uk.ui.windows.setShared('DEFORM_SET_PARAM', params, true);
                
                nts.uk.ui.windows.sub.modal("/view/kmk/004/h/index.xhtml").onClosed(() => {
                    $('#companyYearPicker').focus();
                    
                    // Get params
                    var deformSetOutput: DeformSetParams = nts.uk.ui.windows.getShared('DEFORM_SET_OUTPUT');
                    // If deformSetOutput is undefined
                    if (!deformSetOutput) {
                        return;
                    } else {
                        self.deformAggrSetting().startMonth(deformSetOutput.strMonth);
                        self.deformAggrSetting().period(deformSetOutput.period);
                        self.deformAggrSetting().repeatCls(deformSetOutput.repeatCls);
                        //
                        self.deformLaborWorktime().startWeek(deformSetOutput.startWeek);
                        self.deformAggrSetting().aggregateOutsideTimeSet.includeExtra(deformSetOutput.isIncludeExtraAggr);
                        self.deformAggrSetting().aggregateOutsideTimeSet.includeLegal(deformSetOutput.isIncludeLegalAggr);
                        self.deformAggrSetting().aggregateOutsideTimeSet.includeHoliday(deformSetOutput.isIncludeHolidayAggr);

                        self.deformAggrSetting().excessOutsideTimeSet.includeExtra(deformSetOutput.isIncludeExtraExcessOutside);
                        self.deformAggrSetting().excessOutsideTimeSet.includeLegal(deformSetOutput.isIncludeLegalExcessOutside);
                        self.deformAggrSetting().excessOutsideTimeSet.includeHoliday(deformSetOutput.isIncludeHolidayExcessOutside);
                    }
                });
            }
        }
        
        //===================================WorkTimeSetting DTO========================================================
        export class WorktimeSettingDto {
            // Cuc 5 cais
            statutoryWorktimeSettingDto: StatutoryWorktimeSettingDto;
            // Cuc 3 cai
            monthlyCalSettingDto: MonthlyCalSettingDto;
            
            constructor() {
                let self = this;
                self.statutoryWorktimeSettingDto = new StatutoryWorktimeSettingDto();
                self.monthlyCalSettingDto = new MonthlyCalSettingDto();
            }
            
            public updateYear(year: number): void {
                let self = this;
                self.statutoryWorktimeSettingDto.year = year;
            }
        }
        
        
        export class StatutoryWorktimeSettingDto {
            year: number;
            // 会社別通常勤務労働時間
            regularLaborTime: NormalWorktimeDto;
            // 会社別変形労働労働時間
            transLaborTime: NormalWorktimeDto;
            
            // 会社別通常勤務月間労働時間
            normalSetting: WorktimeNormalDeformSettingDto;
            // 会社別フレックス勤務月間労働時間
//            flexSetting: KnockoutObservable<WorktimeFlexSetting>;
            flexSetting: WorktimeFlexSetting1Dto;
            // 会社別変形労働月間労働時間
            deforLaborSetting: WorktimeNormalDeformSettingDto;
            
            constructor() {
                let self = this;
                self.regularLaborTime = new NormalWorktimeDto();
                self.transLaborTime = new NormalWorktimeDto();
                
                self.normalSetting = new WorktimeNormalDeformSettingDto();
                self.flexSetting = new WorktimeFlexSetting1Dto();
                self.deforLaborSetting = new WorktimeNormalDeformSettingDto();
            }
        }
        
        export class MonthlyCalSettingDto {
            // Details
            // 通常勤務労働会社別月別実績集計設定
            regAggrSetting: NormalWorktimeAggrSettingDto;
            // 変形労働会社別月別実績集計設定
            deforAggrSetting: DeformWorktimeAggrSettingDto;
            // フレックス会社別月別実績集計設定
            flexAggrSetting: FlexWorktimeAggrSettingDto;
            
            constructor() {
                let self = this;
                self.regAggrSetting = new NormalWorktimeAggrSettingDto();
                self.deforAggrSetting = new DeformWorktimeAggrSettingDto();
                self.flexAggrSetting = new FlexWorktimeAggrSettingDto();
            }
        }
        
        export class NormalWorktimeDto {
            // 会社別通常勤務労働時間
            dailyTime: DailyUnitDto;
            weeklyTime: WeeklyUnitDto;
//            startWeek: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.dailyTime = new DailyUnitDto();
                self.weeklyTime = new WeeklyUnitDto();
//                self.startWeek = ko.observable(StartWeek.SUNDAY);
            }
        }
        
        export class DailyUnitDto {
            dailyTime: number;
            constructor() {
                let self = this;
                self.dailyTime = 0;
            }
        }
        
        export class WeeklyUnitDto {
            time: number;
            start: number;
            constructor() {
                let self = this;
                self.time = 0;
                self.start = 0;
            }
        }
        
        /**
         * 会社別通常勤務月間労働時間
         * 会社別変形労働月間労働時間
         */
        export class WorktimeNormalDeformSettingDto {
//            year: KnockoutObservable<number>;
            // 法定時間: 月単位
            statutorySetting: MonthlyUnitDto[];
            
            constructor() {
                let self = this;
//                self.year = ko.observable(new Date().getFullYear());
                self.statutorySetting  = [];
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyUnitDto();
                    m.month = i;
                    m.monthlyTime = 0;
                    self.statutorySetting.push(m);
                }
            }
            
//            public sortMonth(startMonth: number): void {
//                let self = this;
//                let sortedList: Array<any> = new Array<any>();
//                let flexSortedList: Array<any> = new Array<any>();
//                for (let i = 0; i < 12; i++) {
//                    if (startMonth > 12) {
//                        // reset month.
//                        startMonth = 1;
//                    }
//                    let value = self.statutorySetting().filter(m => startMonth == m.month())[0];
//                    sortedList.push(value);
//                    startMonth++;
//                }
//                self.statutorySetting(sortedList);
//            }
        }
        
        /**
         * MonthlyTime 月単位
         */
        export class MonthlyUnitDto {
            month: number;
            monthlyTime: number;
            
            constructor() {
                let self = this;
                self.month = new Date().getMonth();
                self.monthlyTime = 0;
            }
        }
        
        
        /**
         * 会社別フレックス勤務月間労働時間
         */
        export class WorktimeFlexSetting1Dto {
            // 法定時間: 月単位
            statutorySetting: MonthlyUnitDto[];
            // 法定時間: 月単位
            specifiedSetting: MonthlyUnitDto[];
            
            constructor() {
                let self = this;
//                self.year = ko.observable(new Date().getFullYear());
                self.statutorySetting = [];
                self.specifiedSetting = [];
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyUnitDto();
                    m.month = i;
                    m.monthlyTime = 0;
                    self.statutorySetting.push(m);
                    self.specifiedSetting.push(m);
                }
            }
//            public sortMonth(startMonth: number): void {
//                let self = this;
//                let sortedList: Array<any> = new Array<any>();
//                let flexSortedList: Array<any> = new Array<any>();
//                for (let i = 0; i < 12; i++) {
//                    if (startMonth > 12) {
//                        // reset month.
//                        startMonth = 1;
//                    }
//                    let flexValue = self.flexSettingDetail().filter(m => startMonth == m.month())[0];
//                    flexSortedList.push(flexValue);
//                    startMonth++;
//                }
//                self.flexSettingDetail(flexSortedList);
//            }
        }
        
//        export class FlexMonthlyTimeDto {
//            month: KnockoutObservable<number>;
//            statutoryTime: KnockoutObservable<number>;
////            specifiedMonth: KnockoutObservable<number>;
//            specifiedTime: KnockoutObservable<number>;
//            constructor() {
//                let self = this;
//                self.month = ko.observable(new Date().getMonth());
//                self.statutoryTime = ko.observable(0);
//                self.specifiedTime = ko.observable(0);
//            }
//        }
        
        /**
         * 通常勤務の法定内集計設定
         */
        export class NormalWorktimeAggrSettingDto {
            // 時間外超過設定: 割増集計方法
            excessOutsideTimeSet: ExcessOutsideTimeSetDto;
            // 集計時間設定: 割増集計方法
            aggregateTimeSet: ExcessOutsideTimeSetDto;
            
            constructor() {
                let self = this;
                self.excessOutsideTimeSet = new ExcessOutsideTimeSetDto();
                self.aggregateTimeSet = new ExcessOutsideTimeSetDto();
            }
        }
        
        /**
         * 割増集計方法
         */
        export class ExcessOutsideTimeSetDto {
            legalOverTimeWork: boolean;
            legalHoliday: boolean;
            surchargeWeekMonth: boolean;
            constructor() {
                let self = this;
                self.legalOverTimeWork = false;
                self.legalHoliday = false;
                self.surchargeWeekMonth = false;
            }
        }
        
        /**
         * 変形労働時間勤務の法定内集計設定
         */
        export class DeformWorktimeAggrSettingDto {
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSetDto;
            aggregateTimeSet: ExcessOutsideTimeSetDto;
            
            isOtTransCriteria: boolean;
            settlementPeriod: DeforLaborSettlementPeriodDto;
            
//            period: KnockoutObservable<number>;
//            repeatCls: KnockoutObservable<boolean>;
//            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.excessOutsideTimeSet = new ExcessOutsideTimeSetDto();
                self.aggregateTimeSet = new ExcessOutsideTimeSetDto();
                
                self.isOtTransCriteria = false;
                self.settlementPeriod = new DeforLaborSettlementPeriodDto();
                
//                self.period = ko.observable(1);
//                self.repeatCls = ko.observable(false);
//                self.startMonth = ko.observable(new Date().getMonth());
            }
        }
        
        export class DeforLaborSettlementPeriodDto {
            /** The start month. */
            startMonth: number;

            /** The period. */
            period: number;

            /** The repeat atr. */
            repeatAtr: boolean;
            
            constructor() {
                let self = this;
                self.period = 1
                self.repeatAtr = false;
                self.startMonth = new Date().getMonth();
            }
        }
        
        /**
         * フレックス時間勤務の月の集計設定
         */
        export class FlexWorktimeAggrSettingDto {
            // 不足設定: フレックス不足設定// G2_2
            insufficSet: number;
            // 残業時間を含める: するしない区分//// G1_2
            includeOverTime: boolean;
            // 法定内集計設定: 法定内フレックス時間集計
            legalAggrSet: number;
            // 集計方法: フレックス集計方法//
            aggrMethod: number;
            
            constructor() {
                let self = this;
                self.insufficSet = ShortageSetting.CURRENT_MONTH_INTEGRATION;
                self.includeOverTime = false;
                self.legalAggrSet = AggrregateSetting.MANAGED_AS_FLEX_TIME;
                self.aggrMethod = FlexAggregateMethod.PRINCIPLE;
            }
        }
        
        //===================================MODELs=====================================================================
        /**
         * 集計設定
         */
        export class AggrregateSetting {
            static MANAGED_AS_FLEX_TIME = 1;
            static MANAGE_BREAKDOWN = 2;
        }
        
        /**
         * フレックス不足時の繰越設定
         */
        export class ShortageSetting {
            static CURRENT_MONTH_INTEGRATION = 1;
            static NEXT_MONTH_CARRY_FORWARD = 2;
        }
        
        /**
         * フレックス集計方法
         */
        export class FlexAggregateMethod {
            static PRINCIPLE = 1;
            static FOR_CONVINENCE = 2;
        }
        
        /**
         * 週開始
         */
        export class StartWeek {
            static MONDAY = 2;
            static TUESDAY = 3;
            static WEDNESDAY = 4;
            static THURSDAY = 5;
            static FRIDAY = 6;
            static SATURDAY = 7;
            static SUNDAY = 0;
            static CLOSURE_STR_DATE = 1;
        }
        
        /**
         * 割増集計方法
         */
        export class ExcessOutsideTimeSet {
            includeLegal: KnockoutObservable<boolean>;
            includeHoliday: KnockoutObservable<boolean>;
            includeExtra: KnockoutObservable<boolean>;
            constructor() {
                let self = this;
                self.includeLegal = ko.observable(false);
                self.includeHoliday = ko.observable(false);
                self.includeExtra = ko.observable(false);
            }
            
            public updateData(dto: ExcessOutsideTimeSetDto): void {
                let self = this;
                self.includeLegal(dto.legalOverTimeWork);
                self.includeHoliday(dto.legalHoliday);
                self.includeExtra(dto.surchargeWeekMonth);
            }
        }
        /**
         * MonthlyTime 月単位
         */
        export class MonthlyTime {
            month: KnockoutObservable<number>;
            time: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.month = ko.observable(new Date().getMonth());
                self.time = ko.observable(0);
            }
            public updateData(dto: MonthlyUnitDto): void {
                let self = this;
                self.month(dto.month);
                self.time(dto.monthlyTime);
            }
        }
        
        export class FlexMonthlyTime {
            month: KnockoutObservable<number>;
            statutoryTime: KnockoutObservable<number>;
//            specifiedMonth: KnockoutObservable<number>;
            specifiedTime: KnockoutObservable<number>;
            constructor() {
                let self = this;
                self.month = ko.observable(new Date().getMonth());
                self.statutoryTime = ko.observable(0);
                self.specifiedTime = ko.observable(0);
            }
            
//            public updateData(dto: ): void {
//                let self = this;
//            }
        }
        
        export class NormalWorktime {
            // 会社別通常勤務労働時間
            dailyTime: KnockoutObservable<number>;
            weeklyTime: KnockoutObservable<number>;
            startWeek: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.dailyTime = ko.observable(0);
                self.weeklyTime = ko.observable(0);
                self.startWeek = ko.observable(StartWeek.SUNDAY);
            }
            
            public updateData(dto: NormalWorktimeDto): void {
                let self = this;
                self.dailyTime(dto.dailyTime.dailyTime);
                self.weeklyTime(dto.weeklyTime.time);
                self.startWeek(dto.weeklyTime.start);
            }
        }
        
        /**
         * 会社別通常勤務月間労働時間
         * 会社別変形労働月間労働時間
         */
        export class WorktimeNormalDeformSetting {
            year: KnockoutObservable<number>;
            // 法定時間: 月単位
            statutorySetting: KnockoutObservableArray<MonthlyTime>;
            
            constructor() {
                let self = this;
                self.year = ko.observable(new Date().getFullYear());
                self.statutorySetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    self.statutorySetting.push(m);
                }
            }
            
            public updateData(dto: StatutoryWorktimeSettingDto): void {
                let self = this;
                self.year(dto.year);
                self.statutorySetting().forEach(i => {
                    let updatedData: MonthlyUnitDto = dto.normalSetting.statutorySetting.filter(j => i.month() == j.month)[0];
                    i.updateData(updatedData);
                });
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let value = self.statutorySetting().filter(m => startMonth == m.month())[0];
                    sortedList.push(value);
                    startMonth++;
                }
                self.statutorySetting(sortedList);
            }
        }
        /**
         * 会社別フレックス勤務月間労働時間
         */
        export class WorktimeFlexSetting {
            year: KnockoutObservable<number>;
            // 法定時間: 月単位
            statutorySetting: KnockoutObservableArray<MonthlyTime>;
            // 所定時間: 月単位
            specifiedSetting: KnockoutObservableArray<MonthlyTime>;
            
            constructor() {
                let self = this;
                self.year = ko.observable(new Date().getFullYear());
                self.statutorySetting = ko.observableArray([]);
                self.specifiedSetting = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let m = new MonthlyTime();
                    m.month(i);
                    m.time(0);
                    self.statutorySetting.push(m);
                    self.statutorySetting.push(m);
                }
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                let statutorySortedList: Array<any> = new Array<any>();
                let specifiedSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let statutoryValue = self.statutorySetting().filter(m => startMonth == m.month())[0];
                    let specifiedValue = self.specifiedSetting().filter(m => startMonth == m.month())[0];
                    statutorySortedList.push(statutoryValue);
                    specifiedSortedList.push(specifiedValue);
                    startMonth++;
                }
                self.statutorySetting(statutorySortedList);
                self.specifiedSetting(specifiedSortedList);
            }
        }
        
        /**
         * 会社別フレックス勤務月間労働時間
         */
        export class WorktimeFlexSetting1 {
            year: KnockoutObservable<number>;
//            // 法定時間: 月単位
//            statutorySetting: KnockoutObservableArray<MonthlyTime>;
//            // 所定時間: 月単位
//            specifiedSetting: KnockoutObservableArray<MonthlyTime>;
            
            flexSettingDetail: KnockoutObservableArray<FlexMonthlyTime>;
            
            constructor() {
                let self = this;
                self.year = ko.observable(new Date().getFullYear());
                self.flexSettingDetail = ko.observableArray([]);
                for (let i = 1; i < 13; i++) {
                    let mFlex = new FlexMonthlyTime();
                    mFlex.month(i);
                    mFlex.statutoryTime(0);
                    mFlex.specifiedTime(0);
                    self.flexSettingDetail.push(mFlex);
                }
            }
            
            public updateData(dto: StatutoryWorktimeSettingDto): void {
                let self = this;
                self.year(dto.year);
                self.flexSettingDetail().forEach(i => {
                    // Stutory
                    let stutoryData: MonthlyUnitDto = dto.flexSetting.statutorySetting.filter(j => i.month() == j.month)[0];//WorktimeFlexSetting1Dto
                    // Specified
                    let specifiedData: MonthlyUnitDto = dto.flexSetting.specifiedSetting.filter(j => i.month() == j.month)[0];
                    
                    i.month(stutoryData.month);
                    i.statutoryTime(stutoryData.monthlyTime);
                    i.specifiedTime(specifiedData.monthlyTime);
                    
                });
            }
            
            public sortMonth(startMonth: number): void {
                let self = this;
                let sortedList: Array<any> = new Array<any>();
                let flexSortedList: Array<any> = new Array<any>();
                for (let i = 0; i < 12; i++) {
                    if (startMonth > 12) {
                        // reset month.
                        startMonth = 1;
                    }
                    let flexValue = self.flexSettingDetail().filter(m => startMonth == m.month())[0];
                    flexSortedList.push(flexValue);
                    startMonth++;
                }
                self.flexSettingDetail(flexSortedList);
            }
        }
        
        /**
         * 通常勤務の法定内集計設定
         */
        export class NormalWorktimeAggrSetting {
            // 時間外超過設定: 割増集計方法
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            // 集計時間設定: 割増集計方法
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            constructor() {
                let self = this;
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
            }
            // MonthlyCalSettingDto
            public updateData(dto: NormalWorktimeAggrSettingDto): void {
                let self = this;
                self.excessOutsideTimeSet.updateData(dto.excessOutsideTimeSet);
                self.aggregateOutsideTimeSet.updateData(dto.aggregateTimeSet);
            }
        }
        
        /**
         * 変形労働時間勤務の法定内集計設定
         */
        export class DeformWorktimeAggrSetting {
            // 通常勤務労働会社別月別実績集計設定
            excessOutsideTimeSet: ExcessOutsideTimeSet;
            aggregateOutsideTimeSet: ExcessOutsideTimeSet;
            
            isDeformedOT: KnockoutObservable<boolean>;
            period: KnockoutObservable<number>;
            repeatCls: KnockoutObservable<boolean>;
            startMonth: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.excessOutsideTimeSet = new ExcessOutsideTimeSet();
                self.aggregateOutsideTimeSet = new ExcessOutsideTimeSet();
                
                self.isDeformedOT = ko.observable(false);
                self.period = ko.observable(1);
                self.repeatCls = ko.observable(false);
                self.startMonth = ko.observable(new Date().getMonth());
            }
            
            public updateData(dto: DeformWorktimeAggrSettingDto): void {
                let self = this;
                self.excessOutsideTimeSet.updateData(dto.excessOutsideTimeSet);
                self.aggregateOutsideTimeSet.updateData(dto.aggregateTimeSet);
                self.isDeformedOT(dto.isOtTransCriteria);
                self.startMonth(dto.settlementPeriod.startMonth);
                self.period(dto.settlementPeriod.period);
                self.repeatCls(dto.settlementPeriod.repeatAtr);
            }
        }
        
        /**
         * フレックス時間勤務の月の集計設定
         */
        export class FlexWorktimeAggrSetting {
            // 不足設定: フレックス不足設定// G2_2
            shortageSetting: KnockoutObservable<number>;
            // 残業時間を含める: するしない区分//// G1_2
            includeOT: KnockoutObservable<boolean>;
            // 法定内集計設定: 法定内フレックス時間集計
            legalAggrSet: KnockoutObservable<number>;
            // 集計方法: フレックス集計方法//
            aggregateMethod: KnockoutObservable<number>;
            
            constructor() {
                let self = this;
                self.shortageSetting = ko.observable(ShortageSetting.CURRENT_MONTH_INTEGRATION);
                self.includeOT = ko.observable(false);
                self.legalAggrSet = ko.observable(AggrregateSetting.MANAGED_AS_FLEX_TIME);
                self.aggregateMethod = ko.observable(FlexAggregateMethod.PRINCIPLE);
            }
            
            public updateData(dto: FlexWorktimeAggrSettingDto): void {
                let self = this;
                self.shortageSetting(dto.insufficSet);
                self.includeOT(dto.includeOverTime);
                self.legalAggrSet(dto.legalAggrSet);
                self.aggregateMethod(dto.aggrMethod);
            }
        }
        
        /**
         * Normal Setting Params Model (Screen F)
         */
        export class NormalSetParams {
            startWeek: number;
            isIncludeExtraAggr: boolean;
            isIncludeLegalAggr: boolean;
            isIncludeHolidayAggr: boolean;
            isIncludeExtraExcessOutside: boolean;
            isIncludeLegalExcessOutside: boolean;
            isIncludeHolidayExcessOutside: boolean;
            
            constructor() {
                let self = this;
                self.startWeek = 0;
                self.isIncludeExtraAggr = false;
                self.isIncludeLegalAggr = false;
                self.isIncludeHolidayAggr = false;
                self.isIncludeExtraExcessOutside = false;
                self.isIncludeLegalExcessOutside = false;
                self.isIncludeHolidayExcessOutside = false;
            }
        }
        
        /**
         * Flex Setting Params Model
         */
        export class FlexSetParams {
            isIncludeOverTime: boolean;
            shortageSetting: number;
            
            constructor() {
                let self = this;
                self.isIncludeOverTime = false;
                self.shortageSetting = 1;
            }
        }
        
        /**
         * Deformed Labor Setting Params Model
         */
        export class DeformSetParams {
            strMonth: number;
            period: number;
            repeatCls: boolean;
            startWeek: number;
            isIncludeExtraAggr: boolean;
            isIncludeLegalAggr: boolean;
            isIncludeHolidayAggr: boolean;
            isIncludeExtraExcessOutside: boolean;
            isIncludeLegalExcessOutside: boolean;
            isIncludeHolidayExcessOutside: boolean;
            
            constructor() {
                let self = this;
                self.strMonth = moment(new Date()).toDate().getMonth();
                self.period = 1;
                self.repeatCls = false;
                self.startWeek = 0;
                self.isIncludeExtraAggr = false;
                self.isIncludeLegalAggr = false;
                self.isIncludeHolidayAggr = false;
                self.isIncludeExtraExcessOutside = false;
                self.isIncludeLegalExcessOutside = false;
                self.isIncludeHolidayExcessOutside = false;
            }
        }
    }
}