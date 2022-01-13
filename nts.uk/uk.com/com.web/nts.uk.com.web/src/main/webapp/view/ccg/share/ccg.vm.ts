module nts.uk.com.view.ccg.share.ccg {

    import ListType = kcp.share.list.ListType;
    import ComponentOption = kcp.share.list.ComponentOption;
    import TreeComponentOption = kcp.share.tree.TreeComponentOption;
    import StartMode = kcp.share.tree.StartMode;
    import SelectType = kcp.share.list.SelectType;
    import UnitModel = kcp.share.list.UnitModel;
    import EmployeeSearchDto = service.model.EmployeeSearchDto;
    import Ccg001ReturnedData = service.model.Ccg001ReturnedData;
    import GroupOption = service.model.GroupOption;
    import EmployeeRangeSelection = service.model.EmployeeRangeSelection;
    import EmployeeQueryParam = service.model.EmployeeQueryParam;
    import EmployeeDto = service.model.EmployeeDto;
    import DatePeriodDto = service.model.DatePeriodDto;
    import BusinessType = service.model.BusinessType;

    export module viewmodel {
        
        /**
        * Screen Model.
        */
        
        export class ListGroupScreenModel {
            /** Domain characteristic */
            employeeRangeSelection: EmployeeRangeSelection;
            comEmployment: any;
            comClassification: any;
            comJobTitle: any;

            /** Common properties */
            showEmployeeSelection: boolean; // 検索タイプ
            systemType: number; // システム区分
			employeesDoNotManageSchedules: KnockoutObservable<any>; // スケジュール管理しない社員を取り除く
            showQuickSearchTab: boolean; // クイック検索
            showAdvancedSearchTab: boolean; // 詳細検索
            showBaseDate: boolean; // 基準日利用
            showClosure: boolean; // 就業締め日利用
            showAllClosure: boolean; // 全締め表示
            showPeriod: boolean; // 対象期間利用
            showPeriodYM: boolean; // 対象期間精度
            maxPeriodRange: string; // 最長期間 
			

            /** Required parameter */
            inputBaseDate: KnockoutObservable<string>;
            baseDateOfParentScreen: KnockoutObservable<any>;
            periodStartOfParentScreen: KnockoutObservable<any>;
            periodEndOfParentScreen: KnockoutObservable<any>;
            dateRangeOfParentScreen: KnockoutObservable<DateRangePickerModel>;
            inputPeriod: KnockoutObservable<DateRangePickerModel>;
            baseDate: KnockoutComputed<moment.Moment>;
            periodStart: KnockoutComputed<moment.Moment>;
            periodEnd: KnockoutComputed<moment.Moment>;

            /** Quick search tab options */
            showAllReferableEmployee: boolean; // 参照可能な社員すべて
            showOnlyMe: boolean; // 自分だけ
            showSameDepartment: boolean; // 同じ部門の社員
            showSameDepartmentAndChild: boolean; // 同じ部門とその配下の社員
            showSameWorkplace: boolean; // 同じ職場の社員
            showSameWorkplaceAndChild: boolean; // 同じ職場とその配下の社員

            /** Advanced search properties */
            showEmployment: boolean; // 雇用条件
            showDepartment: boolean; // 部門条件
            showWorkplace: boolean; // 職場条件
            showClassification: boolean; // 分類条件
            showJobTitle: boolean; // 職位条件
            showWorktype: boolean; // 勤種条件
            isMultiple: boolean; // 選択モード

            // flags
            isShow: KnockoutObservable<boolean>;
            isOpenStatusOfEmployeeList: KnockoutObservable<boolean>;
            isOpenEmploymentList: KnockoutObservable<boolean>;
            isOpenClassificationList: KnockoutObservable<boolean>;
            isOpenJoptitleList: KnockoutObservable<boolean>;
            isOpenDepartmentList: KnockoutObservable<boolean>;
            isOpenWorkplaceList: KnockoutObservable<boolean>;
            isOpenWorkTypeList: KnockoutObservable<boolean>;
            isInDialog: boolean;
            isApplySearchDone: boolean = true;
            hasShownErrorDialog: boolean = false;
            isFocusAdvancedSearchTab: KnockoutComputed<boolean>;

            // tabs
            tabs: KnockoutObservableArray<any>;
            selectedTab: KnockoutObservable<string>;

            // selected code
            selectedCodeEmployment: KnockoutObservableArray<string>;
            selectedCodeClassification: KnockoutObservableArray<string>;
            selectedCodeJobtitle: KnockoutObservableArray<string>;
            selectedCodeDepartment: KnockoutObservableArray<string>;
            selectedCodeWorkplace: KnockoutObservableArray<string>;
            selectedCodeEmployee: KnockoutObservableArray<string>;

            // params
            employments: ComponentOption;
            classifications: ComponentOption;
            jobtitles: ComponentOption;
            departments: TreeComponentOption;
            workplaces: TreeComponentOption;
            employeeinfo: ComponentOption;
            closureList: KnockoutObservableArray<any>;
            selectedClosure: KnockoutObservable<number>;
            
            //QueryParam
            queryParam: EmployeeQueryParam;
            referenceRange: number;
            
            //params Status Of Employee
            retireStart: KnockoutComputed<string>;
            retireEnd: KnockoutComputed<string>;
            retirePeriod: KnockoutObservable<DateRangePickerModel>;

            incumbentDatasource: KnockoutObservableArray<any>;
            selectedIncumbent: KnockoutObservable<boolean>; // 在職区分
            
            closedDatasource: KnockoutObservableArray<any>;
            selectedClosed: KnockoutObservable<boolean>; // 休業区分
            
            leaveOfAbsenceDatasource: KnockoutObservableArray<any>;
            selectedLeave: KnockoutObservable<boolean>; // 休職区分
            
            retirementDatasource: KnockoutObservableArray<any>;
            selectedRetirement: KnockoutObservable<boolean>; // 退職区分

            // return function
            returnDataFromCcg001: (data: Ccg001ReturnedData) => void;

            // List WorkType
            listWorkType: KnockoutObservableArray<BusinessType>;
            selectedWorkTypeCode: KnockoutObservableArray<string>;
            workTypeColumns: KnockoutObservableArray<any>;

            // flags
            isFirstTime = true;
            isHeightFixed = false;
            isValidInput: KnockoutComputed<boolean>;
            isValidEntryDateSearch: KnockoutComputed<boolean>;
            isValidRetirementDateSearch: KnockoutComputed<boolean>;
            tab2HasLoaded = false;
            isTab2Lazy = true;

            // reserved list employee for KCP005
            reservedEmployees: KnockoutObservableArray<EmployeeSearchDto>;
            reservedEmployeesTab3: KnockoutObservableArray<EmployeeSearchDto>;

            // Acquired baseDate
            acquiredBaseDate: KnockoutObservable<string>;

            employeeListTab3: KnockoutObservableArray<UnitModel>;
            selectedEmployeesTab3: KnockoutObservableArray<string>;
            inputCodeTab3: KnockoutObservable<string>;
            inputNameTab3: KnockoutObservable<string>;
            entryDateTab3: KnockoutObservable<any>;
            retirementDateTab3: KnockoutObservable<any>;
            tab3kcp005option: any;

            employmentSubscriptions: Array<KnockoutSubscription> = [];
            employeeSubscriptions: Array<KnockoutSubscription> = [];
            ccg001Tabindex: number;
            errors: any;

            /**
             * Init screen model
             */
            constructor() {
                var self = this;
                self.initQueryParam();

                // init datasource
                self.initDatasource();

                // init selected values
                self.selectedTab = ko.observable('tab-1');
                self.selectedCodeEmployment = ko.observableArray([]);
                self.selectedCodeClassification = ko.observableArray([]);
                self.selectedCodeJobtitle = ko.observableArray([]);
                self.selectedCodeDepartment = ko.observableArray([]);
                self.selectedCodeWorkplace = ko.observableArray([]);
                self.selectedCodeEmployee = ko.observableArray([]);
                self.closureList = ko.observableArray([]);
                self.selectedClosure = ko.observable(null);

                // init reserved employee list.
                self.reservedEmployees = ko.observableArray([]);
                self.reservedEmployeesTab3 = ko.observableArray([]);

                // date picker
                self.inputBaseDate = ko.observable('');
                self.inputPeriod = ko.observable(new DateRangePickerModel(moment().toISOString(), moment().toISOString()));
                self.retirePeriod = ko.observable(new DateRangePickerModel('1900/01/01', moment().format(CcgDateFormat.YMD)));
                self.retireStart = ko.computed(() => {
                    return _.replace(self.retirePeriod().startDate, /\//g, '-');
                });
                self.retireEnd = ko.computed(() => {
                    return _.replace(self.retirePeriod().endDate, /\//g, '-');
                });

                // flags
                self.isShow = ko.observable(false);
                self.isOpenStatusOfEmployeeList = ko.observable(false);
                self.isOpenEmploymentList = ko.observable(false);
                self.isOpenClassificationList = ko.observable(false);
                self.isOpenJoptitleList = ko.observable(false);
                self.isOpenDepartmentList = ko.observable(false);
                self.isOpenWorkplaceList = ko.observable(false);
                self.isOpenWorkTypeList = ko.observable(false);
                self.isFocusAdvancedSearchTab = ko.pureComputed(() => {
                    return self.selectedTab() == 'tab-2';
                });

                // search reference date & period
                self.acquiredBaseDate = ko.observable('');

                // status of employee
                self.selectedIncumbent = ko.observable(true);
                self.selectedClosed = ko.observable(true);
                self.selectedLeave = ko.observable(true);
                self.selectedRetirement = ko.observable(false);
                
                //WorkType
                self.listWorkType = ko.observableArray([]);
                self.selectedWorkTypeCode = ko.observableArray([]);
                
                // init computed values
                self.initComputedValues();

            }

            /**
             * Initialize computed values
             */
            public initComputedValues(): void {
                let self = this;
                self.baseDate = ko.computed(() => {
                    return moment.utc(self.inputBaseDate());
                });
                self.periodStart = ko.computed(() => {
                    return moment.utc(self.inputPeriod().startDate, CcgDateFormat.YMD);
                });
                self.periodEnd = ko.computed(() => {
                    return moment.utc(self.inputPeriod().endDate, CcgDateFormat.YMD);
                });
                self.isValidInput = ko.computed(() => {
                    // trigger computing when base date or period changed
                    self.inputBaseDate();
                    self.inputPeriod();
                    self.retirePeriod();
                    return !($('#inp_baseDate').ntsError('hasError') ||
                        $('#ccg001-search-period .ntsDateRangeComponent').ntsError('hasError') ||
                        $('#ccg001-search-period .ntsStartDate input').ntsError('hasError') ||
                        $('#ccg001-search-period .ntsEndDate input').ntsError('hasError') ||
                        $('#ccg001-retire-period .ntsDateRangeComponent').ntsError('hasError') ||
                        $('#ccg001-retire-period .ntsStartDate input').ntsError('hasError') ||
                        $('#ccg001-retire-period .ntsEndDate input').ntsError('hasError'));
                });
                self.isValidEntryDateSearch = ko.computed(() => {
                    self.entryDateTab3();
                    return self.isValidInput() &&
                        !($('#ccg001-date-entry .ntsDateRangeComponent').ntsError('hasError') ||
                        $('#ccg001-date-entry .ntsStartDate input').ntsError('hasError') ||
                        $('#ccg001-date-entry .ntsEndDate input').ntsError('hasError'));
                });
                self.isValidRetirementDateSearch = ko.computed(() => {
                    self.retirementDateTab3();
                    return self.isValidInput() &&
                        !($('#ccg001-date-retirement .ntsDateRangeComponent').ntsError('hasError') ||
                        $('#ccg001-date-retirement .ntsStartDate input').ntsError('hasError') ||
                        $('#ccg001-date-retirement .ntsEndDate input').ntsError('hasError'));
                });
            }

            /**
             * Initialize subscribers
             */
            public initSubscribers(): void {
                let self = this;
                self.baseDate.subscribe(vl => {
                    self.applyDataSearch();
                });

                self.selectedTab.subscribe(vl => {
                    if (vl == 'tab-2' && !self.tab2HasLoaded) {
                        self.reloadAdvanceSearchTab();
                    }
                });
                self.selectedTab.valueHasMutated();

                self.inputPeriod.subscribe(value => {
                    if (!$('.ntsDatepicker').ntsError('hasError')) {
                        _.defer(() => self.applyDataSearch());
                    }
                });

                self.isValidInput.subscribe(isValid => {
                    let self = this;
                    const executionButton = $('.has-state');
                    const verticalButtons = executionButton.find('.ccg-btn-vertical')
                    if (isValid) {
                        executionButton.removeClass('disabled');
                        executionButton.attr('tabindex', self.ccg001Tabindex);
                        verticalButtons.attr('tabindex', self.ccg001Tabindex);
                    } else {
                        executionButton.addClass('disabled');
                        executionButton.removeAttr('tabindex');
                        verticalButtons.removeAttr('tabindex');
                    }
                });
            }

            /**
             * Init datasource
             */
            private initDatasource(): void {
                let self = this;
                self.tabs = ko.observableArray([]);
                self.incumbentDatasource = ko.observableArray([
                    { code: true, name: nts.uk.resource.getText("CCG001_40") },
                    { code: false, name: nts.uk.resource.getText("CCG001_41") }
                ]);
                self.closedDatasource = ko.observableArray([
                    { code: true, name: nts.uk.resource.getText("CCG001_40") },
                    { code: false, name: nts.uk.resource.getText("CCG001_41") }
                ]);
                self.leaveOfAbsenceDatasource = ko.observableArray([
                    { code: true, name: nts.uk.resource.getText("CCG001_40") },
                    { code: false, name: nts.uk.resource.getText("CCG001_41") }
                ]);
                self.retirementDatasource = ko.observableArray([
                    { code: true, name: nts.uk.resource.getText("CCG001_40") },
                    { code: false, name: nts.uk.resource.getText("CCG001_41") }
                ]);
                // Define gridlist's columns
                self.workTypeColumns = ko.observableArray([
                    { headerText: nts.uk.resource.getText('CCG001_60'), prop: 'businessTypeCode', width: 100 },
                    { headerText: nts.uk.resource.getText('CCG001_61'), prop: 'businessTypeName', width: 200 }
                ]);

                self.employeeListTab3 = ko.observableArray([]);
                self.selectedEmployeesTab3 = ko.observableArray([]);
                self.inputNameTab3 = ko.observable("");
                self.inputCodeTab3 = ko.observable("");
                self.entryDateTab3 = ko.observable({});
                self.retirementDateTab3 = ko.observable({});
            }
            
            /**
             * Set QuickSearchParam
             */
            private setQuickSearchParam(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;
                let param = self.queryParam;
                param.filterByEmployment = false;
                param.employmentCodes = [];
                param.filterByDepartment = false;
                param.departmentCodes = [];
                param.filterByWorkplace = false;
                param.workplaceCodes = [];
                param.filterByClassification = false;
                param.classificationCodes = [];
                param.filterByJobTitle = false;
                param.jobTitleCodes = [];
                param.filterByWorktype = false;
                param.worktypeCodes = [];
                param.filterByClosure = false;
                param.closureIds = [];
                param.includeIncumbents = true;
                param.includeWorkersOnLeave = true;
                param.includeOccupancy = true;
                param.includeRetirees = false;
                param.systemType = self.systemType;
                param.sortOrderNo = 1; // 並び順NO＝1
                param.nameType = 1; // ビジネスネーム（日本語）
				param.employeesDoNotManageSchedules = self.employeesDoNotManageSchedules();

                // set employments code condition
                if (self.showClosure && self.selectedClosure() != ConfigEnumClosure.CLOSURE_ALL) {
                    service.getEmploymentCodeByClosureId(self.selectedClosure()).done(data => {
                        param.filterByEmployment = true;
                        param.employmentCodes = data;
                        dfd.resolve();
                    });
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            private initQueryParam(): void {
                let self = this;
                self.queryParam = <EmployeeQueryParam>{};
                self.queryParam.sortOrderNo = 1; // 並び順NO＝1
                self.queryParam.nameType = 1; // ビジネスネーム（日本語）
                self.queryParam.baseDate = moment().format(CcgDateFormat.DEFAULT_FORMAT);
				
            }

            /**
             * update select tabs
             */
             
            public updateTabs(): Array<any> {
                let self = this;
                let arrTabs = [];
                // is quick search tab
                if (self.showQuickSearchTab) {
                    // push tab 1
                    arrTabs.push({
                        id: 'tab-1',
                        title: nts.uk.resource.getText("CCG001_22"),
                        content: '.tab-content-1',
                    });
                }
                // is advanced search tab
                if (self.showAdvancedSearchTab) {
                    // push tab 2
                    arrTabs.push({
                        id: 'tab-2',
                        title: nts.uk.resource.getText("CCG001_23"),
                        content: '.tab-content-2',
                    });
                }
                arrTabs.push({
                    id: 'tab-3',
                    title: nts.uk.resource.getText("CCG001_103"),
                    content: '#ccg001-tab-content-3',
                });
                // => data res
                return arrTabs;
            }

            /**
             * get tab by update selected 
             */

            public updateSelectedTab(): string {
                var self = this;
                // res tab 1
                if (self.showQuickSearchTab) {
                    return 'tab-1';
                }
                // res tab 2
                if (self.showAdvancedSearchTab) {
                    return 'tab-2';
                }
                // res none tab
                return '';
            }

            /**
             * init next tab
             */
            public initNextTabFeature() {
                var self = this;
                const TAB_KEY_CODE = 9;

                // when tab to last item of tab 1
                $('#component-ccg001 .tab-content-1').children().last().on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && self.showAdvancedSearchTab) {
                        // switch to tab 2
                        self.selectedTab('tab-2');
                    }
                    if (e.which == TAB_KEY_CODE && !self.showAdvancedSearchTab) {
                        // switch to tab 3
                        self.selectedTab('tab-3');
                    }
                });

                // when tab to last item of tab 2
                if (self.showEmployeeSelection) {
                    $('#ccg001-btn-advanced-search').on('keydown', function(e) {
                        if (e.which == TAB_KEY_CODE) {
                            // switch to tab 3
                            self.selectedTab('tab-3');
                        }
                    });
                } else {
                    $('#ccg001-btn-KCP005-apply').on('keydown', function(e) {
                        if (e.which == TAB_KEY_CODE) {
                            // switch to tab 3
                            self.selectedTab('tab-3');
                        }
                    });
                }

                // auto open accordion
                $('#StatusOfEmployeeList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenStatusOfEmployeeList()) {
                        $('#tab-2 #StatusOfEmployeeList .ui-accordion-header').click();
                        self.isOpenStatusOfEmployeeList(true);
                    }
                });
                $('#EmploymentList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenEmploymentList()) {
                        $('#tab-2 #EmploymentList .ui-accordion-header').click();
                        self.isOpenEmploymentList(true);
                    }
                });
                $('#ClassificationList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenClassificationList()) {
                        $('#tab-2 #ClassificationList .ui-accordion-header').click();
                        self.isOpenClassificationList(true);
                    }
                });
                $('#JoptitleList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenJoptitleList()) {
                        $('#tab-2 #JoptitleList .ui-accordion-header').click();
                        self.isOpenJoptitleList(true);
                    }
                });
                $('#DepartmentList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenDepartmentList()) {
                        $('#tab-2 #DepartmentList .ui-accordion-header').click();
                        self.isOpenDepartmentList(true);
                    }
                });
                $('#WorkplaceList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenWorkplaceList()) {
                        $('#tab-2 #WorkplaceList .ui-accordion-header').click();
                        self.isOpenWorkplaceList(true);
                    }
                });
                $('#WorkTypeList').on('keydown', function(e) {
                    if (e.which == TAB_KEY_CODE && !self.isOpenWorkTypeList()) {
                        $('#tab-2 #WorkTypeList .ui-accordion-header').click();
                        self.isOpenWorkTypeList(true);
                    }
                });
                
                $("#ccg001-input-code").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        self.inputCodeTab3($(this).val());
                        $('#ccg001-tab3-search-by-code').click();
                    }
                });
                
                $("#ccg001-inp-name").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        self.inputNameTab3($(this).val());
                        $('#ccg001-tab3-search-by-name').click();
                    }
                });
                
                $("#ccg001-date-entry .ntsStartDate input").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        $(this).blur();
                        self.entryDateTab3({startDate:$(this).val(), endDate: self.entryDateTab3().endDate});
                        self.entryDateTab3.valueHasMutated();
                        self.validateTab3EntryDate();
                        $(this).focus();
                        if(self.isValidEntryDateSearch()) {
                            $('#search-by-entry-date').click();
                        }
                    }
                });
                
                $("#ccg001-date-entry .ntsEndDate input").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        $(this).blur();
                        self.entryDateTab3({startDate:self.entryDateTab3().startDate, endDate:  $(this).val()});
                        self.entryDateTab3.valueHasMutated();
                        self.validateTab3EntryDate();
                         $(this).focus();
                        if(self.isValidEntryDateSearch()) {
                            $('#search-by-entry-date').click();
                        }
                    }
                });
                
                 $("#ccg001-date-retirement .ntsStartDate input").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        $(this).blur();
                        self.retirementDateTab3({startDate:$(this).val(), endDate: self.retirementDateTab3().endDate});
                        self.retirementDateTab3.valueHasMutated();
                        self.validateTab3DateRetirement();
                        $(this).focus();
                        if(self.isValidRetirementDateSearch()) {
                            self.searchByRetirementDate();
                        }
                    }
                });
                
                $("#ccg001-date-retirement .ntsEndDate input").on("keydown",function (e) {
                    if(e.keyCode == 13) {
                        $(this).blur();
                        self.retirementDateTab3({startDate:self.retirementDateTab3().startDate, endDate:  $(this).val()});
                        self.retirementDateTab3.valueHasMutated();
                        self.validateTab3DateRetirement();
                        $(this).focus();
                        if(self.isValidRetirementDateSearch()) {
                            self.searchByRetirementDate();
                        }
                    }
                });

            }
            
            public validateTab3EntryDate() {
                $('#ccg001-date-entry .ntsDateRangeComponent').trigger('validate');
                $('#ccg001-date-entry .ntsStartDate input').trigger('validate');
                $('#ccg001-date-entry .ntsEndDate input').trigger('validate');
            }
            
            public validateTab3DateRetirement() {
                $('#ccg001-date-retirement .ntsDateRangeComponent').trigger('validate');
                $('#ccg001-date-retirement .ntsStartDate input').trigger('validate');
                $('#ccg001-date-retirement .ntsEndDate input').trigger('validate');
            }

            
            /**
             * Init component.
             */
             
            public init($input: JQuery, data: GroupOption): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;

                // set component properties
                self.setProperties(data);
                
                const initComponent = () => {
                    // start component
                    nts.uk.ui.block.invisible(); // block ui
                    self.startComponent().done(() => {
                        self.setShowHideByReferenceRange();
    
                        // Initial tab panel
                        self.tabs(self.updateTabs());
                        self.selectedTab(self.updateSelectedTab());
    
                        // init view
                        $input.html(CCG001_HTML);
                        _.defer(() => {
                            ko.cleanNode($input[0]);
                            ko.applyBindings(self, $input[0]);
                            // Set tabindex
                            self.initNextTabFeature();
                            $('#ccg001-btn-search-drawer').attr('tabindex', self.ccg001Tabindex);
                            // init ccg show/hide event
                            self.initCcgEvent();
                            // set component height
                            self.setComponentHeight();
    
                            if (data.showOnStart) {
                                self.showComponent().done(() => dfd.resolve());
                            } else {
                                dfd.resolve();
                            }
                            nts.uk.ui.block.clear();
                        });
                    });
                };

                if (_.isNil(ko.dataFor(document.body))) {
                    nts.uk.ui.viewModelApplied.add(initComponent);
                } else {
                    initComponent();
                }

                return dfd.promise();
            }

            private setShowHideByReferenceRange(): void {
                let self = this;
                // set advanced search tab flag
                self.showAdvancedSearchTab = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    self.showAdvancedSearchTab;
                // always show quick search if advanced search is hidden
                self.showQuickSearchTab = self.showAdvancedSearchTab ? self.showQuickSearchTab : true;

                self.showAllReferableEmployee = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    self.showAllReferableEmployee;

                // 部門対応 #106786
                self.showSameDepartment = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    self.showSameDepartment;
                self.showSameDepartmentAndChild = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    (self.referenceRange == EmployeeReferenceRange.ALL_REFERENCE_RANGE
                        || self.referenceRange == EmployeeReferenceRange.AFFILIATION_AND_ALL_SUBORDINATES)
                    && self.showSameDepartmentAndChild;

                self.showSameWorkplace = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    self.showSameWorkplace;
                self.showSameWorkplaceAndChild = self.systemType == ConfigEnumSystemType.ADMINISTRATOR ? true :
                    (self.referenceRange == EmployeeReferenceRange.ALL_REFERENCE_RANGE
                        || self.referenceRange == EmployeeReferenceRange.AFFILIATION_AND_ALL_SUBORDINATES)
                    && self.showSameWorkplaceAndChild;
            }

            /**
             * Start component
             */
            private startComponent(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;
                $.when(service.getRefRangeBySysType(self.systemType),
                    self.loadClosure()).done((refRange, noValue,) => {
                    self.referenceRange = refRange;
                    self.loadWkpManagedByLoginnedUser().done(() => {
                        dfd.resolve();
                    }).fail(err => nts.uk.ui.dialog.alertError(err));
                }).fail(err => nts.uk.ui.dialog.alertError(err));

                return dfd.promise();
            }

			private loadWkpManagedByLoginnedUser(): JQueryPromise<any> {
                let dfd = $.Deferred<void>();
                let self = this,
                    isCheckForAdvancedSearch = self.showAdvancedSearchTab && self.systemType === ConfigEnumSystemType.EMPLOYMENT && self.referenceRange === EmployeeReferenceRange.ONLY_MYSELF,
                    isCheckForAllReferable = self.showAllReferableEmployee && self.systemType === ConfigEnumSystemType.EMPLOYMENT && self.referenceRange === EmployeeReferenceRange.ONLY_MYSELF;
                  
                if (isCheckForAdvancedSearch || isCheckForAllReferable) {
                    service.getCanManageWpkForLoginUser().done(manageWkp => {
                        self.checkForAdvancedSearch(manageWkp);
                        self.checkForAllReferable(manageWkp);
                        
                        dfd.resolve();
                    }).fail(err => dfd.reject(err));    
                } else {
                    self.checkForAdvancedSearch([]);
                    self.checkForAllReferable([]);
                    
                    dfd.resolve(); 
                }

                return dfd.promise();
            }
            
            private checkForAdvancedSearch (manageWkp: Array<any>) {
                let self = this;
                if (self.showAdvancedSearchTab) {
                    if (self.systemType === ConfigEnumSystemType.EMPLOYMENT) {
                        if (self.referenceRange === EmployeeReferenceRange.ONLY_MYSELF && _.isEmpty(manageWkp)) {
                            self.showAdvancedSearchTab = false;
                        }
                    } else {
                        self.showAdvancedSearchTab = false;    
                    }
                }
            }
            
            private checkForAllReferable (manageWkp: Array<any>) {
                let self = this;
                if (self.systemType === ConfigEnumSystemType.ADMINISTRATOR) {
                    self.showAllReferableEmployee = true;
                } else {
                    if (self.showAllReferableEmployee) {
                        if (self.systemType === ConfigEnumSystemType.EMPLOYMENT 
                            && self.referenceRange === EmployeeReferenceRange.ONLY_MYSELF 
                            && _.isEmpty(manageWkp)) {
                            self.showAdvancedSearchTab = false;
							self.showAllReferableEmployee = false;
							self.showSameDepartment = false;
							self.showSameWorkplace = false;
                        }
                    }    
                }
            }

            /**
             * Set advanced search param
             */
            private setAdvancedSearchParam(): void {
                let self = this;
                let param = this.queryParam;
                param.referenceRange = SearchReferenceRange.ALL_REFERENCE_RANGE;

                // filter param
                param.filterByEmployment = self.showEmployment;
                param.filterByDepartment = self.showDepartment;
                param.filterByWorkplace = self.showWorkplace;
                param.filterByClassification = self.showClassification;
                param.filterByJobTitle = self.showJobTitle;
                param.filterByWorktype = self.showWorktype;
                param.filterByClosure = self.showClosure && self.selectedClosure() != ConfigEnumClosure.CLOSURE_ALL;

                // filter status of employment
                param.includeIncumbents = self.selectedIncumbent();
                param.includeWorkersOnLeave = self.selectedLeave();
                param.includeOccupancy = self.selectedClosed();
                param.includeRetirees = self.selectedRetirement();
                param.retireStart = self.retireStart();
                param.retireEnd = self.retireEnd();
                param.systemType = self.systemType;
				param.employeesDoNotManageSchedules = self.employeesDoNotManageSchedules();

                self.queryParam.employmentCodes = self.showEmployment ? self.selectedCodeEmployment() : [];
                self.queryParam.classificationCodes = self.showClassification ? self.selectedCodeClassification() : [];
                self.queryParam.jobTitleCodes = self.showJobTitle ? self.selectedCodeJobtitle() : [];
                self.queryParam.departmentCodes = self.showDepartment ? self.selectedCodeDepartment() : [];
                self.queryParam.workplaceCodes = self.showWorkplace ? self.selectedCodeWorkplace() : [];
                self.queryParam.worktypeCodes = self.showWorktype ? self.selectedWorkTypeCode() : [];
                self.queryParam.closureIds = self.showClosure ? [self.selectedClosure()] : [];
            }

            /**
             * Set component properties
             */
            private setProperties(options: GroupOption): void {
                let self = this;

                /** Common properties */
                self.showEmployeeSelection = _.isNil(options.showEmployeeSelection) ? false : options.showEmployeeSelection;
                self.systemType = _.isNil(options.systemType) ? ConfigEnumSystemType.PERSONAL_INFORMATION : options.systemType;
				self.employeesDoNotManageSchedules = _.isNil(options.employeesDoNotManageSchedules) ? ko.observable(false) : options.employeesDoNotManageSchedules;
                self.showQuickSearchTab = _.isNil(options.showQuickSearchTab) ? true : options.showQuickSearchTab;
                self.showAdvancedSearchTab = _.isNil(options.showAdvancedSearchTab) ? true : options.showAdvancedSearchTab;
              
                // showBaseDate and showPeriod can not hide at the same time
                const isBaseDateAndPeriodHidden = !options.showBaseDate && !options.showPeriod;
                self.showBaseDate = _.isNil(options.showBaseDate) ? true : (isBaseDateAndPeriodHidden ? true : options.showBaseDate);
                self.showAllClosure = _.isNil(options.showAllClosure) ? true : options.showAllClosure;
                self.showPeriod = _.isNil(options.showPeriod) ? false : options.showPeriod;
                self.showClosure = _.isNil(options.showClosure) ? false : options.showClosure;
                // if ShowPeriod = false then period accuracy must be false too. 
                self.showPeriodYM = _.isNil(self.showPeriod) ? false : (self.showPeriod ? options.periodFormatYM : false);
                self.maxPeriodRange = options.maxPeriodRange ? options.maxPeriodRange : 'none';
                self.isTab2Lazy = _.isNil(options.isTab2Lazy) ? true : options.isTab2Lazy;

                /** Required parameter */
                self.setBaseDateAndPeriodOnInit(options);
                
//                   3.14 #102965                
//                self.selectedIncumbent(options.inService);
//                self.selectedLeave(options.leaveOfAbsence);
//                self.selectedClosed(options.closed);
//                self.selectedRetirement(options.retirement);

                /** Quick search tab options */
                self.showAllReferableEmployee = _.isNil(options.showAllReferableEmployee) ? true : options.showAllReferableEmployee;
                self.showOnlyMe = true;
                self.showSameDepartment = _.isNil(options.showSameDepartment) ? false : options.showSameDepartment;
                self.showSameDepartmentAndChild = _.isNil(options.showSameDepartmentAndChild) ? false : options.showSameDepartmentAndChild;
                self.showSameWorkplace = _.isNil(options.showSameWorkplace) ? false : options.showSameWorkplace;
                self.showSameWorkplaceAndChild = _.isNil(options.showSameWorkplaceAndChild) ? false: options.showSameWorkplaceAndChild;

                /** Advanced search properties */
                // update ver 5.3
                self.showEmployment = _.isNil(options.showEmployment) ? false : options.showEmployment;
                self.showDepartment = _.isNil(options.showDepartment) ? false : options.showDepartment;
                self.showWorkplace = _.isNil(options.showWorkplace) ? false : options.showWorkplace;
                self.showClassification = _.isNil(options.showClassification) ? false : options.showClassification;
                self.showJobTitle = _.isNil(options.showJobTitle) ? false : options.showJobTitle;
                // up ver 5.3
                self.showWorktype = self.systemType == ConfigEnumSystemType.EMPLOYMENT && options.showWorktype;
                self.isMultiple = _.isNil(options.isMutipleCheck) ? true : options.isMutipleCheck;

                /** Optional properties */
                self.isInDialog = _.isNil(options.isInDialog) ? false : options.isInDialog;
                self.ccg001Tabindex = _.isNil(options.tabindex) ? 1 : options.tabindex;

                // return data function
                self.returnDataFromCcg001 = options.returnDataFromCcg001;
            }

            /**
             * Set component height
             */
            private setComponentHeight(): void {
                let self = this;
                const headerHeight = $('#header').outerHeight();
                const sidebarHeaderHeight = $('.sidebar-content-header').outerHeight(); // for screen with sidebar
                const functionAreaHeight = $('#functions-area').length > 0 ? $('#functions-area').outerHeight() : 0;
                const buffer = 25;
                let componentHeight = 0;

                // calculate component height
                if (self.isInDialog) {
                    componentHeight = window.innerHeight - functionAreaHeight - buffer;
                } else {
                    const notIncluded = headerHeight + functionAreaHeight + (sidebarHeaderHeight ? sidebarHeaderHeight : 0) + buffer;
                    componentHeight = window.innerHeight - notIncluded;
                }

                const minHeight = 450;
                if (componentHeight < minHeight) {
                    componentHeight = minHeight;
                }

                // set component height
                $('#component-ccg001').outerHeight(componentHeight);
                $('#ccg001-btn-search-drawer').outerHeight(componentHeight / 2);

                // set tab panel height.
                const tabpanelHeight = componentHeight - $('#ccg001-header').outerHeight(true) - 10;
                const tabpanelNavHeight = 85;
                const tabpanelContentHeight = tabpanelHeight - tabpanelNavHeight;
                $('.ccg-tabpanel.pull-left').outerHeight(tabpanelHeight);
                $('.ccg-tabpanel>#tab-1').css('height', tabpanelContentHeight);
                $('.ccg-tabpanel>#tab-2').css('height', tabpanelContentHeight);
                $('.ccg-tabpanel>#tab-3').css('height', tabpanelContentHeight);
            }

            /**
             * Load ListClosure 
             */
            private loadClosure(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showClosure) {
                    service.getClosuresByBaseDate(self.baseDate().format(CcgDateFormat.DEFAULT_FORMAT)).done(data => {
                        if (self.showAllClosure) {
                            data.unshift({
                                closureId: ConfigEnumClosure.CLOSURE_ALL,
                                closureName: ConfigEnumClosure.CLOSURE_ALL_NAME
                            });
                        }

                        // set closure list
                        self.closureList(data);

                        self.getSelectedClosure().done(selected => {
                            // set selected closure
                            self.selectedClosure(selected);

                            // initialize selected cosure subscriber
                            self.selectedClosure.subscribe(vl => {
                                // calculate period by current month
                                // self.calculatePeriod(parseInt(moment().format(CcgDateFormat.YEAR_MONTH))).done(period => {
                                self.calculatePeriod105458().done(period => {
                                    self.isApplySearchDone = false;
                                    self.inputPeriod(new DateRangePickerModel(self.showPeriodYM ? period.endDate : period.startDate, period.endDate));
                                    self.inputBaseDate(period.endDate);
                                    self.isApplySearchDone = true;

                                    this.saveEmployeeRangeSelection();
                                    // apply data search
                                    self.applyDataSearch();
                                    self.employeeListTab3([]);
                                });
                            });

                            dfd.resolve();
                        });
                    });
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Get selected closure id
             */
            private getSelectedClosure(): JQueryPromise<number> {
                let dfd = $.Deferred<number>();
                let self = this;
                service.getEmployeeRangeSelection().done((data: EmployeeRangeSelection) => {
                    if (data) {
                        // set employeeRangeSelection
                        self.employeeRangeSelection = data;

                        // get selected closure id
                        switch (self.systemType) {
                            case ConfigEnumSystemType.PERSONAL_INFORMATION:
                                if (!nts.uk.util.isNullOrEmpty(data.personalInfo.selectedClosureId)) {
                                    dfd.resolve(data.personalInfo.selectedClosureId);
                                } else {
                                    self.getSelectedClosureByEmployment().done(id => dfd.resolve(id));
                                }
                                break;
                            case ConfigEnumSystemType.EMPLOYMENT:
                                if (!nts.uk.util.isNullOrEmpty(data.employmentInfo.selectedClosureId)) {
                                    dfd.resolve(data.employmentInfo.selectedClosureId);
                                } else {
                                    self.getSelectedClosureByEmployment().done(id => dfd.resolve(id));
                                }
                                break;
                            case ConfigEnumSystemType.SALARY:
                                if (!nts.uk.util.isNullOrEmpty(data.salaryInfo.selectedClosureId)) {
                                    dfd.resolve(data.salaryInfo.selectedClosureId);
                                } else {
                                    self.getSelectedClosureByEmployment().done(id => dfd.resolve(id));
                                }
                                break;
                            case ConfigEnumSystemType.HUMAN_RESOURCES:
                                if (!nts.uk.util.isNullOrEmpty(data.humanResourceInfo.selectedClosureId)) {
                                    dfd.resolve(data.humanResourceInfo.selectedClosureId);
                                } else {
                                    self.getSelectedClosureByEmployment().done(id => dfd.resolve(id));
                                }
                                break;
                            default: break; // systemType not found
                        }
                    } else {
                        self.getSelectedClosureByEmployment().done(id => dfd.resolve(id));
                    }
                });
                return dfd.promise();
            }

            /**
             * Get selected closure by employment
             */
            private getSelectedClosureByEmployment(): JQueryPromise<number> {
                let dfd = $.Deferred<number>();
                service.getCurrentHistoryItem().done(item => {
                    if (item) {
                        service.getClosureTiedByEmployment(item.employmentCode).done(id => dfd.resolve(id));
                    } else {
                        const DEFAULT_VALUE = 1;
                        // Q&A: #88282 (update specs)
                        dfd.resolve(DEFAULT_VALUE);
                    }
                });
                return dfd.promise();
            }

            /**
             * Initial ccg event
             */
            private initCcgEvent(): void {
                let self = this;
                $(window).on('click', function(e) {
                    // Check is click to inside component.
                    if (e.target.id == "component-ccg001" || $(e.target).parents("#component-ccg001")[0]) {
                        return;
                    }
                    // click when block ui
                    if (!_.isEmpty($('div.ui-widget-overlay.ui-front'))) {
                        return;
                    }
                    if (!_.isEmpty($('div.blockUI.blockOverlay'))) {
                        return;
                    }
                    // check is click to errors notifier
                    if (e.target.id == 'func-notifier-errors') {
                        return;
                    }
                    // Check is click to dialog.
                    if ($(e.target).parents("[role='dialog']")[0]) {
                        return;
                    }
                    // Check is click to ignite combo-box.
                    if ($(e.target).parents().hasClass('ui-igcombo-dropdown')) {
                        return;
                    }
                    if (e.target.id == "hor-scroll-button-hide" || $(e.target).parents("#hor-scroll-button-hide")[0]) {
                        return;
                    }
                    self.hideComponent();
                });
            }
            
            /**
             * Hide component
             */
            public hideComponent(): void {
                let self = this;
                if (self.isShow()) {
                    $('#component-ccg001').toggle('slide', () => {
                        self.errors = $('#component-ccg001 .error').children();
                        self.errors.ntsError('clear');
                        $('#component-ccg001').css('display', '');
                        $('#component-ccg001').css('visibility', 'hidden');
                    });
                    self.isShow(false);
                }
            }

            /**
             * Show component
             */
            public showComponent(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.isFirstTime) {
                    // Apply data search & load Kcp components
                    self.synchronizeDate();
                    self.toggleSlide().done(() => $.when(self.applyDataSearch(), self.loadKcp005()).always(() => {
                        // Set acquired base date to status period end date
                        self.retirePeriod(new DateRangePickerModel('1900/01/01', self.queryParam.baseDate));

                        // init subscribers
                        self.initSubscribers();

                        // update flag isFirstTime
                        self.isFirstTime = false;
                        dfd.resolve();
                    }));
                } else {
                    // toggle slide ccg001
                    self.toggleSlide().done(() => self.synchronizeDate());
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Synchronize date with parent screen
             */
            private synchronizeDate(): void {
                let self = this;
                self.isApplySearchDone = false;
                // synchronize baseDate
                if (self.baseDateOfParentScreen) {
                    const isSameDate = moment.isMoment(self.baseDateOfParentScreen()) ?
                        self.baseDateOfParentScreen().isSame(self.inputBaseDate()) : self.inputBaseDate() == self.baseDateOfParentScreen();
                    if (!isSameDate) {
                        self.inputBaseDate(self.baseDateOfParentScreen())
                    }
                }

                // synchronize period
                if (self.dateRangeOfParentScreen) {
                    const dateRangeOfParentScreen = _.clone(self.dateRangeOfParentScreen());
                    const isSameDate = DateRangePickerModel.isSamePeriod(dateRangeOfParentScreen, self.inputPeriod());
                    if (!isSameDate) {
                        self.inputPeriod(dateRangeOfParentScreen);
                    }
                } else if (self.periodStartOfParentScreen) {
                    const isSameDate = moment.isMoment(self.periodStartOfParentScreen()) ?
                        self.periodStartOfParentScreen().isSame(self.inputPeriod().startDate) && self.periodEndOfParentScreen().isSame(self.inputPeriod().endDate) :
                        self.periodStartOfParentScreen() == self.inputPeriod().startDate && self.periodEndOfParentScreen() == self.inputPeriod().endDate;
                    if (!isSameDate) {
                        self.inputPeriod(new DateRangePickerModel(self.periodStartOfParentScreen(), self.periodEndOfParentScreen()));
                    }
                }

                // recheck errors
                if (!_.isEmpty(self.errors)) {
                    self.errors.ntsError('check');
                }
                
                self.isApplySearchDone = true;
            }

            /**
             * Toggle slide CCG001
             */
            private toggleSlide(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.isShow()) {
                    return;
                }
                let componentElement = document.getElementById('component-ccg001');
                if (componentElement.style.visibility == 'hidden') {
                    componentElement.style.removeProperty('visibility');
                    componentElement.style.display = 'none';
                }
                $('#component-ccg001').toggle("slide", () => dfd.resolve());
                self.isShow(true);
                return dfd.promise();
            }

            /**
             * Load component KCP005
             */
            private loadKcp005(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;

                $.when(self.loadKcp005OnTab2(), self.loadKcp005OnTab3()).done(() => {
                    self.fixComponentWidth();
                    dfd.resolve();
                });

                return dfd.promise();
            }

            /**
             * Calculate KCP005 rows
             */
            private calculateKcp005Rows(marginHeight: number): number {
                const tabContentHeight = parseInt(document.querySelector('.ccg-tabpanel #ccg001-tab-content-3').style.height);
                const heightPerRow = 24;
                return (tabContentHeight - marginHeight) / heightPerRow;
            }

            private loadKcp005OnTab2(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showAdvancedSearchTab && self.showEmployeeSelection) {
                    const Kcp005MarginHeight = 70;

                    // set KCP005 options
                    self.employeeinfo = {
                        isShowAlreadySet: false,
                        isMultiSelect: self.isMultiple,
                        isMultipleUse: true,
                        listType: ListType.EMPLOYEE,
                        employeeInputList: ko.observableArray([]),
                        selectType: SelectType.SELECT_BY_SELECTED_CODE,
                        selectedCode: self.selectedCodeEmployee,
                        isDialog: true,
                        hasPadding: false,
                        isShowNoSelectRow: false,
                        isShowWorkPlaceName: false,
                        maxRows: self.calculateKcp005Rows(Kcp005MarginHeight),
                        tabindex: self.ccg001Tabindex,
                        subscriptions: self.employeeSubscriptions
                    }

                    // Show KCP005
                    $('#employeeinfo').ntsListComponent(self.employeeinfo).done(() => dfd.resolve());
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            private loadKcp005OnTab3(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                // Load KCP05 on tab 3
                const Kcp005MarginHeight = 255;
                const calculatedRows = self.calculateKcp005Rows(Kcp005MarginHeight);
                const maxRows = calculatedRows < 10 ? 10 : calculatedRows;

                self.tab3kcp005option = {
                    isShowAlreadySet: false,
                    maxWidth: 420,
                    isMultiSelect: self.isMultiple,
                    isMultipleUse: true,
                    listType: ListType.EMPLOYEE,
                    employeeInputList: self.employeeListTab3,
                    selectType: SelectType.SELECT_ALL,
                    selectedCode: self.selectedEmployeesTab3,
                    isDialog: true,
                    hasPadding: false,
                    isShowNoSelectRow: false,
                    isShowWorkPlaceName: true,
                    tabindex: self.ccg001Tabindex,
                    maxRows: maxRows,
                    isSelectAllAfterReload: true,
                }

                // Show KCP005
                $('#tab3kcp005').ntsListComponent(self.tab3kcp005option).done(() => dfd.resolve());
                return dfd.promise();
            }

            /**
             * Fix component width according to screen width
             */
            private fixComponentWidth(): void {
                let self = this;
                _.defer(() => {
                    // update tab 2 width
                    let totalWidth = 5;
                    $('#ccg001-tab-content-2').children('div.pull-left.height-maximum').each((i, e) => totalWidth += $(e).outerWidth(true));
                    $('#ccg001-tab-content-2').outerWidth(totalWidth);

                    // Fix component width if screen width is smaller than component
                    const componentWidth = window.innerWidth - $('#ccg001-btn-search-drawer').offset().left;
                    if (componentWidth <= $('#ccg001-tab-content-2').outerWidth()) {
                        const margin = 20;
                        // fix width and show scrollbar
                        $('.tab-content-2.height-maximum').outerWidth(componentWidth - margin);
                        $('.tab-content-2.height-maximum').css('overflow-x', 'auto');

                        // fix height
                        if (!self.isHeightFixed) {
                            const fixedTabHeight = parseInt(document.querySelector('.ccg-tabpanel>#tab-2').style.height) + 15;
                            $('.ccg-tabpanel>#tab-2').css('height', fixedTabHeight);
                            self.isHeightFixed = true;
                        }
                    }
                });
            }

            /**
             * Check future date
             */
            private isFutureDate(date: moment.Moment): boolean {
                return date.isAfter(moment());
            }

            /**
             * function click by apply data search employee (init tab 2)
             * get base date
             */
            public applyDataSearch(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;
                if (!self.isApplySearchDone) {
                    dfd.resolve();
                    return dfd.promise();
                }

                // validate input base date
                if (!self.isValidInput() || self.isInvalidBaseDate()) {
                    dfd.reject();
                    return dfd.promise();
                }

                self.isApplySearchDone = false;
                nts.uk.ui.block.grayout(); // block ui
                self.setBaseDateAndPeriod().done(() => {
                    // Comparing accquired base date to current system date.
                    if (self.isFutureDate(moment.utc(self.acquiredBaseDate(), CcgDateFormat.DEFAULT_FORMAT))) {
                        // If base date is future date, check future reference permission
                        self.getFuturePermit().done(hasPermission => {
                            if (hasPermission) {
                                self.queryParam.baseDate = self.acquiredBaseDate();
                            } else {
                                const systemDate = moment.utc().toISOString();
                                const systemDateFormated = moment.utc().format(CcgDateFormat.DEFAULT_FORMAT);
                                self.inputBaseDate(systemDate);
                                self.queryParam.baseDate = systemDateFormated;
                                if (!self.showPeriod) {
                                    self.inputPeriod(new DateRangePickerModel(systemDate, systemDate));
                                    self.queryParam.periodStart = systemDateFormated;
                                    self.queryParam.periodEnd = systemDateFormated;
                                }
                            }
                            self.loadAdvancedSearchTab().done(() => {
                                self.isApplySearchDone = true;
                                nts.uk.ui.block.clear();
                                dfd.resolve();
                            });
                        }).fail(err => {
                            nts.uk.ui.dialog.alertError(err);
                            self.isApplySearchDone = true;
                            nts.uk.ui.block.clear();
                            dfd.reject();
                        });
                    } else {
                        self.queryParam.baseDate = self.acquiredBaseDate();
                        self.loadAdvancedSearchTab().done(() => {
                            self.isApplySearchDone = true;
                            nts.uk.ui.block.clear();
                            dfd.resolve();
                        });
                    }
                }).fail(err => {
                    nts.uk.ui.dialog.alertError(err);
                    self.isApplySearchDone = true;
                    nts.uk.ui.block.clear();
                    dfd.reject();
                });

                return dfd.promise();
            }

            /**
             * Load advanced search tab
             */
            private loadAdvancedSearchTab(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;
                if (self.isTab2Lazy && !self.isFocusAdvancedSearchTab()) {
                    dfd.resolve();
                } else {
                    self.reloadAdvanceSearchTab().done(() => dfd.resolve());
                }
                return dfd.promise();
            }

            /**
             * Reload advanced search tab
             */
            private reloadAdvanceSearchTab(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;
                if (!self.tab2HasLoaded) {
                    self.tab2HasLoaded = true;
                }
                // set advanced search param
                self.queryParam.retireStart = self.retireStart();
                self.queryParam.retireEnd = self.retireEnd();

                // reload advanced search tab.
                if (_.isEmpty($('.blockUI.blockOverlay'))) {
                    nts.uk.ui.block.grayout();
                }

                self.setComponentOptions();
                $.when(self.loadEmploymentPart(),
                    self.loadDepartmentPart(),
                    self.loadWorkplacePart(),
                    self.loadWorktypePart()
                ).done(() => {
					self.loadClassificationPart().done(()=>{
						self.loadJobTitlePart().done(()=>{
		                    nts.uk.ui.block.clear();// clear block UI
		                    self.fixComponentWidth();
		                    dfd.resolve();							
						});
					});
                });
                return dfd.promise();
            }

            /**
             * Load employment part
             */
            private loadEmploymentPart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showEmployment) {
                    if (_.isNil(self.comEmployment)) {
                        $('#employmentList').ntsListComponent(self.employments).done(emp => {
                            self.comEmployment = emp;
                            dfd.resolve();
                        });
                    } else {
                        self.comEmployment.reload();
                        dfd.resolve();
                    }
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Load Classification part
             */
            private loadClassificationPart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showClassification) {
                    if (_.isNil(self.comClassification)) {
                        $('#classificationList').ntsListComponent(self.classifications).done(emp => {
                            self.comClassification = emp;
                            dfd.resolve();
                        });
                    } else {
                        self.comClassification.reload();
                        dfd.resolve();
                    }
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Load jobtitle part
             */
            private loadJobTitlePart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showJobTitle) {
                    if (_.isNil(self.comJobTitle)) {
                        $('#jobtitleList').ntsListComponent(self.jobtitles).done(emp => {
                            self.comJobTitle = emp;
                            dfd.resolve();
                        });
                    } else {
                        self.comJobTitle.reload();
                        dfd.resolve();
                    }
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Load department part
             */
            private loadDepartmentPart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showDepartment) {
                    $('#departmentList').ntsTreeComponent(self.departments).done(() => {
                        dfd.resolve();
                    });
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Load workplace part
             */
            private loadWorkplacePart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showWorkplace) {
                    $('#workplaceList').ntsTreeComponent(self.workplaces).done(() => {
                        dfd.resolve();
                    });
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * Load worktype part
             */
            private loadWorktypePart(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                if (self.showWorktype) {
                    service.searchAllWorkType().done((workTypeList: Array<BusinessType>) => {
                        self.listWorkType(workTypeList);
                        self.selectedWorkTypeCode(_.map(workTypeList, vl => vl.businessTypeCode));
                        dfd.resolve();
                    });
                } else {
                    dfd.resolve();
                }
                return dfd.promise();
            }

            /**
             * function click by button detail department or work place (open dialog)
             */
            detailDepartmentWorkplace(startMode: StartMode): void {
                let self = this;
                let inputCDL008 = {
                    baseDate: moment.utc(self.queryParam.baseDate, 'YYYY-MM-DD').toDate(),
                    isMultiple: true,
                    selectedSystemType: self.systemType,
                    selectedCodes: startMode == 0 ? self.selectedCodeWorkplace() : self.selectedCodeDepartment(),
                    isShowBaseDate: false,
                    startMode: startMode
                };
                nts.uk.ui.windows.setShared('inputCDL008', inputCDL008);
                nts.uk.ui.windows.sub.modal('com',"/view/cdl/008/a/index.xhtml").onClosed(() => {
                    if (nts.uk.ui.windows.getShared('CDL008Cancel')) {
                        return;
                    }
                    // 部門対応
                    if (startMode == StartMode.WORKPLACE) {
                        // reload selected workplace
                        self.selectedCodeWorkplace(nts.uk.ui.windows.getShared('outputCDL008'));
                        self.workplaces.selectType = SelectType.SELECT_BY_SELECTED_CODE;
                        $('#workplaceList').ntsTreeComponent(self.workplaces);
                    } else {
                        // reload selected department
                        self.selectedCodeDepartment(nts.uk.ui.windows.getShared('outputCDL008'));
                        self.departments.selectType = SelectType.SELECT_BY_SELECTED_CODE;
                        $('#departmentList').ntsTreeComponent(self.departments);
                    }
                });
            }

            /**
             * function click by button search employee
             */
            extractSelectedEmployees(): void {
                let self = this;
                if (!self.isValidInput()) {
                    return;
                }
                if (_.isEmpty(self.getSelectedCodeEmployee())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_758" });
                    return;
                }

                // Filter selected employee
                let selectedEmployees = self.getSelectedCodeEmployee();
                let filteredList = _.filter(self.reservedEmployees(), e => {
                    return _.includes(selectedEmployees, e.employeeCode);
                });

                // block ui
                nts.uk.ui.block.invisible();

                // return data
                self.returnDataFromCcg001(self.combineData(filteredList));

                // Hide component.
                self.hideComponent();

                // clear block UI
                _.defer(() => nts.uk.ui.block.clear());
            }

            public extractSelectedEmployeesInTab3(): void {
                let self = this;
                if (!self.isValidInput()) {
                    return;
                }
                if (_.isEmpty(self.getSelectedCodeEmployeeTab3())) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_758" });
                    return;
                }

                // Filter selected employee
                let selectedEmployees = self.getSelectedCodeEmployeeTab3();
                let filteredList = _.filter(self.reservedEmployeesTab3(), e => {
                    return _.includes(selectedEmployees, e.employeeCode);
                });

                // block ui
                nts.uk.ui.block.invisible();

                // return data
                self.returnDataFromCcg001(self.combineData(filteredList));

                // Hide component.
                self.hideComponent();

                // clear block UI
                _.defer(() => nts.uk.ui.block.clear());
            }

            /**
             * clear validate client
             */
            clearValiate() {
                $('#inp_baseDate').ntsError('clear');
            }

            /**
             * Validate base date
             */
            isInvalidBaseDate(): boolean {
                let self = this;
                $("#inp_baseDate").ntsEditor("validate");

                if ($('#inp_baseDate').ntsError('hasError')) {
                    return true;
                }

                if (self.showPeriod && self.showBaseDate && !self.isBaseDateInTargetPeriod()) {
                    return true;
                }
                return false;
            }

            // validate input
            private isStatusEmployeePeriodInvalid(): boolean {
                let self = this;
                $("#ccg001-partg-start").ntsEditor("validate");
                $("#ccg001-partg-end").ntsEditor("validate");
                return $("#ccg001-partg-start").ntsError('hasError') || $("#ccg001-partg-end").ntsError('hasError');
            }

            /**
             * function click by button employee login
             */
            getEmployeeLogin(): void {
                let self = this;
                nts.uk.ui.block.grayout(); // block ui
                let param = {
                    baseDate: moment.utc().toDate(),
                    systemType: self.systemType,
					employeesDoNotManageSchedules: self.employeesDoNotManageSchedules()
                };
                service.searchEmployeeByLogin(param)
                    .done(data => {
                        self.returnDataFromCcg001(self.combineData([data]));
                        self.hideComponent();
                    }).fail(function(error) {
                        nts.uk.ui.dialog.alertError(error);
                    }).always(() => nts.uk.ui.block.clear());  // clear block UI
            }

            /**
             * Combine return data
             */
            private combineData(listEmployee: Array<EmployeeSearchDto>): Ccg001ReturnedData {
                let self = this;
                let dto = <Ccg001ReturnedData>{};
                dto.baseDate = moment.utc(self.queryParam.baseDate, CcgDateFormat.DEFAULT_FORMAT).toISOString();
                dto.closureId = self.showClosure ? self.selectedClosure() : undefined;
                dto.periodStart = moment.utc(self.queryParam.periodStart).toISOString();
                dto.periodEnd = moment.utc(self.queryParam.periodEnd).toISOString();
                dto.listEmployee = listEmployee;
                return dto;
            }

            /**
             * Set base date and period
             */
            public setBaseDateAndPeriod(): JQueryPromise<void> {
                let dfd = $.Deferred<void>();
                let self = this;

                // set base date = user input
                if (self.showBaseDate) {
                    self.acquiredBaseDate(self.baseDate().format(CcgDateFormat.DEFAULT_FORMAT));
                }

                // set period
                if (self.showPeriod) {
                    const periodEnd = self.showPeriodYM ? self.periodEnd().endOf("month") : self.periodEnd();
                    self.queryParam.periodStart = self.periodStart().format(CcgDateFormat.DEFAULT_FORMAT);
                    self.queryParam.periodEnd = periodEnd.format(CcgDateFormat.DEFAULT_FORMAT);
                    if (!self.showBaseDate) {
                        self.acquiredBaseDate(self.queryParam.periodEnd);
                    }
                } else {
                    // set period = base date (Period accuracy is YMD)
                    self.queryParam.periodStart = self.baseDate().format(CcgDateFormat.DEFAULT_FORMAT);
                    self.queryParam.periodEnd = self.baseDate().format(CcgDateFormat.DEFAULT_FORMAT);
                }

                // Period accuracy is YM 
                if (self.showPeriodYM) {
//                if (self.showPeriodYM || self.showPeriod) {
                      self.calculatePeriod(parseInt(self.periodEnd().format(CcgDateFormat.YEAR_MONTH))).done(period => {
//                    self.calculatePeriod105458().done(period => {
                        if (!self.showBaseDate) {
                            // set base date = period end
                            self.acquiredBaseDate(period.endDate);
                        } 
                    
                        if (self.showBaseDate && self.showClosure && self.isFirstTime) {
                            self.isApplySearchDone = false;
                            self.inputPeriod(new DateRangePickerModel(self.showPeriodYM ? period.endDate : period.startDate, period.endDate));
                            self.inputBaseDate(period.endDate);
                        }
                        dfd.resolve();
                    });
                } else {
                    dfd.resolve();
                }

                return dfd.promise();
            }

            /**
             * Set baseDate & period on init component
             */
            private setBaseDateAndPeriodOnInit(options: GroupOption): void {
                let self = this;
                // set baseDate
                if (_.isFunction(options.baseDate)) {
                    self.baseDateOfParentScreen = options.baseDate;
                    self.inputBaseDate(options.baseDate());
                } else {
                    self.inputBaseDate(_.isNil(options.baseDate) ? moment().toISOString() : options.baseDate);
                }

                // set period
                if (options.dateRangePickerValue) {
                    self.dateRangeOfParentScreen = options.dateRangePickerValue;
                    self.inputPeriod(_.clone(self.dateRangeOfParentScreen()));
                }
                else if (_.isFunction(options.periodStartDate) && _.isFunction(options.periodEndDate)) {
                    self.periodStartOfParentScreen = options.periodStartDate;
                    self.periodEndOfParentScreen = options.periodEndDate;
                    self.inputPeriod(new DateRangePickerModel(options.periodStartDate(), options.periodEndDate()));
                } else {
                    const periodStart = _.isNil(options.periodStartDate) ? moment().toISOString() : options.periodStartDate;
                    const periodEnd = _.isNil(options.periodEndDate) ? moment().toISOString() : options.periodEndDate;
                    self.inputPeriod(new DateRangePickerModel(periodStart, periodEnd));
                }
            }

            /**
             * Calculate date period from selected closure id and yearMonth
             */
            public calculatePeriod(yearMonth: number): JQueryPromise<DatePeriodDto> {
                let self = this;
                let dfd = $.Deferred<DatePeriodDto>();
                const closureId = (self.selectedClosure() == null || self.selectedClosure() == ConfigEnumClosure.CLOSURE_ALL) ? 1 : self.selectedClosure();
                // アルゴリズム「当月の期間を算出する」を実行する
                service.calculatePeriod(closureId, yearMonth)
                    .done(period => dfd.resolve(period));
                return dfd.promise();
            }
            
            /**
             * Calculate date period from selected closure id and yearMonth
             */
            public calculatePeriod105458(): JQueryPromise<DatePeriodDto> {
                let self = this;
                let dfd = $.Deferred<DatePeriodDto>();
                const closureId = (self.selectedClosure() == null ||self.selectedClosure() == ConfigEnumClosure.CLOSURE_ALL) ? 1 : self.selectedClosure();
                // アルゴリズム「当月の期間を算出する」を実行する
                service.calculatePeriod105458(closureId)
                    .done(period => dfd.resolve(period));
                return dfd.promise();
            }

            /**
             * Get future reference permission
             */
            public getFuturePermit(): JQueryPromise<boolean> {
                let self = this;
                switch (self.systemType) {
                    case ConfigEnumSystemType.PERSONAL_INFORMATION:
                        return service.getPersonalRoleFuturePermit();
                    case ConfigEnumSystemType.EMPLOYMENT:
                        return service.getEmploymentRoleFuturePermit();
                    default: 
                        let dfd = $.Deferred<boolean>();
                        dfd.reject();
                        return dfd.promise();// systemType not found
                }
            }

            /**
             * Validate basedate & target period
             */
            public isBaseDateInTargetPeriod(): boolean {
                let self = this;
                let baseDate = self.baseDate();
                
                if (self.showPeriodYM){
                    baseDate = moment.utc((self.baseDate()).format("YYYY/MM"), "YYYY/MM"); 
                } 
                
                if (baseDate.isBefore(self.periodStart()) || baseDate.isAfter(self.periodEnd())) {
                    if (!self.hasShownErrorDialog) {
                        self.hasShownErrorDialog = true;
                        nts.uk.ui.dialog.alertError({ messageId: 'Msg_765' }).then(() => self.hasShownErrorDialog = false);
                    }
                    return false;
                }
                return true;
            }

            /**
             * function click apply search employee
             */
            advancedSearchEmployee(): void {
                let self = this;
                // validate all inputs & conditions
                if (!self.isValidInput()
                    || !self.isValidAdvancedSearchCondition()
                    || self.isInvalidBaseDate()
                    || self.isStatusEmployeePeriodInvalid()
                ) {
                    return;
                }
                
                // set param
                self.setAdvancedSearchParam();

                nts.uk.ui.block.grayout(); // block ui
                self.findAndReturnListEmployee(true);
            }

            /**
             * Save employeeRangeSelection
             */
            private saveEmployeeRangeSelection(): void {
                let self = this;
                if (self.showClosure) { // save EmployeeRangeSelection if show closure
                    // check data exist
                    let empRangeSelection = self.employeeRangeSelection ?
                        self.employeeRangeSelection : new EmployeeRangeSelection();

                    switch (self.systemType) {
                        case ConfigEnumSystemType.PERSONAL_INFORMATION:
                            empRangeSelection.personalInfo.selectedClosureId = self.selectedClosure();
                            break;
                        case ConfigEnumSystemType.EMPLOYMENT:
                            empRangeSelection.employmentInfo.selectedClosureId = self.selectedClosure();
                            break;
                        case ConfigEnumSystemType.SALARY:
                            empRangeSelection.salaryInfo.selectedClosureId = self.selectedClosure();
                            break;
                        case ConfigEnumSystemType.HUMAN_RESOURCES:
                            empRangeSelection.humanResourceInfo.selectedClosureId = self.selectedClosure();
                            break;
                        default: break; // systemType not found
                    }
                    service.saveEmployeeRangeSelection(empRangeSelection);
                }
            }

            /**
             * Check advanced search conditions
             */
            private isValidAdvancedSearchCondition(): boolean {
                let self = this;
                if (self.showEmployment && nts.uk.util.isNullOrEmpty(self.selectedCodeEmployment())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1195' });
                    return false;
                }
                if (self.showDepartment && nts.uk.util.isNullOrEmpty(self.selectedCodeDepartment())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1196' });
                    return false;
                }
                if (self.showWorkplace && nts.uk.util.isNullOrEmpty(self.selectedCodeWorkplace())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1197' });
                    return false;
                }
                if (self.showClassification && nts.uk.util.isNullOrEmpty(self.selectedCodeClassification())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1198' });
                    return false;
                }
                if (self.showJobTitle && nts.uk.util.isNullOrEmpty(self.selectedCodeJobtitle())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1199' });
                    return false;
                }
				/*
                if (self.showWorktype && nts.uk.util.isNullOrEmpty(self.selectedWorkTypeCode())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1200' });
                    return false;
                }*/
                return true;
            }

            /**
             * function get selected employee to
             */
            public getSelectedCodeEmployee(): string[]{
                var self = this;
                if (self.isMultiple) {
                    return self.selectedCodeEmployee();
                } else {
                    let employeeCodes = [];
                    if (!nts.uk.util.isNullOrEmpty(self.selectedCodeEmployee())) {
                        employeeCodes.push(self.selectedCodeEmployee());
                    }
                    return employeeCodes;
                }
            }        

            /**
             * Get selected code employee in tab3
             */
            public getSelectedCodeEmployeeTab3(): string[]{
                let self = this;
                if (self.isMultiple) {
                    return self.selectedEmployeesTab3();
                } else {
                    let employeeCodes = [];
                    if (!nts.uk.util.isNullOrEmpty(self.selectedEmployeesTab3())) {
                        employeeCodes.push(self.selectedEmployeesTab3());
                    }
                    return employeeCodes;
                }
            }        

            /**
             * Show data to kcp005 on tab 3
             */
            private showDataOnKcp005Tab3(data: Array<EmployeeSearchDto>): void {
                let self = this;
                // Data not found
                if (nts.uk.util.isNullOrEmpty(data)) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_317" });
                }

                // sort by code
                const sortedList = _.sortBy(data, item => item.employeeCode);

                // reserve list data
                self.reservedEmployeesTab3(sortedList);

                // clear selected codes
                self.selectedEmployeesTab3([]);

                // set data to kcp005
                self.employeeListTab3(self.toUnitModelList(sortedList));
                $('#tab3kcp005').ntsListComponent(self.tab3kcp005option);
                return;
            }
            
            /**
             * function convert dto to model init data 
             */
            public toUnitModelList(dataList: EmployeeSearchDto[]): Array<UnitModel> {
                return _.map(dataList, item => {
                    return {
                        code: item.employeeCode,
                        name: item.employeeName,
                        affiliationName: item.affiliationName
                    };
                });
            }
            
            /**
             * search Employee by Reference range
             */
            public searchEmployeeByReferenceRange(referenceRange: SearchReferenceRange): void {
                var self = this;
                self.queryParam.referenceRange = referenceRange;
                self.quickSearchEmployee();
            }

            /**
             * Search current login employee
             */
            public searchCurrentLoginEmployee(): void {
                let self = this;
                if (!self.isValidInput() || self.isInvalidBaseDate()) {
                    return;
                }

                // A：締め状態更新
                if (self.systemType == ConfigEnumSystemType.EMPLOYMENT && self.showClosure) {
                    service.getClosureByCurrentEmployee(self.queryParam.baseDate).done(id => {
                        if (_.isNil(id)) {
                            nts.uk.ui.dialog.alertError({ messageId: 'Msg_1434' });
                            return;
                        }
                        if (self.selectedClosure() != id) {
                            self.selectedClosure(id);
                        }
                        self.getEmployeeLogin();
                    });
                } else {
                    self.getEmployeeLogin();
                }
            }

            /**
             * Search employee by code
             */
            public searchByCode(): void {
                let self = this;
                if (nts.uk.util.isNullOrEmpty(self.inputCodeTab3())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1201' });
                    return;
                }
                const query = {
                    code: self.inputCodeTab3(),
                    useClosure: self.showClosure,
                    closureId: self.selectedClosure(),
                    systemType: self.systemType,
                    referenceDate: moment.utc(self.acquiredBaseDate(), CcgDateFormat.DEFAULT_FORMAT).toDate(),
					employeesDoNotManageSchedules: self.employeesDoNotManageSchedules()
                };
                nts.uk.ui.block.grayout(); // block ui
                service.searchByCode(query).done(data => {
                    self.showDataOnKcp005Tab3(data);
                    nts.uk.ui.block.clear(); // clear block UI
                });
            }

            /**
             * Search employee by name
             */
            public searchByName(): void {
                let self = this;
                if (nts.uk.util.isNullOrEmpty(self.inputNameTab3())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1201' });
                    return;
                }
                const query = {
                    name: self.inputNameTab3(),
                    useClosure: self.showClosure,
                    closureId: self.selectedClosure(),
                    systemType: self.systemType,
                    referenceDate: moment.utc(self.acquiredBaseDate(), CcgDateFormat.DEFAULT_FORMAT).toDate(),
					employeesDoNotManageSchedules: self.employeesDoNotManageSchedules()
                };
                nts.uk.ui.block.grayout(); // block ui
                service.searchByName(query).done(data => {
                    self.showDataOnKcp005Tab3(data);
                    nts.uk.ui.block.clear(); // clear block UI
                });
            }

            /**
             * Search employee by entry date
             */
            public searchByEntryDate(): void {
                let self = this;
                if ($('#ccg001-date-entry *').ntsError('hasError')) {
                    return;
                }
                if (self.isInValidEntryDate()) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1201' });
                    return;
                }
                const query = {
                    useClosure: self.showClosure,
                    closureId: self.selectedClosure(),
                    systemType: self.systemType,
                    referenceDate: moment.utc(self.acquiredBaseDate(), CcgDateFormat.DEFAULT_FORMAT).toDate(),
                    period: self.toPeriodDto(self.entryDateTab3()),
					employeesDoNotManageSchedules: self.employeesDoNotManageSchedules()
                };
                nts.uk.ui.block.grayout(); // block ui
                service.searchByEntryDate(query).done(data => {
                    self.showDataOnKcp005Tab3(data);
                    nts.uk.ui.block.clear(); // clear block UI
                });
            }

            /**
             * Search employee by retirement date
             */
            public searchByRetirementDate(): void {
                let self = this;
                if ($('#ccg001-date-retirement *').ntsError('hasError')) {
                    return;
                }
                if (self.isInValidRetirementDate()) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1201' });
                    return;
                }
                const query = {
                    useClosure: self.showClosure,
                    closureId: self.selectedClosure(),
                    systemType: self.systemType,
                    referenceDate: moment.utc(self.acquiredBaseDate(), CcgDateFormat.DEFAULT_FORMAT).toDate(),
                    period: self.toPeriodDto(self.retirementDateTab3()),
					employeesDoNotManageSchedules: self.employeesDoNotManageSchedules()	
                };
                nts.uk.ui.block.grayout(); // block ui
                service.searchByRetirementDate(query).done(data => {
                    self.showDataOnKcp005Tab3(data);
                    nts.uk.ui.block.clear(); // clear block UI
                });
            }

            /**
             * Check input entry date
             */
            private isInValidEntryDate(): boolean {
                let self = this;
                return nts.uk.util.isNullOrEmpty(self.entryDateTab3().startDate)
                    || nts.uk.util.isNullOrEmpty(self.entryDateTab3().endDate);
            }

            /**
             * Check input retirement date
             */
            private isInValidRetirementDate(): boolean {
                let self = this;
                return nts.uk.util.isNullOrEmpty(self.retirementDateTab3().startDate)
                    || nts.uk.util.isNullOrEmpty(self.retirementDateTab3().endDate);
            }

            /**
             * Convert period in dateRangePicker to period dto
             */
            private toPeriodDto(period: any): any {
                return {
                    startDate: new Date(period.startDate),
                    endDate: new Date(period.endDate)
                };
            }

            /**
             * Quick search employee
             */
            private quickSearchEmployee(): void {
                let self = this;
                if (!self.isValidInput() || self.isInvalidBaseDate()) {
                    return;
                }
                nts.uk.ui.block.grayout(); // block ui
                self.setQuickSearchParam().done(() => {
                    self.findAndReturnListEmployee(false);
                });
            }
            /**
             * Find and return list employee for caller screen.
             */
            public findAndReturnListEmployee(isAdvancedSearch: boolean): void {
                let self = this;
                service.findRegulationInfoEmployee(self.queryParam).done(data => {
                    // Data not found
                    if (nts.uk.util.isNullOrEmpty(data)) {
                        nts.uk.ui.dialog.alertError({ messageId: "Msg_317" });
                        nts.uk.ui.block.clear(); // clear block UI
                        return;
                    }

                    // Data found
                    if (isAdvancedSearch && self.showEmployeeSelection) {
                        // Set reserved list employees
                        self.reservedEmployees(data);

                        // Load list employee to KCP005
                        self.employeeinfo.employeeInputList(self.toUnitModelList(data));
                        $('#employeeinfo').ntsListComponent(self.employeeinfo);

                        // Reset selected employees on KCP005
                        self.selectedCodeEmployee([]);
                    } else {
                        self.returnDataFromCcg001(self.combineData(data));
                        // Hide component.
                        self.hideComponent();
                    }
                    nts.uk.ui.block.clear(); // clear block UI
                });
            }
            

            /**
             * Set component options (for advanced search tab)
             */
            public setComponentOptions(): void {
                var self = this;
                self.employments = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    isMultipleUse: true,
                    selectType: SelectType.SELECT_ALL,
                    listType: ListType.EMPLOYMENT,
                    selectedCode: self.selectedCodeEmployment,
                    isDialog: true,
                    isShowNoSelectRow: false,
                    maxRows: ConfigCCGKCP.MAX_ROWS_EMPLOYMENT,
                    selectedClosureId: self.showClosure ? self.selectedClosure : undefined,
                    hasPadding: false,
                    tabindex: self.ccg001Tabindex,
                    subscriptions: self.employmentSubscriptions,
                    maxWidth: 400
                };

                self.classifications = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    isMultipleUse: true,
                    listType: ListType.Classification,
                    selectType: SelectType.SELECT_ALL,
                    selectedCode: self.selectedCodeClassification,
                    isDialog: true,
                    isShowNoSelectRow: false,
                    hasPadding: false,
                    tabindex: self.ccg001Tabindex,
                    maxRows: ConfigCCGKCP.MAX_ROWS_CLASSIFICATION,
                    maxWidth: 400
                };

                self.jobtitles = {
                    isShowAlreadySet: false,
                    isMultiSelect: true,
                    isMultipleUse: true,
                    listType: ListType.JOB_TITLE,
                    selectType: SelectType.SELECT_ALL,
                    selectedCode: self.selectedCodeJobtitle,
                    isDialog: true,
                    baseDate: ko.observable(moment.utc(self.queryParam.baseDate, CcgDateFormat.DEFAULT_FORMAT).toDate()),
                    isShowNoSelectRow: false,
                    hasPadding: false,
                    tabindex: self.ccg001Tabindex,
                    maxRows: ConfigCCGKCP.MAX_ROWS_JOBTITLE,
                    maxWidth: 400
                };

                self.departments = {
                    isShowAlreadySet: false,
                    systemType: self.systemType,
                    isMultipleUse: true,
                    isMultiSelect: true,
                    startMode: StartMode.DEPARTMENT,
                    selectType: SelectType.SELECT_ALL,
                    isShowSelectButton: true,
                    selectedId: self.selectedCodeDepartment,
                    baseDate: ko.observable(moment.utc(self.queryParam.baseDate, CcgDateFormat.DEFAULT_FORMAT).toDate()),
                    maxRows: ConfigCCGKCP.MAX_ROWS_WORKPLACE,
                    isFullView: true,
                    hasPadding: false,
                    tabindex: self.ccg001Tabindex,
                    isDialog: true
                };

                self.workplaces = {
                    isShowAlreadySet: false,
                    systemType: self.systemType,
                    isMultipleUse: true,
                    isMultiSelect: true,
                    startMode: StartMode.WORKPLACE,
                    selectType: SelectType.SELECT_ALL,
                    isShowSelectButton: true,
                    selectedId: self.selectedCodeWorkplace,
                    baseDate: ko.observable(moment.utc(self.queryParam.baseDate, CcgDateFormat.DEFAULT_FORMAT).toDate()),
                    maxRows: ConfigCCGKCP.MAX_ROWS_WORKPLACE,
                    isFullView: true,
                    hasPadding: false,
                    tabindex: self.ccg001Tabindex,
                    isDialog: true,
                    width: 420
                };
            }
        }
        
        export class ConfigCCGKCP{
            static MAX_ROWS_EMPLOYMENT = 10;
            static MAX_ROWS_CLASSIFICATION = 10;
            static MAX_ROWS_JOBTITLE = 9;
            static MAX_ROWS_WORKPLACE = 10;
        }
        
        export class ConfigEnumSystemType{
            static PERSONAL_INFORMATION = 1;
            static EMPLOYMENT = 2;
            static SALARY = 3;
            static HUMAN_RESOURCES = 4;
            static ADMINISTRATOR = 5;
        }

        export class CcgDateFormat {
            static DEFAULT_FORMAT = 'YYYY-MM-DD';
            static YMD = 'YYYY/MM/DD';
            static YEAR_MONTH = 'YYYYMM';
        }
        
         export class ConfigEnumClosure{
            static CLOSURE_ALL = 0;
            static CLOSURE_ALL_NAME = nts.uk.resource.getText("CCG001_64");
        }
        
        export class ReferenceRange {
            static ALL_REFERENCE_RANGE = 0;
            static AFFILIATION_AND_ALL_SUBORDINATES = 1;
            static AFFILIATION_ONLY = 2;
        }

        export class EmployeeReferenceRange extends ReferenceRange {
            static ONLY_MYSELF = 3;
        }

        export class SearchReferenceRange extends ReferenceRange {
            static DO_NOT_CONSIDER_REFERENCE_RANGE = 3;
        }

        export class DateRangePickerModel {
            startDate: string;
            endDate: string;
            constructor(startDate: string, endDate: string) {
                let self = this;
                self.startDate = startDate;
                self.endDate = endDate;
            }

            public static isSamePeriod(a: DateRangePickerModel, b: DateRangePickerModel): boolean {
                return a.startDate === b.startDate && a.endDate === b.endDate
            }
        }

        export class CCG001TextResource {
            static CCG001_2 = nts.uk.resource.getText('CCG001_2');
            static CCG001_21 = nts.uk.resource.getText('CCG001_21');
            static CCG001_22 = nts.uk.resource.getText('CCG001_22');
            static CCG001_23 = nts.uk.resource.getText('CCG001_23');
            static CCG001_24 = nts.uk.resource.getText('CCG001_24');
            static CCG001_25 = nts.uk.resource.getText('CCG001_25');
            static CCG001_26 = nts.uk.resource.getText('CCG001_26');
            static CCG001_27 = nts.uk.resource.getText('CCG001_27');
            static CCG001_28 = nts.uk.resource.getText('CCG001_28');
            static CCG001_29 = nts.uk.resource.getText('CCG001_29');
            static CCG001_34 = nts.uk.resource.getText('CCG001_34');
            static CCG001_35 = nts.uk.resource.getText('CCG001_35');
            static CCG001_36 = nts.uk.resource.getText('CCG001_36');
            static CCG001_37 = nts.uk.resource.getText('CCG001_37');
            static CCG001_38 = nts.uk.resource.getText('CCG001_38');
            static CCG001_39 = nts.uk.resource.getText('CCG001_39');
            static CCG001_42 = nts.uk.resource.getText('CCG001_42');
            static CCG001_43 = nts.uk.resource.getText('CCG001_43');
            static CCG001_45 = nts.uk.resource.getText('CCG001_45');
            static CCG001_47 = nts.uk.resource.getText('CCG001_47');
            static CCG001_49 = nts.uk.resource.getText('CCG001_49');
            static CCG001_55 = nts.uk.resource.getText('CCG001_55');
            static CCG001_58 = nts.uk.resource.getText('CCG001_58');
            static CCG001_98 = nts.uk.resource.getText('CCG001_98');
            static CCG001_99 = nts.uk.resource.getText('CCG001_99');
            static CCG001_104 = nts.uk.resource.getText('CCG001_104');
            static CCG001_105 = nts.uk.resource.getText('CCG001_105');
            static CCG001_108 = nts.uk.resource.getText('CCG001_108');
            static CCG001_109 = nts.uk.resource.getText('CCG001_109');
            static Com_Employment = nts.uk.resource.getText('Com_Employment');
            static Com_Department = nts.uk.resource.getText('Com_Department');
            static Com_Workplace = nts.uk.resource.getText('Com_Workplace');
            static Com_Class = nts.uk.resource.getText('Com_Class');
            static Com_Jobtitle = nts.uk.resource.getText('Com_Jobtitle');
        }

var CCG001_HTML = `<div id="component-ccg001" class="cf height-maximum" style="visibility: hidden;">
        <div class="pull-left ccg001-content">
            <div id="ccg001-header" class="ccg001-table">
                <div class="ccg001-cell">
                    <!-- ko if: showBaseDate -->
                        <div class="control-group ccg001-control-group">
                            <div class="ccg001-label" data-bind="ntsFormLabel: {required: true}">`+CCG001TextResource.CCG001_27+`</div>
                            <div id="inp_baseDate"
                                data-bind="attr: {tabindex: ccg001Tabindex}, ntsDatePicker: {
                                name: '#[CCG001_27]',
                                dateFormat: 'YYYY/MM/DD',
                                value: inputBaseDate,
                                required: true }"></div>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showClosure -->
                        <div class="control-group ccg001-control-group">
                            <div class="ccg001-cell">
                                <div class="ccg001-label" data-bind="ntsFormLabel: {required: true}">`+CCG001TextResource.CCG001_28+`</div>
                            </div>
                            <div class="ccg001-cell mid">
                                <div id="cbb-closure" style="margin-left: 4px;"
                                    data-bind="attr: {tabindex: ccg001Tabindex}, ntsComboBox: {
                                        name: '#[CCG001_28]',
                                        options: closureList,
                                        optionsValue: 'closureId',
                                        value: selectedClosure,
                                        optionsText: 'closureName',
                                        editable: false,
                                        enable: true,
                                        columns: [
                                            { prop: 'closureId', length: 1 },
                                            { prop: 'closureName', length: 5 },
                                        ]}"></div>
                            </div>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showPeriod -->
                        <div class="control-group ccg001-control-group">
                            <div class="ccg001-cell">
                                <div class="ccg001-label" data-bind="ntsFormLabel: {required: true}">`+CCG001TextResource.CCG001_29+`</div>
                            </div>
                            <div class="ccg001-cell mid">
                                <div id="ccg001-search-period" data-bind="attr: {tabindex: ccg001Tabindex}, ntsDateRangePicker: {
                                    type: showPeriodYM ? 'yearmonth' : 'fullDate',
                                    maxRange: maxPeriodRange,
                                    required: true,
                                    enable: true,
                                    showNextPrevious: true,
                                    value: inputPeriod}"/>
                            </div>
                        </div>
                    <!-- /ko -->
                </div>
                <div class="ccg001-cell bot">
                    <button id="ccg001-btn-apply-search-condition"
                        class="proceed caret-bottom" data-bind="attr: {tabindex: ccg001Tabindex},
                            enable: isValidInput, click: applyDataSearch">`+CCG001TextResource.CCG001_2+`</button>
                </div>
            </div>
        <div id="tab-panel" class="cf ccg-tabpanel pull-left"
            data-bind="attr: {tabindex: ccg001Tabindex}, ntsTabPanel: { dataSource: tabs, active: selectedTab}">
                <div tabindex="-1" class="tab-content-1" data-bind="visible: showQuickSearchTab">
                    <!-- ko if: showAllReferableEmployee -->
                        <div id="ccg001-btn-search-all" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_big ccg-btn-quick-search ccg001-btn"
                                data-bind="click: function(){searchEmployeeByReferenceRange(`+SearchReferenceRange.ALL_REFERENCE_RANGE+`)}">
                                <i class="icon ccg001-icon-btn-big icon-28-allemployee"></i>
                                <label class="labelBigButton">`+CCG001TextResource.CCG001_34+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-big caret-right"></span>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showOnlyMe -->
                        <div id="ccg001-btn-only-me" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_big ccg-btn-quick-search ccg001-btn"
                                data-bind="click: searchCurrentLoginEmployee">
                                <i class="icon ccg001-icon-btn-big icon-26-onlyemployee"></i>
                                <label class="labelBigButton">`+CCG001TextResource.CCG001_35+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-big caret-right"></span>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showSameDepartment -->
                        <div id="ccg001-btn-same-workplace" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_small ccg-btn-quick-search ccg001-btn"
                                data-bind="click: function(){searchEmployeeByReferenceRange(`+SearchReferenceRange.AFFILIATION_ONLY+`)}">
                                <i class="icon ccg001-icon-btn-small icon-48-ofworkplace"></i>
                                <label class="labelSmallButton">`+CCG001TextResource.CCG001_36+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-small caret-right"></span>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showSameDepartmentAndChild -->
                        <div id="ccg001-btn-same-workplace-and-child" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_small ccg-btn-quick-search ccg001-btn"
                                data-bind="click: function(){searchEmployeeByReferenceRange(`+SearchReferenceRange.AFFILIATION_AND_ALL_SUBORDINATES+`)}">
                                <i class="icon ccg001-icon-btn-small icon-49-workplacechild"></i>
                                <label class="labelSmallButton">`+CCG001TextResource.CCG001_37+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-small caret-right"></span>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showSameWorkplace -->
                        <div id="ccg001-btn-same-workplace" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_small ccg-btn-quick-search ccg001-btn"
                                data-bind="click: function(){searchEmployeeByReferenceRange(`+SearchReferenceRange.AFFILIATION_ONLY+`)}">
                                <i class="icon ccg001-icon-btn-small icon-48-ofworkplace"></i>
                                <label class="labelSmallButton">`+CCG001TextResource.CCG001_38+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-small caret-right"></span>
                        </div>
                    <!-- /ko -->
                    <!-- ko if: showSameWorkplaceAndChild -->
                        <div id="ccg001-btn-same-workplace-and-child" class="btn-quick-search has-state" data-bind="attr: {tabindex: ccg001Tabindex}">
                            <div class="flex valign-center btn_small ccg-btn-quick-search ccg001-btn"
                                data-bind="click: function(){searchEmployeeByReferenceRange(`+SearchReferenceRange.AFFILIATION_AND_ALL_SUBORDINATES+`)}">
                                <i class="icon ccg001-icon-btn-small icon-49-workplacechild"></i>
                                <label class="labelSmallButton">`+CCG001TextResource.CCG001_39+`</label> 
                            </div>
                            <span class="ccg001-caret ccg001-caret-quick-small caret-right"></span>
                        </div>
                    <!-- /ko -->
                </div>

                <div tabindex="-1" class="tab-content-2 height-maximum" data-bind="visible: showAdvancedSearchTab">
                        <div id="ccg001-tab-content-2" class="height-maximum">
                            <div class="pull-left height-maximum" style="padding-right: 20px; overflow-y: scroll;">
                                <div>
                                    <label>`+CCG001TextResource.CCG001_24+`</label>
                                </div>
                                <div class="accordion" id="StatusOfEmployeeList"
                                    data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                    <h3>
                                        <label>`+CCG001TextResource.CCG001_42+`</label>
                                    </h3>
                                    <div class="contentkcp">
                                        <div style="padding-bottom: 20px;">
                                            <label>`+CCG001TextResource.CCG001_43+`</label>
                                            <div class="pull-right" id="switch-buttons" data-bind="attr: {tabindex: ccg001Tabindex}, ntsSwitchButton: {
                                                name: '#[CCG001_44]',
                                                options: incumbentDatasource,
                                                optionsValue: 'code',
                                                optionsText: 'name',
                                                value: selectedIncumbent,
                                                enable: true }">
                                            </div>
                                        </div>
                                        <div style="padding-bottom: 20px;">
                                            <label>`+CCG001TextResource.CCG001_47+`</label>
                                            <div class="pull-right" id="switch-buttons" data-bind="attr: {tabindex: ccg001Tabindex}, ntsSwitchButton: {
                                                name: '#[CCG001_48]',
                                                options: closedDatasource,
                                                optionsValue: 'code',
                                                optionsText: 'name',
                                                value: selectedClosed,
                                                enable: true }">
                                            </div>
                                        </div>
                                        <div style="padding-bottom: 20px;">
                                            <label>`+CCG001TextResource.CCG001_45+`</label>
                                            <div class="pull-right" id="switch-buttons" data-bind="attr: {tabindex: ccg001Tabindex}, ntsSwitchButton: {
                                                name: '#[CCG001_46]',
                                                options: leaveOfAbsenceDatasource,
                                                optionsValue: 'code',
                                                optionsText: 'name',
                                                value: selectedLeave,
                                                enable: true }">
                                            </div>
                                        </div>
                                        <div style="padding-bottom: 20px;">
                                            <label>`+CCG001TextResource.CCG001_49+`</label>
                                            <div class="pull-right" id="switch-buttons" data-bind="attr: {tabindex: ccg001Tabindex}, ntsSwitchButton: {
                                                name: '#[CCG001_50]',
                                                options: retirementDatasource,
                                                optionsValue: 'code',
                                                optionsText: 'name',
                                                value: selectedRetirement,
                                                enable: true }">
                                            </div>
                                        </div>
                                        <div class="pull-right" style="padding-top: 10px;">
                                            <div id="ccg001-retire-period"
                                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsDateRangePicker: {
                                                            name: '#[CCG001_92]',
                                                            required: true,
                                                            enable: true,
                                                            showNextPrevious: false,
                                                            value: retirePeriod }"/>
                                        </div>
                                    </div>
                                </div>
                                <!-- ko if: showEmployment -->
                                    <div class="accordion" id="EmploymentList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                        <h3>
                                            <label>`+CCG001TextResource.Com_Employment+`</label>
                                        </h3>
                                        <div class="contentkcp">
                                            <div id="employmentList" style="width: 352px; height: 335px;"></div>
                                        </div>
                                    </div>
                                <!-- /ko -->
                                <!-- ko if: showDepartment -->
                                    <div class="accordion" id="DepartmentList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                        <h3>
                                            <label>`+CCG001TextResource.Com_Department+`</label>
                                        </h3>
                                        <div class="contentkcpWorkplace">
                                            <div id="departmentList"></div><br/>
                                            <button id="btnDetailDepartment" class="small"
                                                data-bind="attr: {tabindex: ccg001Tabindex}, click: function(){detailDepartmentWorkplace(`+StartMode.DEPARTMENT+`)}">`+CCG001TextResource.CCG001_55+`</button>
                                        </div>
                                    </div>
                                <!-- /ko -->
                                <!-- ko if: showWorkplace -->
                                    <div class="accordion" id="WorkplaceList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                        <h3>
                                            <label>`+CCG001TextResource.Com_Workplace+`</label>
                                        </h3>
                                        <div class="contentkcpWorkplace">
                                            <div id="workplaceList"></div><br/>
                                            <button id="btnDetailWorkplace"  class="small"
                                                data-bind="attr: {tabindex: ccg001Tabindex}, click: function(){detailDepartmentWorkplace(`+StartMode.WORKPLACE+`)}">`+CCG001TextResource.CCG001_55+`</button>
                                        </div>
                                    </div>
                                <!-- /ko -->
                                <!-- ko if: showClassification -->
                                    <div class="accordion" id="ClassificationList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                        <h3>
                                            <label>`+CCG001TextResource.Com_Class+`</label>
                                        </h3>
                                        <div class="contentkcp">
                                            <div id="classificationList" style="height: 335px;"></div>
                                        </div>
                                    </div>
                                <!-- /ko -->
                                <!-- ko if: showJobTitle -->
                                    <div class="accordion" id="JoptitleList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}">
                                        <h3>
                                            <label>`+CCG001TextResource.Com_Jobtitle+`</label>
                                        </h3>
                                        <div class="contentkcp">
                                            <div id="jobtitleList" style="height: 310px;"></div>
                                        </div>
                                    </div>
                                <!-- /ko -->
                                <!-- ko if: showWorktype -->
                                    <div class="accordion" id="WorkTypeList"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, ntsAccordion: {enable: true}" hidden ="true">
                                        <h3>
                                            <label>`+CCG001TextResource.CCG001_58+`</label>
                                        </h3>
                                        <div class="contentkcp">
                                            <div style="width: 340px" data-bind="attr: {tabindex: ccg001Tabindex}, ntsSearchBox: {
                                                searchMode: 'filter',
                                                targetKey: 'businessTypeCode',
                                                comId: 'list-worktype', 
                                                items: listWorkType,
                                                selectedKey: 'businessTypeCode',
                                                fields: ['businessTypeCode', 'businessTypeName'],
                                                mode: 'listbox'}" />
                                            <table id="list-worktype"
                                            data-bind="attr: {tabindex: ccg001Tabindex}, ntsGridList: {
                                                name: '#[CCG001_59]',
                                                height: 235,
                                                options: listWorkType,
                                                optionsValue: 'businessTypeCode',
                                                columns: workTypeColumns,
                                                multiple: true,
                                                value: selectedWorkTypeCode
                                            }"></table>
                                        </div>
                                    </div>
                                <!-- /ko -->
                            </div>
                            <div class="pull-left height-maximum margin-left-10 ccg001-table has-state">
                                <div class="ccg001-cell mid">
                                    <div id="ccg001-btn-advanced-search" class="ccg001-btn ccg-btn-vertical height-maximum"
                                        data-bind="attr: {tabindex: ccg001Tabindex}, click: advancedSearchEmployee">
                                        <div class="ccg001-cell mid">
                                            <div class="ccg-lbl-vertical ccg-lbl-extract-emp">`+CCG001TextResource.CCG001_25+`</div>
                                        </div>
                                    </div>
                                </div>
                                <div class="ccg001-cell mid">
                                    <span class="ccg001-caret ccg001-caret-vertical caret-right"></span>
                                </div>
                            </div>
                            
                            <!-- ko if: showEmployeeSelection -->
                                <div class="pull-left height-maximum margin-left-10">
                                    <div id="employeeinfo"></div>
                                </div>
                                <div class="pull-left height-maximum margin-left-10 ccg001-table has-state">
                                    <div class="ccg001-cell mid">
                                        <div id="ccg001-btn-KCP005-apply" class="ccg001-btn ccg-btn-vertical height-maximum"
                                            data-bind="attr: {tabindex: ccg001Tabindex}, click: extractSelectedEmployees">
                                            <div class="ccg001-cell mid">
                                                <i class="icon icon-47-white-check-mark icon-ml"></i>
                                                <div class="ccg-lbl-vertical ccg-lbl-extract-emp">`+CCG001TextResource.CCG001_26+`</div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="ccg001-cell mid">
                                        <span class="ccg001-caret ccg001-caret-vertical caret-right"></span>
                                    </div>
                                </div>
                            <!-- /ko -->
                            <div class="cf"></div>
                        </div>
                        <div class="cf"></div>
                    </div>
            <div id="ccg001-tab-content-3" class="height-maximum">
                <div id="ccg001-part-g" class="pull-left height-maximum">
                    <div class="control-group ccg001-control-group">
                        <div data-bind="ntsFormLabel: {}">`+CCG001TextResource.CCG001_104+`</div>
                        <input class="ccg001-inp" id="ccg001-input-code"
                                    data-bind="attr: {tabindex: ccg001Tabindex}, ntsTextEditor: {
                                    name: '#[CCG001_106]',
                                    value: inputCodeTab3,
                                    valueUpdate: 'keypress',
                                    required: false
                                    }" />
                        <button class="proceed caret-bottom pull-right" id="ccg001-tab3-search-by-code"
                            data-bind="attr: {tabindex: ccg001Tabindex}, click: searchByCode, enable: isValidInput">`+CCG001TextResource.CCG001_108+`</button>
                    </div>
                    <div class="control-group ccg001-control-group">
                        <div data-bind="ntsFormLabel: {}">`+CCG001TextResource.CCG001_105+`</div>
                        <input class="ccg001-inp" id="ccg001-inp-name"
                                    data-bind="attr: {tabindex: ccg001Tabindex}, ntsTextEditor: {
                                    name: '#[CCG001_107]',
                                    value: inputNameTab3,
                                    valueUpdate: 'keypress',
                                    required: false
                                    }" />
                        <button class="proceed caret-bottom pull-right" id="ccg001-tab3-search-by-name"
                            data-bind="attr: {tabindex: ccg001Tabindex}, click: searchByName, enable: isValidInput">`+CCG001TextResource.CCG001_108+`</button>
                    </div>
                    <div class="cf control-group ccg001-control-group">
                        <div class="pull-left" data-bind="ntsFormLabel: {}">`+CCG001TextResource.CCG001_98+`</div>
                        <div id="ccg001-date-entry" class="ccg001-date-range pull-left"
                            data-bind="attr: {tabindex: ccg001Tabindex}, ntsDateRangePicker: {
                                name: '#[CCG001_100]',
                                required: false,
                                enable: true,
                                showNextPrevious: false,
                                value: entryDateTab3
                                }"/>
                        <button class="proceed caret-bottom pull-right" id="search-by-entry-date"
                            data-bind="attr: {tabindex: ccg001Tabindex}, click: searchByEntryDate, enable: isValidEntryDateSearch">`+CCG001TextResource.CCG001_108+`</button>
                    </div>
                    <div class="cf control-group ccg001-control-group">
                        <div class="pull-left" data-bind="ntsFormLabel: {}">`+CCG001TextResource.CCG001_99+`</div>
                        <div id="ccg001-date-retirement" class="ccg001-date-range pull-left"
                            data-bind="attr: {tabindex: ccg001Tabindex}, ntsDateRangePicker: {
                                name: '#[CCG001_100]',
                                required: false,
                                enable: true,
                                showNextPrevious: false,
                                value: retirementDateTab3 }"/>
                        <button class="proceed caret-bottom pull-right" id="search-by-retirement-date"
                            data-bind="attr: {tabindex: ccg001Tabindex}, click: searchByRetirementDate, enable: isValidRetirementDateSearch">`+CCG001TextResource.CCG001_108+`</button>
                    </div>
                    <div id="tab3kcp005"></div>
                </div>
                <div class="pull-right height-maximum ccg001-table has-state">
                    <div class="ccg001-cell mid">
                        <div id="ccg001-btn-KCP005-apply" class="ccg001-btn ccg-btn-vertical height-maximum"
                            data-bind="attr: {tabindex: ccg001Tabindex}, click: extractSelectedEmployeesInTab3">
                            <div class="ccg001-cell mid">
                                <i class="icon icon-47-white-check-mark icon-ml"></i>
                                <div class="ccg-lbl-vertical ccg-lbl-extract-emp">`+CCG001TextResource.CCG001_26+`</div>
                            </div>
                        </div>
                    </div>
                    <div class="ccg001-cell mid">
                        <span class="ccg001-caret ccg001-caret-vertical caret-right"></span>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </div>
    <div id="hor-scroll-button-hide">
        <span class="position-mid ccg001-caret ccg001-caret-vertical caret-right"></span>
        <div id="ccg001-btn-search-drawer" class="position-mid ccg001-btn ccg-btn-vertical" data-bind="click: showComponent">
            <div class="ccg001-cell mid">
            <i class="icon icon-01-searchmode icon-ml"></i>
            <div class="ccg-lbl-vertical ccg-lbl-search-drawer">`+CCG001TextResource.CCG001_21+`</div>
            </div>
        </div>
    </div>`;
    }
}
/**
 * Defined Jquery interface.
 */
interface JQuery {

   ntsGroupComponent(option: nts.uk.com.view.ccg.share.ccg.service.model.GroupOption): JQueryPromise<void>;
}

(function($: any) {
    $.fn.ntsGroupComponent = function(option: nts.uk.com.view.ccg.share.ccg.service.model.GroupOption): JQueryPromise<void> {
        let dfd = $.Deferred<void>();
        new nts.uk.com.view.ccg.share.ccg.viewmodel.ListGroupScreenModel().init(this, option).done(() => {
            nts.uk.ui.block.clear();
            dfd.resolve();
        });
        return dfd.promise();
    }
} (jQuery));