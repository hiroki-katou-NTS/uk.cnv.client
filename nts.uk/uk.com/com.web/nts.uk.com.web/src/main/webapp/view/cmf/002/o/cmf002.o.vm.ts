module nts.uk.com.view.cmf002.o.viewmodel {
    import model = cmf002.share.model;
    import getText = nts.uk.resource.getText;
    import dialog = nts.uk.ui.dialog.info;
    import alertError = nts.uk.ui.dialog.alertError;
    import error = nts.uk.ui.errors;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import block = nts.uk.ui.block;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import Moment = moment.Moment;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    const textLink = nts.uk.resource.getText("CMF002_235");

    export class ScreenModel {
        //wizard
        stepList: Array<NtsWizardStep> = [];
        stepSelected: KnockoutObservable<NtsWizardStep> = ko.observable(null);
        activeStep: KnockoutObservable<number> = ko.observable(0);

        listCondition: KnockoutObservableArray<DisplayTableName> = ko.observableArray([]);
        selectedConditionCd: KnockoutObservable<string> = ko.observable('');
        selectedConditionName: KnockoutObservable<string> = ko.observable('');

        periodDateValue: KnockoutObservable<any> = ko.observable({
            startDate: moment.utc().format("YYYY/MM/DD"),
            endDate: moment.utc().format("YYYY/MM/DD")
        });
        listOutputItem: KnockoutObservableArray<model.StandardOutputItem> = ko.observableArray([]);
        selectedOutputItemCode: KnockoutObservable<string> = ko.observable('');

        listOutputCondition: KnockoutObservableArray<OutputCondition> = ko.observableArray([]);
        selectedOutputConditionItem: KnockoutObservable<string> = ko.observable('');
        alreadySettingPersonal: KnockoutObservableArray<UnitAlreadySettingModel>;
        // setup ccg001
        ccgcomponent: GroupOption;
        selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;
        //set up kcp 005
        listComponentOption: any;
        selectedCode: KnockoutObservable<string>;
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        employeeList: KnockoutObservableArray<UnitModel>;
        // check screen
        isPNextToR: KnockoutObservable<boolean> = ko.observable(true);
        referenceDate: KnockoutObservable<string> = ko.observable(moment.utc().toISOString());
        // data return from ccg001
        dataCcg001: EmployeeSearchDto[] = [];
        // list data id employ
        dataEmployeeId: Array<string>;
        startDate: KnockoutObservable<string> = ko.observable('');
        endDate: KnockoutObservable<string> = ko.observable('');
        //Q5_1
        conditionSettingName: KnockoutObservable<string> = ko.observable('');
        mode: KnockoutObservable<number> = ko.observable(MODE.NEW);

        isLoadScreenQ: boolean = false;

        roleAuthority: any;
        exOutCtgDto:  KnockoutObservable<any> = ko.observable('');
        closureId: KnockoutObservable<any> = ko.observable('');
        closureLists: KnockoutObservableArray<any> = ko.observableArray([]);


        show61DatePeriod: KnockoutObservable<boolean> = ko.observable(false);
        show61YmPeriod: KnockoutObservable<boolean> = ko.observable(false);
        show61Date: KnockoutObservable<boolean> = ko.observable(false);

        show81DatePeriod: KnockoutObservable<boolean> = ko.observable(false);
        show81YmPeriod: KnockoutObservable<boolean> = ko.observable(false);
        show81Date: KnockoutObservable<boolean> = ko.observable(false);
        baseDate: KnockoutObservable<any>;
        valueItemFixedForm: KnockoutObservable<any>;
        date61:  KnockoutObservable<any> = ko.observable('');
        isNoData : KnockoutObservable<boolean> = ko.observable(true);
        constructor() {
            var self = this;
            //起動する
            self.stepList = [
                // { content: '.step-1' },
                {content: '.step-2'},
                {content: '.step-3'},
                {content: '.step-4'}
            ];
            self.dataEmployeeId = [];
            self.valueItemFixedForm = ko.observable('');
            // set up date time P6_1
            self.stepSelected = ko.observable({id: 'step-4', content: '.step-4'});
            self.alreadySettingPersonal = ko.observableArray([]);
            self.baseDate = ko.observable(new Date());
            self.selectedEmployee = ko.observableArray([]);

            self.roleAuthority = getShared("CMF002O_PARAMS");

            //set up kcp005
            self.selectedCode = ko.observable('1');
            self.multiSelectedCode = ko.observableArray(["1"]);
            self.isShowAlreadySet = ko.observable(false);
            self.alreadySettingList = ko.observableArray([
                {code: '1', isAlreadySetting: true},
                {code: '2', isAlreadySetting: true}
            ]);
            self.isDialog = ko.observable(false);
            self.isShowNoSelectRow = ko.observable(false);
            self.isMultiSelect = ko.observable(true);
            self.isShowWorkPlaceName = ko.observable(false);
            self.isShowSelectAllButton = ko.observable(false);
            this.employeeList = ko.observableArray<UnitModel>([]);
            self.listComponentOption = {
                isShowAlreadySet: self.isShowAlreadySet(),
                isMultiSelect: self.isMultiSelect(),
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_ALL,
                selectedCode: self.selectedCode,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton(),
                maxRows: 8
            };
            self.selectedConditionCd.subscribe(data => {
                if (!data) {
                    self.selectedConditionCd('');
                    self.selectedConditionName('');
                }
                else {
                    let conditionName = _.find(self.listCondition(), {'code': self.selectedConditionCd()}).name;
                    self.selectedConditionName(conditionName);
                    let catelogoryId: number = _.find(self.listCondition(), {'code': self.selectedConditionCd()}).catelogoryId;
                    block.invisible();
                    service.getExOutCtgDto(catelogoryId).done(data => {
                        if (data) {
                            self.isNoData(false);
                            let ex: ExOutCtgDto = data.exOutCtgDto;
                            let closureExports: [any] = data.closureExports;
                            self.closureLists(closureExports);

                            self.exOutCtgDto(ex);
                            self.periodDateValue({
                                startDate: moment.utc().format("YYYY/MM/DD"),
                                endDate: moment.utc().format("YYYY/MM/DD")
                            });
                            // show61DatePeriod
                            //外部出期間区分
                            let outingPeriodClassific = ex.outingPeriodClassific;
                            let classificationToUse = ex.classificationToUse;
                            if (outingPeriodClassific == OUTPUTCLASSS.DATE) {
                                if (classificationToUse === TOUSE.DO_NOT_USE) {
                                    self.show61DatePeriod(true);

                                    self.show61YmPeriod(false);
                                    self.show61Date(false);
                                    self.show81DatePeriod(false);
                                    self.show81YmPeriod(false);
                                    self.show81Date(false);
                                } else {
                                    self.show81DatePeriod(true);

                                    self.show61DatePeriod(false);
                                    self.show61YmPeriod(false);
                                    self.show61Date(false);
                                    self.show81YmPeriod(false);
                                    self.show81Date(false);
                                }
                            }
                            if (outingPeriodClassific == OUTPUTCLASSS.YEAR_MONTH) {
                                if (classificationToUse === TOUSE.DO_NOT_USE) {
                                    self.show61YmPeriod(true);
                                    self.show61DatePeriod(false);
                                    self.show61Date(false);
                                    self.show81DatePeriod(false);
                                    self.show81YmPeriod(false);
                                    self.show81Date(false);
                                } else {
                                    self.show81YmPeriod(true);
                                    self.show61DatePeriod(false);
                                    self.show61YmPeriod(false);
                                    self.show61Date(false);
                                    self.show81DatePeriod(false);
                                    self.show81Date(false);
                                }
                            }
                            if (outingPeriodClassific == OUTPUTCLASSS.REFERENCE_DATE) {
                                let date =  moment.utc().format("YYYY/MM/DD");
                                self.date61(date);
                                self.show61Date(true);
                                self.show61DatePeriod(false);
                                self.show61YmPeriod(false);
                                self.show81DatePeriod(false);
                                self.show81YmPeriod(false);
                                self.show81Date(false);

                            }
                            if (outingPeriodClassific == OUTPUTCLASSS.NO_SETTING) {
                                self.show81Date(true);
                                self.show61DatePeriod(false);
                                self.show61YmPeriod(false);
                                self.show61Date(false);
                                self.show81DatePeriod(false);
                                self.show81YmPeriod(false);

                            }
                        }else {
                        }
                    }).always(()=>{
                        block.clear();
                    });
                }
            });

            self.date61.subscribe((data)=>{
                self.periodDateValue({
                    startDate: data,
                    endDate:  data
                });
                self.referenceDate(data);

            });
            self.periodDateValue.subscribe((data)=>{
             let   periodStartDate =  moment.utc(self.periodDateValue().startDate, "YYYY/MM/DD").toISOString();
            });
            self.closureId.subscribe((closureId) => {
                let cl = _.find(self.closureLists(), {'closureId': closureId});
                let startDate = null;
                let endDate = null;
                if(!isNullOrUndefined(cl)){
                     startDate = cl.startDate;
                     endDate = cl.endDate;
                }
                if (self.show81Date()) {
                    self.periodDateValue({
                        startDate :null,
                        endDate :null
                    });
                }
                if (self.show81DatePeriod()) {
                    self.periodDateValue({
                        startDate :startDate,
                        endDate :endDate
                    });
                }

                if (self.show81YmPeriod()) {
                    self.periodDateValue({
                        startDate :startDate,
                        endDate :endDate
                    });
                }
            })
        }

        /**
         * apply ccg001 search data to kcp005
         */
        public applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): void {
            let employeeSearchs: UnitModel[] = [];
            for (let employeeSearch of dataList) {
                let employee: UnitModel = {
                    code: employeeSearch.employeeCode,
                    name: employeeSearch.employeeName,
                    affiliationName: employeeSearch.affiliationName,
                    isAlreadySetting: false
                };
                employeeSearchs.push(employee);
            }
            this.employeeList(employeeSearchs);
        }

        selectStandardMode() {
            block.invisible();
            let self = this;
            $('.content.clearfix .body').attr('style', '');
            service.getConditionSetting(new ParamToScreenP("", "", self.roleAuthority.empRole[0])).done(res => {
                {
                    let dataCndSetCd: Array<StdOutputCondSetDto> = res;
                    self.loadListCondition(dataCndSetCd);
                    // $('#ex_output_wizard').ntsWizard("next");
                    $("#grd_Condition_container").focus();

                    block.clear();
                }

            }).fail(res => {
                self.mode(MODE.NO);
                // $('#ex_output_wizard').ntsWizard("next");
                alertError(res);
                block.clear();
            });


        }

        next() {
            let self = this;
//            if($('.body.current').attr('id') == 'ex_output_wizard-p-1' ){
//                $('.content.clearfix .body').attr('style', '');
//            }
            $('#ex_output_wizard').ntsWizard("next");
        }

        previous() {
            $('#component-items-list').ntsError('clear');
//            if($('.body.current').attr('id') == 'ex_output_wizard-p-1' ){
//                $('.content.clearfix .body').attr('style', '');
//            }
            $('#ex_output_wizard').ntsWizard("prev");
        }

        todoScreenQ() {
            let self = this;
            $('.content.clearfix .body').attr('style', '');
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            error.clearAll();
            let data: ExOutCtgDto = self.exOutCtgDto();
                if (data.categorySet == model.CATEGORY_SETTING.DATA_TYPE) {
                    $('#ex_output_wizard').ntsWizard("goto", 1);
                    self.isPNextToR(false);
                    self.loadScreenQ();
                }
                else {
                    $('#ex_output_wizard').ntsWizard("goto", 2);
                    self.isPNextToR(true);
                    self.initScreenR();
                }
        }
        //find list id from list code
        findListId(dataListCode: Array<string>): Array<string> {
            return _.filter(this.dataCcg001, function (o) {
                return _.includes(dataListCode, o.employeeCode);
            }).map(item => item.employeeId);
        }

        backFromR() {
            let self = this;

            if (self.isPNextToR()) {
                // back To P
                $('#ex_output_wizard').ntsWizard("goto", 0);
            } else {
                // back To Q
                $('#ex_output_wizard').ntsWizard("goto", 1);
            }
        }

        nextToScreenR() {
            let self = this;
            $('#component-items-list').ntsError('clear');
            // get list to pass screen R
            // 外部出力実行社員選択チェック
            self.dataEmployeeId = self.findListId(self.selectedCode());
            if (self.dataEmployeeId.length == 0) {
                alertError({messageId: 'Msg_657'});
                //$('#component-items-list').ntsError('set', {messageId:"Msg_657"});
            }
            else {
                self.next();
                self.initScreenR();
            }
        }

        initScreenR() {
            let self = this;
            service.getExOutSummarySetting(self.selectedConditionCd()).done(res => {
                self.listOutputCondition(_.map(res.ctgItemDataCustomList, (itemData) => {
                    itemData.conditions = self.formatData(itemData.conditions, itemData.dataType);
                    return itemData;
                }));
                self.listOutputItem(res.ctdOutItemCustomList);

                $(".createExOutText").focus();
            }).fail(res => {
                console.log("getExOutSummarySetting fail");
            });

        }

        formatData(data: string, typeData: number): string {
            let self = this;
            if (_.isEmpty(data)) return data;
            if (typeData == model.ITEM_TYPE.INS_TIME || typeData == model.ITEM_TYPE.TIME) {
                let typeFormat = typeData == model.ITEM_TYPE.INS_TIME ? "Clock_Short_HM" : "Time_Short_HM"
                if (data.indexOf(textLink) != -1) {
                    return nts.uk.time.format.byId(typeFormat, Number(data.split(textLink)[0])) + textLink + nts.uk.time.format.byId(typeFormat, Number(data.split(textLink)[1]))
                } else {
                    return nts.uk.time.format.byId(typeFormat, Number(data.split("|")[0])) + data.split("|")[1];
                }
            } else {
                return data;
            }
        }

        createExOutText() {
            block.invisible();

            let self = this;
            let conditionSetCd = self.selectedConditionCd();
            let userId = "";
            let startDate = moment.utc(self.periodDateValue().startDate, "YYYY/MM/DD").toISOString();
            let lastDayOfMonth = moment.utc(self.periodDateValue().endDate, "YYYY/MM/DD");
            let endDate = "";
            if(self.show61YmPeriod()||self.show81YmPeriod()){
                endDate = lastDayOfMonth.clone().endOf('month').toISOString();
            }
            else{
                endDate = lastDayOfMonth.toISOString();
            }
            let referenceDate = self.referenceDate();
            let standardType = true;
            let sidList = self.dataEmployeeId;
            let command = new CreateExOutTextCommand(conditionSetCd, userId, startDate,
                endDate, referenceDate, standardType, sidList);
            service.createExOutText(command).done(res => {
                block.clear();

                let params = {
                    processingId: res,
                    startDate: startDate,
                    endDate: endDate,
                    selectedConditionCd: conditionSetCd,
                    selectedConditionName: self.selectedConditionName()
                };
                setShared("CMF002_R_PARAMS", params);
                nts.uk.ui.windows.sub.modal("/view/cmf/002/s/index.xhtml").onClosed(() => {
                    $('#ex_output_wizard').ntsWizard("goto", 0);
                });
            }).fail(res => {
                block.clear();
                console.log("createExOutText fail");
            });
        }

        loadListCondition(dataCndSetCd: Array<StdOutputCondSetDto>) {
            let self = this;


            let listItemModel: Array<model.ItemModel> = [];
            _.forEach(dataCndSetCd, function (item) {
                listItemModel.push(new DisplayTableName(item.categoryId, item.conditionSetCd, item.conditionSetName));
            });

            self.listCondition(listItemModel);
            self.selectedConditionCd(self.listCondition()[0].code);
            self.selectedConditionName(self.listCondition()[0].name);
        }

        loadScreenQ() {
            let self = this;
//          fix bug 102743
//            if(self.isLoadScreenQ){
//                return;
//            }
//            self.isLoadScreenQ = true;

            $("#component-items-list").focus();
            self.startDate(self.periodDateValue().startDate);
            self.endDate(self.periodDateValue().endDate);
            self.conditionSettingName(self.selectedConditionCd().toString() + ' ' + self.selectedConditionName().toString());
            self.ccgcomponent = {
                /** Common properties */
                systemType: 5, // システム区分 - 管理者
                showEmployeeSelection: false, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: true, // 基準日利用
                showClosure: false, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: false, // 対象期間利用
                periodFormatYM: false, // 対象期間精度
                /** Required parameter */
                baseDate: moment.utc().toISOString(), // 基準日
                periodStartDate: moment.utc(self.periodDateValue().startDate, "YYYY/MM/DD").toISOString(), // 対象期間開始日
                periodEndDate: moment.utc(self.periodDateValue().endDate, "YYYY/MM/DD").toISOString(), // 対象期間終了日
                inService: true, // 在職区分
                leaveOfAbsence: true, // 休職区分
                closed: true, // 休業区分
                retirement: true, // 退職区分
                /** Quick search tab options */
                showAllReferableEmployee: true, // 参照可能な社員すべて
                showOnlyMe: true, // 自分だけ
                showSameWorkplace: true, // 同じ職場の社員
                showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員
                /** Advanced search properties */
                showEmployment: true, // 雇用条件
                showWorkplace: true, // 職場条件
                showClassification: true, // 分類条件
                showJobTitle: true, // 職位条件
                showWorktype: true, // 勤種条件
                isMutipleCheck: true, // 選択モード
                /** Return data */
                returnDataFromCcg001: function (data: Ccg001ReturnedData) {
                    self.dataCcg001 = data.listEmployee;
                    self.applyKCP005ContentSearch(data.listEmployee);
                    self.referenceDate(data.baseDate);
                    $('#component-items-list').ntsError('clear');
                }
            }
            $('#component-items-list').ntsListComponent(self.listComponentOption).done(function () {
                $('#component-items-list').focusComponent();
            });
            $('#com-ccg001').ntsGroupComponent(self.ccgcomponent);
        }
    }

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

    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }

    class OutputCondition {
        seriNum: number;
        itemName: string;
        conditions: string;
        dataType: number;
        displayClassfication: number;

        constructor(seriNum: number, itemName: string, condition: string, dataType: number, displayClassfication: number) {
            this.seriNum = seriNum;
            this.itemName = itemName;
            this.conditions = condition;
            this.dataType = dataType;
            this.displayClassfication = displayClassfication;
        }
    }

    class ParamToScreenP {
        modeScreen: string;
        cndSetCd: string;
        roleId: string;

        constructor(modeScreen: string, cndSetCd: string, roleId: string) {
            this.modeScreen = modeScreen;
            this.cndSetCd = cndSetCd;
            this.roleId = roleId;
        }

    }

    class CreateExOutTextCommand {
        conditionSetCd: string;
        userId: string;
        startDate: Moment;
        endDate: Moment;
        referenceDate: Moment;
        standardType: boolean;
        sidList: Array<string>;

        constructor(conditionSetCd: string, userId: string, startDate: string,
                    endDate: string, referenceDate: string, standardType: boolean, sidList: Array<string>) {
            this.conditionSetCd = conditionSetCd;
            this.userId = userId;
            this.startDate = moment.utc(startDate);
            this.endDate = moment.utc(endDate);
            this.referenceDate = moment.utc(referenceDate);
            this.standardType = standardType;
            this.sidList = sidList;
        }
    }

    class StdOutputCondSetDto {
        cid: string;
        conditionSetCode: string;
        categoryId: number;
        delimiter: number;
        itemOutputName: number;
        autoExecution: number;
        conditionSetName: string;
        conditionOutputName: number;
        stringFormat: number;

        constructor(cid: string, conditionSetCode: string, categoryId: number, delimiter: number
            , itemOutputName: number, autoExecution: number, conditionSetName: string,
                    conditionOutputName: number, stringFormat: number) {
            this.cid = cid;
            this.conditionSetCode = conditionSetCode;
            this.categoryId = categoryId;
            this.delimiter = delimiter;
            this.itemOutputName = itemOutputName;
            this.autoExecution = autoExecution;
            this.conditionSetName = conditionSetName;
            this.conditionOutputName = conditionOutputName;
            this.stringFormat = stringFormat;
        }
    }

    class ExOutCtgDto {
        categoryId: number;
        officeHelperSysAtr: number;
        categoryName: string;
        categorySet: number;
        personSysAtr: number;
        payrollSysAtr: number;
        functionNo: number;
        functionName: string;
        explanation: string;
        displayOrder: number;
        defaultValue: boolean;
        outingPeriodClassific: number;
        classificationToUse: number;

        constructor(categoryId: number,
                    officeHelperSysAtr: number,
                    categoryName: string,
                    categorySet: number,
                    personSysAtr: number,
                    payrollSysAtr: number,
                    functionNo: number,
                    functionName: string,
                    explanation: string,
                    displayOrder: number,
                    defaultValue: boolean,
                    outingPeriodClassific: number,
                    classificationToUse: number) {
            this.categoryId = categoryId;
            this.officeHelperSysAtr = officeHelperSysAtr;
            this.categoryName = categoryName;
            this.categorySet = categorySet;
            this.personSysAtr = personSysAtr;
            this.payrollSysAtr = payrollSysAtr;
            this.functionNo = functionNo;
            this.functionName = functionName;
            this.explanation = explanation;
            this.displayOrder = displayOrder;
            this.defaultValue = defaultValue;
            this.outingPeriodClassific = outingPeriodClassific;
            this.classificationToUse = classificationToUse;
        }
    }


    export interface EmployeeSearchDto {
        employeeId: string;

        employeeCode: string;

        employeeName: string;

        workplaceCode: string;

        workplaceId: string;

        workplaceName: string;
    }

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
        // showDepartment: boolean; // 部門条件 not covered
        // showDelivery: boolean; not covered
        /** Data returned */
        returnDataFromCcg001: (data: Ccg001ReturnedData) => void;
    }

    export interface IDisplayTableName {
        catelogoryId: number;
        code: number;
        name: string;
    }

    export class DisplayTableName {
        catelogoryId: number;
        code: number;
        name: string;

        constructor(catelogoryId: number, code: number, name: string) {
            this.catelogoryId = catelogoryId;
            this.code = code;
            this.name = name;
        }
    }

    export interface IDisplayTableName {
        catelogoryId: number;
        code: number;
        name: string;
    }

    export interface ClosureDisPlay {
        catelogoryId: number;
        code: number;
        name: string;
    }

    export enum MODE {
        NEW = 0,
        UPDATE = 1,
        NO = 2
    }

    export  enum OUTPUTCLASSS {
        /**
         * 基準日
         */
        REFERENCE_DATE = 3,
        /**
         * 年月
         */
        YEAR_MONTH = 2,
        /**
         * 年月日
         */
        DATE = 1,
        /**
         * 設定なし
         */
        NO_SETTING = 0
    }

    export  enum TOUSE {
        /**
         * 使う
         */
        USE = 1,
        /**
         * 年月
         */
        DO_NOT_USE = 0

    }
}