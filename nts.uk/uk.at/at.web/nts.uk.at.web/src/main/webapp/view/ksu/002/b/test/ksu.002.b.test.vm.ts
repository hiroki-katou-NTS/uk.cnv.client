module nts.uk.ui.at.ksu002.b.test {

    const API = {};

    @bean()
    export class ViewModel extends ko.ViewModel {
        dateValue: KnockoutObservable<any>;
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;
        empCode: KnockoutObservable<string>;
        empName: KnockoutObservable<string>;
        targetDate: KnockoutObservable<string>;
        startDay: KnockoutObservable<any>;
        dayOfWeekList: KnockoutObservableArray<DayOfWeekModel>;

        constructor() {
            super();
            const vm = this;
            vm.targetDate = ko.observable(new Date().toISOString());
            vm.startDay = ko.observable(1);
            vm.startDateString = ko.observable("");
            vm.endDateString = ko.observable("");
            vm.dateValue = ko.observable({startDate: new Date().toISOString(), endDate: new Date().toISOString()});
            vm.startDateString.subscribe(function (value) {
                vm.dateValue().startDate = value;
                vm.dateValue.valueHasMutated();
            });
            vm.endDateString.subscribe(function (value) {
                vm.dateValue().endDate = value;
                vm.dateValue.valueHasMutated();
            });

            vm.empCode = ko.observable("000000000001");
            vm.empName = ko.observable("会社名0001");
            vm.dayOfWeekList = ko.observableArray([
                new DayOfWeekModel(7, '日曜日'),  // Sunday
                new DayOfWeekModel(1, '月曜日'),  // Monday
                new DayOfWeekModel(2, '火曜日'),
                new DayOfWeekModel(3, '水曜日'),
                new DayOfWeekModel(4, '木曜日'),
                new DayOfWeekModel(5, '金曜日'),
                new DayOfWeekModel(6, '土曜日')   // Saturday
            ]);
        }

        created() {
            const vm = this;
            _.extend(window, {vm});
        }

        mounted() {
            const vm = this;
        }

        openKsu002b() {
            let vm = this;
            let shareData = {
                startDate: vm.dateValue().startDate,
                endDate: vm.dateValue().endDate,
                employeeCode: vm.empCode(),
                employeeName: vm.empName(),
                targetDate: vm.targetDate().substring(0,10).replace(new RegExp('-', 'g'), '/'),
                startDay: vm.startDay(),
                isStartingDayOfWeek: false
            }
            vm.$window.storage("ksu002B_params", shareData).then(() => {
                vm.$window.modal('/view/ksu/002/b/index.xhtml').then(() => {

                });
            });
        }
    }
}

class DayOfWeekModel {
    dayOfWeekCode: number;
    dayOfWeekName: string;

    constructor(dayOfWeekCode: number, dayOfWeekName: string) {
        this.dayOfWeekCode = dayOfWeekCode;
        this.dayOfWeekName = dayOfWeekName;
    }
}