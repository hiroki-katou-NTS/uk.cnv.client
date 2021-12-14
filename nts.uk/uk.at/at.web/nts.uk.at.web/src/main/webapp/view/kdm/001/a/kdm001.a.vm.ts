/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdm001.a.viewmodel {
    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import dialog = nts.uk.ui.dialog;
    import getText = nts.uk.resource.getText;
    import modal = nts.uk.ui.windows.sub.modal;
    import block = nts.uk.ui.block;

    export class ScreenModel {
        closureID: string;
        selectedEmployeeObject: any;
        periodOptionItem: KnockoutObservableArray<ItemModel> = ko.observableArray([
            new ItemModel(0, getText("KDM001_4")),
            new ItemModel(1, getText("KDM001_152"))
        ]);
        listEmployee: Array<EmployeeInfo>;
        selectedPeriodItem: KnockoutObservable<number> = ko.observable(0);
        dateValue: KnockoutObservable<any> = ko.observable({});
        startDateString: KnockoutObservable<string> = ko.observable("");
        endDateString: KnockoutObservable<string> = ko.observable("");
        totalRemainingNumber: KnockoutObservable<number> = ko.observable(null);
        expirationDate: KnockoutObservable<string> = ko.observable(null);
        newDataDisable: KnockoutObservable<boolean> = ko.observable(false);
        //_____CCG001________
        ccgcomponent: GroupOption;
        selectedEmployee: KnockoutObservableArray<EmployeeSearchDto> = ko.observableArray([]);
        showinfoSelectedEmployee: KnockoutObservable<boolean> = ko.observable(false);
        baseDate: KnockoutObservable<Date> = ko.observable(new Date());
        //___________KCP009______________
        employeeInputList: KnockoutObservableArray<EmployeeKcp009> = ko.observableArray([]);
        systemReference: KnockoutObservable<number> = ko.observable(SystemType.EMPLOYMENT);
        isDisplayOrganizationName: KnockoutObservable<boolean> = ko.observable(false);
        targetBtnText: string = getText("KCP009_3");
        listComponentOption: ComponentOption;
        selectedItem: KnockoutObservable<string> = ko.observable(null);
        tabindex: number = -1;
        compositePayOutSubMngData: KnockoutObservableArray<CompositePayOutSubMngData> = ko.observableArray([]);
        value: any;
        expirationDateRes: number = 0;
        /** A4_3  凡例 */
        legendOptions: any = {
            items: [
                { labelText: nts.uk.resource.getText("KDM001_154") },
                { labelText: nts.uk.resource.getText("KDM001_155") },
                { labelText: nts.uk.resource.getText("KDM001_156") }
            ]
        };

        // Update ver48
        remainingData: KnockoutObservableArray<any> = ko.observableArray([]);

        residualNumber: KnockoutComputed<number> = ko.computed(() => {
            let total = 0;
            _.map(this.remainingData(), item => {
                total += item.dayLetf;
            });
            return total;
        });

        displayResidualNumber: KnockoutComputed<string> = ko.computed(() => getText('KDM001_25', [this.residualNumber()]));

        expiredNumber: KnockoutComputed<number> = ko.computed(() => {
            let total = 0;
            _.map(this.remainingData(), item => {
                total += item.usedDay;
            });
            return total;
        });

        displayExpiredNumber: KnockoutComputed<string> = ko.computed(() => getText('KDM001_26', [this.expiredNumber()]));
        // End update ver48

        constructor() {
            const vm = this;
            vm.ccgcomponent = {
                /** Common properties */
                systemType: 2,//就業　sửa theo mã bug 102240
                showEmployeeSelection: true,
                showQuickSearchTab: true,
                showAdvancedSearchTab: true,
                showBaseDate: false,
                showClosure: false,
                showAllClosure: false,
                showPeriod: false,
                periodFormatYM: false,

                /** Required parameter */
                baseDate: moment().toISOString(),
                periodStartDate: moment().toISOString(),
                periodEndDate: moment().toISOString(),
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
                returnDataFromCcg001: function (data: Ccg001ReturnedData) {
                    vm.showinfoSelectedEmployee(true);
                    vm.selectedEmployee(data.listEmployee);
                    vm.convertEmployeeCcg01ToKcp009(data.listEmployee);
                }
            }
            window.onresize = function () {
                $("#compositePayOutSubMngDataGrid_scrollContainer").height(window.innerHeight - 513);
                $("#compositePayOutSubMngDataGrid_displayContainer").height(window.innerHeight - 513);
                $("#compositePayOutSubMngDataGrid_container").height(window.innerHeight - 400);
                $("#substituteDataGrid_scrollContainer").height(window.innerHeight - 513);
                $("#substituteDataGrid_displayContainer").height(window.innerHeight - 513);
                $("#substituteDataGrid_container").height(window.innerHeight - 400);
            };
        }

        mounted() {
            const vm = this;
            $("#compositePayOutSubMngDataGrid").ntsGrid({
                // width: '800px',
                height: window.innerHeight - 400 + 'px',
                dataSource: vm.compositePayOutSubMngData(),
                primaryKey: 'id',
                virtualization: true,
                virtualizationMode: 'continuous',
                rowVirtualization: true,
                value: vm.value,
                
                columns: [
                    { headerText: 'ID', key: 'id', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'stateAtr', dataType: 'Number', width: '0px', hidden: true },
                    { headerText: '', key: 'unUsedDays', dataType: 'Number', width: '0px', hidden: true },
                    { headerText: '', key: 'remainDays', dataType: 'Number', width: '0px', hidden: true },
                    { headerText: '', key: 'deadLine', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'occurrenceId', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'digestionId', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'occurredDaysText', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'digestionDaysText', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'dayLetfText', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'usedDayText', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'dataType', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'startDate', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'endDate', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'expiredDateText', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'mergeCell', dataType: 'string', width: '0px', hidden: true },
                    { headerText: '', key: 'legalDistinction', dataType: 'string', width: '0px', hidden: true },
                    // A4_4_1 & A4_4_2
                    {
                        headerText: `${getText('KDM001_8')} ${getText('KDM001_157')}`,
                        key: 'accrualDate',
                        width: '200px',
                        template: '<div style="float:left;white-space:nowrap"> ${accrualDate}${expiredDateText}</div>',
                        dataType: 'string'
                    },
                    // A4_2_2
                    {
                        headerText: getText('KDM001_9'),
                        key: 'occurrenceDay',
                        width: '86px',
                        template: '<div style="float:right"> ${occurrenceDay}${occurredDaysText} </div>',
                        dataType: 'string'
                    },
                    // A4_2_7
                    { 
                        headerText: getText('KDM001_14'),
                        key: 'legalDistinction',
                        width: '100px',
                        formatter: getLawAtr,
                        dataType: 'string'
                    },
                    // A4_2_3
                    {
                        headerText: getText('KDM001_10'),
                        key: 'digestionDay',
                        width: '120px',
                        template: '<div style="float:left">${digestionDay} </div>',
                        dataType: 'string'
                    },
                    // A4_2_4
                    {
                        headerText: getText('KDM001_11'),
                        key: 'digestionDays',
                        width: '86px',
                        template: '<div style="float:right"> ${digestionDays}${digestionDaysText} </div>',
                        dataType: 'string'
                    },
                    // A4_2_5
                    { 
                        headerText: getText('KDM001_12'),
                        key: 'dayLetf',
                        width: '86px',
                        template: '<div style="float:right"> ${dayLetf}${dayLetfText} </div>',
                        dataType: 'string'
                    },
                    // A4_2_6
                    { 
                        headerText: getText('KDM001_13'),
                        key: 'usedDay',
                        width: '86px',
                        template: '<div style="float:right"> ${usedDay}${usedDayText} </div>',
                        dataType: 'string'
                    },                  
                    // A4_2_9
                    { headerText: '', key: 'delete', dataType: 'string', width: '55px', unbound: true, ntsControl: 'ButtonCorrection' }
                ],
                features: [
                    {
                        name: 'Paging',
                        type: "local",
                        pageSize: 100,
                        pageSizeList: [15, 50, 100]
                    },
                    {
                        name: 'CellMerging',
                        mergeType: 'physical',
                        columnSettings: [
                            {
                                columnKey: 'accrualDate',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    this.isMergeStrategy(prevRec, curRec, columnKey)
                            },
                            {
                                columnKey: 'occurrenceDay',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    prevRec[ 'accrualDate' ] === curRec[ 'accrualDate' ]
                                    && this.isMergeStrategy(prevRec, curRec, columnKey)
                            },
                            {
                                columnKey: 'legalDistinction',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    this.isMergeStrategy(prevRec, curRec, columnKey)
                            },
                            {
                                columnKey: 'digestionDay',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    this.isMergeStrategy(prevRec, curRec, columnKey)
                            },
                            {
                                columnKey: 'digestionDays',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    prevRec[ 'digestionDay' ] === curRec[ 'digestionDay' ]
                                    && this.isMergeStrategy(prevRec, curRec, columnKey)
                            },
                            {
                                columnKey: 'dayLetf',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    prevRec['mergeCell'] === curRec['mergeCell'] && prevRec[columnKey] === curRec[columnKey]
                            },
                            {
                                columnKey: 'usedDay',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any, columnKey: any) =>
                                    prevRec['mergeCell'] === curRec['mergeCell'] && prevRec[columnKey] === curRec[columnKey]
                            },
                            {
                                columnKey: 'delete',
                                mergeOn: 'always',
                                mergeStrategy: (prevRec: any, curRec: any) => prevRec['mergeCell'] === curRec['mergeCell']
                            }
                        ]
                    }
                ],
                ntsControls: [
                    { name: 'ButtonCorrection', text: getText('KDM001_100'), click: function (value: any) { vm.removeData(value) }, controlType: 'Button' }
                ]
            });
            vm.startDateString.subscribe(function (value) {
                vm.dateValue().startDate = value;
                vm.dateValue.valueHasMutated();
            });
            vm.endDateString.subscribe(function (value) {
              vm.dateValue().endDate = value;
              vm.dateValue.valueHasMutated();
            });
            vm.selectedItem.subscribe(x => {
              if(vm.listEmployee) {
                vm.selectedEmployeeObject = _.find(vm.listEmployee, item => item.employeeId === x);
              }
              vm.updateDataList(true);
            });
            vm.selectedPeriodItem.subscribe(period => {
                if (period === 0) {
                    nts.uk.ui.errors.clearAll();
                    vm.updateDataList(true);
                } else {
                    vm.updateDataList(true);
                }
            });
        }

        isMergeStrategy(prevRec: any, curRec: any, columnKey: any): boolean {
            if ($.type(prevRec[ columnKey ]) === 'string' &&
                $.type(curRec[ columnKey ]) === 'string' &&
                prevRec[ 'mergeCell' ] === curRec[ 'mergeCell' ] &&
                !_.isEmpty(prevRec[ columnKey ])) {
                    return prevRec[ columnKey ].toLowerCase() === curRec[ columnKey ].toLowerCase();
            }
            return false;
        }

        // A4_2_9 削除
        public removeData(value: CompositePayOutSubMngData) {
            block.invisible();
            dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                const vm = this;
                const mergeCell = value.mergeCell;
                const listItem: CompositePayOutSubMngData[] = _.filter(vm.compositePayOutSubMngData(),
                    item => _.toNumber(item.mergeCell) === _.toNumber(mergeCell));
                const payoutIds = _.map(listItem, item => item.occurrenceId);
                const subOfHDIDs = _.map(listItem, item => item.digestionId);
                const data = {
                    payoutId: payoutIds,
                    subOfHDID: subOfHDIDs
                };

                service.removePayout(data).done(() => {
                    dialog.info({ messageId: "Msg_16" })
                        .then(() => vm.updateDataList(true));
                }).fail(function (res) {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId });
                    vm.updateDataList(true);
                }).always(() => block.clear());
            }).then(() => block.clear());
        }

        openNewSubstituteData() {
            let self = this;
            setShared('KDM001_D_PARAMS', { selectedEmployee: self.selectedEmployeeObject, closureId: self.closureID, residualNumber: self.residualNumber() });

            modal("/view/kdm/001/d/index.xhtml").onClosed(function () {
                let params = getShared('KDM001_A_PARAMS');

                if (params.isSuccess) {
                    self.selectedPeriodItem(1);
                    self.updateDataList(true);
                }

                $('#compositePayOutSubMngDataGrid').focus();
            });
        }

        goToKDR004() {
            let self = this;
            setShared('KDM001_PARAMS', {
                selectedEmployee: self.selectedEmployeeObject, closureId: self.closureID, dateRange: self.dateValue(),
                selectedPeriodItem: self.selectedPeriodItem(), totalRemainingNumber: self.totalRemainingNumber(), expirationDate: self.expirationDate()
            });
            nts.uk.request.jump("/view/kdr/004/a/index.xhtml");
        }

        goToScreenB() {
            nts.uk.request.jump("/view/kdm/001/b/index.xhtml");
        }

        clickGetDataList() {
            let self = this;

            self.updateDataList(true);

            $('#compositePayOutSubMngDataGrid').focus();
        }

        updateDataList(isShowMsg: boolean) {
            let self = this;
            let empId = self.selectedItem();
            let isPeriod = self.selectedPeriodItem() == 0 ? false : true;
            if (isPeriod) {
                $("#daterangepickerA .ntsDatepicker").trigger("validate");
            }
            $("#compositePayOutSubMngDataGrid").igGrid("dataSourceObject", []).igGrid("dataBind");

            if (!nts.uk.ui.errors.hasError()) {
                service.getFurikyuMngDataExtraction(empId, isPeriod).done(function (res: any) {
                    let arrayResponse = res.remainingData;
                    self.remainingData(res.remainingData);
                    let compositePayOutSubMngDataArray: CompositePayOutSubMngData[] = [];
                    for (let i = 0; i < arrayResponse.length; i++) {
                        compositePayOutSubMngDataArray.push(new CompositePayOutSubMngData(arrayResponse[i], res.startDate, res.endDate));
                    }

                    // update data to view
                    self.compositePayOutSubMngData(compositePayOutSubMngDataArray);
                    self.totalRemainingNumber(res.totalRemainingNumber);
                    self.expirationDateRes = res.expirationDate;
                    self.expirationDate(self.getExpirationTime(res.expirationDate));
                    self.closureID = res.closureId;
                    self.newDataDisable(false);

                    if (isShowMsg && arrayResponse.length == 0) {
                        dialog.alertError({ messageId: "Msg_725" });
                    }

                    // update grid
                    $("#compositePayOutSubMngDataGrid").igGrid("dataSourceObject", self.compositePayOutSubMngData()).igGrid("dataBind");

                    // disable edit button
                    _.forEach(self.compositePayOutSubMngData(), function (value) {
                        let rowId = value.id;

                        if (value.isLinked) {
                            $("#compositePayOutSubMngDataGrid").ntsGrid("disableNtsControlAt", rowId, 'edit', 'Button');
                        } else {
                            $("#compositePayOutSubMngDataGrid").ntsGrid("enableNtsControlAt", rowId, "edit", 'Button');
                        }
                    });

                    if (_.isNil(res.wkHistory)) {
                        self.selectedEmployeeObject = {
                            employeeId: res.employeeId,
                            employeeCode: res.employeeCode,
                            employeeName: res.employeeName,
                            workplaceId: '',
                            workplaceCode: '',
                            workplaceName: ''
                        };
                    } else {
                        self.selectedEmployeeObject = {
                            employeeId: res.employeeId,
                            employeeCode: res.employeeCode,
                            employeeName: res.employeeName,
                            workplaceId: res.wkHistory.workplaceId,
                            workplaceCode: res.wkHistory.workplaceCode,
                            workplaceName: res.wkHistory.workplaceName
                        };
                    }

                }).fail(function (res: any) {
                  dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds })
                    if(res.messageId === 'Msg_1731'){
                      self.newDataDisable(true);
                    }

                    self.compositePayOutSubMngData([]);
                    console.log(res);
                });

                $('#compositePayOutSubMngDataGrid').focus();
            } else {
                service.getFurikyuMngDataExtraction(empId, false).done(function (res: any) {
                    // update data to view
                    self.totalRemainingNumber(0);
                    self.expirationDate("");
                    self.closureID = "";
                    self.newDataDisable(true);
                    self.startDateString(res.startDate);
                    self.endDateString(res.endDate);
                    // update grid
                    self.compositePayOutSubMngData([]);
                    $("#compositePayOutSubMngDataGrid").igGrid("dataSourceObject", self.compositePayOutSubMngData()).igGrid("dataBind");

                    // add dialog
                    dialog.alertError({ messageId: "Msg_1306" });

                    if (_.isNil(res.wkHistory)) {
                        self.selectedEmployeeObject = {
                            employeeId: res.employeeId,
                            employeeCode: res.employeeCode,
                            employeeName: res.employeeName,
                            workplaceId: '',
                            workplaceCode: '',
                            workplaceName: ''
                        };
                    } else {
                        self.selectedEmployeeObject = {
                            employeeId: res.employeeId,
                            employeeCode: res.employeeCode,
                            employeeName: res.employeeName,
                            workplaceId: res.wkHistory.workplaceId,
                            workplaceCode: res.wkHistory.workplaceCode,
                            workplaceName: res.wkHistory.workplaceName
                        };
                    }

                })
                .fail((res: any) => {
                    self.compositePayOutSubMngData([]);
                    console.log(res);
                });
            }
        }

        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            self.mounted();
            block.invisible();
            service.getInfoEmLogin().done(function (emp) {
                service.getWpName().done(function (wp) {
                    if (wp == null || wp.workplaceId == null || wp.workplaceId == "") {
                        dialog.alertError({ messageId: "Msg_504" }).then(() => {
                            nts.uk.request.jump("com", "/view/ccg/008/a/index.xhtml");
                        });
                    } else {
                        self.selectedEmployeeObject = {
                            employeeId: emp.sid, employeeCode: emp.employeeCode, employeeName: emp.employeeName,
                            workplaceId: wp.workplaceId, workplaceCode: wp.code, workplaceName: wp.name
                        };

                        if (!_.find(self.employeeInputList(), item => item.id === emp.sid)) {
                            self.employeeInputList.push(new EmployeeKcp009(emp.sid, emp.employeeCode, emp.employeeName, wp.name, wp.name));
                        }
                        self.initKCP009();
                        dfd.resolve();
                    }
                }).fail(function (result) {
                    dialog.alertError({ messageId: result.messageId, messageParams: result.parameterIds }).then(function () { nts.uk.ui.block.clear(); });;
                    dfd.reject();
                });
            }).fail(function (result) {
                dialog.alertError({ messageId: result.messageId, messageParams: result.parameterIds }).then(function () { nts.uk.ui.block.clear(); });;
                dfd.reject();
            });

            return dfd.promise();
        }

        initKCP009() {
            let self = this;
            //_______KCP009_______
            // Initial listComponentOption
            self.listComponentOption = {
                systemReference: self.systemReference(),
                isDisplayOrganizationName: self.isDisplayOrganizationName(),
                employeeInputList: self.employeeInputList,
                targetBtnText: self.targetBtnText,
                selectedItem: self.selectedItem,
                tabIndex: self.tabindex
            };
            $('#emp-componentA').ntsLoadListComponent(self.listComponentOption);
        }

        convertEmployeeCcg01ToKcp009(dataList: EmployeeSearchDto[]): void {

            // set data for KCP009 screen B
            if (!($("#tabpanel-2").hasClass("disappear"))) {
                var screenBModel = __viewContext.viewModel.viewmodelB;
                screenBModel.convertEmployeeCcg01ToKcp009(dataList);
            } else {
                let self = this;
                self.listEmployee = [];
                self.employeeInputList([]);
                _.each(dataList, function (item) {
                  self.listEmployee.push(new EmployeeInfo(item.employeeId, item.employeeCode, item.employeeName, '', ''));
                    self.employeeInputList.push(new EmployeeKcp009(item.employeeId, item.employeeCode, item.employeeName, item.workplaceName, ""));
                });
                $('#emp-componentA').ntsLoadListComponent(self.listComponentOption);
                if (dataList.length == 0) {
                    self.selectedItem('');
                } else {
                    let item = self.findIdSelected(dataList, self.selectedItem());
                    let sortByEmployeeCode = _.orderBy(dataList, ["employeeCode"], ["asc"]);
                    if (item == undefined) self.selectedItem(sortByEmployeeCode[0].employeeId);
                }

            }
        }

        findIdSelected(dataList: Array<any>, selectedItem: string): any {
            return _.find(dataList, function (obj) {
                return obj.employeeId == selectedItem;
            })
        }

        pegSetting(value: any) {
            let self = this;
            let selectedRowData = value;
            if (value.dataType == 1) {
                if (value.digestionDay.length < 2) value.digestionDay = "";
                setShared('KDM001_EFGH_PARAMS', { rowValue: value, selectedEmployee: self.selectedEmployeeObject, closureId: self.closureID });
                modal("/view/kdm/001/f/index.xhtml").onClosed(function () {
                    let params = getShared('KDM001_A_PARAMS');
                    if (params.isSuccess) {
                        self.updateDataList(false);
                    }
                });
            } else {
                if (value.accrualDate.length < 2) value.accrualDate = "";
                setShared('KDM001_EFGH_PARAMS', { rowValue: value, selectedEmployee: self.selectedEmployeeObject, closureId: self.closureID });
                modal("/view/kdm/001/e/index.xhtml").onClosed(function () {
                    let params = getShared('KDM001_A_PARAMS');
                    if (params.isSuccess) {
                        self.updateDataList(false);
                    }
                });
            }
        }

        getExpirationTime(value: number) {
            if (value == 0) {
                return getText("Enum_ExpirationTime_THIS_MONTH");
            } else if (value == 1) {
                return getText("Enum_ExpirationTime_UNLIMITED");
            } else if (value == 2) {
                return getText("Enum_ExpirationTime_END_OF_YEAR");
            } else if (value == 3) {
                return getText("Enum_ExpirationTime_ONE_MONTH");
            } else if (value == 4) {
                return getText("Enum_ExpirationTime_TWO_MONTH");
            } else if (value == 5) {
                return getText("Enum_ExpirationTime_THREE_MONTH");
            } else if (value == 6) {
                return getText("Enum_ExpirationTime_FOUR_MONTH");
            } else if (value == 7) {
                return getText("Enum_ExpirationTime_FIVE_MONTH");
            } else if (value == 8) {
                return getText("Enum_ExpirationTime_SIX_MONTH");
            } else if (value == 9) {
                return getText("Enum_ExpirationTime_SEVEN_MONTH");
            } else if (value == 10) {
                return getText("Enum_ExpirationTime_EIGHT_MONTH");
            } else if (value == 11) {
                return getText("Enum_ExpirationTime_NINE_MONTH");
            } else if (value == 12) {
                return getText("Enum_ExpirationTime_TEN_MONTH");
            } else if (value == 13) {
                return getText("Enum_ExpirationTime_ELEVEN_MONTH");
            } else if (value == 14) {
                return getText("Enum_ExpirationTime_ONE_YEAR");
            } else if (value == 15) {
                return getText("Enum_ExpirationTime_TWO_YEAR");
            } else if (value == 16) {
                return getText("Enum_ExpirationTime_THREE_YEAR");
            } else if (value == 17) {
                return getText("Enum_ExpirationTime_FOUR_YEAR");
            } else if (value == 18) {
                return getText("Enum_ExpirationTime_FIVE_YEAR");
            } else {
                return "";
            }
        }
    }

    export class ItemModel {
        value: number;
        name: string;
        constructor(value: number, name: string) {
            this.value = value;
            this.name = name;
        }
    }

    //__________KCP009_________
    export interface ComponentOption {
        systemReference: SystemType;
        isDisplayOrganizationName: boolean;
        employeeInputList: KnockoutObservableArray<EmployeeKcp009>;
        targetBtnText: string;
        selectedItem: KnockoutObservable<string>;
        tabIndex: number;
    }

    export class EmployeeKcp009 {
        id: string;
        code: string;
        businessName: string;
        workplaceName: string;
        depName: string;
        constructor(id: string, code: string, businessName: string, workplaceName: string, depName: string) {
            this.id = id;
            this.code = code;
            this.businessName = businessName;
            this.workplaceName = workplaceName;
            this.depName = depName;
        }
    }

    export class SystemType {
        static EMPLOYMENT = 1;
        static SALARY = 2;
        static PERSONNEL = 3;
        static ACCOUNTING = 4;
        static OH = 6;
    }

    //Interfaces
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

    export interface EmployeeSearchDto {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        workplaceId: string;
        workplaceName: string;
    }
    export class EmployeeInfo {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        workplaceId: string;
        workplaceName: string;
        constructor(employeeId: string, employeeCode: string, employeeName: string, workplaceId: string, workplaceName: string) {
            this.employeeId = employeeId;
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.workplaceId = workplaceId;
            this.workplaceName = workplaceName;
        }
  }

    export interface Ccg001ReturnedData {
        baseDate: string; // 基準日
        closureId?: number; // 締めID
        periodStart: string; // 対象期間（開始)
        periodEnd: string; // 対象期間（終了）
        listEmployee: Array<EmployeeSearchDto>; // 検索結果
    }

  export class CompositePayOutSubMngData {
        id: string;
        occurrenceId: string;
        accrualDate: string;
        deadLine: string;
        legalDistinction: number;
        occurrenceDay: string;
        unUsedDays: number;
        stateAtr: number;
        payoutTied: string;
        subOfHDID: string;
        unknownDateSub: boolean;
        digestionDay: string;
        digestionDays: string;
        remainDays: number;
        digestionId: string;
        startDate: string
        expirationDate : number;
        endDate: string;
        dayLetf: string;
        usedDay: string;
        dataType: number = 1;
        occurredDaysText: string;
        digestionDaysText: string;
        dayLetfText: string;
        usedDayText: string;
        expiredDateText: string;
        mergeCell: number

        constructor(params:any, startDateP: string, endDateP:string ) {
            this.startDate = startDateP;
            this.endDate = endDateP;
            this.id = `${params.occurrenceId}-${params.digestionId}`;
            this.occurrenceId = params.occurrenceId;
            this.digestionId = params.digestionId;
            let dayOffPayout = "";

            if (params.accrualDate) {
                dayOffPayout = params.accrualDate;
            }
            this.accrualDate = !_.isEmpty(params.occurrenceId) && params.occurrenceId !== 0 && _.isEmpty(params.accrualDate) ? getText('KDM001_160') : dayOffPayout;
            this.deadLine = params.deadLine;
            this.legalDistinction = params.legalDistinction;
            this.unUsedDays = params.unUsedDays;
            this.stateAtr = params.stateAtr;
            this.payoutTied = params.payoutTied ? getText("KDM001_130") : "";
            this.subOfHDID = params.subOfHDID;
            this.unknownDateSub = params.unknownDateSub;
            let digestionDay = "";
            if (params.digestionDay) {
                digestionDay = params.digestionDay;
            }
            this.digestionDay = !_.isEmpty(params.digestionId) && params.digestionId !== 0 && _.isEmpty(params.digestionDay) ? getText('KDM001_160') : digestionDay;
            this.remainDays = params.remainDays;

            if (params.occurrenceDay > 0) {
                this.occurredDaysText = getText("KDM001_27");
                this.occurrenceDay = params.occurrenceDay.toFixed(1);
            } else {
                this.occurrenceDay = "";
            }

            if (params.digestionDays > 0) {
                this.digestionDaysText = getText("KDM001_27");
                this.digestionDays = params.digestionDays.toFixed(1);
            } else {
                this.digestionDays = "";
            }

            if (params.occurrenceId != null && params.occurrenceId != "") {
                if (this.stateAtr !== 2 &&moment.utc(params.deadLine, "YYYY/MM/DD").diff(moment.utc(moment.utc().format("YYYY/MM/DD"), "YYYY/MM/DD")) >= 0) {
                    this.dayLetf = params.dayLetf;
                    this.usedDay = "0";
                    if (params.dayLetf > 0) {
                        this.dayLetfText = getText("KDM001_27");
                        this.dayLetf = params.dayLetf.toFixed(1);
                    }
                } else {
                    this.dayLetf = "0";
                    this.usedDay = "" + params.unUsedDays;
                    if (params.unUsedDays > 0) {
                        this.usedDay = params.unUsedDays.toFixed(1);
                    }
                }
            } else if (params.digestionId != null && params.digestionId != "") {
                this.dayLetf = "" + params.remainDays * -1;
                this.usedDay = "0";
                if (params.remainDays > 0) {
                    this.dayLetfText = getText("KDM001_27");
                    this.dayLetf = (params.remainDays * -1).toFixed(1);
                }
            }

            if (params.usedDay > 0) {
                this.usedDay = params.usedDay.toFixed(1);
            } else {
                this.usedDay = "";
            }

            if (params.dayLetf > 0) {
                this.dayLetfText = getText("KDM001_27");
                this.dayLetf = params.dayLetf.toFixed(1);
            } else {
                this.dayLetf = "";
            }
            
            let expiredDateText = moment.utc(params.deadLine, 'YYYY/MM/DD');
            let startDate = moment.utc(this.startDate);
            let endDate = moment.utc(this.endDate);
            if (params.deadLine === '') {
                this.expiredDateText === '';
            } else if (startDate.isSameOrBefore(expiredDateText) && endDate.isSameOrAfter(expiredDateText)) {
                this.expiredDateText = getText('KDM001_161', [params.deadLine]);
            } else if (startDate.isSameOrAfter(expiredDateText) && params.usedDay > 0) {
                this.expiredDateText = getText('KDM001_162', [params.deadLine]);
            } else {
                this.expiredDateText = getText('KDM001_163', [params.deadLine]);
            }

            if (this.usedDay !== '') {
                this.usedDayText = getText('KDM001_27');
            }

            if (params.mergeCell !== null) {
                this.mergeCell = params.mergeCell
            }
         
        }
    }

    function getLawAtr(value: any, row: any) {
        if (value && value === '0') {
            return getText("Enum_HolidayAtr_STATUTORY_HOLIDAYS");
        } else if (value && value === '1') {
            return getText("Enum_HolidayAtr_NON_STATUTORY_HOLIDAYS");
        } else if (value && value === '2') {
            return getText("Enum_HolidayAtr_PUBLIC_HOLIDAY");
        } else {
            return '';
        }
    }
}

