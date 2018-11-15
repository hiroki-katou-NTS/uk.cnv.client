module nts.uk.pr.view.qmm038.a {
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import validation = nts.uk.ui.validation;
    export module viewmodel {

        export class ScreenModel {
            statementItems: Array<IDataScreen> = [];
            ccg001ComponentOption: GroupOption = null;
            baseDate: KnockoutObservable<any> = ko.observable(null);
            giveCurrTreatYear: KnockoutObservable<any> = ko.observable(null);
            employeeIds: Array<any>;
            numberValidator = new validation.NumberValidator(getText("QMM038_11"), "AverageWage", { required: true });
            dataUpdate: Array<UpdateEmployee> = [];


            constructor() {
                let self = this;
                nts.uk.pr.view.qmm038.a.service.defaultData().done(function(response) {
                    self.giveCurrTreatYear(response[0].substr(0,4) + "/" + response[0].substr(4));
                    self.baseDate(response[1]);
                    $("#A2_3").focus();
                });

                // CCG001
                self.ccg001ComponentOption = <GroupOption>{
                    /** Common properties */
                    systemType: 1,
                    showEmployeeSelection: true,
                    showQuickSearchTab: true,
                    showAdvancedSearchTab: true,
                    showBaseDate: true,
                    showClosure: false,
                    showAllClosure: false,
                    showPeriod: false,
                    periodFormatYM: false,

                    /** Required parameter */
                    baseDate: self.baseDate(),
                    periodStartDate: moment().toISOString(),
                    periodEndDate: moment().toISOString(),
                    inService: true,
                    leaveOfAbsence: true,
                    closed: true,
                    retirement: true,

                    /** Quick search tab options */
                    showAllReferableEmployee: true,
                    showOnlyMe: true,
                    showSameWorkplace: false,
                    showSameWorkplaceAndChild: false,

                    /** Advanced search properties */
                    showEmployment: true,
                    showWorkplace: false,
                    showClassification: false,
                    showJobTitle: false,
                    showWorktype: false,
                    isMutipleCheck: false,
                    tabindex: 4,

                    /**
                     * Self-defined function: Return data from CCG001
                     * @param: data: the data return from CCG001
                     */
                    returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                        block.invisible();
                        nts.uk.ui.errors.clearAll();
                        self.employeeIds = data.listEmployee.map(item => item.employeeId);
                        let command = { employeeIds: self.employeeIds, baseDate: self.baseDate(),giveCurrTreatYear: self.giveCurrTreatYear() };
                        nts.uk.pr.view.qmm038.a.service.findByEmployee(command).done(function(response) {
                            self.statementItems = [];
                            self.statementItems = _.sortBy(response, ["employeeCode"]);
                            $("#gridStatement").ntsGrid("destroy");
                            self.loadGrid();
                            block.clear();
                        });

                    }

                }

                $('#com-ccg001').ntsGroupComponent(self.ccg001ComponentOption);
                self.loadGrid();
            }

            loadGrid(){
                let self = this;
                $("#gridStatement").ntsGrid({
                    width: '807px',
                    height: '350px',
                    dataSource: self.statementItems,
                    primaryKey: 'employeeCode',
                    virtualization: true,
                    virtualizationMode: 'continuous',
                    columns: [
                        {headerText: getText("QMM038_7"), key: 'employeeCode', dataType: 'string', width: '150px'},
                        {headerText: getText("QMM038_8"), key: 'businessName', dataType: 'string', width: '150px'},
                        {headerText: getText("QMM038_9"), key: 'departmentName', dataType: 'string', width: '150px'},
                        {headerText: getText("QMM038_10"), key: 'employmentName', dataType: 'string', width: '150px'},
                        {
                            headerText: getText("QMM012_121"), key: 'averageWage', dataType: 'string', width: '207px',
                            ntsControl: 'TextEditor'
                        }
                    ],
                    features: [
                        {
                            name: 'Selection',
                            mode: 'row',
                            multipleSelection: false
                        },
                        {
                            name: 'Paging',
                            pageSize: 20,
                            currentPageIndex: 0
                        }

                    ],
                    ntsControls: [
                        {name: 'TextEditor', controlType: 'TextEditor', constraint: {valueType: 'String', required: false}}
                    ]
                });
            }

            findByEmployee() {
                nts.uk.ui.errors.clearAll();
                let self = this;
                let command = { employeeIds: self.employeeIds, baseDate: self.baseDate(),giveCurrTreatYear: self.giveCurrTreatYear() };
                nts.uk.pr.view.qmm038.a.service.findByEmployee(command).done(function(response) {
                    self.statementItems = [];
                    self.statementItems = _.sortBy(response, ["employeeCode"]);
                    $("#gridStatement").ntsGrid("destroy");
                    self.loadGrid();
                });
                $("#A3_1").focus();
            }

            updateStatelmentItemName() {
                let self = this;
                let statementItems: Array<IDataScreen> = $("#gridStatement").igGrid("option", "dataSource")

                self.validateForm(statementItems);
                if(nts.uk.ui.errors.hasError()) {
                    return;
                }
                block.invisible();
                // update
                _.forEach(statementItems, (item: IDataScreen) => {
                    self.dataUpdate.push(new UpdateEmployee(item.employeeId,item.averageWage));
                })
                let command = {
                    employeeDtoList : self.dataUpdate,
                    giveCurrTreatYear : self.giveCurrTreatYear()
                }
                service.update(command).done(function(response) {
                    if(response[0] == "Msg_15") {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {

                        });
                    }
                }).fail(function (error) {

                }).always(function () {
                    block.clear();
                });

            }

            validateForm(statementItems: Array<IDataScreen>) {
                let self = this,
                    check: any;
                nts.uk.ui.errors.clearAll();
                _.each(statementItems, (item: IDataScreen) => {
                    check = self.numberValidator.validate(item.averageWage.toString());
                    if(!check.isValid) {
                        self.setErrorAverageWage(item.employeeCode, check.errorCode, check.errorMessage)
                    }
                })
            }

            setErrorAverageWage(id: string, messageId: any, message: any) {
                $("#gridStatement").find(".nts-grid-control-averageWage-" + id + " input").ntsError('set', {
                    messageId: messageId,
                    message: message
                });
            }

        }


    }
        export interface IDataScreen {
            employeeId: string; // 基準日
            employeeCode: string; // 社員コード
            businessName: string; // ビジネスネーム
            departmentName: string; // 所属部門
            employmentName: string; // 所属雇用
            averageWage: string; // 所属雇用
        }

        // Note: Defining these interfaces are optional
        export interface GroupOption {
            /** Common properties */
            showEmployeeSelection?: boolean; // 検索タイプ
            systemType: number; // システム区分
            showQuickSearchTab?: boolean; // クイック検索
            showAdvancedSearchTab?: boolean; // 詳細検索
            showBaseDate?: boolean; // 基準日利用
            showClosure?: boolean; // 就業締め日利用
            showAllClosure?: boolean; // 全締め表示
            showPeriod?: boolean; // 対象期間利用
            periodFormatYM?: boolean; // 対象期間精度
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
            showAllReferableEmployee?: boolean; // 参照可能な社員すべて
            showOnlyMe?: boolean; // 自分だけ
            showSameWorkplace?: boolean; // 同じ職場の社員
            showSameWorkplaceAndChild?: boolean; // 同じ職場とその配下の社員

            /** Advanced search properties */
            showEmployment?: boolean; // 雇用条件
            showWorkplace?: boolean; // 職場条件
            showClassification?: boolean; // 分類条件
            showJobTitle?: boolean; // 職位条件
            showWorktype?: boolean; // 勤種条件
            isMutipleCheck?: boolean; // 選択モード
            isTab2Lazy?: boolean;

            /** Data returned */
            returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
        }
        export class UpdateEmployee {
            employeeId: string;
            averageWage: string;
            constructor(employeeId: string, averageWage: string) {
                this.employeeId = employeeId;
                this.averageWage = averageWage;
            }
        }
        export interface Ccg001ReturnedData {
            employeeId: string; // 基準日
            employeeCode: string; // 社員コード
            businessName: string; // ビジネスネーム
            departmentName: string; // 所属部門
            employmentName: string; // 所属雇用
            averageWage: string; // 所属雇用
        }

}