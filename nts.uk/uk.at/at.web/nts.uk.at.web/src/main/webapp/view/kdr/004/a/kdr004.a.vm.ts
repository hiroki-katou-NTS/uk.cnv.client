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
                systemType: 2, //?????????????????? - 2: ??????
                showEmployeeSelection: true,
                showQuickSearchTab: true, //??????????????????
                showAdvancedSearchTab: true, //????????????
                showBaseDate: true, //???????????????
                showClosure: true,
                showAllClosure: true, //???????????????	-> ????????????????????????????????????
                showPeriod: false, //??????????????????
                periodFormatYM: false,

                /** Required parameter */
                baseDate: moment.utc(period.endDate).toISOString(), //?????????
                inService: true, //???????????? = ??????
                leaveOfAbsence: true, //???????????? = ??????
                closed: true, //???????????? = ??????
                retirement: false, // ???????????? = ?????????

                /** Quick search tab options */
                showAllReferableEmployee: true,// ??????????????????????????????
                showOnlyMe: true,// ????????????
                showSameDepartment: false,//?????????????????????
                showSameDepartmentAndChild: false,// ????????????????????????????????????
                showSameWorkplace: true, // ?????????????????????
                showSameWorkplaceAndChild: true,// ????????????????????????????????????

                /** Advanced search properties */
                showEmployment: true,// ????????????
                showDepartment: false, // ????????????
                showWorkplace: true,// ????????????
                showClassification: true,// ????????????
                showJobTitle: true,// ????????????
                showWorktype: true,// ????????????
                isMutipleCheck: true,// ???????????????

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
         * ????????????    : 0
         * ???????????? : 1
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

            //?????????????????????????????????????????????
            if (nts.uk.util.isNullOrEmpty(vm.multiSelectedCode())) {
                vm.$dialog.error({messageId: 'Msg_1923'}).then(() => {
                });
                hasError.error = true;
                hasError.focusId = 'kcp005';
                return hasError;
            }
            //?????????????????????????????????????????????

            return hasError;
            //??????????????????????????????????????????????????? | ?????????????????????????????????
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