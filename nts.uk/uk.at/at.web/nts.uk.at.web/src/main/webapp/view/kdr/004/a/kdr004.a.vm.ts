/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdr004.a {
    import common = nts.uk.at.view.kdr004.common;

    const PATH = {
        getClosurePeriod: "ctx/at/shared/workrule/closure/get-current-closure-period-by-logged-in-employee",
        exportExcelPDF: 'at/function/holidayconfirmationtable/exportKdr004',
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        // start variable of CCG001
        ccg001ComponentOption: common.GroupOption;

        zeroDisplayClassification: KnockoutObservable<number> = ko.observable(0);
        pageBreakSpecification: KnockoutObservable<number> = ko.observable(0);

        // start declare KCP005
        listComponentOption: any;
        multiSelectedCode: KnockoutObservableArray<string>;
        alreadySettingList: KnockoutObservableArray<common.UnitAlreadySettingModel>;
        employeeList: KnockoutObservableArray<common.UnitModel>;
        // end KCP005

        haveMoreHolidayThanDrawOut: KnockoutObservable<boolean> = ko.observable(false);
        haveMoreDrawOutThanHoliday: KnockoutObservable<boolean> = ko.observable(false);

        created() {
            const vm = this;
            vm.$window.storage("KDR004_OPTIONS").done(options => {
                if (options) {
                    vm.haveMoreHolidayThanDrawOut(options.haveMoreHolidayThanDrawOut);
                    vm.haveMoreDrawOutThanHoliday(options.haveMoreDrawOutThanHoliday);
                    vm.zeroDisplayClassification(options.howToPrintDate);
                    vm.pageBreakSpecification(options.pageBreak);
                }
            });

            vm.$blockui("show");
            vm.$ajax("at", PATH.getClosurePeriod).done(data => {
                vm.CCG001_load(data);
                vm.KCP005_load();
            }).fail(error => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("hide");
            });
        }

        mounted() {
            $('#kcp005 table').attr('tabindex', '-1');
            $('#btnExportExcel').focus();
        }

        CCG001_load(period: any) {
            const vm = this;
            // Set component option
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
                baseDate: moment.utc(period.endDate).toISOString(), //基準日
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
                /**
                 * vm-defined function: Return data from CCG001
                 * @param: data: the data return from CCG001
                 */
                returnDataFromCcg001: function (data: common.Ccg001ReturnedData) {
                    vm.getListEmployees(data);
                }
            };
            // Start component
            $('#CCG001').ntsGroupComponent(vm.ccg001ComponentOption);
        }

        KCP005_load() {
            const vm = this;

            // start define KCP005
            vm.multiSelectedCode = ko.observableArray([]);
            vm.alreadySettingList = ko.observableArray([]);
            vm.employeeList = ko.observableArray<common.UnitModel>([]);

            vm.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: true,
                listType: common.ListType.EMPLOYEE,
                employeeInputList: vm.employeeList,
                selectType: common.SelectType.SELECT_BY_SELECTED_CODE,
                selectedCode: vm.multiSelectedCode,
                isDialog: true,
                isShowNoSelectRow: false,
                alreadySettingList: vm.alreadySettingList,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: true,
                isSelectAllAfterReload: false,
                tabindex: 5,
                maxRows: 20
            };

            $('#kcp005').ntsListComponent(vm.listComponentOption).then(() => {
                $(".pull-left .panel-frame").height($("#kcp005").height());
            });
        }

        /**
         *  get employees from CCG001
         */

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

        /**
         * Duplicate Setting
         * */

        /**
         * Gets setting listwork status
         * @param type
         * 定型選択    : 0
         * 自由設定 : 1
         */

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

            //【社員】が選択されていません。
            if (nts.uk.util.isNullOrEmpty(vm.multiSelectedCode())) {
                vm.$dialog.error({messageId: 'Msg_1923'}).then(() => {
                });
                hasError.error = true;
                hasError.focusId = 'kcp005';
                return hasError;
            }
            //自由設定が選択されていません。

            return hasError;
            //勤務状況表の対象ファイルを出力する | 対象データがありません
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
            vm.$window.storage("KDR004_OPTIONS", {
                haveMoreHolidayThanDrawOut: vm.haveMoreHolidayThanDrawOut(),
                haveMoreDrawOutThanHoliday: vm.haveMoreDrawOutThanHoliday(),
                howToPrintDate: vm.zeroDisplayClassification(),
                pageBreak: vm.pageBreakSpecification()
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
                employeeIds: lstEmployeeIds,
                haveMoreHolidayThanDrawOut: vm.haveMoreHolidayThanDrawOut(),
                haveMoreDrawOutThanHoliday: vm.haveMoreDrawOutThanHoliday(),
                howToPrintDate: vm.zeroDisplayClassification(),
                pageBreak: vm.pageBreakSpecification()
            }).fail(error => {
                vm.$dialog.error(error).then(() => {
                    if (error.messageId == "Msg_1926") $("#kcp005").focus()
                });
            }).always(() => {
                vm.$blockui("hide");
            });
        }

    }

    //=================================================================

    export class ItemModel {
        id: string;
        code: string;
        name: string;

        constructor(code?: string, name?: string, id?: string) {
            this.code = code;
            this.name = name;
            this.id = id;
        }
    }
}