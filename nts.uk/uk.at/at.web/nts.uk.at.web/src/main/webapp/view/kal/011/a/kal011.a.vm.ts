module nts.uk.at.kal011.a {

    import common = nts.uk.at.kal014.common;

    const API = {
        INIT: "at/function/alarm-workplace/alarm-list/init",
        GET_CHECK_CONDITION: "at/function/alarm-workplace/alarm-list/get-check-conditions",
        EXTRACT_CHECK: "at/function/alarm-workplace/alarm-list/extract/check"
    }

    @bean()
    export class Kal011AViewModel extends ko.ViewModel {
        employmentCode: string;
        processingYm: number;
        closureStartDate: number;
        closureEndDate: number;
        alarmPatterns: KnockoutObservableArray<AlarmPattern> = ko.observableArray([]);
        alarmPatternCode: KnockoutObservable<string> = ko.observable(null);
        alarmPatternName: string;
        conditions: KnockoutObservableArray<CheckCondition> = ko.observableArray([]);

        isCheckAll: KnockoutObservable<boolean> = ko.observable(false);
        isCheckAll_Temp: boolean = false;

        // work place list
        workplaceIds: KnockoutObservable<any> = ko.observableArray([]);
        baseDate: KnockoutObservable<Date> = ko.observable(this.$date.today());
        alreadySettingList: KnockoutObservableArray<any> = ko.observableArray([]);
        treeGrid: any;

        constructor() {
            super();
            const vm = this;

            vm.treeGrid = {
                isMultipleUse: false,
                isMultiSelect: true,
                startMode: 0, // WORKPLACE
                selectedId: vm.workplaceIds,
                baseDate: vm.baseDate,
                selectType: 3, // SELECT_FIRST_ITEM
                isShowSelectButton: true,
                isDialog: false,
                alreadySettingList: vm.alreadySettingList,
                maxRows: 12,
                tabindex: 3,
                systemType: 2 // EMPLOYMENT
            };
        }

        created() {
            const vm = this;
            vm.$blockui("invisible");
            $('#tree-grid').ntsTreeComponent(vm.treeGrid).done(() => {
                vm.init().done(() => {
                    vm.alarmPatternCode.subscribe((code) => {
                        if (!code) return;
                        vm.$blockui("invisible");

                        // get alarmPatternName
                        let pattern = _.find(vm.alarmPatterns(), (item: AlarmPattern) => item.code == code);
                        vm.alarmPatternName = pattern ? pattern.name : '';

                        vm.getCheckCondition().done(() => {
                            // reset check all
                            vm.isCheckAll_Temp = false;
                            vm.isCheckAll(vm.isCheckAll_Temp);

                            vm.$errors("clear");
                        }).fail((err: any) => {
                            vm.$dialog.error(err);
                        }).always(() => {
                            vm.$blockui("clear");
                        });
                    });
                    vm.alarmPatternCode.valueHasMutated();
                }).fail((err: any) => {
                    vm.$dialog.error(err);
                }).always(() => {
                    vm.$blockui("clear");
                });
                $('#tree-grid').focusTreeGridComponent();
            });
            $("#fixed-table").ntsFixedTable({ width: 435 });
            _.extend(window, { vm });
        }

        init(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred();

            vm.$ajax(API.INIT).done((res: IInitActiveAlarmList) => {
                if (res) {
                    if (!res.processingYm) {
                        dfd.reject({ messageId: "Msg_1143" });
                        return;
                    }
                    vm.employmentCode = res.employmentCode;
                    vm.processingYm = res.processingYm;
                    vm.closureStartDate = res.closureStartDate;
                    vm.closureEndDate = res.closureEndDate;

                    let parterns: Array<AlarmPattern> = [];
                    _.each(res.alarmPatterns, (item: IAlarmPatternSettingWorkPlace) => {
                        parterns.push(new AlarmPattern(item.alarmPatternCode, item.alarmPatternName));
                    })
                    vm.alarmPatterns(parterns);
					vm.workplaceIds([res.workplaceId]);
                }
                dfd.resolve();
            }).fail((err: any) => {
                dfd.reject(err)
            })

            return dfd.promise();
        }

        getCheckCondition(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred();

            let param: any = {
                alarmPatternCode: vm.alarmPatternCode(),
                processingYm: vm.processingYm,
                closureStartDate: moment(vm.closureStartDate, 'YYYY/MM/DD').toISOString(),
                closureEndDate: moment(vm.closureEndDate, 'YYYY/MM/DD').toISOString()
            };
            vm.$ajax(API.GET_CHECK_CONDITION, param).done((conditions: Array<ICheckCondition>) => {
                let conds: Array<CheckCondition> = [];
                if (conditions) {
                    let index = 1;
                    _.each(conditions, (condition: ICheckCondition) => {
                        let checkCond = new CheckCondition(condition);
                        checkCond.index = "period-ctg-" + index.toString();
                        vm.triggerError(checkCond);
                        conds.push(checkCond);
                        index++;
                    })
                }
                vm.conditions(_.sortBy(conds, ["category"]));
                dfd.resolve();
            }).fail((err: any) => {
                dfd.reject(err);
            });

            return dfd.promise();
        }

        triggerError(checkCond: CheckCondition) {
            const vm = this;
            checkCond.isChecked.subscribe((value: boolean) => {
                let getCheckedList = _.filter(vm.conditions(), (condition: CheckCondition) => {
                    return condition.isChecked() === true;
                });

                nts.uk.ui.errors.clearAll();
                vm.$errors("clear").then((valid: boolean) => {                    
                    _.each(getCheckedList, (condition: CheckCondition, index) => {                     
                        if( condition.isChecked ) {
                            $("#" + condition.index + " .ntsStartDatePicker").trigger('validate');
                            $("#" + condition.index + " .ntsEndDatePicker").trigger('validate');
                        } else {
                            vm.$validate("#" + condition.index + " .ntsStartDatePicker");
                            vm.$validate("#" + condition.index + " .ntsEndDatePicker");        
                        }
                    });     
                });      
                
                /* if (value) {
                    vm.$validate("#" + checkCond.index);
                    vm.$validate("#" + checkCond.index + " .ntsStartDatePicker");
                    vm.$validate("#" + checkCond.index + " .ntsEndDatePicker");
                } else {                    
                    vm.$errors("clear", "#" + checkCond.index);
                    vm.$errors("clear", "#" + checkCond.index + " .ntsStartDatePicker");
                    vm.$errors("clear", "#" + checkCond.index + " .ntsEndDatePicker");
                } */
            });
        }

        checkBoxAllOrNot(condition: CheckCondition) {
            const vm = this;
            condition.isChecked(!condition.isChecked());
            let checkeds = _.filter(vm.conditions(), (condition: CheckCondition) => condition.isChecked());
            vm.isCheckAll_Temp = checkeds.length == vm.conditions().length
            vm.isCheckAll(vm.isCheckAll_Temp);
        }

        checkAll() {
            const vm = this;
            vm.isCheckAll_Temp = !vm.isCheckAll_Temp;
            vm.isCheckAll(vm.isCheckAll_Temp);
            _.each(vm.conditions(), (condition: CheckCondition) => {
                condition.isChecked(vm.isCheckAll_Temp);
            })
        }

        exportAlarmList() {
            const vm = this;

            // アラームリスト出力開始前チェック
            vm.$validate("#B3_2").then((valid: boolean) => {
                if (!valid) return;
                if (_.isEmpty(vm.workplaceIds())) {
                    vm.$dialog.error({ messageId: "Msg_719" });
                    return
                }
                if (!vm.alarmPatternCode()) {
                    vm.$dialog.error({ messageId: "Msg_1167" });
                    return
                }
                let conditionSelecteds = _.filter(vm.conditions(), (condition: CheckCondition) => condition.isChecked());
                if (_.isEmpty(conditionSelecteds)) {
                    vm.$dialog.error({ messageId: "Msg_1168" });
                    return
                }
                let conditionSelectedInvalids = _.filter(conditionSelecteds, (condition: CheckCondition) => !condition.isValid());
                if (!_.isEmpty(conditionSelectedInvalids)) {
                    vm.$dialog.error({ messageId: "Msg_1167" });
                    return
                }

                vm.$blockui("invisible");
                // vm.$ajax(API.EXTRACT_CHECK).done(() => {
                    let categoryPeriods = _.map(conditionSelecteds, (condition: CheckCondition) => {
                        switch (condition.periodType) {
                            case PeriodType.PERIOD_DATE:
                                return {
                                    category: condition.category,
                                    startDate: condition.dateRange().startDate.toISOString(),
                                    endDate: condition.dateRange().endDate.toISOString()
                                }
                            case PeriodType.PERIOD_YM:
                                return {
                                    category: condition.category,
                                    startYm: condition.dateRangeYm().startDate,
                                    endYm: condition.dateRangeYm().endDate
                                }
                            case PeriodType.SINGLE_MONTH:
                                return {
                                    category: condition.category,
                                    yearMonth: condition.yearMonth()
                                }
                        }
                    })
    
                    let modalData: any = {
                        alarmPatternCode: vm.alarmPatternCode(),
                        workplaceIds: vm.workplaceIds(),
                        categoryPeriods: categoryPeriods
                    }
                    vm.$window.storage('KAL011DModalData', modalData).done(() => {
                        vm.$window.modal('/view/kal/011/d/index.xhtml')
                            .then((result: any) => {
                                vm.openKal011BMOdal();
                            });
                    });
                // }).fail((err: any) => {
                //     vm.$dialog.error(err);
                // }).always(() => vm.$blockui("clear"));
            })
        }

        openKal011BMOdal() {
            const vm = this;
            vm.$blockui("invisible");
            vm.$window.storage('KAL011DModalData').done((data) => {
                if (!data.isClose) {
                    let dataStored = {
                        processId: data.processId,
                        alarmPatternCode: vm.alarmPatternCode(),
                        alarmPatternName: vm.alarmPatternName
                    }
                    vm.$window.storage('KAL011BModalData', dataStored).done(() => {
                        vm.$window.modal('/view/kal/011/b/index.xhtml')
                    });
                } else {
                    vm.$blockui("clear");
                }
            });
        }

    }

    class AlarmPattern {
        code: string;
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    interface IInitActiveAlarmList {
        employmentCode: string;
        alarmPatterns: Array<IAlarmPatternSettingWorkPlace>;
        processingYm: number;
        closureStartDate: any;
        closureEndDate: any;
		workplaceId: string;
    }

    interface IAlarmPatternSettingWorkPlace {
        alarmPatternCode: string;
        alarmPatternName: string;
    }

    interface ICheckCondition {
        category: number;
        categoryName: string;
        checkConditionLis: Array<string>;
        periodType: PeriodType;
        startDate: any;
        endDate: any;
        startYm: any;
        endYm: any
        yearMonth: number;
    }

    enum PeriodType {
        PERIOD_DATE = 1,
        PERIOD_YM = 2,
        SINGLE_MONTH = 3
    }

    class CheckCondition {
        index: string;
        isChecked: KnockoutObservable<boolean>;
        category: common.WORKPLACE_CATAGORY;
        categoryName: any;
        periodType: PeriodType;
        dateRange: KnockoutObservable<DateRangePickerModel>;
        dateRangeYm: KnockoutObservable<DateRangePickerModel>;
        yearMonth: KnockoutObservable<number>;

        constructor(data: ICheckCondition) {
            this.isChecked = ko.observable(false);
            this.category = data.category;
            this.categoryName = data.categoryName;
            this.periodType = data.periodType;
            this.dateRange = ko.observable(new DateRangePickerModel(data.startDate, data.endDate));
            this.dateRangeYm = ko.observable(new DateRangePickerModel(data.startYm, data.endYm));
            this.yearMonth = ko.observable(data.yearMonth);
        }

        isValid() {
            switch (this.periodType) {
                case PeriodType.PERIOD_DATE:
                    if (!this.dateRange().startDate || !this.dateRange().endDate) {
                        return false;
                    }
                    break;
                case PeriodType.PERIOD_YM:
                    if (!this.dateRangeYm().startDate || !this.dateRangeYm().endDate) {
                        return false;
                    }
                    break;
                case PeriodType.SINGLE_MONTH:
                    if (!this.yearMonth()) {
                        return false;
                    }
                    break;
            }
            return true;
        }
    }

    class DateRangePickerModel {
        startDate: any;
        endDate: any;

        constructor(startDate: any, endDate: any) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static isSamePeriod(a: DateRangePickerModel, b: DateRangePickerModel): boolean {
            return a.startDate === b.startDate && a.endDate === b.endDate
        }
    }
}