/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kal013.a {

    import common = nts.uk.at.view.kal013.common;
    import tab = nts.uk.at.view.kal013.a.tab;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;

    const PATH_API = {
        getEnumAlarmCategory: "at/function/alarm/get/enum/alarm/category",
        changeCategory: "alarmworkplace/checkCondition/getByCategory",
        changeAlarmCode: "alarmworkplace/checkCondition/getByCategoryItemCD",
        register: "alarmworkplace/checkCondition/register",
        delete: "alarmworkplace/checkCondition/delete",
        update: "alarmworkplace/checkCondition/update"
    };

    @bean()
    class ViewModel extends ko.ViewModel {

        backButon: string = "/view/kal/012/a/index.xhtml";
        //categories
        categoryList: KnockoutObservableArray<common.Category> = ko.observableArray(common.workplaceCategory());
        // selectedCategoryCode: KnockoutObservable<number> = ko.observable(common.WorkplaceCategory.MASTER_CHECK_BASIC);
        selectedCategoryCode: KnockoutObservable<number> = ko.observable(null);
        selectedCategory: KnockoutObservable<common.CategoryPattern> = ko.observable(null);
        selectedCategoryName: KnockoutObservable<string> = ko.observable(null);
        //Alarm list
        selectedAlarmCode: KnockoutObservable<string> = ko.observable(null);
        alarmListItems: KnockoutObservableArray<common.Alarm> = ko.observableArray([]);
        currentCode: KnockoutObservable<string> = ko.observable(null);
        currentName: KnockoutObservable<string> = ko.observable(null);

        // List Fixed Items
        fixedItems: KnockoutObservableArray<common.AlarmDto> = ko.observableArray([]);
        // List Conditions
        conditions: KnockoutObservableArray<any> = ko.observableArray([]);
        // List Optional Items
        optionalItems: KnockoutObservableArray<any> = ko.observableArray([]);
        // Actual List
        actualFixedItems: KnockoutObservableArray<common.AlarmDto> = ko.observableArray([]);

        //tab panel
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string> = ko.observable(null); //active
        //grid list
        selectedAll: KnockoutObservable<boolean> = ko.observable(false);
        alarmArrangeList: KnockoutObservableArray<common.AlarmDto> = ko.observableArray([]);
        //switch button
        roundingRules: KnockoutObservableArray<any>;
        selectedRuleCode: KnockoutObservable<number> = ko.observable(1);

        workplaceCategory: any = {};

        uniqueConditions: KnockoutObservable<tab.UniqueCondition>;
        checkConditions: KnockoutObservable<tab.CheckCondition>;

        isNewMode: KnockoutObservable<boolean> = ko.observable(true);
        isCodeChangedFromScreenD: KnockoutObservable<boolean> = ko.observable(false);

        constructor(params: any) {
            super();
            const vm = this;

            vm.workplaceCategory = common.WorkplaceCategory;

            // vm.getAlarmChecklist();

            vm.tabSelections();

            vm.selectedTab.subscribe((newTab) => {
                const vm = this;
                let category: any = vm.selectedCategoryCode();

                switch (category) {
                    case vm.workplaceCategory.MONTHLY:
                    case vm.workplaceCategory.SCHEDULE_DAILY:
                        let hasClicked: any = $("#fixedTableCCDT").attr('data-clicked');
                        if (typeof hasClicked == 'undefined' || hasClicked === 'false') {
                            $("#fixedTableCCDT")
                                .attr('data-clicked', 'true')
                                // .ntsFixedTable({height: 250});
                        }
                        break;
                }
            });

            vm.selectedCategoryCode.subscribe(value => {
                if (value != null) {
                    vm.changeCategory();
                    vm.getAlarmChecklist(value);
                    vm.showHiddenTabByCategory(value);
                    vm.selectedCategoryName(_.filter(common.workplaceCategory(), i => i.code == value)[0].name);
                    vm.selectedTab('tab-1');
                }
            });

            vm.selectedAlarmCode.subscribe(value => {
                if (value) {
                    vm.changeAlarmCode();
                }
            });

            vm.actualFixedItems.subscribe(value => {
                vm.uniqueConditions().selectedAll(false);
                vm.uniqueConditions().alarmListItem(value);

            });

            //show tabs
            vm.uniqueConditions = ko.observable(new tab.UniqueCondition(vm.selectedCategoryCode()));
            vm.checkConditions = ko.observable(null);
        }

        created(params: any) {
            const vm = this;
        }

        mounted() {
            const vm = this;
            $("#fixedTable").ntsFixedTable({});
            // vm.changeCategory();

            vm.selectedCategoryCode(common.WorkplaceCategory.MASTER_CHECK_BASIC);
        }

        switchNewMode() {
            const vm = this;
            vm.isNewMode(true);
            vm.currentCode(null);
            vm.currentName(null);
            vm.selectedAlarmCode(null);
            vm.actualFixedItems(vm.fixedItems());
            vm.actualFixedItems.valueHasMutated();
            //vm.uniqueConditions().alarmListItem.removeAll();
            if(!isNullOrUndefined(vm.checkConditions())
                && !isNullOrUndefined(vm.checkConditions().checkConditionsList())){
                vm.checkConditions().checkConditionsList.removeAll();
            }
            $('#code').focus();
        }
        openDialogD() {
            const vm = this;
            vm.$window.modal('/view/kal/013/d/index.xhtml', vm.selectedCategoryCode())
                .then((result: any) => {
                    if (result && !_.isEmpty(result)) {
                        vm.$errors("clear");
                        vm.isCodeChangedFromScreenD(true);
                        if (result.shareParam == vm.selectedCategoryCode()) {
                            vm.selectedCategoryCode.valueHasMutated();
                        } else {
                            vm.selectedCategoryCode(result.shareParam);
                        }
                    }
                });

        }

        copyAlarm() {
            const vm = this;

            vm.$errors("clear");
            vm.currentCode(null);
            vm.currentName(null);
            vm.selectedAlarmCode(null);
            vm.isNewMode(true);
            $('#code').focus();
        }

        findItemSelected(code: any, seachArr: Array<any>): any {
            const vm = this;
            if (!_.isArray(seachArr)) return null;
            let found = _.find(seachArr, ['code', code]);
            return (!_.isNil(found)) ? found : null;
        }

        changeCategory() {
            const vm = this;

            vm.$errors("clear");

            vm.$blockui("grayout");
            return vm.$ajax(PATH_API.changeCategory + "/" + vm.selectedCategoryCode()).done((res: any) => {
                if (res) {
                    let categoryItem = _.map(res.alarmWkpCheckCdt, function (i: any) {
                        return new common.Alarm(i.code, i.name);
                    });
                    let fixedItems = _.map(res.fixedItems, function (i: any) {
                        return new common.AlarmDto(
                            false,
                            i.no,
                            i.classification,
                            i.name,
                            i.message
                        );
                    });

                    vm.fixedItems(fixedItems);

                    if (categoryItem.length) {
                        vm.alarmListItems(_.sortBy(categoryItem, i => i.code));
                        if (vm.isCodeChangedFromScreenD()) {
                            vm.switchNewMode();
                            vm.isCodeChangedFromScreenD(false);
                        } else {
                            vm.isNewMode(false);
                            vm.selectedAlarmCode("");
                            vm.selectedAlarmCode(categoryItem[0].code);
                        }
                    } else {
                        vm.actualFixedItems(fixedItems);
                        vm.alarmListItems([]);
                        if (!vm.isNewMode()) {
                            vm.switchNewMode();
                        } else {
                            $('#code').focus();
                        }
                    }

                }
            }).fail((err) => {
                vm.$dialog.error(err);
            }).always(() => vm.$blockui("clear"));

        }

        changeAlarmCode() {
            const vm = this;

            vm.$errors("clear");

            vm.isNewMode(false);
            let params = {
                category: vm.selectedCategoryCode(),
                code: vm.selectedAlarmCode()
            };
            let selectedItems = _.find(vm.alarmListItems(), i => i.code == vm.selectedAlarmCode());

            if (selectedItems) {
                vm.currentCode(selectedItems.code);
                vm.currentName(selectedItems.name);
            }

            vm.$blockui("grayout");
            vm.$ajax(PATH_API.changeAlarmCode, params).done((res: any) => {
                if (res) {
                    let fixed = ko.toJS(vm.fixedItems());
                    let lstOpItems: any = [];
                    let actual = _.map(fixed, function (i: any) {
                        if (res.conditions && res.conditions.length) {
                            let con = res.conditions;
                            let itemHasSameNo = _.find(con, (x: FixedExtractionConditionDto) => x.no == i.no);
                            if (itemHasSameNo) {
                                i.message = itemHasSameNo.displayMessage;
                                // i.isChecked(itemHasSameNo.useAtr);
                                i.isChecked = itemHasSameNo.useAtr;
                            }
                        }
                        return new common.AlarmDto(
                            i.isChecked,
                            i.no,
                            i.classification,
                            i.name,
                            i.message
                        );
                    });

                    vm.actualFixedItems(actual);

                    if (vm.selectedCategoryCode() == common.WorkplaceCategory.MONTHLY) {
                        if (res.opMonCons && res.opMonCons.length) {
                            lstOpItems = _.map(res.opMonCons, function (i: ExtractionMonConDto) {
                                return new common.CheckConditionDto(
                                    false,
                                    i.errorAlarmWorkplaceId,
                                    i.no,
                                    i.checkMonthlyItemsType,
                                    i.useAtr ? 1 : 0,
                                    i.monExtracConName,
                                    i.minValue,
                                    i.maxValue,
                                    i.operator,
                                    i.messageDisp,
                                    i.checkTarget,
                                    i.averageRatio,
                                    i.additionAttendanceItems,
                                    i.substractionAttendanceItems
                                );
                            });

                        }
                    }

                    if (vm.selectedCategoryCode() == common.WorkplaceCategory.SCHEDULE_DAILY) {
                        if (res.opSchelCons && res.opSchelCons.length) {
                            lstOpItems = _.map(res.opSchelCons, function (i: ExtractionSchelCon) {
                                return new common.CheckConditionDto(
                                    false,
                                    i.errorAlarmWorkplaceId,
                                    i.orderNumber,
                                    i.checkDayItemsType,
                                    i.useAtr ? 1 : 0,
                                    i.daiExtracConName,
                                    i.minValue,
                                    i.maxValue,
                                    i.operator,
                                    i.messageDisp,
                                    i.checkTarget,
                                    i.contrastType,
                                    [],[]
                                );
                            });
                        }
                    }
                    if (vm.checkConditions()) {
                        vm.checkConditions().checkConditionsList(_.sortBy(lstOpItems, i => i.no()));
                    }
                }


            }).fail((err) => {
                vm.$dialog.error(err);
            }).always(() => {
                vm.$blockui("clear");
                $('#name').focus();
            });

        }

        openScreenB() {
            const vm = this;
            //vm.$window.storage('');
            vm.$window.modal('/view/kal/013/d/index.xhtml').then(() => {
            });
        }

        /**
         * Registration of alarm list check conditions (by workplace)
         */
        registerAlarmListByWorkplace() {
            const vm = this;

            let alarmCheck = {
                category: vm.selectedCategoryCode(),
                code: vm.currentCode(),
                name: vm.currentName()
            };

            let alarmCheckCon = _.map(ko.toJS(vm.actualFixedItems()), function (i: common.AlarmDto) {
               return {
                   no: i.no,
                   message: i.message,
                   check: i.isChecked
               };
            });

            let opItems = [];

            if (_.includes([common.WorkplaceCategory.MONTHLY, common.WorkplaceCategory.SCHEDULE_DAILY], vm.selectedCategoryCode())) {
                opItems = ko.toJS(vm.checkConditions().checkConditionsList());
            };

            let param = {
                alarmCheck,
                alarmCheckCon,
                opItems
            };

            const apiString = vm.isNewMode() ? PATH_API.register : PATH_API.update;
            vm.$validate(".nts-input").then((valid) => {
                if (valid) {
                    vm.$blockui("grayout");
                    vm.$ajax(apiString, param).done((res) => {
                        vm.$dialog.info({messageId: "Msg_15"}).then(() => {
                            vm.changeCategory().then(() => {
                                vm.selectedAlarmCode(alarmCheck.code);
                            });
                        });
                    }).fail((err) => {
                        vm.$dialog.error(err);
                    }).always(() => vm.$blockui("clear"));
                }
            });
        }

        /**
         * Delete of alarm list check conditions (by workplace)
         */
        deleteAlarmListByWorkplace() {
            const vm = this;

            vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: 'no' | 'yes' | 'cancel') => {
                if (result === 'yes') {
                    vm.$errors("clear");

                    let alarmList = ko.toJS(vm.alarmListItems());
                    let index = _.findIndex(alarmList, (i: any) => i.code == vm.selectedAlarmCode());
                    const lastIndex = _.findLastIndex(alarmList);
                    let param = {
                        category: vm.selectedCategoryCode(),
                        code: vm.selectedAlarmCode()
                    };

                    vm.$blockui("grayout");
                    vm.$ajax(PATH_API.delete, param).done((res: any) => {
                        vm.$dialog.info({messageId: "Msg_16"}).then(() => {
                            vm.changeCategory().then(() => {
                                if (vm.alarmListItems().length) {
                                    let newIndex = index == lastIndex ? lastIndex - 1 : index;
                                    vm.selectedAlarmCode(vm.alarmListItems()[newIndex].code);
                                } else {
                                    vm.switchNewMode();
                                }
                            });
                        });
                    }).fail((err) => {
                        vm.$dialog.error(err);
                    }).always(() => vm.$blockui("clear"));
                } else {
                    return;
                }
            });
        }

        /**
         * Gets alarm checklist / アラームチェックリスト
         */
        getAlarmChecklist(code?: number) {
            const vm = this;

        }

        /**
         * Gets selected category
         * @param categoryCode
         * @returns  CategoryPattern
         */
        getSelectedCategory(categoryCode: number) {
            const vm = this;
            if (categoryCode < 0) return;

            let fountCategory = vm.findItemSelected(categoryCode, vm.categoryList());
            if (!_.isNil(fountCategory)) {
                vm.selectedCategory(new common.CategoryPattern(fountCategory.code, fountCategory.name));
            } else {
                vm.selectedCategory(new common.CategoryPattern('', ''));
            }
        }

        /**
         * Tabs selections
         */
        tabSelections() {
            const vm = this;

            vm.tabs = ko.observableArray([
                {
                    id: 'tab-1',
                    title: vm.$i18n('KAL003_67'),
                    content: '.tab-content-1',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-2',
                    title: vm.$i18n('KAL003_16'),
                    content: '.tab-content-2',
                    enable: ko.observable(true),
                    visible: ko.observable(false)
                }
            ]);

            vm.selectedTab('tab-1');
        }

        showHiddenTabByCategory(Category: any) {
            const vm = this;

            //hidden all tab
            _.forEach(vm.tabs(), (tab: any, index) => {
                tab.visible(index === 0);
            });
            /*
            MASTER_CHECK_BASIC = 0,// マスタチェック(基本)
            MASTER_CHECK_WORKPLACE = 1,// マスタチェック(職場)
            MASTER_CHECK_DAILY = 2,// マスタチェック(日次)
            SCHEDULE_DAILY = 3, // "スケジュール／日次",
            MONTHLY = 4,// 月次
            APPLICATION_APPROVAL = 5, //"申請承認"/
            */

            //vm.checkConditions = ko.observable(null);

            switch (Category) {

                case vm.workplaceCategory.APPLICATION_APPROVAL:
                case vm.workplaceCategory.MASTER_CHECK_WORKPLACE:
                case vm.workplaceCategory.MASTER_CHECK_BASIC:
                case vm.workplaceCategory.MASTER_CHECK_DAILY:
                    break;

                case vm.workplaceCategory.SCHEDULE_DAILY:
                    vm.tabs()[1].visible(true);
                    vm.checkConditions(new tab.CheckCondition(true, vm.workplaceCategory.SCHEDULE_DAILY));
                    break;

                case vm.workplaceCategory.MONTHLY:
                    vm.tabs()[1].visible(true);
                    vm.checkConditions(new tab.CheckCondition(true, vm.workplaceCategory.MONTHLY));
                    break;
            }
        }

        //check by tabs
    }

    interface FixedExtractionConditionDto {
        // ID
        id: string;
        // No
        no: number;
        // 使用区分
        useAtr: boolean;
        // 表示するメッセージ
        displayMessage: string;
    }

    interface OptionalItemDto {
        checkItem: number;
        no: number;
        conditionName: string;
        message: string;
        useAtr: boolean;
    }

    interface FixedExtractionItemDto {
        // ID
        id: string;
        // NO
        no: number;
        // 区分
        classification: number;
        // 名称
        name: string;
        // 表示メッセージ
        message: string;
    }

    interface AlarmWkpCheckCdtDto {
        // カテゴリ
        category: number;
        // アラームチェック条件コード
        code: string;
        // 名称
        name: string;
    }

    interface ExtractionMonConDto {
        errorAlarmWorkplaceId: string;
        no: number;
        checkMonthlyItemsType: number;
        useAtr: boolean;
        errorAlarmCheckID: string;
        checkTarget: string;
        averageNumberOfDays: number;
        averageNumberOfTimes: number;
        averageTime: number;
        averageRatio: number;
        monExtracConName: string;
        messageDisp: string;
        minValue: string;
        maxValue: string;
        operator: number;

        additionAttendanceItems: Array<number>;
        substractionAttendanceItems: Array<number>;
    }

    interface ExtractionSchelCon {
        errorAlarmWorkplaceId: string;
        orderNumber: number;
        checkDayItemsType: number;
        useAtr: boolean;
        errorAlarmCheckID: string;
        checkTarget: string;
        contrastType: number;
        daiExtracConName: string;
        messageDisp: string;
        minValue: string;
        maxValue: string;
        operator: number;

        additionAttendanceItems: Array<number>;
        substractionAttendanceItems: Array<number>;
    }
}