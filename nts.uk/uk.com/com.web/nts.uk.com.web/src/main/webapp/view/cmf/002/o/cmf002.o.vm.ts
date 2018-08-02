module nts.uk.com.view.cmf002.o.viewmodel {
    import model = cmf002.share.model;
    import getText = nts.uk.resource.getText;
    import dialog = nts.uk.ui.dialog.info;
    import alertError = nts.uk.ui.dialog.alertError;
    import error = nts.uk.ui.errors;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    export class ScreenModel {
        //wizard
        stepList: Array<NtsWizardStep> = [];
        stepSelected: KnockoutObservable<NtsWizardStep> = ko.observable(null);
        activeStep: KnockoutObservable<number> = ko.observable(0);

        listCondition: KnockoutObservableArray<DisplayTableName> = ko.observableArray([]);
        selectedConditionCd: KnockoutObservable<string> = ko.observable('');
        selectedConditionName: KnockoutObservable<string> = ko.observable('');

        periodDateValue: KnockoutObservable<any> = ko.observable({});

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
        isCheckScreen: boolean = true;
<<<<<<< HEAD
        referenceDate: KnockoutObservable<string> = ko.observable(moment.utc().toISOString());
=======
        // data return from ccg001
        dataCcg001 :EmployeeSearchDto[] =[];
>>>>>>> ba8f83cde3e689bebf17ecccf10b8593110d3f6b

        constructor() {
            var self = this;
            //起動する
            self.stepList = [
                { content: '.step-1' },
                { content: '.step-2' },
                { content: '.step-3' },
                { content: '.step-4' }
            ];
            self.stepSelected = ko.observable({ id: 'step-4', content: '.step-4' });
            self.alreadySettingPersonal = ko.observableArray([]);
            self.baseDate = ko.observable(new Date());
            self.selectedEmployee = ko.observableArray([]);
            //set up kcp005
            let self = this;
            self.baseDate = ko.observable(new Date());
            self.selectedCode = ko.observable('1');
            self.multiSelectedCode = ko.observableArray([]);
            self.isShowAlreadySet = ko.observable(false);
            self.alreadySettingList = ko.observableArray([
                { code: '1', isAlreadySetting: true },
                { code: '2', isAlreadySetting: true }
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
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedCode,
                isDialog: self.isDialog(),
                isShowNoSelectRow: self.isShowNoSelectRow(),
                alreadySettingList: self.alreadySettingList,
                isShowWorkPlaceName: self.isShowWorkPlaceName(),
                isShowSelectAllButton: self.isShowSelectAllButton()
            };
            // setup kcp 005


        }
        /**
       * apply ccg001 search data to kcp005
       */
        public applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): void {
            let self = this;
            let employeeSearchs: UnitModel[] = [];
            for (let employeeSearch of dataList) {
                let employee: UnitModel = {
                    code: employeeSearch.employeeCode,
                    name: employeeSearch.employeeName,
                    workplaceName: employeeSearch.workplaceName,
                    isAlreadySetting: false
                };
                employeeSearchs.push(employee);
            }
            this.employeeList(employeeSearchs);
        }

        selectStandardMode() {
            let modeScreen = "a";
            let cndSetCd = "002";
            let self = this;

            service.getConditionSetting(modeScreen, cndSetCd).done(res => {
                {
                    let dataCndSetCd: Array<StdOutputCondSetDto> = res;
                    console.log(res);
                    self.loadListCondition(dataCndSetCd);
                    $('#ex_output_wizard').ntsWizard("next");
                }

            }).fail(res => {
                alertError(res);
            });
        }

        next() {
            let self = this;
            $('#ex_output_wizard').ntsWizard("next");
        }
        previous() {
            $('#ex_output_wizard').ntsWizard("prev");
        }

        todoScreenQ() {
            let self = this;
            error.clearAll();
            $(".nts-input").trigger("validate");
            if (!nts.uk.ui.errors.hasError()) {
                let catelogoryId: number = _.find(self.listCondition(), { 'code': self.selectedConditionCd() }).catelogoryId;
                let isNextGetData: boolean = moment.utc(self.periodDateValue().startDate, "YYYY/MM/DD").diff(moment.utc(self.periodDateValue().endDate, "YYYY/MM/DD")) > 0;
                if (isNextGetData) {
                    alertError('Msg_662');
                }
                else {
                    service.getExOutCtgDto(catelogoryId).done(res => {
                        {
                            let data: ExOutCtgDto = res;
                            if (data.categorySet == 6) {
                                $('#ex_output_wizard').ntsWizard("goto", 2);
                                self.isCheckScreen = false;
                                self.loadScreenQ();
                            }
                            else {
                                $('#ex_output_wizard').ntsWizard("goto", 3);
                                self.initScreenR();
                            }
                        }

                    }).fail(res => {
                        alertError(res);
                    });

                }
            }
        }
        //find list id from list code
        findListId(dataListCode: Array<string>): Array<string> {
            let data :EmployeeSearchDto[] = _.filter(this.dataCcg001, function(o) {
                        return _.includes(dataListCode, o.employeeCode);
                    }); 
            let listId :Array<string> = _.map(data, 'employeeId').reverse();
            return listId;
        }
         nextToScreenR() {
            let self = this;
<<<<<<< HEAD
            
=======
             // list id from list code
            console.log(self.findListId(self.selectedCode()) + ' gmfogokdof ');
>>>>>>> ba8f83cde3e689bebf17ecccf10b8593110d3f6b
            self.next();
            self.initScreenR();
        }
        
        initScreenR() {
            let self = this;
            
            service.getExOutSummarySetting(self.selectedConditionCd()).done(res => {
                self.listOutputCondition(res.ctgItemDataCustomList);
                self.listOutputItem(res.ctdOutItemCustomList);
            }).fail(res => {
                console.log("getExOutSummarySetting fail");
            });

            $(".createExOutText").focus();
        }

        createExOutText() {
            let self = this;
             //TODO set command
            let conditionSetCd = self.selectedConditionCd();
            let userId = "";
            let startDate = self.periodDateValue().startDate;
            let endDate = self.periodDateValue().endDate;
            let referenceDate = self.referenceDate();
            let standardType = true;
            let sidList = ["001", "002"];
            let command = new CreateExOutTextCommand(conditionSetCd, userId, startDate,
                endDate, referenceDate, standardType, sidList);
            service.createExOutText(command).done(res => {
                let params = {
                    processingId: res
                };

                setShared("CMF002_R_PARAMS", params);
                nts.uk.ui.windows.sub.modal("/view/cmf/002/s/index.xhtml").onClosed(() => {
                    $(".goback").focus();
                });
            }).fail(res => {
                console.log("createExOutText fail");
            });
        }

        loadListCondition(dataCndSetCd: Array<StdOutputCondSetDto>) {
            let self = this;
            let listItemModel: Array<model.ItemModel> = [];
            _.forEach(dataCndSetCd, function(item) {
                listItemModel.push(new DisplayTableName(item.categoryId, item.conditionSetCd, item.conditionSetName));
            });
            console.log(listItemModel.length);
            self.listCondition(listItemModel);
            self.selectedConditionCd(self.listCondition()[0].code);
            self.selectedConditionName('test a');
        }

        loadScreenQ() {
            let self = this;

            self.ccgcomponent = {
                /** Common properties */
                systemType: 1, // システム区分
                showEmployeeSelection: true, // 検索タイプ
                showQuickSearchTab: true, // クイック検索
                showAdvancedSearchTab: true, // 詳細検索
                showBaseDate: false, // 基準日利用
                showClosure: false, // 就業締め日利用
                showAllClosure: false, // 全締め表示
                showPeriod: true, // 対象期間利用
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
                returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                    self.dataCcg001 = data.listEmployee;
                    self.applyKCP005ContentSearch(data.listEmployee);
<<<<<<< HEAD
                    self.referenceDate(data.baseDate);
=======
                    //                    self.referenceDate(data.baseDate);
>>>>>>> ba8f83cde3e689bebf17ecccf10b8593110d3f6b
                }
            }
            $('#component-items-list').ntsListComponent(self.listComponentOption);
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
        workplaceName?: string;
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
        itemName: string;
        condition: string;
        constructor(itemName: string, condition: string) {
            this.itemName = itemName;
            this.condition = condition;
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
        conditionSetCd: string;
        categoryId: number;
        delimiter: number;
        itemOutputName: number;
        autoExecution: number;
        conditionSetName: string;
        conditionOutputName: number;
        stringFormat: number;
        constructor(cid: string, conditionSetCd: string, categoryId: number, delimiter: number
            , itemOutputName: number, autoExecution: number, conditionSetName: string,
            conditionOutputName: number, stringFormat: number) {
            this.cid = cid;
            this.conditionSetCd = conditionSetCd;
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
        constructor(categoryId: number, officeHelperSysAtr: number, categoryName: string, categorySet: number
            , personSysAtr: number, payrollSysAtr: number, functionNo: number,
            functionName: string, explanation: string, displayOrder: number, defaultValue: boolean) {
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


}