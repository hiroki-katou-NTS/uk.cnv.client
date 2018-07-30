module nts.uk.com.view.cli003.b.viewmodel {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    //C screen
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
    import ComponentOption = kcp.share.list.ComponentOption;
    import ListType = kcp.share.list.ListType;
    import SelectType = kcp.share.list.SelectType;
    import UnitModel = kcp.share.list.UnitModel;
    import UnitAlreadySettingModel = kcp.share.list.UnitAlreadySettingModel;


    export class ScreenModel {
        //wizard
        stepList: Array<NtsWizardStep> = [];
        stepSelected: KnockoutObservable<NtsWizardStep>;
        activeStep: KnockoutObservable<number>;
        

        //B
        itemList: KnockoutObservableArray<ItemModel>;
        itemName: KnockoutObservable<string>;
        currentCode: KnockoutObservable<number>
        logTypeSelectedCode: KnockoutObservable<string>;
        selectedCodes: KnockoutObservableArray<string>;
        isEnable: KnockoutObservable<boolean>;

        //C
        roundingRules: KnockoutObservableArray<any>;
        selectedRuleCode: any;
        employeeList: KnockoutObservableArray<UnitModel>;
        initEmployeeList: KnockoutObservableArray<UnitModel>;
        enable: KnockoutObservable<boolean>;
        required: KnockoutObservable<boolean>;
        dateValue: KnockoutObservable<any>;
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;
        dateCtoE: KnockoutObservable<string>;

        //D
        roundingRulesOperator: KnockoutObservableArray<any>;
        startDateOperator: KnockoutObservable<string>;
        endDateOperator: KnockoutObservable<string>;
        selectedRuleCodeOperator: any;
        employeeListOperator: KnockoutObservableArray<UnitModel>;
        initEmployeeListOperator: KnockoutObservableArray<UnitModel>;
        selectedTitleAtrOperator: KnockoutObservable<number>;
        selectedEmployeeCodeOperator: KnockoutObservableArray<string>;
        listEmployeeIdOperator: KnockoutObservableArray<any>;
        startDateNameOperator: KnockoutObservable<string>;
        endDateNameOperator: KnockoutObservable<string>;

        //E
        dateOperator: KnockoutObservable<string>;
        isDisplayTarget: KnockoutObservable<boolean>;
        logTypeSelectedName: KnockoutObservable<string>;
        targetNumber: KnockoutObservable<string>;
        operatorNumber: KnockoutObservable<string>;

        //F
        itemsIgGrid: KnockoutObservableArray<ItemDataModel>;
        columnsIgGrid: KnockoutObservableArray<IgGridColumnModel>;
        supColumnsIgGrid: KnockoutObservableArray<IgGridColumnModel>;
        columnsHeaderLogRecord: KnockoutObservableArray<String> = ko.observableArray(['2', '3', '7', '19', '20', '22']);
        columnsHeaderLogStartUp: KnockoutObservableArray<String> = ko.observableArray(['2', '3', '7', '18', '19']);
        columnsHeaderLogPersionInfo: KnockoutObservableArray<String> = ko.observableArray(['2', '3', '7', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29','31', '33', '36']);
        columnsHeaderLogDataCorrect: KnockoutObservableArray<String> = ko.observableArray(['2', '3', '7', '20', '21', '22', '23', '24', '26', '27', '30', '31']);
        listLogBasicInforModel: LogBasicInfoModel[];
        logBasicInforCsv: LogBasicInfoModel[];
        isDisplayText: KnockoutObservable<boolean> = ko.observable(false);

        // I
        itemOutPutSelect: KnockoutObservable<string>;
        listItemNo:KnockoutObservableArray<String>;
        listLogBasicInforAllModel: LogBasicInforAllModel[];
        columnsIgAllGrid: KnockoutObservableArray<IgGridColumnAllModel>;  
        constructor() {
            var self = this;
            $("#ccgcomponent").hide();
            self.stepList = [
                { content: '.step-1' },
                { content: '.step-2' },
                { content: '.step-3' },
                { content: '.step-4' },
                { content: '.step-5' }
            ];
            //C   
            self.initComponentC();
            self.initComponnentKCP005();
            self.initComponentCCG001();


            //D 
            self.initComponentD();
            self.initComponnentKCP005Operator();
            //E
            self.initComponentE();
           
            self.itemList = ko.observableArray([
                new ItemModel(RECORD_TYPE.LOGIN, 'ログイン'),
                new ItemModel(RECORD_TYPE.START_UP, '起動'),
            //    new ItemModel(RECORD_TYPE.UPDATE_MASTER, 'マスタ修正'),
                new ItemModel(RECORD_TYPE.UPDATE_PERSION_INFO, '個人情報修正'),
            //    new ItemModel(RECORD_TYPE.DATA_REFERENCE, 'データ参照'),
          //      new ItemModel(RECORD_TYPE.DATA_MANIPULATION, 'データ操作'),
                new ItemModel(RECORD_TYPE.DATA_CORRECT, 'データ修正')
          //      new ItemModel(RECORD_TYPE.MY_NUMBER, 'マイナンバー'),
          //      new ItemModel(RECORD_TYPE.TERMINAL_COMMUNICATION_INFO, '情報端末通信')

            ]);
            self.itemName = ko.observable('');
            self.currentCode = ko.observable(3);
            self.logTypeSelectedCode = ko.observable(RECORD_TYPE.LOGIN);
            self.isEnable = ko.observable(true);
            // end screen B
            self.activeStep = ko.observable(0);
            self.displayStep2 = ko.observable(false);
            self.stepSelected = ko.observable({ id: 'step-1', content: '.step-1' });

        }

        //C list selectedEmployeeCodeTarget
        initComponentC() {
            var self = this;

            self.initEmployeeList = ko.observableArray([]);
            self.employeeDeletionList = ko.observableArray([]);
            self.categoryDeletionList = ko.observableArray([]);
            self.selectedEmployeeCodeTarget = ko.observableArray([]);
            // update id sau check  
            self.targetEmployeeIdList = ko.observableArray([]);

            self.alreadySettingPersonal = ko.observableArray([]);

            self.employeeList = ko.observableArray([]);
            //Date
            self.enable = ko.observable(true);
            self.required = ko.observable(true);

            self.startDateString = ko.observable("");
            self.endDateString = ko.observable("");
            self.dateValue = ko.observable({});

            self.startDateString.subscribe(function(value) {
                self.dateValue().startDate = value;
                self.dateValue.valueHasMutated();
            });

            self.endDateString.subscribe(function(value) {
                self.dateValue().endDate = value;
                self.dateValue.valueHasMutated();
            });
            self.dateValue = ko.observable({
                startDate: moment.utc().format("YYYY/MM/DD"),
                endDate: moment.utc().format("YYYY/MM/DD")

            });

            self.roundingRules = ko.observableArray([
                { code: '1', name: getText('CLI003_17') },
                { code: '2', name: getText('CLI003_18') }
            ]);
            self.selectedRuleCode = ko.observable(1);
            self.selectedTitleAtr = ko.observable(0);
            self.selectedTitleAtr.subscribe(function(value) {
                if (value == 1) {
                    self.applyKCP005ContentSearch(self.initEmployeeList());
                }
                else {
                    self.applyKCP005ContentSearch([]);
                }
            });


        }
        initComponentCCG001() {
            let self = this;
            // Set component option 
            self.ccg001ComponentOption = {
                showEmployeeSelection: false,
                systemType: 5,
                showQuickSearchTab: false,
                showAdvancedSearchTab: true,
                showBaseDate: true,
                showClosure: false,
                showAllClosure: false,
                showPeriod: false,
                periodFormatYM: false,


                /** Quick search tab options */
                showAllReferableEmployee: false,
                showOnlyMe: false,
                showSameWorkplace: false,
                showSameWorkplaceAndChild: false,


                /** Advanced search properties */
                showEmployment: true,
                showWorkplace: true,
                showClassification: true,
                showJobTitle: true,
                showWorktype: false,
                isMutipleCheck: true,



                /** Required parameter */
                baseDate: moment().toISOString(),
                periodStartDate: moment().toISOString(),
                periodEndDate: moment().toISOString(),
                inService: true,
                leaveOfAbsence: true,
                closed: true,
                retirement: true,
                /**
                * Self-defined function: Return data from CCG001
                * @param: data: the data return from CCG001
                */
                returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                    self.selectedTitleAtr(1);
                    if (self.activeStep() == 1) {
                        self.initEmployeeList(data.listEmployee);
                        self.applyKCP005ContentSearch(data.listEmployee);
                    }
                    if (self.activeStep() == 2) {
                        self.initEmployeeListOperator(data.listEmployee);
                        self.applyKCP005ContentSearchOperator(data.listEmployee);
                    }

                }
            }
        }

        applyKCP005ContentSearch(dataEmployee: EmployeeSearchDto[]) {
            var self = this;
            var employeeSearchs: UnitModel[] = [];
            _.forEach(dataEmployee, function(item: EmployeeSearchDto) {
                employeeSearchs.push(new UnitModel(item.employeeId, item.employeeCode,
                    item.employeeName, item.workplaceName));
            });
            self.employeeList(employeeSearchs);
        }

        initComponnentKCP005() {
            //KCP005
            var self = this;
            self.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: true,
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_ALL,
                selectedCode: self.selectedEmployeeCodeTarget,
                isDialog: true,
                isShowNoSelectRow: false,
                alreadySettingList: self.alreadySettingPersonal,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: true,
                maxWidth: 550,
                maxRows: 15
            };
        }
        //start page data 
        public startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            self.switchCodeChange();
            dfd.resolve(self);
            return dfd.promise();
        }

        //update the list of selected employees C targetEmployeeIdList

        setEmployeeListTarget() {
            var self = this;
            self.targetEmployeeIdList.removeAll();
            if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.SPECIFY) {
                let empCodeLength = self.selectedEmployeeCodeTarget().length;
                let empListLength = self.employeeList().length;
                for (var i = 0; i < empCodeLength; i++) {
                    for (var j = 0; j < empListLength; j++) {
                        let employee = self.employeeList()[j];
                        if (employee.code == self.selectedEmployeeCodeTarget()[i]) {
                            self.targetEmployeeIdList.push(employee.id);
                        }
                    }
                }
            }
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.ALL) {
                let empListLength = self.employeeList().length;
                for (var j = 0; j < empListLength; j++) {
                    let employee = self.employeeList()[j];
                    self.targetEmployeeIdList.push(employee.id);
                }
            }

        }

        /**
         *Check validate client
         */
        private validateForm() {
            $(".validate_form").trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return false;
            }
            return true;
        };

        //D component
        initComponentD() {
            var self = this;
            self.readonly = ko.observable(false);
            self.employeeDeletionListOperator = ko.observableArray([]);
            self.initEmployeeListOperator = ko.observableArray([]);
            self.selectedEmployeeCodeOperator = ko.observableArray([]);
            self.alreadySettingPersonalOperator = ko.observableArray([]);
            self.employeeListOperator = ko.observableArray([]);
            self.listEmployeeIdOperator = ko.observableArray([]);
            self.startDateOperator = ko.observable(moment.utc().format("YYYY/MM/DD 0:00:00"));
            self.endDateOperator = ko.observable(moment.utc().format("YYYY/MM/DD 23:59:59"));
            self.startDateNameOperator = ko.observable(getText('CLI003_52'));
            self.endDateNameOperator = ko.observable(getText('CLI003_53'));
            self.roundingRulesOperator = ko.observableArray([
                { code: EMPLOYEE_SPECIFIC.SPECIFY, name: getText('CLI003_17') },
                { code: EMPLOYEE_SPECIFIC.ALL, name: getText('CLI003_18') }
            ]);
            self.selectedRuleCodeOperator = ko.observable(1);
            self.selectedTitleAtrOperator = ko.observable(0);
            self.selectedTitleAtrOperator.subscribe(function(value) {
                if (value == 1) {
                    self.applyKCP005ContentSearchOperator(self.initEmployeeListOperator());
                }
                else {
                    self.applyKCP005ContentSearchOperator([]);
                }
            });
        }


        initComponentE() {
            var self = this;
            self.dateOperator = ko.observable("");
            self.logTypeSelectedName = ko.observable("");
            self.isDisplayTarget = ko.observable(false);
            self.targetNumber = ko.observable("");
            self.operatorNumber = ko.observable("");
        }
        applyKCP005ContentSearchOperator(dataEmployee: EmployeeSearchDto[]) {
            var self = this;
            var employeeSearchs: UnitModel[] = [];
            _.forEach(dataEmployee, function(item: EmployeeSearchDto) {
                employeeSearchs.push(new UnitModel(item.employeeId, item.employeeCode,
                    item.employeeName, item.workplaceName));
            });
            self.employeeListOperator(employeeSearchs);
        }

        initComponnentKCP005Operator() {
            //KCP005
            var self = this;
            self.listComponentOptionOperator = {
                isShowAlreadySet: false,
                isMultiSelect: true,
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeListOperator,
                selectType: SelectType.SELECT_ALL,
                selectedCode: self.selectedEmployeeCodeOperator,
                isDialog: true,
                isShowNoSelectRow: false,
                alreadySettingList: self.alreadySettingPersonalOperator,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: true,
                maxWidth: 550,
                maxRows: 15
            };
        }

        //D array ID Operator
        getListEmployeeIdOperator() {
            var self = this;
            self.listEmployeeIdOperator.removeAll();
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                let empCodeLength = self.selectedEmployeeCodeOperator().length;
                let empListLength = self.employeeListOperator().length;
                for (var i = 0; i < empCodeLength; i++) {
                    for (var j = 0; j < empListLength; j++) {
                        let employee = self.employeeListOperator()[j];
                        if (employee.code == self.selectedEmployeeCodeOperator()[i]) {
                            self.listEmployeeIdOperator.push(employee.id);
                        }
                    }
                }
            }
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.ALL) {
                let empListLength = self.employeeList().length;
                for (var j = 0; j < empListLength; j++) {
                    let employee = self.employeeList()[j];
                    self.listEmployeeIdOperator.push(employee.id);
                }
            }

        }

        //E 
        getlogTypeName() {
            var self = this;
            for (var i = 0; i < self.itemList().length; i++) {
                let temp = self.itemList()[i];
                if (temp.code == self.logTypeSelectedCode()) {
                    self.logTypeSelectedName(temp.name);
                }
            }
        }
        getDateOperator() {
            var self = this;
            self.dateOperator(self.startDateOperator() + " ~ " + self.endDateOperator());
        }
        getOperatorNumber() {
            var self = this;
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                self.operatorNumber(nts.uk.text.format(nts.uk.resource.getText("CLI003_57"), self.listEmployeeIdOperator().length));
            }
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.ALL) {
                self.operatorNumber(getText('CLI003_18'));
            }
        }
        getTargetDate() {
            var self = this;
            self.dateCtoE = self.dateValue().startDate + " ~ " + self.dateValue().endDate;
        }
        getTargetNumber() {
            var self = this;
            if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.SPECIFY) {
                self.targetNumber(nts.uk.text.format(nts.uk.resource.getText("CLI003_57"), self.selectedEmployeeCodeTarget().length));
            }
            if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.ALL) {
                self.targetNumber(getText('CLI003_18'));
            }
        }

        //F
        initComponentScreenF() {
            var self = this

            //F igGrid
            self.itemsIgGrid = ko.observableArray([]);
            self.columnsIgGrid = ko.observableArray([]);
            self.supColumnsIgGrid = ko.observableArray([]);
            self.listLogBasicInforModel = [];
            self.isDisplayText = ko.observable(false);
            let recordType = Number(self.logTypeSelectedCode());

            // set param log
            let paramLog = {
                listTagetEmployeeId: self.targetEmployeeIdList(),
                listOperatorEmployeeId: self.listEmployeeIdOperator(),
                startDateTaget: moment(self.dateValue().startDate, "YYYY/MM/DD").toISOString(),
                endDateTaget: moment(self.dateValue().endDate, "YYYY/MM/DD").toISOString(),
                startDateOperator: moment(self.startDateOperator(), "YYYY/MM/DD H:mm:ss").toISOString(),
                endDateOperator: moment(self.endDateOperator(), "YYYY/MM/DD H:mm:ss").toISOString(),
                recordType: self.logTypeSelectedCode()
            };
            // set param for get parent header name
            let paramOutputItem = {
                recordType: self.logTypeSelectedCode()
            };
            let checkProcess = false;
            switch (recordType) {
                case RECORD_TYPE.LOGIN: {
                    paramOutputItem.itemNos = self.columnsHeaderLogRecord();
                    checkProcess = true;
                    break
                }
                case RECORD_TYPE.START_UP: {
                    paramOutputItem.itemNos = self.columnsHeaderLogStartUp();
                    checkProcess = true;
                    break;
                }
                case RECORD_TYPE.UPDATE_PERSION_INFO: {
                    paramOutputItem.itemNos = self.columnsHeaderLogPersionInfo();
                    checkProcess = true;
                    break
                }
                case RECORD_TYPE.DATA_CORRECT: {
                    paramOutputItem.itemNos = self.columnsHeaderLogDataCorrect();
                    checkProcess = true;
                    break;
                }
                default: {
                    break;
                }
            }
            if (checkProcess) {
                // get log out put items
                service.getLogOutputItemsByRecordTypeItemNos(paramOutputItem).done(function(dataOutputItems: Array<any>) {
                    if (dataOutputItems.length > 0) {
                        // Get Log basic infor
                        service.getLogBasicInfoByModifyDate(paramLog).done(function(data: Array<LogBasicInfoModel>) {

                            if (data.length > 0) {
                                // generate columns header parent
                                self.setListColumnHeaderLog(recordType, dataOutputItems);
                                let countLog = 1;
                                if (data.length > 1000) {
                                    self.isDisplayText(true);
                                }
                                // process sub header with record type = persion infro and data correct
                                _.forEach(data, function(logBasicInfoModel) {
                                    if (countLog <= 1000) {
                                        let logtemp = "";
                                        if (recordType == RECORD_TYPE.LOGIN || recordType == RECORD_TYPE.START_UP) {
                                            self.listLogBasicInforModel.push(logBasicInfoModel);
                                        }
                                        if (recordType == RECORD_TYPE.UPDATE_PERSION_INFO) {
                                            logtemp = self.getSubHeaderPersionInfo(logBasicInfoModel);
                                            self.listLogBasicInforModel.push(logtemp);
                                        }
                                        if (recordType == RECORD_TYPE.DATA_CORRECT) {
                                            logtemp = self.getSubHeaderDataCorect(logBasicInfoModel);
                                            self.listLogBasicInforModel.push(logtemp);
                                        }
                                        countLog++;
                                    } else {
                                        return false;
                                    }
                                });
                                // Generate table
                                if (recordType == RECORD_TYPE.DATA_CORRECT) {
                                    self.generateDataCorrectLogGrid();
                                }else if(recordType == RECORD_TYPE.UPDATE_PERSION_INFO){
                                     self.generatePersionInforGrid(); 
                                } else {
                                    self.generateIgGrid();
                                }

                            } else {
                                alertError({ messageId: "Msg_1220" }).then(function() {
                                    self.previousScreenE();
                                    nts.uk.ui.block.clear();
                                });
                            }
                        }).fail(function(error) {
                            alertError(error);
                        });

                    } else {
                        alertError({ messageId: "Msg_1221" }).then(function() {
                            self.previousScreenE();
                            nts.uk.ui.block.clear();
                        });
                        
                    }

                }).fail(function(error) {
                    alertError(error);
                });
            }
        }

        getSubHeaderDataCorect(logBasicInfoModel: LogBasicInfoModel) {
            let tempList = logBasicInfoModel.lstLogOutputItemDto;
            var subColumHeaderTemp: IgGridColumnModel[] = [];
            _.forEach(logBasicInfoModel.lstLogOutputItemDto, function(logOutputItemDto) {
                // generate columns header chidrent
                switch (logOutputItemDto.itemNo) {
                    case ITEM_NO.ITEM_NO22:
                    case ITEM_NO.ITEM_NO23:
                    case ITEM_NO.ITEM_NO24: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "targetDate", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO26: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "correctionAttr", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO27: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "itemName", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO30: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "valueBefore", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO31: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "valueAfter", "string", false));
                        break;
                    }
                }
            });
            logBasicInfoModel.subColumnsHeaders = subColumHeaderTemp;
            return logBasicInfoModel;
        }

        getSubHeaderPersionInfo(logBasicInfoModel: LogBasicInfoModel) {
            let tempList = logBasicInfoModel.lstLogOutputItemDto;
            var subColumHeaderTemp: IgGridColumnModel[] = [];
            _.forEach(logBasicInfoModel.lstLogOutputItemDto, function(logOutputItemDto) {
                // generate columns header chidrent
                switch (logOutputItemDto.itemNo) {
                    case ITEM_NO.ITEM_NO23: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "categoryName", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO99: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "targetDate", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO24: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "infoOperateAttr", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO29: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "itemName", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO31: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "valueBefore", "string", false));
                        break;
                    }
                    case ITEM_NO.ITEM_NO33: {
                        subColumHeaderTemp.push(new IgGridColumnModel(logOutputItemDto.itemName, "valueAfter", "string", false));
                        break;
                    }
                }
            });
            logBasicInfoModel.subColumnsHeaders = subColumHeaderTemp;
            return logBasicInfoModel;
        }

        generateIgGrid() {
            var self = this;
            $("#igGridLog").igGrid({
                width: '100%',
                height: '400',
                features: [
                    {
                        name: "Paging",
                        type: "local",
                        pageSize: 10
                    },
                    {
                        name: "Sorting",
                        type: "local"
                    },
                    {
                        name: "Resizing",
                        deferredResizing: false,
                        allowDoubleClickToResize: true
                    },
                    {
                        name: "Filtering",
                        type: "local",
                        filterDropDownItemIcons: false,
                        filterDropDownWidth: 200
                    }
                ],
                rowVirtualization:true,
                dataSource: self.listLogBasicInforModel,
                columns: self.columnsIgGrid()
            });
        }
        generatePersionInforGrid() {
            var self = this;
            let listLogBasicInfor = self.listLogBasicInforModel;
            //generate generateHierarchialGrid
            $("#igGridLog").igHierarchicalGrid({
                width: "100%",
                height: '400',
                dataSource: listLogBasicInfor,
                features: [
                    {
                        name: "Responsive",
                        enableVerticalRendering: false
                    },
                    {
                        name: "Resizing",
                        deferredResizing: false,
                        allowDoubleClickToResize: true
                    },
                    {
                        name: "Sorting",
                        inherit: true
                    },
                    {
                        name: "Paging",
                        pageSize: 10,
                        type: "local",
                        inherit: true
                    },
                    {
                        name: "Filtering",
                        type: "local",
                        filterDropDownItemIcons: false,
                        filterDropDownWidth: 200
                    }
                ],
                autoGenerateColumns: false,
                primaryKey: "operationId",
                columns: self.columnsIgGrid(),
                autoGenerateLayouts: false,
                columnLayouts: [
                    {
                        width: "100%",
                        childrenDataProperty: "lstLogPerCateCorrectRecordDto",
                        autoGenerateColumns: false,
                        primaryKey: "targetDate",
                        foreignKey: "operationId",
                        columns: [
                            { key: "categoryName", headerText: "categoryName", dataType: "string", width: "20%" },
                            { key: "targetDate", headerText: "targetDate", dataType: "string", width: "15%" },
                            { key: "itemName", headerText: "itemName", dataType: "string", width: "15%" },
                            { key: "infoOperateAttr", headerText: "infoOperateAttr", dataType: "string", width: "10%" },
                            { key: "valueBefore", headerText: "valueBefore", dataType: "string", width: "20%" },
                            { key: "valueAfter", headerText: "valueAfter", dataType: "string", width: "20%" }
                           
                        ],
                        features: [
                            {
                                name: "Responsive",
                                enableVerticalRendering: false,
                                columnSettings: []
                            },
                            {
                                name: "Resizing",
                                deferredResizing: false,
                                allowDoubleClickToResize: true
                            },
                            {
                                name: "Paging",
                                pageSize: 20,
                                type: "local",
                                inherit: true
                            }
                        ]
                    }
                ],
            });


            $(document).delegate("#igGridLog", "igchildgridcreated", function(evt, ui) {
                var headerSetting = $(ui.element).data("headersetting");
                var header = ui.element.find("th[role='columnheader']");
                for (var i = 0; i < headerSetting.length; i++) {
                    var currentSetting = headerSetting[i];
                    header.filter("th[aria-label='" + currentSetting.key + "']")
                        .find(".ui-iggrid-headertext").text(currentSetting.headerText);
                }
            });

            $(document).delegate("#igGridLog", "igchildgridcreating", function(evt, ui) {
                evt;
                var childSource = ui.options.dataSource;
                var ds = $("#igGridLog").igGrid("option", "dataSource");
                var parentSource = _.isArray(ds) ? ds : ds._data;
                var headerSetting = [];
                var newSource = [];
                for (var i = 0; i < parentSource.length; i++) {
                    if (parentSource[i].operationId === childSource[0].operationId) {
                        headerSetting = parentSource[i].subColumnsHeaders;
                        newSource = _.cloneDeep(parentSource[i].lstLogPerCateCorrectRecordDto);
                    }
                }
                ui.options.dataSource = newSource;
                $(ui.element).data("headersetting", headerSetting);
            });
        }
        generateDataCorrectLogGrid() {
            var self = this;
            let listLogBasicInfor = self.listLogBasicInforModel;
            //generate generateHierarchialGrid
            $("#igGridLog").igHierarchicalGrid({
                width: "100%",
                dataSource: listLogBasicInfor,
                features: [
                    {
                        name: "Responsive",
                        enableVerticalRendering: false
                    }, 
                    {
                        name: "Resizing",
                        deferredResizing: false,
                        allowDoubleClickToResize: true
                    },
                    {
                        name: "Sorting",
                        inherit: true
                    },
                    {
                        name: "Paging",
                        pageSize: 10,
                        type: "local",
                        inherit: true
                    },
                    {
                        name: "Filtering",
                        type: "local",
                        filterDropDownItemIcons: false,
                        filterDropDownWidth: 200
                    }
                ],
                autoGenerateColumns: false,
                primaryKey: "operationId",
                columns: self.columnsIgGrid(),
                autoGenerateLayouts: false,
                columnLayouts: [
                    {
                        width: "100%",
                        childrenDataProperty: "lstLogDataCorrectRecordRefeDto",
                        autoGenerateColumns: false,
                        primaryKey: "targetDate",
                        foreignKey: "operationId",
                        columns: [
                            { key: "targetDate", headerText: "targetDate", dataType: "string", width: "20%" },
                            { key: "itemName", headerText: "itemName", dataType: "string", width: "20%" },
                            { key: "valueBefore", headerText: "valueBefore", dataType: "string", width: "20%" },
                            { key: "valueAfter", headerText: "valueAfter", dataType: "string", width: "20%" },
                            { key: "correctionAttr", headerText: "correctionAttr", dataType: "string", width: "20%" }
                        ],
                        features: [
                            {
                                name: "Responsive",
                                enableVerticalRendering: false,
                                columnSettings: []
                            },
                            {
                                name: "Resizing",
                                deferredResizing: false,
                                allowDoubleClickToResize: true
                            },
                            {
                                name: "Paging",
                                pageSize: 20,
                                type: "local",
                                inherit: true
                            }
                        ]
                    }
                ],
            });


            $(document).delegate("#igGridLog", "igchildgridcreated", function(evt, ui) {
                var headerSetting = $(ui.element).data("headersetting");
                var header = ui.element.find("th[role='columnheader']");
                for (var i = 0; i < headerSetting.length; i++) {
                    var currentSetting = headerSetting[i];
                    header.filter("th[aria-label='" + currentSetting.key + "']")
                        .find(".ui-iggrid-headertext").text(currentSetting.headerText);
                }
            });

            $(document).delegate("#igGridLog", "igchildgridcreating", function(evt, ui) {
                evt;
                var childSource = ui.options.dataSource;
                var ds = $("#igGridLog").igGrid("option", "dataSource");
                var parentSource = _.isArray(ds) ? ds : ds._data;
                var headerSetting = [];
                var newSource = [];
                for (var i = 0; i < parentSource.length; i++) {
                    if (parentSource[i].operationId === childSource[0].operationId) {
                        headerSetting = parentSource[i].subColumnsHeaders;
                        newSource = _.cloneDeep(parentSource[i].lstLogDataCorrectRecordRefeDto);
                    }
                }
                ui.options.dataSource = newSource;
                $(ui.element).data("headersetting", headerSetting);
            });
        }

        setListColumnHeaderLog(recordType: number, listOutputItem: Array<any>) {
            var self = this;
            self.columnsIgGrid.push(new IgGridColumnModel("primarykey", "operationId", "string", true));
            switch (recordType) {
                case RECORD_TYPE.LOGIN: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "modifyDateTime", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "loginStatus", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "methodName", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO22: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "note", "string", false));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                case RECORD_TYPE.START_UP: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "modifyDateTime", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO18: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "note", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "menuName", "string", false));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                case RECORD_TYPE.UPDATE_PERSION_INFO: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "modifyDateTime", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameTaget", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO21: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeTaget", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO22: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "processAttr", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO36: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "note", "string", false));
                                break;
                            }
                            // add sub list
                            case ITEM_NO.ITEM_NO23: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "categoryName", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO99: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "targetDate", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO24: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "infoOperateAttr", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO29: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "itemName", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO31: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "valueBefore", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO33: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "valueAfter", "string", false));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                    }
                case RECORD_TYPE.DATA_CORRECT: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeLogin", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "modifyDateTime", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "userNameTaget", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO21: {
                                self.columnsIgGrid.push(new IgGridColumnModel(item.itemName, "employeeCodeTaget", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO22:
                            case ITEM_NO.ITEM_NO23:
                            case ITEM_NO.ITEM_NO24: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "targetDate", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO26: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "correctionAttr", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO27: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "itemName", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO30: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "valueBefore", "string", false));
                                break;
                            }
                            case ITEM_NO.ITEM_NO31: {
                                self.supColumnsIgGrid.push(new IgGridColumnModel(item.itemName, "valueAfter", "string", false));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                default: {
                    break;
                }
            }
        }
        exportCsvF() {
            let self = this;
            self.logBasicInforCsv = [];
            let recordType = Number(self.logTypeSelectedCode());
            _.forEach(self.listLogBasicInforModel, function(logBaseInfo) {
                let lstDataCorrect: DataCorrectLogModel[] = [];
                let lstPerCorrect: PerCateCorrectRecordModel[] = [];

                switch (recordType) {

                    case RECORD_TYPE.UPDATE_PERSION_INFO: {
                        //setting list persion correct
                        _.forEach(logBaseInfo.lstLogPerCateCorrectRecordDto, function(persionCorrect) {
                            lstPerCorrect.push(new PerCateCorrectRecordModel(persionCorrect.operationId, moment.utc(persionCorrect.targetDate, 'YYYY/MM/DD'), persionCorrect.categoryName, persionCorrect.itemName, persionCorrect.valueBefore, persionCorrect.valueAfter, persionCorrect.infoOperateAttr))
                        });
                        break;
                    }
                    case RECORD_TYPE.DATA_CORRECT: {
                        //setting list data correct
                        _.forEach(logBaseInfo.lstLogDataCorrectRecordRefeDto, function(dataCorrect) {
                            lstDataCorrect.push(new DataCorrectLogModel(dataCorrect.operationId, moment.utc(dataCorrect.targetDate, 'YYYY/MM/DD'), dataCorrect.targetDataType, dataCorrect.itemName, dataCorrect.valueBefore, dataCorrect.valueAfter, dataCorrect.remarks, dataCorrect.correctionAttr))
                        });
                        break;
                    }
                    default: {
                        break;
                    }
                }

                let logBaseInfoTemp: LogBasicInfoModel = new LogBasicInfoModel(logBaseInfo.userNameLogin, logBaseInfo.employeeCodeLogin, logBaseInfo.userIdTaget, logBaseInfo.userNameTaget, logBaseInfo.employeeCodeTaget, logBaseInfo.modifyDateTime,
                    logBaseInfo.processAttr,lstDataCorrect, logBaseInfo.lstLogLoginDto, logBaseInfo.lstLogOutputItemDto, logBaseInfo.menuName, logBaseInfo.note, logBaseInfo.methodName, logBaseInfo.loginStatus,
                    lstPerCorrect);
                self.logBasicInforCsv.push(logBaseInfoTemp);
            });

            let params = {
                recordType: Number(self.logTypeSelectedCode()),
                lstLogBasicInfoDto: self.logBasicInforCsv,
                lstHeaderDto: self.columnsIgGrid(),
                lstSupHeaderDto: self.supColumnsIgGrid()
            };
            service.logSettingExportCsv(params).done(() => {
                console.log("Export success");
            });
        }

        checkDestroyIgGrid() {
            let self = this;
            let recordType = Number(self.logTypeSelectedCode());
            if (recordType == RECORD_TYPE.DATA_CORRECT ||
                recordType == RECORD_TYPE.UPDATE_PERSION_INFO) {
                //generate table data correct
                if ($('#igGridLog_container').length > 0 || $('#igGridLog_employeeCodeTaget').length > 0) {
                    $("#igGridLog").igHierarchicalGrid("destroy");
                }
            } else {
                if ($('#igGridLog_container').length > 0) {
                    $("#igGridLog").igGrid("destroy");
                }
            }
        }
        /**
        * start button screen C
        */
        backScreenCtoB() {
            var self = this;

            if (self.validateForm()) {
                self.previous();
                $("#ccgcomponent").hide();
                $('#list-box_b').focus();
                self.scrollToLeftTop();
            }

        }
        nextScreenCtoD() {
            var self = this;
            self.setEmployeeListTarget();
            self.getlogTypeName();
            if (self.validateForm()) {
                if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.SPECIFY) {
                    if (self.selectedEmployeeCodeTarget().length > 0) {
                        self.next();
                    } else {
                        alertError( {messageId: 'Msg_1216', messageParams: getText('CLI003_16')});
                    }
                } else if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.ALL) {
                    self.next();
                }
                if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                    $("#ccgcomponent").show();
                    $("#employeeSearchD").show();
                } else {
                    $("#ccgcomponent").hide();
                    $("#employeeSearchD").hide();
                }
            }

            $("#D1_4 .ntsDatepicker").first().attr("tabindex", -1).focus();
            self.scrollToLeftTop();
        }
        //and button screen C

        //B
        nextScreenCD() {
            let self = this;
            //present date time C   
            let checkLogType = parseInt(self.logTypeSelectedCode());
            switch (checkLogType) {
                case RECORD_TYPE.LOGIN:
                case RECORD_TYPE.START_UP:
                case RECORD_TYPE.UPDATE_MASTER:
                case RECORD_TYPE.TERMINAL_COMMUNICATION_INFO: {
                    $("#ex_accept_wizard-t-1").parents("li").hide();
                    $("#ex_accept_wizard").ntsWizard("goto", 2);
                    if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                        $("#ccgcomponent").show();
                        $("#employeeSearchD").show();
                    } else {
                        $("#ccgcomponent").hide();
                        $("#employeeSearchD").hide();
                    }
                    $("#D1_4 .ntsDatepicker").first().attr("tabindex", -1).focus();
                    self.scrollToLeftTop();
                    break;
                }
                case RECORD_TYPE.UPDATE_PERSION_INFO:
                case RECORD_TYPE.DATA_REFERENCE:
                case RECORD_TYPE.DATA_MANIPULATION:
                case RECORD_TYPE.DATA_CORRECT:
                case RECORD_TYPE.MY_NUMBER: {
                    $("#ex_accept_wizard").ntsWizard("goto", 1);
                    $("#ccgcomponent").show();
                    $("#C1_4 .ntsStartDatePicker").focus();

                    self.scrollToLeftTop();
                    break;
                }
                default: {
                    //                    console.log('11');
                    //                    
                    //                    $('#list-box_b_grid_container').trigger("valie");    
                    //                    self.checkLogType = 11;
   
                    break;
                }
            }

        }


        backScreenD() {
            var self = this;
            self.previous();
            if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                $("#ccgcomponent").show();
                $("#employeeSearchD").show();
            } else {
                $("#ccgcomponent").hide();
                $("#employeeSearchD").hide();
            }
            $("#D1_4 .ntsDatepicker").first().attr("tabindex", -1).focus();
            self.scrollToLeftTop();
        }
        /**
        * Open screen F
        */
        nextScreenF() {
            var self = this;
            // init compnent screen F
            self.initComponentScreenF();
            self.checkDestroyIgGrid();
            self.next();
        }
        /**
        * Open screen I
        */
        nextScreenI() {
            var self = this;
            var paramtranfer = ko.observable(self.logTypeSelectedCode());
            nts.uk.ui.windows.setShared("recordType", paramtranfer);
            $('#contents-area').focus();
            //I igGrid
            self.itemsIgGrid = ko.observableArray([]);
            self.columnsIgAllGrid = ko.observableArray([]);
            self.supColumnsIgGrid = ko.observableArray([]);
            self.listLogBasicInforModel = [];

            // varriable for export file CSV
            // set param log
            let paramLog = {
                // recordType=0,1 k co taget
                listTagetEmployeeId: self.targetEmployeeIdList(),
                listOperatorEmployeeId: self.listEmployeeIdOperator(),
                startDateTaget: moment(self.dateValue().startDate, "YYYY/MM/DD").toISOString(),
                endDateTaget: moment(self.dateValue().endDate, "YYYY/MM/DD").toISOString(),
                startDateOperator: moment(self.startDateOperator(), "YYYY/MM/DD H:mm:ss").toISOString(),
                endDateOperator: moment(self.endDateOperator(), "YYYY/MM/DD H:mm:ss").toISOString(),
                recordType: self.logTypeSelectedCode()
            };
            //fix itemNo
            let paramOutputItem = {
                //   itemNos: self.columnsHeaderLogDataCorrect(),
                recordType: self.logTypeSelectedCode()
            }
            //set itemNo
//            switch (parseInt(self.logTypeSelectedCode())) {
//                case RECORD_TYPE.LOGIN: {
//                    paramOutputItem.itemNos = self.columnsHeaderLogRecord();
//                    break
//                }
//                case RECORD_TYPE.START_UP: {
//                    paramOutputItem.itemNos = self.columnsHeaderLogStartUp();
//                    break;
//                }
//                case RECORD_TYPE.UPDATE_PERSION_INFO: {
//                    paramOutputItem.itemNos = self.columnsHeaderLogPersionInfo;
//                    break
//                }
//                case RECORD_TYPE.DATA_CORRECT: {
//                    paramOutputItem.itemNos = self.columnsHeaderLogDataCorrect();
//                    break;
//                }
//                default: {
//                    break;
//                }
//            }

            nts.uk.ui.windows.sub.modal("/view/cli/003/i/index.xhtml").onClosed(() => {
                let dataSelect = nts.uk.ui.windows.getShared("datacli003");
                // function get logdisplaysetting by code
                 self.listItemNo = ko.observableArray([]);
                 self.listLogBasicInforAllModel = [];
                service.getLogDisplaySettingByCodeAndFlag(dataSelect.toString()).done(function(dataLogDisplaySetting: Array<any>) {
                    if (dataLogDisplaySetting) {
                        // function get logoutputItem by recordType and itemNo 
                        let dataOutPutItem=dataLogDisplaySetting.logSetOutputItems;
                     if( dataOutPutItem.length>0){
                         _.forEach(dataOutPutItem,function(dataItemNo:any){
                             self.listItemNo.push(dataItemNo.itemNo);
                             });
                         paramOutputItem.itemNos=self.listItemNo();
  //                       console.log('giatriItem:'self.listItemNo());
                         }
                        service.getLogOutputItemsByRecordTypeItemNosAll(paramOutputItem).done(function(dataOutputItems: Array<any>) {
                            if (dataOutputItems && dataOutputItems.length > 0) {

                                // Get Log basic infor
                                service.getLogBasicInfoDataByModifyDate(paramLog).done(function(data: Array<LogBasicInforAllModel>) {
                                    // generate columns header 
                                    self.setListColumnHeaderLogScreenI(Number(self.logTypeSelectedCode()), dataOutputItems);                                 
                                    if(data && data.length>0){
                                        self.listLogBasicInforAllModel=data;                                       
                                         // export file csv
                                        self.exportCsvI();
                                        }else{
                                             alertError({ messageId: "Msg_1220" });
                                        }
                                    
                                }).fail(function(error) {
                                    alertError(error);
                                });
                            } else {
                                alertError({ messageId: "Msg_1221" });
                            }

                        }).fail(function(error) {
                            alertError(error);
                        });
                    } else {
                        alertError({ messageId: "Msg_1215" });
                    }
                }).fail(function(error) {
                    alertError(error);
                })

            });
        }
        
        setListColumnHeaderLogScreenI(recordType: number, listOutputItem: Array<any>) {
            var self = this;
          //  self.columnsIgAllGrid.push(new IgGridColumnAllModel("primarykey", "operationId", "string",0));
            switch (recordType) {
                case RECORD_TYPE.LOGIN: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO1: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdLogin", "string",ITEM_NO.ITEM_NO1));
                                break;
                            }
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameLogin", "string",ITEM_NO.ITEM_NO2));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeLogin", "string", ITEM_NO.ITEM_NO3));
                                break;
                            }
                             case ITEM_NO.ITEM_NO4: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "ipAddress", "string", ITEM_NO.ITEM_NO4));
                                break;
                            }
                            case ITEM_NO.ITEM_NO5: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "pcName", "string", ITEM_NO.ITEM_NO5));
                                break;
                            }
                            case ITEM_NO.ITEM_NO6: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "account", "string", ITEM_NO.ITEM_NO6));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "modifyDateTime", "string", ITEM_NO.ITEM_NO7));
                                break;
                            }
                            case ITEM_NO.ITEM_NO8: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employmentAuthorityName", "string", ITEM_NO.ITEM_NO8));
                                break;
                            }
                            case ITEM_NO.ITEM_NO9: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "salarytAuthorityName", "string", ITEM_NO.ITEM_NO9));
                                break;
                            }
                            case ITEM_NO.ITEM_NO10: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personelAuthorityName", "string", ITEM_NO.ITEM_NO10));
                                break;
                            }                           
                            case ITEM_NO.ITEM_NO11: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "officeHelperAuthorityName", "string", ITEM_NO.ITEM_NO11));
                                break;
                            }
                             case ITEM_NO.ITEM_NO12: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "accountAuthorityName", "string", ITEM_NO.ITEM_NO12));
                                break;
                            }
                           case ITEM_NO.ITEM_NO13: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "myNumberAuthorityName", "string", ITEM_NO.ITEM_NO13));
                                break;
                            }
                           case ITEM_NO.ITEM_NO14: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "groupCompanyAddminAuthorityName", "string", ITEM_NO.ITEM_NO14));
                                break;
                            }
                           case ITEM_NO.ITEM_NO15: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "companyAddminAuthorityName", "string", ITEM_NO.ITEM_NO15));
                                break;
                            }
                           case ITEM_NO.ITEM_NO16: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "systemAdminAuthorityName", "string", ITEM_NO.ITEM_NO16));
                                break;
                            }
                           case ITEM_NO.ITEM_NO17: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personalInfoAuthorityName", "string", ITEM_NO.ITEM_NO17));
                                break;
                            }
                            case ITEM_NO.ITEM_NO18: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "menuName", "string", ITEM_NO.ITEM_NO18));
                                break;
                            }                         
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "loginStatus", "string", ITEM_NO.ITEM_NO19));
                                break;
                            }
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "loginMethod", "string", ITEM_NO.ITEM_NO20));
                                break;
                            }
                            case ITEM_NO.ITEM_NO21: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "accessResourceUrl", "string", ITEM_NO.ITEM_NO21));
                                break;
                            }   
                            case ITEM_NO.ITEM_NO22: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "note", "string", ITEM_NO.ITEM_NO22));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                case RECORD_TYPE.START_UP: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                              case ITEM_NO.ITEM_NO1: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdLogin", "string",ITEM_NO.ITEM_NO1));
                                break;
                            }
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameLogin", "string",ITEM_NO.ITEM_NO2));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeLogin", "string", ITEM_NO.ITEM_NO3));
                                break;
                            }
                             case ITEM_NO.ITEM_NO4: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "ipAddress", "string", ITEM_NO.ITEM_NO4));
                                break;
                            }
                            case ITEM_NO.ITEM_NO5: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "pcName", "string", ITEM_NO.ITEM_NO5));
                                break;
                            }
                            case ITEM_NO.ITEM_NO6: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "account", "string", ITEM_NO.ITEM_NO6));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "modifyDateTime", "string", ITEM_NO.ITEM_NO7));
                                break;
                            }
                            case ITEM_NO.ITEM_NO8: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employmentAuthorityName", "string", ITEM_NO.ITEM_NO8));
                                break;
                            }
                            case ITEM_NO.ITEM_NO9: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "salarytAuthorityName", "string", ITEM_NO.ITEM_NO9));
                                break;
                            }
                            case ITEM_NO.ITEM_NO10: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personelAuthorityName", "string", ITEM_NO.ITEM_NO10));
                                break;
                            }                           
                            case ITEM_NO.ITEM_NO11: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "officeHelperAuthorityName", "string", ITEM_NO.ITEM_NO11));
                                break;
                            }
                             case ITEM_NO.ITEM_NO12: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "accountAuthorityName", "string", ITEM_NO.ITEM_NO12));
                                break;
                            }
                           case ITEM_NO.ITEM_NO13: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "myNumberAuthorityName", "string", ITEM_NO.ITEM_NO13));
                                break;
                            }
                           case ITEM_NO.ITEM_NO14: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "groupCompanyAddminAuthorityName", "string", ITEM_NO.ITEM_NO14));
                                break;
                            }
                           case ITEM_NO.ITEM_NO15: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "companyAddminAuthorityName", "string", ITEM_NO.ITEM_NO15));
                                break;
                            }
                           case ITEM_NO.ITEM_NO16: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "systemAdminAuthorityName", "string", ITEM_NO.ITEM_NO16));
                                break;
                            }
                           case ITEM_NO.ITEM_NO17: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personalInfoAuthorityName", "string", ITEM_NO.ITEM_NO17));
                                break;
                            }
                            case ITEM_NO.ITEM_NO18: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "note", "string", ITEM_NO.ITEM_NO18));
                                break;
                            }
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "menuName", "string", ITEM_NO.ITEM_NO19));
                                break;
                            }
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "menuNameReSource", "string", ITEM_NO.ITEM_NO20));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                case RECORD_TYPE.UPDATE_PERSION_INFO: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO1: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdLogin", "string",ITEM_NO.ITEM_NO1));
                                break;
                            }
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameLogin", "string",ITEM_NO.ITEM_NO2));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeLogin", "string", ITEM_NO.ITEM_NO3));
                                break;
                            }
                             case ITEM_NO.ITEM_NO4: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "ipAddress", "string", ITEM_NO.ITEM_NO4));
                                break;
                            }
                            case ITEM_NO.ITEM_NO5: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "pcName", "string", ITEM_NO.ITEM_NO5));
                                break;
                            }
                            case ITEM_NO.ITEM_NO6: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "account", "string", ITEM_NO.ITEM_NO6));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "modifyDateTime", "string", ITEM_NO.ITEM_NO7));
                                break;
                            }
                            case ITEM_NO.ITEM_NO8: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employmentAuthorityName", "string", ITEM_NO.ITEM_NO8));
                                break;
                            }
                            case ITEM_NO.ITEM_NO9: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "salarytAuthorityName", "string", ITEM_NO.ITEM_NO9));
                                break;
                            }
                            case ITEM_NO.ITEM_NO10: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personelAuthorityName", "string", ITEM_NO.ITEM_NO10));
                                break;
                            }                           
                            case ITEM_NO.ITEM_NO11: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "officeHelperAuthorityName", "string", ITEM_NO.ITEM_NO11));
                                break;
                            }
                             case ITEM_NO.ITEM_NO12: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "accountAuthorityName", "string", ITEM_NO.ITEM_NO12));
                                break;
                            }
                           case ITEM_NO.ITEM_NO13: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "myNumberAuthorityName", "string", ITEM_NO.ITEM_NO13));
                                break;
                            }
                           case ITEM_NO.ITEM_NO14: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "groupCompanyAddminAuthorityName", "string", ITEM_NO.ITEM_NO14));
                                break;
                            }
                           case ITEM_NO.ITEM_NO15: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "companyAddminAuthorityName", "string", ITEM_NO.ITEM_NO15));
                                break;
                            }
                           case ITEM_NO.ITEM_NO16: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "systemAdminAuthorityName", "string", ITEM_NO.ITEM_NO16));
                                break;
                            }
                           case ITEM_NO.ITEM_NO17: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personalInfoAuthorityName", "string", ITEM_NO.ITEM_NO17));
                                break;
                            }
                             case ITEM_NO.ITEM_NO18: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "menuName", "string", ITEM_NO.ITEM_NO18));
                                break;
                            }
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdTaget", "string", ITEM_NO.ITEM_NO19));
                                break;
                            }       
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameTaget", "string", ITEM_NO.ITEM_NO20));
                                break;
                            }
                            case ITEM_NO.ITEM_NO21: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeTaget", "string", ITEM_NO.ITEM_NO21));
                                break;
                            }
                         
                            case ITEM_NO.ITEM_NO22: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "categoryProcess", "string", ITEM_NO.ITEM_NO22));
                                break;
                            }
                             case ITEM_NO.ITEM_NO23: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "categoryName", "string", ITEM_NO.ITEM_NO23));
                                break;
                            }
                              case ITEM_NO.ITEM_NO24: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "methodCorrection", "string", ITEM_NO.ITEM_NO24));
                                break;
                            }
                             case ITEM_NO.ITEM_NO25: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetYmd", "string", ITEM_NO.ITEM_NO25));
                                break;
                            }
                            case ITEM_NO.ITEM_NO26: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetYm", "string", ITEM_NO.ITEM_NO26));
                                break;
                            }                         
                            case ITEM_NO.ITEM_NO27: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetY", "string", ITEM_NO.ITEM_NO27));
                                break;
                            }
                            case ITEM_NO.ITEM_NO28: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "keyString", "string", ITEM_NO.ITEM_NO28));
                                break;
                            }
                            case ITEM_NO.ITEM_NO29: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemName", "string", ITEM_NO.ITEM_NO29));
                                break;
                            }
                            case ITEM_NO.ITEM_NO30: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemvalueBefor", "string", ITEM_NO.ITEM_NO30));
                                break;
                            }
                            case ITEM_NO.ITEM_NO31: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemContentValueBefor", "string", ITEM_NO.ITEM_NO31));
                                break;
                            }
                            case ITEM_NO.ITEM_NO32: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemvalueAppter", "string", ITEM_NO.ITEM_NO32));
                                break;
                            }
                            case ITEM_NO.ITEM_NO33: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemContentValueAppter", "string", ITEM_NO.ITEM_NO33));
                                break;
                            }
                            case ITEM_NO.ITEM_NO34: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemEditAddition", "string", ITEM_NO.ITEM_NO34));
                                break;
                            }
                            case ITEM_NO.ITEM_NO35: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetYmdEditAddition", "string", ITEM_NO.ITEM_NO35));
                                break;
                            }   
                             case ITEM_NO.ITEM_NO36: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "note", "string", ITEM_NO.ITEM_NO.ITEM_NO36));
                                break;
                            }
                            
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                    }
                case RECORD_TYPE.DATA_CORRECT: {
                    _.forEach(listOutputItem, function(item) {
                        switch (item.itemNo) {
                            case ITEM_NO.ITEM_NO1: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdLogin", "string",ITEM_NO.ITEM_NO1));
                                break;
                            }
                            case ITEM_NO.ITEM_NO2: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameLogin", "string",ITEM_NO.ITEM_NO2));
                                break;
                            }
                            case ITEM_NO.ITEM_NO3: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeLogin", "string", ITEM_NO.ITEM_NO3));
                                break;
                            }
                             case ITEM_NO.ITEM_NO4: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "ipAddress", "string", ITEM_NO.ITEM_NO4));
                                break;
                            }
                            case ITEM_NO.ITEM_NO5: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "pcName", "string", ITEM_NO.ITEM_NO5));
                                break;
                            }
                            case ITEM_NO.ITEM_NO6: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "account", "string", ITEM_NO.ITEM_NO6));
                                break;
                            }
                            case ITEM_NO.ITEM_NO7: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "modifyDateTime", "string", ITEM_NO.ITEM_NO7));
                                break;
                            }
                            case ITEM_NO.ITEM_NO8: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employmentAuthorityName", "string", ITEM_NO.ITEM_NO8));
                                break;
                            }
                            case ITEM_NO.ITEM_NO9: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "salarytAuthorityName", "string", ITEM_NO.ITEM_NO9));
                                break;
                            }
                            case ITEM_NO.ITEM_NO10: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personelAuthorityName", "string", ITEM_NO.ITEM_NO10));
                                break;
                            }                           
                            case ITEM_NO.ITEM_NO11: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "officeHelperAuthorityName", "string", ITEM_NO.ITEM_NO11));
                                break;
                            }
                             case ITEM_NO.ITEM_NO12: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "accountAuthorityName", "string", ITEM_NO.ITEM_NO12));
                                break;
                            }
                           case ITEM_NO.ITEM_NO13: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "myNumberAuthorityName", "string", ITEM_NO.ITEM_NO13));
                                break;
                            }
                           case ITEM_NO.ITEM_NO14: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "groupCompanyAddminAuthorityName", "string", ITEM_NO.ITEM_NO14));
                                break;
                            }
                           case ITEM_NO.ITEM_NO15: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "companyAddminAuthorityName", "string", ITEM_NO.ITEM_NO15));
                                break;
                           case ITEM_NO.ITEM_NO16: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "systemAdminAuthorityName", "string", ITEM_NO.ITEM_NO16));
                                break;
                            }
                           case ITEM_NO.ITEM_NO17: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "personalInfoAuthorityName", "string", ITEM_NO.ITEM_NO17));
                                break;
                            }
                            case ITEM_NO.ITEM_NO18: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "menuName", "string", ITEM_NO.ITEM_NO18));
                                break;
                            }
                            case ITEM_NO.ITEM_NO19: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userIdTaget", "string", ITEM_NO.ITEM_NO19));
                                break;
                            }       
                            case ITEM_NO.ITEM_NO20: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "userNameTaget", "string", ITEM_NO.ITEM_NO20));
                                break;
                            }
                            case ITEM_NO.ITEM_NO21: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "employeeCodeTaget", "string", ITEM_NO.ITEM_NO21));
                                break;
                            }                        
                            case ITEM_NO.ITEM_NO22: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetYmd", "string", ITEM_NO.ITEM_NO22));
                                break;
                            }
                            case ITEM_NO.ITEM_NO23: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetYm", "string", ITEM_NO.ITEM_NO23));
                                break;
                            }
                            case ITEM_NO.ITEM_NO24: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "tarGetY", "string", ITEM_NO.ITEM_NO24));
                                break;
                            }
                            case ITEM_NO.ITEM_NO25: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "catagoryCorection", "string", ITEM_NO.ITEM_NO25));
                                break;
                            }
                            case ITEM_NO.ITEM_NO26: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "keyString", "string", ITEM_NO.ITEM_NO26));
                                break;
                            }
                            case ITEM_NO.ITEM_NO27: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemName", "string", ITEM_NO.ITEM_NO27));
                                break;
                            }
                             case ITEM_NO.ITEM_NO28: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemvalueBefor", "string", ITEM_NO.ITEM_NO28));
                                break;
                            }
                             case ITEM_NO.ITEM_NO29: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemvalueAppter", "string", ITEM_NO.ITEM_NO29));
                                break;
                            }
                            case ITEM_NO.ITEM_NO30: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemContentValueBefor", "string", ITEM_NO.ITEM_NO30));
                                break;
                            }
                            case ITEM_NO.ITEM_NO31: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "itemContentValueAppter", "string", ITEM_NO.ITEM_NO31));
                                break;
                            }
                            case ITEM_NO.ITEM_NO32: {
                                self.columnsIgAllGrid.push(new IgGridColumnAllModel(item.itemName, "note", "string", ITEM_NO.ITEM_NO32));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                    break;
                }
                default: {
                    break;
                }
            }
        }
       // export 
        exportCsvI() {
            let self = this;
            self.logBasicInforCsv = [];
            let recordType = Number(self.logTypeSelectedCode());
//            _.forEach(self.listLogBasicInforModel, function(logBaseInfo) {
//                let lstDataCorrect: DataCorrectLogModel[] = [];
//                let lstPerCorrect: PerCateCorrectRecordModel[] = [];
//
//                switch (recordType) {
//
//                    case RECORD_TYPE.UPDATE_PERSION_INFO: {
//                        //setting list persion correct
//                        _.forEach(logBaseInfo.lstLogPerCateCorrectRecordDto, function(persionCorrect) {
//                            lstPerCorrect.push(new PerCateCorrectRecordModel(persionCorrect.operationId, moment.utc(persionCorrect.targetDate, 'YYYY/MM/DD'), persionCorrect.categoryName, persionCorrect.itemName, persionCorrect.valueBefore, persionCorrect.valueAfter, persionCorrect.infoOperateAttr))
//                        });
//                        break;
//                    }
//                    case RECORD_TYPE.DATA_CORRECT: {
//                        //setting list data correct
//                        _.forEach(logBaseInfo.lstLogDataCorrectRecordRefeDto, function(dataCorrect) {
//                            lstDataCorrect.push(new DataCorrectLogModel(dataCorrect.operationId, moment.utc(dataCorrect.targetDate, 'YYYY/MM/DD'), dataCorrect.targetDataType, dataCorrect.itemName, dataCorrect.valueBefore, dataCorrect.valueAfter, dataCorrect.remarks, dataCorrect.correctionAttr))
//                        });
//                        break;
//                    }
//                    default: {
//                        break;
//                    }
//                }
//
//                let logBaseInfoTemp: LogBasicInfoModel = new LogBasicInfoModel(logBaseInfo.userNameLogin, logBaseInfo.employeeCodeLogin, logBaseInfo.userIdTaget, logBaseInfo.userNameTaget, logBaseInfo.employeeCodeTaget, logBaseInfo.modifyDateTime,
//                    logBaseInfo.processAttr,lstDataCorrect, logBaseInfo.lstLogLoginDto, logBaseInfo.lstLogOutputItemDto, logBaseInfo.menuName, logBaseInfo.note, logBaseInfo.methodName, logBaseInfo.loginStatus,
//                    lstPerCorrect);
//                self.logBasicInforCsv.push(logBaseInfoTemp);
//            });

            let params = {
                recordType: Number(self.logTypeSelectedCode()),
                listLogBasicInfoAllDto:self.listLogBasicInforAllModel,
                lstHeaderDto: self.columnsIgAllGrid()             
            };
            console.log('listHeader:'+self.columnsIgAllGrid());
            service.logSettingExportCsvScreenI(params).done(() => {
                console.log("Export success screeni");
            });
        }
        //D
        backScreenDtoBC() {
            var self = this;
            //back to Screen B
            if (self.validateForm()) {
                switch (Number(self.logTypeSelectedCode())) {
                    case RECORD_TYPE.LOGIN:
                    case RECORD_TYPE.START_UP:
                    case RECORD_TYPE.UPDATE_MASTER:
                    case RECORD_TYPE.TERMINAL_COMMUNICATION_INFO: {
                        $("#ccgcomponent").hide();
                        $("#ex_accept_wizard-t-1").parents("li").show();
                        $("#ex_accept_wizard").ntsWizard("goto", 0);
                        $('#list-box_b').focus();
                        self.scrollToLeftTop();
                        break;
                    }
                    // back to Screen C
                    case RECORD_TYPE.UPDATE_PERSION_INFO:
                    case RECORD_TYPE.DATA_REFERENCE:
                    case RECORD_TYPE.DATA_MANIPULATION:
                    case RECORD_TYPE.DATA_CORRECT:
                    case RECORD_TYPE.MY_NUMBER: {
                        self.previous();
                        if (self.selectedRuleCode() == EMPLOYEE_SPECIFIC.SPECIFY) {
                            $("#ccgcomponent").show();
                            $("#employeeSearch").show();
                        } else {
                            $("#ccgcomponent").hide();
                            $("#employeeSearch").hide();
                        }
                        $(".ntsStartDate").focus();
                        self.scrollToLeftTop();
                        $("#C1_4 .ntsStartDatePicker").focus();
                        break;
                    }
                }
            }


        }

        nextScreenDtoE() {
            let self = this;
            self.getListEmployeeIdOperator();
            self.getlogTypeName();
            self.getDateOperator();
            self.getOperatorNumber();
            self.getTargetNumber();
            self.getTargetDate();
            
            //Check to display or not Screen C infomation.
            self.isDisplayTarget(false);


            switch (Number(self.logTypeSelectedCode())) {
                case RECORD_TYPE.UPDATE_PERSION_INFO:
                case RECORD_TYPE.DATA_REFERENCE:
                case RECORD_TYPE.DATA_MANIPULATION:
                case RECORD_TYPE.DATA_CORRECT:
                case RECORD_TYPE.MY_NUMBER: {
                    self.isDisplayTarget(true);
                    break;
                }
            }
            if (self.validateForm()) {
                if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.SPECIFY) {
                    if (self.selectedEmployeeCodeOperator().length > 0) {
                        $("#ccgcomponent").hide();
                        self.next();
                    }
                    else {
                        alertError({messageId: 'Msg_1216', messageParams:getText('CLI003_23')});
                    }
                }
                if (self.selectedRuleCodeOperator() == EMPLOYEE_SPECIFIC.ALL) {
                    $("#ccgcomponent").hide();
                    self.next();
                }
            }
            $('#E2_3').focus();
            self.scrollToLeftTop();
        }
        previousScreenE() {
            var self = this;
            self.previous();
            $('#E2_3').focus();
            self.checkDestroyIgGrid();
            self.scrollToLeftTop();
        }
        next() {
            $('#ex_accept_wizard').ntsWizard("next");
        }

        previous() {
            $('#ex_accept_wizard').ntsWizard("prev");
        }
        private scrollToLeftTop(): void {
            $("#contents-area").scrollLeft(0);
            $("#contents-area").scrollTop(0);
        }
        public switchCodeChange(): void {
            let self = this;
            self.selectedRuleCode.subscribe((newCode) => {
                if (newCode == EMPLOYEE_SPECIFIC.ALL) {
                    $("#ccgcomponent").hide();
                    $("#employeeSearch").hide();
                } else {
                    $("#ccgcomponent").show();
                    $("#employeeSearch").show();
                }
            });
            self.selectedRuleCodeOperator.subscribe((newCode) => {
                if (newCode == EMPLOYEE_SPECIFIC.ALL) {
                    $("#ccgcomponent").hide();
                    $("#employeeSearchD").hide();
                } else {
                    $("#ccgcomponent").show();
                    $("#employeeSearchD").show();
                }
            });

        }
    }



    class ItemModel {
        code: number;
        name: string;
        description: string;
        constructor(code: number, name: string, description: string) {
            this.code = code;
            this.name = name;
            this.description = description;
        }
    }

    class LogBasicInfoModel {
        operationId: string;
        userNameLogin: string;
        employeeCodeLogin: string;
        userIdTaget: string;
        userNameTaget: string;
        employeeCodeTaget: string;
        modifyDateTime: string;
        menuName: string;
        note: string;
        methodName: string;
        loginStatus: string;
        processAttr: string;
        lstLogDataCorrectRecordRefeDto: KnockoutObservableArray<DataCorrectLogModel>;
        lstLogLoginDto: KnockoutObservableArray<LoginLogModel>;
        lstLogOutputItemDto: KnockoutObservableArray<LogOutputItemDto>;
        subColumnsHeaders: KnockoutObservableArray<IgGridColumnModel>;
        lstLogPerCateCorrectRecordDto: KnockoutObservableArray<PerCateCorrectRecordModel>;
        constructor(userNameLogin: string, employeeCodeLogin: string, userIdTaget: string, userNameTaget: string, employeeCodeTaget: any, modifyDateTime: any,
            processAttr : string,lstLogDataCorrectRecordRefeDto: KnockoutObservableArray<DataCorrectLogModel>, lstLogLoginDto: KnockoutObservableArray<LoginLogModel>,
            lstLogOutputItemDto: KnockoutObservableArray<LogOutputItemDto>, menuName: string, note: string, methodName: string, loginStatus: string,
            lstLogPerCateCorrectRecordDto: KnockoutObservableArray<PerCateCorrectRecordModel>) {
            this.userNameLogin = userNameLogin;
            this.employeeCodeLogin = employeeCodeLogin;
            this.userIdTaget = userIdTaget;
            this.userNameTaget = userNameTaget;
            this.employeeCodeTaget = employeeCodeTaget;
            this.modifyDateTime = modifyDateTime;
            this.processAttr = processAttr;
            this.lstLogDataCorrectRecordRefeDto = lstLogDataCorrectRecordRefeDto;
            this.lstLogLoginDto = lstLogLoginDto;
            this.lstLogOutputItemDto = lstLogOutputItemDto;
            this.menuName = menuName;
            this.note = note;
            this.methodName = methodName;
            this.loginStatus = loginStatus;
            this.lstLogPerCateCorrectRecordDto = lstLogPerCateCorrectRecordDto;
        }
    }
    
     class LogBasicInforAllModel {
        operationId: string;
        userNameLogin: string;
        employeeCodeLogin: string;
        userIdTaget: string;
        userNameTaget: string;
        employeeIdTaget: string;
        employeeCodeTaget: string;
        ipAddress: string; 
        modifyDateTime: string;
        processAttr: string; 
        menuName: string;
        note: string;
        menuNameReSource: string;
        methodName: string;
        loginStatus: string;
        userIdLogin: string;
        pcName: string;
        account: string;
        employmentAuthorityName: string;
        salarytAuthorityName: string;
        personelAuthorityName: string;
        officeHelperAuthorityName: string;
        accountAuthorityName: string;
        myNumberAuthorityName: string;
        groupCompanyAddminAuthorityName:string;
        companyAddminAuthorityName:string;
        systemAdminAuthorityName:string;
        personalInfoAuthorityName:string;
        accessResourceUrl:string;
              constructor(userNameLogin: string,employeeCodeLogin: string,userIdTaget: string,  userNameTaget: string,
                    employeeIdTaget: string,employeeCodeTaget: string,ipAddress: string,modifyDateTime: string,
                    processAttr: string,  menuName: string,note: string, menuNameReSource: string, methodName: string,
                    loginStatus: string, userIdLogin: string,   pcName: string,    account: string, 
                    employmentAuthorityName: string,salarytAuthorityName: string,personelAuthorityName: string,
                    officeHelperAuthorityName: string,accountAuthorityName: string,myNumberAuthorityName: string,
                    groupCompanyAddminAuthorityName:string,
                    companyAddminAuthorityName:string,
                    systemAdminAuthorityName:string,
                    personalInfoAuthorityName:string,
                    accessResourceUrl:string) {
                        this.userNameLogin = userNameLogin;
                        this.employeeCodeLogin = employeeCodeLogin;
                        this.userIdTaget = userIdTaget;
                        this.userNameTaget = userNameTaget;
                        this.employeeIdTaget=employeeIdTaget;
                        this.employeeCodeTaget = employeeCodeTaget;
                        this.ipAddress=ipAddress;
                        this.modifyDateTime = modifyDateTime;
                        this.processAttr = processAttr;
                        this.menuName = menuName;
                        this.note = note;
                        this.menuNameReSource;
                        this.methodName = methodName;
                        this.loginStatus = loginStatus;
                        this.userIdLogin=userIdLogin;
                        this.pcName=pcName;
                        this.account=account;
                        this.employmentAuthorityName=employmentAuthorityName;
                        this.salarytAuthorityName=salarytAuthorityName;
                        this.personelAuthorityName=personelAuthorityName;
                        this.officeHelperAuthorityName=officeHelperAuthorityName;
                        this.accountAuthorityName=accountAuthorityName;
                        this.myNumberAuthorityName=myNumberAuthorityName;
                        this.groupCompanyAddminAuthorityName=groupCompanyAddminAuthorityName;
                        this.companyAddminAuthorityName=companyAddminAuthorityName;
                        this.systemAdminAuthorityName=systemAdminAuthorityName;
                        this.personalInfoAuthorityName=personalInfoAuthorityName;
                        this.accessResourceUrl=accessResourceUrl;
                               
                    }
    }

    class DataCorrectLogModel {
        operationId: string;
        targetDate: any;
        targetDataType: number;
        itemName: string;
        valueBefore: string;
        valueAfter: string;
        remarks: string;
        correctionAttr: string;
        constructor(operationId: string, targetDate: any, targetDataType: number, itemName: string, valueBefore: string, valueAfter: string,
            remarks: string, correctionAttr: string) {
            this.operationId = operationId;
            this.targetDate = targetDate;
            this.targetDataType = targetDataType;
            this.itemName = itemName;
            this.valueBefore = valueBefore;
            this.valueAfter = valueAfter;
            this.remarks = remarks;
            this.correctionAttr = correctionAttr;
        }
    }
    class PerCateCorrectRecordModel {
        operationId: string;
        targetDate: any;
        categoryName: string;
        itemName: string;
        valueBefore: string;
        valueAfter: string;
        infoOperateAttr: string;
        constructor(operationId: string, targetDate: any, categoryName: string, itemName: string, valueBefore: string, valueAfter: string,
            infoOperateAttr: string) {
            this.operationId = operationId;
            this.targetDate = targetDate;
            this.itemName = itemName;
            this.valueBefore = valueBefore;
            this.valueAfter = valueAfter;
            this.infoOperateAttr = infoOperateAttr;
        }
    }
    class ItemDataModel {
        loginEmpCode: string;
        loginEmpName: string;
        loginTime: string;
        method: string;
        status: string;
        remarks: string;
        constructor(loginEmpCode: string, loginEmpName: string, loginTime: string, method: string,
            status: string, remarks: string) {
            this.loginEmpCode = loginEmpCode;
            this.loginEmpName = loginEmpName;
            this.loginTime = loginTime;
            this.method = method;
            this.status = status;
            this.remarks = remarks;
        }
    }

    class LogOutputItemDto {
        itemNo: number;
        itemName: string;
        recordType: number;
        sortOrder: number;
        parentKey: string;
        constructor(itemNo: number, itemName: string, recordType: number, sortOrder: number, parentKey: string) {
            this.itemNo = itemNo;
            this.itemName = itemName;
            this.recordType = recordType;
            this.sortOrder = sortOrder;
            this.parentKey = parentKey;
        }
    }

    class IgGridColumnModel {
        headerText: string;
        key: string;
        dataType: string;
        hidden: boolean;
        // setting for using export csv
        itemName: string;
        constructor(headerText: string, key: string, dataType: string, hidden: boolean) {
            this.headerText = headerText;
            this.key = key;
            this.dataType = dataType;
            this.hidden = hidden;
            this.itemName = headerText;
        }
    }
    class IgGridColumnAllModel {
        headerText: string;
        key: string;
        dataType: string;
        itemNo:number;     
        
        // setting for using export csv
        itemName: string;
        constructor(headerText: string, key: string, dataType: string,itemNo:number) {
            this.headerText = headerText;
            this.key = key;
            this.dataType = dataType;
            this.itemNo=itemNo;         
            this.itemName = headerText;
        }
    }

    /**
    * The enum of RECORD_TYPE
    */
    export enum RECORD_TYPE {
        LOGIN = 0,
        START_UP = 1,
        UPDATE_MASTER = 2,
        UPDATE_PERSION_INFO = 3,
        DATA_REFERENCE = 4,
        DATA_MANIPULATION = 5,
        DATA_CORRECT = 6,
        MY_NUMBER = 7,
        TERMINAL_COMMUNICATION_INFO = 8

    }

    /**
* The enum of RECORD_TYPE
*/
    export enum ITEM_NO {
        ITEM_NO1 = 1,
        ITEM_NO2 = 2,
        ITEM_NO3 = 3,
        ITEM_NO4 = 4,
        ITEM_NO5 = 5,
        ITEM_NO6 = 6,
        ITEM_NO7 = 7,
        ITEM_NO8 = 8,
        ITEM_NO9 = 9,
        ITEM_NO10 = 10,
        ITEM_NO11 = 11,
        ITEM_NO12 = 12,
        ITEM_NO13 = 13,
        ITEM_NO14 = 14,
        ITEM_NO15 = 15,
        ITEM_NO16 = 16,
        ITEM_NO17 = 17,
        ITEM_NO18 = 18,
        ITEM_NO19 = 19,
        ITEM_NO20 = 20,
        ITEM_NO21 = 21,
        ITEM_NO22 = 22,
        ITEM_NO23 = 23,
        ITEM_NO24 = 24,
        ITEM_NO25 = 25,
        ITEM_NO26 = 26,
        ITEM_NO27 = 27,
        ITEM_NO28 = 28,
        ITEM_NO29 = 29,
        ITEM_NO30 = 30,
        ITEM_NO31 = 31,
        ITEM_NO32 = 32,
        ITEM_NO33 = 33,
        ITEM_NO34 = 34,
        ITEM_NO35 = 35,
        ITEM_NO36 = 36,
        ITEM_NO99 = 99
    }

    /*C
    *the enum of EMPLOYEE_SPECIFIC
    */
    export enum EMPLOYEE_SPECIFIC {
        SPECIFY = 1,
        ALL = 2
    }

    export class UnitModel {
        id: string;
        code: string;
        name: string;
        workplaceName: string;

        constructor(id: string, code: string, name: string, workplaceName: string) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.workplaceName = workplaceName;
        }
    }
}


