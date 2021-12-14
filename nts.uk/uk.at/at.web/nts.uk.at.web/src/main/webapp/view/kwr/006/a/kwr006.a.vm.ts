module nts.uk.at.view.kwr006.a {

    import message = nts.uk.resource.getMessage;
    import service = nts.uk.at.view.kwr006.a.service;
    import blockUI = nts.uk.ui.block;
    import MonthlyWorkScheduleConditionDto = service.model.MonthlyWorkScheduleConditionDto;
    import WorkScheduleSettingTotalOutputDto = service.model.WorkScheduleSettingTotalOutputDto;
    import TotalWorkplaceHierachyDto = service.model.TotalWorkplaceHierachyDto;
    import MonthlyWorkScheduleQuery = service.model.MonthlyWorkScheduleQuery;

    export module viewmodel {

        export class ScreenModel {
            monthlyWorkScheduleConditionModel: MonthlyWorkScheduleConditionModel;
            // datepicker A1_6 A1_7 A1_8
            enableDatePicker: KnockoutObservable<boolean>;
            requiredDatePicker: KnockoutObservable<boolean>;
            datepickerValue: KnockoutObservable<any>;
            startDatepicker: KnockoutObservable<string>;
            endDatepicker: KnockoutObservable<string>;

            dataOutputType: KnockoutObservableArray<any>;

            // dropdownlist A7_3
            itemCodeStandardSelection: KnockoutObservableArray<any>;
            enableA7_3: KnockoutObservable<boolean> = ko.observable(true);

            // dropdownlist A7_11
            itemCodeFreeSetting: KnockoutObservableArray<any>;
            enableA7_11: KnockoutObservable<boolean> = ko.observable(false);

            // dropdownlist A9_2
            itemListTypePageBrake: KnockoutObservableArray<any>;
            // switch button A12_2
            dataDisplayClassification: KnockoutObservableArray<any>;
            // switch button A13_2
            dataDisplaySwitching: KnockoutObservableArray<any>;
            
            // Selected employee
            selectedEmployee: KnockoutObservableArray<string>;
            
            taskId: KnockoutObservable<string>;
            errorLogs : KnockoutObservableArray<EmployeeError>;
            errorLogsNoWorkplace : KnockoutObservableArray<EmployeeError>;

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

            enableBtnConfigure: KnockoutObservable<boolean>;
            // start variable of CCG001
            ccg001ComponentOption: GroupOption;
            // end variable of CCG001
            
            closureId: KnockoutObservable<number>;
            freeSettingEnabled: KnockoutObservable<boolean> = ko.observable(false);

            constructor() {
                let self = this;
                self.monthlyWorkScheduleConditionModel = new MonthlyWorkScheduleConditionModel();
                self.defineDatasource();

                // datepicker A1_6 A1_7 A1_8
                self.enableDatePicker = ko.observable(true);
                self.requiredDatePicker = ko.observable(true);
                self.enableBtnConfigure = ko.observable(true);
                self.datepickerValue = ko.observable({});
                self.startDatepicker = ko.observable("");
                self.endDatepicker = ko.observable("");

                // dropdownlist A7_3
                self.itemCodeStandardSelection = ko.observableArray([]);
                
                // dropdownlist A7_11
                self.itemCodeFreeSetting = ko.observableArray([]);
                
                self.selectedEmployee = ko.observableArray([]);

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

                self.defineComponentOption();
                self.initSubscribers();
                
                self.taskId = ko.observable('');
                self.errorLogs = ko.observableArray([]);
                self.errorLogsNoWorkplace = ko.observableArray([]);
                self.closureId = ko.observable(0);
            }

            private initSubscribers(): void {
                let self = this;
                self.startDatepicker.subscribe(function(value) {
                    self.datepickerValue().startDate = value;
                    self.datepickerValue.valueHasMutated();
                });

                self.endDatepicker.subscribe(function(value) {
                    self.datepickerValue().endDate = value;
                    self.datepickerValue.valueHasMutated();
                });

                self.monthlyWorkScheduleConditionModel.itemSettingType.subscribe((value) => {
                    self.enableA7_3(value == 0);
                    self.enableA7_11(value == 1);
                    nts.uk.ui.errors.clearAll();
                });
            }

            private defineDatasource(): void {
                let self = this;
                // dropdownlist A9_2
                self.itemListTypePageBrake = ko.observableArray([
                    { code: 0, name: nts.uk.resource.getText("KWR006_99")},
                    { code: 1, name: nts.uk.resource.getText("KWR006_100")},
                    { code: 2, name: nts.uk.resource.getText("KWR006_101")}
                ]);
                self.dataOutputType = ko.observableArray([
                    { code: 0, name: nts.uk.resource.getText("KWR006_10") },
                    { code: 1, name: nts.uk.resource.getText("KWR006_11") }
                ]);
                // A12_1
                self.dataDisplayClassification = ko.observableArray([
                    { code: 1, name: nts.uk.resource.getText("KWR006_88")},// 表示
                    { code: 0, name: nts.uk.resource.getText("KWR006_89")} // 非表示
                ]);
                // A13_1
                self.dataDisplaySwitching = ko.observableArray([
                    { code: 0, name: nts.uk.resource.getText("KWR006_97")},
                    { code: 1, name: nts.uk.resource.getText("KWR006_98")}
                ]);
            }

            private defineComponentOption(): void {
                let self = this;
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
                    periodFormatYM: true,

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

                    /**
                    * Self-defined function: Return data from CCG001
                    * @param: data: the data return from CCG001
                    */
                    returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                        
                        //画面項目「A1_7」「A1_8」を更新する
                        self.datepickerValue().startDate = moment(data.periodStart).format("YYYY/MM");
                        self.datepickerValue().endDate = moment(data.periodEnd).format("YYYY/MM");
                        self.datepickerValue.valueHasMutated();
                        self.employeeList.removeAll();
                        const result = data.listEmployee
//                            .filter(f => !_.isEmpty(f.workplaceId))
                            .map(item => {
                                return {
                                    id: item.employeeId,
                                    code: item.employeeCode,
                                    name: item.employeeName,
                                    affiliationName: item.affiliationName
                                };
                            });
                        self.employeeList(result);
                        self.closureId(data.closureId);
                    }
                }
            }

            public startPage(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                let self = this;
                
                var getCurrentLoginerRole = service.getCurrentLoginerRole().done((role: any) => {
                    self.enableBtnConfigure(role.employeeCharge);
                });

                const getFreeSettingAuthority = service.getFreeSettingAuthority().done((role) => {
                    self.freeSettingEnabled(role.freeSettingAuthority);
                });
                $.when(self.loadOutputFreeSetting()
                     , self.loadOutputStandardSetting()
                     , self.loadPeriod()
                     , getCurrentLoginerRole
                     , getFreeSettingAuthority).done(() => {
                        self.loadWorkScheduleOutputCondition(self.freeSettingEnabled()).done(() => dfd.resolve());
                });
                return dfd.promise();
            }

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

            public isInvalidSetting(): boolean {
                let self = this;
                return !self.hasSelectedEmployees() || self.monthlyWorkScheduleConditionModel.totalOutputSetting.isInvalidTotalOutputSetting() ||
                    self.monthlyWorkScheduleConditionModel.totalOutputSetting.workplaceHierarchyTotal.isInvalidSetting();
            }

            public exportExcel(): void {
                let self = this;
                if (self.isInvalidSetting()) {
                    return;
                }

                self.saveWorkScheduleOutputCondition().done(function() {
                    const query = self.getExportQuery();
                    query.fileType = 0;
                    query.baseDate = self.ccg001ComponentOption.baseDate;
    
                    nts.uk.ui.block.grayout();
                    service.exportSchedule(query).done(function(response) {
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
                        if (self.errorLogsNoWorkplace().length > 0)
                            nts.uk.ui.dialog.alertError({
                                messageId: "Msg_1396",
                                message: message("Msg_1396") + employeeStr,
                                messageParams: [self.errorLogs().length]
                            });
                    }).fail(function (error) {
                        nts.uk.ui.dialog.alertError({ messageId: error.message, messageParams: null});
                    }).always(function (error) {
                        nts.uk.ui.block.clear(); 
                    });
                });

                
            }

            public exportPdf(): void {
                let self = this;
                if (self.isInvalidSetting()) {
                    return;
                }

                self.saveWorkScheduleOutputCondition().done(function() {
                    const query = self.getExportQuery();
                    query.fileType = 1;
                    query.baseDate = self.ccg001ComponentOption.baseDate;
    
                    nts.uk.ui.block.grayout();
                    service.exportSchedule(query).done(function(response) {
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
                            } else if (item.key.substring(0, 6) == "NOWPK_") {
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
                        if (self.errorLogsNoWorkplace().length > 0)
                            nts.uk.ui.dialog.alertError({
                                messageId: "Msg_1396",
                                message: message("Msg_1396") + employeeStr,
                                messageParams: [self.errorLogs().length]
                            });
                    }).fail(function (error) {
                        nts.uk.ui.dialog.alertError({ messageId: error.message, messageParams: null});
                    }).always(function (error) {
                        nts.uk.ui.block.clear(); 
                    });
                });
            }

            public openScreenC(): void {
                let self = this;
                let codeShared = self.monthlyWorkScheduleConditionModel.itemSettingType() === ItemSelectionEnum.STANDARD_SELECTION
                                ? self.monthlyWorkScheduleConditionModel.selectedCode()
                                : self.monthlyWorkScheduleConditionModel.selectedCodeFreeSetting();
                nts.uk.ui.windows.setShared('selectedCode', codeShared);
                nts.uk.ui.windows.setShared('itemSelection', self.monthlyWorkScheduleConditionModel.itemSettingType());
                nts.uk.ui.windows.sub.modal('/view/kwr/006/c/index.xhtml', { height: 750 }).onClosed(() => {
                    $.when(self.loadOutputFreeSetting() , self.loadOutputStandardSetting()).then(() => {
                        let data = nts.uk.ui.windows.getShared('selectedCodeScreenC');
                        if (self.monthlyWorkScheduleConditionModel.itemSettingType() === ItemSelectionEnum.STANDARD_SELECTION) {
                            self.monthlyWorkScheduleConditionModel.selectedCode(data);
                        } else {
                            self.monthlyWorkScheduleConditionModel.selectedCodeFreeSetting(data);
                        }
                    });
                });
            }

            /**
             * Get export query.
             * Need to set file type.
             */
            private getExportQuery(): MonthlyWorkScheduleQuery {
                let self = this;
                let period = self.datepickerValue();
                let startYM = parseInt(period.startDate.replace('/', ''));
                let endYM = parseInt(period.endDate.replace('/', ''));
                
                return {
                    startYearMonth: startYM,
                    endYearMonth: endYM,
                    workplaceIds: [],
                    code: self.monthlyWorkScheduleConditionModel.itemSettingType() === ItemSelectionEnum.STANDARD_SELECTION
                            ? self.monthlyWorkScheduleConditionModel.selectedCode()
                            : self.monthlyWorkScheduleConditionModel.selectedCodeFreeSetting(),
                    employeeId: self.getListSelectedEmployee(),
                    condition: self.monthlyWorkScheduleConditionModel.toDto(),
                    fileType: null,
                    closureId: self.closureId()
                };
            }
            
            private getListSelectedEmployee() {
                let self = this;
                self.selectedEmployee.removeAll();
                _.forEach(self.multiSelectedCode(), code => {
                    var employee = self.employeeList().filter(function(emp) {
                        return code == emp.code;
                    });
                    if(employee.length > 0){
                        self.selectedEmployee().push(employee[0].id);
                    }
                });
                return self.selectedEmployee();
            }

            /**
             * Load domain characteristic: WorkScheduleOutputCondition
             */
            private loadWorkScheduleOutputCondition(authorityFreeSetting: boolean): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.restoreCharacteristic().done(data => {
                    if (!_.isNil(data)) {
                        self.monthlyWorkScheduleConditionModel.updateData(data, authorityFreeSetting);
                    }
                    dfd.resolve();
                });
                return dfd.promise();
            }

            private loadOutputStandardSetting() : JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.findAllOutputItemMonthlyWorkSchedule(ItemSelectionEnum.STANDARD_SELECTION).done(data => {
                    let datas = _.sortBy(data, item => item.itemCode);
                    self.itemCodeStandardSelection(_.map(datas, item => new ItemModel(item.itemCode, item.itemName)));
                    dfd.resolve();
                });
                return dfd.promise();
            }

            private loadOutputFreeSetting() : JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.findAllOutputItemMonthlyWorkSchedule(ItemSelectionEnum.FREE_SETTING).done(data => {
                    let datas = _.sortBy(data, item => item.itemCode);
                    self.itemCodeFreeSetting(_.map(datas, item => new ItemModel(item.itemCode, item.itemName)));
                    dfd.resolve();
                });
                return dfd.promise();
            }

            private loadPeriod(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.getPeriod().done(period => {
                    const startYearMonth = period.startYearMonth;
                    const endYearMonth = period.endYearMonth;
                    const parsedStart = startYearMonth.slice(0, 4) + '/' + startYearMonth.slice(4);
                    const parsedEnd = endYearMonth.slice(0, 4) + '/' + endYearMonth.slice(4);
                    self.datepickerValue({
                        startDate: parsedStart,
                        endDate: parsedEnd
                    });
                    dfd.resolve();
                });
                return dfd.promise();
            }

            /**
             * Save domain characteristic: WorkScheduleOutputCondition
             */
            private saveWorkScheduleOutputCondition(): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                service.saveCharacteristic(self.monthlyWorkScheduleConditionModel.toDto()).done(() => dfd.resolve());
                return dfd.promise();
            }

            /**
             * 印刷前チェック処理 (Xử lý check trước khi in)
             */
            private hasSelectedEmployees(): boolean {
                let self = this;
                // 出力対象の社員をチェック (Check employee đối tượng ouput)
                // 選択されている社員=0
                if (_.isEmpty(self.multiSelectedCode())) {
                    // エラーメッセージ（ID：Msg_884）を表示する  (Hien thi error msg )
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_884' });
                    return false;
                }
                return true;
            }
        }

        // dropdownlist A9_2
        class ItemModel {
            code: string;
            name: string;

            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }

        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
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
            baseDate?: string; // 基準日
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

        export interface Ccg001ReturnedData {
            baseDate: string; // 基準日
            closureId?: number; // 締めID
            periodStart: string; // 対象期間（開始)
            periodEnd: string; // 対象期間（終了）
            listEmployee: Array<EmployeeSearchDto>; // 検索結果
        }
        // end CCG001

        export interface EmployeeSearchDto {
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            workplaceId: string;
            workplaceName: string;
        }

        export class MonthlyWorkScheduleConditionModel {
            companyId: string;
            userId: string;
            selectedCode: KnockoutObservable<string>;
            outputType: KnockoutObservable<number>;
            pageBreakIndicator: KnockoutObservable<number>;
            totalOutputSetting: WorkScheduleSettingTotalOutputModel;
            isIndividualTypeSelected: KnockoutComputed<boolean>;
            itemSettingType: KnockoutObservable<number>;
            displayType: KnockoutObservable<number>;
            itemDisplaySwitch: KnockoutObservable<number>;
            selectedCodeFreeSetting: KnockoutObservable<string>;

            constructor() {
                let self = this;
                self.companyId = __viewContext.user.companyId;
                self.userId = __viewContext.user.employeeId;
                self.selectedCode = ko.observable('');
                self.selectedCodeFreeSetting = ko.observable('');
                self.outputType = ko.observable(0);
                self.pageBreakIndicator = ko.observable(0);
                self.isIndividualTypeSelected = ko.computed(() => self.outputType() == 0);
                self.totalOutputSetting = new WorkScheduleSettingTotalOutputModel();
                self.totalOutputSetting.isIndividualTypeSelected = self.isIndividualTypeSelected;
                self.itemSettingType = ko.observable(ItemSelectionEnum.STANDARD_SELECTION);
                self.displayType = ko.observable(0);
                self.itemDisplaySwitch = ko.observable(0);
            }
            public updateData(data: MonthlyWorkScheduleConditionDto, authorityFreeSetting: boolean): void {
                let self = this;
                self.companyId = data.companyId;
                self.userId = data.userId;
                self.itemSettingType(data.itemSettingType === ItemSelectionEnum.FREE_SETTING && authorityFreeSetting 
                                    ? data.itemSettingType
                                    : ItemSelectionEnum.STANDARD_SELECTION);
                if (data.itemSettingType === ItemSelectionEnum.STANDARD_SELECTION) {
                    self.selectedCode(data.selectedCode);
                } else {
                    self.selectedCodeFreeSetting(data.selectedCodeFreeSetting);
                }
                self.outputType(data.outputType);
                self.pageBreakIndicator(data.pageBreakIndicator);
                self.displayType(data.displayType ? data.displayType: 0);
                self.itemDisplaySwitch(data.itemDisplaySwitch ? data.itemDisplaySwitch: 0);
                self.totalOutputSetting.updateData(data.totalOutputSetting);
            }

            public toDto(): MonthlyWorkScheduleConditionDto {
                let self = this;
                let dto = <MonthlyWorkScheduleConditionDto>{};
                dto.companyId = self.companyId;
                dto.userId = self.userId;
                dto.selectedCode = self.selectedCode();
                dto.selectedCodeFreeSetting = self.selectedCodeFreeSetting();
                dto.outputType = self.outputType();
                dto.pageBreakIndicator = self.pageBreakIndicator(); 
                dto.itemSettingType = self.itemSettingType(); 
                dto.displayType = self.displayType(); 
                dto.itemDisplaySwitch = self.itemDisplaySwitch(); 
                dto.totalOutputSetting = self.totalOutputSetting.toDto();
                return dto;
            }
        }

        export class WorkScheduleSettingTotalOutputModel {
            details: KnockoutObservable<boolean>;
            personalTotal: KnockoutObservable<boolean>;
            workplaceTotal: KnockoutObservable<boolean>;
            grossTotal: KnockoutObservable<boolean>;
            cumulativeWorkplace: KnockoutObservable<boolean>;
            workplaceHierarchyTotal: TotalWorkplaceHierachyModel;
            isIndividualTypeSelected: KnockoutComputed<boolean>;

            constructor() {
                let self = this;
                self.details = ko.observable(false);
                self.personalTotal = ko.observable(false);
                self.workplaceTotal = ko.observable(false);
                self.grossTotal = ko.observable(false);
                self.cumulativeWorkplace = ko.observable(false);
                self.workplaceHierarchyTotal = new TotalWorkplaceHierachyModel();
                self.workplaceHierarchyTotal.cumulativeWorkplace = self.cumulativeWorkplace;
            }
            public updateData(data: WorkScheduleSettingTotalOutputDto): void {
                let self = this;
                self.details(data.details);
                self.personalTotal(data.personalTotal);
                self.workplaceTotal(data.workplaceTotal);
                self.grossTotal(data.grossTotal);
                self.cumulativeWorkplace(data.cumulativeWorkplace);
                self.workplaceHierarchyTotal.updateData(data.workplaceHierarchyTotal);
            }

            public toDto(): WorkScheduleSettingTotalOutputDto {
                let self = this;
                let dto = <WorkScheduleSettingTotalOutputDto>{};
                dto.details = self.details();
                dto.personalTotal = self.personalTotal();
                dto.workplaceTotal = self.workplaceTotal();
                dto.grossTotal = self.grossTotal();
                dto.workplaceHierarchyTotal = self.workplaceHierarchyTotal.toDto();
                dto.cumulativeWorkplace = self.cumulativeWorkplace();

                return dto;
            }

            public isInvalidTotalOutputSetting(): boolean {
                let self = this;
                let settingCount = 0;
                if (self.details()) {
                    settingCount++;
                }
                if (self.personalTotal()) {
                    settingCount++;
                }
                if (self.workplaceTotal()) {
                    settingCount++;
                }
                if (self.grossTotal()) {
                    settingCount++;
                }
                if (self.cumulativeWorkplace()) {
                    settingCount++;
                }
                if (settingCount == 0) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_2309' });
                    return true;
                }
                return false;
            }
        }

        export class TotalWorkplaceHierachyModel {
            firstLevel: KnockoutObservable<boolean>;
            secondLevel: KnockoutObservable<boolean>;
            thirdLevel: KnockoutObservable<boolean>;
            fourthLevel: KnockoutObservable<boolean>;
            fifthLevel: KnockoutObservable<boolean>;
            sixthLevel: KnockoutObservable<boolean>;
            seventhLevel: KnockoutObservable<boolean>;
            eighthLevel: KnockoutObservable<boolean>;
            ninthLevel: KnockoutObservable<boolean>;
            cumulativeWorkplace: KnockoutObservable<boolean>;

            constructor() {
                let self = this;
                self.firstLevel = ko.observable(false);
                self.secondLevel = ko.observable(false);
                self.thirdLevel = ko.observable(false);
                self.fourthLevel = ko.observable(false);
                self.fifthLevel = ko.observable(false);
                self.sixthLevel = ko.observable(false);
                self.seventhLevel = ko.observable(false);
                self.eighthLevel = ko.observable(false);
                self.ninthLevel = ko.observable(false);
            }

            public updateData(data: TotalWorkplaceHierachyDto): void {
                let self = this;
                self.firstLevel(data.firstLevel);
                self.secondLevel(data.secondLevel);
                self.thirdLevel(data.thirdLevel);
                self.fourthLevel(data.fourthLevel);
                self.fifthLevel(data.fifthLevel);
                self.sixthLevel(data.sixthLevel);
                self.seventhLevel(data.seventhLevel);
                self.eighthLevel(data.eighthLevel);
                self.ninthLevel(data.ninthLevel);
            }

            public toDto(): TotalWorkplaceHierachyDto {
                let self = this;
                let dto = <TotalWorkplaceHierachyDto>{};
                dto.firstLevel = self.firstLevel();
                dto.secondLevel = self.secondLevel();
                dto.thirdLevel = self.thirdLevel();
                dto.fourthLevel = self.fourthLevel();
                dto.fifthLevel = self.fifthLevel();
                dto.sixthLevel = self.sixthLevel();
                dto.seventhLevel = self.seventhLevel();
                dto.eighthLevel = self.eighthLevel();
                dto.ninthLevel = self.ninthLevel();
                return dto;
            }

            // when cumulativeWorkplace is checked. level check count must be from 1 ~ 5.
            public isInvalidSetting(): boolean {
                let self = this;
                if (!self.cumulativeWorkplace()) {
                    return false;
                }
                let levelCount = 0;
                if(self.firstLevel()){
                    levelCount++;
                }
                if(self.secondLevel()){
                    levelCount++;
                }
                if(self.thirdLevel()){
                    levelCount++;
                }
                if(self.fourthLevel()){
                    levelCount++;
                }
                if(self.fifthLevel()){
                    levelCount++;
                }
                if(self.sixthLevel()){
                    levelCount++;
                }
                if(self.seventhLevel()){
                    levelCount++;
                }
                if(self.eighthLevel()){
                    levelCount++;
                }
                if(self.ninthLevel()){
                    levelCount++;
                }
                if (levelCount == 0 || levelCount > 5) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_1184' });
                    return true;
                };
                return false;
            }
        }

        class EmployeeError {
            employeeCode: string;
            employeeName: string;

            constructor(employeeCode: string, employeeName: string) {
                this.employeeCode = employeeCode;
                this.employeeName = employeeName;
            }
        }

        class ItemSelectionEnum {
            static FREE_SETTING = 1;
            static STANDARD_SELECTION = 0;
        }
    }
}