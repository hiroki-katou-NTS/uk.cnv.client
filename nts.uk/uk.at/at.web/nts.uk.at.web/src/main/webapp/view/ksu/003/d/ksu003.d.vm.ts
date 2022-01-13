/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.ksu003.d {
    import characteristics = nts.uk.characteristics;

    const API = {
        init: "screen/at/schedule/ksu003/d/init",
        export: "ctx/at/schedule/personal/by-date/export",
    };

    @bean()
    class ViewModel extends ko.ViewModel {
        KEY: string = 'characteristicsKsu003D';
        characteristicsStore: IScheduleByDateCharacteristics;
        graphTimeStartRange: KnockoutObservableArray<RangeModel>;
        selectedGraphStartTime: KnockoutObservable<string>;                // D2_2
        selectedGraphVacationDisplay: KnockoutObservable<number>;          // D2_3
        actualDisplay: KnockoutObservable<boolean> = ko.observable(false); // D2_4
        selectedWorkDisplay: KnockoutObservable<number>;                   // D3_2
        selected2WorkDisplay: KnockoutObservable<number>;                  // D3_3
        selectedTotalTimeDisplay: KnockoutObservable<number>;              // D4_2
        selectedTotalAmountDisplay: KnockoutObservable<number>;            // D4_3
        selectedSupportScheduleDisplay: KnockoutObservable<number>;        // D5_2
        wkpNameDispOfSupporter: KnockoutObservable<boolean>;               // D5_2_4
        dataFromA: IDataFromScreenA;
        show2Work: KnockoutObservable<boolean> = ko.observable(false);
        showActual: KnockoutObservable<boolean> = ko.observable(false);

        constructor(params: any) {
            super();
            const vm = this;
            vm.graphTimeStartRange = ko.observableArray([
                new RangeModel('0', 0),
                new RangeModel('1', 1),
                new RangeModel('2', 2),
                new RangeModel('3', 3),
                new RangeModel('4', 4),
                new RangeModel('5', 5),
                new RangeModel('6', 6),
                new RangeModel('7', 7),
                new RangeModel('8', 8),
                new RangeModel('9', 9),
                new RangeModel('10', 10),
                new RangeModel('11', 11),
                new RangeModel('12', 12)
            ]);
            vm.dataFromA = {
                targetOrg: null,
                employeeIds: [],
                targetPeriod: null
            };
            vm.characteristicsStore = {
                graphStartTime: 0,
                graphVacationDisplay: 0,
                displayActual: false,
                workDisplay: 0,
                doubleWorkDisplay: 0,
                totalTimeDisplay: 0,
                totalAmountDisplay: 0,
                scheduledToSupport: 0,
                wkpNameDisplayOfSupporter: false
            };
            vm.selectedGraphStartTime = ko.observable('0');
            vm.selectedGraphVacationDisplay = ko.observable(1);
            vm.selectedWorkDisplay = ko.observable(2);
            vm.selected2WorkDisplay = ko.observable(1);
            vm.selectedTotalTimeDisplay = ko.observable(1);
            vm.selectedTotalAmountDisplay = ko.observable(1);
            vm.selectedSupportScheduleDisplay = ko.observable(1);
            vm.wkpNameDispOfSupporter = ko.observable(false);
            vm.initData();
        }

        created(params?: any) {
            const vm = this;
            vm.$window.shared("dataShareKsu003D").done((data: IDataFromScreenA) => {
                vm.dataFromA.targetOrg = data.targetOrg;
                vm.dataFromA.employeeIds = data.employeeIds;
                vm.dataFromA.targetPeriod = data.targetPeriod;
            });
            vm.restoreCharacteristics();
        }

        mounted() {
            $("#D1_1").focus();
        }

        public initData(): JQueryPromise<any> {
            const vm = this;
            let dfd = $.Deferred<any>();
            vm.$blockui("invisible");
            vm.$ajax(API.init).then(data => {
                vm.show2Work(data.workManagementMulti.useATR === 1);
                if(!vm.show2Work()){
                    $('#double-work').hide();
                    vm.selected2WorkDisplay(1);
                }
                vm.showActual(!_.isNull(data.scheFuncControl) ? data.scheFuncControl.displayActual : false);
                if(!vm.showActual()){
                    $('#display-actual').hide();
                    vm.actualDisplay(false);
                }
                dfd.resolve();
            }).fail(error => {
                vm.$dialog.error(error);
                dfd.reject();
            }).always(() => vm.$blockui("clear"));

            return dfd.promise();
        }

        public exportFile(): void {
            let vm = this;
            let query: IScheduleByDateOutputSettingQuery = {
                orgUnit: vm.dataFromA.targetOrg.unit,
                orgId: vm.dataFromA.targetOrg.unit === 0 ? vm.dataFromA.targetOrg.workplaceId : vm.dataFromA.targetOrg.workplaceGroupId,
                baseDate: vm.dataFromA.targetPeriod.endDate,
                sortedEmployeeIds: vm.dataFromA.employeeIds,
                graphStartTime: parseInt(vm.selectedGraphStartTime()),
                graphVacationDisplay: vm.selectedGraphVacationDisplay() === 2,
                displayActual: vm.actualDisplay(),
                workDisplay: vm.selectedWorkDisplay() === 2,
                doubleWorkDisplay: vm.selected2WorkDisplay() === 2,
                totalTimeDisplay: vm.selectedTotalTimeDisplay() === 2,
                totalAmountDisplay: vm.selectedTotalAmountDisplay() === 2,
                scheduledToSupport: vm.selectedSupportScheduleDisplay() === 2,
                wkpNameDisplayOfSupporter: vm.wkpNameDispOfSupporter()
            };
            vm.$blockui("invisible");
            nts.uk.request.exportFile(API.export, query).done((success: any) => {
                vm.saveCharacteristics();
            }).fail((error: any) => {
                vm.$dialog.error(error);
            }).always(() => {
                vm.$blockui("clear");
            });
        }

        public closeDialog(): void {
            const vm = this;
            vm.$window.close();
        }

        public saveCharacteristics(): void {
            const vm = this;
            vm.characteristicsStore.graphStartTime = parseInt(vm.selectedGraphStartTime());
            vm.characteristicsStore.graphVacationDisplay = vm.selectedGraphVacationDisplay();
            vm.characteristicsStore.displayActual = vm.actualDisplay();
            vm.characteristicsStore.workDisplay = vm.selectedWorkDisplay();
            vm.characteristicsStore.doubleWorkDisplay = vm.selected2WorkDisplay();
            vm.characteristicsStore.totalTimeDisplay = vm.selectedTotalTimeDisplay();
            vm.characteristicsStore.totalAmountDisplay = vm.selectedTotalAmountDisplay();
            vm.characteristicsStore.scheduledToSupport = vm.selectedSupportScheduleDisplay();
            vm.characteristicsStore.wkpNameDisplayOfSupporter = vm.wkpNameDispOfSupporter();
            characteristics.save(vm.KEY, vm.characteristicsStore);
        }

        public restoreCharacteristics(): void {
            let vm = this;
            characteristics.restore(vm.KEY).done((data: IScheduleByDateCharacteristics) => {
                if (data) {
                    vm.selectedGraphStartTime(data.graphStartTime.toString());
                    vm.selectedGraphVacationDisplay(data.graphVacationDisplay);
                    vm.actualDisplay(data.displayActual);
                    vm.selectedWorkDisplay(data.workDisplay);
                    vm.selected2WorkDisplay(data.doubleWorkDisplay);
                    vm.selectedTotalTimeDisplay(data.totalTimeDisplay);
                    vm.selectedTotalAmountDisplay(data.totalAmountDisplay);
                    vm.selectedSupportScheduleDisplay(data.scheduledToSupport);
                    vm.wkpNameDispOfSupporter(data.wkpNameDisplayOfSupporter);
                }
            });
        }
    }

    interface IScheduleByDateCharacteristics {
        graphStartTime: number;             // グラフスタート時刻
        graphVacationDisplay: number;       // グラフ休暇表示
        displayActual: boolean;             // 実績グラフ表示
        workDisplay: number;                // 勤務表示
        doubleWorkDisplay: number;          // ２回勤務表示
        totalTimeDisplay: number;           // 合計時間表示
        totalAmountDisplay: number;         // 合計金額表示
        scheduledToSupport: number;         // 応援予定
        wkpNameDisplayOfSupporter: boolean; // 応援者の職場名表示
    }

    interface IDataFromScreenA {
        targetOrg: ITargetOrganization;     // 基準の組織情報
        employeeIds: Array<string>;         // 並び順社員リスト
        targetPeriod: ITargetPeriod;        // 対象期間
    }

    interface ITargetOrganization {
        unit: number;
        workplaceId: string;
        workplaceGroupId: string;
    }

    interface ITargetPeriod {
        startDate: string;
        endDate: string;
    }

    interface IScheduleByDateOutputSettingQuery {
        orgUnit: number;
        orgId: string;
        baseDate: string;
        sortedEmployeeIds: Array<string>;
        graphStartTime: number;
        graphVacationDisplay: boolean;
        displayActual: boolean;
        workDisplay: boolean;
        doubleWorkDisplay: boolean;
        totalTimeDisplay: boolean;
        totalAmountDisplay: boolean;
        scheduledToSupport: boolean;
        wkpNameDisplayOfSupporter: boolean;
    }

    class RangeModel {
        name: string;
        code: number;

        constructor(name: string, code: number) {
            this.name = name;
            this.code = code;
        }
    }
}