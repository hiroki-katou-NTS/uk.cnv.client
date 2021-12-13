module nts.uk.at.view.kdr001.a.viewmodel {

    import ScheduleBatchCorrectSettingSave = service.model.ScheduleBatchCorrectSettingSave;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import modal = nts.uk.ui.windows.sub.modal;
    import getText = nts.uk.resource.getText;
    import hasError = nts.uk.ui.errors.hasError;
    const KDR001_SAVE_DATA = 'OUTPUT_CONDITIONS';
    export class ScreenModel extends ko.ViewModel{

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
        lstSearchEmployee: KnockoutObservableArray<EmployeeSearchDto>;
        showEmployment: KnockoutObservable<boolean>; // 雇用条件
        showWorkplace: KnockoutObservable<boolean>; // 職場条件
        showClassification: KnockoutObservable<boolean>; // 分類条件
        showJobTitle: KnockoutObservable<boolean>; // 職位条件
        showWorktype: KnockoutObservable<boolean>; // 勤種条件
        inService: KnockoutObservable<boolean>; // 在職区分
        leaveOfAbsence: KnockoutObservable<boolean>; // 休職区分
        closed: KnockoutObservable<boolean>; // 休業区分
        retirement: KnockoutObservable<boolean>; // 退職区分
        systemType: KnockoutObservable<number>;
        showClosure: KnockoutObservable<boolean>; // 就業締め日利用
        showBaseDate: KnockoutObservable<boolean>; // 基準日利用
        showAllClosure: KnockoutObservable<boolean>; // 全締め表示
        showPeriod: KnockoutObservable<boolean>; // 対象期間利用
        periodFormatYM: KnockoutObservable<boolean>; // 対象期間精度

        selectedImplementAtrCode: KnockoutObservable<number>;
        resetWorkingHours: KnockoutObservable<boolean>;
        resetDirectLineBounce: KnockoutObservable<boolean>;
        resetMasterInfo: KnockoutObservable<boolean>;
        resetTimeChildCare: KnockoutObservable<boolean>;
        resetAbsentHolidayBusines: KnockoutObservable<boolean>;
        resetTimeAssignment: KnockoutObservable<boolean>;

        periodDate: KnockoutObservable<any>;
        copyStartDate: KnockoutObservable<Date>;
        dateValue: KnockoutObservable<any>;
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;

        lstLabelInfomation: KnockoutObservableArray<string>;
        infoPeriodDate: KnockoutObservable<string>;
        lengthEmployeeSelected: KnockoutObservable<string>;

        // Employee tab
        lstPersonComponentOption: any;
        selectedEmployeeCode: KnockoutObservableArray<string>;
        employeeName: KnockoutObservable<string>;
        employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray([]);
        alreadySettingPersonal: KnockoutObservableArray<UnitAlreadySettingModel>;
        ccgcomponentPerson: GroupOption;

        //combo-box
        lstHolidayRemaining: KnockoutObservableArray<HolidayRemainingModel> = ko.observableArray([]);
        itemSelected: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        selectedCode: KnockoutObservable<number> = ko.observable(0);
        freeCode: KnockoutObservable<string> = ko.observable('');
        standardCode: KnockoutObservable<string> = ko.observable('');

        isEmployeeCharge: KnockoutObservable<boolean> = ko.observable(false);
        closureId: KnockoutObservable<number> = ko.observable(0);

        //radio test
        selectedId: KnockoutObservable<number> = ko.observable(0);
        enable: KnockoutObservable<boolean> = ko.observable(true);
        listFreeSetting: KnockoutObservableArray<ItemNewModel> = ko.observableArray([]);
        listStandard: KnockoutObservableArray<ItemNewModel> = ko.observableArray([]);

        getCheckauthor: KnockoutObservable<boolean> = ko.observable(false);
        isEmployee : KnockoutObservable<boolean> = ko.observable(false);

        //end
        constructor() {
            super();
            var self = this;
            self.systemType = ko.observableArray([
                {name: 'システム管理者', value: 1}, // PERSONAL_INFORMATION
                {name: '就業', value: 2} // EMPLOYMENT
            ]);
            self.lstSearchEmployee = ko.observableArray([]);
            // initial ccg options
            self.setDefaultCcg001Option();

            self.periodFormatYM.subscribe(item => {
                if (item) {
                    self.showClosure(true);
                }
            });
            self.isEmployee(__viewContext.user.role.isInCharge.attendance);
            self.dateValue = ko.observable("");
            self.selectedEmployeeCode = ko.observableArray([]);
            self.alreadySettingPersonal = ko.observableArray([]);
            self.periodDate = ko.observable({
                startDate: self.dateValue().startDate,
                endDate: self.dateValue().endDate
            });
            self.startDateString = ko.observable("");
            self.endDateString = ko.observable("");
            self.startDateString.subscribe(function (value) {
                self.dateValue().startDate = value;
                self.dateValue.valueHasMutated();
            });

            self.endDateString.subscribe(function (value) {
                self.dateValue().endDate = value;
                self.dateValue.valueHasMutated();
            });

            self.resetWorkingHours = ko.observable(false);
            self.resetDirectLineBounce = ko.observable(false);
            self.resetMasterInfo = ko.observable(false);
            self.resetTimeChildCare = ko.observable(false);
            self.resetAbsentHolidayBusines = ko.observable(false);
            self.resetTimeAssignment = ko.observable(false);
            self.copyStartDate = ko.observable(new Date());

            //radio test
            self.selectedId = ko.observable(0);
            self.selectedId.subscribe((value) => {
                self.enable(value === StandardOrFree.Standard);
            });
            //end
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
            self.isSelectAllEmployee = ko.observable(false);
            self.baseDate = ko.observable(moment());
            self.periodStartDate = ko.observable(moment());
            self.periodEndDate = ko.observable(moment());
            self.showEmployment = ko.observable(true); // 雇用条件
            self.showWorkplace = ko.observable(true); // 職場条件
            self.showClassification = ko.observable(true); // 分類条件
            self.showJobTitle = ko.observable(true); // 職位条件
            self.showWorktype = ko.observable(true); // 勤種条件
            self.inService = ko.observable(true); // 在職区分
            self.leaveOfAbsence = ko.observable(true); // 休職区分
            self.closed = ko.observable(true); // 休業区分
            self.retirement = ko.observable(true); // 退職区分
            self.systemType = ko.observable(2);
            self.showClosure = ko.observable(true); // 就業締め日利用
            self.showBaseDate = ko.observable(false); // 基準日利用
            self.showAllClosure = ko.observable(true); // 全締め表示
            self.showPeriod = ko.observable(true); // 対象期間利用
            self.periodFormatYM = ko.observable(true); // 対象期間精度
        }

        /**
         * Reload component CCG001
         */
        public reloadCcg001(): void {
            var self = this;
            var periodStartDate, periodEndDate: string;
            if (self.showBaseDate()) {
                periodStartDate = moment(self.dateValue().startDate).format("YYYY-MM-DD");
                periodEndDate = moment(self.dateValue().endDate).format("YYYY-MM-DD");
            } else {
                periodStartDate = moment(self.dateValue().startDate).format("YYYY-MM");
                periodEndDate = moment(self.dateValue().endDate).format("YYYY-MM"); // 対象期間終了日
            }

            if (!self.showBaseDate() && !self.showClosure() && !self.showPeriod()) {
                nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!");
                return;
            }
            self.ccgcomponent = {
                /** Common properties */
                systemType: self.systemType(), // システム区分
                showEmployeeSelection: self.isSelectAllEmployee(), // 検索タイプ
                showQuickSearchTab: self.isQuickSearchTab(), // クイック検索
                showAdvancedSearchTab: self.isAdvancedSearchTab(), // 詳細検索
                showBaseDate: self.showBaseDate(), // 基準日利用
                showClosure: self.showClosure(), // 就業締め日利用
                showAllClosure: self.showAllClosure(), // 全締め表示
                showPeriod: self.showPeriod(), // 対象期間利用
                periodFormatYM: self.periodFormatYM(), // 対象期間精度

                /** Required parameter */
                baseDate: moment(self.baseDate()).format("YYYY-MM-DD"), // 基準日
                dateRangePickerValue: self.dateValue,
                inService: self.inService(), // 在職区分
                leaveOfAbsence: self.leaveOfAbsence(), // 休職区分
                closed: self.closed(), // 休業区分
                retirement: self.retirement(), // 退職区分

                /** Quick search tab options */
                showAllReferableEmployee: self.isAllReferableEmployee(), // 参照可能な社員すべて
                showOnlyMe: self.isOnlyMe(), // 自分だけ
                showSameWorkplace: self.isEmployeeOfWorkplace(), // 同じ職場の社員
                showSameWorkplaceAndChild: self.isEmployeeWorkplaceFollow(), // 同じ職場とその配下の社員

                /** Advanced search properties */
                showEmployment: self.showEmployment(), // 雇用条件
                showWorkplace: self.showWorkplace(), // 職場条件
                showClassification: self.showClassification(), // 分類条件
                showJobTitle: self.showJobTitle(), // 職位条件
                showWorktype: self.showWorktype(), // 勤種条件
                isMutipleCheck: self.isMutipleCheck(), // 選択モード
                tabindex: - 1,

                /** Return data */
                returnDataFromCcg001: function (data: Ccg001ReturnedData) {
                    self.lstSearchEmployee(data.listEmployee);
                    self.dateValue().startDate = moment(data.periodStart).format("YYYY/MM/DD");
                    self.dateValue().endDate = moment(data.periodEnd).format("YYYY/MM/DD");
                    self.dateValue.valueHasMutated();
                    self.applyKCP005ContentSearch(data.listEmployee);
                    self.closureId(data.closureId);
                }
            }
        }

        /**
         * start page data
         */
        public startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            nts.uk.ui.block.invisible();
            let user: any = __viewContext.user;
            //combo-box2
            service.getBreakSelection().done(data => {
                for (let i = 0, count = data.length; i < count; i++) {
                    self.itemSelected.push(new ItemModel(data[i].value, data[i].localizedName));
                }
            });
            $.when(service.findAll(),
                service.getDate(),
                service.getCurrentLoginerRole(),
                service.getCheckAuthor(),
                nts.uk.characteristics.restore("UserSpecific_" + user.employeeId)
            ).done((holidayRemainings: any,
                    dateData: GetDate,
                    role: any,
                    author: boolean,
                    userSpecific) => {
                self.loadAllHolidayRemaining(holidayRemainings);
                self.getCheckauthor(author);
                let startDate = moment(dateData ? dateData.startDate || moment() : moment());
                let endDate = moment(dateData ? dateData.endDate || moment() : moment());
                //画面項目「A3_4：終了年月」にパラメータ「当月+１月」をセットする
                let nextMonth = moment(endDate).add(1, 'M');

                //画面項目「A3_2：開始年月」にパラメータ「当月」－1年した値をセットする
                let preYear = moment(nextMonth).add(-1, 'Y');
                self.dateValue({
                    startDate: moment(startDate).add(0, 'M').format("YYYY/MM"),
                    endDate: moment(endDate).format("YYYY/MM")
                });
                self.dateValue.valueHasMutated();

                self.isEmployeeCharge(role.employeeCharge);
                if (userSpecific) {
                    if (_.find(holidayRemainings, x => {
                            return x.cd == userSpecific.outputItemSettingCode;
                        })) {
                        self.freeCode(userSpecific.outputItemSettingCode);
                    }
                    else {
                        self.freeCode('');
                        self.standardCode('');

                    }
                    self.selectedCode(userSpecific.pageBreakAtr);
                }
                // Init component.
                self.reloadCcg001();
                dfd.resolve(self);
                self.getWorkScheduleOutputConditions();

            }).fail(function (res) {
                nts.uk.ui.dialog.alertError({messageId: res.messageId});
            }).always(() => {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }

        /**
         * load and set item selected
         */
        loadAllHolidayRemaining(data: any) {

            let self = this;
            if (data) {
                let freeSetting = data.listFreeSetting;
                let standard = data.listStandard;
                self.listFreeSetting(freeSetting);
                self.listStandard(standard);

            }
            // no data
            else {
                self.freeCode('');
                self.standardCode('');
            }
        }

        /**
         * apply ccg001 search data to kcp005
         */
        public applyKCP005ContentSearch(dataList: EmployeeSearchDto[]): void {
            var self = this;
            var employeeSearchs: UnitModel[] = [];
            self.selectedEmployeeCode([]);
            for (var employeeSearch of dataList) {
                var employee: UnitModel = {
                    code: employeeSearch.employeeCode,
                    name: employeeSearch.employeeName,
                    affiliationName: employeeSearch.affiliationName
                };
                employeeSearchs.push(employee);
                self.selectedEmployeeCode.push(employee.code);
            }
            self.employeeList(employeeSearchs);
            self.lstPersonComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: true,
                listType: ListType.EMPLOYEE,
                employeeInputList: self.employeeList,
                selectType: SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: self.selectedEmployeeCode,
                isDialog: true,
                isShowNoSelectRow: false,
                alreadySettingList: self.alreadySettingPersonal,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: true,
                isSelectAllAfterReload: true,
                maxRows: 20
            };
        }

        /**
         * function export excel button
         */
        private exportButton() {
            let self = this;
            $('.nts-input').trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            nts.uk.ui.block.invisible();
            let startMonth = moment(self.dateValue().startDate, 'YYYY/MM');
            let endMonth = moment(self.dateValue().endDate, 'YYYY/MM');
            let end = (parseInt(endMonth.format("YYYY")) * 12 + parseInt(endMonth.format("MM")))
            let start = (parseInt(startMonth.format("YYYY")) * 12 + parseInt(startMonth.format("MM")));
            let totalMonths = (end - start) + 1;
            if (totalMonths < 0) {
                nts.uk.ui.dialog.alertError({messageId: 'Msg_1217'});
                nts.uk.ui.block.clear();
                return;
            }
            if (totalMonths > 13) {
                nts.uk.ui.dialog.alertError({messageId: 'Msg_1173'});
                nts.uk.ui.block.clear();
                return;
            }
            // get and build selected employee
            let lstSelectedEployee: Array<EmployeeSearchDto> = [];
            var index = -1;
            for (var i = 0; i < self.selectedEmployeeCode().length; i++) {
                index = _.findIndex(self.lstSearchEmployee(), function (x) {
                    return x.employeeCode === self.selectedEmployeeCode()[i]
                });
                if (index > -1) {
                    lstSelectedEployee.push(self.lstSearchEmployee()[index]);
                }
            }
            if (!lstSelectedEployee || lstSelectedEployee.length === 0) {
                nts.uk.ui.dialog.alertError({messageId: 'Msg_884'});
                nts.uk.ui.block.clear();
                return;
            }

            let user: any = __viewContext.user,
                objComboxSelected = _.find(self.lstHolidayRemaining(), function (c) {
                    return c.cd == self.freeCode();
                });
            let userSpecificInformation = new UserSpecificInformation(
                user.employeeId,
                user.companyId,
                self.freeCode(),
                self.selectedCode()
            );
            nts.uk.characteristics.save("UserSpecific_" + user.employeeId, userSpecificInformation);
            let layOutId = self.selectedId() == 0 ? self.standardCode() : self.freeCode();
            let holidayRemainingOutputCondition = new HolidayRemainingOutputCondition(
                startMonth.format("YYYY/MM/DD"),
                endMonth.endOf('month').format("YYYY/MM/DD"),
                layOutId,
                self.selectedCode(),
                self.baseDate().format("YYYY/MM/DD"),
                self.closureId(),
                objComboxSelected != undefined ? objComboxSelected.name : ""
            );
            let lstSelected: Array<EmployeeQuery> = [];
            for (let i = 0; i < lstSelectedEployee.length; i++) {
                let e = lstSelectedEployee[i];
                lstSelected.push(new EmployeeQuery(
                    e.employeeCode,
                    e.employeeId,
                    e.employeeName,
                    e.affiliationCode,
                    e.affiliationId,
                    e.affiliationName
                ))
            }
            let data = new ReportInfor(holidayRemainingOutputCondition, lstSelected);
            if (nts.uk.util.isNullOrUndefined(data.holidayRemainingOutputCondition.layOutId)) {

                if (self.selectedId() === 0 && nts.uk.util.isNullOrEmpty(self.standardCode()))
                 {
                     $('#KDR001_30').ntsError('set', {messageId: 'Msg_1141'});
                     $('#KDR001_30').focus();
                     $('#residence-code').ntsError('set', {messageId: 'Msg_1141'});

                     nts.uk.ui.block.clear();

                }
                if(self.selectedId() === 1 && nts.uk.util.isNullOrEmpty(self.freeCode())){
                    $('#KDR001_60').ntsError('set', {messageId: 'Msg_1141'});
                    $('#KDR001_60').focus();
                    $('#residence-code').ntsError('set', {messageId: 'Msg_1141'});
                    nts.uk.ui.block.clear();
                }
            } else {
                service.saveAsExcel(data).always(() => {

                }).done(() => {
                }).fail(function (res: any) {
                    nts.uk.ui.dialog.alertError({messageId: res.messageId});
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }
            self.selectedId.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            });

            self.freeCode.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            })

            self.standardCode.subscribe(() => {
                nts.uk.ui.errors.clearAll();
            })
            self.saveWorkScheduleOutputConditions();
        }
        saveWorkScheduleOutputConditions(): JQueryPromise<void> {
            let vm = this,
                dfd = $.Deferred<void>(),
                companyId: string = vm.$user.companyId,
                employeeId: string = vm.$user.employeeId;
            let data: any = {
                itemSelection: this.selectedId(), //項目選択
                standardSelectedCode: vm.standardCode(), //定型選択
                freeSelectedCode: vm.freeCode(), //自由設定
                selectedCode:    vm.selectedCode(),
            };

            let storageKey: string = KDR001_SAVE_DATA + "_companyId_" + companyId + "_employeeId_" + employeeId;
            vm.$window.storage(storageKey, data).then(() => {
                dfd.resolve();
            });

            return dfd.promise();
        }
        getWorkScheduleOutputConditions() {
            const vm = this,
                dfd = $.Deferred<void>(),
                companyId: string = vm.$user.companyId,
                employeeId: string = vm.$user.employeeId;
            let storageKey: string = KDR001_SAVE_DATA + "_companyId_" + companyId + "_employeeId_" + employeeId;

            vm.$window.storage(storageKey).then((data: any) => {
                if (!_.isNil(data)) {
                    let standardCode = _.find(vm.listStandard(),(e)=>{
                        return e.layoutId === data.standardSelectedCode
                    });
                    let freeCode = _.find(vm.listFreeSetting(), (e)=>{
                        return e.layoutId === data.freeSelectedCode
                    });
                    vm.selectedId(!vm.getCheckauthor() ? 0 : data.itemSelection); //項目選択
                    let  idSt= !_.isNil(standardCode) ? data.standardSelectedCode: null;
                    let  idFree= !_.isNil(freeCode) ? data.freeSelectedCode : null;
                    vm.freeCode(idFree);
                    vm.standardCode(idSt);
                    vm.selectedCode(data.selectedCode)
                }
                dfd.resolve();
            }).always(() => {
                dfd.resolve();
            });
        }
        /**
         * Open dialog B
         */
        openKDR001b(settingId: number) {
            let self = this;
            nts.uk.ui.block.invisible();
            nts.uk.ui.errors.clearAll();
            let id: any;
            if (settingId == 1) {
                id = this.freeCode();
            } else {
                id = this.standardCode();
            }
            let params = new PramToScrrenB(settingId, id);
            setShared('KDR001Params', params);
            modal("/view/kdr/001/b/index.xhtml").onClosed(function () {
                let id = getShared('KDR001B2A_cd');

                service.findAll().done(function (data: Array<any>) {
                    self.loadAllHolidayRemaining(data);
                    self.freeCode(id);
                    self.standardCode(id);
                }).fail(function (res) {
                    nts.uk.ui.dialog.alertError({messageId: res.messageId});
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            });
        }
    }

    //radio test
    export enum StandardOrFree {
        Standard = 0,
        Free = 1
    }

    //end
    export class PramToScrrenB {
        settingId: number;
        layOutId: string

        constructor(settingId: number, layOutId: string) {
            this.layOutId = layOutId;
            this.settingId = settingId;
        }

    }

    export class EmployeeQuery {
        employeeCode: string;
        employeeId: string;
        employeeName: string;
        workplaceCode: string;
        workplaceId: string;
        workplaceName: string;

        constructor(employeeCode: string, employeeId: string, employeeName: string, workplaceCode: string,
                    workplaceId: string, workplaceName: string) {
            this.employeeCode = employeeCode;
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.workplaceCode = workplaceCode;
            this.workplaceId = workplaceId;
            this.workplaceName = workplaceName;
        }


    }

    export class HolidayRemainingModel {
        cd: string;
        name: string;

        constructor(code: string, name: string) {
            this.cd = code;
            this.name = name;
        }
    }

    export class ItemModel {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    export class ItemNewModel {
        layoutId: string;
        cd: string;
        name: string;

        constructor(layoutId: string, name: string, cd: string) {
            this.layoutId = layoutId;
            this.name = name;
            this.cd = cd;
        }
    }

    export class Datta {
        listFreeSetting: [ItemNewModel];
        listStandard: [ItemNewModel];

        constructor(listFreeSetting: [ItemNewModel], listStandard: [ItemNewModel]) {
            this.listFreeSetting = listFreeSetting;
            this.listStandard = listStandard;
        }
    }

    export class BoxModel {
        id: number;
        name: string;

        constructor(id: number, name: string) {
            var vm = this;
            vm.id = id;
            vm.name = name;
        }
    }

    // スケジュール一括修正設定
    export class ScheduleBatchCorrectSetting {
        // 勤務種類
        worktypeCode: string;

        // 社員ID
        employeeId: string;

        // 終了日
        endDate: string;

        // 開始日
        startDate: string;

        // 就業時間帯
        worktimeCode: string;

        constructor() {
            var self = this;
            self.worktypeCode = '';
            self.employeeId = '';
            self.endDate = '';
            self.startDate = '';
            self.worktimeCode = '';
        }
    }

    export class GetDate {
        startDate: string;
        endDate: string;

        constructor(startDate: string, endDate: string) {
            this.startDate = startDate;
            this.endDate = endDate;
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

    export interface EmployeeSearchDto {
        employeeId: string;

        employeeCode: string;

        employeeName: string;

        workplaceCode: string;

        workplaceId: string;

        workplaceName: string;
    }

    export interface GroupOption {
        baseDate?: KnockoutObservable<Date>;

        periodStartDate?: KnockoutObservable<Date>;

        periodEndDate?: KnockoutObservable<Date>;

        inService: boolean;

        leaveOfAbsence: boolean;

        closed: boolean;

        retirement: boolean;

        // クイック検索タブ
        isQuickSearchTab: boolean;
        // 参照可能な社員すべて
        isAllReferableEmployee: boolean;
        //自分だけ
        isOnlyMe: boolean;
        //おなじ部門の社員
        isEmployeeOfWorkplace: boolean;
        //おなじ＋配下部門の社員
        isEmployeeWorkplaceFollow: boolean;


        // 詳細検索タブ
        isAdvancedSearchTab: boolean;
        //複数選択 
        isMutipleCheck: boolean;

        //社員指定タイプ or 全社員タイプ
        isSelectAllEmployee: boolean;

        onSearchAllClicked: (data: EmployeeSearchDto[]) => void;

        onSearchOnlyClicked: (data: EmployeeSearchDto) => void;

        onSearchOfWorkplaceClicked: (data: EmployeeSearchDto[]) => void;

        onSearchWorkplaceChildClicked: (data: EmployeeSearchDto[]) => void;

        onApplyEmployee: (data: EmployeeSearchDto[]) => void;
    }

    export class ReportInfor {
        holidayRemainingOutputCondition: any;
        lstEmpIds: any[];

        constructor(holidayRemainingOutputCondition: any, lstEmpIds: any[]) {
            this.holidayRemainingOutputCondition = holidayRemainingOutputCondition;
            this.lstEmpIds = lstEmpIds;
        }
    }

    export class HolidayRemainingOutputCondition {
        startMonth: string;
        endMonth: string;
        layOutId: string;
        pageBreak: string;
        baseDate: string;
        closureId: number;
        title: string;

        constructor(startMonth: string, endMonth: string, layOutId: string, pageBreak: string,
                    baseDate: string, closureId: number, title: string) {
            this.startMonth = startMonth;
            this.endMonth = endMonth;
            this.layOutId = layOutId;
            this.pageBreak = pageBreak;
            this.baseDate = baseDate;
            this.closureId = closureId;
            this.title = title;
        }
    }

    export class UserSpecificInformation {
        userId: string;
        companyId: string;
        outputItemSettingCode: string;
        pageBreakAtr: string;

        constructor(userId: string, companyId: string, outputItemSettingCode: string, pageBreakAtr: string) {
            this.userId = userId;
            this.companyId = companyId;
            this.outputItemSettingCode = outputItemSettingCode;
            this.pageBreakAtr = pageBreakAtr;
        }
    }

}