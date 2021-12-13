/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdr003.a {
    import common = nts.uk.at.view.kdr003.common;
    const PATH = {
        exportExcelPDF: 'at/function/holidayconfirmationtable/export',
        getClosurePeriod: "ctx/at/shared/workrule/closure/get-current-closure-period-by-logged-in-employee"
    };

    @bean()
    class ViewModel extends ko.ViewModel {

        // start variable of CCG001
        ccg001ComponentOption: common.GroupOption;
        closureId: KnockoutObservable<number> = ko.observable(null);
        // end variable of CCG001

        //panel left
        dpkYearMonth: KnockoutObservable<number> = ko.observable(202010);

        //panel right
        rdgSelectedId: KnockoutObservable<number> = ko.observable(0);
        standardSelectedCode: KnockoutObservable<string> = ko.observable(null);

        freeSelectedCode: KnockoutObservable<string> = ko.observable(null);
        isEnableSelectedCode: KnockoutObservable<boolean> = ko.observable(true);

        howToPrintDate: KnockoutObservable<number> = ko.observable(0);
        pageBreakSpecification: KnockoutObservable<number> = ko.observable(0);
        isWorker: KnockoutObservable<boolean> = ko.observable(true);
        settingListItems1: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        settingListItems2: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

        // start declare KCP005`
        listComponentOption: any;
        selectedCode: KnockoutObservable<string>;
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingList: KnockoutObservableArray<common.UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection: KnockoutObservable<boolean>;

        employeeList: KnockoutObservableArray<common.UnitModel>;
        baseDate: KnockoutObservable<Date>;
        mode: KnockoutObservable<common.UserSpecificInformation> = ko.observable(null);

        isPermission51: KnockoutObservable<boolean> = ko.observable(false);
        itemSelection: KnockoutObservableArray<any> = ko.observableArray([]);

        periodDate: KnockoutObservable<any> = ko.observable(null);
        moreSubstituteHolidaysThanHolidays: KnockoutObservable<boolean> = ko.observable(false);
        moreHolidaysThanSubstituteHolidays: KnockoutObservable<boolean> = ko.observable(false);

        constructor(params: any) {
            super();
            const vm = this;
            vm.$window.storage("KDR003_OPTIONS").done(options => {
                if (options) {
                    vm.moreSubstituteHolidaysThanHolidays(options.moreSubstituteHolidaysThanHolidays);
                    vm.moreHolidaysThanSubstituteHolidays(options.moreHolidaysThanSubstituteHolidays);
                    vm.howToPrintDate(options.howToPrintDate);
                    vm.pageBreakSpecification(options.pageBreakSpecification);
                }
            });
            //パラメータ.就業担当者であるか = true || false
            vm.isWorker(vm.$user.role.isInCharge.attendance);
            vm.getItemSelection();
            vm.$ajax("at", PATH.getClosurePeriod).done(data => {
                vm.CCG001_load(data);
                vm.KCP005_load();
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("hide");
            });
        }
        created(params: any) {
            const vm = this;

        }

        mounted() {
            const vm = this;
            $('#kcp005 table').attr('tabindex', '-1');
            $('#btnExportExcel').focus();
        }

        CCG001_load(period: any) {
            const vm = this;
            // Set component option
            let date = moment.utc(period.endDate, "YYYY/MM/DD").toISOString()
            vm.ccg001ComponentOption = {
                /** Common properties */
                systemType: 2, //システム区分 - 2: 就業
                showEmployeeSelection: true,
                showQuickSearchTab: true, //クイック検索
                showAdvancedSearchTab: true, //詳細検索
                showBaseDate: true, //基準日利用
                showClosure: true,
                showAllClosure: true, //氏名の種類	-> ビジネスネーム（日本語）
                showPeriod: false, //対象期間利用
                periodFormatYM: false,

                /** Required parameter */
                baseDate: date, //基準日
                inService: true, //在職区分 = 対象
                leaveOfAbsence: true, //休職区分 = 対象
                closed: true, //休業区分 = 対象
                retirement: false, // 退職区分 = 対象外

                /** Quick search tab options */
                showAllReferableEmployee: true,// 参照可能な社員すべて
                showOnlyMe: true,// 自分だけ
                showSameDepartment: false,//同じ部門の社員
                showSameDepartmentAndChild: false,// 同じ部門とその配下の社員
                showSameWorkplace: true, // 同じ職場の社員
                showSameWorkplaceAndChild: true,// 同じ職場とその配下の社員

                /** Advanced search properties */
                showEmployment: true,// 雇用条件
                showDepartment: false, // 部門条件
                showWorkplace: true,// 職場条件
                showClassification: true,// 分類条件
                showJobTitle: true,// 職位条件
                showWorktype: true,// 勤種条件
                isMutipleCheck: true,// 選択モード

                tabindex: -1,
                returnDataFromCcg001: function (data: common.Ccg001ReturnedData) {
                    vm.closureId(data.closureId);
                    vm.getListEmployees(data);
                }
            }
            $('#CCG001').ntsGroupComponent(vm.ccg001ComponentOption);
        }

        KCP005_load() {
            const vm = this;

            // start define KCP005
            vm.baseDate = ko.observable(new Date());
            vm.selectedCode = ko.observable('1');
            vm.multiSelectedCode = ko.observableArray([]);
            vm.isShowAlreadySet = ko.observable(false);
            vm.alreadySettingList = ko.observableArray([
                {code: '1', isAlreadySetting: true},
                {code: '2', isAlreadySetting: true}
            ]);
            vm.isDialog = ko.observable(true);
            vm.isShowNoSelectRow = ko.observable(false);
            vm.isMultiSelect = ko.observable(true);
            vm.isShowWorkPlaceName = ko.observable(true);
            vm.isShowSelectAllButton = ko.observable(true);
            //vm.disableSelection = ko.observable(false);
            vm.employeeList = ko.observableArray<common.UnitModel>([]);

            vm.listComponentOption = {
                isShowAlreadySet: vm.isShowAlreadySet(),
                isMultiSelect: vm.isMultiSelect(),
                listType: common.ListType.EMPLOYEE,
                employeeInputList: vm.employeeList,
                selectType: common.SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: vm.multiSelectedCode,
                isDialog: vm.isDialog(),
                isShowNoSelectRow: vm.isShowNoSelectRow(),
                alreadySettingList: vm.alreadySettingList,
                isShowWorkPlaceName: vm.isShowWorkPlaceName(),
                isShowSelectAllButton: vm.isShowSelectAllButton(),
                isSelectAllAfterReload: false,
                tabindex: 5,
                maxRows: 20
            };

            $('#kcp005').ntsListComponent(vm.listComponentOption)
        }

        getListEmployees(data: common.Ccg001ReturnedData) {
            let vm = this,
                multiSelectedCode: Array<string> = [],
                employeeSearchs: Array<common.UnitModel> = [];
            let newListEmployee: Array<any> = vm.removeDuplicateItem(data.listEmployee);
            _.forEach(newListEmployee, function (value: any) {
                var employee: common.UnitModel = {
                    id: value.employeeId,
                    code: value.employeeCode,
                    name: value.employeeName,
                    affiliationName: value.affiliationName
                };
                employeeSearchs.push(employee);
                multiSelectedCode.push(value.employeeCode);
            });

            vm.employeeList(employeeSearchs);
            vm.multiSelectedCode(multiSelectedCode);
        }

        checkErrorConditions() {
            const vm = this;

            let hasError: any = {
                error: false,
                focusId: ''
            };
            if (nts.uk.ui.errors.hasError()) {
                hasError.error = true;
                hasError.focusId = '';
                return hasError;
            }

            if (nts.uk.util.isNullOrEmpty(vm.multiSelectedCode())) {
                vm.$dialog.error({messageId: 'Msg_1923'}).then(() => {
                });
                hasError.error = true;
                hasError.focusId = 'kcp005';
                return hasError;
            }
            return hasError;
        }

        removeDuplicateItem(listItems: Array<any>): Array<any> {
            if (listItems.length <= 0) return [];

            let newListItems = _.filter(listItems, (element, index, self) => {
                return index === _.findIndex(self, (x) => {
                    return x.employeeCode === element.employeeCode;
                });
            });
            return newListItems;
        }

        exportExcelPDF(mode: number = 1) {
            let vm = this,
                validateError: any = {}; //not error

            $('.ntsDatepicker').trigger('validate');
            validateError = vm.checkErrorConditions();

            if (validateError.error) {
                if (!_.isNull(validateError.focusId)) {
                    $('#' + validateError.focusId).focus();
                }
                return;
            }
            //save conditions
            vm.$window.storage("KDR003_OPTIONS", {
                moreSubstituteHolidaysThanHolidays: vm.moreSubstituteHolidaysThanHolidays(),
                moreHolidaysThanSubstituteHolidays: vm.moreHolidaysThanSubstituteHolidays(),
                howToPrintDate: vm.howToPrintDate(),
                pageBreakSpecification: vm.pageBreakSpecification()
            });
            let multiSelectedCode: Array<string> = vm.multiSelectedCode();
            let lstEmployeeIds: Array<string> = [];
            _.forEach(multiSelectedCode, (employeeCode) => {
                let employee = _.find(vm.employeeList(), (x) => x.code.trim() === employeeCode.trim());
                if (!_.isNil(employee)) {
                    lstEmployeeIds.push(employee.id);
                }
            });
            vm.$blockui("show");
            nts.uk.request.exportFile(PATH.exportExcelPDF, {
                listEmployeeId: lstEmployeeIds,
                moreSubstituteHolidaysThanHolidays: vm.moreSubstituteHolidaysThanHolidays(),
                moreHolidaysThanSubstituteHolidays: vm.moreHolidaysThanSubstituteHolidays(),
                howToPrintDate: vm.howToPrintDate(),
                pageBreak: vm.pageBreakSpecification()
            }).fail(error => {
                vm.$dialog.error(error).then(() => {
                    if (error.messageId == "Msg_1926") $("#kcp005").focus()
                });
            }).always(() => {
                vm.$blockui("hide");
            });
        }

        getItemSelection() {
            const vm = this;
            vm.itemSelection.push({id: 0, name: vm.$i18n('KDR003_10')});
            vm.itemSelection.push({id: 1, name: vm.$i18n('KDR003_11')});
        }

    }

}