module nts.uk.pr.view.qsi001.a.viewmodel {
    import setShared = nts.uk.ui.windows.setShared;
    import errors = nts.uk.ui.errors;
    import block = nts.uk.ui.block;
    import dialog = nts.uk.ui.dialog;
    import getText = nts.uk.resource.getText;

    export class ScreenModel {
        ccg001ComponentOption: GroupOption;
        employeeInputList: KnockoutObservableArray<EmployeeModel>;

        //kcp009
        systemReference: KnockoutObservable<number>;
        isDisplayOrganizationName: KnockoutObservable<boolean>;
        targetBtnText: string;

        listComponentOption: ComponentOption;
        selectedItem: KnockoutObservable<string>;
        tabindex: number;
        //
        //switch
        simpleValue: KnockoutObservable<string>;
        employees: any;
        selectedRuleCode: any;
        //datepicker
        baseDate: KnockoutObservable<moment.Moment>;
        baseDate1: KnockoutObservable<moment.Moment>;
        startDate: KnockoutObservable<moment.Moment>;
        endDate: KnockoutObservable<moment.Moment>;
        japanStartDate: KnockoutObservable<string>;
        japanEndDate: KnockoutObservable<string>;
        japanBaseDate: KnockoutObservable<string>;
        //combobox
        itemList: KnockoutObservableArray<ItemModel>;
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        //grid
        columns: KnockoutObservableArray<any>;
        items: KnockoutObservableArray<ItemModelGrid>;
        currentCodeList: KnockoutObservableArray<any>;
        listEmployee: KnockoutObservableArray<any>;

        socialInsurNotiCrSet : KnockoutObservable<SocialInsurNotiCrSet> = ko.observable(new SocialInsurNotiCrSet({
            officeInformation: 0,
            printPersonNumber: 0,
            businessArrSymbol: 0,
            outputOrder: 0,
            submittedName: 0,
            insuredNumber: 0,
            fdNumber: '',
            textPersonNumber: 0,
            outputFormat: 0,
            lineFeedCode: 0,
            historyId: '',
            selection: 0,
            availableAtr: 0,
            numValue: null,
            charValue: null,
            timeValue: 0,
            targetAtr: 0
        }));

        //switch submiit name
        switchSubmittedName : KnockoutObservable<number> = ko.observable(0);

        // combobox
        officeInformations: KnockoutObservableArray<ItemModel> = ko.observableArray(getBusinessDivision());
        businessArrSymbols: KnockoutObservableArray<ItemModel> = ko.observableArray(getBussEsimateClass());
        outputOrders: KnockoutObservableArray<ItemModel> = ko.observableArray(getSocialInsurOutOrder());
        printPersonNumbers: KnockoutObservableArray<ItemModel> = ko.observableArray(getPersonalNumClass());
        submittedNames: KnockoutObservableArray<ItemModel> = ko.observableArray(getSubNameClass());
        insuredNumbers: KnockoutObservableArray<ItemModel> = ko.observableArray(getInsurPersonNumDivision());
        textPersonNumbers: KnockoutObservableArray<ItemModel> = ko.observableArray(getTextPerNumberClass());
        outputFormats: KnockoutObservableArray<ItemModel> = ko.observableArray(getOutputFormatClass());
        lineFeedCodes: KnockoutObservableArray<ItemModel> = ko.observableArray(getLineFeedCode());


        //kcp005

        listComponentOptionKCP005: any;
        selectedCodeKCP005: KnockoutObservable<string>  = ko.observable('');
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection : KnockoutObservable<boolean>;

        employeeListKCP005: KnockoutObservableArray<UnitModel>;

        constructor() {
            block.invisible();
            let self = this;

            //init switch
            self.simpleValue = ko.observable("123");
            self.selectedRuleCode = ko.observable(1);
            let end  = new Date();
            let start = new Date();
            start.setMonth(start.getMonth() - 1);
            start.setDate(start.getDate() + 1);
            let mmStart = start.getMonth() + 1;
            let dStart = start.getDate();
            let mmEnd = end.getMonth() + 1;
            let dEnd = end.getDate();
            let yyyyS = start.getFullYear();
            let yyyyE = end.getFullYear();
            //init datepicker
            self.startDate = ko.observable(yyyyS + "/" +  mmStart + "/" + dStart);
            self.startDate.subscribe(e => {
                //self.japanStartDate = ko.observable(nts.uk.time.dateInJapanEmpire(moment.utc(self.startDate()).format("YYYYMMDD")).toString());
                self.japanStartDate("("+nts.uk.time.dateInJapanEmpire(moment.utc(self.startDate()).format("YYYYMMDD")).toString()+")");
            });


            self.japanStartDate = ko.observable("("+nts.uk.time.dateInJapanEmpire(moment.utc(self.startDate()).format("YYYYMMDD")).toString()+")");
            self.endDate = ko.observable(yyyyE + "/" +  mmEnd + "/" + dEnd);
            self.endDate.subscribe(e => {
                self.japanEndDate("("+ nts.uk.time.dateInJapanEmpire(moment.utc(self.endDate()).format("YYYYMMDD")).toString()+")");
            });
            self.japanEndDate = ko.observable("("+nts.uk.time.dateInJapanEmpire(moment.utc(self.endDate()).format("YYYYMMDD")).toString()+")");


            self.baseDate1 = ko.observable(yyyyE + "/" +  mmEnd + "/" + dEnd);

            self.baseDate1.subscribe(e => {
                self.japanBaseDate("("+nts.uk.time.dateInJapanEmpire(moment.utc(self.baseDate1()).format("YYYYMMDD")).toString()+")");
            });
            self.japanBaseDate = ko.observable("("+nts.uk.time.dateInJapanEmpire(moment.utc(self.baseDate1()).format("YYYYMMDD")).toString()+")");






            //init combobox
            self.itemList = ko.observableArray([
                new ItemModel('1', '?????????'),
                new ItemModel('2', '????????????'),
                new ItemModel('3', '???????????????????????????????????????????????????????????????')
            ]);

            self.isEnable = ko.observable(true);
            self.isEditable = ko.observable(true);
            //grid

            this.columns = ko.observableArray([
                {headerText: '?????????', key: 'id', width: 100, hidden: true},
                {headerText: '??????', key: 'code', width: 150},
                {headerText: '??????', key: 'businessName', width: 150},
                {headerText: '??????1', key: 'workplaceName', width: 150}
            ]);

            self.items = ko.observableArray([]);

            self.currentCodeList = ko.observableArray([]);
            self.listEmployee = ko.observableArray([]);
            self.switchSubmittedName = ko.observable(0);
            self.loadCCG001();
            self.loadKCP005();
            block.clear();
            self.initScreen();

        }

        getListEmployee(empCode: Array, listEmp: Array){
            let listEmployee: any = [];
            _.each(empCode, (item) =>{
                let emp = _.find(listEmp, function(itemEmp) { return itemEmp.employeeCode === item; });
                listEmployee.push(emp);
            });
            return listEmployee;
        }

        getStyle(){
            let self = this;
            return self.japanStartDate().length > 13 ?  "width:140px; display: inline-block;" : "width:140px; display:inline";
        }

        openScreenB() {
            let self = this;

            setShared('QSI001_PARAMS_TO_SCREEN_B', {
                listEmpId: self.getListEmployee(self.selectedCodeKCP005(),self.employees),
                date: moment.utc(self.baseDate1(), "YYYY/MM/DD"),
                startDate: moment.utc(self.startDate(), "YYYY/MM/DD"),
                endDate: moment.utc(self.endDate(), "YYYY/MM/DD")
            });

            nts.uk.ui.windows.sub.modal("/view/qsi/001/b/index.xhtml").onClosed(() => {

            });
        }
        //KCP005
        loadKCP005(){
            let self = this;
            self.baseDate = ko.observable(new Date());
            self.multiSelectedCode = ko.observableArray(['0', '1', '4']);
            self.isShowAlreadySet = ko.observable(false);
            self.alreadySettingList = ko.observableArray([
                {code: '1', isAlreadySetting: true},
                {code: '2', isAlreadySetting: true}
            ]);
            self.isDialog = ko.observable(true);
            self.isShowNoSelectRow = ko.observable(false);
            self.isMultiSelect = ko.observable(true);
            self.isShowWorkPlaceName = ko.observable(true);
            self.isShowSelectAllButton = ko.observable(false);
            self.disableSelection = ko.observable(false);

            self.employeeListKCP005 = self.listEmployee;
            self.listComponentOptionKCP005 = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: self.isMultiSelect(),
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeListKCP005,
                selectType: SelectType.SELECT_ALL,
                selectedCode: self.selectedCodeKCP005,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton(),
                disableSelection : self.disableSelection(),
                showOptionalColumn: false,
                maxRows: 18,
                optionalColumnName: nts.uk.resource.getText('KSM005_18'),
                optionalColumnDatasource: ko.observableArray([]),
            };

            $('#kcp005').ntsListComponent(self.listComponentOptionKCP005);
        }
        /* CCG001 */
        loadCCG001() {
            let self = this;
            self.ccg001ComponentOption = {
                /** Common properties */
                systemType: 1,
                showEmployeeSelection: false,
                showQuickSearchTab: true,
                showAdvancedSearchTab: true,
                showBaseDate: true,
                showClosure: false,
                showAllClosure: false,
                showPeriod: false,
                periodFormatYM: false,
                tabindex: 9,
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
                showSameWorkplace: false,
                showSameWorkplaceAndChild: false,

                /** Advanced search properties */
                showEmployment: true,
                showWorkplace: true,
                showClassification: true,
                showDepartment: true,
                showJobTitle: true,
                showWorktype: false,
                isMutipleCheck: true,
                /**
                 * Self-defined function: Return data from CCG001
                 * @param: data: the data return from CCG001
                 */
                returnDataFromCcg001: function (data: Ccg001ReturnedData) {
                    self.createGridList(data.listEmployee);
                    self.employees = data.listEmployee;
                    self.loadKCP005();

                }
            }

            $('#com-ccg001').ntsGroupComponent(self.ccg001ComponentOption);

        }

        createEmployeeModel(data) {
            let self = this;
            let listEmployee = [];
            _.each(data, data => {
                listEmployee.push({
                    id: data.employeeId,
                    code: data.employeeCode,
                    businessName: data.employeeName,
                    workplaceName: data.workplaceName
                });

            });

            return listEmployee;
        }

        createGridList(data) {
            let self = this;
            self.listEmployee([]);
            _.each(data, data => {
                self.listEmployee.push(new UnitModel(data.employeeCode, data.employeeName, data.workplaceName,false));
            });

        }

        validate(){
            errors.clearAll();
            $("#A2_4").trigger("validate");
            $("#A2_7").trigger("validate");
            $("#A4_4").trigger("validate");
            return errors.hasError();
        }

        exportFileExcel(): void {
            let self = this;
            if(self.validate()){
                return;
            }
            let employList = self.getListEmpId(self.selectedCodeKCP005(), self.employees);
            let data: any = {
                socialInsurNotiCreateSetQuery: {
                    officeInformation: self.socialInsurNotiCrSet().officeInformation(),
                    businessArrSymbol: self.socialInsurNotiCrSet().businessArrSymbol(),
                    outputOrder: self.socialInsurNotiCrSet().outputOrder(),
                    printPersonNumber: self.socialInsurNotiCrSet().printPersonNumber(),
                    submittedName: self.socialInsurNotiCrSet().submittedName(),
                    insuredNumber: self.socialInsurNotiCrSet().insuredNumber(),
                    fdNumber: self.socialInsurNotiCrSet().fdNumber(),
                    textPersonNumber: self.socialInsurNotiCrSet().textPersonNumber(),
                    outputFormat: self.socialInsurNotiCrSet().outputFormat(),
                    lineFeedCode: self.socialInsurNotiCrSet().lineFeedCode()
                },
                empIds: employList,
                startDate: moment.utc(self.startDate(), "YYYY/MM/DD"),
                endDate: moment.utc(self.endDate(), "YYYY/MM/DD"),
                baseDate: moment.utc(self.baseDate1(), "YYYY/MM/DD")
            };
            nts.uk.ui.block.grayout();
            service.exportFile(data).done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError(error);
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }


        exportFileCsv(): void {
            let self = this;
            let employList = self.getListEmpId(self.selectedCodeKCP005(), self.employees);
            if(employList.length == 0) {
                dialog.alertError({ messageId: 'Msg_37' });
                return;
            }
            let data: any = {
                socialInsurNotiCreateSetQuery: {
                    officeInformation: self.socialInsurNotiCrSet().officeInformation(),
                    printPersonNumber: self.socialInsurNotiCrSet().printPersonNumber(),
                    businessArrSymbol: self.socialInsurNotiCrSet().businessArrSymbol(),
                    outputOrder: self.socialInsurNotiCrSet().outputOrder(),
                    submittedName: self.socialInsurNotiCrSet().submittedName(),
                    insuredNumber: self.socialInsurNotiCrSet().insuredNumber(),
                    fdNumber: self.socialInsurNotiCrSet().fdNumber().trim() == '' ? null : self.socialInsurNotiCrSet().fdNumber(),
                    textPersonNumber: self.socialInsurNotiCrSet().textPersonNumber(),
                    outputFormat: self.socialInsurNotiCrSet().outputFormat(),
                    lineFeedCode: self.socialInsurNotiCrSet().lineFeedCode()
                },
                typeExport : 1,
                empIds: employList,
                startDate: moment.utc(self.startDate(), "YYYY/MM/DD"),
                endDate: moment.utc(self.endDate(), "YYYY/MM/DD"),
                baseDate: moment.utc(self.baseDate1(), "YYYY/MM/DD")
            };
            nts.uk.ui.block.grayout();
            service.exportFile(data).done(function() {
            }).fail(function(error) {
                nts.uk.ui.dialog.alertError(error);
            }).always(function() {
                nts.uk.ui.block.clear();
            });
        }

        getListEmpId(empCode: Array, listEmp: Array){
            let listEmpId =[];
            _.each(empCode, (item) =>{
                let emp = _.find(listEmp, function(itemEmp) { return itemEmp.employeeCode == item; });
                listEmpId.push(emp.employeeId);
            });
            return listEmpId;
        }


        initScreen() {
            let self = this;
            service.getDataInitScreen().done((data: INotifiOfInsurQuaAcDto) => {
                self.socialInsurNotiCrSet().officeInformation(data.officeInformation);
                self.socialInsurNotiCrSet().printPersonNumber(data.printPersonNumber);
                self.socialInsurNotiCrSet().businessArrSymbol(data.businessArrSymbol);
                self.socialInsurNotiCrSet().outputOrder(data.outputOrder);
                self.socialInsurNotiCrSet().submittedName(data.submittedName);
                self.socialInsurNotiCrSet().insuredNumber(data.insuredNumber);
                self.socialInsurNotiCrSet().fdNumber(data.fdNumber == null ? "0" : data.fdNumber);
                self.socialInsurNotiCrSet().textPersonNumber(data ? data.textPersonNumber : null);
                self.socialInsurNotiCrSet().outputFormat(data ? data.outputFormat : null);
                self.socialInsurNotiCrSet().lineFeedCode(data ? data.lineFeedCode : null);

            }).fail(error => {
                dialog.alertError(error);
            });
        }

    }

    // Note: Defining these interfaces are optional
    export interface GroupOption {
        /** Common properties */
        showEmployeeSelection?: boolean; // ???????????????
        systemType: number; // ??????????????????
        showQuickSearchTab?: boolean; // ??????????????????
        showAdvancedSearchTab?: boolean; // ????????????
        showBaseDate?: boolean; // ???????????????
        showClosure?: boolean; // ?????????????????????
        showAllClosure?: boolean; // ???????????????
        showPeriod?: boolean; // ??????????????????
        periodFormatYM?: boolean; // ??????????????????
        maxPeriodRange?: string; // ????????????
        showSort?: boolean; // ???????????????
        nameType?: number; // ???????????????

        /** Required parameter */
        baseDate?: any; // ????????? KnockoutObservable<string> or string
        periodStartDate?: any; // ????????????????????? KnockoutObservable<string> or string
        periodEndDate?: any; // ????????????????????? KnockoutObservable<string> or string
        dateRangePickerValue?: KnockoutObservable<any>;
        inService: boolean; // ????????????
        leaveOfAbsence: boolean; // ????????????
        closed: boolean; // ????????????
        retirement: boolean; // ????????????

        /** Quick search tab options */
        showAllReferableEmployee?: boolean; // ??????????????????????????????
        showOnlyMe?: boolean; // ????????????
        showSameDepartment?: boolean; //?????????????????????
        showSameDepartmentAndChild?: boolean; // ????????????????????????????????????
        showSameWorkplace?: boolean; // ?????????????????????
        showSameWorkplaceAndChild?: boolean; // ????????????????????????????????????

        /** Advanced search properties */
        showEmployment?: boolean; // ????????????
        showDepartment?: boolean; // ????????????
        showWorkplace?: boolean; // ????????????
        showClassification?: boolean; // ????????????
        showJobTitle?: boolean; // ????????????
        showWorktype?: boolean; // ????????????
        isMutipleCheck?: boolean; // ???????????????

        /** Optional properties */
        isInDialog?: boolean;
        showOnStart?: boolean;
        isTab2Lazy?: boolean;
        tabindex?: number;

        /** Data returned */
        returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
    }

    export interface EmployeeSearchDto {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        affiliationId: string; // departmentId or workplaceId based on system type
        affiliationName: string; // departmentName or workplaceName based on system type
    }

    export interface Ccg001ReturnedData {
        baseDate: string; // ?????????
        closureId?: number; // ??????ID
        periodStart: string; // ?????????????????????)
        periodEnd: string; // ????????????????????????
        listEmployee: Array<EmployeeSearchDto>; // ????????????
    }

    export interface ComponentOption {
        systemReference: SystemType;
        isDisplayOrganizationName: boolean;
        employeeInputList: KnockoutObservableArray<EmployeeModel>;
        targetBtnText: string;
        selectedItem: KnockoutObservable<string>;
        tabIndex: number;
        baseDate?: KnockoutObservable<Date>;
    }

    export interface EmployeeModel {
        id: string;
        code: string;
        businessName: string;
        depName?: string;
        workplaceName?: string;
    }

    export interface INotifiOfInsurQuaAcDto {
        /**
         * ???????????????
         */
        officeInformation: number;

        /**
         * ?????????????????????
         */
        businessArrSymbol: number;

        /**
         * ?????????
         */
        outputOrder: number;
        /**
         * ??????????????????
         */
        printPersonNumber: number;
        /**
         * ??????????????????
         */
        submittedName: number;

        /**
         * ????????????????????????
         */
        insuredNumber: number;

        /**
         * FD??????
         */
        fdNumber: string;

        /**
         * ????????????????????????
         */
        textPersonNumber: number;

        /**
         * ????????????
         */
        outputFormat: number;

        /**
         * ???????????????
         */
        lineFeedCode: number;

        // param value
        /**
         * ??????ID
         */
        historyId: string;

        /**
         * ?????????
         */
        selection: number;

        /**
         * ????????????
         */
        availableAtr: number;

        /**
         * ???????????????
         */
        numValue: string;

        /**
         * ???????????????
         */
        charValue: string;

        /**
         * ???????????????
         */
        timeValue: number;

        /**
         * ????????????
         */
        targetAtr: number;
    }

    export class SystemType {
        static EMPLOYMENT = 1;
        static SALARY = 2;
        static PERSONNEL = 3;
        static ACCOUNTING = 4;
        static OH = 6;
    }

    export class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }


    class ItemModelGrid {
        id: string;
        code: string;
        businessName: string;
        workplaceName: string;

        constructor(id: string, code: string, businessName: string, workplaceName: string) {
            this.id = id;
            this.code = code;
            this.businessName = businessName;
            this.workplaceName = workplaceName;
        }
    }

    class SocialInsurNotiCrSet {
        /**
         * ???????????????
         */
        officeInformation: KnockoutObservable<number> ;

        /**
         * ?????????????????????
         */
        businessArrSymbol: KnockoutObservable<number>;

        /**
         * ?????????
         */
        outputOrder: KnockoutObservable<number> ;

        /**
         * ??????????????????
         */
        printPersonNumber: KnockoutObservable<number> ;
        /**
         * ??????????????????
         */
        submittedName: KnockoutObservable<number>;

        /**
         * ????????????????????????
         */
        insuredNumber: KnockoutObservable<number> ;

        /**
         * FD??????
         */
        fdNumber: KnockoutObservable<string>;

        /**
         * ????????????????????????
         */
        textPersonNumber: KnockoutObservable<number>;
        /**
         * ????????????
         */
        outputFormat: KnockoutObservable<number> ;
        /**
         * ???????????????
         */
        lineFeedCode: KnockoutObservable<number> ;
        constructor(params: INotifiOfInsurQuaAcDto) {

            this.officeInformation = ko.observable(params.officeInformation);
            this.printPersonNumber = ko.observable(params.printPersonNumber);
            this.businessArrSymbol = ko.observable(params.businessArrSymbol);
            this.outputOrder = ko.observable(params.outputOrder);
            this.submittedName = ko.observable(params.submittedName);
            this.insuredNumber = ko.observable(params.insuredNumber);
            this.fdNumber = ko.observable(params.fdNumber);
            this.textPersonNumber = ko.observable(params ? params.textPersonNumber : null);
            this.outputFormat = ko.observable(params ? params.outputFormat : null);
            this.lineFeedCode = ko.observable(params ? params.lineFeedCode : null);

        }


    }

    class UnitModel {
        code: string;
        name?: string;
        workplaceName?: string;
        isAlreadySetting?: boolean;

        constructor(code:string, name: string, workplaceName: string, isAlreadySetting: boolean){
            this.code = code;
            this.name = name;
            this.workplaceName = workplaceName;
            this.isAlreadySetting = isAlreadySetting;
        }
    }


    class SalGenParaValue {
        // param value
        /**
         * ??????ID
         */
        historyId: string;

        /**
         * ?????????
         */
        selection: number;

        /**
         * ????????????
         */
        availableAtr: number;

        /**
         * ???????????????
         */
        numValue: string;

        /**
         * ???????????????
         */
        charValue: string;

        /**
         * ???????????????
         */
        timeValue: number;

        /**
         * ????????????
         */
        targetAtr: number;

        constructor(historyId: string,
                    selection: number,
                    availableAtr: number,
                    numValue: string,
                    charValue: string,
                    timeValue: number,
                    targetAtr: number) {
            this.historyId = historyId;
            this.selection = selection;
            this.availableAtr = availableAtr;
            this.numValue = numValue;
            this.charValue = charValue;
            this.timeValue = timeValue;
            this.targetAtr = targetAtr;
        }

        constructor() {

        }
    }
    //ENUM
    export function getBusinessDivision(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_BusinessDivision_OUTPUT_COMPANY_NAME')),
            new ItemModel('1', getText('Enum_BusinessDivision_OUTPUT_SIC_INSURES')),
            new ItemModel('2', getText('Enum_BusinessDivision_DO_NOT_OUTPUT')),
            new ItemModel('3', getText('Enum_BusinessDivision_DO_NOT_OUTPUT_BUSINESS'))
        ];
    }
    export function getBussEsimateClass(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_BussEsimateClass_HEAL_INSUR_OFF_ARR_SYMBOL')),
            new ItemModel('1', getText('Enum_BussEsimateClass_EMPEN_ESTAB_REARSIGN'))
        ];
    }


    export function getSocialInsurOutOrder(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_SocialInsurOutOrder_DIVISION_EMP_ORDER')),
            new ItemModel('1', getText('Enum_SocialInsurOutOrder_EMPLOYEE_CODE_ORDER')),
            new ItemModel('2', getText('Enum_SocialInsurOutOrder_EMPLOYEE_KANA_ORDER'))
        ];
    }

    export function getPersonalNumClass(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_PersonalNumClass_OUTPUT_PER_NUMBER')),
            new ItemModel('1', getText('Enum_PersonalNumClass_OUTPUT_BASIC_PER_NUMBER')),
            new ItemModel('2', getText('Enum_PersonalNumClass_OUTPUT_BASIC_PEN_NOPER')),
            new ItemModel('3', getText('Enum_PersonalNumClass_DO_NOT_OUTPUT'))
        ];
    }

    export function getSubNameClass(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_SubNameClass_PERSONAL_NAME')),
            new ItemModel('1', getText('Enum_SubNameClass_REPORTED_NAME'))
        ];
    }


    export function getInsurPersonNumDivision(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_InsurPersonNumDivision_DO_NOT_OUPUT')),
            new ItemModel('1', getText('Enum_InsurPersonNumDivision_OUTPUT_HEAL_INSUR_NUM')),
            new ItemModel('2', getText('Enum_InsurPersonNumDivision_OUTPUT_THE_WELF_PENNUMBER')),
            new ItemModel('3', getText('Enum_InsurPersonNumDivision_OUTPUT_HEAL_INSUR_UNION')),
            new ItemModel('4', getText('Enum_InsurPersonNumDivision_OUTPUT_THE_FUN_MEMBER'))
        ];
    }

    export function getTextPerNumberClass(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_TextPerNumberClass_OUTPUT_NUMBER')),
            new ItemModel('1', getText('Enum_TextPerNumberClass_OUPUT_BASIC_PEN_NUMBER')),
            new ItemModel('2', getText('Enum_TextPerNumberClass_OUTPUT_BASIC_NO_PERSONAL'))
        ];
    }

    export function getOutputFormatClass(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_OutputFormatClass_PEN_OFFICE')),
            new ItemModel('1', getText('Enum_OutputFormatClass_HEAL_INSUR_ASSO')),
            new ItemModel('2', getText('Enum_OutputFormatClass_OUTPUT_THE_WELF_PEN'))
        ];
    }

    export function getLineFeedCode(): Array<ItemModel> {
        return [
            new ItemModel('0', getText('Enum_LineFeedCode_ADD')),
            new ItemModel('1', getText('Enum_LineFeedCode_DO_NOT_ADD')),
            new ItemModel('2', getText('Enum_LineFeedCode_E_GOV'))
        ];
    }

    //CKP005

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

    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }



}